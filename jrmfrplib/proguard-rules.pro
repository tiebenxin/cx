# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
#
# Starting with version 2.2 of the Android plugin for Gradle, these files are no longer used. Newer
# versions are distributed with the plugin and unpacked at build time. Files in this directory are
# no longer maintained.

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class ** extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}


# webView处理
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

-keepattributes Exceptions,InnerClasses

-keepattributes Signature

-keepattributes *Annotation*

# Gson
-keep class com.google.gson.** { *; }

# alipay
-keep class com.alipay.** {*;}
-keep class ta.utdid2.**{*;}
-keep class ut.device.**{*;}
-keep class org.json.alipay.**{*;}

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}

# 保留继承的
-keep public class * extends android.support.v4.**

#红包

-keepclassmembers class com.jrmf360.rplib.adapter.**{*;}

-keep class com.jrmf360.rplib.bean.EnvelopeBean{*;}

-keep class com.jrmf360.rplib.bean.TransAccountBean{*;}

-keep class com.jrmf360.rplib.bean.GrabRpBean{*;}

-keepclassmembers class com.jrmf360.rplib.fragment.**{
    <methods>;
}

-keep class com.jrmf360.rplib.http.HttpManager$*{*;}
-keep class com.jrmf360.rplib.http.HttpManager{
    public static void init(android.content.Context);
}


#新添加model混淆
-keepclassmembers class * extends com.jrmf360.rplib.http.model.BaseModel{*;}
-keep class com.jrmf360.rplib.http.model.TradeItemDetail{*;}
-keep class com.jrmf360.rplib.http.model.SendRpItemModel{*;}
-keep class com.jrmf360.rplib.http.model.RpInfoModel$* {*;}
-keep class com.jrmf360.rplib.http.model.BaseModel{*;}



-keep class com.jrmf360.rplib.manager.JsonManager{
    *;
}

-keep class com.jrmf360.rplib.ui.BaseActivity{*;}

-keep class com.jrmf360.rplib.ui.TradeHistoryActivity{*;}

-keep class com.jrmf360.rplib.ui.**{
    public int getLayoutId();
    public int initView();
    public int initListener();
    public int onClick(int);
    public int onClick(android.view.View);
    public void initData(android.os.Bundle);
    #listview 的滚动
    public void onScrollStateChanged(android.widget.AbsListView,int);
    public void onScroll(android.widget.AbsListView, int, int, int);

    #LeftAndRightDialogFragment
    public void onLeft();
    public void onRight();

    public void notifyContext(java.lang.Object);

    protected void onStart();
    protected void onDestroy();
    protected void onResume();
    protected void onNewIntent(android.content.Intent);
    protected void onActivityResult(int, int, android.content.Intent);
    public void onBackPressed();
}

-keep class com.jrmf360.rplib.ui.**$*{*;}

-keep class com.jrmf360.rplib.widget.**{*;}

-keep class com.jrmf360.rplib.utils.callback.**{*;}

-keepclassmembers class com.jrmf360.rplib.utils.RotateAnimation{
    <methods>;
}

-keep class com.jrmf360.rplib.JrmfRpClient{*;}

-keep class com.jrmf360.rplib.JrmfRpClient$* {
    *;
}

-ignorewarnings