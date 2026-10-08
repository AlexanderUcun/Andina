package com.andina.finanzas.ui.consultorio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.andina.finanzas.core.intelligence.triage.TriageLevel
import com.andina.finanzas.domain.model.Money
import com.andina.finanzas.ui.components.muggy.MuggyAvatar
import com.andina.finanzas.ui.components.muggy.MuggyPose
import com.andina.finanzas.ui.help.AntExpenseMirrorCard
import com.andina.finanzas.ui.help.PocketSimulatorCard
import com.andina.finanzas.ui.theme.AndeanPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultorioScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRegister: (amount: Long, merchant: String, wallet: String, category: String, isAnt: Boolean) -> Unit,
    onNavigateToPockets: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConsultorioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ConsultorioEffect.NavigateToRegisterWithPrefill -> {
                    onNavigateToRegister(
                        effect.amount,
                        effect.merchant,
                        effect.wallet,
                        effect.category,
                        effect.isAnt
                    )
                }
                is ConsultorioEffect.NavigateToPockets -> {
                    onNavigateToPockets()
                }
                is ConsultorioEffect.ShowSnackbar -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Consultorio de Muggy",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(11.dp),
                                tint = AndeanPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "100% Offline · Local & Privado",
                                fontSize = 11.sp,
                                color = AndeanPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            val diagnosis = uiState.diagnosis

            if (diagnosis != null) {
                // 1. Tarjeta Hero con Score y Métricas Clave
                HeroConsultorioCard(
                    score = diagnosis.healthScore.totalScore,
                    triageLevel = diagnosis.healthScore.triageLevel,
                    oxygenDays = diagnosis.quincenaReport.oxygenDays,
                    dailyBurnRate = diagnosis.quincenaReport.dailyBurnRate,
                    pose = diagnosis.healthScore.recommendedPose
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Tarjeta con Diagnóstico de Muggy (Burbuja de diálogo ilustrada)
                com.andina.finanzas.ui.components.muggy.MuggySpeechBubble(
                    text = diagnosis.insight.diagnosis,
                    title = diagnosis.insight.title,
                    greeting = diagnosis.insight.greeting,
                    keyMetric = diagnosis.insight.keyMetricSummary,
                    pose = diagnosis.healthScore.recommendedPose,
                    actionContent = if (diagnosis.insight.actionButtonText != null) {
                        {
                            Button(
                                onClick = {
                                    when (diagnosis.insight.actionType) {
                                        com.andina.finanzas.core.intelligence.nlg.DialogueActionType.CONTRIBUTE_POCKET -> onNavigateToPockets()
                                        com.andina.finanzas.core.intelligence.nlg.DialogueActionType.OPEN_ANT_MIRROR -> {
                                            viewModel.onEvent(ConsultorioEvent.SelectTab(ConsultorioTab.ESPEJO_HORMIGA))
                                        }
                                        else -> {}
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AndeanPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(diagnosis.insight.actionButtonText, fontSize = 13.sp, color = Color.White)
                            }
                        }
                    } else null
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Selector de Pestañas / Herramientas Interactivas (LazyRow horizontal anti-colapso)
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedTab == ConsultorioTab.DIAGNOSTICO,
                        onClick = { viewModel.onEvent(ConsultorioEvent.SelectTab(ConsultorioTab.DIAGNOSTICO)) },
                        label = { Text("Resumen", fontSize = 12.sp, maxLines = 1) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AndeanPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.selectedTab == ConsultorioTab.ESPEJO_HORMIGA,
                        onClick = { viewModel.onEvent(ConsultorioEvent.SelectTab(ConsultorioTab.ESPEJO_HORMIGA)) },
                        label = { Text("🐜 Hormiga", fontSize = 12.sp, maxLines = 1) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AndeanPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.selectedTab == ConsultorioTab.SIMULADOR_BOLSILLO,
                        onClick = { viewModel.onEvent(ConsultorioEvent.SelectTab(ConsultorioTab.SIMULADOR_BOLSILLO)) },
                        label = { Text("🌱 Simulador", fontSize = 12.sp, maxLines = 1) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AndeanPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.selectedTab == ConsultorioTab.SMS_PARSER,
                        onClick = { viewModel.onEvent(ConsultorioEvent.SelectTab(ConsultorioTab.SMS_PARSER)) },
                        label = { Text("📩 SMS", fontSize = 12.sp, maxLines = 1) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AndeanPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Contenido Dinámico según la pestaña seleccionada
            when (uiState.selectedTab) {
                ConsultorioTab.DIAGNOSTICO -> {
                    TopCategoriesSection(uiState.topCategories, uiState.totalExpensesThisMonth)
                }
                ConsultorioTab.ESPEJO_HORMIGA -> {
                    AntExpenseMirrorCard(
                        onCreatePocketWithSavings = { name, amount ->
                            viewModel.onEvent(ConsultorioEvent.OnCreatePocketFromAntMirror(name, amount))
                        }
                    )
                }
                ConsultorioTab.SIMULADOR_BOLSILLO -> {
                    PocketSimulatorCard(
                        pocketName = "Fondo de Tranquilidad",
                        targetAmount = Money(2_000_000L),
                        currentAmount = Money(450_000L)
                    )
                }
                ConsultorioTab.SMS_PARSER -> {
                    SmsParserToolSection(
                        inputText = uiState.smsInputText,
                        onInputChanged = { viewModel.onEvent(ConsultorioEvent.UpdateSmsInput(it)) },
                        onParse = { viewModel.onEvent(ConsultorioEvent.ParseSms) },
                        parsedSms = uiState.parsedSms,
                        onUseInRegister = { viewModel.onEvent(ConsultorioEvent.OnUseParsedSmsInRegister) },
                        onClear = { viewModel.onEvent(ConsultorioEvent.ClearParsedSms) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HeroConsultorioCard(
    score: Int,
    triageLevel: TriageLevel,
    oxygenDays: Int,
    dailyBurnRate: Long,
    pose: MuggyPose
) {
    val levelColor = when (triageLevel) {
        TriageLevel.TRIAGE_HOLGADO -> Color(0xFF435C3C)
        TriageLevel.TRIAGE_PREVENTIVO -> Color(0xFF845400)
        TriageLevel.TRIAGE_AJUSTE -> Color(0xFFB55800)
        TriageLevel.TRIAGE_CRITICO -> Color(0xFF9E2A2B)
    }

    val levelBadgeText = when (triageLevel) {
        TriageLevel.TRIAGE_HOLGADO -> "Nivel: Holgado"
        TriageLevel.TRIAGE_PREVENTIVO -> "Nivel: Preventivo"
        TriageLevel.TRIAGE_AJUSTE -> "Nivel: Ajuste"
        TriageLevel.TRIAGE_CRITICO -> "Nivel: Crítico"
    }

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp)
        ) {
            // Círculo con Score
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(levelColor.copy(alpha = 0.14f))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$score",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = levelColor
                    )
                    Text(
                        text = "/100",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = levelColor.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(levelColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = levelBadgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = levelColor
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$oxygenDays Días de Autonomía Financiera 🛡️",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Gasto diario estimado: ~${Money(dailyBurnRate).formatCop()}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TopCategoriesSection(
    topCategories: List<Pair<String, Money>>,
    totalExpenses: Money
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Distribución del Caudal este Mes",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B19)
            )

            Spacer(modifier = Modifier.height(14.dp))

            topCategories.forEach { (categoryName, amount) ->
                val progress = if (totalExpenses.pesos > 0L) {
                    (amount.pesos.toFloat() / totalExpenses.pesos.toFloat()).coerceIn(0f, 1f)
                } else 0f

                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(categoryName, fontSize = 13.sp, color = Color(0xFF2C241E))
                        Text(amount.formatCop(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF435C3C))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        color = Color(0xFF435C3C),
                        trackColor = Color(0xFFECE7DE),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun SmsParserToolSection(
    inputText: String,
    onInputChanged: (String) -> Unit,
    onParse: () -> Unit,
    parsedSms: com.andina.finanzas.core.intelligence.nlp.ParsedBankSms?,
    onUseInRegister: () -> Unit,
    onClear: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Decodificador Offline de SMS Bancarios",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B19)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pega un mensaje de Bancolombia, Nequi o DaviPlata. Se procesa 100% en tu teléfono.",
                fontSize = 12.sp,
                color = Color(0xFF6B655D)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChanged,
                placeholder = {
                    Text(
                        "Ej: Bancolombia le informa compra por $45.000 en TIENDAS D1...",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF435C3C),
                    unfocusedBorderColor = Color(0xFFD6CEBD)
                ),
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onParse,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF435C3C)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Decodificar Localmente", fontSize = 13.sp, color = Color.White)
                }
                if (inputText.isNotBlank()) {
                    Button(
                        onClick = onClear,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0DDD5))
                    ) {
                        Text("Limpiar", fontSize = 13.sp, color = Color(0xFF4A453E))
                    }
                }
            }

            AnimatedVisibility(visible = parsedSms != null) {
                if (parsedSms != null && parsedSms.isSuccess) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F7F2)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF435C3C),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SMS Decodificado con Éxito",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF435C3C)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Monto: ${Money(parsedSms.amount).formatCop()}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Comercio: ${parsedSms.merchant}", fontSize = 13.sp)
                            Text("Billetera: ${parsedSms.walletName}", fontSize = 13.sp)
                            Text("Categoría Sugerida: ${parsedSms.suggestedCategory}", fontSize = 13.sp)

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = onUseInRegister,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF435C3C)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("✍️ Registrar este movimiento", fontSize = 13.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
