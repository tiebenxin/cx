package com.yanlong.im.circle.follow;

import com.yanlong.im.circle.bean.MessageFlowItemBean;

import net.cb.cb.library.base.bind.IBaseView;

import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/9/7 0007
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public interface FollowView extends IBaseView {

    void onSuccess(List<MessageFlowItemBean> list);

    void onShowMessage(String msg);
}
