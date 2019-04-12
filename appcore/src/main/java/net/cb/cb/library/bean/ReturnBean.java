package net.cb.cb.library.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @author jyj
 * @date 2016/12/20
 */
public class ReturnBean<T> {
    String code;
    String msg;
    T data;

    public Boolean isOk() {
        return code.equals("000000");
    }
    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }
}
