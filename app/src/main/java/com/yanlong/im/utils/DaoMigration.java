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

        if (oldVersion == 0) {
           /* schema.create("Person")
                    .addField("name", String.class)
                    .addField("age", int.class);*/
            schema.create("MsgCancel")
                    .addField("msgid", String.class,FieldAttribute.PRIMARY_KEY)
                    .addField("uid", Long.class)
                    .addField("note", String.class)
                    .addField("msgidCancel", String.class)
            ;

            schema.get("MsgAllBean")
                    .addRealmObjectField("msgCancel", schema.get("MsgCancel"))
                    ;

            oldVersion++;
        }

        if (oldVersion == 1) {
           /* schema.get("Person")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addRealmObjectField("favoriteDog", schema.get("Dog"))
                    .addRealmListField("dogs", schema.get("Dog"));*/
            oldVersion++;
        }

    }
}
