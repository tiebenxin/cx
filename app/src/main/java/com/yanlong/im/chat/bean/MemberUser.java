package com.yanlong.im.chat.bean;


import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/*
* 群成员，用户表
* */
public class MemberUser extends RealmObject /*implements Comparable<MemberUser>*/ {
    private Long uid;
    private String gid;
    @SerializedName("nickname")
    private String name;//昵称
    @SerializedName("gender")
    private int sex;
    private String imid;//产品号
    @SerializedName("avatar")
    private String head;//头像
    private String membername;//群的昵称
    private int joinType;
    private String joinTime;
    private String inviter;
    private String inviterName;
    @Ignore
    private boolean isChecked = false;

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

    public void setJoinType(int joinType) {
        this.joinType = joinType;
    }

    public String getJoinTime() {
        return joinTime;
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

    public String getMembername() {
        return membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
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

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getHead() {
        return head == null ? "" : head;
    }

    public void setHead(String head) {
        this.head = head;
    }


//    public void setTag(String tag) {
//        Pattern pattern = Pattern.compile("[0-9]");
//        Matcher isNum = pattern.matcher(tag);
//        if (isNum.matches()) {
//            tag = "#";
//        }
//
//        this.tag = tag;
//    }

//    @Override
//    public int compareTo(MemberUser o) {
//
//        int last = getTag().charAt(0);
//        if (getTag().equals("#")) {
//            return 1;
//        }
//        if (o.getTag().equals("#")) {
//            return -1;
//        }
//
//        if (getTag().equals("↑")) {
//            return -1;
//        }
//        if (o.getTag().equals("↑")) {
//            return 1;
//        }
//
//        if (last > o.getTag().charAt(0)) {
//            return 1;
//        } else if (last < o.getTag().charAt(0)) {
//            return -1;
//        } else {
//            return 0;
//        }
//    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || this.uid == null || ((MemberUser) obj).uid == null) {
            return false;
        }
        if (obj instanceof MemberUser) {
            if (((MemberUser) obj).uid.equals(this.uid)) {
                return true;
            }
        }
        return false;
    }
}
