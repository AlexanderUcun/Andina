package com.andina.finanzas.ui.customization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andina.finanzas.domain.model.Category
import com.andina.finanzas.domain.model.CategoryKind
import com.andina.finanzas.domain.model.UserProfile
import com.andina.finanzas.domain.repository.FinanceRepository
import com.andina.finanzas.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomizationUiState(
    val userProfile: UserProfile = UserProfile(),
    val categories: List<Category> = emptyList(),
    val selectedKind: CategoryKind = CategoryKind.GASTO,
    val isLoading: Boolean = true
) {
    val filteredCategories: List<Category>
        get() = categories.filter { it.kind == selectedKind }
}

@HiltViewModel
class CustomizationViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CustomizationUiState())
    val state: StateFlow<CustomizationUiState> = _state.asStateFlow()

    init {
        combine(
            financeRepository.observeCategories(),
            userProfileRepository.observeUserProfile()
        ) { categories, profile ->
            _state.update { current ->
                current.copy(
                    categories = categories,
                    userProfile = profile,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    fun selectCategoryKind(kind: CategoryKind) {
        _state.update { it.copy(selectedKind = kind) }
    }

    fun updateModuleVisibility(
        showBudget: Boolean,
        showWallets: Boolean,
        showMuggy: Boolean,
        showPockets: Boolean
    ) {
        viewModelScope.launch {
            userProfileRepository.updateModuleVisibility(
                showBudget = showBudget,
                showWallets = showWallets,
                showMuggy = showMuggy,
                showPockets = showPockets
            )
        }
    }
}
