package net.cb.cb.library.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * @author Liszt
 * @date 2019/11/8
 * Description
 */
public class DeviceUtils {
    public static String TAG = DeviceUtils.class.getSimpleName();

    public static String HUA_WEI = "Huawei";
    public static String XIAO_MI = "Xiaomi";
    public static String VIVO = "Vivo";
    public static String OPPO = "Oppo";
    public static String SAMSUNG = "Samsung";


    /**
     * 手机型号
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获取设备厂商
     * <p>如Xiaomi</p>
     *
     * @return 设备厂商
     */

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 手机品牌
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    public static boolean isViVoAndOppo() {

        LogUtil.getLog().d("a=", TAG + "--手机品牌名=" + getBrand());
        if (getBrand().equals(VIVO) || getBrand().equals(OPPO)) {
            return true;
        }
        return false;
    }

    //获取运行内存大小,GB
    public static int getTotalRam() {
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (firstLine != null) {
            totalRam = (int) Math.ceil((new Float(Float.valueOf(firstLine) / (1024 * 1024)).doubleValue()));
        }
        LogUtil.getLog().d("a=", TAG + "--运行内存--" + totalRam + "GB");
        return totalRam;
    }


    //获取设备ID
    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        final TelephonyManager TelephonyMgr = (TelephonyManager) context
                .getSystemService(TELEPHONY_SERVICE);
        String id;
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            assert TelephonyMgr != null;
            if (!TextUtils.isEmpty(TelephonyMgr.getDeviceId())) {
                id = TelephonyMgr.getDeviceId();
            } else {
                id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            LogUtil.getLog().d("a=", TAG + "--DeviceId:" + id);
            return id;

        } else {
            assert TelephonyMgr != null;
            if (!TextUtils.isEmpty(TelephonyMgr.getDeviceId())) {
                id = TelephonyMgr.getDeviceId();
            } else {
                id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            LogUtil.getLog().d("a=", TAG + "--DeviceId:" + id);

            return id;
        }


    }

}
