# ProGuard rules for PlayWitMe

# ─── Kotlin ──────────────────────────────────────────────────────────────────
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**

# ─── Jetpack Compose ─────────────────────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ─── Hilt ────────────────────────────────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# ─── Room ────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# ─── Moshi ───────────────────────────────────────────────────────────────────
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}

# ─── Retrofit ────────────────────────────────────────────────────────────────
-keep class retrofit2.** { *; }
-keepattributes Signature, Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# ─── OkHttp ──────────────────────────────────────────────────────────────────
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# ─── ML Kit ──────────────────────────────────────────────────────────────────
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# ─── Domain Models ───────────────────────────────────────────────────────────
-keep class com.playwithme.assistant.domain.model.** { *; }
-keep class com.playwithme.assistant.data.remote.dto.** { *; }

# ─── Serialization ───────────────────────────────────────────────────────────
-keepattributes *Annotation*
-keep class kotlinx.serialization.** { *; }
-keepclassmembers @kotlinx.serialization.Serializable class * { *; }

# ─── WorkManager ─────────────────────────────────────────────────────────────
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.CoroutineWorker

# ─── General ─────────────────────────────────────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
