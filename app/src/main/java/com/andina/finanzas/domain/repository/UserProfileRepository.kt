package com.andina.finanzas.domain.repository

import com.andina.finanzas.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio para el perfil y preferencias del usuario.
 * Fuente única de verdad para la identidad de usuario en toda la app.
 */
interface UserProfileRepository {
    fun observeUserProfile(): Flow<UserProfile>
    suspend fun getUserProfile(): UserProfile
    suspend fun updateUserProfile(profile: UserProfile)
    suspend fun updateNameAndNickname(name: String, nickname: String)
    suspend fun updateAvatar(emoji: String, name: String)
    suspend fun updateMonthlyBudgetLimit(limit: Long)
    suspend fun updateModuleVisibility(
        showBudget: Boolean,
        showWallets: Boolean,
        showMuggy: Boolean,
        showPockets: Boolean
    )
}
