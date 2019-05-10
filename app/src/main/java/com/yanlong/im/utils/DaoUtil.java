package com.yanlong.im.utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmResults;


public class DaoUtil {
    private static DaoUtil util;
    private static RealmConfiguration config;

    //放在初始化中
    public void initConfig(String dbName) {
        config = new RealmConfiguration.Builder()
                .name(dbName + ".realm")//指定数据库的名称。如不指定默认名为default。
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()//声明版本冲突时自动删除原数据库，开发时候打开
//                .inMemory()// 声明数据库只在内存中持久化
                .build();

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
        //  return Realm.getDefaultInstance();
        return Realm.getInstance(config);
    }


    //保存对象到表中
    public static void save(RealmModel obj) {
        if(obj==null)
            return;
        try {
            Realm realm = open();
            realm.beginTransaction();
            realm.insert(obj);
            //realm.copyToRealm(obj);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void update(RealmModel obj) {
        if(obj==null)
            return;
        try {
            Realm realm = open();
            realm.beginTransaction();
            realm.insertOrUpdate(obj);
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //查找所有数据
    public static <T extends RealmModel> List findAll(Class<T> clss) {
        List beans=new ArrayList();
        Realm realm = open();

        RealmResults list = realm.where(clss).findAll();
        if(list!=null){
            beans = realm.copyFromRealm(list);
        }

        realm.close();
        return beans;
    }

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
        return (T) beans;
    }

    public static <T extends RealmModel> void deleteOne(Class<T> clss, String fieldName, Object value) {
        RealmModel beans = null;
        Realm realm = open();
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


        if (res != null){
            res.deleteAllFromRealm();
        }

        realm.commitTransaction();

        realm.close();
    }

    public static  List page(int page, RealmResults list, Realm realm ){
       return page(10, page,  list,  realm );
    }
    /***
     * 分页处理
     */
    public static  List page(int pSize,int page, RealmResults list, Realm realm ){
        int from = pSize * page;
        int to = from + pSize;
        to = to < list.size() ? to : list.size();
        from=from>to?to:from;


        return realm.copyFromRealm(list.subList(from, to));
    }


}
