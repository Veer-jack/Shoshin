package com.example.shoshinapp.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.util.*

object AnalyticsManager {
    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun initialize(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        Log.d("Analytics", "Analytics Manager Initialized")
    }

    private fun logEvent(eventName: String, params: Bundle = Bundle()) {
        try {
            params.putLong("timestamp", System.currentTimeMillis())
            firebaseAnalytics?.logEvent(eventName, params)
            Log.d("Analytics", "Event: $eventName, Params: $params")
        } catch (e: Exception) {
            Log.e("Analytics", "Error logging event: $eventName", e)
        }
    }

    fun setUserProperties(userType: String, signupMethod: String, hasReferral: Boolean) {
        firebaseAnalytics?.apply {
            setUserProperty("user_type", userType)
            setUserProperty("signup_method", signupMethod)
            setUserProperty("has_referral_code", hasReferral.toString())
            setUserProperty("app_version", "1.0.1")
        }
    }

    // --- Auth Events ---
    fun logAuthMethodSelected(method: String) {
        val bundle = Bundle().apply { putString("method", method) }
        logEvent("auth_method_selected", bundle)
    }

    fun logSignupCompleted(method: String, hadReferral: Boolean) {
        val bundle = Bundle().apply {
            putString("method", method)
            putBoolean("had_referral_code", hadReferral)
        }
        logEvent("auth_signup_completed", bundle)
    }

    fun logLoginCompleted(method: String) {
        val bundle = Bundle().apply { putString("method", method) }
        logEvent("auth_login_completed", bundle)
    }

    // --- Checkpoint Events ---
    fun logCheckpointStarted(userType: String, streak: Int) {
        val calendar = Calendar.getInstance()
        val bundle = Bundle().apply {
            putString("user_type", userType)
            putInt("current_streak", streak)
            putInt("hour_of_day", calendar.get(Calendar.HOUR_OF_DAY))
            putString("day_of_week", getDayOfWeekName(calendar.get(Calendar.DAY_OF_WEEK)))
        }
        logEvent("checkpoint_started", bundle)
    }

    fun logCheckpointCompleted(userType: String, streak: Int, hadPhoto: Boolean, timeSeconds: Int) {
        val calendar = Calendar.getInstance()
        val bundle = Bundle().apply {
            putString("user_type", userType)
            putInt("current_streak", streak)
            putBoolean("had_photo", hadPhoto)
            putInt("hour_of_day", calendar.get(Calendar.HOUR_OF_DAY))
            putString("day_of_week", getDayOfWeekName(calendar.get(Calendar.DAY_OF_WEEK)))
            putInt("completion_time_seconds", timeSeconds)
        }
        logEvent("checkpoint_completed", bundle)
    }

    // --- Streak Events ---
    fun logStreakUpdated(newStreak: Int, previousStreak: Int, userType: String) {
        val bundle = Bundle().apply {
            putInt("new_streak", newStreak)
            putInt("previous_streak", previousStreak)
            putString("user_type", userType)
        }
        logEvent("streak_updated", bundle)
    }

    fun logMilestoneReached(milestone: Int, userType: String) {
        val bundle = Bundle().apply {
            putInt("milestone", milestone)
            putString("user_type", userType)
        }
        logEvent("streak_milestone_reached", bundle)
    }

    fun logStreakFreezeUsed(streakPreserved: Int) {
        val bundle = Bundle().apply {
            putInt("streak_preserved", streakPreserved)
        }
        logEvent("streak_freeze_used", bundle)
    }

    // --- Group Events ---
    fun logGroupJoined(userType: String, groupSize: Int, joinedCount: Int) {
        val bundle = Bundle().apply {
            putString("user_type", userType)
            putInt("group_size", groupSize)
            putInt("groups_joined_count", joinedCount)
        }
        logEvent("group_joined", bundle)
    }

    // --- Referral Events ---
    fun logReferralShared(platform: String) {
        val bundle = Bundle().apply { putString("platform", platform) }
        logEvent("referral_code_shared", bundle)
    }

    // --- Location Events ---
    fun logLocationCaptured(lat: Double, lng: Double, type: String) {
        val bundle = Bundle().apply {
            putDouble("latitude", Math.round(lat * 100.0) / 100.0)
            putDouble("longitude", Math.round(lng * 100.0) / 100.0)
            putString("location_type", type)
        }
        logEvent("location_captured", bundle)
    }

    // --- Retention Events ---
    fun logAppOpened(daysSinceSignup: Int, daysSinceLastOpen: Int, streak: Int, userType: String) {
        val calendar = Calendar.getInstance()
        val bundle = Bundle().apply {
            putInt("days_since_signup", daysSinceSignup)
            putInt("days_since_last_open", daysSinceLastOpen)
            putInt("current_streak", streak)
            putString("user_type", userType)
            putInt("hour_of_day", calendar.get(Calendar.HOUR_OF_DAY))
            putString("day_of_week", getDayOfWeekName(calendar.get(Calendar.DAY_OF_WEEK)))
        }
        logEvent("app_opened", bundle)
    }

    // --- Share Events ---
    fun logShareCompleted(platform: String, streak: Int, type: String) {
        val bundle = Bundle().apply {
            putString("platform", platform)
            putInt("streak", streak)
            putString("share_type", type)
        }
        logEvent("share_completed", bundle)
    }

    // --- Badge Events ---
    fun logBadgeUnlocked(badgeId: String, badgeName: String, category: String, userType: String) {
        val bundle = Bundle().apply {
            putString("badge_id", badgeId)
            putString("badge_name", badgeName)
            putString("badge_category", category)
            putString("user_type", userType)
        }
        logEvent("badge_unlocked", bundle)
    }

    private fun getDayOfWeekName(day: Int): String {
        return when (day) {
            Calendar.MONDAY -> "monday"
            Calendar.TUESDAY -> "tuesday"
            Calendar.WEDNESDAY -> "wednesday"
            Calendar.THURSDAY -> "thursday"
            Calendar.FRIDAY -> "friday"
            Calendar.SATURDAY -> "saturday"
            Calendar.SUNDAY -> "sunday"
            else -> "unknown"
        }
    }
}
