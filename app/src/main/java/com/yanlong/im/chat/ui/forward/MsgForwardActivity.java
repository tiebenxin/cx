package com.yanlong.im.chat.ui.forward;


import android.Manifest;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.cx.sharelib.message.CxMediaMessage;
import com.example.nim_lib.config.Preferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jrmf360.tools.utils.ThreadUtil;
import com.yanlong.im.MainActivity;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
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
import com.yanlong.im.chat.bean.ReplyMessage;
import com.yanlong.im.chat.bean.SendFileMessage;
import com.yanlong.im.chat.bean.ShippedExpressionMessage;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.bean.WebMessage;
import com.yanlong.im.chat.eventbus.AckEvent;
import com.yanlong.im.chat.server.UpLoadService;
import com.yanlong.im.chat.tcp.TcpConnection;
import com.yanlong.im.chat.ui.forward.vm.ForwardViewModel;
import com.yanlong.im.chat.ui.view.AlertForward;
import com.yanlong.im.databinding.ActivityMsgForwardBinding;
import com.yanlong.im.location.LocationUtils;
import com.yanlong.im.share.ShareDialog;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.CollectionInfo;
import com.yanlong.im.utils.CommonUtils;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.dialog.DialogCommon2;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.FileUtils;
import net.cb.cb.library.utils.GsonUtils;
import net.cb.cb.library.utils.ImgSizeUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.ClearEditText;
import net.cb.cb.library.view.CustomTabView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.NonNull;

import static net.cb.cb.library.utils.FileUtils.SIZETYPE_B;

/***
 * 消息转换
 *
 * 备注：相册图片分享到常信，收到分享的图片后复用此界面
 */

public class MsgForwardActivity extends AppActivity implements IForwardListener {
    public static final String AGM_JSON = "JSON";
    public static final String MODE = "mode";

    private ActionbarView actionbar;
    private ActivityMsgForwardBinding ui;
    private ClearEditText edtSearch;

    private MsgAllBean msgAllBean;
    private MsgAllBean sendMessage;//转发消息
    private CollectionInfo collectionInfo;//收藏转发


    @CustomTabView.ETabPosition
    private int currentPager = CustomTabView.ETabPosition.LEFT;
    private String json;

    public static boolean isSingleSelected = true;//转发单人 转发多人
    public static List<MoreSessionBean> moreSessionBeanList = new ArrayList<>();//转发多人集合
    public static int maxNumb = 9;
    public static String searchKey = null;

    private CheckPermission2Util permission2Util = new CheckPermission2Util();
    private int model;
    private List<MsgAllBean> msgList;

    //    private List<MsgAllBean> sendQueue = new ArrayList<>();
    private Map<String, MsgAllBean> sendQueue = new HashMap<>();

    //系统发送图片路径
    private String filePath;

    //第三方分享
    private String shareText;
    private String shareImageUrl;
    private String shareWebUrl;
    private int mediaType;
    private String appName;
    private String appIcon;
    private String shareDescription;
    private String shareTitle;
    private List<String> shareUrls;
    private DialogCommon2 dialogSendProgress;
    private int prePosition;
    private int preProgress;
    private ForwardViewModel viewModel;

    //图片编辑地址
    private String editPicPath = "";

    //收藏转发
    private boolean fromCollect = false;


    //单条消息转发
    public static Intent newIntent(Context context, @ChatEnum.EForwardMode int mode, String json) {
        Intent intent = new Intent(context, MsgForwardActivity.class);
        intent.putExtra(MODE, mode);
        intent.putExtra(AGM_JSON, json);
        return intent;
    }

    //第三方或系统分享内容
    public static Intent newIntent(Context context, @ChatEnum.EForwardMode int mode, Bundle bundle) {
        Intent intent = new Intent(context, MsgForwardActivity.class);
        intent.putExtras(bundle);
        intent.putExtra(MODE, mode);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_msg_forward);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(MyAppLication.getInstance())).get(ForwardViewModel.class);
        initIntent();
        findViews();
        initEvent();
        showFragment(currentPager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.checkRealmStatus();
        }
        if (!SocketUtil.getSocketUtil().isRun()) {
            TcpConnection.getInstance(AppConfig.getContext()).startConnect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.getLog().i("跟踪", "MsgForward--stop--" + SocketUtil.getSocketUtil().isKeepConnect());
        if (SocketUtil.getSocketUtil().isKeepConnect()) {
            SocketUtil.getSocketUtil().setKeepConnect(false);
            if (!SocketUtil.getSocketUtil().isMainLive()) {
                TcpConnection.getInstance(AppConfig.getContext()).destroyConnect();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dismissSendProgress();
        if (viewModel != null) {
            viewModel.onDestroy();
        }
    }

    private void dismissSendProgress() {
        if (dialogSendProgress != null) {
            dialogSendProgress.dismiss();
            dialogSendProgress = null;
        }
    }

    private void initIntent() {
        Intent intent = getIntent();
        model = intent.getIntExtra(MODE, ChatEnum.EForwardMode.DEFAULT);
        json = intent.getStringExtra(AGM_JSON);
        fromCollect = intent.getBooleanExtra("from_collect", false);
        if (model == ChatEnum.EForwardMode.DEFAULT || model == ChatEnum.EForwardMode.MERGE) {
            msgAllBean = GsonUtils.getObject(json, MsgAllBean.class);
        } else if (model == ChatEnum.EForwardMode.ONE_BY_ONE) {
            Gson gson = new Gson();
            msgList = gson.fromJson(json, new TypeToken<List<MsgAllBean>>() {
            }.getType());
            if (msgList != null) {
                sendQueue.clear();
                for (int i = 0; i < msgList.size(); i++) {
                    MsgAllBean msg = msgList.get(i);
                    sendQueue.put(msg.getMsg_id(), msg);

                }
            }
        } else if (model == ChatEnum.EForwardMode.SYS_SEND || model == ChatEnum.EForwardMode.SYS_SEND_MULTI) {
            getSysImgShare();
        } else if (model == ChatEnum.EForwardMode.SHARE) {
            mediaType = intent.getIntExtra("cxapi_sendmessagetowx_req_media_type", 0);
            shareTitle = intent.getStringExtra("cxobject_title");
            shareDescription = intent.getStringExtra("cxobject_description");

            if (mediaType == CxMediaMessage.EMediaType.UNKNOWN) {//未知
                finish();
            } else if (mediaType == CxMediaMessage.EMediaType.TEXT) {//文本
                shareText = intent.getStringExtra("cx_object_text");
            } else if (mediaType == CxMediaMessage.EMediaType.IMAGE) {//图片
                shareImageUrl = intent.getStringExtra("cx_imageobject_imagePath");
            } else if (mediaType == CxMediaMessage.EMediaType.VIDEO) {//视频

            } else if (mediaType == CxMediaMessage.EMediaType.WEB) {//web
                shareWebUrl = intent.getStringExtra("cxwebpageobject_webpageUrl");
                appName = intent.getStringExtra("app_name");
                appIcon = intent.getStringExtra("app_icon");
            }
        } else if (model == ChatEnum.EForwardMode.EDIT_PIC) {
            if (intent.getExtras() != null) {
                if (intent.getExtras().getString("edit_pic_path") != null) {
                    editPicPath = intent.getExtras().getString("edit_pic_path");
                    mediaType = CxMediaMessage.EMediaType.IMAGE;
                }
            }
        }
    }

    //自动寻找控件
    private void findViews() {
        actionbar = ui.headView.getActionbar();
        edtSearch = findViewById(R.id.edt_search);
    }


    //自动生成的控件事件
    private void initEvent() {
        searchKey = null;
        isSingleSelected = true;
        moreSessionBeanList = new ArrayList<>();
        json = getIntent().getStringExtra(AGM_JSON);
        if (!fromCollect) {
            msgAllBean = GsonUtils.getObject(json, MsgAllBean.class);
        } else {
            collectionInfo = GsonUtils.getObject(json, CollectionInfo.class);//来自收藏的转发特殊处理
        }
        resetRightText();
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                if (isSingleSelected) {
                    onBackPressed();
                } else {
                    isSingleSelected = true;
                    resetRightText();

                    EventBus.getDefault().post(new SingleOrMoreEvent(isSingleSelected));
                }
            }

            @Override
            public void onRight() {
                if (!isSingleSelected && moreSessionBeanList.size() > 0) {
                    onForward(0L, "", "", "");//仅仅是唤起弹窗
                } else {
                    isSingleSelected = !isSingleSelected;
                    resetRightText();
                    EventBus.getDefault().post(new SingleOrMoreEvent(isSingleSelected));
                }
            }
        });

        ui.tabView.setTabSelectListener(new CustomTabView.OnTabSelectListener() {
            @Override
            public void onLeft() {
                showFragment(CustomTabView.ETabPosition.LEFT);
            }

            @Override
            public void onRight() {
                showFragment(CustomTabView.ETabPosition.RIGHT);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtSearch.getText().toString().length() == 0) {
                    searchKey = null;
                    EventBus.getDefault().post(new SearchKeyEvent());
                } else {
                    searchKey = s.toString();
                    EventBus.getDefault().post(new SearchKeyEvent());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void resetTitle(@CustomTabView.ETabPosition int tab) {
        if (tab == CustomTabView.ETabPosition.RIGHT) {
            ui.headView.setTitle("消息转发");// 选择一个联系人
        } else if (tab == CustomTabView.ETabPosition.LEFT) {
            ui.headView.setTitle("消息转发");// 选择一个聊天
        }
    }

    private void resetRightText() {
        if (model == ChatEnum.EForwardMode.SYS_SEND_MULTI || model == ChatEnum.EForwardMode.SYS_SEND || model == ChatEnum.EForwardMode.EDIT_PIC) {
            actionbar.setTxtRight("");
            return;
        }
        if (isSingleSelected) {
            actionbar.getBtnLeft().setVisibility(View.VISIBLE);
            actionbar.setTxtLeft("");
            actionbar.setTxtRight("多选");
        } else {
            actionbar.getBtnLeft().setVisibility(View.GONE);
            actionbar.setTxtLeft("取消");
            if (moreSessionBeanList.size() == 0) {
                actionbar.setTxtRight("完成");
            } else {
                actionbar.setTxtRight("完成(" + moreSessionBeanList.size() + ")");
            }

        }
    }

    private void showFragment(@CustomTabView.ETabPosition int tab) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment newFragment = fragmentManager.findFragmentByTag(tab + "");
        if (newFragment == null) {
            newFragment = createFragment(tab);
        }
        if (newFragment == null) {
            return;
        }
        prepareFragment(newFragment);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fl_content, newFragment, tab + "");
        ft.attach(newFragment);
        ft.commitAllowingStateLoss();
        currentPager = tab;
        resetTitle(currentPager);
    }

    private void prepareFragment(Fragment fragment) {
        if (fragment instanceof ForwardSessionFragment) {
            ForwardSessionFragment sessionFragment = (ForwardSessionFragment) fragment;
            sessionFragment.setForwardListener(this);
        } else if (fragment instanceof ForwardRosterFragment) {
            ForwardRosterFragment rosterFragment = (ForwardRosterFragment) fragment;
            rosterFragment.setForwardListener(new IForwardRosterListener() {
                @Override
                public void onSelectMuc() {
                    if (ViewUtils.isFastDoubleClick()) {
                        return;
                    }
                    Intent intent = new Intent(MsgForwardActivity.this, GroupSelectActivity.class);
                    intent.putExtra(AGM_JSON, json);
                    intent.putExtra(Preferences.DATA, moreSessionBeanList.size());
                    startActivityForResult(intent, 0);

                }

                @Override
                public void onForward(long uid, String gid, String avatar, String nick) {
                    MsgForwardActivity.this.onForward(uid, gid, avatar, nick);
                }
            });
        }
    }

    private Fragment createFragment(@CustomTabView.ETabPosition int tab) {
        switch (tab) {
            case CustomTabView.ETabPosition.LEFT:
                return new ForwardSessionFragment();
            case CustomTabView.ETabPosition.RIGHT:
                return new ForwardRosterFragment();
        }
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doAckEvent(AckEvent event) {
        Object data = event.getData();
        if (data instanceof MsgAllBean) {
            MsgAllBean msgAllBean = (MsgAllBean) data;
            if (isWaitModel()) {
                removeSendQueue(msgAllBean.getMsg_id());
            } else {
                if (sendQueue != null && sendQueue.size() > 0) {
                    removeSendQueue(msgAllBean.getMsg_id());
                } else {
                    doSendSuccess();
                }
            }
        } else if (data instanceof MsgBean.AckMessage) {
            MsgBean.AckMessage ackMessage = (MsgBean.AckMessage) data;
            if (isWaitModel()) {
                removeSendQueue(ackMessage.getMsgId(0));
            } else {
                if (sendQueue != null && sendQueue.size() > 0) {
                    removeSendQueue(ackMessage.getMsgId(0));
                } else {
                    ToastUtil.show(this, "发送失败");
                }
            }
        }
    }

    private void removeSendQueue(String msgId) {
        if (TextUtils.isEmpty(msgId)) {
            return;
        }
        removeQueue(msgId);
        if (sendQueue.size() == 0) {
            if (model == ChatEnum.EForwardMode.SHARE) {
                showShareDialog();
            } else if (model == ChatEnum.EForwardMode.SYS_SEND) {
                startActivity(new Intent(MsgForwardActivity.this, MainActivity.class));
                MsgForwardActivity.this.finish();
            } else if (model == ChatEnum.EForwardMode.SYS_SEND_MULTI) {
                dismissSendProgress();
                startActivity(new Intent(MsgForwardActivity.this, MainActivity.class));
                MsgForwardActivity.this.finish();
            } else if (model == ChatEnum.EForwardMode.EDIT_PIC) {
                ToastUtil.show("转发成功!");
                MsgForwardActivity.this.finish();
            } else if (model == ChatEnum.EForwardMode.DEFAULT) {
                ToastUtil.show("转发成功!");
                MsgForwardActivity.this.finish();
            } else if (model == ChatEnum.EForwardMode.MERGE) {
                ToastUtil.show("转发成功!");
                MsgForwardActivity.this.finish();
            }
        }
    }

    private synchronized void removeQueue(String msgId) {
        LogUtil.getLog().i("转发", "--remove--" + msgId);
        sendQueue.remove(msgId);
    }

    @Override
    public void onForward(final long toUid, final String toGid, String mIcon, String mName) {
        if (!SocketUtil.getSocketUtil().isRun()) {
            ToastUtil.show(this, "连接已断开，请退出常信重新分享");
            return;
        }
        if (model == ChatEnum.EForwardMode.DEFAULT && msgAllBean == null) {
            return;
        } else if (model == ChatEnum.EForwardMode.SYS_SEND) {
            if (TextUtils.isEmpty(filePath)) {
                return;
            }
            if (mediaType == CxMediaMessage.EMediaType.IMAGE) {
                ImgSizeUtil.ImageSize imgSize = ImgSizeUtil.getAttribute(filePath);
                if (imgSize == null) {
                    return;
                }
                ImageMessage image = SocketData.createImageMessage(SocketData.getUUID(), filePath, "", imgSize.getWidth(), imgSize.getHeight(), true, false, imgSize.getSize());
                msgAllBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.IMAGE, ChatEnum.ESendStatus.PRE_SEND, SocketData.getFixTime(), image);
            } else if (mediaType == CxMediaMessage.EMediaType.FILE) {
                double fileSize = FileUtils.getFileOrFilesSize(filePath, SIZETYPE_B);
                String fileName = FileUtils.getFileName(filePath);
                String fileFormat = FileUtils.getFileSuffix(fileName);
                SendFileMessage fileMessage = SocketData.createFileMessage(SocketData.getUUID(), filePath, "", fileName, new Double(fileSize).longValue(), fileFormat, false);
                msgAllBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.FILE, ChatEnum.ESendStatus.PRE_SEND, SocketData.getFixTime(), fileMessage);
            }
        } else if (model == ChatEnum.EForwardMode.SYS_SEND_MULTI) {
            if (shareUrls == null || shareUrls.isEmpty()) {
                return;
            }
            if (mediaType == CxMediaMessage.EMediaType.IMAGE) {
                msgList = getMsgList(shareUrls, toUid, toGid);
                sendQueue.clear();
                for (int i = 0; i < msgList.size(); i++) {
                    MsgAllBean msg = msgList.get(i);
                    sendQueue.put(msg.getMsg_id(), msg);
                }
            }
        } else if (model == ChatEnum.EForwardMode.SHARE) {
            if (mediaType == CxMediaMessage.EMediaType.TEXT) {//文本
                if (!TextUtils.isEmpty(shareText)) {
                    ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), shareText);
                    msgAllBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                }
            } else if (mediaType == CxMediaMessage.EMediaType.IMAGE) {
                if (!TextUtils.isEmpty(shareImageUrl)) {
                    ImageMessage imageMsg = SocketData.createImageMessage(SocketData.getUUID(), "", shareImageUrl, 0, 0, true, false, 0);
                    msgAllBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.IMAGE, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), imageMsg);
                }

            } else if (mediaType == CxMediaMessage.EMediaType.WEB) {
                if (!TextUtils.isEmpty(shareWebUrl)) {
                    WebMessage webMsg = SocketData.createWebMessage(SocketData.getUUID(), appName, appIcon, shareTitle, shareDescription, shareWebUrl);
                    msgAllBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.WEB, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), webMsg);
                }
            }

            if (msgAllBean == null) {
                ToastUtil.show(this, "数据异常，分享失败");
                return;
            }
        } else if (model == ChatEnum.EForwardMode.EDIT_PIC) { //图片编辑
            if (TextUtils.isEmpty(editPicPath)) {
                return;
            }
            ImgSizeUtil.ImageSize imgSize = ImgSizeUtil.getAttribute(editPicPath);
            if (imgSize == null) {
                return;
            }
            ImageMessage image = SocketData.createImageMessage(SocketData.getUUID(), editPicPath, "", imgSize.getWidth(), imgSize.getHeight(), true, false, imgSize.getSize());
            msgAllBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.IMAGE, ChatEnum.ESendStatus.PRE_SEND, SocketData.getFixTime(), image);
        }

        String btm = "发送";
        if (!isSingleSelected && moreSessionBeanList.size() > 0) {
            btm = "发送(" + moreSessionBeanList.size() + ")";
        }

        AlertForward alertForward = new AlertForward();
        String txt = "";
        String imageUrl = "";
        int type = 0;
        if (model == ChatEnum.EForwardMode.DEFAULT || model == ChatEnum.EForwardMode.SYS_SEND || model == ChatEnum.EForwardMode.SHARE) {
            //默认转发
            if (fromCollect == false) {
                if (msgAllBean == null) {
                    return;
                }
                type = msgAllBean.getMsg_type();
                if (msgAllBean.getChat() != null) {//转换文字
                    txt = msgAllBean.getChat().getMsg();
                } else if (msgAllBean.getImage() != null) {
                    imageUrl = msgAllBean.getImage().getThumbnail();
                    if (TextUtils.isEmpty(imageUrl)) {
                        if (model == ChatEnum.EForwardMode.SYS_SEND || model == ChatEnum.EForwardMode.SHARE) {
                            imageUrl = msgAllBean.getImage().getLocalimg();
                        }
                    }
                } else if (msgAllBean.getAtMessage() != null) {
                    txt = msgAllBean.getAtMessage().getMsg();
                } else if (msgAllBean.getVideoMessage() != null) {
                    imageUrl = msgAllBean.getVideoMessage().getBg_url();
                } else if (msgAllBean.getLocationMessage() != null) {
                    txt = "[位置]" + msgAllBean.getLocationMessage().getAddress();
                } else if (msgAllBean.getShippedExpressionMessage() != null) {
                    imageUrl = msgAllBean.getShippedExpressionMessage().getId();
                } else if (msgAllBean.getVideoMessage() != null) {
                    imageUrl = msgAllBean.getVideoMessage().getBg_url();
                } else if (msgAllBean.getLocationMessage() != null) {
                    imageUrl = LocationUtils.getLocationUrl(msgAllBean.getLocationMessage().getLatitude(), msgAllBean.getLocationMessage().getLongitude());
                } else if (msgAllBean.getSendFileMessage() != null) {
                    txt = "[文件]" + msgAllBean.getSendFileMessage().getFile_name();
                } else if (msgAllBean.getWebMessage() != null) {
                    txt = "[链接]" + msgAllBean.getWebMessage().getTitle();
                } else if (msgAllBean.getReplyMessage() != null) {
                    if (msgAllBean.getReplyMessage().getAtMessage() != null) {
                        txt = msgAllBean.getReplyMessage().getAtMessage().getMsg();
                    } else if (msgAllBean.getReplyMessage().getChatMessage() != null) {
                        txt = msgAllBean.getReplyMessage().getChatMessage().getMsg();
                    }
                }
            } else {
                //收藏转发特殊处理
                if (collectionInfo == null) {
                    return;
                }
                type = CommonUtils.transformMsgType(collectionInfo.getType());
                //支持收藏转发的类型
                if (type == ChatEnum.EMessageType.TEXT) {//转换文字
                    CollectChatMessage bean1 = new Gson().fromJson(collectionInfo.getData(), CollectChatMessage.class);
                    txt = bean1.getMsg();
                } else if (type == ChatEnum.EMessageType.IMAGE) {
                    CollectImageMessage bean2 = new Gson().fromJson(collectionInfo.getData(), CollectImageMessage.class);
                    imageUrl = bean2.getThumbnail();
                } else if (type == ChatEnum.EMessageType.AT) {
                    CollectAtMessage bean3 = new Gson().fromJson(collectionInfo.getData(), CollectAtMessage.class);
                    txt = bean3.getMsg();
                } else if (type == ChatEnum.EMessageType.MSG_VIDEO) {
                    CollectVideoMessage bean4 = new Gson().fromJson(collectionInfo.getData(), CollectVideoMessage.class);
                    imageUrl = bean4.getVideoBgURL();
                } else if (type == ChatEnum.EMessageType.LOCATION) {
                    CollectLocationMessage bean5 = new Gson().fromJson(collectionInfo.getData(), CollectLocationMessage.class);
                    txt = "[位置]" + bean5.getAddr();
                    imageUrl = LocationUtils.getLocationUrl(bean5.getLat(), bean5.getLon());
                } else if (type == ChatEnum.EMessageType.SHIPPED_EXPRESSION) {
                    CollectShippedExpressionMessage bean6 = new Gson().fromJson(collectionInfo.getData(), CollectShippedExpressionMessage.class);
                    imageUrl = bean6.getExpression();
                } else if (type == ChatEnum.EMessageType.FILE) {
                    CollectSendFileMessage bean7 = new Gson().fromJson(collectionInfo.getData(), CollectSendFileMessage.class);
                    txt = "[文件]" + bean7.getFileName();
                }
            }
        } else if (model == ChatEnum.EForwardMode.ONE_BY_ONE) {
            if (msgList == null) {
                return;
            }
            txt = "[逐条转发]共" + msgList.size() + "条消息";
        } else if (model == ChatEnum.EForwardMode.MERGE) {
            txt = "[合并转发]";
        } else if (model == ChatEnum.EForwardMode.SYS_SEND_MULTI) {
            if (msgList == null) {
                return;
            }
        }

        alertForward.init(MsgForwardActivity.this, type, mIcon, mName, txt, imageUrl, btm, toGid, new AlertForward.Event() {
            @Override
            public void onON() {

            }

            @Override
            public void onYes(String content) {
                if (model == ChatEnum.EForwardMode.DEFAULT || model == ChatEnum.EForwardMode.MERGE) {
                    //默认转发
                    if (fromCollect == false) {
                        send(msgAllBean, content, toUid, toGid);
                    } else {
                        //收藏转发
                        send(collectionInfo, content, toUid, toGid);
                    }

//                    doSendSuccess();
                } else if (model == ChatEnum.EForwardMode.ONE_BY_ONE) {
                    if (msgList != null) {
                        int len = msgList.size();
                        if (len > 0) {
                            for (int i = 0; i < len; i++) {
                                MsgAllBean msg = msgList.get(i);
                                send(msg, content, toUid, toGid);
                            }
                        }
                    }
                } else if (model == ChatEnum.EForwardMode.SYS_SEND) {
                    UpFileAction.PATH uploadType = getUploadType(mediaType);
                    if (uploadType != null) {
                        upload(filePath, uploadType, msgAllBean);
                    } else {
                        ToastUtil.show(MsgForwardActivity.this, "分享失败，不支持文件类型");
                        return;
                    }

                } else if (model == ChatEnum.EForwardMode.SYS_SEND_MULTI) {
                    UpFileAction.PATH uploadType = getUploadType(mediaType);
                    if (uploadType != null) {
                        if (msgList != null) {
//                            for (int i = 0; i < msgList.size(); i++) {
//                                MsgAllBean msg = msgList.get(i);
//                                upload(msg.getImage().getLocalimg(), uploadType, msg);
//                            }
                            showSendProgress(1, msgList.size(), 0);
                            MsgAllBean msg = msgList.get(0);
                            upload(msg.getImage().getLocalimg(), uploadType, msg, 0);
                        } else {
                            ToastUtil.show(MsgForwardActivity.this, "分享失败，数据异常");
                        }
                    } else {
                        ToastUtil.show(MsgForwardActivity.this, "分享失败，不支持文件类型");
                        return;
                    }

                } else if (model == ChatEnum.EForwardMode.SHARE) {
                    if (mediaType == CxMediaMessage.EMediaType.TEXT || mediaType == CxMediaMessage.EMediaType.WEB) {
                        send(msgAllBean, content, toUid, toGid);
                    } else if (mediaType == CxMediaMessage.EMediaType.IMAGE) {
                        if (!TextUtils.isEmpty(shareImageUrl)) {
                            if (isHttp(shareImageUrl)) {
                                send(msgAllBean, content, toUid, toGid);
                            } else {
                                upload(shareImageUrl, UpFileAction.PATH.IMG, msgAllBean);
                            }
                        }
                    } else if (mediaType == CxMediaMessage.EMediaType.FILE) {
//                        if (!TextUtils.isEmpty(shareImageUrl)) {
//                            if (isHttp(shareImageUrl)) {
//                                send(msgAllBean, content, toUid, toGid);
//                            } else {
//                                upload(shareImageUrl, UpFileAction.PATH.IMG, msgAllBean);
//                            }
//                        }
                    }
                } else if (model == ChatEnum.EForwardMode.EDIT_PIC) {
                    UpFileAction.PATH uploadType = getUploadType(mediaType);
                    if (uploadType != null) {
                        //上传编辑后的图片，发送图片消息+留言消息
                        upload(editPicPath, uploadType, msgAllBean);
                        sendLeaveMessage(content, toUid, toGid);
                    } else {
                        ToastUtil.show(MsgForwardActivity.this, "分享失败，不支持文件类型");
                        return;
                    }

                }
            }
        });

        alertForward.show();

    }

    //处理逻辑
    private void send(MsgAllBean msgAllBean, String content, long toUid, String toGid) {
        if (msgAllBean.getChat() != null) {//转换文字
            if (isSingleSelected) {
                ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), msgAllBean.getChat().getMsg());
                MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                if (allBean != null) {
                    sendMessage(allBean);
                }
                sendLeaveMessage(content, toUid, toGid);
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);

                    ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), msgAllBean.getChat().getMsg());
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                    if (allBean != null) {
                        sendMessageForMulti(allBean);
                    }
                    sendLeaveMessageForMulti(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
        } else if (msgAllBean.getImage() != null) {
            if (isSingleSelected) {
                ImageMessage imagesrc = msgAllBean.getImage();
                if (msgAllBean.getFrom_uid() == UserAction.getMyId().longValue()) {
                    imagesrc.setReadOrigin(true);
                }
                ImageMessage imageMessage = SocketData.createImageMessage(SocketData.getUUID(), imagesrc.getOrigin(), imagesrc.getPreview(), imagesrc.getThumbnail(), imagesrc.getWidth(), imagesrc.getHeight(), !TextUtils.isEmpty(imagesrc.getOrigin()), imagesrc.isReadOrigin(), imagesrc.getSize());
                MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), imageMessage);
                if (allBean != null) {
                    sendMessage(allBean);
                }
                sendLeaveMessage(content, toUid, toGid);
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);
                    ImageMessage imagesrc = msgAllBean.getImage();
                    if (msgAllBean.getFrom_uid() == UserAction.getMyId().longValue()) {
                        imagesrc.setReadOrigin(true);
                    }
                    ImageMessage imageMessage = SocketData.createImageMessage(SocketData.getUUID(), imagesrc.getOrigin(), imagesrc.getPreview(), imagesrc.getThumbnail(), imagesrc.getWidth(), imagesrc.getHeight(), !TextUtils.isEmpty(imagesrc.getOrigin()), imagesrc.isReadOrigin(), imagesrc.getSize());
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), imageMessage);
                    if (allBean != null) {
                        sendMessage(allBean);
                    }
                    sendLeaveMessage(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
        } else if (msgAllBean.getAtMessage() != null) {
            if (isSingleSelected) {
                ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), msgAllBean.getAtMessage().getMsg());
                MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                if (allBean != null) {
                    sendMessage(allBean);
                }
                sendLeaveMessage(content, toUid, toGid);
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);

                    ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), msgAllBean.getAtMessage().getMsg());
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                    if (allBean != null) {
                        sendMessage(allBean);
                    }
                    sendLeaveMessage(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
        } else if (msgAllBean.getVideoMessage() != null) {
            if (isSingleSelected) {
                VideoMessage video = msgAllBean.getVideoMessage();
                VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), video.getBg_url(), video.getUrl(), video.getDuration(), video.getWidth(), video.getHeight(), video.isReadOrigin());
                MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), videoMessage);
                if (allBean != null) {
                    sendMessage(allBean);
                }
                sendLeaveMessage(content, toUid, toGid);
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);
                    VideoMessage video = msgAllBean.getVideoMessage();
                    VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), video.getBg_url(), video.getUrl(), video.getDuration(), video.getWidth(), video.getHeight(), video.isReadOrigin());
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), videoMessage);
                    if (allBean != null) {
                        sendMessage(allBean);
                    }
                    sendLeaveMessage(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
        } else if (msgAllBean.getLocationMessage() != null) {

            if (isSingleSelected) {
                LocationMessage location = msgAllBean.getLocationMessage();
                LocationMessage locationMessage = SocketData.createLocationMessage(SocketData.getUUID(), location);
                MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), locationMessage);
                if (allBean != null) {
                    sendMessage(allBean);
                }
                sendLeaveMessage(content, toUid, toGid);
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);

                    LocationMessage locationMessage = SocketData.createLocationMessage(SocketData.getUUID(), msgAllBean.getLocationMessage());
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL,
                            SocketData.getFixTime(), locationMessage);
                    if (allBean != null) {
                        sendMessage(allBean);
                    }
                    sendLeaveMessage(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
        } else if (msgAllBean.getShippedExpressionMessage() != null) {
            if (isSingleSelected) {
                ShippedExpressionMessage message = SocketData.createFaceMessage(SocketData.getUUID(), msgAllBean.getShippedExpressionMessage().getId());
                MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.SHIPPED_EXPRESSION, ChatEnum.ESendStatus.NORMAL,
                        SocketData.getFixTime(), message);
                if (allBean != null) {
                    sendMessage(allBean);
                }
                sendLeaveMessage(content, toUid, toGid);
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);
                    ShippedExpressionMessage chatMessage = SocketData.createFaceMessage(SocketData.getUUID(), msgAllBean.getShippedExpressionMessage().getId());
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), ChatEnum.EMessageType.SHIPPED_EXPRESSION,
                            ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                    if (allBean != null) {
                        sendMessage(allBean);
                    }
                    sendLeaveMessage(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
        } else if (msgAllBean.getSendFileMessage() != null) { //转发文件消息
            //文件分为两种情况：转发他人/自己转发自己，转发他人的文件需要下载，转发自己的文件直接从本地查找
            boolean isFromOther;
            //如果是自己转发自己的文件
            if (msgAllBean.getFrom_uid() == UserAction.getMyId().longValue()) {
                isFromOther = false;
            } else {
                isFromOther = true;
            }
            if (isSingleSelected) {
                SendFileMessage fileMessage = SocketData.createFileMessage(SocketData.getUUID(), msgAllBean.getSendFileMessage().getLocalPath(), msgAllBean.getSendFileMessage().getUrl(), msgAllBean.getSendFileMessage().getFile_name(), msgAllBean.getSendFileMessage().getSize(), msgAllBean.getSendFileMessage().getFormat(), isFromOther);
                MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), fileMessage);
                if (allBean != null) {
                    sendMessage(allBean);
                }
                sendLeaveMessage(content, toUid, toGid);
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);
                    SendFileMessage fileMessage = SocketData.createFileMessage(SocketData.getUUID(), msgAllBean.getSendFileMessage().getLocalPath(), msgAllBean.getSendFileMessage().getUrl(), msgAllBean.getSendFileMessage().getFile_name(), msgAllBean.getSendFileMessage().getSize(), msgAllBean.getSendFileMessage().getFormat(), isFromOther);
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), fileMessage);
                    if (allBean != null) {
                        sendMessage(allBean);
                        sendMessage = allBean;
                    }
                    sendLeaveMessage(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
        } else if (msgAllBean.getWebMessage() != null) { //分享web消息
            if (isSingleSelected) {
                WebMessage webMessage = SocketData.createWebMessage(SocketData.getUUID(), appName, appIcon, shareTitle, shareDescription, shareWebUrl);
                MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), webMessage);
                if (allBean != null) {
                    sendMessage(allBean);
                }
                sendLeaveMessage(content, toUid, toGid);
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);
                    WebMessage webMessage = SocketData.createWebMessage(SocketData.getUUID(), appName, appIcon, shareTitle, shareDescription, shareWebUrl);
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), webMessage);
                    if (allBean != null) {
                        sendMessage(allBean);
                        sendMessage = allBean;
                    }
                    sendLeaveMessage(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
        } else if (msgAllBean.getReplyMessage() != null) { //回复消息
            if (isSingleSelected) {
                ReplyMessage replyMessage = msgAllBean.getReplyMessage();
                String msg = "";
                if (replyMessage.getChatMessage() != null) {
                    msg = replyMessage.getChatMessage().getMsg();
                } else if (replyMessage.getAtMessage() != null) {
                    msg = replyMessage.getAtMessage().getMsg();
                }
                if (!TextUtils.isEmpty(msg)) {
                    ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), msg);
                    MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                    if (allBean != null) {
                        sendMessage(allBean);
                    }
                    sendLeaveMessage(content, toUid, toGid);
                }
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);
                    ReplyMessage replyMessage = msgAllBean.getReplyMessage();
                    String msg = "";
                    if (replyMessage.getChatMessage() != null) {
                        msg = replyMessage.getChatMessage().getMsg();
                    } else if (replyMessage.getAtMessage() != null) {
                        msg = replyMessage.getAtMessage().getMsg();
                    }
                    if (!TextUtils.isEmpty(msg)) {
                        ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), msg);
                        MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                        if (allBean != null) {
                            sendMessage(allBean);
                        }
                        sendLeaveMessage(content, bean.getUid(), bean.getGid());
                    }
                }
                isSingleSelected = true;
            }
        }
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

    /*
     * 发送留言消息
     * */
    private void sendLeaveMessageForMulti(String content, long toUid, String toGid) {
        if (StringUtil.isNotNull(content)) {
            ChatMessage chat = SocketData.createChatMessage(SocketData.getUUID(), content);
            MsgAllBean messageBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chat);
            if (messageBean != null) {
                sendMessageForMulti(messageBean);
            }
        }
    }

    public void doSendSuccess() {
        ToastUtil.show(this, getResources().getString(R.string.forward_success));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                finish();
            }
        }
    }

    public static void addOrDeleteMoreSessionBeanList(boolean isAdd, long uid, String gid, String avatar, String nick) {
        boolean has = false;
        int hasInt = -1;
        for (int i = 0; i < moreSessionBeanList.size(); i++) {
            if (StringUtil.isNotNull(gid) && uid == moreSessionBeanList.get(i).getUid() && gid.equals(moreSessionBeanList.get(i).getGid())) {
                has = true;
                hasInt = i;
            } else if (!StringUtil.isNotNull(gid) && uid == moreSessionBeanList.get(i).getUid()) {
                has = true;
                hasInt = i;
            }
        }

        if (isAdd && !has) {
            MoreSessionBean bean = new MoreSessionBean();
            bean.setUid(uid);
            bean.setGid(gid);
            bean.setAvatar(avatar);
            bean.setNick(nick);
            moreSessionBeanList.add(bean);
        } else if (!isAdd && hasInt > -1) {
            moreSessionBeanList.remove(hasInt);
        }
        EventBus.getDefault().post(new SelectNumbEvent(moreSessionBeanList.size() + ""));
    }

    public static Boolean findMoreSessionBeanList(long uid, String gid) {
        for (int i = 0; i < moreSessionBeanList.size(); i++) {
            if (StringUtil.isNotNull(gid) && uid == moreSessionBeanList.get(i).getUid() && gid.equals(moreSessionBeanList.get(i).getGid())) {
                return true;
            } else if (!StringUtil.isNotNull(gid) && uid == moreSessionBeanList.get(i).getUid()) {
                return true;
            }
        }
        return false;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void posting(SelectNumbEvent event) {
        if ("0".equals(event.type)) {
            actionbar.setTxtRight("完成");
        } else {
            actionbar.setTxtRight("完成(" + event.type + ")");
        }
    }

    //获取系统相册分享的图片(暂时仅支持单张图片分享)
    private void getSysImgShare() {
        //TODO 担心有权限问题，加一层保险起见
        permission2Util.requestPermissions(MsgForwardActivity.this, new CheckPermission2Util.Event() {
            @Override
            public void onSuccess() {
                Intent intent = getIntent();
                Bundle extras = intent.getExtras();
                String type = intent.getType();
                mediaType = getMediaType(type);
                if (mediaType == CxMediaMessage.EMediaType.UNKNOWN) {
                    return;
                }
                try {
                    if (model == ChatEnum.EForwardMode.SYS_SEND) {
                        Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
                        filePath = FileUtils.getFilePathByUri(MsgForwardActivity.this, uri);
                        if (TextUtils.isEmpty(filePath)) {
                            ToastUtil.show("路径解析异常，分享失败");
                        } /*else {
                            if (mediaType == CxMediaMessage.EMediaType.IMAGE) {
                                PicImgSizeUtil.ImageSize imgSize = PicImgSizeUtil.getAttribute(filePath);
                                if (imgSize == null) {
                                    filePath = "";
                                    return;
                                } else {
                                    if (imgSize.getWidth() > 4096 || imgSize.getHeight() > 4096) {
                                        filePath = "";
                                        ToastUtil.show("图片过大，发送失败");
                                    }
                                }
                            }
                        }*/
                    } else if (model == ChatEnum.EForwardMode.SYS_SEND_MULTI) {
                        List<Uri> uriList = extras.getParcelableArrayList(Intent.EXTRA_STREAM);
                        shareUrls = FileUtils.getUrisForList(MsgForwardActivity.this, uriList);
                    }
                } catch (Exception e) {
                    LogUtil.getLog().e(e.toString());
                    ToastUtil.show("路径解析异常，分享失败");
                }
            }

            @Override
            public void onFail() {
                ToastUtil.show("需要同意访问权限");
            }
        }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permission2Util.onRequestPermissionsResult();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void upload(String file, UpFileAction.PATH type, MsgAllBean msg) {
        if (TextUtils.isEmpty(file)) {
            return;
        }
        LogUtil.getLog().i("分享", file);
        uploadFile(file, msg, type, new UpLoadService.UpLoadCallback() {
            @Override
            public void success(String url) {
                if (!TextUtils.isEmpty(url)) {
                    switch (type) {
                        case IMG:
                            ImageMessage image = msg.getImage();
                            ImageMessage imageMessage = SocketData.createImageMessage(image.getMsgId(), image.getLocalimg(), url, image.getWidth(), image.getHeight(), true, true, image.getSize());
                            msg.setImage(imageMessage);
                            sendMessage(msg);
                            break;
                        case FILE:
                            SendFileMessage file = msg.getSendFileMessage();
                            SendFileMessage fileMessage = SocketData.createFileMessage(file.getMsgId(), file.getLocalPath(), url, file.getFile_name(), file.getSize(), file.getFormat(), false);
                            msg.setSendFileMessage(fileMessage);
                            sendMessage(msg);
                            break;

                    }
                }
            }

            @Override
            public void fail() {

            }
        });
    }


    //上传文件，图片文件等
    public void uploadFile(String file, MsgAllBean msgAllBean, UpFileAction.PATH type, UpLoadService.UpLoadCallback upLoadCallback) {
//        LogUtil.getLog().i("分享uploadFile", file);
        UpFileAction upFileAction = new UpFileAction();
        upFileAction.upFile(type, this, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                upLoadCallback.success(url);
            }

            @Override
            public void fail() {
                upLoadCallback.fail();
            }

            @Override
            public void inProgress(long progress, long zong) {
                int pg = new Double(progress / (zong + 0.0f) * 100.0).intValue();
                updateSendProgress(msgAllBean, pg);
            }
        }, file);
    }

    private void sendMessage(MsgAllBean msgAllBean) {
        if (isWaitModel()) {
            addQueue(msgAllBean);
        }
        SocketData.sendAndSaveMessage(msgAllBean);
        sendMessage = msgAllBean;
    }

    private synchronized void addQueue(MsgAllBean msgAllBean) {
        if (!sendQueue.containsKey(msgAllBean.getMsg_id())) {
            sendQueue.put(msgAllBean.getMsg_id(), msgAllBean);
            LogUtil.getLog().i("转发", "--add--" + msgAllBean.getMsg_id());

        }
    }

    //多选发送时需要添加进发送队列
    private void sendMessageForMulti(MsgAllBean msgAllBean) {
        addQueue(msgAllBean);
        SocketData.sendAndSaveMessage(msgAllBean);
        sendMessage = msgAllBean;
    }

    //等待发送成功
    private boolean isWaitModel() {
        if (model == ChatEnum.EForwardMode.SHARE || model == ChatEnum.EForwardMode.SYS_SEND || model == ChatEnum.EForwardMode.SYS_SEND_MULTI
                || model == ChatEnum.EForwardMode.EDIT_PIC /*|| model == ChatEnum.EForwardMode.DEFAULT || model == ChatEnum.EForwardMode.MERGE*/) {
            return true;
        }
        return false;
    }

    private void showShareDialog() {
        ShareDialog dialog = new ShareDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.initOtherAppName("微信")
                .setListener(new ShareDialog.IDialogListener() {
                    @Override
                    public void onLeft() {
                        setResult(RESULT_OK);
                        MsgForwardActivity.this.finish();
                    }

                    @Override
                    public void onRight() {
                        //留在常信
                        startActivity(new Intent(MsgForwardActivity.this, MainActivity.class));
                    }
                }).show();
    }

    /**
     * 是否是网络图片
     *
     * @param path
     * @return
     */
    public boolean isHttp(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith("http")
                    || path.startsWith("https")) {
                return true;
            }
        }
        return false;
    }

    @CxMediaMessage.EMediaType
    private int getMediaType(String type) {
        if (!TextUtils.isEmpty(type)) {
            if (type.startsWith("image/")) {
                return CxMediaMessage.EMediaType.IMAGE;
            } else if (type.startsWith("text/") || type.startsWith("application/")) {
                return CxMediaMessage.EMediaType.FILE;
            }
        }
        return CxMediaMessage.EMediaType.UNKNOWN;
    }

    private UpFileAction.PATH getUploadType(int mediaType) {
        if (mediaType == CxMediaMessage.EMediaType.IMAGE) {
            return UpFileAction.PATH.IMG;
        } else if (mediaType == CxMediaMessage.EMediaType.FILE) {
            return UpFileAction.PATH.FILE;
        }
        return null;
    }


    private List<MsgAllBean> getMsgList(List<String> urls, long uid, String gid) {
        if (urls == null) {
            return null;
        }
        int len = urls.size();
        if (len > 0) {
            List<MsgAllBean> list = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                String url = urls.get(i);
                ImgSizeUtil.ImageSize imgSize = ImgSizeUtil.getAttribute(url);
                if (imgSize == null) {
                    continue;
                }
                ImageMessage image = SocketData.createImageMessage(SocketData.getUUID(), url, "", imgSize.getWidth(), imgSize.getHeight(), true, false, imgSize.getSize());
                MsgAllBean msgAllBean = SocketData.createMessageBean(uid, gid, ChatEnum.EMessageType.IMAGE, ChatEnum.ESendStatus.PRE_SEND, SocketData.getFixTime(), image);
                if (msgAllBean != null) {
                    list.add(msgAllBean);
                }
            }
            return list;
        }
        return null;
    }

    private void upload(String file, UpFileAction.PATH type, MsgAllBean msg, final int position) {
        if (TextUtils.isEmpty(file)) {
            return;
        }
//        LogUtil.getLog().i("分享", file);
        uploadFile(file, msg, type, new UpLoadService.UpLoadCallback() {
            @Override
            public void success(String url) {
                if (!TextUtils.isEmpty(url)) {
                    switch (type) {
                        case IMG:
                            ImageMessage image = msg.getImage();
                            ImageMessage imageMessage = SocketData.createImageMessage(image.getMsgId(), image.getLocalimg(), url, image.getWidth(), image.getHeight(), true, true, image.getSize());
                            msg.setImage(imageMessage);
                            sendMessage(msg);
                            break;
                        case FILE:
                            break;

                    }
                    if (model == ChatEnum.EForwardMode.SYS_SEND_MULTI) {
                        if (msgList != null && position < msgList.size() - 1) {
                            int newP = position + 1;
                            MsgAllBean msgAllBean = msgList.get(newP);
                            updateSendProgress(msgAllBean, 0);
                            upload(msgAllBean.getImage().getLocalimg(), type, msgAllBean, newP);
                        }
                    }
                }
            }

            @Override
            public void fail() {

            }
        });
    }

    private void showSendProgress(int position, int count, int progress) {
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                dialogSendProgress = new DialogCommon2(MsgForwardActivity.this)
                        .setContent(getProgressText(position, count, progress), false)
                        .setListener(new DialogCommon2.IDialogListener() {
                            @Override
                            public void onClick() {
                                if (msgList != null) {
                                    msgList.clear();
                                }
                            }
                        });
                dialogSendProgress.show();
            }
        });
    }

    private void updateSendProgress(MsgAllBean msgAllBean, int progress) {
        if (msgList == null || msgAllBean == null || dialogSendProgress == null) {
            return;
        }
        int index = msgList.indexOf(msgAllBean);
        if (index < 0) {
            return;
        }
        int len = msgList.size();
        int position = index + 1;
        if (prePosition == position) {
            if (progress > preProgress) {
                preProgress = progress;
            } else {
                return;
            }
        } else {
            prePosition = position;
            preProgress = progress;
        }
        if (dialogSendProgress.isShowing()) {
            actionbar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialogSendProgress != null) {
                        dialogSendProgress.setContent(getProgressText(position, len, progress), false);
                    }
                }
            }, 100);
        }

    }

    private String getProgressText(int position, int count, int progress) {
        LogUtil.getLog().i("分享", position + "/" + count + "--进度==" + progress);
        return "正在发送图片（" + position + "/" + count + "): " + progress + "%";
    }


    public ForwardViewModel getViewModel() {
        return viewModel;
    }

    //处理逻辑-收藏转发
    private void send(CollectionInfo collectionInfo, String content, long toUid, String toGid) {
        int type = CommonUtils.transformMsgType(collectionInfo.getType());
        if (type == ChatEnum.EMessageType.TEXT) {//转换文字
            CollectChatMessage bean1 = new Gson().fromJson(collectionInfo.getData(), CollectChatMessage.class);
            if (isSingleSelected) {
                ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), bean1.getMsg());
                MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, type, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                if (allBean != null) {
                    sendMessage(allBean);
                }
                sendLeaveMessage(content, toUid, toGid);
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);

                    ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), bean1.getMsg());
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), type, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                    if (allBean != null) {
                        sendMessageForMulti(allBean);
                    }
                    sendLeaveMessageForMulti(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
        } else if (type == ChatEnum.EMessageType.IMAGE) {
            CollectImageMessage bean2 = new Gson().fromJson(collectionInfo.getData(), CollectImageMessage.class);
            if (isSingleSelected) {
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
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);
                    CollectImageMessage imagesrc = bean2;
                    if (collectionInfo.getFromUid() == UserAction.getMyId().longValue()) {
                        imagesrc.setReadOrigin(true);
                    }
                    ImageMessage imageMessage = SocketData.createImageMessage(SocketData.getUUID(), imagesrc.getOrigin(), imagesrc.getPreview(), imagesrc.getThumbnail(), imagesrc.getWidth(), imagesrc.getHeight(), !TextUtils.isEmpty(imagesrc.getOrigin()), imagesrc.isReadOrigin(), imagesrc.getSize());
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), type, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), imageMessage);
                    if (allBean != null) {
                        sendMessage(allBean);
                    }
                    sendLeaveMessage(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
        } else if (type == ChatEnum.EMessageType.AT) {
            CollectAtMessage bean3 = new Gson().fromJson(collectionInfo.getData(), CollectAtMessage.class);
            if (isSingleSelected) {
                ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), bean3.getMsg());
                MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                if (allBean != null) {
                    sendMessage(allBean);
                }
                sendLeaveMessage(content, toUid, toGid);
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);

                    ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), bean3.getMsg());
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                    if (allBean != null) {
                        sendMessage(allBean);
                    }
                    sendLeaveMessage(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
        } else if (type == ChatEnum.EMessageType.MSG_VIDEO) {
            CollectVideoMessage bean4 = new Gson().fromJson(collectionInfo.getData(), CollectVideoMessage.class);
            if (isSingleSelected) {
                CollectVideoMessage video = bean4;
                VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), video.getVideoBgURL(), video.getVideoURL(), video.getVideoDuration(), video.getWidth(), video.getHeight(), video.isReadOrigin());
                MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, type, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), videoMessage);
                if (allBean != null) {
                    sendMessage(allBean);
                }
                sendLeaveMessage(content, toUid, toGid);
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);
                    CollectVideoMessage video = bean4;
                    VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), video.getVideoBgURL(), video.getVideoURL(), video.getVideoDuration(), video.getWidth(), video.getHeight(), video.isReadOrigin());
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), type, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), videoMessage);
                    if (allBean != null) {
                        sendMessage(allBean);
                    }
                    sendLeaveMessage(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
        } else if (type == ChatEnum.EMessageType.LOCATION) {
            CollectLocationMessage bean5 = new Gson().fromJson(collectionInfo.getData(), CollectLocationMessage.class);
            if (isSingleSelected) {
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
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);
                    LocationMessage locationMessage = new LocationMessage();
                    locationMessage.setMsgId(SocketData.getUUID());
                    locationMessage.setLatitude(bean5.getLat());
                    locationMessage.setLongitude(bean5.getLon());
                    locationMessage.setImg(bean5.getImg());
                    locationMessage.setAddress(bean5.getAddr());
                    locationMessage.setAddressDescribe(bean5.getAddressDesc());
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), type, ChatEnum.ESendStatus.NORMAL,
                            SocketData.getFixTime(), locationMessage);
                    if (allBean != null) {
                        sendMessage(allBean);
                    }
                    sendLeaveMessage(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
        } else if (type == ChatEnum.EMessageType.SHIPPED_EXPRESSION) {
            CollectShippedExpressionMessage bean6 = new Gson().fromJson(collectionInfo.getData(), CollectShippedExpressionMessage.class);
            if (isSingleSelected) {
                ShippedExpressionMessage message = SocketData.createFaceMessage(SocketData.getUUID(), bean6.getExpression());
                MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.SHIPPED_EXPRESSION, ChatEnum.ESendStatus.NORMAL,
                        SocketData.getFixTime(), message);
                if (allBean != null) {
                    sendMessage(allBean);
                }
                sendLeaveMessage(content, toUid, toGid);
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);
                    ShippedExpressionMessage chatMessage = SocketData.createFaceMessage(SocketData.getUUID(), bean6.getExpression());
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), ChatEnum.EMessageType.SHIPPED_EXPRESSION,
                            ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), chatMessage);
                    if (allBean != null) {
                        sendMessage(allBean);
                    }
                    sendLeaveMessage(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
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
            if (isSingleSelected) {
                SendFileMessage fileMessage = SocketData.createFileMessage(SocketData.getUUID(), bean8.getCollectLocalPath(), bean8.getFileURL(), bean8.getFileName(), bean8.getFileSize(), bean8.getFileFormat(), isFromOther);
                MsgAllBean allBean = SocketData.createMessageBean(toUid, toGid, type, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), fileMessage);
                if (allBean != null) {
                    sendMessage(allBean);
                }
                sendLeaveMessage(content, toUid, toGid);
            } else {
                for (int i = 0; i < moreSessionBeanList.size(); i++) {
                    MoreSessionBean bean = moreSessionBeanList.get(i);
                    SendFileMessage fileMessage = SocketData.createFileMessage(SocketData.getUUID(), bean8.getCollectLocalPath(), bean8.getFileURL(), bean8.getFileName(), bean8.getFileSize(), bean8.getFileFormat(), isFromOther);
                    MsgAllBean allBean = SocketData.createMessageBean(bean.getUid(), bean.getGid(), type, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), fileMessage);
                    if (allBean != null) {
                        sendMessage(allBean);
                        sendMessage = allBean;
                    }
                    sendLeaveMessage(content, bean.getUid(), bean.getGid());
                }
                isSingleSelected = true;
            }
        }
    }
}
