package com.yanlong.im.circle.recommend;

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
import android.widget.TextView;

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
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.details.CircleDetailsActivity;
import com.yanlong.im.circle.mycircle.MyInteractActivity;
import com.yanlong.im.databinding.FragmentRecommendBinding;
import com.yanlong.im.databinding.ViewNewCircleMessageBinding;
import com.yanlong.im.interf.ICircleClickListener;
import com.yanlong.im.user.ui.ComplaintActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.base.bind.BaseBindMvpFragment;
import net.cb.cb.library.inter.ICircleSetupClick;
import net.cb.cb.library.net.NetWorkUtils;
import net.cb.cb.library.utils.DialogHelper;
import net.cb.cb.library.utils.SpUtil;
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
 * @description 朋友圈 推荐
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class RecommendFragment extends BaseBindMvpFragment<RecommendPresenter, FragmentRecommendBinding>
        implements RecommendView, ICircleClickListener {

    private CircleFlowAdapter mFlowAdapter;
    private List<MessageFlowItemBean> mFollowList;
    public static final String IS_OPEN = "is_open";
    public static final String REFRESH_COUNT = "refresh_count";
    private final int PAGE_SIZE = 8;
    private Long mCurrentPage = 0l;
    private boolean mIsAddLocation = false;// 是否添加了本地
    private final int MAX_REFRESH_COUNT = 2;// 刷新次数大于2不显示
    private final int MAX_REFRESH_MINUTE = 2;// 超过3分钟不显示
    ViewNewCircleMessageBinding messageBinding;

    protected RecommendPresenter createPresenter() {
        return new RecommendPresenter(getContext());
    }

    @Override
    public int setLayout() {
        return R.layout.fragment_recommend;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mFollowList = new ArrayList<>();
        mFlowAdapter = new CircleFlowAdapter(mFollowList, false, false, this);
        bindingView.recyclerRecommend.setAdapter(mFlowAdapter);
        bindingView.recyclerRecommend.setLayoutManager(new YLLinearLayoutManager(getContext()));
        bindingView.srlFollow.setRefreshHeader(new MaterialHeader(getActivity()));
        bindingView.srlFollow.setRefreshFooter(new ClassicsFooter(getActivity()));
        ((DefaultItemAnimator) bindingView.recyclerRecommend.getItemAnimator()).setSupportsChangeAnimations(false);
        mPresenter.getRecommendMomentList(mCurrentPage, PAGE_SIZE, 0);

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

    /**
     * 添加刚发布的一条动态
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addRecomendEvent(EventFactory.AddRecomendEvent event) {
        MessageInfoBean infoBean = new Gson().fromJson(event.content, MessageInfoBean.class);
        MessageFlowItemBean flowItemBean = new MessageFlowItemBean(event.type, infoBean);
        if (mIsAddLocation) {
            mFollowList.remove(0);
        }
        mFollowList.add(0, flowItemBean);
        bindingView.recyclerRecommend.scrollToPosition(0);
        mFlowAdapter.notifyDataSetChanged();
        mIsAddLocation = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshFollow(EventFactory.RefreshSignRecomendEvent event) {
        mPresenter.queryById(event.id, event.uid, event.postion);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshFollow(EventFactory.RefreshRecomendEvent event) {
        mCurrentPage = 0l;
        mPresenter.getRecommendMomentList(mCurrentPage, PAGE_SIZE, 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void checkUnreadMsg(EventFactory.CheckUnreadMsgEvent event) {
        mPresenter.getUnreadMsg();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateNewMsgEvent(EventFactory.UpdateNewMsgEvent event) {
        mFlowAdapter.removeAllHeaderView();
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
                mCurrentPage = 0l;
                mPresenter.getRecommendMomentList(mCurrentPage, PAGE_SIZE, 0);
            }
        });
        bindingView.srlFollow.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mFollowList != null && mFollowList.size() == PAGE_SIZE) {
                    mCurrentPage = 0l;
                }
                mPresenter.getRecommendMomentList(mCurrentPage, PAGE_SIZE, 1);
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
                        gotoCircleDetailsActivity(true, position);
                        break;
                    case R.id.iv_header:// 头像
                        startActivity(new Intent(getContext(), UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, messageInfoBean.getUid())
                                .putExtra(UserInfoActivity.SHOW_TRENDS, true));
                        break;
                    case R.id.iv_like:// 点赞
                        if (messageInfoBean.getLike() == PictureEnum.ELikeType.YES) {
                            mPresenter.comentCancleLike(messageInfoBean.getId(), messageInfoBean.getUid(), position);
                        } else {
                            mPresenter.comentLike(messageInfoBean.getId(), messageInfoBean.getUid(), position);
                        }
                        break;
                    case R.id.iv_setup:// 设置
                        DialogHelper.getInstance().createFollowDialog(getActivity(), messageInfoBean.isFollow() ? "取消关注" : "关注TA",
                                mPresenter.getUserType(messageInfoBean.getUid()) == 0 ? true : false, new ICircleSetupClick() {
                                    @Override
                                    public void onClickFollow() {
                                        if (messageInfoBean.isFollow()) {
                                            mPresenter.followCancle(messageInfoBean.getUid(), position);
                                        } else {
                                            mPresenter.followAdd(messageInfoBean.getUid(), position);
                                        }
                                    }

                                    @Override
                                    public void onClickNoLook() {
                                        mPresenter.addSee(messageInfoBean.getUid());
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
                }
            }
        });

        bindingView.recyclerRecommend.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    //停止滑动状态
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        if (mFlowAdapter != null) {
                            mFlowAdapter.setFirstVisiblePosition(firstItemPosition);
                        }
                    }
                    // 判断当前是否有语音或视频播放
                    if (AudioPlayUtil.isPlay()) {
                        if (AudioPlayUtil.getRecyclerviewPosition() == -1 ||
                                AudioPlayUtil.getRecyclerviewPosition() < firstItemPosition ||
                                AudioPlayUtil.getRecyclerviewPosition() > lastItemPosition) {
                            AudioPlayUtil.stopAudioPlay();
                        }
                    }
                }
            }
        });
    }

    private void gotoCircleDetailsActivity(boolean isOpen, int position) {
        if (!NetWorkUtils.isNetworkConnected()) {
            ToastUtil.show(getResources().getString(R.string.network_error_msg));
            return;
        }
        Postcard postcard = ARouter.getInstance().build(CircleDetailsActivity.path);
        postcard.withBoolean(IS_OPEN, isOpen);
        MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(position).getData();
        postcard.withBoolean(CircleDetailsActivity.SOURCE_TYPE, messageInfoBean.isFollow());
        postcard.withInt(CircleDetailsActivity.ITEM_DATA_POSTION, position);
        postcard.withString(CircleDetailsActivity.ITEM_DATA, new Gson().toJson(messageInfoBean));
        postcard.withInt(CircleDetailsActivity.ITEM_DATA_TYPE, mFlowAdapter.getData().get(position).getItemType());
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
            MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(parentPostion).getData();
            mPresenter.voteAnswer(postion + 1, parentPostion, messageInfoBean.getId(), messageInfoBean.getUid());
        }
    }

    @Override
    public void onSuccess(List<MessageFlowItemBean> list, int serviceType) {
        if (serviceType == 0) {
            mFollowList.clear();
            // 判断缓存是否有数据
            SpUtil spUtil = SpUtil.getSpUtil();
            String value = spUtil.getSPValue(REFRESH_COUNT, "");
            if (!TextUtils.isEmpty(value)) {
                MessageInfoBean infoBean = new Gson().fromJson(value, MessageInfoBean.class);
                // 有则 判断刷新次数是否大于2 ，时间是否大于3分钟
                long minute = System.currentTimeMillis() - infoBean.getCreateTime();
                minute = minute / 1000 / 60;
                if (infoBean.getRefreshCount() >= MAX_REFRESH_COUNT || (int) minute > MAX_REFRESH_MINUTE) {
                    spUtil.putSPValue(REFRESH_COUNT, "");
                } else {
                    // 添加到头部
                    infoBean.setRefreshCount(infoBean.getRefreshCount() + 1);
                    spUtil.putSPValue(REFRESH_COUNT, new Gson().toJson(infoBean));
                    MessageFlowItemBean flowItemBean = mPresenter.createFlowItemBean(infoBean);
                    mFollowList.add(flowItemBean);
                }
            }
        }
        if (serviceType == 0 && list.size() == 0) {
            View view = View.inflate(getActivity(), R.layout.view_follow_no_data, null);
            TextView tvMessage = view.findViewById(R.id.tv_message);
            tvMessage.setText("暂无推荐消息");
            bindingView.srlFollow.post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            bindingView.srlFollow.getHeight());
                    view.setLayoutParams(layoutParams);
                    mFlowAdapter.setEmptyView(view);
                }
            });
            bindingView.srlFollow.setEnableLoadMore(false);
            bindingView.srlFollow.finishLoadMore();
        } else {
            if (list != null && list.size() > 0) {
                mFollowList.addAll(list);
                mFlowAdapter.notifyDataSetChanged();
            }

            if (list == null || list.size() == 0) {
                bindingView.srlFollow.finishLoadMoreWithNoMoreData();
            } else if (list.size() > 0 && list.size() < PAGE_SIZE) {
                bindingView.srlFollow.finishLoadMoreWithNoMoreData();
            } else {
                mCurrentPage = ((MessageInfoBean) list.get(list.size() - 1).getData()).getId();
                bindingView.srlFollow.setEnableLoadMore(true);
                bindingView.srlFollow.finishLoadMore();
            }

            if (serviceType == 0) {
                bindingView.recyclerRecommend.scrollToPosition(0);
            }
        }
        bindingView.srlFollow.finishRefresh();

    }

    @Override
    public void onShowMessage(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            ToastUtil.show(msg);
        }
        bindingView.srlFollow.finishLoadMore();
        bindingView.srlFollow.finishRefresh();
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
    public void onSuccess(int position, boolean isFollow, String msg) {
        ((MessageInfoBean) mFollowList.get(position).getData()).setFollow(isFollow);
        mFlowAdapter.notifyItemChanged(position);
    }

    @Override
    public void addSeeSuccess(String msg) {
        mCurrentPage = 0l;
        mPresenter.getRecommendMomentList(mCurrentPage, PAGE_SIZE, 0);
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
                // 判断缓存是否有，有则更新
                SpUtil spUtil = SpUtil.getSpUtil();
                String value = spUtil.getSPValue(REFRESH_COUNT, "");
                if (!TextUtils.isEmpty(value)) {
                    MessageInfoBean infoBean = new Gson().fromJson(value, MessageInfoBean.class);
                    if (infoBean.getId().longValue() == serverInfoBean.getId().longValue()) {
                        spUtil.putSPValue(REFRESH_COUNT, new Gson().toJson(serverInfoBean));
                    }
                }
                mFlowAdapter.notifyItemChanged(position);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onVoteSuccess(int parentPostion, String msg) {
        try {
            MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(parentPostion).getData();
            if (messageInfoBean != null) {
                mPresenter.queryById(messageInfoBean.getId(), messageInfoBean.getUid(), parentPostion);
            }
        } catch (Exception e) {

        }
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
        //通知首页显示红点
        EventFactory.HomePageShowUnreadMsgEvent event = new EventFactory.HomePageShowUnreadMsgEvent();
        event.num = unCount;
        EventBus.getDefault().post(event);
    }
}