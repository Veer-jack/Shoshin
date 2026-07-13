package com.example.shoshinapp.data.db.dao

import androidx.room.*
import com.example.shoshinapp.data.db.entities.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Query("SELECT * FROM photos WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserPhotosFlow(userId: String): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getUserPhotos(userId: String): List<PhotoEntity>

    @Query("SELECT * FROM photos WHERE photoId = :photoId")
    suspend fun getPhoto(photoId: String): PhotoEntity?

    @Update
    suspend fun updatePhoto(photo: PhotoEntity)

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    @Query("SELECT * FROM photos WHERE syncStatus = 'pending' AND userId = :userId")
    suspend fun getPendingPhotos(userId: String): List<PhotoEntity>
}
