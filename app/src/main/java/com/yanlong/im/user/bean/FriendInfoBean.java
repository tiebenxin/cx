package com.yanlong.im.user.bean;

import android.text.TextUtils;

import net.cb.cb.library.base.BaseBean;
import net.cb.cb.library.utils.CheckUtil;
import net.cb.cb.library.utils.PinyinUtil;
import net.cb.cb.library.utils.StringUtil;
import net.sourceforge.pinyin4j.PinyinHelper;

public class FriendInfoBean extends BaseBean implements Comparable<FriendInfoBean> {
    private String nickname;// 用户昵称
    private Long uid;//　用户ｉｄ
    private String avatar;// 头像
    private String tag;// 首字母拼音
    private String imid;
    private int gender;// 性别
    private int switchmask;
    private String alias;
    private String phoneremark;// 电话备注
    private String phone;// 手机号
    private boolean isShowPinYin;// 是否显示拼音
    private boolean isRegister;// 是否显示邀请
    private long createTime;// 创建时间

    public String getPhoneremark() {
        return phoneremark;
    }

    public void setPhoneremark(String phoneremark) {
        this.phoneremark = phoneremark;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        toTag();
    }

    public boolean isShowPinYin() {
        return isShowPinYin;
    }

    public void setShowPinYin(boolean showPinYin) {
        isShowPinYin = showPinYin;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getImid() {
        return imid;
    }

    public void setImid(String imid) {
        this.imid = imid;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getSwitchmask() {
        return switchmask;
    }

    public void setSwitchmask(int switchmask) {
        this.switchmask = switchmask;
    }

    public boolean isRegister() {
        return isRegister;
    }

    public void setRegister(boolean register) {
        isRegister = register;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    /***
     * 重设tag

     */
    public void toTag() {
        if (!TextUtils.isEmpty(nickname) && CheckUtil.isMobileNO(nickname)) {
            nickname = phoneremark;
        }
        if (TextUtils.isEmpty(nickname)) {
            setTag("#");
        } else if (!("" + nickname.charAt(0)).matches("^[0-9a-zA-Z\\u4e00-\\u9fa5]+$")) {
            setTag("#");
        } else {
            String[] n = PinyinHelper.toHanyuPinyinStringArray(nickname.charAt(0));
            if (n == null) {
                if (StringUtil.ifContainEmoji(nickname)) {
                    setTag("#");
                } else {
                    setTag("" + (nickname.toUpperCase()).charAt(0));
                }
            } else {
                String value = "";
                // 判断是否为多音字
                if (n.length > 1) {
                    value = PinyinUtil.getUserName(nickname.charAt(0) + "");
                    if (TextUtils.isEmpty(value)) {
                        setTag("" + n[0].toUpperCase().charAt(0));
                    } else {
                        setTag(value);
                    }
                } else {
                    setTag("" + n[0].toUpperCase().charAt(0));
                }
            }
        }
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        if (tag.hashCode() < 65 || tag.hashCode() > 91) {
            tag = "#";
        }
        this.tag = tag;
    }

    @Override
    public int compareTo(FriendInfoBean o) {
        int last = getTag().charAt(0);
        if (getTag().equals("#")) {
            return 1;
        }
        if (last > o.getTag().charAt(0)) {
            return 1;
        }
        return -1;

    }
}
