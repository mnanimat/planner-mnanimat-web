package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_subjects")
data class StudySubject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String, // Matemática, Biologia, Física, Redação, etc.
    val stepAula: Boolean = false,
    val stepResumo: Boolean = false,
    val stepAutoexplicacao: Boolean = false,
    val stepExercicios: Boolean = false,
    val stepCadernoErros: Boolean = false,
    val stepRevisao: Boolean = false,
    val stepSimulado: Boolean = false
) {
    fun getProgressPercent(): Int {
        val steps = listOf(stepAula, stepResumo, stepAutoexplicacao, stepExercicios, stepCadernoErros, stepRevisao, stepSimulado)
        val completed = steps.count { it }
        return (completed * 100) / steps.size
    }
}

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val answer: String,
    val intervalDays: Int = 1,
    val easeFactor: Float = 2.5f,
    val repetitions: Int = 0,
    val dueDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "simulados")
data class Simulado(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "video_aulas")
data class VideoAula(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String,
    val youtubeIdOrUrl: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "caderno_erros")
data class CadernoErro(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val questionText: String,
    val errorReason: String,
    val correctConcept: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "essays")
data class Essay(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val text: String,
    val feedback: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ritvida_hours")
data class RitVidaHour(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val functionName: String, // Trabalho, Saúde, Estudante, Administrativo
    val hours: Float,
    val dateString: String // yyyy-MM-dd
)

@Entity(tableName = "ritvida_finances")
data class RitVidaFinance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val amount: Float,
    val type: String, // REVENUE, EXPENSE
    val dateString: String // yyyy-MM-dd
)

@Entity(tableName = "ritvida_projects")
data class RitVidaProject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val progressPercentage: Int,
    val targetDateString: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "study_schedules")
data class StudySchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayOfWeek: String, // Segunda, Terça, etc.
    val durationMinutes: Int,
    val subjectTitle: String
)

// --- MEI Financeiro Pro: Lançamentos ---
@Entity(tableName = "mei_transactions")
data class MeiTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateString: String,
    val description: String,
    val amount: Float,
    val category: String,
    val accountType: String, // "PJ" (MEI) or "PESSOAL"
    val transactionType: String, // "RECEITA" or "DESPESA"
    val hasInvoice: Boolean = false,
    val status: String = "Pago", // "Pago" or "Pendente"
    val notes: String = ""
)

// --- MEI Financeiro Pro: Notas Fiscais ---
@Entity(tableName = "mei_invoices")
data class MeiInvoice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val serviceDescription: String,
    val amount: Float,
    val dueDate: String,
    val isIssued: Boolean = false,
    val isSent: Boolean = false,
    val isReceived: Boolean = false,
    val invoiceLink: String = ""
)

// --- MEI Financeiro Pro: Configurações ---
@Entity(tableName = "mei_configs")
data class MeiConfig(
    @PrimaryKey val id: Int = 1,
    val annualLimit: Float = 81000f,
    val monthlyDas: Float = 81.9f,
    val monthlySavingsGoal: Float = 500f,
    val emergencyFundGoal: Float = 6000f,
    val monthlyMeiRevenueGoal: Float = 6750f
)

// --- Planner MNAnimat: Conta do Usuário e Configurações ---
@Entity(tableName = "user_account")
data class UserAccount(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val email: String = "",
    val passwordHash: String = "",
    val termsAccepted: Boolean = false,
    val termsAcceptedTimestamp: Long = 0L,
    val isDarkTheme: Boolean = true,
    val financeMode: String = "MEI + Pessoal"
)

// --- Academia e Dieta ---
@Entity(tableName = "gym_workouts")
data class GymWorkout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exercise: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Float,
    val dateString: String, // yyyy-MM-dd
    val isCompleted: Boolean = false
)

@Entity(tableName = "diet_logs")
data class DietLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mealType: String, // Café da Manhã, Almoço, Café da Tarde, Jantar, Lanches, etc.
    val foodName: String,
    val caloriesKcal: Int,
    val waterIntakeMl: Int = 0,
    val dateString: String // yyyy-MM-dd
)

@Entity(tableName = "visual_tasks")
data class VisualTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val startDate: String,       // yyyy-MM-dd
    val startTime: String,       // HH:mm
    val endDate: String,         // yyyy-MM-dd
    val endTime: String,         // HH:mm
    val startHour: Int = 9,      // 0 to 23
    val durationHours: Int = 1,  // 1 to 24
    val function: String,        // e.g. "Trabalho", "Saúde"
    val tag: String,             // Option to set tag
    val checklistRaw: String = "" // "item1:false|item2:true"
)

@Entity(tableName = "custom_cronograma_items")
data class CustomCronogramaItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val week: String,
    val dateInterval: String,
    val isCompleted: Boolean = false
)



