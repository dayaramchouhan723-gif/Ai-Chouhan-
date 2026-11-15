package com.example.service

import android.content.Context
import com.example.data.datastore.StarPreferencesDataStore
import com.example.data.local.MemoryNoteEntity
import com.example.data.local.StarDatabase
import com.example.data.local.TriggerEntity
import com.example.data.local.VaultItemEntity
import com.example.data.local.WorldModelEntity
import com.example.data.model.SanamChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Local Sovereign Memory & JSON Backup Engine.
 * Zero server uploads unless user explicitly activates cloud sync.
 * Privacy enforced: Contacts only for Call, no background recording, camera/awareness OFF default.
 */
class MemoryService(
    private val context: Context,
    private val database: StarDatabase,
    private val dataStore: StarPreferencesDataStore
) {
    val allMemoryFlow: Flow<List<MemoryNoteEntity>> = database.memoryDao().getAllMemoryItems()
    val allVaultFlow: Flow<List<VaultItemEntity>> = database.vaultDao().getAllVaultItems()

    suspend fun saveNote(type: String, title: String, content: String, dueDate: String = ""): Long {
        return database.memoryDao().insertMemory(
            MemoryNoteEntity(
                itemType = type,
                title = title,
                content = content,
                dueDate = dueDate
            )
        )
    }

    suspend fun toggleTodoCompleted(id: Long, completed: Boolean) {
        database.memoryDao().setCompleted(id, completed)
    }

    suspend fun deleteMemoryItem(id: Long) {
        database.memoryDao().deleteById(id)
    }

    suspend fun persistChatMessage(msg: SanamChatMessage) {
        database.memoryDao().insertMemory(
            MemoryNoteEntity(
                itemType = "CHAT",
                title = msg.senderName,
                content = msg.text,
                agentId = msg.agentId,
                dueDate = msg.timestamp,
                isCompleted = msg.isUser
            )
        )
    }

    suspend fun loadRecentChats(limit: Int = 50): List<SanamChatMessage> {
        return database.memoryDao().getAllMemoryOnce()
            .filter { it.itemType == "CHAT" }
            .take(limit)
            .reversed()
            .map { entity ->
                SanamChatMessage(
                    id = "db_${entity.id}",
                    agentId = entity.agentId,
                    senderName = entity.title,
                    text = entity.content,
                    timestamp = entity.dueDate.ifBlank { "Saved" },
                    isUser = entity.isCompleted
                )
            }
    }

    suspend fun exportBackupJson(customAppName: String, ownerLikes: String): String {
        val memories = database.memoryDao().getAllMemoryOnce()
        val triggers = database.triggerDao().getAllTriggersOnce()
        val worldItems = database.worldModelDao().getAllItemsOnce()
        val vaultItems = database.vaultDao().getAllVaultItemsOnce()

        val root = JSONObject().apply {
            put("app", "CHOUHAN STAR")
            put("version", "2.2.8")
            put("owner", "Dayaram Chouhan")
            put("customAppName", customAppName)
            put("ownerLikes", ownerLikes)
            put("exportedAt", System.currentTimeMillis())

            put("memoryItems", JSONArray().apply {
                memories.forEach { m ->
                    put(JSONObject().apply {
                        put("type", m.itemType)
                        put("title", m.title)
                        put("content", m.content)
                        put("agentId", m.agentId)
                        put("dueDate", m.dueDate)
                        put("isCompleted", m.isCompleted)
                        put("createdAt", m.createdAt)
                    })
                }
            })

            put("triggers", JSONArray().apply {
                triggers.forEach { t ->
                    put(JSONObject().apply {
                        put("title", t.title)
                        put("type", t.triggerType)
                        put("action", t.action)
                        put("enabled", t.isEnabled)
                    })
                }
            })

            put("worldModel", JSONArray().apply {
                worldItems.forEach { w ->
                    put(JSONObject().apply {
                        put("category", w.category)
                        put("title", w.title)
                        put("learnedAction", w.learnedAction)
                    })
                }
            })

            put("vaultMetadataCount", vaultItems.size)
        }

        val jsonText = root.toString(2)
        try {
            val backupDir = File(context.getExternalFilesDir(null), "CHOUHAN_STAR_Backups")
            if (!backupDir.exists()) backupDir.mkdirs()
            val outFile = File(backupDir, "chouhan_star_backup_latest.json")
            outFile.writeText(jsonText)
        } catch (_: Exception) {
        }
        return jsonText
    }

    suspend fun importBackupJson(jsonText: String): Int {
        val root = JSONObject(jsonText)
        var restoredCount = 0

        root.optString("customAppName").takeIf { it.isNotBlank() }?.let {
            dataStore.setCustomAppName(it)
        }
        root.optString("ownerLikes").takeIf { it.isNotBlank() }?.let {
            dataStore.setOwnerLikes(it)
        }

        val memArray = root.optJSONArray("memoryItems")
        if (memArray != null) {
            for (i in 0 until memArray.length()) {
                val obj = memArray.optJSONObject(i) ?: continue
                database.memoryDao().insertMemory(
                    MemoryNoteEntity(
                        itemType = obj.optString("type", "NOTE"),
                        title = obj.optString("title", "Restored"),
                        content = obj.optString("content", ""),
                        agentId = obj.optString("agentId", "zara"),
                        dueDate = obj.optString("dueDate", ""),
                        isCompleted = obj.optBoolean("isCompleted", false),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
                restoredCount++
            }
        }

        val worldArray = root.optJSONArray("worldModel")
        if (worldArray != null && worldArray.length() > 0) {
            val items = (0 until worldArray.length()).mapNotNull { idx ->
                val obj = worldArray.optJSONObject(idx) ?: return@mapNotNull null
                WorldModelEntity(
                    category = obj.optString("category", "Preference"),
                    title = obj.optString("title", "Restored"),
                    learnedAction = obj.optString("learnedAction", "Synced")
                )
            }
            database.worldModelDao().insertAll(items)
            restoredCount += items.size
        }

        val trigArray = root.optJSONArray("triggers")
        if (trigArray != null && trigArray.length() > 0 && database.triggerDao().getAllTriggers().first().isEmpty()) {
            val items = (0 until trigArray.length()).mapNotNull { idx ->
                val obj = trigArray.optJSONObject(idx) ?: return@mapNotNull null
                TriggerEntity(
                    title = obj.optString("title", "Rule"),
                    triggerType = obj.optString("type", "VOICE"),
                    action = obj.optString("action", "Run"),
                    isEnabled = obj.optBoolean("enabled", false)
                )
            }
            database.triggerDao().insertAll(items)
        }

        return restoredCount
    }

    suspend fun clearMemoryOnly() {
        database.memoryDao().clearAll()
        database.worldModelDao().clearAll()
    }

    suspend fun deleteAllUserData() {
        database.memoryDao().clearAll()
        database.worldModelDao().clearAll()
        database.intruderDao().clearAll()
        database.vaultDao().clearAll()
        dataStore.clearPersonalPreferences()
    }
}
