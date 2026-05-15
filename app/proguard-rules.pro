# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android Studio SDK directory.

# ─── Room Database ───────────────────────────────────────
# Keep Room entities
-keep class com.matrusneh.health.data.db.entity.** { *; }
# Keep Room DAOs
-keep interface com.matrusneh.health.data.db.dao.** { *; }
# Keep Room Database class
-keep class com.matrusneh.health.data.db.** { *; }

# ─── Kotlin ──────────────────────────────────────────────
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings { <fields>; }

# ─── Kotlin Coroutines ───────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ─── WorkManager ─────────────────────────────────────────
-keep class androidx.work.** { *; }
-keep class com.matrusneh.health.worker.** { *; }
-keepclassmembers class * extends androidx.work.Worker { *; }
-keepclassmembers class * extends androidx.work.CoroutineWorker { *; }

# ─── ViewModel ───────────────────────────────────────────
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel { <init>(...); }

# ─── Navigation Component ────────────────────────────────
-keep class androidx.navigation.** { *; }

# ─── Hilt (Dependency Injection) ─────────────────────────
-keepclasseswithmembernames class * { @dagger.hilt.* <methods>; }
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-dontwarn dagger.hilt.**

# ─── General Android ─────────────────────────────────────
-keepattributes *Annotation*
-keepattributes SourceFile, LineNumberTable
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
