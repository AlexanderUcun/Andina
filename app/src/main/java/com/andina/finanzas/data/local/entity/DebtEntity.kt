package com.andina.finanzas.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personName: String,
    val type: String, // "RECEIVABLE" (Me deben / Presté) or "PAYABLE" (Yo debo / Pedí prestado)
    val totalAmountPesos: Long,
    val paidAmountPesos: Long = 0L,
    val walletId: Long,
    val dueDateMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val note: String = "",
    val isSettled: Boolean = false
)
