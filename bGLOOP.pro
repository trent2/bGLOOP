# -libraryjars C:\Program Files\Java\jdk1.8.0_66\jre\lib
-libraryjars /opt/java/jre/lib/rt.jar
-libraryjars ./jogl-2.3.2/gluegen-rt.jar
-libraryjars ./jogl-2.3.2/jogl-all.jar
# -libraryjars ./jogl-2.3.2/gluegen-rt-natives-linux-amd64.jar
# -libraryjars ./jogl-2.3.2/jogl-all-natives-linux-amd64.jar

-target 1.7
-optimizationpasses 2
-mergeinterfacesaggressively
-overloadaggressively
-verbose


-keep public class bGLOOP.* {
    !private !protected public <fields>;
    !private !protected public <methods>;
}

-keep,allowshrinking,allowoptimization,allowobfuscation class bGLOOP.*

# Also keep - Enumerations. Keep the special static methods that are required in
# enumeration classes.
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,includedescriptorclasses,allowshrinking class * {
    native <methods>;
}