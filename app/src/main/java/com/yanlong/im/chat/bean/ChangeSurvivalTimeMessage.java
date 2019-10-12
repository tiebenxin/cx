package com.yanlong.im.chat.bean;

/**
 * @创建人 shenxin
 * @创建时间 2019/10/11 0011 11:53
 */
public class ChangeSurvivalTimeMessage {
    //阅后即焚开关通知(-1:退出即焚|0:关 1消息有效时间(秒))
    private int survival_time;

    public int getSurvival_time() {
        return survival_time;
    }

    public void setSurvival_time(int survival_time) {
        this.survival_time = survival_time;
    }
}
