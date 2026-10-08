package com.andina.finanzas.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andina.finanzas.domain.model.UserProfile
import com.andina.finanzas.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: UserProfileRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    init {
        repository.observeUserProfile()
            .onEach { _userProfile.value = it }
            .launchIn(viewModelScope)
    }

    fun updateIdentity(name: String, nickname: String) {
        viewModelScope.launch {
            repository.updateNameAndNickname(name, nickname)
        }
    }

    fun updateAvatar(emoji: String, name: String) {
        viewModelScope.launch {
            repository.updateAvatar(emoji, name)
        }
    }

    fun updatePreferences(
        isBiometric: Boolean,
        isPrivacyDefault: Boolean,
        isHighPerformance: Boolean,
        currencyFormat: String
    ) {
        viewModelScope.launch {
            val current = _userProfile.value
            repository.updateUserProfile(
                current.copy(
                    isBiometricEnabled = isBiometric,
                    isPrivacyByDefault = isPrivacyDefault,
                    isHighPerformanceMode = isHighPerformance,
                    currencyFormat = currencyFormat
                )
            )
        }
    }
}
