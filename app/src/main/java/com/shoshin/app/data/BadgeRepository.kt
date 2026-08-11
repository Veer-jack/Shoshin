package com.Shoshin.app.data

import com.Shoshin.app.data.db.dao.BadgeDao
import com.Shoshin.app.data.db.entities.BadgeEntity
import com.Shoshin.app.data.db.entities.toBadgeIdList
import com.Shoshin.app.data.db.entities.toBadgeIdString
import com.Shoshin.app.data.models.Badge
import com.Shoshin.app.data.models.BadgeCategory
import com.Shoshin.app.data.models.BadgeDefinitions
import com.Shoshin.app.data.models.BadgeRarity
import com.Shoshin.app.data.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BadgeRepository(
    private val badgeDao: BadgeDao,
    private val userRepository: UserRepository
) {

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

        val user = userRepository.getUser(userId) ?: return
        val currentIds = user.unlockedBadgeIds.toBadgeIdList()
        if (badgeId !in currentIds) {
            userRepository.updateUser(user.copy(unlockedBadgeIds = (currentIds + badgeId).toBadgeIdString()))
        }
    }
}
