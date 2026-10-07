package com.andina.finanzas.domain.usecase

import com.andina.finanzas.domain.model.DomainError
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.repository.FinanceRepository
import javax.inject.Inject

/**
 * Caso de uso para retirar dinero ahorrado en un bolsillo y devolverlo a una billetera.
 *
 * Blindaje financiero:
 * 1. Valida que el monto sea estrictamente positivo (> $0 COP).
 * 2. Verifica la existencia de la meta y de la billetera destino.
 * 3. Valida que el bolsillo tenga saldo suficiente acumulado antes de permitir el retiro.
 * 4. Ejecuta la operación atómica en el Libro Mayor contable.
 */
class WithdrawFromPocketUseCase @Inject constructor(
    private val repository: FinanceRepository
) {

    suspend operator fun invoke(
        pocketId: Long,
        walletId: Long,
        amount: Money
    ): Result<Boolean> {
        // 1. Validar monto positivo
        if (!amount.isPositive) {
            return Result.failure(DomainError.InvalidAmount)
        }

        // 2. Verificar existencia del bolsillo y saldo ahorrado
        val pocket = repository.getPocketById(pocketId)
            ?: return Result.failure(DomainError.PocketNotFound(pocketId))

        if (pocket.currentBalance.pesos < amount.pesos) {
            return Result.failure(
                DomainError.InsufficientFunds(
                    available = pocket.currentBalance,
                    required = amount
                )
            )
        }

        // 3. Verificar existencia de la billetera destino
        val wallet = repository.getWalletById(walletId)
            ?: return Result.failure(DomainError.WalletNotFound(walletId))

        // 4. Ejecución atómica en el Ledger
        return runCatching {
            repository.withdrawFromPocket(pocketId = pocket.id, walletId = wallet.id, amount = amount)
        }
    }
}
