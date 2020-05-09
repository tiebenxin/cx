package com.yanlong.im.user.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.nim_lib.ui.BaseBindActivity;
import com.google.gson.Gson;
import com.hm.cxpay.utils.DateUtils;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.databinding.ActivityCollectionBinding;
import com.yanlong.im.databinding.ItemCollectionViewBinding;
import com.yanlong.im.location.LocationUtils;
import com.yanlong.im.user.bean.CollectionInfo;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.audio.AudioPlayManager;
import com.yanlong.im.utils.audio.IVoicePlayListener;
import com.yanlong.im.view.face.FaceView;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.FileUtils;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;

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
    private List<CollectionInfo> allCollectList = new ArrayList<>();//默认所有收藏数据
    private List<CollectionInfo> searchCollectList = new ArrayList<>();//搜索出来的数据
    private MsgDao mMsgDao = new MsgDao();
    private MsgAction msgAction = new MsgAction();
    private String key = "";//搜索关键字
    public static final int CANCEL_COLLECT = 0;//取消收藏

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

                if (mList != null && mList.size() > 0) {
                    CollectionInfo collectionInfo = mList.get(position);
                    if (!TextUtils.isEmpty(collectionInfo.getData())) {
                        MsgAllBean bean = new Gson().fromJson(collectionInfo.getData(), MsgAllBean.class);
                        //显示用户名或群名
                        if (!TextUtils.isEmpty(collectionInfo.getFromGroupName())) {
                            binding.tvName.setText("来自群聊 " + collectionInfo.getFromGroupName());
                        } else if (!TextUtils.isEmpty(collectionInfo.getFromUsername())) {
                            binding.tvName.setText("来自用户 " + collectionInfo.getFromUsername());
                        } else {
                            binding.tvName.setText("未知来源");
                        }
                        //收藏时间
                        if (!TextUtils.isEmpty(collectionInfo.getCreateTime())) {
                            binding.tvDate.setText(TimeToString.getTimeForCollect(Long.parseLong(collectionInfo.getCreateTime())));
                        } else {
                            binding.tvDate.setText("");
                        }
                        //不同类型显示
                        switch (collectionInfo.getType()) {
                            case ChatEnum.EMessageType.TEXT: //文字
                                binding.tvContent.setVisibility(VISIBLE);//显示文字相关布局，隐藏其他类型相关布局
                                binding.layoutVoice.setVisibility(GONE);
                                binding.layoutPic.setVisibility(GONE);
                                binding.layoutFile.setVisibility(GONE);
                                binding.layoutLocation.setVisibility(GONE);
                                if (bean != null) {
                                    if (bean.getChat() != null) {
                                        if (!TextUtils.isEmpty(bean.getChat().getMsg())) {
                                            binding.tvContent.setText(getSpan(bean.getChat().getMsg()));
                                        } else {
                                            binding.tvContent.setText("");
                                        }
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.IMAGE: //图片
                                binding.tvContent.setVisibility(GONE);//显示图片相关布局，隐藏其他类型相关布局
                                binding.layoutVoice.setVisibility(GONE);
                                binding.layoutPic.setVisibility(VISIBLE);
                                binding.layoutFile.setVisibility(GONE);
                                binding.layoutLocation.setVisibility(GONE);
                                binding.ivPlay.setVisibility(GONE);
                                if (bean != null) {
                                    if (bean.getImage() != null) { //显示预览图或者缩略图
                                        if (!TextUtils.isEmpty(bean.getImage().getPreview())) {
                                            Glide.with(CollectionActivity.this).load(bean.getImage().getPreview())
                                                    .apply(GlideOptionsUtil.headImageOptions()).into(binding.ivPic);
                                        } else if (!TextUtils.isEmpty(bean.getImage().getThumbnail())) {
                                            Glide.with(CollectionActivity.this).load(bean.getImage().getThumbnail())
                                                    .apply(GlideOptionsUtil.headImageOptions()).into(binding.ivPic);
                                        }
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.SHIPPED_EXPRESSION: //大表情
                                binding.tvContent.setVisibility(GONE);//显示表情相关布局，隐藏其他类型相关布局
                                binding.layoutVoice.setVisibility(GONE);
                                binding.layoutPic.setVisibility(VISIBLE);
                                binding.layoutFile.setVisibility(GONE);
                                binding.layoutLocation.setVisibility(GONE);
                                binding.ivPlay.setVisibility(GONE);
                                if (bean != null) {
                                    if (bean.getShippedExpressionMessage() != null) {
                                        if (!TextUtils.isEmpty(bean.getShippedExpressionMessage().getId())) {
                                            Glide.with(CollectionActivity.this).load(Integer.parseInt(FaceView.map_FaceEmoji.get(bean.getShippedExpressionMessage().getId()).toString())).apply(GlideOptionsUtil.headImageOptions()).into(binding.ivPic);
                                        }
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.MSG_VIDEO: //短视频消息
                                binding.tvContent.setVisibility(GONE);//显示短视频相关布局，隐藏其他类型相关布局
                                binding.layoutVoice.setVisibility(GONE);
                                binding.layoutPic.setVisibility(VISIBLE);
                                binding.layoutFile.setVisibility(GONE);
                                binding.layoutLocation.setVisibility(GONE);
                                binding.ivPlay.setVisibility(VISIBLE);
                                if (bean != null) {
                                    if (bean.getVideoMessage() != null) {
                                        if (!TextUtils.isEmpty(bean.getVideoMessage().getBg_url())) {
                                            Glide.with(CollectionActivity.this).load(bean.getVideoMessage().getBg_url())
                                                    .apply(GlideOptionsUtil.headImageOptions()).into(binding.ivPic);
                                        }
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.VOICE: //语音
                                binding.tvContent.setVisibility(GONE);//显示语音相关布局，隐藏其他类型相关布局
                                binding.layoutVoice.setVisibility(VISIBLE);
                                binding.layoutPic.setVisibility(GONE);
                                binding.layoutFile.setVisibility(GONE);
                                binding.layoutLocation.setVisibility(GONE);
                                if (bean != null) {
                                    if (bean.getVoiceMessage() != null) {
                                        if (bean.getVoiceMessage().getTime() != 0) {
                                            binding.tvVoiceTime.setText(DateUtils.getSecondFormatTime(Long.valueOf(bean.getVoiceMessage().getTime() + "")));
                                        }
//                                        VoiceMessage vm = bean.getVoiceMessage();
//                                        String url = bean.isMe() ? vm.getLocalUrl() : vm.getUrl();
//                                        binding.voiceView.init(true,vm.getTime(), true, AudioPlayManager.getInstance().isPlay(Uri.parse(url)), vm.getPlayStatus());
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.LOCATION: //位置消息
                                binding.tvContent.setVisibility(GONE);//显示位置相关布局，隐藏其他类型相关布局
                                binding.layoutVoice.setVisibility(GONE);
                                binding.layoutPic.setVisibility(GONE);
                                binding.layoutFile.setVisibility(GONE);
                                binding.layoutLocation.setVisibility(VISIBLE);
                                if (bean != null) {
                                    if (bean.getLocationMessage() != null) {
                                        if (!TextUtils.isEmpty(bean.getLocationMessage().getAddress())) {
                                            binding.tvLocationName.setText(bean.getLocationMessage().getAddress());
                                        }
                                        if (!TextUtils.isEmpty(bean.getLocationMessage().getAddressDescribe())) {
                                            binding.tvLocationDesc.setText(bean.getLocationMessage().getAddressDescribe());
                                        }
                                        if (!TextUtils.isEmpty(bean.getLocationMessage().getImg())) {
                                            Glide.with(CollectionActivity.this)
                                                    .asBitmap()
                                                    .load(bean.getLocationMessage().getImg())
                                                    .into(new SimpleTarget<Bitmap>() {
                                                        @Override
                                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                            binding.ivLocation.setImageBitmap(resource);
                                                        }
                                                    });
                                        } else {
                                            String baiduImageUrl = LocationUtils.getLocationUrl(bean.getLocationMessage().getLatitude(), bean.getLocationMessage().getLongitude());
                                            Glide.with(CollectionActivity.this)
                                                    .asBitmap()
                                                    .load(baiduImageUrl)
                                                    .into(new SimpleTarget<Bitmap>() {
                                                        @Override
                                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                            binding.ivLocation.setImageBitmap(resource);
                                                        }
                                                    });
                                        }
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.AT: //艾特@消息
                                binding.tvContent.setVisibility(VISIBLE);//显示@消息相关布局，隐藏其他类型相关布局
                                binding.layoutVoice.setVisibility(GONE);
                                binding.layoutPic.setVisibility(GONE);
                                binding.layoutFile.setVisibility(GONE);
                                binding.layoutLocation.setVisibility(GONE);
                                if (bean != null) {
                                    if (bean.getAtMessage() != null) {
                                        if (!TextUtils.isEmpty(bean.getAtMessage().getMsg())) {
                                            binding.tvContent.setText(bean.getAtMessage().getMsg());
                                        }
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.FILE: //文件
                                binding.tvContent.setVisibility(GONE);//显示文件相关布局，隐藏其他类型相关布局
                                binding.layoutVoice.setVisibility(GONE);
                                binding.layoutPic.setVisibility(GONE);
                                binding.layoutFile.setVisibility(VISIBLE);
                                binding.layoutLocation.setVisibility(GONE);
                                if (bean != null) {
                                    if (bean.getSendFileMessage() != null) {
                                        if (!TextUtils.isEmpty(bean.getSendFileMessage().getFile_name())) {
                                            binding.tvFileName.setText(bean.getSendFileMessage().getFile_name());
                                        }
                                        if (!TextUtils.isEmpty(bean.getSendFileMessage().getFormat())) {
                                            String fileFormat = bean.getSendFileMessage().getFormat();
                                            if (fileFormat.equals("txt")) {
                                                binding.ivFilePic.setImageResource(R.mipmap.ic_txt);
                                            } else if (fileFormat.equals("xls") || fileFormat.equals("xlsx")) {
                                                binding.ivFilePic.setImageResource(R.mipmap.ic_excel);
                                            } else if (fileFormat.equals("ppt") || fileFormat.equals("pptx") || fileFormat.equals("pdf")) { //PDF暂用此图标
                                                binding.ivFilePic.setImageResource(R.mipmap.ic_ppt);
                                            } else if (fileFormat.equals("doc") || fileFormat.equals("docx")) {
                                                binding.ivFilePic.setImageResource(R.mipmap.ic_word);
                                            } else if (fileFormat.equals("rar") || fileFormat.equals("zip")) {
                                                binding.ivFilePic.setImageResource(R.mipmap.ic_zip);
                                            } else if (fileFormat.equals("exe")) {
                                                binding.ivFilePic.setImageResource(R.mipmap.ic_exe);
                                            } else {
                                                binding.ivFilePic.setImageResource(R.mipmap.ic_unknow);
                                            }
                                        }
                                        if (bean.getSendFileMessage().getSize() != 0L) {
                                            binding.tvFileSize.setText(FileUtils.getFileSizeString(bean.getSendFileMessage().getSize()));
                                        }
                                    }
                                }
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
    private void onEvent(ItemCollectionViewBinding binding, int position, CollectionInfo bean) {
        binding.layoutView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPop(view, bean.getMsgId(), position);
                return true;
            }
        });
        binding.layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CollectionActivity.this,CollectDetailsActivity.class);
                intent.putExtra("json_data",new Gson().toJson(bean));//转换成json字符串再传过去
                intent.putExtra("position",position);//位置
                startActivityForResult(intent,CANCEL_COLLECT);
            }
        });
//        binding.ivPic.setOnClickListener(o->{
//            if(bean.getMsg_type() == ChatEnum.EMessageType.IMAGE){//点击图片
//                List<LocalMedia> selectList = new ArrayList<>();
//                LocalMedia lc = new LocalMedia();
//                if(!TextUtils.isEmpty(bean.getImage().getPreview())){
//                    lc.setPath(bean.getImage().getPreview());
//                }else if(!TextUtils.isEmpty(bean.getImage().getThumbnail())){
//                    lc.setPath(bean.getImage().getThumbnail());
//                }else {
//                    lc.setPath("");
//                    ToastUtil.show("图片路径出错!");
//                }
//                selectList.add(lc);
//                PictureSelector.create(CollectionActivity.this)
//                        .themeStyle(R.style.picture_default_style)
//                        .isGif(false)
//                        .openExternalPreviewImage(0, selectList);
//            }else if(bean.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO){//点击视频
//                RxJavaUtil.run(new RxJavaUtil.OnRxAndroidListener<MsgAllBean>() {
//                    @Override
//                    public MsgAllBean doInBackground() throws Throwable {
//                        return bean;
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
//                        intent.putExtra("msg_id", msgbean.getMsg_id());
//                        intent.putExtra("bg_url", msgbean.getVideoMessage().getBg_url());
//                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//                });
//            }else if(bean.getMsg_type() == ChatEnum.EMessageType.SHIPPED_EXPRESSION){//点击表情
//                if (ViewUtils.isFastDoubleClick()) {
//                    return;
//                }
//                if(!TextUtils.isEmpty(bean.getShippedExpressionMessage().getId())){
//                    Bundle bundle = new Bundle();
//                    bundle.putString(Preferences.DATA, FaceView.map_FaceEmoji.get(bean.getShippedExpressionMessage().getId()).toString());
//                    IntentUtil.gotoActivity(CollectionActivity.this, ShowBigFaceActivity.class, bundle);
//                }
//            }
//        });

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
//                mList = mMsgDao.findCollectionInfo(charSequence.toString());
//                mViewAdapter.setData(mList);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || TextUtils.isEmpty(s.toString())) {
                    key = "";
                    mList.clear();
                    mList.addAll(allCollectList);
                    checkData();
                    mViewAdapter.notifyDataSetChanged();
                } else {
                    taskSearch();
                }
            }
        });
    }

    //加载数据
    @Override
    protected void loadData() {
        //暂时只处理有网的情况
        if (!checkNetConnectStatus()) {
            return;
        }
        httpGetCollectList();
//        if (NetUtil.isNetworkConnected()) {
//            httpGetCollectList();
//        } else {
//            //没有网络则拿本地缓存数据
//            mList = mMsgDao.findCollectionInfo("");
//            mViewAdapter.setData(mList);
//            checkData();
//        }
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
    private void showPop(View v, String msgId, int postion) {
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
        mTxtView1.setOnClickListener(o -> {
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
            }
            if (NetUtil.isNetworkConnected()) {
                onRetransmission(mList.get(postion).getData());
            } else {
                ToastUtil.show("请检查网络连接是否正常");
            }
        });
        //删除 (暂时只处理有网的情况)
        mTxtView2.setOnClickListener(o -> {
            if (!checkNetConnectStatus()) {
                return;
            }
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
            }
            if (mList.get(postion) != null) {
                if (mList.get(postion).getId() != 0L) {
                    httpCancelCollect(mList.get(postion).getId(), postion);
                }
            }

//            else {
//                //暂时本地删除
//                ToastUtil.showLong(CollectionActivity.this, "请检查网络连接是否正常\n已为您暂时隐藏此消息");
//                mMsgDao.deleteCollectionInfo(msgId);
//                mList.remove(postion);
//                checkData();
//                mViewAdapter.notifyDataSetChanged();
//            }
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

    private void checkData() {
        if (mList.size() == 0) {
            bindingView.viewNodata.setVisibility(VISIBLE);
            bindingView.recyclerView.setVisibility(GONE);
        } else {
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
                if (response.body().isOk()) {
                    List<CollectionInfo> list = response.body().getData();
                    mList.clear();
                    mList.addAll(list);
                    mViewAdapter.setData(mList);
                    allCollectList.addAll(list);
                    checkData();
                    //本地保存收藏列表数据
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<CollectionInfo>>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show(t.getMessage());
            }
        });
    }

    /**
     * 发请求->取消收藏
     *
     * @param id
     */
    private void httpCancelCollect(Long id, int postion) {
        msgAction.cancelCollectMsg(id, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
//                    ToastUtil.show(CollectionActivity.this, "删除成功!");
                    Snackbar.make(findViewById(R.id.layout_main), "取消收藏成功!", Snackbar.LENGTH_SHORT).show();
                    //同时将本地删除
//                    mMsgDao.deleteCollectionInfo(msgId);
                    mList.remove(postion);
                    checkData();
                    mViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                Snackbar.make(findViewById(R.id.layout_main), "取消收藏失败!", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * 发送消息前，需要检测网络连接状态，网络不可用，不能发送
     * 每条消息发送前，需要检测，语音和小视频录制之前，仍需要检测
     * */
    public boolean checkNetConnectStatus() {
        boolean isOk;
        if (!NetUtil.isNetworkConnected()) {
            ToastUtil.show(this, "网络连接不可用，请稍后重试");
            isOk = false;
        } else {
            isOk = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.CONN_STATUS).get4Json(Boolean.class);
            if (!isOk) {
                ToastUtil.show(this, "连接已断开，请稍后再试");
            }
        }
        return isOk;
    }

    /**
     * 开始对当前已有数据查询搜索
     */
    private void taskSearch() {
        searchCollectList.clear();//及时清空，重新搜索
        key = bindingView.edtSearch.getText().toString();
        if (key.length() <= 0) {
            return;
        }
        //不同类型仅判断标题来过滤搜索结果
        for (int i = 0; i < mList.size(); i++) {
            //找到每一条收藏消息，搜索用户名/群名/类型，含有关键字的被保存到searchCollectList
            CollectionInfo collectionInfo = mList.get(i);
            if (!TextUtils.isEmpty(collectionInfo.getFromUsername()) && collectionInfo.getFromUsername().contains(key)) {
                searchCollectList.add(collectionInfo);
            } else if (!TextUtils.isEmpty(collectionInfo.getFromGroupName()) && collectionInfo.getFromGroupName().contains(key)) {
                searchCollectList.add(collectionInfo);
            } else {
                //图片 视频 表情 不方便搜索
                if (!TextUtils.isEmpty(collectionInfo.getData())) {
                    MsgAllBean bean = new Gson().fromJson(collectionInfo.getData(), MsgAllBean.class);
                    if (collectionInfo.getType() == ChatEnum.EMessageType.TEXT) { //文字
                        if (bean.getChat() != null) {
                            if (!TextUtils.isEmpty(bean.getChat().getMsg())) {
                                if (bean.getChat().getMsg().contains(key)) {
                                    searchCollectList.add(collectionInfo);
                                }
                            }
                        }
                    } else if (collectionInfo.getType() == ChatEnum.EMessageType.LOCATION) { //位置
                        if (bean.getLocationMessage() != null) {
                            if (!TextUtils.isEmpty(bean.getLocationMessage().getAddress())) {
                                if (bean.getLocationMessage().getAddress().contains(key)) {
                                    searchCollectList.add(collectionInfo);
                                }
                            }
                            if (!TextUtils.isEmpty(bean.getLocationMessage().getAddressDescribe())) {
                                if (bean.getLocationMessage().getAddressDescribe().contains(key)) {
                                    searchCollectList.add(collectionInfo);
                                }
                            }
                        }
                    } else if (collectionInfo.getType() == ChatEnum.EMessageType.AT) {
                        if (bean.getAtMessage() != null) {
                            if (!TextUtils.isEmpty(bean.getAtMessage().getMsg())) {
                                if (bean.getAtMessage().getMsg().contains(key)) {
                                    searchCollectList.add(collectionInfo);
                                }
                            }
                        }
                    } else if (collectionInfo.getType() == ChatEnum.EMessageType.FILE) {
                        if (bean.getSendFileMessage() != null) {
                            if (!TextUtils.isEmpty(bean.getSendFileMessage().getFile_name())) {
                                if (bean.getSendFileMessage().getFile_name().contains(key)) {
                                    searchCollectList.add(collectionInfo);
                                }
                            }
                        }
                    }
                }
            }
        }
        mList.clear();
        mList.addAll(searchCollectList);
        checkData();
        mViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==CANCEL_COLLECT){
                if(data.getIntExtra("cancel_collect_position",-1) != (-1)){
                    int cancelPosition = data.getIntExtra("cancel_collect_position",-1);
                    if (mList.get(cancelPosition) != null) {
                        if (mList.get(cancelPosition).getId() != 0L) {
                            httpCancelCollect(mList.get(cancelPosition).getId(), cancelPosition);
                        }
                    }
                }
            }
        }
    }

    private SpannableString getSpan(String msg) {
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        SpannableString spannableString = null;
        if (fontSize != null) {
            spannableString = ExpressionUtil.getExpressionString(getContext(), fontSize.intValue(), msg);
        } else {
            spannableString = ExpressionUtil.getExpressionString(getContext(), ExpressionUtil.DEFAULT_SIZE, msg);
        }
        return spannableString;
    }
}
