package net.cb.cb.library.utils;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class InstallAppUtil {
    private AppCompatActivity activity;
    private String apkPath;
    private final int INSTALL_APK_REQUESTCODE = 5631;
    private final int GET_UNKNOWN_APP_SOURCES = 5632;

    public void install(AppCompatActivity act, String apkPath) {
        activity = act;
        this.apkPath = apkPath;
        if (Build.VERSION.SDK_INT >= 26) {
            //来判断应用是否有权限安装apk
            boolean installAllowed = activity.getPackageManager().canRequestPackageInstalls();
            //有权限
            if (installAllowed) {
                //安装apk
                install();
            } else {
                //无权限 申请权限
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_APK_REQUESTCODE);
            }
        } else {
            install();
        }
    }

    public void onRequestPermissionsResult(AppCompatActivity activity,int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(activity==null)
            return;
        switch (requestCode) {
            case INSTALL_APK_REQUESTCODE:
                //有注册权限且用户允许安装
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    install();
                } else {
                    //将用户引导至安装未知应用界面。
                    Uri packageURI = Uri.parse("package:"+activity.getPackageName());
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
                    activity.startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
                    ToastUtil.show(activity.getApplicationContext(),"请开启允许安装应用开关");
                }
                break;

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case GET_UNKNOWN_APP_SOURCES:
                install();
                break;

            default:
                break;
        }
    }

    /***
     * 只支持缓存目录安装
     */
    private void install() {
        //7.0以上通过FileProvider
        if (Build.VERSION.SDK_INT>= 24) {
            String at=activity.getApplication().getPackageName()+".app";
           Uri uri = FileProvider.getUriForFile(activity, at, new File(apkPath));
          //  Uri uri=Uri.fromFile(new File(apkPath));
            Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + apkPath), "application/vnd.android.package-archive");
            activity.startActivity(intent);
        }
    }
}
