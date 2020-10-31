package com.yanlong.im.circle.follow;

import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.audio.AudioPlayUtil;
import com.luck.picture.lib.audio.IAudioPlayProgressListener;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.AttachmentBean;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.event.EventFactory;
import com.luck.picture.lib.tools.DoubleUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanlong.im.R;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.circle.CircleUIHelper;
import com.yanlong.im.circle.adapter.CircleFlowAdapter;
import com.yanlong.im.circle.bean.CircleCommentBean;
import com.yanlong.im.circle.bean.InteractMessage;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.details.CircleDetailsActivity;
import com.yanlong.im.circle.mycircle.FriendTrendsActivity;
import com.yanlong.im.circle.mycircle.MyInteractActivity;
import com.yanlong.im.databinding.FragmentFollowBinding;
import com.yanlong.im.databinding.ViewNewCircleMessageBinding;
import com.yanlong.im.interf.ICircleClickListener;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.ui.ComplaintActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.AutoPlayUtils;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.UserUtil;
import com.yanlong.im.utils.audio.AudioPlayManager;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.base.bind.BaseBindMvpFragment;
import net.cb.cb.library.inter.ICircleSetupClick;
import net.cb.cb.library.net.NetWorkUtils;
import net.cb.cb.library.utils.DialogHelper;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.YLLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-07
 * @updateAuthor
 * @updateDate
 * @description 朋友圈 关注
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class FollowFragment extends BaseBindMvpFragment<FollowPresenter, FragmentFollowBinding> implements FollowView, ICircleClickListener {

    private CircleFlowAdapter mFlowAdapter;
    private List<MessageFlowItemBean> mFollowList;
    public static final String IS_OPEN = "is_open";
    private final int PAGE_SIZE = 20;
    private int mCurrentPage = 1;
    ViewNewCircleMessageBinding messageBinding;
    private YLLinearLayoutManager linearLayoutManager;
    private int firstOffset;
    private boolean isRefreshing;
    private CommonSelectDialog dialog;
    private CommonSelectDialog.Builder builder;
    private boolean isAudioPlaying = false;//是否语音正在播放

    protected FollowPresenter createPresenter() {
        return new FollowPresenter(getContext());
    }

    @Override
    public int setLayout() {
        return R.layout.fragment_follow;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mFollowList = new ArrayList<>();
        mFlowAdapter = new CircleFlowAdapter(mFollowList, true, false, this);
        bindingView.recyclerFollow.setAdapter(mFlowAdapter);
        linearLayoutManager = new YLLinearLayoutManager(getContext());
        bindingView.recyclerFollow.setLayoutManager(linearLayoutManager);
        bindingView.srlFollow.setRefreshHeader(new MaterialHeader(getActivity()));
        bindingView.srlFollow.setRefreshFooter(new ClassicsFooter(getActivity()));
        ((DefaultItemAnimator) bindingView.recyclerFollow.getItemAnimator()).setSupportsChangeAnimations(false);
        mPresenter.getFollowMomentList(mCurrentPage, PAGE_SIZE);

        messageBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.view_new_circle_message, null, false);
        mPresenter.getUnreadMsg();
        builder = new CommonSelectDialog.Builder(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void initEvent() {
        messageBinding.layoutNotice.setOnClickListener(v -> {
            if (ViewUtils.isFastDoubleClick()) {
                return;
            }
            mFlowAdapter.removeAllHeaderView();
            EventBus.getDefault().post(new EventFactory.UpdateNewMsgEvent());
            Intent intent = new Intent(getActivity(), MyInteractActivity.class);
            startActivity(intent);
        });
        bindingView.srlFollow.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                EventBus.getDefault().post(new EventFactory.UpdateRedEvent());
                EventFactory.HomePageRedDotEvent homePageRedDotEvent = new EventFactory.HomePageRedDotEvent();
                homePageRedDotEvent.ifShow = false;
                EventBus.getDefault().post(homePageRedDotEvent);
                mCurrentPage = 1;
                mPresenter.getFollowMomentList(mCurrentPage, PAGE_SIZE);
            }
        });
        bindingView.srlFollow.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getFollowMomentList(++mCurrentPage, PAGE_SIZE);
            }
        });
        mFlowAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (DoubleUtils.isFastDoubleClick()) {
                    return;
                }
                gotoCircleDetailsActivity(false, position);
                checkAudioStatus(true);
            }
        });
        mFlowAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (!NetWorkUtils.isNetworkConnected()) {
                    ToastUtil.show(getResources().getString(R.string.network_error_msg));
                    return;
                }
                Intent intent;
                MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(position).getData();
                switch (view.getId()) {
                    case R.id.iv_comment:// 评论
                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                            ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                            return;
                        }
                        gotoCircleDetailsActivity(true, position);
                        checkAudioStatus(true);
                        break;
                    case R.id.iv_header:// 头像
                        if (AudioPlayUtil.isPlay()) {
                            AudioPlayUtil.stopAudioPlay();
                        }
                        //如果是我自己，则跳朋友圈，其他人跳详细资料
                        if (messageInfoBean.getUid() == UserAction.getMyInfo().getUid().longValue()) {
                            intent = new Intent(getContext(), FriendTrendsActivity.class);
                            intent.putExtra("uid", messageInfoBean.getUid());
                            intent.putExtra(FriendTrendsActivity.POSITION, position);
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(getContext(), UserInfoActivity.class)
                                    .putExtra(UserInfoActivity.FROM, "FollowFragment")
                                    .putExtra(FriendTrendsActivity.POSITION, position)
                                    .putExtra(UserInfoActivity.ID, messageInfoBean.getUid()));
                        }
                        break;
                    case R.id.iv_like:// 点赞
                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                            ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                            return;
                        }
                        if (messageInfoBean.getLike() == PictureEnum.ELikeType.YES) {
                            mPresenter.comentCancleLike(messageInfoBean.getId(), messageInfoBean.getUid(), position);
                        } else {
                            mPresenter.comentLike(messageInfoBean.getId(), messageInfoBean.getUid(), position);
                        }
                        break;
                    case R.id.iv_setup:// 设置
                        DialogHelper.getInstance().createFollowDialog(getActivity(), "取消关注",
                                mPresenter.getUserType(messageInfoBean.getUid()) == 0 ? true : false, new ICircleSetupClick() {
                                    @Override
                                    public void onClickFollow() {
                                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                            ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                                            return;
                                        }
                                        showCancleFollowDialog(messageInfoBean.getUid(), position);
                                    }

                                    @Override
                                    public void onClickNoLook() {

                                    }

                                    @Override
                                    public void onClickChat(boolean isFriend) {
                                        if (isFriend) {
                                            startActivity(new Intent(getContext(), ChatActivity.class)
                                                    .putExtra(ChatActivity.AGM_TOUID, messageInfoBean.getUid()));
                                        } else {
                                            Intent intent = new Intent(getContext(), UserInfoActivity.class);
                                            intent.putExtra(UserInfoActivity.ID, messageInfoBean.getUid());
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onClickReport() {
                                        Intent intent = new Intent(getContext(), ComplaintActivity.class);
                                        intent.putExtra(ComplaintActivity.UID, messageInfoBean.getUid() + "");
                                        intent.putExtra(ComplaintActivity.FROM_WHERE, 1);
                                        intent.putExtra(ComplaintActivity.COMMENT_ID, 0);
                                        intent.putExtra(ComplaintActivity.DEFENDANT_UID, messageInfoBean.getUid());
                                        intent.putExtra(ComplaintActivity.MOMENT_ID, messageInfoBean.getId());
                                        startActivity(intent);
                                    }
                                });
                        break;
                    case R.id.iv_sign_picture:// 单张图片
                        List<AttachmentBean> attachmentBeans = new Gson().fromJson(messageInfoBean.getAttachment(),
                                new TypeToken<List<AttachmentBean>>() {
                                }.getType());
                        if (messageInfoBean.getType() != null && messageInfoBean.getType() == PictureEnum.EContentType.PICTRUE) {
                            toPicturePreview(0, attachmentBeans);
                            checkAudioStatus(false);
                        }
                        break;
                    case R.id.tv_user_name:// 昵称，没注销的用户才允许跳朋友圈
                        if (!TextUtils.isEmpty(messageInfoBean.getNickname()) || !TextUtils.isEmpty(messageInfoBean.getAvatar())) {
                            intent = new Intent(getContext(), FriendTrendsActivity.class);
                            intent.putExtra("uid", messageInfoBean.getUid());
                            intent.putExtra(FriendTrendsActivity.POSITION, position);
                            startActivity(intent);
                        } else {
                            ToastUtil.show("该用户已注销");
                        }
                        break;
                }
            }
        });
        bindingView.recyclerFollow.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                Jzvd jzvd = view.findViewById(R.id.video_player);
                if (jzvd != null && Jzvd.CURRENT_JZVD != null && jzvd.jzDataSource != null &&
                        jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.getCurrentUrl())) {
                    if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                        Jzvd.releaseAllVideos();
                    }
                }
            }
        });

        bindingView.recyclerFollow.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    AutoPlayUtils.onScrollPlayVideo(recyclerView, R.id.video_player,
                            linearLayoutManager.findFirstVisibleItemPosition(),
                            linearLayoutManager.findLastVisibleItemPosition());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy != 0) {
                    AutoPlayUtils.onScrollReleaseAllVideos(linearLayoutManager.findFirstVisibleItemPosition(),
                            linearLayoutManager.findLastVisibleItemPosition(), 0.2f);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshFollow(EventFactory.RefreshSignFollowEvent event) {
        if (isContain(event.id)) {
            mPresenter.queryById(event.id, event.uid, event.postion);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshFollow(EventFactory.RefreshFollowEvent event) {
        mCurrentPage = 1;
        mPresenter.getFollowMomentList(mCurrentPage, PAGE_SIZE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void checkUnreadMsg(EventFactory.CheckUnreadMsgEvent event) {
        mPresenter.getUnreadMsg();
        if (event.data instanceof InteractMessage) {
            InteractMessage message = (InteractMessage) event.data;
            if (message.getInteractType() == 1 || message.getInteractType() == 2 || message.getInteractType() == 4) {//点赞，评论或投票互动
                int position = 0;
                if (isContain(message.getMomentId(), position)) {
                    mPresenter.queryById(message.getMomentId(), message.getFromUid(), position);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateNewMsgEvent(EventFactory.UpdateNewMsgEvent event) {
        mFlowAdapter.removeAllHeaderView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFollowState(EventFactory.UpdateFollowStateEvent event) {
        //更改目标用户全部关注状态
        for (MessageFlowItemBean bean : mFlowAdapter.getData()) {
            MessageInfoBean msgBean = (MessageInfoBean) bean.getData();
            if (msgBean.getUid().longValue() == event.uid) {
                if (event.type == 1) {
                    msgBean.setFollow(true);
                } else {
                    msgBean.setFollow(false);
                }
            }
            mFlowAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDeleteItem(int position) {
        mFollowList.remove(position);
        mFlowAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAddFriendSuccess(boolean isSuccess) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteItem(EventFactory.DeleteItemTrend event) {
        //推荐列表和关注列表只更新自己点击的数据
        onDeleteItem(event.position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateOneTrend(EventFactory.UpdateOneTrendEvent event) {
        //更新关注单条动态
        if (event.action == 3) {
            for (int i = 0; i < mFlowAdapter.getData().size(); i++) {
                MessageFlowItemBean bean = mFlowAdapter.getData().get(i);
                MessageInfoBean msgBean = (MessageInfoBean) bean.getData();
                //如果找到这一条，则刷新点赞状态，点赞数+1
                if (msgBean.getId() != null && msgBean.getId().longValue() == event.id) {
                    if (event.isLike == 1) {
                        msgBean.setLike(1);
                        msgBean.setLikeCount(msgBean.getLikeCount() + 1);
                    } else {
                        msgBean.setLike(0);
                        msgBean.setLikeCount(msgBean.getLikeCount() - 1);
                    }
                    mFlowAdapter.notifyItemChanged(i);
                }
            }
        }
    }

    private void gotoCircleDetailsActivity(boolean isOpen, int position) {
        if (!NetWorkUtils.isNetworkConnected()) {
            ToastUtil.show(getResources().getString(R.string.network_error_msg));
            return;
        }
        Postcard postcard = ARouter.getInstance().build(CircleDetailsActivity.path);
        postcard.withBoolean(IS_OPEN, isOpen);
        postcard.withBoolean(CircleDetailsActivity.SOURCE_TYPE, true);
        MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(position).getData();
        postcard.withInt(CircleDetailsActivity.ITEM_DATA_POSTION, position);
        postcard.withString(CircleDetailsActivity.ITEM_DATA, new Gson().toJson(messageInfoBean));
        postcard.withInt(CircleDetailsActivity.ITEM_DATA_TYPE, mFlowAdapter.getData().get(position).getItemType());
        postcard.withString(CircleDetailsActivity.FROM, "FollowFragment");//来自广场关注列表
        postcard.navigation();
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
            localMedia.setCutPath(bean.getUrl());
            localMedia.setCompressPath(bean.getUrl());
            localMedia.setSize(bean.getSize());
            localMedia.setWidth(bean.getWidth());
            localMedia.setHeight(bean.getHeight());
            selectList.add(localMedia);
        }
        PictureSelector.create(getActivity())
                .themeStyle(R.style.picture_default_style)
                .isGif(true)
                .openExternalPreview1(position, selectList, "", 0L, PictureConfig.FROM_CIRCLE, "");
    }

    @Override
    public void onSuccess(List<MessageFlowItemBean> list) {
        isRefreshing = false;
        if (mCurrentPage == 1) {
            mFollowList.clear();
        }
        if (mCurrentPage == 1 && list.size() == 0) {
            View view = View.inflate(getActivity(), R.layout.view_follow_no_data, null);
            bindingView.srlFollow.post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            bindingView.srlFollow.getHeight());
                    view.setLayoutParams(layoutParams);
                    mFlowAdapter.setEmptyView(view);
                }
            });
            mFlowAdapter.removeAllHeaderView();
            bindingView.srlFollow.setEnableLoadMore(false);
            bindingView.srlFollow.finishLoadMore();
        } else {
            if (list != null && list.size() > 0) {
                mFollowList.addAll(list);
                mFlowAdapter.notifyDataSetChanged();
            }

            if (list == null || list.size() == 0) {
                bindingView.srlFollow.setEnableLoadMore(false);
                bindingView.srlFollow.finishLoadMore();
            } else if (list.size() > 0 && list.size() < PAGE_SIZE) {
                bindingView.srlFollow.finishLoadMoreWithNoMoreData();
            } else {
                bindingView.srlFollow.setEnableLoadMore(true);
                bindingView.srlFollow.finishLoadMore();
            }

            if (mCurrentPage == 1) {
                bindingView.recyclerFollow.scrollToPosition(0);
            }
        }
        bindingView.srlFollow.finishRefresh();

    }

    @Override
    public void onSuccess(int position, MessageFlowItemBean flowItemBean) {
        try {
            if (flowItemBean != null) {
                // TODO 服务端没返回头像跟昵称所以取原来的数据
                MessageInfoBean serverInfoBean = (MessageInfoBean) flowItemBean.getData();
                MessageInfoBean locationInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(position).getData();
                serverInfoBean.setAvatar(locationInfoBean.getAvatar());
                serverInfoBean.setNickname(locationInfoBean.getNickname());
                mFlowAdapter.getData().get(position).setData(flowItemBean.getData());
                if (mFlowAdapter.getHeaderLayoutCount() > 0) {
                    position = position + 1;
                }
                mFlowAdapter.notifyItemChanged(position);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onCommentSuccess(CircleCommentBean commentBean) {

    }

    @Override
    public void onVoteSuccess(int parentPosition, String msg) {
        try {
            MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(parentPosition).getData();
            if (messageInfoBean != null) {
                mPresenter.queryById(messageInfoBean.getId(), messageInfoBean.getUid(), parentPosition);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onLikeSuccess(int position, String msg) {
        MessageInfoBean messageInfoBean = mFlowAdapter.getData().get(position).getData();
        int like;
        if (messageInfoBean.getLike() != null) {
            like = messageInfoBean.getLike().intValue() == PictureEnum.ELikeType.YES ? 0 : 1;
        } else {
            like = 1;
        }
        if (messageInfoBean.getLikeCount() != null) {
            if (like == PictureEnum.ELikeType.YES) {
                messageInfoBean.setLikeCount(messageInfoBean.getLikeCount() + 1);
            } else {
                messageInfoBean.setLikeCount(messageInfoBean.getLikeCount() - 1);
            }
        } else {
            messageInfoBean.setLikeCount(1);
        }
        messageInfoBean.setLike(like);
        if (mFlowAdapter.getHeaderLayoutCount() > 0) {
            position = position + 1;
        }
        mFlowAdapter.notifyItemChanged(position);
    }

    @Override
    public void onSuccess(int postion, boolean isCancelFollow, String msg) {
        mCurrentPage = 1;
        mPresenter.getFollowMomentList(mCurrentPage, PAGE_SIZE);
    }

    @Override
    public void onShowMessage(String msg) {
        isRefreshing = false;
        if (!TextUtils.isEmpty(msg)) {
            ToastUtil.show(msg);
        }
        bindingView.srlFollow.finishLoadMore();
        bindingView.srlFollow.finishRefresh();
    }

    @Override
    public void onCommentSuccess(boolean isAdd) {

    }

    @Override
    public void showUnreadMsg(int unCount, String avatar) {
        //是否有未读互动消息
        if (unCount > 0) {
            if (mFlowAdapter.getHeaderLayoutCount() == 0) {
                mFlowAdapter.addHeaderView(messageBinding.getRoot());
            }
            Glide.with(getActivity())
                    .asBitmap()
                    .load(avatar)
                    .apply(GlideOptionsUtil.headImageOptions())
                    .into(messageBinding.ivNoticeAvatar);
            messageBinding.tvNotice.setText(unCount + "条新消息");
        } else {
            mFlowAdapter.removeAllHeaderView();
        }
    }

    /**
     * 内容展开、收起
     *
     * @param position
     * @param parentPosition 父类位置
     * @param type           0：展开、收起 1：详情 2文字投票 3图片投票
     */
    @Override
    public void onClick(int position, int parentPosition, int type, View view) {
        if (type == CoreEnum.EClickType.CONTENT_DETAILS || type == CoreEnum.EClickType.CONTENT_DOWN) {
            if (type == CoreEnum.EClickType.CONTENT_DOWN) {
                MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(position).getData();
                messageInfoBean.setShowAll(!messageInfoBean.isShowAll());
                if (mFlowAdapter.getHeaderLayoutCount() > 0) {
                    position = position + 1;
                }
                mFlowAdapter.notifyItemChanged(position);
            } else {
                if (!DoubleUtils.isFastDoubleClick()) {
                    gotoCircleDetailsActivity(false, position);
                    checkAudioStatus(true);
                }
            }
        } else if (type == CoreEnum.EClickType.CLICK_VOICE) {
            MessageInfoBean messageInfoBean = mFlowAdapter.getData().get(position).getData();
            if (messageInfoBean != null && messageInfoBean.getType() != null &&
                    (messageInfoBean.getType() == PictureEnum.EContentType.VOICE || messageInfoBean.getType() == PictureEnum.EContentType.VOICE_AND_VOTE)) {
                playVoice(messageInfoBean);
            }

        } else {
            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                return;
            }
            MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(parentPosition).getData();
            mPresenter.voteAnswer(position + 1, parentPosition, messageInfoBean.getId(), messageInfoBean.getUid());
        }
    }

    /**
     * 是否取消关注提示弹框
     *
     * @param uid
     * @param position
     */
    private void showCancleFollowDialog(long uid, int position) {
        dialog = builder.setTitle("是否取消关注?")
                .setShowLeftText(true)
                .setRightText("确认")
                .setLeftText("取消")
                .setRightOnClickListener(v -> {
                    mPresenter.followCancle(uid, position);
                    dialog.dismiss();
                })
                .setLeftOnClickListener(v ->
                        dialog.dismiss()
                )
                .build();
        dialog.show();
    }

    @Override
    public void notifyShow() {
        if (mFlowAdapter == null || mFlowAdapter.getItemCount() <= 0) {
            if (mPresenter != null) {
                mPresenter.getFollowMomentList(mCurrentPage, PAGE_SIZE);
            }
        }
    }

    //列表是否包含該消息
    private boolean isContain(Long id) {
        boolean isContain = false;
        if (mFlowAdapter != null && mFlowAdapter.getData() != null) {
            for (MessageFlowItemBean<MessageInfoBean> bean : mFlowAdapter.getData()) {
                if (bean.getData().getId() != null && id != null && bean.getData().getId().equals(id)) {
                    isContain = true;
                    break;
                }
            }
        }
        return isContain;
    }

    //列表是否包含該消息
    private boolean isContain(Long id, int position) {
        boolean isContain = false;
        if (mFlowAdapter != null && mFlowAdapter.getData() != null) {
            int size = mFlowAdapter.getData().size();
            for (int i = 0; i < size; i++) {
                MessageFlowItemBean<MessageInfoBean> bean = mFlowAdapter.getData().get(i);
                if (bean.getData().getId() != null && id != null && bean.getData().getId().equals(id)) {
                    isContain = true;
                    position = i;
                    break;
                }
            }
        }
        return isContain;
    }


    public void playVoice(MessageInfoBean messageInfoBean) {
        if (!TextUtils.isEmpty(messageInfoBean.getAttachment())) {
            List<AttachmentBean> attachmentBeans = null;
            try {
                attachmentBeans = new Gson().fromJson(messageInfoBean.getAttachment(),
                        new TypeToken<List<AttachmentBean>>() {
                        }.getType());
            } catch (Exception e) {
                attachmentBeans = new ArrayList<>();
            }
            if (attachmentBeans != null && attachmentBeans.size() > 0) {
                AttachmentBean attachmentBean = attachmentBeans.get(0);
                if (messageInfoBean.isPlay()) {
                    if (AudioPlayManager.getInstance().isPlay(Uri.parse(attachmentBean.getUrl()))) {
                        AudioPlayManager.getInstance().stopPlay();
                    }
                } else {

                    AudioPlayUtil.startAudioPlay(getActivity(), attachmentBean.getUrl(), new IAudioPlayProgressListener() {
                        @Override
                        public void onStart(Uri var1) {
                            isAudioPlaying = true;
                            messageInfoBean.setPlay(true);
                            messageInfoBean.setPlayProgress(0);
                            updatePosition(messageInfoBean);
                        }

                        @Override
                        public void onStop(Uri var1) {
                            isAudioPlaying = false;
                            messageInfoBean.setPlay(false);
                            updatePosition(messageInfoBean);

                        }

                        @Override
                        public void onComplete(Uri var1) {
                            isAudioPlaying = false;
                            messageInfoBean.setPlay(false);
                            messageInfoBean.setPlayProgress(100);
                            updatePosition(messageInfoBean);

                        }

                        @Override
                        public void onProgress(int progress) {
                            LogUtil.getLog().i("语音", "播放进度--" + progress);
                            messageInfoBean.setPlay(true);
                            messageInfoBean.setPlayProgress(progress);
                            updatePosition(messageInfoBean);
                        }
                    });

                }
            }
        }
    }

    private void updatePosition(MessageInfoBean messageInfoBean) {
        if (mFlowAdapter == null || mFlowAdapter.getData() == null) {
            return;
        }
        bindingView.recyclerFollow.postDelayed(new Runnable() {
            @Override
            public void run() {
                MessageFlowItemBean bean = new MessageFlowItemBean(CircleUIHelper.getHolderType(messageInfoBean.getType()), messageInfoBean);
                int position = mFlowAdapter.getData().indexOf(bean);
                LogUtil.getLog().i("语音", "position=" + position + "  id=" + messageInfoBean.getId() + "  isPlay=" + messageInfoBean.isPlay());
                if (position >= 0) {
                    mFlowAdapter.getData().set(position, bean);
                    if (mFlowAdapter.getHeaderLayoutCount() > 0) {
                        position = position + 1;
                    }
                    mFlowAdapter.notifyItemChanged(position);
                }
            }
        }, 100);
    }

    private void checkAudioStatus(boolean stop) {
        if (isAudioPlaying && stop) {
            AudioPlayUtil.stopAudioPlay();
        }
    }
}