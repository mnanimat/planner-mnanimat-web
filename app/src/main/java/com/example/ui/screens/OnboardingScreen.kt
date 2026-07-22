package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: AppViewModel) {
    val existingAccount by viewModel.userAccount.collectAsState()
    var step by remember { mutableIntStateOf(1) } // 1: Terms, 2: Registration
    
    var acceptTerms by remember { mutableStateOf(false) }
    var acceptPrivacy by remember { mutableStateOf(false) }
    var acceptStorage by remember { mutableStateOf(false) }
    
    var name by remember { mutableStateOf("") }
    var useDemoData by remember { mutableStateOf(true) }
    var selectedEnem by remember { mutableStateOf(true) }
    var selectedIta by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (step == 1) {
                // Step 1: Legal Terms Acceptance
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Planner MNAnimat",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text(
                        text = "Termos e Políticas de Lançamento",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Terms box - Scrollable
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "TERMOS DE USO, POLÍTICA DE PRIVACIDADE E LICENÇA MIT",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            Text(
                                text = "Bem-vindo ao Planner MNAnimat! Para garantir conformidade com as diretrizes da Google Play Store, a legislação brasileira (LGPD) e as regulamentações internacionais (GDPR), por favor leia e aceite nossos termos integrados.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Divider(color = MaterialTheme.colorScheme.outlineVariant)

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                Text(text = "1. Termos de Uso (Uso Local)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Text(
                                text = "O Planner MNAnimat é um ecossistema projetado para gerenciar de forma unificada as esferas de Estudos, Rotinas e Dinheiro. O aplicativo funciona de forma estritamente autônoma e offline. Todos os seus dados são gravados localmente na sandbox segura do sistema operacional Android do seu dispositivo, utilizando o banco de dados interno SQLite (Room). Você é inteiramente responsável pela guarda e proteção do seu aparelho e das credenciais locais definidas.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                Text(text = "2. Política de Privacidade (LGPD & GDPR)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Text(
                                text = "Em total conformidade com a Lei Geral de Proteção de Dados Pessoais do Brasil (LGPD - Lei nº 13.709/2018) e com o Regulamento Geral sobre a Proteção de Dados da União Europeia (GDPR):\n" +
                                        "• AGENTE DE TRATAMENTO: Por se tratar de um aplicativo de armazenamento exclusivamente local, o próprio USUÁRIO atua como único Controlador e Operador de seus dados pessoais. O desenvolvedor NÃO possui qualquer acesso técnico aos seus dados.\n" +
                                        "• COLETA DE DADOS: Não há coleta, transmissão, processamento centralizado ou venda de informações pessoais. Seus dados de estudos, senhas, fluxo financeiro e registros de rotina nunca saem do seu aparelho.\n" +
                                        "• SEGURANÇA: O aplicativo emprega as permissões mínimas necessárias e isolamento de processos oferecido pelo sandbox do Android.\n" +
                                        "• DIREITOS DO TITULAR: Você possui controle total sobre suas informações, podendo retificar ou excluir todos os dados do aplicativo a qualquer momento bastando limpar os dados do aplicativo nas configurações do Android ou desinstalá-lo (Direito ao Esquecimento / Direito de Eliminação).",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                Text(text = "3. Consentimento de Armazenamento Offline", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Text(
                                text = "Ao aceitar este termo, você reconhece e concorda que, caso desinstale o aplicativo, realize a limpeza manual de dados/cache nas configurações do Android, ou formate o seu celular, todas as informações salvas no banco de dados local serão perdidas permanentemente. O desenvolvedor do aplicativo não possui backups externos e não poderá ser responsabilizado por eventuais perdas de dados decorrentes dessas ações do usuário.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                Text(text = "4. Licença MIT (MIT License)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Text(
                                text = "Este aplicativo é distribuído sob os termos da Licença MIT:\n\n" +
                                        "Copyright (c) 2026 MNAnimat\n\n" +
                                        "É concedida permissão, gratuitamente, a qualquer pessoa que obtenha uma cópia deste software e arquivos de documentação associados, para lidar com o software sem restrições, incluindo, sem limitação, os direitos de usar, copiar, modificar, mesclar, publicar, distribuir, sublicenciar e/ou vender cópias do software, sujeito às seguintes condições:\n\n" +
                                        "O aviso de copyright acima e este aviso de permissão devem ser incluídos em todas as cópias ou partes substanciais do Software.\n\n" +
                                        "O SOFTWARE É FORNECIDO 'COMO ESTÁ', SEM GARANTIA DE QUALQUER TIPO, EXPRESSA OU IMPLÍCITA, INCLUINDO, MAS NÃO SE LIMITANDO ÀS GARANTIAS DE COMERCIALIZAÇÃO, ADEQUAÇÃO A UM DETERMINADO FIM E NÃO INFRAÇÃO. EM NENHUM CASO OS AUTORES OU DETENTORES DE DIREITOS AUTORAIS SERÃO RESPONSÁVEIS POR QUALQUER REIVINDICAÇÃO, DANOS OU OUTRA RESPONSABILIDADE, SEJA IN COORDENAÇÃO DE CONTRATO, ILÍCITO OU DE OUTRA FORMA, DECORRENTE DE, FORA DE OU EM CONEXÃO COM O SOFTWARE OU O USO OU OUTRAS NEGOCIAÇÕES NO SOFTWARE.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                Text(text = "5. Uso Comercial Autorizado", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Text(
                                text = "Em conformidade com a licença de código aberto adotada, este aplicativo está plenamente autorizado para uso comercial por parte de seus usuários e desenvolvedores, garantindo segurança jurídica nas esferas de faturamento (módulo financeiro), gestão de projetos e controle profissional.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                Text(text = "6. Isenção de Responsabilidade de Saúde (Aviso Médico)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Text(
                                text = "NÃO É RECOMENDAÇÃO MÉDICA: O módulo de registro de treinos de academia e registro de alimentação saudável contido neste aplicativo é estritamente uma ferramenta para fins informativos e de registro pessoal do usuário. O aplicativo não fornece conselhos médicos, diagnósticos, prescrições de dieta ou treinamentos de educação física. Consulte sempre profissionais de saúde habilitados (médicos, nutricionistas e educadores físicos) antes de iniciar qualquer rotina de exercícios físicos ou plano dietético.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                Text(text = "7. Desenvolvimento & Sugestões de Melhoria", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Text(
                                text = "O Aplicativo foi desenvolvido com o uso de inteligência artificial e Google AI Studio com Micael Nildo Oliveira Souza dando as orientações para guiar o desenvolvimento. Para enviar sugestões de melhorias, entre em contato pelo e-mail: mnanimat@gmail.com",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Checkboxes
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = acceptTerms,
                                onCheckedChange = { acceptTerms = it },
                                modifier = Modifier.testTag("checkbox_terms")
                            )
                            Text(
                                text = "Li e concordo com os Termos de Uso.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = acceptPrivacy,
                                onCheckedChange = { acceptPrivacy = it },
                                modifier = Modifier.testTag("checkbox_privacy")
                            )
                            Text(
                                text = "Aceito a Política de Privacidade de dados offline.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = acceptStorage,
                                onCheckedChange = { acceptStorage = it },
                                modifier = Modifier.testTag("checkbox_storage")
                            )
                            Text(
                                text = "Compreendo que os dados salvos ficam na memória local.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { if (acceptTerms && acceptPrivacy && acceptStorage) step = 2 },
                        enabled = acceptTerms && acceptPrivacy && acceptStorage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("button_continue_onboarding"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Continuar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            } else if (step == 2) {
                // Step 2: Account Creation
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Icon(
                        imageVector = Icons.Default.AppRegistration,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Criar Conta Local",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Text(
                        text = "Crie seu perfil para gerenciar o Planner MNAnimat com segurança local na memória do celular.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Seu Nome Completo") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_registration_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("E-mail") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_registration_email"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Senha de Acesso Local") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_registration_password"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    if (formError.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = formError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { step = 1 },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Voltar")
                        }
                        
                        Button(
                            onClick = {
                                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                                    formError = "Preencha todos os campos para continuar!"
                                } else if (!email.contains("@")) {
                                    formError = "Insira um endereço de e-mail válido!"
                                } else {
                                    formError = ""
                                    val currentAcc = existingAccount
                                    if (currentAcc != null &&
                                        currentAcc.name.trim().equals(name.trim(), ignoreCase = true) &&
                                        currentAcc.email.trim().equals(email.trim(), ignoreCase = true) &&
                                        currentAcc.passwordHash == password
                                    ) {
                                        viewModel.loginWithExistingAccount()
                                    } else {
                                        step = 3
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(2.0f)
                                .height(50.dp)
                                .testTag("button_create_profile"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Avançar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            } else if (step == 3) {
                // Step 3: Startup Template Selection
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Modelo de Inicialização",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Escolha como deseja iniciar a sua base de dados no aplicativo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { useDemoData = true },
                        colors = CardDefaults.cardColors(
                            containerColor = if (useDemoData) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, if (useDemoData) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RadioButton(
                                selected = useDemoData,
                                onClick = { useDemoData = true }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Modelo de Demonstração", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                Text("O aplicativo será preenchido com dados fictícios de exemplo de rotinas, projetos, tarefas, treinos, dieta e finanças para você testar todas as funcionalidades imediatamente.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { useDemoData = false },
                        colors = CardDefaults.cardColors(
                            containerColor = if (!useDemoData) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, if (!useDemoData) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                RadioButton(
                                    selected = !useDemoData,
                                    onClick = { useDemoData = false }
                                )
                                Column {
                                    Text("Modelo Vazio", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    Text("Inicie um banco de dados limpo para inserir suas próprias informações. Selecione quais trilhas de checklists de estudo deseja carregar:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            
                            if (!useDemoData) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedEnem = !selectedEnem },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Checkbox(
                                        checked = selectedEnem,
                                        onCheckedChange = { selectedEnem = it }
                                    )
                                    Text("Carregar Checklist Cronograma ENEM", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedIta = !selectedIta },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Checkbox(
                                        checked = selectedIta,
                                        onCheckedChange = { selectedIta = it }
                                    )
                                    Text("Carregar Checklist Cronograma ITA", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { step = 2 },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Voltar")
                        }
                        
                        Button(
                            onClick = {
                                viewModel.setupOnboardingMode(
                                    useDemo = useDemoData,
                                    selectedEnem = if (!useDemoData) selectedEnem else false,
                                    selectedIta = if (!useDemoData) selectedIta else false,
                                    name = name,
                                    email = email,
                                    passwordHash = password
                                )
                            },
                            modifier = Modifier
                                .weight(2.0f)
                                .height(50.dp)
                                .testTag("button_complete_onboarding"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Concluir", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
