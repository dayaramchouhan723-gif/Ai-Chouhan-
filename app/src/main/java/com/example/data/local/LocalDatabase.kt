package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "intruder_snapshots")
data class IntruderRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val triggerType: String,
    val timestamp: Long,
    val localPhotoPath: String?,
    val note: String
)

@Dao
interface IntruderDao {
    @Query("SELECT * FROM intruder_snapshots ORDER BY timestamp DESC")
    fun getAllSnapshots(): Flow<List<IntruderRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(record: IntruderRecord): Long

    @Query("DELETE FROM intruder_snapshots")
    suspend fun clearAll()
}

@Entity(tableName = "triggers")
data class TriggerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val triggerType: String,
    val action: String,
    val isEnabled: Boolean = false,
    val updatedTime: String = "Active"
)

@Dao
interface TriggerDao {
    @Query("SELECT * FROM triggers ORDER BY id ASC")
    fun getAllTriggers(): Flow<List<TriggerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrigger(trigger: TriggerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(triggers: List<TriggerEntity>)

    @Update
    suspend fun updateTrigger(trigger: TriggerEntity)

    @Query("UPDATE triggers SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun updateTriggerEnabled(id: Long, isEnabled: Boolean)

    @Delete
    suspend fun deleteTrigger(trigger: TriggerEntity)

    @Query("DELETE FROM triggers")
    suspend fun clearAll()
}

@Entity(tableName = "world_model")
data class WorldModelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val category: String, // "App", "Contact", "Workflow"
    val title: String,
    val learnedAction: String,
    val updatedAt: String = "Updated 2 days ago"
)

@Dao
interface WorldModelDao {
    @Query("SELECT * FROM world_model ORDER BY id ASC")
    fun getAllItems(): Flow<List<WorldModelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: WorldModelEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WorldModelEntity>)

    @Delete
    suspend fun deleteItem(item: WorldModelEntity)

    @Query("DELETE FROM world_model")
    suspend fun clearAll()
}

@Database(
    entities = [IntruderRecord::class, TriggerEntity::class, WorldModelEntity::class],
    version = 2,
    exportSchema = false
)
abstract class StarDatabase : RoomDatabase() {
    abstract fun intruderDao(): IntruderDao
    abstract fun triggerDao(): TriggerDao
    abstract fun worldModelDao(): WorldModelDao

    companion object {
        @Volatile
        private var INSTANCE: StarDatabase? = null

        fun getInstance(context: Context): StarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StarDatabase::class.java,
                    "chouhan_star_local.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
