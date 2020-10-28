package net.cb.cb.library;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
 * 常量封装类，类似于枚举
 * */
public class CoreEnum {

    /*
     * 常量0，1
     * */
    @IntDef({ESureType.NO, ESureType.YES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ESureType {
        int NO = 0;
        int YES = 1;
    }

    /*
     * 用户状态
     * */
    @IntDef({EUserType.DEFAULT, EUserType.DISABLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EUserType {
        int DEFAULT = 0;// 正常
        int DISABLE = 1;// 封号
    }

    /*
     *花名册指令
     *
     * */
    @IntDef({ERosterAction.DEFAULT, ERosterAction.REQUEST_FRIEND, ERosterAction.ACCEPT_BE_FRIENDS, ERosterAction.REMOVE_FRIEND, ERosterAction.UPDATE_INFO,
            ERosterAction.LOAD_ALL_SUCCESS, ERosterAction.BLACK, ERosterAction.PHONE_MATCH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ERosterAction {
        int DEFAULT = 0; // 默认，无指令
        int REQUEST_FRIEND = 1; // 请求添加好友
        int ACCEPT_BE_FRIENDS = 2; // 接收请求添加为好友
        int REMOVE_FRIEND = 3; // 删除好友
        int UPDATE_INFO = 4; // 信息更新
        int LOAD_ALL_SUCCESS = 5; // 所有数据加载完毕
        int BLACK = 6; // 加入黑名单
        int PHONE_MATCH = 7; // 手机通讯匹配
    }


    @IntDef({ENetStatus.SUCCESS_ON_NET, ENetStatus.ERROR_ON_NET, ENetStatus.SUCCESS_ON_SERVER, ENetStatus.ERROR_ON_SERVER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ENetStatus {
        int SUCCESS_ON_NET = 0;//本地网络正常
        int ERROR_ON_NET = 1;//本地网络错误
        int SUCCESS_ON_SERVER = 2;//服务器正常
        int ERROR_ON_SERVER = 3;//服务器错误
    }

    /*
     *聊天类型，单聊还是群聊
     * */
    @IntDef({EChatType.PRIVATE, EChatType.GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EChatType {
        int PRIVATE = 0; // 私聊或单聊
        int GROUP = 1; // 群聊
    }

    /*
     *session刷新类型
     * */
    @IntDef({ESessionRefreshTag.SINGLE, ESessionRefreshTag.ALL, ESessionRefreshTag.DELETE, ESessionRefreshTag.BLACK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ESessionRefreshTag {
        int SINGLE = 0; // 单刷
        int ALL = 1; //全刷
        int DELETE = 2; //删除，删除里面消息
        int BLACK = 3; //黑名单，不删里面消息
    }

    /*
     *语音通话类型
     * */
    @IntDef({VoiceType.WAIT, VoiceType.RECEIVE, VoiceType.CALLING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VoiceType {
        int WAIT = 0; // 呼叫
        int RECEIVE = 1; // 接收
        int CALLING = 2; // 通话
    }

    /*
     *刷新类型
     * */
    @IntDef({ERefreshType.SINGLE, ERefreshType.ALL, ERefreshType.DELETE, ERefreshType.ADD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ERefreshType {
        int SINGLE = 0; // 单刷
        int ALL = 1; //全刷
        int DELETE = 2; //删除
        int ADD = 3; //添加
    }

    /*
     * 常量0，1
     * */
    @IntDef({ECheckType.NO, ECheckType.YES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ECheckType {
        int NO = 0;
        int YES = 1;
    }

    /*
     * 查看大图或视频，长按操作类型
     * */
    @IntDef({EActionType.FORWARD, EActionType.SAVE, EActionType.COLLECTION, EActionType.EDIT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EActionType {
        int FORWARD = 0; // 转发
        int SAVE = 1; // 保存
        int COLLECTION = 2; // 收藏
        int EDIT = 3; // b编辑
    }

    /**
     * 0：展开、收起 1：详情 2文字投票 3图片投票 4评论回复 5点击头像 6 长按
     */
    @IntDef({EClickType.CONTENT_DOWN, EClickType.CONTENT_DETAILS, EClickType.VOTE_CHAR, EClickType.VOTE_PICTURE
            , EClickType.COMMENT_REPLY, EClickType.COMMENT_HEAD, EClickType.COMMENT_LONG, EClickType.FOLLOW, EClickType.ADD_FRIEND,
            EClickType.CHAT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EClickType {
        int CONTENT_DOWN = 0;// 展开、收起
        int CONTENT_DETAILS = 1;// 详情
        int VOTE_CHAR = 2;// 文字投票
        int VOTE_PICTURE = 3;// 图片投票
        int COMMENT_REPLY = 4;// 评论回复
        int COMMENT_HEAD = 5;// 点击头像
        int COMMENT_LONG = 6;// 长按
        int FOLLOW = 7;// 关注
        int ADD_FRIEND = 8;// 加好友
        int CHAT = 9;// 私聊
    }

    @IntDef({ELongType.COPY, ELongType.DELETE, ELongType.REPORT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ELongType {
        int COPY = 1;// 复制
        int DELETE = 2;// 删除
        int REPORT = 3;// 举报
    }
}
