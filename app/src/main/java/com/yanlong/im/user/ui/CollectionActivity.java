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
import com.cx.sharelib.message.CxMediaMessage;
import com.example.nim_lib.ui.BaseBindActivity;
import com.google.gson.Gson;
import com.hm.cxpay.utils.DateUtils;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.AtMessage;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.CollectAtMessage;
import com.yanlong.im.chat.bean.CollectChatMessage;
import com.yanlong.im.chat.bean.CollectImageMessage;
import com.yanlong.im.chat.bean.CollectLocationMessage;
import com.yanlong.im.chat.bean.CollectSendFileMessage;
import com.yanlong.im.chat.bean.CollectShippedExpressionMessage;
import com.yanlong.im.chat.bean.CollectVideoMessage;
import com.yanlong.im.chat.bean.CollectVoiceMessage;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.LocationMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.SendFileMessage;
import com.yanlong.im.chat.bean.ShippedExpressionMessage;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.forward.MoreSessionBean;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.chat.ui.view.AlertForward;
import com.yanlong.im.databinding.ActivityCollectionBinding;
import com.yanlong.im.databinding.ItemCollectionViewBinding;
import com.yanlong.im.location.LocationUtils;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.CollectionInfo;
import com.yanlong.im.utils.CommonUtils;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.audio.AudioPlayManager;
import com.yanlong.im.utils.audio.IVoicePlayListener;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.view.face.FaceView;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.FileUtils;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    public static final int FROM_DEFAULT = 0;//来自首页我的(默认来源)
    public static final int FROM_CHAT = 1;//来自聊天面板收藏
    private int fromWhere = 0;//从哪里跳转过来

    //点击转发相关数据
    private String groupHead = "";
    private String groupId = "";
    private String groupName = "";
    private String userHead = "";
    private long userId = -1L;
    private String userName = "";
    private boolean isGroup = false;//是单聊还是群聊

    //加载布局
    @Override
    protected int setView() {
        return R.layout.activity_collection;
    }

    //初始化
    @Override
    protected void init(Bundle savedInstanceState) {
        getIntentData();
        mViewAdapter = new CommonRecyclerViewAdapter<CollectionInfo, ItemCollectionViewBinding>(this, R.layout.item_collection_view) {
            //item显示
            @Override
            public void bind(ItemCollectionViewBinding binding, CollectionInfo memberUser,
                             int position, RecyclerView.ViewHolder viewHolder) {

                if (mList != null && mList.size() > 0) {
                    CollectionInfo collectionInfo = mList.get(position);
                    if (!TextUtils.isEmpty(collectionInfo.getData())) {
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
                        //不同类型显示，考虑和IOS兼容，类型完全按protobuf规则制定，需要转换一下为安卓自己的类型
                        switch (CommonUtils.transformMsgType(collectionInfo.getType())) {
                            case ChatEnum.EMessageType.TEXT: //文字
                                binding.tvContent.setVisibility(VISIBLE);//显示文字相关布局，隐藏其他类型相关布局
                                binding.layoutVoice.setVisibility(GONE);
                                binding.layoutPic.setVisibility(GONE);
                                binding.layoutFile.setVisibility(GONE);
                                binding.layoutLocation.setVisibility(GONE);
                                CollectChatMessage bean1 = new Gson().fromJson(collectionInfo.getData(), CollectChatMessage.class);
                                if (bean1 != null) {
                                    if (!TextUtils.isEmpty(bean1.getMsg())) {
                                        binding.tvContent.setText(getSpan(bean1.getMsg()));
                                    } else {
                                        binding.tvContent.setText("");
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
                                CollectImageMessage bean2 = new Gson().fromJson(collectionInfo.getData(), CollectImageMessage.class);
                                if (bean2 != null) { //显示预览图或者缩略图
                                    if (!TextUtils.isEmpty(bean2.getPreview())) {
                                        Glide.with(CollectionActivity.this).load(bean2.getPreview())
                                                .apply(GlideOptionsUtil.headImageOptions()).into(binding.ivPic);
                                    } else if (!TextUtils.isEmpty(bean2.getThumbnail())) {
                                        Glide.with(CollectionActivity.this).load(bean2.getThumbnail())
                                                .apply(GlideOptionsUtil.headImageOptions()).into(binding.ivPic);
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
                                CollectShippedExpressionMessage bean3 = new Gson().fromJson(collectionInfo.getData(), CollectShippedExpressionMessage.class);
                                if (bean3 != null) {
                                    if (!TextUtils.isEmpty(bean3.getExpression())) {
                                        Glide.with(CollectionActivity.this).load(Integer.parseInt(FaceView.map_FaceEmoji.get(bean3.getExpression()).toString())).apply(GlideOptionsUtil.headImageOptions()).into(binding.ivPic);
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
                                CollectVideoMessage bean4 = new Gson().fromJson(collectionInfo.getData(), CollectVideoMessage.class);
                                if (bean4 != null) {
                                    if (!TextUtils.isEmpty(bean4.getVideoBgURL())) {
                                        Glide.with(CollectionActivity.this).load(bean4.getVideoBgURL())
                                                .apply(GlideOptionsUtil.headImageOptions()).into(binding.ivPic);
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.VOICE: //语音
                                binding.tvContent.setVisibility(GONE);//显示语音相关布局，隐藏其他类型相关布局
                                binding.layoutVoice.setVisibility(VISIBLE);
                                binding.layoutPic.setVisibility(GONE);
                                binding.layoutFile.setVisibility(GONE);
                                binding.layoutLocation.setVisibility(GONE);
                                CollectVoiceMessage bean5 = new Gson().fromJson(collectionInfo.getData(), CollectVoiceMessage.class);
                                if (bean5 != null) {
                                    if (bean5.getVoiceDuration() != 0) {
                                        binding.tvVoiceTime.setText(DateUtils.getSecondFormatTime(Long.valueOf(bean5.getVoiceDuration() + "")));
                                    }
//                                        VoiceMessage vm = bean.getVoiceMessage();
//                                        String url = bean.isMe() ? vm.getLocalUrl() : vm.getUrl();
//                                        binding.voiceView.init(true,vm.getTime(), true, AudioPlayManager.getInstance().isPlay(Uri.parse(url)), vm.getPlayStatus());
                                }
                                break;
                            case ChatEnum.EMessageType.LOCATION: //位置消息
                                binding.tvContent.setVisibility(GONE);//显示位置相关布局，隐藏其他类型相关布局
                                binding.layoutVoice.setVisibility(GONE);
                                binding.layoutPic.setVisibility(GONE);
                                binding.layoutFile.setVisibility(GONE);
                                binding.layoutLocation.setVisibility(VISIBLE);
                                CollectLocationMessage bean6 = new Gson().fromJson(collectionInfo.getData(), CollectLocationMessage.class);
                                if (bean6 != null) {
                                    if (!TextUtils.isEmpty(bean6.getAddr())) {
                                        binding.tvLocationName.setText(bean6.getAddr());
                                    }
                                    if (!TextUtils.isEmpty(bean6.getAddressDesc())) {
                                        binding.tvLocationDesc.setText(bean6.getAddressDesc());
                                    }
                                    if (!TextUtils.isEmpty(bean6.getImg())) {
                                        Glide.with(CollectionActivity.this)
                                                .asBitmap()
                                                .load(bean6.getImg())
                                                .into(new SimpleTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                        binding.ivLocation.setImageBitmap(resource);
                                                    }
                                                });
                                    } else {
                                        String baiduImageUrl = LocationUtils.getLocationUrl(bean6.getLat(), bean6.getLon());
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
                                break;
                            case ChatEnum.EMessageType.AT: //艾特@消息
                                binding.tvContent.setVisibility(VISIBLE);//显示@消息相关布局，隐藏其他类型相关布局
                                binding.layoutVoice.setVisibility(GONE);
                                binding.layoutPic.setVisibility(GONE);
                                binding.layoutFile.setVisibility(GONE);
                                binding.layoutLocation.setVisibility(GONE);
                                CollectAtMessage bean7 = new Gson().fromJson(collectionInfo.getData(), CollectAtMessage.class);
                                if (bean7 != null) {
                                    if (!TextUtils.isEmpty(bean7.getMsg())) {
                                        binding.tvContent.setText(getSpan(bean7.getMsg()));
                                    } else {
                                        binding.tvContent.setText("");
                                    }
                                }
                                break;
                            case ChatEnum.EMessageType.FILE: //文件
                                binding.tvContent.setVisibility(GONE);//显示文件相关布局，隐藏其他类型相关布局
                                binding.layoutVoice.setVisibility(GONE);
                                binding.layoutPic.setVisibility(GONE);
                                binding.layoutFile.setVisibility(VISIBLE);
                                binding.layoutLocation.setVisibility(GONE);
                                CollectSendFileMessage bean8 = new Gson().fromJson(collectionInfo.getData(), CollectSendFileMessage.class);
                                if (bean8 != null) {
                                    if (!TextUtils.isEmpty(bean8.getFileName())) {
                                        binding.tvFileName.setText(bean8.getFileName());
                                    }
                                    if (!TextUtils.isEmpty(bean8.getFileFormat())) {
                                        String fileFormat = bean8.getFileFormat();
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
                                    if (bean8.getFileSize() != 0L) {
                                        binding.tvFileSize.setText(FileUtils.getFileSizeString(bean8.getFileSize()));
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
                if (fromWhere == CollectionActivity.FROM_DEFAULT) {
                    showPop(view, bean.getMsgId(), position);
                    return true;
                } else {
                    return true;
                }
            }
        });
        binding.layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromWhere == CollectionActivity.FROM_DEFAULT) {
                    Intent intent = new Intent(CollectionActivity.this, CollectDetailsActivity.class);
                    intent.putExtra("json_data", new Gson().toJson(bean));//转换成json字符串再传过去
                    intent.putExtra("position", position);//位置
                    startActivityForResult(intent, CANCEL_COLLECT);
                } else {
                    //直接转发收藏消息到当前群或用户
                    showTransDialog(bean, CommonUtils.transformMsgType(bean.getType()));
                }
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
            if (CommonUtils.transformMsgType(mList.get(postion).getType()) == ChatEnum.EMessageType.VOICE) {
                ToastUtil.show("语音消息无法转发");
            } else {
                if (NetUtil.isNetworkConnected()) {
                    startActivity(new Intent(context, MsgForwardActivity.class)
                            .putExtra(MsgForwardActivity.AGM_JSON, new Gson().toJson(mList.get(postion))).putExtra("from_collect", true));
//                Intent intent = MsgForwardActivity.newIntent(this, ChatEnum.EForwardMode.DEFAULT, new Gson().toJson(mList.get(postion)));//传collectinfo
//                startActivity(intent);
                } else {
                    ToastUtil.show("请检查网络连接是否正常");
                }
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
                    timeSortList(list);
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
                    ToastUtil.show(CollectionActivity.this, "删除成功");
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
                ToastUtil.show(CollectionActivity.this, "删除失败");
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
            //gid不为空，则必定会显示群昵称，从群昵称查
            if (!TextUtils.isEmpty(collectionInfo.getFromGid()) && collectionInfo.getFromGroupName().contains(key)) {
                searchCollectList.add(collectionInfo);
                //gid为空，则必定会显示用户名，从用户名查
            } else if (TextUtils.isEmpty(collectionInfo.getFromGid()) && collectionInfo.getFromUsername().contains(key)) {
                searchCollectList.add(collectionInfo);
            } else {
                //图片 视频 表情 不方便搜索
                if (!TextUtils.isEmpty(collectionInfo.getData())) {
                    int msgType = CommonUtils.transformMsgType(collectionInfo.getType());
                    if (msgType == ChatEnum.EMessageType.TEXT) { //文字
                        CollectChatMessage bean1 = new Gson().fromJson(collectionInfo.getData(), CollectChatMessage.class);
                        if (bean1 != null) {
                            if (bean1.getMsg().contains(key)) {
                                searchCollectList.add(collectionInfo);
                            }
                        }
                    } else if (msgType == ChatEnum.EMessageType.LOCATION) { //位置
                        CollectLocationMessage bean2 = new Gson().fromJson(collectionInfo.getData(), CollectLocationMessage.class);
                        if (bean2 != null) {
                            if (!TextUtils.isEmpty(bean2.getAddr())) {
                                if (bean2.getAddr().contains(key)) {
                                    searchCollectList.add(collectionInfo);
                                }
                            }
                            if (!TextUtils.isEmpty(bean2.getAddressDesc())) {
                                if (bean2.getAddressDesc().contains(key)) {
                                    searchCollectList.add(collectionInfo);
                                }
                            }
                        }
                    } else if (msgType == ChatEnum.EMessageType.AT) {
                        CollectAtMessage bean3 = new Gson().fromJson(collectionInfo.getData(), CollectAtMessage.class);
                        if (bean3 != null) {
                            if (!TextUtils.isEmpty(bean3.getMsg())) {
                                if (bean3.getMsg().contains(key)) {
                                    searchCollectList.add(collectionInfo);
                                }
                            }
                        }
                    } else if (msgType == ChatEnum.EMessageType.FILE) {
                        CollectSendFileMessage bean4 = new Gson().fromJson(collectionInfo.getData(), CollectSendFileMessage.class);
                        if (bean4 != null) {
                            if (!TextUtils.isEmpty(bean4.getFileName())) {
                                if (bean4.getFileName().contains(key)) {
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
        if (resultCode == RESULT_OK) {
            if (requestCode == CANCEL_COLLECT) {
                if (data.getIntExtra("cancel_collect_position", -1) != (-1)) {
                    int cancelPosition = data.getIntExtra("cancel_collect_position", -1);
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

    //按重新时间排序(后端没有处理，改为前端自行排序)
    private void timeSortList(List<CollectionInfo> sortList) {
        Collections.sort(sortList, new Comparator<CollectionInfo>() {
            @Override
            public int compare(CollectionInfo o1, CollectionInfo o2) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date dt1 = format.parse(TimeToString.YYYY_MM_DD_HH_MM_SS(Long.parseLong(o1.getCreateTime())));
                    Date dt2 = format.parse(TimeToString.YYYY_MM_DD_HH_MM_SS(Long.parseLong(o2.getCreateTime())));
                    if (dt1.getTime() > dt2.getTime()) {
                        return -1;
                    } else if (dt1.getTime() < dt2.getTime()) {
                        return 1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }


    //转发
    private void showTransDialog(CollectionInfo info, int type) {
        if (info == null) {
            return;
        }
        String txt = "";
        String imageUrl = "";
        String avatar = "";
        String name = "";

        AlertForward alertForward = new AlertForward();
        if (type == ChatEnum.EMessageType.TEXT) {//文字
            CollectChatMessage bean1 = new Gson().fromJson(info.getData(), CollectChatMessage.class);
            txt = bean1.getMsg() == null ? "" : bean1.getMsg();
        } else if (type == ChatEnum.EMessageType.IMAGE) {//图片
            CollectImageMessage bean2 = new Gson().fromJson(info.getData(), CollectImageMessage.class);
            imageUrl = bean2.getThumbnail() == null ? "" : bean2.getThumbnail();
        } else if (type == ChatEnum.EMessageType.AT) {//AT
            CollectAtMessage bean3 = new Gson().fromJson(info.getData(), CollectAtMessage.class);
            txt = bean3.getMsg() == null ? "" : bean3.getMsg();
        } else if (type == ChatEnum.EMessageType.MSG_VIDEO) {//视频
            CollectVideoMessage bean4 = new Gson().fromJson(info.getData(), CollectVideoMessage.class);
            imageUrl = bean4.getVideoBgURL() == null ? "" : bean4.getVideoBgURL();
        } else if (type == ChatEnum.EMessageType.LOCATION) {//位置
            CollectLocationMessage bean5 = new Gson().fromJson(info.getData(), CollectLocationMessage.class);
            txt = bean5.getAddr() == null ? "" : "[位置]" + bean5.getAddr();
            imageUrl = LocationUtils.getLocationUrl(bean5.getLat(), bean5.getLon());
        } else if (type == ChatEnum.EMessageType.SHIPPED_EXPRESSION) {//大表情
            CollectShippedExpressionMessage bean6 = new Gson().fromJson(info.getData(), CollectShippedExpressionMessage.class);
            imageUrl = bean6.getExpression() == null ? "" : bean6.getExpression();
        } else if (type == ChatEnum.EMessageType.FILE) {//文件
            CollectSendFileMessage bean7 = new Gson().fromJson(info.getData(), CollectSendFileMessage.class);
            txt = bean7.getFileName() == null ? "" : "[文件]" + bean7.getFileName();
        }//todo 回复暂未添加
        if (isGroup) {
            avatar = groupHead;
            name = groupName;
        } else {
            avatar = userHead;
            name = userName;
        }
        alertForward.init(CollectionActivity.this, type, avatar, name, txt, imageUrl, "发送", groupId, new AlertForward.Event() {
            @Override
            public void onON() {

            }

            @Override
            public void onYes(String content) {
                //收藏转发
                send(info, content, userId, groupId);
            }
        });
        alertForward.show();
    }

    //获取传过来的数据
    private void getIntentData() {
        fromWhere = getIntent().getIntExtra("from", 0);
        isGroup = getIntent().getBooleanExtra("is_group", false);
        groupHead = getIntent().getStringExtra("group_head");
        groupId = getIntent().getStringExtra("group_id");
        groupName = getIntent().getStringExtra("group_name");
        userHead = getIntent().getStringExtra("user_head");
        userId = getIntent().getLongExtra("user_id", -1L);
        userName = getIntent().getStringExtra("user_name");
    }

    //处理逻辑-收藏转发
    private void send(CollectionInfo collectionInfo, String content, long toUid, String toGid) {
        int type = CommonUtils.transformMsgType(collectionInfo.getType());
        if (type == ChatEnum.EMessageType.TEXT) {//转换文字
            CollectChatMessage bean1 = new Gson().fromJson(collectionInfo.getData(), CollectChatMessage.class);
            ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), bean1.getMsg());
            MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, type, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
            if (allBean != null) {
                sendMessage(allBean);
            }
            sendLeaveMessage(content, toUid, toGid);
        } else if (type == ChatEnum.EMessageType.IMAGE) {
            CollectImageMessage bean2 = new Gson().fromJson(collectionInfo.getData(), CollectImageMessage.class);
            CollectImageMessage imagesrc = bean2;
            if (collectionInfo.getFromUid() == UserAction.getMyId().longValue()) {
                imagesrc.setReadOrigin(true);
            }
            ImageMessage imageMessage = SocketData.createImageMessage(SocketData.getUUID(), imagesrc.getOrigin(), imagesrc.getPreview(), imagesrc.getThumbnail(), imagesrc.getWidth(), imagesrc.getHeight(), !TextUtils.isEmpty(imagesrc.getOrigin()), imagesrc.isReadOrigin(), imagesrc.getSize());
            MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, type, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), imageMessage);
            if (allBean != null) {
                sendMessage(allBean);
            }
            sendLeaveMessage(content, toUid, toGid);

        } else if (type == ChatEnum.EMessageType.AT) {
            CollectAtMessage bean3 = new Gson().fromJson(collectionInfo.getData(), CollectAtMessage.class);

            ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), bean3.getMsg());
            MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
            if (allBean != null) {
                sendMessage(allBean);
            }
            sendLeaveMessage(content, toUid, toGid);

        } else if (type == ChatEnum.EMessageType.MSG_VIDEO) {
            CollectVideoMessage bean4 = new Gson().fromJson(collectionInfo.getData(), CollectVideoMessage.class);
            CollectVideoMessage video = bean4;
            VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), video.getVideoBgURL(), video.getVideoURL(), video.getVideoDuration(), video.getWidth(), video.getHeight(), video.isReadOrigin());
            MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, type, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), videoMessage);
            if (allBean != null) {
                sendMessage(allBean);
            }
            sendLeaveMessage(content, toUid, toGid);

        } else if (type == ChatEnum.EMessageType.LOCATION) {
            CollectLocationMessage bean5 = new Gson().fromJson(collectionInfo.getData(), CollectLocationMessage.class);
            CollectLocationMessage location = bean5;
            //收藏用的不多，手动创建位置消息
            LocationMessage locationMessage = new LocationMessage();
            locationMessage.setMsgId(SocketData.getUUID());
            locationMessage.setLatitude(location.getLat());
            locationMessage.setLongitude(location.getLon());
            locationMessage.setImg(location.getImg());
            locationMessage.setAddress(location.getAddr());
            locationMessage.setAddressDescribe(location.getAddressDesc());
            MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, type, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), locationMessage);
            if (allBean != null) {
                sendMessage(allBean);
            }
            sendLeaveMessage(content, toUid, toGid);
        } else if (type == ChatEnum.EMessageType.SHIPPED_EXPRESSION) {
            CollectShippedExpressionMessage bean6 = new Gson().fromJson(collectionInfo.getData(), CollectShippedExpressionMessage.class);
            ShippedExpressionMessage message = SocketData.createFaceMessage(SocketData.getUUID(), bean6.getExpression());
            MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.SHIPPED_EXPRESSION, ChatEnum.ESendStatus.NORMAL,
                    SocketData.getFixTime(), message);
            if (allBean != null) {
                sendMessage(allBean);
            }
            sendLeaveMessage(content, toUid, toGid);
        } else if (type == ChatEnum.EMessageType.FILE) { //转发文件消息
            CollectSendFileMessage bean8 = new Gson().fromJson(collectionInfo.getData(), CollectSendFileMessage.class);
            //文件分为两种情况：转发他人/自己转发自己，转发他人的文件需要下载，转发自己的文件直接从本地查找
            boolean isFromOther;
            //如果是自己转发自己的文件
            if (collectionInfo.getFromUid() == UserAction.getMyId().longValue()) {
                isFromOther = false;
            } else {
                isFromOther = true;
            }
            SendFileMessage fileMessage = SocketData.createFileMessage(SocketData.getUUID(), bean8.getCollectLocalPath(), bean8.getFileURL(), bean8.getFileName(), bean8.getFileSize(), bean8.getFileFormat(), isFromOther);
            MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, type, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), fileMessage);
            if (allBean != null) {
                sendMessage(allBean);
            }
            sendLeaveMessage(content, toUid, toGid);
        }
    }

    private void sendMessage(MsgAllBean msgAllBean) {
        SocketData.sendAndSaveMessage(msgAllBean);
        ToastUtil.show(this, getResources().getString(R.string.forward_success));
        finish();
    }

    /*
     * 发送留言消息
     * */
    private void sendLeaveMessage(String content, long toUid, String toGid) {
        if (StringUtil.isNotNull(content)) {
            ChatMessage chat = SocketData.createChatMessage(SocketData.getUUID(), content);
            MsgAllBean messageBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chat);
            if (messageBean != null) {
                sendMessage(messageBean);
            }
        }
    }

}
