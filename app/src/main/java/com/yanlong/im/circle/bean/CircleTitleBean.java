package com.yanlong.im.circle.bean;

import net.cb.cb.library.base.bind.BaseModel;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-09
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class CircleTitleBean extends BaseModel {
    private String title;
    private boolean isCheck;

    public CircleTitleBean(String title, boolean check) {
        this.title = title;
        this.isCheck = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
