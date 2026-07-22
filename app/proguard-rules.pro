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
-keep class com.shoshin.app.data.db.entities.** { *; }
-keep class com.shoshin.app.data.groups.** { *; }

# Compose Rules
-keep class androidx.compose.runtime.** { *; }
-dontwarn androidx.compose.runtime.**

# FileProvider (Ensure metadata is not stripped)
-keep class androidx.core.content.FileProvider { *; }

# General optimizations
-optimizationpasses 5
-allowaccessmodification
-dontpreverify
