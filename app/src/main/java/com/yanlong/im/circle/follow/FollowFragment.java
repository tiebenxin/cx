package com.yanlong.im.circle.follow;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.audio.AudioPlayUtil;
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
import com.yanlong.im.chat.ui.VideoPlayActivity;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.circle.adapter.CircleFlowAdapter;
import com.yanlong.im.circle.bean.CircleCommentBean;
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
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.UserUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.base.bind.BaseBindMvpFragment;
import net.cb.cb.library.inter.ICircleSetupClick;
import net.cb.cb.library.net.NetWorkUtils;
import net.cb.cb.library.utils.DialogHelper;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.YLLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

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
    private int firstOffset;
    private boolean isRefreshing;

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
        bindingView.recyclerFollow.setLayoutManager(new YLLinearLayoutManager(getContext()));
        bindingView.srlFollow.setRefreshHeader(new MaterialHeader(getActivity()));
        bindingView.srlFollow.setRefreshFooter(new ClassicsFooter(getActivity()));
        ((DefaultItemAnimator) bindingView.recyclerFollow.getItemAnimator()).setSupportsChangeAnimations(false);
        mPresenter.getFollowMomentList(mCurrentPage, PAGE_SIZE);

        messageBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.view_new_circle_message, null, false);
        mPresenter.getUnreadMsg();
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
            public void onRefresh(@android.support.annotation.NonNull RefreshLayout refreshLayout) {
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
            }
        });
        mFlowAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (!NetWorkUtils.isNetworkConnected()) {
                    ToastUtil.show(getResources().getString(R.string.network_error_msg));
                    return;
                }
                MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(position).getData();
                switch (view.getId()) {
                    case R.id.iv_comment:// 评论
                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                            ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                            return;
                        }
                        gotoCircleDetailsActivity(true, position);
                        break;
                    case R.id.iv_header:// 头像
                        if (AudioPlayUtil.isPlay()) {
                            AudioPlayUtil.stopAudioPlay();
                        }
                        //如果是我自己，则跳朋友圈，其他人跳详细资料
                        if (messageInfoBean.getUid() == UserAction.getMyInfo().getUid().longValue()) {
                            Intent intent = new Intent(getContext(), FriendTrendsActivity.class);
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
                                        mPresenter.followCancle(messageInfoBean.getUid(), position);
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
                                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                            ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                                            return;
                                        }
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
                    case R.id.rl_video:// 播放视频
                        AudioPlayUtil.stopAudioPlay();
                        List<AttachmentBean> attachmentBeans = new Gson().fromJson(messageInfoBean.getAttachment(),
                                new TypeToken<List<AttachmentBean>>() {
                                }.getType());
                        if (messageInfoBean.getType() != null && messageInfoBean.getType() == PictureEnum.EContentType.PICTRUE) {
                            toPictruePreview(0, attachmentBeans);
                        } else {
                            Intent intent = new Intent(getContext(), VideoPlayActivity.class);
                            if (attachmentBeans.size() > 0) {
                                intent.putExtra("videopath", attachmentBeans.get(0).getUrl());
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                        }
                        break;
                    case R.id.tv_user_name:// 昵称，没注销的用户才允许跳朋友圈
                        if (!TextUtils.isEmpty(messageInfoBean.getNickname()) || !TextUtils.isEmpty(messageInfoBean.getAvatar())) {
                            Intent intent = new Intent(getContext(), FriendTrendsActivity.class);
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
        //TODO 需求改为只要不在本界面才暂停，随意滑动不暂停
        bindingView.recyclerFollow.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@android.support.annotation.NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                //判断是当前layoutManager是否为LinearLayoutManager
                // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取最后一个可见view的位置
                    int lastItemPosition = linearManager.findLastVisibleItemPosition();
                    //获取第一个可见view的位置
                    int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    if (mFlowAdapter != null) {
                        mFlowAdapter.setFirstVisiblePosition(firstItemPosition);
                    }
                    //停止滑动状态
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        firstItemPosition = linearManager.findFirstVisibleItemPosition();
                        View topView = layoutManager.getChildAt(firstItemPosition);
                        if (topView != null) {
                            //获取与该view的底部的偏移量
                            firstOffset = topView.getTop();
                        }
                        if (mFlowAdapter != null) {
                            mFlowAdapter.setFirstVisiblePosition(firstItemPosition);
                            mFlowAdapter.notifyItemChanged(firstItemPosition);
                        }
                    }
                    if (!isRefreshing && lastItemPosition >= mFlowAdapter.getItemCount() - 5) {
                        isRefreshing = true;
                        mPresenter.getFollowMomentList(++mCurrentPage, PAGE_SIZE);
                    }
                    // 判断当前是否有语音或视频播放
//                    if (AudioPlayUtil.isPlay()) {
//                        if (AudioPlayUtil.getRecyclerviewPosition() == -1 ||
//                                AudioPlayUtil.getRecyclerviewPosition() < firstItemPosition ||
//                                AudioPlayUtil.getRecyclerviewPosition() > lastItemPosition) {
//                            AudioPlayUtil.stopAudioPlay();
//                        }
//                    }
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshFollow(EventFactory.RefreshSignFollowEvent event) {
        mPresenter.queryById(event.id, event.uid, event.postion);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshFollow(EventFactory.RefreshFollowEvent event) {
        mCurrentPage = 1;
        mPresenter.getFollowMomentList(mCurrentPage, PAGE_SIZE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void checkUnreadMsg(EventFactory.CheckUnreadMsgEvent event) {
        mPresenter.getUnreadMsg();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteItem(EventFactory.DeleteItemTrend event) {
        //推荐列表和关注列表只更新自己点击的数据
        if (event.fromWhere.equals("FollowFragment")) {
            onDeleteItem(event.position);
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
        PictureSelector.create(getActivity())
                .themeStyle(R.style.picture_default_style)
                .isGif(true)
                .openExternalPreview1(postion, selectList, "", 0L, PictureConfig.FROM_CIRCLE, "");
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
        MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(position).getData();
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
     * @param postion
     * @param parentPostion 父类位置
     * @param type          0：展开、收起 1：详情 2文字投票 3图片投票
     */
    @Override
    public void onClick(int postion, int parentPostion, int type, View view) {
        if (type == CoreEnum.EClickType.CONTENT_DETAILS || type == CoreEnum.EClickType.CONTENT_DOWN) {
            if (type == CoreEnum.EClickType.CONTENT_DOWN) {
                MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(postion).getData();
                messageInfoBean.setShowAll(!messageInfoBean.isShowAll());
                if (mFlowAdapter.getHeaderLayoutCount() > 0) {
                    postion = postion + 1;
                }
                mFlowAdapter.notifyItemChanged(postion);
            } else {
                if (!DoubleUtils.isFastDoubleClick()) {
                    gotoCircleDetailsActivity(false, postion);
                }
            }
        } else {
            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                return;
            }
            MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(parentPostion).getData();
            mPresenter.voteAnswer(postion + 1, parentPostion, messageInfoBean.getId(), messageInfoBean.getUid());
        }
    }
}