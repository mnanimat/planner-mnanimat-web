package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        StudySubject::class,
        Flashcard::class,
        Simulado::class,
        VideoAula::class,
        CadernoErro::class,
        Essay::class,
        RitVidaHour::class,
        RitVidaFinance::class,
        RitVidaProject::class,
        StudySchedule::class,
        MeiTransaction::class,
        MeiInvoice::class,
        MeiConfig::class,
        UserAccount::class,
        GymWorkout::class,
        DietLog::class,
        VisualTask::class,
        CustomCronogramaItem::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studySubjectDao(): StudySubjectDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun simuladoDao(): SimuladoDao
    abstract fun videoAulaDao(): VideoAulaDao
    abstract fun cadernoErroDao(): CadernoErroDao
    abstract fun essayDao(): EssayDao
    abstract fun ritVidaHourDao(): RitVidaHourDao
    abstract fun ritVidaFinanceDao(): RitVidaFinanceDao
    abstract fun ritVidaProjectDao(): RitVidaProjectDao
    abstract fun studyScheduleDao(): StudyScheduleDao
    abstract fun meiTransactionDao(): MeiTransactionDao
    abstract fun meiInvoiceDao(): MeiInvoiceDao
    abstract fun meiConfigDao(): MeiConfigDao
    abstract fun userAccountDao(): UserAccountDao
    abstract fun gymWorkoutDao(): GymWorkoutDao
    abstract fun dietLogDao(): DietLogDao
    abstract fun visualTaskDao(): VisualTaskDao
    abstract fun customCronogramaDao(): CustomCronogramaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "focovest_ritvida_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
