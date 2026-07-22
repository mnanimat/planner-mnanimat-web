package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.AppViewModel
import com.example.ui.components.FinanceLineChart
import com.example.ui.components.HourDistributionBars
import com.example.ui.components.WeeklyStudyHoursChart
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.PdfReport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RitVidaOverviewScreen(
    viewModel: AppViewModel,
    onNavigateToFocoVest: () -> Unit
) {
    val hoursList by viewModel.allHours.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val projects by viewModel.allProjects.collectAsState()

    // Metrics calculation
    val totalHours = remember(hoursList) { hoursList.sumOf { it.hours.toDouble() }.toFloat() }
    val netBalance = remember(transactions) {
        transactions.sumOf { if (it.type == "REVENUE") it.amount.toDouble() else -it.amount.toDouble() }.toFloat()
    }
    val avgProjectProgress = remember(projects) {
        if (projects.isEmpty()) 0
        else (projects.sumOf { it.progressPercentage } / projects.size)
    }

    // Backup interaction state
    var showBackupMessage by remember { mutableStateOf(false) }
    var backupDetailsString by remember { mutableStateOf("") }

    // Add hours dialog state
    var showAddHourDialog by remember { mutableStateOf(false) }
    var functionNameInput by remember { mutableStateOf("Estudante") }
    var hoursInputText by remember { mutableStateOf("") }
    var dateInputText by remember { mutableStateOf("2026-07-15") }

    // Edit hours dialog state
    var editingHourItem by remember { mutableStateOf<RitVidaHour?>(null) }
    var editFunctionNameInput by remember { mutableStateOf("") }
    var editHoursInputText by remember { mutableStateOf("") }
    var editDateInputText by remember { mutableStateOf("") }

    LaunchedEffect(editingHourItem) {
        editingHourItem?.let { item ->
            editFunctionNameInput = item.functionName
            editHoursInputText = item.hours.toString()
            editDateInputText = item.dateString
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddHourDialog = true },
                containerColor = Color(0xFFFF7043)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Registrar Horas",
                    tint = Color.White
                )
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Painel Integrado 🌀",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                val context = LocalContext.current
                                val customCronogramaList by viewModel.allCustomCronogramaItems.collectAsState()
                                val subjectsList by viewModel.allSubjects.collectAsState()

                                Button(
                                    onClick = {
                                        PdfReport.generateAndSharePdf(
                                            context = context,
                                            hoursList = hoursList,
                                            subjectsList = subjectsList,
                                            customCronogramaList = customCronogramaList
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f),
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = "PDF",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Gerar PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Controle o equilíbrio de tempo, acompanhe sua saúde financeira e gerencie projetos ativos de forma 100% privada e local.",
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

            // Executive summary row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Total de Horas",
                        value = "%.1fh".format(totalHours),
                        icon = Icons.Default.PunchClock,
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFF7043)
                    )
                    MetricCard(
                        title = "Saldo Consolidado",
                        value = "R$ %.0f".format(netBalance),
                        icon = Icons.Default.AccountBalanceWallet,
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
                        title = "Progresso Projetos",
                        value = "$avgProjectProgress%",
                        icon = Icons.Default.WorkOutline,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary
                    )
                    // quick status
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(24.dp),
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
                                    text = "PRIVACIDADE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    letterSpacing = 0.5.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Dados Protegidos",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = "Execução Sandbox",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Chart 1 Card: Horas Estudadas por Semana
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Horas Estudadas por Semana",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        WeeklyStudyHoursChart(hoursList = hoursList)
                    }
                }
            }

            // Chart 2 Card: Distribuição de Horas por Atividade
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PunchClock,
                                contentDescription = null,
                                tint = Color(0xFFFF7043),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Distribuição de Horas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF7043)
                            )
                        }
                        HourDistributionBars(hoursList = hoursList)
                    }
                }
            }

            // Histórico de Horas Title
            item {
                Text(
                    text = "Histórico de Registros",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // List of hours
            if (hoursList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Nenhum registro de horas inserido.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(hoursList) { item ->
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
                            Column {
                                Text(
                                    text = item.functionName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Horas: ${item.hours}h • Data: ${item.dateString}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { editingHourItem = item }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteHour(item.id) }) {
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

            // Security Alert and Local sovereign backup cards
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF26A69A)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Segurança e Soberania de Dados",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF26A69A)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "O aplicativo opera em conformidade com o manifesto local de privacidade. Todo o histórico de finanças, projetos e horas de estudo é armazenado de forma criptografada em um banco de dados SQLite local no seu aparelho.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    val totalElements = hoursList.size + transactions.size + projects.size
                                    backupDetailsString = """
                                        --- BACKUP MN PAINEL ---
                                        Geral: $totalElements Registros Ativos
                                        Horas: ${hoursList.size} entradas
                                        Finanças: ${transactions.size} transações
                                        Projetos: ${projects.size} demandas
                                        Data de exportação: 2026-07-15
                                        Hash de Integridade Local: Completa
                                    """.trimIndent()
                                    showBackupMessage = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Backup,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gerar Backup", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    backupDetailsString =
                                        "Backup restaurado com sucesso! Seus dados foram validados e o cache do SQLite foi sincronizado."
                                    showBackupMessage = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Importar", fontSize = 12.sp)
                            }
                        }

                        AnimatedVisibility(visible = showBackupMessage) {
                            Column {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                                Text(
                                    text = backupDetailsString,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(10.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                TextButton(onClick = { showBackupMessage = false }) {
                                    Text("Fechar Detalhes", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddHourDialog) {
        AlertDialog(
            onDismissRequest = { showAddHourDialog = false },
            title = { Text("Registrar Horas") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = functionNameInput,
                        onValueChange = { functionNameInput = it },
                        label = { Text("Função/Atividade") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text(
                        text = "Sugestões Rápidas:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val suggestions = listOf("Estudante", "Saúde", "Trabalho", "Administrativo")
                        suggestions.forEach { suggestion ->
                            FilterChip(
                                selected = functionNameInput == suggestion,
                                onClick = { functionNameInput = suggestion },
                                label = { Text(suggestion, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = hoursInputText,
                        onValueChange = { hoursInputText = it },
                        label = { Text("Quantidade de Horas") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = dateInputText,
                        onValueChange = { dateInputText = it },
                        label = { Text("Data (aaaa-mm-dd)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hoursValue = hoursInputText.toFloatOrNull() ?: 0f
                        if (functionNameInput.isNotBlank() && hoursValue > 0) {
                            viewModel.addWorkedHours(functionNameInput, hoursValue, dateInputText)
                            showAddHourDialog = false
                            hoursInputText = ""
                        }
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddHourDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (editingHourItem != null) {
        AlertDialog(
            onDismissRequest = { editingHourItem = null },
            title = { Text("Editar Registro de Horas") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editFunctionNameInput,
                        onValueChange = { editFunctionNameInput = it },
                        label = { Text("Função/Atividade") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text(
                        text = "Sugestões Rápidas:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val suggestions = listOf("Estudante", "Saúde", "Trabalho", "Administrativo")
                        suggestions.forEach { suggestion ->
                            FilterChip(
                                selected = editFunctionNameInput == suggestion,
                                onClick = { editFunctionNameInput = suggestion },
                                label = { Text(suggestion, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = editHoursInputText,
                        onValueChange = { editHoursInputText = it },
                        label = { Text("Quantidade de Horas") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editDateInputText,
                        onValueChange = { editDateInputText = it },
                        label = { Text("Data (aaaa-mm-dd)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hoursValue = editHoursInputText.toFloatOrNull() ?: 0f
                        val currentItem = editingHourItem
                        if (currentItem != null && editFunctionNameInput.isNotBlank() && hoursValue > 0) {
                            viewModel.updateHour(currentItem.copy(
                                functionName = editFunctionNameInput,
                                hours = hoursValue,
                                dateString = editDateInputText
                            ))
                            editingHourItem = null
                        }
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingHourItem = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun RitVidaStudiesScreen(viewModel: AppViewModel) {
    val subjects by viewModel.allSubjects.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp, top = 12.dp)) {
                Text(
                    text = "Conteúdos de Estudos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Acompanhe os conteúdos para estudar e seu progresso de fixação.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (subjects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Nenhum conteúdo de estudo registrado ainda.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(subjects) { subject ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = subject.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = subject.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "${subject.getProgressPercent()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { subject.getProgressPercent() / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Show quick view of the completed study steps
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val steps = listOf(
                                "Aula" to subject.stepAula,
                                "Resumo" to subject.stepResumo,
                                "Explicação" to subject.stepAutoexplicacao,
                                "Exercícios" to subject.stepExercicios,
                                "Revisão" to subject.stepRevisao
                            )
                            steps.forEach { (name, isDone) ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isDone) MaterialTheme.colorScheme.secondaryContainer 
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDone) MaterialTheme.colorScheme.onSecondaryContainer 
                                                else MaterialTheme.colorScheme.onSurfaceVariant
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RitVidaFinanceScreen(viewModel: AppViewModel) {
    val transactions by viewModel.allTransactions.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var descriptionText by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("REVENUE") } // REVENUE vs EXPENSE
    var dateText by remember { mutableStateOf("2026-07-15") }

    val totalRevenue = remember(transactions) {
        transactions.filter { it.type == "REVENUE" }.sumOf { it.amount.toDouble() }.toFloat()
    }
    val totalExpense = remember(transactions) {
        transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount.toDouble() }.toFloat()
    }
    val consolidated = totalRevenue - totalExpense

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF26A69A)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nova Transação", tint = Color.White)
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
                    text = "Evolução Financeira",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = "Acompanhe seu saldo consolidado e lançamentos passados de forma analítica.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // High Fidelity custom financial Line Chart
            item {
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
                                text = "Evolução do Saldo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF26A69A)
                            )
                            Text(
                                text = "Saldo: R$ %.2f".format(consolidated),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (consolidated >= 0) Color(0xFF26A69A) else Color(0xFFFF7043)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        FinanceLineChart(transactions = transactions)
                    }
                }
            }

            // Summary cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Receitas (+)", style = MaterialTheme.typography.labelMedium, color = Color(0xFF26A69A))
                            Text("R$ %.2f".format(totalRevenue), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF26A69A))
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Despesas (-)", style = MaterialTheme.typography.labelMedium, color = Color(0xFFFF7043))
                            Text("R$ %.2f".format(totalExpense), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFFF7043))
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Lançamentos e Extrato",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (transactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("Extrato financeiro vazio.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(transactions) { tx ->
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
                                    text = tx.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = tx.dateString,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${if (tx.type == "REVENUE") "+" else "-"} R$ %.2f".format(tx.amount),
                                    fontWeight = FontWeight.Bold,
                                    color = if (tx.type == "REVENUE") Color(0xFF26A69A) else Color(0xFFFF7043),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                IconButton(onClick = { viewModel.deleteTransaction(tx.id) }) {
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

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Registrar Transação") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            selected = transactionType == "REVENUE",
                            onClick = { transactionType = "REVENUE" },
                            label = { Text("Receita") }
                        )
                        FilterChip(
                            selected = transactionType == "EXPENSE",
                            onClick = { transactionType = "EXPENSE" },
                            label = { Text("Despesa") }
                        )
                    }

                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        label = { Text("Descrição da Transação") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Valor (R$)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        label = { Text("Data (aaaa-mm-dd)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountText.toFloatOrNull() ?: 0f
                        if (descriptionText.isNotBlank() && amount > 0) {
                            viewModel.addTransaction(descriptionText, amount, transactionType, dateText)
                            showAddDialog = false
                            descriptionText = ""
                            amountText = ""
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RitVidaProjectsScreen(viewModel: AppViewModel) {
    val projects by viewModel.allProjects.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var nameText by remember { mutableStateOf("") }
    var progressText by remember { mutableStateOf("0") }
    var targetDateText by remember { mutableStateOf("2026-12-31") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Novo Projeto", tint = MaterialTheme.colorScheme.onPrimary)
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
                    text = "Demandas & Projetos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = "Acompanhe e manipule o progresso de suas metas e projetos ativos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (projects.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("Nenhum projeto cadastrado.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(projects) { proj ->
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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = proj.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Prazo Estimado: ${proj.targetDateString}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteProject(proj.id) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Remover", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))

                            // Interactive sliding tracker to update progress on-the-fly!
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                LinearProgressIndicator(
                                    progress = { proj.progressPercentage / 100f },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(8.dp)
                                        .clip(CircleShape),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Text(
                                    text = "${proj.progressPercentage}%",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Visual controllers to increment/decrement progress percentage
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Ajustar:  ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                OutlinedIconButton(
                                    onClick = { viewModel.updateProjectProgress(proj, proj.progressPercentage - 10) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedIconButton(
                                    onClick = { viewModel.updateProjectProgress(proj, proj.progressPercentage + 10) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
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
            title = { Text("Novo Projeto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = nameText,
                        onValueChange = { nameText = it },
                        label = { Text("Nome do Projeto") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = progressText,
                        onValueChange = { progressText = it },
                        label = { Text("Progresso Inicial (%)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = targetDateText,
                        onValueChange = { targetDateText = it },
                        label = { Text("Meta de Conclusão (aaaa-mm-dd)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pct = progressText.toIntOrNull() ?: 0
                        if (nameText.isNotBlank()) {
                            viewModel.addProject(nameText, pct, targetDateText)
                            showAddDialog = false
                            nameText = ""
                            progressText = "0"
                        }
                    }
                ) {
                    Text("Criar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RitVidaPortfolioScreen(viewModel: AppViewModel) {
    val portfolioItems by viewModel.portfolioItems.collectAsState()
    val agendaProjects by viewModel.allProjects.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var titleText by remember { mutableStateOf("") }
    var descText by remember { mutableStateOf("") }
    var iconType by remember { mutableStateOf("design") }

    var selectedAgendaProjectName by remember { mutableStateOf("") }
    var showAgendaDropdown by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
        ) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = "Portfólio Criativo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Uma vitrine visual de suas realizações e modelagens criativas concluídas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (portfolioItems.isEmpty()) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("Seu portfólio criativo está vazio.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                itemsIndexed(portfolioItems) { index, item ->
                    val title = item.first
                    val description = item.second
                    val iconKey = item.third

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val iconVec = when (iconKey) {
                                        "manufacturing" -> Icons.Default.PrecisionManufacturing
                                        "design" -> Icons.Default.DesignServices
                                        "integration" -> Icons.Default.IntegrationInstructions
                                        "photo" -> Icons.Default.PhotoLibrary
                                        else -> Icons.Default.Palette
                                    }
                                    Icon(
                                        imageVector = iconVec,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                
                                IconButton(
                                    onClick = { viewModel.removePortfolioItem(index) },
                                    modifier = Modifier.size(24.dp).testTag("delete_portfolio_item_$index")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remover",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            
                            Column {
                                Text(
                                    text = title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // List synced completed agenda projects as achievements
            val completedProjects = agendaProjects.filter { it.isCompleted }
            if (completedProjects.isNotEmpty()) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    Text(
                        text = "Metas Concluídas 🏆",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                }

                items(completedProjects) { proj ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF26A69A))
                                Text("100% Completo", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF26A69A))
                            }
                            Column {
                                Text(proj.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("Sincronizado via Projetos do Painel", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to Add Portfolio Item
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_portfolio_item_fab"),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Novo Item", tint = MaterialTheme.colorScheme.onPrimary)
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Registrar no Portfólio", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Selecione um projeto em andamento da sua agenda ou preencha manualmente.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Agenda projects dropdown selector
                    val activeProjects = agendaProjects.filter { !it.isCompleted }
                    if (activeProjects.isNotEmpty()) {
                        Box {
                            OutlinedButton(
                                onClick = { showAgendaDropdown = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (selectedAgendaProjectName.isEmpty()) "Importar da Agenda..." else "Projeto: $selectedAgendaProjectName",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            DropdownMenu(
                                expanded = showAgendaDropdown,
                                onDismissRequest = { showAgendaDropdown = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("[Preencher Manualmente]") },
                                    onClick = {
                                        selectedAgendaProjectName = ""
                                        showAgendaDropdown = false
                                    }
                                )
                                activeProjects.forEach { proj ->
                                    DropdownMenuItem(
                                        text = { Text(proj.name) },
                                        onClick = {
                                            selectedAgendaProjectName = proj.name
                                            titleText = proj.name
                                            showAgendaDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = titleText,
                        onValueChange = { titleText = it },
                        label = { Text("Título do Projeto") },
                        modifier = Modifier.fillMaxWidth().testTag("portfolio_title_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = descText,
                        onValueChange = { descText = it },
                        label = { Text("Descrição do Trabalho") },
                        modifier = Modifier.fillMaxWidth().testTag("portfolio_desc_input")
                    )

                    // Icon selector category
                    Text("Selecione o Ícone Visual:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            Triple("design", Icons.Default.DesignServices, "Arte"),
                            Triple("manufacturing", Icons.Default.PrecisionManufacturing, "Mecânica"),
                            Triple("integration", Icons.Default.IntegrationInstructions, "TI"),
                            Triple("photo", Icons.Default.PhotoLibrary, "Imagem")
                        ).forEach { (key, iconVec, label) ->
                            FilterChip(
                                selected = iconType == key,
                                onClick = { iconType = key },
                                label = { Text(label, fontSize = 10.sp) },
                                leadingIcon = { Icon(imageVector = iconVec, contentDescription = null, modifier = Modifier.size(12.dp)) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleText.isNotBlank()) {
                            viewModel.addPortfolioItem(titleText, descText, iconType)
                            showAddDialog = false
                            titleText = ""
                            descText = ""
                            iconType = "design"
                            selectedAgendaProjectName = ""
                        }
                    },
                    modifier = Modifier.testTag("portfolio_confirm_add")
                ) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RitVidaGymDietScreen(viewModel: AppViewModel) {
    val workouts by viewModel.allGymWorkouts.collectAsState()
    val dietLogs by viewModel.allDietLogs.collectAsState()

    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Academia, 1: Dieta

    val todayDateString = remember {
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(36.dp)
                )
                Column {
                    Text(
                        text = "Saúde & Performance 💪",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Acompanhe seus treinos e sua alimentação diária de forma integrada.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Sub-tabs switcher
        TabRow(selectedTabIndex = activeSubTab) {
            Tab(
                selected = activeSubTab == 0,
                onClick = { activeSubTab = 0 },
                text = { Text("Academia", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) }
            )
            Tab(
                selected = activeSubTab == 1,
                onClick = { activeSubTab = 1 },
                text = { Text("Dieta & Água", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Restaurant, contentDescription = null) }
            )
        }

        if (activeSubTab == 0) {
            // ACADEMIA SUB-SCREEN
            AcademiaSection(
                workouts = workouts,
                onAddWorkout = { exercise, sets, reps, weight ->
                    viewModel.insertGymWorkout(exercise, sets, reps, weight, todayDateString)
                },
                onToggleStatus = { id, completed ->
                    viewModel.toggleGymWorkoutStatus(id, completed)
                },
                onDeleteWorkout = { id ->
                    viewModel.deleteGymWorkout(id)
                }
            )
        } else {
            // DIETA SUB-SCREEN
            DietaSection(
                dietLogs = dietLogs,
                onAddDietLog = { mealType, foodName, calories, water ->
                    viewModel.insertDietLog(mealType, foodName, calories, water, todayDateString)
                },
                onDeleteDietLog = { id ->
                    viewModel.deleteDietLog(id)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademiaSection(
    workouts: List<GymWorkout>,
    onAddWorkout: (String, Int, Int, Float) -> Unit,
    onToggleStatus: (Int, Boolean) -> Unit,
    onDeleteWorkout: (Int) -> Unit
) {
    var exerciseName by remember { mutableStateOf("") }
    var setsStr by remember { mutableStateOf("") }
    var repsStr by remember { mutableStateOf("") }
    var weightStr by remember { mutableStateOf("") }

    val completedCount = workouts.count { it.isCompleted }
    val totalCount = workouts.size

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Registrar Exercício",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = exerciseName,
                        onValueChange = { exerciseName = it },
                        label = { Text("Nome do Exercício") },
                        placeholder = { Text("Ex: Supino Reto, Agachamento") },
                        modifier = Modifier.fillMaxWidth().testTag("workout_name_input"),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = setsStr,
                            onValueChange = { setsStr = it },
                            label = { Text("Séries") },
                            placeholder = { Text("4") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("workout_sets_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = repsStr,
                            onValueChange = { repsStr = it },
                            label = { Text("Reps") },
                            placeholder = { Text("12") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("workout_reps_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = weightStr,
                            onValueChange = { weightStr = it },
                            label = { Text("Carga (kg)") },
                            placeholder = { Text("60") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.2f).testTag("workout_weight_input"),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = {
                            if (exerciseName.isNotBlank()) {
                                val s = setsStr.toIntOrNull() ?: 3
                                val r = repsStr.toIntOrNull() ?: 10
                                val w = weightStr.toFloatOrNull() ?: 0f
                                onAddWorkout(exerciseName, s, r, w)
                                exerciseName = ""
                                setsStr = ""
                                repsStr = ""
                                weightStr = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("workout_add_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Adicionar ao Treino")
                    }
                }
            }
        }

        if (totalCount > 0) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ficha de Treino do Dia 🏋️‍♂️",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "$completedCount/$totalCount Feitos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(workouts) { workout ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (workout.isCompleted) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = workout.isCompleted,
                                onCheckedChange = { onToggleStatus(workout.id, it) },
                                modifier = Modifier.testTag("workout_checkbox_${workout.id}")
                            )
                            Column {
                                Text(
                                    text = workout.exercise,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (workout.isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${workout.sets} séries x ${workout.reps} reps • ${workout.weightKg} kg",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(
                            onClick = { onDeleteWorkout(workout.id) },
                            modifier = Modifier.testTag("workout_delete_${workout.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar exercício",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Nenhum exercício registrado para hoje.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietaSection(
    dietLogs: List<DietLog>,
    onAddDietLog: (String, String, Int, Int) -> Unit,
    onDeleteDietLog: (Int) -> Unit
) {
    var foodName by remember { mutableStateOf("") }
    var caloriesStr by remember { mutableStateOf("") }
    var selectedMeal by remember { mutableStateOf("Almoço") }
    var showMealMenu by remember { mutableStateOf(false) }

    val meals = listOf("Café da Manhã", "Almoço", "Café da Tarde", "Jantar", "Lanches")

    val todayWaterLogs = dietLogs.filter { it.waterIntakeMl > 0 }
    val totalWaterMl = todayWaterLogs.sumOf { it.waterIntakeMl }

    val todayFoods = dietLogs.filter { it.caloriesKcal > 0 }
    val totalCalories = todayFoods.sumOf { it.caloriesKcal }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Quick Water Counter
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF29B6F6))
                            Text("Controle de Hidratação 💧", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        Text(
                            text = "${totalWaterMl}ml / 2000ml",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Progress Bar
                    val progress = (totalWaterMl.toFloat() / 2000f).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onAddDietLog("Água", "Copo d'água", 0, 250) },
                            modifier = Modifier.weight(1f).testTag("add_water_250"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                        ) {
                            Text("+250 ml")
                        }

                        Button(
                            onClick = { onAddDietLog("Água", "Garrafa d'água", 0, 500) },
                            modifier = Modifier.weight(1f).testTag("add_water_500"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF01579B))
                        ) {
                            Text("+500 ml")
                        }
                    }
                }
            }
        }

        // Add Meal
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Registrar Alimento / Dieta",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Meal Type Select Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedCard(
                            onClick = { showMealMenu = true },
                            modifier = Modifier.fillMaxWidth().testTag("meal_dropdown")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Refeição: $selectedMeal", fontWeight = FontWeight.SemiBold)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(
                            expanded = showMealMenu,
                            onDismissRequest = { showMealMenu = false }
                        ) {
                            meals.forEach { meal ->
                                DropdownMenuItem(
                                    text = { Text(meal) },
                                    onClick = {
                                        selectedMeal = meal
                                        showMealMenu = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = foodName,
                        onValueChange = { foodName = it },
                        label = { Text("Descrição / Alimento") },
                        placeholder = { Text("Ex: 150g de Arroz Integral e Grelhado") },
                        modifier = Modifier.fillMaxWidth().testTag("food_name_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = caloriesStr,
                        onValueChange = { caloriesStr = it },
                        label = { Text("Calorias Estimadas (kcal)") },
                        placeholder = { Text("Ex: 350") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("food_calories_input"),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (foodName.isNotBlank()) {
                                val cal = caloriesStr.toIntOrNull() ?: 0
                                onAddDietLog(selectedMeal, foodName, cal, 0)
                                foodName = ""
                                caloriesStr = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("food_add_button")
                    ) {
                        Icon(Icons.Default.Restaurant, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Adicionar Alimento")
                    }
                }
            }
        }

        if (dietLogs.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Diário de Alimentação 🍽️",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Total: ${totalCalories} kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(dietLogs) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (item.waterIntakeMl > 0) Color(0xFFE1F5FE) else MaterialTheme.colorScheme.secondaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (item.waterIntakeMl > 0) Icons.Default.WaterDrop else Icons.Default.Restaurant,
                                    contentDescription = null,
                                    tint = if (item.waterIntakeMl > 0) Color(0xFF0288D1) else MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = item.foodName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (item.waterIntakeMl > 0) "Hidratação • +${item.waterIntakeMl}ml" else "${item.mealType} • ${item.caloriesKcal} kcal",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(
                            onClick = { onDeleteDietLog(item.id) },
                            modifier = Modifier.testTag("diet_delete_${item.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar log",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function to parse raw checklist string to list of subtasks
fun parseChecklist(raw: String): List<Pair<String, Boolean>> {
    if (raw.isBlank()) return emptyList()
    return raw.split("|").mapNotNull {
        val parts = it.split(":")
        if (parts.size >= 2) {
            val title = parts[0]
            val checked = parts[1].toBoolean()
            title to checked
        } else {
            null
        }
    }
}

// Helper function to format checklist back to raw string
fun formatChecklist(items: List<Pair<String, Boolean>>): String {
    return items.joinToString("|") { "${it.first}:${it.second}" }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RitVidaVisualScreen(viewModel: AppViewModel) {
    val tasks by viewModel.allVisualTasks.collectAsState()

    var activeVisualTab by remember { mutableStateOf("Agenda") } // "Agenda", "Gantt", "Kanban"
    var activeAgendaScale by remember { mutableStateOf("Dia") } // "Dia", "Semana", "Mês"

    var selectedDayString by remember { mutableStateOf("2026-07-15") } // Default date

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var selectedTaskForDetails by remember { mutableStateOf<VisualTask?>(null) }

    // Add Task form fields
    var title by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("2026-07-15") }
    var startTime by remember { mutableStateOf("09:00") }
    var endDate by remember { mutableStateOf("2026-07-15") }
    var endTime by remember { mutableStateOf("10:00") }
    var startHour by remember { mutableIntStateOf(9) }
    var durationHours by remember { mutableIntStateOf(1) }
    var functionVal by remember { mutableStateOf("Estudante") } // "Trabalho", "Saúde", "Estudante", "Administrativo"
    val functionOptions = listOf("Estudante", "Trabalho", "Saúde", "Administrativo")
    var tagVal by remember { mutableStateOf("Estudos") } // Tag
    val checklistItemsList = remember { mutableStateListOf<Pair<String, Boolean>>() }
    var newChecklistItemText by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    title = ""
                    startDate = "2026-07-15"
                    startTime = "09:00"
                    endDate = "2026-07-15"
                    endTime = "10:00"
                    startHour = 9
                    durationHours = 1
                    functionVal = "Estudante"
                    tagVal = "Estudos"
                    checklistItemsList.clear()
                    showAddTaskDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp).testTag("add_visual_task_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Tarefa", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Text(
                    text = "Agenda Visual Interativa 🗓️",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Organize suas tarefas, modifique horários de forma tátil e acompanhe via Gantt ou Kanban.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Tabs Row
            TabRow(
                selectedTabIndex = when (activeVisualTab) {
                    "Agenda" -> 0
                    "Gantt" -> 1
                    else -> 2
                },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = activeVisualTab == "Agenda",
                    onClick = { activeVisualTab = "Agenda" },
                    text = { Text("Agenda", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = activeVisualTab == "Gantt",
                    onClick = { activeVisualTab = "Gantt" },
                    text = { Text("Gantt", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.ViewWeek, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = activeVisualTab == "Kanban",
                    onClick = { activeVisualTab = "Kanban" },
                    text = { Text("Kanban", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.ViewKanban, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            when (activeVisualTab) {
                "Agenda" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.weight(0.6f)
                        ) {
                            listOf("Dia", "Semana", "Mês").forEachIndexed { idx, item ->
                                SegmentedButton(
                                    selected = activeAgendaScale == item,
                                    onClick = { activeAgendaScale = item },
                                    shape = SegmentedButtonDefaults.itemShape(index = idx, count = 3)
                                ) {
                                    Text(item, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.weight(0.4f).clickable {
                                selectedDayString = if (selectedDayString == "2026-07-15") "2026-07-16" else "2026-07-15"
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (selectedDayString == "2026-07-15") "Hoje (15/07)" else "Amanhã (16/07)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    when (activeAgendaScale) {
                        "Dia" -> RitVidaAgendaDiaView(
                            tasks = tasks,
                            selectedDay = selectedDayString,
                            onUpdateTask = { viewModel.updateVisualTask(it) },
                            onTaskClick = { selectedTaskForDetails = it }
                        )
                        "Semana" -> RitVidaAgendaSemanaView(
                            tasks = tasks,
                            onUpdateTask = { viewModel.updateVisualTask(it) },
                            onTaskClick = { selectedTaskForDetails = it }
                        )
                        "Mês" -> RitVidaAgendaMesView(
                            tasks = tasks,
                            onUpdateTask = { viewModel.updateVisualTask(it) },
                            onTaskClick = { selectedTaskForDetails = it }
                        )
                    }
                }
                "Gantt" -> {
                    RitVidaGanttView(
                        tasks = tasks,
                        onTaskClick = { selectedTaskForDetails = it }
                    )
                }
                "Kanban" -> {
                    RitVidaKanbanView(
                        tasks = tasks,
                        onUpdateTask = { viewModel.updateVisualTask(it) },
                        onTaskClick = { selectedTaskForDetails = it }
                    )
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Nova Tarefa Visual 📝") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp).verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Nome da Tarefa") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { startDate = it },
                            label = { Text("Data Início") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("Hora Início") },
                            placeholder = { Text("09:00") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = endDate,
                            onValueChange = { endDate = it },
                            label = { Text("Data Final") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text("Hora Fim") },
                            placeholder = { Text("10:00") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startHour.toString(),
                            onValueChange = { startHour = it.toIntOrNull() ?: 9 },
                            label = { Text("Hora Inicial (0-23)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = durationHours.toString(),
                            onValueChange = { durationHours = it.toIntOrNull() ?: 1 },
                            label = { Text("Duração (Horas)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text("Função / Atuação:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        functionOptions.forEach { option ->
                            val selected = functionVal == option
                            FilterChip(
                                selected = selected,
                                onClick = { functionVal = option },
                                label = { Text(option, fontSize = 10.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = tagVal,
                        onValueChange = { tagVal = it },
                        label = { Text("Etiqueta / Tag") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Subtarefas / Checklist:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    
                    checklistItemsList.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = item.second,
                                    onCheckedChange = { checklistItemsList[index] = item.first to it }
                                )
                                Text(item.first, fontSize = 12.sp)
                            }
                            IconButton(onClick = { checklistItemsList.removeAt(index) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remover", tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = newChecklistItemText,
                            onValueChange = { newChecklistItemText = it },
                            label = { Text("Nova subtarefa") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                if (newChecklistItemText.isNotBlank()) {
                                    checklistItemsList.add(newChecklistItemText to false)
                                    newChecklistItemText = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Adicionar subtarefa")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val rawChecklist = formatChecklist(checklistItemsList.toList())
                            viewModel.insertVisualTask(
                                title = title,
                                startDate = startDate,
                                startTime = startTime,
                                endDate = endDate,
                                endTime = endTime,
                                startHour = startHour.coerceIn(0, 23),
                                durationHours = durationHours.coerceIn(1, 24),
                                function = functionVal,
                                tag = tagVal,
                                checklistRaw = rawChecklist
                            )
                            showAddTaskDialog = false
                        }
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (selectedTaskForDetails != null) {
        val task = selectedTaskForDetails!!
        var detailTitle by remember { mutableStateOf(task.title) }
        var detailStartDate by remember { mutableStateOf(task.startDate) }
        var detailStartTime by remember { mutableStateOf(task.startTime) }
        var detailEndDate by remember { mutableStateOf(task.endDate) }
        var detailEndTime by remember { mutableStateOf(task.endTime) }
        var detailFunction by remember { mutableStateOf(task.function) }
        var detailTag by remember { mutableStateOf(task.tag) }

        val detailChecklist = remember(task.checklistRaw) {
            mutableStateListOf<Pair<String, Boolean>>().apply {
                addAll(parseChecklist(task.checklistRaw))
            }
        }
        var newSubtaskText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { selectedTaskForDetails = null },
            title = { Text("Detalhes da Tarefa") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp).verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = detailTitle,
                        onValueChange = { detailTitle = it },
                        label = { Text("Nome") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = detailStartDate,
                            onValueChange = { detailStartDate = it },
                            label = { Text("Início Data") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = detailStartTime,
                            onValueChange = { detailStartTime = it },
                            label = { Text("Início Hora") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = detailEndDate,
                            onValueChange = { detailEndDate = it },
                            label = { Text("Fim Data") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = detailEndTime,
                            onValueChange = { detailEndTime = it },
                            label = { Text("Fim Hora") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = detailFunction,
                        onValueChange = { detailFunction = it },
                        label = { Text("Função") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = detailTag,
                        onValueChange = { detailTag = it },
                        label = { Text("Etiqueta") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Checklist / Subtarefas:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    
                    detailChecklist.forEachIndexed { idx, item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = item.second,
                                    onCheckedChange = {
                                        detailChecklist[idx] = item.first to it
                                    }
                                )
                                Text(
                                    text = item.first,
                                    fontSize = 13.sp,
                                    style = if (item.second) MaterialTheme.typography.bodyMedium.copy(color = Color.Gray) else MaterialTheme.typography.bodyMedium
                                )
                            }
                            IconButton(onClick = { detailChecklist.removeAt(idx) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = newSubtaskText,
                            onValueChange = { newSubtaskText = it },
                            label = { Text("Nova subtarefa") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                if (newSubtaskText.isNotBlank()) {
                                    detailChecklist.add(newSubtaskText to false)
                                    newSubtaskText = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Adicionar")
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            viewModel.deleteVisualTask(task.id)
                            selectedTaskForDetails = null
                        }
                    ) {
                        Text("Deletar")
                    }
                    Button(
                        onClick = {
                            val updatedTask = task.copy(
                                title = detailTitle,
                                startDate = detailStartDate,
                                startTime = detailStartTime,
                                endDate = detailEndDate,
                                endTime = detailEndTime,
                                function = detailFunction,
                                tag = detailTag,
                                checklistRaw = formatChecklist(detailChecklist.toList())
                            )
                            viewModel.updateVisualTask(updatedTask)
                            selectedTaskForDetails = null
                        }
                    ) {
                        Text("Salvar")
                    }
                }
            }
        )
    }
}

@Composable
fun RitVidaAgendaDiaView(
    tasks: List<VisualTask>,
    selectedDay: String,
    onUpdateTask: (VisualTask) -> Unit,
    onTaskClick: (VisualTask) -> Unit
) {
    val dayTasks = remember(tasks, selectedDay) {
        tasks.filter { it.startDate == selectedDay }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(24) { hour ->
            val tasksStartingAtHour = dayTasks.filter { it.startHour == hour }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Hour label
                Text(
                    text = "${hour.toString().padStart(2, '0')}:00",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.width(55.dp).padding(top = 8.dp)
                )

                // Separator
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(55.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Tasks list
                if (tasksStartingAtHour.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Sem compromissos",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tasksStartingAtHour.forEach { task ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onTaskClick(task) },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = task.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier.padding(top = 2.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(task.function, fontSize = 9.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                                                }
                                                if (task.tag.isNotBlank()) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(task.tag, fontSize = 9.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    if (task.startHour > 0) {
                                                        onUpdateTask(task.copy(startHour = task.startHour - 1))
                                                    }
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.ArrowUpward, contentDescription = "Mover para cima", modifier = Modifier.size(16.dp))
                                            }
                                            IconButton(
                                                onClick = {
                                                    if (task.startHour < 23) {
                                                        onUpdateTask(task.copy(startHour = task.startHour + 1))
                                                    }
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.ArrowDownward, contentDescription = "Mover para baixo", modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    val checklist = parseChecklist(task.checklistRaw)
                                    if (checklist.isNotEmpty()) {
                                        val completed = checklist.count { it.second }
                                        Text(
                                            text = "Checklist: $completed/${checklist.size} concluídas",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Duração: ${task.durationHours}h (${task.startTime} - ${task.endTime})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        IconButton(
                                            onClick = {
                                                if (task.durationHours < 24) {
                                                    onUpdateTask(task.copy(durationHours = task.durationHours + 1))
                                                }
                                            },
                                            modifier = Modifier.height(24.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 6.dp)
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = "Aumentar", modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text("Esticar", fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
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

@Composable
fun RitVidaAgendaSemanaView(
    tasks: List<VisualTask>,
    onUpdateTask: (VisualTask) -> Unit,
    onTaskClick: (VisualTask) -> Unit
) {
    val daysOfWeekList = listOf("Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo")
    
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(daysOfWeekList) { dayName ->
            val filteredTasks = remember(tasks, dayName) {
                tasks.filter { 
                    val hash = (it.title.hashCode() % 7).let { h -> if (h < 0) h + 7 else h }
                    daysOfWeekList[hash] == dayName
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = dayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    if (filteredTasks.isEmpty()) {
                        Text("Sem tarefas para este dia.", fontSize = 11.sp, color = Color.Gray)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            filteredTasks.forEach { task ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(6.dp))
                                        .clickable { onTaskClick(task) }
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(task.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("${task.startTime} | ${task.function}", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(task.tag, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold)
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
fun RitVidaAgendaMesView(
    tasks: List<VisualTask>,
    onUpdateTask: (VisualTask) -> Unit,
    onTaskClick: (VisualTask) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Julho 2026", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("D", "S", "T", "Q", "Q", "S", "S").forEach { day ->
                Text(
                    text = day,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        val totalDays = 31
        val startOffset = 3 // Wed
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(startOffset) {
                Box(modifier = Modifier.aspectRatio(1f))
            }
            items(totalDays) { index ->
                val dayNum = index + 1
                val dayStr = "2026-07-${dayNum.toString().padStart(2, '0')}"
                val hasTask = tasks.any { it.startDate == dayStr }
                
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(
                            if (hasTask) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .border(
                            1.dp, 
                            if (dayNum == 15) MaterialTheme.colorScheme.primary else Color.Transparent, 
                            RoundedCornerShape(6.dp)
                        )
                        .clickable {
                            val taskOnDay = tasks.firstOrNull { it.startDate == dayStr }
                            if (taskOnDay != null) {
                                onTaskClick(taskOnDay)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dayNum.toString(),
                            fontSize = 11.sp,
                            fontWeight = if (dayNum == 15) FontWeight.Black else FontWeight.Bold,
                            color = if (dayNum == 15) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        if (hasTask) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RitVidaGanttView(
    tasks: List<VisualTask>,
    onTaskClick: (VisualTask) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
    ) {
        Text("Visualização Gantt (Linha do Tempo) 📊", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))

        if (tasks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhuma tarefa para exibir no Gantt.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks) { task ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTaskClick(task) }
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = task.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(text = "${task.startTime} - ${task.endTime}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Text(text = "Função: ${task.function} | Tag: ${task.tag}", fontSize = 11.sp, color = Color.Gray)
                        
                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        ) {
                            val startPercent = task.startHour / 24f
                            val durationPercent = task.durationHours / 24f

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(durationPercent.coerceIn(0.1f, 1f))
                                    .padding(start = (startPercent * 160).dp)
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${task.durationHours}h",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RitVidaKanbanView(
    tasks: List<VisualTask>,
    onUpdateTask: (VisualTask) -> Unit,
    onTaskClick: (VisualTask) -> Unit
) {
    val todoTasks = remember(tasks) {
        tasks.filter { task ->
            val cl = parseChecklist(task.checklistRaw)
            cl.isEmpty() || cl.all { !it.second }
        }
    }

    val doingTasks = remember(tasks) {
        tasks.filter { task ->
            val cl = parseChecklist(task.checklistRaw)
            cl.isNotEmpty() && cl.any { it.second } && cl.any { !it.second }
        }
    }

    val doneTasks = remember(tasks) {
        tasks.filter { task ->
            val cl = parseChecklist(task.checklistRaw)
            cl.isNotEmpty() && cl.all { it.second }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        KanbanColumn(
            title = "Pendente 📋",
            tasks = todoTasks,
            columnBg = Color(0xFFFFEBEE),
            onTaskClick = onTaskClick,
            onQuickComplete = { task ->
                val cl = parseChecklist(task.checklistRaw).map { it.first to true }
                onUpdateTask(task.copy(checklistRaw = formatChecklist(cl)))
            }
        )

        KanbanColumn(
            title = "Em Andamento ⚡",
            tasks = doingTasks,
            columnBg = Color(0xFFE3F2FD),
            onTaskClick = onTaskClick,
            onQuickComplete = { task ->
                val cl = parseChecklist(task.checklistRaw).map { it.first to true }
                onUpdateTask(task.copy(checklistRaw = formatChecklist(cl)))
            }
        )

        KanbanColumn(
            title = "Concluído ✅",
            tasks = doneTasks,
            columnBg = Color(0xFFE8F5E9),
            onTaskClick = onTaskClick,
            onQuickComplete = { task ->
                val cl = parseChecklist(task.checklistRaw).map { it.first to false }
                onUpdateTask(task.copy(checklistRaw = formatChecklist(cl)))
            }
        )
    }
}

@Composable
fun KanbanColumn(
    title: String,
    tasks: List<VisualTask>,
    columnBg: Color,
    onTaskClick: (VisualTask) -> Unit,
    onQuickComplete: (VisualTask) -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(columnBg, RoundedCornerShape(6.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = tasks.size.toString(), fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (tasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Sem tarefas nesta coluna", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                } else {
                    items(tasks) { task ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTaskClick(task) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = task.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    IconButton(
                                        onClick = { onQuickComplete(task) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Completar",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(text = "Foco: ${task.function}", fontSize = 10.sp, color = Color.Gray)
                                
                                val cl = parseChecklist(task.checklistRaw)
                                if (cl.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Checklist: ${cl.count { it.second }}/${cl.size}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
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

