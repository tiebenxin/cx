package com.yanlong.im.circle.mycircle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hm.cxpay.widget.refresh.EndlessRecyclerOnScrollListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.audio.AudioPlayUtil;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.rxbus2.RxBus;
import com.yanlong.im.R;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.circle.adapter.MyTrendsAdapter;
import com.yanlong.im.circle.bean.CircleTrendsBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.databinding.ActivityMyCircleBinding;
import com.yanlong.im.interf.IRefreshListenr;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.view.YLLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

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

    private TempAction action;
    private UpFileAction upFileAction;
    private MyTrendsAdapter adapter;
    private List<MessageInfoBean> mList;
    private MsgDao msgDao;

    @Override
    protected int setView() {
        return R.layout.activity_my_circle;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        action = new TempAction();
        mList = new ArrayList<>();
        msgDao = new MsgDao();
        bindingView.layoutFollow.setVisibility(View.GONE);
        bindingView.layoutChat.setVisibility(View.GONE);
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
        adapter = new MyTrendsAdapter(MyTrendsActivity.this,mList,1,0);
        bindingView.recyclerView.setAdapter(adapter);
        bindingView.recyclerView.setLayoutManager(new YLLinearLayoutManager(this));
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
            public void onRefresh() {
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
        bindingView.swipeRefreshLayout.setColorSchemeResources(R.color.c_169BD5);
        //发新动态
        bindingView.ivCreateCircle.setOnClickListener(v -> {
            AudioPlayUtil.stopAudioPlay();
            PictureSelector.create(MyTrendsActivity.this)
                    .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                    .previewImage(false)// 是否可预览图片 true or false
                    .isCamera(true)// 是否显示拍照按钮 ture or false
                    .maxVideoSelectNum(1)
                    .compress(true)// 是否压缩 true or false
                    .isGif(true)
                    .selectArtworkMaster(true)
                    .toResult(PictureConfig.CHOOSE_REQUEST);//结果回调 code
        });
        //是否有未读互动消息
        if(msgDao.getUnreadMsgList()!=null && msgDao.getUnreadMsgList().size()>0){
            String avatar = "";
            int size = msgDao.getUnreadMsgList().size();
            if(msgDao.getUnreadMsgList().get(0)!=null){
                if(!TextUtils.isEmpty(msgDao.getUnreadMsgList().get(0).getAvatar())){
                    avatar = msgDao.getUnreadMsgList().get(0).getAvatar();
                }
            }
            adapter.showNotice(true,avatar,size);
        }else {
            adapter.showNotice(false,"",0);
        }
    }

    /**
     * 发请求->获取我的动态(说说主页及列表)
     */
    private void httpGetMyTrends() {
        action.httpGetMyTrends(page, DEFAULT_PAGE_SIZE, new CallBack<ReturnBean<CircleTrendsBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<CircleTrendsBean>> call, Response<ReturnBean<CircleTrendsBean>> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    //1 有数据
                    if(response.body().getData()!=null){
                        CircleTrendsBean bean = response.body().getData();
                        //动态列表
                        if(bean.getMomentList()!=null && bean.getMomentList().size()>0){
                            //1-1 加载更多，则分页数据填充到尾部
                            if (page > 1) {
                                adapter.addMoreList(bean.getMomentList());
                                adapter.setLoadState(adapter.LOADING_MORE);
                            }else {
                                //1-2 第一次加载，若超过3个显示加载更多
                                mList.clear();
                                mList.addAll(bean.getMomentList());
                                adapter.setTopData(bean);
                                adapter.updateList(mList);
                                if(mList.size()>=EndlessRecyclerOnScrollListener.DEFULT_SIZE_3){
                                    adapter.setLoadState(adapter.LOADING_MORE);
                                }
                            }
                            page++;
                        }else {
                            //2 无数据
                            //2-1 加载更多，当没有数据的时候，提示已经到底了
                            if (page > 1) {
                                adapter.setLoadState(adapter.LOADING_END);
                            } else {
                                //2-2 第一次加载，没有数据则不显示尾部
                                adapter.setLoadState(adapter.LOADING_GONE);
                                adapter.setTopData(bean);
                            }
                        }
                    }
                }else {
                    ToastUtil.show("获取我的动态失败");
                }
                bindingView.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ReturnBean<CircleTrendsBean>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("获取我的动态失败");
                bindingView.swipeRefreshLayout.setRefreshing(false);
            }
        });
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
                    //显示背景图
                    adapter.notifyBackground(file);
                    //上传背景图
                    if(upFileAction==null){
                        upFileAction = new UpFileAction();
                    }
                    upFileAction.upFile(UserAction.getMyId() + "", UpFileAction.PATH.IMG, getContext(), new UpFileUtil.OssUpCallback() {
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
                if (response.body().isOk()){
                    ToastUtil.show("更新背景图成功");
                }else {
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

//    /**
//     * 是否显示无数据占位图
//     * @param ifShow
//     */
//    private void showNoDataLayout(boolean ifShow) {
//        if (ifShow) {
//            bindingView.recyclerView.setVisibility(View.GONE);
//            bindingView.noDataLayout.setVisibility(View.VISIBLE);
//        } else {
//            bindingView.recyclerView.setVisibility(View.VISIBLE);
//            bindingView.noDataLayout.setVisibility(View.GONE);
//        }
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (RxBus.getDefault().isRegistered(this)) {
//            RxBus.getDefault().unregister(this);
//        }
//        AudioPlayUtil.stopAudioPlay();
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().unregister(this);
        }
        AudioPlayUtil.stopAudioPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        httpGetMyTrends();
    }
}
