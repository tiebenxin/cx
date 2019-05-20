package com.yanlong.im.user.bean;

import net.cb.cb.library.utils.StringUtil;
import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FriendInfoBean implements Comparable<FriendInfoBean>{
    private String nickname;
    private Long uid;
    private String avatar;
    private String tag;
    private String imid;
    private int gender;
    private int switchmask;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        toTag();
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
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

    /***
     * 重设tag

     */
    public void toTag(){
        String[] n= PinyinHelper.toHanyuPinyinStringArray(nickname.charAt(0));
        if (n==null){
            setTag( ""+(nickname.toUpperCase()).charAt(0));
        }else{
            setTag(""+n[0].toUpperCase().charAt(0));
        }
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
            Pattern pattern = Pattern.compile("[0-9]");
            Matcher isNum = pattern.matcher(tag);
            if( isNum.matches() ){
                tag="#";
            }

        this.tag = tag;
    }

    @Override
    public int compareTo(FriendInfoBean o) {
        int last=getTag().charAt(0);
        if(getTag().equals("#")){
            return 1;
        }
        if (last>o.getTag().charAt(0)){
            return 1;
        }
        return -1;

    }
}
