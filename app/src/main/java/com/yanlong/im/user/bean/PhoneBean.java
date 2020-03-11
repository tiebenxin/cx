package com.yanlong.im.user.bean;

import net.cb.cb.library.base.BaseBean;

/**
 * @author Liszt
 * @date 2020/3/11
 * Description
 */
public class PhoneBean extends BaseBean {
    private String phoneremark;
    private String phone;

    public String getName() {
        return phoneremark;
    }

    public void setName(String phoneremark) {
        this.phoneremark = phoneremark;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
