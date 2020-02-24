package net.cb.cb.library;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class MainApplication extends MultiDexApplication {
    private Context context;
    private static final String TAG = "MainApplication";
    public static MainApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        instance = this;
        AppConfig.APP_CONTEXT = context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // TODO 处理android.content.res.AssetManager.finalize() timed out after 120 seconds
        //  部分OPPO机型 AssetManager.finalize() timed out的修复
        fixOppoAssetManager();
    }

    public static MainApplication getInstance() {
        return instance;
    }

    private void fixOppoAssetManager() {
        String device = android.os.Build.BRAND;
        if (!TextUtils.isEmpty(device)) {
            if (device.contains("OPPO R9") || device.contains("OPPO A5")) {
                try {
                    // 关闭掉FinalizerWatchdogDaemon
                    Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");
                    Method method = clazz.getSuperclass().getDeclaredMethod("stop");
                    method.setAccessible(true);
                    Field field = clazz.getDeclaredField("INSTANCE");
                    field.setAccessible(true);
                    method.invoke(field.get(null));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
