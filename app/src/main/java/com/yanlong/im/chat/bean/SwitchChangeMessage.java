package com.yanlong.im.chat.bean;

import io.realm.RealmObject;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-11-13
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class SwitchChangeMessage extends RealmObject {
    private int switch_type;
    //    enum SwitchType {
//    READ = 0; // 单聊已读
//    VIP = 1; // vip变更
//    MASTER_READ = 2; //已读总开关
//}
    private int switch_value;//开关值(0:关闭|1:打开)

    public int getSwitch_type() {
        return switch_type;
    }

    public void setSwitch_type(int switch_type) {
        this.switch_type = switch_type;
    }

    public int getSwitch_value() {
        return switch_value;
    }

    public void setSwitch_value(int switch_value) {
        this.switch_value = switch_value;
    }
}