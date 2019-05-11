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

 //   private RealmList<MsgAllBean> msgs;

    //用户类型 0:陌生人或者群友,1:自己,2:通讯录,3黑名单
    private Integer uType;



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
        return head;
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
