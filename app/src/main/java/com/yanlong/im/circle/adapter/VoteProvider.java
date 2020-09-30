package com.yanlong.im.circle.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.google.gson.Gson;
import com.luck.picture.lib.PictureEnum;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.CircleCommentBean;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.bean.VoteBean;
import com.yanlong.im.interf.ICircleClickListener;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.utils.TimeToString;

import java.util.ArrayList;
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
    private boolean isFollow, isDetails;
    private List<CircleCommentBean> commentList;

    public VoteProvider(boolean isDetails, boolean isFollow, ICircleClickListener iCircleClickListener,
                        List<CircleCommentBean> commentList) {
        this.isFollow = isFollow;
        this.isDetails = isDetails;
        clickListener = iCircleClickListener;
        this.commentList = commentList;
    }

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
        TextView ivLike = helper.getView(R.id.iv_like);
        Glide.with(mContext)
                .asBitmap()
                .load(messageInfoBean.getAvatar())
                .apply(GlideOptionsUtil.headImageOptions())
                .into(ivHead);
        helper.setText(R.id.tv_user_name, messageInfoBean.getNickname());
        helper.setText(R.id.tv_date, TimeToString.getTimeWx(messageInfoBean.getCreateTime()));
        helper.setText(R.id.tv_content, messageInfoBean.getContent());
        helper.setText(R.id.tv_vote_number, getVoteSum(messageInfoBean.getVoteAnswer().getSumDataList()) + "人参与了投票");
        if (isFollow) {
            helper.setVisible(R.id.iv_follow, true);
        } else {
            helper.setGone(R.id.iv_follow, false);
        }
        if (TextUtils.isEmpty(messageInfoBean.getPosition())) {
            helper.setGone(R.id.tv_location, false);
        } else {
            helper.setVisible(R.id.tv_location, true);
            helper.setText(R.id.tv_location, messageInfoBean.getPosition());
        }

        if (isDetails) {
            if (messageInfoBean.getCommentCount() != null && messageInfoBean.getCommentCount() > 0) {
                helper.setVisible(R.id.tv_comment_count, true);
                helper.setVisible(R.id.recycler_comment, true);
            } else {
                helper.setGone(R.id.tv_comment_count, false);
                helper.setGone(R.id.recycler_comment, false);
            }
            helper.setVisible(R.id.tv_follow, true);
            helper.setGone(R.id.iv_setup, false);
            if (isFollow) {
                helper.setText(R.id.tv_follow, "取消关注");
            } else {
                helper.setText(R.id.tv_follow, "关注TA");
            }
            RecyclerView recyclerComment = helper.getView(R.id.recycler_comment);
            recyclerComment.setLayoutManager(new LinearLayoutManager(mContext));
            CommentAdapter checkTxtAdapter = new CommentAdapter(false);
            recyclerComment.setAdapter(checkTxtAdapter);
            List<CircleCommentBean> list = new ArrayList<>();
            if (commentList != null) {
                list.addAll(commentList);
            }
            checkTxtAdapter.setNewData(list);
        } else {
            helper.setGone(R.id.tv_comment_count, false);
            helper.setGone(R.id.recycler_comment, false);
            helper.setGone(R.id.tv_follow, false);
            helper.setVisible(R.id.iv_setup, true);
        }

        if (messageInfoBean.getLikeCount() != null && messageInfoBean.getLikeCount() > 0) {
            ivLike.setText(messageInfoBean.getLikeCount() + "");
        } else {
            helper.setText(R.id.iv_like, "");
        }
        if (messageInfoBean.getLike() != null && messageInfoBean.getLike() == PictureEnum.ELikeType.YES) {
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.ic_circle_like);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
            ivLike.setCompoundDrawables(drawable, null, null, null);
        } else {
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.ic_circle_give);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
            ivLike.setCompoundDrawables(drawable, null, null, null);
        }

        if (messageInfoBean.getCommentCount() != null && messageInfoBean.getCommentCount() > 0) {
            helper.setText(R.id.iv_comment, messageInfoBean.getCommentCount() + "");
            helper.setText(R.id.tv_comment_count, "所有评论（" + messageInfoBean.getCommentCount() + "）");
        } else {
            helper.setText(R.id.iv_comment, "");
        }

        TextView tvContent = helper.getView(R.id.tv_content);
        tvContent.setText(messageInfoBean.getContent());
        toggleEllipsize(mContext, tvContent, MAX_ROW_NUMBER, messageInfoBean.getContent(),
                "展开", R.color.blue_500, messageInfoBean.isShowAll(), position, messageInfoBean);
        helper.addOnClickListener(R.id.iv_comment, R.id.iv_header, R.id.tv_follow,
                R.id.layout_vote_pictrue, R.id.layout_vote_txt, R.id.iv_like, R.id.iv_setup);

        RecyclerView recyclerVote = helper.getView(R.id.recycler_vote);
        recyclerVote.setLayoutManager(new LinearLayoutManager(mContext));
        if (!TextUtils.isEmpty(messageInfoBean.getVote())) {
            VoteBean voteBean = new Gson().fromJson(messageInfoBean.getVote(), VoteBean.class);
            setRecycleView(recyclerVote, voteBean.getItems(), voteBean.getType(), position,
                    messageInfoBean.getVoteAnswer().getSelfAnswerItem(),
                    getVoteSum(messageInfoBean.getVoteAnswer().getSumDataList())
                    , messageInfoBean.getVoteAnswer().getSumDataList());
        }
    }

    private int getVoteSum(List<MessageInfoBean.VoteAnswerBean.SumDataListBean> sumDataList) {
        int sum = 0;
        if (sumDataList != null && sumDataList.size() > 0) {
            for (MessageInfoBean.VoteAnswerBean.SumDataListBean bean : sumDataList) {
                sum += bean.getCnt();
            }
        }
        return sum;
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
                    clickListener.onClick(postion, 0, 0);
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
                    clickListener.onClick(postion, 0, 1);
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

    /**
     * 投票
     *
     * @param rv
     * @param voteList
     * @param type          类型 1文字 2 图片
     * @param parentPostion 父类位置
     * @param isVote        未投票-1，其他则为itemId:1-4
     * @param voteSum       投票总数
     * @param sumDataList   答案列表
     */
    private void setRecycleView(RecyclerView rv, List<VoteBean.Item> voteList, int type, int parentPostion,
                                int isVote, int voteSum, List<MessageInfoBean.VoteAnswerBean.SumDataListBean> sumDataList) {
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        VoteAdapter taskAdapter = new VoteAdapter(type, isVote, voteSum, sumDataList);
        rv.setAdapter(taskAdapter);
        taskAdapter.setNewData(voteList);
        taskAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (isVote == -1) {
                    switch (view.getId()) {
                        case R.id.layout_vote_pictrue:// 图片投票
                            if (clickListener != null) {
                                clickListener.onClick(position, parentPostion, 3);
                            }
                            break;
                        case R.id.layout_vote_txt:// 文字投票
                            if (clickListener != null) {
                                clickListener.onClick(position, parentPostion, 2);
                            }
                            break;
                    }
                }
            }
        });
    }
}
