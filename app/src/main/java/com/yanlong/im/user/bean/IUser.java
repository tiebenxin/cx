package com.yanlong.im.user.bean;

/**
 * @author Liszt
 * @date 2020/4/14
 * Description
 */
public interface IUser {
    int getMasterRead();

    int getMyRead();

    int getFriendRead();

    String getVip();

    boolean isChecked();

    String getInviterName();

    int getJoinType();

    String getInviter();

    boolean isEmptyPassword();

    int getActiveType();

    Long getLastonline();

    String getNeteaseAccid();

    String getSayHi();

    int getScreenshotNotification();

    Integer getAuthStat();

    String getMembername();

    Integer getMessagenotice();

    Integer getDisplaydetail();

    Integer getStat();

    String getOldimid();

    String getPhone();

    Integer getPhonefind();

    Integer getImidfind();

    Integer getFriendvalid();

    Integer getGroupvalid();

    Integer getDisturb();

    Integer getIstop();

    int getSex();

    String getImid();

    Integer getuType();

    Long getUid();

    String getName();

    String getName4Show();

    String getHead();

    String getMkName();

    void toTag();

    String getTag();

    String getDescribe();

    int getLockCloudRedEnvelope();

    String getBankReqSignKey();

    boolean isSystemUser();

    String getJoinTime();
}
