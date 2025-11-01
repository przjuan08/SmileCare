# Add any project specific keep options here:

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
    <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Firebase
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Material Components
-keep class com.google.android.material.** { *; }

# CircleImageView
-keep class de.hdodenhof.circleimageview.** { *; }

# SpinKit
-keep class com.github.ybq.android.spinkit.** { *; }

# Keep our models
-keep class sv.edu.udb.smilecare.models.** { *; }

# Keep database classes
-keep class sv.edu.udb.smilecare.database.** { *; }

# Keep utility classes
-keep class sv.edu.udb.smilecare.utils.** { *; }