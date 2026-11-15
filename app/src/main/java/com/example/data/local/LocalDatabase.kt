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

    @Query("SELECT * FROM triggers ORDER BY id ASC")
    suspend fun getAllTriggersOnce(): List<TriggerEntity>

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
    val category: String,
    val title: String,
    val learnedAction: String,
    val updatedAt: String = "Updated 2 days ago"
)

@Dao
interface WorldModelDao {
    @Query("SELECT * FROM world_model ORDER BY id ASC")
    fun getAllItems(): Flow<List<WorldModelEntity>>

    @Query("SELECT * FROM world_model ORDER BY id ASC")
    suspend fun getAllItemsOnce(): List<WorldModelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: WorldModelEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WorldModelEntity>)

    @Delete
    suspend fun deleteItem(item: WorldModelEntity)

    @Query("DELETE FROM world_model")
    suspend fun clearAll()
}

@Entity(tableName = "memory_items")
data class MemoryNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val itemType: String, // NOTE, TODO, REMINDER, BIRTHDAY, CHAT
    val title: String,
    val content: String,
    val agentId: String = "zara",
    val dueDate: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memory_items ORDER BY createdAt DESC")
    fun getAllMemoryItems(): Flow<List<MemoryNoteEntity>>

    @Query("SELECT * FROM memory_items WHERE itemType = :type ORDER BY createdAt DESC LIMIT 50")
    fun getMemoryByType(type: String): Flow<List<MemoryNoteEntity>>

    @Query("SELECT * FROM memory_items ORDER BY createdAt DESC LIMIT 100")
    suspend fun getAllMemoryOnce(): List<MemoryNoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(item: MemoryNoteEntity): Long

    @Query("UPDATE memory_items SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean)

    @Query("DELETE FROM memory_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM memory_items")
    suspend fun clearAll()
}

@Entity(tableName = "vault_items")
data class VaultItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val folder: String, // Photos, Notes, Prompts
    val title: String,
    val encryptedPayload: String,
    val mediaUri: String = "",
    val isDecoy: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface VaultDao {
    @Query("SELECT * FROM vault_items ORDER BY createdAt DESC")
    fun getAllVaultItems(): Flow<List<VaultItemEntity>>

    @Query("SELECT * FROM vault_items ORDER BY createdAt DESC")
    suspend fun getAllVaultItemsOnce(): List<VaultItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultItem(item: VaultItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVaultItems(items: List<VaultItemEntity>)

    @Query("DELETE FROM vault_items WHERE id = :id")
    suspend fun deleteVaultItemById(id: Long)

    @Query("DELETE FROM vault_items")
    suspend fun clearAll()
}

@Database(
    entities = [
        IntruderRecord::class,
        TriggerEntity::class,
        WorldModelEntity::class,
        MemoryNoteEntity::class,
        VaultItemEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class StarDatabase : RoomDatabase() {
    abstract fun intruderDao(): IntruderDao
    abstract fun triggerDao(): TriggerDao
    abstract fun worldModelDao(): WorldModelDao
    abstract fun memoryDao(): MemoryDao
    abstract fun vaultDao(): VaultDao

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
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
