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
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.AttachmentBean;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.bean.VoteBean;
import com.yanlong.im.interf.ICircleClickListener;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.wight.avatar.RoundImageView;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final int MAX_ROW_NUMBER = 4;
    private final String END_MSG = " 收起";
    private ICircleClickListener clickListener;
    private boolean isFollow, isDetails;
    private Map<Integer, TextView> hashMap = new HashMap<>();
    private int isVote;

    /**
     * @param isDetails            是否是详情
     * @param isFollow             关注还是推荐
     * @param iCircleClickListener
     */
    public VoteProvider(boolean isDetails, boolean isFollow, ICircleClickListener iCircleClickListener) {
        this.isFollow = isFollow;
        this.isDetails = isDetails;
        clickListener = iCircleClickListener;
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
        RecyclerView recyclerView = helper.getView(R.id.recycler_view);
        MessageInfoBean messageInfoBean = data.getData();
        ImageView ivHead = helper.getView(R.id.iv_header);
        RoundImageView ivVideo = helper.getView(R.id.iv_video);
        ImageView ivVoicePlay = helper.getView(R.id.iv_voice_play);
        TextView ivLike = helper.getView(R.id.iv_like);
        ProgressBar pbProgress = helper.getView(R.id.pb_progress);
        Glide.with(mContext)
                .asBitmap()
                .load(messageInfoBean.getAvatar())
                .apply(GlideOptionsUtil.headImageOptions())
                .into(ivHead);
        helper.setText(R.id.tv_user_name, messageInfoBean.getNickname());
        helper.setText(R.id.tv_date, TimeToString.formatCircleDate(messageInfoBean.getCreateTime()));
        helper.setText(R.id.tv_vote_number, getVoteSum(messageInfoBean.getVoteAnswer()) + "人参与了投票");
        if (isFollow || messageInfoBean.isFollow()) {
            helper.setVisible(R.id.iv_follow, true);
        } else {
            helper.setGone(R.id.iv_follow, false);
        }
        if (TextUtils.isEmpty(messageInfoBean.getPosition()) && TextUtils.isEmpty(messageInfoBean.getCity())) {
            helper.setGone(R.id.tv_location, false);
        } else {
            helper.setVisible(R.id.tv_location, true);
            if (!TextUtils.isEmpty(messageInfoBean.getPosition())) {
                helper.setText(R.id.tv_location, messageInfoBean.getPosition());
            } else {
                helper.setText(R.id.tv_location, messageInfoBean.getCity());
            }
        }
        // 附件
        if (!TextUtils.isEmpty(messageInfoBean.getAttachment())) {
            List<AttachmentBean> attachmentBeans = null;
            try {
                attachmentBeans = new Gson().fromJson(messageInfoBean.getAttachment(),
                        new TypeToken<List<AttachmentBean>>() {
                        }.getType());
            } catch (Exception e) {
                attachmentBeans = new ArrayList<>();
            }
            if (messageInfoBean.getType() != null &&
                    (messageInfoBean.getType() == PictureEnum.EContentType.VOICE ||
                            messageInfoBean.getType() == PictureEnum.EContentType.VOICE_AND_VOTE)) {
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
            } else if (messageInfoBean.getType() != null && (messageInfoBean.getType() == PictureEnum.EContentType.PICTRUE ||
                    messageInfoBean.getType() == PictureEnum.EContentType.PICTRUE_AND_VOTE)) {
                if (attachmentBeans != null && attachmentBeans.size() > 0) {
                    if (attachmentBeans.size() == 1) {
                        resetSize(ivVideo, attachmentBeans.get(0).getWidth(), attachmentBeans.get(0).getHeight());
                        Glide.with(mContext)
                                .asBitmap()
                                .load(StringUtil.loadThumbnail(attachmentBeans.get(0).getUrl()))
                                .apply(GlideOptionsUtil.circleImageOptions())
                                .into(ivVideo);
                        helper.setVisible(R.id.rl_video, true);
                        recyclerView.setVisibility(View.GONE);
                        helper.setGone(R.id.layout_voice, false);
                        helper.setGone(R.id.iv_play, false);
                    } else {
                        helper.setGone(R.id.layout_voice, false);
                        helper.setGone(R.id.rl_video, false);
                        recyclerView.setVisibility(View.VISIBLE);
                        setRecycleView(recyclerView, attachmentBeans, position);
                    }
                }
            } else if (messageInfoBean.getType() != null && (messageInfoBean.getType() == PictureEnum.EContentType.VIDEO ||
                    messageInfoBean.getType() == PictureEnum.EContentType.VIDEO_AND_VOTE)) {
                if (attachmentBeans != null && attachmentBeans.size() > 0) {
                    AttachmentBean attachmentBean = attachmentBeans.get(0);
                    resetSize(ivVideo, attachmentBean.getWidth(), attachmentBean.getHeight());
                    Glide.with(mContext)
                            .asBitmap()
                            .load(StringUtil.loadThumbnail(attachmentBean.getBgUrl()))
                            .apply(GlideOptionsUtil.circleImageOptions())
                            .into(ivVideo);
                    helper.setVisible(R.id.rl_video, true);
                    recyclerView.setVisibility(View.GONE);
                    helper.setGone(R.id.layout_voice, false);
                    helper.setGone(R.id.iv_play, true);
                }
            }
        } else {
            helper.setGone(R.id.layout_voice, false);
            recyclerView.setVisibility(View.GONE);
            helper.setGone(R.id.rl_video, false);
        }
        helper.setGone(R.id.iv_delete_voice, false);
        TextView tvContent = helper.getView(R.id.tv_content);
        tvContent.setText(getSpan(messageInfoBean.getContent()));
        if (isDetails) {
            tvContent.setMaxLines(Integer.MAX_VALUE);
            helper.setVisible(R.id.tv_follow, true);
            helper.setGone(R.id.iv_setup, false);
            helper.setGone(R.id.view_line, false);
            if (UserAction.getMyId() != null
                    && messageInfoBean.getUid() != null &&
                    UserAction.getMyId().longValue() != messageInfoBean.getUid().longValue()) {
                helper.setVisible(R.id.tv_follow, true);
            } else {
                helper.setVisible(R.id.tv_follow, false);
            }
            if (isFollow || messageInfoBean.isFollow()) {
                helper.setText(R.id.tv_follow, "取消关注");
            } else {
                helper.setText(R.id.tv_follow, "关注TA");
            }
        } else {
            if (UserAction.getMyId() != null
                    && messageInfoBean.getUid() != null &&
                    UserAction.getMyId().longValue() != messageInfoBean.getUid().longValue()) {
                helper.setVisible(R.id.iv_setup, true);
            } else {
                helper.setVisible(R.id.iv_setup, false);
            }
            helper.setGone(R.id.tv_follow, false);
            helper.setVisible(R.id.view_line, true);
//            toggleEllipsize(mContext, tvContent, MAX_ROW_NUMBER, messageInfoBean.getContent(),
//                    "展开", R.color.blue_500, messageInfoBean.isShowAll(), position, messageInfoBean);
            tvContent.setMaxLines(MAX_ROW_NUMBER);//默认三行
            tvContent.setTag("" + helper.getAdapterPosition());
            hashMap.put(helper.getAdapterPosition(), tvContent);
            tvContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    // 避免重复监听
                    for (Integer postion : hashMap.keySet()) {
                        hashMap.get(postion).getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    int ellipsisCount = 0;
                    if (tvContent.getLayout() != null) {
                        ellipsisCount = tvContent.getLayout().getEllipsisCount(tvContent.getLineCount() - 1);
                    }
                    int line = tvContent.getLineCount();
                    if (ellipsisCount > 0 || line > MAX_ROW_NUMBER) {
                        helper.setGone(R.id.tv_show_all, true);
                        TextView tvMore = helper.getView(R.id.tv_show_all);
                        // 内容高度小1000时不滚动
                        setTextViewLines(tvContent, tvMore, messageInfoBean.isShowAll(), helper);
                    } else {
                        helper.setGone(R.id.tv_show_all, false);
                    }
                    return true;
                }
            });
        }

        if (messageInfoBean.getLikeCount() != null && messageInfoBean.getLikeCount() > 0) {
            ivLike.setText(StringUtil.numberFormart(messageInfoBean.getLikeCount()));
        } else {
            ivLike.setText("点赞");
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
            helper.setText(R.id.iv_comment, StringUtil.numberFormart(messageInfoBean.getCommentCount()));
        } else {
            helper.setText(R.id.iv_comment, "评论");
        }

        helper.getView(R.id.tv_show_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(position, 0, CoreEnum.EClickType.CONTENT_DOWN, v);
                }
            }
        });

        helper.addOnClickListener(R.id.iv_comment, R.id.iv_header, R.id.tv_follow,
                R.id.layout_vote_pictrue, R.id.layout_vote_txt, R.id.iv_like, R.id.iv_setup, R.id.rl_video);

        RecyclerView recyclerVote = helper.getView(R.id.recycler_vote);
        recyclerVote.setLayoutManager(new LinearLayoutManager(mContext));
        if (!TextUtils.isEmpty(messageInfoBean.getVote())) {
            VoteBean voteBean = new Gson().fromJson(messageInfoBean.getVote(), VoteBean.class);
            setRecycleView(recyclerVote, voteBean.getItems(), voteBean.getType(), position, messageInfoBean.getVoteAnswer(),
                    getVoteSum(messageInfoBean.getVoteAnswer()));
        }
    }

    private void setTextViewLines(TextView content, TextView btn, boolean isShowAll, BaseViewHolder helper) {
        if (!isShowAll) {
            //显示3行，按钮设置为点击显示全部。
            content.setMaxLines(MAX_ROW_NUMBER);
            btn.setText("展开");
        } else {
            //展示全部，按钮设置为点击收起。
            content.setMaxLines(Integer.MAX_VALUE);
            btn.setText("收起");
        }
    }

    private void resetSize(RoundImageView imageView, int imgWidth, int imgHeight) {
        //w/h = 3/4
        final int DEFAULT_W = DensityUtil.dip2px(mContext, 120);
        final int DEFAULT_H = DensityUtil.dip2px(mContext, 180);
        int width = DEFAULT_W;
        int height = DEFAULT_H;

        if (imgHeight > 0) {
            double scale = (imgWidth * 1.00) / imgHeight;
            if (imgWidth > imgHeight) {
                width = DEFAULT_W;
                height = (int) (width / scale);
            } else if (imgWidth < imgHeight) {
                height = DEFAULT_H;
                width = (int) (height * scale);
            } else {
                width = height = DEFAULT_W;
            }
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.width = width;
        lp.height = height;
        imageView.setLayoutParams(lp);
    }

    /**
     * 投票总数
     *
     * @param voteAnswerBean
     * @return
     */
    private int getVoteSum(MessageInfoBean.VoteAnswerBean voteAnswerBean) {
        int sum = 0;
        if (voteAnswerBean != null) {
            List<MessageInfoBean.VoteAnswerBean.SumDataListBean> sumDataList = voteAnswerBean.getSumDataList();
            if (sumDataList != null && sumDataList.size() > 0) {
                for (MessageInfoBean.VoteAnswerBean.SumDataListBean bean : sumDataList) {
                    sum += bean.getCnt();
                }
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
                            textView.setText(getSpan(originText));
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
            textView.setText(getSpan(originText));
        }
    }

    /**
     * 富文本点击事件
     *
     * @param context
     * @param textView
     * @param temp
     * @param endText
     * @param postion
     * @param endColorID
     */
    private void setSpanClick(Context context, TextView textView, CharSequence temp, String endText, int postion, int endColorID) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(temp);
        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View widget) {
                if (clickListener != null) {
                    clickListener.onClick(postion, 0, CoreEnum.EClickType.CONTENT_DOWN, widget);
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
                    clickListener.onClick(postion, 0, CoreEnum.EClickType.CONTENT_DETAILS, widget);
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
     * 获取富文有表情则显示表情
     *
     * @param msg
     * @return
     */
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

    // 记录点下去X坐标
    private float downX;
    // 记录点下去Y坐标
    private float downY;
    // 获取该组件在屏幕的x坐标
    private float deltaX;
    // 获取该组件在屏幕的y坐标
    private float deltaY;

    /**
     * 图片列表
     *
     * @param rv
     * @param attachmentBeans
     * @param postion
     */
    private void setRecycleView(RecyclerView rv, List<AttachmentBean> attachmentBeans, int postion) {
        rv.setLayoutManager(new GridLayoutManager(mContext, 3));
        ShowImagesAdapter taskAdapter = new ShowImagesAdapter();
        rv.setAdapter(taskAdapter);
        taskAdapter.setNewData(attachmentBeans);
        taskAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                AudioPlayUtil.stopAudioPlay();
                toPictruePreview(position, attachmentBeans);
            }
        });
        rv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 获取该组件在屏幕的x坐标
                deltaX = event.getRawX();
                // 获取该组件在屏幕的y坐标
                deltaY = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 获取x坐标
                        downX = event.getRawX();
                        // 获取y坐标
                        downY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        // 判断是否触发点击事件
                        if (Math.abs(downX - deltaX) < 10 && Math.abs(downY - deltaY) < 10 && null != clickListener) {
                            clickListener.onClick(postion, 0, CoreEnum.EClickType.CONTENT_DETAILS, v);
                        }
                }
                return true;
            }
        });
    }

    /**
     * 查看图片
     *
     * @param postion         位置
     * @param attachmentBeans 图片集合
     */
    private void toPictruePreview(int postion, List<AttachmentBean> attachmentBeans) {
        List<LocalMedia> selectList = new ArrayList<>();
        for (AttachmentBean bean : attachmentBeans) {
            LocalMedia localMedia = new LocalMedia();
            localMedia.setCutPath(bean.getUrl());
            localMedia.setCompressPath(bean.getUrl());
            localMedia.setSize(bean.getSize());
            localMedia.setWidth(bean.getWidth());
            localMedia.setHeight(bean.getHeight());
            selectList.add(localMedia);
        }
        PictureSelector.create((Activity) mContext)
                .themeStyle(R.style.picture_default_style)
                .isGif(true)
                .openExternalPreview1(postion, selectList, "", 0L, PictureConfig.FROM_CIRCLE, "");
    }

    /**
     * 投票
     *
     * @param rv
     * @param voteList
     * @param type          类型 1文字 2 图片
     * @param parentPostion 父类位置
     * @param answerBean    答案列表
     * @param voteSum       投票总数
     */
    private void setRecycleView(RecyclerView rv, List<VoteBean.Item> voteList, int type, int parentPostion,
                                MessageInfoBean.VoteAnswerBean answerBean, int voteSum) {
        int columns = 0;
        if (type == PictureEnum.EVoteType.TXT) {
            rv.setLayoutManager(new LinearLayoutManager(mContext));
        } else {
            if (voteList != null && voteList.size() == 4 || voteList.size() == 2) {
                columns = 2;
            } else {
                columns = 3;
            }
            rv.setLayoutManager(new GridLayoutManager(mContext, columns));
        }
        isVote = -1;// 未投票-1，其他则为itemId:1-4
        List<MessageInfoBean.VoteAnswerBean.SumDataListBean> sumDataList = new ArrayList<>();
        if (answerBean != null) {
            isVote = answerBean.getSelfAnswerItem();
            sumDataList.addAll(answerBean.getSumDataList());
        }
        VoteAdapter taskAdapter = new VoteAdapter(columns, type, isVote, voteSum, sumDataList);
        rv.setAdapter(taskAdapter);
        taskAdapter.setNewData(voteList);
        taskAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (clickListener == null) {
                    return;
                }
                if (answerBean == null || answerBean.getSelfAnswerItem() == -1) {
                    switch (view.getId()) {
                        case R.id.layout_vote_pictrue:// 图片投票
                        case R.id.layout_vote_bg:
                            clickListener.onClick(position, parentPostion, CoreEnum.EClickType.VOTE_PICTRUE, view);
                            break;
                        case R.id.layout_vote_txt:// 文字投票
                            clickListener.onClick(position, parentPostion, CoreEnum.EClickType.VOTE_CHAR, view);
                            break;
                    }
                } else {
                    if (type == PictureEnum.EVoteType.TXT) {
                        clickListener.onClick(parentPostion, 0, CoreEnum.EClickType.CONTENT_DETAILS, view);
                    } else {
                        AudioPlayUtil.stopAudioPlay();
                        List<AttachmentBean> attachmentBeans = new ArrayList<>();
                        for (VoteBean.Item item : voteList) {
                            AttachmentBean bean = new AttachmentBean();
                            bean.setHeight(item.getHeight());
                            bean.setWidth(item.getWidth());
                            bean.setUrl(item.getItem());
                            bean.setSize(item.getSize());
                            attachmentBeans.add(bean);
                        }
                        toPictruePreview(position, attachmentBeans);
                    }
                }
            }
        });
    }
}
