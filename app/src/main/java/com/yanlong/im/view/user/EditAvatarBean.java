package com.yanlong.im.view.user;

import androidx.annotation.Nullable;

import com.yanlong.im.chat.bean.MemberUser;

import net.cb.cb.library.base.BaseBean;

/**
 * @author Liszt
 * @date 2020/8/25
 * Description 头像增删bean
 */
public class EditAvatarBean extends BaseBean {
    private int deleteCount = 2;//可删除次数，默认删除两次才是真的删除
    private MemberUser user;

    public EditAvatarBean(MemberUser member) {
        deleteCount = 2;
        user = member;
    }

    public int getDeleteCount() {
        return deleteCount;
    }

    public void setDeleteCount(int deleteCount) {
        this.deleteCount = deleteCount;
    }

    public MemberUser getUser() {
        return user;
    }

    public void setUser(MemberUser user) {
        this.user = user;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == null || ((EditAvatarBean) o).getUser() == null) {
            return false;
        }
        if (o instanceof EditAvatarBean) {
            EditAvatarBean bean = (EditAvatarBean) o;
            if (bean.getUser().getMemberId().equals(this.user.getMemberId())) {
                return true;
            }
        }
        return false;
    }
}
