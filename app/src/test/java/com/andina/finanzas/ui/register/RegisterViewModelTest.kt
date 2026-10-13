package com.andina.finanzas.ui.register

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
import com.andina.finanzas.domain.usecase.DetectAntExpensesUseCase
import com.andina.finanzas.domain.usecase.RegisterTransactionUseCase
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
 * Quality Gate de la Fase 07: Pruebas unitarias completas de [RegisterViewModel]
 * y [SpanishMoneyWords].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeFinanceRepository
    private lateinit var antDetector: DetectAntExpensesUseCase
    private lateinit var registerUseCase: RegisterTransactionUseCase
    private lateinit var intelligenceCore: com.andina.finanzas.core.intelligence.AndinaIntelligenceCore

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeFinanceRepository()
        antDetector = DetectAntExpensesUseCase()
        registerUseCase = RegisterTransactionUseCase(fakeRepository, antDetector)
        intelligenceCore = com.andina.finanzas.core.intelligence.AndinaIntelligenceCore()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `SpanishMoneyWords convierte montos colombianos correctamente a palabras`() {
        assertEquals("Cero pesos", SpanishMoneyWords.toWords(0L))
        assertEquals("Diez mil pesos", SpanishMoneyWords.toWords(10_000L))
        assertEquals("Cincuenta mil pesos", SpanishMoneyWords.toWords(50_000L))
        assertEquals("Ciento veinte mil pesos", SpanishMoneyWords.toWords(120_000L))
        assertEquals("Un millón de pesos", SpanishMoneyWords.toWords(1_000_000L))
        assertEquals("Dos millones quinientos mil pesos", SpanishMoneyWords.toWords(2_500_000L))
        assertEquals("Catorce millones ochocientos cincuenta mil pesos", SpanishMoneyWords.toWords(14_850_000L))
    }

    @Test
    fun `digitacion progresiva forma el monto correcto y evita ceros a la izquierda`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle()
        val viewModel = RegisterViewModel(savedState, registerUseCase, fakeRepository, intelligenceCore)
        advanceUntilIdle()

        // Digitar '0' al inicio debe ser ignorado
        viewModel.onAction(RegisterAction.DigitPressed('0'))
        assertEquals("", viewModel.state.value.amountDigits)

        // Digitar 5, 0, 0, 0, 0 -> 50.000
        viewModel.onAction(RegisterAction.DigitPressed('5'))
        viewModel.onAction(RegisterAction.DigitPressed('0'))
        viewModel.onAction(RegisterAction.DigitPressed('0'))
        viewModel.onAction(RegisterAction.DigitPressed('0'))
        viewModel.onAction(RegisterAction.DigitPressed('0'))

        val state = viewModel.state.value
        assertEquals("50000", state.amountDigits)
        assertEquals(Money(50_000L), state.amountMoney)
        assertEquals("$ 50.000 COP", state.amountFormatted)
        assertEquals("Cincuenta mil pesos", state.amountInWords)
        assertEquals(0, state.lastPressedDigit)
    }

    @Test
    fun `backspace y clear limpian los digitos correctamente`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle(mapOf("reg_amount_digits" to "15000"))
        val viewModel = RegisterViewModel(savedState, registerUseCase, fakeRepository, intelligenceCore)
        advanceUntilIdle()

        assertEquals("15000", viewModel.state.value.amountDigits)

        viewModel.onAction(RegisterAction.BackspacePressed)
        assertEquals("1500", viewModel.state.value.amountDigits)

        viewModel.onAction(RegisterAction.ClearAmount)
        assertEquals("", viewModel.state.value.amountDigits)
        assertEquals(Money.ZERO, viewModel.state.value.amountMoney)
    }

    @Test
    fun `atajos rapidos de incremento suman al monto actual`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle()
        val viewModel = RegisterViewModel(savedState, registerUseCase, fakeRepository, intelligenceCore)
        advanceUntilIdle()

        viewModel.onAction(RegisterAction.AddQuickAmount(10_000L))
        assertEquals("10000", viewModel.state.value.amountDigits)

        viewModel.onAction(RegisterAction.AddQuickAmount(50_000L))
        assertEquals("60000", viewModel.state.value.amountDigits)
        assertEquals(Money(60_000L), viewModel.state.value.amountMoney)
    }

    @Test
    fun `proyeccion de saldo restante se calcula en tiempo real para la billetera`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle()
        val viewModel = RegisterViewModel(savedState, registerUseCase, fakeRepository, intelligenceCore)
        advanceUntilIdle()

        // Seleccionar Nequi (saldo $100.000)
        viewModel.onAction(RegisterAction.WalletSelected(1L))
        viewModel.onAction(RegisterAction.TypeChanged(TransactionType.GASTO))
        viewModel.onAction(RegisterAction.AddQuickAmount(45_000L))

        val state = viewModel.state.value
        assertNotNull(state.projectedRemainingBalance)
        assertEquals(Money(55_000L), state.projectedRemainingBalance)

        // Cambiar a Ingreso: $100.000 + $45.000 = $145.000
        viewModel.onAction(RegisterAction.TypeChanged(TransactionType.INGRESO))
        assertEquals(Money(145_000L), viewModel.state.value.projectedRemainingBalance)
    }

    @Test
    fun `deteccion y toggle de gasto hormiga operan adecuadamente`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle()
        val viewModel = RegisterViewModel(savedState, registerUseCase, fakeRepository, intelligenceCore)
        advanceUntilIdle()

        // Categoría café/panadería es candidata a hormiga (ID 1)
        viewModel.onAction(RegisterAction.CategorySelected(1L))
        viewModel.onAction(RegisterAction.AddQuickAmount(15_000L)) // <= $25.000

        val state = viewModel.state.value
        assertTrue(state.isAntExpense)

        // El usuario puede forzar apagarlo
        viewModel.onAction(RegisterAction.ToggleAntExpense)
        assertFalse(viewModel.state.value.isAntExpense)

        // Y volverlo a encender
        viewModel.onAction(RegisterAction.ToggleAntExpense)
        assertTrue(viewModel.state.value.isAntExpense)
    }

    @Test
    fun `canSave valida que el formulario este completo antes de permitir guardar`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle()
        val viewModel = RegisterViewModel(savedState, registerUseCase, fakeRepository, intelligenceCore)
        advanceUntilIdle()

        // Sin monto -> canSave = false
        assertFalse(viewModel.state.value.canSave)

        // Con monto pero sin billetera destino en transferencia -> canSave = false
        viewModel.onAction(RegisterAction.AddQuickAmount(20_000L))
        viewModel.onAction(RegisterAction.TypeChanged(TransactionType.TRANSFERENCIA))
        viewModel.onAction(RegisterAction.WalletSelected(1L))
        assertFalse(viewModel.state.value.canSave)

        // Transferencia con destino igual a origen -> canSave = false
        viewModel.onAction(RegisterAction.ToWalletSelected(1L))
        assertFalse(viewModel.state.value.canSave)

        // Transferencia con destino diferente -> canSave = true
        viewModel.onAction(RegisterAction.ToWalletSelected(2L))
        assertTrue(viewModel.state.value.canSave)

        // Gasto normal con billetera y monto -> canSave = true
        viewModel.onAction(RegisterAction.TypeChanged(TransactionType.GASTO))
        assertTrue(viewModel.state.value.canSave)
    }

    @Test
    fun `guardado exitoso registra la transaccion y emite efecto Saved`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle()
        val viewModel = RegisterViewModel(savedState, registerUseCase, fakeRepository, intelligenceCore)
        advanceUntilIdle()

        viewModel.onAction(RegisterAction.WalletSelected(1L))
        viewModel.onAction(RegisterAction.CategorySelected(1L))
        viewModel.onAction(RegisterAction.AddQuickAmount(50_000L))

        viewModel.onAction(RegisterAction.Save)
        advanceUntilIdle()

        val effect = viewModel.effects.first()
        assertTrue(effect is RegisterEffect.Saved)
        val savedEffect = effect as RegisterEffect.Saved
        assertEquals(101L, savedEffect.txId)
        assertTrue(savedEffect.message.contains("50.000"))

        // La persistencia de dígitos debe haberse limpiado
        assertEquals("", viewModel.state.value.amountDigits)
    }

    @Test
    fun `recuperacion de SavedStateHandle restaura digitos y selecciones tras proceso muerto`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle(
            mapOf(
                "reg_amount_digits" to "85000",
                "reg_wallet_id" to 2L,
                "reg_note" to "Mercado semanal",
                "reg_is_ant_override" to false
            )
        )

        val viewModel = RegisterViewModel(savedState, registerUseCase, fakeRepository, intelligenceCore)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("85000", state.amountDigits)
        assertEquals(Money(85_000L), state.amountMoney)
        assertEquals(2L, state.walletId)
        assertEquals("Mercado semanal", state.note)
        assertEquals(false, state.isAntOverride)
    }

    @Test
    fun `cambio entre Gasto e Ingreso filtra las categorias correctamente`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle()
        val viewModel = RegisterViewModel(savedState, registerUseCase, fakeRepository, intelligenceCore)
        advanceUntilIdle()

        // Estado inicial GASTO: debe contener categorías de gasto (Café & Panadería, Transporte)
        assertEquals(TransactionType.GASTO, viewModel.state.value.type)
        val initialCategories = viewModel.state.value.categories
        assertEquals(2, initialCategories.size)
        assertTrue(initialCategories.all { it.kind == CategoryKind.GASTO })

        // Cambiar a INGRESO: debe mostrar solo Salario
        viewModel.onAction(RegisterAction.TypeChanged(TransactionType.INGRESO))
        advanceUntilIdle()

        val incomeCategories = viewModel.state.value.categories
        assertEquals(1, incomeCategories.size)
        assertEquals("Salario", incomeCategories.first().name)
        assertEquals(CategoryKind.INGRESO, incomeCategories.first().kind)

        // Volver a GASTO: debe recuperar las categorías de gasto de la caché
        viewModel.onAction(RegisterAction.TypeChanged(TransactionType.GASTO))
        advanceUntilIdle()

        val restoredCategories = viewModel.state.value.categories
        assertEquals(2, restoredCategories.size)
        assertTrue(restoredCategories.all { it.kind == CategoryKind.GASTO })
    }

    @Test
    fun `limite estricto de 8 digitos y tope de 99_999_999 COP se respeta`() = runTest(testDispatcher) {
        val savedState = SavedStateHandle()
        val viewModel = RegisterViewModel(savedState, registerUseCase, fakeRepository, intelligenceCore)
        advanceUntilIdle()

        // Intentar escribir más de 8 dígitos
        "123456789".forEach { digit ->
            viewModel.onAction(RegisterAction.DigitPressed(digit))
        }

        // Solo debe aceptar hasta 8 dígitos ("12345678")
        assertEquals(8, viewModel.state.value.amountDigits.length)
        assertEquals("12345678", viewModel.state.value.amountDigits)
    }
}

/**
 * Repositorio falso en memoria para pruebas unitarias de ViewModel.
 */
private class FakeFinanceRepository : FinanceRepository {
    private val wallets = listOf(
        Wallet(id = 1L, name = "Nequi", type = WalletType.NEQUI, currentBalance = Money(100_000L), colorHex = "#700054"),
        Wallet(id = 2L, name = "Daviplata", type = WalletType.DAVIPLATA, currentBalance = Money(50_000L), colorHex = "#B32D2D")
    )

    private val categories = listOf(
        Category(id = 1L, name = "Café & Panadería", icon = "fastfood", colorHex = "#4A6B47", kind = CategoryKind.GASTO, isAntCandidate = true),
        Category(id = 2L, name = "Transporte", icon = "directions_bus", colorHex = "#845400", kind = CategoryKind.GASTO, isAntCandidate = true),
        Category(id = 3L, name = "Salario", icon = "local_atm", colorHex = "#386A20", kind = CategoryKind.INGRESO, isAntCandidate = false)
    )

    override fun observeWallets(): Flow<List<Wallet>> = flowOf(wallets)
    override fun observeArchivedWallets(): Flow<List<Wallet>> = flowOf(emptyList())
    override fun observeWallet(id: Long): Flow<Wallet?> = flowOf(wallets.find { it.id == id })
    override suspend fun getWalletById(id: Long): Wallet? = wallets.find { it.id == id }
    override suspend fun createWallet(wallet: Wallet): Long = wallet.id
    override suspend fun updateWallet(wallet: Wallet) {}
    override suspend fun archiveWallet(id: Long) {}
    override suspend fun unarchiveWallet(id: Long) {}
    override suspend fun deleteWallet(id: Long) {}

    override fun observeFinancialSummary(fromMillis: Long, toMillis: Long): Flow<FinancialSummary> {
        return flowOf(FinancialSummary(Money(150_000L), Money(50_000L), Money(20_000L), Money(80_000L), Money(5_000L)))
    }

    override fun observeRecentTransactions(limit: Int): Flow<List<Transaction>> = flowOf(emptyList())
    override fun getTransactionsPaged(
        query: String,
        type: com.andina.finanzas.domain.model.TransactionType?,
        walletId: Long?,
        categoryId: Long?,
        onlyAntExpense: Boolean,
        fromMillis: Long?,
        toMillis: Long?
    ): Flow<androidx.paging.PagingData<Transaction>> = flowOf(androidx.paging.PagingData.empty())
    override suspend fun getTransactionById(id: Long): Transaction? = null
    override suspend fun registerTransaction(transaction: Transaction): Long = 101L
    override suspend fun updateTransaction(transaction: Transaction) {}
    override suspend fun restoreTransaction(transaction: Transaction): Long = 101L
    override suspend fun deleteTransaction(id: Long) {}

    override fun observePockets(): Flow<List<Pocket>> = flowOf(emptyList())
    override fun observePocket(id: Long): Flow<Pocket?> = flowOf(null)
    override suspend fun getPocketById(id: Long): Pocket? = null
    override suspend fun createPocket(pocket: Pocket): Long = 1L
    override suspend fun updatePocket(pocket: Pocket) {}
    override suspend fun archivePocket(id: Long) {}
    override suspend fun unarchivePocket(id: Long) {}
    override suspend fun deletePocket(id: Long) {}
    override suspend fun contributeToPocket(pocketId: Long, walletId: Long, amount: Money): Boolean = true
    override suspend fun withdrawFromPocket(pocketId: Long, walletId: Long, amount: Money): Boolean = true

    override fun observeCategories(kind: CategoryKind?): Flow<List<Category>> {
        return flowOf(if (kind != null) categories.filter { it.kind == kind } else categories)
    }

    override suspend fun getCategoryById(id: Long): Category? = categories.find { it.id == id }
    override suspend fun createCategory(category: Category): Long = 1L
    override suspend fun updateCategory(category: Category) {}
    override suspend fun deleteCategory(id: Long) {}
    override suspend fun resetDemoData() {}
}

