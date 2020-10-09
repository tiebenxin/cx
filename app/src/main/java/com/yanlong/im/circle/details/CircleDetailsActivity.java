package com.yanlong.im.circle.details;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.entity.AttachmentBean;
import com.luck.picture.lib.event.EventFactory;
import com.luck.picture.lib.tools.DoubleUtils;
import com.yanlong.im.R;
import com.yanlong.im.chat.ui.VideoPlayActivity;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.circle.CircleCommentDialog;
import com.yanlong.im.circle.adapter.CircleFlowAdapter;
import com.yanlong.im.circle.bean.CircleCommentBean;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.follow.FollowFragment;
import com.yanlong.im.circle.follow.FollowPresenter;
import com.yanlong.im.circle.follow.FollowView;
import com.yanlong.im.databinding.ActivityCircleDetailsBinding;
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
    public static final String IS_ME = "is_me";//是否为自己 若为自己的朋友圈详情无需显示"去关注"按钮
    public static final String ITEM_DATA_POSTION = "item_data_postion";

    private CircleFlowAdapter mFlowAdapter;
    private List<MessageFlowItemBean> mFollowList;
    private MessageInfoBean mMessageInfoBean;
    private boolean isFollow;
    private final int PAGE_SIZE = 10;
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
        mFlowAdapter = new CircleFlowAdapter(mFollowList, isFollow, true, this, null);
        bindingView.recyclerFollow.setAdapter(mFlowAdapter);
        bindingView.recyclerFollow.setLayoutManager(new YLLinearLayoutManager(getContext()));

        mPresenter.circleCommentList(mCurrentPage, PAGE_SIZE, mMessageInfoBean.getId(), mMessageInfoBean.getUid(),
                UserAction.getMyId() == mMessageInfoBean.getUid() ? 1 : 0);
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
                        isFollow ? "取消关注" : "关注TA", false, new ICircleSetupClick() {
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
                        showCommentDialog(false);
                        break;
                    case R.id.iv_header:// 头像
                        gotoUserInfoActivity();
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
                            mPresenter.followCancle(mMessageInfoBean.getUid(), position);
                        } else {
                            mPresenter.followAdd(mMessageInfoBean.getUid(), position);
                        }
                        break;
                    case R.id.rl_video:// 播放视频
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
    }

    @Override
    protected void loadData() {
        boolean isOpen = getIntent().getBooleanExtra(FollowFragment.IS_OPEN, false);
        if (isOpen) {
            showCommentDialog(false);
        }
    }

    /**
     * 显示评论输入框
     *
     * @param isReply 是否是回复
     */
    private void showCommentDialog(boolean isReply) {
        mCommentDialog = new CircleCommentDialog(CircleDetailsActivity.this, new CircleCommentDialog.OnMessageListener() {
            @Override
            public void OnMessage(String msg) {
                mPresenter.circleComment(msg, mMessageInfoBean.getId(), mMessageInfoBean.getUid(), 0l);
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
     * @param type          0：展开、收起 1：详情 2文字投票 3图片投票
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
            showCommentDialog(true);
        } else if (type == CoreEnum.EClickType.COMMENT_HEAD) {
            gotoUserInfoActivity();
        } else if (type == CoreEnum.EClickType.COMMENT_LONG) {
            showPop(view, postion);
        }
    }

    private void showPop(View view, int postion) {
        new DeletPopWindow(this, true, new DeletPopWindow.OnClickListener() {
            @Override
            public void onClick(int type) {
                if (type == CoreEnum.ELongType.COPY) {
                    onCopy(mFlowAdapter.getCommentList().get(postion).getContent());
                } else if (type == CoreEnum.ELongType.DELETE) {
                    mPresenter.delComment(mFlowAdapter.getCommentList().get(postion).getId(), mMessageInfoBean.getId(),
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

    private void gotoUserInfoActivity() {
        startActivity(new Intent(getContext(), UserInfoActivity.class)
                .putExtra(UserInfoActivity.ID, mMessageInfoBean.getUid()));
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
    public void onCommentSuccess(List<CircleCommentBean> list) {
        if (list != null && list.size() > 0) {
            mMessageInfoBean.setCommentCount(list.size());
        } else {
            mMessageInfoBean.setCommentCount(0);
        }
        mFlowAdapter.setCommentList(list);
        mFlowAdapter.notifyDataSetChanged();
        mFlowAdapter.finishInitialize();
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
     * 刷新关注列表
     */
    private void refreshFollowList() {
        EventFactory.RefreshFollowEvent event = new EventFactory.RefreshFollowEvent();
        event.postion = mPostion;
        event.id = mMessageInfoBean.getId();
        event.uid = mMessageInfoBean.getUid();
        EventBus.getDefault().post(event);
    }

    @Override
    public void onSuccess(int position, String msg) {
        if (mCommentDialog != null && mCommentDialog.isShowing()) {
            mCommentDialog.dismiss();
        }
        if (mMessageInfoBean != null) {
            mPresenter.circleCommentList(mCurrentPage, PAGE_SIZE, mMessageInfoBean.getId(), mMessageInfoBean.getUid(),
                    UserAction.getMyId() == mMessageInfoBean.getUid() ? 1 : 0);
        }
        refreshFollowList();
    }

    @Override
    public void onShowMessage(String msg) {
        ToastUtil.show(msg);
    }
}