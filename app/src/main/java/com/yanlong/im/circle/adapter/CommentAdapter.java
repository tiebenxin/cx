package com.yanlong.im.circle.adapter;

import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.luck.picture.lib.PictureEnum;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.CircleCommentBean;
import com.yanlong.im.utils.CommonUtils;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-10
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class CommentAdapter extends BaseQuickAdapter<CircleCommentBean.CommentListBean, BaseViewHolder> {

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
    protected void convert(BaseViewHolder helper, CircleCommentBean.CommentListBean commentBean) {
        Glide.with(mContext)
                .asBitmap()
                .load(commentBean.getAvatar())
                .apply(GlideOptionsUtil.headImageOptions())
                .into((ImageView) helper.getView(R.id.iv_header));
        TextView tvName = helper.getView(R.id.tv_user_name);
        TextView tvContent = helper.getView(R.id.tv_content);
        TextView ivLike = helper.getView(R.id.iv_like);
        helper.setText(R.id.tv_date, TimeToString.getTimeWx(commentBean.getCreateTime()));
        tvName.setText(commentBean.getNickname());
        if (commentBean.getLikeCount() != null && commentBean.getLikeCount() > 0) {
            ivLike.setText(StringUtil.numberFormart(commentBean.getLikeCount()));
        } else {
            ivLike.setText("点赞");
        }
        if (commentBean.getLike() != null && commentBean.getLike() == PictureEnum.ELikeType.YES) {
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.ic_circle_like);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
            ivLike.setCompoundDrawables(drawable, null, null, null);
        } else {
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.ic_circle_give);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
            ivLike.setCompoundDrawables(drawable, null, null, null);
        }

        if (commentBean.getReplyUid() != null && commentBean.getReplyUid() != 0) {
            tvName.setTextColor(mContext.getResources().getColor(R.color.color_488));
            tvContent.setText(getSpan(commentBean.getContent()));
            CommonUtils.setSignTextColor("回复" + commentBean.getReplyNickname() + ":" + commentBean.getContent(),
                    commentBean.getReplyNickname(), R.color.color_488, 2,tvContent,mContext);
        } else {
            tvName.setTextColor(mContext.getResources().getColor(R.color.gray_757));
            tvContent.setTextColor(mContext.getResources().getColor(R.color.gray_484));
            tvContent.setText(getSpan(commentBean.getContent()));
        }
        helper.addOnClickListener(R.id.layout_item, R.id.iv_header,R.id.iv_like);
        helper.addOnLongClickListener(R.id.layout_item);
    }

    private SpannableString getSpan(String msg) {
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        SpannableString spannableString = null;
        if (fontSize != null) {
            spannableString = ExpressionUtil.getExpressionString(mContext, fontSize.intValue(), msg);
        } else {
            spannableString = ExpressionUtil.getExpressionString(mContext, ExpressionUtil.DEFAULT_SIZE, msg);
        }
        return spannableString;
    }
}
