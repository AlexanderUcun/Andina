Create app/src/main/java/com/andina/finanzas/data/local/dao/DebtDao.kt:

fun observeAllDebts(): Flow<List<DebtEntity>>

fun observeActiveDebts(): Flow<List<DebtEntity>>

suspend fun insertDebt(debt: DebtEntity): Long

suspend fun updateDebt(debt: DebtEntity)

suspend fun deleteDebt(debtId: Long)

Update AppDatabase.kt & DatabaseModule.kt:

Register DebtEntity::class in @Database(entities = [...], version = 2, exportSchema = true).

Provide a safe Room Migration(1, 2) in DatabaseModule.kt that executes:
CREATE TABLE IF NOT EXISTS debts (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, personName TEXT NOT NULL, type TEXT NOT NULL, totalAmountPesos INTEGER NOT NULL, paidAmountPesos INTEGER NOT NULL, walletId INTEGER NOT NULL, dueDateMillis INTEGER, createdAtMillis INTEGER NOT NULL, note TEXT NOT NULL, isSettled INTEGER NOT NULL) (and keep .fallbackToDestructiveMigration() as safety fallback if already configured).

Expose abstract fun debtDao(): DebtDao and bind it in DatabaseModule.kt.

Step 4.2: Domain Model, Repository & Atomic Ledger Synchronization (domain/ & data/repository/)
Create app/src/main/java/com/andina/finanzas/domain/model/Debt.kt:

```kotlin
package com.andina.finanzas.domain.model

enum class DebtType {
    RECEIVABLE, // Me deben (Presté dinero)
    PAYABLE     // Yo debo (Pedí prestado / Fiado)
}

data class Debt(
    val id: Long = 0,
    val personName: String,
    val type: DebtType,
    val totalAmount: Money,
    val paidAmount: Money = Money(0L),
    val walletId: Long,
    val dueDateMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val note: String = "",
    val isSettled: Boolean = false
) {
    val remainingAmount: Money
        get() = Money((totalAmount.pesos - paidAmount.pesos).coerceAtLeast(0L))
    val progressFraction: Float
        get() = if (totalAmount.pesos > 0) (paidAmount.pesos.toFloat() / totalAmount.pesos.toFloat()).coerceIn(0f, 1f) else 0f
}
```

Ledger Synchronization Rule in FinanceRepository & FinanceRepositoryImpl:

Add methods to FinanceRepository:

fun observeDebts(): Flow<List<Debt>>

suspend fun createDebt(debt: Debt, affectWalletBalance: Boolean): Result<Long>

suspend fun registerDebtPayment(debtId: Long, paymentAmount: Money, walletId: Long, note: String): Result<Unit>

When creating a RECEIVABLE ("Presté dinero") with affectWalletBalance = true:

Insert the DebtEntity AND atomically insert a TransactionEntity on walletId so the money leaves the wallet in wallet_balances, tagged in its note/category as "🤝 Préstamo a ${debt.personName}". Ensure BudgetViewModel and ConsultorioViewModel exclude loan disbursements from regular monthly consumption expenses so lending money does NOT trigger false budget overruns.

When registering a partial or full payment (Abono) on a RECEIVABLE ("Me pagaron"):

Increment paidAmountPesos (and set isSettled = true if paidAmountPesos >= totalAmountPesos), and atomically insert an INCOME transaction on the chosen walletId ("🤝 Abono de ${debt.personName}").

When creating a PAYABLE ("Me prestaron") with affectWalletBalance = true:

Insert the DebtEntity AND atomically insert an INCOME transaction on walletId ("🤝 Préstamo recibido de ${debt.personName}").

When registering a payment (Abonar deuda) on a PAYABLE ("Pagué parte de mi deuda"):

Increment paidAmountPesos and atomically insert an EXPENSE transaction on walletId ("🤝 Pago de deuda a ${debt.personName}").

Step 4.3: UI Integration in Patrimonio (WalletsScreen.kt) & RegisterScreen.kt
Target Files:

app/src/main/java/com/andina/finanzas/ui/wallets/WalletsScreen.kt
app/src/main/java/com/andina/finanzas/ui/wallets/WalletsViewModel.kt
app/src/main/java/com/andina/finanzas/ui/wallets/WalletsContract.kt

Actions:

Expand the Top Segmented Switcher in Patrimonio (WalletsScreen.kt):

Currently it has 2 segments: 💳 Cuentas and 🌱 Metas.
Expand it to 3 segments: 💳 Cuentas | 🌱 Metas | 🤝 Deudas.

Build the 🤝 Deudas Tab Content inside WalletsScreen.kt:

Top Dual Summary Hero Card:
Left column: Me deben (Por cobrar) in SemanticDebtReceivableGold (+$ X COP).
Right column: Yo debo (Por pagar) in SemanticDebtPayableOrange (-$ Y COP).
Bottom pill: Balance Neto de Deudas y botón + Nueva Deuda / Préstamo.

Filter Sub-chips: Todas, Me deben (Por cobrar), Yo debo (Por pagar), Saldadas.

Debt Cards (DebtItemCard):
Displays personName, badge (Me debe in Gold vs Le debo in Terracotta Orange), note, optional dueDate chip, progress bar (Pagado $ X de $ Y), remaining balance, and a prominent + Abonar button on active debts.

Modals / BottomSheets:
Create Debt Modal: Choose Presté dinero (Me deben) or Pedí prestado (Yo debo), input Nombre de la persona, Monto en COP, Billetera afectada (with toggle "Afectar saldo actual de la billetera"), Fecha límite (opcional), and Nota.
Register Abono Modal: Pre-fills the remaining amount (allowing partial or 100% full payment), selects which Wallet receives or pays the money, and updates both the debt progress bar and the wallet balance in 1 tap.

## 🚀 PHASE 5: BUILD, TEST & SELF-CORRECTION VERIFICATION
After implementing Phases 1 through 4, execute the following verification pipeline in the terminal:

Run Unit Tests:
./gradlew testDebugUnitTest --no-daemon
Update or add unit tests in app/src/test/ to verify that Debt remaining calculations, partial payments (Abonos), and Wallet balance adjustments pass 100%.

Assemble Debug APK:
./gradlew assembleDebug --no-daemon
If any Kotlin compilation error, missing import, or Room schema mismatch appears, fix it immediately until BUILD SUCCESSFUL is achieved with zero errors.

---

## ✅ ESTADO DE EJECUCIÓN: 100% COMPLETADO

1. **FASE 1: Tokens Semánticos Contables (Completado)**
   - `SemanticIncomeEmerald` (`#10B981`)
   - `SemanticExpenseCoral` (`#EF4444`)
   - `SemanticTransferCyan` (`#0EA5E9`)
   - `SemanticDebtReceivableGold` (`#E5A93C`)
   - `SemanticDebtPayableOrange` (`#F97316`)
   - Aplicados rigurosamente en toda la app sin violaciones del guardián de arquitectura.

2. **FASE 2: Muggy Ilustrado 2.5D Volumétrico (Completado)**
   - Reemplazado el arte plano por Canvas 2.5D volumétrico en `MuggyAvatar.kt` y `MuggyPeekingHeader.kt`.
   - Lentes en Oro Frailejón cepillado con reflejo especular.
   - Ojos con catchlights vivos y seguimiento háptico/resorte elástico.
   - Eliminada la flor de la oreja y la letra 'Z'.

3. **FASE 3: Refinamiento Visual y Microinteracciones (Completado)**
   - `BudgetScreen.kt`: Colisión de filas de categorías solucionada con pesos flexibles, truncamiento con elipsis y banner sincronizador de límites.
   - `CategoryManagementScreen.kt`: Padding horizontal en carrusel de paletas y chips redondeados a 14dp.
   - `HomeScreen.kt` & `UserProfileScreen.kt`: Padding inferior a 120dp para evitar solapamiento con la barra de navegación; subtítulo duplicado eliminado.
   - Bóveda 100% local protegida.

4. **FASE 4: Módulo de Deudas y Préstamos (Completado)**
   - `DebtEntity.kt` y `DebtDao.kt` integrados en Room con migración atómica v1->v2 (`MIGRATION_1_2`).
   - `FinanceRepository.kt` y `FinanceRepositoryImpl.kt` con registro atómico en el libro mayor (`TransactionEntity`) y ajuste reactivo de saldo de billetera sin alterar presupuestos de consumo mensual.
   - `WalletsScreen.kt` con pestaña `[ 💳 Cuentas | 🌱 Metas | 🤝 Deudas ]`.
   - `DebtsTabContent.kt` con Hero Card dual, chips de filtro, tarjetas con barra de progreso, botón `+ Abonar`, modal de creación y modal de abono.

5. **FASE 5: Verificación y Compilación (Completado)**
   - Suite de pruebas unitarias (`DebtUnitTest.kt`, `MoneyMathUnitTest.kt`, etc.): **107/107 pruebas pasadas (100%)**.
   - Compilación completa: `./gradlew assembleDebug --no-daemon` -> **BUILD SUCCESSFUL**.
   - Cumplimiento estricto: **Sin push a GitHub**.

