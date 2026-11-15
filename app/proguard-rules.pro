# CHOUHAN STAR v2.2.8 ProGuard / R8 Keep Rules

-keepattributes Signature,InnerClasses,EnclosingMethod,SourceFile,LineNumberTable
-keepattributes *Annotation*

# Kotlin Coroutines & Serialization
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# Room Database Entities & DAOs
-keep class com.example.data.local.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Jetpack Compose & ViewModels
-keep class com.example.viewmodel.** { *; }
-keep class com.example.data.model.** { *; }

# Firebase Auth, Firestore, AI Logic & App Check
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Jetpack Credential Manager & Google Identity
-keep class androidx.credentials.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }

# Networking: OkHttp, Retrofit, Moshi
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep class com.example.service.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
