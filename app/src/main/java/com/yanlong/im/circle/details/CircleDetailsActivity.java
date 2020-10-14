package com.yanlong.im.circle.details;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.audio.AudioPlayUtil;
import com.luck.picture.lib.entity.AttachmentBean;
import com.luck.picture.lib.event.EventFactory;
import com.luck.picture.lib.tools.DoubleUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.yanlong.im.R;
import com.yanlong.im.chat.ui.VideoPlayActivity;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.circle.CircleCommentDialog;
import com.yanlong.im.circle.adapter.CircleFlowAdapter;
import com.yanlong.im.circle.adapter.CommentAdapter;
import com.yanlong.im.circle.bean.CircleCommentBean;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.follow.FollowFragment;
import com.yanlong.im.circle.follow.FollowPresenter;
import com.yanlong.im.circle.follow.FollowView;
import com.yanlong.im.databinding.ActivityCircleDetailsBinding;
import com.yanlong.im.databinding.ViewCircleDetailsBinding;
import com.yanlong.im.interf.ICircleClickListener;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.ui.ComplaintActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.view.DeletPopWindow;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.base.bind.BaseBindMvpActivity;
import net.cb.cb.library.inter.ICircleSetupClick;
import net.cb.cb.library.utils.DialogHelper;
import net.cb.cb.library.utils.ScreenUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.YLLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-07
 * @updateAuthor
 * @updateDate
 * @description 朋友圈 详情
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
@Route(path = CircleDetailsActivity.path)
public class CircleDetailsActivity extends BaseBindMvpActivity<FollowPresenter, ActivityCircleDetailsBinding>
        implements FollowView, ICircleClickListener {
    public static final String path = "/circle/details/CircleDetailsActivity";

    public static final String SOURCE_TYPE = "source_type";
    public static final String ITEM_DATA = "item_data";
    public static final String ITEM_DATA_TYPE = "item_data_type";
    public static final String ITEM_DATA_POSTION = "item_data_postion";

    protected ViewCircleDetailsBinding binding;
    private CircleFlowAdapter mFlowAdapter;
    private List<MessageFlowItemBean> mFollowList;
    private MessageInfoBean mMessageInfoBean;

    private CommentAdapter mCommentTxtAdapter;
    private List<CircleCommentBean.CommentListBean> mCommentList;
    private boolean isFollow;
    private final int PAGE_SIZE = 20;
    private int mCurrentPage = 1, mPostion;
    private CircleCommentDialog mCommentDialog;

    @Override
    protected FollowPresenter createPresenter() {
        return new FollowPresenter(getContext());
    }

    @Override
    protected int setView() {
        return R.layout.activity_circle_details;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        AudioPlayUtil.stopAudioPlay();
        isFollow = getIntent().getBooleanExtra(SOURCE_TYPE, false);
        mPostion = getIntent().getIntExtra(ITEM_DATA_POSTION, 0);
        String dataJson = getIntent().getStringExtra(ITEM_DATA);
        int itemType = getIntent().getIntExtra(ITEM_DATA_TYPE, 0);
        mFollowList = new ArrayList<>();
        if (!TextUtils.isEmpty(dataJson)) {
            mMessageInfoBean = new Gson().fromJson(dataJson, MessageInfoBean.class);
            MessageFlowItemBean flowItemBean = new MessageFlowItemBean(itemType, mMessageInfoBean);
            mFollowList.add(flowItemBean);
        }
        mFlowAdapter = new CircleFlowAdapter(mFollowList, isFollow, true, this);

        // 评论列表
        bindingView.recyclerComment.setLayoutManager(new LinearLayoutManager(this));
        bindingView.srlFollow.setRefreshFooter(new ClassicsFooter(this));
        mCommentTxtAdapter = new CommentAdapter(true);
        bindingView.recyclerComment.setAdapter(mCommentTxtAdapter);
        mCommentList = new ArrayList<>();
        mCommentTxtAdapter.setNewData(mCommentList);
    }

    @Override
    public void initEvent() {
        ImageView ivRight = bindingView.headView.getActionbar().getBtnRight();
        ivRight.setImageResource(R.mipmap.ic_circle_more);
        ivRight.setVisibility(View.VISIBLE);
        ivRight.setPadding(ScreenUtil.dip2px(this, 10), 0,
                ScreenUtil.dip2px(this, 10), 0);
        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                DialogHelper.getInstance().createFollowDialog(CircleDetailsActivity.this,
                        (isFollow || mMessageInfoBean.isFollow()) ? "取消关注" : "关注TA", false, new ICircleSetupClick() {
                            @Override
                            public void onClickFollow() {
                                if (isFollow) {
                                    mPresenter.followCancle(mMessageInfoBean.getUid(), 0);
                                } else {
                                    mPresenter.followAdd(mMessageInfoBean.getUid(), 0);
                                }
                            }

                            @Override
                            public void onClickNoLook() {

                            }

                            @Override
                            public void onClickChat(boolean isFriend) {
                                if (isFriend) {
                                    startActivity(new Intent(getContext(), ChatActivity.class)
                                            .putExtra(ChatActivity.AGM_TOUID, mMessageInfoBean.getUid()));
                                } else {
                                    Intent intent = new Intent(getContext(), UserInfoActivity.class);
                                    intent.putExtra(UserInfoActivity.ID, mMessageInfoBean.getUid());
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onClickReport() {
                                gotoComplaintActivity();
                            }
                        });
            }
        });
        mFlowAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (DoubleUtils.isFastDoubleClick()) {
                    return;
                }
                switch (view.getId()) {
                    case R.id.iv_comment:// 评论
                        showCommentDialog("", 0l);
                        break;
                    case R.id.iv_header:// 头像
                        gotoUserInfoActivity(mMessageInfoBean.getUid());
                        break;
                    case R.id.iv_like:// 点赞
                        if (mMessageInfoBean.getLike() == PictureEnum.ELikeType.YES) {
                            mPresenter.comentCancleLike(mMessageInfoBean.getId(), mMessageInfoBean.getUid(), position);
                        } else {
                            mPresenter.comentLike(mMessageInfoBean.getId(), mMessageInfoBean.getUid(), position);
                        }
                        break;
                    case R.id.tv_follow:// 关注TA\取消关注
                        if (isFollow) {
                            cancleFollowDialog(position);
                        } else {
                            if (mMessageInfoBean.isFollow()) {
                                cancleFollowDialog(position);
                            } else {
                                mPresenter.followAdd(mMessageInfoBean.getUid(), position);
                            }
                        }
                        break;
                    case R.id.rl_video:// 播放视频
                        AudioPlayUtil.stopAudioPlay();
                        List<AttachmentBean> attachmentBeans = new Gson().fromJson(mMessageInfoBean.getAttachment(),
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
        mCommentTxtAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                try {
                    if (UserAction.getMyId() != null && mCommentList.get(position).getUid() != null &&
                            UserAction.getMyId().longValue() != mCommentList.get(position).getUid().longValue()) {
                        switch (view.getId()) {
                            case R.id.layout_item:
                                onClick(position, 0, CoreEnum.EClickType.COMMENT_REPLY, view);
                                break;
                            case R.id.iv_header:
                                gotoUserInfoActivity(mCommentList.get(position).getUid());
                                break;
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
        mCommentTxtAdapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                boolean isMe = false;
                if (UserAction.getMyId() != null && mCommentList.get(position).getUid() != null &&
                        UserAction.getMyId().longValue() == mCommentList.get(position).getUid().longValue()) {
                    isMe = true;
                }
                showPop(view, position, isMe);
                return true;
            }
        });
        bindingView.srlFollow.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.circleCommentList(++mCurrentPage, PAGE_SIZE, mMessageInfoBean.getId(), mMessageInfoBean.getUid(),
                        UserAction.getMyId() == mMessageInfoBean.getUid() ? 1 : 0, 0);
            }
        });
    }

    @Override
    protected void loadData() {
        boolean isOpen = getIntent().getBooleanExtra(FollowFragment.IS_OPEN, false);
        if (isOpen) {
            showCommentDialog("", 0l);
        }
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.view_circle_details, null, false);
        binding.recyclerView.setAdapter(mFlowAdapter);
        binding.recyclerView.setLayoutManager(new YLLinearLayoutManager(getContext()));
        mCommentTxtAdapter.addHeaderView(binding.getRoot());

        if (mMessageInfoBean.getCommentCount() != null && mMessageInfoBean.getCommentCount() > 0) {
            binding.tvCommentCount.setVisibility(View.VISIBLE);
            binding.tvCommentCount.setText("所有评论（" + mMessageInfoBean.getCommentCount() + "）");
        }

        mPresenter.circleCommentList(mCurrentPage, PAGE_SIZE, mMessageInfoBean.getId(), mMessageInfoBean.getUid(),
                UserAction.getMyId() == mMessageInfoBean.getUid() ? 1 : 0, 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayUtil.stopAudioPlay();
    }

    private void cancleFollowDialog(int position) {
        AlertYesNo alertYesNo = new AlertYesNo();
        alertYesNo.init(CircleDetailsActivity.this, "提示", "确定取消关注?", "确定", "取消", new AlertYesNo.Event() {
            @Override
            public void onON() {
            }

            @Override
            public void onYes() {
                mPresenter.followCancle(mMessageInfoBean.getUid(), position);
            }
        });
        alertYesNo.show();
    }

    /**
     * 显示评论输入框
     *
     * @param replyName 回复人昵称
     */
    private void showCommentDialog(String replyName, Long replyUid) {
        mCommentDialog = new CircleCommentDialog(CircleDetailsActivity.this, replyName, new CircleCommentDialog.OnMessageListener() {
            @Override
            public void OnMessage(String msg) {
                mPresenter.circleComment(msg, mMessageInfoBean.getId(), mMessageInfoBean.getUid(), replyUid);
            }
        });
        if (mCommentDialog != null && !mCommentDialog.isShowing()) {
            mCommentDialog.show();
        }
    }

    /**
     * 内容展开、收起
     *
     * @param postion
     * @param parentPostion 父类位置
     * @param type          0：展开、收起 1：详情 2文字投票 3图片投票 4评论回复 5点击头像 6 长按
     * @param view
     */
    @Override
    public void onClick(int postion, int parentPostion, int type, View view) {
        if (type == CoreEnum.EClickType.CONTENT_DOWN) {
            MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(postion).getData();
            messageInfoBean.setShowAll(!messageInfoBean.isShowAll());
            mFlowAdapter.notifyItemChanged(postion);
        } else if (type == CoreEnum.EClickType.VOTE_CHAR || type == CoreEnum.EClickType.VOTE_PICTRUE) {
            MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(parentPostion).getData();
            mPresenter.voteAnswer(postion + 1, parentPostion, messageInfoBean.getId(), messageInfoBean.getUid());
        } else if (type == CoreEnum.EClickType.COMMENT_REPLY) {
            showCommentDialog(mCommentList.get(postion).getNickname(), mCommentList.get(postion).getUid());
        }
    }

    private void showPop(View view, int postion, boolean isMe) {
        new DeletPopWindow(this, isMe, new DeletPopWindow.OnClickListener() {
            @Override
            public void onClick(int type) {
                if (type == CoreEnum.ELongType.COPY) {
                    onCopy(mCommentList.get(postion).getContent());
                } else if (type == CoreEnum.ELongType.DELETE) {
                    mPresenter.delComment(mCommentList.get(postion).getId(), mMessageInfoBean.getId(),
                            mMessageInfoBean.getUid(), postion);
                } else if (type == CoreEnum.ELongType.REPORT) {
                    gotoComplaintActivity();
                }
            }
        }).showViewTop(view);
    }

    private void gotoComplaintActivity() {
        Intent intent = new Intent(getContext(), ComplaintActivity.class);
        intent.putExtra(ComplaintActivity.UID, mMessageInfoBean.getUid() + "");
        startActivity(intent);
    }

    /**
     * 复制
     *
     * @param content
     */
    private void onCopy(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText(content, content);
        cm.setPrimaryClip(mClipData);
        ToastUtil.show("复制成功");
    }

    private void gotoUserInfoActivity(Long uid) {
        startActivity(new Intent(getContext(), UserInfoActivity.class)
                .putExtra(UserInfoActivity.ID, uid));
    }

    @Override
    public void onCommentSuccess(boolean isAdd) {
        if (mCommentDialog != null && mCommentDialog.isShowing()) {
            mCommentDialog.dismiss();
        }
        if (isAdd) {
            mMessageInfoBean.setCommentCount(mMessageInfoBean.getCommentCount() + 1);
        } else {
            mMessageInfoBean.setCommentCount(mMessageInfoBean.getCommentCount() - 1);
        }
        if (mMessageInfoBean != null) {
            mCurrentPage = 1;
            mPresenter.circleCommentList(mCurrentPage, PAGE_SIZE, mMessageInfoBean.getId(), mMessageInfoBean.getUid(),
                    UserAction.getMyId() == mMessageInfoBean.getUid() ? 1 : 0, 0);
        }
        binding.tvCommentCount.setText("所有评论（" + mMessageInfoBean.getCommentCount() + "）");
        mFlowAdapter.notifyDataSetChanged();
        mFlowAdapter.finishInitialize();
        refreshFollowList();
    }

    @Override
    public void onSuccess(List<MessageFlowItemBean> list) {

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
                mFlowAdapter.notifyItemChanged(position);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onCommentSuccess(CircleCommentBean commentBean) {

        if (mCurrentPage == 1) {
            mCommentList.clear();
        }
        List<CircleCommentBean.CommentListBean> list = commentBean.getCommentList();
        if (mCurrentPage == 1 && (list == null || list.size() == 0)) {
//            View view = View.inflate(this, R.layout.view_follow_no_data, null);
//            TextView textView = view.findViewById(R.id.tv_message);
//            textView.setText("快点给楼主写评论吧");
//            bindingView.srlFollow.post(new Runnable() {
//                @Override
//                public void run() {
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                            bindingView.srlFollow.getHeight());
//                    view.setLayoutParams(layoutParams);
//                    mCommentTxtAdapter.setEmptyView(view);
//                }
//            });
            bindingView.srlFollow.setEnableLoadMore(false);
            bindingView.srlFollow.finishLoadMore();
        } else {
            if (list != null && list.size() > 0) {
                mCommentList.addAll(list);
                mCommentTxtAdapter.notifyDataSetChanged();
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
        }
    }

    @Override
    public void onVoteSuccess(int parentPostion, String msg) {
        refreshFollowList();
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
        mFlowAdapter.notifyItemChanged(position);

        refreshFollowList();
    }

    /**
     * 刷新关注列表 单条
     */
    private void refreshFollowList() {
        if (isFollow) {
            EventFactory.RefreshSignFollowEvent event = new EventFactory.RefreshSignFollowEvent();
            event.postion = mPostion;
            event.id = mMessageInfoBean.getId();
            event.uid = mMessageInfoBean.getUid();
            EventBus.getDefault().post(event);
        } else {
            EventFactory.RefreshSignRecomendEvent event = new EventFactory.RefreshSignRecomendEvent();
            event.postion = mPostion;
            event.id = mMessageInfoBean.getId();
            event.uid = mMessageInfoBean.getUid();
            EventBus.getDefault().post(event);
        }
    }

    @Override
    public void onSuccess(int position, boolean isCancleFollow, String msg) {
        if (isCancleFollow) {
            if (isFollow) {
                EventBus.getDefault().post(new EventFactory.RefreshFollowEvent());
                finish();
            } else {
                mMessageInfoBean.setFollow(false);
                mFlowAdapter.notifyItemChanged(position);
                EventBus.getDefault().post(new EventFactory.RefreshRecomendEvent());
            }
        } else {
            mMessageInfoBean.setFollow(true);
            mFlowAdapter.notifyItemChanged(position);
            EventBus.getDefault().post(new EventFactory.RefreshRecomendEvent());
        }
    }

    @Override
    public void onShowMessage(String msg) {
        ToastUtil.show(msg);
    }
}