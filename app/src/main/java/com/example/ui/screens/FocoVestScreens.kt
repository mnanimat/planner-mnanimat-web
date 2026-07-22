package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.AppViewModel
import com.example.ui.ChatMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocoVestDashboardScreen(
    viewModel: AppViewModel,
    onNavigateToTab: (String) -> Unit
) {
    val subjects by viewModel.allSubjects.collectAsState()
    val simulados by viewModel.allSimulados.collectAsState()
    val dueFlashcards by viewModel.allFlashcards.collectAsState()
    val hoursList by viewModel.allHours.collectAsState()

    // Derived Metrics
    val totalHoursStudied = remember(hoursList) {
        hoursList.filter { it.functionName == "Estudante" }.sumOf { it.hours.toDouble() }.toFloat()
    }
    val avgSuccessRate = remember(simulados) {
        if (simulados.isEmpty()) 0f
        else (simulados.sumOf { it.correctAnswers.toDouble() } / simulados.sumOf { it.totalQuestions.toDouble() } * 100).toFloat()
    }
    val pendingRevisions = remember(dueFlashcards) {
        dueFlashcards.count { it.dueDate <= System.currentTimeMillis() }
    }
    val estimatedScore = remember(avgSuccessRate) {
        if (avgSuccessRate == 0f) 520 else (450 + (avgSuccessRate * 5.1f)).toInt().coerceAtMost(980)
    }

    // Selected subject for Funnel visualizer
    var selectedSubjectForFunnel by remember { mutableStateOf<StudySubject?>(null) }
    LaunchedEffect(subjects) {
        if (selectedSubjectForFunnel == null && subjects.isNotEmpty()) {
            selectedSubjectForFunnel = subjects.first()
        } else if (selectedSubjectForFunnel != null) {
            selectedSubjectForFunnel = subjects.find { it.id == selectedSubjectForFunnel!!.id }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Welcome and Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.secondaryContainer
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Rumo à Aprovação! 🎓",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Acompanhe seu progresso, revise com flashcards inteligentes e simule provas com IA.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Security Chip - Local Data Branding
        item {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE2E2E6))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E7D32))
                )
                Text(
                    text = "PROCESSAMENTO LOCAL & SEGURO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF44474E),
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Horas Estudadas",
                    value = "%.1fh".format(totalHoursStudied),
                    icon = Icons.Default.Timeline,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )
                MetricCard(
                    title = "Taxa de Acerto",
                    value = "%.1f%%".format(avgSuccessRate),
                    icon = Icons.Default.CheckCircleOutline,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF26A69A)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Revisões Pendentes",
                    value = pendingRevisions.toString(),
                    icon = Icons.Default.Timer,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFFF7043)
                )
                MetricCard(
                    title = "Projeção ENEM",
                    value = "$estimatedScore pt",
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFAB47BC)
                )
            }
        }

        // Fila Inteligente (Próximo Passo)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fila Inteligente",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.5f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Próximo Passo",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val nextTopics = remember(subjects) {
                        subjects.filter { it.getProgressPercent() < 100 }.take(2)
                    }

                    if (nextTopics.isEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.4f))
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Excelente! Todos os assuntos agendados estão 100% completos.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            nextTopics.forEachIndexed { index, topic ->
                                val formattedIndex = "%02d".format(index + 1)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White.copy(alpha = 0.4f))
                                        .clickable {
                                            selectedSubjectForFunnel = topic
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF004A77)), // Deep rich blue block from HTML
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = formattedIndex,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${topic.category}: ${topic.title}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Revisão pendente • Clique para focar",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Funil de Aprendizagem de 7 Etapas
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Funil de Aprendizagem",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Acompanhe visualmente as 7 etapas para fixar o assunto na memória de longo prazo.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (subjects.isEmpty()) {
                        Text(
                            text = "Nenhum assunto cadastrado nas trilhas. Adicione na aba Trilhas!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // Dropdown-like selector
                        var expandedSelector by remember { mutableStateOf(false) }
                        Box {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.outlineVariant,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { expandedSelector = true }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedSubjectForFunnel?.title ?: "Selecione um Assunto",
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                            }

                            DropdownMenu(
                                expanded = expandedSelector,
                                onDismissRequest = { expandedSelector = false }
                            ) {
                                subjects.forEach { sub ->
                                    DropdownMenuItem(
                                        text = { Text(sub.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                        onClick = {
                                            selectedSubjectForFunnel = sub
                                            expandedSelector = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        selectedSubjectForFunnel?.let { activeSub ->
                            val stepsList = listOf(
                                Triple("Aula", "aula", Icons.Default.School),
                                Triple("Resumo", "resumo", Icons.Default.MenuBook),
                                Triple("Autoexplicação", "autoexplicacao", Icons.AutoMirrored.Filled.Chat),
                                Triple("Exercícios", "exercicios", Icons.Default.Edit),
                                Triple("Caderno de Erros", "cadernoerros", Icons.Default.ErrorOutline),
                                Triple("Revisão", "revisao", Icons.Default.Loop),
                                Triple("Simulado", "simulado", Icons.Default.Assignment)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                stepsList.forEachIndexed { idx, step ->
                                    val checked = when (step.second) {
                                        "aula" -> activeSub.stepAula
                                        "resumo" -> activeSub.stepResumo
                                        "autoexplicacao" -> activeSub.stepAutoexplicacao
                                        "exercicios" -> activeSub.stepExercicios
                                        "cadernoerros" -> activeSub.stepCadernoErros
                                        "revisao" -> activeSub.stepRevisao
                                        "simulado" -> activeSub.stepSimulado
                                        else -> false
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                            )
                                            .clickable {
                                                viewModel.toggleSubjectStep(activeSub, step.second)
                                            }
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(
                                                    if (checked) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.surfaceVariant,
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = (idx + 1).toString(),
                                                color = if (checked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Icon(
                                            imageVector = step.third,
                                            contentDescription = null,
                                            tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = step.first,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal,
                                            color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Checkbox(
                                            checked = checked,
                                            onCheckedChange = { viewModel.toggleSubjectStep(activeSub, step.second) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

data class EventCountdown(
    val title: String,
    val date: String,
    val weeksLeft: Int,
    val daysLeft: Int
)

data class StudyBlock(
    val exam: String,
    val subject: String,
    val watchLink: String?,
    val week: String,
    val period: String
)

data class SimulationStrategy(
    val type: String,
    val timeEst: String,
    val strategy: String,
    val frequency: String
)

data class ErrorAutopsy(
    val cause: String,
    val action: String
)

val countdownEvents = listOf(
    EventCountdown("ITA 1ª Fase ✈️", "27/09/2026", 10, 74),
    EventCountdown("ITA 2ª Fase 🎯", "20/10/2026", 14, 97),
    EventCountdown("ENEM 1º Dia 📝", "08/11/2026", 16, 116),
    EventCountdown("ENEM 2º Dia 📐", "15/11/2026", 17, 123)
)

val cronogramaEnemList = listOf(
    StudyBlock("ENEM", "Mat: Razão, Proporção e Escalas", "https://www.youtube.com/results?search_query=razao+proporcao+escala+enem+ferretto", "Semana 1", "15/07 a 21/07"),
    StudyBlock("ENEM", "Bio: Ecologia (Cadeias Alimentares e Desequilíbrios)", "https://www.youtube.com/results?search_query=ecologia+cadeias+alimentares+desequilibrio+ambiental+enem", "Semana 1", "15/07 a 21/07"),
    StudyBlock("ENEM", "Quí: Meio Ambiente e Problemas Ambientais", "https://www.youtube.com/results?search_query=quimica+ambiental+problemas+ambientais+enem", "Semana 2", "22/07 a 28/07"),
    StudyBlock("ENEM", "Mat: Porcentagem e Juros Simples/Compostos", "https://www.youtube.com/results?search_query=porcentagem+juros+compostos+matematica+basica+enem", "Semana 2", "22/07 a 28/07"),
    StudyBlock("ENEM", "His: Brasil Colônia (Economia e Escravidão)", "https://www.youtube.com/results?search_query=brasil+colonia+economia+escravidao+enem", "Semana 3", "29/07 a 04/08"),
    StudyBlock("ENEM", "Mat: Estatística (Média, Moda, Mediana) e Gráficos", "https://www.youtube.com/results?search_query=estatistica+media+moda+mediana+graficos+enem", "Semana 3", "29/07 a 04/08"),
    StudyBlock("ENEM", "Fís: Mecânica (Cinemática, Leis de Newton e Energia)", "https://www.youtube.com/results?search_query=mecanica+leis+de+newton+energia+enem", "Semana 4", "05/08 a 11/08"),
    StudyBlock("ENEM", "Geo: Agrária (Estrutura Fundiária e Agronegócio)", "https://www.youtube.com/results?search_query=geografia+agraria+agronegocio+enem", "Semana 4", "05/08 a 11/08"),
    StudyBlock("ENEM", "Bio: Citologia (Organelas e Transporte na Membrana)", "https://www.youtube.com/results?search_query=citologia+organelas+transporte+membrana+enem", "Semana 5", "12/08 a 18/08"),
    StudyBlock("ENEM", "Lin: Interpretação de Textos e Coesão Textual", "https://www.youtube.com/results?search_query=interpretacao+de+textos+coesao+textual+enem", "Semana 5", "12/08 a 18/08"),
    StudyBlock("ENEM", "Mat: Geometria Plana (Áreas e Teorema de Pitágoras)", "https://www.youtube.com/results?search_query=geometria+plana+areas+pitagoras+enem", "Semana 6", "19/08 a 25/08"),
    StudyBlock("ENEM", "Quí: Físico-Química (Estequiometria Básica e Soluções)", "https://www.youtube.com/results?search_query=calculo+estequiometrico+solucoes+enem", "Semana 6", "19/08 a 25/08"),
    StudyBlock("ENEM", "Fís: Termologia (Calorimetria e Transferência de Calor)", "https://www.youtube.com/results?search_query=termologia+calorimetria+propagacao+de+calor+enem", "Semana 7", "26/08 a 01/09"),
    StudyBlock("ENEM", "His: Era Vargas e Ditadura Civil-Militar", "https://www.youtube.com/results?search_query=era+vargas+ditadura+militar+brasil+enem", "Semana 7", "26/08 a 01/09"),
    StudyBlock("ENEM", "Bio: Fisiologia Humana (Sistemas Imunológico e Digestório)", "https://www.youtube.com/results?search_query=fisiologia+humana+imunologico+digestorio+enem", "Semana 8", "02/09 a 08/09"),
    StudyBlock("ENEM", "Red: Estrutura Dissertativa e Proposta de Intervenção", "https://www.youtube.com/results?search_query=redacao+enem+estrutura+proposta+de+intervencao", "Semana 8", "02/09 a 08/09"),
    StudyBlock("ENEM", "Mat: Geometria Espacial (Prismas, Cilindros e Cones)", "https://www.youtube.com/results?search_query=geometria+espacial+cilindro+cone+prisma+enem", "Semana 9", "09/09 a 15/09"),
    StudyBlock("ENEM", "Geo: Urbanização e Demografia Brasileira", "https://www.youtube.com/results?search_query=urbanizacao+demografia+geografia+enem", "Semana 9", "09/09 a 15/09"),
    StudyBlock("ENEM", "Quí: Química Orgânica (Funções e Isomeria)", "https://www.youtube.com/results?search_query=quimica+organica+funcoes+isomeria+enem", "Semana 10", "16/09 a 22/09"),
    StudyBlock("ENEM", "Fil/Soc: Contratualistas, Platão e Aristóteles", "https://www.youtube.com/results?search_query=filosofia+platao+aristoteles+contratualistas+enem", "Semana 10", "16/09 a 22/09"),
    StudyBlock("ENEM", "Bio: Genética (Leis de Mendel e Heredogramas)", "https://www.youtube.com/results?search_query=genetica+mendel+heredogramas+enem", "Semana 11", "23/09 a 29/09"),
    StudyBlock("ENEM", "Fís: Eletrodinâmica (Leis de Ohm, Potência e Resistores)", "https://www.youtube.com/results?search_query=eletrodinamica+leis+de+ohm+potencia+enem", "Semana 11", "23/09 a 29/09"),
    StudyBlock("ENEM", "Mat: Funções (Afim, Quadrática, Exponencial)", "https://www.youtube.com/results?search_query=funcoes+afim+quadratica+exponencial+enem", "Semana 12", "30/09 a 06/10"),
    StudyBlock("ENEM", "His: Guerra Fria e Revolução Industrial", "https://www.youtube.com/results?search_query=guerra+fria+revolucao+industrial+enem", "Semana 12", "30/09 a 06/10"),
    StudyBlock("ENEM", "Bio: Evolução (Lamarckismo, Darwinismo e Neodarwinismo)", "https://www.youtube.com/results?search_query=evolucao+darwin+lamarck+neodarwinismo+enem", "Semana 13", "07/10 a 13/10"),
    StudyBlock("ENEM", "Fil/Soc: Durkheim, Marx e Weber (Sociologia Clássica)", "https://www.youtube.com/results?search_query=sociologia+durkheim+marx+weber+enem", "Semana 13", "07/10 a 13/10"),
    StudyBlock("ENEM", "Fís: Ondulatória e Acústica (Características e Fenômenos)", "https://www.youtube.com/results?search_query=ondulatoria+fenomenos+acustica+enem", "Semana 14", "14/10 a 20/10"),
    StudyBlock("ENEM", "Lin: Figuras de Linguagem e Funções da Linguagem", "https://www.youtube.com/results?search_query=figuras+de+linguagem+funcoes+da+linguagem+enem", "Semana 14", "14/10 a 20/10"),
    StudyBlock("ENEM", "Mat: Probabilidade e Análise Combinatória Básica", "https://www.youtube.com/results?search_query=probabilidade+analise+combinatoria+enem", "Semana 15", "21/10 a 27/10"),
    StudyBlock("ENEM", "Quí: Termoquímica (Endo/Exo e Entalpia)", "https://www.youtube.com/results?search_query=termoquimica+entalpia+enem", "Semana 15", "21/10 a 27/10"),
    StudyBlock("ENEM", "Bio: Metabolismo Energético (Respiração e Fotossíntese)", "https://www.youtube.com/results?search_query=metabolismo+energetico+respiracao+celular+fotossintese+enem", "Semana 16", "28/10 a 03/11"),
    StudyBlock("ENEM", "Geo: Globalização e Geopolítica Atual", "https://www.youtube.com/results?search_query=globalizacao+geopolitica+enem", "Semana 16", "28/10 a 03/11"),
    StudyBlock("ENEM", "Lin: Vanguardas Europeias e Arte Contemporânea", "https://www.youtube.com/results?search_query=vanguardas+europeias+arte+contemporanea+enem", "Semana 17", "04/11 a 10/11"),
    StudyBlock("ENEM", "RETA FINAL ENEM: Simulados Diários, Redação e Revisão", null, "Semana 17", "04/11 a 10/11")
)

val cronogramaItaList = listOf(
    StudyBlock("ITA", "Mat: Álgebra (Princípio de Indução Finita e Desigualdade das Médias)", "https://www.youtube.com/results?search_query=principio+inducao+finita+desigualdade+das+medias+ita", "Semana 1", "15/07 a 21/07"),
    StudyBlock("ITA", "Fís: Mecânica Vetorial e Lançamentos com Resistência do Ar", "https://www.youtube.com/results?search_query=lancamento+obliquo+resistencia+ar+ita", "Semana 1", "15/07 a 21/07"),
    StudyBlock("ITA", "Quí: Química Quântica (Orbitais, Equação de Schrödinger)", "https://www.youtube.com/results?search_query=quimica+quantica+orbitais+schrodinger+ita", "Semana 1", "15/07 a 21/07"),
    StudyBlock("ITA", "Mat: Matrizes e Sistemas (Determinante de Vandermonde e Cramer)", "https://www.youtube.com/results?search_query=matrizes+determinante+vandermonde+ita", "Semana 2", "22/07 a 28/07"),
    StudyBlock("ITA", "Fís: Dinâmica (Forças Fictícias e Referenciais Não Inerciais)", "https://www.youtube.com/results?search_query=forcas+ficticias+referenciais+nao+inerciais+ita", "Semana 2", "22/07 a 28/07"),
    StudyBlock("ITA", "Quí: Físico-Química (Cinética de 1ª Ordem, Clausius-Clapeyron)", "https://www.youtube.com/results?search_query=cinetica+quimica+clausius+clapeyron+ita", "Semana 2", "22/07 a 28/07"),
    StudyBlock("ITA", "Mat: Geometria Plana (Teoremas de Ptolomeu, Ceva e Menelaus)", "https://www.youtube.com/results?search_query=teorema+ptolomeu+ceva+menelaus+geometria+ita", "Semana 3", "29/07 a 04/08"),
    StudyBlock("ITA", "Fís: Oscilações (MHS, Pêndulo Físico e Osciladores Acoplados)", "https://www.youtube.com/results?search_query=mhs+pendulo+fisico+osciladores+acoplados+ita", "Semana 3", "29/07 a 04/08"),
    StudyBlock("ITA", "Por: Sintaxe Avançada (Período Composto, Regência, Crase)", "https://www.youtube.com/results?search_query=sintaxe+periodo+composto+regencia+ita", "Semana 3", "29/07 a 04/08"),
    StudyBlock("ITA", "Mat: Trigonometria (Prostaférese e Equações Complexas)", "https://www.youtube.com/results?search_query=trigonometria+prostaferese+equacoes+complexas+ita", "Semana 4", "05/08 a 11/08"),
    StudyBlock("ITA", "Fís: Eletrostática (Método das Imagens e Dielétricos)", "https://www.youtube.com/results?search_query=eletrostatica+metodo+das+imagens+dieletricos+ita", "Semana 4", "05/08 a 11/08"),
    StudyBlock("ITA", "Quí: Estequiometria Avançada (Misturas, Rendimento e Pureza)", "https://www.youtube.com/results?search_query=calculo+estequiometrico+avancado+ita", "Semana 4", "05/08 a 11/08"),
    StudyBlock("ITA", "Mat: Números Complexos (Forma Trigonométrica e Moivre)", "https://www.youtube.com/results?search_query=numeros+complexos+forma+trigonometrica+moivre+ita", "Semana 5", "12/08 a 18/08"),
    StudyBlock("ITA", "Fís: Eletromagnetismo (Leis de Kirchhoff em Malhas e Indução)", "https://www.youtube.com/results?search_query=eletromagnetismo+leis+de+kirchhoff+inducao+ita", "Semana 5", "12/08 a 18/08"),
    StudyBlock("ITA", "Lit: Movimentos Literários (Realismo, Romantismo, Modernismo)", "https://www.youtube.com/results?search_query=escolas+literarias+modernismo+realismo+ita", "Semana 5", "12/08 a 18/08"),
    StudyBlock("ITA", "Mat: Polinômios (Relações de Girard, Equações Recíprocas)", "https://www.youtube.com/results?search_query=polinomios+relacoes+de+girard+equacoes+reciprocas+ita", "Semana 6", "19/08 a 25/08"),
    StudyBlock("ITA", "Fís: Óptica Geométrica (Lentes Justapostas, Reflexão Total)", "https://www.youtube.com/results?search_query=optica+reflexao+total+prismas+ita", "Semana 6", "19/08 a 25/08"),
    StudyBlock("ITA", "Quí: Equilíbrio Iônico (Curvas de Titulação, Kps, Tampão)", "https://www.youtube.com/results?search_query=equilibrio+ionico+titulacao+kps+ita", "Semana 6", "19/08 a 25/08"),
    StudyBlock("ITA", "Mat: Geometria Analítica (Cônicas: Elipse, Hipérbole e Parábola)", "https://www.youtube.com/results?search_query=geometria+analitica+conicas+elipse+hiperbole+ita", "Semana 7", "26/08 a 01/09"),
    StudyBlock("ITA", "Fís: Óptica Física (Interferência de Young, Difração e Polarização)", "https://www.youtube.com/results?search_query=optica+fisica+experiencia+de+young+ita", "Semana 7", "26/08 a 01/09"),
    StudyBlock("ITA", "Red: Texto Dissertativo Analítico (Fuga de modelos engessados)", "https://www.youtube.com/results?search_query=redacao+ita+estrutura+analitica", "Semana 7", "26/08 a 01/09"),
    StudyBlock("ITA", "Mat: Geometria Espacial (Esferas Inscritas, Teorema de L'Huilier)", "https://www.youtube.com/results?search_query=geometria+espacial+esferas+inscritas+ita", "Semana 8", "02/09 a 08/09"),
    StudyBlock("ITA", "Fís: Hidro/Dinâmica de Fluidos (Equação de Bernoulli e Torricelli)", "https://www.youtube.com/results?search_query=hidrodinamica+equacao+bernoulli+ita", "Semana 8", "02/09 a 08/09"),
    StudyBlock("ITA", "Quí: Eletroquímica (Equação de Nernst e Termodinâmica de Pilhas)", "https://www.youtube.com/results?search_query=eletroquimica+equacao+nernst+termodinamica+ita", "Semana 8", "02/09 a 08/09"),
    StudyBlock("ITA", "Mat: Análise Combinatória Avançada (Lema de Kaplansky)", "https://www.youtube.com/results?search_query=analise+combinatoria+kaplansky+solucoes+inteiras+ita", "Semana 9", "09/09 a 15/09"),
    StudyBlock("ITA", "Fís: Física Moderna (Radiação de Corpo Negro, Bohr, Relatividade)", "https://www.youtube.com/results?search_query=fisica+moderna+relatividade+efeito+fotoeletrico+ita", "Semana 9", "09/09 a 15/09"),
    StudyBlock("ITA", "Quí: Orgânica Avançada e Isomeria (Mecanismos e Misturas Racêmicas)", "https://www.youtube.com/results?search_query=isomeria+optica+enantiomeros+ita", "Semana 9", "09/09 a 15/09"),
    StudyBlock("ITA", "Ing: Leitura Instrumental, Inferência e Phrasal Verbs", "https://www.youtube.com/results?search_query=ingles+instrumental+phrasal+verbs+ita", "Semana 10", "16/09 a 22/09"),
    StudyBlock("ITA", "Reta Final 1ª Fase: Resolução Múltipla Escolha e Decorebas", null, "Semana 10", "16/09 a 22/09"),
    StudyBlock("ITA", "PROVA DA 1ª FASE DO ITA", null, "Semana 11", "23/09 a 29/09"),
    StudyBlock("ITA", "Foco 2ª Fase: Matemática Discursiva (Demonstrações)", null, "Semana 12", "30/09 a 06/10"),
    StudyBlock("ITA", "Foco 2ª Fase: Quí Discursiva (Escrever Mecanismos SN1, SN2, E1, E2)", "https://www.youtube.com/results?search_query=mecanismos+reacoes+organicas+sn1+sn2+ita", "Semana 12", "30/09 a 06/10"),
    StudyBlock("ITA", "Foco 2ª Fase: Física Discursiva (Demonstrar equações e vínculos)", null, "Semana 13", "07/10 a 13/10"),
    StudyBlock("ITA", "Foco 2ª Fase: Redação e Revisão de Discursivas", null, "Semana 14", "14/10 a 20/10"),
    StudyBlock("ITA", "PROVA DA 2ª FASE DO ITA", null, "Semana 15", "21/10 a 27/10")
)

val manualSimuladosList = listOf(
    SimulationStrategy("ENEM", "Dia 1: 5h30 | Dia 2: 5h", "Estratégia TRI: O ENEM pune quem chuta e erra fácil. Comece caçando as 15 fáceis. Leia o comando antes do texto.", "Domingos à tarde (das 13h30 às 18h30/19h)"),
    SimulationStrategy("ITA 1ª Fase", "5 horas para 70 questões", "Sem TRI. Faça Linguagens e Inglês primeiro. Depois, as Exatas que sabe a fórmula de imediato. Pule os monstros.", "Sábados de manhã (das 8h às 13h)"),
    SimulationStrategy("ITA 2ª Fase", "4 horas para 10 questões + 15 objetivas ou Redação", "Escreva legível. Indique os passos. O corretor avalia o raciocínio correto, mesmo se errar cálculo no final.", "Simulações direcionadas")
)

val autopsiaErrosList = listOf(
    ErrorAutopsy("Falta de Atenção (Sinal trocado, não leu o EXCETO)", "Faça 10 exercícios da matéria prestando atenção obsessiva. Sublinhe os comandos da questão durante a prova."),
    ErrorAutopsy("Falta de Tempo (Sabia fazer, mas não deu tempo)", "Altere sua estratégia de pular questões difíceis. Treine listas com cronômetro."),
    ErrorAutopsy("Falta de Base (Não lembrava o conceito)", "Volte na videoaula e coloque a fórmula no Anki (Flashcards).")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocoVestCronogramaScreen(viewModel: AppViewModel) {
    val schedules by viewModel.allSchedules.collectAsState()
    val customCronogramaItems by viewModel.allCustomCronogramaItems.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf("Segunda") }
    var durationText by remember { mutableStateOf("") }
    var subjectTitleText by remember { mutableStateOf("") }

    var showImportCustomDialog by remember { mutableStateOf(false) }
    var importText by remember { mutableStateOf("") }
    var importError by remember { mutableStateOf("") }
    var importSuccess by remember { mutableStateOf("") }

    var showAddCustomItemDialog by remember { mutableStateOf(false) }
    var customContentInput by remember { mutableStateOf("") }
    var customWeekInput by remember { mutableStateOf("Semana 1") }
    var customIntervalInput by remember { mutableStateOf("") }

    val daysOfWeek = listOf("Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo")
    val sections = listOf("Cronograma ENEM 📝", "Cronograma ITA ✈️", "Manual Estratégico 🧠", "Blocos Semanais 🗓️", "Personalizado 🛠️")
    var selectedSectionTab by remember { mutableStateOf(0) }
    val uriHandler = LocalUriHandler.current

    Scaffold(
        floatingActionButton = {
            if (selectedSectionTab == 3) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("add_weekly_block_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Novo Bloco", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Text(
                    text = "Cronograma & Planejamento",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = "Consulte cronogramas oficiais de preparação, guias estratégicos ou organize seus próprios blocos de estudo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Countdown row
            item {
                Text(
                    text = "Contagem Regressiva para os Vestibulares ⏳",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(countdownEvents) { event ->
                        Card(
                            modifier = Modifier.width(180.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = event.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Data: ${event.date}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${event.daysLeft} dias",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${event.weeksLeft} semanas restantes",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Navigation Tab Row
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedSectionTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedSectionTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    sections.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedSectionTab == index,
                            onClick = { selectedSectionTab = index },
                            text = { Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Render active section
            when (selectedSectionTab) {
                0 -> {
                    // ENEM Schedule
                    item {
                        Text(
                            text = "Cronograma Completo ENEM (17 Semanas)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Matérias estruturadas semana a semana de 15/07 a 10/11.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Group by week
                    val groupedEnem = cronogramaEnemList.groupBy { it.week }
                    groupedEnem.forEach { (week, items) ->
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = week,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = items.firstOrNull()?.period ?: "",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items.forEach { block ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = block.subject,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                                if (block.watchLink != null) {
                                                    IconButton(
                                                        onClick = {
                                                            try {
                                                                uriHandler.openUri(block.watchLink)
                                                            } catch (e: Exception) {
                                                                // ignore
                                                            }
                                                        }
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.PlayCircle,
                                                            contentDescription = "Assistir Aula",
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(22.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // ITA Schedule
                    item {
                        Text(
                            text = "Cronograma de Elite ITA (15 Semanas)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Foco total na preparação avançada para a 1ª e 2ª fase do ITA.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val groupedIta = cronogramaItaList.groupBy { it.week }
                    groupedIta.forEach { (week, items) ->
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = week,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = items.firstOrNull()?.period ?: "",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items.forEach { block ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = block.subject,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                                if (block.watchLink != null) {
                                                    IconButton(
                                                        onClick = {
                                                            try {
                                                                uriHandler.openUri(block.watchLink)
                                                            } catch (e: Exception) {
                                                                // ignore
                                                            }
                                                        }
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.PlayCircle,
                                                            contentDescription = "Assistir Aula",
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(22.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Manual Estratégico
                    item {
                        Text(
                            text = "Manual Estratégico de Estudos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Estratégias avançadas de simulação de provas, autópsia dos erros e produção de redação.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Simulados e Revisão Subsection
                    item {
                        Text(
                            text = "📊 Estratégias de Simulados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    items(manualSimuladosList) { sim ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = sim.type,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tempo estimado: ${sim.timeEst}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Frequência sugerida: ${sim.frequency}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = sim.strategy,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Autópsia dos Erros Subsection
                    item {
                        Text(
                            text = "🔍 Autópsia dos Erros",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = "Identifique por que você errou a questão e aja cirurgicamente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(autopsiaErrosList) { error ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = error.cause,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Ação corretiva: ${error.action}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Guia de Redação Subsection
                    item {
                        Text(
                            text = "✍️ Guia de Redação (ENEM & ITA)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Redação ENEM",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Estrutura sugerida: Introdução, Desenvolvimento 1, Desenvolvimento 2, Conclusão.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Elementos Obrigatórios da Proposta de Intervenção (Conclusão):",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                val enemElements = listOf(
                                    "1. Agente (Quem vai resolver?)",
                                    "2. Ação (O que será feito?)",
                                    "3. Modo/Meio (Como será feito?)",
                                    "4. Efeito (Para que será feito?)",
                                    "5. Detalhamento (Informação extra de algum elemento)"
                                )
                                enemElements.forEach { element ->
                                    Text(
                                        text = "• $element",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Redação ITA",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Temas típicos: Ética na Ciência, Impacto da Tecnologia, Comportamento Humano.",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "❌ O que NÃO fazer:",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                val itaDonts = listOf(
                                    "Citar a Constituição Federal de forma forçada.",
                                    "Usar conectivos enlatados comuns de modelo ENEM.",
                                    "Apresentar Proposta de Intervenção engessada."
                                )
                                itaDonts.forEach { item ->
                                    Text(
                                        text = "• $item",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "✅ O que fazer:",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                val itaDos = listOf(
                                    "Usar Repertório Culto (Ciência e Filosofia Clássica/Moderna).",
                                    "Manter uma estrutura argumentativa fluida sem engessamento.",
                                    "Elaborar uma Conclusão Sintética que reafirma a tese, expondo a complexidade do problema."
                                )
                                itaDos.forEach { item ->
                                    Text(
                                        text = "• $item",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF2E7D32),
                                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Custom weekly study blocks (Original feature preserved perfectly)
                    item {
                        Text(
                            text = "Planejamento de Blocos Semanais",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Crie seus próprios blocos semanais customizados para estruturar sua rotina de estudos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(daysOfWeek) { day ->
                        val daySchedules = schedules.filter { it.dayOfWeek.lowercase() == day.lowercase() }
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = day,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    val totalMin = daySchedules.sumOf { it.durationMinutes }
                                    if (totalMin > 0) {
                                        SuggestionChip(
                                            onClick = { },
                                            label = { Text("$totalMin min") }
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                if (daySchedules.isEmpty()) {
                                    Text(
                                        text = "Nenhum bloco planejado para hoje.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                } else {
                                    daySchedules.forEach { block ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = block.subjectTitle,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = "Duração: ${block.durationMinutes} minutos",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            // Move block dropdown trigger
                                            var showMoveMenu by remember { mutableStateOf(false) }
                                            Box {
                                                IconButton(
                                                    onClick = { showMoveMenu = true },
                                                    modifier = Modifier.testTag("move_block_button_${block.id}")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.DriveFileMove,
                                                        contentDescription = "Mover Bloco",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                                DropdownMenu(
                                                    expanded = showMoveMenu,
                                                    onDismissRequest = { showMoveMenu = false }
                                                ) {
                                                    Text(
                                                        text = " Mover para:",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                                    )
                                                    daysOfWeek.forEach { targetDay ->
                                                        if (targetDay.lowercase() != day.lowercase()) {
                                                            DropdownMenuItem(
                                                                text = { Text(targetDay, fontSize = 13.sp) },
                                                                onClick = {
                                                                    viewModel.moveScheduleDay(block.id, targetDay)
                                                                    showMoveMenu = false
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            IconButton(onClick = { viewModel.deleteSchedule(block.id) }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Deletar",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                4 -> {
                    // Custom Cronograma Personalizado!
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Cronograma Personalizado 🛠️",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Importe ou cadastre seu próprio cronograma de estudos customizado para acompanhar seu progresso semanal.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showImportCustomDialog = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Importar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { showAddCustomItemDialog = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Novo Item", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (customCronogramaItems.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = { viewModel.clearAllCustomCronogramaItems() },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Apagar Todo o Cronograma", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (customCronogramaItems.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Nenhum item cadastrado no seu cronograma personalizado.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Clique em 'Importar' para colar sua lista ou 'Novo Item' para adicionar manualmente.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        val groupedCustom = customCronogramaItems.groupBy { it.week }
                        groupedCustom.forEach { (week, items) ->
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = week,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        items.forEachIndexed { idx, customItem ->
                                            if (idx > 0) Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                            
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = customItem.isCompleted,
                                                    onCheckedChange = {
                                                        viewModel.updateCustomCronogramaItem(customItem.copy(isCompleted = it))
                                                    }
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = customItem.content,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Medium,
                                                        textDecoration = if (customItem.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                                                        color = if (customItem.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    if (customItem.dateInterval.isNotBlank()) {
                                                        Text(
                                                            text = customItem.dateInterval,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                                        )
                                                    }
                                                }
                                                IconButton(
                                                    onClick = {
                                                        viewModel.deleteCustomCronogramaItem(customItem.id)
                                                    },
                                                    modifier = Modifier.size(36.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Remover",
                                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Novo Bloco de Estudo") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    var expandedDayDropdown by remember { mutableStateOf(false) }
                    
                    Box {
                        OutlinedButton(
                            onClick = { expandedDayDropdown = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Dia: $selectedDay")
                        }
                        DropdownMenu(
                            expanded = expandedDayDropdown,
                            onDismissRequest = { expandedDayDropdown = false }
                        ) {
                            daysOfWeek.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d) },
                                    onClick = {
                                        selectedDay = d
                                        expandedDayDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = subjectTitleText,
                        onValueChange = { subjectTitleText = it },
                        label = { Text("Assunto ou Disciplina") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it },
                        label = { Text("Duração (minutos)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val duration = durationText.toIntOrNull() ?: 0
                        if (subjectTitleText.isNotBlank() && duration > 0) {
                            viewModel.addSchedule(selectedDay, duration, subjectTitleText)
                            showAddDialog = false
                            durationText = ""
                            subjectTitleText = ""
                        }
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showImportCustomDialog) {
        AlertDialog(
            onDismissRequest = {
                showImportCustomDialog = false
                importText = ""
                importError = ""
                importSuccess = ""
            },
            title = { Text("Importar Cronograma 📋") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cole o seu cronograma formatado do Google Sheets ou Excel. Cada linha deve seguir um dos formatos:\n\nSemana | Conteúdo | Data\nou apenas:\nSemana | Conteúdo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = importText,
                        onValueChange = {
                            importText = it
                            importError = ""
                            importSuccess = ""
                        },
                        label = { Text("Dados do Cronograma") },
                        placeholder = { Text("Ex:\nSemana 1 | Introdução à Física | 15/07 a 21/07\nSemana 1 | Cinemática Escalar\nSemana 2 | Leis de Newton | 22/07 a 28/07") },
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        maxLines = 15
                    )
                    if (importError.isNotBlank()) {
                        Text(text = importError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    if (importSuccess.isNotBlank()) {
                        Text(text = importSuccess, color = Color(0xFF26A69A), style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importText.isNotBlank()) {
                            val lines = importText.split("\n")
                            var count = 0
                            for (line in lines) {
                                if (line.trim().isBlank()) continue
                                val parts = if (line.contains("|")) {
                                    line.split("|")
                                } else if (line.contains("\t")) {
                                    line.split("\t")
                                } else if (line.contains(";")) {
                                    line.split(";")
                                } else {
                                    line.split(",")
                                }
                                
                                if (parts.size >= 2) {
                                    val weekStr = parts[0].trim()
                                    val contentStr = parts[1].trim()
                                    val intervalStr = if (parts.size > 2) parts[2].trim() else ""
                                    
                                    if (contentStr.isNotBlank()) {
                                        viewModel.addCustomCronogramaItem(
                                            content = contentStr,
                                            week = weekStr,
                                            dateInterval = intervalStr
                                        )
                                        count++
                                    }
                                } else if (parts.size == 1 && parts[0].isNotBlank()) {
                                    viewModel.addCustomCronogramaItem(
                                        content = parts[0].trim(),
                                        week = "Geral",
                                        dateInterval = ""
                                    )
                                    count++
                                }
                            }
                            if (count > 0) {
                                importSuccess = "$count itens importados com sucesso!"
                                importText = ""
                                showImportCustomDialog = false
                            } else {
                                importError = "Nenhum item válido encontrado na importação."
                            }
                        } else {
                            importError = "Insira algum texto para importar."
                        }
                    }
                ) {
                    Text("Importar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImportCustomDialog = false
                        importText = ""
                        importError = ""
                        importSuccess = ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showAddCustomItemDialog) {
        AlertDialog(
            onDismissRequest = { showAddCustomItemDialog = false },
            title = { Text("Novo Item no Cronograma 🛠️") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = customWeekInput,
                        onValueChange = { customWeekInput = it },
                        label = { Text("Semana de Estudo") },
                        placeholder = { Text("Ex: Semana 1") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = customContentInput,
                        onValueChange = { customContentInput = it },
                        label = { Text("Conteúdo a ser estudado") },
                        placeholder = { Text("Ex: Dinâmica e Atrito") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = customIntervalInput,
                        onValueChange = { customIntervalInput = it },
                        label = { Text("Intervalo de Data / Período (Opcional)") },
                        placeholder = { Text("Ex: 15/07 a 21/07") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customContentInput.isNotBlank() && customWeekInput.isNotBlank()) {
                            viewModel.addCustomCronogramaItem(
                                content = customContentInput,
                                week = customWeekInput,
                                dateInterval = customIntervalInput
                            )
                            customContentInput = ""
                            customIntervalInput = ""
                            showAddCustomItemDialog = false
                        }
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomItemDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocoVestTrilhasScreen(viewModel: AppViewModel) {
    val subjects by viewModel.allSubjects.collectAsState()
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var categoryInput by remember { mutableStateOf("Matemática") }

    val categories = listOf("Matemática", "Física", "Biologia", "Redação", "História")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSubjectDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Text(
                    text = "Trilhas & Checklist",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = "Acompanhe de forma analítica seu progresso dentro de cada disciplina.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Categories list and subjects
            categories.forEach { cat ->
                val catSubjects = subjects.filter { it.category.lowercase() == cat.lowercase() }
                
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Badge(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ) {
                                Text(
                                    text = "${catSubjects.count { it.getProgressPercent() == 100 }}/${catSubjects.size}",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        if (catSubjects.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                            ) {
                                Text(
                                    text = "Nenhum assunto cadastrado em $cat.",
                                    modifier = Modifier.padding(16.dp),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            catSubjects.forEach { sub ->
                                var isExpanded by remember { mutableStateOf(false) }
                                
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .clickable { isExpanded = !isExpanded }
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = sub.title,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                LinearProgressIndicator(
                                                    progress = { sub.getProgressPercent() / 100f },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(6.dp)
                                                        .clip(CircleShape),
                                                    color = MaterialTheme.colorScheme.primary,
                                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Text(
                                                text = "${sub.getProgressPercent()}%",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        // Detailed checklists when expanded
                                        AnimatedVisibility(visible = isExpanded) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 16.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                HorizontalDivider()
                                                Text(
                                                    text = "Etapas de Estudo (Clique para alternar):",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )

                                                val steps = listOf(
                                                    "Aula" to "aula",
                                                    "Resumo" to "resumo",
                                                    "Autoexplicação" to "autoexplicacao",
                                                    "Exercícios" to "exercicios",
                                                    "Caderno de Erros" to "cadernoerros",
                                                    "Revisão Espaçada" to "revisao",
                                                    "Simulado" to "simulado"
                                                )

                                                steps.forEach { step ->
                                                    val checked = when (step.second) {
                                                        "aula" -> sub.stepAula
                                                        "resumo" -> sub.stepResumo
                                                        "autoexplicacao" -> sub.stepAutoexplicacao
                                                        "exercicios" -> sub.stepExercicios
                                                        "cadernoerros" -> sub.stepCadernoErros
                                                        "revisao" -> sub.stepRevisao
                                                        "simulado" -> sub.stepSimulado
                                                        else -> false
                                                    }

                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                viewModel.toggleSubjectStep(sub, step.second)
                                                            }
                                                            .padding(vertical = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(step.first, fontSize = 14.sp)
                                                        Checkbox(
                                                            checked = checked,
                                                            onCheckedChange = { viewModel.toggleSubjectStep(sub, step.second) }
                                                        )
                                                    }
                                                }

                                                Box(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    contentAlignment = Alignment.CenterEnd
                                                ) {
                                                    TextButton(
                                                        onClick = { viewModel.deleteSubject(sub.id) },
                                                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                                    ) {
                                                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("Deletar Assunto", fontSize = 12.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }

    if (showAddSubjectDialog) {
        AlertDialog(
            onDismissRequest = { showAddSubjectDialog = false },
            title = { Text("Novo Assunto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    var expandedCatDropdown by remember { mutableStateOf(false) }
                    
                    Box {
                        OutlinedButton(
                            onClick = { expandedCatDropdown = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Matéria: $categoryInput")
                        }
                        DropdownMenu(
                            expanded = expandedCatDropdown,
                            onDismissRequest = { expandedCatDropdown = false }
                        ) {
                            categories.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = {
                                        categoryInput = c
                                        expandedCatDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Nome do Assunto") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleInput.isNotBlank()) {
                            viewModel.addNewSubject(titleInput, categoryInput)
                            showAddSubjectDialog = false
                            titleInput = ""
                        }
                    }
                ) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubjectDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun FocoVestAnkiScreen(viewModel: AppViewModel) {
    val flashcards by viewModel.allFlashcards.collectAsState()
    val dueFlashcards = remember(flashcards) {
        flashcards.filter { it.dueDate <= System.currentTimeMillis() }
    }

    var showAddCardDialog by remember { mutableStateOf(false) }
    var questionText by remember { mutableStateOf("") }
    var answerText by remember { mutableStateOf("") }

    var isAnswerVisible by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Revisão Espaçada",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Retenção com repetição espaçada SM-2.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Button(onClick = { showAddCardDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Criar Card", fontSize = 12.sp)
                }
            }
        }

        // Active revision card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Sessão de Revisão",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (dueFlashcards.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF26A69A),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tudo revisado por hoje! 💫",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Adicione mais cartões acima se quiser treinar.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        val activeCard = dueFlashcards.first()
                        
                        Text(
                            text = activeCard.question,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        )

                        AnimatedVisibility(
                            visible = isAnswerVisible,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                                Text(
                                    text = activeCard.answer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "Como foi sua resposta?",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.answerFlashcard(activeCard, 0)
                                            isAnswerVisible = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Errei", fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.answerFlashcard(activeCard, 3)
                                            isAnswerVisible = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300))
                                    ) {
                                        Text("Difícil", fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.answerFlashcard(activeCard, 5)
                                            isAnswerVisible = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A))
                                    ) {
                                        Text("Fácil", fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        if (!isAnswerVisible) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { isAnswerVisible = true }) {
                                Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mostrar Resposta")
                            }
                        }
                    }
                }
            }
        }

        // List of all flashcards
        item {
            Text(
                text = "Meus Cartões (${flashcards.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (flashcards.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Nenhum cartão cadastrado ainda.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(flashcards) { card ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = card.question,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Resp: ${card.answer}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(onClick = { viewModel.deleteFlashcard(card.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Excluir",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddCardDialog) {
        AlertDialog(
            onDismissRequest = { showAddCardDialog = false },
            title = { Text("Novo Flashcard") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = questionText,
                        onValueChange = { questionText = it },
                        label = { Text("Pergunta / Frente") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false
                    )

                    OutlinedTextField(
                        value = answerText,
                        onValueChange = { answerText = it },
                        label = { Text("Resposta / Verso") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (questionText.isNotBlank() && answerText.isNotBlank()) {
                            viewModel.addNewFlashcard(questionText, answerText)
                            showAddCardDialog = false
                            questionText = ""
                            answerText = ""
                        }
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCardDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun FocoVestSimuladosScreen(viewModel: AppViewModel) {
    val history by viewModel.allSimulados.collectAsState()

    var subjectText by remember { mutableStateOf("") }
    var totalText by remember { mutableStateOf("") }
    var correctText by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Simulados e Provas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = "Registre seus acertos em simulados para alimentar as projeções do Dashboard.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Simulado submission form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Registrar Novo Simulado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = subjectText,
                        onValueChange = { subjectText = it },
                        label = { Text("Matéria ou Nome da Prova (ex: ENEM Matemática)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = correctText,
                            onValueChange = { correctText = it },
                            label = { Text("Acertos") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = totalText,
                            onValueChange = { totalText = it },
                            label = { Text("Questões Totais") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it },
                        label = { Text("Duração (minutos)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            val total = totalText.toIntOrNull() ?: 0
                            val correct = correctText.toIntOrNull() ?: 0
                            val duration = durationText.toIntOrNull() ?: 0
                            if (subjectText.isNotBlank() && total > 0 && correct <= total) {
                                viewModel.addNewSimulado(subjectText, total, correct, duration)
                                subjectText = ""
                                totalText = ""
                                correctText = ""
                                durationText = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Salvar Resultado")
                    }
                }
            }
        }

        // History
        item {
            Text(
                text = "Histórico de Simulados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (history.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Nenhum simulado registrado no histórico.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(history) { sim ->
                val pct = (sim.correctAnswers.toFloat() / sim.totalQuestions * 100)
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = sim.subject,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Acertos: ${sim.correctAnswers} de ${sim.totalQuestions} • Tempo: ${sim.durationMinutes} min",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (pct >= 70) Color(0xFF26A69A).copy(alpha = 0.12f)
                                            else Color(0xFFFF7043).copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "%.0f%%".format(pct),
                                fontWeight = FontWeight.Bold,
                                color = if (pct >= 70) Color(0xFF26A69A) else Color(0xFFFF7043)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocoVestVideosScreen(viewModel: AppViewModel) {
    val videos by viewModel.allVideos.collectAsState()
    val schedules by viewModel.allSchedules.collectAsState()
    val uriHandler = LocalUriHandler.current

    val uniqueScheduleSubjects = remember(schedules) {
        schedules.map { it.subjectTitle }.distinct()
    }

    var selectedCategoryFilter by remember { mutableStateOf("Todos") }
    val filterOptions = listOf("Todos", "Matemática", "Biologia", "Física", "Redação", "História")

    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("Matemática") }
    var newUrl by remember { mutableStateOf("") }

    var showEditDialog by remember { mutableStateOf(false) }
    var editingVideo by remember { mutableStateOf<VideoAula?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf("Matemática") }
    var editUrl by remember { mutableStateOf("") }

    val filteredVideos = remember(videos, selectedCategoryFilter) {
        if (selectedCategoryFilter == "Todos") videos
        else videos.filter { it.category.lowercase() == selectedCategoryFilter.lowercase() }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Aula",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Videoaulas Gratuitas 📺",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Assista a aulas recomendadas e siga seu cronograma.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { viewModel.resetVideosToEnemCronograma() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.padding(start = 8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restaurar Cronograma ENEM",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sincronizar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (uniqueScheduleSubjects.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Aulas do seu Cronograma 📅",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Clique em um tema para abrir uma busca rápida de vídeoaulas no YouTube.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            items(uniqueScheduleSubjects) { subjectTitle ->
                                Card(
                                    modifier = Modifier
                                        .width(200.dp)
                                        .clickable {
                                            try {
                                                val encodedQuery = java.net.URLEncoder.encode(subjectTitle, "UTF-8")
                                                uriHandler.openUri("https://www.youtube.com/results?search_query=$encodedQuery")
                                            } catch (e: Exception) {
                                                // ignore
                                            }
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = subjectTitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.heightIn(min = 36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "Buscar no YouTube",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "Buscar no YouTube",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Horizontal filter chips
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterOptions) { opt ->
                        FilterChip(
                            selected = selectedCategoryFilter == opt,
                            onClick = { selectedCategoryFilter = opt },
                            label = { Text(opt) }
                        )
                    }
                }
            }

            // Grid listing
            if (filteredVideos.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    ) {
                        Box(
                            modifier = Modifier.padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhuma aula cadastrada nesta matéria.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredVideos) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val url = if (item.youtubeIdOrUrl.startsWith("http")) {
                                    item.youtubeIdOrUrl
                                } else {
                                    "https://www.youtube.com/watch?v=${item.youtubeIdOrUrl}"
                                }
                                try {
                                    uriHandler.openUri(url)
                                } catch (e: Exception) {
                                    // Fallback / ignore
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column {
                            // Styled media placeholder representing Video Thumbnail
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primaryContainer,
                                                MaterialTheme.colorScheme.secondaryContainer
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.PlayCircleFilled,
                                        contentDescription = "Assistir Aula",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(52.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "CLIQUE PARA ASSISTIR • ${item.category.uppercase()}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            // Video details
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = if (item.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                                            contentDescription = null,
                                            tint = if (item.isCompleted) Color(0xFF26A69A) else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (item.isCompleted) "Estudado" else "Marcar como Estudado",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (item.isCompleted) Color(0xFF26A69A) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                editingVideo = item
                                                editTitle = item.title
                                                editCategory = item.category
                                                editUrl = item.youtubeIdOrUrl
                                                showEditDialog = true
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Editar aula",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Switch(
                                            checked = item.isCompleted,
                                            onCheckedChange = { viewModel.toggleVideoCompleted(item) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Adicionar Videoaula 📺") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Título da Aula") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Matéria / Categoria:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val cats = listOf("Matemática", "Biologia", "Física", "Redação", "História")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(cats) { c ->
                                FilterChip(
                                    selected = newCategory == c,
                                    onClick = { newCategory = c },
                                    label = { Text(c) }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newUrl,
                        onValueChange = { newUrl = it },
                        label = { Text("Link do YouTube ou ID") },
                        placeholder = { Text("Ex: https://www.youtube.com/watch?v=...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank() && newUrl.isNotBlank()) {
                            viewModel.addNewVideo(newTitle, newCategory, newUrl)
                            newTitle = ""
                            newUrl = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showEditDialog && editingVideo != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar Videoaula 📺") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Título da Aula") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Matéria / Categoria:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val cats = listOf("Matemática", "Biologia", "Física", "Redação", "História")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(cats) { c ->
                                FilterChip(
                                    selected = editCategory == c,
                                    onClick = { editCategory = c },
                                    label = { Text(c) }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = editUrl,
                        onValueChange = { editUrl = it },
                        label = { Text("Link do YouTube ou ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editTitle.isNotBlank() && editUrl.isNotBlank()) {
                            viewModel.updateVideo(
                                editingVideo!!.copy(
                                    title = editTitle,
                                    category = editCategory,
                                    youtubeIdOrUrl = editUrl
                                )
                            )
                            showEditDialog = false
                            editingVideo = null
                        }
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun FocoVestTutorScreen(viewModel: AppViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    
    var inputText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val suggestionPrompts = listOf(
        "Explicar função quadrática",
        "Dicas redação ENEM",
        "Questões leis de Newton",
        "Genética: 1ª lei de Mendel"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tutor IA Gratuito",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tire dúvidas locais de ENEM e ITA.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = { viewModel.clearChat() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Resetar Conversa",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Chat Bubble area
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(messages) { msg ->
                val isAI = msg.sender == "AI"
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isAI) Alignment.CenterStart else Alignment.CenterEnd
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAI) MaterialTheme.colorScheme.surfaceVariant
                            else MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isAI) 2.dp else 16.dp,
                            bottomEnd = if (isAI) 16.dp else 2.dp
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isAI) "Tutor IA 🤖" else "Você 🧑",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isAI) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isAI) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            if (isChatLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tutor IA está pensando...", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Prompt Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(suggestionPrompts) { prompt ->
                SuggestionChip(
                    onClick = {
                        viewModel.sendChatMessage(prompt)
                    },
                    label = { Text(prompt) }
                )
            }
        }

        // Message input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Perguntar ao tutor...") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendChatMessage(inputText)
                        inputText = ""
                        keyboardController?.hide()
                    }
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun FocoVestRedacaoScreen(viewModel: AppViewModel) {
    val essays by viewModel.allEssays.collectAsState()
    val isCorrecting by viewModel.isEssayCorrecting.collectAsState()

    var essayTitle by remember { mutableStateOf("") }
    var essayText by remember { mutableStateOf("") }
    var confirmedDataPrivacy by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Corretor de Redação",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = "Insira o título e escreva/cole seu texto para obter avaliação de redação do ENEM.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Input forms
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Nova Redação para Correção",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = essayTitle,
                        onValueChange = { essayTitle = it },
                        label = { Text("Tema ou Título da Redação") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = essayText,
                        onValueChange = { essayText = it },
                        label = { Text("Texto da redação (máximo 2 MB / ~30 linhas)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        singleLine = false
                    )

                    // simulated upload and privacy check
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { confirmedDataPrivacy = !confirmedDataPrivacy }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = confirmedDataPrivacy,
                            onCheckedChange = { confirmedDataPrivacy = it }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Confirmo que removi todos os meus dados pessoais sensíveis e autorizo o processamento off-line.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = {
                            if (essayTitle.isNotBlank() && essayText.isNotBlank() && confirmedDataPrivacy) {
                                viewModel.correctEssay(essayTitle, essayText)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = confirmedDataPrivacy && !isCorrecting && essayTitle.isNotBlank() && essayText.isNotBlank()
                    ) {
                        if (isCorrecting) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Avaliando Redação...")
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submeter para Correção por IA")
                        }
                    }
                }
            }
        }

        // Historical Essay Corrections List
        item {
            Text(
                text = "Redações Corrigidas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (essays.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Nenhuma redação corrigida ainda.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(essays) { essay ->
                var showFeedbackDialog by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showFeedbackDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = essay.title,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Clique para abrir o feedback do corretor",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { viewModel.deleteEssay(essay.id) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                if (showFeedbackDialog) {
                    AlertDialog(
                        onDismissRequest = { showFeedbackDialog = false },
                        title = { Text(essay.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        text = {
                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Sua Redação:",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = essay.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Avaliação da Correção por IA:",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = essay.feedback,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showFeedbackDialog = false }) {
                                Text("Fechar")
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocoVestFerramentasScreen(viewModel: AppViewModel) {
    val errors by viewModel.allErrors.collectAsState()

    var showAddErrorDialog by remember { mutableStateOf(false) }
    var errorSubject by remember { mutableStateOf("") }
    var errorQuestion by remember { mutableStateOf("") }
    var errorReasonInput by remember { mutableStateOf("") }
    var correctConceptInput by remember { mutableStateOf("") }

    val timerSecondsLeft by viewModel.pomodoroSecondsLeft.collectAsState()
    val timerIsRunning by viewModel.pomodoroIsRunning.collectAsState()
    val timerBlockMinutes by viewModel.pomodoroBlockMinutes.collectAsState()

    val formattedTime = remember(timerSecondsLeft) {
        val mins = timerSecondsLeft / 60
        val secs = timerSecondsLeft % 60
        "%02d:%02d".format(mins, secs)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Ferramentas de Foco",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = "Aumente seu foco com o Pomodoro e organize erros recorrentes para não errar de novo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Pomodoro Timer Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⏱️ Timer Pomodoro",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Block Minutes selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(25, 50, 90).forEach { mins ->
                            FilterChip(
                                selected = timerBlockMinutes == mins,
                                onClick = { viewModel.selectPomodoroBlock(mins) },
                                label = { Text("$mins min") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Large circular/text representation
                    Text(
                        text = formattedTime,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = {
                                if (timerIsRunning) viewModel.pausePomodoro()
                                else viewModel.startPomodoro()
                            }
                        ) {
                            Icon(
                                imageVector = if (timerIsRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (timerIsRunning) "Pausar" else "Iniciar")
                        }

                        OutlinedButton(onClick = { viewModel.resetPomodoro() }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reiniciar")
                        }
                    }
                }
            }
        }

        // Caderno de Erros Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Caderno de Erros",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = { showAddErrorDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Registrar Erro", fontSize = 12.sp)
                }
            }
        }

        if (errors.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Nenhum erro catalogado ainda.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(errors) { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SuggestionChip(
                                onClick = {},
                                label = { Text(error.subject) }
                            )
                            IconButton(onClick = { viewModel.deleteError(error.id) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Questão: ${error.questionText}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Por que errei? ${error.errorReason}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Conceito correto: ${error.correctConcept}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF26A69A)
                        )
                    }
                }
            }
        }
    }

    if (showAddErrorDialog) {
        AlertDialog(
            onDismissRequest = { showAddErrorDialog = false },
            title = { Text("Registrar Novo Erro") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = errorSubject,
                        onValueChange = { errorSubject = it },
                        label = { Text("Matéria (ex: Química, Matemática)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = errorQuestion,
                        onValueChange = { errorQuestion = it },
                        label = { Text("Enunciado ou Resumo da Questão") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false
                    )
                    OutlinedTextField(
                        value = errorReasonInput,
                        onValueChange = { errorReasonInput = it },
                        label = { Text("Padrão do Erro (Ex: Errei sinal, falta de atenção)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false
                    )
                    OutlinedTextField(
                        value = correctConceptInput,
                        onValueChange = { correctConceptInput = it },
                        label = { Text("Conceito Correto") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (errorSubject.isNotBlank() && errorQuestion.isNotBlank()) {
                            viewModel.addNewError(errorSubject, errorQuestion, errorReasonInput, correctConceptInput)
                            showAddErrorDialog = false
                            errorSubject = ""
                            errorQuestion = ""
                            errorReasonInput = ""
                            correctConceptInput = ""
                        }
                    }
                ) {
                    Text("Registrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddErrorDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
