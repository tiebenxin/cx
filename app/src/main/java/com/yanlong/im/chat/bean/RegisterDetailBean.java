package com.yanlong.im.chat.bean;

import android.os.Parcel;
import android.os.Parcelable;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.base.BaseBean;

/**
 * @author Liszt
 * @date 2020/11/16
 * Description 注册完善资料bean
 */
public class RegisterDetailBean extends BaseBean implements Parcelable {
    @CoreEnum.ESexType
    private int sex = 0;//默认值，0表示未知，1男，2女
    private long birthday = -1;
    private int height = 0;
    private String location;
    private String nick;
    private String avatar;
    private boolean isBirthDayInit = false;

    public RegisterDetailBean() {

    }

    protected RegisterDetailBean(Parcel in) {
        sex = in.readInt();
        birthday = in.readLong();
        height = in.readInt();
        location = in.readString();
        nick = in.readString();
        avatar = in.readString();
        isBirthDayInit = in.readByte() != 0;
    }

    public static final Creator<RegisterDetailBean> CREATOR = new Creator<RegisterDetailBean>() {
        @Override
        public RegisterDetailBean createFromParcel(Parcel in) {
            return new RegisterDetailBean(in);
        }

        @Override
        public RegisterDetailBean[] newArray(int size) {
            return new RegisterDetailBean[size];
        }
    };

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        isBirthDayInit = true;
        this.birthday = birthday;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isBirthDayInit() {
        return isBirthDayInit;
    }

    public void setBirthDayInit(boolean birthDayInit) {
        isBirthDayInit = birthDayInit;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sex);
        dest.writeLong(birthday);
        dest.writeInt(height);
        dest.writeString(location);
        dest.writeString(nick);
        dest.writeString(avatar);
        dest.writeByte((byte) (isBirthDayInit ? 1 : 0));
    }
}
