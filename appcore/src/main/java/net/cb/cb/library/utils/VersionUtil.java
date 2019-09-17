package net.cb.cb.library.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VersionUtil {
    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }


    /**
     * 比较新版本
     */
    public static boolean isNewVersion(Context context, String newVerName) {
        List<String> newVerNames ;
        List<String> OldVerNames ;
        if (TextUtils.isEmpty(newVerName)) {
            return false;
        } else {
            newVerNames = Arrays.asList(newVerName.split("\\."));
        }
        String OldVerName = getVerName(context);
        OldVerNames = Arrays.asList(OldVerName.split("\\."));

        if ((newVerNames == null || newVerNames.size() != 3) || (OldVerNames == null || OldVerNames.size() != 3)) {
            return false;
        } else {
            int newVerNum = Integer.valueOf(newVerNames.get(0)) * 100 + Integer.valueOf(newVerNames.get(1)) * 10 + Integer.valueOf(newVerNames.get(2));
            int oldVerNum = Integer.valueOf(OldVerNames.get(0)) * 100 + Integer.valueOf(OldVerNames.get(1)) * 10 + Integer.valueOf(OldVerNames.get(2));
            if (oldVerNum < newVerNum) {
                return true;
            } else {
                return false;
            }
        }
    }


    public static String getPhoneModel() {
        return android.os.Build.BRAND + " " + android.os.Build.MODEL;
    }

}