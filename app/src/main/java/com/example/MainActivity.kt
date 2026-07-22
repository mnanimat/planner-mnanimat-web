package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.ui.AppViewModel
import com.example.ui.AppViewModelFactory
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize local SQLite Room data layer
        val database = AppDatabase.getDatabase(this)
        val repository = AppRepository(database)
        
        setContent {
            val database = remember { AppDatabase.getDatabase(applicationContext) }
            val repository = remember { AppRepository(database) }
            val appViewModel: AppViewModel = viewModel(
                factory = AppViewModelFactory(application, repository)
            )
            val userAccountState by appViewModel.userAccount.collectAsState()
            val isDarkTheme = userAccountState?.isDarkTheme ?: true

            MyApplicationTheme(darkTheme = isDarkTheme) {
                if (userAccountState == null || !userAccountState!!.termsAccepted) {
                    OnboardingScreen(viewModel = appViewModel)
                } else {
                    MainAppShell(viewModel = appViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(viewModel: AppViewModel) {
    val userAccountState by viewModel.userAccount.collectAsState()
    var showProfileDialog by remember { mutableStateOf(false) }

    // Current Active Module: "FOCOVEST", "RITVIDA", or "MEI_PRO"
    var activeModule by remember { mutableStateOf("FOCOVEST") }
    
    // Sub-tab indexing for FocoVest
    var selectedFocoVestTab by remember { mutableIntStateOf(0) }
    
    // Sub-tab indexing for RitVida
    var selectedRitVidaTab by remember { mutableIntStateOf(0) }

    // Sub-tab indexing for MEI Financeiro Pro
    var selectedMeiTab by remember { mutableIntStateOf(0) }

    val FocoVestTabs = listOf(
        FocoVestTabItem("Dashboard", Icons.Default.Dashboard, Icons.Outlined.Dashboard),
        FocoVestTabItem("Cronograma", Icons.Default.CalendarMonth, Icons.Outlined.CalendarMonth),
        FocoVestTabItem("Trilhas", Icons.Default.Checklist, Icons.Outlined.Checklist),
        FocoVestTabItem("Revisão", Icons.Default.Style, Icons.Outlined.Style),
        FocoVestTabItem("Simulados", Icons.Default.Assignment, Icons.Outlined.Assignment),
        FocoVestTabItem("Videoaulas", Icons.Default.PlayCircle, Icons.Outlined.PlayCircle),
        FocoVestTabItem("Tutor IA", Icons.Default.AutoAwesome, Icons.Outlined.AutoAwesome),
        FocoVestTabItem("Redação", Icons.Default.RateReview, Icons.Outlined.RateReview),
        FocoVestTabItem("Ferramentas", Icons.Default.Build, Icons.Outlined.Build)
    )

    val RitVidaTabs = listOf(
        RitVidaTabItem("Visão Geral", Icons.Default.Home, Icons.Outlined.Home),
        RitVidaTabItem("Estudos", Icons.Default.School, Icons.Outlined.School),
        RitVidaTabItem("Visual", Icons.Default.CalendarViewMonth, Icons.Outlined.CalendarViewMonth),
        RitVidaTabItem("Saúde & Treinos", Icons.Default.FitnessCenter, Icons.Outlined.FitnessCenter),
        RitVidaTabItem("Finanças", Icons.Default.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
        RitVidaTabItem("Projetos", Icons.Default.WorkOutline, Icons.Outlined.WorkOutline),
        RitVidaTabItem("Portfólio", Icons.Default.Palette, Icons.Outlined.Palette)
    )

    val MeiTabs = listOf(
        MeiTabItem("Dashboard", Icons.Default.Dashboard, Icons.Outlined.Dashboard),
        MeiTabItem("Lançamentos", Icons.Default.Receipt, Icons.Outlined.Receipt),
        MeiTabItem("MEI & Pessoal", Icons.Default.AccountBalance, Icons.Outlined.AccountBalance),
        MeiTabItem("Notas Fiscais", Icons.Default.FactCheck, Icons.Outlined.FactCheck),
        MeiTabItem("Importação", Icons.Default.SyncAlt, Icons.Outlined.SyncAlt),
        MeiTabItem("Configurações", Icons.Default.Settings, Icons.Outlined.Settings)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Top header bar with branding and system settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ECOSSISTEMA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.5.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = when (activeModule) {
                                    "FOCOVEST" -> "Estudo"
                                    "RITVIDA" -> "Painel"
                                    else -> "Dinheiro"
                                },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "MNAnimat",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                    }

                    // Top Right Profile Avatar representation (from Design HTML spec)
                    val initials = remember(userAccountState) {
                        val name = userAccountState?.name
                        if (!name.isNullOrBlank()) {
                            val parts = name.trim().split("\\s+".toRegex())
                            if (parts.size >= 2) {
                                (parts[0].take(1) + parts[1].take(1)).uppercase()
                            } else {
                                parts[0].take(2).uppercase()
                            }
                        } else {
                            "MN"
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.tertiary)
                            .border(2.dp, Color.White, RoundedCornerShape(20.dp))
                            .clickable { showProfileDialog = true }
                            .testTag("avatar_profile_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))

                // High fidelity segmented tab switch to toggle platforms (Estudo, Rotina, Dinheiro)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(19.dp))
                            .background(
                                if (activeModule == "FOCOVEST") MaterialTheme.colorScheme.primary
                                else Color.Transparent
                            )
                            .clickable { activeModule = "FOCOVEST" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Estudo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (activeModule == "FOCOVEST") MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(19.dp))
                            .background(
                                if (activeModule == "RITVIDA") MaterialTheme.colorScheme.primary
                                else Color.Transparent
                            )
                            .clickable { activeModule = "RITVIDA" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Painel",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (activeModule == "RITVIDA") MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(19.dp))
                            .background(
                                if (activeModule == "MEI_PRO") MaterialTheme.colorScheme.primary
                                else Color.Transparent
                            )
                            .clickable { activeModule = "MEI_PRO" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Dinheiro",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (activeModule == "MEI_PRO") MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal scrollable sub-tabs for FocoVest, RitVida, or MEI Pro
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    when (activeModule) {
                        "FOCOVEST" -> {
                            itemsIndexed(FocoVestTabs) { index, tab ->
                                val selected = selectedFocoVestTab == index
                                InputChip(
                                    selected = selected,
                                    onClick = { selectedFocoVestTab = index },
                                    label = { Text(tab.title) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                        "RITVIDA" -> {
                            itemsIndexed(RitVidaTabs) { index, tab ->
                                val selected = selectedRitVidaTab == index
                                InputChip(
                                    selected = selected,
                                    onClick = { selectedRitVidaTab = index },
                                    label = { Text(tab.title) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                        "MEI_PRO" -> {
                            itemsIndexed(MeiTabs) { index, tab ->
                                val selected = selectedMeiTab == index
                                InputChip(
                                    selected = selected,
                                    onClick = { selectedMeiTab = index },
                                    label = { Text(tab.title) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        // Main Screen content area with beautiful cross-fade animation transitions
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = activeModule to (
                    if (activeModule == "FOCOVEST") selectedFocoVestTab
                    else if (activeModule == "RITVIDA") selectedRitVidaTab
                    else selectedMeiTab
                ),
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "MainContentTransition"
            ) { (module, subTab) ->
                when (module) {
                    "FOCOVEST" -> {
                        when (subTab) {
                            0 -> FocoVestDashboardScreen(viewModel = viewModel, onNavigateToTab = { tabName ->
                                val idx = FocoVestTabs.indexOfFirst { it.title.lowercase() == tabName.lowercase() }
                                if (idx != -1) selectedFocoVestTab = idx
                            })
                            1 -> FocoVestCronogramaScreen(viewModel = viewModel)
                            2 -> FocoVestTrilhasScreen(viewModel = viewModel)
                            3 -> FocoVestAnkiScreen(viewModel = viewModel)
                            4 -> FocoVestSimuladosScreen(viewModel = viewModel)
                            5 -> FocoVestVideosScreen(viewModel = viewModel)
                            6 -> FocoVestTutorScreen(viewModel = viewModel)
                            7 -> FocoVestRedacaoScreen(viewModel = viewModel)
                            8 -> FocoVestFerramentasScreen(viewModel = viewModel)
                        }
                    }
                    "RITVIDA" -> {
                        when (subTab) {
                            0 -> RitVidaOverviewScreen(viewModel = viewModel, onNavigateToFocoVest = {
                                activeModule = "FOCOVEST"
                                selectedFocoVestTab = 0
                            })
                            1 -> RitVidaStudiesScreen(viewModel = viewModel)
                            2 -> RitVidaVisualScreen(viewModel = viewModel)
                            3 -> RitVidaGymDietScreen(viewModel = viewModel)
                            4 -> RitVidaFinanceScreen(viewModel = viewModel)
                            5 -> RitVidaProjectsScreen(viewModel = viewModel)
                            6 -> RitVidaPortfolioScreen(viewModel = viewModel)
                        }
                    }
                    "MEI_PRO" -> {
                        when (subTab) {
                            0 -> MeiDashboardScreen(viewModel = viewModel, onNavigateToTab = { tabIdx ->
                                selectedMeiTab = tabIdx
                            })
                            1 -> MeiTransactionsScreen(viewModel = viewModel)
                            2 -> MeiSpecificScreen(viewModel = viewModel)
                            3 -> MeiInvoicesScreen(viewModel = viewModel)
                            4 -> MeiOpenFinanceScreen(viewModel = viewModel)
                            5 -> MeiConfigScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }

    if (showProfileDialog) {
        val account = userAccountState ?: com.example.data.UserAccount()
        var tempName by remember { mutableStateOf(account.name) }
        var tempEmail by remember { mutableStateOf(account.email) }
        
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Perfil & Configurações",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Dados salvos com segurança na memória local do celular.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Nome Completo") },
                        modifier = Modifier.fillMaxWidth().testTag("profile_name_input"),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = tempEmail,
                        onValueChange = { tempEmail = it },
                        label = { Text("E-mail") },
                        modifier = Modifier.fillMaxWidth().testTag("profile_email_input"),
                        singleLine = true
                    )
                    
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Tema Visual",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (account.isDarkTheme) "Modo Escuro Ativo" else "Modo Claro Ativo",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = account.isDarkTheme,
                            onCheckedChange = { viewModel.toggleTheme() },
                            modifier = Modifier.testTag("theme_toggle_switch")
                        )
                    }
                    
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    
                    var showLegalDialog by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Termos Aceitos em:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val sdf = remember { java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()) }
                            val acceptedDateStr = if (account.termsAcceptedTimestamp > 0) {
                                sdf.format(java.util.Date(account.termsAcceptedTimestamp))
                            } else {
                                "Não disponível"
                            }
                            Text(
                                text = acceptedDateStr,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        OutlinedButton(
                            onClick = { showLegalDialog = true },
                            modifier = Modifier.testTag("show_legal_terms_button")
                        ) {
                            Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Termos & Licença", fontSize = 11.sp)
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Button(
                        onClick = {
                            viewModel.logoutUserAccount()
                            showProfileDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("logout_reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sair da conta",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (showLegalDialog) {
                        AlertDialog(
                            onDismissRequest = { showLegalDialog = false },
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text("Termos de Uso e Licença MIT", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            },
                            text = {
                                Box(
                                    modifier = Modifier
                                        .heightIn(max = 350.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Text(
                                            text = "TERMOS DE USO, PRIVACIDADE E LICENÇA",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "O Planner MNAnimat é um ecossistema projetado para gerenciar de forma unificada as esferas de Estudos, Rotinas e Dinheiro de forma estritamente autônoma e offline. Todos os seus dados são gravados localmente na sandbox segura do sistema operacional Android do seu dispositivo.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Divider()
                                        Text(
                                            text = "CONFORMIDADE LGPD (BRASIL) & GDPR (UE):",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = "• AGENTE DE TRATAMENTO: O próprio usuário atua como único Controlador e Operador de seus dados pessoais. O desenvolvedor não coleta, vende ou processa seus dados pessoais.\n" +
                                                    "• DIREITOS DO TITULAR: Você possui controle total e imediato para remover todos os dados bastando apagar o cache ou desinstalar o aplicativo.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Divider()
                                        Text(
                                            text = "LICENÇA MIT (MIT LICENSE):",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = "Copyright (c) 2026 MNAnimat\n\n" +
                                                    "É concedida permissão, gratuitamente, a qualquer pessoa que obtenha uma cópia deste software, para usar, copiar, modificar, mesclar, publicar, distribuir, sublicenciar e/ou vender cópias do software, sem restrições, sujeito à condição de que o aviso de copyright acima e este aviso de permissão sejam incluídos em todas as cópias ou partes substanciais do Software.\n\n" +
                                                    "O SOFTWARE É FORNECIDO 'COMO ESTÁ', SEM GARANTIA DE QUALQUER TIPO, EXPRESSA OU IMPLÍCITA.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Divider()
                                        Text(
                                            text = "USO COMERCIAL:",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = "Autorizado plenamente para faturamento, gestão de projetos e controle profissional em conformidade com as diretrizes da Play Store.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Divider()
                                        Text(
                                            text = "AVISO MÉDICO & SAÚDE:",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = "NÃO É RECOMENDAÇÃO MÉDICA: O módulo de registro de treinos de academia e registro de alimentação saudável é estritamente uma ferramenta informativa de registro pessoal. O aplicativo não fornece conselhos médicos, diagnósticos, prescrições de dieta ou treinamentos físicos. Consulte sempre profissionais de saúde credenciados antes de iniciar qualquer rotina física ou alimentar.\n\nDESENVOLVIMENTO & SUGESTÕES: O Aplicativo foi desenvolvido com o uso de inteligência artificial e Google AI Studio com Micael Nildo Oliveira Souza dando as orientações para guiar o desenvolvimento. Para enviar sugestões de melhorias, entre em contato pelo e-mail: mnanimat@gmail.com",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            confirmButton = {
                                Button(onClick = { showLegalDialog = false }) {
                                    Text("Entendido")
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.isNotBlank() && tempEmail.isNotBlank()) {
                            viewModel.updateUserAccount(tempName, tempEmail)
                            showProfileDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_profile_button")
                ) {
                    Text("Salvar Alterações")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Fechar")
                }
            }
        )
    }
}

data class FocoVestTabItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

data class RitVidaTabItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

data class MeiTabItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
