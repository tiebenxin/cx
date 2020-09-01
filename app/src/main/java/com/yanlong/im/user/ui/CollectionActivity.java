package com.yanlong.im.user.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.nim_lib.ui.BaseBindActivity;
import com.google.gson.Gson;
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.hm.cxpay.utils.DateUtils;
import com.yanlong.im.R;
import com.yanlong.im.adapter.AdapterPopMenu;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
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
import com.yanlong.im.chat.bean.OfflineDelete;
import com.yanlong.im.chat.bean.SendFileMessage;
import com.yanlong.im.chat.bean.ShippedExpressionMessage;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.chat.ui.view.AlertForward;
import com.yanlong.im.databinding.ActivityCollectionBinding;
import com.yanlong.im.databinding.ItemCollectionViewBinding;
import com.yanlong.im.location.LocationUtils;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.CollectionInfo;
import com.yanlong.im.utils.ChatBitmapCache;
import com.yanlong.im.utils.CommonUtils;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.UserUtil;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketUtil;
import com.yanlong.im.view.face.FaceView;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.inter.SwipeLayoutOpenCloseListener;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.FileUtils;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.ScreenUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import me.kareluo.ui.OptionMenu;
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
    private MsgDao msgDao = new MsgDao();
    private MsgAction msgAction = new MsgAction();
    private String key = "";//搜索关键字
    public static final int CANCEL_COLLECT = 0;//取消收藏

    public static final int FROM_DEFAULT = 0;//来自首页我的(默认来源)
    public static final int FROM_CHAT = 1;//来自聊天面板收藏
    private int fromWhere = 0;//从哪里跳转过来 0 我的收藏,默认 1 聊天面板收藏项

    //点击转发相关数据
    private String groupHead = "";
    private String groupId = "";
    private String groupName = "";
    private String userHead = "";
    private long userId = -1L;
    private String userName = "";
    private boolean isGroup = false;//是单聊还是群聊
    private List<CollectionInfo> LocalList;//本地离线收藏列表数据

    private boolean inEditMode = false;//是否处于编辑模式 (即多选删除模式，此模式下，单击只响应选中，不再响应长按，回退恢复默认模式)
    private List<CollectionInfo> needDeleteData;//需要删除的指定收藏集
    private CommonSelectDialog.Builder builder;
    private CommonSelectDialog dialogOne;//确认删除弹框
    private boolean isVertical = true;//竖图(true)还是横图(false)  默认竖图

    private RecyclerView mRecyclerBubble;

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
                    //是否显示编辑按钮
                    if(collectionInfo.isShowEdit()==true){
                        binding.ivCheck.setVisibility(VISIBLE);
                        //是否选中
                        if(collectionInfo.isChecked()==true){
                            binding.ivCheck.setImageResource(R.drawable.ic_select);
                        }else {
                            binding.ivCheck.setImageResource(R.drawable.ic_unselect);
                        }
                        binding.swipeLayout.setSwipeEnable(false);//TODO 新增侧滑，默认允许，编辑模式禁止响应
                    }else {
                        binding.ivCheck.setVisibility(GONE);
                        binding.swipeLayout.setSwipeEnable(true);
                    }
                    if (!TextUtils.isEmpty(collectionInfo.getData())) {
                        //显示用户名或群名
                        if (!TextUtils.isEmpty(collectionInfo.getFromGroupName())) {
                            binding.tvName.setText(collectionInfo.getFromGroupName());
                        } else if (!TextUtils.isEmpty(collectionInfo.getFromUsername())) {
                            binding.tvName.setText(collectionInfo.getFromUsername());
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
                                    String thumbnail = bean2.getThumbnailShow();
                                    //缓存
                                    Bitmap localBitmap = ChatBitmapCache.getInstance().getAndGlideCache(thumbnail);
                                    if(localBitmap==null){
                                        RequestOptions mRequestOptions = RequestOptions.centerInsideTransform()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .skipMemoryCache(false)
                                                .centerCrop();
                                        Glide.with(getContext())
                                                .asBitmap()
                                                .load(thumbnail)
                                                .apply(mRequestOptions)
                                                .into(new SimpleTarget<Bitmap>() {
                                                    @Override
                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                        binding.ivPic.setImageBitmap(resource);
                                                    }
                                                });
                                    }else{
                                        binding.ivPic.setImageBitmap(localBitmap);
                                    }



//                                    if(NetUtil.isNetworkConnected()){
//                                        if (!TextUtils.isEmpty(bean2.getPreview())) {
//                                            Glide.with(CollectionActivity.this).load(bean2.getPreview())
//                                                    .apply(GlideOptionsUtil.defaultImageOptions()).into(binding.ivPic);
//                                        } else if (!TextUtils.isEmpty(bean2.getThumbnail())) {
//                                            Glide.with(CollectionActivity.this).load(bean2.getThumbnail())
//                                                    .apply(GlideOptionsUtil.defaultImageOptions()).into(binding.ivPic);
//                                        }
//                                    }else {
//                                        Bitmap localBitmap = ChatBitmapCache.getInstance().getAndGlideCache(thumbnail);
//                                        if (localBitmap == null) {
//                                            Glide.with(CollectionActivity.this)
//                                                    .asBitmap()
//                                                    .load(thumbnail)
//                                                    .apply(GlideOptionsUtil.defaultImageOptions())
//                                                    .into(binding.ivPic);
//                                        } else {
//                                            binding.ivPic.setImageBitmap(localBitmap);
//                                        }

//                                    }
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
                                        Glide.with(CollectionActivity.this).load(Integer.parseInt(FaceView.map_FaceEmoji.get(bean3.getExpression()).toString())).apply(GlideOptionsUtil.defaultImageOptions()).into(binding.ivPic);
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
                                    String bgUrl = bean4.getVideoBgURL();
                                    //有网情况走网络请求，无网情况拿缓存
                                    if(NetUtil.isNetworkConnected()){
                                        if (!TextUtils.isEmpty(bgUrl)) {
                                            Glide.with(CollectionActivity.this).load(bgUrl)
                                                    .apply(GlideOptionsUtil.defaultImageOptions()).into(binding.ivPic);
                                        }
                                    }else {
                                        Bitmap localBitmap = ChatBitmapCache.getInstance().getAndGlideCache(bgUrl);
                                        if (localBitmap == null) {
                                            Glide.with(CollectionActivity.this)
                                                    .asBitmap()
                                                    .load(bgUrl)
                                                    .apply(GlideOptionsUtil.defaultImageOptions())
                                                    .into(binding.ivPic);
                                        } else {
                                            binding.ivPic.setImageBitmap(localBitmap);
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
                                    String locationImg = bean6.getImg();
                                    if (!TextUtils.isEmpty(locationImg)) {
                                        //有网情况走网络请求，无网情况拿缓存
                                        if(NetUtil.isNetworkConnected()){
                                            Glide.with(CollectionActivity.this)
                                                    .asBitmap()
                                                    .load(locationImg)
                                                    .into(new SimpleTarget<Bitmap>() {
                                                        @Override
                                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                            binding.ivLocation.setImageBitmap(resource);
                                                        }
                                                    });
                                        }else {
                                            Bitmap localBitmap = ChatBitmapCache.getInstance().getAndGlideCache(bean6.getImg());
                                            if(localBitmap==null){
                                                Glide.with(CollectionActivity.this)
                                                        .asBitmap()
                                                        .load(locationImg)
                                                        .into(new SimpleTarget<Bitmap>() {
                                                            @Override
                                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                                binding.ivLocation.setImageBitmap(resource);
                                                            }
                                                        });
                                            }else {
                                                binding.ivLocation.setImageBitmap(localBitmap);
                                            }
                                        }
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
                    //编辑模式，不响应
                    if(inEditMode){
                        //Do Nothing
                    }else {
                        //默认模式，长按弹框
                        showPop(view, position);
                    }
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
                    //编辑模式，点击子项仅支持选中
                    if(inEditMode){
                        //收录需要删除的收藏消息集
                        if(needDeleteData==null){
                            needDeleteData = new ArrayList<>();
                        }
                        //勾选效果
                        if(bean.isChecked()==false){
                            bean.setChecked(true);
                            binding.ivCheck.setImageResource(R.drawable.ic_select);
                            if(!needDeleteData.contains(bean)){
                                needDeleteData.add(bean);
                            }
                        }else {
                            bean.setChecked(false);
                            binding.ivCheck.setImageResource(R.drawable.ic_unselect);
                            if(needDeleteData.contains(bean)){
                                needDeleteData.remove(bean);
                            }
                        }
                    }else {
                        //默认模式，点击子项跳转到收藏详情
                        Intent intent = new Intent(CollectionActivity.this, CollectDetailsActivity.class);
                        intent.putExtra("json_data", new Gson().toJson(bean));//转换成json字符串再传过去
                        intent.putExtra("position", position);//位置
                        startActivityForResult(intent, CANCEL_COLLECT);
                    }
                } else {
                    //直接转发收藏消息到当前群或用户
                    showTransDialog(bean, CommonUtils.transformMsgType(bean.getType()));
                }
            }
        });
        binding.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.ivDelete.setVisibility(View.INVISIBLE);
                binding.tvSureDelete.setVisibility(View.VISIBLE);
            }
        });
        binding.tvSureDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.get(position) != null) {
                    httpCancelCollect(mList.get(position).getId(), position,mList.get(position).getMsgId());
                }
            }
        });
        //监听侧滑开关状态
        binding.swipeLayout.setOpenOrCloseListenr(new SwipeLayoutOpenCloseListener() {
            @Override
            public void changeStatus(boolean isOpen) { //true 开 false 关
                if(isOpen==false){
                    //每次关闭初始化(垃圾桶/确认删除)状态
                    binding.ivDelete.setVisibility(View.VISIBLE);
                    binding.tvSureDelete.setVisibility(View.INVISIBLE);
                }
            }
        });
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
        bindingView.edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //关闭软键盘
                InputUtil.hideKeyboard(CollectionActivity.this);
                return false;
            }
        });
        //如存在底部垃圾桶标识，允许批量删除
        bindingView.ivDeleteMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //批量删除
                //有数据则弹框，无数据直接退出编辑模式
                if(needDeleteData!=null && needDeleteData.size()>0){
                    //弹框初始化
                    if(builder==null){
                        builder = new CommonSelectDialog.Builder(CollectionActivity.this);
                    }
                    showDialog();
                }else {
                    //关闭编辑模式
                    switchEditMode(false);
                }
            }
        });
    }

    //加载数据
    @Override
    protected void loadData() {
        //1 有网获取收藏列表
        if (checkNetConnectStatus()) {
            httpGetCollectList();
        }else {
            //2 无网加载本地收藏列表
            LocalList = msgDao.getAllCollections();
            if(LocalList!=null && LocalList.size()>0){
                timeSortList(LocalList);
                mList.clear();
                mList.addAll(LocalList);
                mViewAdapter.setData(mList);
                allCollectList.addAll(LocalList);
            }
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
    private TextView mTxtView1;//转发
    private TextView mTxtView2;//删除
    private TextView mTxtView3;//更多
    private View layoutContent;
    private View mRootView;
    private RelativeLayout mRlDown, mRlUp;

    /**
     * 初始化弹出项
     */
    private List<OptionMenu> initMenus() {
        List<OptionMenu> menus = new ArrayList<>();
        menus.add(new OptionMenu("转发"));
        menus.add(new OptionMenu("删除"));
        menus.add(new OptionMenu("更多"));
        return menus;
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        mRootView = getLayoutInflater().inflate(R.layout.view_chat_pop, null, false);
        //获取自身的长宽高
        mRootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWidth = mRootView.getMeasuredWidth();
        popupHeight = mRootView.getMeasuredHeight();

        mRlUp = mRootView.findViewById(R.id.rl_up);
        mImgTriangleUp = mRootView.findViewById(R.id.img_triangle_up);
        mRlDown = mRootView.findViewById(R.id.rl_down);
        mRecyclerBubble = mRootView.findViewById(R.id.recycler_bubble);
    }

    /***
     * 长按的气泡处理
     * @param v
     */
    private void showPop(View v, int postion) {
        try {
            List<OptionMenu> menus = initMenus();
            AdapterPopMenu adapterPopMenu = new AdapterPopMenu(initMenus(), this);
            int spanCount;
            if (menus.size() == 1) {
                spanCount = 1;
            } else if (menus.size() == 2) {
                spanCount = 2;
            } else if (menus.size() == 3) {
                spanCount = 3;
            } else {
                spanCount = 4;
            }
            mRecyclerBubble.setLayoutManager(new GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false));
            mRecyclerBubble.setAdapter(adapterPopMenu);
            adapterPopMenu.setListener(new AdapterPopMenu.IMenuClickListener() {
                @Override
                public void onClick(OptionMenu menu) {
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                    if(menu.getTitle().equals("转发")){
                        if (mPopupWindow != null) {
                            mPopupWindow.dismiss();
                        }
                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                            ToastUtil.show(getResources().getString(R.string.user_disable_message));
                            return;
                        }
                        if (CommonUtils.transformMsgType(mList.get(postion).getType()) == ChatEnum.EMessageType.VOICE) {
                            ToastUtil.showToast(CollectionActivity.this,"收藏的语音信息不可以转发",1);
                        } else {
                            if (NetUtil.isNetworkConnected()) {
                                startActivity(new Intent(context, MsgForwardActivity.class)
                                        .putExtra(MsgForwardActivity.AGM_JSON, new Gson().toJson(mList.get(postion))).putExtra("from_collect", true));
                            } else {
                                ToastUtil.show("请检查网络连接是否正常");
                            }
                        }
                    }else if(menu.getTitle().equals("删除")){
                        if (mPopupWindow != null) {
                            mPopupWindow.dismiss();
                        }
                        if (mList.get(postion) != null) {
                            httpCancelCollect(mList.get(postion).getId(), postion,mList.get(postion).getMsgId());
                        }
                    }else if(menu.getTitle().equals("更多")){
                        if (mPopupWindow != null) {
                            mPopupWindow.dismiss();
                        }
                        //打开编辑模式
                        switchEditMode(true);
                    }
                }
            });
            // 获取ActionBar位置，判断消息是否到顶部
            // 获取ListView在屏幕顶部的位置
            int[] location = new int[2];
            bindingView.recyclerView.getLocationOnScreen(location);
            // 获取View在屏幕的位置
            int[] locationView = new int[2];
            v.getLocationOnScreen(locationView);
            if (mPopupWindow != null && mPopupWindow.isShowing()) mPopupWindow.dismiss();
            mPopupWindow = null;

            mPopupWindow = new PopupWindow(mRootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            // 设置弹窗外可点击
            mPopupWindow.setTouchable(true);
            mPopupWindow.setOutsideTouchable(true);
            //popwindow不获取焦点
            mPopupWindow.setFocusable(false);
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
                    mRlUp.setVisibility(VISIBLE);
                    mImgTriangleUp.setVisibility(VISIBLE);
                    mRlDown.setVisibility(GONE);
                    setArrowLocation(v, 1, menus.size());
                    mPopupWindow.showAsDropDown(v);
                } else {
                    // 中间弹出
                    mRlUp.setVisibility(GONE);
                    mImgTriangleUp.setVisibility(GONE);
                    mRlDown.setVisibility(VISIBLE);
                    setArrowLocation(v, 1, menus.size());
                    showPopupWindowUp(v, 1);
                }
            } else if (locationView[1] < location[1]) {
                mRlUp.setVisibility(VISIBLE);
                mImgTriangleUp.setVisibility(VISIBLE);
                mRlDown.setVisibility(GONE);
                setArrowLocation(v, 1, menus.size());
                mPopupWindow.showAsDropDown(v);
            } else {
                mRlUp.setVisibility(GONE);
                mImgTriangleUp.setVisibility(GONE);
                mRlDown.setVisibility(VISIBLE);
                setArrowLocation(v, 2, menus.size());
                showPopupWindowUp(v, 2);
            }
        } catch (Exception e) {
        }

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
                    if(list!=null && list.size()>0){
                        timeSortList(list);
                        mList.clear();
                        mList.addAll(list);
                        mViewAdapter.setData(mList);
                        allCollectList.addAll(list);
                        //同步到本地收藏列表，保持前后端数据一致性
                        msgDao.updateLocalCollection(list);
                    }
                    checkData();
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
    private void httpCancelCollect(Long id, int postion, String msgId) {
        //1 有网删除
        if(NetUtil.isNetworkConnected()){
            if(mList.get(postion).getId() != 0L){
                msgAction.cancelCollectMsg(id, new CallBack<ReturnBean>() {
                    @Override
                    public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                        super.onResponse(call, response);
                        if (response.body() == null) {
                            return;
                        }
                        if (response.body().isOk()) {
                            ToastUtil.showToast(CollectionActivity.this, "删除成功",1);
                            mList.remove(postion);
                            checkData();
                            mViewAdapter.notifyDataSetChanged();
                            msgDao.deleteLocalCollection(msgId);//从本地收藏列表删除
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnBean> call, Throwable t) {
                        super.onFailure(call, t);
                        ToastUtil.showToast(CollectionActivity.this, "删除失败",1);
                    }
                });
            }
        }else {
            //2 无网删除
            //2-1 如果本地收藏列表存在这条数据
            if(msgDao.findLocalCollection(msgId)!=null){
                msgDao.deleteLocalCollection(msgId);//从本地收藏列表删除
                //2-1-1 如果有这条数据的收藏操作记录，则直接抵消掉此条记录，即收藏表和删除表都不再记录此操作
                if(msgDao.findOfflineCollectRecord(msgId)!=null){
                    msgDao.deleteOfflineCollectRecord(msgId);
                }else {
                    //2-1-2 如果没有这条数据的收藏操作记录，代表一种场景，即卸载或换手机以后，通过接口获取了最新收藏列表，但本地无收藏操作记录
                    //此时如果进行离线删除，则需要记录删除操作，一旦联网通知后台，确保数据同步一致
                    OfflineDelete offlineDelete = new OfflineDelete();
                    offlineDelete.setMsgId(msgId);
                    msgDao.addOfflineDeleteRecord(offlineDelete);//保存到离线收藏删除记录表
                }
                mList.remove(postion);
                checkData();
                mViewAdapter.notifyDataSetChanged();
            }
            //2-2 如果本地收藏列表不存在这条数据，无需再重复删除，不做任何操作
            ToastUtil.showToast(CollectionActivity.this, "删除成功",1);//离线提示
        }

    }

    /*
     * 发送消息前，需要检测网络连接状态，网络不可用，不能发送
     * 每条消息发送前，需要检测，语音和小视频录制之前，仍需要检测
     * */
    public boolean checkNetConnectStatus() {
        boolean isOk;
        if (!NetUtil.isNetworkConnected()) {
            isOk = false;
        } else {
            isOk = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.CONN_STATUS).get4Json(Boolean.class);
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
                        httpCancelCollect(mList.get(cancelPosition).getId(), cancelPosition,mList.get(cancelPosition).getMsgId());
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
            if(bean2.getHeight()>=bean2.getWidth()){
                isVertical = true;
            }else {
                isVertical = false;
            }
        } else if (type == ChatEnum.EMessageType.AT) {//AT
            CollectAtMessage bean3 = new Gson().fromJson(info.getData(), CollectAtMessage.class);
            txt = bean3.getMsg() == null ? "" : bean3.getMsg();
        } else if (type == ChatEnum.EMessageType.MSG_VIDEO) {//视频
            CollectVideoMessage bean4 = new Gson().fromJson(info.getData(), CollectVideoMessage.class);
            imageUrl = bean4.getVideoBgURL() == null ? "" : bean4.getVideoBgURL();
            if(bean4.getHeight()>=bean4.getWidth()){
                isVertical = true;
            }else {
                isVertical = false;
            }
        } else if (type == ChatEnum.EMessageType.LOCATION) {//位置
            CollectLocationMessage bean5 = new Gson().fromJson(info.getData(), CollectLocationMessage.class);
            txt = bean5.getAddr() == null ? "" : "[位置]" + bean5.getAddr();
            imageUrl = LocationUtils.getLocationUrl(bean5.getLat(), bean5.getLon());
            isVertical = false;
        } else if (type == ChatEnum.EMessageType.SHIPPED_EXPRESSION) {//大表情
            CollectShippedExpressionMessage bean6 = new Gson().fromJson(info.getData(), CollectShippedExpressionMessage.class);
            imageUrl = bean6.getExpression() == null ? "" : bean6.getExpression();
        } else if (type == ChatEnum.EMessageType.FILE) {//文件
            CollectSendFileMessage bean7 = new Gson().fromJson(info.getData(), CollectSendFileMessage.class);
            txt = bean7.getFileName() == null ? "" : "[文件]" + bean7.getFileName();
        } else if (type == ChatEnum.EMessageType.VOICE){
            ToastUtil.showToast(CollectionActivity.this,"收藏的语音信息不可以转发",1);
            return;
        }
        //todo 回复暂未添加
        if (isGroup) {
            avatar = groupHead;
            name = groupName;
        } else {
            avatar = userHead;
            name = userName;
        }
        alertForward.init(CollectionActivity.this, type, avatar, name, txt, imageUrl, "发送", groupId,isVertical, new AlertForward.Event() {
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


    /**
     * 切换编辑模式
     * @param open 是否打开编辑模式
     */
    private void switchEditMode(boolean open){
        inEditMode = open;
        if(open){
            //显示底部布局
            bindingView.layoutBottom.setVisibility(VISIBLE);
            //显示编辑按钮
            for(int i=0; i<mViewAdapter.getList().size(); i++){
                mViewAdapter.getList().get(i).setShowEdit(true);
            }
        }else {
            //关闭底部布局
            bindingView.layoutBottom.setVisibility(GONE);
            //关闭编辑按钮
            for(int i=0; i<mViewAdapter.getList().size(); i++){
                mViewAdapter.getList().get(i).setShowEdit(false);
            }
            //清空选中状态
            for(CollectionInfo info : mList){
                info.setChecked(false);
            }
        }
        //生效
        mViewAdapter.notifyDataSetChanged();
    }


    /**
     * 物理回退键，响应退出编辑模式，恢复到默认模式
     */
    @Override
    public void onBackPressed() {
        if (inEditMode) {
            //关闭编辑模式
            switchEditMode(false);
            if (needDeleteData != null) {
                needDeleteData.clear();
            }
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 是否确认删除弹框
     */
    private void showDialog(){
        dialogOne = builder.setTitle("确认删除所选的收藏项?")
                .setLeftText("取消")
                .setRightText("确定")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消
                        dialogOne.dismiss();
                    }
                })
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //确认
                        dialogOne.dismiss();
                        //1 有网直接删
                        if(NetUtil.isNetworkConnected()){
                            List<String> msgIds = new ArrayList<>();
                            for(int i=0; i<needDeleteData.size(); i++){
                                msgIds.add(needDeleteData.get(i).getMsgId());
                            }
                            msgAction.offlineDeleteCollections(msgIds, new CallBack<ReturnBean>() {
                                @Override
                                public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                                    super.onResponse(call, response);
                                    if (response.body() == null) {
                                        return;
                                    }
                                    if (response.body().isOk()) {
                                        //实时刷新数据
                                        mList.removeAll(needDeleteData);
                                        checkData();
                                        mViewAdapter.notifyDataSetChanged();
                                        ToastUtil.showToast(CollectionActivity.this, "删除成功",1);
                                        for(int i=0; i<needDeleteData.size(); i++){
                                            msgDao.deleteLocalCollection(needDeleteData.get(i).getMsgId());//从本地收藏列表删除
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ReturnBean> call, Throwable t) {
                                    super.onFailure(call, t);
                                    ToastUtil.showToast(CollectionActivity.this, "删除失败",1);
                                }
                            });
                            //关闭编辑模式
                            switchEditMode(false);
                        }else {
                            //2 无网走离线删除逻辑
                            for(int i=0; i<needDeleteData.size(); i++){
                                //2-1 如果本地收藏列表存在这条数据
                                String tempId = needDeleteData.get(i).getMsgId();
                                if(msgDao.findLocalCollection(tempId)!=null){
                                    msgDao.deleteLocalCollection(tempId);//从本地收藏列表删除
                                    //2-1-1 如果有这条数据的收藏操作记录，则直接抵消掉此条记录，即收藏表和删除表都不再记录此操作
                                    if(msgDao.findOfflineCollectRecord(tempId)!=null){
                                        msgDao.deleteOfflineCollectRecord(tempId);
                                    }else {
                                        //2-1-2 如果没有这条数据的收藏操作记录，代表一种场景，即卸载或换手机以后，通过接口获取了最新收藏列表，但本地无收藏操作记录
                                        //此时如果进行离线删除，则需要记录删除操作，一旦联网通知后台，确保数据同步一致
                                        OfflineDelete offlineDelete = new OfflineDelete();
                                        offlineDelete.setMsgId(tempId);
                                        msgDao.addOfflineDeleteRecord(offlineDelete);//保存到离线收藏删除记录表
                                    }
                                }//2-2 如果本地收藏列表不存在这条数据，无需再重复删除，不做任何操作
                            }
                            //实时刷新数据
                            mList.removeAll(needDeleteData);
                            checkData();
                            mViewAdapter.notifyDataSetChanged();
                            ToastUtil.showToast(CollectionActivity.this, "删除成功",1);//离线提示
                            //关闭编辑模式
                            switchEditMode(false);
                        }
                    }
                })
                .build();
        dialogOne.show();
    }

    /**
     * 设置气泡箭头的位置
     *
     * @param v
     * @param gravity     1显示向上箭头 2显示向下箭头
     * @param itemCount   选项个数
     */
    private void setArrowLocation(View v, int gravity,  int itemCount) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(v.getWidth() - ScreenUtil.dip2px(this, 6),
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.rl_up);
//        if (isMe) {
            if (itemCount < 4) {
                params.setMargins(0, 0, ScreenUtil.dip2px(this, 57), 0);
            }
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.setMargins(0, 0, ScreenUtil.dip2px(this, 57), 0);
//        } else {
//            if (itemCount < 4) {
//                params.setMargins(ScreenUtil.dip2px(this, 57), 0, 0, 0);
//            }
//            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//            layoutParams.setMargins(ScreenUtil.dip2px(this, 57), 0, 0, 0);
//        }
        mRecyclerBubble.setLayoutParams(params);
        if (gravity == 1) {
            mRlUp.setLayoutParams(layoutParams);
        } else {
            //在控件上方显示
            layoutParams.addRule(RelativeLayout.BELOW, R.id.recycler_bubble);
            mRlDown.setLayoutParams(layoutParams);
        }
    }
}
