package com.yanlong.im.circle.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.MultipleItemRvAdapter;
import com.yanlong.im.circle.bean.CircleCommentBean;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.interf.ICircleClickListener;

import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-09
 * @updateAuthor
 * @updateDate
 * @description 朋友圈适配器 适用于不同类别的样式
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class CircleFlowAdapter extends MultipleItemRvAdapter<MessageFlowItemBean, BaseViewHolder> {

    public static final int MESSAGE_DEFAULT = 0;// 默认
    public static final int MESSAGE_VOTE = 1;// 默认
    private boolean isDetails, isFollow;
    private ICircleClickListener clickListener;
    private List<CircleCommentBean> commentList;

    public CircleFlowAdapter(@Nullable List<MessageFlowItemBean> data, boolean isFollow,
                             boolean isDetails, ICircleClickListener listener, List<CircleCommentBean> commentList) {
        super(data);
        this.isDetails = isDetails;
        this.clickListener = listener;
        this.isFollow = isFollow;
        this.commentList = commentList;
        finishInitialize();
    }

    public List<CircleCommentBean> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CircleCommentBean> commentList) {
        this.commentList = commentList;
    }

    @Override
    protected int getViewType(MessageFlowItemBean messageFlowItemBean) {
        return messageFlowItemBean.getItemType();
    }

    @Override
    public void registerItemProvider() {
        mProviderDelegate.registerProvider(new FollowProvider(isDetails, isFollow, clickListener, commentList));
        mProviderDelegate.registerProvider(new VoteProvider(isDetails, isFollow, clickListener, commentList));
    }
}
