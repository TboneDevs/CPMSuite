# Add project specific ProGuard rules here.
# Keep Shizuku
-keep class rikka.shizuku.** { *; }
-keep class moe.shizuku.** { *; }
# Keep Room entities
-keep class com.dynogamer.studio.data.local.entity.** { *; }
# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
