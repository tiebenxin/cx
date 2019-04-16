package com.yanlong.im;


import net.cb.cb.library.AppConfig;
import net.cb.cb.library.BuildConfig;
import net.cb.cb.library.MainApplication;
import net.cb.cb.library.utils.LogUtil;

import com.yanlong.im.utils.DaoUtil;

public class MyAppLication extends MainApplication {




    @Override
    public void onCreate() {
        super.onCreate();
        switch (BuildConfig.BUILD_TYPE) {
            case "debug":

                AppConfig.URL_HOST = "http://192.168.10.110:8080";

                AppConfig.DEBUG = true;


                break;
            case "pre":
                AppConfig.DEBUG = true;
                AppConfig.URL_HOST = "https://baidu.net";

                break;
            case "release":
                //test 后面这里改false
                AppConfig.DEBUG = false;
                AppConfig.URL_HOST = "https://baidu.com";

                break;
        }

        LogUtil.getLog().init(AppConfig.DEBUG);
      //初始化
        DaoUtil.get();


    }

}
