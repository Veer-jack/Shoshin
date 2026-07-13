package com.example.shoshinapp.data

import com.example.shoshinapp.data.db.dao.BadgeDao
import com.example.shoshinapp.data.db.entities.BadgeEntity
import com.example.shoshinapp.data.models.Badge
import com.example.shoshinapp.data.models.BadgeCategory
import com.example.shoshinapp.data.models.BadgeDefinitions
import com.example.shoshinapp.data.models.BadgeRarity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BadgeRepository(private val badgeDao: BadgeDao) {

    fun getBadgesForUser(userId: String): Flow<List<Badge>> {
        return badgeDao.getBadgesForUser(userId).map { entities ->
            val entityMap = entities.associateBy { it.badgeId }
            BadgeDefinitions.ALL_BADGES.map { definition ->
                val entity = entityMap[definition.id]
                definition.copy(
                    unlockedDate = entity?.unlockedDate?.takeIf { it > 0 },
                    isLocked = entity?.isLocked ?: true,
                    currentProgress = entity?.progress ?: 0
                )
            }
        }
    }

    suspend fun updateProgress(userId: String, badgeId: String, progress: Int) {
        val definition = BadgeDefinitions.ALL_BADGES.find { it.id == badgeId } ?: return
        val existing = badgeDao.getBadge(userId, badgeId)
        
        if (existing == null) {
            badgeDao.insertOrUpdateBadge(BadgeEntity(userId, badgeId, progress = progress))
        } else {
            badgeDao.updateProgress(userId, badgeId, progress)
        }

        // Auto unlock if threshold reached
        if (progress >= definition.threshold && (existing?.isLocked != false)) {
            unlockBadge(userId, badgeId)
        }
    }

    suspend fun unlockBadge(userId: String, badgeId: String) {
        val now = System.currentTimeMillis()
        val existing = badgeDao.getBadge(userId, badgeId)
        if (existing == null) {
            badgeDao.insertOrUpdateBadge(BadgeEntity(userId, badgeId, unlockedDate = now, isLocked = false))
        } else {
            badgeDao.unlockBadge(userId, badgeId, now)
        }
    }
}
