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
            if (oldVersion == 0) {
                updateV1(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion) {
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
     * 新增群头像表
     * */
    private void updateV2(RealmSchema schema) {
//        schema.create("GroupImageHead")
//                .addField("gid", String.class, FieldAttribute.PRIMARY_KEY)
//                .addField("imgHeadUrl", String.class);
    }
}
