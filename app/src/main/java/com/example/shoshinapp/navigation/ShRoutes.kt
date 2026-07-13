package com.example.shoshinapp.navigation

object ShRoutes {
    // Onboarding stack (not in bottom nav)
    const val SPLASH          = "splash"
    const val AUTH            = "auth"
    const val OTP_PHONE       = "otp/phone/{phoneNumber}"
    const val OTP_EMAIL       = "otp/email/{email}"
    const val ONBOARDING      = "onboarding/{pageIndex}"
    const val PERMISSIONS     = "permissions"
    const val GOAL_SELECTION  = "goal_selection"
    const val ROUTINE_TEMPLATE = "routine_template/{goalKey}"

    // Main shell (bottom nav tabs)
    const val MAIN            = "main"   // hosts bottom nav scaffold

    // Tab: Home
    const val HOME            = "home"
    const val ALARM_SETUP     = "alarm_setup"
    const val SOUND_PICKER    = "sound_picker"

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

    // Helpers
    fun otpPhone(number: String)  = "otp/phone/$number"
    fun otpEmail(email: String)   = "otp/email/$email"
    fun onboarding(page: Int = 0) = "onboarding/$page"
    fun routineTemplate(goal: String) = "routine_template/$goal"
    fun cameraVerify(idx: Int, label: String) = "morning/camera/$idx/$label"
}
