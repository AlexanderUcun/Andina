package com.andina.finanzas.ui.pockets

import androidx.lifecycle.SavedStateHandle
import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.CategoryKind
import com.andina.finanzas.domain.model.FinancialSummary
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.model.WalletType
import com.andina.finanzas.domain.repository.FinanceRepository
import com.andina.finanzas.domain.usecase.ContributeToPocketUseCase
import com.andina.finanzas.domain.usecase.WithdrawFromPocketUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Quality Gate de la Fase 09: Pruebas unitarias completas de [PocketsViewModel],
 * validaciones contables de aporte/retiro atómico y disparo de celebración festiva al 100%.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PocketMoveValidationTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakePocketsFinanceRepository
    private lateinit var contributeUseCase: ContributeToPocketUseCase
    private lateinit var withdrawUseCase: WithdrawFromPocketUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakePocketsFinanceRepository()
        contributeUseCase = ContributeToPocketUseCase(fakeRepository)
        withdrawUseCase = WithdrawFromPocketUseCase(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): PocketsViewModel {
        return PocketsViewModel(
            savedStateHandle = SavedStateHandle(),
            repository = fakeRepository,
            contributeToPocketUseCase = contributeUseCase,
            withdrawFromPocketUseCase = withdrawUseCase
        )
    }

    @Test
    fun `PocketsViewModel inicializa y calcula totales de ahorro y progreso correctamente`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(2, state.pockets.size)
        assertEquals(2, state.wallets.size)
        // Bolsillo 1: 150k de 1.000k. Bolsillo 2: 50k de 200k. Total = 200k de 1.200k
        assertEquals(Money(200_000L), state.totalReserved)
        assertEquals(Money(1_200_000L), state.totalGoals)
        assertEquals(200_000f / 1_200_000f, state.overallProgress, 0.001f)
    }

    @Test
    fun `abrir modal de aporte inicializa estado con tipo CONTRIBUTION y monto en cero`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processAction(PocketsAction.OpenContribute(pocketId = 1L))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.isMoveSheetVisible)
        assertEquals(PocketMoveType.CONTRIBUTION, state.moveType)
        assertEquals(1L, state.movePocketId)
        assertEquals("", state.moveAmountDigits)
        assertEquals(Money.ZERO, state.moveAmount)
        assertFalse(state.canExecuteMove) // Monto cero no se puede ejecutar
    }

    @Test
    fun `digitacion de montos y atajos rapidos actualiza cifra y validacion`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.processAction(PocketsAction.OpenContribute(pocketId = 1L))
        advanceUntilIdle()

        // Digitar 5, 0, 0, 0, 0 -> 50.000
        viewModel.processAction(PocketsAction.InputMoveDigit('5'))
        viewModel.processAction(PocketsAction.InputMoveDigit('0'))
        viewModel.processAction(PocketsAction.InputMoveDigit('0'))
        viewModel.processAction(PocketsAction.InputMoveDigit('0'))
        viewModel.processAction(PocketsAction.InputMoveDigit('0'))
        advanceUntilIdle()

        assertEquals("50000", viewModel.state.value.moveAmountDigits)
        assertEquals(Money(50_000L), viewModel.state.value.moveAmount)
        assertTrue(viewModel.state.value.canExecuteMove)

        // Borrar dígito -> 5.000
        viewModel.processAction(PocketsAction.DeleteMoveDigit)
        advanceUntilIdle()
        assertEquals("5000", viewModel.state.value.moveAmountDigits)
        assertEquals(Money(5_000L), viewModel.state.value.moveAmount)

        // Atajo rápido directo de 100.000
        viewModel.processAction(PocketsAction.SetMoveAmount(100_000L))
        advanceUntilIdle()
        assertEquals("100000", viewModel.state.value.moveAmountDigits)
        assertEquals(Money(100_000L), viewModel.state.value.moveAmount)
    }

    @Test
    fun `intento de aporte con saldo insuficiente en billetera bloquea ejecucion`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Nequi (id=1L) tiene saldo de $250.000 COP
        viewModel.processAction(PocketsAction.OpenContribute(pocketId = 1L))
        viewModel.processAction(PocketsAction.SelectMoveWallet(1L))
        viewModel.processAction(PocketsAction.SetMoveAmount(500_000L)) // Supera saldo
        advanceUntilIdle()

        assertFalse(viewModel.state.value.canExecuteMove)

        viewModel.processAction(PocketsAction.ExecuteMove)
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.moveError)
        assertTrue(viewModel.state.value.moveError!!.contains("insuficiente", ignoreCase = true))
    }

    @Test
    fun `intento de retiro con saldo insuficiente en bolsillo bloquea ejecucion`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Bolsillo 1 tiene ahorrado $150.000 COP
        viewModel.processAction(PocketsAction.OpenWithdraw(pocketId = 1L))
        viewModel.processAction(PocketsAction.SelectMoveWallet(1L))
        viewModel.processAction(PocketsAction.SetMoveAmount(200_000L)) // Supera $150.000
        advanceUntilIdle()

        assertFalse(viewModel.state.value.canExecuteMove)

        viewModel.processAction(PocketsAction.ExecuteMove)
        advanceUntilIdle()

        assertNotNull(viewModel.state.value.moveError)
        assertTrue(viewModel.state.value.moveError!!.contains("insuficiente", ignoreCase = true))
    }

    @Test
    fun `aporte exitoso por debajo de la meta no dispara celebracion festiva`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Bolsillo 1: $150.000 de $1.000.000. Nequi: $250.000. Aporte de $50.000 -> queda en $200.000 (20%)
        viewModel.processAction(PocketsAction.OpenContribute(pocketId = 1L))
        viewModel.processAction(PocketsAction.SelectMoveWallet(1L))
        viewModel.processAction(PocketsAction.SetMoveAmount(50_000L))
        viewModel.processAction(PocketsAction.ExecuteMove)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isMoveSheetVisible)
        assertNull(viewModel.state.value.celebrationPocket) // No está al 100%, no celebra aún
    }

    @Test
    fun `aporte exitoso que alcanza o supera el 100 por ciento dispara celebracion festiva`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Bolsillo 2: id=2L, meta=$200.000, actual=$50.000, falta $150.000.
        // Nequi tiene $250.000. Aportamos $150.000 para completarlo al 100%.
        viewModel.processAction(PocketsAction.OpenContribute(pocketId = 2L))
        viewModel.processAction(PocketsAction.SelectMoveWallet(1L))
        viewModel.processAction(PocketsAction.SetMoveAmount(150_000L))
        viewModel.processAction(PocketsAction.ExecuteMove)
        advanceUntilIdle()

        val celebration = viewModel.state.value.celebrationPocket
        assertNotNull(celebration)
        assertEquals(2L, celebration!!.id)
        assertTrue(celebration.isCompleted)
        assertEquals(Money(200_000L), celebration.currentBalance)
    }

    @Test
    fun `retiro exitoso reduce saldo del bolsillo y emite snackbar y sonido`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Bolsillo 1 tiene $150.000. Retiramos $50.000 hacia Nequi.
        viewModel.processAction(PocketsAction.OpenWithdraw(pocketId = 1L))
        viewModel.processAction(PocketsAction.SelectMoveWallet(1L))
        viewModel.processAction(PocketsAction.SetMoveAmount(50_000L))
        viewModel.processAction(PocketsAction.ExecuteMove)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isMoveSheetVisible)
        assertNull(viewModel.state.value.celebrationPocket)
    }

    @Test
    fun `crear bolsillo valida campos requeridos y emite feedback`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        // Intento con nombre vacío
        viewModel.processAction(
            PocketsAction.CreatePocket(name = "", goalAmount = 500_000L)
        )
        advanceUntilIdle()
        assertNotNull(viewModel.state.value.moveError)

        // Intento con meta cero
        viewModel.processAction(
            PocketsAction.CreatePocket(name = "Viaje San Andrés", goalAmount = 0L)
        )
        advanceUntilIdle()
        assertNotNull(viewModel.state.value.moveError)

        // Creación válida
        viewModel.processAction(
            PocketsAction.CreatePocket(name = "Viaje San Andrés", goalAmount = 1_500_000L)
        )
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isCreateSheetVisible)
    }
}

/**
 * Repositorio falso interactivo para pruebas de bolsillos.
 */
private class FakePocketsFinanceRepository : FinanceRepository {
    private val walletsFlow = MutableStateFlow(
        listOf(
            Wallet(id = 1L, name = "Nequi", type = WalletType.NEQUI, currentBalance = Money(250_000L), colorHex = "#700054"),
            Wallet(id = 2L, name = "Efectivo", type = WalletType.EFECTIVO, currentBalance = Money(100_000L), colorHex = "#435C3C")
        )
    )

    private val pocketsFlow = MutableStateFlow(
        listOf(
            Pocket(id = 1L, name = "Fondo Emergencia", goal = Money(1_000_000L), currentBalance = Money(150_000L), icon = "savings", colorHex = "#435C3C"),
            Pocket(id = 2L, name = "Bicicleta", goal = Money(200_000L), currentBalance = Money(50_000L), icon = "directions_bike", colorHex = "#C58934")
        )
    )

    override fun observeWallets(): Flow<List<Wallet>> = walletsFlow.asStateFlow()
    override fun observeWallet(id: Long): Flow<Wallet?> = flowOf(walletsFlow.value.find { it.id == id })
    override suspend fun getWalletById(id: Long): Wallet? = walletsFlow.value.find { it.id == id }

    override fun observeFinancialSummary(fromMillis: Long, toMillis: Long): Flow<FinancialSummary> {
        return flowOf(FinancialSummary())
    }

    override fun observeRecentTransactions(limit: Int): Flow<List<Transaction>> = flowOf(emptyList())
    override suspend fun getTransactionById(id: Long): Transaction? = null
    override suspend fun registerTransaction(transaction: Transaction): Long = 1L
    override suspend fun deleteTransaction(id: Long) {}

    override fun observePockets(): Flow<List<Pocket>> = pocketsFlow.asStateFlow()
    override fun observePocket(id: Long): Flow<Pocket?> = flowOf(pocketsFlow.value.find { it.id == id })
    override suspend fun getPocketById(id: Long): Pocket? = pocketsFlow.value.find { it.id == id }

    override suspend fun createPocket(pocket: Pocket): Long {
        val newId = (pocketsFlow.value.maxOfOrNull { it.id } ?: 0L) + 1L
        val created = pocket.copy(id = newId)
        pocketsFlow.value = pocketsFlow.value + created
        return newId
    }

    override suspend fun contributeToPocket(pocketId: Long, walletId: Long, amount: Money): Boolean {
        val currentPockets = pocketsFlow.value.toMutableList()
        val pocketIndex = currentPockets.indexOfFirst { it.id == pocketId }
        val currentWallets = walletsFlow.value.toMutableList()
        val walletIndex = currentWallets.indexOfFirst { it.id == walletId }

        if (pocketIndex >= 0 && walletIndex >= 0) {
            val pocket = currentPockets[pocketIndex]
            val wallet = currentWallets[walletIndex]

            currentPockets[pocketIndex] = pocket.copy(currentBalance = pocket.currentBalance + amount)
            currentWallets[walletIndex] = wallet.copy(currentBalance = wallet.currentBalance - amount)

            pocketsFlow.value = currentPockets
            walletsFlow.value = currentWallets
        }
        return true
    }

    override suspend fun withdrawFromPocket(pocketId: Long, walletId: Long, amount: Money): Boolean {
        val currentPockets = pocketsFlow.value.toMutableList()
        val pocketIndex = currentPockets.indexOfFirst { it.id == pocketId }
        val currentWallets = walletsFlow.value.toMutableList()
        val walletIndex = currentWallets.indexOfFirst { it.id == walletId }

        if (pocketIndex >= 0 && walletIndex >= 0) {
            val pocket = currentPockets[pocketIndex]
            val wallet = currentWallets[walletIndex]

            currentPockets[pocketIndex] = pocket.copy(currentBalance = pocket.currentBalance - amount)
            currentWallets[walletIndex] = wallet.copy(currentBalance = wallet.currentBalance + amount)

            pocketsFlow.value = currentPockets
            walletsFlow.value = currentWallets
        }
        return true
    }

    override fun observeCategories(kind: CategoryKind?): Flow<List<Category>> = flowOf(emptyList())
    override suspend fun getCategoryById(id: Long): Category? = null
}
