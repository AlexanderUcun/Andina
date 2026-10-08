package com.andina.finanzas.domain.model

/**
 * Modelo de Dominio para la Identidad y Preferencias del Usuario Andino.
 * Centraliza el estado del perfil en todas las pantallas.
 */
data class UserProfile(
    val name: String = "Valentina",
    val nickname: String = "Guardián del Frailejón",
    val avatarEmoji: String = "🐻",
    val avatarName: String = "Muggy",
    val level: String = "Nivel Frailejón Mayor",
    val isBiometricEnabled: Boolean = true,
    val isPrivacyByDefault: Boolean = false,
    val isHighPerformanceMode: Boolean = true,
    val currencyFormat: String = "COP ($)",
    val monthlyBudgetLimit: Long = 2_500_000L,
    val showBudgetCard: Boolean = true,
    val showWalletsCard: Boolean = true,
    val showMuggyInsight: Boolean = true,
    val showPocketsCard: Boolean = true
)
