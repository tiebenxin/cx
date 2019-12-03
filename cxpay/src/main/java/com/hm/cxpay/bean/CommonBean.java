package com.hm.cxpay.bean;

/**
 * @类名：通用实体类
 * @Date：2019/12/3
 * @by zjy
 * @备注：
 */
public class CommonBean {

    //充值
    private int code = 0;//状态码(1:成功 2:失败 99:处理中)
    //

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
