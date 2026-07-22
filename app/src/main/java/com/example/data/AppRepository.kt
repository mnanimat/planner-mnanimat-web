package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val db: AppDatabase) {

    val allSubjects: Flow<List<StudySubject>> = db.studySubjectDao().getAllSubjects()
    val allFlashcards: Flow<List<Flashcard>> = db.flashcardDao().getAllFlashcards()
    val allSimulados: Flow<List<Simulado>> = db.simuladoDao().getAllSimulados()
    val allVideos: Flow<List<VideoAula>> = db.videoAulaDao().getAllVideos()
    val allErrors: Flow<List<CadernoErro>> = db.cadernoErroDao().getAllErrors()
    val allEssays: Flow<List<Essay>> = db.essayDao().getAllEssays()
    val allHours: Flow<List<RitVidaHour>> = db.ritVidaHourDao().getAllHours()
    val allTransactions: Flow<List<RitVidaFinance>> = db.ritVidaFinanceDao().getAllTransactions()
    val allProjects: Flow<List<RitVidaProject>> = db.ritVidaProjectDao().getAllProjects()
    val allSchedules: Flow<List<StudySchedule>> = db.studyScheduleDao().getAllSchedules()
    val allMeiTransactions: Flow<List<MeiTransaction>> = db.meiTransactionDao().getAllTransactions()
    val allMeiInvoices: Flow<List<MeiInvoice>> = db.meiInvoiceDao().getAllInvoices()
    val meiConfig: Flow<MeiConfig?> = db.meiConfigDao().getConfig()
    val userAccount: Flow<UserAccount?> = db.userAccountDao().getUserAccountFlow()
    val allGymWorkouts: Flow<List<GymWorkout>> = db.gymWorkoutDao().getAllGymWorkoutsFlow()
    val allDietLogs: Flow<List<DietLog>> = db.dietLogDao().getAllDietLogsFlow()
    val allVisualTasks: Flow<List<VisualTask>> = db.visualTaskDao().getAllVisualTasksFlow()
    val allCustomCronogramaItems: Flow<List<CustomCronogramaItem>> = db.customCronogramaDao().getAllCustomItems()

    suspend fun insertCustomCronogramaItem(item: CustomCronogramaItem) = withContext(Dispatchers.IO) {
        db.customCronogramaDao().insertCustomItem(item)
    }

    suspend fun updateCustomCronogramaItem(item: CustomCronogramaItem) = withContext(Dispatchers.IO) {
        db.customCronogramaDao().updateCustomItem(item)
    }

    suspend fun deleteCustomCronogramaItemById(id: Int) = withContext(Dispatchers.IO) {
        db.customCronogramaDao().deleteCustomItemById(id)
    }

    suspend fun deleteAllCustomCronogramaItems() = withContext(Dispatchers.IO) {
        db.customCronogramaDao().deleteAllCustomItems()
    }

    suspend fun insertVisualTask(task: VisualTask) = withContext(Dispatchers.IO) {
        db.visualTaskDao().insertVisualTask(task)
    }

    suspend fun updateVisualTask(task: VisualTask) = withContext(Dispatchers.IO) {
        db.visualTaskDao().updateVisualTask(task)
    }

    suspend fun deleteVisualTaskById(id: Int) = withContext(Dispatchers.IO) {
        db.visualTaskDao().deleteVisualTaskById(id)
    }

    suspend fun getUserAccount(): UserAccount? = withContext(Dispatchers.IO) {
        db.userAccountDao().getUserAccount()
    }

    suspend fun saveUserAccount(account: UserAccount) = withContext(Dispatchers.IO) {
        db.userAccountDao().saveUserAccount(account)
    }

    suspend fun deleteUserAccount() = withContext(Dispatchers.IO) {
        db.userAccountDao().deleteUserAccount()
    }

    suspend fun insertGymWorkout(workout: GymWorkout) = withContext(Dispatchers.IO) {
        db.gymWorkoutDao().insertGymWorkout(workout)
    }

    suspend fun deleteGymWorkoutById(id: Int) = withContext(Dispatchers.IO) {
        db.gymWorkoutDao().deleteGymWorkoutById(id)
    }

    suspend fun updateGymWorkoutStatus(id: Int, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        db.gymWorkoutDao().updateGymWorkoutStatus(id, isCompleted)
    }

    suspend fun insertDietLog(diet: DietLog) = withContext(Dispatchers.IO) {
        db.dietLogDao().insertDietLog(diet)
    }

    suspend fun deleteDietLogById(id: Int) = withContext(Dispatchers.IO) {
        db.dietLogDao().deleteDietLogById(id)
    }

    fun getDueFlashcards(currentTime: Long): Flow<List<Flashcard>> =
        db.flashcardDao().getDueFlashcards(currentTime)

    suspend fun insertSubject(subject: StudySubject) = withContext(Dispatchers.IO) {
        db.studySubjectDao().insertSubject(subject)
    }

    suspend fun updateSubject(subject: StudySubject) = withContext(Dispatchers.IO) {
        db.studySubjectDao().updateSubject(subject)
    }

    suspend fun deleteSubjectById(id: Int) = withContext(Dispatchers.IO) {
        db.studySubjectDao().deleteSubjectById(id)
    }

    suspend fun insertFlashcard(flashcard: Flashcard) = withContext(Dispatchers.IO) {
        db.flashcardDao().insertFlashcard(flashcard)
    }

    suspend fun deleteFlashcardById(id: Int) = withContext(Dispatchers.IO) {
        db.flashcardDao().deleteFlashcardById(id)
    }

    suspend fun insertSimulado(simulado: Simulado) = withContext(Dispatchers.IO) {
        db.simuladoDao().insertSimulado(simulado)
    }

    suspend fun insertVideo(video: VideoAula) = withContext(Dispatchers.IO) {
        db.videoAulaDao().insertVideo(video)
    }

    suspend fun updateVideo(video: VideoAula) = withContext(Dispatchers.IO) {
        db.videoAulaDao().updateVideo(video)
    }

    suspend fun deleteAllVideos() = withContext(Dispatchers.IO) {
        db.videoAulaDao().deleteAllVideos()
    }

    suspend fun insertError(error: CadernoErro) = withContext(Dispatchers.IO) {
        db.cadernoErroDao().insertError(error)
    }

    suspend fun deleteErrorById(id: Int) = withContext(Dispatchers.IO) {
        db.cadernoErroDao().deleteErrorById(id)
    }

    suspend fun insertEssay(essay: Essay) = withContext(Dispatchers.IO) {
        db.essayDao().insertEssay(essay)
    }

    suspend fun deleteEssayById(id: Int) = withContext(Dispatchers.IO) {
        db.essayDao().deleteEssayById(id)
    }

    suspend fun insertHour(hour: RitVidaHour) = withContext(Dispatchers.IO) {
        db.ritVidaHourDao().insertHour(hour)
    }

    suspend fun deleteHourById(id: Int) = withContext(Dispatchers.IO) {
        db.ritVidaHourDao().deleteHourById(id)
    }

    suspend fun insertTransaction(transaction: RitVidaFinance) = withContext(Dispatchers.IO) {
        db.ritVidaFinanceDao().insertTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Int) = withContext(Dispatchers.IO) {
        db.ritVidaFinanceDao().deleteTransactionById(id)
    }

    suspend fun insertProject(project: RitVidaProject) = withContext(Dispatchers.IO) {
        db.ritVidaProjectDao().insertProject(project)
    }

    suspend fun deleteProjectById(id: Int) = withContext(Dispatchers.IO) {
        db.ritVidaProjectDao().deleteProjectById(id)
    }

    suspend fun insertSchedule(schedule: StudySchedule) = withContext(Dispatchers.IO) {
        db.studyScheduleDao().insertSchedule(schedule)
    }

    suspend fun deleteScheduleById(id: Int) = withContext(Dispatchers.IO) {
        db.studyScheduleDao().deleteScheduleById(id)
    }

    suspend fun insertMeiTransaction(transaction: MeiTransaction) = withContext(Dispatchers.IO) {
        db.meiTransactionDao().insertTransaction(transaction)
    }

    suspend fun deleteMeiTransactionById(id: Int) = withContext(Dispatchers.IO) {
        db.meiTransactionDao().deleteTransactionById(id)
    }

    suspend fun deleteAllMeiTransactions() = withContext(Dispatchers.IO) {
        db.meiTransactionDao().deleteAllTransactions()
    }

    suspend fun insertMeiInvoice(invoice: MeiInvoice) = withContext(Dispatchers.IO) {
        db.meiInvoiceDao().insertInvoice(invoice)
    }

    suspend fun deleteMeiInvoiceById(id: Int) = withContext(Dispatchers.IO) {
        db.meiInvoiceDao().deleteInvoiceById(id)
    }

    suspend fun deleteAllMeiInvoices() = withContext(Dispatchers.IO) {
        db.meiInvoiceDao().deleteAllInvoices()
    }

    suspend fun saveMeiConfig(config: MeiConfig) = withContext(Dispatchers.IO) {
        db.meiConfigDao().saveConfig(config)
    }

    suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        // Pre-populate Subjects
        val subjectsCount = allSubjects.first().size
        if (subjectsCount == 0) {
            listOf(
                StudySubject(title = "Introdução à Redação do ENEM", category = "Redação", stepAula = true, stepResumo = true, stepAutoexplicacao = true),
                StudySubject(title = "Função Quadrática", category = "Matemática", stepAula = true, stepResumo = true, stepExercicios = true),
                StudySubject(title = "Cinemática Escalar", category = "Física", stepAula = true, stepResumo = false),
                StudySubject(title = "Genética Mendeliana", category = "Biologia", stepAula = true, stepResumo = true, stepAutoexplicacao = true, stepExercicios = true, stepRevisao = true),
                StudySubject(title = "Eletrostática", category = "Física", stepAula = false),
                StudySubject(title = "Revolução Francesa", category = "História", stepAula = true, stepResumo = true)
            ).forEach { db.studySubjectDao().insertSubject(it) }
        }

        // Pre-populate Flashcards
        val cardsCount = allFlashcards.first().size
        if (cardsCount == 0) {
            listOf(
                Flashcard(question = "Qual o elemento obrigatório na Proposta de Intervenção da redação do ENEM?", answer = "A Proposta de Intervenção deve conter os 5 elementos essenciais:\n1. Agente (quem faz)\n2. Ação (o que faz)\n3. Meio/Modo (como faz)\n4. Efeito (para que faz)\n5. Detalhamento (explicação extra de um dos itens)."),
                Flashcard(question = "Como se calcula o vértice de uma parábola (Xv e Yv)?", answer = "Xv = -b / (2a)\nYv = -Δ / (4a)\nonde Δ = b² - 4ac."),
                Flashcard(question = "Qual a primeira Lei de Newton?", answer = "Inércia: Um corpo permanece em seu estado de repouso ou de movimento retilíneo uniforme, a menos que seja compelido a alterar esse estado por forças impressas sobre ele.")
            ).forEach { db.flashcardDao().insertFlashcard(it) }
        }

        // Pre-populate Video Lectures
        val videosCount = allVideos.first().size
        if (videosCount == 0) {
            listOf(
                VideoAula(title = "REDAÇÃO ENEM: COMO FAZER A INTRODUÇÃO PERFEITA", category = "Redação", youtubeIdOrUrl = "https://www.youtube.com/watch?v=7YvN0bY_mQ0"),
                VideoAula(title = "FUNÇÃO QUADRÁTICA (FUNÇÃO DO 2º GRAU) - Aula 1", category = "Matemática", youtubeIdOrUrl = "https://www.youtube.com/watch?v=bL6FfCg1eH8"),
                VideoAula(title = "CINEMÁTICA - CONCEITOS BÁSICOS - Aula 1", category = "Física", youtubeIdOrUrl = "https://www.youtube.com/watch?v=GAnC7z-b6Dk"),
                VideoAula(title = "GENÉTICA: 1ª LEI DE MENDEL", category = "Biologia", youtubeIdOrUrl = "https://www.youtube.com/watch?v=9_6K8N7mZfA"),
                VideoAula(title = "REVOLUÇÃO FRANCESA - Resumo Desenhado", category = "História", youtubeIdOrUrl = "https://www.youtube.com/watch?v=sc7Z8Yq49jM")
            ).forEach { db.videoAulaDao().insertVideo(it) }
        }

        // Pre-populate Projects
        val projectsCount = allProjects.first().size
        if (projectsCount == 0) {
            listOf(
                RitVidaProject(name = "Protótipo de veículo off-road", progressPercentage = 45, targetDateString = "2026-12-15", isCompleted = false),
                RitVidaProject(name = "Identidade visual do estúdio", progressPercentage = 85, targetDateString = "2026-08-01", isCompleted = false),
                RitVidaProject(name = "Plataforma Educacional", progressPercentage = 95, targetDateString = "2026-07-20", isCompleted = false)
            ).forEach { db.ritVidaProjectDao().insertProject(it) }
        }

        // Pre-populate Hours
        val hoursCount = allHours.first().size
        if (hoursCount == 0) {
            listOf(
                // Week 29
                RitVidaHour(functionName = "Trabalho", hours = 15.5f, dateString = "2026-07-14"),
                RitVidaHour(functionName = "Saúde", hours = 28.0f, dateString = "2026-07-14"),
                RitVidaHour(functionName = "Estudante", hours = 22.5f, dateString = "2026-07-14"),
                RitVidaHour(functionName = "Administrativo", hours = 10.0f, dateString = "2026-07-14"),
                // Week 28
                RitVidaHour(functionName = "Estudante", hours = 15.0f, dateString = "2026-07-07"),
                // Week 27
                RitVidaHour(functionName = "Estudante", hours = 18.5f, dateString = "2026-06-30"),
                // Week 26
                RitVidaHour(functionName = "Estudante", hours = 12.0f, dateString = "2026-06-23"),
                // Week 25
                RitVidaHour(functionName = "Estudante", hours = 25.0f, dateString = "2026-06-16")
            ).forEach { db.ritVidaHourDao().insertHour(it) }
        }

        // Pre-populate Finances
        val financeCount = allTransactions.first().size
        if (financeCount == 0) {
            listOf(
                RitVidaFinance(description = "Bolsa de Estudos / Projetos", amount = 1500f, type = "REVENUE", dateString = "2026-07-10"),
                RitVidaFinance(description = "Serviço Modelagem 3D", amount = 2500f, type = "REVENUE", dateString = "2026-07-12"),
                RitVidaFinance(description = "Livros Didáticos ENEM", amount = 250f, type = "EXPENSE", dateString = "2026-07-13"),
                RitVidaFinance(description = "Mensalidade Internet", amount = 120f, type = "EXPENSE", dateString = "2026-07-14")
            ).forEach { db.ritVidaFinanceDao().insertTransaction(it) }
        }

        // Pre-populate Schedule
        val schedulesCount = allSchedules.first().size
        if (schedulesCount == 0) {
            listOf(
                StudySchedule(dayOfWeek = "Segunda", durationMinutes = 120, subjectTitle = "Função Quadrática"),
                StudySchedule(dayOfWeek = "Terça", durationMinutes = 90, subjectTitle = "Cinemática Escalar"),
                StudySchedule(dayOfWeek = "Quarta", durationMinutes = 120, subjectTitle = "Introdução à Redação do ENEM"),
                StudySchedule(dayOfWeek = "Quinta", durationMinutes = 90, subjectTitle = "Genética Mendeliana"),
                StudySchedule(dayOfWeek = "Sexta", durationMinutes = 120, subjectTitle = "Revolução Francesa")
            ).forEach { db.studyScheduleDao().insertSchedule(it) }
        }

        // Pre-populate MEI Config
        val configExist = db.meiConfigDao().getConfig().first()
        if (configExist == null) {
            db.meiConfigDao().saveConfig(MeiConfig())
        }

        // Pre-populate MEI Transactions
        val meiTxCount = db.meiTransactionDao().getAllTransactions().first().size
        if (meiTxCount == 0) {
            listOf(
                MeiTransaction(description = "Desenvolvimento de App", amount = 4200f, category = "Serviços", accountType = "PJ", transactionType = "RECEITA", dateString = "2026-07-10", status = "Pago", hasInvoice = true),
                MeiTransaction(description = "Venda de Placa Eletrônica", amount = 1250f, category = "Vendas", accountType = "PJ", transactionType = "RECEITA", dateString = "2026-07-12", status = "Pago", hasInvoice = false),
                MeiTransaction(description = "Hospedagem Cloud AWS", amount = 180f, category = "Serviços de TI", accountType = "PJ", transactionType = "DESPESA", dateString = "2026-07-13", status = "Pago"),
                MeiTransaction(description = "DAS MEI Julho 2026", amount = 81.9f, category = "Impostos", accountType = "PJ", transactionType = "DESPESA", dateString = "2026-07-15", status = "Pago"),
                MeiTransaction(description = "Supermercado Semanal", amount = 350f, category = "Alimentação", accountType = "PESSOAL", transactionType = "DESPESA", dateString = "2026-07-14", status = "Pago"),
                MeiTransaction(description = "Salário Emprego CLT", amount = 3800f, category = "Salário", accountType = "PESSOAL", transactionType = "RECEITA", dateString = "2026-07-05", status = "Pago")
            ).forEach { db.meiTransactionDao().insertTransaction(it) }
        }

        // Pre-populate MEI Invoices
        val meiInvoiceCount = db.meiInvoiceDao().getAllInvoices().first().size
        if (meiInvoiceCount == 0) {
            listOf(
                MeiInvoice(clientName = "Estúdio Criativo LTDA", serviceDescription = "Modelagem de Cenários 3D", amount = 1200f, dueDate = "2026-07-20", isIssued = true, isSent = true, isReceived = false),
                MeiInvoice(clientName = "Autopeças Silva", serviceDescription = "Consultoria de Engenharia Automotiva", amount = 2500f, dueDate = "2026-07-25", isIssued = true, isSent = false, isReceived = false),
                MeiInvoice(clientName = "Editora Educacional", serviceDescription = "Criação de Banco de Questões", amount = 1500f, dueDate = "2026-07-30", isIssued = false, isSent = false, isReceived = false)
            ).forEach { db.meiInvoiceDao().insertInvoice(it) }
        }

        // Pre-populate Visual Tasks with Educational App Development Tasks
        val tasksCount = db.visualTaskDao().getAllVisualTasksFlow().first().size
        if (tasksCount == 0) {
            listOf(
                VisualTask(
                    title = "Modelagem do Banco de Dados local (Room)",
                    startDate = "2026-07-15",
                    startTime = "09:00",
                    endDate = "2026-07-15",
                    endTime = "11:00",
                    startHour = 9,
                    durationHours = 2,
                    function = "Estudante",
                    tag = "Plataforma Educacional",
                    checklistRaw = "Definir tabelas do Room:true|Criar DAOs do banco:true|Validar relacionamentos de entidades:false"
                ),
                VisualTask(
                    title = "Criação da Interface da Trilha de Aprendizagem",
                    startDate = "2026-07-16",
                    startTime = "14:00",
                    endDate = "2026-07-16",
                    endTime = "17:00",
                    startHour = 14,
                    durationHours = 3,
                    function = "Saúde",
                    tag = "Plataforma Educacional",
                    checklistRaw = "Rascunhar wireframes da trilha:true|Implementar Canvas com Jetpack Compose:true|Testar animação de transição:false"
                ),
                VisualTask(
                    title = "Integração do Tutor de IA local",
                    startDate = "2026-07-17",
                    startTime = "10:00",
                    endDate = "2026-07-17",
                    endTime = "12:00",
                    startHour = 10,
                    durationHours = 2,
                    function = "Trabalho",
                    tag = "Plataforma Educacional",
                    checklistRaw = "Configurar chaves de API local:true|Desenvolver prompts de auxílio educacional:true|Implementar stream de respostas:true"
                ),
                VisualTask(
                    title = "Testes de Usabilidade com Estudantes",
                    startDate = "2026-07-18",
                    startTime = "15:00",
                    endDate = "2026-07-18",
                    endTime = "18:00",
                    startHour = 15,
                    durationHours = 3,
                    function = "Estudante",
                    tag = "Plataforma Educacional",
                    checklistRaw = "Recrutar 5 estudantes de teste:true|Aplicar roteiro de tarefas no app:true|Coletar feedbacks e relatórios de bugs:true"
                )
            ).forEach { db.visualTaskDao().insertVisualTask(it) }
        }
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        db.clearAllTables()
    }
}
