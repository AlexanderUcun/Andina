package com.andina.finanzas.domain.model

sealed class DomainError(message: String) : Exception(message) {
    data object InvalidAmount : DomainError("El monto debe ser estrictamente mayor a cero")
    data object SameWalletTransfer : DomainError("La billetera de origen y destino no pueden ser iguales")
    data class WalletNotFound(val id: Long) : DomainError("La billetera con ID $id no fue encontrada")
    data class PocketNotFound(val id: Long) : DomainError("El bolsillo con ID $id no fue encontrado")
    data class InsufficientFunds(val available: Money, val required: Money) :
        DomainError("Saldo insuficiente. Disponible: ${available.formatCop()}, Requerido: ${required.formatCop()}")
}
