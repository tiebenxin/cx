package com.yanlong.im.chat.dao;

import com.yanlong.im.chat.bean.MsgAllBean;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MsgDao {



    public List<MsgAllBean> getMsg4User(Long userid){
        List<MsgAllBean> beans;
        Realm realm = Realm.getDefaultInstance();

        RealmResults list = realm.where(MsgAllBean.class).equalTo("from_uid",userid).notEqualTo("msg_type",0).findAll();
        list= list.sort("timestamp", Sort.DESCENDING);
        beans = realm.copyFromRealm(list);
        realm.close();
        return beans;
    }
}
