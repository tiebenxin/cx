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
     *UserInfo 新增字段  lockCloudRedEnvelope，destroy
     * */
    private void updateV2(RealmSchema schema) {
        schema.get("UserInfo")
                .addField("lockCloudRedEnvelope", int.class)
                .addField("destroy", int.class)
                .addField("destroyTime", long.class);

    }

}
