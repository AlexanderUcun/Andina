package com.andina.finanzas.data.export

import com.andina.finanzas.data.local.dao.CategoryDao
import com.andina.finanzas.data.local.dao.PocketDao
import com.andina.finanzas.data.local.dao.TransactionDao
import com.andina.finanzas.data.local.dao.WalletDao
import com.andina.finanzas.data.local.entity.CategoryEntity
import com.andina.finanzas.data.local.entity.PocketEntity
import com.andina.finanzas.data.local.entity.PocketMovementEntity
import com.andina.finanzas.data.local.entity.TransactionEntity
import com.andina.finanzas.data.local.entity.WalletEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class WalletBackupDto(
    val id: Long,
    val name: String,
    val type: String,
    val initialBalance: Long,
    val colorHex: String,
    val archived: Boolean,
    val sortOrder: Int
)

@Serializable
data class CategoryBackupDto(
    val id: Long,
    val name: String,
    val icon: String,
    val colorHex: String,
    val kind: String,
    val isAntCandidate: Boolean,
    val isDefault: Boolean,
    val archived: Boolean,
    val sortOrder: Int
)

@Serializable
data class TransactionBackupDto(
    val id: Long,
    val type: String,
    val amount: Long,
    val walletId: Long,
    val toWalletId: Long?,
    val categoryId: Long?,
    val note: String,
    val occurredAt: Long,
    val createdAt: Long,
    val isAntExpense: Boolean,
    val receiptUri: String?,
    val recurringRuleId: Long?
)

@Serializable
data class PocketBackupDto(
    val id: Long,
    val name: String,
    val goal: Long,
    val icon: String,
    val colorHex: String,
    val deadline: Long?,
    val archived: Boolean,
    val sortOrder: Int
)

@Serializable
data class PocketMovementBackupDto(
    val id: Long,
    val pocketId: Long,
    val walletId: Long,
    val amount: Long,
    val occurredAt: Long
)

@Serializable
data class AndinaBackupPayload(
    val version: Int = 1,
    val exportedAtMillis: Long = System.currentTimeMillis(),
    val appVersion: String = "1.0.0",
    val wallets: List<WalletBackupDto>,
    val categories: List<CategoryBackupDto>,
    val transactions: List<TransactionBackupDto>,
    val pockets: List<PocketBackupDto>,
    val pocketMovements: List<PocketMovementBackupDto>
)

data class BackupRestoreSummary(
    val walletsRestored: Int,
    val categoriesRestored: Int,
    val transactionsRestored: Int,
    val pocketsRestored: Int
)

/**
 * Gestor de copias de seguridad portables (.andina).
 * Serializa de forma integral todas las entidades del libro mayor y permite
 * cifrado de grado militar AES-GCM con PIN opcional para migración segura entre dispositivos.
 */
@Singleton
class BackupManager @Inject constructor(
    private val walletDao: WalletDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val pocketDao: PocketDao
) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }

    /**
     * Genera la copia de seguridad completa de la base de datos local.
     * Si se provee un [pin], los datos se cifran con AES-GCM y clave derivada por PBKDF2.
     */
    suspend fun createBackup(pin: String? = null): ByteArray {
        val wallets = walletDao.getAllWallets().map {
            WalletBackupDto(
                id = it.id,
                name = it.name,
                type = it.type,
                initialBalance = it.initialBalance,
                colorHex = it.colorHex,
                archived = it.archived,
                sortOrder = it.sortOrder
            )
        }

        val categories = categoryDao.getAllCategories().map {
            CategoryBackupDto(
                id = it.id,
                name = it.name,
                icon = it.icon,
                colorHex = it.colorHex,
                kind = it.kind,
                isAntCandidate = it.isAntCandidate,
                isDefault = it.isDefault,
                archived = it.archived,
                sortOrder = it.sortOrder
            )
        }

        val transactions = transactionDao.getAllTransactions().map {
            TransactionBackupDto(
                id = it.id,
                type = it.type,
                amount = it.amount,
                walletId = it.walletId,
                toWalletId = it.toWalletId,
                categoryId = it.categoryId,
                note = it.note,
                occurredAt = it.occurredAt,
                createdAt = it.createdAt,
                isAntExpense = it.isAntExpense,
                receiptUri = it.receiptUri,
                recurringRuleId = it.recurringRuleId
            )
        }

        val pockets = pocketDao.getAllPockets().map {
            PocketBackupDto(
                id = it.id,
                name = it.name,
                goal = it.goal,
                icon = it.icon,
                colorHex = it.colorHex,
                deadline = it.deadline,
                archived = it.archived,
                sortOrder = it.sortOrder
            )
        }

        val movements = pocketDao.getAllPocketMovements().map {
            PocketMovementBackupDto(
                id = it.id,
                pocketId = it.pocketId,
                walletId = it.walletId,
                amount = it.amount,
                occurredAt = it.occurredAt
            )
        }

        val payload = AndinaBackupPayload(
            wallets = wallets,
            categories = categories,
            transactions = transactions,
            pockets = pockets,
            pocketMovements = movements
        )

        val jsonString = json.encodeToString(payload)
        val jsonBytes = jsonString.toByteArray(StandardCharsets.UTF_8)

        return if (pin.isNullOrBlank()) {
            val header = MAGIC_RAW.toByteArray(StandardCharsets.UTF_8)
            val result = ByteArray(header.size + jsonBytes.size)
            System.arraycopy(header, 0, result, 0, header.size)
            System.arraycopy(jsonBytes, 0, result, header.size, jsonBytes.size)
            result
        } else {
            encryptWithPin(jsonBytes, pin)
        }
    }

    /**
     * Restaura una copia de seguridad (.andina) en la base de datos local.
     */
    suspend fun restoreBackup(backupBytes: ByteArray, pin: String? = null): BackupRestoreSummary {
        val jsonBytes = if (hasPrefix(backupBytes, MAGIC_RAW)) {
            val headerSize = MAGIC_RAW.toByteArray(StandardCharsets.UTF_8).size
            val size = backupBytes.size - headerSize
            val content = ByteArray(size)
            System.arraycopy(backupBytes, headerSize, content, 0, size)
            content
        } else if (hasPrefix(backupBytes, MAGIC_ENC)) {
            require(!pin.isNullOrBlank()) { "La copia de seguridad está protegida con PIN" }
            decryptWithPin(backupBytes, pin)
        } else {
            throw IllegalArgumentException("El archivo no es una copia de seguridad válida de Andina (.andina)")
        }

        val jsonString = String(jsonBytes, StandardCharsets.UTF_8)
        val payload = json.decodeFromString<AndinaBackupPayload>(jsonString)

        // Limpieza atómica de tablas para restauración completa
        transactionDao.deleteAllTransactions()
        pocketDao.deleteAllMovements()
        pocketDao.deleteAllPockets()
        walletDao.deleteAllWallets()
        categoryDao.deleteAllCategories()

        // Inserción de entidades
        val walletEntities = payload.wallets.map {
            WalletEntity(
                id = it.id,
                name = it.name,
                type = it.type,
                initialBalance = it.initialBalance,
                colorHex = it.colorHex,
                archived = it.archived,
                sortOrder = it.sortOrder
            )
        }
        walletDao.insertWallets(walletEntities)

        val categoryEntities = payload.categories.map {
            CategoryEntity(
                id = it.id,
                name = it.name,
                icon = it.icon,
                colorHex = it.colorHex,
                kind = it.kind,
                isAntCandidate = it.isAntCandidate,
                isDefault = it.isDefault,
                archived = it.archived,
                sortOrder = it.sortOrder
            )
        }
        categoryDao.insertCategories(categoryEntities)

        val pocketEntities = payload.pockets.map {
            PocketEntity(
                id = it.id,
                name = it.name,
                goal = it.goal,
                icon = it.icon,
                colorHex = it.colorHex,
                deadline = it.deadline,
                archived = it.archived,
                sortOrder = it.sortOrder
            )
        }
        for (pocket in pocketEntities) {
            pocketDao.insertPocket(pocket)
        }

        val movementEntities = payload.pocketMovements.map {
            PocketMovementEntity(
                id = it.id,
                pocketId = it.pocketId,
                walletId = it.walletId,
                amount = it.amount,
                occurredAt = it.occurredAt
            )
        }
        for (movement in movementEntities) {
            pocketDao.insertMovement(movement)
        }

        val transactionEntities = payload.transactions.map {
            TransactionEntity(
                id = it.id,
                type = it.type,
                walletId = it.walletId,
                toWalletId = it.toWalletId,
                categoryId = it.categoryId,
                amount = it.amount,
                occurredAt = it.occurredAt,
                createdAt = it.createdAt,
                isAntExpense = it.isAntExpense,
                receiptUri = it.receiptUri,
                recurringRuleId = it.recurringRuleId,
                note = it.note
            )
        }
        transactionDao.insertTransactions(transactionEntities)

        return BackupRestoreSummary(
            walletsRestored = walletEntities.size,
            categoriesRestored = categoryEntities.size,
            transactionsRestored = transactionEntities.size,
            pocketsRestored = pocketEntities.size
        )
    }

    private fun encryptWithPin(data: ByteArray, pin: String): ByteArray {
        val salt = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(salt)

        val key = deriveKeyFromPin(pin, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val iv = cipher.iv
        val cipherText = cipher.doFinal(data)

        val header = MAGIC_ENC.toByteArray(StandardCharsets.UTF_8)
        val totalSize = header.size + salt.size + iv.size + cipherText.size
        val result = ByteArray(totalSize)

        var offset = 0
        System.arraycopy(header, 0, result, offset, header.size); offset += header.size
        System.arraycopy(salt, 0, result, offset, salt.size); offset += salt.size
        System.arraycopy(iv, 0, result, offset, iv.size); offset += iv.size
        System.arraycopy(cipherText, 0, result, offset, cipherText.size)

        return result
    }

    private fun decryptWithPin(encryptedData: ByteArray, pin: String): ByteArray {
        val header = MAGIC_ENC.toByteArray(StandardCharsets.UTF_8)
        var offset = header.size

        val salt = ByteArray(SALT_LENGTH)
        System.arraycopy(encryptedData, offset, salt, 0, SALT_LENGTH); offset += SALT_LENGTH

        val iv = ByteArray(GCM_IV_LENGTH)
        System.arraycopy(encryptedData, offset, iv, 0, GCM_IV_LENGTH); offset += GCM_IV_LENGTH

        val cipherTextSize = encryptedData.size - offset
        val cipherText = ByteArray(cipherTextSize)
        System.arraycopy(encryptedData, offset, cipherText, 0, cipherTextSize)

        val key = deriveKeyFromPin(pin, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)

        return cipher.doFinal(cipherText)
    }

    private fun deriveKeyFromPin(pin: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(pin.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    private fun hasPrefix(data: ByteArray, prefix: String): Boolean {
        val prefixBytes = prefix.toByteArray(StandardCharsets.UTF_8)
        if (data.size < prefixBytes.size) return false
        for (i in prefixBytes.indices) {
            if (data[i] != prefixBytes[i]) return false
        }
        return true
    }

    companion object {
        private const val MAGIC_RAW = "ANDINA_RAW_V1\n"
        private const val MAGIC_ENC = "ANDINA_ENC_V1\n"
        private const val SALT_LENGTH = 16
        private const val GCM_IV_LENGTH = 12
        private const val PBKDF2_ITERATIONS = 10000
        private const val KEY_LENGTH_BITS = 256
    }
}
