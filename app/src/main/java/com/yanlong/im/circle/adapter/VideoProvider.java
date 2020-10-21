package com.yanlong.im.circle.adapter;

import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.entity.AttachmentBean;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.bean.VoteBean;
import com.yanlong.im.interf.ICircleClickListener;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.MyDiskCache;
import com.yanlong.im.utils.MyDiskCacheUtils;
import com.yanlong.im.wight.avatar.RoundImageView;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.luck.picture.lib.tools.PictureFileUtils.APP_NAME;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-27
 * @updateAuthor
 * @updateDate
 * @description 投票适配器
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class VideoProvider extends BaseItemProvider<MessageFlowItemBean<MessageInfoBean>, BaseViewHolder> implements TextureView.SurfaceTextureListener {

    private final int MAX_ROW_NUMBER = 4;
    private final String END_MSG = " 收起";
    private ICircleClickListener clickListener;
    private boolean isFollow, isDetails;
    private Map<Integer, TextView> hashMap = new HashMap<>();
    private int isVote;
    private TextureView textureView;
    private BaseViewHolder viewHolder;
    private Surface mSurface;
    private MediaPlayer mediaPlayer;
    private String videoUrl;

    /**
     * @param isDetails            是否是详情
     * @param isFollow             关注还是推荐
     * @param iCircleClickListener
     */
    public VideoProvider(boolean isDetails, boolean isFollow, ICircleClickListener iCircleClickListener) {
        this.isFollow = isFollow;
        this.isDetails = isDetails;
        clickListener = iCircleClickListener;
    }

    @Override
    public int viewType() {
        return CircleFlowAdapter.MESSAGE_VIDEO;
    }

    @Override
    public int layout() {
        return R.layout.view_circle_video;
    }

    @Override
    public void convert(BaseViewHolder helper, MessageFlowItemBean<MessageInfoBean> data, int position) {
        viewHolder = helper;
        MessageInfoBean messageInfoBean = data.getData();
        ImageView ivHead = helper.getView(R.id.iv_header);
        RoundImageView ivVideo = helper.getView(R.id.iv_video);
        textureView = helper.getView(R.id.texture_view);
        textureView.setSurfaceTextureListener(this);
        TextView ivLike = helper.getView(R.id.iv_like);
        RecyclerView recyclerVote = helper.getView(R.id.recycler_vote);
        int type = PictureEnum.EContentType.VIDEO;
        if (messageInfoBean.getType() != null) {
            type = messageInfoBean.getType();
        }
        if (type == PictureEnum.EContentType.VIDEO_AND_VOTE) {
            helper.setVisible(R.id.ll_vote, true);
        } else {
            helper.setVisible(R.id.ll_vote, false);
        }
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
            if (type == PictureEnum.EContentType.VIDEO || type == PictureEnum.EContentType.VIDEO_AND_VOTE) {
                if (attachmentBeans != null && attachmentBeans.size() > 0) {
                    AttachmentBean attachmentBean = attachmentBeans.get(0);
                    videoUrl = attachmentBean.getUrl();
                    resetSize(ivVideo, textureView, attachmentBean.getWidth(), attachmentBean.getHeight());
                    Glide.with(mContext)
                            .asBitmap()
                            .load(StringUtil.loadThumbnail(attachmentBean.getBgUrl()))
                            .apply(GlideOptionsUtil.circleImageOptions())
                            .into(ivVideo);
                    helper.setVisible(R.id.rl_video, true);
                    helper.setGone(R.id.iv_play, true);
                }
            }
        } else {
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
            if (!isMe(messageInfoBean.getUid())) {
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
                    for (Integer position : hashMap.keySet()) {
                        hashMap.get(position).getViewTreeObserver().removeOnPreDrawListener(this);
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
        recyclerVote.setLayoutManager(new LinearLayoutManager(mContext));
        if (type == PictureEnum.EContentType.VIDEO_AND_VOTE && !TextUtils.isEmpty(messageInfoBean.getVote())) {
            VoteBean voteBean = new Gson().fromJson(messageInfoBean.getVote(), VoteBean.class);
            setRecycleView(recyclerVote, voteBean.getItems(), voteBean.getType(), position, messageInfoBean.getVoteAnswer(),
                    getVoteSum(messageInfoBean.getVoteAnswer()), messageInfoBean.getUid());
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

    private void resetSize(RoundImageView imageView, TextureView textureView, int imgWidth, int imgHeight) {
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
        if (textureView != null) {
            textureView.setLayoutParams(lp);
        }
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

    /**
     * 投票
     *
     * @param rv
     * @param voteList
     * @param type           类型 1文字 2 图片
     * @param parentPosition 父类位置
     * @param answerBean     答案列表
     * @param voteSum        投票总数
     */
    private void setRecycleView(RecyclerView rv, List<VoteBean.Item> voteList, int type, int parentPosition,
                                MessageInfoBean.VoteAnswerBean answerBean, int voteSum, long uid) {
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
        VoteAdapter taskAdapter = new VoteAdapter(columns, type, isVote, voteSum, sumDataList, isMe(uid));
        rv.setAdapter(taskAdapter);
        taskAdapter.setNewData(voteList);
        taskAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (clickListener == null) {
                    return;
                }
                if (view.getId() == R.id.layout_vote_bg) {
                    if (!isMe(uid) && (answerBean == null || answerBean.getSelfAnswerItem() == -1)) {
                        clickListener.onClick(position, parentPosition, CoreEnum.EClickType.VOTE_PICTRUE, view);
                    } else {
                        clickListener.onClick(parentPosition, 0, CoreEnum.EClickType.CONTENT_DETAILS, view);
                    }
                } else if (view.getId() == R.id.layout_vote_txt) {
                    if (!isMe(uid) && (answerBean == null || answerBean.getSelfAnswerItem() == -1)) {
                        clickListener.onClick(position, parentPosition, CoreEnum.EClickType.VOTE_CHAR, view);
                    } else {
                        clickListener.onClick(parentPosition, 0, CoreEnum.EClickType.CONTENT_DETAILS, view);
                    }
                } else {
                    if (type == PictureEnum.EVoteType.TXT) {
                        clickListener.onClick(parentPosition, 0, CoreEnum.EClickType.CONTENT_DETAILS, view);
                    } /*else {
                        gotoPictruePreview(position, voteList);
                    }*/
                }
            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        preparePlayer();

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void preparePlayer() {
        if (mSurface == null) {
            return;
        }
        mediaPlayer = new MediaPlayer();
    }

    private void startPlay() throws IOException {
        if (mediaPlayer == null || mSurface == null || TextUtils.isEmpty(videoUrl)) {
            return;
        }
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置需要播放的视频
        mediaPlayer.setDataSource(videoUrl);
//        LogUtil.getLog().i(TAG, "setDataSource--path=" + path);
        // 把视频画面输出到Surface
        mediaPlayer.setSurface(mSurface);
        mediaPlayer.setLooping(false);
        mediaPlayer.prepareAsync();
        mediaPlayer.seekTo(0);
        mediaPlayer.start();

    }

    private void download(String url) {
        final File appDir = new File(Environment.getExternalStorageDirectory() + "/" + APP_NAME + "/Mp4/");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        final String fileName = MyDiskCache.getFileNmae(url) + ".mp4";
        final File fileVideo = new File(appDir, fileName);

        try {
            DownloadUtil.get().downLoadFile(url, fileVideo, new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(File file) {
//                    media.setVideoLocalUrl(fileVideo.getAbsolutePath());
//                    if (!TextUtils.isEmpty(msgId)) {
//                        msgDao.fixVideoLocalUrl(msgId, fileVideo.getAbsolutePath());
//                    }
                    MyDiskCacheUtils.getInstance().putFileNmae(appDir.getAbsolutePath(), fileVideo.getAbsolutePath());

                }

                @Override
                public void onDownloading(int progress) {
//                    LogUtil.getLog().i("DownloadUtil", "progress:" + progress);
//                    downloadState = 1;
                }

                @Override
                public void onDownloadFailed(Exception e) {
                    LogUtil.getLog().i("DownloadUtil", "Exception下载失败:" + e.getMessage());
//                    downloadState = 0;
                }
            });

        } catch (Exception e) {
            LogUtil.getLog().i("DownloadUtil", "Exception:" + e.getMessage());
        }
    }

    private boolean isMe(Long uid) {
        if (UserAction.getMyId() != null
                && uid != null &&
                UserAction.getMyId().longValue() != uid.longValue()) {
            return false;
        } else {
            return true;
        }
    }

}
