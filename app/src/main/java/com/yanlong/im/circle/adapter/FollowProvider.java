package com.yanlong.im.circle.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.AttachmentBean;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.interf.ICircleClickListener;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.AutoPlayUtils;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.socket.SocketUtil;
import com.yanlong.im.view.JzvdStdCircle;
import com.yanlong.im.wight.avatar.RoundImageView;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.ScreenUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jzvd.Jzvd;

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
    private final int MAX_ROW_NUMBER = 4;
    private ICircleClickListener clickListener;
    private final String END_MSG = " 收起";
    private Map<Integer, TextView> hashMap = new HashMap<>();
    private int action;

    /**
     * @param isDetails     是否是详情
     * @param clickListener
     */
    public FollowProvider(boolean isDetails, boolean isFollow, ICircleClickListener clickListener) {
        this.isDetails = isDetails;
        this.isFollow = isFollow;
        this.clickListener = clickListener;
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
        MessageInfoBean messageInfoBean = data.getData();
        ImageView ivHead = helper.getView(R.id.iv_header);
        RoundImageView ivSignPicture = helper.getView(R.id.iv_sign_picture);
        JzvdStdCircle jzvdStd = helper.getView(R.id.video_player);
        ImageView ivVoicePlay = helper.getView(R.id.iv_voice_play);
        TextView ivLike = helper.getView(R.id.iv_like);
        ProgressBar pbProgress = helper.getView(R.id.pb_progress);
        if (isFollow || messageInfoBean.isFollow()) {
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
        if (data.getRefreshTime() > 0) {
            helper.setText(R.id.tv_date, TimeToString.getRecommendTime(data.getRefreshTime()));
        } else {
            helper.setText(R.id.tv_date, TimeToString.formatCircleDate(messageInfoBean.getCreateTime()));
        }
        if (TextUtils.isEmpty(messageInfoBean.getPosition()) && TextUtils.isEmpty(messageInfoBean.getCity())) {
            //详情我的动态新增浏览量，UI和好友动态location显示有区别，只有我才会显示浏览量
            if (messageInfoBean.getUid() != null && messageInfoBean.getUid().longValue() == UserAction.getMyId()) {
                helper.setGone(R.id.tv_location, false);
                if (isDetails) {
                    helper.setGone(R.id.tv_watch_num, true);
                } else {
                    helper.setGone(R.id.tv_watch_num, false);
                }
                helper.setGone(R.id.tv_location_new, false);
            } else {
                helper.setGone(R.id.tv_location, false);
                helper.setGone(R.id.tv_watch_num, false);
                helper.setGone(R.id.tv_location_new, false);
            }
        } else {
            if (messageInfoBean.getUid() != null && messageInfoBean.getUid().longValue() == UserAction.getMyId()) {
                if (isDetails) {
                    helper.setGone(R.id.tv_location, false);
                    helper.setGone(R.id.tv_watch_num, true);
                    helper.setGone(R.id.tv_location_new, true);
                    if (!TextUtils.isEmpty(messageInfoBean.getPosition())) {
                        helper.setText(R.id.tv_location_new, messageInfoBean.getPosition());
                    } else {
                        helper.setText(R.id.tv_location_new, messageInfoBean.getCity());
                    }
                } else {
                    helper.setGone(R.id.tv_location, true);
                    helper.setGone(R.id.tv_watch_num, false);
                    helper.setGone(R.id.tv_location_new, false);
                    if (!TextUtils.isEmpty(messageInfoBean.getPosition())) {
                        helper.setText(R.id.tv_location, messageInfoBean.getPosition());
                    } else {
                        helper.setText(R.id.tv_location, messageInfoBean.getCity());
                    }
                }
            } else {
                helper.setGone(R.id.tv_location, true);
                helper.setGone(R.id.tv_watch_num, false);
                helper.setGone(R.id.tv_location_new, false);
                if (!TextUtils.isEmpty(messageInfoBean.getPosition())) {
                    helper.setText(R.id.tv_location, messageInfoBean.getPosition());
                } else {
                    helper.setText(R.id.tv_location, messageInfoBean.getCity());
                }
            }
        }
        //浏览量
        helper.setText(R.id.tv_watch_num, messageInfoBean.getBrowseCount() + "浏览");

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
        helper.setGone(R.id.iv_sign_picture, false);
        // 附件
        if (!TextUtils.isEmpty(messageInfoBean.getAttachment())) {
            addAttachment(helper, position, recyclerView, messageInfoBean, ivSignPicture, jzvdStd, ivVoicePlay, pbProgress);
        } else {
            helper.setGone(R.id.layout_voice, false);
            recyclerView.setVisibility(View.GONE);
            helper.setGone(R.id.rl_video, false);
        }
        helper.setGone(R.id.iv_delete_voice, false);
        TextView tvContent = helper.getView(R.id.tv_content);
        if (TextUtils.isEmpty(messageInfoBean.getContent())) {
            tvContent.setVisibility(View.GONE);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(getSpan(messageInfoBean.getContent()));
        }

        if (isDetails) {
            tvContent.setMaxLines(Integer.MAX_VALUE);
            if (UserAction.getMyId() != null
                    && messageInfoBean.getUid() != null &&
                    UserAction.getMyId().longValue() != messageInfoBean.getUid().longValue()) {
                helper.setVisible(R.id.tv_follow, true);
            } else {
                helper.setVisible(R.id.tv_follow, false);
            }
            helper.setGone(R.id.iv_setup, false);
            helper.setGone(R.id.view_line, false);
            //有好友关系
            action = CoreEnum.EClickType.FOLLOW;
            if (messageInfoBean.getUserType() == ChatEnum.EUserType.FRIEND || messageInfoBean.getUserType() == ChatEnum.EUserType.BLACK) {
                if (isFollow || messageInfoBean.isFollow()) {
                    helper.setText(R.id.tv_follow, "私聊");
                    action = CoreEnum.EClickType.CHAT;
                } else {
                    helper.setText(R.id.tv_follow, "关注TA");
                }
            } else {
                if (isFollow || messageInfoBean.isFollow()) {
                    helper.setText(R.id.tv_follow, "加好友");
                    action = CoreEnum.EClickType.ADD_FRIEND;
                } else {
                    helper.setText(R.id.tv_follow, "关注TA");
                }
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
            TextView tvMore = helper.getView(R.id.tv_show_all);
            if (!TextUtils.isEmpty(messageInfoBean.getContent())) {
                setContent(helper, messageInfoBean, tvContent, tvMore);
            } else {
                tvMore.setVisibility(View.GONE);
            }
        }

        helper.getView(R.id.tv_show_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(position, 0, CoreEnum.EClickType.CONTENT_DOWN, v);
                }
            }
        });
        helper.getView(R.id.tv_follow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(position, 0, action, v);
                }
            }
        });
        helper.addOnClickListener(R.id.iv_comment, R.id.iv_header,
                R.id.iv_like, R.id.iv_setup, R.id.tv_user_name, R.id.iv_sign_picture);
    }

    /**
     * 设置内容
     *
     * @param helper
     * @param messageInfoBean
     * @param tvContent
     * @param tvMore
     */
    private void setContent(BaseViewHolder helper, MessageInfoBean messageInfoBean, TextView tvContent, TextView tvMore) {
        if (!messageInfoBean.isShowAll()) {
            int contentWidth = ScreenUtil.getScreenWidth(mContext) -
                    mContext.getResources().getDimensionPixelOffset(R.dimen.circle_content_margin);
            float textWidth = tvContent.getPaint().measureText(tvContent.getText().toString());
            float line = textWidth / contentWidth;
            if (line > MAX_ROW_NUMBER) {
                tvMore.setVisibility(View.VISIBLE);
                // 内容高度小1000时不滚动
                setTextViewLines(tvContent, tvMore, messageInfoBean.isShowAll(), helper);
            } else {
                tvMore.setVisibility(View.GONE);
            }
        } else {
            tvMore.setVisibility(View.VISIBLE);
            tvContent.setMaxLines(Integer.MAX_VALUE);
            tvMore.setText("收起");
        }
    }

    /**
     * 添加附件
     *
     * @param helper
     * @param position
     * @param recyclerView
     * @param messageInfoBean
     * @param ivSignPicture
     * @param jzvdStd
     * @param ivVoicePlay
     * @param pbProgress
     */
    private void addAttachment(BaseViewHolder helper, int position, RecyclerView recyclerView, MessageInfoBean messageInfoBean,
                               ImageView ivSignPicture, JzvdStdCircle jzvdStd, ImageView ivVoicePlay, ProgressBar pbProgress) {
        List<AttachmentBean> attachmentBeans = null;
        try {
            attachmentBeans = new Gson().fromJson(messageInfoBean.getAttachment(),
                    new TypeToken<List<AttachmentBean>>() {
                    }.getType());
        } catch (Exception e) {
            attachmentBeans = new ArrayList<>();
        }
        if (messageInfoBean.getType() != null && messageInfoBean.getType() == PictureEnum.EContentType.VOICE) {
            if (attachmentBeans != null && attachmentBeans.size() > 0) {
                AttachmentBean attachmentBean = attachmentBeans.get(0);
                helper.setText(R.id.tv_time, TimeToString.MM_SS(attachmentBean.getDuration() * 1000));
                pbProgress.setProgress(messageInfoBean.getPlayProgress());
                // 未播放则重置播放进度
                LogUtil.getLog().i("语音", "position=" + position + "  id=" + messageInfoBean.getId() + "  isPlay=" + messageInfoBean.isPlay());
                if (!messageInfoBean.isPlay()) {
                    pbProgress.setProgress(0);
                    AnimationDrawable animationDrawable = (AnimationDrawable) ivVoicePlay.getBackground();
                    animationDrawable.stop();
                    animationDrawable.selectDrawable(0);
                    ivVoicePlay.setBackground(null);
                    ivVoicePlay.setBackgroundResource(R.drawable.ic_voice_anim_circle);
                } else {
                    AnimationDrawable animationDrawable = (AnimationDrawable) ivVoicePlay.getBackground();
                    animationDrawable.start();
                }
                ivVoicePlay.setOnClickListener(o -> {
                    if (checkNetConnectStatus(0)) {
                        if (!TextUtils.isEmpty(attachmentBean.getUrl())) {
                            // 记录播放状态
                            if (clickListener != null) {
                                if (jzvdStd != null) {
                                    jzvdStd.releaseAllVideos();
                                }
                                clickListener.onClick(position, 0, CoreEnum.EClickType.CLICK_VOICE, pbProgress);
                            }
                        }
                    }
                });
            }
            recyclerView.setVisibility(View.GONE);
            helper.setGone(R.id.rl_video, false);
            helper.setVisible(R.id.layout_voice, true);
        } else if (messageInfoBean.getType() != null && messageInfoBean.getType() == PictureEnum.EContentType.PICTRUE) {
            if (attachmentBeans != null && attachmentBeans.size() > 0) {
                if (attachmentBeans.size() == 1) {
                    resetSize(ivSignPicture, attachmentBeans.get(0).getWidth(), attachmentBeans.get(0).getHeight());
                    String path = StringUtil.loadThumbnail(attachmentBeans.get(0).getUrl());
                    if (isGif(path)) {
                        RequestOptions options = new RequestOptions();
                        options.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                        Glide.with(mContext).load(path).apply(options).listener(new RequestListener() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).into(ivSignPicture);
                    } else {
                        Glide.with(mContext)
                                .asBitmap()
                                .load(path)
                                .apply(GlideOptionsUtil.circleImageOptions())
                                .into(ivSignPicture);
                    }
                    helper.setVisible(R.id.iv_sign_picture, true);
                    recyclerView.setVisibility(View.GONE);
                    helper.setGone(R.id.layout_voice, false);
                } else {
                    helper.setGone(R.id.layout_voice, false);
                    recyclerView.setVisibility(View.VISIBLE);
                    setRecycleView(recyclerView, attachmentBeans, position);
                }
                helper.setGone(R.id.rl_video, false);
            }
        } else if (messageInfoBean.getType() != null && messageInfoBean.getType() == PictureEnum.EContentType.VIDEO) {
            if (attachmentBeans != null && attachmentBeans.size() > 0) {
                LogUtil.getLog().i("1212", "55555555");
                AttachmentBean attachmentBean = attachmentBeans.get(0);
                resetSize(jzvdStd, attachmentBean.getWidth(), attachmentBean.getHeight());
                // 没有正在播放则设置 处理刷新暂停视频问题
                if (!AutoPlayUtils.isPlayVideo(jzvdStd)) {
                    jzvdStd.setUp(attachmentBean.getUrl(), "", Jzvd.SCREEN_NORMAL);

                    Glide.with(jzvdStd.getContext())
                            .load(StringUtil.loadThumbnail(attachmentBean.getBgUrl()))
                            .apply(GlideOptionsUtil.circleImageOptions())
                            .into(jzvdStd.posterImageView);
                }
                jzvdStd.setVideoUrl(attachmentBean.getUrl());
                jzvdStd.setBgUrl(StringUtil.loadThumbnail(attachmentBean.getBgUrl()));
                jzvdStd.setAttachmentBean(attachmentBean);

                helper.setVisible(R.id.rl_video, true);
                recyclerView.setVisibility(View.GONE);
                helper.setGone(R.id.layout_voice, false);
            }
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

    private void resetSize(View view, int imgWidth, int imgHeight) {
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
        if (view instanceof RoundImageView) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.width = width;
            lp.height = height;
            lp.setMargins(0, ScreenUtil.dip2px(mContext, 10), 0, 0);
            view.setLayoutParams(lp);
        } else {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.width = width;
            lp.height = height;
            view.setLayoutParams(lp);
        }
    }

    /**
     * 富文本
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

    /**
     * 富文本
     *
     * @param spannableString
     * @return
     */
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

        textView.setText(getSpan(ssb));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
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
     */
    private void setRecycleView(RecyclerView rv, List<AttachmentBean> attachmentBeans, int position) {
        if (attachmentBeans == null) {
            return;
        }
        int size = attachmentBeans.size();
        if (size == 2 || size == 4) {
            rv.setLayoutManager(new GridLayoutManager(mContext, 2));
        } else {
            rv.setLayoutManager(new GridLayoutManager(mContext, 3));
        }
        ShowImagesAdapter taskAdapter = new ShowImagesAdapter();
        rv.setAdapter(taskAdapter);
        taskAdapter.setNewData(attachmentBeans);
        taskAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                AudioPlayUtil.stopAudioPlay();
                toPicturePreview(position, attachmentBeans);
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
                            clickListener.onClick(position, 0, CoreEnum.EClickType.CONTENT_DETAILS, v);
                        }
                }
                return true;
            }
        });
    }

    /**
     * 查看图片
     *
     * @param position        位置
     * @param attachmentBeans 图片集合
     */
    private void toPicturePreview(int position, List<AttachmentBean> attachmentBeans) {
        List<LocalMedia> selectList = new ArrayList<>();
        for (AttachmentBean bean : attachmentBeans) {
            LocalMedia localMedia = new LocalMedia();
            if (PictureMimeType.isHttp(bean.getUrl())) {
                localMedia.setCutPath(StringUtil.loadThumbnail(bean.getUrl()));
            } else {
                localMedia.setCutPath(bean.getUrl());
            }
            localMedia.setCompressPath(bean.getUrl());
            localMedia.setSize(bean.getSize());
            localMedia.setWidth(bean.getWidth());
            localMedia.setHeight(bean.getHeight());
            selectList.add(localMedia);
        }
        PictureSelector.create((Activity) mContext)
                .themeStyle(R.style.picture_default_style)
                .isGif(true)
                .openExternalPreview1(position, selectList, "", 0L, PictureConfig.FROM_CIRCLE, "");
    }

    public boolean isGif(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.toLowerCase().contains(".gif")) {
                return true;
            }
        }
        return false;
    }

    /*
     * 发送消息前，需要检测网络连接状态，网络不可用，不能发送
     * 每条消息发送前，需要检测，语音和小视频录制之前，仍需要检测
     * type=0 默认提示 type=1 仅获取断网状态/不提示
     * */
    public boolean checkNetConnectStatus(int type) {
        boolean isOk;
        if (!NetUtil.isNetworkConnected()) {
            if (type == 0) {
                ToastUtil.show("网络连接不可用，请稍后重试");
            }
            isOk = false;
        } else {
            isOk = SocketUtil.getSocketUtil().getOnlineState();
            if (!isOk) {
                if (type == 0) {
                    ToastUtil.show( "连接已断开，请稍后再试");
                }
            }
        }
        return isOk;
    }
}
