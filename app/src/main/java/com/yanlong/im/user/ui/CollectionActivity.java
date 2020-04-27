package com.yanlong.im.user.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.ui.BaseBindActivity;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.NoRedEnvelopesBean;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.VideoPlayActivity;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.databinding.ActivityCollectionBinding;
import com.yanlong.im.databinding.ItemCollectionViewBinding;
import com.yanlong.im.user.bean.CollectionInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.audio.AudioPlayManager;
import com.yanlong.im.utils.audio.IVoicePlayListener;
import com.yanlong.im.view.face.FaceView;
import com.yanlong.im.view.face.ShowBigFaceActivity;
import com.zhaoss.weixinrecorded.util.RxJavaUtil;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-02-2
 * @updateAuthor
 * @updateDate
 * @description 收藏
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class CollectionActivity extends BaseBindActivity<ActivityCollectionBinding> {

    private CommonRecyclerViewAdapter<CollectionInfo, ItemCollectionViewBinding> mViewAdapter;
    private List<CollectionInfo> mList = new ArrayList<>();
    private MsgDao mMsgDao = new MsgDao();
    private MsgAction msgAction = new MsgAction();

    //加载布局
    @Override
    protected int setView() {
        return R.layout.activity_collection;
    }

    //初始化
    @Override
    protected void init(Bundle savedInstanceState) {
        mViewAdapter = new CommonRecyclerViewAdapter<CollectionInfo, ItemCollectionViewBinding>(this, R.layout.item_collection_view) {
            //item显示
            @Override
            public void bind(ItemCollectionViewBinding binding, CollectionInfo memberUser,
                             int position, RecyclerView.ViewHolder viewHolder) {

                if(mList!=null && mList.size()>0){
                    CollectionInfo collectionInfo = mList.get(position);
                    if(!TextUtils.isEmpty(collectionInfo.getData())){
                        MsgAllBean bean = new Gson().fromJson(collectionInfo.getData(),MsgAllBean.class) ;
                        //显示用户名或群名
                        if(!TextUtils.isEmpty(collectionInfo.getFromGroupName())){
                            binding.tvName.setText(collectionInfo.getFromGroupName());
                        }else if(!TextUtils.isEmpty(collectionInfo.getFromUsername())){
                            binding.tvName.setText(collectionInfo.getFromUsername());
                        }else {
                            binding.tvName.setText("");
                        }
                        //收藏时间
                        if(!TextUtils.isEmpty(collectionInfo.getCreateTime())){
                            binding.tvDate.setText(TimeToString.getTimeForCollect(Long.parseLong(collectionInfo.getCreateTime())));
                        }else {
                            binding.tvDate.setText("");
                        }
                        //不同类型显示
                        switch (collectionInfo.getType()){
                            case ChatEnum.EMessageType.TEXT: //文字
                                binding.tvContent.setVisibility(VISIBLE);//显示文字相关布局
                                if(bean!=null){
                                    if(bean.getChat()!=null){
                                        if(!TextUtils.isEmpty(bean.getChat().getMsg())){
                                            binding.tvContent.setText(bean.getChat().getMsg());
                                        }else {
                                            binding.tvContent.setText("");
                                        }
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.IMAGE: //图片
                                binding.layoutPic.setVisibility(VISIBLE);
                                binding.ivPic.setVisibility(View.VISIBLE);
                                if(bean!=null){
                                    if(bean.getImage()!=null){ //显示预览图或者缩略图
                                        if(!TextUtils.isEmpty(bean.getImage().getPreview())){
                                            Glide.with(CollectionActivity.this).load(bean.getImage().getPreview())
                                                    .apply(GlideOptionsUtil.headImageOptions()).into(binding.ivPic);
                                        }else if(!TextUtils.isEmpty(bean.getImage().getThumbnail())){
                                            Glide.with(CollectionActivity.this).load(bean.getImage().getThumbnail())
                                                    .apply(GlideOptionsUtil.headImageOptions()).into(binding.ivPic);
                                        }
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.SHIPPED_EXPRESSION: //大表情
                                binding.layoutPic.setVisibility(VISIBLE);
                                binding.ivPic.setVisibility(View.VISIBLE);
                                if(bean!=null){
                                    if(bean.getShippedExpressionMessage()!=null){
                                        if(!TextUtils.isEmpty(bean.getShippedExpressionMessage().getId())){
                                            Glide.with(CollectionActivity.this).load(Integer.parseInt(FaceView.map_FaceEmoji.get(bean.getShippedExpressionMessage().getId()).toString())).
                                                    listener(new RequestListener() {
                                                        @Override
                                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                                                            return false;
                                                        }
                                                    }).apply(GlideOptionsUtil.headImageOptions()).into(binding.ivPic);
                                        }
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.MSG_VIDEO: //短视频消息
                                binding.layoutPic.setVisibility(VISIBLE);
                                binding.ivPic.setVisibility(View.VISIBLE);
                                binding.ivPlay.setVisibility(VISIBLE);
                                if(bean!=null){
                                    if(bean.getVideoMessage()!=null){ //显示背景图
                                        if(!TextUtils.isEmpty(bean.getVideoMessage().getBg_url())){
                                            Glide.with(CollectionActivity.this).load(bean.getVideoMessage().getBg_url())
                                                    .apply(GlideOptionsUtil.headImageOptions()).into(binding.ivPic);
                                        }
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.VOICE: //语音
                                binding.voiceView.setVisibility(VISIBLE);
                                if(bean!=null){
                                    if(bean.getVoiceMessage()!=null){
                                        VoiceMessage vm = bean.getVoiceMessage();
                                        String url = bean.isMe() ? vm.getLocalUrl() : vm.getUrl();
                                        binding.voiceView.init(true,vm.getTime(), true, AudioPlayManager.getInstance().isPlay(Uri.parse(url)), vm.getPlayStatus());
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.LOCATION: //位置消息
                                break;
                            case ChatEnum.EMessageType.AT: //艾特@消息
                                binding.tvContent.setVisibility(VISIBLE);//显示文字相关布局
                                if(bean!=null){
                                    if(bean.getAtMessage()!=null){
                                        if(!TextUtils.isEmpty(bean.getAtMessage().getMsg())){
                                            binding.tvContent.setText(bean.getChat().getMsg());
                                        }
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.FILE: //文件

                                break;
                            default:
                                break;
                        }
                        onEvent(binding, position, collectionInfo);
                    }
                }
            }
        };


        bindingView.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bindingView.recyclerView.setAdapter(mViewAdapter);
    }

    //item点击事件
    private void onEvent(ItemCollectionViewBinding binding, int position, CollectionInfo collectionInfo) {
//        binding.imgContent.setOnClickListener(o->{
//            if(collectionInfo.getCollectionType() == ChatEnum.EMessageType.IMAGE){
//                List<LocalMedia> selectList = new ArrayList<>();
//                LocalMedia lc = new LocalMedia();
//                lc.setPath(collectionInfo.getPath());
//                selectList.add(lc);
//                PictureSelector.create(CollectionActivity.this)
//                        .themeStyle(R.style.picture_default_style)
//                        .isGif(false)
//                        .openExternalPreviewImage(0, selectList);
//            }else if(collectionInfo.getCollectionType() == ChatEnum.EMessageType.SHIPPED_EXPRESSION){
//                if (ViewUtils.isFastDoubleClick()) {
//                    return;
//                }
//                Bundle bundle = new Bundle();
//                bundle.putString(Preferences.DATA, FaceView.map_FaceEmoji.get(collectionInfo.getPath()).toString());
//                IntentUtil.gotoActivity(CollectionActivity.this, ShowBigFaceActivity.class, bundle);
//            }else if(collectionInfo.getCollectionType() == ChatEnum.EMessageType.MSG_VIDEO){
//                RxJavaUtil.run(new RxJavaUtil.OnRxAndroidListener<MsgAllBean>() {
//                    @Override
//                    public MsgAllBean doInBackground() throws Throwable {
//                        return new MsgDao().getMsgById(collectionInfo.getMsgId());
//                    }
//
//                    @Override
//                    public void onFinish(MsgAllBean msgbean) {
//                        String localUrl = msgbean.getVideoMessage().getLocalUrl();
//                        if (StringUtil.isNotNull(localUrl)) {
//                            File file = new File(localUrl);
//                            if (!file.exists()) {
//                                localUrl = msgbean.getVideoMessage().getUrl();
//                            }
//                        } else {
//                            localUrl = msgbean.getVideoMessage().getUrl();
//                        }
//                        Intent intent = new Intent(CollectionActivity.this, VideoPlayActivity.class);
//                        intent.putExtra("videopath", localUrl);
//                        intent.putExtra("videomsg", new Gson().toJson(msgbean));
//                        intent.putExtra("msg_id", collectionInfo.getMsgId());
//                        intent.putExtra("bg_url", msgbean.getVideoMessage().getBg_url());
//                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//                });
//            }
//        });
        binding.layoutView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPop(view,collectionInfo.getMsgId(),position);
                return true;
            }
        });
//        binding.voiceView.setOnClickListener(o->{
//            MsgAllBean msgAllBean = new Gson().fromJson(collectionInfo.getMsgBean(),MsgAllBean.class);
//            playVoice(msgAllBean,false,position);
//        });
    }

    //activity点击事件
    @Override
    protected void initEvent() {
        setActionBarLeft(bindingView.headView);
        bindingView.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mList = mMsgDao.findCollectionInfo(charSequence.toString());
                mViewAdapter.setData(mList);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //加载数据
    @Override
    protected void loadData() {
        //有网络则请求接口
        if(NetUtil.isNetworkConnected()){
            httpGetCollectList();
        }else {
            //没有网络则拿本地缓存数据
            mList = mMsgDao.findCollectionInfo("");
            mViewAdapter.setData(mList);
            checkData();
        }
        initPopupWindow();
    }

    // 气泡视图
    private PopupWindow mPopupWindow;// 长按消息弹出气泡PopupWindow
    private int popupWidth;// 气泡宽
    private int popupHeight;// 气泡高
    private ImageView mImgTriangleUp;// 上箭头
    private ImageView mImgTriangleDown;// 下箭头
    private TextView mTxtView1;
    private TextView mTxtView2;
    private View layoutContent;
    private View mRootView;

    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        mRootView = getLayoutInflater().inflate(R.layout.view_chat_bubble, null, false);
        //获取自身的长宽高
        mRootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWidth = mRootView.getMeasuredWidth();
        popupHeight = mRootView.getMeasuredHeight();
        mImgTriangleUp = mRootView.findViewById(R.id.img_triangle_up);
        mImgTriangleDown = mRootView.findViewById(R.id.img_triangle_down);
        layoutContent = mRootView.findViewById(R.id.layout_content);
        mTxtView1 = mRootView.findViewById(R.id.txt_value1);
        mTxtView2 = mRootView.findViewById(R.id.txt_value2);
        layoutContent.setVisibility(VISIBLE);
        mTxtView1.setVisibility(VISIBLE);
        mTxtView2.setVisibility(VISIBLE);
        mTxtView1.setText("转发");
        mTxtView2.setText("删除");
    }

    /***
     * 长按的气泡处理
     * @param v
     */
    private void showPop(View v,String msgId,int postion) {
        // 重新获取自身的长宽高
        mRootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWidth = mRootView.getMeasuredWidth();
        popupHeight = mRootView.getMeasuredHeight();

        // 获取ActionBar位置，判断消息是否到顶部
        // 获取ListView在屏幕顶部的位置
        int[] location = new int[2];
        bindingView.recyclerView.getLocationOnScreen(location);
        // 获取View在屏幕的位置
        int[] locationView = new int[2];
        v.getLocationOnScreen(locationView);

        mPopupWindow = new PopupWindow(mRootView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置弹窗外可点击
        mPopupWindow.setTouchable(true);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 当View Y轴的位置小于ListView Y轴的位置时 气泡向下弹出来，否则向上弹出
        if (v.getMeasuredHeight() >= bindingView.recyclerView.getMeasuredHeight() && locationView[1] < location[1]) {
            // 内容展示完，向上弹出
            if (locationView[1] < 0 && (v.getMeasuredHeight() - Math.abs(locationView[1]) < bindingView.recyclerView.getMeasuredHeight())) {
                mImgTriangleUp.setVisibility(VISIBLE);
                mImgTriangleDown.setVisibility(GONE);
                mPopupWindow.showAsDropDown(v);
            } else {
                // 中间弹出
                mImgTriangleUp.setVisibility(GONE);
                mImgTriangleDown.setVisibility(VISIBLE);
                showPopupWindowUp(v, 1);
            }
        } else if (locationView[1] < location[1]) {
            mImgTriangleUp.setVisibility(VISIBLE);
            mImgTriangleDown.setVisibility(GONE);
            mPopupWindow.showAsDropDown(v);
        } else {
            mImgTriangleUp.setVisibility(GONE);
            mImgTriangleDown.setVisibility(VISIBLE);
            showPopupWindowUp(v, 2);
        }
        //转发
        mTxtView1.setOnClickListener(o->{
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
            }
            if (NetUtil.isNetworkConnected()) {
                onRetransmission(mList.get(postion).getData());
            }else {
                ToastUtil.show("请检查网络连接是否正常");
            }
        });
        //删除
        mTxtView2.setOnClickListener(o->{
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
            }
            if (NetUtil.isNetworkConnected()) {
                if(mList.get(postion)!=null){
                    if(mList.get(postion).getId()!=0L){
                        httpCancelCollect(mList.get(postion).getId(),postion,msgId);
                    }
                }
            }else {
                //暂时本地删除
                ToastUtil.showLong(CollectionActivity.this,"请检查网络连接是否正常\n已为您暂时隐藏此消息");
                mMsgDao.deleteCollectionInfo(msgId);
                mList.remove(postion);
                checkData();
                mViewAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 转发
     *
     * @param value
     */
    private void onRetransmission(String value) {
        startActivity(new Intent(getContext(), MsgForwardActivity.class)
                .putExtra(MsgForwardActivity.AGM_JSON, value));
    }

    /**
     * 设置显示在v上方(以v的左边距为开始位置)
     *
     * @param v
     */
    public void showPopupWindowUp(View v, int gravity) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        if (gravity == 1) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, dm.heightPixels / 2);
        } else {
            //在控件上方显示
            mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
        }
    }

    private void checkData(){
        if(mList.size()==0){
            bindingView.viewNodata.setVisibility(VISIBLE);
            bindingView.recyclerView.setVisibility(GONE);
        }else{
            bindingView.viewNodata.setVisibility(GONE);
            bindingView.recyclerView.setVisibility(VISIBLE);
        }
    }

    private void playVoice(final MsgAllBean bean, final boolean canAutoPlay, final int position) {
        VoiceMessage vm = bean.getVoiceMessage();
        if (vm == null || TextUtils.isEmpty(vm.getUrl())) {
            return;
        }
        String url = "";
        if (bean.isMe()) {
            url = vm.getLocalUrl();
        } else {
            url = vm.getUrl();
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (AudioPlayManager.getInstance().isPlay(Uri.parse(url))) {
            AudioPlayManager.getInstance().stopPlay();
        } else {
            startPlayVoice(bean, canAutoPlay, position);
        }
    }

    private void updatePlayStatus(MsgAllBean bean, int position, @ChatEnum.EPlayStatus int status) {
        if (ChatEnum.EPlayStatus.PLAYING == status) {
            MessageManager.getInstance().setCanStamp(false);
        } else if (ChatEnum.EPlayStatus.STOP_PLAY == status || ChatEnum.EPlayStatus.PLAYED == status) {
            MessageManager.getInstance().setCanStamp(true);
        }
    }

    private void startPlayVoice(MsgAllBean bean, boolean canAutoPlay, final int position) {
        AudioPlayManager.getInstance().startPlay(context, bean, position, canAutoPlay, new IVoicePlayListener() {
            @Override
            public void onStart(MsgAllBean bean) {
                updatePlayStatus(bean, position, ChatEnum.EPlayStatus.PLAYING);
            }

            @Override
            public void onStop(MsgAllBean bean) {
                updatePlayStatus(bean, position, ChatEnum.EPlayStatus.STOP_PLAY);
            }

            @Override
            public void onComplete(MsgAllBean bean) {
                updatePlayStatus(bean, position, ChatEnum.EPlayStatus.PLAYED);
            }
        });
    }

    /**
     * 发请求->获取收藏列表
     */
    private void httpGetCollectList() {
        msgAction.getCollectList(new CallBack<ReturnBean<List<CollectionInfo>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<CollectionInfo>>> call, Response<ReturnBean<List<CollectionInfo>>> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    List<CollectionInfo> list = response.body().getData();
                    mList.clear();
                    mList.addAll(list);
                    mViewAdapter.setData(mList);
                    checkData();
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<CollectionInfo>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    /**
     * 发请求->取消收藏
     * @param id
     */
    private void httpCancelCollect(Long id,int postion,String msgId) {
        msgAction.cancelCollectMsg(id,new CallBack<ReturnBean>() {
                    @Override
                    public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                        super.onResponse(call, response);
                        if (response.body() == null) {
                            return;
                        }
                        if (response.body().isOk()) {
                            ToastUtil.show(CollectionActivity.this, "删除成功!");
                            //同时将本地删除
                            mMsgDao.deleteCollectionInfo(msgId);
                            mList.remove(postion);
                            checkData();
                            mViewAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnBean> call, Throwable t) {
                        super.onFailure(call, t);
                        ToastUtil.show(CollectionActivity.this, t.getMessage());
                    }
                });
    }

}
