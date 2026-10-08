package com.andina.finanzas.ui.transactions

import androidx.paging.PagingData
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
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
import java.util.Calendar

/**
 * Quality Gate de la Fase 10: Pruebas unitarias de filtros multi-criterio,
 * agrupación temporal y ciclo de vida de eliminación con ventana "Deshacer" (5s).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsFilterTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeTransactionsFinanceRepository
    private lateinit var viewModel: TransactionsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTransactionsFinanceRepository()
        viewModel = TransactionsViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasDefaultFilters_andEmptyQuery() = runTest {
        advanceUntilIdle()
        val filter = viewModel.filter.value
        assertEquals("", filter.query)
        assertNull(filter.type)
        assertNull(filter.walletId)
        assertNull(filter.categoryId)
        assertFalse(filter.onlyAntExpense)
        assertEquals(DateRangeFilterPreset.ALL, filter.datePreset)
        assertEquals(0, filter.activeFilterCount)
        assertTrue(filter.isDefault)
    }

    @Test
    fun searchQueryFilter_updatesFilterState() = runTest {
        viewModel.onEvent(TransactionsEvent.OnQueryChanged("Tinto campesino"))
        advanceUntilIdle()

        val filter = viewModel.filter.value
        assertEquals("Tinto campesino", filter.query)
        assertEquals(1, filter.activeFilterCount)
        assertFalse(filter.isDefault)
    }

    @Test
    fun typeFilter_updatesFilterState_andActiveCount() = runTest {
        viewModel.onEvent(TransactionsEvent.OnTypeSelected(TransactionType.GASTO))
        advanceUntilIdle()

        val filter = viewModel.filter.value
        assertEquals(TransactionType.GASTO, filter.type)
        assertEquals(1, filter.activeFilterCount)
    }

    @Test
    fun walletFilter_updatesFilterState() = runTest {
        viewModel.onEvent(TransactionsEvent.OnWalletSelected(1L))
        advanceUntilIdle()

        val filter = viewModel.filter.value
        assertEquals(1L, filter.walletId)
        assertEquals(1, filter.activeFilterCount)
    }

    @Test
    fun antExpenseToggle_updatesFilterState() = runTest {
        viewModel.onEvent(TransactionsEvent.OnToggleAntExpense(true))
        advanceUntilIdle()

        val filter = viewModel.filter.value
        assertTrue(filter.onlyAntExpense)
        assertEquals(1, filter.activeFilterCount)
    }

    @Test
    fun datePreset_updatesPreset_andActiveCount() = runTest {
        viewModel.onEvent(TransactionsEvent.OnDatePresetSelected(DateRangeFilterPreset.THIS_MONTH))
        advanceUntilIdle()

        val filter = viewModel.filter.value
        assertEquals(DateRangeFilterPreset.THIS_MONTH, filter.datePreset)
        assertEquals(1, filter.activeFilterCount)
    }

    @Test
    fun multipleFilters_accumulateActiveCount() = runTest {
        viewModel.onEvent(TransactionsEvent.OnQueryChanged("Almuerzo"))
        viewModel.onEvent(TransactionsEvent.OnTypeSelected(TransactionType.GASTO))
        viewModel.onEvent(TransactionsEvent.OnWalletSelected(2L))
        viewModel.onEvent(TransactionsEvent.OnToggleAntExpense(true))
        viewModel.onEvent(TransactionsEvent.OnDatePresetSelected(DateRangeFilterPreset.TODAY))
        advanceUntilIdle()

        val filter = viewModel.filter.value
        assertEquals(5, filter.activeFilterCount)
        assertFalse(filter.isDefault)
    }

    @Test
    fun resetFilters_restoresAllDefaults() = runTest {
        viewModel.onEvent(TransactionsEvent.OnQueryChanged("Gasolina"))
        viewModel.onEvent(TransactionsEvent.OnTypeSelected(TransactionType.GASTO))
        viewModel.onEvent(TransactionsEvent.OnToggleAntExpense(true))
        advanceUntilIdle()
        assertEquals(3, viewModel.filter.value.activeFilterCount)

        viewModel.onEvent(TransactionsEvent.OnResetFilters)
        advanceUntilIdle()

        val filter = viewModel.filter.value
        assertEquals("", filter.query)
        assertNull(filter.type)
        assertFalse(filter.onlyAntExpense)
        assertEquals(0, filter.activeFilterCount)
        assertTrue(filter.isDefault)
    }

    @Test
    fun filterSheetVisibility_togglesCorrectly() = runTest {
        assertFalse(viewModel.state.value.isFilterSheetVisible)

        viewModel.onEvent(TransactionsEvent.OnSetFilterSheetVisible(true))
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isFilterSheetVisible)

        viewModel.onEvent(TransactionsEvent.OnSetFilterSheetVisible(false))
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isFilterSheetVisible)
    }

    @Test
    fun deleteTransaction_callsRepository_andStoresPendingUndo() = runTest {
        val testTx = Transaction(
            id = 42L,
            type = TransactionType.GASTO,
            amount = Money(15_000L),
            walletId = 1L,
            note = "Arepa de chócolo",
            occurredAt = System.currentTimeMillis()
        )

        viewModel.onEvent(TransactionsEvent.OnDeleteTransaction(testTx))
        testScheduler.runCurrent()

        assertEquals(42L, fakeRepository.lastDeletedId)
        assertEquals(testTx, viewModel.state.value.lastDeletedTransaction)
    }

    @Test
    fun undoDelete_restoresTransactionInRepository_andClearsPending() = runTest {
        val testTx = Transaction(
            id = 99L,
            type = TransactionType.INGRESO,
            amount = Money(250_000L),
            walletId = 2L,
            note = "Honorarios freelance",
            occurredAt = System.currentTimeMillis()
        )

        // 1. Eliminar movimiento
        viewModel.onEvent(TransactionsEvent.OnDeleteTransaction(testTx))
        testScheduler.runCurrent()
        assertEquals(99L, fakeRepository.lastDeletedId)
        assertNotNull(viewModel.state.value.lastDeletedTransaction)

        // 2. Presionar Deshacer
        viewModel.onEvent(TransactionsEvent.OnUndoDelete)
        testScheduler.runCurrent()

        // 3. Verificar restauración
        assertEquals(testTx, fakeRepository.lastRestoredTransaction)
        assertNull(viewModel.state.value.lastDeletedTransaction)
    }

    @Test
    fun deleteTransaction_after5Seconds_clearsPendingUndo() = runTest {
        val testTx = Transaction(
            id = 55L,
            type = TransactionType.GASTO,
            amount = Money(8_000L),
            walletId = 1L,
            note = "Café",
            occurredAt = System.currentTimeMillis()
        )

        viewModel.onEvent(TransactionsEvent.OnDeleteTransaction(testTx))
        testScheduler.runCurrent()
        assertNotNull(viewModel.state.value.lastDeletedTransaction)

        // Avanzar el tiempo más allá de la ventana de 5 segundos
        advanceTimeBy(5001L)
        testScheduler.runCurrent()

        assertNull(viewModel.state.value.lastDeletedTransaction)
    }

    @Test
    fun dateRangeBounds_resolvesTodayCorrectly() {
        val now = 1791400000000L // Epoch fijo de prueba
        val filter = TransactionsFilter(datePreset = DateRangeFilterPreset.TODAY)
        val (from, to) = filter.resolveDateRangeBounds(now)

        assertNotNull(from)
        assertNull(to)
        assertTrue(from!! <= now)
    }

    @Test
    fun dateGroupHeader_classifiesTodayAndYesterdayProperly() {
        val now = System.currentTimeMillis()
        assertEquals("Hoy", getDateGroupHeader(now, now))

        val yesterday = now - (24L * 60L * 60L * 1000L)
        assertEquals("Ayer", getDateGroupHeader(yesterday, now))
    }
}

/**
 * Repositorio falso interactivo para pruebas unitarias de transacciones y libro mayor.
 */
private class FakeTransactionsFinanceRepository : FinanceRepository {
    var lastDeletedId: Long? = null
    var lastRestoredTransaction: Transaction? = null
    var lastUpdatedTransaction: Transaction? = null

    private val walletsFlow = MutableStateFlow(
        listOf(
            Wallet(id = 1L, name = "Nequi", type = WalletType.NEQUI, currentBalance = Money(500_000L), colorHex = "#700054"),
            Wallet(id = 2L, name = "Efectivo", type = WalletType.EFECTIVO, currentBalance = Money(80_000L), colorHex = "#435C3C")
        )
    )

    private val categoriesFlow = MutableStateFlow(
        listOf(
            Category(id = 1L, name = "Alimentación", icon = "fastfood", colorHex = "#435C3C", kind = CategoryKind.GASTO),
            Category(id = 2L, name = "Transporte", icon = "directions_bus", colorHex = "#C58934", kind = CategoryKind.GASTO)
        )
    )

    override fun observeWallets(): Flow<List<Wallet>> = walletsFlow.asStateFlow()
    override fun observeWallet(id: Long): Flow<Wallet?> = flowOf(walletsFlow.value.find { it.id == id })
    override suspend fun getWalletById(id: Long): Wallet? = walletsFlow.value.find { it.id == id }
    override suspend fun createWallet(wallet: Wallet): Long = wallet.id
    override suspend fun updateWallet(wallet: Wallet) {}
    override suspend fun archiveWallet(id: Long) {}

    override fun observeFinancialSummary(fromMillis: Long, toMillis: Long): Flow<FinancialSummary> =
        flowOf(FinancialSummary())

    override fun observeRecentTransactions(limit: Int): Flow<List<Transaction>> = flowOf(emptyList())

    override fun getTransactionsPaged(
        query: String,
        type: TransactionType?,
        walletId: Long?,
        categoryId: Long?,
        onlyAntExpense: Boolean,
        fromMillis: Long?,
        toMillis: Long?
    ): Flow<PagingData<Transaction>> = flowOf(PagingData.empty())

    override suspend fun getTransactionById(id: Long): Transaction? = null

    override suspend fun registerTransaction(transaction: Transaction): Long = 101L

    override suspend fun updateTransaction(transaction: Transaction) {
        lastUpdatedTransaction = transaction
    }

    override suspend fun restoreTransaction(transaction: Transaction): Long {
        lastRestoredTransaction = transaction
        return transaction.id
    }

    override suspend fun deleteTransaction(id: Long) {
        lastDeletedId = id
    }

    override fun observePockets(): Flow<List<Pocket>> = flowOf(emptyList())
    override fun observePocket(id: Long): Flow<Pocket?> = flowOf(null)
    override suspend fun getPocketById(id: Long): Pocket? = null
    override suspend fun createPocket(pocket: Pocket): Long = 1L
    override suspend fun updatePocket(pocket: Pocket) {}
    override suspend fun archivePocket(id: Long) {}
    override suspend fun contributeToPocket(pocketId: Long, walletId: Long, amount: Money): Boolean = true
    override suspend fun withdrawFromPocket(pocketId: Long, walletId: Long, amount: Money): Boolean = true

    override fun observeCategories(kind: CategoryKind?): Flow<List<Category>> = categoriesFlow.asStateFlow()
    override suspend fun getCategoryById(id: Long): Category? = categoriesFlow.value.find { it.id == id }
}
