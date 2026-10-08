package com.andina.finanzas.domain.model

import com.andina.finanzas.domain.repository.FinanceRepository
import com.andina.finanzas.domain.usecase.ContributeToPocketUseCase
import com.andina.finanzas.domain.usecase.DetectAntExpensesUseCase
import com.andina.finanzas.domain.usecase.RegisterTransactionUseCase
import com.andina.finanzas.domain.usecase.WithdrawFromPocketUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Suite de pruebas unitarias matemáticas de la Capa de Dominio (Fase 04).
 *
 * Valida:
 * 1. Precisión matemática de [Money] sin coma flotante.
 * 2. Formato y parseo monetario para Colombia (COP).
 * 3. Progresos y metas de [Pocket].
 * 4. Detección y respeto de overrides de gastos hormiga [DetectAntExpensesUseCase].
 * 5. Validaciones transaccionales de [RegisterTransactionUseCase].
 * 6. Aportes y retiros atómicos con protección de saldo [ContributeToPocketUseCase] y [WithdrawFromPocketUseCase].
 */
class MoneyMathUnitTest {

    // =========================================================================
    // 1. Matemáticas y Precisión de Money (Long)
    // =========================================================================

    @Test
    fun `operaciones basicas de Money conservan precision exacta sin redondeos de coma flotante`() {
        val saldoInicial = Money(14_850_000L)
        val ingreso = Money(5_150_000L)
        val gasto = Money(3_000_000L)

        val total = saldoInicial + ingreso - gasto

        assertEquals(17_000_000L, total.pesos)
        assertTrue(total.isPositive)
        assertFalse(total.isNegative)
        assertFalse(total.isZero)
    }

    @Test
    fun `multiplicacion y division de Money operan correctamente`() {
        val cuota = Money(250_000L)

        val cuatroCuotas = cuota * 4
        val mediaCuota = cuota / 2L
        val conPorcentaje = cuota * 1.10 // +10%

        assertEquals(1_000_000L, cuatroCuotas.pesos)
        assertEquals(125_000L, mediaCuota.pesos)
        assertEquals(275_000L, conPorcentaje.pesos)
    }

    @Test
    fun `formatCop produce formato estandar colombiano con puntos de mil sin decimales`() {
        val catorceMillones = Money(14_850_000L)
        val cincuentaMil = Money(50_000L)
        val negativo = Money(-25_000L)
        val cero = Money.ZERO

        assertEquals("$ 14.850.000", catorceMillones.formatCop())
        assertEquals("$ 14.850.000 COP", catorceMillones.formatCop(includeCode = true))
        assertEquals("$ 50.000", cincuentaMil.formatCop())
        assertEquals("-$ 25.000", negativo.formatCop())
        assertEquals("-$ 25.000 COP", negativo.formatCop(includeCode = true))
        assertEquals("$ 0", cero.formatCop())
    }

    @Test
    fun `fromStringOrNull limpia caracteres especiales y parsea montos validos`() {
        assertEquals(Money(50_000L), Money.fromStringOrNull("$ 50.000"))
        assertEquals(Money(14_850_000L), Money.fromStringOrNull("14.850.000 COP"))
        assertEquals(Money(-200_000L), Money.fromStringOrNull("-$ 200.000"))
        assertEquals(Money(0L), Money.fromStringOrNull("$ 0"))
        assertNull(Money.fromStringOrNull(""))
        assertNull(Money.fromStringOrNull("   "))
        assertNull(Money.fromStringOrNull("sin numeros"))
        assertNull(Money.fromStringOrNull(null))
    }

    // =========================================================================
    // 2. Modelo Pocket: Progreso y Restante
    // =========================================================================

    @Test
    fun `pocket calcula correctamente porcentaje de avance y saldo pendiente`() {
        val meta = Pocket(
            id = 1,
            name = "Fondo de Emergencia",
            goal = Money(1_000_000L),
            currentBalance = Money(250_000L)
        )

        assertEquals(0.25f, meta.progress, 0.001f)
        assertEquals(Money(750_000L), meta.remaining)
        assertFalse(meta.isCompleted)

        val metaCumplida = meta.copy(currentBalance = Money(1_200_000L))
        assertEquals(1.0f, metaCumplida.progress, 0.001f)
        assertEquals(Money.ZERO, metaCumplida.remaining)
        assertTrue(metaCumplida.isCompleted)
    }

    // =========================================================================
    // 3. Detector de Gastos Hormiga (DetectAntExpensesUseCase)
    // =========================================================================

    @Test
    fun `detector clasifica como hormiga gastos menores al umbral en categorias candidatas`() {
        val detector = DetectAntExpensesUseCase()

        // Café de $8.000 en categoría candidata -> Hormiga
        val esHormiga = detector(
            type = TransactionType.GASTO,
            amount = Money(8_000L),
            isCategoryAntCandidate = true
        )
        assertTrue(esHormiga)

        // Compra de $45.000 en categoría candidata (> $25.000) -> No es hormiga
        val superaUmbral = detector(
            type = TransactionType.GASTO,
            amount = Money(45_000L),
            isCategoryAntCandidate = true
        )
        assertFalse(superaUmbral)

        // Gasto en categoría no candidata (ej. Servicios) -> No es hormiga
        val noCandidata = detector(
            type = TransactionType.GASTO,
            amount = Money(15_000L),
            isCategoryAntCandidate = false
        )
        assertFalse(noCandidata)

        // Ingreso -> Nunca es hormiga
        val ingreso = detector(
            type = TransactionType.INGRESO,
            amount = Money(5_000L),
            isCategoryAntCandidate = true
        )
        assertFalse(ingreso)
    }

    @Test
    fun `detector respeta la decision explicita (override) del usuario`() {
        val detector = DetectAntExpensesUseCase()

        // El usuario decide explícitamente marcarlo como hormiga aunque supere el umbral
        val forzadoHormiga = detector(
            type = TransactionType.GASTO,
            amount = Money(80_000L),
            isCategoryAntCandidate = false,
            isAntOverride = true
        )
        assertTrue(forzadoHormiga)

        // El usuario decide desmarcarlo aunque cumpla todas las condiciones
        val desmarcado = detector(
            type = TransactionType.GASTO,
            amount = Money(5_000L),
            isCategoryAntCandidate = true,
            isAntOverride = false
        )
        assertFalse(desmarcado)
    }

    // =========================================================================
    // 4. Casos de Uso Transaccionales y de Ahorro Atómico
    // =========================================================================

    @Test
    fun `RegisterTransactionUseCase bloquea montos invalidos o transferencias erroneas`() = runBlocking {
        val fakeRepo = FakeFinanceRepository()
        val useCase = RegisterTransactionUseCase(fakeRepo, DetectAntExpensesUseCase())

        // 1. Monto cero
        val resCero = useCase(
            type = TransactionType.GASTO,
            amount = Money.ZERO,
            walletId = 1L
        )
        assertTrue(resCero.isFailure)
        assertTrue(resCero.exceptionOrNull() is DomainError.InvalidAmount)

        // 2. Transferencia a la misma billetera
        val resMismaBilletera = useCase(
            type = TransactionType.TRANSFERENCIA,
            amount = Money(50_000L),
            walletId = 1L,
            toWalletId = 1L
        )
        assertTrue(resMismaBilletera.isFailure)
        assertTrue(resMismaBilletera.exceptionOrNull() is DomainError.SameWalletTransfer)

        // 3. Billetera inexistente
        val resInexistente = useCase(
            type = TransactionType.GASTO,
            amount = Money(20_000L),
            walletId = 999L
        )
        assertTrue(resInexistente.isFailure)
        assertTrue(resInexistente.exceptionOrNull() is DomainError.WalletNotFound)

        // 4. Registro exitoso
        val resExito = useCase(
            type = TransactionType.GASTO,
            amount = Money(15_000L),
            walletId = 1L,
            categoryId = 10L,
            note = "Café en la mañana"
        )
        assertTrue(resExito.isSuccess)
        assertEquals(101L, resExito.getOrNull())
        assertTrue(fakeRepo.lastRegisteredTransaction?.isAntExpense == true)
    }

    @Test
    fun `ContributeToPocketUseCase y WithdrawFromPocketUseCase validan saldos atómicamente`() = runBlocking {
        val fakeRepo = FakeFinanceRepository()
        val contribute = ContributeToPocketUseCase(fakeRepo)
        val withdraw = WithdrawFromPocketUseCase(fakeRepo)

        // Billetera 1 tiene $500.000 disponibles. Bolsillo 1 tiene $100.000 ahorrados.

        // 1. Intento de aporte mayor al saldo disponible ($600.000 > $500.000)
        val aporteExcedido = contribute(
            pocketId = 1L,
            walletId = 1L,
            amount = Money(600_000L)
        )
        assertTrue(aporteExcedido.isFailure)
        assertTrue(aporteExcedido.exceptionOrNull() is DomainError.InsufficientFunds)

        // 2. Aporte válido de $200.000
        val aporteValido = contribute(
            pocketId = 1L,
            walletId = 1L,
            amount = Money(200_000L)
        )
        assertTrue(aporteValido.isSuccess)

        // 3. Intento de retiro mayor al saldo ahorrado en el bolsillo ($350.000 > $100.000)
        val retiroExcedido = withdraw(
            pocketId = 1L,
            walletId = 1L,
            amount = Money(350_000L)
        )
        assertTrue(retiroExcedido.isFailure)
        assertTrue(retiroExcedido.exceptionOrNull() is DomainError.InsufficientFunds)

        // 4. Retiro válido de $50.000
        val retiroValido = withdraw(
            pocketId = 1L,
            walletId = 1L,
            amount = Money(50_000L)
        )
        assertTrue(retiroValido.isSuccess)
    }
}

/**
 * Falso en memoria del repositorio de finanzas para pruebas unitarias sin dependencias de Room.
 */
private class FakeFinanceRepository : FinanceRepository {

    private val wallets = mutableMapOf(
        1L to Wallet(id = 1L, name = "Nequi", type = WalletType.NEQUI, currentBalance = Money(500_000L), colorHex = "#4A154B"),
        2L to Wallet(id = 2L, name = "Bancolombia", type = WalletType.BANCOLOMBIA, currentBalance = Money(1_000_000L), colorHex = "#FDDA24")
    )

    private val pockets = mutableMapOf(
        1L to Pocket(id = 1L, name = "Vacaciones", goal = Money(1_500_000L), currentBalance = Money(100_000L))
    )

    private val categories = mutableMapOf(
        10L to Category(id = 10L, name = "Café y Snacks", icon = "coffee", colorHex = "#D97706", kind = CategoryKind.GASTO, isAntCandidate = true)
    )

    var lastRegisteredTransaction: Transaction? = null

    override fun observeWallets(): Flow<List<Wallet>> = flowOf(wallets.values.toList())
    override fun observeWallet(id: Long): Flow<Wallet?> = flowOf(wallets[id])
    override suspend fun getWalletById(id: Long): Wallet? = wallets[id]
    override suspend fun createWallet(wallet: Wallet): Long {
        wallets[wallet.id] = wallet
        return wallet.id
    }
    override suspend fun updateWallet(wallet: Wallet) {
        wallets[wallet.id] = wallet
    }
    override suspend fun archiveWallet(id: Long) {
        wallets.remove(id)
    }

    override fun observeFinancialSummary(fromMillis: Long, toMillis: Long): Flow<FinancialSummary> =
        flowOf(FinancialSummary(totalNetWorth = Money(1_600_000L)))

    override fun observeRecentTransactions(limit: Int): Flow<List<Transaction>> = flowOf(emptyList())
    override fun getTransactionsPaged(
        query: String,
        type: TransactionType?,
        walletId: Long?,
        categoryId: Long?,
        onlyAntExpense: Boolean,
        fromMillis: Long?,
        toMillis: Long?
    ): Flow<androidx.paging.PagingData<Transaction>> = flowOf(androidx.paging.PagingData.empty())
    override suspend fun getTransactionById(id: Long): Transaction? = null

    override suspend fun registerTransaction(transaction: Transaction): Long {
        lastRegisteredTransaction = transaction
        return 101L
    }

    override suspend fun updateTransaction(transaction: Transaction) {}
    override suspend fun restoreTransaction(transaction: Transaction): Long = 101L
    override suspend fun deleteTransaction(id: Long) {}

    override fun observePockets(): Flow<List<Pocket>> = flowOf(pockets.values.toList())
    override fun observePocket(id: Long): Flow<Pocket?> = flowOf(pockets[id])
    override suspend fun getPocketById(id: Long): Pocket? = pockets[id]
    override suspend fun createPocket(pocket: Pocket): Long = 1L
    override suspend fun updatePocket(pocket: Pocket) {}
    override suspend fun archivePocket(id: Long) {}

    override suspend fun contributeToPocket(pocketId: Long, walletId: Long, amount: Money): Boolean = true
    override suspend fun withdrawFromPocket(pocketId: Long, walletId: Long, amount: Money): Boolean = true

    override fun observeCategories(kind: CategoryKind?): Flow<List<Category>> = flowOf(categories.values.toList())
    override suspend fun getCategoryById(id: Long): Category? = categories[id]
}
