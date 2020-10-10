package com.yanlong.im.circle.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.audio.AudioPlayUtil;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.AttachmentBean;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.R;
import com.yanlong.im.chat.ui.VideoPlayActivity;
import com.yanlong.im.circle.bean.CircleTrendsBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.details.CircleDetailsActivity;
import com.yanlong.im.circle.mycircle.FollowMeActivity;
import com.yanlong.im.circle.mycircle.MyFollowActivity;
import com.yanlong.im.circle.mycircle.MyInteractActivity;
import com.yanlong.im.circle.mycircle.MyMeetingActivity;
import com.yanlong.im.circle.mycircle.TempAction;
import com.yanlong.im.interf.IRefreshListenr;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.inter.ICommonSelectClickListner;
import net.cb.cb.library.inter.ITrendClickListner;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.DialogHelper;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.yanlong.im.circle.adapter.CircleFlowAdapter.MESSAGE_DEFAULT;
import static com.yanlong.im.circle.adapter.CircleFlowAdapter.MESSAGE_VOTE;
import static com.yanlong.im.circle.follow.FollowFragment.IS_OPEN;

/**
 * @类名：我的动态(我的朋友圈)适配器 (含上拉加载)
 * @Date：2019/12/9
 * @by zjy
 * @备注：
 */

public class MyTrendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //头部尾部数量(可直接修改数量控制头部和尾部)
    private final static int HEAD_COUNT = 1;
    private final static int FOOT_COUNT = 1;
    //区分布局类型
    private final static int TYPE_HEAD = 0;
    private final static int TYPE_CONTENT = 1;
    private final static int TYPE_FOOTER = 2;

    // 当前加载状态，默认为隐藏底部
    private int loadState = 4;
    // 正在加载
    public final int LOADING = 1;
    // 加载更多
    public final int LOADING_MORE = 2;
    // 加载到底
    public final int LOADING_END = 3;
    // 隐藏底部
    public final int LOADING_GONE = 4;

    private int type;//1 我的朋友圈 2 别人的朋友圈
    private long friendUid;//朋友的uid
    private List<String> listOne = Arrays.asList("置顶","取消置顶");
    private List<String> listTwo = Arrays.asList("广场可见","仅好友可见","仅陌生人可见","自己可见");

    private LayoutInflater inflater;
    private Activity activity;
    private CircleTrendsBean topData;//顶部数据
    private List<MessageInfoBean> dataList;//动态列表数据
    private Drawable dislike;
    private Drawable like;
    private TempAction action;
    private IRefreshListenr refreshListenr;
    private UserBean userBean;
    private CheckPermission2Util permission2Util = new CheckPermission2Util();
    private RequestOptions mRequestOptions;
    private boolean haveNewMsg = false;//是否展示顶部新消息通知
    private boolean isFollow = false;//是否关注

    public MyTrendsAdapter(Activity activity, List<MessageInfoBean> dataList, int type,long friendUid) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.type = type;
        this.friendUid = friendUid;
        this.dataList = new ArrayList<>();
        if(dataList!=null && dataList.size()>0){
            this.dataList.addAll(dataList);
        }
        init();
    }

    public void setOnRefreshListenr(IRefreshListenr refreshListenr) {
        this.refreshListenr = refreshListenr;
    }

    //初始化相关设置
    private void init() {
        dislike = activity.getResources().getDrawable(R.mipmap.ic_circle_give,null);
        like = activity.getResources().getDrawable(R.mipmap.ic_circle_like,null);
        dislike.setBounds(0, 0, dislike.getMinimumWidth(), dislike.getMinimumHeight());
        like.setBounds(0, 0, like.getMinimumWidth(), like.getMinimumHeight());
        action = new TempAction();
        //图片相关设置
        mRequestOptions = RequestOptions.centerInsideTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .placeholder(com.yanlong.im.R.drawable.ic_info_head)
                .error(com.yanlong.im.R.drawable.ic_info_head)
                .centerCrop();
        if(type==1){
            userBean = (UserBean) new UserAction().getMyInfo();
        }else {
            userBean = new UserBean();
            httpGetUserInfo(friendUid);
        }
    }

    //刷新数据
    public void updateList(List<MessageInfoBean> list) {
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    //加载更多
    public void addMoreList(List list) {
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    //设置头部数据
    public void setTopData(CircleTrendsBean topData){
        this.topData = topData;
    }

    //更新背景图
    public void notifyBackground(String localPath){
        topData.setBgImage(localPath);
        notifyItemChanged(0);//第一项是头部
    }

    //展示顶部通知
    public void showNotice(boolean haveNewMsg){
        this.haveNewMsg = haveNewMsg;
        notifyItemChanged(0);
    }

    //是否关注
    public void ifFollow(boolean isFollow){
        this.isFollow = isFollow;
    }

    //列表内容数量
    public int getContentSize() {
        return dataList.size();
    }


    @Override
    public int getItemViewType(int position) {
        int contentSize = getContentSize();
        if (HEAD_COUNT != 0 && position == 0) { // 头部
            return TYPE_HEAD;
        } else if (FOOT_COUNT != 0 && position == HEAD_COUNT + contentSize) { // 尾部
            return TYPE_FOOTER;
        } else {
            return TYPE_CONTENT; // 内容
        }
    }

    //item总数
    @Override
    public int getItemCount() {
        return dataList.size() + HEAD_COUNT + FOOT_COUNT;
    }


    //具体显示逻辑
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        //子项
        if (viewHolder instanceof MyTrendsAdapter.ContentHolder) {
            ContentHolder holder = (ContentHolder) viewHolder;
            if (dataList != null && dataList.size() > 0) {
                if (dataList.get(position-1) != null) {
                    MessageInfoBean bean = dataList.get(position-1);
                    //时间
                    holder.tvCreateTime.setText(TimeToString.YYYY_MM_DD_HH_MM(bean.getCreateTime()));
                    //内容
                    if(!TextUtils.isEmpty(bean.getContent())){
//                        holder.tvContent.setText(bean.getContent());
                        holder.tvText.setText(getSpan(bean.getContent()));
                    }
                    //位置
                    if(!TextUtils.isEmpty(bean.getPosition())){
                        holder.tvLocation.setText(bean.getPosition());
                    }
                    //点赞数 评论数
                    holder.tvLike.setText(bean.getLikeCount()+"");
                    holder.tvComment.setText(bean.getCommentCount()+"");
                    //说说可见度
                    if(type==1){
                        holder.tvCanSee.setVisibility(View.VISIBLE);
                        if(bean.getVisibility()==0){
                            holder.tvCanSee.setText("广场可见");
                        }else if(bean.getVisibility()==1){
                            holder.tvCanSee.setText("好友可见");
                        }else if(bean.getVisibility()==2){
                            holder.tvCanSee.setText("陌生人可见");
                        }else {
                            holder.tvCanSee.setText("自己可见");
                        }
                        holder.ivSetup.setVisibility(View.VISIBLE);
                        //设置-> 置顶 权限 删除
                        holder.ivSetup.setOnClickListener(v -> {
                            DialogHelper.getInstance().createTrendDialog(activity, new ITrendClickListner() {
                                @Override
                                public void clickIsTop() {
                                    DialogHelper.getInstance().createCommonSelectListDialog(activity, listOne, new ICommonSelectClickListner() {
                                        @Override
                                        public void selectOne() {
                                            //置顶
                                            httpIsTop(bean.getId(),1);
                                        }

                                        @Override
                                        public void selectTwo() {
                                            //取消置顶
                                            httpIsTop(bean.getId(),0);
                                        }

                                        @Override
                                        public void selectThree() {

                                        }

                                        @Override
                                        public void selectFour() {

                                        }

                                        @Override
                                        public void onCancle() {

                                        }
                                    });
                                }

                                @Override
                                public void clickAuthority() {
                                    //设置动态可见度
                                    DialogHelper.getInstance().createCommonSelectListDialog(activity, listTwo, new ICommonSelectClickListner() {
                                        @Override
                                        public void selectOne() {
                                            //广场可见
                                            httpSetVisibility(bean.getId(),0,holder.tvCanSee,position-1);
                                        }

                                        @Override
                                        public void selectTwo() {
                                            //好友可见
                                            httpSetVisibility(bean.getId(),1,holder.tvCanSee,position-1);
                                        }

                                        @Override
                                        public void selectThree() {
                                            //陌生人可见
                                            httpSetVisibility(bean.getId(),2,holder.tvCanSee,position-1);
                                        }

                                        @Override
                                        public void selectFour() {
                                            //自己可见
                                            httpSetVisibility(bean.getId(),3,holder.tvCanSee,position-1);
                                        }

                                        @Override
                                        public void onCancle() {

                                        }
                                    });
                                }

                                @Override
                                public void clickDelete() {
                                    //删除动态
                                    httpDeleteTrend(bean.getId(),position-1);
                                }

                                @Override
                                public void clickCancle() {

                                }
                            });
                        });
                    }else {
                        holder.tvCanSee.setVisibility(View.GONE);
                        holder.ivSetup.setVisibility(View.GONE);
                    }
                    //跳详情(拼凑一下昵称和头像)
                    holder.layoutItem.setOnClickListener(v -> {
                                if (!TextUtils.isEmpty(userBean.getHead())) {
                                    bean.setAvatar(userBean.getHead());
                                }
                                if (!TextUtils.isEmpty(userBean.getName())) {
                                    bean.setNickname(userBean.getName());
                                }
                                gotoCircleDetailsActivity(false, bean);
                            }
                    );
                    //是否置顶
                    if(bean.getIsTop()==0){
                        holder.ivIstop.setVisibility(View.GONE);
                        holder.tvIstop.setVisibility(View.GONE);
                    }else {
                        holder.ivIstop.setVisibility(View.VISIBLE);
                        holder.tvIstop.setVisibility(View.VISIBLE);
                    }
                    //是否点赞
                    if(bean.getLike()==0){
                        holder.tvLike.setCompoundDrawables(dislike,null,null,null);
                    }else {
                        holder.tvLike.setCompoundDrawables(like,null,null,null);
                    }
                    holder.tvLike.setOnClickListener(v -> {
                        if(bean.getLike()==0){
                            httpLike(bean.getId(),bean.getUid(),holder.tvLike,position-1,bean.getLikeCount());
                        }else {
                            httpCancleLike(bean.getId(),bean.getUid(),holder.tvLike,position-1,bean.getLikeCount());
                        }
                    });
                    //根据附件显示不同类型语音、图片、视频
                    if (!TextUtils.isEmpty(bean.getAttachment())) {
                        holder.layoutContent.setVisibility(View.VISIBLE);
                        List<AttachmentBean> attachmentBeans = new Gson().fromJson(bean.getAttachment(),
                                new TypeToken<List<AttachmentBean>>() {
                                }.getType());
                        if (bean.getType() != null && bean.getType() == PictureEnum.EContentType.VOICE) {
                            holder.recyclerView.setVisibility(View.GONE);
                            holder.layoutVideo.setVisibility(View.GONE);
                            holder.layoutVoice.setVisibility(View.VISIBLE);
                            if (attachmentBeans != null && attachmentBeans.size() > 0) {
                                AttachmentBean attachmentBean = attachmentBeans.get(0);
                                holder.tvTime.setText(attachmentBean.getDuration() + "");
                                holder.pbProgress.setProgress(0);
                                holder.ivVoicePlay.setOnClickListener(o -> {
                                    if (!TextUtils.isEmpty(attachmentBean.getUrl())) {
                                        AudioPlayUtil.startAudioPlay(activity, attachmentBean.getUrl(), holder.ivVoicePlay, holder.pbProgress);
                                    }
                                });
                                holder.ivDeleteVoice.setVisibility(View.GONE);
                            }

                        } else if (bean.getType() != null && bean.getType() == PictureEnum.EContentType.PICTRUE) {
                            holder.recyclerView.setVisibility(View.VISIBLE);
                            holder.layoutVideo.setVisibility(View.GONE);
                            holder.layoutVoice.setVisibility(View.GONE);
                            List<String> imgs = new ArrayList<>();
                            if (attachmentBeans != null && attachmentBeans.size() > 0) {
                                for (AttachmentBean attachmentBean : attachmentBeans) {
                                    imgs.add(attachmentBean.getUrl());
                                }
                            }
                            setRecycleView(holder.recyclerView, imgs);
                        } else if (bean.getType() != null && bean.getType() == PictureEnum.EContentType.VIDEO) {
                            holder.recyclerView.setVisibility(View.GONE);
                            holder.layoutVideo.setVisibility(View.VISIBLE);
                            holder.layoutVoice.setVisibility(View.GONE);
                            if (attachmentBeans != null && attachmentBeans.size() > 0) {
                                AttachmentBean attachmentBean = attachmentBeans.get(0);
                                Glide.with(activity)
                                        .asBitmap()
                                        .load(attachmentBean.getBgUrl())
                                        .apply(GlideOptionsUtil.headImageOptions())
                                        .into(holder.ivVideo);
                                holder.layoutVideo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(activity, VideoPlayActivity.class);
                                        if (attachmentBeans.size() > 0) {
                                            intent.putExtra("videopath", attachmentBeans.get(0).getUrl());
                                        }
                                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        activity.startActivity(intent);
                                    }
                                });
                            }
                        }
                    } else {
                        holder.layoutContent.setVisibility(View.GONE);
                    }
                }
            }
        } else if(viewHolder instanceof MyTrendsAdapter.FootHolder){
            //加载更多-尾部
            FootHolder holder = (FootHolder) viewHolder;
            switch (loadState) {
                case LOADING:
                    holder.footerLayout.setVisibility(View.VISIBLE);
                    holder.loadingMore.setVisibility(View.GONE);
                    holder.loading.setVisibility(View.VISIBLE);
                    holder.loadingNoMore.setVisibility(View.GONE);
                    break;
                case LOADING_MORE:
                    holder.footerLayout.setVisibility(View.VISIBLE);
                    holder.loadingMore.setVisibility(View.VISIBLE);
                    holder.loading.setVisibility(View.GONE);
                    holder.loadingNoMore.setVisibility(View.GONE);
                    break;
                case LOADING_END:
                    holder.footerLayout.setVisibility(View.VISIBLE);
                    holder.loadingMore.setVisibility(View.GONE);
                    holder.loading.setVisibility(View.GONE);
                    holder.loadingNoMore.setVisibility(View.VISIBLE);
                    break;
                case LOADING_GONE:
                    holder.footerLayout.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }else {
            //头部
            HeadHolder holder = (HeadHolder) viewHolder;
            //展示头部数据
            if(topData!=null){
                //第一页拿部分数据，我关注的，关注我的，看过我的总数
                holder.tvMyFollowNum.setText(topData.getMyFollowCount() + "");
                holder.tvFollowMeNum.setText(topData.getFollowMyCount() + "");
                holder.tvWhoSeeMeNum.setText(topData.getAccessCount() + "");
                //展示背景图
                if (!TextUtils.isEmpty(topData.getBgImage())) {
                    Glide.with(activity).load(topData.getBgImage())
                            .apply(GlideOptionsUtil.defImageOptions1()).into(holder.ivBackground);
                }else {
                    Glide.with(activity).load(R.mipmap.ic_trend_default_bg)
                            .apply(GlideOptionsUtil.defImageOptions1()).into(holder.ivBackground);
                }
            }else {
                Glide.with(activity).load(R.mipmap.ic_trend_default_bg)
                        .apply(GlideOptionsUtil.defImageOptions1()).into(holder.ivBackground);
            }
            //新消息提醒
            if(haveNewMsg){
                holder.layoutNotice.setVisibility(View.VISIBLE);
            }else {
                holder.layoutNotice.setVisibility(View.GONE);
            }
            holder.layoutNotice.setOnClickListener(v -> {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                holder.layoutNotice.setVisibility(View.GONE);
                Intent intent = new Intent(activity, MyInteractActivity.class);
                activity.startActivity(intent);
            });
            holder.layoutMyFollow.setOnClickListener(v -> {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                Intent intent = new Intent(activity, MyFollowActivity.class);
                activity.startActivity(intent);
            });
            holder.layoutFollowMe.setOnClickListener(v -> {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                Intent intent = new Intent(activity, FollowMeActivity.class);
                activity.startActivity(intent);
            });
            holder.layoutWhoSeeMe.setOnClickListener(v -> {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                Intent intent = new Intent(activity, MyMeetingActivity.class);
                activity.startActivity(intent);
            });
            //我的动态顶部样式
            if(userBean!=null){
                if(type==1){
                    holder.layoutCenter.setVisibility(View.VISIBLE);
                    holder.ivFriendHeader.setVisibility(View.GONE);
                    holder.tvFriendName.setVisibility(View.GONE);
                    holder.ivMyHeader.setVisibility(View.VISIBLE);
                    holder.tvMyName.setVisibility(View.VISIBLE);
                    //头像 昵称
                    if (!TextUtils.isEmpty(userBean.getHead())) {
                        Glide.with(activity)
                                .load(userBean.getHead())
                                .apply(mRequestOptions)
                                .into(holder.ivMyHeader);
                    } else {
                        Glide.with(activity)
                                .load(R.drawable.ic_info_head)
                                .into(holder.ivMyHeader);
                    }
                    if (!TextUtils.isEmpty(userBean.getName())) {
                        holder.tvMyName.setText(userBean.getName());
                    } else {
                        holder.tvMyName.setText("未知用户名");
                    }
                    //点击布局切换背景
                    holder.ivBackground.setOnClickListener(v -> permission2Util.requestPermissions(activity, new CheckPermission2Util.Event() {
                        @Override
                        public void onSuccess() {
                            PictureSelector.create(activity)
                                    .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                                    .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                    .previewImage(false)// 是否可预览图片 true or false
                                    .isCamera(false)// 是否显示拍照按钮 ture or false
                                    .compress(true)// 是否压缩 true or false
                                    .enableCrop(true)
                                    .withAspectRatio(1, 1)
                                    .freeStyleCropEnabled(false)
                                    .rotateEnabled(false)
                                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                        }

                        @Override
                        public void onFail() {
                            ToastUtil.show("请允许访问权限");
                        }
                    }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}));
                }else {
                    //好友的动态顶部样式
                    holder.layoutCenter.setVisibility(View.GONE);
                    holder.ivFriendHeader.setVisibility(View.VISIBLE);
                    holder.tvFriendName.setVisibility(View.VISIBLE);
                    holder.ivMyHeader.setVisibility(View.GONE);
                    holder.tvMyName.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(userBean.getHead())) {
                        Glide.with(activity)
                                .load(userBean.getHead())
                                .apply(mRequestOptions)
                                .into(holder.ivFriendHeader);
                    } else {
                        Glide.with(activity)
                                .load(R.drawable.ic_info_head)
                                .into(holder.ivFriendHeader);
                    }
                    if (!TextUtils.isEmpty(userBean.getName())) {
                        holder.tvFriendName.setText(userBean.getName());
                    } else {
                        holder.tvFriendName.setText("未知用户名");
                    }
                }
            }

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        //进行判断显示类型，来创建返回不同的View
        if (position == TYPE_CONTENT) {
            View itemView = inflater.inflate(R.layout.item_trend, parent, false);
            return new MyTrendsAdapter.ContentHolder(itemView);
        } else if (position == TYPE_FOOTER){
            View itemView = inflater.inflate(R.layout.main_footer_layout, parent, false);
            return new MyTrendsAdapter.FootHolder(itemView);
        }else {
            View itemView = inflater.inflate(R.layout.trend_head, parent, false);
            return new MyTrendsAdapter.HeadHolder(itemView);
        }
    }

    // 子项
    private class ContentHolder extends RecyclerView.ViewHolder {
        private TextView tvCreateTime;
        private TextView tvCanSee;
        private TextView tvText;
        private TextView tvLocation;
        private TextView tvLike;
        private TextView tvComment;
        private ImageView ivSetup;
        private ImageView ivIstop;
        private TextView tvIstop;
        private RelativeLayout layoutItem;
        private LinearLayout layoutContent;
        private RecyclerView recyclerView;
        private RelativeLayout layoutVideo;
        private ImageView ivVideo;
        private LinearLayout layoutVoice;
        private TextView tvTime;
        private ProgressBar pbProgress;
        private ImageView ivVoicePlay;
        private ImageView ivDeleteVoice;


        public ContentHolder(View itemView) {
            super(itemView);
            tvCreateTime = itemView.findViewById(R.id.tv_create_time);
            tvCanSee = itemView.findViewById(R.id.tv_can_see);
            tvText = itemView.findViewById(R.id.tv_text);
            tvLocation = itemView.findViewById(R.id.tv_location);
            layoutItem = itemView.findViewById(R.id.layout_item);
            tvLike = itemView.findViewById(R.id.tv_like);
            tvComment = itemView.findViewById(R.id.tv_comment);
            ivSetup = itemView.findViewById(R.id.iv_setup);
            ivIstop = itemView.findViewById(R.id.iv_istop);
            tvIstop = itemView.findViewById(R.id.tv_istop);
            recyclerView = itemView.findViewById(R.id.recycler_view);
            layoutVideo = itemView.findViewById(R.id.layout_video);
            layoutContent = itemView.findViewById(R.id.layout_content);
            ivVideo = itemView.findViewById(R.id.iv_video);
            layoutVoice = itemView.findViewById(R.id.layout_voice);
            tvTime = itemView.findViewById(R.id.tv_time);
            pbProgress = itemView.findViewById(R.id.pb_progress);
            ivVoicePlay = itemView.findViewById(R.id.iv_voice_play);
            ivDeleteVoice = itemView.findViewById(R.id.iv_delete_voice);
        }
    }

    // 尾部
    class FootHolder extends RecyclerView.ViewHolder {
        private TextView loading;
        private TextView loadingMore;
        private TextView loadingNoMore;
        private LinearLayout footerLayout;

        public FootHolder(View itemView) {
            super(itemView);
            loading = itemView.findViewById(R.id.loading);
            loadingMore = itemView.findViewById(R.id.loading_more);
            loadingNoMore = itemView.findViewById(R.id.loading_no_more);
            footerLayout = itemView.findViewById(R.id.footer_layout);
        }
    }

    // 头部
    class HeadHolder extends RecyclerView.ViewHolder {
        private ImageView ivFriendHeader;
        private ImageView ivMyHeader;
        private TextView tvFriendName;
        private TextView tvMyName;
        private TextView tvMyFollowNum;
        private TextView tvFollowMeNum;
        private TextView tvWhoSeeMeNum;
        private ImageView ivBackground;
        private LinearLayout layoutMyFollow;
        private LinearLayout layoutFollowMe;
        private LinearLayout layoutWhoSeeMe;
        private LinearLayout layoutCenter;
        private LinearLayout layoutNotice;

        public HeadHolder(View itemView) {
            super(itemView);
            ivFriendHeader = itemView.findViewById(R.id.iv_friend_header);
            ivMyHeader = itemView.findViewById(R.id.iv_my_header);
            tvFriendName = itemView.findViewById(R.id.tv_friend_name);
            tvMyName = itemView.findViewById(R.id.tv_my_name);
            tvMyFollowNum = itemView.findViewById(R.id.tv_my_follow_num);
            tvFollowMeNum = itemView.findViewById(R.id.tv_follow_me_num);
            tvWhoSeeMeNum = itemView.findViewById(R.id.tv_who_see_me_num);
            ivBackground = itemView.findViewById(R.id.iv_background);
            layoutMyFollow = itemView.findViewById(R.id.layout_my_follow);
            layoutFollowMe = itemView.findViewById(R.id.layout_follow_me);
            layoutWhoSeeMe = itemView.findViewById(R.id.layout_who_see_me);
            layoutCenter = itemView.findViewById(R.id.layout_center);
            layoutNotice = itemView.findViewById(R.id.layout_notice);
        }
    }

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    private void gotoCircleDetailsActivity(boolean isOpen,MessageInfoBean messageInfoBean) {
        Postcard postcard = ARouter.getInstance().build(CircleDetailsActivity.path);
        postcard.withBoolean(IS_OPEN, isOpen);
        postcard.withBoolean(CircleDetailsActivity.SOURCE_TYPE, isFollow);//是否关注
        postcard.withBoolean(CircleDetailsActivity.IS_ME, type==1 ? true:false);//是否为我自己的动态详情
        postcard.withString(CircleDetailsActivity.ITEM_DATA, new Gson().toJson(messageInfoBean));
        if(!TextUtils.isEmpty(messageInfoBean.getVote())){//是否含有投票
            postcard.withInt(CircleDetailsActivity.ITEM_DATA_TYPE, MESSAGE_VOTE);
        }else {
            postcard.withInt(CircleDetailsActivity.ITEM_DATA_TYPE, MESSAGE_DEFAULT);
        }
        postcard.navigation();
    }


    /**
     * 发请求->点赞
     */
    private void httpLike(long id,long uid, TextView tvLike,int position,int oldCount) {
        action.httpLike(id,uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("点赞成功");
                    dataList.get(position).setLike(1);
                    dataList.get(position).setLikeCount(oldCount+1);
                    notifyItemChanged(position+1,tvLike);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("点赞失败");
            }
        });
    }

    /**
     * 发请求->取消点赞
     */
    private void httpCancleLike(long id,long uid, TextView tvLike,int position,int oldCount) {
        action.httpCancleLike(id,uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("已取消点赞");
                    dataList.get(position).setLike(0);
                    dataList.get(position).setLikeCount(oldCount-1);
                    notifyItemChanged(position+1,tvLike);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("取消点赞失败");
            }
        });
    }

    /**
     * 发请求->置顶
     */
    private void httpIsTop(long id,int isTop) {
        action.httpIsTop(id,isTop, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    if(isTop==1){
                        ToastUtil.show("置顶成功");
                    }else {
                        ToastUtil.show("取消置顶成功");
                    }
                    refreshListenr.onRefresh();
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                if(isTop==1){
                    ToastUtil.show("置顶失败");
                }else {
                    ToastUtil.show("取消置顶失败");
                }
            }
        });
    }

    /**
     * 发请求->修改可见度
     */
    private void httpSetVisibility(long id,int visibility, TextView tvCanSee,int position) {
        action.httpSetVisibility(id,visibility, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("设置成功");
                    dataList.get(position).setVisibility(visibility);
                    notifyItemChanged(position+1,tvCanSee);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("设置失败");
            }
        });
    }


    /**
     * 发请求->删除动态
     */
    private void httpDeleteTrend(long id,int position) {
        action.httpDeleteTrend(id, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("删除成功");
                    dataList.remove(position);//删除数据源,移除集合中当前下标的数据
                    notifyItemRemoved(position);//刷新被删除的地方
                    notifyItemRangeChanged(position,getItemCount()); //刷新被删除数据，以及其后面的数据
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("删除失败");
            }
        });
    }


    private void httpGetUserInfo(long uid) {
        new UserAction().getUserInfo4Id(uid, new CallBack<ReturnBean<UserInfo>>() {
            @Override
            public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                if (response.body() == null || response.body().getData() == null) {
                    return;
                }
                UserInfo mUserInfo = response.body().getData();
                //只要拿用户最新昵称和头像
                if(!TextUtils.isEmpty(mUserInfo.getHead())){
                    userBean.setHead(mUserInfo.getHead());
                }
                if(!TextUtils.isEmpty(mUserInfo.getName())){
                    userBean.setName(mUserInfo.getName());
                }
                notifyItemChanged(0);
            }
        });
    }

    private SpannableString getSpan(String msg) {
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        SpannableString spannableString = null;
        if (fontSize != null) {
            spannableString = ExpressionUtil.getExpressionString(activity, fontSize.intValue(), msg);
        } else {
            spannableString = ExpressionUtil.getExpressionString(activity, ExpressionUtil.DEFAULT_SIZE, msg);
        }
        return spannableString;
    }

    private SpannableString getSpan(SpannableString spannableString) {
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        if (fontSize != null) {
            return ExpressionUtil.getExpressionString(activity, fontSize.intValue(), spannableString);
        } else {
            return ExpressionUtil.getExpressionString(activity, ExpressionUtil.DEFAULT_SIZE, spannableString);
        }
    }

    private void setRecycleView(RecyclerView rv, List<String> imgs) {
        rv.setLayoutManager(new GridLayoutManager(activity, 3));
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
        PictureSelector.create(activity)
                .themeStyle(R.style.picture_default_style)
                .isGif(true)
                .openExternalPreview(postion, selectList);
    }


}
