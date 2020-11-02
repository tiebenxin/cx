package com.yanlong.im.circle.recommend;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.yanlong.im.circle.bean.InteractMessage;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.details.CircleDetailsActivity;
import com.yanlong.im.circle.mycircle.FriendTrendsActivity;
import com.yanlong.im.circle.mycircle.MyInteractActivity;
import com.yanlong.im.circle.mycircle.MyTrendsActivity;
import com.yanlong.im.databinding.FragmentRecommendBinding;
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
import net.cb.cb.library.utils.SpUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.YLLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.jzvd.Jzvd;

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
    private YLLinearLayoutManager linearLayoutManager;
    private int firstItemPosition;
    private int firstOffset;
    private boolean isRefreshing;
    private CommonSelectDialog dialog;
    private CommonSelectDialog.Builder builder;
    private boolean isAudioPlaying = false;//是否语音正在播放
    private MessageInfoBean currentMessage;


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
        linearLayoutManager = new YLLinearLayoutManager(getContext());
        bindingView.recyclerRecommend.setLayoutManager(linearLayoutManager);
        bindingView.srlFollow.setRefreshHeader(new MaterialHeader(getActivity()));
        bindingView.srlFollow.setRefreshFooter(new ClassicsFooter(getActivity()));
        ((DefaultItemAnimator) bindingView.recyclerRecommend.getItemAnimator()).setSupportsChangeAnimations(false);
        mPresenter.getRecommendMomentList(mCurrentPage, PAGE_SIZE, 0);

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
        mPresenter.mModel.setData(mFollowList);
        scrollToPosition(0);
        notifyChangeAll();
        mIsAddLocation = true;
    }

    private void notifyChangeAll() {
        if (mFlowAdapter != null) {
            mFlowAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshFollow(EventFactory.RefreshSignRecomendEvent event) {
        if (isContain(event.id)) {
            mPresenter.queryById(event.id, event.uid, event.postion);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshFollow(EventFactory.RefreshRecomendEvent event) {
        mCurrentPage = 0l;
        mPresenter.getRecommendMomentList(mCurrentPage, PAGE_SIZE, 0);
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
    public void deleteItem(EventFactory.DeleteItemTrend event) {
        //推荐列表和关注列表只更新自己点击的数据
        onDeleteItem(event.position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFollowState(EventFactory.UpdateFollowStateEvent event) {
        //更改目标用户全部关注状态
        for (MessageFlowItemBean bean : mFlowAdapter.getData()) {
            MessageInfoBean msgBean = (MessageInfoBean) bean.getData();
            if (msgBean.getUid() != null && msgBean.getUid().longValue() == event.uid) {
                if (event.type == 1) {
                    msgBean.setFollow(true);
                } else {
                    msgBean.setFollow(false);
                }
            }
            mFlowAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void noSee(EventFactory.NoSeeEvent event) {
        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
            ToastUtil.show(getActivity().getString(R.string.user_disable_message));
            return;
        }
        mPresenter.addSee(event.uid);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateOneTrend(EventFactory.UpdateOneTrendEvent event) {
        //更新推荐单条动态
        if (event.action == 3) {
            for (int i = 0; i < mFlowAdapter.getData().size(); i++) {
                MessageFlowItemBean bean = mFlowAdapter.getData().get(i);
                MessageInfoBean msgBean = (MessageInfoBean) bean.getData();
                //如果找到这一条，则刷新
                if (msgBean.getId() != null && msgBean.getId().longValue() == event.id) {
                    mPresenter.queryById(event.id, msgBean.getUid(), i);
                    break;
                }
            }
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
                mCurrentPage = 0l;
                mPresenter.getRecommendMomentList(mCurrentPage, PAGE_SIZE, 0);
            }
        });
        bindingView.srlFollow.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
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
                            intent = new Intent(getContext(), MyTrendsActivity.class);
                            startActivity(intent);
                        } else {
                            startActivity(new Intent(getContext(), UserInfoActivity.class)
                                    .putExtra(UserInfoActivity.FROM, "RecommendFragment")
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
                        DialogHelper.getInstance().createFollowDialog(getActivity(), messageInfoBean.isFollow() ? "取消关注" : "关注TA",
                                mPresenter.getUserType(messageInfoBean.getUid()) == 0 ? true : false, new ICircleSetupClick() {
                                    @Override
                                    public void onClickFollow() {
                                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                            ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                                            return;
                                        }
                                        if (messageInfoBean.isFollow()) {
                                            showCancleFollowDialog(messageInfoBean.getUid(), position);
                                        } else {
                                            mPresenter.followAdd(messageInfoBean.getUid(), position);
                                        }
                                    }

                                    @Override
                                    public void onClickNoLook(boolean isDel) {
                                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                            ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                                            return;
                                        }
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
                        if (AudioPlayUtil.isPlay()) {
                            AudioPlayUtil.stopAudioPlay();
                        }
                        //如果是我自己，则跳我的朋友圈，其他人跳好友朋友圈
                        if (messageInfoBean.getUid() == UserAction.getMyInfo().getUid().longValue()) {
                            intent = new Intent(getContext(), MyTrendsActivity.class);
                            startActivity(intent);
                        } else {
                            if (!TextUtils.isEmpty(messageInfoBean.getNickname()) || !TextUtils.isEmpty(messageInfoBean.getAvatar())) {
                                intent = new Intent(getContext(), FriendTrendsActivity.class);
                                intent.putExtra("uid", messageInfoBean.getUid());
                                intent.putExtra(FriendTrendsActivity.POSITION, position);
                                startActivity(intent);
                            } else {
                                ToastUtil.show("该用户已注销");
                            }
                        }
                        break;
                }
            }
        });
        bindingView.recyclerRecommend.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
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

        bindingView.recyclerRecommend.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        postcard.withString(CircleDetailsActivity.FROM, "RecommendFragment");//来自广场推荐列表
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
            currentMessage = mFlowAdapter.getData().get(position).getData();
            if (currentMessage != null && currentMessage.getType() != null &&
                    (currentMessage.getType() == PictureEnum.EContentType.VOICE || currentMessage.getType() == PictureEnum.EContentType.VOICE_AND_VOTE)) {
                playVoice();
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

    @Override
    public void onSuccess(List<MessageFlowItemBean> list, int serviceType) {
        //serviceType 0 下拉刷新  1 加载更多   -1 源数据过滤
        isRefreshing = false;
        if (serviceType == 0 || serviceType == -1) {
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
                    //若第一项已经为我刚发的动态，则无需再次置顶
                    if (infoBean.getId().longValue() == ((MessageInfoBean) list.get(0).getData()).getId()) {
                    } else {
                        //添加到头部
                        infoBean.setRefreshCount(infoBean.getRefreshCount() + 1);
                        spUtil.putSPValue(REFRESH_COUNT, new Gson().toJson(infoBean));
                        MessageFlowItemBean flowItemBean = mPresenter.createFlowItemBean(infoBean);
                        mFollowList.add(flowItemBean);
                    }
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
                notifyChangeAll();
            }

            if (list == null || list.size() == 0) {
                bindingView.srlFollow.finishLoadMoreWithNoMoreData();
            } else if (list.size() > 0 && list.size() < PAGE_SIZE) {
                bindingView.srlFollow.finishLoadMoreWithNoMoreData();
            } else {
                if (serviceType == 0) {
                    List<MessageFlowItemBean> tempList = list;
                    // 易路 时间排序取最大的id
                    Collections.sort(tempList, new Comparator<MessageFlowItemBean>() {
                        @Override
                        public int compare(MessageFlowItemBean o1, MessageFlowItemBean o2) {
                            //return o1.para - o2.para;  //升序
                            long time = o2.getData().getCreateTime() - o1.getData().getCreateTime();
                            int i = 0;
                            if (time > 0) {
                                i = 1;
                            } else if (time < 0) {
                                i = -1;
                            }
                            return i; //降序
                        }
                    });
                    mCurrentPage = tempList.get(0).getData().getId();
                } else {
                    mCurrentPage = ((MessageInfoBean) list.get(list.size() - 1).getData()).getId();
                }
                bindingView.srlFollow.setEnableLoadMore(true);
                bindingView.srlFollow.finishLoadMore();
            }

            if (serviceType == 0 || serviceType == -1) {
                if (firstItemPosition >= 0) {
                    scrollListView(firstItemPosition, firstOffset);
                } else {
                    scrollToPosition(0);
                }
            }
        }
        mPresenter.mModel.setData(mFollowList);
        bindingView.srlFollow.finishRefresh();
    }

    private void scrollToPosition(int position) {
        bindingView.recyclerRecommend.scrollToPosition(position);
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
    public void onDeleteItem(int position) {
        mFollowList.remove(position);
        mFlowAdapter.notifyDataSetChanged();
    }

    public void scrollListView(int position, int offset) {
        if (position >= 0) {
            ((LinearLayoutManager) bindingView.recyclerRecommend.getLayoutManager()).scrollToPositionWithOffset(position, offset);
        } else {
            scrollToPosition(0);

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
        //如果这条点赞的动态为我刚发布的，需要及时更改缓存的数据，避免刷新一下，我是否点赞状态没有被更改
        SpUtil spUtil = SpUtil.getSpUtil();
        String value = spUtil.getSPValue(REFRESH_COUNT, "");
        if (!TextUtils.isEmpty(value)) {
            MessageInfoBean infoBean = new Gson().fromJson(value, MessageInfoBean.class);
            if (messageInfoBean.getId().longValue() == infoBean.getId().longValue()) {
                infoBean.setLike(1);
                infoBean.setLikeCount(infoBean.getLikeCount() + 1);
                spUtil.putSPValue(REFRESH_COUNT, new Gson().toJson(infoBean));
            }
        }
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
                if (currentMessage != null && currentMessage.getId().equals(serverInfoBean.getId())) {
                    MessageInfoBean tempMessage = currentMessage;//保存本地播放数据
                    currentMessage = serverInfoBean;
                    currentMessage.setPlayProgress(tempMessage.getPlayProgress());
                    currentMessage.setPlay(tempMessage.isPlay());
                    LogUtil.getLog().i("语音", "更新current" + currentMessage.getId());
                    currentMessage.setAvatar(tempMessage.getAvatar());
                    currentMessage.setNickname(tempMessage.getNickname());
                    mFlowAdapter.getData().get(position).setData(currentMessage);
                    if (mFlowAdapter.getHeaderLayoutCount() > 0) {
                        position = position + 1;
                    }
                    mFlowAdapter.notifyItemChanged(position);
                } else {
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

    @Override
    public void notifyShow() {
        if (mFlowAdapter == null || mFlowAdapter.getItemCount() <= 0) {
            if (mPresenter != null) {
                mPresenter.getRecommendMomentList(mCurrentPage, PAGE_SIZE, 0);
            }
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

    public void playVoice() {
        if (currentMessage == null) {
            return;
        }
        if (!TextUtils.isEmpty(currentMessage.getAttachment())) {
            List<AttachmentBean> attachmentBeans = null;
            try {
                attachmentBeans = new Gson().fromJson(currentMessage.getAttachment(),
                        new TypeToken<List<AttachmentBean>>() {
                        }.getType());
            } catch (Exception e) {
                attachmentBeans = new ArrayList<>();
            }
            if (attachmentBeans != null && attachmentBeans.size() > 0) {
                AttachmentBean attachmentBean = attachmentBeans.get(0);
                if (currentMessage.isPlay()) {
                    if (AudioPlayManager.getInstance().isPlay(Uri.parse(attachmentBean.getUrl()))) {
//                        AudioPlayManager.getInstance().stopPlay();
                        AudioPlayUtil.stopAudioPlay();
                    }
                } else {

                    AudioPlayUtil.startAudioPlay(getActivity(), attachmentBean.getUrl(), new IAudioPlayProgressListener() {
                        @Override
                        public void onStart(Uri var1) {
                            isAudioPlaying = true;
                            currentMessage.setPlay(true);
                            currentMessage.setPlayProgress(0);
                            updatePosition(currentMessage);
                        }

                        @Override
                        public void onStop(Uri var1) {
                            isAudioPlaying = false;
                            currentMessage.setPlay(false);
                            updatePosition(currentMessage);

                        }

                        @Override
                        public void onComplete(Uri var1) {
                            isAudioPlaying = false;
                            currentMessage.setPlay(false);
                            currentMessage.setPlayProgress(100);
                            updatePosition(currentMessage);

                        }

                        @Override
                        public void onProgress(int progress) {
                            LogUtil.getLog().i("语音", "播放进度--" + progress);
                            currentMessage.setPlay(true);
                            currentMessage.setPlayProgress(progress);
                            updatePosition(currentMessage);
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
        bindingView.recyclerRecommend.postDelayed(new Runnable() {
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