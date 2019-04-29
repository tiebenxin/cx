package com.yanlong.im.utils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmResults;


public class DaoUtil {
    private static DaoUtil util;


    private DaoUtil() {
    }

    public static DaoUtil get() {
        if (util == null) {
            util = new DaoUtil();

        }

        return util;
    }

    public static void save(RealmModel obj) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(obj);
        //realm.copyToRealm(obj);
        realm.commitTransaction();
        realm.close();
    }

    public static <T extends RealmModel> List findAll(Class<T> clss) {
        List beans;
        Realm realm = Realm.getDefaultInstance();

        RealmResults list = realm.where(clss).findAll();
        beans = realm.copyFromRealm(list);
        realm.close();
        return beans;
    }

    public static <T extends RealmModel> T findOne(Class<T> clss, String fieldName, Object value) {
        RealmModel beans = null;
        Realm realm = Realm.getDefaultInstance();
        RealmModel res = null;
        if (value instanceof Integer) {
            res = realm.where(clss).equalTo(fieldName,(Integer) value).findFirst();
        }else if(value instanceof String){
            res = realm.where(clss).equalTo(fieldName,(String) value).findFirst();
        }else if(value instanceof Float){
            res = realm.where(clss).equalTo(fieldName,(Float) value).findFirst();
        }else if(value instanceof Double){
            res = realm.where(clss).equalTo(fieldName,(Double) value).findFirst();
        }else if(value instanceof Long){
            res = realm.where(clss).equalTo(fieldName,(Long) value).findFirst();
        }else if(value instanceof Boolean){
            res = realm.where(clss).equalTo(fieldName,(Boolean) value).findFirst();
        }else if(value instanceof Byte){
            res = realm.where(clss).equalTo(fieldName,(Byte) value).findFirst();
        }



        if (res != null)
            beans = realm.copyFromRealm(res);
        realm.close();
        return (T) beans;
    }


}
