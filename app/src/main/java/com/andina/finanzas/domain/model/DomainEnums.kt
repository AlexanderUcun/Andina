package com.andina.finanzas.domain.model

enum class TransactionType {
    GASTO,
    INGRESO,
    TRANSFERENCIA;

    companion object {
        fun fromString(value: String): TransactionType = when (value.uppercase()) {
            "GASTO" -> GASTO
            "INGRESO" -> INGRESO
            "TRANSFERENCIA" -> TRANSFERENCIA
            else -> GASTO
        }
    }
}

enum class WalletType {
    NEQUI,
    DAVIPLATA,
    BANCOLOMBIA,
    EFECTIVO,
    TARJETA_CREDITO,
    OTRA;

    companion object {
        fun fromString(value: String): WalletType = when (value.uppercase()) {
            "NEQUI" -> NEQUI
            "DAVIPLATA" -> DAVIPLATA
            "BANCOLOMBIA" -> BANCOLOMBIA
            "EFECTIVO" -> EFECTIVO
            "TARJETA_CREDITO" -> TARJETA_CREDITO
            else -> OTRA
        }
    }
}

enum class CategoryKind {
    GASTO,
    INGRESO;

    companion object {
        fun fromString(value: String): CategoryKind = when (value.uppercase()) {
            "INGRESO" -> INGRESO
            else -> GASTO
        }
    }
}
