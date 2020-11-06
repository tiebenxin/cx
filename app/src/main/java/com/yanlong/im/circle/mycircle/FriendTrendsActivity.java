package com.yanlong.im.circle.mycircle;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.hm.cxpay.widget.refresh.EndlessRecyclerOnScrollListener;
import com.luck.picture.lib.audio.AudioPlayManager2;
import com.luck.picture.lib.audio.AudioPlayUtil;
import com.luck.picture.lib.audio.IAudioPlayProgressListener;
import com.luck.picture.lib.entity.AttachmentBean;
import com.luck.picture.lib.event.EventFactory;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.circle.adapter.MyTrendsAdapter;
import com.yanlong.im.circle.bean.CircleTrendsBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.recommend.RecommendModel;
import com.yanlong.im.databinding.ActivityMyCircleBinding;
import com.yanlong.im.interf.IEditModeListenr;
import com.yanlong.im.interf.IPlayVoiceListener;
import com.yanlong.im.interf.IRefreshListenr;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.ComplaintActivity;
import com.yanlong.im.utils.UserUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.inter.IFriendTrendClickListner;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.DialogHelper;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.YLLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.Response;

import static com.yanlong.im.circle.mycircle.MyFollowActivity.DEFAULT_PAGE_SIZE;

/**
 * @类名：别人的动态(别人的朋友圈)
 * @Date：2020/9/25
 * @by zjy
 * @备注：
 */

public class FriendTrendsActivity extends BaseBindActivity<ActivityMyCircleBinding> {


    public static String POSITION = "position";
    private int page = 1;//默认第一页

    private MyCircleAction action;
    private MyTrendsAdapter adapter;
    private List<MessageInfoBean> mList;
    private long friendUid;//别人的uid
    private int isFollow;//是否关注了该用户
    private CommonSelectDialog dialog;
    private CommonSelectDialog editDialog;
    private CommonSelectDialog.Builder builder;
    private int uType;//好友关系

    private boolean openEditMode;//是否处于编辑模式(超级用户专用)

    @Override
    protected int setView() {
        return R.layout.activity_my_circle;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //TODO 上线前此行代码注释
        openEditMode = true;

        action = new MyCircleAction();
        mList = new ArrayList<>();
        bindingView.layoutFollow.setVisibility(View.VISIBLE);
        bindingView.layoutBottom.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 全透明实现
            //getWindow.setStatusBarColor(Color.TRANSPARENT)
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        builder = new CommonSelectDialog.Builder(FriendTrendsActivity.this);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void loadData() {
        friendUid = getIntent().getLongExtra("uid", 0);
        UserInfo userInfo = new UserDao().findUserInfo(friendUid);
        if (userInfo != null && userInfo.getuType() != null) {
            uType = userInfo.getuType().intValue();
        }
        httpGetFriendTrends();
        adapter = new MyTrendsAdapter(FriendTrendsActivity.this, mList, 2, friendUid,openEditMode);
        bindingView.recyclerView.getItemAnimator().setChangeDuration(0);
        bindingView.recyclerView.setAdapter(adapter);
        bindingView.recyclerView.setLayoutManager(new YLLinearLayoutManager(this));

        //加载更多
        bindingView.recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                adapter.setLoadState(adapter.LOADING);
                httpGetFriendTrends();
            }
        });
        //下拉刷新
        bindingView.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                httpGetFriendTrends();
            }
        });
        //播放语音
        adapter.setPlayVoiceListener(new IPlayVoiceListener() {
            @Override
            public void play(MessageInfoBean bean) {
                playVoice(bean);
            }
        });
        //置顶->刷新回调
        adapter.setOnRefreshListenr(new IRefreshListenr() {
            @Override
            public void onRefresh(MessageInfoBean bean) {
                page = 1;
                httpGetFriendTrends();
            }

            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                boolean ifShow = isFollow == 1 ? true : false;
                DialogHelper.getInstance().createFriendTrendDialog(ifShow, FriendTrendsActivity.this, new IFriendTrendClickListner() {

                    @Override
                    public void clickFollow() {
                        showCancleFollowDialog(friendUid);
                    }

                    @Override
                    public void clickReport() {
                        //举报
                        Intent intent = new Intent(FriendTrendsActivity.this, ComplaintActivity.class);
                        intent.putExtra(ComplaintActivity.UID, friendUid + "");
                        intent.putExtra(ComplaintActivity.FROM_WHERE, 0);
                        startActivity(intent);
                    }

                    @Override
                    public void clickCancle() {

                    }
                });
            }
        });
        if(openEditMode){
            adapter.setEditListener(new IEditModeListenr() {
                @Override
                public void onSetNewName() {
                    showEditNameDialog();
                }

                @Override
                public void onSetNewAvatar() {

                }
            });
        }
        bindingView.swipeRefreshLayout.setColorSchemeResources(R.color.c_169BD5);
        bindingView.ivCreateCircle.setVisibility(View.GONE);
        //关注点击事件
        bindingView.layoutFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                    ToastUtil.show(getString(R.string.user_disable_message));
                    return;
                }
                if (bindingView.tvFollow.getText().toString().equals("关注")) {
                    httpToFollow(friendUid);
                } else if (bindingView.tvFollow.getText().toString().equals("私聊")) {
                    startActivity(new Intent(getContext(), ChatActivity.class)
                            .putExtra(ChatActivity.AGM_TOUID, friendUid));
                    finish();
                } else if (bindingView.tvFollow.getText().toString().equals("加好友")) {
                    String sayHi;
                    if (!TextUtils.isEmpty(UserAction.getMyInfo().getName())) {
                        sayHi = "你好，我是" + UserAction.getMyInfo().getName();
                    } else {
                        sayHi = "交个朋友吧~";
                    }
                    new UserAction().friendApply(friendUid, sayHi, null, new CallBack<ReturnBean>() {
                        @Override
                        public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                            if (response.body() == null) {
                                return;
                            }
                            //可能开了验证，不能通过接口返回结果来判断是否已经是好友了
                            if (response.body().isOk()) {
                                ToastUtil.show("好友申请已发送");
                            } else {
                                ToastUtil.show(response.body().getMsg());
                            }
                        }

                        @Override
                        public void onFailure(Call<ReturnBean> call, Throwable t) {
                            super.onFailure(call, t);
                            ToastUtil.show(t.getMessage());
                        }
                    });

                }
            }
        });
        // topbar是自定义的标题栏
        bindingView.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) bindingView.recyclerView.getLayoutManager();
                // 第一个可见Item的位置
                int position = layoutManager.findFirstVisibleItemPosition();
                // 是第一项才去渐变
                if (position == 0) {
                    // 注意此操作如果第一项划出屏幕外,拿到的是空的，所以必须是position是0的时候才能调用
                    View firstView = layoutManager.findViewByPosition(position);
                    // 第一项Item的高度
                    int firstHeight = firstView.getHeight();
                    // 距离顶部的距离，是负数，也就是说-top就是它向上滑动的距离
                    int scrollY = -firstView.getTop();
                    // 要在它滑到二分之一的时候去渐变
                    int changeHeight = firstHeight / 2;
                    // 小于头部高度一半隐藏标题栏
                    if (scrollY <= changeHeight) {
                        bindingView.layoutTop.setVisibility(View.GONE);
                    } else {
                        bindingView.layoutTop.setVisibility(View.VISIBLE);
                        // 设置了一条分割线，渐变的时候分割线先GONE掉，要不不好看
//                        bindingView.layoutTop.getViewGrayLine().setVisibility(View.GONE);
                        // 从高度的一半开始算透明度，也就是说移动到头部Item的中部，透明度从0开始计算
                        float alpha = (float) (scrollY - changeHeight) / changeHeight;
                        bindingView.layoutTop.setAlpha(alpha);
                    }
                    // 其他的时候就设置都可见，透明度是1
                } else {
                    bindingView.layoutTop.setVisibility(View.VISIBLE);
//                    bindingView.layoutTop.getViewGrayLine().setVisibility(View.VISIBLE);
                    bindingView.layoutTop.setAlpha(1);
                }
            }
        });
        bindingView.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bindingView.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ifShow = isFollow == 1 ? true : false;
                DialogHelper.getInstance().createFriendTrendDialog(ifShow, FriendTrendsActivity.this, new IFriendTrendClickListner() {
                    @Override
                    public void clickReport() {
                        //举报
                        Intent intent = new Intent(FriendTrendsActivity.this, ComplaintActivity.class);
                        intent.putExtra(ComplaintActivity.UID, friendUid + "");
                        intent.putExtra(ComplaintActivity.FROM_WHERE, 0);
                        startActivity(intent);
                    }

                    @Override
                    public void clickCancle() {

                    }

                    @Override
                    public void clickFollow() {
                        showCancleFollowDialog(friendUid);
                    }
                });

            }
        });
        bindingView.tvTitle.setVisibility(View.GONE);
    }

    /**
     * 发请求->获取好友的动态(说说主页及列表)
     */
    private void httpGetFriendTrends() {
        action.httpGetFriendTrends(page, DEFAULT_PAGE_SIZE, friendUid, new CallBack<ReturnBean<CircleTrendsBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<CircleTrendsBean>> call, Response<ReturnBean<CircleTrendsBean>> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    //1 有数据
                    if (response.body().getData() != null) {
                        CircleTrendsBean bean = response.body().getData();
                        //动态列表
                        if (bean.getMomentList() != null && bean.getMomentList().size() > 0) {
                            //1-1 加载更多，则分页数据填充到尾部
                            if (page > 1) {
                                adapter.addMoreList(bean.getMomentList());
                                adapter.setLoadState(adapter.LOADING_MORE);
                            } else {
                                //1-2 第一次加载，若超过3个显示加载更多
                                isFollow = bean.getMyFollow();
                                if (isFollow == 0) {
                                    adapter.ifFollow(false);
                                } else {
                                    adapter.ifFollow(true);
                                }
                                showBottomView();
                                mList.clear();
                                mList.addAll(bean.getMomentList());
                                adapter.setTopData(bean);
                                adapter.updateList(mList);
                                if (mList.size() >= EndlessRecyclerOnScrollListener.DEFULT_SIZE_3) {
                                    adapter.setLoadState(adapter.LOADING_MORE);
                                }
                            }
                            page++;
                        } else {
                            //2 无数据
                            //2-1 加载更多，当没有数据的时候，提示已经到底了
                            if (page > 1) {
                                adapter.setLoadState(adapter.LOADING_END);
                            } else {
                                //2-2 第一次加载，没有数据则不显示尾部
                                isFollow = bean.getMyFollow();
                                if (isFollow == 0) {
                                    adapter.ifFollow(false);
                                } else {
                                    adapter.ifFollow(true);
                                }
                                showBottomView();
                                adapter.setLoadState(adapter.LOADING_GONE);
                            }
                        }
                    }
                } else {
                    ToastUtil.show("获取好友动态失败");
                }
                bindingView.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ReturnBean<CircleTrendsBean>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("获取好友动态失败");
                bindingView.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 发请求->关注
     */
    private void httpToFollow(long uid) {
        action.httpToFollow(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    ToastUtil.show("关注成功");
                    isFollow = 1;
                    adapter.ifFollow(true);
                    showBottomView();
                    //关注单个用户，回到关注列表需要及时更新
                    EventFactory.UpdateFollowStateEvent event = new EventFactory.UpdateFollowStateEvent();
                    event.type = 1;
                    event.uid = uid;
                    EventBus.getDefault().post(event);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("关注失败");
            }
        });
    }

    /**
     * 发请求->取消关注
     */
    private void httpCancelFollow(long uid) {
        action.httpCancelFollow(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    ToastUtil.show("取消关注成功");
                    isFollow = 0;
                    adapter.ifFollow(false);
                    showBottomView();
                    //取消关注单个用户，推荐/关注列表都要及时更新
                    EventFactory.UpdateFollowStateEvent event = new EventFactory.UpdateFollowStateEvent();
                    event.type = 0;
                    event.uid = uid;
                    EventBus.getDefault().post(event);
                } else {
                    ToastUtil.show("取消关注失败");
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("取消关注失败");
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateFollowState(EventFactory.UpdateFollowStateEvent event) {
        if (event.uid == friendUid) {
            if (event.type == 0) {
                isFollow = 0;
                adapter.ifFollow(false);
            } else {
                isFollow = 1;
                adapter.ifFollow(true);
            }
            showBottomView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        AudioPlayUtil.stopAudioPlay();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateOneTrend(EventFactory.UpdateOneTrendEvent event) {
        //更新好友单条动态
        if (event.action == 0) {
            MessageInfoBean bean = adapter.getDataList().get(event.position - 1);//去掉头部
            if (bean.getId() != null && bean.getUid() != null) {
                queryById(bean.getId().longValue(), bean.getUid().longValue(), event.position - 1);
            }
        }
    }

    /**
     * 获取单条朋友圈
     *
     * @param momentId  说说ID
     * @param momentUid 说说发布者
     * @param position  位置
     */
    public void queryById(Long momentId, Long momentUid, int position) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("momentId", momentId);
        params.put("momentUid", momentUid);
        new RecommendModel().queryById(params, new CallBack<ReturnBean<MessageInfoBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<MessageInfoBean>> call, Response<ReturnBean<MessageInfoBean>> response) {
                super.onResponse(call, response);
                if (response.body().isOk()) {
                    if (response.body() != null && response.body().getData() != null) {
//                        MessageInfoBean bean = response.body().getData();
                        MessageInfoBean oldBean = adapter.getDataList().get(position);
                        MessageInfoBean bean = response.body().getData();
                        oldBean.setLike(bean.getLike());
                        oldBean.setLikeCount(bean.getLikeCount());
                        oldBean.setCommentCount(bean.getCommentCount());
                        adapter.notifyItemChanged(position + 1);
                    }
                } else {
                    ToastUtil.show("获取动态失败");
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<MessageInfoBean>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("获取动态失败");
            }
        });
    }

    /**
     * 是否取消关注提示弹框
     *
     * @param uid
     */
    private void showCancleFollowDialog(long uid) {
        dialog = builder.setTitle("是否取消关注?")
                .setShowLeftText(true)
                .setRightText("确认")
                .setLeftText("取消")
                .setRightOnClickListener(v -> {
                    httpCancelFollow(uid);
                    dialog.dismiss();
                })
                .setLeftOnClickListener(v ->
                        dialog.dismiss()
                )
                .build();
        dialog.show();
    }

    //展示底部按钮文案
    private void showBottomView() {
        if (uType == ChatEnum.EUserType.FRIEND || uType == ChatEnum.EUserType.BLACK) {
            if (isFollow == 1) {
                bindingView.ivFollow.setImageResource(R.mipmap.ic_chat);
                bindingView.tvFollow.setText("私聊");
            } else {
                bindingView.tvFollow.setText("关注");
                bindingView.ivFollow.setImageResource(R.mipmap.ic_follow);
            }
        } else {
            if (isFollow == 1) {
                bindingView.tvFollow.setText("加好友");
            } else {
                bindingView.tvFollow.setText("关注");
            }
            bindingView.ivFollow.setImageResource(R.mipmap.ic_follow);
        }
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
                    if (AudioPlayManager2.getInstance().isPlay(Uri.parse(attachmentBean.getUrl()))) {
                        AudioPlayUtil.stopAudioPlay();
                    }
                } else {
                    try {
                        if (AudioPlayManager2.getInstance().getPlayingUri() != null) {
                            AudioPlayUtil.completeAudioPlay();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    AudioPlayUtil.startAudioPlay(FriendTrendsActivity.this, attachmentBean.getUrl(), messageInfoBean, new IAudioPlayProgressListener() {
                        @Override
                        public void onStart(Uri var1, Object o) {
                            messageInfoBean.setPlay(true);
                            messageInfoBean.setPlayProgress(0);
                            updatePosition(messageInfoBean);
                        }

                        @Override
                        public void onStop(Uri var1, Object o) {
                            messageInfoBean.setPlay(false);
                            updatePosition(messageInfoBean);

                        }

                        @Override
                        public void onComplete(Uri var1, Object o) {
                            messageInfoBean.setPlay(false);
                            messageInfoBean.setPlayProgress(100);
                            updatePosition(messageInfoBean);

                        }

                        @Override
                        public void onProgress(int progress, Object o) {
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
        if (adapter == null || adapter.getDataList() == null|| messageInfoBean == null) {
            return;
        }
        bindingView.recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int position = adapter.getDataList().indexOf(messageInfoBean);
                if (position >= 0) {
                    adapter.getDataList().set(position, messageInfoBean);
                    adapter.notifyItemChanged(position + 1);//头部
                }
            }
        }, 100);
    }

    /**
     * 编辑模式->修改用户名弹框
     */
    private void showEditNameDialog() {
        if (editDialog == null) {
            editDialog = builder.setTitle("修改该用户昵称")
                    .setLeftText("取消")
                    .setRightText("确定")
                    .setType(1)
                    .setLeftOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDialog.dismiss();
                        }
                    })
                    .setRightOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editDialog.dismiss();
                            if(!TextUtils.isEmpty(editDialog.getEditContent())){
//                                taskUserInfoSet(editDialog.getEditContent());
                            }
                        }
                    })
                    .build();
        }
        editDialog.show();
    }

//    private void taskUserInfoSet(final String nickname) {
//        new UserAction().myInfoSet(null, null, nickname, null,robotId, new CallBack<ReturnBean>() {
//            @Override
//            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
//                if (response.body() == null) {
//                    return;
//                }
//                if (response.body().isOk()) {
//                    ToastUtil.show("修改成功");
//                }else {
//                    ToastUtil.show(response.body().getMsg());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ReturnBean> call, Throwable t) {
//                super.onFailure(call, t);
//                ToastUtil.show( t.getMessage());
//            }
//        });
//    }


}
