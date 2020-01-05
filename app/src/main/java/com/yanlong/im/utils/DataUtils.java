package com.yanlong.im.utils;

import android.content.Context;
import android.content.pm.PackageInfo;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;

import java.util.List;

/**
 * author : zgd
 * date   : 2019/12/211:11
 */
public class DataUtils {

    // *** 隐藏
    public static String getHideData(String data,int notHideNumb){
        if(StringUtil.isNotNull(data)){
//            LogUtil.getLog().e("====length==="+data.length());
            if(data.length()<=notHideNumb||notHideNumb<=0){
                String start="";
                for (int i = 0; i < data.length(); i++) {
                    start=start+"*";
//                    LogUtil.getLog().e("====start==="+start);
                }
                return start;
            }else {
                String dataTemp=data.substring(0,notHideNumb);
                String start="";
                for (int i = notHideNumb; i < data.length(); i++) {
                    start=start+"*";
//                    LogUtil.getLog().e("====start==="+start);
                }
                dataTemp=dataTemp+start;
                return dataTemp;
            }
        }
        return data;
    }

    //判断手机中是否安装指定包名的软件
    public static boolean isInstallApk(Context context, String packageName) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageInfo.packageName.equals(packageName)) {
                return true;
            } else {
                continue;
            }
        }
        return false;
    }

}
