package com.yanlong.im.user.bean;

import net.cb.cb.library.base.BaseBean;

import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/8/6 0006
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class AddressBookMatchingBean extends BaseBean {

    private List<FriendInfoBean> matchList;
    private List<NotExistListBean> notExistList;

    public List<FriendInfoBean> getMatchList() {
        return matchList;
    }

    public void setMatchList(List<FriendInfoBean> matchList) {
        this.matchList = matchList;
    }

    public List<NotExistListBean> getNotExistList() {
        return notExistList;
    }

    public void setNotExistList(List<NotExistListBean> notExistList) {
        this.notExistList = notExistList;
    }

    public static class NotExistListBean extends BaseBean{
        /**
         * phone : 15075869899
         * phoneremark : 幸福
         */

        private String phone;
        private String phoneremark;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPhoneremark() {
            return phoneremark;
        }

        public void setPhoneremark(String phoneremark) {
            this.phoneremark = phoneremark;
        }
    }
}
