package com.yanlong.im.pay.ui.select;

import com.yanlong.im.chat.bean.MemberUser;

/**
 * @author Liszt
 * @date 2020/8/26
 * Description 编辑（增删）头像监听
 */
public interface IEditAvatarListener {

    void remove(MemberUser user);

    void add(MemberUser user);

    void clear();
}
