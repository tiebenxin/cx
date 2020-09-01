package com.yanlong.im.user.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Liszt
 * @date 2020/3/11
 * Description
 */
public class PhoneBean extends RealmObject {
    private String phoneremark;
    private String phone;

    public String getPhoneremark() {
        return phoneremark;
    }

    public void setPhoneremark(String phoneremark) {
        this.phoneremark = phoneremark;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
