package com.yanlong.im.circle.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.interf.ICircleClickListener;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.utils.TimeToString;

import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-27
 * @updateAuthor
 * @updateDate
 * @description 投票适配器
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class VoteProvider extends BaseItemProvider<MessageFlowItemBean<MessageInfoBean>, BaseViewHolder> {

    private final int MAX_ROW_NUMBER = 3;
    private final String END_MSG = " 收起";
    private ICircleClickListener clickListener;

    @Override
    public int viewType() {
        return CircleFlowAdapter.MESSAGE_VOTE;
    }

    @Override
    public int layout() {
        return R.layout.view_circle_vote;
    }

    @Override
    public void convert(BaseViewHolder helper, MessageFlowItemBean<MessageInfoBean> data, int position) {
        MessageInfoBean messageInfoBean = data.getData();
        ImageView ivHead = helper.getView(R.id.iv_header);
        Glide.with(mContext)
                .asBitmap()
                .load(messageInfoBean.getAvatar())
                .apply(GlideOptionsUtil.headImageOptions())
                .into(ivHead);
        helper.setText(R.id.tv_user_name, messageInfoBean.getNickname());
        helper.setText(R.id.tv_date, TimeToString.getTimeWx(messageInfoBean.getCreateTime()));
        helper.setText(R.id.tv_content, messageInfoBean.getContent());
        if (TextUtils.isEmpty(messageInfoBean.getPosition())) {
            helper.setGone(R.id.tv_location, false);
        } else {
            helper.setVisible(R.id.tv_location, true);
            helper.setText(R.id.tv_location, messageInfoBean.getPosition());
        }

        if (messageInfoBean.getLikeCount() != null && messageInfoBean.getLikeCount() > 0) {
            helper.setText(R.id.iv_like, messageInfoBean.getLikeCount());
        } else {
            helper.setText(R.id.iv_like, "");
        }

        if (messageInfoBean.getCommentCount() != null && messageInfoBean.getCommentCount() > 0) {
            helper.setText(R.id.iv_comment, messageInfoBean.getCommentCount());
        } else {
            helper.setText(R.id.iv_comment, "");
        }

        TextView tvContent = helper.getView(R.id.tv_content);
        tvContent.setText(messageInfoBean.getContent());
        toggleEllipsize(mContext, tvContent, MAX_ROW_NUMBER, messageInfoBean.getContent(),
                "展开", R.color.blue_500, messageInfoBean.isShowAll(), position, messageInfoBean);
        helper.addOnClickListener(R.id.iv_comment, R.id.iv_header, R.id.tv_follow);

        RecyclerView recyclerVote = helper.getView(R.id.recycler_vote);
        recyclerVote.setLayoutManager(new LinearLayoutManager(mContext));
        setRecycleView(recyclerVote, null);
    }

    /**
     * 设置textView结尾...后面显示的文字和颜色
     *
     * @param context    上下文
     * @param textView   textview
     * @param minLines   最少的行数
     * @param originText 原文本
     * @param endText    结尾文字
     * @param endColorID 结尾文字颜色id
     * @param isExpand   当前是否是展开状态
     * @param postion    位置
     */
    public void toggleEllipsize(final Context context, final TextView textView, final int minLines,
                                final String originText, final String endText, final int endColorID,
                                final boolean isExpand, final int postion, MessageInfoBean messageInfoBean) {
        if (TextUtils.isEmpty(originText)) {
            return;
        }
        try {
            textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                    .OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (isExpand) {
                        if (messageInfoBean.isRowsMore()) {
                            setSpanClick(context, textView, originText + END_MSG, END_MSG, postion, endColorID);
                        } else {
                            textView.setText(originText);
                        }
                    } else {
                        int paddingLeft = textView.getPaddingLeft();
                        int paddingRight = textView.getPaddingRight();
                        TextPaint paint = textView.getPaint();
                        float moreText = textView.getTextSize() * endText.length();
                        float availableTextWidth = (textView.getWidth() - paddingLeft - paddingRight) * minLines - moreText;
                        availableTextWidth = availableTextWidth - paint.measureText("中");// 减去一个字的宽度
                        CharSequence ellipsizeStr = TextUtils.ellipsize(originText, paint, availableTextWidth, TextUtils.TruncateAt.END);
                        if (ellipsizeStr.length() < originText.length()) {
                            CharSequence temp = ellipsizeStr + endText;
                            setSpanClick(context, textView, temp, endText, postion, endColorID);
                            messageInfoBean.setRowsMore(true);
                        } else {
                            textView.setText(originText);
                        }
                    }
                    if (Build.VERSION.SDK_INT >= 16) {
                        textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        textView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        } catch (Exception e) {
            textView.setText(originText);
        }
    }

    private void setSpanClick(Context context, TextView textView, CharSequence temp, String endText, int postion, int endColorID) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(temp);
        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {
                if (clickListener != null) {
                    clickListener.onClick(postion, 0);
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan contentSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {
                if (clickListener != null) {
                    clickListener.onClick(postion, 1);
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        ssb.setSpan(clickableSpan, temp.length() - endText.length(), temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(contentSpan, 0, temp.length() - endText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.setSpan(new ForegroundColorSpan(context.getResources().getColor(endColorID)),
                temp.length() - endText.length(), temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ssb);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setRecycleView(RecyclerView rv, List<String> imgs) {
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        ShowImagesAdapter taskAdapter = new ShowImagesAdapter();
        rv.setAdapter(taskAdapter);
        taskAdapter.setNewData(imgs);
        taskAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });
    }
}
