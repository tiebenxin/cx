package com.yanlong.im.utils;

import android.database.sqlite.SQLiteDatabase;


import com.yanlong.im.gen.DaoMaster;
import com.yanlong.im.gen.DaoSession;

import net.cb.cb.library.AppConfig;



public class DaoUtil {
    private static DaoUtil util;
    private static DaoSession daoSession;

    private DaoUtil() {
    }

    public static DaoUtil get() {
        if (util == null) {
            util = new DaoUtil();
            initGreenDao();
        }

        return util;
    }


    private static void initGreenDao() {

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(AppConfig.APP_CONTEXT, "main.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
