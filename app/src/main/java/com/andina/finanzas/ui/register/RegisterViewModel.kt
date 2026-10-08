package com.andina.finanzas.ui.register

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.CategoryKind
import com.andina.finanzas.domain.model.DomainError
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.repository.FinanceRepository
import com.andina.finanzas.domain.usecase.RegisterTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel resiliente para el modal y pantalla de Registro Rápido.
 *
 * Características clave:
 * 1. Resiliencia ante proceso muerto: Guarda y restaura cifras y selecciones en [SavedStateHandle].
 * 2. Reactividad en tiempo real: Conecta billeteras y categorías desde [FinanceRepository].
 * 3. Prevención de doble toque y validación estricta de dinero COP.
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val registerTransactionUseCase: RegisterTransactionUseCase,
    private val repository: FinanceRepository,
    private val intelligenceCore: com.andina.finanzas.core.intelligence.AndinaIntelligenceCore
) : ViewModel() {

    companion object {
        private const val KEY_AMOUNT_DIGITS = "reg_amount_digits"
        private const val KEY_WALLET_ID = "reg_wallet_id"
        private const val KEY_TO_WALLET_ID = "reg_to_wallet_id"
        private const val KEY_CATEGORY_ID = "reg_category_id"
        private const val KEY_NOTE = "reg_note"
        private const val KEY_TYPE = "reg_type"
        private const val KEY_IS_ANT_OVERRIDE = "reg_is_ant_override"
        private const val KEY_RECEIPT_URI = "reg_receipt_uri"

        // Límite de 8 dígitos ($99.999.999 COP) para integridad contable y control de topes
        private const val MAX_DIGITS_LENGTH = 8
        private const val MAX_AMOUNT_COP = 99_999_999L
        private const val HIGH_AMOUNT_THRESHOLD_COP = 10_000_000L
    }

    // Inicializar estado recuperando variables de SavedStateHandle o argumentos de navegación
    private val initialTypeStr: String = savedStateHandle.get<String>("initialType")
        ?: savedStateHandle.get<String>(KEY_TYPE)
        ?: "GASTO"
    private val initialType: TransactionType = TransactionType.fromString(initialTypeStr)

    private val initialWalletId: Long? = savedStateHandle.get<Long>("preselectedWalletId")
        ?: savedStateHandle.get<Long>(KEY_WALLET_ID)

    // Cache de todas las categorías recibidas del repositorio para permitir cambio reactivo entre tipos
    private var allCategoriesCache: List<Category> = emptyList()

    private val _state = MutableStateFlow(
        RegisterUiState(
            type = initialType,
            amountDigits = savedStateHandle.get<String>(KEY_AMOUNT_DIGITS) ?: "",
            walletId = initialWalletId,
            toWalletId = savedStateHandle.get<Long>(KEY_TO_WALLET_ID),
            categoryId = savedStateHandle.get<Long>(KEY_CATEGORY_ID),
            note = savedStateHandle.get<String>(KEY_NOTE) ?: "",
            isAntOverride = savedStateHandle.get<Boolean?>(KEY_IS_ANT_OVERRIDE),
            receiptUri = savedStateHandle.get<String?>(KEY_RECEIPT_URI),
            isLoading = true
        )
    )
    val state = _state.asStateFlow()

    private val _effects = Channel<RegisterEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        observeWalletsAndCategories()
    }

    private fun observeWalletsAndCategories() {
        viewModelScope.launch {
            combine(
                repository.observeWallets(),
                repository.observeCategories()
            ) { walletsList, categoriesList ->
                Pair(walletsList, categoriesList)
            }.collectLatest { (walletsList, categoriesList) ->
                allCategoriesCache = categoriesList
                _state.update { current ->
                    val resolvedWalletId = current.walletId ?: walletsList.firstOrNull()?.id
                    val selectedWallet = walletsList.find { it.id == resolvedWalletId }
                    val toWallet = walletsList.find { it.id == current.toWalletId }

                    val filteredCategories = filterCategoriesForType(categoriesList, current.type)
                    val resolvedCategoryId = if (current.categoryId != null && filteredCategories.any { it.id == current.categoryId }) {
                        current.categoryId
                    } else {
                        filteredCategories.firstOrNull()?.id
                    }
                    val selectedCategory = filteredCategories.find { it.id == resolvedCategoryId }

                    current.copy(
                        wallets = walletsList,
                        categories = filteredCategories,
                        walletId = resolvedWalletId,
                        selectedWallet = selectedWallet,
                        toWallet = toWallet,
                        categoryId = if (current.type == TransactionType.TRANSFERENCIA) null else resolvedCategoryId,
                        selectedCategory = if (current.type == TransactionType.TRANSFERENCIA) null else selectedCategory,
                        isAntCandidate = selectedCategory?.isAntCandidate ?: false,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun filterCategoriesForType(
        allCategories: List<Category>,
        type: TransactionType
    ): List<Category> {
        return when (type) {
            TransactionType.GASTO -> allCategories.filter { it.kind == CategoryKind.GASTO }
            TransactionType.INGRESO -> allCategories.filter { it.kind == CategoryKind.INGRESO }
            TransactionType.TRANSFERENCIA -> emptyList()
        }
    }

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.TypeChanged -> handleTypeChanged(action.type)
            is RegisterAction.DigitPressed -> handleDigitPressed(action.digit)
            is RegisterAction.AmountDigitsChanged -> handleAmountDigitsChanged(action.digits)
            RegisterAction.BackspacePressed -> handleBackspace()
            RegisterAction.ClearAmount -> handleClearAmount()
            is RegisterAction.AddQuickAmount -> handleAddQuickAmount(action.addition)
            is RegisterAction.WalletSelected -> handleWalletSelected(action.walletId)
            is RegisterAction.ToWalletSelected -> handleToWalletSelected(action.walletId)
            is RegisterAction.CategorySelected -> handleCategorySelected(action.categoryId)
            is RegisterAction.NoteChanged -> handleNoteChanged(action.note)
            is RegisterAction.DateChanged -> handleDateChanged(action.timestamp)
            is RegisterAction.ParseSms -> handleParseSms(action.text)
            is RegisterAction.CreateCategory -> handleCreateCategory(action.name, action.kind, action.icon, action.colorHex)
            RegisterAction.ToggleAntExpense -> handleToggleAntExpense()
            is RegisterAction.ReceiptAttached -> handleReceiptAttached(action.uri)
            RegisterAction.ConfirmHighAmount -> {
                _state.update { it.copy(showHighAmountConfirmation = false) }
                save()
            }
            RegisterAction.DismissHighAmount -> {
                _state.update { it.copy(showHighAmountConfirmation = false) }
            }
            RegisterAction.ConfirmNegativeBalance -> {
                _state.update { it.copy(showNegativeBalanceConfirmation = false) }
                executeSave()
            }
            RegisterAction.DismissNegativeBalance -> {
                _state.update { it.copy(showNegativeBalanceConfirmation = false) }
            }
            RegisterAction.Save -> save()
        }
    }

    private fun handleParseSms(smsText: String) {
        if (smsText.isBlank()) return
        val parsed = intelligenceCore.parseBankSms(smsText)
        if (parsed.isSuccess) {
            val digits = parsed.amount.toString()
            val cleanDigits = digits.take(MAX_DIGITS_LENGTH)
            savedStateHandle[KEY_AMOUNT_DIGITS] = cleanDigits

            // Match wallet
            val matchedWallet = _state.value.wallets.find { w ->
                w.name.contains(parsed.walletName, ignoreCase = true) ||
                (parsed.walletName.contains("Nequi", ignoreCase = true) && w.type == com.andina.finanzas.domain.model.WalletType.NEQUI) ||
                (parsed.walletName.contains("Daviplata", ignoreCase = true) && w.type == com.andina.finanzas.domain.model.WalletType.DAVIPLATA) ||
                (parsed.walletName.contains("Bancolombia", ignoreCase = true) && w.type == com.andina.finanzas.domain.model.WalletType.BANCOLOMBIA)
            }
            val targetWalletId = matchedWallet?.id ?: _state.value.walletId

            // Match category
            val matchedCat = _state.value.categories.find { c ->
                c.name.contains(parsed.suggestedCategory, ignoreCase = true) ||
                parsed.suggestedCategory.contains(c.name, ignoreCase = true)
            }
            val targetCatId = matchedCat?.id ?: _state.value.categoryId

            _state.update { current ->
                current.copy(
                    type = TransactionType.GASTO,
                    amountDigits = cleanDigits,
                    walletId = targetWalletId,
                    selectedWallet = matchedWallet ?: current.selectedWallet,
                    categoryId = targetCatId,
                    selectedCategory = matchedCat ?: current.selectedCategory,
                    note = parsed.merchant,
                    isAntOverride = parsed.isAntCandidate,
                    amountError = null
                )
            }
        }
    }

    private fun handleCreateCategory(
        name: String,
        kind: CategoryKind,
        icon: String,
        colorHex: String
    ) {
        viewModelScope.launch {
            val newId = repository.createCategory(
                Category(
                    id = 0L,
                    name = name,
                    kind = kind,
                    icon = icon,
                    colorHex = colorHex
                )
            )
            _state.update { it.copy(categoryId = newId) }
        }
    }


    private fun handleTypeChanged(newType: TransactionType) {
        savedStateHandle[KEY_TYPE] = newType.name
        _state.update { current ->
            val filtered = filterCategoriesForType(allCategoriesCache, newType)
            val newCategoryId = if (newType == TransactionType.TRANSFERENCIA) {
                null
            } else if (current.categoryId != null && filtered.any { it.id == current.categoryId }) {
                current.categoryId
            } else {
                filtered.firstOrNull()?.id
            }
            val newCategory = filtered.find { it.id == newCategoryId }

            current.copy(
                type = newType,
                categories = filtered,
                categoryId = newCategoryId,
                selectedCategory = newCategory,
                isAntCandidate = newCategory?.isAntCandidate ?: false
            )
        }
    }

    private fun handleDigitPressed(digit: Char) {
        if (!digit.isDigit()) return
        val currentDigits = _state.value.amountDigits

        // Evitar ceros a la izquierda innecesarios (ej. "00")
        if (currentDigits.isEmpty() && digit == '0') return
        if (currentDigits.length >= MAX_DIGITS_LENGTH) return

        val potentialAmount = (currentDigits + digit).toLongOrNull() ?: 0L
        if (potentialAmount > MAX_AMOUNT_COP) return

        val newDigits = currentDigits + digit
        savedStateHandle[KEY_AMOUNT_DIGITS] = newDigits

        _state.update {
            it.copy(
                amountDigits = newDigits,
                lastPressedDigit = digit.digitToIntOrNull(),
                amountError = null
            )
        }
    }

    private fun handleAmountDigitsChanged(digits: String) {
        val cleanDigits = digits.filter { it.isDigit() }.take(MAX_DIGITS_LENGTH)
        val num = cleanDigits.toLongOrNull() ?: 0L
        val finalDigits = if (num > MAX_AMOUNT_COP) {
            MAX_AMOUNT_COP.toString()
        } else {
            cleanDigits.trimStart('0')
        }
        savedStateHandle[KEY_AMOUNT_DIGITS] = finalDigits
        _state.update {
            it.copy(
                amountDigits = finalDigits,
                lastPressedDigit = finalDigits.lastOrNull()?.digitToIntOrNull(),
                amountError = null
            )
        }
    }

    private fun handleBackspace() {
        val currentDigits = _state.value.amountDigits
        if (currentDigits.isNotEmpty()) {
            val newDigits = currentDigits.dropLast(1)
            savedStateHandle[KEY_AMOUNT_DIGITS] = newDigits
            _state.update {
                it.copy(
                    amountDigits = newDigits,
                    lastPressedDigit = null,
                    amountError = null
                )
            }
        }
    }

    private fun handleClearAmount() {
        savedStateHandle[KEY_AMOUNT_DIGITS] = ""
        _state.update {
            it.copy(
                amountDigits = "",
                lastPressedDigit = null,
                amountError = null
            )
        }
    }

    private fun handleAddQuickAmount(addition: Long) {
        val currentAmount = _state.value.amountDigits.toLongOrNull() ?: 0L
        val newAmount = (currentAmount + addition).coerceAtMost(MAX_AMOUNT_COP)
        val newDigits = newAmount.toString()
        savedStateHandle[KEY_AMOUNT_DIGITS] = newDigits

        _state.update {
            it.copy(
                amountDigits = newDigits,
                lastPressedDigit = null,
                amountError = null
            )
        }
    }

    private fun handleWalletSelected(walletId: Long) {
        savedStateHandle[KEY_WALLET_ID] = walletId
        _state.update { current ->
            current.copy(
                walletId = walletId,
                selectedWallet = current.wallets.find { it.id == walletId }
            )
        }
    }

    private fun handleToWalletSelected(toWalletId: Long) {
        savedStateHandle[KEY_TO_WALLET_ID] = toWalletId
        _state.update { current ->
            current.copy(
                toWalletId = toWalletId,
                toWallet = current.wallets.find { it.id == toWalletId }
            )
        }
    }

    private fun handleCategorySelected(categoryId: Long) {
        savedStateHandle[KEY_CATEGORY_ID] = categoryId
        _state.update { current ->
            val cat = current.categories.find { it.id == categoryId }
            current.copy(
                categoryId = categoryId,
                selectedCategory = cat,
                isAntCandidate = cat?.isAntCandidate ?: false
            )
        }
    }

    private fun handleNoteChanged(note: String) {
        savedStateHandle[KEY_NOTE] = note
        _state.update { it.copy(note = note) }
    }

    private fun handleDateChanged(timestamp: Long) {
        _state.update { it.copy(occurredAt = timestamp) }
    }

    private fun handleToggleAntExpense() {
        val currentVal = _state.value.isAntExpense
        val newVal = !currentVal
        savedStateHandle[KEY_IS_ANT_OVERRIDE] = newVal
        _state.update { it.copy(isAntOverride = newVal) }
    }

    private fun handleReceiptAttached(uri: String?) {
        savedStateHandle[KEY_RECEIPT_URI] = uri
        _state.update { it.copy(receiptUri = uri) }
    }

    private fun save() {
        val current = _state.value
        if (!current.canSave) {
            val errorMsg = when {
                current.amountMoney.isZero -> "Ingresa un monto mayor a cero"
                current.walletId == null -> "Selecciona un método de pago"
                current.type == TransactionType.TRANSFERENCIA && current.toWalletId == null -> "Selecciona la cuenta de destino"
                current.type == TransactionType.TRANSFERENCIA && current.toWalletId == current.walletId -> "La cuenta destino no puede ser la misma de origen"
                else -> "Revisa los campos del formulario"
            }
            _state.update { it.copy(amountError = errorMsg) }
            viewModelScope.launch {
                _effects.send(RegisterEffect.Error(errorMsg))
            }
            return
        }

        // Alerta de confirmación a 2 pasos si la cifra supera los $10.000.000 COP
        if (current.amountMoney.pesos > HIGH_AMOUNT_THRESHOLD_COP && !current.showHighAmountConfirmation) {
            _state.update { it.copy(showHighAmountConfirmation = true) }
            return
        }

        // Control de Saldos Negativos y Capacidad de Deuda (Módulo 14)
        if (current.type == TransactionType.GASTO || current.type == TransactionType.TRANSFERENCIA) {
            val wallet = current.selectedWallet ?: current.wallets.find { it.id == current.walletId }
            if (wallet != null && wallet.type != com.andina.finanzas.domain.model.WalletType.TARJETA_CREDITO) {
                val available = wallet.currentBalance.pesos
                val required = current.amountMoney.pesos
                if (available < required && !current.showNegativeBalanceConfirmation) {
                    _state.update { it.copy(showNegativeBalanceConfirmation = true) }
                    return
                }
            }
        }

        executeSave()
    }

    private fun executeSave() {
        val current = _state.value
        val walletId = current.walletId ?: return

        _state.update { it.copy(isSaving = true, amountError = null) }

        viewModelScope.launch {
            val result = registerTransactionUseCase(
                type = current.type,
                amount = current.amountMoney,
                walletId = walletId,
                toWalletId = current.toWalletId,
                categoryId = current.categoryId,
                note = current.note,
                occurredAt = current.occurredAt,
                isAntOverride = current.isAntOverride,
                receiptUri = current.receiptUri
            )

            result.onSuccess { txId ->
                // Limpiar campos persistidos tras guardado exitoso
                savedStateHandle[KEY_AMOUNT_DIGITS] = ""
                savedStateHandle[KEY_NOTE] = ""
                savedStateHandle[KEY_IS_ANT_OVERRIDE] = null
                savedStateHandle[KEY_RECEIPT_URI] = null

                _state.update {
                    it.copy(
                        amountDigits = "",
                        note = "",
                        isAntOverride = null,
                        receiptUri = null,
                        isSaving = false,
                        amountError = null
                    )
                }
                val successMessage = when (current.type) {
                    TransactionType.GASTO -> "Gasto de ${current.amountMoney.formatCop()} registrado"
                    TransactionType.INGRESO -> "Ingreso de ${current.amountMoney.formatCop()} registrado"
                    TransactionType.TRANSFERENCIA -> "Transferencia de ${current.amountMoney.formatCop()} realizada"
                }

                _effects.send(RegisterEffect.Saved(txId = txId, message = successMessage))
            }.onFailure { throwable ->
                val userMessage = when (throwable) {
                    is DomainError.InvalidAmount -> "El monto debe ser mayor a cero"
                    is DomainError.WalletNotFound -> "La billetera seleccionada no existe"
                    is DomainError.SameWalletTransfer -> "No puedes transferir a la misma cuenta"
                    else -> throwable.message ?: "Error al guardar el movimiento"
                }
                _effects.send(RegisterEffect.Error(userMessage))
                _state.update { it.copy(isSaving = false, amountError = userMessage) }
            }
        }
    }
}
