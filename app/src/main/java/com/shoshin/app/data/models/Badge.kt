package com.Shoshin.app.data.models

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val category: BadgeCategory,
    val rarity: BadgeRarity,
    val color: String,
    val requirementDescription: String,
    val threshold: Int,
    var unlockedDate: Long? = null,
    var isLocked: Boolean = true,
    var currentProgress: Int = 0
)

enum class BadgeCategory {
    STREAK, MILESTONE, BEHAVIOR, CHALLENGE
}

enum class BadgeRarity {
    COMMON, UNCOMMON, RARE, LEGENDARY
}

object BadgeDefinitions {
    val ALL_BADGES = listOf(
        // Streak Badges — every 15 days (day 7 is the one-time exception)
        Badge("streak_7", "Starter", "You're on your way!", "streak_7", BadgeCategory.STREAK, BadgeRarity.COMMON, "#C0C0C0", "Reach a 7-day streak", 7),
        Badge("streak_15", "Rising", "Momentum building", "streak_15", BadgeCategory.STREAK, BadgeRarity.COMMON, "#B0BEC5", "Reach a 15-day streak", 15),
        Badge("streak_30", "Committed", "One month strong", "streak_30", BadgeCategory.STREAK, BadgeRarity.UNCOMMON, "#FFD700", "Reach a 30-day streak", 30),
        Badge("streak_45", "Focused", "Six weeks in!", "streak_45", BadgeCategory.STREAK, BadgeRarity.UNCOMMON, "#FF9800", "Reach a 45-day streak", 45),
        Badge("streak_60", "Legend", "Two months of brilliance", "streak_60", BadgeCategory.STREAK, BadgeRarity.RARE, "#E91E63", "Reach a 60-day streak", 60),
        Badge("streak_75", "Elite", "75 days unstoppable", "streak_75", BadgeCategory.STREAK, BadgeRarity.RARE, "#9C27B0", "Reach a 75-day streak", 75),
        Badge("streak_90", "Immortal", "Three months legend", "streak_90", BadgeCategory.STREAK, BadgeRarity.LEGENDARY, "#FFC107", "Reach a 90-day streak", 90),
        Badge("streak_105", "Infinite", "Limitless potential", "streak_105", BadgeCategory.STREAK, BadgeRarity.LEGENDARY, "#00BCD4", "Reach a 105-day streak", 105),
        Badge("streak_120", "Transcendent", "4 months of excellence", "streak_120", BadgeCategory.STREAK, BadgeRarity.LEGENDARY, "#3F51B5", "Reach a 120-day streak", 120),
        
        // Milestone Badges
        Badge("milestone_first", "Beginner", "Completed first checkpoint", "milestone", BadgeCategory.MILESTONE, BadgeRarity.COMMON, "#4A7C59", "Complete your first checkpoint", 1),
        Badge("group_creator", "Leader", "Created your first group", "groups", BadgeCategory.MILESTONE, BadgeRarity.COMMON, "#FF6B6B", "Create a group", 1),
        Badge("influencer", "Influencer", "Built a community of 10+ members", "influence", BadgeCategory.MILESTONE, BadgeRarity.UNCOMMON, "#FFC107", "Have 10 members in your group", 10),
        
        // Behavior Badges
        Badge("early_bird", "Early Bird", "The early morning warrior", "sun", BadgeCategory.BEHAVIOR, BadgeRarity.UNCOMMON, "#FF9800", "Complete 5 checkpoints before 7:00 AM", 5),
        Badge("thinker", "Thinker", "Introspection is your strength", "thought", BadgeCategory.BEHAVIOR, BadgeRarity.UNCOMMON, "#9C27B0", "Write 10 reflections", 10),
        Badge("networker", "Networker", "Inspiring others through sharing", "share", BadgeCategory.BEHAVIOR, BadgeRarity.UNCOMMON, "#E91E63", "Share your streak 5 times", 5),
        Badge("team_player", "Team Player", "Part of multiple communities", "community", BadgeCategory.BEHAVIOR, BadgeRarity.UNCOMMON, "#2196F3", "Join 3 different groups", 3)
    )
}
