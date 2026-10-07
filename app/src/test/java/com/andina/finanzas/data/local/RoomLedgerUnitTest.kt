package com.andina.finanzas.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.andina.finanzas.data.local.dao.CategoryDao
import com.andina.finanzas.data.local.dao.LedgerDao
import com.andina.finanzas.data.local.dao.PocketDao
import com.andina.finanzas.data.local.dao.TransactionDao
import com.andina.finanzas.data.local.dao.WalletDao
import com.andina.finanzas.data.local.entity.CategoryEntity
import com.andina.finanzas.data.local.entity.PocketEntity
import com.andina.finanzas.data.local.entity.TransactionEntity
import com.andina.finanzas.data.local.entity.WalletEntity
import com.andina.finanzas.data.local.seed.DatabaseSeeder
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Quality Gate de Fase 03:
 * Pruebas unitarias de integridad contable sobre Room y Libro Mayor (DatabaseView).
 * Verifica que los saldos se deriven matemáticamente sin desincronización,
 * que los gastos descuenten, los ingresos sumen y las transferencias conserven el patrimonio.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class RoomLedgerUnitTest {

    private lateinit var db: AppDatabase
    private lateinit var walletDao: WalletDao
    private lateinit var categoryDao: CategoryDao
    private lateinit var transactionDao: TransactionDao
    private lateinit var pocketDao: PocketDao
    private lateinit var ledgerDao: LedgerDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = AppDatabase.buildInMemory(context)
        walletDao = db.walletDao()
        categoryDao = db.categoryDao()
        transactionDao = db.transactionDao()
        pocketDao = db.pocketDao()
        ledgerDao = db.ledgerDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInitialSeedContainsDefaultWalletsAndCategories() = runBlocking {
        // Ejecutar seeder sobre la base de datos abierta
        DatabaseSeeder.seedInitialData(db.openHelper.writableDatabase)

        val wallets = walletDao.getActiveWallets()
        assertEquals("Deben existir 4 billeteras colombianas por defecto", 4, wallets.size)
        val walletNames = wallets.map { it.name }
        assertTrue("Debe contener Nequi", walletNames.contains("Nequi"))
        assertTrue("Debe contener Daviplata", walletNames.contains("Daviplata"))
        assertTrue("Debe contener Bancolombia", walletNames.contains("Bancolombia"))
        assertTrue("Debe contener Efectivo", walletNames.contains("Efectivo"))

        val categories = categoryDao.getActiveCategories()
        assertTrue("Deben existir categorías precargadas", categories.isNotEmpty())
        val antCategories = categories.filter { it.isAntCandidate }
        assertTrue("Debe haber categorías marcadas como candidatas a gasto hormiga", antCategories.isNotEmpty())
    }

    @Test
    fun testExpenseReducesWalletBalanceInDatabaseView() = runBlocking {
        // Billetera con saldo inicial de $1.000.000 COP
        val walletId = walletDao.insertWallet(
            WalletEntity(
                name = "Mi Bancolombia",
                type = WalletEntity.TYPE_BANCOLOMBIA,
                initialBalance = 1_000_000L,
                colorHex = "#FDDA24"
            )
        )

        // Registrar un gasto de $150.000 COP
        val tx = TransactionEntity(
            type = TransactionEntity.TYPE_GASTO,
            amount = 150_000L,
            walletId = walletId,
            note = "Mercado quincenal",
            occurredAt = System.currentTimeMillis()
        )
        transactionDao.insertTransaction(tx)

        // Verificar el balance derivado del Ledger
        val balanceView = walletDao.getBalanceByWalletId(walletId)
        assertNotNull("La vista de balance debe devolver un registro", balanceView)
        assertEquals(
            "El saldo derivado debe ser 1.000.000 - 150.000 = 850.000 COP",
            850_000L,
            balanceView!!.balance
        )
    }

    @Test
    fun testIncomeIncreasesWalletBalanceInDatabaseView() = runBlocking {
        val walletId = walletDao.insertWallet(
            WalletEntity(
                name = "Nequi Personal",
                type = WalletEntity.TYPE_NEQUI,
                initialBalance = 200_000L,
                colorHex = "#4A154B"
            )
        )

        // Registrar un ingreso de $500.000 COP
        val tx = TransactionEntity(
            type = TransactionEntity.TYPE_INGRESO,
            amount = 500_000L,
            walletId = walletId,
            note = "Pago freelance",
            occurredAt = System.currentTimeMillis()
        )
        transactionDao.insertTransaction(tx)

        val balanceView = walletDao.getBalanceByWalletId(walletId)
        assertNotNull(balanceView)
        assertEquals(
            "El saldo derivado debe ser 200.000 + 500.000 = 700.000 COP",
            700_000L,
            balanceView!!.balance
        )
    }

    @Test
    fun testTransferBetweenWalletsPreservesTotalLedgerBalance() = runBlocking {
        val fromId = walletDao.insertWallet(
            WalletEntity(
                name = "Bancolombia Principal",
                type = WalletEntity.TYPE_BANCOLOMBIA,
                initialBalance = 1_000_000L,
                colorHex = "#FDDA24"
            )
        )
        val toId = walletDao.insertWallet(
            WalletEntity(
                name = "Nequi de Bolsillo",
                type = WalletEntity.TYPE_NEQUI,
                initialBalance = 200_000L,
                colorHex = "#4A154B"
            )
        )

        // Transferir $300.000 COP de Bancolombia a Nequi de forma atómica
        ledgerDao.transferBetweenWallets(
            fromWalletId = fromId,
            toWalletId = toId,
            amount = 300_000L,
            note = "Recarga a Nequi"
        )

        val fromBalance = walletDao.getBalanceByWalletId(fromId)!!.balance
        val toBalance = walletDao.getBalanceByWalletId(toId)!!.balance

        assertEquals("Bancolombia debe descontar 300.000 -> 700.000 COP", 700_000L, fromBalance)
        assertEquals("Nequi debe recibir 300.000 -> 500.000 COP", 500_000L, toBalance)
        assertEquals(
            "El patrimonio total consolidado debe ser constante (1.200.000 COP)",
            1_200_000L,
            fromBalance + toBalance
        )
    }

    @Test
    fun testContributeAndWithdrawFromPocketUpdatesBothBalancesCorrectly() = runBlocking {
        val walletId = walletDao.insertWallet(
            WalletEntity(
                name = "Daviplata",
                type = WalletEntity.TYPE_DAVIPLATA,
                initialBalance = 600_000L,
                colorHex = "#EE2724"
            )
        )

        val pocketId = pocketDao.insertPocket(
            PocketEntity(
                name = "Fondo de Emergencia",
                goal = 2_000_000L,
                icon = "shield",
                colorHex = "#435C3C"
            )
        )

        // 1. Aportar $200.000 al bolsillo
        ledgerDao.contributeToPocket(
            pocketId = pocketId,
            walletId = walletId,
            amount = 200_000L
        )

        var walletBal = walletDao.getBalanceByWalletId(walletId)!!.balance
        var pocketBal = pocketDao.getPocketBalanceById(pocketId)!!.currentBalance

        assertEquals("La billetera debe descontar el aporte (400.000 COP)", 400_000L, walletBal)
        assertEquals("El bolsillo debe acumular el aporte (200.000 COP)", 200_000L, pocketBal)
        assertEquals("El patrimonio total debe conservarse (600.000 COP)", 600_000L, walletBal + pocketBal)

        // 2. Retirar $50.000 del bolsillo hacia la billetera
        ledgerDao.withdrawFromPocket(
            pocketId = pocketId,
            walletId = walletId,
            amount = 50_000L
        )

        walletBal = walletDao.getBalanceByWalletId(walletId)!!.balance
        pocketBal = pocketDao.getPocketBalanceById(pocketId)!!.currentBalance

        assertEquals("La billetera debe sumar el retiro de vuelta (450.000 COP)", 450_000L, walletBal)
        assertEquals("El bolsillo debe reducirse (150.000 COP)", 150_000L, pocketBal)
        assertEquals("El patrimonio total sigue siendo exactamente 600.000 COP", 600_000L, walletBal + pocketBal)
    }
}
