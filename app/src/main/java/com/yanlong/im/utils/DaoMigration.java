package com.yanlong.im.utils;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import net.cb.cb.library.utils.LogUtil;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

// 1.建bean  继承 RealmObject 2.DaoMigration 写schema 升级updateVxx  3. DaoUtil 升级dbVer
public class DaoMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        // DynamicRealm exposes an editable schema
        LogUtil.getLog().e(DaoMigration.class.getSimpleName(), "升级数据库--oldVer=" + oldVersion + "--newVer=" + newVersion);
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

            if (newVersion > oldVersion && oldVersion == 9) {
                updateV10(schema);
                oldVersion++;
            }

            if (newVersion > oldVersion && oldVersion == 10) {
                updateV11(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 11) {
                updateV12(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 12) {
                updateV13(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 13) {
                updateV14(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 14) {
                updateV15(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 15) {
                updateV16(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 16) {
                updateV17(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 17) {
                updateV18(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 18) {
                updateV19(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 19) {
                updateV20(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 20) {
                updateV21(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 21) {
                updateV22(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 22) {
                updateV23(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 23) {
                updateV24(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 24) {
                updateV25(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 25) {
                updateV26(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 26) {
                updateV27(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 27) {
                updateV28(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 28) {
                updateV29(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 29) {
                updateV30(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 30) {
                updateV31(schema);
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

    /*
     * 新增群头像表
     * */
    private void updateV4(RealmSchema schema) {
        schema.get("MsgAllBean")
                .addRealmObjectField("videoMessage", schema.get("VideoMessage"));
        schema.get("VideoMessage")
                .addField("localUrl", String.class);

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


    //新增群阅后即焚
    private void updateV10(RealmSchema schema) {
        schema.get("Group")
                .addField("survivaltime", int.class);

        schema.create("ChangeSurvivalTimeMessage")
                .addField("msgid", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("survival_time", int.class);

        schema.get("MsgAllBean")
                .addField("survival_time", int.class)
                .addField("serverTime", long.class)
                .addField("endTime", long.class)
                .addField("readTime", long.class)
                .addField("startTime", long.class)
                .addField("read", int.class)
                .addRealmObjectField("changeSurvivalTimeMessage", schema.get("ChangeSurvivalTimeMessage"));

        schema.get("UserInfo")
                .addField("masterRead", int.class)
                .addField("myRead", int.class)
                .addField("friendRead", int.class);
    }


    //
    private void updateV11(RealmSchema schema) {
        schema.create("ApplyBean")
                .addField("aid", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("chatType", int.class)

                .addField("uid", long.class)
                .addField("nickname", String.class)
                .addField("alias", String.class)
                .addField("avatar", String.class)
                .addField("sayHi", String.class)
                .addField("stat", int.class)

                .addField("gid", String.class)
                .addField("groupName", String.class)
                .addField("joinType", int.class)
                .addField("inviter", long.class)
                .addField("inviterName", String.class)
                .addField("time", long.class);

        schema.get("Group").addField("merchantEntry", String.class);
    }

    //更新红包消息
    private void updateV12(RealmSchema schema) {
        schema.get("RedEnvelopeMessage")
                .addField("traceId", long.class)
                .addField("actionId", String.class);
    }

    //更新红包消息token
    private void updateV13(RealmSchema schema) {
        schema.get("RedEnvelopeMessage")
                .addField("accessToken", String.class);

    }

    //更新零钱助手及位置消息
    private void updateV14(RealmSchema schema) {
        schema.get("RedEnvelopeMessage")
                .addField("envelopStatus", int.class);


        schema.create("BalanceAssistantMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("tradeId", long.class)
                .addField("detailType", int.class)
                .addField("time", long.class)
                .addField("title", String.class)
                .addField("amountTitle", String.class)
                .addField("amount", long.class)
                .addField("items", String.class);

        schema.create("LocationMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("latitude", int.class)
                .addField("longitude", int.class)
                .addField("address", String.class)
                .addField("addressDescribe", String.class)
                .addField("img", String.class);

        schema.get("MsgAllBean")
                .addRealmObjectField("balanceAssistantMessage", schema.get("BalanceAssistantMessage"))
                .addRealmObjectField("locationMessage", schema.get("LocationMessage"));


    }

    //更新红包消息token
    private void updateV15(RealmSchema schema) {
        schema.get("RedEnvelopeMessage")
                .addField("sign", String.class);

        schema.get("TransferMessage")
                .addField("sign", String.class)
                .addField("opType", int.class);
    }

    /**
     * 更群管理, 发送失败红包临时存储表，领取转账通知表
     *
     * @param schema
     */
    private void updateV16(RealmSchema schema) {
        schema.get("Group")
                .addRealmListField("viceAdmins", Long.class)
                .addField("wordsNotAllowed", Integer.class);

        schema.get("UserInfo")
                .addField("bankReqSignKey", String.class);


        schema.create("EnvelopeInfo")
                .addField("rid", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("comment", String.class)
                .addField("reType", int.class)
                .addField("envelopeStyle", int.class)
                .addField("sendStatus", int.class)
                .addField("sign", String.class)
                .addField("createTime", long.class);

        schema.create("TransferNoticeMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("rid", String.class)
                .addField("content", String.class);

        schema.get("MsgAllBean")
                .addRealmObjectField("transferNoticeMessage", schema.get("TransferNoticeMessage"));
    }

    //更新红包消息token
    private void updateV17(RealmSchema schema) {
        schema.get("EnvelopeInfo")
                .addField("gid", String.class)
                .addField("uid", int.class)
                .addField("amount", long.class);

    }

    /**
     * 添加是否能领取零钱红包
     *
     * @param schema
     */
    private void updateV18(RealmSchema schema) {
        schema.get("Group")
                .addField("cantOpenUpRedEnv", int.class);
    }

    /**
     * 添加动画表情
     *
     * @param schema
     */
    private void updateV19(RealmSchema schema) {
        schema.create("ShippedExpressionMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("id", String.class);
        schema.get("MsgAllBean")
                .addRealmObjectField("shippedExpressionMessage", schema.get("ShippedExpressionMessage"));

    }

    //更新截屏通知开关
    private void updateV20(RealmSchema schema) {
        schema.get("UserInfo")
                .addField("screenshotNotification", int.class);//单聊截屏通知
        schema.get("Group")
                .addField("screenshotNotification", int.class);//群聊截屏通知
    }

    //文件消息类型 新建表
    private void updateV21(RealmSchema schema) {
        schema.create("SendFileMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("url", String.class)
                .addField("file_name", String.class)
                .addField("format", String.class)
                .addField("size", long.class)
                .addField("localPath", String.class);

        schema.get("MsgAllBean")
                .addRealmObjectField("sendFileMessage", schema.get("SendFileMessage"));
    }

    //文件消息类型 新建表
    private void updateV22(RealmSchema schema) {
        schema.create("WebMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("appName", String.class)
                .addField("title", String.class)
                .addField("description", String.class)
                .addField("webUrl", String.class)
                .addField("iconUrl", String.class);

        schema.get("MsgAllBean")
                .addRealmObjectField("webMessage", schema.get("WebMessage"));
    }

    private void updateV23(RealmSchema schema) {
        schema.get("SendFileMessage")
                .addField("isFromOther", boolean.class);
    }

    private void updateV24(RealmSchema schema) {
        schema.get("SendFileMessage")
                .addField("realFileRename", String.class);
    }

    private void updateV25(RealmSchema schema) {
        schema.create("SessionDetail")
                .addField("sid", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("name", String.class)
                .addField("avatar", String.class)
                .addField("avatarList", String.class)
                .addField("senderName", String.class)
                .addRealmObjectField("message", schema.get("MsgAllBean"));
    }

    private void updateV26(RealmSchema schema) {
        schema.get("SessionDetail")
                .addField("messageContent", String.class);
    }

    private void updateV27(RealmSchema schema) {
        schema.get("Group")
                .addField("stat", int.class);
    }

    private void updateV28(RealmSchema schema) {
        schema.get("MsgAllBean")
                .addField("isLocal", int.class);
    }

    //新建UserBean表，将登陆账号信息单独存储，以区别文件传输助手（userId即为自己id）
    private void updateV29(RealmSchema schema) {
        schema.create("UserBean")
                .addField("uid", Long.class, FieldAttribute.PRIMARY_KEY)
                .addField("name", String.class)
                .addField("mkName", String.class)
                .addField("sex", int.class)
                .addField("imid", String.class)
                .addField("tag", String.class)
                .addField("head", String.class)
                .addField("uType", Integer.class)
                .addField("phone", String.class)
                .addField("oldimid", String.class)
                .addField("neteaseAccid", String.class)
                .addField("vip", String.class)
                .addField("disturb", Integer.class)
                .addField("istop", Integer.class)
                .addField("phonefind", Integer.class)
                .addField("imidfind", Integer.class)
                .addField("friendvalid", Integer.class)
                .addField("groupvalid", Integer.class)
                .addField("messagenotice", Integer.class)
                .addField("displaydetail", Integer.class)
                .addField("stat", Integer.class)
                .addField("authStat", Integer.class)
                .addField("screenshotNotification", int.class)
                .addField("masterRead", int.class)
                .addField("myRead", int.class)
                .addField("friendRead", int.class)
                .addField("emptyPassword", boolean.class)
                .addField("sayHi", String.class)
                .addField("lastonline", Long.class)
                .addField("activeType", int.class)
                .addField("describe", String.class)
                .addField("lockCloudRedEnvelope", int.class)
                .addField("destroy", int.class)
                .addField("destroyTime", long.class)
                .addField("joinType", int.class)
                .addField("joinTime", String.class)
                .addField("inviter", String.class)
                .addField("inviterName", String.class)
                .addField("bankReqSignKey", String.class);
    }

    //新增单条消息回复相关表
    private final void updateV30(RealmSchema schema) {
        schema.create("QuotedMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("timestamp", long.class)
                .addField("msgType", int.class)
                .addField("fromUid", long.class)
                .addField("nickName", String.class)
                .addField("avatar", String.class)
                .addField("url", String.class)
                .addField("msg", String.class);

        schema.create("ReplyMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addRealmObjectField("quotedMessage", schema.get("QuotedMessage"))
                .addRealmObjectField("chatMessage", schema.get("ChatMessage"))
                .addRealmObjectField("atMessage", schema.get("AtMessage"));

        schema.get("MsgAllBean")
                .addRealmObjectField("replyMessage", schema.get("ReplyMessage"));
    }

    //新增是否正在回复消息字段
    private final void updateV31(RealmSchema schema) {
        schema.get("MsgAllBean")
                .addField("isReplying", int.class);
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
