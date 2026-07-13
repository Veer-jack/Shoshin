# ProGuard rules for Shoshin App

# Firebase Rules
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Room Rules
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Dao
-keep class * { @androidx.room.Entity *; }
-dontwarn androidx.room.**

# Shoshin Data Models (Prevent obfuscation of Firestore/Room entities)
-keep class com.example.shoshinapp.data.db.entities.** { *; }
-keep class com.example.shoshinapp.data.groups.** { *; }

# Compose Rules
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.runtime.**

# General optimizations
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
