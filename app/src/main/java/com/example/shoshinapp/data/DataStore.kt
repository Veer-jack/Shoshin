package com.example.shoshinapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "shoshin_prefs")

object PrefsKeys {
    val USER_NAME     = stringPreferencesKey("user_name")
    val USER_EMAIL    = stringPreferencesKey("user_email")
    val USER_PHONE    = stringPreferencesKey("user_phone")
    val IS_LOGGED_IN  = booleanPreferencesKey("is_logged_in")
    val TEMPLATE      = stringPreferencesKey("template")
    val STREAK_COUNT  = intPreferencesKey("streak_count")
    val STREAK_DATE   = stringPreferencesKey("streak_date")
    val ALARM_HOUR    = intPreferencesKey("alarm_hour")
    val ALARM_MINUTE  = intPreferencesKey("alarm_minute")
    val ALARM_TONE    = stringPreferencesKey("alarm_tone")
    val ALARM_INTENSITY = intPreferencesKey("alarm_intensity") // 1-10
    val ALARM_SET     = booleanPreferencesKey("alarm_set")
    val ONBOARDING    = booleanPreferencesKey("onboarding_done")
}

class ShoshinRepository(private val context: Context) {

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[PrefsKeys.IS_LOGGED_IN] ?: false }

    val userName: Flow<String> = context.dataStore.data
        .map { it[PrefsKeys.USER_NAME] ?: "" }

    val template: Flow<String> = context.dataStore.data
        .map { it[PrefsKeys.TEMPLATE] ?: "walk" }

    val streakCount: Flow<Int> = context.dataStore.data
        .map { it[PrefsKeys.STREAK_COUNT] ?: 0 }

    val alarmHour: Flow<Int> = context.dataStore.data
        .map { it[PrefsKeys.ALARM_HOUR] ?: 5 }

    val alarmMinute: Flow<Int> = context.dataStore.data
        .map { it[PrefsKeys.ALARM_MINUTE] ?: 30 }

    val alarmTone: Flow<String> = context.dataStore.data
        .map { it[PrefsKeys.ALARM_TONE] ?: "Standard" }

    val alarmIntensity: Flow<Int> = context.dataStore.data
        .map { it[PrefsKeys.ALARM_INTENSITY] ?: 7 }

    val onboardingDone: Flow<Boolean> = context.dataStore.data
        .map { it[PrefsKeys.ONBOARDING] ?: false }

    suspend fun saveUser(name: String, email: String = "", phone: String = "") {
        context.dataStore.edit { prefs ->
            prefs[PrefsKeys.USER_NAME]    = name
            prefs[PrefsKeys.USER_EMAIL]   = email
            prefs[PrefsKeys.USER_PHONE]   = phone
            prefs[PrefsKeys.IS_LOGGED_IN] = true
        }
    }

    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun saveTemplate(template: String) {
        context.dataStore.edit { it[PrefsKeys.TEMPLATE] = template }
    }

    suspend fun saveAlarm(hour: Int, minute: Int) {
        context.dataStore.edit { prefs ->
            prefs[PrefsKeys.ALARM_HOUR]   = hour
            prefs[PrefsKeys.ALARM_MINUTE] = minute
            prefs[PrefsKeys.ALARM_SET]    = true
        }
    }

    suspend fun saveAlarmSettings(tone: String, intensity: Int) {
        context.dataStore.edit { prefs ->
            prefs[PrefsKeys.ALARM_TONE] = tone
            prefs[PrefsKeys.ALARM_INTENSITY] = intensity
        }
    }

    suspend fun recordActivation() {
        val today = java.time.LocalDate.now().toString()
        context.dataStore.edit { prefs ->
            val lastDate  = prefs[PrefsKeys.STREAK_DATE] ?: ""
            val yesterday = java.time.LocalDate.now().minusDays(1).toString()
            val current   = prefs[PrefsKeys.STREAK_COUNT] ?: 0
            prefs[PrefsKeys.STREAK_COUNT] = when (lastDate) {
                yesterday -> current + 1
                today     -> current
                else      -> 1
            }
            prefs[PrefsKeys.STREAK_DATE] = today
        }
    }

    suspend fun completeOnboarding() {
        context.dataStore.edit { it[PrefsKeys.ONBOARDING] = true }
    }
}
