package com.yanlong.im.utils;

import androidx.annotation.Nullable;

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
            if (newVersion > oldVersion && oldVersion == 3) {
                updateV4(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 4) {
                updateV5(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 5) {
                updateV6(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 6) {
                updateV7(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 7) {
                updateV8(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 8) {
                updateV9(schema);
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

    private void updateV5(RealmSchema schema) {
        schema.get("UserInfo")
                .addField("joinType", int.class)
                .addField("joinTime", String.class)
                .addField("inviter", String.class)
                .addField("inviterName", String.class);
    }

    /*
     * 1. 新建群成员表，与通讯录分离
     * 2. 更改Group中群成员存储字段名字
     * 3. 新建音视频通话表
     * setNullable，设置不能为null，也可以通过注解@Required 来实现
     * */
    private void updateV6(RealmSchema schema) {
        schema.create("MemberUser")
                .addField("memberId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("uid", long.class)
                .addField("gid", String.class)/*.setNullable("gid", true)*/
                .addField("name", String.class)/*.setNullable("name", true)*/
                .addField("sex", int.class)/*.setNullable("sex", true)*/
                .addField("imid", String.class)/*.setNullable("imid", true)*/
                .addField("head", String.class)/*.setNullable("head", true)*/
                .addField("membername", String.class)/*.setNullable("membername", true)*/
                .addField("joinType", int.class)
                .addField("joinTime", String.class)
                .addField("inviter", String.class)/*.setNullable("inviter", true)*/
                .addField("inviterName", String.class)/*.setNullable("inviterName", true)*/
                .addField("tag", String.class)/*.setNullable("tag", true)*/;
        schema.get("Group")
                .removeField("users")
                .addRealmListField("members", schema.get("MemberUser"));

        schema.create("P2PAuVideoMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("av_type", int.class)
                .addField("operation", String.class)
                .addField("desc", String.class);

        schema.get("UserInfo")
                .addField("neteaseAccid", String.class);
        schema.get("MsgAllBean")
                .addRealmObjectField("p2PAuVideoMessage", schema.get("P2PAuVideoMessage"));
    }

    private void updateV7(RealmSchema schema) {
        schema.get("UserInfo")
                .addField("vip", String.class);
    }

    private void updateV8(RealmSchema schema) {
        schema.create("P2PAuVideoDialMessage")
                .addField("av_type", int.class);
        schema.get("MsgAllBean")
                .addRealmObjectField("p2PAuVideoDialMessage", schema.get("P2PAuVideoDialMessage"));
    }

    private void updateV9(RealmSchema schema) {
        schema.get("MsgCancel")
                .addField("cancelContent", String.class)
                .addField("cancelContentType", Integer.class);
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof DaoMigration;
    }

    @Override
    public int hashCode() {
        return 100;
    }
}
