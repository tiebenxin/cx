package com.yanlong.im.circle.follow;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.audio.AudioPlayUtil;
import com.luck.picture.lib.entity.AttachmentBean;
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
import com.yanlong.im.circle.mycircle.MyInteractActivity;
import com.yanlong.im.databinding.FragmentFollowBinding;
import com.yanlong.im.databinding.ViewNewCircleMessageBinding;
import com.yanlong.im.interf.ICircleClickListener;
import com.yanlong.im.user.ui.ComplaintActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.base.bind.BaseBindMvpFragment;
import net.cb.cb.library.inter.ICircleSetupClick;
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
                if (DoubleUtils.isFastDoubleClick()) {
                    return;
                }
                MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(position).getData();
                switch (view.getId()) {
                    case R.id.iv_comment:// 评论
                        gotoCircleDetailsActivity(true, position);
                        break;
                    case R.id.iv_header:// 头像
                        startActivity(new Intent(getContext(), UserInfoActivity.class)
                                .putExtra(UserInfoActivity.ID, messageInfoBean.getUid()));
                        break;
                    case R.id.iv_like:// 点赞
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

                        Intent intent = new Intent(getContext(), VideoPlayActivity.class);
                        if (attachmentBeans.size() > 0) {
                            intent.putExtra("videopath", attachmentBeans.get(0).getUrl());
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        break;
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

    private void gotoCircleDetailsActivity(boolean isOpen, int position) {
        Postcard postcard = ARouter.getInstance().build(CircleDetailsActivity.path);
        postcard.withBoolean(IS_OPEN, isOpen);
        postcard.withBoolean(CircleDetailsActivity.SOURCE_TYPE, true);
        MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(position).getData();
        postcard.withInt(CircleDetailsActivity.ITEM_DATA_POSTION, position);
        postcard.withString(CircleDetailsActivity.ITEM_DATA, new Gson().toJson(messageInfoBean));
        postcard.withInt(CircleDetailsActivity.ITEM_DATA_TYPE, mFlowAdapter.getData().get(position).getItemType());
        postcard.navigation();
    }

    @Override
    public void onSuccess(List<MessageFlowItemBean> list) {
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
                bindingView.recyclerFollow.smoothScrollToPosition(0);
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
    public void onSuccess(int postion, boolean isCancleFollow, String msg) {
        mCurrentPage = 1;
        mPresenter.getFollowMomentList(mCurrentPage, PAGE_SIZE);
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
}