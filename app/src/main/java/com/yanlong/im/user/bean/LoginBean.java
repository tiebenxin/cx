package com.yanlong.im.user.bean;

public class LoginBean {
    private String password;
    private Long phone;
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }
    public Long getPhone() {
        return phone;
    }
}
