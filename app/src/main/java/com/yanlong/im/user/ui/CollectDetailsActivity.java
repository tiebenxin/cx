package com.yanlong.im.user.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.nim_lib.controll.AVChatProfile;
import com.google.gson.Gson;
import com.hm.cxpay.utils.DateUtils;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.yanlong.im.BuildConfig;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.LocationMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.SendFileMessage;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.ui.FileDownloadActivity;
import com.yanlong.im.chat.ui.VideoPlayActivity;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.location.LocationPersimmions;
import com.yanlong.im.location.LocationService;
import com.yanlong.im.location.LocationUtils;
import com.yanlong.im.user.bean.CollectionInfo;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.audio.AudioPlayManager;
import com.yanlong.im.utils.audio.IVoicePlayListener;
import com.yanlong.im.view.face.FaceView;

import net.cb.cb.library.bean.EventFileRename;
import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.FileConfig;
import net.cb.cb.library.utils.FileUtils;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.PopupSelectView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * @类名：收藏详情
 * @Date：2020/4/28
 * @by zjy
 * @备注：
 */
public class CollectDetailsActivity extends AppActivity {

    private HeadView mHeadView;
    private ActionbarView actionbar;
    private CollectionInfo collectionInfo;
    private TextView tvFrom;
    private TextView tvOne;
    private TextView tvTime;
    private TextView tvContent;
    private ImageView ivPic;
    private ImageView ivPlay;
    private ImageView ivExpress;//表情
    private TextView tvVoiceTime;
    private RelativeLayout layoutText;
    private RelativeLayout layoutPic;
    private RelativeLayout layoutMap;
    private LinearLayout layoutAddr;
    private LinearLayout layoutVoice;
    private LinearLayout layoutMain;
    private RelativeLayout layoutFile;
    private PopupSelectView popupSelectView;
    private String[] strings = {"转发", "删除", "取消"};
    private int position = -1;
    private MsgAllBean bean;
    //地图相关
    private MapView mapview;
    private String city = "长沙市";//默认城市
    private String addr = "";
    private String addrDesc = "";
    private Boolean dragging = false;// 拖拽中 Dragging  draggableing
    private float zoom = 18.0f;//缩放稳定判断
    private BaiduMap mBaiduMap;
    private LocationService locService;
    private BDAbstractLocationListener listener;
    private TextView tvAddr;
    private TextView tvAddrDesc;
    private ImageView ivCurrLocation;
    //文件相关
    private TextView tvFileName;
    private TextView tvFileSize;
    private TextView tvDownload;
    private ImageView ivFilePic;
    private String filePath = "";//打开的文件路径
    private SendFileMessage fileMessage;
    private int status = 0;// 0可打开(下载完成) 1点击下载(未下载前) 2文件不存在 3下载中 4下载失败

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_details);
        initView();
        getExtras();
        initEvent();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        tvFrom = findViewById(R.id.tv_from);
        tvTime = findViewById(R.id.tv_time);
        tvContent = findViewById(R.id.tv_content);
        tvOne = findViewById(R.id.tv_one);
        layoutText = findViewById(R.id.layout_text);
        layoutPic = findViewById(R.id.layout_pic);
        layoutVoice = findViewById(R.id.layout_voice);
        layoutFile = findViewById(R.id.layout_file);
        ivPic = findViewById(R.id.iv_pic);
        ivPlay = findViewById(R.id.iv_play);
        ivExpress = findViewById(R.id.iv_express);
        tvVoiceTime = findViewById(R.id.tv_voice_time);
        layoutMain = findViewById(R.id.layout_main);
        tvAddr = findViewById(R.id.tv_addr);
        tvAddrDesc = findViewById(R.id.tv_addr_desc);
        ivCurrLocation = findViewById(R.id.iv_curr_location);
        mapview = findViewById(R.id.mapview);
        layoutMap = findViewById(R.id.layout_map);
        layoutAddr = findViewById(R.id.layout_addr);
        tvFileName = findViewById(R.id.tv_file_name);
        tvFileSize = findViewById(R.id.tv_file_size);
        ivFilePic = findViewById(R.id.iv_file_image);
        tvDownload = findViewById(R.id.tv_download);
        actionbar = mHeadView.getActionbar();
        actionbar.getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        actionbar.getBtnRight().setVisibility(VISIBLE);

    }

    private void initEvent() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onRight() {
                initPopup();
            }
        });
        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status==0){
                    openAndroidFile(filePath);
                }else if(status==1){
                    DownloadFile(fileMessage);
                }else {
                    ToastUtil.show("文件不存在或者已被删除");
                }
            }
        });
        layoutVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVoice(bean,true,0);
            }
        });
    }

    //获取传递过来的数据
    private void getExtras() {
        if (getIntent() != null) {
            if(getIntent().getIntExtra("position",-1) != (-1)){
                position = getIntent().getIntExtra("position",-1);
            }
            if (getIntent().getStringExtra("json_data") != null) {
                collectionInfo = new Gson().fromJson(getIntent().getStringExtra("json_data"), CollectionInfo.class);
                if (!TextUtils.isEmpty(collectionInfo.getData())) {
                    bean = new Gson().fromJson(collectionInfo.getData(), MsgAllBean.class);
                    //显示用户名或群名
                    if (!TextUtils.isEmpty(collectionInfo.getFromGroupName())) {
                        tvOne.setText("来自群聊");
                        tvFrom.setText(collectionInfo.getFromGroupName());
                    } else if (!TextUtils.isEmpty(collectionInfo.getFromUsername())) {
                        tvOne.setText("来自用户");
                        tvFrom.setText(collectionInfo.getFromUsername());
                    } else {
                        tvOne.setText("未知来源");
                        tvFrom.setText("");
                    }
                    //收藏时间
                    if (!TextUtils.isEmpty(collectionInfo.getCreateTime())) {
                        tvTime.setText(TimeToString.getTimeForCollect(Long.parseLong(collectionInfo.getCreateTime()))+" 收藏");
                    } else {
                        tvTime.setText("");
                    }
                    //不同类型
                    switch (collectionInfo.getType()) {
                        case ChatEnum.EMessageType.TEXT: //文字
                            layoutText.setVisibility(VISIBLE);//显示文字相关布局，隐藏其他类型相关布局
                            layoutVoice.setVisibility(GONE);
                            layoutPic.setVisibility(GONE);
                            layoutFile.setVisibility(GONE);
                            layoutMap.setVisibility(GONE);
                            layoutAddr.setVisibility(GONE);
                            if (bean != null) {
                                if (bean.getChat() != null) {
                                    if (!TextUtils.isEmpty(bean.getChat().getMsg())) {
                                        tvContent.setText(getSpan(bean.getChat().getMsg()));
                                    } else {
                                        tvContent.setText("");
                                    }
                                }
                            }
                            break;
                        case ChatEnum.EMessageType.IMAGE: //图片
                            layoutText.setVisibility(GONE);//显示图片相关布局，隐藏其他类型相关布局
                            layoutVoice.setVisibility(GONE);
                            layoutPic.setVisibility(VISIBLE);
                            layoutFile.setVisibility(GONE);
                            ivPic.setVisibility(VISIBLE);
                            ivExpress.setVisibility(GONE);
                            ivPlay.setVisibility(GONE);
                            layoutMap.setVisibility(GONE);
                            layoutAddr.setVisibility(GONE);
                            if (bean != null) {
                                if (bean.getImage() != null) { //显示预览图或者缩略图
                                    String thumbnail = bean.getImage().getThumbnailShow();
                                    if (isGif(thumbnail)) { //动图加载
                                        String gif = bean.getImage().getPreview();
                                        Glide.with(this)
                                                .load(gif)
                                                .listener(new RequestListener<Drawable>() {
                                                    @Override
                                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                        return false;
                                                    }

                                                    @Override
                                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                        return false;
                                                    }
                                                })
                                                .apply(GlideOptionsUtil.notDefImageOptions())
                                                .into(ivPic);
                                    }else {
                                        if (!TextUtils.isEmpty(bean.getImage().getPreview())) {
                                            Glide.with(CollectDetailsActivity.this).load(bean.getImage().getPreview())
                                                    .apply(GlideOptionsUtil.notDefImageOptions()).into(ivPic);
                                        } else if (!TextUtils.isEmpty(bean.getImage().getThumbnail())) {
                                            Glide.with(CollectDetailsActivity.this).load(bean.getImage().getThumbnail())
                                                    .apply(GlideOptionsUtil.notDefImageOptions()).into(ivPic);
                                        }
                                    }
                                }
                            }
                            break;
                        case ChatEnum.EMessageType.SHIPPED_EXPRESSION: //大表情
                            layoutText.setVisibility(GONE);//显示大表情相关布局，隐藏其他类型相关布局
                            layoutVoice.setVisibility(GONE);
                            layoutPic.setVisibility(VISIBLE);
                            layoutFile.setVisibility(GONE);
                            ivPic.setVisibility(GONE);
                            ivExpress.setVisibility(VISIBLE);
                            ivPlay.setVisibility(GONE);
                            layoutMap.setVisibility(GONE);
                            layoutAddr.setVisibility(GONE);
                            if (bean != null) {
                                if (bean.getShippedExpressionMessage() != null) {
                                    if (!TextUtils.isEmpty(bean.getShippedExpressionMessage().getId())) {
                                        Glide.with(CollectDetailsActivity.this).load(Integer.parseInt(FaceView.map_FaceEmoji.get(bean.getShippedExpressionMessage().getId()).toString())).apply(GlideOptionsUtil.headImageOptions()).into(ivExpress);
                                    }
                                }
                            }
                            break;
                        case ChatEnum.EMessageType.MSG_VIDEO: //短视频消息
                            layoutText.setVisibility(GONE);//显示短视频相关布局，隐藏其他类型相关布局
                            layoutVoice.setVisibility(GONE);
                            layoutPic.setVisibility(VISIBLE);
                            layoutFile.setVisibility(GONE);
                            ivPic.setVisibility(VISIBLE);
                            ivExpress.setVisibility(GONE);
                            ivPlay.setVisibility(VISIBLE);
                            layoutMap.setVisibility(GONE);
                            layoutAddr.setVisibility(GONE);
                            if (bean != null) {
                                if (bean.getVideoMessage() != null) {
                                    if (!TextUtils.isEmpty(bean.getVideoMessage().getBg_url())) {
                                        Glide.with(CollectDetailsActivity.this).load(bean.getVideoMessage().getBg_url())
                                                .apply(GlideOptionsUtil.headImageOptions()).into(ivPic);
                                    }
                                }
                            }
                            layoutPic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (bean != null) {
                                        clickVideo(bean);
                                    }
                                }
                            });
                            break;
                        case ChatEnum.EMessageType.VOICE: //语音
                            layoutText.setVisibility(GONE);//显示语音相关布局，隐藏其他类型相关布局
                            layoutVoice.setVisibility(VISIBLE);
                            layoutPic.setVisibility(GONE);
                            layoutFile.setVisibility(GONE);
                            layoutMap.setVisibility(GONE);
                            layoutAddr.setVisibility(GONE);
                            if (bean != null) {
                                if (bean.getVoiceMessage() != null) {
                                    if (bean.getVoiceMessage().getTime() != 0) {
                                        tvVoiceTime.setText(DateUtils.getSecondFormatTime(Long.valueOf(bean.getVoiceMessage().getTime() + "")));
                                    }
                                }
                            }
                            break;
                        case ChatEnum.EMessageType.LOCATION: //位置消息
                            layoutText.setVisibility(GONE);//显示位置相关布局，隐藏其他类型相关布局
                            layoutVoice.setVisibility(GONE);
                            layoutPic.setVisibility(GONE);
                            layoutFile.setVisibility(GONE);
                            layoutMap.setVisibility(VISIBLE);
                            layoutAddr.setVisibility(VISIBLE);
                            if (!LocationPersimmions.checkPermissions(CollectDetailsActivity.this)) {
                                return;
                            }
                            if (bean != null) {
                                if (bean.getLocationMessage() != null) {
                                    addr = bean.getLocationMessage().getAddress();
                                    addrDesc = bean.getLocationMessage().getAddressDescribe();
                                    //百度地图参数
                                    mBaiduMap = mapview.getMap();
                                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18f));
                                    mBaiduMap.setMyLocationEnabled(true);
                                    mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
                                        @Override
                                        public void onMapStatusChangeStart(MapStatus mapStatus) {
                                            dragging = true;
                                            if (mapStatus != null) {
                                                zoom = mapStatus.zoom;
                                            }
                                        }

                                        @Override
                                        public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
                                        }

                                        @Override
                                        public void onMapStatusChange(MapStatus mapStatus) {
                                        }

                                        @Override
                                        public void onMapStatusChangeFinish(MapStatus mapStatus) {
                                            dragging = false;
                                        }
                                    });

                                    locService = ((MyAppLication) getApplication()).locationService;
                                    LocationClientOption mOption = locService.getDefaultLocationClientOption();
                                    mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
                                    mOption.setCoorType("bd09ll");
                                    locService.setLocationOption(mOption);
                                    listener = new BDAbstractLocationListener() {
                                        @Override
                                        public void onReceiveLocation(BDLocation bdLocation) {
                                            try {
                                                if (bdLocation != null && bdLocation.getPoiList() != null) {
                                                    city = bdLocation.getCity();
                                                    setLocationBitmap(true, bdLocation.getLatitude(), bdLocation.getLongitude());
                                                    locService.stop();//定位成功后停止点位
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    locService.registerListener(listener);
                                }
                                tvAddr.setText(addr);
                                tvAddrDesc.setText(addrDesc);
                                actionbar.getBtnRight().setVisibility(View.VISIBLE);
                                actionbar.getBtnRight().setImageResource(R.mipmap.ic_chat_more);
                                ivCurrLocation.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (ViewUtils.isFastDoubleClick()) {
                                            return;
                                        }
                                        locService.start();
                                    }
                                });
                                locService.start();
                            }
                            break;
                        case ChatEnum.EMessageType.AT: //艾特@消息
                            layoutText.setVisibility(VISIBLE);//显示文字相关布局，隐藏其他类型相关布局
                            layoutVoice.setVisibility(GONE);
                            layoutPic.setVisibility(GONE);
                            layoutFile.setVisibility(GONE);
                            layoutMap.setVisibility(GONE);
                            layoutAddr.setVisibility(GONE);
                            if (bean != null) {
                                if (bean.getAtMessage() != null) {
                                    if (!TextUtils.isEmpty(bean.getAtMessage().getMsg())) {
                                        tvContent.setText(bean.getAtMessage().getMsg());
                                    }
                                }
                            }
                            break;
                        case ChatEnum.EMessageType.FILE: //文件
                            layoutText.setVisibility(GONE);//显示文件相关布局，隐藏其他类型相关布局
                            layoutVoice.setVisibility(GONE);
                            layoutPic.setVisibility(GONE);
                            layoutFile.setVisibility(VISIBLE);
                            layoutMap.setVisibility(GONE);
                            layoutAddr.setVisibility(GONE);
                            if (bean != null) {
                                if (bean.getSendFileMessage() != null) {
                                    fileMessage = bean.getSendFileMessage();
                                    if (!TextUtils.isEmpty(bean.getSendFileMessage().getFile_name())) {
                                        tvFileName.setText(bean.getSendFileMessage().getFile_name());
                                    }
                                    if (!TextUtils.isEmpty(bean.getSendFileMessage().getFormat())) {
                                        String fileFormat = bean.getSendFileMessage().getFormat();
                                        if (fileFormat.equals("txt")) {
                                            ivFilePic.setImageResource(R.mipmap.ic_txt);
                                        } else if (fileFormat.equals("xls") || fileFormat.equals("xlsx")) {
                                            ivFilePic.setImageResource(R.mipmap.ic_excel);
                                        } else if (fileFormat.equals("ppt") || fileFormat.equals("pptx") || fileFormat.equals("pdf")) { //PDF暂用此图标
                                            ivFilePic.setImageResource(R.mipmap.ic_ppt);
                                        } else if (fileFormat.equals("doc") || fileFormat.equals("docx")) {
                                            ivFilePic.setImageResource(R.mipmap.ic_word);
                                        } else if (fileFormat.equals("rar") || fileFormat.equals("zip")) {
                                            ivFilePic.setImageResource(R.mipmap.ic_zip);
                                        } else if (fileFormat.equals("exe")) {
                                            ivFilePic.setImageResource(R.mipmap.ic_exe);
                                        } else {
                                            ivFilePic.setImageResource(R.mipmap.ic_unknow);
                                        }
                                    }
                                    if (bean.getSendFileMessage().getSize() != 0L) {
                                        tvFileSize.setText("文件大小 "+FileUtils.getFileSizeString(bean.getSendFileMessage().getSize()));
                                    }
                                    //显示下载状态
                                    //1 如果是我发的文件
                                    if (bean.isMe()) {
                                        //TODO 这里不考虑转发和重名
                                        //1-1 没有本地路径，代表为PC端发的文件，需要下载
                                        if (TextUtils.isEmpty(fileMessage.getLocalPath())) {
                                            //从下载路径里找，若存在该文件，则允许直接打开；否则需要下载
                                            if (net.cb.cb.library.utils.FileUtils.fileIsExist(FileConfig.PATH_DOWNLOAD + fileMessage.getRealFileRename())) {
                                                filePath = FileConfig.PATH_DOWNLOAD + fileMessage.getRealFileRename();
                                                status = 0;
                                                tvDownload.setText("打开");
                                            } else {
                                                if (!TextUtils.isEmpty(fileMessage.getUrl())) {
                                                    ToastUtil.show("检测到该文件来源于PC端，请点击下载");
                                                } else {
                                                    ToastUtil.show("文件下载地址错误，请联系客服");
                                                }
                                                status = 1;
                                                tvDownload.setText("下载");
                                            }
                                        } else {
                                            //1-2 有本地路径，则为手机本地文件，从本地路径里找，有则打开，没有提示文件已被删除
                                            if (net.cb.cb.library.utils.FileUtils.fileIsExist(fileMessage.getLocalPath())) {
                                                filePath = fileMessage.getLocalPath();
                                                status = 0;
                                            } else {
                                                ToastUtil.show("文件不存在或者已被删除");
                                                status = 2;
                                            }
                                            tvDownload.setText("打开");
                                        }
                                    }else {
                                        //2 如果是别人发的文件
                                        //从下载路径里找，若存在该文件，则直接打开；否则需要下载 TODO 暂时直接打开原文件名
                                        if (net.cb.cb.library.utils.FileUtils.fileIsExist(FileConfig.PATH_DOWNLOAD + fileMessage.getFile_name())) {
                                            filePath = FileConfig.PATH_DOWNLOAD + fileMessage.getFile_name();
                                            status = 0;
                                            tvDownload.setText("打开");
                                        } else {
                                            if (TextUtils.isEmpty(fileMessage.getUrl())) {
                                                ToastUtil.show("文件下载地址错误，请联系客服");
                                            }
                                            status = 1;
                                            tvDownload.setText("下载");
                                        }
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private void clickVideo(MsgAllBean msg) {
        if (AVChatProfile.getInstance().isCallIng() || AVChatProfile.getInstance().isCallEstablished()) {
            if (AVChatProfile.getInstance().isChatType() == AVChatType.VIDEO.getValue()) {
                ToastUtil.show(CollectDetailsActivity.this, getString(R.string.avchat_peer_busy_video));
            } else {
                ToastUtil.show(CollectDetailsActivity.this, getString(R.string.avchat_peer_busy_voice));
            }
        } else {
            String localUrl = msg.getVideoMessage().getLocalUrl();
            if (StringUtil.isNotNull(localUrl)) {
                File file = new File(localUrl);
                if (!file.exists()) {
                    localUrl = msg.getVideoMessage().getUrl();
                }
            } else {
                localUrl = msg.getVideoMessage().getUrl();
            }
            Intent intent = new Intent(CollectDetailsActivity.this, VideoPlayActivity.class);
            intent.putExtra("videopath", localUrl);
            intent.putExtra("videomsg", new Gson().toJson(msg));
            intent.putExtra("msg_id", msg.getMsg_id());
            intent.putExtra("bg_url", msg.getVideoMessage().getBg_url());
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);

        }
    }

    private void initPopup() {
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(layoutMain, Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 0:
                        //转发
                        if (bean != null) {
                            startActivity(new Intent(context, MsgForwardActivity.class)
                                    .putExtra(MsgForwardActivity.AGM_JSON, new Gson().toJson(bean)));
                        }
                        break;
                    case 1:
                        //取消收藏
                        Intent intent = new Intent();
                        intent.putExtra("cancel_collect_position",position);
                        setResult(RESULT_OK,intent);
                        finish();
                        break;
                    case 2:
                        //取消
                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }

    private void setLocationBitmap(Boolean isMyLocation, double latitude, double longitude) {
        LogUtil.getLog().e("===location====" + latitude + "====" + longitude);
        LatLng point = new LatLng(latitude, longitude);
        // 构建Marker图标
        BitmapDescriptor bitmap = null;
        if (isMyLocation) {
            bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.location_circle_big); // 非推算结果
        } else {
            mBaiduMap.clear();
            bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.location_two); // 非推算结果
        }
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        if(mapview!=null){
            mapview.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        if(mapview!=null){
            mapview.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if(mapview!=null){
            if(locService!=null && listener!=null){
                locService.unregisterListener(listener);
                locService.stop();
            }
            mapview.onDestroy();
        }
    }

    /**
     * 选择已有程序打开文件
     *
     * @param filepath
     */
    public void openAndroidFile(String filepath) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            File file = new File(filepath);
            Uri uri = null;
            // 7.0行为变更适配，加上文件权限，通过FileProvider在应用中共享文件
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".app", file);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(file);
            }
            intent.setDataAndType(uri, net.cb.cb.library.utils.FileUtils.getMIMEType(file));//设置类型
            if (context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(context, "没有找到对应的程序", Toast.LENGTH_SHORT).show();
            }
//            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            ToastUtil.show("附件不能打开，请下载相关软件！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 下载文件
     *
     * @param sendFileMessage
     */
    private void DownloadFile(SendFileMessage sendFileMessage) {
        String fileMsgId = "";
        String fileUrl = "";
        String fileName = "";

        //获取文件消息id
        if (!TextUtils.isEmpty(sendFileMessage.getMsgId())) {
            fileMsgId = sendFileMessage.getMsgId();
        }

        //显示文件名
        if (!TextUtils.isEmpty(sendFileMessage.getFile_name())) {
            fileName = sendFileMessage.getFile_name();
            //若有同名文件，则重命名，保存最终真实文件名，如123.txt若有重名则依次保存为123.txt(1) 123.txt(2)
            //若没有同名文件，则按默认新文件来保存
            fileName = net.cb.cb.library.utils.FileUtils.getFileRename(fileName);
        }

        //获取url，自动开始下载文件
        if (!TextUtils.isEmpty(sendFileMessage.getUrl())) {
            fileUrl = sendFileMessage.getUrl();
            //指定下载路径文件夹，若不存在则创建
            File fileDir = new File(FileConfig.PATH_DOWNLOAD);
            if (!fileDir.exists()) {
                fileDir.mkdir();
            }
            File file = new File(fileDir, fileName);
            try {
                String finalFileMsgId = fileMsgId;
                String fileNewName = fileName;
                DownloadUtil.get().downLoadFile(fileUrl, file, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        ToastUtil.showLong(CollectDetailsActivity.this, "下载成功! \n文件已保存：" + FileConfig.PATH_DOWNLOAD + "目录下");
                        //下载成功
                        //1 本地数据库刷新：保存一个新增属性-真实文件名，主要用于多个同名文件区分保存，防止重名，方便用户点击打开重名文件
                        MsgAllBean reMsg = DaoUtil.findOne(MsgAllBean.class, "msg_id", finalFileMsgId);
                        reMsg.getSendFileMessage().setRealFileRename(fileNewName);
                        DaoUtil.update(reMsg);
                        //2 通知ChatActivity刷新该文件消息
                        EventFileRename eventFileRename = new EventFileRename();
                        sendFileMessage.setRealFileRename(fileNewName);
                        eventFileRename.setMsgAllBean(bean);
                        EventBus.getDefault().post(eventFileRename);
                        status = 0;
                        tvDownload.setText("打开");
                        filePath = FileConfig.PATH_DOWNLOAD+fileNewName;
                    }

                    @Override
                    public void onDownloading(int progress) {
                        status = 3;
                        tvDownload.setText("下载中 "+progress+"%");
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        status = 4;
                        ToastUtil.show("文件下载失败");
                        tvDownload.setText("下载失败");
                    }
                });

            } catch (Exception e) {
                ToastUtil.show("文件下载失败");
                tvDownload.setText("下载失败");
            }
        }
    }

    private void playVoice(final MsgAllBean bean, final boolean canAutoPlay,
                           final int position) {
//        LogUtil.getLog().i(TAG, "playVoice--" + position);
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
            if (bean.getVoiceMessage().getPlayStatus() == ChatEnum.EPlayStatus.NO_DOWNLOADED && !bean.isMe()) {
                AudioPlayManager.getInstance().downloadAudio(context, bean, new DownloadUtil.IDownloadVoiceListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        bean.getVoiceMessage().setPlayStatus(ChatEnum.EPlayStatus.NO_PLAY);
                        AudioPlayManager.getInstance().startPlay(context, bean, position, canAutoPlay, new IVoicePlayListener() {
                            @Override
                            public void onStart(MsgAllBean bean) {
                                bean.getVoiceMessage().setPlayStatus(ChatEnum.EPlayStatus.PLAYING);
                            }

                            @Override
                            public void onStop(MsgAllBean bean) {
                                bean.getVoiceMessage().setPlayStatus(ChatEnum.EPlayStatus.STOP_PLAY);
                            }

                            @Override
                            public void onComplete(MsgAllBean bean) {
                                bean.getVoiceMessage().setPlayStatus(ChatEnum.EPlayStatus.PLAYED);
                            }
                        });
                    }

                    @Override
                    public void onDownloading(int progress) {

                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        bean.getVoiceMessage().setPlayStatus(ChatEnum.EPlayStatus.NO_DOWNLOADED);
                        ToastUtil.show("语音消息下载失败!");
                    }
                });
            } else {
                AudioPlayManager.getInstance().startPlay(context, bean, position, canAutoPlay, new IVoicePlayListener() {
                    @Override
                    public void onStart(MsgAllBean bean) {
                        bean.getVoiceMessage().setPlayStatus(ChatEnum.EPlayStatus.PLAYING);
                    }

                    @Override
                    public void onStop(MsgAllBean bean) {
                        bean.getVoiceMessage().setPlayStatus(ChatEnum.EPlayStatus.STOP_PLAY);
                    }

                    @Override
                    public void onComplete(MsgAllBean bean) {
                        bean.getVoiceMessage().setPlayStatus(ChatEnum.EPlayStatus.PLAYED);
                    }
                });
            }
        }
    }

    //是否为动图
    private boolean isGif(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.toLowerCase().contains(".gif")) {
                return true;
            }
        }
        return false;
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
