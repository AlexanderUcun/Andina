package com.andina.finanzas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un bolsillo de ahorro o meta programada (ej. Vacaciones, Fondo de Emergencia).
 * El saldo acumulado se calcula mediante la suma de sus pocket_movements.
 */
@Entity(tableName = "pockets")
data class PocketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val goal: Long, // Meta en pesos COP
    val icon: String = "savings",
    val colorHex: String = "#435C3C", // Verde Musgo por defecto
    val deadline: Long? = null, // Fecha límite en epoch millis
    val archived: Boolean = false,
    val sortOrder: Int = 0
)
