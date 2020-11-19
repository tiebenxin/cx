package com.yanlong.im.user.bean;


import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.yanlong.im.chat.ChatEnum;

import net.cb.cb.library.manager.Constants;
import net.cb.cb.library.utils.PinyinUtil;
import net.cb.cb.library.utils.StringUtil;
import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * 好友信息
 * 注意：不可在get方法中赋值，对于Alive的对象会造成写操作
 */
public class UserInfo extends RealmObject implements Comparable<UserInfo>, IUser {
    @PrimaryKey
    private Long uid;
    @SerializedName("nickname")
    private String name;//昵称
    @SerializedName("alias")
    private String mkName;//备注名
    @SerializedName("gender")
    private int sex;
    private String imid;
    //数据库中，数字存储的是Z1（非#） >Z，便于排序
    private String tag;
    private String pinyin;//存储的备注名/昵称全拼音，数字TODO
    private String pinyinHead;// 存储的备注名/昵称简拼音
    @SerializedName("avatar")
    private String head;
    //用户类型 0:陌生人或者群友,1:自己,2:通讯录,3黑名单,4小助手
    private Integer uType;
    private String phone;
    private String oldimid;
    private String neteaseAccid;// 网易id
    private String vip;// (0:普通|1:vip)

    @SerializedName("friendLockUser")
    private int lockedstatus;// 好友状态 (0:解封|1:锁定)
    private Integer disturb;///消息免打扰(0:关闭|1:打开)
    private Integer istop;//聊天置顶(0:关闭|1:打开)
    private Integer phonefind;//通过手机号找到自己(0:关闭|1:打开)
    private Integer imidfind;//通过常信号找到自己(0:关闭|1:打开)
    private Integer friendvalid;//加我为朋友时需要验证(0:关闭|1:打开)
    private Integer groupvalid; //允许被直接添加至群聊(0:关闭|1:打开)
    private Integer messagenotice;//新消息通知(0:关闭|1:打开)
    private Integer displaydetail;//显示详情(0:关闭|1:打开)
    private Integer stat; //好友状态(0:正常|1:待同意|2:黑名单|9:系统用户，如小助手)
    private Integer authStat; //身份验证状态(0:未认证|1:已认证未上传证件照|2:已认证已上传证件照)
    private int screenshotNotification;//截屏通知(0:关闭|1:打开)
    private int masterRead;//已读总开关(0:关闭|1:打开)
    private int myRead;//我对100101是否开了已读(0:否|1:是)
    private int friendRead;//100101对我是否开了已读(0:否|1:是)

    private boolean emptyPassword = false;// 是否未设置密码

    //阅后即焚
//    private Integer destroy = 1;
//    private Long destroyTime = 30L;

    @Ignore
    private String membername;//群的昵称
    private String sayHi;//待同意好友招呼语
    //通讯录存储数字的tag
    @Ignore
    public static final String FRIEND_NUMBER_TAG = "Z1";//存到数据库符号，方便排序
    @Ignore
    public static final String FRIEND_NUMBER_SHOW_TAG = "#";//显示的符号
    private Long lastonline;
    private int activeType; //是否在线（0：离线|1：在线）
    private String describe; //用户描述
    private int lockCloudRedEnvelope; //1锁定红包，0不锁定
    @SerializedName("survivaltime")
    private int destroy = 0; //销毁开关
    private long destroyTime; //销毁时间
    private int joinType;
    private String joinTime;
    private String inviter;
    private String inviterName;
    @Ignore
    private boolean isChecked = false;

    private String bankReqSignKey;//支付签名
    private int lockedFunctions;//封锁功能：0 不封，1 封

    private int deactivateStat;//注销状态 0 正常 1 注销中 -1 已注销
    private int friendDeactivateStat;//好友注销状态 0 正常 1 注销中 -1 已注销

    //新增->朋友圈最新动态
    @Ignore
    private List<String> momentList;

    public List<String> getMomentList() {
        return momentList;
    }

    public void setMomentList(List<String> momentList) {
        this.momentList = momentList;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public int getMasterRead() {
        return masterRead;
    }

    public void setMasterRead(int masterRead) {
        this.masterRead = masterRead;
    }

    public int getMyRead() {
        return myRead;
    }

    public void setMyRead(int myRead) {
        this.myRead = myRead;
    }

    public int getFriendRead() {
        return friendRead;
    }

    public void setFriendRead(int friendRead) {
        this.friendRead = friendRead;
    }

    public Integer getDestroy() {
        return destroy;
    }

    public void setDestroy(Integer destroy) {
        this.destroy = destroy;
    }

    public Long getDestroyTime() {
        return destroyTime;
    }

    public void setDestroyTime(Long destroyTime) {
        this.destroyTime = destroyTime;
    }

    public int getLockedstatus() {
        return lockedstatus;
    }

    public void setLockedstatus(int lockedstatus) {
        this.lockedstatus = lockedstatus;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getInviterName() {
        return inviterName;
    }

    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    public int getJoinType() {
        return joinType;
    }

    public String getPinyinHead() {
        return pinyinHead;
    }

    public void setPinyinHead(String pinyinHead) {
        this.pinyinHead = pinyinHead;
    }

    public void setJoinType(int joinType) {
        this.joinType = joinType;
    }

    public String getJoinTime() {
        return joinTime;
    }

    @Override
    public int getHistoryClear() {
        return 0;
    }

    @Override
    public int getInfoStat() {
        return 0;
    }

    @Override
    public long getBirthday() {
        return 0;
    }

    @Override
    public String getLocation() {
        return "";
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public boolean isEmptyPassword() {
        return emptyPassword;
    }

    public void setEmptyPassword(boolean emptyPassword) {
        this.emptyPassword = emptyPassword;
    }

    public int getActiveType() {
        return activeType;
    }

    public void setActiveType(int activeType) {
        this.activeType = activeType;
    }

    public Long getLastonline() {
        return lastonline == null ? 0L : lastonline;
    }

    public String getNeteaseAccid() {
        return neteaseAccid;
    }

    public void setNeteaseAccid(String neteaseAccid) {
        this.neteaseAccid = neteaseAccid;
    }

    public void setLastonline(Long lastonline) {
        this.lastonline = lastonline;
    }

    public String getSayHi() {
        return sayHi;
    }

    public void setSayHi(String sayHi) {
        this.sayHi = sayHi;
    }

    public int getScreenshotNotification() {
        return screenshotNotification;
    }

    public void setScreenshotNotification(int screenshotNotification) {
        this.screenshotNotification = screenshotNotification;
    }

    public Integer getAuthStat() {
        if (authStat == null) {
            authStat = 0;
        }
        return authStat;
    }

    public void setAuthStat(Integer authStat) {
        this.authStat = authStat;
    }

    public String getMembername() {
        return membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
    }

    public Integer getMessagenotice() {
        return messagenotice == null ? 0 : messagenotice;
    }

    public void setMessagenotice(Integer messagenotice) {
        this.messagenotice = messagenotice;
    }

    public Integer getDisplaydetail() {
        return displaydetail == null ? 0 : displaydetail;
    }

    public void setDisplaydetail(Integer displaydetail) {
        this.displaydetail = displaydetail;
    }

    public Integer getStat() {
        //stat== null 一定是非好友
        return stat == null ? 1 : stat;
    }

    public void setStat(Integer stat) {
        this.stat = stat;
    }

    public String getOldimid() {
        return oldimid;
    }

    public void setOldimid(String oldimid) {
        this.oldimid = oldimid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPhonefind() {
        return phonefind == null ? 0 : phonefind;
    }

    public void setPhonefind(Integer phonefind) {
        this.phonefind = phonefind;
    }

    public Integer getImidfind() {
        return imidfind == null ? 0 : imidfind;
    }

    public void setImidfind(Integer imidfind) {
        this.imidfind = imidfind;
    }

    public Integer getFriendvalid() {
        return friendvalid == null ? 0 : friendvalid;
    }

    public void setFriendvalid(Integer friendvalid) {
        this.friendvalid = friendvalid;
    }

    public Integer getGroupvalid() {
        return groupvalid == null ? 0 : groupvalid;
    }

    public void setGroupvalid(Integer groupvalid) {
        this.groupvalid = groupvalid;
    }

    public Integer getDisturb() {
        return disturb == null ? 0 : disturb;
    }

    public void setDisturb(Integer disturb) {
        this.disturb = disturb;
    }

    public Integer getIstop() {
        return istop == null ? 0 : istop;
    }

    public void setIstop(Integer istop) {
        this.istop = istop;
    }


    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getImid() {
        return imid;
    }

    public void setImid(String imid) {
        this.imid = imid;
    }

    //用户类型 0:陌生人或者群友,1:自己,2:通讯录,3黑名单(不区分和陌生人)
    public Integer getuType() {
        return uType == null ? 0 : uType;
    }

    public void setuType(Integer uType) {
        this.uType = uType;
    }


    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    /***
     * 显示的名称
     * @return
     */
    public String getName4Show() {
        return StringUtil.isNotNull(mkName) ? mkName : name;
    }


    public void setName(String name) {
        this.name = name == null ? "" : name;
        toTag();
    }

    public String getHead() {
        return head == null ? "" : head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getMkName() {
        return mkName;
    }

    public void setMkName(String mkName) {
        this.mkName = mkName;
        toTag();
    }


    /***
     * 重设tag
     */
    public void toTag() {
        try {
            String name = StringUtil.isNotNull(this.mkName) ? this.mkName : this.name;
            if (TextUtils.isEmpty(name)) {
                setTag(FRIEND_NUMBER_TAG);
                setPinyin("");
                setPinyinHead("");
            } else if (!("" + name.charAt(0)).matches("^[0-9a-zA-Z\\u4e00-\\u9fa5]+$")) {
                setTag(FRIEND_NUMBER_TAG);
                setPinyin(PinyinUtil.toPinyin(name));
                setPinyinHead(PinyinUtil.getPinYinHeadChar(name));
            } else {
                setPinyinHead(PinyinUtil.getPinYinHeadChar(name));
                String[] n = PinyinHelper.toHanyuPinyinStringArray(name.charAt(0));
                if (n == null) {
                    if (StringUtil.ifContainEmoji(name)) {
                        setTag(FRIEND_NUMBER_TAG);
                        setPinyin("");
                    } else {
                        setTag("" + (name.toUpperCase()).charAt(0));
                        setPinyin(PinyinUtil.toPinyin(name));
                    }
                } else {
                    String value = "";
                    // 判断是否为多音字
                    if (n.length > 1) {
                        value = PinyinUtil.getUserName(name.charAt(0) + "");
                        if (TextUtils.isEmpty(value)) {
                            setTag("" + n[0].toUpperCase().charAt(0));
                        } else {
                            setTag(value);
                        }
                    } else {
                        setTag("" + n[0].toUpperCase().charAt(0));
                    }
                    setPinyin(PinyinUtil.toPinyin(name));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTag(String tag) {
        if (tag.hashCode() < 65 || tag.hashCode() > 91) {//非字母开头
            tag = FRIEND_NUMBER_TAG;
        }
        this.tag = tag;
    }

    @Override
    public int compareTo(UserInfo o) {
        if (TextUtils.isEmpty(getTag()) || (o == null || TextUtils.isEmpty(o.getTag()))) {
            return -1;
        }
        int last = getTag().charAt(0);
        if (getTag().equals(FRIEND_NUMBER_TAG)) {
            return 1;
        }
        if (o.getTag().equals(FRIEND_NUMBER_TAG)) {
            return -1;
        }

        if (getTag().equals("↑")) {
            return -1;
        }
        if (o.getTag().equals("↑")) {
            return 1;
        }

        if (last > o.getTag().charAt(0)) {
            return 1;
        } else if (last < o.getTag().charAt(0)) {
            return -1;
        } else {
            return 0;
        }
    }

    public String getTag() {
        //数据库中存储的是Z1，便于排序
        return TextUtils.isEmpty(tag) || tag.equals(FRIEND_NUMBER_TAG) ? FRIEND_NUMBER_SHOW_TAG : tag;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getLockCloudRedEnvelope() {
        return lockCloudRedEnvelope;
    }

    public void setLockCloudRedEnvelope(int lockCloudRedEnvelope) {
        this.lockCloudRedEnvelope = lockCloudRedEnvelope;
    }

    public String getBankReqSignKey() {
        return bankReqSignKey;
    }

    public void setBankReqSignKey(String bankReqSignKey) {
        this.bankReqSignKey = bankReqSignKey;
    }

    public int getLockedFunctions() {
        return lockedFunctions;
    }

    public void setLockedFunctions(int lockedFunctions) {
        this.lockedFunctions = lockedFunctions;
    }

    public int getDeactivateStat() {
        return deactivateStat;
    }

    public void setDeactivateStat(int deactivateStat) {
        this.deactivateStat = deactivateStat;
    }

    public int getFriendDeactivateStat() {
        return friendDeactivateStat;
    }

    public void setFriendDeactivateStat(int friendDeactivateStat) {
        this.friendDeactivateStat = friendDeactivateStat;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || this.uid == null || ((UserInfo) obj).uid == null) {
            return false;
        }
        if (obj instanceof UserInfo) {
            if (((UserInfo) obj).uid.equals(this.uid)) {
                return true;
            }
        }
        return false;
    }

    //判断该用户是否官方系统用户
    public boolean isSystemUser() {
        if (uid == null) {
            return false;
        }
        if (uid.equals(Constants.CX888_UID) || uid.equals(Constants.CX999_UID) || uid.equals(Constants.CX_HELPER_UID) || (uType != null && uType.intValue() == ChatEnum.EUserType.ASSISTANT)) {
            return true;
        }
        return false;
    }
}
