package com.andina.finanzas.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.andina.finanzas.domain.model.UserProfile
import com.andina.finanzas.domain.repository.UserProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserProfileRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("andina_user_profile_prefs", Context.MODE_PRIVATE)

    private val _userProfileFlow = MutableStateFlow(loadFromPrefs())

    private fun loadFromPrefs(): UserProfile {
        return UserProfile(
            name = prefs.getString(KEY_NAME, "Valentina") ?: "Valentina",
            nickname = prefs.getString(KEY_NICKNAME, "Guardián del Frailejón") ?: "Guardián del Frailejón",
            avatarEmoji = prefs.getString(KEY_AVATAR_EMOJI, "🐻") ?: "🐻",
            avatarName = prefs.getString(KEY_AVATAR_NAME, "Muggy") ?: "Muggy",
            level = prefs.getString(KEY_LEVEL, "Nivel Frailejón Mayor") ?: "Nivel Frailejón Mayor",
            isBiometricEnabled = prefs.getBoolean(KEY_BIOMETRIC, true),
            isPrivacyByDefault = prefs.getBoolean(KEY_PRIVACY_DEFAULT, false),
            isHighPerformanceMode = prefs.getBoolean(KEY_PERFORMANCE, true),
            currencyFormat = prefs.getString(KEY_CURRENCY, "COP ($)") ?: "COP ($)",
            monthlyBudgetLimit = prefs.getLong(KEY_BUDGET_LIMIT, 2_500_000L),
            showBudgetCard = prefs.getBoolean(KEY_SHOW_BUDGET, true),
            showWalletsCard = prefs.getBoolean(KEY_SHOW_WALLETS, true),
            showMuggyInsight = prefs.getBoolean(KEY_SHOW_MUGGY, true),
            showPocketsCard = prefs.getBoolean(KEY_SHOW_POCKETS, true)
        )
    }

    private fun saveToPrefs(profile: UserProfile) {
        prefs.edit()
            .putString(KEY_NAME, profile.name)
            .putString(KEY_NICKNAME, profile.nickname)
            .putString(KEY_AVATAR_EMOJI, profile.avatarEmoji)
            .putString(KEY_AVATAR_NAME, profile.avatarName)
            .putString(KEY_LEVEL, profile.level)
            .putBoolean(KEY_BIOMETRIC, profile.isBiometricEnabled)
            .putBoolean(KEY_PRIVACY_DEFAULT, profile.isPrivacyByDefault)
            .putBoolean(KEY_PERFORMANCE, profile.isHighPerformanceMode)
            .putString(KEY_CURRENCY, profile.currencyFormat)
            .putLong(KEY_BUDGET_LIMIT, profile.monthlyBudgetLimit)
            .putBoolean(KEY_SHOW_BUDGET, profile.showBudgetCard)
            .putBoolean(KEY_SHOW_WALLETS, profile.showWalletsCard)
            .putBoolean(KEY_SHOW_MUGGY, profile.showMuggyInsight)
            .putBoolean(KEY_SHOW_POCKETS, profile.showPocketsCard)
            .apply()
    }

    override fun observeUserProfile(): Flow<UserProfile> = _userProfileFlow.asStateFlow()

    override suspend fun getUserProfile(): UserProfile = _userProfileFlow.value

    override suspend fun updateUserProfile(profile: UserProfile) {
        saveToPrefs(profile)
        _userProfileFlow.value = profile
    }

    override suspend fun updateNameAndNickname(name: String, nickname: String) {
        _userProfileFlow.update { current ->
            val updated = current.copy(name = name, nickname = nickname)
            saveToPrefs(updated)
            updated
        }
    }

    override suspend fun updateAvatar(emoji: String, name: String) {
        _userProfileFlow.update { current ->
            val updated = current.copy(avatarEmoji = emoji, avatarName = name)
            saveToPrefs(updated)
            updated
        }
    }

    override suspend fun updateMonthlyBudgetLimit(limit: Long) {
        _userProfileFlow.update { current ->
            val updated = current.copy(monthlyBudgetLimit = limit)
            saveToPrefs(updated)
            updated
        }
    }

    override suspend fun updateModuleVisibility(
        showBudget: Boolean,
        showWallets: Boolean,
        showMuggy: Boolean,
        showPockets: Boolean
    ) {
        _userProfileFlow.update { current ->
            val updated = current.copy(
                showBudgetCard = showBudget,
                showWalletsCard = showWallets,
                showMuggyInsight = showMuggy,
                showPocketsCard = showPockets
            )
            saveToPrefs(updated)
            updated
        }
    }

    companion object {
        private const val KEY_NAME = "user_name"
        private const val KEY_NICKNAME = "user_nickname"
        private const val KEY_AVATAR_EMOJI = "user_avatar_emoji"
        private const val KEY_AVATAR_NAME = "user_avatar_name"
        private const val KEY_LEVEL = "user_level"
        private const val KEY_BIOMETRIC = "user_biometric"
        private const val KEY_PRIVACY_DEFAULT = "user_privacy_default"
        private const val KEY_PERFORMANCE = "user_performance"
        private const val KEY_CURRENCY = "user_currency"
        private const val KEY_BUDGET_LIMIT = "user_budget_limit"
        private const val KEY_SHOW_BUDGET = "user_show_budget"
        private const val KEY_SHOW_WALLETS = "user_show_wallets"
        private const val KEY_SHOW_MUGGY = "user_show_muggy"
        private const val KEY_SHOW_POCKETS = "user_show_pockets"
    }
}
