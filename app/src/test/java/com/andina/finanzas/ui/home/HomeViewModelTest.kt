package com.andina.finanzas.ui.home

import androidx.lifecycle.SavedStateHandle
import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.CategoryKind
import com.andina.finanzas.domain.model.FinancialSummary
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.model.WalletType
import com.andina.finanzas.domain.repository.FinanceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Quality Gate de la Fase 08: Pruebas unitarias completas de [HomeViewModel]
 * y [HomeUiState] para el Master Dashboard de Inicio.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeFinanceRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeFinanceRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `HomeViewModel inicializa y carga reactivamente el patrimonio y las billeteras`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle()
        val viewModel = HomeViewModel(savedState, fakeRepository)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(Money(550_000L), state.totalNetWorth)
        assertEquals(Money(300_000L), state.periodIncome)
        assertEquals(Money(120_000L), state.periodExpense)
        assertEquals(Money(180_000L), state.periodNetRemaining)
        assertEquals(2, state.wallets.size)
        assertEquals(1, state.pockets.size)
        assertEquals(1, state.recentTransactions.size)
    }

    @Test
    fun `modo de privacidad enmascara cifras con mascara de puntos y persiste en SavedStateHandle`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle()
        val viewModel = HomeViewModel(savedState, fakeRepository)
        advanceUntilIdle()

        // Estado inicial: visible
        assertFalse(viewModel.state.value.isPrivacyMode)
        assertEquals("$ 550.000", viewModel.state.value.formattedAmount(viewModel.state.value.totalNetWorth))

        // Alternar a privado
        viewModel.onAction(HomeAction.TogglePrivacyMode)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isPrivacyMode)
        assertEquals(true, savedState.get<Boolean>("home_privacy_mode"))
        assertEquals("••••••••", viewModel.state.value.formattedAmount(viewModel.state.value.totalNetWorth))

        // Alternar de vuelta a visible
        viewModel.onAction(HomeAction.TogglePrivacyMode)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isPrivacyMode)
        assertEquals(false, savedState.get<Boolean>("home_privacy_mode"))
        assertEquals("$ 550.000", viewModel.state.value.formattedAmount(viewModel.state.value.totalNetWorth))
    }

    @Test
    fun `recuperacion de SavedStateHandle restaura privacidad preexistente tras proceso muerto`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle(mapOf("home_privacy_mode" to true, "home_insight_dismissed" to true))
        val viewModel = HomeViewModel(savedState, fakeRepository)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isPrivacyMode)
        assertTrue(viewModel.state.value.isInsightDismissed)
        assertEquals("••••••••", viewModel.state.value.formattedAmount(viewModel.state.value.totalNetWorth))
    }

    @Test
    fun `descartar insight oculta el banner y persiste en SavedStateHandle`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle()
        val viewModel = HomeViewModel(savedState, fakeRepository)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isInsightDismissed)

        viewModel.onAction(HomeAction.DismissInsight)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isInsightDismissed)
        assertEquals(true, savedState.get<Boolean>("home_insight_dismissed"))
    }

    @Test
    fun `acciones de navegacion emiten efectos correspondientes a detalle y pestanas`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle()
        val viewModel = HomeViewModel(savedState, fakeRepository)
        advanceUntilIdle()

        // Click en billetera
        viewModel.onAction(HomeAction.WalletClick(42L))
        val effect1 = viewModel.effects.first()
        assertTrue(effect1 is HomeEffect.NavigateToWallet && effect1.walletId == 42L)

        // Click en bolsillo
        viewModel.onAction(HomeAction.PocketClick(10L))
        val effect2 = viewModel.effects.first()
        assertTrue(effect2 is HomeEffect.NavigateToPocket && effect2.pocketId == 10L)

        // Click en movimiento
        viewModel.onAction(HomeAction.TransactionClick(99L))
        val effect3 = viewModel.effects.first()
        assertTrue(effect3 is HomeEffect.NavigateToTransactionDetail && effect3.transactionId == 99L)

        // Ver todas las billeteras
        viewModel.onAction(HomeAction.SeeAllWalletsClick)
        val effect4 = viewModel.effects.first()
        assertTrue(effect4 is HomeEffect.NavigateToWalletsTab)

        // Ver todos los bolsillos
        viewModel.onAction(HomeAction.SeeAllPocketsClick)
        val effect5 = viewModel.effects.first()
        assertTrue(effect5 is HomeEffect.NavigateToPocketsTab)

        // Ver todos los movimientos
        viewModel.onAction(HomeAction.SeeAllTransactionsClick)
        val effect6 = viewModel.effects.first()
        assertTrue(effect6 is HomeEffect.NavigateToTransactionsTab)

        // Registro rápido (+)
        viewModel.onAction(HomeAction.QuickRegisterClick)
        val effect7 = viewModel.effects.first()
        assertTrue(effect7 is HomeEffect.NavigateToRegister)
    }

    @Test
    fun `calculo de nivel del paramo refleja la tasa de ahorro correctamente`() = runTest(testDispatcher) {
        // En FakeFinanceRepository, ingreso = 300k, gasto = 120k -> ahorro = 180k (60% de ahorro)
        // >= 30% corresponde a "Nivel Páramo Sagrado"
        val savedState = SavedStateHandle()
        val viewModel = HomeViewModel(savedState, fakeRepository)
        advanceUntilIdle()

        assertEquals("Nivel Páramo Sagrado", viewModel.state.value.paramoHealthLevel)
    }
}

/**
 * Repositorio simulado con datos determinísticos para pruebas unitarias del dashboard de Inicio.
 */
private class FakeFinanceRepository : FinanceRepository {
    private val testWallets = listOf(
        Wallet(id = 1L, name = "Nequi", type = WalletType.NEQUI, currentBalance = Money(250_000L), colorHex = "#700054"),
        Wallet(id = 2L, name = "Efectivo", type = WalletType.EFECTIVO, currentBalance = Money(150_000L), colorHex = "#435C3C")
    )

    private val testPockets = listOf(
        Pocket(id = 1L, name = "Fondo Emergencia", goal = Money(1_000_000L), currentBalance = Money(150_000L), icon = "savings", colorHex = "#386A20")
    )

    private val testTransactions = listOf(
        Transaction(
            id = 1L,
            amount = Money(25_000L),
            type = TransactionType.GASTO,
            walletId = 1L,
            walletName = "Nequi",
            categoryId = 1L,
            categoryName = "Restaurantes",
            categoryIcon = "fastfood",
            occurredAt = System.currentTimeMillis()
        )
    )

    override fun observeWallets(): Flow<List<Wallet>> = flowOf(testWallets)
    override fun observeWallet(id: Long): Flow<Wallet?> = flowOf(testWallets.find { it.id == id })
    override suspend fun getWalletById(id: Long): Wallet? = testWallets.find { it.id == id }

    override fun observeFinancialSummary(fromMillis: Long, toMillis: Long): Flow<FinancialSummary> {
        return flowOf(
            FinancialSummary(
                totalNetWorth = Money(550_000L),
                totalInWallets = Money(400_000L),
                totalInPockets = Money(150_000L),
                monthlyIncome = Money(300_000L),
                monthlyExpense = Money(120_000L),
                monthlyAntExpense = Money(15_000L),
                monthlyNetBalance = Money(180_000L)
            )
        )
    }

    override fun observeRecentTransactions(limit: Int): Flow<List<Transaction>> = flowOf(testTransactions)
    override fun getTransactionsPaged(
        query: String,
        type: TransactionType?,
        walletId: Long?,
        categoryId: Long?,
        onlyAntExpense: Boolean,
        fromMillis: Long?,
        toMillis: Long?
    ): Flow<androidx.paging.PagingData<Transaction>> = flowOf(androidx.paging.PagingData.empty())
    override suspend fun getTransactionById(id: Long): Transaction? = testTransactions.find { it.id == id }
    override suspend fun registerTransaction(transaction: Transaction): Long = 1L
    override suspend fun updateTransaction(transaction: Transaction) {}
    override suspend fun restoreTransaction(transaction: Transaction): Long = 1L
    override suspend fun deleteTransaction(id: Long) {}

    override fun observePockets(): Flow<List<Pocket>> = flowOf(testPockets)
    override fun observePocket(id: Long): Flow<Pocket?> = flowOf(testPockets.find { it.id == id })
    override suspend fun getPocketById(id: Long): Pocket? = testPockets.find { it.id == id }
    override suspend fun createPocket(pocket: Pocket): Long = 1L
    override suspend fun contributeToPocket(pocketId: Long, walletId: Long, amount: Money): Boolean = true
    override suspend fun withdrawFromPocket(pocketId: Long, walletId: Long, amount: Money): Boolean = true

    override fun observeCategories(kind: CategoryKind?): Flow<List<Category>> = flowOf(emptyList())
    override suspend fun getCategoryById(id: Long): Category? = null
}
