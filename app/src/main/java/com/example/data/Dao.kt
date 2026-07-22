package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySubjectDao {
    @Query("SELECT * FROM study_subjects ORDER BY id DESC")
    fun getAllSubjects(): Flow<List<StudySubject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: StudySubject)

    @Update
    suspend fun updateSubject(subject: StudySubject)

    @Query("DELETE FROM study_subjects WHERE id = :id")
    suspend fun deleteSubjectById(id: Int)
}

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards ORDER BY id DESC")
    fun getAllFlashcards(): Flow<List<Flashcard>>

    @Query("SELECT * FROM flashcards WHERE dueDate <= :currentTime ORDER BY dueDate ASC")
    fun getDueFlashcards(currentTime: Long): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard)

    @Query("DELETE FROM flashcards WHERE id = :id")
    suspend fun deleteFlashcardById(id: Int)
}

@Dao
interface SimuladoDao {
    @Query("SELECT * FROM simulados ORDER BY timestamp DESC")
    fun getAllSimulados(): Flow<List<Simulado>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSimulado(simulado: Simulado)
}

@Dao
interface VideoAulaDao {
    @Query("SELECT * FROM video_aulas ORDER BY id DESC")
    fun getAllVideos(): Flow<List<VideoAula>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoAula)

    @Update
    suspend fun updateVideo(video: VideoAula)

    @Query("DELETE FROM video_aulas")
    suspend fun deleteAllVideos()
}

@Dao
interface CadernoErroDao {
    @Query("SELECT * FROM caderno_erros ORDER BY timestamp DESC")
    fun getAllErrors(): Flow<List<CadernoErro>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertError(error: CadernoErro)

    @Query("DELETE FROM caderno_erros WHERE id = :id")
    suspend fun deleteErrorById(id: Int)
}

@Dao
interface EssayDao {
    @Query("SELECT * FROM essays ORDER BY timestamp DESC")
    fun getAllEssays(): Flow<List<Essay>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEssay(essay: Essay)

    @Query("DELETE FROM essays WHERE id = :id")
    suspend fun deleteEssayById(id: Int)
}

@Dao
interface RitVidaHourDao {
    @Query("SELECT * FROM ritvida_hours ORDER BY dateString DESC")
    fun getAllHours(): Flow<List<RitVidaHour>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHour(hour: RitVidaHour)

    @Query("DELETE FROM ritvida_hours WHERE id = :id")
    suspend fun deleteHourById(id: Int)
}

@Dao
interface RitVidaFinanceDao {
    @Query("SELECT * FROM ritvida_finances ORDER BY dateString DESC")
    fun getAllTransactions(): Flow<List<RitVidaFinance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: RitVidaFinance)

    @Query("DELETE FROM ritvida_finances WHERE id = :id")
    suspend fun deleteTransactionById(id: Int)
}

@Dao
interface RitVidaProjectDao {
    @Query("SELECT * FROM ritvida_projects ORDER BY id DESC")
    fun getAllProjects(): Flow<List<RitVidaProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: RitVidaProject)

    @Query("DELETE FROM ritvida_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Int)
}

@Dao
interface StudyScheduleDao {
    @Query("SELECT * FROM study_schedules ORDER BY id DESC")
    fun getAllSchedules(): Flow<List<StudySchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: StudySchedule)

    @Query("DELETE FROM study_schedules WHERE id = :id")
    suspend fun deleteScheduleById(id: Int)
}

@Dao
interface MeiTransactionDao {
    @Query("SELECT * FROM mei_transactions ORDER BY dateString DESC, id DESC")
    fun getAllTransactions(): Flow<List<MeiTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: MeiTransaction)

    @Query("DELETE FROM mei_transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Int)

    @Query("DELETE FROM mei_transactions")
    suspend fun deleteAllTransactions()
}

@Dao
interface MeiInvoiceDao {
    @Query("SELECT * FROM mei_invoices ORDER BY dueDate DESC, id DESC")
    fun getAllInvoices(): Flow<List<MeiInvoice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: MeiInvoice)

    @Query("DELETE FROM mei_invoices WHERE id = :id")
    suspend fun deleteInvoiceById(id: Int)

    @Query("DELETE FROM mei_invoices")
    suspend fun deleteAllInvoices()
}

@Dao
interface MeiConfigDao {
    @Query("SELECT * FROM mei_configs WHERE id = 1 LIMIT 1")
    fun getConfig(): Flow<MeiConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: MeiConfig)
}

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_account WHERE id = 1 LIMIT 1")
    fun getUserAccountFlow(): Flow<UserAccount?>

    @Query("SELECT * FROM user_account WHERE id = 1 LIMIT 1")
    suspend fun getUserAccount(): UserAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserAccount(userAccount: UserAccount)

    @Query("DELETE FROM user_account")
    suspend fun deleteUserAccount()
}

@Dao
interface GymWorkoutDao {
    @Query("SELECT * FROM gym_workouts ORDER BY id DESC")
    fun getAllGymWorkoutsFlow(): Flow<List<GymWorkout>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGymWorkout(workout: GymWorkout)

    @Query("DELETE FROM gym_workouts WHERE id = :id")
    suspend fun deleteGymWorkoutById(id: Int)

    @Query("UPDATE gym_workouts SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateGymWorkoutStatus(id: Int, isCompleted: Boolean)
}

@Dao
interface DietLogDao {
    @Query("SELECT * FROM diet_logs ORDER BY id DESC")
    fun getAllDietLogsFlow(): Flow<List<DietLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDietLog(diet: DietLog)

    @Query("DELETE FROM diet_logs WHERE id = :id")
    suspend fun deleteDietLogById(id: Int)
}

@Dao
interface VisualTaskDao {
    @Query("SELECT * FROM visual_tasks ORDER BY id DESC")
    fun getAllVisualTasksFlow(): Flow<List<VisualTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisualTask(task: VisualTask)

    @Update
    suspend fun updateVisualTask(task: VisualTask)

    @Query("DELETE FROM visual_tasks WHERE id = :id")
    suspend fun deleteVisualTaskById(id: Int)
}

@Dao
interface CustomCronogramaDao {
    @Query("SELECT * FROM custom_cronograma_items ORDER BY id ASC")
    fun getAllCustomItems(): Flow<List<CustomCronogramaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomItem(item: CustomCronogramaItem)

    @Update
    suspend fun updateCustomItem(item: CustomCronogramaItem)

    @Query("DELETE FROM custom_cronograma_items WHERE id = :id")
    suspend fun deleteCustomItemById(id: Int)

    @Query("DELETE FROM custom_cronograma_items")
    suspend fun deleteAllCustomItems()
}



