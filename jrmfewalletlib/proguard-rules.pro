# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\ylcf\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify


-keepattributes *Annotation*
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

#钱包
-keepclassmembers class com.jrmf360.ewalletlib.adapter.**{*;}

-keepclassmembers class com.jrmf360.ewalletlib.fragment.**{
        <methods>;
}

#model
#-keep class com.jrmf360.ewalletlib.http.model.**{*;}
-keepclassmembers class * extends com.jrmf360.ewalletlib.http.model.BaseModel{*;}
-keepclassmembers class com.jrmf360.ewalletlib.http.model.AccountModel{*;}
-keepclassmembers class com.jrmf360.ewalletlib.http.model.BankCardListModel{*;}
-keep class com.jrmf360.ewalletlib.http.model.TradeItemDetail{*;}
-keep class com.jrmf360.ewalletlib.http.model.BaseModel{*;}


-keep class com.jrmf360.ewalletlib.http.HttpManager{
    public static void init(android.content.Context);
}
-keep class com.jrmf360.ewalletlib.http.HttpManager$*{*;}

-keep class com.jrmf360.ewalletlib.ui.BaseActivity{*;}
-keep class com.jrmf360.ewalletlib.ui.**{
    public int getLayoutId();
    public int initView();
    protected void onResume();
    public int initListener();
    public int onClick(int);
    public int onClick(android.view.View);
    public void initData(android.os.Bundle);
    #listview滚动方法
    public void onScrollStateChanged(android.widget.AbsListView,int);
    public void onScroll(android.widget.AbsListView, int, int, int);
    #viewpager的页面滑动方法
    public void onPageScrollStateChanged(int);
    public void onPageScrolled(int,float,int);
    public void onPageSelected(int);

    protected void onStart();
    protected void onActivityResult(int, int, android.content.Intent);
    public void onBackPressed();

}
-keep class com.jrmf360.ewalletlib.ui.**$*{*;}

-keep class com.jrmf360.ewalletlib.webview.**{*;}

-keep class com.jrmf360.ewalletlib.widget.**{*;}

-keep class com.jrmf360.ewalletlib.JrmfEWalletClient{*;}

-keep class com.jrmf360.ewalletlib.JrmfEWalletClient$* {
    *;
}

-ignorewarnings