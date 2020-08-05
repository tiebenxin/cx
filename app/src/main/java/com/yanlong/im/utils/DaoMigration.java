package com.yanlong.im.utils;

import androidx.annotation.Nullable;

import net.cb.cb.library.utils.LogUtil;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

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
            if (newVersion > oldVersion && oldVersion == 31) {
                updateV32(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 32) {
                updateV33(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 33) {
                updateV34(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 34) {
                updateV35(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 35) {
                updateV36(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 36) {
                updateV37(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 37) {
                updateV38(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 38) {
                updateV39(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 39) {
                updateV40(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 40) {
                updateV41(schema);
                oldVersion++;
            }
            if (newVersion > oldVersion && oldVersion == 41) {
                updateV42(schema);
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

    //添加收藏相关字段，添加新的收藏消息类型
    private void updateV32(RealmSchema schema) {
        schema.create("CollectionInfo")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("createTime", String.class)
                .addField("data", String.class)
                .addField("fromGid", String.class)
                .addField("fromGroupName", String.class)
                .addField("fromUid", long.class)
                .addField("fromUsername", String.class)
                .addField("id", long.class)
                .addField("type", int.class);
        schema.create("CollectAtMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("msg", String.class);
        schema.create("CollectChatMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("msg", String.class);
        schema.create("CollectImageMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("origin", String.class)
                .addField("preview", String.class)
                .addField("thumbnail", String.class)
                .addField("localimg", String.class)
                .addField("isReadOrigin", boolean.class)
                .addField("width", long.class)
                .addField("height", long.class)
                .addField("size", long.class);
        schema.create("CollectLocationMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("lat", int.class)
                .addField("lon", int.class)
                .addField("addr", String.class)
                .addField("addressDesc", String.class)
                .addField("img", String.class);
        schema.create("CollectSendFileMessage")  //除了ignore本地字段，都要加上
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("fileURL", String.class)
                .addField("fileName", String.class)
                .addField("fileFormat", String.class)
                .addField("fileSize", long.class)
                .addField("collectLocalPath", String.class)
                .addField("collectIsFromOther", boolean.class)
                .addField("collectRealFileRename", String.class);
        schema.create("CollectShippedExpressionMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("expression", String.class);
        schema.create("CollectVideoMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("videoDuration", long.class)
                .addField("videoBgURL", String.class)
                .addField("width", long.class)
                .addField("height", long.class)
                .addField("size", long.class)
                .addField("videoURL", String.class)
                .addField("isReadOrigin", boolean.class)
                .addField("localUrl", String.class);
        schema.create("CollectVoiceMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("voiceURL", String.class)
                .addField("voiceDuration", int.class)
                .addField("playStatus", int.class)
                .addField("localUrl", String.class);
    }

    //新增用户全拼字段
    private final void updateV33(RealmSchema schema) {
        schema.get("UserInfo")
                .addField("pinyin", String.class);
    }

    //新增小助手广告消息
    private final void updateV34(RealmSchema schema) {
        schema.create("AdMessage")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("title", String.class)
                .addField("summary", String.class)
                .addField("thumbnail", String.class)
                .addField("buttonTxt", String.class)
                .addField("appId", String.class)
                .addField("webUrl", String.class)
                .addField("schemeUrl", String.class);

        schema.get("MsgAllBean")
                .addRealmObjectField("adMessage", schema.get("AdMessage"));
    }

    //新增收藏操作表、收藏删除操作表，用于支持离线收藏功能
    private final void updateV35(RealmSchema schema) {
        schema.create("OfflineCollect")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addRealmObjectField("collectionInfo", schema.get("CollectionInfo"));
        schema.create("OfflineDelete")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY);
    }

    //用户信息页面增加封锁功能
    private final void updateV36(RealmSchema schema) {
        schema.get("UserBean")
                .addField("lockedFunctions", int.class);
        schema.get("UserInfo")
                .addField("lockedFunctions", int.class);
    }

    //session新增标记已读未读字段
    private final void updateV37(RealmSchema schema) {
        schema.get("Session")
                .addField("markRead", int.class);
    }

    //用户信息页面增加简拼字段，转账消息增加操作creator字段
    private final void updateV38(RealmSchema schema) {
        schema.get("UserInfo")
                .addField("pinyinHead", String.class);
        schema.get("TransferMessage")
                .addField("creator", long.class);
    }

    //增加转账消息被动关系字段
    private final void updateV39(RealmSchema schema) {
        schema.get("TransferMessage")
                .addField("passive", int.class);
    }

    //新增红包消息备份表
    private final void updateV40(RealmSchema schema) {
        schema.get("UserInfo")
                .addField("lockedstatus", int.class);

        schema.create("EnvelopeTemp")
                .addField("msgId", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("id", String.class)
                .addField("re_type", int.class)
                .addField("comment", String.class)
                .addField("isInvalid", int.class)
                .addField("style", int.class)
                .addField("traceId", long.class)
                .addField("actionId", String.class)
                .addField("accessToken", String.class)
                .addField("envelopStatus", int.class)
                .addField("sign", String.class);

        schema.create("MessageDBTemp")
                .addField("msg_id", String.class, FieldAttribute.PRIMARY_KEY)
                .addField("timestamp", Long.class)
                .addField("send_state", int.class)
                .addField("send_data", byte[].class)
                .addField("isRead", boolean.class)
                .addField("request_id", String.class)
                .addField("from_uid", Long.class)
                .addField("from_nickname", String.class)
                .addField("from_avatar", String.class)
                .addField("from_group_nickname", String.class)
                .addField("to_uid", Long.class)
                .addField("gid", String.class)
                .addField("read", int.class)
                .addField("msg_type", Integer.class)
                .addField("survival_time", int.class)
                .addField("endTime", int.class)
                .addField("readTime", int.class)
                .addField("startTime", int.class)
                .addField("serverTime", int.class)
                .addField("isLocal", int.class)
                .addField("isReplying", int.class)
                .addRealmObjectField("envelopeMessage", schema.get("EnvelopeTemp"))
                ;
    }

    //增加撤回消息新字段
    private final void updateV41(RealmSchema schema) {
        schema.get("MsgCancel")
                .addField("role", int.class);
    }

    //增加撤回消息新字段
    private final void updateV42(RealmSchema schema) {
        schema.get("MsgCancel")
                .addField("alterantive_name", String.class);
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
