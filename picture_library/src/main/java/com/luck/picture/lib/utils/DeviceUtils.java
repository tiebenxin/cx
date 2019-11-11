package com.luck.picture.lib.utils;

import android.os.Build;

/**
 * @anthor Liszt
 * @data 2019/11/8
 * Description
 */
public class DeviceUtils {

    public static String HUA_WEI = "huawei";
    public static String XIAO_MI = "xiaomi";
    public static String VIVO = "vivo";
    public static String OPPO = "oppo";
    public static String SAMSUNG = "samsung";


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
        if (getBrand().toLowerCase().equals(VIVO) || getBrand().toLowerCase().equals(OPPO)) {
            return true;
        }
        return false;
    }

}
