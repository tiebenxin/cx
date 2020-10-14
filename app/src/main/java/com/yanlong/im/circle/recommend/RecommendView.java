package com.yanlong.im.circle.recommend;

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
public interface RecommendView extends IBaseView {
    void onSuccess(List<MessageFlowItemBean> list);

    void onShowMessage(String msg);

    void onLikeSuccess(int position, String msg);

    void onSuccess(int position, boolean isFollow, String msg);

    void addSeeSuccess(String msg);

    void onSuccess(int position, MessageFlowItemBean flowItemBean);

    void onVoteSuccess(int parentPostion, String msg);
}
