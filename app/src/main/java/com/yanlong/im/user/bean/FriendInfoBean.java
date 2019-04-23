package com.yanlong.im.user.bean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FriendInfoBean implements Comparable<FriendInfoBean>{
    private String name;
    private String id;
    private String head;
    private String tag;
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
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
