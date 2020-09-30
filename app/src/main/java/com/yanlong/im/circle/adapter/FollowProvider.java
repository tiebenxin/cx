package com.yanlong.im.circle.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.audio.AudioPlayUtil;
import com.luck.picture.lib.entity.AttachmentBean;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.CircleCommentBean;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.interf.ICircleClickListener;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.wight.avatar.RoundImageView;

import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.TimeToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-09
 * @updateAuthor
 * @updateDate
 * @description 关注适配器
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class FollowProvider extends BaseItemProvider<MessageFlowItemBean<MessageInfoBean>, BaseViewHolder> {

    private boolean isDetails, isFollow;
    private final int MAX_ROW_NUMBER = 3;
    private ICircleClickListener clickListener;
    private final String END_MSG = " 收起";
    private List<CircleCommentBean> commentList;

    /**
     * @param isDetails     是否是详情
     * @param isFollow      是否是关注
     * @param clickListener
     */
    public FollowProvider(boolean isDetails, boolean isFollow, ICircleClickListener clickListener,
                          List<CircleCommentBean> commentList) {
        this.isDetails = isDetails;
        this.isFollow = isFollow;
        this.clickListener = clickListener;
        this.commentList = commentList;
    }

    @Override
    public int viewType() {
        return CircleFlowAdapter.MESSAGE_DEFAULT;
    }

    @Override
    public int layout() {
        return R.layout.item_follow;
    }

    @Override
    public void convert(BaseViewHolder helper, MessageFlowItemBean<MessageInfoBean> data, int position) {
        RecyclerView recyclerView = helper.getView(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        MessageInfoBean messageInfoBean = data.getData();
        ImageView ivHead = helper.getView(R.id.iv_header);
        RoundImageView ivVideo = helper.getView(R.id.iv_video);
        ImageView ivVoicePlay = helper.getView(R.id.iv_voice_play);
        TextView ivLike = helper.getView(R.id.iv_like);
        ProgressBar pbProgress = helper.getView(R.id.pb_progress);
        if (isFollow) {
            helper.setVisible(R.id.iv_follow, true);
        } else {
            helper.setGone(R.id.iv_follow, false);
        }
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
        // 附件
        if (!TextUtils.isEmpty(messageInfoBean.getAttachment())) {
            List<AttachmentBean> attachmentBeans = new Gson().fromJson(messageInfoBean.getAttachment(),
                    new TypeToken<List<AttachmentBean>>() {
                    }.getType());
            if (messageInfoBean.getType() != null && messageInfoBean.getType() == PictureEnum.EContentType.VOICE) {
                if (attachmentBeans != null && attachmentBeans.size() > 0) {
                    AttachmentBean attachmentBean = attachmentBeans.get(0);
                    helper.setText(R.id.tv_time, attachmentBean.getDuration() + "");
                    pbProgress.setProgress(0);
                    ivVoicePlay.setOnClickListener(o -> {
                        if (!TextUtils.isEmpty(attachmentBean.getUrl())) {
                            AudioPlayUtil.startAudioPlay(mContext, attachmentBean.getUrl(), ivVoicePlay, pbProgress);
                        }
                    });
                }
                recyclerView.setVisibility(View.GONE);
                helper.setGone(R.id.rl_video, false);
                helper.setVisible(R.id.layout_voice, true);
            } else if (messageInfoBean.getType() != null && messageInfoBean.getType() == PictureEnum.EContentType.PICTRUE) {
                List<String> imgs = new ArrayList<>();
                helper.setGone(R.id.layout_voice, false);
                helper.setGone(R.id.rl_video, false);
                recyclerView.setVisibility(View.VISIBLE);
                if (attachmentBeans != null && attachmentBeans.size() > 0) {
                    for (AttachmentBean attachmentBean : attachmentBeans) {
                        imgs.add(attachmentBean.getUrl());
                    }
                }
                setRecycleView(recyclerView, imgs);
            } else if (messageInfoBean.getType() != null && messageInfoBean.getType() == PictureEnum.EContentType.VIDEO) {
                helper.setVisible(R.id.rl_video, true);
                recyclerView.setVisibility(View.GONE);
                helper.setGone(R.id.layout_voice, false);
                if (attachmentBeans != null && attachmentBeans.size() > 0) {
                    AttachmentBean attachmentBean = attachmentBeans.get(0);
                    Glide.with(mContext)
                            .asBitmap()
                            .load(attachmentBean.getBgUrl())
                            .apply(GlideOptionsUtil.headImageOptions())
                            .into(ivVideo);
                }

            }
        } else {
            helper.setGone(R.id.layout_voice, false);
            recyclerView.setVisibility(View.GONE);
            helper.setGone(R.id.rl_video, false);
        }
        helper.setGone(R.id.iv_delete_voice, false);

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
        TextView tvContent = helper.getView(R.id.tv_content);
        tvContent.setText(getSpan(messageInfoBean.getContent()));
        toggleEllipsize(mContext, tvContent, MAX_ROW_NUMBER, messageInfoBean.getContent(),
                "展开", R.color.blue_500, messageInfoBean.isShowAll(), position, messageInfoBean);
        helper.addOnClickListener(R.id.iv_comment, R.id.iv_header, R.id.tv_follow,
                R.id.iv_like, R.id.iv_setup, R.id.rl_video);
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

    private SpannableString getSpan(SpannableString spannableString) {
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        if (fontSize != null) {
            return ExpressionUtil.getExpressionString(mContext, fontSize.intValue(), spannableString);
        } else {
            return ExpressionUtil.getExpressionString(mContext, ExpressionUtil.DEFAULT_SIZE, spannableString);
        }
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
                            textView.setText(getSpan((originText)));
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
                            textView.setText(getSpan((originText)));
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
            textView.setText(getSpan((originText)));
        }
    }

    private void setSpanClick(Context context, TextView textView, CharSequence temp, String endText, int postion, int endColorID) {
        SpannableString ssb = new SpannableString(temp);
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

        textView.setText(getSpan(ssb));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setRecycleView(RecyclerView rv, List<String> imgs) {
        rv.setLayoutManager(new GridLayoutManager(mContext, 3));
        ShowImagesAdapter taskAdapter = new ShowImagesAdapter();
        rv.setAdapter(taskAdapter);
        taskAdapter.setNewData(imgs);
        taskAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                toPictruePreview(position, imgs);
            }
        });
    }

    private void toPictruePreview(int postion, List<String> imgs) {
        List<LocalMedia> selectList = new ArrayList<>();
        for (String s : imgs) {
            LocalMedia localMedia = new LocalMedia();
            localMedia.setPath(s);
            localMedia.setCompressPath(s);
            selectList.add(localMedia);
        }
        PictureSelector.create((Activity) mContext)
                .themeStyle(R.style.picture_default_style)
                .isGif(true)
                .openExternalPreview(postion, selectList);
    }
}
