package com.yanlong.im.utils;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class DaoMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();
        if (newVersion > oldVersion) {
            if (oldVersion == 0) {//从0升到1
                updateV1(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 1) {//从1升到2
                updateV2(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 2) {//从2升到3
                updateV3(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 3) {//从1升到2
                updateV4(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 4) {
                updateV5(schema);
                oldVersion++;
            }
        }
    }

    /*
     * 新增群头像表
     * */
    private void updateV1(RealmSchema schema) {
        schema.create("GroupImageHead")
                .addField("gid", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("imgHeadUrl", String.class);
    }

    /*
     * 新增群头像表
     * */
    private void updateV4(RealmSchema schema) {
        schema.get("MsgAllBean")
                .addRealmObjectField("videoMessage", schema.get("VideoMessage"));
        schema.get("VideoMessage")
                .addField("localUrl", String.class);

    }

    /*
     *UserInfo 新增字段  lockCloudRedEnvelope，destroy
     * */
    private void updateV2(RealmSchema schema) {
        schema.get("UserInfo")
                .addField("lockCloudRedEnvelope", int.class)
                .addField("destroy", int.class)
                .addField("destroyTime", long.class);

    }

    //短视频数据库
    private void updateV3(RealmSchema schema) {
        schema.create("VideoMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("duration", long.class)
                .addField("bg_url", String.class)
                .addField("width", long.class)
                .addField("height", long.class)
                .addField("isReadOrigin", boolean.class)
                .addField("url", String.class);
//                .addField("localUrl", String.class);
    }

    //新增群阅后即焚
    private void updateV6(RealmSchema schema) {
        schema.get("Group")
                .addField("survivaltime", int.class);
    }



    private void updateV5(RealmSchema schema){
        schema.get("UserInfo")
                .addField("joinType", int.class)
                .addField("joinTime", String.class)
                .addField("inviter", String.class)
                .addField("inviterName",String.class);
    }


//    @Override
//    public boolean equals(@Nullable Object obj) {
//        return obj instanceof DaoMigration;
//    }
//
//    @Override
//    public int hashCode() {
//        return super.hashCode();
//    }
}
