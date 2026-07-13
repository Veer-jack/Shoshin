package com.example.shoshinapp.data.models

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
        // Streak Badges
        Badge("streak_7", "Starter", "Built a 7-day streak", "🥈", BadgeCategory.STREAK, BadgeRarity.COMMON, "#C0C0C0", "Reach a 7-day streak", 7),
        Badge("streak_30", "Committed", "Maintained consistency for a month", "🥇", BadgeCategory.STREAK, BadgeRarity.UNCOMMON, "#FFD700", "Reach a 30-day streak", 30),
        Badge("streak_100", "Legend", "Achieved 100 days of consistency", "👑", BadgeCategory.STREAK, BadgeRarity.RARE, "#E91E63", "Reach a 100-day streak", 100),
        Badge("streak_365", "Immortal", "One year of unstoppable commitment", "⭐", BadgeCategory.STREAK, BadgeRarity.LEGENDARY, "#FFC107", "Reach a 365-day streak", 365),
        
        // Milestone Badges
        Badge("milestone_first", "Beginner", "Completed first checkpoint", "🎯", BadgeCategory.MILESTONE, BadgeRarity.COMMON, "#4A7C59", "Complete your first checkpoint", 1),
        Badge("group_creator", "Leader", "Created your first group", "👥", BadgeCategory.MILESTONE, BadgeRarity.COMMON, "#FF6B6B", "Create a group", 1),
        Badge("influencer", "Influencer", "Built a community of 10+ members", "🌟", BadgeCategory.MILESTONE, BadgeRarity.UNCOMMON, "#FFC107", "Have 10 members in your group", 10),
        
        // Behavior Badges
        Badge("early_bird", "Early Bird", "The early morning warrior", "🌅", BadgeCategory.BEHAVIOR, BadgeRarity.UNCOMMON, "#FF9800", "Complete 5 checkpoints before 7:00 AM", 5),
        Badge("thinker", "Thinker", "Introspection is your strength", "💭", BadgeCategory.BEHAVIOR, BadgeRarity.UNCOMMON, "#9C27B0", "Write 10 reflections", 10),
        Badge("networker", "Networker", "Inspiring others through sharing", "🦋", BadgeCategory.BEHAVIOR, BadgeRarity.UNCOMMON, "#E91E63", "Share your streak 5 times", 5),
        Badge("team_player", "Team Player", "Part of multiple communities", "🤝", BadgeCategory.BEHAVIOR, BadgeRarity.UNCOMMON, "#2196F3", "Join 3 different groups", 3)
    )
}
