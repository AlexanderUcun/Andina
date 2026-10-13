package com.andina.finanzas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una categoría de gasto o ingreso.
 * Incluye bandera isAntCandidate para sugerir automáticamente compra hormiga.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String,
    val colorHex: String,
    val kind: String, // GASTO, INGRESO
    val isAntCandidate: Boolean = false,
    val isDefault: Boolean = false,
    val archived: Boolean = false,
    val sortOrder: Int = 0
) {
    companion object {
        const val KIND_GASTO = "GASTO"
        const val KIND_INGRESO = "INGRESO"

        /**
         * Categorías base del ecosistema Andina.
         */
        val DEFAULT_CATEGORIES = listOf(
            // --- GASTOS ---
            CategoryEntity(
                name = "Café y Snacks",
                icon = "coffee",
                colorHex = "#D97706",
                kind = KIND_GASTO,
                isAntCandidate = true,
                isDefault = true,
                sortOrder = 0
            ),
            CategoryEntity(
                name = "Alimentación",
                icon = "restaurant",
                colorHex = "#2E7D32",
                kind = KIND_GASTO,
                isAntCandidate = false,
                isDefault = true,
                sortOrder = 1
            ),
            CategoryEntity(
                name = "Transporte",
                icon = "directions_bus",
                colorHex = "#1976D2",
                kind = KIND_GASTO,
                isAntCandidate = false,
                isDefault = true,
                sortOrder = 2
            ),
            CategoryEntity(
                name = "Domicilios",
                icon = "delivery_dining",
                colorHex = "#E65100",
                kind = KIND_GASTO,
                isAntCandidate = true,
                isDefault = true,
                sortOrder = 3
            ),
            CategoryEntity(
                name = "Servicios y Hogar",
                icon = "home",
                colorHex = "#546E7A",
                kind = KIND_GASTO,
                isAntCandidate = false,
                isDefault = true,
                sortOrder = 4
            ),
            CategoryEntity(
                name = "Entretenimiento",
                icon = "movie",
                colorHex = "#7B1FA2",
                kind = KIND_GASTO,
                isAntCandidate = false,
                isDefault = true,
                sortOrder = 5
            ),
            CategoryEntity(
                name = "Salud y Bienestar",
                icon = "favorite",
                colorHex = "#D32F2F",
                kind = KIND_GASTO,
                isAntCandidate = false,
                isDefault = true,
                sortOrder = 6
            ),
            CategoryEntity(
                name = "Cuidado Personal",
                icon = "spa",
                colorHex = "#E91E63",
                kind = KIND_GASTO,
                isAntCandidate = false,
                isDefault = true,
                sortOrder = 7
            ),
            CategoryEntity(
                name = "Familia y Hogar",
                icon = "family_restroom",
                colorHex = "#00897B",
                kind = KIND_GASTO,
                isAntCandidate = false,
                isDefault = true,
                sortOrder = 8
            ),
            CategoryEntity(
                name = "Educación",
                icon = "school",
                colorHex = "#0288D1",
                kind = KIND_GASTO,
                isAntCandidate = false,
                isDefault = true,
                sortOrder = 9
            ),
            CategoryEntity(
                name = "Otros Gastos",
                icon = "more_horiz",
                colorHex = "#78909C",
                kind = KIND_GASTO,
                isAntCandidate = false,
                isDefault = true,
                sortOrder = 10
            ),

            // --- INGRESOS ---
            CategoryEntity(
                name = "Salario / Sueldo",
                icon = "payments",
                colorHex = "#2E7D32",
                kind = KIND_INGRESO,
                isAntCandidate = false,
                isDefault = true,
                sortOrder = 9
            ),
            CategoryEntity(
                name = "Honorarios / Freelance",
                icon = "work",
                colorHex = "#1565C0",
                kind = KIND_INGRESO,
                isAntCandidate = false,
                isDefault = true,
                sortOrder = 10
            ),
            CategoryEntity(
                name = "Rendimientos / Inversión",
                icon = "trending_up",
                colorHex = "#6A1B9A",
                kind = KIND_INGRESO,
                isAntCandidate = false,
                isDefault = true,
                sortOrder = 11
            ),
            CategoryEntity(
                name = "Otros Ingresos",
                icon = "add_circle",
                colorHex = "#37474F",
                kind = KIND_INGRESO,
                isAntCandidate = false,
                isDefault = true,
                sortOrder = 12
            )
        )
    }
}
