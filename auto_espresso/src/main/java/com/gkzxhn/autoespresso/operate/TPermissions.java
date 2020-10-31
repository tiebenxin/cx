package com.gkzxhn.autoespresso.operate;

import android.os.Build;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.permission.PermissionRequester;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

/**
 * Created by Raleigh.Luo on 18/3/13.
 */

public class TPermissions {
    /**使用shell命令获取权限
     * @param permissions 权限 eg:android.permission.CALL_PHONE
     */
    public static void get_permission_shell(String... permissions){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for(String permission : permissions) {
                getInstrumentation().getUiAutomation().executeShellCommand(
                        "pm grant " + getTargetContext().getPackageName()
                                + " " + permission);
            }
        }
    }

    /**使用shell命令获取权限
     * @param permissions 权限 eg:android.permission.CALL_PHONE
     */
    public static void request_permissionsByGrant(String... permissions){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for(String permission : permissions){
                GrantPermissionRule.grant(permission);
            }
        }
    }
    public static void request_permissions(String... permissions){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionRequester permissionRequester  = new  PermissionRequester();
            for(String permission : permissions){
                permissionRequester.addPermissions(permission);
            }
            permissionRequester.requestPermissions();
        }
    }



}
