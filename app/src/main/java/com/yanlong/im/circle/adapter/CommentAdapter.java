package com.yanlong.im.circle.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.CircleCommentBean;
import com.yanlong.im.utils.CommonUtils;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-10
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class CommentAdapter extends BaseQuickAdapter<CircleCommentBean, BaseViewHolder> {

    private boolean isShowAll = false;// 是否全部显示

    public boolean isShowAll() {
        return isShowAll;
    }

    public void setShowAll(boolean showAll) {
        isShowAll = showAll;
    }

    public CommentAdapter(boolean isShowAll) {
        super(R.layout.item_comment_txt);
        this.isShowAll = isShowAll;
    }

    @Override
    public int getItemCount() {
        int count;
        if (isShowAll) {
            return super.getItemCount();
        } else {
            if (super.getItemCount() > 3) {
                count = 3;
            } else {
                count = super.getItemCount();
            }
        }
        return count;
    }

    @Override
    protected void convert(BaseViewHolder helper, CircleCommentBean commentBean) {
        TextView tvMessage = helper.getView(R.id.tv_message);
        CommonUtils.setTextColor("我：", commentBean.getContent(), R.color.color_488, R.color.gray_484, tvMessage, mContext);
    }
}
