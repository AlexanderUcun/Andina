package com.andina.finanzas.domain.usecase

import com.andina.finanzas.domain.model.DomainError
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.repository.FinanceRepository
import javax.inject.Inject

/**
 * Caso de uso para aportar dinero a un bolsillo o meta de ahorro de forma atómica.
 *
 * Blindaje financiero:
 * 1. Valida que el monto sea estrictamente positivo (> $0 COP).
 * 2. Verifica la existencia de la meta y de la billetera origen.
 * 3. Valida que la billetera tenga saldo suficiente disponible antes de proceder.
 * 4. Ejecuta la operación atómica en el Libro Mayor contable.
 */
class ContributeToPocketUseCase @Inject constructor(
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

        // 2. Verificar existencia del bolsillo
        val pocket = repository.getPocketById(pocketId)
            ?: return Result.failure(DomainError.PocketNotFound(pocketId))

        // 3. Verificar existencia y saldo de la billetera
        val wallet = repository.getWalletById(walletId)
            ?: return Result.failure(DomainError.WalletNotFound(walletId))

        if (wallet.currentBalance.pesos < amount.pesos) {
            return Result.failure(
                DomainError.InsufficientFunds(
                    available = wallet.currentBalance,
                    required = amount
                )
            )
        }

        // 4. Ejecución atómica en el Ledger
        return runCatching {
            repository.contributeToPocket(pocketId = pocket.id, walletId = wallet.id, amount = amount)
        }
    }
}
