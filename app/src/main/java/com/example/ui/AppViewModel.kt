package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val sender: String, // "USER" or "AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class AppViewModel(application: Application, private val repository: AppRepository) : AndroidViewModel(application) {

    // Initialize Database Prepopulation
    init {
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    // --- FocoVest Study Subjects (Trilhas e Checklist) ---
    val allSubjects: StateFlow<List<StudySubject>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleSubjectStep(subject: StudySubject, step: String) {
        viewModelScope.launch {
            val updated = when (step.lowercase()) {
                "aula" -> subject.copy(stepAula = !subject.stepAula)
                "resumo" -> subject.copy(stepResumo = !subject.stepResumo)
                "autoexplicacao" -> subject.copy(stepAutoexplicacao = !subject.stepAutoexplicacao)
                "exercicios" -> subject.copy(stepExercicios = !subject.stepExercicios)
                "cadernoerros" -> subject.copy(stepCadernoErros = !subject.stepCadernoErros)
                "revisao" -> subject.copy(stepRevisao = !subject.stepRevisao)
                "simulado" -> subject.copy(stepSimulado = !subject.stepSimulado)
                else -> subject
            }
            repository.updateSubject(updated)
        }
    }

    fun addNewSubject(title: String, category: String) {
        if (title.isBlank() || category.isBlank()) return
        viewModelScope.launch {
            repository.insertSubject(StudySubject(title = title, category = category))
        }
    }

    fun deleteSubject(id: Int) {
        viewModelScope.launch {
            repository.deleteSubjectById(id)
        }
    }

    // --- Revisão Anki (Flashcards) ---
    val allFlashcards: StateFlow<List<Flashcard>> = repository.allFlashcards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addNewFlashcard(question: String, answer: String) {
        if (question.isBlank() || answer.isBlank()) return
        viewModelScope.launch {
            repository.insertFlashcard(Flashcard(question = question, answer = answer))
        }
    }

    fun answerFlashcard(card: Flashcard, difficulty: Int) {
        viewModelScope.launch {
            val isCorrect = difficulty >= 3
            val newRepetitions = if (isCorrect) card.repetitions + 1 else 0
            val newInterval = when {
                !isCorrect -> 1
                newRepetitions == 1 -> 1
                newRepetitions == 2 -> 6
                else -> (card.intervalDays * card.easeFactor).toInt().coerceAtLeast(1)
            }
            // Simple SuperMemo ease factor adjustment
            val newEaseFactor = (card.easeFactor + (0.1f - (5 - difficulty) * (0.08f + (5 - difficulty) * 0.02f)))
                .coerceAtLeast(1.3f)

            val updated = card.copy(
                intervalDays = newInterval,
                repetitions = newRepetitions,
                easeFactor = newEaseFactor,
                dueDate = System.currentTimeMillis() + (newInterval * 24L * 60L * 60L * 1000L)
            )
            repository.insertFlashcard(updated)
        }
    }

    fun deleteFlashcard(id: Int) {
        viewModelScope.launch {
            repository.deleteFlashcardById(id)
        }
    }

    // --- Simulados ---
    val allSimulados: StateFlow<List<Simulado>> = repository.allSimulados
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addNewSimulado(subject: String, totalQuestions: Int, correctAnswers: Int, durationMinutes: Int) {
        viewModelScope.launch {
            repository.insertSimulado(
                Simulado(
                    subject = subject,
                    totalQuestions = totalQuestions,
                    correctAnswers = correctAnswers,
                    durationMinutes = durationMinutes
                )
            )
        }
    }

    // --- Videoaulas ---
    val allVideos: StateFlow<List<VideoAula>> = repository.allVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleVideoCompleted(video: VideoAula) {
        viewModelScope.launch {
            repository.updateVideo(video.copy(isCompleted = !video.isCompleted))
        }
    }

    fun addNewVideo(title: String, category: String, url: String) {
        if (title.isBlank() || category.isBlank()) return
        viewModelScope.launch {
            repository.insertVideo(VideoAula(title = title, category = category, youtubeIdOrUrl = url))
        }
    }

    fun updateVideo(video: VideoAula) {
        viewModelScope.launch {
            repository.updateVideo(video)
        }
    }

    fun resetVideosToEnemCronograma() {
        viewModelScope.launch {
            repository.deleteAllVideos()
            listOf(
                VideoAula(title = "Redação ENEM: Como fazer uma Introdução Nota 1000", category = "Redação", youtubeIdOrUrl = "https://www.youtube.com/watch?v=Lp7eNOn_E6E"),
                VideoAula(title = "Matemática ENEM: Introdução à Função Quadrática", category = "Matemática", youtubeIdOrUrl = "https://www.youtube.com/watch?v=0hWcoA7GfGk"),
                VideoAula(title = "Física ENEM: Cinemática Escalar e Conceitos Iniciais", category = "Física", youtubeIdOrUrl = "https://www.youtube.com/watch?v=Vp2C-Z8wHTo"),
                VideoAula(title = "Biologia ENEM: Genética Mendeliana e Cruzamentos Básicos", category = "Biologia", youtubeIdOrUrl = "https://www.youtube.com/watch?v=P_Yw8vW71Z8"),
                VideoAula(title = "História ENEM: Revolução Francesa Resumo Completo", category = "História", youtubeIdOrUrl = "https://www.youtube.com/watch?v=I8q0S_L4_vU")
            ).forEach { repository.insertVideo(it) }
        }
    }

    // --- Tutor IA Chat (Bate-papo) ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("AI", "Olá! Sou seu Tutor IA gratuito e local. Como posso te ajudar com os seus estudos hoje?")
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage("USER", text)
        _chatMessages.value = _chatMessages.value + userMsg

        _isChatLoading.value = true
        viewModelScope.launch {
            val systemInstruction = "Você é um tutor de vestibular altamente didático e empático, focado no ENEM e vestibulares militares/federais como o ITA. Explique com exemplos."
            val reply = GeminiClient.getCompletion(text, systemInstruction)
            _chatMessages.value = _chatMessages.value + ChatMessage("AI", reply)
            _isChatLoading.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage("AI", "Chat limpo! Como posso te ajudar nos seus estudos?")
        )
    }

    // --- Corretor de Redação ---
    val allEssays: StateFlow<List<Essay>> = repository.allEssays
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isEssayCorrecting = MutableStateFlow(false)
    val isEssayCorrecting: StateFlow<Boolean> = _isEssayCorrecting.asStateFlow()

    fun correctEssay(title: String, text: String) {
        if (title.isBlank() || text.isBlank()) return
        _isEssayCorrecting.value = true
        viewModelScope.launch {
            val prompt = """
                Corrija a seguinte redação para o vestibular do ENEM. O tema proposto está implícito ou explícito no título: "$title".
                
                Texto da redação:
                "$text"
                
                Forneça uma correção detalhada baseada estritamente nas 5 competências do ENEM (Dê notas de 0 a 200 para cada uma):
                1. Domínio da norma culta
                2. Compreensão do tema e aplicação das áreas de conhecimento
                3. Seleção, relação e interpretação de informações (argumentação)
                4. Demonstração de conhecimento dos mecanismos linguísticos (coesão)
                5. Elaboração de proposta de intervenção
                
                Por fim, apresente uma nota total (soma das competências de 0 a 1000) e dê conselhos específicos de reescrita para melhorar a nota.
            """.trimIndent()

            val feedback = GeminiClient.getCompletion(prompt, "Você é um corretor oficial da redação do ENEM especializado e detalhista.")
            repository.insertEssay(Essay(title = title, text = text, feedback = feedback))
            _isEssayCorrecting.value = false
        }
    }

    fun deleteEssay(id: Int) {
        viewModelScope.launch {
            repository.deleteEssayById(id)
        }
    }

    // --- Caderno de Erros ---
    val allErrors: StateFlow<List<CadernoErro>> = repository.allErrors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addNewError(subject: String, questionText: String, errorReason: String, correctConcept: String) {
        if (subject.isBlank() || questionText.isBlank()) return
        viewModelScope.launch {
            repository.insertError(
                CadernoErro(
                    subject = subject,
                    questionText = questionText,
                    errorReason = errorReason,
                    correctConcept = correctConcept
                )
            )
        }
    }

    fun deleteError(id: Int) {
        viewModelScope.launch {
            repository.deleteErrorById(id)
        }
    }

    // --- Pomodoro Timer ---
    private val _pomodoroSecondsLeft = MutableStateFlow(25 * 60)
    val pomodoroSecondsLeft: StateFlow<Int> = _pomodoroSecondsLeft.asStateFlow()

    private val _pomodoroIsRunning = MutableStateFlow(false)
    val pomodoroIsRunning: StateFlow<Boolean> = _pomodoroIsRunning.asStateFlow()

    private val _pomodoroBlockMinutes = MutableStateFlow(25)
    val pomodoroBlockMinutes: StateFlow<Int> = _pomodoroBlockMinutes.asStateFlow()

    private var pomodoroJob: Job? = null

    fun selectPomodoroBlock(minutes: Int) {
        _pomodoroBlockMinutes.value = minutes
        _pomodoroSecondsLeft.value = minutes * 60
        pausePomodoro()
    }

    fun startPomodoro() {
        if (_pomodoroIsRunning.value) return
        _pomodoroIsRunning.value = true
        pomodoroJob = viewModelScope.launch {
            while (_pomodoroSecondsLeft.value > 0) {
                delay(1000L)
                _pomodoroSecondsLeft.value = _pomodoroSecondsLeft.value - 1
            }
            _pomodoroIsRunning.value = false
            // Optional: Triggers standard system notification or sound locally
        }
    }

    fun pausePomodoro() {
        _pomodoroIsRunning.value = false
        pomodoroJob?.cancel()
        pomodoroJob = null
    }

    fun resetPomodoro() {
        pausePomodoro()
        _pomodoroSecondsLeft.value = _pomodoroBlockMinutes.value * 60
    }

    // --- MN RitVida: Painel de Horas ---
    val allHours: StateFlow<List<RitVidaHour>> = repository.allHours
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addWorkedHours(functionName: String, hours: Float, dateString: String) {
        if (functionName.isBlank() || hours <= 0) return
        viewModelScope.launch {
            repository.insertHour(RitVidaHour(functionName = functionName, hours = hours, dateString = dateString))
        }
    }

    fun deleteHour(id: Int) {
        viewModelScope.launch {
            repository.deleteHourById(id)
        }
    }

    fun updateHour(hour: RitVidaHour) {
        viewModelScope.launch {
            repository.insertHour(hour)
        }
    }

    // --- MN RitVida: Finanças ---
    val allTransactions: StateFlow<List<RitVidaFinance>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTransaction(description: String, amount: Float, type: String, dateString: String) {
        if (description.isBlank() || amount <= 0) return
        viewModelScope.launch {
            repository.insertTransaction(
                RitVidaFinance(
                    description = description,
                    amount = amount,
                    type = type,
                    dateString = dateString
                )
            )
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            repository.deleteTransactionById(id)
        }
    }

    // --- MN RitVida: Projetos ---
    val allProjects: StateFlow<List<RitVidaProject>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addProject(name: String, progress: Int, targetDate: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertProject(
                RitVidaProject(
                    name = name,
                    progressPercentage = progress.coerceIn(0, 100),
                    targetDateString = targetDate,
                    isCompleted = progress >= 100
                )
            )
        }
    }

    fun updateProjectProgress(project: RitVidaProject, progress: Int) {
        viewModelScope.launch {
            repository.insertProject(
                project.copy(
                    progressPercentage = progress.coerceIn(0, 100),
                    isCompleted = progress >= 100
                )
            )
        }
    }

    fun deleteProject(id: Int) {
        viewModelScope.launch {
            repository.deleteProjectById(id)
        }
    }

    // --- FocoVest: Cronograma & Calendário ---
    val allSchedules: StateFlow<List<StudySchedule>> = repository.allSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCustomCronogramaItems: StateFlow<List<CustomCronogramaItem>> = repository.allCustomCronogramaItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCustomCronogramaItem(content: String, week: String, dateInterval: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.insertCustomCronogramaItem(
                CustomCronogramaItem(
                    content = content,
                    week = week,
                    dateInterval = dateInterval,
                    isCompleted = false
                )
            )
        }
    }

    fun updateCustomCronogramaItem(item: CustomCronogramaItem) {
        viewModelScope.launch {
            repository.updateCustomCronogramaItem(item)
        }
    }

    fun deleteCustomCronogramaItem(id: Int) {
        viewModelScope.launch {
            repository.deleteCustomCronogramaItemById(id)
        }
    }

    fun clearAllCustomCronogramaItems() {
        viewModelScope.launch {
            repository.deleteAllCustomCronogramaItems()
        }
    }

    fun addSchedule(day: String, duration: Int, subject: String) {
        if (day.isBlank() || subject.isBlank() || duration <= 0) return
        viewModelScope.launch {
            repository.insertSchedule(
                StudySchedule(
                    dayOfWeek = day,
                    durationMinutes = duration,
                    subjectTitle = subject
                )
            )
        }
    }

    fun deleteSchedule(id: Int) {
        viewModelScope.launch {
            repository.deleteScheduleById(id)
        }
    }

    fun moveScheduleDay(scheduleId: Int, newDay: String) {
        viewModelScope.launch {
            val schedules = allSchedules.value
            val target = schedules.find { it.id == scheduleId }
            if (target != null) {
                repository.insertSchedule(target.copy(dayOfWeek = newDay))
            }
        }
    }

    // --- Portfolio Items State ---
    private val _portfolioItems = MutableStateFlow<List<Triple<String, String, String>>>(
        listOf(
            Triple("Animação 3D de Personagem", "Ciclo de caminhada expressivo e animação de diálogo com sincronia labial avançada criada no Blender.", "design"),
            Triple("Modelagem de Cenários 3D", "Modelagem de cenários low-poly de alta fidelidade e mapeamento UV detalhado para ambientes virtuais.", "photo"),
            Triple("Plataforma Educacional", "Tecnologia educacional com exercícios dinâmicos, gamificação e trilhas de estudo personalizadas.", "integration"),
            Triple("Protótipo Físico Articulado", "Construção e montagem de maquete física automatizada com motores servo e controle por microcontrolador.", "manufacturing")
        )
    )
    val portfolioItems: StateFlow<List<Triple<String, String, String>>> = _portfolioItems.asStateFlow()

    fun addPortfolioItem(title: String, description: String, iconType: String) {
        if (title.isBlank()) return
        _portfolioItems.value = _portfolioItems.value + Triple(title, description, iconType)
    }

    fun removePortfolioItem(index: Int) {
        val current = _portfolioItems.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _portfolioItems.value = current
        }
    }

    // --- MEI Financeiro Pro: Lançamentos ---
    val allMeiTransactions: StateFlow<List<MeiTransaction>> = repository.allMeiTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addMeiTransaction(
        description: String,
        amount: Float,
        category: String,
        accountType: String,
        transactionType: String,
        dateString: String,
        hasInvoice: Boolean,
        status: String,
        notes: String
    ) {
        if (description.isBlank() || amount <= 0) return
        viewModelScope.launch {
            repository.insertMeiTransaction(
                MeiTransaction(
                    description = description,
                    amount = amount,
                    category = category,
                    accountType = accountType,
                    transactionType = transactionType,
                    dateString = dateString,
                    hasInvoice = hasInvoice,
                    status = status,
                    notes = notes
                )
            )
        }
    }

    fun deleteMeiTransaction(id: Int) {
        viewModelScope.launch {
            repository.deleteMeiTransactionById(id)
        }
    }

    fun clearMeiTransactions() {
        viewModelScope.launch {
            repository.deleteAllMeiTransactions()
        }
    }

    // --- MEI Financeiro Pro: Notas Fiscais ---
    val allMeiInvoices: StateFlow<List<MeiInvoice>> = repository.allMeiInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addMeiInvoice(
        clientName: String,
        serviceDescription: String,
        amount: Float,
        dueDate: String,
        isIssued: Boolean,
        isSent: Boolean,
        isReceived: Boolean,
        invoiceLink: String
    ) {
        if (clientName.isBlank() || amount <= 0) return
        viewModelScope.launch {
            repository.insertMeiInvoice(
                MeiInvoice(
                    clientName = clientName,
                    serviceDescription = serviceDescription,
                    amount = amount,
                    dueDate = dueDate,
                    isIssued = isIssued,
                    isSent = isSent,
                    isReceived = isReceived,
                    invoiceLink = invoiceLink
                )
            )
        }
    }

    fun updateMeiInvoice(invoice: MeiInvoice) {
        viewModelScope.launch {
            repository.insertMeiInvoice(invoice)
        }
    }

    fun deleteMeiInvoice(id: Int) {
        viewModelScope.launch {
            repository.deleteMeiInvoiceById(id)
        }
    }

    fun clearMeiInvoices() {
        viewModelScope.launch {
            repository.deleteAllMeiInvoices()
        }
    }

    // --- MEI Financeiro Pro: Configurações ---
    val meiConfig: StateFlow<MeiConfig> = repository.meiConfig
        .map { it ?: MeiConfig() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MeiConfig() // fallback default value
        )

    fun saveMeiConfig(config: MeiConfig) {
        viewModelScope.launch {
            repository.saveMeiConfig(config)
        }
    }

    fun restoreExampleMeiData() {
        viewModelScope.launch {
            repository.deleteAllMeiTransactions()
            repository.deleteAllMeiInvoices()
            repository.saveMeiConfig(MeiConfig())
            
            // Reinsert defaults
            listOf(
                MeiTransaction(description = "Desenvolvimento de App", amount = 4200f, category = "Serviços", accountType = "PJ", transactionType = "RECEITA", dateString = "2026-07-10", status = "Pago", hasInvoice = true),
                MeiTransaction(description = "Venda de Placa Eletrônica", amount = 1250f, category = "Vendas", accountType = "PJ", transactionType = "RECEITA", dateString = "2026-07-12", status = "Pago", hasInvoice = false),
                MeiTransaction(description = "Hospedagem Cloud AWS", amount = 180f, category = "Serviços de TI", accountType = "PJ", transactionType = "DESPESA", dateString = "2026-07-13", status = "Pago"),
                MeiTransaction(description = "DAS MEI Julho 2026", amount = 81.9f, category = "Impostos", accountType = "PJ", transactionType = "DESPESA", dateString = "2026-07-15", status = "Pago"),
                MeiTransaction(description = "Supermercado Semanal", amount = 350f, category = "Alimentação", accountType = "PESSOAL", transactionType = "DESPESA", dateString = "2026-07-14", status = "Pago"),
                MeiTransaction(description = "Salário Emprego CLT", amount = 3800f, category = "Salário", accountType = "PESSOAL", transactionType = "RECEITA", dateString = "2026-07-05", status = "Pago")
            ).forEach { repository.insertMeiTransaction(it) }

            listOf(
                MeiInvoice(clientName = "Estúdio Criativo LTDA", serviceDescription = "Modelagem de Cenários 3D", amount = 1200f, dueDate = "2026-07-20", isIssued = true, isSent = true, isReceived = false),
                MeiInvoice(clientName = "Autopeças Silva", serviceDescription = "Consultoria de Engenharia Automotiva", amount = 2500f, dueDate = "2026-07-25", isIssued = true, isSent = false, isReceived = false),
                MeiInvoice(clientName = "Editora Educacional", serviceDescription = "Criação de Banco de Questões", amount = 1500f, dueDate = "2026-07-30", isIssued = false, isSent = false, isReceived = false)
            ).forEach { repository.insertMeiInvoice(it) }
        }
    }

    // --- User Account & Theme ---
    val userAccount: StateFlow<UserAccount?> = repository.userAccount
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    val allGymWorkouts: StateFlow<List<GymWorkout>> = repository.allGymWorkouts
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val allDietLogs: StateFlow<List<DietLog>> = repository.allDietLogs
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun acceptTermsAndCreateAccount(name: String, email: String, passwordHash: String) {
        viewModelScope.launch {
            val account = UserAccount(
                name = name,
                email = email,
                passwordHash = passwordHash,
                termsAccepted = true,
                termsAcceptedTimestamp = System.currentTimeMillis(),
                isDarkTheme = true,
                financeMode = "MEI + Pessoal"
            )
            repository.saveUserAccount(account)
        }
    }

    fun setupOnboardingMode(useDemo: Boolean, selectedEnem: Boolean, selectedIta: Boolean, name: String, email: String, passwordHash: String) {
        viewModelScope.launch {
            repository.clearAllData()
            
            if (useDemo) {
                repository.prepopulateIfEmpty()
            } else {
                if (selectedEnem) {
                    listOf(
                        StudySubject(title = "Matemática ENEM (Álgebra e Geometria)", category = "Matemática"),
                        StudySubject(title = "Linguagens ENEM (Gramática e Literatura)", category = "Linguagens"),
                        StudySubject(title = "Ciências da Natureza ENEM (Física, Química, Biologia)", category = "Ciências da Natureza"),
                        StudySubject(title = "Ciências Humanas ENEM (História, Geografia, Filosofia)", category = "Ciências Humanas"),
                        StudySubject(title = "Redação ENEM (Produção Textual)", category = "Redação")
                    ).forEach { repository.insertSubject(it) }
                    
                    listOf(
                        VideoAula(title = "Redação ENEM: Como fazer uma Introdução Nota 1000", category = "Redação", youtubeIdOrUrl = "https://www.youtube.com/watch?v=Lp7eNOn_E6E"),
                        VideoAula(title = "Matemática ENEM: Introdução à Função Quadrática", category = "Matemática", youtubeIdOrUrl = "https://www.youtube.com/watch?v=0hWcoA7GfGk")
                    ).forEach { repository.insertVideo(it) }
                }
                
                if (selectedIta) {
                    listOf(
                        StudySubject(title = "Matemática ITA (Cônicas, Matrizes, Polinômios, Números Complexos)", category = "Matemática"),
                        StudySubject(title = "Física ITA (Mecânica Avançada, Termodinâmica, Eletromagnetismo)", category = "Física"),
                        StudySubject(title = "Química ITA (Química Inorgânica, Cinética Química, Equilíbrio Químico)", category = "Química"),
                        StudySubject(title = "Língua Portuguesa & Literatura ITA (Classicismo, Modernismo e Obras Obrigatórias)", category = "Português"),
                        StudySubject(title = "Redação ITA (Argumentativa e Temas Filosóficos)", category = "Redação")
                    ).forEach { repository.insertSubject(it) }
                    
                    listOf(
                        VideoAula(title = "Matemática ITA: Números Complexos e Forma Trigonométrica", category = "Matemática", youtubeIdOrUrl = "https://www.youtube.com/watch?v=f-R3S_kof3Q"),
                        VideoAula(title = "Física ITA: Leis de Newton em Referenciais Não-Inerciais", category = "Física", youtubeIdOrUrl = "https://www.youtube.com/watch?v=kYJvMvIdfU0")
                    ).forEach { repository.insertVideo(it) }
                }

                repository.saveMeiConfig(MeiConfig())
            }

            val account = UserAccount(
                name = name,
                email = email,
                passwordHash = passwordHash,
                termsAccepted = true,
                termsAcceptedTimestamp = System.currentTimeMillis(),
                isDarkTheme = true,
                financeMode = "MEI + Pessoal"
            )
            repository.saveUserAccount(account)
        }
    }

    fun updateUserAccount(name: String, email: String) {
        viewModelScope.launch {
            val current = userAccount.value ?: UserAccount()
            val updated = current.copy(name = name, email = email)
            repository.saveUserAccount(updated)
        }
    }

    fun deleteUserAccount() {
        viewModelScope.launch {
            repository.deleteUserAccount()
        }
    }

    fun logoutUserAccount() {
        viewModelScope.launch {
            val current = userAccount.value
            if (current != null) {
                val updated = current.copy(termsAccepted = false)
                repository.saveUserAccount(updated)
            } else {
                repository.deleteUserAccount()
            }
        }
    }

    fun loginWithExistingAccount() {
        viewModelScope.launch {
            val current = userAccount.value
            if (current != null) {
                val updated = current.copy(termsAccepted = true)
                repository.saveUserAccount(updated)
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val current = userAccount.value ?: UserAccount()
            val updated = current.copy(isDarkTheme = !current.isDarkTheme)
            repository.saveUserAccount(updated)
        }
    }

    fun updateFinanceMode(mode: String) {
        viewModelScope.launch {
            val current = userAccount.value ?: UserAccount()
            val updated = current.copy(financeMode = mode)
            repository.saveUserAccount(updated)
        }
    }

    fun insertGymWorkout(exercise: String, sets: Int, reps: Int, weightKg: Float, dateString: String) {
        viewModelScope.launch {
            repository.insertGymWorkout(GymWorkout(exercise = exercise, sets = sets, reps = reps, weightKg = weightKg, dateString = dateString))
        }
    }

    fun deleteGymWorkout(id: Int) {
        viewModelScope.launch {
            repository.deleteGymWorkoutById(id)
        }
    }

    fun toggleGymWorkoutStatus(id: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateGymWorkoutStatus(id, isCompleted)
        }
    }

    fun insertDietLog(mealType: String, foodName: String, caloriesKcal: Int, waterIntakeMl: Int, dateString: String) {
        viewModelScope.launch {
            repository.insertDietLog(DietLog(mealType = mealType, foodName = foodName, caloriesKcal = caloriesKcal, waterIntakeMl = waterIntakeMl, dateString = dateString))
        }
    }

    fun deleteDietLog(id: Int) {
        viewModelScope.launch {
            repository.deleteDietLogById(id)
        }
    }

    // --- Visual Tasks ---
    val allVisualTasks: StateFlow<List<VisualTask>> = repository.allVisualTasks
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun insertVisualTask(
        title: String,
        startDate: String,
        startTime: String,
        endDate: String,
        endTime: String,
        startHour: Int,
        durationHours: Int,
        function: String,
        tag: String,
        checklistRaw: String
    ) {
        viewModelScope.launch {
            repository.insertVisualTask(
                VisualTask(
                    title = title,
                    startDate = startDate,
                    startTime = startTime,
                    endDate = endDate,
                    endTime = endTime,
                    startHour = startHour,
                    durationHours = durationHours,
                    function = function,
                    tag = tag,
                    checklistRaw = checklistRaw
                )
            )
        }
    }

    fun updateVisualTask(task: VisualTask) {
        viewModelScope.launch {
            repository.updateVisualTask(task)
        }
    }

    fun deleteVisualTask(id: Int) {
        viewModelScope.launch {
            repository.deleteVisualTaskById(id)
        }
    }
}

class AppViewModelFactory(private val application: Application, private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
