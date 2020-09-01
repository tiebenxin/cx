package net.cb.cb.library.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

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
            if (context != null) {
                verName = context.getPackageManager().
                        getPackageInfo(context.getPackageName(), 0).versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }


    /**
     * 比较新版本
     */
    public static boolean isNewVersion(Context context, String newVerName) {
        boolean isNeed = false;
        try {
            //获取当前版本
            String OldVerName = getVerName(context);
            if (!TextUtils.isEmpty(newVerName) && !TextUtils.isEmpty(OldVerName)) {
                String[] newVer = newVerName.split("\\.");
                String[] oldVer = OldVerName.split("\\.");
                //这里因为服务器和本地版本号的格式一样，所以随便哪个的长度都可以使用
                for (int i = 0; i < newVer.length; i++) {
                    int newNumber = Integer.parseInt(newVer[i]);
                    int oldNumber = Integer.parseInt(oldVer[i]);
                    if (oldNumber > newNumber) {
                        isNeed = false;
                        break;
                    }
                    if (oldNumber < newNumber) {
                        isNeed = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getLog().e("比较版本号时出错");
        }
        return isNeed;
    }


    /**
     * 判断是否为新版本，且为大版本更新（如0.9->1.0 或者 1.0->1.1）
     *
     * @param context
     * @param newVerName
     * @return
     */
    public static boolean isBigVersion(Context context, String newVerName) {
        boolean isBigVersion = false;
        try {
            //获取当前版本
            String OldVerName = getVerName(context);
            //截取出每一位，如1.1.1，再分别比较每一个元素
            if (!TextUtils.isEmpty(newVerName) && !TextUtils.isEmpty(OldVerName)) {
                String[] newVer = newVerName.split("\\.");
                String[] oldVer = OldVerName.split("\\.");
                //这里只判断是否为大版本更新，所以只循环比较前两位元素即可，循环次数-1
                for (int i = 0; i < newVer.length - 1; i++) {
                    int newNumber = Integer.parseInt(newVer[i]);
                    int oldNumber = Integer.parseInt(oldVer[i]);
                    //旧版本号更大，不需要更新
                    if (oldNumber > newNumber) {
                        isBigVersion = false;
                        break;
                    }
                    //旧版本号较小，需要更新
                    if (oldNumber < newNumber) {
                        isBigVersion = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getLog().e("比较版本号时出错");
        }
        return isBigVersion;
    }

    /**
     * 判断当前版本是否低于比较版本newVerName
     *
     * @param context
     * @param newVerName
     * @return true 低于 false 高于
     */
    public static boolean isLowerVersion(Context context, String newVerName) {
        boolean isLower = false;
        try {
            //获取当前版本
            String OldVerName = getVerName(context);
            //截取出每一位，如1.1.1，再分别比较每一个元素
            if (!TextUtils.isEmpty(newVerName) && !TextUtils.isEmpty(OldVerName)) {
                String[] newVer = newVerName.split("\\.");
                String[] oldVer = OldVerName.split("\\.");
                for (int i = 0; i < newVer.length; i++) {
                    int newNumber = Integer.parseInt(newVer[i]);
                    int oldNumber = Integer.parseInt(oldVer[i]);
                    //旧版本号更大，不需要更新
                    if (oldNumber < newNumber) {
                        isLower = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getLog().e("比较版本号时出错");
        }
        return isLower;
    }
}