package com.example.shoshinapp.data.db.dao

import androidx.room.*
import com.example.shoshinapp.data.db.entities.SyncQueueItem

@Dao
interface SyncQueueDao {
    @Insert
    suspend fun insertItem(item: SyncQueueItem)

    @Query("SELECT * FROM sync_queue WHERE userId = :userId ORDER BY timestamp ASC")
    suspend fun getPendingItems(userId: String): List<SyncQueueItem>

    @Delete
    suspend fun deleteItem(item: SyncQueueItem)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteItemById(id: String)

    @Query("UPDATE sync_queue SET retryCount = retryCount + 1 WHERE id = :id")
    suspend fun incrementRetry(id: String)
}
