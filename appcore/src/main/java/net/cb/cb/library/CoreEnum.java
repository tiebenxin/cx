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
     *花名册指令
     *
     * */
    @IntDef({ERosterAction.DEFAULT, ERosterAction.REQUEST_FRIEND, ERosterAction.ACCEPT_BE_FRIENDS, ERosterAction.REMOVE_FRIEND, ERosterAction.UPDATE_INFO, ERosterAction.LOAD_ALL_SUCESS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ERosterAction {
        int DEFAULT = 0; // 默认，无指令
        int REQUEST_FRIEND = 1; // 请求添加好友
        int ACCEPT_BE_FRIENDS = 2; // 接收请求添加为好友
        int REMOVE_FRIEND = 3; // 删除好友
        int UPDATE_INFO = 4; // 信息更新
        int LOAD_ALL_SUCESS = 5; // 所有数据加载完毕
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
    @IntDef({ESessionRefreshTag.SINGLE, ESessionRefreshTag.ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ESessionRefreshTag {
        int SINGLE = 0; // 单刷
        int ALL = 1; //全刷
    }


}
