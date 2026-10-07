package com.andina.finanzas.domain.usecase

import com.andina.finanzas.domain.model.DomainError
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.domain.model.Transaction
import com.andina.finanzas.domain.model.TransactionType
import com.andina.finanzas.domain.repository.FinanceRepository
import javax.inject.Inject

/**
 * Caso de uso principal para registrar cualquier movimiento financiero (Gasto, Ingreso o Transferencia).
 *
 * Aplica todas las reglas de negocio y validaciones financieras antes de delegar a la persistencia:
 * - El monto debe ser estrictamente positivo.
 * - En transferencias, la cuenta destino debe ser válida y diferente a la origen.
 * - Detecta automáticamente gastos hormiga o respeta la bandera provista por el usuario.
 */
class RegisterTransactionUseCase @Inject constructor(
    private val repository: FinanceRepository,
    private val antDetector: DetectAntExpensesUseCase
) {

    suspend operator fun invoke(
        type: TransactionType,
        amount: Money,
        walletId: Long,
        toWalletId: Long? = null,
        categoryId: Long? = null,
        note: String = "",
        occurredAt: Long = System.currentTimeMillis(),
        isAntOverride: Boolean? = null,
        receiptUri: String? = null
    ): Result<Long> {
        // 1. Validación de monto positivo
        if (!amount.isPositive) {
            return Result.failure(DomainError.InvalidAmount)
        }

        // 2. Validación de transferencias
        if (type == TransactionType.TRANSFERENCIA) {
            if (toWalletId == null || toWalletId == walletId) {
                return Result.failure(DomainError.SameWalletTransfer)
            }
        }

        // 3. Validación de existencia de billetera origen
        val sourceWallet = repository.getWalletById(walletId)
            ?: return Result.failure(DomainError.WalletNotFound(walletId))

        // 4. Si es transferencia, validar billetera destino
        if (type == TransactionType.TRANSFERENCIA && toWalletId != null) {
            repository.getWalletById(toWalletId)
                ?: return Result.failure(DomainError.WalletNotFound(toWalletId))
        }

        // 5. Determinar flag de gasto hormiga
        val isCategoryAntCandidate = if (categoryId != null) {
            repository.getCategoryById(categoryId)?.isAntCandidate ?: false
        } else {
            false
        }

        val isAnt = antDetector(
            type = type,
            amount = amount,
            isCategoryAntCandidate = isCategoryAntCandidate,
            isAntOverride = isAntOverride
        )

        // 6. Construir transacción de dominio
        val transaction = Transaction(
            type = type,
            amount = amount,
            walletId = walletId,
            toWalletId = toWalletId,
            categoryId = categoryId,
            note = note.trim(),
            occurredAt = occurredAt,
            isAntExpense = isAnt,
            receiptUri = receiptUri
        )

        return runCatching {
            repository.registerTransaction(transaction)
        }
    }
}
