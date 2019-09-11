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
    @IntDef({ERosterAction.DEFAULT, ERosterAction.REQUEST_FRIEND, ERosterAction.ACCEPT_BE_FRIENDS, ERosterAction.REMOVE_FRIEND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ERosterAction {
        int DEFAULT = 0; // 默认，无指令
        int REQUEST_FRIEND = 1; // 请求添加好友
        int ACCEPT_BE_FRIENDS = 2; // 接收请求添加为好友
        int REMOVE_FRIEND = 3; // 删除好友
    }


}
