package com.example.shoshinapp.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.shoshinapp.data.db.dao.*
import com.example.shoshinapp.data.db.entities.*

@Database(
    entities = [
        UserEntity::class,
        StreakEntity::class,
        ReflectionEntity::class,
        PhotoEntity::class,
        SyncQueueItem::class,
        GroupEntity::class,
        GroupMemberEntity::class,
        GroupPostEntity::class,
        SocialShareEntity::class,
        BadgeEntity::class,
        FriendEntity::class,
        UserLimitsEntity::class,
        CheckpointEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun streakDao(): StreakDao
    abstract fun reflectionDao(): ReflectionDao
    abstract fun photoDao(): PhotoDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun groupDao(): GroupDao
    abstract fun groupMemberDao(): GroupMemberDao
    abstract fun groupPostDao(): GroupPostDao
    abstract fun socialShareDao(): SocialShareDao
    abstract fun badgeDao(): BadgeDao
    abstract fun friendDao(): FriendDao
    abstract fun userLimitsDao(): UserLimitsDao
    abstract fun statsDao(): StatsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shoshin_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
