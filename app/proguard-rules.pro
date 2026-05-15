# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android Studio SDK directory.

# Keep Room entities
-keep class com.matrusneh.health.data.db.entity.** { *; }

# Keep Hilt generated classes
-keepclasseswithmembernames class * { @dagger.hilt.* <methods>; }
