package com.yanlong.im.utils;

import com.tencent.bugly.crashreport.CrashReport;
import com.yanlong.im.MyAppLication;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SpUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;

// 1.建bean  继承 RealmObject 2.DaoMigration 写schema 升级updateVxx  3. DaoUtil 升级dbVer

public class DaoUtil {
    private static final String TAG = "DaoUtil";
    private static DaoUtil util;
    private static RealmConfiguration config;

    //放在初始化中
    public static void initConfig(String dbName) {
        //---------------重要-------------------------
        //数据库版本,数据库如果有变动,需要修改2个地方
        // 1.dbVer的版本号+1
        // 2.DaoMigration类中migrate()处理升级之后的字段
        //-------------------------------------------
        long dbVer = 36;
        if (AppConfig.DEBUG) {//debug版本就直接清理数据
//            config = new RealmConfiguration.Builder()
//                    .name(dbName + ".realm")//指定数据库的名称。如不指定默认名为default。
//                    .schemaVersion(dbVer)
//                    .deleteRealmIfMigrationNeeded()//声明版本冲突时自动删除原数据库，开发时候打开
//                    .build();
            config = new RealmConfiguration.Builder()
                    .name(dbName + ".realm")//指定数据库的名称。如不指定默认名为default。
                    .schemaVersion(dbVer)
                    .migration(new DaoMigration())//数据库版本升级处理
                    .build();
        } else {//正式版本就进行数据库升级
            config = new RealmConfiguration.Builder()
                    .name(dbName + ".realm")//指定数据库的名称。如不指定默认名为default。
                    .schemaVersion(dbVer)
                    .migration(new DaoMigration())//数据库版本升级处理
                    .build();
        }


    }

    private DaoUtil() {
    }

    public static DaoUtil get() {
        if (util == null) {
            util = new DaoUtil();

        }

        return util;
    }

    /***
     * 获取数据库实例
     * @return
     */
    public static Realm open() {
        // TODO  处理异常config为空时情况，重新初始化
        if (config == null) {
            LogUtil.getLog().e(TAG, "openRealm");
            Long uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UID).get4Json(Long.class);
            int type = SpUtil.getSpUtil().getSPValue("ipType", 0);
            String appType = "";//服务器类型名称
            if (type == 1) {
                appType = "_debug";
            } else if (type == 2) {
                appType = "_pre";
            }
            initConfig("db_user_" + uid + appType);
        }
        return Realm.getInstance(config);
    }


    //保存对象到表中
    public static void save(RealmModel obj) {
        if (obj == null)
            return;
        Realm realm = open();
        try {
            realm.beginTransaction();
            realm.insert(obj);
            //realm.copyToRealm(obj);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            close(realm);
            reportException(e);
        }
    }

    public static boolean update(RealmModel obj) {
        if (obj == null)
            return false;
        Realm realm = open();
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(obj);
            realm.commitTransaction();
            realm.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            close(realm);
            reportException(e);
        }
        return false;
    }

    //查找所有数据

    /***
     * 简单查询某一条
     * @param clss
     * @param fieldName
     * @param value
     * @param <T>
     * @return
     */
    public static <T extends RealmModel> T findOne(Class<T> clss, String fieldName, Object value) {
        RealmModel beans = null;
        Realm realm = open();
        RealmModel res = null;
        try {
            if (value instanceof Integer) {
                res = realm.where(clss).equalTo(fieldName, (Integer) value).findFirst();
            } else if (value instanceof String) {
                res = realm.where(clss).equalTo(fieldName, (String) value).findFirst();
            } else if (value instanceof Float) {
                res = realm.where(clss).equalTo(fieldName, (Float) value).findFirst();
            } else if (value instanceof Double) {
                res = realm.where(clss).equalTo(fieldName, (Double) value).findFirst();
            } else if (value instanceof Long) {
                res = realm.where(clss).equalTo(fieldName, (Long) value).findFirst();
            } else if (value instanceof Boolean) {
                res = realm.where(clss).equalTo(fieldName, (Boolean) value).findFirst();
            } else if (value instanceof Byte) {
                res = realm.where(clss).equalTo(fieldName, (Byte) value).findFirst();
            }
            if (res != null)
                beans = realm.copyFromRealm(res);
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            close(realm);
            reportException(e);
        }
        return (T) beans;
    }

    public static <T extends RealmModel> List findAll(Class<T> clss) {
        List beans = new ArrayList();
        Realm realm = open();
        try {
            RealmResults list = realm.where(clss).findAll();
            if (list != null) {
                beans = realm.copyFromRealm(list);
            }
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            close(realm);
            reportException(e);
        }
        return beans;
    }

    public static <T extends RealmModel> void deleteOne(Class<T> clss, String fieldName, Object value) {
        RealmModel beans = null;
        Realm realm = open();
        try {
            realm.beginTransaction();
            RealmResults<T> res = null;
            if (value instanceof Integer) {
                res = realm.where(clss).equalTo(fieldName, (Integer) value).findAll();
            } else if (value instanceof String) {
                res = realm.where(clss).equalTo(fieldName, (String) value).findAll();
            } else if (value instanceof Float) {
                res = realm.where(clss).equalTo(fieldName, (Float) value).findAll();
            } else if (value instanceof Double) {
                res = realm.where(clss).equalTo(fieldName, (Double) value).findAll();
            } else if (value instanceof Long) {
                res = realm.where(clss).equalTo(fieldName, (Long) value).findAll();
            } else if (value instanceof Boolean) {
                res = realm.where(clss).equalTo(fieldName, (Boolean) value).findAll();
            } else if (value instanceof Byte) {
                res = realm.where(clss).equalTo(fieldName, (Byte) value).findAll();
            }
            if (res != null) {
                res.deleteAllFromRealm();
            }
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
            close(realm);
            reportException(e);
        }
    }

    public static List page(int page, RealmResults list, Realm realm) {
        return page(10, page, list, realm);
    }

    /***
     * 分页处理
     */
    public static List page(int pSize, int page, RealmResults list, Realm realm) {
        int from = pSize * page;
        int to = from + pSize;
        to = to < list.size() ? to : list.size();
        from = from > to ? to : from;
        return realm.copyFromRealm(list.subList(from, to));
    }

    /***
     * 自动开启事务
     * @param event
     */
    public static void start(EventTransaction event) {
        Realm realm = DaoUtil.open();
        realm.beginTransaction();
        event.run(realm);
        realm.commitTransaction();
        realm.close();
    }

    public interface EventTransaction {
        void run(Realm realm);
    }

    /**
     * 启动数据库异步事务
     * 注意：不需关闭Realm数据库对象
     *
     * @param realm
     * @param transaction
     * @param onSuccess
     * @param onError
     */
    public static void executeTransactionAsync(Realm realm, Realm.Transaction transaction,
                                               Realm.Transaction.OnSuccess onSuccess,
                                               Realm.Transaction.OnError onError) {
        try {
            if (realm != null && !realm.isClosed())
                realm.executeTransactionAsync(transaction, onSuccess, onError);
        } catch (RejectedExecutionException e) {//异步线程过多异常，等待一秒后再+重试
            MyAppLication.INSTANCE().handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    executeTransactionAsync(realm, transaction, onSuccess, onError);
                }
            },1000);

        }
    }

    public static void close(Realm realm) {
        if (realm != null) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            realm.close();
        }
    }

    /**
     * 添加或者修改(性能优于下面的saveOrUpdate（）方法)
     *
     * @param object
     * @return 保存或者修改是否成功
     */
    public boolean insertOrUpdate(RealmObject object) {
        Realm realm = open();

        try {
            realm.beginTransaction();
            realm.insertOrUpdate(object);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            close(realm);
            reportException(e);
            return false;
        }
    }

    /**
     * @param list
     * @return 保存或者修改是否成功
     */
    public boolean insertOrUpdateBatch(List<? extends RealmObject> list) {
        Realm realm = open();
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(list);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            close(realm);
            reportException(e);
            return false;
        }
    }

    public static void reportException(Exception e) {
        CrashReport.postCatchedException(e);
    }
}
