package com.example.shoshinapp.navigation

object ShRoutes {
    // Onboarding stack (not in bottom nav)
    const val SPLASH          = "splash"
    const val AUTH            = "auth"
    const val OTP_PHONE       = "otp/phone/{phoneNumber}?code={code}"
    const val OTP_EMAIL       = "otp/email/{email}?pass={pass}&code={code}"
    const val ONBOARDING      = "onboarding" // Simplified as it's now internal state
    const val PERMISSIONS     = "permissions"
    const val GOAL_SELECTION  = "goal_selection"
    const val ROUTINE_TEMPLATE = "routine_template/{goalKey}"

    // Main shell (bottom nav tabs)
    const val MAIN            = "main"   // hosts bottom nav scaffold

    // Tab: Home
    const val HOME            = "home"
    const val ALARM_SETUP     = "alarm_setup"
    const val SOUND_PICKER    = "sound_picker"
    const val STREAK_DETAILS  = "streak_details"

    // Morning flow (launched from FAB or HOME CTA)
    const val ACTIVATION      = "morning/activation"
    const val CAMERA_VERIFY   = "morning/camera/{checkpointIndex}/{checkpointLabel}"
    const val CHECKPOINT      = "morning/checkpoint"
    const val MORNING_COMPLETE = "morning/complete"

    // Tab: Progress
    const val CONSISTENCY     = "progress/consistency"
    const val HISTORY         = "progress/history"
    const val CHALLENGE_21    = "progress/21day"
    const val DISCIPLINE_71   = "progress/71day"

    // Tab: Groups
    const val GROUPS          = "groups"
    const val GROUP_DETAIL    = "group_detail/{groupId}"
    const val CREATE_GROUP    = "create_group"

    // Tab: You (Profile)
    const val PROFILE         = "profile"
    const val EDIT_PROFILE    = "edit_profile"
    const val SETTINGS        = "settings"
    const val ROUTINE_EDITOR  = "routine_editor"
    const val CLOCK           = "clock"
    const val PAYWALL         = "paywall"
    const val PRIVACY         = "legal/privacy"
    const val TERMS           = "legal/terms"
    const val STREAK_SHARE    = "streak_share/{streak}/{habitName}/{startDate}"
    const val BADGES          = "badges"
    const val BADGE_DETAIL    = "badge_detail/{badgeId}"
    const val ALL_FRIENDS     = "friends/all"
    const val FRIEND_PROFILE  = "friend_profile/{userId}"
    const val INVITE          = "invite"
    const val REFERRALS       = "referrals"
    const val STATS           = "stats"

    // Helpers
    fun otpPhone(number: String, code: String? = null)  = "otp/phone/$number" + (if (code != null) "?code=$code" else "")
    fun otpEmail(email: String, pass: String, code: String? = null) = "otp/email/$email?pass=$pass" + (if (code != null) "&code=$code" else "")
    fun onboarding(page: Int = 0) = "onboarding/$page"
    fun routineTemplate(goal: String) = "routine_template/$goal"
    fun cameraVerify(idx: Int, label: String) = "morning/camera/$idx/$label"
    fun streakShare(streak: Int, habit: String, start: Long) = "streak_share/$streak/$habit/$start"
    fun badgeDetail(badgeId: String) = "badge_detail/$badgeId"
    fun friendProfile(userId: String) = "friend_profile/$userId"
}
