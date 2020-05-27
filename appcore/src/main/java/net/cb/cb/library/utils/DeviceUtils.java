package net.cb.cb.library.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import net.cb.cb.library.AppConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Method;

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

    /*
     * deviceId, 设备序列号，imei 都具备唯一性
     * */
    @SuppressLint("HardwareIds")
    public static String getIMEI(Context context) {
        String imei = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    return imei;
                }
                if (!TextUtils.isEmpty(tm.getDeviceId())) {
                    imei = tm.getDeviceId();
                } else {
                    imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < 29) {
                Method method = tm.getClass().getMethod("getImei");
                imei = (String) method.invoke(tm);
                if (TextUtils.isEmpty(imei)) {
                    imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                }
            } else if (Build.VERSION.SDK_INT >= 29) {
                imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(imei)) {
            imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return imei;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    //获取设备名称，及手机型号
    public static String getPhoneModel() {
        return android.os.Build.BRAND + " " + android.os.Build.MODEL + " " + android.os.Build.VERSION.RELEASE;
    }

    //获取设备名称
    public static String getDeviceName() {
        String name = Settings.Global.getString(AppConfig.getContext().getContentResolver(), Settings.Global.DEVICE_NAME);
        if (TextUtils.isEmpty(name) || name.equalsIgnoreCase("unknown")) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter != null) {
                name = mBluetoothAdapter.getName();
            }
            if (TextUtils.isEmpty(name) || name.equalsIgnoreCase("unknown")) {
                name = android.os.Build.MODEL;
            }
        }
        return name;
    }
}
