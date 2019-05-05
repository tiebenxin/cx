package com.yanlong.im.chat.dao;

import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.utils.DaoUtil;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MsgDao {


    /***
     * 单用户消息列表
     * @param userid
     * @return
     */
    public List<MsgAllBean> getMsg4User(Long userid,int page) {
        List<MsgAllBean> beans;
        Realm realm = DaoUtil.open();

        RealmResults list = realm.where(MsgAllBean.class)
                .equalTo("from_uid", userid).or().equalTo("to_uid", userid)
                .and().equalTo("gid", "").and()
                .notEqualTo("msg_type", 0)
                .sort("timestamp", Sort.DESCENDING)
                .findAll();


        int pSize=10;

        beans = realm.copyFromRealm(list.subList(pSize*page,pSize*(page+1)));
        //翻转列表
        Collections.reverse(beans);
        realm.close();
        return beans;
    }


}
