package com.Shoshin.app.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.Shoshin.app.data.db.dao.*
import com.Shoshin.app.data.db.entities.*

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
        CheckpointEntity::class,
        NotificationEntity::class,
        RoutineCheckpointEntity::class,
        FeedbackEntity::class
    ],
    version = 15,
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
    abstract fun notificationDao(): NotificationDao
    abstract fun checkpointDao(): CheckpointDao
    abstract fun routineCheckpointDao(): RoutineCheckpointDao
    abstract fun feedbackDao(): FeedbackDao

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
