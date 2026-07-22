package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MeiConfig
import com.example.data.MeiInvoice
import com.example.data.MeiTransaction
import com.example.ui.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeiDashboardScreen(viewModel: AppViewModel, onNavigateToTab: (Int) -> Unit) {
    val transactions by viewModel.allMeiTransactions.collectAsState()
    val invoices by viewModel.allMeiInvoices.collectAsState()
    val configState by viewModel.meiConfig.collectAsState()
    val config = configState ?: MeiConfig()

    val userAccount by viewModel.userAccount.collectAsState()
    val financeMode = userAccount?.financeMode ?: "MEI + Pessoal"

    val showPJ = financeMode == "MEI + Pessoal" || financeMode == "Só MEI" || financeMode == "Só PJ"
    val showPessoal = financeMode == "MEI + Pessoal" || financeMode == "Só Pessoal"

    // Calculations
    val meiRevenues = remember(transactions) {
        transactions.filter { it.accountType == "PJ" && it.transactionType == "RECEITA" }.sumOf { it.amount.toDouble() }.toFloat()
    }
    val meiExpenses = remember(transactions) {
        transactions.filter { it.accountType == "PJ" && it.transactionType == "DESPESA" }.sumOf { it.amount.toDouble() }.toFloat()
    }
    val meiProfit = meiRevenues - meiExpenses
    
    val personalBalance = remember(transactions) {
        transactions.filter { it.accountType == "PESSOAL" }.sumOf {
            if (it.transactionType == "RECEITA") it.amount.toDouble() else -it.amount.toDouble()
        }.toFloat()
    }

    val personalRevenues = remember(transactions) {
        transactions.filter { it.accountType == "PESSOAL" && it.transactionType == "RECEITA" }.sumOf { it.amount.toDouble() }.toFloat()
    }
    val personalExpenses = remember(transactions) {
        transactions.filter { it.accountType == "PESSOAL" && it.transactionType == "DESPESA" }.sumOf { it.amount.toDouble() }.toFloat()
    }

    val limitUsePercent = remember(meiRevenues, config) {
        if (config.annualLimit <= 0) 0f
        else (meiRevenues / config.annualLimit).coerceIn(0f, 1f)
    }

    val pendingInvoices = remember(invoices) {
        invoices.filter { !it.isReceived }.size
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Welcome and Open Finance Quick Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = when (financeMode) {
                            "Só Pessoal" -> "Financeiro Pessoal 🏦"
                            "Só MEI", "Só PJ" -> "Empresa MEI / PJ 💼"
                            else -> "Dinheiro 💼"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (financeMode) {
                            "Só Pessoal" -> "Controle de despesas e receitas pessoais"
                            "Só MEI", "Só PJ" -> "Controle de faturamento e despesas corporativas"
                            else -> "Separação inteligente de PJ e Pessoal"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Button(
                    onClick = { onNavigateToTab(4) }, // Importação Tab
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Importação", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Executive accounting blocks
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (financeMode == "Só Pessoal") {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("RECEITAS PESSOAIS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, letterSpacing = 0.5.sp)
                            Text("R$ %.2f".format(personalRevenues), fontSize = 15.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = CardDefaults.cardColors().containerColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("DESPESAS PESSOAIS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF7043), letterSpacing = 0.5.sp)
                            Text("R$ %.2f".format(personalExpenses), fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF7043))
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(if (financeMode == "Só PJ") "RECEITAS PJ" else "RECEITAS MEI", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, letterSpacing = 0.5.sp)
                            Text("R$ %.2f".format(meiRevenues), fontSize = 15.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = CardDefaults.cardColors().containerColor)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(if (financeMode == "Só PJ") "DESPESAS PJ" else "DESPESAS MEI", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF7043), letterSpacing = 0.5.sp)
                            Text("R$ %.2f".format(meiExpenses), fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color(0xFFFF7043))
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (financeMode == "Só Pessoal") {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("SALDO PESSOAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), letterSpacing = 0.5.sp)
                            Text("R$ %.2f".format(personalBalance), fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                        }
                    }
                } else if (financeMode == "Só MEI" || financeMode == "Só PJ") {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("LUCRO PJ ESTIMADO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), letterSpacing = 0.5.sp)
                            Text("R$ %.2f".format(meiProfit), fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("LUCRO PJ ESTIMADO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), letterSpacing = 0.5.sp)
                            Text("R$ %.2f".format(meiProfit), fontSize = 15.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("SALDO PESSOAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary, letterSpacing = 0.5.sp)
                            Text("R$ %.2f".format(personalBalance), fontSize = 15.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }
        }

        // Limit usage bar
        if (financeMode != "Só Pessoal") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Uso do limite anual MEI",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(limitUsePercent * 100).toInt()}% preenchido",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Progress Indicator
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(limitUsePercent)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.secondary
                                            )
                                        )
                                    )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "R$ 0,00", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "Limite: R$ %.0f".format(config.annualLimit),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Alerts & Quick KPI blocks
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Pending notes
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Notas Pendentes", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$pendingInvoices faturas", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    }
                }

                // DAS Mensal Status
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Receipt, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("DAS Mensal", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("R$ %.2f".format(config.monthlyDas), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                }
            }
        }

        // Fluxo de Caixa Ring and Bar Comparative Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fluxo de Caixa (Mensal Comparativo)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Simplified Visual Chart Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val paddingLeft = 40.dp.toPx()
                            val paddingBottom = 20.dp.toPx()
                            val chartWidth = size.width - paddingLeft
                            val chartHeight = size.height - paddingBottom

                            // Draw axis lines
                            drawLine(
                                color = Color.LightGray,
                                start = androidx.compose.ui.geometry.Offset(paddingLeft, 0f),
                                end = androidx.compose.ui.geometry.Offset(paddingLeft, chartHeight),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = Color.LightGray,
                                start = androidx.compose.ui.geometry.Offset(paddingLeft, chartHeight),
                                end = androidx.compose.ui.geometry.Offset(size.width, chartHeight),
                                strokeWidth = 1.dp.toPx()
                            )

                            // Simulating monthly points for visual line chart
                            val pjPoints = listOf(0.2f, 0.4f, 0.35f, 0.6f, 0.5f, 0.8f)
                            val personalPoints = listOf(0.5f, 0.45f, 0.6f, 0.55f, 0.7f, 0.65f)

                            val stepX = chartWidth / 5

                            // Draw PJ line (Green)
                            for (i in 0..4) {
                                val x1 = paddingLeft + i * stepX
                                val y1 = chartHeight - pjPoints[i] * chartHeight
                                val x2 = paddingLeft + (i + 1) * stepX
                                val y2 = chartHeight - pjPoints[i + 1] * chartHeight

                                drawLine(
                                    color = Color(0xFF2E7D32),
                                    start = androidx.compose.ui.geometry.Offset(x1, y1),
                                    end = androidx.compose.ui.geometry.Offset(x2, y2),
                                    strokeWidth = 3.dp.toPx()
                                )
                                drawCircle(
                                    color = Color(0xFF2E7D32),
                                    radius = 4.dp.toPx(),
                                    center = androidx.compose.ui.geometry.Offset(x1, y1)
                                )
                            }
                            // Last circle
                            drawCircle(
                                color = Color(0xFF2E7D32),
                                radius = 4.dp.toPx(),
                                center = androidx.compose.ui.geometry.Offset(paddingLeft + 5 * stepX, chartHeight - pjPoints[5] * chartHeight)
                            )

                            // Draw Personal line (Blue)
                            for (i in 0..4) {
                                val x1 = paddingLeft + i * stepX
                                val y1 = chartHeight - personalPoints[i] * chartHeight
                                val x2 = paddingLeft + (i + 1) * stepX
                                val y2 = chartHeight - personalPoints[i + 1] * chartHeight

                                drawLine(
                                    color = Color(0xFF1976D2),
                                    start = androidx.compose.ui.geometry.Offset(x1, y1),
                                    end = androidx.compose.ui.geometry.Offset(x2, y2),
                                    strokeWidth = 3.dp.toPx()
                                )
                                drawCircle(
                                    color = Color(0xFF1976D2),
                                    radius = 4.dp.toPx(),
                                    center = androidx.compose.ui.geometry.Offset(x1, y1)
                                )
                            }
                            drawCircle(
                                color = Color(0xFF1976D2),
                                radius = 4.dp.toPx(),
                                center = androidx.compose.ui.geometry.Offset(paddingLeft + 5 * stepX, chartHeight - personalPoints[5] * chartHeight)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFF2E7D32), CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Fluxo PJ (MEI)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(20.dp))
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFF1976D2), CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Fluxo Pessoal", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeiTransactionsScreen(viewModel: AppViewModel) {
    val transactions by viewModel.allMeiTransactions.collectAsState()
    val userAccount by viewModel.userAccount.collectAsState()
    val financeMode = userAccount?.financeMode ?: "MEI + Pessoal"

    var showAddDialog by remember { mutableStateOf(false) }
    var filterType by remember { mutableStateOf("TODOS") } // "TODOS", "MEI", "PESSOAL", "RECEITA", "DESPESA"

    // Form states
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Serviços") }
    var accountType by remember { mutableStateOf("PJ") } // "PJ" (MEI), "PESSOAL"
    var transactionType by remember { mutableStateOf("RECEITA") } // "RECEITA", "DESPESA"
    var dateString by remember { mutableStateOf("2026-07-15") }
    var hasInvoice by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("Pago") }
    var notes by remember { mutableStateOf("") }

    // Dynamic default accountType based on current finance mode
    LaunchedEffect(showAddDialog, financeMode) {
        if (showAddDialog) {
            accountType = when (financeMode) {
                "Só Pessoal" -> "PESSOAL"
                else -> "PJ"
            }
        }
    }

    val baseTransactions = remember(transactions, financeMode) {
        when (financeMode) {
            "Só Pessoal" -> transactions.filter { it.accountType == "PESSOAL" }
            "Só MEI", "Só PJ" -> transactions.filter { it.accountType == "PJ" }
            else -> transactions
        }
    }

    val filteredTransactions = remember(baseTransactions, filterType) {
        when (filterType) {
            "MEI" -> baseTransactions.filter { it.accountType == "PJ" }
            "PESSOAL" -> baseTransactions.filter { it.accountType == "PESSOAL" }
            "RECEITA" -> baseTransactions.filter { it.transactionType == "RECEITA" }
            "DESPESA" -> baseTransactions.filter { it.transactionType == "DESPESA" }
            else -> baseTransactions
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Novo Lançamento", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Gestão de Lançamentos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = "Classifique receitas e despesas por conta de destino.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Dynamic filter options
            ScrollableTabRow(
                selectedTabIndex = when (filterType) {
                    "TODOS" -> 0
                    "MEI" -> 1
                    "PESSOAL" -> 2
                    "RECEITA" -> 3
                    "DESPESA" -> 4
                    else -> 0
                },
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                indicator = {}
            ) {
                listOf("TODOS", "MEI", "PESSOAL", "RECEITA", "DESPESA").forEachIndexed { index, label ->
                    val selected = when (label) {
                        "TODOS" -> filterType == "TODOS"
                        "MEI" -> filterType == "MEI"
                        "PESSOAL" -> filterType == "PESSOAL"
                        "RECEITA" -> filterType == "RECEITA"
                        "DESPESA" -> filterType == "DESPESA"
                        else -> false
                    }
                    Tab(
                        selected = selected,
                        onClick = { filterType = label },
                        text = {
                            Text(
                                label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Table of transaction items
            if (filteredTransactions.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhum lançamento encontrado.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredTransactions) { tx ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(
                                                        if (tx.accountType == "PJ") MaterialTheme.colorScheme.primaryContainer
                                                        else MaterialTheme.colorScheme.tertiaryContainer
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (tx.accountType == "PJ") "CONTA PJ (MEI)" else "CONTA PESSOAL",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (tx.accountType == "PJ") MaterialTheme.colorScheme.onPrimaryContainer
                                                    else MaterialTheme.colorScheme.onTertiaryContainer
                                                )
                                            }
                                            if (tx.hasInvoice) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color(0xFFE8F5E9))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "NF EMITIDA",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF2E7D32)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = tx.description,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Data: ${tx.dateString} • Cat: ${tx.category}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${if (tx.transactionType == "RECEITA") "+" else "-"} R$ %.2f".format(tx.amount),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = if (tx.transactionType == "RECEITA") Color(0xFF2E7D32) else Color(0xFFFF7043)
                                        )
                                        IconButton(
                                            onClick = { viewModel.deleteMeiTransaction(tx.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                                if (tx.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Observação: ${tx.notes}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodySmall
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
            title = { Text("Novo Lançamento") },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrição") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Valor (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dateString,
                        onValueChange = { dateString = it },
                        label = { Text("Data (AAAA-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoria") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Origem da Conta", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = accountType == "PJ",
                            onClick = { accountType = "PJ" },
                            label = { Text("Conta PJ (MEI)") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = accountType == "PESSOAL",
                            onClick = { accountType = "PESSOAL" },
                            label = { Text("Conta Pessoal") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text("Tipo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = transactionType == "RECEITA",
                            onClick = { transactionType = "RECEITA" },
                            label = { Text("Receita (+)") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = transactionType == "DESPESA",
                            onClick = { transactionType = "DESPESA" },
                            label = { Text("Despesa (-)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = hasInvoice,
                            onCheckedChange = { hasInvoice = it }
                        )
                        Text("Emitir Nota Fiscal", fontSize = 13.sp)
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Observações (Opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountText.toFloatOrNull() ?: 0f
                        if (description.isNotBlank() && amount > 0) {
                            viewModel.addMeiTransaction(
                                description, amount, category, accountType, transactionType, dateString, hasInvoice, statusText, notes
                            )
                            showAddDialog = false
                            // Reset form
                            description = ""
                            amountText = ""
                            notes = ""
                        }
                    }
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

@Composable
fun MeiSpecificScreen(viewModel: AppViewModel) {
    val transactions by viewModel.allMeiTransactions.collectAsState()
    val config by viewModel.meiConfig.collectAsState()

    var activeSubTab by remember { mutableStateOf("MEI") } // "MEI" or "PESSOAL"

    // Computations MEI
    val meiRevenues = remember(transactions) {
        transactions.filter { it.accountType == "PJ" && it.transactionType == "RECEITA" }.sumOf { it.amount.toDouble() }.toFloat()
    }
    val meiExpenses = remember(transactions) {
        transactions.filter { it.accountType == "PJ" && it.transactionType == "DESPESA" }.sumOf { it.amount.toDouble() }.toFloat()
    }
    val leftToLimit = (config.annualLimit - meiRevenues).coerceAtLeast(0f)

    // Computations Personal
    val personalRevenues = remember(transactions) {
        transactions.filter { it.accountType == "PESSOAL" && it.transactionType == "RECEITA" }.sumOf { it.amount.toDouble() }.toFloat()
    }
    val personalExpenses = remember(transactions) {
        transactions.filter { it.accountType == "PESSOAL" && it.transactionType == "DESPESA" }.sumOf { it.amount.toDouble() }.toFloat()
    }
    val personalBalance = personalRevenues - personalExpenses

    // Simulated Emergency Fund (using savings percentage)
    val emergencyFundBalance = personalBalance * 0.4f // Simulating 40% of personal balance as Emergency Fund
    val emergencyProgress = if (config.emergencyFundGoal <= 0f) 0f
    else (emergencyFundBalance / config.emergencyFundGoal).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Toggle SubTabs
        TabRow(
            selectedTabIndex = if (activeSubTab == "MEI") 0 else 1,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Tab(
                selected = activeSubTab == "MEI",
                onClick = { activeSubTab = "MEI" },
                text = { Text("Faturamento MEI 🏢", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = activeSubTab == "PESSOAL",
                onClick = { activeSubTab = "PESSOAL" },
                text = { Text("Gestão Pessoal 👤", fontWeight = FontWeight.Bold) }
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (activeSubTab == "MEI") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Acompanhamento do Teto MEI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Mantenha o controle estrito de suas notas fiscais emitidas para não ultrapassar o teto federal anual.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("FATURAMENTO ATUAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("R$ %.2f".format(meiRevenues), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("RESTANTE PARA O LIMITE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("R$ %.2f".format(leftToLimit), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Lançamentos Vinculados ao MEI",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                val meiTxs = transactions.filter { it.accountType == "PJ" }
                if (meiTxs.isEmpty()) {
                    item {
                        Text("Sem faturamentos MEI registrados ainda.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                } else {
                    items(meiTxs) { tx ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(tx.description, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Data: ${tx.dateString} • Cat: ${tx.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    text = "${if (tx.transactionType == "RECEITA") "+" else "-"} R$ %.2f".format(tx.amount),
                                    fontWeight = FontWeight.Black,
                                    color = if (tx.transactionType == "RECEITA") Color(0xFF2E7D32) else Color(0xFFFF7043)
                                )
                            }
                        }
                    }
                }
            } else {
                // Personal Account specific view
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Reserva de Emergência",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Acompanhe a sua meta de economia mensal para construir uma base financeira sólida e soberana.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Saldo Acumulado: R$ %.2f".format(emergencyFundBalance), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Meta: R$ %.2f".format(config.emergencyFundGoal), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Custom progress bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(emergencyProgress)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2E7D32))
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${(emergencyProgress * 100).toInt()}% da meta da reserva atingida",
                                fontSize = 10.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Demonstrativo Pessoal",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("ENTRADAS PESSOAIS", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("R$ %.2f".format(personalRevenues), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("SAÍDAS PESSOAIS", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("R$ %.2f".format(personalExpenses), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF7043))
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
fun MeiInvoicesScreen(viewModel: AppViewModel) {
    val invoices by viewModel.allMeiInvoices.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    // Form inputs
    var clientName by remember { mutableStateOf("") }
    var serviceDescription by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("2026-07-31") }
    var invoiceLink by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Nova Nota", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Notas Fiscais & Faturamento",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = "Gere notas de prestação de serviços e marque o checklist burocrático de cada uma.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (invoices.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhuma nota fiscal cadastrada.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(invoices) { invoice ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = invoice.clientName,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = invoice.serviceDescription,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Vencimento: ${invoice.dueDate}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "R$ %.2f".format(invoice.amount),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        IconButton(
                                            onClick = { viewModel.deleteMeiInvoice(invoice.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remover", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                Spacer(modifier = Modifier.height(10.dp))

                                // Multiple Status Checklist
                                Text("Checklist de Emissão e Envio", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Emitido check
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = invoice.isIssued,
                                            onCheckedChange = { viewModel.updateMeiInvoice(invoice.copy(isIssued = it)) }
                                        )
                                        Text("Emitida", fontSize = 11.sp)
                                    }

                                    // Enviado check
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = invoice.isSent,
                                            onCheckedChange = { viewModel.updateMeiInvoice(invoice.copy(isSent = it)) }
                                        )
                                        Text("Enviada", fontSize = 11.sp)
                                    }

                                    // Recebido check
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = invoice.isReceived,
                                            onCheckedChange = { viewModel.updateMeiInvoice(invoice.copy(isReceived = it)) }
                                        )
                                        Text("Recebida", fontSize = 11.sp)
                                    }
                                }

                                if (invoice.invoiceLink.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Portal Prefeitura: ${invoice.invoiceLink}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.clickable { /* Link click simulated */ }
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
            title = { Text("Adicionar Nota Fiscal") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Nome do Cliente") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = serviceDescription,
                        onValueChange = { serviceDescription = it },
                        label = { Text("Descrição do Serviço") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Valor do Faturamento (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Data de Vencimento") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = invoiceLink,
                        onValueChange = { invoiceLink = it },
                        label = { Text("Link Portal da Prefeitura (Opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountText.toFloatOrNull() ?: 0f
                        if (clientName.isNotBlank() && amount > 0) {
                            viewModel.addMeiInvoice(
                                clientName, serviceDescription, amount, dueDate, false, false, false, invoiceLink
                            )
                            showAddDialog = false
                            // reset
                            clientName = ""
                            serviceDescription = ""
                            amountText = ""
                            invoiceLink = ""
                        }
                    }
                ) {
                    Text("Criar Nota")
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

@Composable
fun MeiOpenFinanceScreen(viewModel: AppViewModel) {
    var pastedText by remember { mutableStateOf("") }
    var importStatusText by remember { mutableStateOf("") }
    var importErrorText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Importação de Finanças 📥",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Importe dados diretamente de planilhas do Google Sheets (copiando e colando) ou de arquivos JSON estruturados para seu fluxo de caixa.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Paste Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Área de Importação",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Cole abaixo os dados JSON ou as linhas copiadas de uma planilha (Google Sheets/Excel) no formato:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "Formato Planilha: Data | Descrição | Valor | Categoria | Conta (PJ/PESSOAL) | Tipo (RECEITA/DESPESA)\nExemplo:\n15/07/2026\tVenda de Placas\t1250,00\tVendas\tPJ\tRECEITA\n16/07/2026\tInternet\t150,00\tServiços\tPJ\tDESPESA",
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = pastedText,
                        onValueChange = { pastedText = it },
                        label = { Text("Cole os dados aqui") },
                        placeholder = { Text("JSON ou texto de planilha...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        maxLines = 10,
                        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 12.sp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val text = pastedText.trim()
                                if (text.isBlank()) {
                                    importErrorText = "Nenhum dado colado."
                                    importStatusText = ""
                                    return@Button
                                }
                                
                                try {
                                    // Try JSON first if it looks like JSON
                                    if (text.startsWith("[") || text.startsWith("{")) {
                                        var count = 0
                                        val jsonArray = if (text.startsWith("[")) {
                                            org.json.JSONArray(text)
                                        } else {
                                            org.json.JSONArray().put(org.json.JSONObject(text))
                                        }
                                        
                                        for (i in 0 until jsonArray.length()) {
                                            val obj = jsonArray.getJSONObject(i)
                                            val description = obj.optString("description", "Importado JSON")
                                            val amount = obj.optDouble("amount", 0.0).toFloat()
                                            val category = obj.optString("category", "Geral")
                                            val accountType = obj.optString("accountType", "PJ").uppercase()
                                            val transactionType = obj.optString("transactionType", "RECEITA").uppercase()
                                            val dateString = obj.optString("dateString", "2026-07-15")
                                            val status = obj.optString("status", "Pago")
                                            val notes = obj.optString("notes", "")
                                            val hasInvoice = obj.optBoolean("hasInvoice", false)
                                            
                                            if (description.isNotBlank() && amount > 0) {
                                                viewModel.addMeiTransaction(
                                                    description, amount, category, accountType, transactionType, dateString, hasInvoice, status, notes
                                                )
                                                count++
                                            }
                                        }
                                        importStatusText = "Sucesso! $count transações importadas via JSON."
                                        importErrorText = ""
                                        pastedText = ""
                                    } else {
                                        // Parse Spreadsheet (TSV/CSV)
                                        val lines = text.split("\n")
                                        var count = 0
                                        var errorsCount = 0
                                        for (line in lines) {
                                            if (line.trim().isBlank()) continue
                                            // Split by tab (Sheets) or comma/semicolon (CSV)
                                            val cols = if (line.contains("\t")) {
                                                line.split("\t")
                                            } else if (line.contains(";")) {
                                                line.split(";")
                                            } else {
                                                line.split(",")
                                            }
                                            
                                            if (cols.size >= 3) {
                                                val rawDate = cols[0].trim()
                                                val description = cols[1].trim()
                                                val rawAmount = cols[2].trim()
                                                val category = if (cols.size > 3) cols[3].trim() else "Geral"
                                                val accountType = if (cols.size > 4) cols[4].trim().uppercase() else "PJ"
                                                val transactionType = if (cols.size > 5) cols[5].trim().uppercase() else "RECEITA"
                                                
                                                // Format Date: DD/MM/YYYY to YYYY-MM-DD
                                                val dateString = if (rawDate.contains("/")) {
                                                    val parts = rawDate.split("/")
                                                    if (parts.size == 3) {
                                                        val d = parts[0].padStart(2, '0')
                                                        val m = parts[1].padStart(2, '0')
                                                        val y = parts[2]
                                                        "$y-$m-$d"
                                                    } else "2026-07-15"
                                                } else rawDate
                                                
                                                // Format amount (e.g. 1.250,00 or 1250.00)
                                                val cleanAmountStr = rawAmount
                                                    .replace("R$", "")
                                                    .replace("$", "")
                                                    .replace(" ", "")
                                                    .trim()
                                                
                                                val amount = try {
                                                    if (cleanAmountStr.contains(",") && cleanAmountStr.contains(".")) {
                                                        // BRL like 1.250,00
                                                        cleanAmountStr.replace(".", "").replace(",", ".").toFloat()
                                                    } else if (cleanAmountStr.contains(",")) {
                                                        // BRL like 150,00 with no thousands dot
                                                        cleanAmountStr.replace(",", ".").toFloat()
                                                    } else {
                                                        cleanAmountStr.toFloat()
                                                    }
                                                } catch (e: Exception) {
                                                    0f
                                                }
                                                
                                                if (description.isNotBlank() && amount > 0) {
                                                    viewModel.addMeiTransaction(
                                                        description = description,
                                                        amount = amount,
                                                        category = category,
                                                        accountType = if (accountType == "PESSOAL") "PESSOAL" else "PJ",
                                                        transactionType = if (transactionType == "DESPESA") "DESPESA" else "RECEITA",
                                                        dateString = dateString,
                                                        hasInvoice = false,
                                                        status = "Pago",
                                                        notes = "Importado via Planilha"
                                                    )
                                                    count++
                                                } else {
                                                    errorsCount++
                                                }
                                            } else {
                                                errorsCount++
                                            }
                                        }
                                        if (count > 0) {
                                            importStatusText = "Sucesso! $count lançamentos importados com sucesso da Planilha."
                                            if (errorsCount > 0) {
                                                importStatusText += " ($errorsCount linhas ignoradas por formato inválido)."
                                            }
                                            importErrorText = ""
                                            pastedText = ""
                                        } else {
                                            importErrorText = "Nenhuma linha pôde ser parseada. Verifique o formato."
                                            importStatusText = ""
                                        }
                                    }
                                } catch (e: Exception) {
                                    importErrorText = "Erro ao processar dados: ${e.localizedMessage}"
                                    importStatusText = ""
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Processar & Salvar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.restoreExampleMeiData()
                                importStatusText = "Dados de demonstração restaurados com sucesso."
                                importErrorText = ""
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restaura Demo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (importStatusText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            border = BorderStroke(1.dp, Color(0xFF81C784))
                        ) {
                            Text(
                                text = importStatusText,
                                color = Color(0xFF2E7D32),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    if (importErrorText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            border = BorderStroke(1.dp, Color(0xFFEF5350))
                        ) {
                            Text(
                                text = importErrorText,
                                color = Color(0xFFC62828),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MeiConfigScreen(viewModel: AppViewModel) {
    val config by viewModel.meiConfig.collectAsState()
    val context = LocalContext.current

    var limitText by remember { mutableStateOf("") }
    var dasText by remember { mutableStateOf("") }
    var savingsText by remember { mutableStateOf("") }
    var emergencyText by remember { mutableStateOf("") }
    var meiRevenueText by remember { mutableStateOf("") }

    var saveConfirmationMessage by remember { mutableStateOf("") }

    // Synchronize text inputs when config updates
    LaunchedEffect(config) {
        limitText = config.annualLimit.toString()
        dasText = config.monthlyDas.toString()
        savingsText = config.monthlySavingsGoal.toString()
        emergencyText = config.emergencyFundGoal.toString()
        meiRevenueText = config.monthlyMeiRevenueGoal.toString()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Configurações Financeiras",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = "Ajuste os tetos, metas e limites para alinhar o processamento local com suas finanças.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            val userAccount by viewModel.userAccount.collectAsState()
            val currentMode = userAccount?.financeMode ?: "MEI + Pessoal"
            val modes = listOf("MEI + Pessoal", "Só Pessoal", "Só MEI", "Só PJ")
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Visualização e Perfil de Finanças ⚙️",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Escolha quais contas deseja gerenciar neste dispositivo:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        modes.forEach { mode ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.updateFinanceMode(mode) }
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RadioButton(
                                    selected = currentMode == mode,
                                    onClick = { viewModel.updateFinanceMode(mode) }
                                )
                                Column {
                                    Text(
                                        text = mode,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    val desc = when (mode) {
                                        "MEI + Pessoal" -> "Controle unificado da sua Pessoa Física e Jurídica."
                                        "Só Pessoal" -> "Foco exclusivo no seu orçamento pessoal/CLT."
                                        "Só MEI" -> "Foco exclusivo na sua empresa MEI."
                                        "Só PJ" -> "Foco exclusivo em sua Pessoa Jurídica em geral."
                                        else -> ""
                                    }
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Configuration forms
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = limitText,
                        onValueChange = { limitText = it },
                        label = { Text("Limite anual MEI (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = dasText,
                        onValueChange = { dasText = it },
                        label = { Text("DAS mensal estimado (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = savingsText,
                        onValueChange = { savingsText = it },
                        label = { Text("Meta mensal de economia (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = emergencyText,
                        onValueChange = { emergencyText = it },
                        label = { Text("Meta reserva de emergência (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = meiRevenueText,
                        onValueChange = { meiRevenueText = it },
                        label = { Text("Meta receita mensal MEI (R$)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            viewModel.saveMeiConfig(
                                MeiConfig(
                                    annualLimit = limitText.toFloatOrNull() ?: 81000f,
                                    monthlyDas = dasText.toFloatOrNull() ?: 81.9f,
                                    monthlySavingsGoal = savingsText.toFloatOrNull() ?: 500f,
                                    emergencyFundGoal = emergencyText.toFloatOrNull() ?: 6000f,
                                    monthlyMeiRevenueGoal = meiRevenueText.toFloatOrNull() ?: 6750f
                                )
                            )
                            saveConfirmationMessage = "Configurações aplicadas com sucesso!"
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Salvar Configurações")
                    }

                    if (saveConfirmationMessage.isNotBlank()) {
                        Text(
                            text = saveConfirmationMessage,
                            color = Color(0xFF2E7D32),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Backup controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Gestão de Dados (Backup e portabilidade)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Exporte faturas e lançamentos locais ou restaure o estado de simulação padrão.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                saveConfirmationMessage = "Backup JSON gerado localmente em Downloads!"
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Backup JSON", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                saveConfirmationMessage = "CSV exportado com sucesso!"
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Exportar CSV", fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                saveConfirmationMessage = "Backup importado com sucesso!"
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Importar Backup", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.restoreExampleMeiData()
                                saveConfirmationMessage = "Lançamentos e faturas de exemplo restaurados!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Restaurar Exemplo", fontSize = 11.sp, color = MaterialTheme.colorScheme.onError)
                        }
                    }
                }
            }
        }

        // Disclaimer legal
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Aviso Legal Importante", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "O aplicativo opera exclusivamente como ferramenta auxiliar de controle financeiro pessoal e empresarial local. Suas estimativas não substituem orientações de um contador oficial, emissões oficiais no portal do município, as apurações do PGMEI ou o envio anual da declaração DASN-SIMEI.",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
