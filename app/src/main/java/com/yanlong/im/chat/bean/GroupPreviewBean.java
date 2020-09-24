package com.yanlong.im.chat.bean;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import net.cb.cb.library.base.BaseBean;

import java.util.List;

/**
 * @author Liszt
 * @date 2020/9/15
 * Description 分组浏览bean
 */
public class GroupPreviewBean extends BaseBean implements Comparable<GroupPreviewBean> {
    String time;
    List<MsgAllBean> msgAllBeans;
    long startTime;
    long endTime;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<MsgAllBean> getMsgAllBeans() {
        return msgAllBeans;
    }

    public void setMsgAllBeans(List<MsgAllBean> msgAllBeans) {
        this.msgAllBeans = msgAllBeans;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public int compareTo(GroupPreviewBean o) {
        if (o == null) {
            return 0;
        }

        return 0;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        try {
            if (obj != null && !TextUtils.isEmpty(this.time) && !TextUtils.isEmpty(((GroupPreviewBean) obj).time) && this.time.equals(((GroupPreviewBean) obj).time)) {
                return true;
            }
        } catch (Exception e) {

        }

        return false;
    }

    public boolean isBetween(long time) {
        if (time >= startTime && time <= endTime) {
            return true;
        }
        return false;
    }
}
