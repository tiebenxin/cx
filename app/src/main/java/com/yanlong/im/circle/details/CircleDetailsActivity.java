package com.yanlong.im.circle.details;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.audio.AudioPlayUtil;
import com.luck.picture.lib.audio.IAudioPlayProgressListener;
import com.luck.picture.lib.circle.CreateCircleActivity;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.AttachmentBean;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.event.EventFactory;
import com.luck.picture.lib.face.FaceViewPager;
import com.luck.picture.lib.face.bean.FaceBean;
import com.luck.picture.lib.utils.InputUtil;
import com.luck.picture.lib.utils.PatternUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.ui.VideoPlayActivity;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.circle.CirclePowerSetupActivity;
import com.yanlong.im.circle.CircleUIHelper;
import com.yanlong.im.circle.adapter.CircleFlowAdapter;
import com.yanlong.im.circle.adapter.CommentAdapter;
import com.yanlong.im.circle.bean.CircleCommentBean;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.bean.NewTrendDetailsBean;
import com.yanlong.im.circle.follow.FollowFragment;
import com.yanlong.im.circle.follow.FollowPresenter;
import com.yanlong.im.circle.follow.FollowView;
import com.yanlong.im.circle.mycircle.MyCircleAction;
import com.yanlong.im.circle.mycircle.FriendTrendsActivity;
import com.yanlong.im.circle.mycircle.MyTrendsActivity;
import com.yanlong.im.circle.recommend.RecommendFragment;
import com.yanlong.im.databinding.ActivityCircleDetails2Binding;
import com.yanlong.im.databinding.ViewCircleDetailsBinding;
import com.yanlong.im.databinding.ViewNoCommentsBinding;
import com.yanlong.im.interf.ICircleClickListener;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.ComplaintActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.UserUtil;
import com.yanlong.im.utils.audio.AudioPlayManager;
import com.yanlong.im.view.DeletPopWindow;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.base.bind.BaseBindMvpActivity;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.inter.ICircleSetupClick;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.DialogHelper;
import net.cb.cb.library.utils.GsonUtils;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SoftKeyBoardListener;
import net.cb.cb.library.utils.SpUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.YLLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.luck.picture.lib.config.PictureConfig.FROM_CIRCLE;

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
public class CircleDetailsActivity extends BaseBindMvpActivity<FollowPresenter, ActivityCircleDetails2Binding>
        implements FollowView, ICircleClickListener, View.OnClickListener {
    public static final String path = "/circle/details/CircleDetailsActivity";

    private static final int REQUEST_CODE_POWER = 200;
    public static final String SOURCE_TYPE = "source_type";
    public static final String ITEM_DATA = "item_data";
    public static final String ITEM_DATA_TYPE = "item_data_type";
    public static final String ITEM_DATA_POSTION = "item_data_postion";
    public static final String MOMENT_ID = "moment_id";
    public static final String VISIBLE = "visible";
    public static final String FROM = "FROM";
    public static final String TREND_POSITION = "TREND_POSITION";

    protected ViewCircleDetailsBinding binding;
    private CircleFlowAdapter mFlowAdapter;
    private List<MessageFlowItemBean> mFollowList;
    private MessageInfoBean mMessageInfoBean;

    private CommentAdapter mCommentTxtAdapter;
    private List<CircleCommentBean.CommentListBean> mCommentList;
    private boolean isFollow;
    private final int PAGE_SIZE = 20;
    private int mCurrentPage = 1, mPostion;
    private ViewNoCommentsBinding bindEmpty;
    private boolean isShowSoft = false;
    private int mKeyboardHeight = 0;// 记录软键盘的高度
    private Long replyUid;
    private String fromWhere;//来自哪里
    private NewTrendDetailsBean newTrendDetailsBean;//互动消息进详情新接口，第一页评论已经携带过来了
    private CircleDetailViewModel mViewModel = new CircleDetailViewModel();
    private boolean isFirst = true;
    private int TrendPosition;//我的动态、好友动态主页位置
    private CommonSelectDialog dialog;
    private CommonSelectDialog.Builder builder;
    private boolean isAudioPlaying;

    @Override
    protected FollowPresenter createPresenter() {
        return new FollowPresenter(this);
    }

    @Override
    protected int setView() {
        return R.layout.activity_circle_details2;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        AudioPlayUtil.stopAudioPlay();
        isFollow = getIntent().getBooleanExtra(SOURCE_TYPE, false);
        mPostion = getIntent().getIntExtra(ITEM_DATA_POSTION, 0);
        TrendPosition = getIntent().getIntExtra(TREND_POSITION, 0);
        String dataJson = getIntent().getStringExtra(ITEM_DATA);
        fromWhere = getIntent().getStringExtra(FROM);
        int itemType = getIntent().getIntExtra(ITEM_DATA_TYPE, 0);
        mKeyboardHeight = getSharedPreferences("keyboard_setting", Context.MODE_PRIVATE).getInt("keyboard_setting", 0);
        initObserver();
        mFollowList = new ArrayList<>();
        if (!TextUtils.isEmpty(dataJson)) {
            //若来自我的互动，解析成另一个新详情结构数据
            if (!TextUtils.isEmpty(fromWhere) && fromWhere.equals("MyInteract")) {
                newTrendDetailsBean = new Gson().fromJson(dataJson, NewTrendDetailsBean.class);
                mMessageInfoBean = newTrendDetailsBean.getOtherMomentVo();
                //如果查询详情为我自己，需要自行拼凑昵称头像，后端沟通后不返回
                if (newTrendDetailsBean.getOtherMomentVo().getUid() == UserAction.getMyInfo().getUid().longValue()) {
                    mMessageInfoBean.setNickname(UserAction.getMyInfo().getName());
                    mMessageInfoBean.setAvatar(UserAction.getMyInfo().getHead());
                }
            } else {
                mMessageInfoBean = new Gson().fromJson(dataJson, MessageInfoBean.class);
                if (mMessageInfoBean != null) {
                    UserInfo userInfo = new UserDao().findUserInfo(mMessageInfoBean.getUid());
                    if (userInfo != null && userInfo.getuType() != null) {
                        mMessageInfoBean.setUserType(userInfo.getuType());
                    }
                }
            }
            MessageFlowItemBean flowItemBean = new MessageFlowItemBean(itemType, mMessageInfoBean);
            mFollowList.add(flowItemBean);
        }
        mFlowAdapter = new CircleFlowAdapter(mFollowList, isFollow, true, this);

        // 评论列表
        bindingView.recyclerComment.setLayoutManager(new LinearLayoutManager(this));
        bindingView.srlFollow.setRefreshFooter(new ClassicsFooter(this));
        ((DefaultItemAnimator) bindingView.recyclerComment.getItemAnimator()).setSupportsChangeAnimations(false);
        mCommentTxtAdapter = new CommentAdapter(true);
        bindingView.recyclerComment.setAdapter(mCommentTxtAdapter);
        mCommentList = new ArrayList<>();
        mCommentTxtAdapter.setNewData(mCommentList);
        mViewModel.init();
        builder = new CommonSelectDialog.Builder(CircleDetailsActivity.this);
    }

    private void initObserver() {
        mViewModel.isInputText.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean value) {
                showOrHideInput(value, mViewModel.isOpenEmoj.getValue());
                if (value) {//打开
                    bindingView.etMessage.requestFocus();
                    InputUtil.showKeyboard(bindingView.etMessage);
                } else {//关闭
                    //清除焦点
                    bindingView.etMessage.clearFocus();
                    if (!TextUtils.isEmpty(bindingView.etMessage.getText())) {
                        bindingView.etMessage.setHint("发表评论");
                    }
                    // 关闭软键盘
                    net.cb.cb.library.utils.InputUtil.hideKeyboard(bindingView.etMessage);
                }
            }
        });

        mViewModel.isOpenEmoj.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean value) {
                showOrHideInput(mViewModel.isInputText.getValue(), value);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initEvent() {
//        ImageView ivRight = bindingView.headView.getActionbar().getBtnRight();
//        ivRight.setImageResource(R.mipmap.ic_circle_more);
//        ivRight.setVisibility(View.VISIBLE);
//        ivRight.setPadding(ScreenUtil.dip2px(this, 10), 0,
//                ScreenUtil.dip2px(this, 10), 0);
        bindingView.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bindingView.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFunctionDialog();
            }
        });
//        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
//            @Override
//            public void onBack() {
//                onBackPressed();
//            }
//
//            @Override
//            public void onRight() {
//                showFunctionDialog();
//            }
//        });
        mFlowAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//                if (DoubleUtils.isFastDoubleClick()) {
//                    return;
//                }
                switch (view.getId()) {
                    case R.id.iv_comment:// 评论
                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                            ToastUtil.show(getString(R.string.user_disable_message));
                            return;
                        }
                        inputComment("", 0l, true);
                        break;
                    case R.id.iv_header:// 头像
                        AudioPlayUtil.stopAudioPlay();
                        //如果是我自己，则跳朋友圈，其他人跳详细资料
                        if (mMessageInfoBean.getUid() == UserAction.getMyInfo().getUid().longValue()) {
                            Intent intent = new Intent(CircleDetailsActivity.this, MyTrendsActivity.class);
                            startActivity(intent);
                        } else {
                            gotoUserInfoActivity(mMessageInfoBean.getUid());
                        }
                        break;
                    case R.id.iv_like:// 点赞
                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                            ToastUtil.show(getString(R.string.user_disable_message));
                            return;
                        }
                        if (mMessageInfoBean.getLike() == PictureEnum.ELikeType.YES) {
                            mPresenter.comentCancleLike(mMessageInfoBean.getId(), mMessageInfoBean.getUid(), position);
                        } else {
                            mPresenter.comentLike(mMessageInfoBean.getId(), mMessageInfoBean.getUid(), position);
                        }
                        break;
//                    case R.id.tv_follow:// 关注TA\取消关注
//                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
//                            ToastUtil.show(getString(R.string.user_disable_message));
//                            return;
//                        }
//                        if (isFollow) {
//                            cancelFollowDialog(position);
//                        } else {
//                            if (mMessageInfoBean.isFollow()) {
//                                cancelFollowDialog(position);
//                            } else {
//                                mPresenter.followAdd(mMessageInfoBean.getUid(), position);
//                            }
//                        }
//                        break;
                    case R.id.rl_video:// 播放视频
                        AudioPlayUtil.stopAudioPlay();
                        List<AttachmentBean> attachmentBeans = new Gson().fromJson(mMessageInfoBean.getAttachment(),
                                new TypeToken<List<AttachmentBean>>() {
                                }.getType());
                        if (mMessageInfoBean.getType() != null && mMessageInfoBean.getType() == PictureEnum.EContentType.PICTRUE) {
                            toPicturePreview(0, attachmentBeans);
                        } else {
                            Intent intent = new Intent(getContext(), VideoPlayActivity.class);
                            if (attachmentBeans.size() > 0) {
                                intent.putExtra("json", GsonUtils.optObject(attachmentBeans.get(0)));
                                intent.putExtra("videopath", attachmentBeans.get(0).getUrl());
                                intent.putExtra("bg_url", attachmentBeans.get(0).getBgUrl());
                            }
                            intent.putExtra("from", PictureConfig.FROM_CIRCLE);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                        }
                        break;
                    case R.id.tv_user_name:// 昵称，没注销的用户才允许跳朋友圈
                        if (!TextUtils.isEmpty(mMessageInfoBean.getNickname()) || !TextUtils.isEmpty(mMessageInfoBean.getAvatar())) {
                            Intent intent = new Intent(CircleDetailsActivity.this, FriendTrendsActivity.class);
                            intent.putExtra("uid", mMessageInfoBean.getUid());
                            intent.putExtra(FriendTrendsActivity.POSITION, position);
                            startActivity(intent);
                        } else {
                            ToastUtil.show("该用户已注销");
                        }
                        break;
                    case R.id.iv_sign_picture:// 单张图片
                        List<AttachmentBean> attachments = new Gson().fromJson(mMessageInfoBean.getAttachment(),
                                new TypeToken<List<AttachmentBean>>() {
                                }.getType());
                        if (mMessageInfoBean.getType() != null && mMessageInfoBean.getType() == PictureEnum.EContentType.PICTRUE) {
                            toPicturePreview(0, attachments);
                            checkAudioStatus(false);
                        }
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
                            case R.id.iv_like:
                                if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                    ToastUtil.show(getString(R.string.user_disable_message));
                                    return;
                                }
                                CircleCommentBean.CommentListBean bean = mCommentList.get(position);
                                int like;
                                if (bean.getLike() == 0) {
                                    like = 1;//赞
                                } else {
                                    like = 0;//取消赞
                                }
                                new MyCircleAction().httpCommentLike(bean.getId(), like, mMessageInfoBean.getId(), mMessageInfoBean.getUid(), new CallBack<ReturnBean>() {
                                    @Override
                                    public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                                        super.onResponse(call, response);
                                        if (like == 1) {
                                            mCommentList.get(position).setLike(1);
                                            mCommentList.get(position).setLikeCount(mCommentList.get(position).getLikeCount() + 1);
                                        } else {
                                            mCommentList.get(position).setLike(0);
                                            mCommentList.get(position).setLikeCount(mCommentList.get(position).getLikeCount() - 1);
                                        }
                                        mCommentTxtAdapter.notifyItemChanged(position + 1);
                                    }

                                    @Override
                                    public void onFailure(Call<ReturnBean> call, Throwable t) {
                                        super.onFailure(call, t);
                                        if (like == 1) {
                                            ToastUtil.show("点赞失败");
                                        } else {
                                            ToastUtil.show("取消点赞失败");
                                        }
                                    }
                                });
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
                        UserAction.getMyId() == mMessageInfoBean.getUid() ? 1 : 0, 0, mPostion, fromWhere);
            }
        });

        bindingView.recyclerComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mViewModel.isInputText.setValue(false);
                return false;
            }
        });


        //输入框
        bindingView.etMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!mViewModel.isInputText.getValue())
                    mViewModel.isInputText.setValue(true);
                return false;
            }
        });
        bindingView.ivEmj.setOnClickListener(this);
        bindingView.tvSend.setOnClickListener(this);
        SoftKeyBoardListener kbLinst = new SoftKeyBoardListener(this);
        kbLinst.setOnSoftKeyBoardChangeListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int h) {
                isFirst = false;
                isShowSoft = true;
                mKeyboardHeight = h;
                if (mViewModel.isOpenEmoj != null && mViewModel.isOpenEmoj.getValue()) {
                    mViewModel.isOpenEmoj.setValue(false);
                }
            }

            @Override
            public void keyBoardHide(int h) {
                isShowSoft = false;
                if (TextUtils.isEmpty(bindingView.etMessage.getText())) {
//                    bindingView.etMessage.setText("");
                    bindingView.etMessage.setHint("发表评论");
                }
                mViewModel.isInputText.setValue(false);
//                hideKeyboard();
            }
        });

        // 表情点击事件
        bindingView.viewFaceview.setOnItemClickListener(new FaceViewPager.FaceClickListener() {

            @Override
            public void OnItemClick(FaceBean bean) {
                if ((bindingView.etMessage.getText().toString().length() + bean.getName().length()) < 150) {
                    bindingView.etMessage.addEmojSpan(bean.getName());
                }
            }
        });
        // 删除表情按钮
        bindingView.viewFaceview.setOnDeleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int selection = bindingView.etMessage.getSelectionStart();
                    String msg = bindingView.etMessage.getText().toString().trim();
                    if (selection >= 1) {
                        if (selection >= PatternUtil.FACE_EMOJI_LENGTH) {
                            String emoji = msg.substring(selection - PatternUtil.FACE_EMOJI_LENGTH, selection);
                            if (PatternUtil.isExpression(emoji)) {
                                bindingView.etMessage.getText().delete(selection - PatternUtil.FACE_EMOJI_LENGTH, selection);
                                return;
                            }
                        }
                        bindingView.etMessage.getText().delete(selection - 1, selection);
                    }
                } catch (Exception e) {
                }
            }
        });

//        bindingView.srlFollow.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                //如果bottom小于oldBottom,说明键盘是弹起。
//                if (bottom < oldBottom && oldBottom - bottom == mKeyboardHeight) {
//                    //滑动到底部
//                } else if (bottom > oldBottom && bottom - oldBottom == mKeyboardHeight) {//软键盘关闭，键盘右上角
//                    mViewModel.isInputText.setValue(false);
//                }
//            }
//        });
    }

    private void showFunctionDialog() {
        String type;
        if (isMe()) {
            type = "";
        } else {
            type = (isFollow || mMessageInfoBean.isFollow()) ? "取消关注" : "关注TA";
        }
        DialogHelper.getInstance().createFollowDialog(CircleDetailsActivity.this,
                type, mPresenter.getUserType(mMessageInfoBean.getUid()) == 0 ? true : false,
                new ICircleSetupClick() {
                    @Override
                    public void onClickFollow() {
                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                            ToastUtil.show(getString(R.string.user_disable_message));
                            return;
                        }
                        if (isMe()) {
                            Postcard postcard = ARouter.getInstance().build(CirclePowerSetupActivity.path);
                            postcard.withInt(VISIBLE, mMessageInfoBean.getVisibility());
                            postcard.withLong(MOMENT_ID, mMessageInfoBean.getId());
                            postcard.navigation(CircleDetailsActivity.this, REQUEST_CODE_POWER);
                        } else {
                            if (isFollow || mMessageInfoBean.isFollow()) {
                                showCancleFollowDialog(mMessageInfoBean.getUid(), 0);
                            } else {
                                mPresenter.followAdd(mMessageInfoBean.getUid(), 0);
                            }
                        }
                    }

                    @Override
                    public void onClickNoLook(boolean isDel) {
                        if (isDel) {
                            mPresenter.circleDelete(mMessageInfoBean.getId(), mPostion, fromWhere);
                        } else {
                            EventFactory.NoSeeEvent event = new EventFactory.NoSeeEvent();
                            event.uid = mMessageInfoBean.getUid();
                            EventBus.getDefault().post(event);
                        }
                        finish();
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
                        gotoComplaintActivity(0, 0, 0);
                    }
                });
    }

    @Override
    protected void loadData() {
        boolean isOpen = getIntent().getBooleanExtra(FollowFragment.IS_OPEN, false);
        if (isOpen) {
            inputComment("", 0l, true);
        } else {
            inputComment("", 0l, false);

        }
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.view_circle_details, null, false);
        binding.recyclerView.setAdapter(mFlowAdapter);
        binding.recyclerView.setLayoutManager(new YLLinearLayoutManager(getContext()));
        ((DefaultItemAnimator) binding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mCommentTxtAdapter.addHeaderView(binding.getRoot());

        bindEmpty = DataBindingUtil.inflate(getLayoutInflater(), R.layout.view_no_comments, null, false);

        if (mMessageInfoBean.getCommentCount() != null && mMessageInfoBean.getCommentCount() > 0) {
            binding.tvCommentCount.setVisibility(View.VISIBLE);
            binding.tvCommentCount.setText("所有评论（" + mMessageInfoBean.getCommentCount() + "）");
            showFooterView(false);
        } else {
            binding.tvCommentCount.setVisibility(View.VISIBLE);
            binding.tvCommentCount.setText("所有评论");
            showFooterView(true);
        }
        if (!TextUtils.isEmpty(fromWhere) && fromWhere.equals("MyInteract")) { //若来自我的互动无需再次请求评论列表，已经包含第一页评论的内容
            if (mCurrentPage == 1) {
                mCommentList.clear();
            }
            List<CircleCommentBean.CommentListBean> list = newTrendDetailsBean.getCommentVoList();
            if (mCurrentPage == 1 && (list == null || list.size() == 0)) {
                bindingView.srlFollow.setEnableLoadMore(false);
                bindingView.srlFollow.finishLoadMore();
            } else {
                if (list != null && list.size() > 0) {
                    mCommentList.addAll(list);
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
                showFooterView(false);
            }
            binding.tvCommentCount.setVisibility(View.VISIBLE);
            mCommentTxtAdapter.notifyDataSetChanged();
        } else {
            mPresenter.circleCommentList(mCurrentPage, PAGE_SIZE, mMessageInfoBean.getId(), mMessageInfoBean.getUid(),
                    UserAction.getMyId() == mMessageInfoBean.getUid() ? 1 : 0, 1, mPostion, fromWhere);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayUtil.stopAudioPlay();
    }

    private boolean isMe() {
        if (UserAction.getMyId() != null
                && mMessageInfoBean.getUid() != null &&
                UserAction.getMyId().longValue() == mMessageInfoBean.getUid().longValue()) {
            return true;
        } else {
            return false;
        }
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
        PictureSelector.create(this)
                .themeStyle(R.style.picture_default_style)
                .isGif(true)
                .openExternalPreview1(position, selectList, "", 0L, FROM_CIRCLE, "");
    }

    private void cancelFollowDialog(int position) {
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

    private void inputComment(String replyName, Long replyUid, boolean showSoftKey) {
        this.replyUid = replyUid;
        //初始化输入框
        if (!TextUtils.isEmpty(replyName)) {
            bindingView.etMessage.setHint("回复" + replyName + ":");
        } else {
            bindingView.etMessage.setHint("发表评论");
        }
        if (showSoftKey) {
            setSoftKeyboard();
            mViewModel.isInputText.setValue(true);
//            mViewModel.isOpenEmoj.setValue(false);
        }
    }

    /**
     * 内容展开、收起
     *
     * @param position
     * @param parentPosition 父类位置
     * @param type           0：展开、收起 1：详情 2文字投票 3图片投票 4评论回复 5点击头像 6 长按
     * @param view
     */
    @Override
    public void onClick(int position, int parentPosition, int type, View view) {
        if (type == CoreEnum.EClickType.CONTENT_DOWN) {
            MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(position).getData();
            messageInfoBean.setShowAll(!messageInfoBean.isShowAll());
            mFlowAdapter.notifyItemChanged(position);
        } else if (type == CoreEnum.EClickType.VOTE_CHAR || type == CoreEnum.EClickType.VOTE_PICTURE) {
            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                ToastUtil.show(getString(R.string.user_disable_message));
                return;
            }
            MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(parentPosition).getData();
            mPresenter.voteAnswer(position + 1, parentPosition, messageInfoBean.getId(), messageInfoBean.getUid());
        } else if (type == CoreEnum.EClickType.COMMENT_REPLY) {
            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                ToastUtil.show(getString(R.string.user_disable_message));
                return;
            }
            inputComment(mCommentList.get(position).getNickname(), mCommentList.get(position).getUid(), true);
        } else if (type == CoreEnum.EClickType.FOLLOW) {
            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                ToastUtil.show(getString(R.string.user_disable_message));
                return;
            }
            mPresenter.followAdd(mMessageInfoBean.getUid(), position);
        } else if (type == CoreEnum.EClickType.ADD_FRIEND) {
            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                ToastUtil.show(getString(R.string.user_disable_message));
                return;
            }
            if (!TextUtils.isEmpty(UserAction.getMyInfo().getName())) {
                mPresenter.addFriend(mMessageInfoBean.getUid(), "你好，我是" + UserAction.getMyInfo().getName());
            } else {
                mPresenter.addFriend(mMessageInfoBean.getUid(), "交个朋友吧~");
            }

        } else if (type == CoreEnum.EClickType.CHAT) {
            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                ToastUtil.show(getString(R.string.user_disable_message));
                return;
            }
            startActivity(new Intent(getContext(), ChatActivity.class)
                    .putExtra(ChatActivity.AGM_TOUID, mMessageInfoBean.getUid()));
            finish();
        } else if (type == CoreEnum.EClickType.CLICK_VOICE) {
            MessageInfoBean messageInfoBean = mFlowAdapter.getData().get(position).getData();
            if (messageInfoBean != null && messageInfoBean.getType() != null && messageInfoBean.getType() == PictureEnum.EContentType.VOICE) {
                playVoice(messageInfoBean);
            }

        }
    }

    private void showPop(View view, int position, boolean isMe) {
        new DeletPopWindow(this, isMe, new DeletPopWindow.OnClickListener() {
            @Override
            public void onClick(int type) {
                if (type == CoreEnum.ELongType.COPY) {
                    onCopy(mCommentList.get(position).getContent());
                } else if (type == CoreEnum.ELongType.DELETE) {
                    if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                        ToastUtil.show(getString(R.string.user_disable_message));
                        return;
                    }
                    mPresenter.delComment(mCommentList.get(position).getId(), mMessageInfoBean.getId(),
                            mMessageInfoBean.getUid(), position);
                } else if (type == CoreEnum.ELongType.REPORT) {
                    gotoComplaintActivity(1, mCommentList.get(position).getId(), mCommentList.get(position).getUid());
                }
            }
        }).showViewTop(view);
    }

    private void gotoComplaintActivity(int type, long commentId, long momentUid) {
        Intent intent = new Intent(getContext(), ComplaintActivity.class);
        intent.putExtra(ComplaintActivity.UID, mMessageInfoBean.getUid() + "");
        if (type == 0) { //普通投诉
            intent.putExtra(ComplaintActivity.FROM_WHERE, 0);
        } else { //广场投诉(含评论)
            intent.putExtra(ComplaintActivity.FROM_WHERE, 1);
            intent.putExtra(ComplaintActivity.COMMENT_ID, commentId);
            intent.putExtra(ComplaintActivity.DEFENDANT_UID, mMessageInfoBean.getUid());
            intent.putExtra(ComplaintActivity.MOMENT_ID, mMessageInfoBean.getId());
            if (momentUid == 0) {
                momentUid = mMessageInfoBean.getUid();
            }
            intent.putExtra(ComplaintActivity.MOMENT_UID, momentUid);
        }
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
        clearEdit();
        int count = 0;
        if (isAdd) {
            mMessageInfoBean.setCommentCount(count = mMessageInfoBean.getCommentCount() + 1);
        } else {
            mMessageInfoBean.setCommentCount(count = mMessageInfoBean.getCommentCount() - 1);
        }
        if (mMessageInfoBean != null) {
            mCurrentPage = 1;
            mPresenter.circleCommentList(mCurrentPage, PAGE_SIZE, mMessageInfoBean.getId(), mMessageInfoBean.getUid(),
                    UserAction.getMyId() == mMessageInfoBean.getUid() ? 1 : 0, 0, mPostion, fromWhere);
        }
        if (count <= 0) {
            binding.tvCommentCount.setText("所有评论");
            showFooterView(true);
        } else {
            binding.tvCommentCount.setText("所有评论（" + mMessageInfoBean.getCommentCount() + "）");
        }
        mFlowAdapter.notifyDataSetChanged();
        mFlowAdapter.finishInitialize();
        bindingView.tvSend.setEnabled(true);
        bindingView.recyclerComment.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayoutManager) bindingView.recyclerComment.getLayoutManager()).scrollToPositionWithOffset(mCommentTxtAdapter.getItemCount() - 1, Integer.MIN_VALUE);
            }
        }, 100);

        if (!TextUtils.isEmpty(fromWhere) && fromWhere.equals("MyTrendsActivity") || fromWhere.equals("FriendTrendsActivity")) {
            //通知好友动态主页、我的动态主页刷新
            EventFactory.UpdateOneTrendEvent event = new EventFactory.UpdateOneTrendEvent();
            if (fromWhere.equals("FriendTrendsActivity")) {
                event.action = 0;
            } else if (fromWhere.equals("MyTrendsActivity")) {
                event.action = 1;
            }
            event.position = TrendPosition;
            EventBus.getDefault().post(event);
        } else {
            refreshFollowList();
        }
    }

    private void showFooterView(boolean isShow) {
        if (mCommentTxtAdapter == null || bindEmpty == null) {
            return;
        }
        if (isShow) {
            mCommentTxtAdapter.addFooterView(bindEmpty.getRoot());
        } else {
            mCommentTxtAdapter.removeFooterView(bindEmpty.getRoot());
        }

    }

    @Override
    public void showUnreadMsg(int unCount, String avatar) {

    }

    @Override
    public void onDeleteItem(int position) {

    }

    @Override
    public void onAddFriendSuccess(boolean isSuccess) {
        if (isSuccess) {
            mMessageInfoBean.setUserType(ChatEnum.EUserType.FRIEND);
            mFlowAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.show("好友申请已发送");
        }

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
            bindingView.srlFollow.setEnableLoadMore(false);
            bindingView.srlFollow.finishLoadMore();
        } else {
            if (list != null && list.size() > 0) {
                mCommentList.addAll(list);
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
            showFooterView(false);
        }
        binding.tvCommentCount.setVisibility(View.VISIBLE);
        mCommentTxtAdapter.notifyDataSetChanged();
        //获取浏览量
        MessageInfoBean messageInfoBean = (MessageInfoBean) mFlowAdapter.getData().get(0).getData();
        messageInfoBean.setBrowseCount(commentBean.getBrowseCount());
        mFlowAdapter.notifyItemChanged(0);
        //通知更新广场推荐/关注列表某一项状态
        EventFactory.UpdateOneTrendEvent event1 = new EventFactory.UpdateOneTrendEvent();
        event1.id = messageInfoBean.getId();
        event1.action = 3;
        EventBus.getDefault().post(event1);
    }

    @Override
    public void onVoteSuccess(int parentPosition, String msg) {
        if (!TextUtils.isEmpty(fromWhere) && fromWhere.equals("MyTrendsActivity") || fromWhere.equals("FriendTrendsActivity")) {
            //通知好友动态主页、我的动态主页刷新
            EventFactory.UpdateOneTrendEvent event = new EventFactory.UpdateOneTrendEvent();
            if (fromWhere.equals("FriendTrendsActivity")) {
                event.action = 0;
            } else if (fromWhere.equals("MyTrendsActivity")) {
                event.action = 1;
            }
            event.position = TrendPosition;
            EventBus.getDefault().post(event);
            //通知更新广场推荐/关注列表某一项状态
            EventFactory.UpdateOneTrendEvent event1 = new EventFactory.UpdateOneTrendEvent();
            event1.id = mMessageInfoBean.getId();
            event1.action = 3;
            EventBus.getDefault().post(event1);
        } else {
            refreshFollowList();
        }
        mPresenter.queryById(mMessageInfoBean.getId(), mMessageInfoBean.getUid(), parentPosition);

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
        if (!TextUtils.isEmpty(fromWhere) && fromWhere.equals("MyTrendsActivity") || fromWhere.equals("FriendTrendsActivity")) {
            //通知好友动态主页、我的动态主页刷新
            EventFactory.UpdateOneTrendEvent event = new EventFactory.UpdateOneTrendEvent();
            if (fromWhere.equals("FriendTrendsActivity")) {
                event.action = 0;
            } else if (fromWhere.equals("MyTrendsActivity")) {
                event.action = 1;
            }
            event.position = TrendPosition;
            EventBus.getDefault().post(event);
            //通知更新广场推荐/关注列表某一项状态
            EventFactory.UpdateOneTrendEvent event1 = new EventFactory.UpdateOneTrendEvent();
            event1.id = messageInfoBean.getId();
            event1.action = 3;
            EventBus.getDefault().post(event1);
        } else {
            refreshFollowList();
        }
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
    public void onSuccess(int position, boolean isCancelFollow, String msg) {
        if (isCancelFollow) {
            if (isFollow) {
                EventBus.getDefault().post(new EventFactory.RefreshFollowEvent());
                finish();
            } else {
                mMessageInfoBean.setFollow(false);
                mFlowAdapter.notifyItemChanged(position);
            }
        } else {
            mMessageInfoBean.setFollow(true);
            mFlowAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onShowMessage(String msg) {
        bindingView.tvSend.setEnabled(true);
        ToastUtil.show(msg);
        if (msg.equals("动态被藏起来了")) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_POWER) {
                String visible = data.getStringExtra(CreateCircleActivity.INTENT_POWER);
                if (mMessageInfoBean != null) {
                    mMessageInfoBean.setVisibility(StringUtil.getVisible(visible));
                }
            }
        }
    }

    private void clearEdit() {
        bindingView.etMessage.setText("");
        bindingView.etMessage.setHint("发表评论");
        hideKeyboard();
    }

    private void setSoftKeyboard() {
        bindingView.etMessage.setFocusable(true);
        bindingView.etMessage.setFocusableInTouchMode(true);
        bindingView.etMessage.requestFocus();
        //为 commentEditText 设置监听器，在 DialogFragment 绘制完后立即呼出软键盘，呼出成功后即注销
        bindingView.etMessage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                InputMethodManager inputMethodManager = (InputMethodManager) CircleDetailsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    if (inputMethodManager.showSoftInput(bindingView.etMessage, 0)) {
                        bindingView.etMessage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_send:
                if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                    ToastUtil.show(getString(R.string.user_disable_message));
                    return;
                }
                String msg = bindingView.etMessage.getText().toString().trim();
                if (!TextUtils.isEmpty(msg)) {
                    bindingView.tvSend.setEnabled(false);
                    mPresenter.circleComment(msg, mMessageInfoBean.getId(), mMessageInfoBean.getUid(), replyUid);
                } else {
                    ToastUtil.show("请输入评论");
                }
                break;
            case R.id.iv_emj:
                if (isFirst) {
                    mViewModel.isOpenEmoj.setValue(true);
                } else {
                    isShowSoft = !isShowSoft;
                    mViewModel.isInputText.setValue(isShowSoft);
                    mViewModel.isOpenEmoj.setValue(!isShowSoft);
                }
                isFirst = false;
                break;
        }
    }


    private synchronized void showOrHideInput(boolean isSoftShow, boolean isEmojiOpen) {
        LogUtil.getLog().i(getClass().getSimpleName(), "showOrHideInput--isSoftShow=" + isSoftShow + "--isEmojiOpen=" + isEmojiOpen);
        if (isSoftShow || isEmojiOpen) {
            if (isEmojiOpen) {
                InputUtil.hideKeyboard(bindingView.etMessage);
                bindingView.ivEmj.setImageLevel(1);
                bindingView.viewFaceview.setVisibility(View.VISIBLE);
            } else {
                bindingView.ivEmj.setImageLevel(0);
                bindingView.viewFaceview.setVisibility(View.GONE);
                bindingView.etMessage.requestFocus();
                InputUtil.showKeyboard(bindingView.etMessage);
            }
        } else {
            bindingView.viewFaceview.setVisibility(View.GONE);
            InputUtil.hideKeyboard(bindingView.etMessage);
            if (!TextUtils.isEmpty(bindingView.etMessage.getText())) {
                bindingView.etMessage.setHint("发表评论");
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

                    AudioPlayUtil.startAudioPlay(this.getApplicationContext(), attachmentBean.getUrl(), new IAudioPlayProgressListener() {
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
        bindingView.recyclerComment.postDelayed(new Runnable() {
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