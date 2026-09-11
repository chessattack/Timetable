package com.example

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "arrangement_history")
data class ArrangementHistoryEntity(
    @PrimaryKey
    val dateString: String, // e.g. "10/09/2026"
    val sheetDataJson: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface ArrangementHistoryDao {
    @Query("SELECT * FROM arrangement_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<ArrangementHistoryEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: ArrangementHistoryEntity)
}

@Database(entities = [ArrangementHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun arrangementHistoryDao(): ArrangementHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "timetable_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
