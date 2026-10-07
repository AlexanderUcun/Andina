package com.andina.finanzas.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor de seguridad criptográfica respaldado por hardware mediante Android Keystore.
 * Custodia la clave maestra AES-256 en el TEE/StrongBox del dispositivo y genera
 * de manera segura la frase de paso (passphrase) de 32 bytes para SQLCipher.
 */
@Singleton
class KeystoreHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @Volatile
    private var cachedDbPassphrase: ByteArray? = null

    /**
     * Obtiene o genera la frase de paso de 32 bytes (256 bits) para la base de datos cifrada.
     * La frase se genera una sola vez con [SecureRandom], se cifra con la clave maestra de
     * Android Keystore y se guarda cifrada en el almacenamiento privado de la app.
     */
    @Synchronized
    fun getOrCreateDatabasePassphrase(): ByteArray {
        cachedDbPassphrase?.let { return it }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val encryptedBase64 = prefs.getString(KEY_ENCRYPTED_PASSPHRASE, null)

        val passphrase = if (encryptedBase64 != null) {
            try {
                val encryptedBytes = Base64.decode(encryptedBase64, Base64.NO_WRAP)
                decrypt(encryptedBytes, DB_KEY_ALIAS)
            } catch (e: Exception) {
                // Si la clave del Keystore fue invalidada, regeneramos la frase de paso de emergencia
                generateAndPersistNewPassphrase(prefs)
            }
        } else {
            generateAndPersistNewPassphrase(prefs)
        }

        cachedDbPassphrase = passphrase
        return passphrase
    }

    private fun generateAndPersistNewPassphrase(prefs: android.content.SharedPreferences): ByteArray {
        val newPassphrase = generateSecureRandomBytes(32)
        val encrypted = encrypt(newPassphrase, DB_KEY_ALIAS)
        val encryptedBase64 = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        prefs.edit().putString(KEY_ENCRYPTED_PASSPHRASE, encryptedBase64).apply()
        return newPassphrase
    }

    /**
     * Obtiene o genera una clave simétrica AES-256 en Android Keystore.
     */
    fun getOrCreateMasterKey(alias: String = MASTER_KEY_ALIAS): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }

        if (keyStore.containsAlias(alias)) {
            val entry = keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry
            if (entry != null) {
                return entry.secretKey
            }
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )

        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    /**
     * Cifra un arreglo de bytes con AES/GCM/NoPadding usando la clave maestra custodiada.
     * Retorna el arreglo empaquetado: [12 bytes IV] + [Ciphertext + 16 bytes Auth Tag].
     */
    fun encrypt(plainData: ByteArray, keyAlias: String = MASTER_KEY_ALIAS): ByteArray {
        val secretKey = getOrCreateMasterKey(keyAlias)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val iv = cipher.iv ?: throw IllegalStateException("El IV no fue generado por el Cipher")
        val cipherText = cipher.doFinal(plainData)

        // Empaquetamos: IV (12 bytes) + CipherText (con GCM Tag de 16 bytes al final)
        val combined = ByteArray(iv.size + cipherText.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)
        return combined
    }

    /**
     * Descifra un arreglo de bytes empaquetado [12 bytes IV] + [Ciphertext + Tag].
     */
    fun decrypt(encryptedPayload: ByteArray, keyAlias: String = MASTER_KEY_ALIAS): ByteArray {
        require(encryptedPayload.size > GCM_IV_LENGTH) {
            "El tamaño del payload cifrado (${encryptedPayload.size}) es menor que el IV ($GCM_IV_LENGTH)"
        }

        val secretKey = getOrCreateMasterKey(keyAlias)
        val iv = ByteArray(GCM_IV_LENGTH)
        System.arraycopy(encryptedPayload, 0, iv, 0, GCM_IV_LENGTH)

        val cipherTextSize = encryptedPayload.size - GCM_IV_LENGTH
        val cipherText = ByteArray(cipherTextSize)
        System.arraycopy(encryptedPayload, GCM_IV_LENGTH, cipherText, 0, cipherTextSize)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

        return cipher.doFinal(cipherText)
    }

    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val MASTER_KEY_ALIAS = "andina_master_vault_key"
        private const val DB_KEY_ALIAS = "andina_db_encryption_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH_BITS = 128
        private const val PREFS_NAME = "andina_security_vault"
        private const val KEY_ENCRYPTED_PASSPHRASE = "enc_db_passphrase_v1"

        /**
         * Genera un arreglo de bytes aleatorios criptográficamente seguros.
         */
        fun generateSecureRandomBytes(length: Int = 32): ByteArray {
            val random = SecureRandom()
            val bytes = ByteArray(length)
            random.nextBytes(bytes)
            return bytes
        }
    }
}
