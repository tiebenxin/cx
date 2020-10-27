package com.yanlong.im.circle.follow;

import com.yanlong.im.circle.bean.CircleCommentBean;
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

    void onSuccess(int position, MessageFlowItemBean flowItemBean);

    void onCommentSuccess(CircleCommentBean commentBean);

    void onVoteSuccess(int parentPosition, String msg);

    void onLikeSuccess(int position, String msg);

    void onSuccess(int position, boolean isCancelFollow, String msg);

    void onShowMessage(String msg);

    void onCommentSuccess(boolean isAdd);

    void showUnreadMsg(int unCount,String avatar);

    void onDeleteItem(int position);
}
