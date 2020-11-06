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
import com.hm.cxpay.widget.refresh.EndlessRecyclerOnScrollListener;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.audio.AudioPlayManager2;
import com.luck.picture.lib.audio.AudioPlayUtil;
import com.luck.picture.lib.audio.IAudioPlayProgressListener;
import com.luck.picture.lib.circle.CreateCircleActivity;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.AttachmentBean;
import com.luck.picture.lib.event.EventFactory;
import com.luck.picture.lib.rxbus2.RxBus;
import com.yanlong.im.R;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.circle.adapter.MyTrendsAdapter;
import com.yanlong.im.circle.bean.CircleTrendsBean;
import com.yanlong.im.circle.bean.InteractMessage;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.recommend.RecommendModel;
import com.yanlong.im.databinding.ActivityMyCircleBinding;
import com.yanlong.im.interf.IPlayVoiceListener;
import com.yanlong.im.interf.IRefreshListenr;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.UserUtil;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.FileCacheUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.view.YLLinearLayoutManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.Response;

import static com.yanlong.im.circle.mycircle.MyFollowActivity.DEFAULT_PAGE_SIZE;

/**
 * @类名：我的动态(我的朋友圈)
 * @Date：2020/9/25
 * @by zjy
 * @备注：
 */

public class MyTrendsActivity extends BaseBindActivity<ActivityMyCircleBinding> {


    private int page = 1;//默认第一页

    private MyCircleAction action;
    private UpFileAction upFileAction;
    private MyTrendsAdapter adapter;
    private List<MessageInfoBean> mList;
    private MsgDao msgDao;
    private MessageInfoBean currentMessage;
    private String voiceUrl;

    @Override
    protected int setView() {
        return R.layout.activity_my_circle;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        action = new MyCircleAction();
        mList = new ArrayList<>();
        msgDao = new MsgDao();
        bindingView.layoutFollow.setVisibility(View.GONE);
        bindingView.layoutBottom.setVisibility(View.GONE);
        if (!RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().register(this);
        }
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
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void loadData() {
        adapter = new MyTrendsAdapter(MyTrendsActivity.this, mList, 1, 0,false);
        bindingView.recyclerView.setAdapter(adapter);
        bindingView.recyclerView.getItemAnimator().setChangeDuration(0);
        bindingView.recyclerView.setLayoutManager(new YLLinearLayoutManager(this));
        httpGetMyTrends();
        //加载更多
        bindingView.recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                adapter.setLoadState(adapter.LOADING);
                httpGetMyTrends();
            }
        });
        //下拉刷新
        bindingView.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                httpGetMyTrends();
            }
        });
        //置顶->刷新回调
        adapter.setOnRefreshListenr(new IRefreshListenr() {
            @Override
            public void onRefresh(MessageInfoBean bean) {
                //考虑到此动态语音播放途中置顶的情况，拿到置顶语音的url
                if (bean.getType() != null && bean.getType() == PictureEnum.EContentType.VOICE || bean.getType() == PictureEnum.EContentType.VOICE_AND_VOTE) {
                    if (!TextUtils.isEmpty(bean.getAttachment())) {
                        List<AttachmentBean> attachmentBeans;
                        try {
                            attachmentBeans = new Gson().fromJson(bean.getAttachment(),
                                    new TypeToken<List<AttachmentBean>>() {
                                    }.getType());
                        } catch (Exception e) {
                            attachmentBeans = new ArrayList<>();
                        }
                        if (attachmentBeans != null && attachmentBeans.size() > 0) {
                            AttachmentBean attachmentBean = attachmentBeans.get(0);
                            if(!TextUtils.isEmpty(attachmentBean.getUrl())){
                                voiceUrl = attachmentBean.getUrl();
                                //语音播放过程中置顶/取消置顶
                                if(currentMessage!=null){
                                    if(bean.getIsTop()==1){
                                        currentMessage.setIsTop(1);
                                    }else {
                                        currentMessage.setIsTop(0);
                                    }
                                }
                            }
                        }
                    }
                }else {
                    voiceUrl = "";
                }
                page = 1;
                httpGetMyTrends();
            }

            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
        //播放语音
        adapter.setPlayVoiceListener(new IPlayVoiceListener() {
            @Override
            public void play(MessageInfoBean bean) {
                playVoice(bean);
            }
        });
        bindingView.swipeRefreshLayout.setColorSchemeResources(R.color.c_169BD5);
        //发新动态
        bindingView.ivCreateCircle.setOnClickListener(v -> {
            AudioPlayUtil.stopAudioPlay();
            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                ToastUtil.show(getString(R.string.user_disable_message));
                return;
            }
            PictureSelector.create(MyTrendsActivity.this)
                    .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                    .previewImage(false)// 是否可预览图片 true or false
                    .isCamera(true)// 是否显示拍照按钮 ture or false
                    .maxVideoSelectNum(1)
                    .recordVideoSecond(CreateCircleActivity.RECORD_VIDEO_SECOND)// 视频最长可以录制多少秒
                    .compress(true)// 是否压缩 true or false
                    .isGif(true)
                    .selectArtworkMaster(false)
                    .toResult(PictureConfig.CHOOSE_REQUEST);//结果回调 code
        });
        //是否有未读互动消息
        List<InteractMessage> list = msgDao.getUnreadMsgList();
        if (list != null && list.size() > 0) {
            String avatar = "";
            int size = list.size();
            if (list.get(0) != null) {
                if (!TextUtils.isEmpty(list.get(0).getAvatar())) {
                    avatar = list.get(0).getAvatar();
                }
            }
            adapter.showNotice(true, avatar, size);
        } else {
            adapter.showNotice(false, "", 0);
        }
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
        bindingView.ivMore.setVisibility(View.GONE);
    }

    /**
     * 发请求->获取我的动态(说说主页及列表)
     */
    private void httpGetMyTrends() {
        if (checkNetConnectStatus(1)) {
            action.httpGetMyTrends(page, DEFAULT_PAGE_SIZE, new CallBack<ReturnBean<CircleTrendsBean>>() {
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
                            if (bean != null){
                                adapter.setTopData(bean);
                            }
                            //动态列表
                            if (bean.getMomentList() != null && bean.getMomentList().size() > 0) {
                                //1-1 加载更多，则分页数据填充到尾部
                                if (page > 1) {
                                    adapter.addMoreList(bean.getMomentList());
                                    adapter.setLoadState(adapter.LOADING_MORE);
                                } else {
                                    if(!TextUtils.isEmpty(voiceUrl)){
                                        if (AudioPlayManager2.getInstance().isPlay(Uri.parse(voiceUrl))) {
                                            updatePositionList(adapter.getDataList());//如果超过一页，所以要把全部数据放进去查找
                                        }
                                    }
                                    //1-2 第一次加载，若超过3个显示加载更多
                                    mList.clear();
                                    mList.addAll(bean.getMomentList());
                                    adapter.updateList(mList);
                                    if (mList.size() >= EndlessRecyclerOnScrollListener.DEFULT_SIZE_3) {
                                        adapter.setLoadState(adapter.LOADING_MORE);
                                    }
                                    //缓存我的动态第一页数据
                                    FileCacheUtil.putFirstPageCache(UserAction.getMyId() + "httpGetMyTrends",
                                            new Gson().toJson(response.body().getData()));
                                }
                                page++;
                            } else {
                                //2 无数据
                                //2-1 加载更多，当没有数据的时候，提示已经到底了
                                if (page > 1) {
                                    adapter.setLoadState(adapter.LOADING_END);
                                } else {
                                    //2-2 第一次加载，没有数据则不显示尾部
                                    adapter.setLoadState(adapter.LOADING_GONE);
                                }
                            }
                        }
                    } else {
                        ToastUtil.show("获取我的动态失败，请检查您的网络是否正常");
                    }
                    bindingView.swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<ReturnBean<CircleTrendsBean>> call, Throwable t) {
                    super.onFailure(call, t);
                    ToastUtil.show("获取我的动态失败，请检查您的网络是否正常");
                    bindingView.swipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            if (page == 1) {
                String content = FileCacheUtil.getFirstPageCache(UserAction.getMyId() + "httpGetMyTrends");
                if (!TextUtils.isEmpty(content)) {
                    CircleTrendsBean bean = new Gson().fromJson(content, CircleTrendsBean.class);
                    if (bean.getMomentList() != null && bean.getMomentList().size() > 0) {
                        //1-2 第一次加载，若超过3个显示加载更多
                        mList.clear();
                        mList.addAll(bean.getMomentList());
                        adapter.setTopData(bean);
                        adapter.updateList(mList);
                        if (mList.size() >= EndlessRecyclerOnScrollListener.DEFULT_SIZE_3) {
                            adapter.setLoadState(adapter.LOADING_MORE);
                        }
                    } else {
                        ToastUtil.show("获取我的动态失败，请检查您的网络是否正常");
                    }
                    page++;
                }
            } else {
                ToastUtil.show("获取我的动态失败，请检查您的网络是否正常");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHANGE_BACKGROUND:
                    // 图片选择结果回调
                    final String file = PictureSelector.obtainMultipleResult(data).get(0).getCompressPath();
                    // 例如 LocalMedia 里面返回两种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
//                    Uri uri = Uri.fromFile(new File(file));
                    alert.show();
                    //上传背景图
                    if (upFileAction == null) {
                        upFileAction = new UpFileAction();
                    }
                    upFileAction.upFile(UserAction.getMyId() + "", UpFileAction.PATH.CIRCLE_BACKGROUND, getContext(), new UpFileUtil.OssUpCallback() {
                        @Override
                        public void success(String url) {
                            alert.dismiss();
                            //通知更新背景图
                            httpSetBackground(url);
                        }

                        @Override
                        public void fail() {
                            alert.dismiss();
                            ToastUtil.show(getContext(), "背景图上传失败!");
                        }

                        @Override
                        public void inProgress(long progress, long zong) {

                        }
                    }, file);
                    break;
            }
        }
    }

    /**
     * 发请求->更新背景图
     *
     * @param url
     */
    private void httpSetBackground(String url) {
        action.httpSetBackground(url, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    ToastUtil.show("更新背景图成功");
                    //显示背景图
                    adapter.notifyBackground(url);
                } else {
                    ToastUtil.show("更新背景图失败");
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("更新背景图失败");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().unregister(this);
        }
        AudioPlayUtil.stopAudioPlay();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UpdateOneTrend(EventFactory.UpdateOneTrendEvent event) {
        //更新我的单条动态
        if (event.action == 1) {
            MessageInfoBean bean = adapter.getDataList().get(event.position - 1);//去掉头部
            if (bean.getId() != null && bean.getUid() != null) {
                queryById(bean.getId().longValue(), bean.getUid().longValue(), event.position - 1);
            }
            //详情修改可见度
        } else if (event.action == 4) {
            adapter.getDataList().get(event.position - 1).setVisibility(event.visibility);
            adapter.notifyItemChanged(event.position);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteItem(EventFactory.DeleteMyItemTrend event) {
        adapter.getDataList().remove(event.position - 1);
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshMyTrends(EventFactory.CreateNewInMyTrends event) {
        page = 1;
        httpGetMyTrends();
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
                    //开始播放语音，传入点击的动态实体类
                    AudioPlayUtil.startAudioPlay(MyTrendsActivity.this, attachmentBean.getUrl(), messageInfoBean, new IAudioPlayProgressListener() {
                        @Override
                        public void onStart(Uri var1, Object o) {
                            messageInfoBean.setPlay(true);
                            messageInfoBean.setPlayProgress(0);//设置播放状态和进度
                            currentMessage = messageInfoBean;//开始播放的时候，保存一份数据复制出来
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
                            messageInfoBean.setPlayProgress(100);//播放完成
                            updatePosition(messageInfoBean);

                        }

                        @Override
                        public void onProgress(int progress, Object o) {
                            LogUtil.getLog().i("语音", "播放进度--" + progress);
                            messageInfoBean.setPlay(true);
                            messageInfoBean.setPlayProgress(progress);//更新进度
                            updatePosition(messageInfoBean);
                        }
                    });

                }
            }
        }
    }

    private void updatePosition(MessageInfoBean messageInfoBean) {
        if (adapter == null || adapter.getDataList() == null || messageInfoBean == null) {
            return;
        }
        bindingView.recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                //从最新的数据里面查找出正在播放对象的位置
                int position = adapter.getDataList().indexOf(messageInfoBean);
                if (position >= 0) {
                    adapter.getDataList().set(position, messageInfoBean);//每隔0.1s刷新进度并替换进去
                    adapter.notifyItemChanged(position + 1);//头部
                }
            }
        }, 100);
    }

    /*
     * 发送消息前，需要检测网络连接状态，网络不可用，不能发送
     * 每条消息发送前，需要检测，语音和小视频录制之前，仍需要检测
     * type=0 默认提示 type=1 仅获取断网状态/不提示
     * */
    public boolean checkNetConnectStatus(int type) {
        boolean isOk;
        if (!NetUtil.isNetworkConnected()) {
            if (type == 0) {
                ToastUtil.show("网络连接不可用，请稍后重试");
            }
            isOk = false;
        } else {
            isOk = SocketUtil.getSocketUtil().getOnlineState();
            if (!isOk) {
                if (type == 0) {
                    ToastUtil.show("连接已断开，请稍后再试");
                }
            }
        }
        return isOk;
    }

    private void updatePositionList(List<MessageInfoBean> list) {
        if (currentMessage == null) {
            return;
        }
        MessageInfoBean msgTemp = null;
        int index = list.indexOf(currentMessage);//通过重写equals方法，比较id，找到正在播放的同一个object对象
        if (index < 0) {
            return;
        }
        msgTemp = list.get(index);
        msgTemp.setPlay(currentMessage.isPlay());
        msgTemp.setPlayProgress(currentMessage.getPlayProgress());
        list.set(index,msgTemp);
    }

}
