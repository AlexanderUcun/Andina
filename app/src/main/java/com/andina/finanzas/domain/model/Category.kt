package com.andina.finanzas.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val colorHex: String,
    val kind: CategoryKind,
    val isAntCandidate: Boolean = false,
    val isDefault: Boolean = false,
    val archived: Boolean = false,
    val sortOrder: Int = 0
)
