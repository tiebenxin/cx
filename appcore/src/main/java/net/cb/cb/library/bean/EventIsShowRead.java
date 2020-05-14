package net.cb.cb.library.bean;

import android.support.annotation.IntDef;

import net.cb.cb.library.event.BaseEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @创建人 shenxin
 * @创建时间 2019/11/7 0007 11:22
 */
public class EventIsShowRead extends BaseEvent {
    long uid;
    @EReadSwitchType
    int switchType;
    int result;

    public EventIsShowRead(long uid,int switchT, int value) {
        this.uid = uid;
        switchType = switchT;
        result = value;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getSwitchType() {
        return switchType;
    }

    public void setSwitchType(int switchType) {
        this.switchType = switchType;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    /*
     *已读开关类型
     * */
    @IntDef({EReadSwitchType.SWITCH_FRIEND, EReadSwitchType.SWITCH_MASTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EReadSwitchType {
        int SWITCH_FRIEND = 0; // 好友已读开关
        int SWITCH_MASTER = 1; // 好友已读总开关
    }

}
