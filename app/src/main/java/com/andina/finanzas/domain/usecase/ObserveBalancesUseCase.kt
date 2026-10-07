package com.andina.finanzas.domain.usecase

import com.andina.finanzas.domain.model.FinancialSummary
import com.andina.finanzas.domain.model.Pocket
import com.andina.finanzas.domain.model.Wallet
import com.andina.finanzas.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class BalancesDashboard(
    val wallets: List<Wallet>,
    val pockets: List<Pocket>,
    val summary: FinancialSummary
)

/**
 * Caso de uso para observar reactivamente el estado de saldos del usuario.
 *
 * Se alimenta de las vistas del Libro Mayor de Room (WalletBalanceView y PocketBalanceView),
 * por lo que cualquier inserción o cambio actualiza los flujos instantáneamente.
 */
class ObserveBalancesUseCase @Inject constructor(
    private val repository: FinanceRepository
) {

    operator fun invoke(fromMillis: Long, toMillis: Long): Flow<BalancesDashboard> {
        return combine(
            repository.observeWallets(),
            repository.observePockets(),
            repository.observeFinancialSummary(fromMillis, toMillis)
        ) { wallets, pockets, summary ->
            BalancesDashboard(
                wallets = wallets,
                pockets = pockets,
                summary = summary
            )
        }
    }

    fun observeWallets(): Flow<List<Wallet>> = repository.observeWallets()

    fun observeSummary(fromMillis: Long, toMillis: Long): Flow<FinancialSummary> =
        repository.observeFinancialSummary(fromMillis, toMillis)
}
