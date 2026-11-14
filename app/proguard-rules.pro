# ProGuard rules for LunaStore - tối ưu cho build nhẹ

# Keep classes cần thiết
-keep class com.lunastore.app.** { *; }
-keep class androidx.compose.** { *; }
-keep class androidx.lifecycle.** { *; }

# Keep annotations
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses

# OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Okio
-dontwarn okio.**
-keep class okio.** { *; }

# JSON
-keep class org.json.** { *; }

# Compose
-keepclassmembers class androidx.compose.** {
    *;
}

# Reduce size tối đa
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
