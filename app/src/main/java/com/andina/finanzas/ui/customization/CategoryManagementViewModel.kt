package com.andina.finanzas.ui.customization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.CategoryKind
import com.andina.finanzas.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado inmutable de la pantalla de personalización integral de categorías.
 */
data class CategoryManagementState(
    val categories: List<Category> = emptyList(),
    val selectedTabKind: CategoryKind = CategoryKind.GASTO,
    val editingCategoryId: Long? = null,
    val nameInput: String = "",
    val selectedKind: CategoryKind = CategoryKind.GASTO,
    val selectedIcon: String = "coffee",
    val selectedColorHex: String = "#C5A059",
    val isAntCandidate: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false
) {
    val filteredCategories: List<Category>
        get() = categories.filter { it.kind == selectedTabKind }

    val isEditing: Boolean
        get() = editingCategoryId != null
}

sealed interface CategoryManagementEffect {
    data class ShowMessage(val message: String) : CategoryManagementEffect
    data object CategorySaved : CategoryManagementEffect
}

@HiltViewModel
class CategoryManagementViewModel @Inject constructor(
    private val financeRepository: FinanceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryManagementState())
    val state: StateFlow<CategoryManagementState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<CategoryManagementEffect>()
    val effects: SharedFlow<CategoryManagementEffect> = _effects.asSharedFlow()

    init {
        financeRepository.observeCategories()
            .onEach { categoryList ->
                _state.update { current ->
                    current.copy(
                        categories = categoryList,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun selectTabKind(kind: CategoryKind) {
        _state.update { it.copy(selectedTabKind = kind) }
    }

    fun onNameChange(name: String) {
        _state.update { it.copy(nameInput = name) }
    }

    fun onKindChange(kind: CategoryKind) {
        _state.update { it.copy(selectedKind = kind) }
    }

    fun onIconChange(iconKey: String) {
        _state.update { it.copy(selectedIcon = iconKey) }
    }

    fun onColorChange(colorHex: String) {
        _state.update { it.copy(selectedColorHex = colorHex) }
    }

    fun onAntCandidateChange(isAnt: Boolean) {
        _state.update { it.copy(isAntCandidate = isAnt) }
    }

    fun startEdit(category: Category) {
        _state.update { current ->
            current.copy(
                editingCategoryId = category.id,
                nameInput = category.name,
                selectedKind = category.kind,
                selectedIcon = category.icon,
                selectedColorHex = category.colorHex,
                isAntCandidate = category.isAntCandidate,
                selectedTabKind = category.kind
            )
        }
    }

    fun cancelEdit() {
        _state.update { current ->
            current.copy(
                editingCategoryId = null,
                nameInput = "",
                selectedIcon = "coffee",
                selectedColorHex = "#C5A059",
                isAntCandidate = false
            )
        }
    }

    fun saveCategory() {
        val current = _state.value
        val trimmedName = current.nameInput.trim()
        if (trimmedName.isBlank()) {
            viewModelScope.launch {
                _effects.emit(CategoryManagementEffect.ShowMessage("Por favor ingresa un nombre para la categoría"))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val editingId = current.editingCategoryId
            if (editingId != null) {
                // Actualización
                val existing = current.categories.find { it.id == editingId }
                val updated = Category(
                    id = editingId,
                    name = trimmedName,
                    icon = current.selectedIcon,
                    colorHex = current.selectedColorHex,
                    kind = current.selectedKind,
                    isAntCandidate = if (current.selectedKind == CategoryKind.GASTO) current.isAntCandidate else false,
                    isDefault = existing?.isDefault ?: false,
                    archived = existing?.archived ?: false,
                    sortOrder = existing?.sortOrder ?: 0
                )
                financeRepository.updateCategory(updated)
                _effects.emit(CategoryManagementEffect.ShowMessage("Categoría '$trimmedName' actualizada"))
            } else {
                // Creación nueva
                val newCategory = Category(
                    id = 0L,
                    name = trimmedName,
                    icon = current.selectedIcon,
                    colorHex = current.selectedColorHex,
                    kind = current.selectedKind,
                    isAntCandidate = if (current.selectedKind == CategoryKind.GASTO) current.isAntCandidate else false
                )
                financeRepository.createCategory(newCategory)
                _effects.emit(CategoryManagementEffect.ShowMessage("Categoría '$trimmedName' creada"))
            }

            _state.update {
                it.copy(
                    isSaving = false,
                    editingCategoryId = null,
                    nameInput = "",
                    selectedIcon = if (it.selectedKind == CategoryKind.INGRESO) "payments" else "coffee",
                    selectedColorHex = "#C5A059",
                    isAntCandidate = false
                )
            }
            _effects.emit(CategoryManagementEffect.CategorySaved)
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            val toDelete = _state.value.categories.find { it.id == categoryId }
            financeRepository.deleteCategory(categoryId)
            _effects.emit(CategoryManagementEffect.ShowMessage("Categoría '${toDelete?.name ?: ""}' eliminada"))
        }
    }
}
