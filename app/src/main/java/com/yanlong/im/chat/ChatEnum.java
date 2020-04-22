package com.yanlong.im.chat;

import android.support.annotation.IntDef;

import com.yanlong.im.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.yanlong.im.chat.ChatEnum.EAuthStatus.AUTH_FIRST;
import static com.yanlong.im.chat.ChatEnum.EAuthStatus.AUTH_NO;
import static com.yanlong.im.chat.ChatEnum.EAuthStatus.AUTH_SECOND;
import static com.yanlong.im.chat.ChatEnum.EMessageType.AT;
import static com.yanlong.im.chat.ChatEnum.EMessageType.BUSINESS_CARD;
import static com.yanlong.im.chat.ChatEnum.EMessageType.IMAGE;
import static com.yanlong.im.chat.ChatEnum.EMessageType.NOTICE;
import static com.yanlong.im.chat.ChatEnum.EMessageType.RED_ENVELOPE;
import static com.yanlong.im.chat.ChatEnum.EMessageType.STAMP;
import static com.yanlong.im.chat.ChatEnum.EMessageType.TEXT;
import static com.yanlong.im.chat.ChatEnum.EMessageType.TRANSFER;
import static com.yanlong.im.chat.ChatEnum.EMessageType.UNRECOGNIZED;
import static com.yanlong.im.chat.ChatEnum.EMessageType.VOICE;
import static com.yanlong.im.chat.ChatEnum.ESendStatus.ERROR;
import static com.yanlong.im.chat.ChatEnum.ESendStatus.NORMAL;
import static com.yanlong.im.chat.ChatEnum.ESendStatus.PRE_SEND;
import static com.yanlong.im.chat.ChatEnum.ESendStatus.SENDING;
import static com.yanlong.im.chat.ChatEnum.EUserType.BLACK;
import static com.yanlong.im.chat.ChatEnum.EUserType.FRIEND;
import static com.yanlong.im.chat.ChatEnum.EUserType.SELF;
import static com.yanlong.im.chat.ChatEnum.EUserType.STRANGE;

public class ChatEnum {
    /*
     *1. 聊天布局枚举
     * */
    public enum EChatCellLayout {
        // 文本消息，接收和发送布局
        TEXT_RECEIVED(R.layout.cell_txt_received),
        TEXT_SEND(R.layout.cell_txt_send),

        //图片消息
        IMAGE_RECEIVED(R.layout.cell_img_received),
        IMAGE_SEND(R.layout.cell_img_send),

        //语音消息
        VOICE_RECEIVED(R.layout.cell_voice_received),
        VOICE_SEND(R.layout.cell_voice_send),

        //视频消息
        VIDEO_RECEIVED(R.layout.cell_video_received),
        VIDEO_SEND(R.layout.cell_video_send),

        //位置消息
        MAP_RECEIVED(R.layout.cell_location_received),
        MAP_SEND(R.layout.cell_location_send),

        //名片消息
        CARD_RECEIVED(R.layout.cell_card_received),
        CARD_SEND(R.layout.cell_card_send),

        //红包消息
        RED_ENVELOPE_RECEIVED(R.layout.cell_redenvelope_received),
        RED_ENVELOPE_SEND(R.layout.cell_redenvelope_send),


        //转账消息，共用红包布局
        TRANSFER_RECEIVED(R.layout.cell_redenvelope_received),
        TRANSFER_SEND(R.layout.cell_redenvelope_send),

        //戳一下消息
        STAMP_RECEIVED(R.layout.cell_stamp_received),
        STAMP_SEND(R.layout.cell_stamp_send),

        //@消息, 共用文本消息布局
        AT_RECEIVED(R.layout.cell_txt_received),
        AT_SEND(R.layout.cell_txt_send),

        //文件消息
        FILE_RECEIVED(R.layout.cell_file_received),
        FILE_SEND(R.layout.cell_file_send),

        //音视频通话消息
        CALL_RECEIVED(R.layout.cell_call_received),
        CALL_SEND(R.layout.cell_call_send),

        //分享 web 或 游戏消息
        WEB_RECEIVED(R.layout.cell_web_received),
        WEB_SEND(R.layout.cell_web_send),

        //表情消息
        EXPRESS_RECEIVED(R.layout.cell_img_received),
        EXPRESS_SEND(R.layout.cell_img_send),

        //合并转发
        MULTI_RECEIVED(R.layout.cell_txt_received),
        MULTI_SEND(R.layout.cell_txt_send),

        //通知消息
        NOTICE(R.layout.cell_notice),

        //零钱助手消息
        BALANCE_ASSISTANT(R.layout.cell_notice),

        //常信小助手消息
        ASSISTANT(R.layout.cell_txt_received),

        //端到端加密消息
        LOCK(R.layout.cell_lock),

        //未知消息
        UNRECOGNIZED(R.layout.cell_txt_received);


        public final int LayoutId;

        EChatCellLayout(int layoutId) {
            this.LayoutId = layoutId;
        }

        public static EChatCellLayout fromOrdinal(int ordinal) {
            EChatCellLayout result = null;
            for (EChatCellLayout item : EChatCellLayout.values()) {
                if (item.ordinal() == ordinal) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("EChatCellLayout - fromOrdinal");
            }
            return result;
        }

        public static EChatCellLayout fromLayoutId(int layoutId) {
            EChatCellLayout result = null;
            for (EChatCellLayout item : EChatCellLayout.values()) {
                if (item.LayoutId == layoutId) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("EChatCellLayout - fromLayoutId");
            }
            return result;
        }

        public static int size() {
            return values().length;
        }
    }

    /*
     * cell 点击事件类型
     * */
    @IntDef({ECellEventType.TXT_CLICK, ECellEventType.IMAGE_CLICK, ECellEventType.CARD_CLICK, ECellEventType.RED_ENVELOPE_CLICK, ECellEventType.LONG_CLICK, ECellEventType.TRANSFER_CLICK,
            ECellEventType.AVATAR_CLICK, ECellEventType.RESEND_CLICK, ECellEventType.AVATAR_LONG_CLICK, ECellEventType.VOICE_CLICK, ECellEventType.VIDEO_CLICK, ECellEventType.FILE_CLICK,
            ECellEventType.BALANCE_ASSISTANT_CLICK, ECellEventType.WEB_CLICK, ECellEventType.MULTI_CLICK, ECellEventType.MAP_CLICK, ECellEventType.VOICE_VIDEO_CALL, ECellEventType.EXPRESS_CLICK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ECellEventType {
        int TXT_CLICK = 0; //点击文本消息
        int IMAGE_CLICK = 1;//点击图片
        int CARD_CLICK = 2;//点击名片
        int RED_ENVELOPE_CLICK = 3;//点击红包
        int LONG_CLICK = 4;//长按事件
        int TRANSFER_CLICK = 5;//点击转账
        int AVATAR_CLICK = 6;//点击头像
        int RESEND_CLICK = 7;//点击重新发送
        int AVATAR_LONG_CLICK = 8;//头像长按事件
        int VOICE_CLICK = 9;//语音消息
        int VIDEO_CLICK = 10;//视频消息
        int FILE_CLICK = 11;//文件消息
        int BALANCE_ASSISTANT_CLICK = 12;//零钱助手消息
        int WEB_CLICK = 13;//分享web消息
        int MULTI_CLICK = 14;//合并消息
        int MAP_CLICK = 15;//位置消息
        int VOICE_VIDEO_CALL = 16;//点击音视频电话消息
        int EXPRESS_CLICK = 17;//点击表情
    }


    /*
     * 消息type
     * */
    @IntDef({NOTICE, TEXT, STAMP, RED_ENVELOPE, IMAGE, BUSINESS_CARD, TRANSFER, VOICE, AT, EMessageType.ASSISTANT, EMessageType.MSG_CANCEL,
            UNRECOGNIZED, EMessageType.MSG_VIDEO, EMessageType.MSG_VOICE_VIDEO, EMessageType.LOCK, EMessageType.CHANGE_SURVIVAL_TIME,
            EMessageType.READ, EMessageType.MSG_VOICE_VIDEO_NOTICE, EMessageType.LOCATION, EMessageType.BALANCE_ASSISTANT, EMessageType.SHIPPED_EXPRESSION,
            EMessageType.FILE, EMessageType.WEB, EMessageType.TRANSFER_NOTICE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EMessageType {
        int UNRECOGNIZED = -1; //未识别
        int NOTICE = 0; //公告
        int TEXT = 1;//文本
        int STAMP = 2;//戳一下
        int RED_ENVELOPE = 3;//红包
        int IMAGE = 4;//图片
        int BUSINESS_CARD = 5;//名片
        int TRANSFER = 6;//转账
        int VOICE = 7;//语音
        int AT = 8;//艾特@消息
        int ASSISTANT = 9;//小助手
        int MSG_CANCEL = 10; //撤回消息
        int MSG_VIDEO = 11; //短视频消息
        int MSG_VOICE_VIDEO = 12; //音视频消息
        int MSG_VOICE_VIDEO_NOTICE = 13; //音视频消息通知
        int LOCATION = 14; //位置消息
        int BALANCE_ASSISTANT = 15; //零钱助手消息
        int SHIPPED_EXPRESSION = 16;// 大表情
        int FILE = 17;//文件
        int WEB = 18;//web消息
        int TRANSFER_NOTICE = 19; //转账提醒

        int LOCK = 100; //端到端加密提示消息,本地自定义消息

        int CHANGE_SURVIVAL_TIME = 113;//阅后即焚
        int READ = 120;//已读消息

    }

    /*
     * 发送状态
     * 0:正常,1:错误,2:发送中 -1 预发送 3阅读即焚
     * */
    @IntDef({PRE_SEND, NORMAL, ERROR, SENDING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ESendStatus {
        int PRE_SEND = -1; //预发送
        int NORMAL = 0; //正常
        int ERROR = 1;//错误
        int SENDING = 2;//发送中
    }

    /*
     * 用户类型
     * 0:陌生人或者群友,1:自己,2:通讯录,3黑名单,4小助手
     * */
    @IntDef({STRANGE, SELF, FRIEND, BLACK, EUserType.ASSISTANT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EUserType {
        int STRANGE = 0; //陌生
        int SELF = 1; //自己
        int FRIEND = 2;//通讯录好友
        int BLACK = 3;//黑名单
        int ASSISTANT = 4;//系统小助手
    }

    /*
     * 认证状态
     * 0:未认证|1:已认证未上传证件照|2:已认证已上传证件照
     * */
    @IntDef({AUTH_NO, AUTH_FIRST, AUTH_SECOND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EAuthStatus {
        int AUTH_NO = 0; // 未认证
        int AUTH_FIRST = 1; //一级认证，认证但未上传证照
        int AUTH_SECOND = 2;//二级认证，认证已上传证照
    }

    /*
     * 播放状态
     *
     * */
    @IntDef({EPlayStatus.NO_DOWNLOADED, EPlayStatus.DOWNLOADING, EPlayStatus.NO_PLAY, EPlayStatus.PLAYING, EPlayStatus.STOP_PLAY, EPlayStatus.PLAYED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EPlayStatus {
        int NO_DOWNLOADED = 0; // 未下载
        int DOWNLOADING = 1; // 开始下载
        int NO_PLAY = 2; //下载成功，未播放
        int PLAYING = 3; //正播放
        int STOP_PLAY = 4; //停止播放
        int PLAYED = 5;//已播放
    }


    /*
     * 通知类型
     *
     * */
    @IntDef({ENoticeType.ENTER_BY_QRCODE, ENoticeType.INVITED, ENoticeType.KICK, ENoticeType.FORTH, ENoticeType.TRANSFER_GROUP_OWNER, ENoticeType.LEAVE, ENoticeType.RED_ENVELOPE_RECEIVED,
            ENoticeType.RECEIVE_RED_ENVELOPE, ENoticeType.CANCEL, ENoticeType.BLACK_ERROR, ENoticeType.NO_FRI_ERROR, ENoticeType.LOCK, ENoticeType.CHANGE_VICE_ADMINS_ADD,
            ENoticeType.CHANGE_VICE_ADMINS_CANCEL, ENoticeType.FORBIDDEN_WORDS_OPEN, ENoticeType.FORBIDDEN_WORDS_CLOSE, ENoticeType.RED_ENVELOPE_RECEIVED_SELF,
            ENoticeType.FORBIDDEN_WORDS_SINGE, ENoticeType.OPEN_UP_RED_ENVELOPER, ENoticeType.SYS_ENVELOPE_RECEIVED_SELF, ENoticeType.RECEIVE_SYS_ENVELOPE, ENoticeType.SYS_ENVELOPE_RECEIVED,
            ENoticeType.GROUP_FORBID, ENoticeType.GROUP_BAN_WORDS, ENoticeType.CANCEL_CAN_EDIT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ENoticeType {
        int ENTER_BY_QRCODE = 1; //扫二维码进群
        int INVITED = 2; //被邀请进群
        int KICK = 3; //被移出群聊
        int FORTH = 4; //
        int TRANSFER_GROUP_OWNER = 5; //群主转让
        int LEAVE = 6;//离开群聊
        int RED_ENVELOPE_RECEIVED = 7;//xx领取你的云红包
        int RECEIVE_RED_ENVELOPE = 8;//你领取了xx的红包
        int CANCEL = 9;//撤回
        int BLACK_ERROR = 10;//拉黑，消息被拒错误
        int NO_FRI_ERROR = 11;//被删好友，消息发送错误
        int LOCK = 12;//端对端加密
        int CHANGE_VICE_ADMINS_ADD = 13;// 群管理变更通知 新增
        int CHANGE_VICE_ADMINS_CANCEL = 14;// 群管理变更通知 取消
        int FORBIDDEN_WORDS_OPEN = 15;// 接收到群禁言 开
        int FORBIDDEN_WORDS_CLOSE = 16;// 接收到群禁言 关
        int RED_ENVELOPE_RECEIVED_SELF = 17;//自己领取自己的零钱红包
        int FORBIDDEN_WORDS_SINGE = 18;// 单人禁言
        int OPEN_UP_RED_ENVELOPER = 19;// 领取群红包
        int SYS_ENVELOPE_RECEIVED_SELF = 20;//自己领取自己的零钱红包
        int RECEIVE_SYS_ENVELOPE = 21;//你领取了xx的零钱红包
        int SYS_ENVELOPE_RECEIVED = 22;//xx领取你的零钱红包
        int SNAPSHOT_SCREEN = 23;//截屏通知
        int GROUP_FORBID = 24;//封群通知
        int GROUP_BAN_WORDS = 25;//自己开启或者关闭群禁言
        int CANCEL_CAN_EDIT = 26;//能重新编辑的撤销消息
    }

    /*
     * AT 类型
     * */
    @IntDef({EAtType.MULTIPLE, EAtType.ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EAtType {
        int MULTIPLE = 0; // 多个
        int ALL = 1; // 所有人
    }


    /*
     *标签类型
     * */
    @IntDef({ETagType.USER, ETagType.LOCK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ETagType {
        int USER = 0; // 用户
        int LOCK = 1; // 端到端
    }

    /*
     *from
     * */
    @IntDef({EFromType.DEFAULT, EFromType.SEARCH, EFromType.FRIEND, EFromType.GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EFromType {
        int DEFAULT = 0; // 默认
        int SEARCH = 1; // 好友搜索界面
        int FRIEND = 2; // 通讯录好友界面
        int GROUP = 3; // 群详情界面
    }

    /*
     *from
     * */
    @IntDef({EShowType.FUNCTION, EShowType.EMOJI, EShowType.VOICE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EShowType {
        int FUNCTION = 0; // 主功能
        int EMOJI = 1; // emoji
        int VOICE = 2; // 语音
    }

    /*
     *session 消息类型
     * */
    @IntDef({ESessionType.DEFAULT, ESessionType.SINGLE, ESessionType.ALL, ESessionType.DRAFT, ESessionType.ENVELOPE_FAIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ESessionType {
        int DEFAULT = 1000; // 主功能
        int SINGLE = 0; // @单人
        int ALL = 1; // @所有人
        int DRAFT = 2; // 草稿
        int ENVELOPE_FAIL = 3;//红包发送失败
    }

    /*
     *聊天拓展功能id
     * */
    @IntDef({EFunctionId.GALLERY, EFunctionId.TAKE_PHOTO, EFunctionId.ENVELOPE_SYS, EFunctionId.ENVELOPE_MF, EFunctionId.STAMP, EFunctionId.TRANSFER,
            EFunctionId.CARD, EFunctionId.VIDEO_CALL, EFunctionId.LOCATION, EFunctionId.GROUP_ASSISTANT, EFunctionId.FILE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EFunctionId {
        int GALLERY = 1; // 相册
        int TAKE_PHOTO = 2; // 拍摄
        int ENVELOPE_SYS = 3; // 零钱红包
        int TRANSFER = 4;//转账
        int VIDEO_CALL = 5;//视频通话
        int ENVELOPE_MF = 6; // 魔方红包
        int LOCATION = 7;//位置
        int STAMP = 8; //戳一戳
        int CARD = 9;//名片
        int GROUP_ASSISTANT = 10;//群助手
        int FILE = 11;//文件
    }


    /*
     *聊天转发功能id
     * */
    @IntDef({EForwardMode.DEFAULT, EForwardMode.ONE_BY_ONE, EForwardMode.MERGE, EForwardMode.SHARE, EForwardMode.SYS_SEND, EForwardMode.SYS_SEND_MULTI, EForwardMode.EDIT_PIC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EForwardMode {
        int DEFAULT = 0; // 默认，普通单条转发
        int ONE_BY_ONE = 1; // 逐条转发
        int MERGE = 2; // 合并
        int SHARE = 3; // 第三方分享
        int SYS_SEND = 4; // 系统分享或发送
        int SYS_SEND_MULTI = 5; // 系统批量分享或发送
        int EDIT_PIC = 6; // 图片编辑转发
    }


    /*
     *服务器类型
     * */
    @IntDef({EServiceType.DEFAULT, EServiceType.DEBUG, EServiceType.BETA, EServiceType.RELEASE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EServiceType {
        int DEFAULT = 0; // 默认，跟随编译环境
        int DEBUG = 1; // 测试服
        int BETA = 2; // 预发布服
        int RELEASE = 3; // 生产服
    }


    /*
     *群状态, 0正常，1解散，2封禁
     * */
    @IntDef({EGroupStatus.NORMAL, EGroupStatus.DISBAND, EGroupStatus.BANED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EGroupStatus {
        int NORMAL = 0; // 正常
        int DISBAND = 1; // 群被解散
        int BANED = 2; // 群被封禁
    }
}
