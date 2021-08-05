
#   http://developer.android.com/guide/developing/tools/proguard.html

-keepclassmembers class com.subhamgupta.bookkeeper.models.** { *; }


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application


-libraryjars  libs/commons-io-2.2.jar
-libraryjars  libs/gson-2.2.2.jar
-keep public class org.apache.commons.io.**
-keep public class com.google.gson.**
-keep public class com.google.gson.** {public private protected *;}

##---------------Begin: proguard configuration for Gson ----------
-keepattributes *Annotation*,Signature

# To support Enum type of class members
-keepclassmembers enum * { *; }
##---------------End: proguard configuration for Gson ----------
# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile