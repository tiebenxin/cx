package com.yanlong.im.user.bean;

import com.google.gson.annotations.SerializedName;
import com.yanlong.im.chat.bean.MsgAllBean;

import net.cb.cb.library.utils.StringUtil;
import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class UserInfo extends RealmObject implements Comparable<UserInfo> {
    @PrimaryKey
    private Long uid;
    @SerializedName("nickname")
    private String name;
    @SerializedName("alias")
    private String mkName;
    @SerializedName("gender")
    private int sex;
    private String imid;
    private String tag;
    @SerializedName("avatar")
    private String head;
    //用户类型 0:陌生人或者群友,1:自己,2:通讯录,3黑名单
    private Integer uType;


    private Integer disturb;///消息免打扰(0:关闭|1:打开)
    private Integer istop;//聊天置顶(0:关闭|1:打开)
    private Integer phonefind;//通过手机号找到自己(0:关闭|1:打开)
    private Integer imidfind;//通过产品号找到自己(0:关闭|1:打开)
    private Integer friendvalid;//加我为朋友时需要验证(0:关闭|1:打开)
    private Integer groupvalid; //允许被直接添加至群聊(0:关闭|1:打开)

    public Integer getPhonefind() {
        return phonefind;
    }

    public void setPhonefind(Integer phonefind) {
        this.phonefind = phonefind;
    }

    public Integer getImidfind() {
        return imidfind;
    }

    public void setImidfind(Integer imidfind) {
        this.imidfind = imidfind;
    }

    public Integer getFriendvalid() {
        return friendvalid;
    }

    public void setFriendvalid(Integer friendvalid) {
        this.friendvalid = friendvalid;
    }

    public Integer getGroupvalid() {
        return groupvalid;
    }

    public void setGroupvalid(Integer groupvalid) {
        this.groupvalid = groupvalid;
    }

    public Integer getDisturb() {
        return disturb;
    }

    public void setDisturb(Integer disturb) {
        this.disturb = disturb;
    }

    public Integer getIstop() {
        return istop;
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
        return uType;
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
        return StringUtil.isNotNull(mkName)?mkName:name;
    }


    public void setName(String name) {

        this.name = name;

        toTag();

    }

    public String getHead() {
        return head==null?"":head;
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
    public void toTag(){
        String name=StringUtil.isNotNull(this.mkName)?this.mkName:this.name;
        String[] n= PinyinHelper.toHanyuPinyinStringArray(name.charAt(0));
        if (n==null){
            setTag( ""+(name.toUpperCase()).charAt(0));
        }else{
            setTag(""+n[0].toUpperCase().charAt(0));
        }
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
    public int compareTo(UserInfo o) {
        int last=getTag().charAt(0);
        if(getTag().equals("#")){
            return 1;
        }
        if (last>o.getTag().charAt(0)){
            return 1;
        }
        return -1;

    }

    public String getTag() {
        return tag;
    }
}
