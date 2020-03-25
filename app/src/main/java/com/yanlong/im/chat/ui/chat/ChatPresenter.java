package com.yanlong.im.chat.ui.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.controll.AVChatProfile;
import com.example.nim_lib.ui.VideoActivity;
import com.hm.cxpay.bean.CxEnvelopeBean;
import com.hm.cxpay.bean.TransferDetailBean;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.dailog.ChangeSelectDialog;
import com.hm.cxpay.dailog.DialogDefault;
import com.hm.cxpay.global.PayEnum;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.payword.SetPaywordActivity;
import com.hm.cxpay.ui.redenvelope.MultiRedPacketActivity;
import com.hm.cxpay.ui.redenvelope.SingleRedPacketActivity;
import com.hm.cxpay.ui.transfer.TransferActivity;
import com.hm.cxpay.ui.transfer.TransferDetailActivity;
import com.jrmf360.rplib.JrmfRpClient;
import com.jrmf360.rplib.bean.EnvelopeBean;
import com.jrmf360.rplib.bean.GrabRpBean;
import com.jrmf360.rplib.bean.TransAccountBean;
import com.jrmf360.rplib.utils.callback.GrabRpCallBack;
import com.jrmf360.rplib.utils.callback.TransAccountCallBack;
import com.jrmf360.tools.utils.ThreadUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DateUtils;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.yalantis.ucrop.util.FileUtils;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.EventSurvivalTimeAdd;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.AtMessage;
import com.yanlong.im.chat.bean.BusinessCardMessage;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.EnvelopeInfo;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.GroupConfig;
import com.yanlong.im.chat.bean.IMsgContent;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.LocationMessage;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.bean.RedEnvelopeMessage;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.ShippedExpressionMessage;
import com.yanlong.im.chat.bean.StampMessage;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.eventbus.AckEvent;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.server.ChatServer;
import com.yanlong.im.chat.server.UpLoadService;
import com.yanlong.im.chat.ui.GroupRobotActivity;
import com.yanlong.im.chat.ui.GroupSelectUserActivity;
import com.yanlong.im.location.LocationActivity;
import com.yanlong.im.location.LocationSendEvent;
import com.yanlong.im.pay.action.PayAction;
import com.yanlong.im.pay.bean.SignatureBean;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.ui.SelectUserActivity;
import com.yanlong.im.user.ui.ServiceAgreementActivity;
import com.yanlong.im.utils.BurnManager;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.GroupHeadImageUtil;
import com.yanlong.im.utils.PatternUtil;
import com.yanlong.im.utils.audio.AudioPlayManager;
import com.yanlong.im.utils.audio.IVoicePlayListener;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;
import com.yanlong.im.view.face.AddFaceActivity;
import com.yanlong.im.view.face.FaceView;
import com.yanlong.im.view.face.bean.FaceBean;
import com.zhaoss.weixinrecorded.activity.RecordedActivity;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.base.BasePresenter;
import net.cb.cb.library.base.DBOptionObserver;
import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.bean.EventFindHistory;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.EventUpImgLoadEvent;
import net.cb.cb.library.bean.EventUserOnlineChange;
import net.cb.cb.library.bean.EventVoicePlay;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.dialog.DialogEnvelopePast;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.inter.ICustomerItemClick;
import net.cb.cb.library.manager.Constants;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.DialogHelper;
import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.GsonUtils;
import net.cb.cb.library.utils.ImgSizeUtil;
import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.RunUtils;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.AlertTouch;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.MsgEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @author Liszt
 * @date 2019/9/19
 * Description
 */
public class ChatPresenter extends BasePresenter<ChatModel, ChatView> implements SocketEvent {
    private final String TAG = "ChatActivity";
    public final static int MIN_TEXT = 1000;//
    //红包和转账
    public static final int REQ_RP = 9653;
    public static final int REQ_TRANS = 9653;
    public static final int VIDEO_RP = 9419;
    public static final int REQUEST_RED_ENVELOPE = 1 << 2;


    private List<String> sendTexts;//文本分段发送
    private List<MsgAllBean> downloadList = new ArrayList<>();//下载列表
    private Map<String, MsgAllBean> uploadMap = new HashMap<>();//上传列表
    private List<MsgAllBean> uploadList = new ArrayList<>();//上传列表
    private Context context;
    private ChatActivity3 activity;
    private boolean isSendingHypertext;
    private int textPosition;
    private final PayAction payAction = new PayAction();
    private boolean needRefresh;
    private CheckPermission2Util permission2Util = new CheckPermission2Util();
    private MsgDao msgDao = new MsgDao();

    private ChangeSelectDialog.Builder builder;
    private ChangeSelectDialog dialogOne;//通用提示选择弹框：实名认证

    public void init(Context con) {
        context = con;
        activity = (ChatActivity3) con;
        builder = new ChangeSelectDialog.Builder(activity);
    }

    /*
     * 整个刷新数据
     * */
    public void loadAndSetData() {
        if (needRefresh) {
            needRefresh = false;
        }
        Observable<List<MsgAllBean>> observable = model.loadMessages();
        observable.compose(RxSchedulers.<List<MsgAllBean>>compose())
                .compose(RxSchedulers.<List<MsgAllBean>>handleResult())
                .subscribe(new DBOptionObserver<List<MsgAllBean>>() {
                    @Override
                    public void onOptionSuccess(List<MsgAllBean> list) {
                        getView().setAndRefreshData(list);
                    }
                });
    }

    public void checkLockMessage() {
        model.checkLockMessage();
    }

    public void registerIMListener() {
        SocketUtil.getSocketUtil().addEvent(this);
    }

    public void unregisterIMListener() {
        SocketUtil.getSocketUtil().removeEvent(this);
    }

    @Override
    protected void onViewDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onViewStart() {
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void taskRefreshMessageEvent(EventRefreshChat event) {
        loadAndSetData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventExitChat event) {
        ((Activity) context).onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventUserOnlineChange event) {
        getView().updateOnlineStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventCheckVoice(EventVoicePlay event) {
        checkMoreVoice(event.getPosition(), (MsgAllBean) event.getBean());
    }

    //发送位置
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void locationSendEvent(LocationSendEvent event) {
        LocationMessage message = SocketData.createLocationMessage(SocketData.getUUID(), event.message);
        sendMessage(message, ChatEnum.EMessageType.LOCATION);
    }

    //处理ack
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doAck(AckEvent event) {
        LogUtil.getLog().i(TAG, "doAck--收到回执" + event.getData());
        if (event.getData() instanceof MsgAllBean) {//发送成功
            MsgAllBean msg = (MsgAllBean) event.getData();
            getView().replaceListDataAndNotify(msg);
        } else if (event.getData() instanceof MsgBean.AckMessage) {
            MsgBean.AckMessage ackMessage = (MsgBean.AckMessage) event.getData();
            if (ackMessage.getRejectType() != MsgBean.RejectType.ACCEPTED) {//发送失败
                loadAndSetData();
            }
        }
        if (isSendingHypertext) {
            if (sendTexts != null && sendTexts.size() > 0 && textPosition != sendTexts.size() - 1) {
                sendHypertext(sendTexts, textPosition + 1);
            }
        }
    }


    /***
     * 查询历史
     * @param history
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void taskFinadHistoryMessage(EventFindHistory history) {
        Observable<List<MsgAllBean>> observable = model.loadHistoryMessages(history.getStime());
        observable.subscribe(new DBOptionObserver<List<MsgAllBean>>() {
            @Override
            public void onOptionSuccess(List<MsgAllBean> list) {
                getView().bindData(list, true);
                getView().scrollToPositionWithOff(0, 0);
            }
        });
    }

    @Override
    public void onHeartbeat() {

    }


    @Override
    public void onACK(MsgBean.AckMessage bean) {
        fixSendTime(bean.getMsgId(0));
        if (bean.getRejectType() == MsgBean.RejectType.NOT_FRIENDS_OR_GROUP_MEMBER || bean.getRejectType() == MsgBean.RejectType.IN_BLACKLIST) {
            loadAndSetData();
        } else {
            if (UpLoadService.getProgress(bean.getMsgId(0)) == null /*|| UpLoadService.getProgress(bean.getMsgId(0)) == 100*/) {//忽略图片上传的刷新,图片上传成功后
                for (String msgId : bean.getMsgIdList()) {
                    //撤回消息不做刷新
                    if (ChatServer.getCancelList().containsKey(msgId)) {
                        LogUtil.getLog().i(TAG, "onACK: 收到取消回执,等待刷新列表2");
                        return;
                    }
                }
            }
            loadAndSetData();
        }
        if (isSendingHypertext) {
            if (sendTexts != null && sendTexts.size() > 0 && textPosition != sendTexts.size() - 1) {
                sendHypertext(sendTexts, textPosition + 1);
            }
        }
    }

    @Override
    public void onMsg(MsgBean.UniversalMessage bean) {
        needRefresh = false;
        for (MsgBean.UniversalMessage.WrapMessage msg : bean.getWrapMsgList()) {
            //8.7 是属于这个会话就刷新
            if (!needRefresh) {
                if (model.isGroup()) {
                    needRefresh = msg.getGid().equals(model.getGid());
                } else {
                    needRefresh = msg.getFromUid() == model.getUid();
                }

                if (msg.getMsgType() == MsgBean.MessageType.OUT_GROUP) {//提出群的消息是以个人形式发的
                    needRefresh = msg.getOutGroup().getGid().equals(model.getGid());
                }
                if (msg.getMsgType() == MsgBean.MessageType.REMOVE_GROUP_MEMBER) {//提出群的消息是以个人形式发的
                    needRefresh = msg.getRemoveGroupMember().getGid().equals(model.getGid());
                }
            }
            onMsgBranch(msg);
        }
        //从数据库读取消息
        if (needRefresh) {
            loadAndSetData();
        }
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                initUnreadCount();
            }
        });
    }

    @Override
    public void onSendMsgFailure(MsgBean.UniversalMessage.Builder bean) {
        //撤回处理
        if (bean.getWrapMsg(0).getMsgType() == MsgBean.MessageType.CANCEL) {
            ToastUtil.show(context, "撤回失败");
            return;
        }
        MsgAllBean msgAllBean = MsgConversionBean.ToBean(bean.getWrapMsg(0), bean, true);
        if (msgAllBean.getMsg_type().intValue() == ChatEnum.EMessageType.MSG_CANCEL) {//取消的指令不保存到数据库
            return;
        }
        msgAllBean.setSend_state(ChatEnum.ESendStatus.ERROR);
        ///这里写库
        msgAllBean.setSend_data(bean.build().toByteArray());
        DaoUtil.update(msgAllBean);
        loadAndSetData();
    }

    @Override
    public void onLine(boolean state) {

    }

    public void initUnreadCount() {
        getView().initLeftUnreadCount(model.getLeftUnreadCount());
    }

    //重新发送消息
    private void resendMessage(MsgAllBean msgBean) {
        MsgAllBean reMsg = DaoUtil.findOne(MsgAllBean.class, "msg_id", msgBean.getMsg_id());
        try {
            LogUtil.getLog().d(TAG, "点击重复发送" + reMsg.getMsg_id() + "--" + reMsg.getTimestamp());
            if (reMsg.getMsg_type() == ChatEnum.EMessageType.IMAGE) {//图片重发处理7.31
                String file = reMsg.getImage().getLocalimg();
                if (!TextUtils.isEmpty(file)) {
                    boolean isArtworkMaster = StringUtil.isNotNull(reMsg.getImage().getOrigin()) ? true : false;
                    ImageMessage image = SocketData.createImageMessage(reMsg.getMsg_id(), file, isArtworkMaster);
//                    MsgAllBean imgMsgBean = SocketData.sendFileUploadMessagePre(reMsg.getMsg_id(), model.getUid(), model.getGid(), reMsg.getTimestamp(), image, ChatEnum.EMessageType.IMAGE);
//                    getView().replaceListDataAndNotify(imgMsgBean);
//                    UpLoadService.onAddImage(reMsg.getMsg_id(), file, isArtworkMaster, model.getUid(), model.getGid(), reMsg.getTimestamp());
                    MsgAllBean imageMsgBean = sendMessage(image, ChatEnum.EMessageType.IMAGE, false);
                    UpLoadService.onAddImage(imageMsgBean, file, isArtworkMaster);
                    getView().startUploadService();
                } else {
                    //点击发送的时候如果要改变成发送中的状态
                    reMsg.setSend_state(ChatEnum.ESendStatus.SENDING);
                    DaoUtil.update(reMsg);
                    MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(reMsg.getSend_data()).toBuilder();
                    SocketUtil.getSocketUtil().sendData4Msg(bean);
                    loadAndSetData();
                }
            } else if (reMsg.getMsg_type() == ChatEnum.EMessageType.VOICE) {
                String url = reMsg.getVoiceMessage().getLocalUrl();
                if (!TextUtils.isEmpty(url)) {
                    reMsg.setSend_state(ChatEnum.ESendStatus.PRE_SEND);
                    getView().replaceListDataAndNotify(reMsg);
                    uploadVoice(url, reMsg);
                } else {
                    //点击发送的时候如果要改变成发送中的状态
                    reMsg.setSend_state(ChatEnum.ESendStatus.SENDING);
                    DaoUtil.update(reMsg);
                    MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(reMsg.getSend_data()).toBuilder();
                    SocketUtil.getSocketUtil().sendData4Msg(bean);
                    getView().replaceListDataAndNotify(reMsg);
                }
            } else if (reMsg.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {
                //todo 重新上传视频
                String url = reMsg.getVideoMessage().getLocalUrl();
                reMsg.setSend_state(ChatEnum.ESendStatus.SENDING);
                if (!TextUtils.isEmpty(url)) {
                    VideoMessage videoMessage = reMsg.getVideoMessage();
                    LogUtil.getLog().e("TAG", videoMessage.toString() + videoMessage.getHeight() + "----" + videoMessage.getWidth() + "----" + videoMessage.getDuration() + "----" + videoMessage.getBg_url() + "----");
                    VideoMessage videoMessageSD = SocketData.createVideoMessage(reMsg.getMsg_id(), "file://" + url, videoMessage.getBg_url(), false, videoMessage.getDuration(), videoMessage.getWidth(), videoMessage.getHeight(), url);
                    MsgAllBean msgAllBean = sendMessage(videoMessageSD, ChatEnum.EMessageType.MSG_VIDEO, false);
                    getView().replaceListDataAndNotify(msgAllBean);
                    if (!TextUtils.isEmpty(videoMessage.getBg_url())) {
                        // 当预览图清空掉时重新获取
                        File file = new File(videoMessage.getBg_url());
                        if (file == null || !file.exists()) {
                            videoMessage.setBg_url(getVideoAttBitmap(url));
                        }
                    }
                    UpLoadService.onAddVideo(this.context, msgAllBean, false);
                    getView().startUploadService();
                } else {
                    //点击发送的时候如果要改变成发送中的状态
                    DaoUtil.update(reMsg);
                    MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(reMsg.getSend_data()).toBuilder();
                    SocketUtil.getSocketUtil().sendData4Msg(bean);
//                    taskRefreshMessage(false);
                    loadAndSetData();
                }
            } else {
                //点击发送的时候如果要改变成发送中的状态
                reMsg.setSend_state(ChatEnum.ESendStatus.SENDING);
                DaoUtil.update(reMsg);
                MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(reMsg.getSend_data()).toBuilder();
                SocketUtil.getSocketUtil().sendData4Msg(bean);
//                taskRefreshMessage(false);
                loadAndSetData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    void uploadVoice(String file, final MsgAllBean bean) {
        uploadMap.put(bean.getMsg_id(), bean);
        uploadList.add(bean);
        updateSendStatus(ChatEnum.ESendStatus.SENDING, bean);
        new UpFileAction().upFile(UpFileAction.PATH.VOICE, context, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                LogUtil.getLog().e(ChatActivity3.class.getSimpleName(), "上传语音成功--" + url);
                VoiceMessage voice = bean.getVoiceMessage();
                voice.setUrl(url);
                SocketData.sendAndSaveMessage(bean);
            }

            @Override
            public void fail() {
                updateSendStatus(ChatEnum.ESendStatus.ERROR, bean);
            }

            @Override
            public void inProgress(long progress, long zong) {
            }
        }, file);
    }

    private void updateSendStatus(@ChatEnum.ESendStatus int status, MsgAllBean bean) {
        bean.setSend_state(status);
        model.updateSendStatus(bean.getMsg_id(), status);
        getView().replaceListDataAndNotify(bean);
    }

    public void doSendText(MsgEditText edtChat, boolean isGroup, int survivalTime) {
        String txt = edtChat.getText().toString();
        if (txt.startsWith("@000")) {
            int count = Integer.parseInt(txt.split("_")[1]);
            taskTestSend(count);
            return;
        }
        //  }

        if (isGroup && edtChat.getUserIdList() != null && edtChat.getUserIdList().size() > 0) {
            String text = edtChat.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                int totalSize = text.length();
                if (totalSize > MIN_TEXT) {
                    ToastUtil.show(context, "@消息长度不能超过" + MIN_TEXT);
                    edtChat.getText().clear();
                    return;
                }
            }
            if (edtChat.isAtAll()) {
                AtMessage message = SocketData.createAtMessage(SocketData.getUUID(), text, ChatEnum.EAtType.ALL, edtChat.getUserIdList());
                sendMessage(message, ChatEnum.EMessageType.AT);
                edtChat.getText().clear();
            } else {
                AtMessage message = SocketData.createAtMessage(SocketData.getUUID(), text, ChatEnum.EAtType.MULTIPLE, edtChat.getUserIdList());
                sendMessage(message, ChatEnum.EMessageType.AT);
                edtChat.getText().clear();
            }
        } else {
            //发送普通消息
            String text = edtChat.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                int totalSize = text.length();
                int per = totalSize / MIN_TEXT;
                if (per > 10) {
                    ToastUtil.show(context, "文本长度不能超过" + 10 * MIN_TEXT);
                    edtChat.getText().clear();
                    return;
                }
                if (totalSize <= MIN_TEXT) {//非长文本
                    isSendingHypertext = false;
                    ChatMessage message = SocketData.createChatMessage(SocketData.getUUID(), text);
                    sendMessage(message, ChatEnum.EMessageType.TEXT);
                    edtChat.getText().clear();
                } else {
                    isSendingHypertext = true;//正在分段发送长文本
                    if (totalSize > per * MIN_TEXT) {
                        per = per + 1;
                    }
                    sendTexts = new ArrayList<>();
                    for (int i = 0; i < per; i++) {
                        if (i < per - 1) {
                            sendTexts.add(StringUtil.splitEmojiString(text, i * MIN_TEXT, (i + 1) * MIN_TEXT));
                        } else {
                            sendTexts.add(StringUtil.splitEmojiString(text, i * MIN_TEXT, totalSize));
                        }
                    }
                    sendHypertext(sendTexts, 0);
                    edtChat.getText().clear();
                }
            }
        }
    }

    private void sendHypertext(List<String> list, int position) {
        if (position == list.size() - 1) {
            isSendingHypertext = false;
        }
        textPosition = position;
        ChatMessage message = SocketData.createChatMessage(SocketData.getUUID(), list.get(position));
        sendMessage(message, ChatEnum.EMessageType.TEXT);
    }

    private void taskTestSend(final int count) {
        ToastUtil.show(context, "连续发送" + count + "测试开始");
        new RunUtils(new RunUtils.Enent() {
            @Override
            public void onRun() {

                try {
                    for (int i = 1; i <= count; i++) {
                        if (i % 10 == 0) {
                            ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), "连续测试发送" + i + "-------");
                            sendMessage(chatMessage, ChatEnum.EMessageType.TEXT);
                        } else {
                            ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), "连续测试发送" + i);
                            sendMessage(chatMessage, ChatEnum.EMessageType.TEXT);
                        }
                        if (i % 100 == 0)
                            Thread.sleep(2 * 1000);

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMain() {
                getView().notifyDataAndScrollBottom(false);
            }
        }).run();
    }

    /*
     * 发送红包
     * */
    void sendRb() {
        payAction.SignatureBean(new CallBack<ReturnBean<SignatureBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SignatureBean>> call, Response<ReturnBean<SignatureBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    SignatureBean sign = response.body().getData();
                    String token = sign.getSign();
                    UserInfo info = UserAction.getMyInfo();
                    if (model.isGroup()) {
                        Group group = model.getGroup();
                        JrmfRpClient.sendGroupEnvelopeForResult((Activity) context, "" + model.getGid(), "" + UserAction.getMyId(), token,
                                group.getUsers().size(), info.getName(), info.getHead(), REQ_RP);
                    } else {
                        JrmfRpClient.sendSingleEnvelopeForResult((Activity) context, "" + model.getUid(), "" + info.getUid(), token,
                                info.getName(), info.getHead(), REQ_RP);
                    }
                }
            }
        });
    }

    /***
     * 转账
     */
    public void doTrans() {
        payAction.SignatureBean(new CallBack<ReturnBean<SignatureBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SignatureBean>> call, Response<ReturnBean<SignatureBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    SignatureBean sign = response.body().getData();
                    String token = sign.getSign();
                    UserInfo otherInfo = model.getUserInfo();
                    UserInfo mInfo = UserAction.getMyInfo();
                    JrmfRpClient.transAccount((Activity) context, "" + otherInfo.getUid(), "" + mInfo.getUid(), token,
                            mInfo.getName(), mInfo.getHead(), otherInfo.getName4Show(), otherInfo.getHead(), new TransAccountCallBack() {
                                @Override
                                public void transResult(TransAccountBean transAccountBean) {
                                    String rid = transAccountBean.getTransferOrder();
                                    String info = transAccountBean.getTransferDesc();
                                    String money = transAccountBean.getTransferAmount();
                                    //设置转账消息
                                    MsgAllBean msgAllbean = SocketData.send4Trans(model.getUid(), rid, info, money);
//                                    showSendObj(msgAllbean);
                                    loadAndSetData();

                                }
                            });
                }
            }
        });
    }

    //戳一下
    public void doStamp(int survivalTime) {
        AlertTouch alertTouch = new AlertTouch();
        alertTouch.init((Activity) context, "请输入戳一下消息", "确定", R.mipmap.ic_chat_actionme, new AlertTouch.Event() {
            @Override
            public void onON() {

            }

            @Override
            public void onYes(String content) {
                if (!TextUtils.isEmpty(content)) {
                    //发送普通消息
                    MsgAllBean msgAllbean = SocketData.send4action(model.getUid(), model.getGid(), content);
//                    showSendObj(msgAllbean);
                    loadAndSetData();
                } else {
                    ToastUtil.show(context, "留言不能为空");
                }
            }
        });
        alertTouch.show();
        alertTouch.setEdHintOrSize(null, 15);
    }


    public void loadAndSetMoreData(boolean isMore) {
        final int position = model.getTotalSize();
        Observable<List<MsgAllBean>> observable = model.loadMoreMessages();
        observable.compose(RxSchedulers.<List<MsgAllBean>>compose())
                .compose(RxSchedulers.<List<MsgAllBean>>handleResult())
                .subscribe(new DBOptionObserver<List<MsgAllBean>>() {
                    @Override
                    public void onOptionSuccess(List<MsgAllBean> list) {
                        getView().bindData(list, isMore);
                        getView().scrollToPositionWithOff(list.size() - position, DensityUtil.dip2px(context, 20f));
                    }
                });
    }

    public void setAndClearDraft() {
        Session session = model.getSession();
        if (session == null) {
            return;
        }
        if (!TextUtils.isEmpty(session.getDraft())) {
            getView().setDraft(session.getDraft());
            model.updateDraft("");
        }

    }

    private void fixSendTime(String msgId) {
        MsgAllBean bean = uploadMap.get(msgId);
        boolean needRefresh = false;
        if (bean != null) {
            if (uploadList.indexOf(bean) == 0) {
                needRefresh = true;
            }
            uploadMap.remove(msgId);
        }
        if (needRefresh && uploadMap.size() > 0) {
            for (Map.Entry<String, MsgAllBean> entry : uploadMap.entrySet()) {
                MsgAllBean msg = entry.getValue();
                msg.setTimestamp(SocketData.getFixTime());
                DaoUtil.update(msg);
            }
        }
    }

    //消息的分发
    public void onMsgBranch(MsgBean.UniversalMessage.WrapMessage msg) {
        switch (msg.getMsgType()) {

            case DESTROY_GROUP:
                // ToastUtil.show(getApplicationContext(), "销毁群");
                taskGroupConf();
            case REMOVE_GROUP_MEMBER://退出群
                taskGroupConf();
                break;
            case ACCEPT_BE_GROUP://邀请进群刷新
                if (model.getGroup() == null) {
                    return;
                }
                if (StringUtil.isNotNull(model.getGroup().getAvatar())) {
                    taskGroupConf();
                } else {
                    if (model.getGroup().getUsers().size() >= 9) {
                        taskGroupConf();
                    } else {
                        taskGroupConf();
                        createAndSaveImg(model.getGid());
                    }
                }
                break;
//            case OTHER_REMOVE_GROUP:
//                createAndSaveImg(model.getGid());
//                break;
            case CHANGE_GROUP_META:
                getView().initTitle();
                break;
        }

    }

    private void createAndSaveImg(String gid) {
        Group group = model.getGroup();
        int i = group.getUsers().size();
        i = i > 9 ? 9 : i;
        //头像地址
        String url[] = new String[i];
        for (int j = 0; j < i; j++) {
            MemberUser userInfo = group.getUsers().get(j);
//            if (j == i - 1) {
//                name += userInfo.getName();
//            } else {
//                name += userInfo.getName() + "、";
//            }
            url[j] = userInfo.getHead();
        }
        File file = GroupHeadImageUtil.synthesis(context, url);
        MsgDao msgDao = new MsgDao();
        msgDao.groupHeadImgCreate(group.getGid(), file.getAbsolutePath());
    }

    /***
     * 获取群配置,并显示更多按钮
     */
    void taskGroupConf() {
        if (!model.isGroup()) {
            return;
        }
        GroupConfig config = model.getGroupConfig();
        if (config != null) {
            boolean isExited;
            if (config.getIsExit() == 1) {
                isExited = true;
            } else {
                isExited = false;
            }
            getView().setBanView(isExited);
        }
        taskGroupInfo();
    }

    public void taskGroupInfo() {
        new MsgAction().groupInfo(model.getGid(), true, new CallBack<ReturnBean<Group>>() {
            @Override
            public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                if (response.body() == null)
                    return;

                Group groupInfo = response.body().getData();

                if (groupInfo == null) {//取不到群信息了
                    groupInfo = new Group();
                    groupInfo.setMaster("");
                    groupInfo.setUsers(new RealmList<MemberUser>());
                }
                model.setGroup(groupInfo);

                if (groupInfo.getMaster().equals(UserAction.getMyId().toString())) {//本人群主
                    getView().setRobotView(true);
                } else {
                    getView().setRobotView(false);
                }

                //如果自己不在群里面
                boolean isExit = false;
                for (MemberUser uifo : groupInfo.getUsers()) {
                    if (uifo.getUid() == UserAction.getMyId().longValue()) {
                        isExit = true;
                    }
                }
                getView().setBanView(!isExit);
            }
        });
    }

    private void checkMoreVoice(int start, MsgAllBean b) {
//        LogUtil.getLog().i("AudioPlayManager", "checkMoreVoice--onCreate=" + onCreate);
        int length = model.getTotalSize();
        int index = model.getListData().indexOf(b);
        if (index < 0) {
            return;
        }
        if (index != start) {//修正一下起始位置
            start = index;
        }
        MsgAllBean message = null;
        int position = -1;
        if (start < length - 1) {
            for (int i = start + 1; i < length; i++) {
                MsgAllBean bean = model.getListData().get(i);
                if (bean.getMsg_type() == ChatEnum.EMessageType.VOICE && !bean.isMe() && !bean.isRead()) {
                    message = bean;
                    position = i;
                    break;
                }
            }
        }
//        MsgAllBean bean = msgDao.getNextVoiceMessage(toUId,toGid,b.getTimestamp(),UserAction.getMyInfo().getUid());
        if (message != null) {
            playVoice(message, true, position);
        }

    }

    private void playVoice(final MsgAllBean bean, final boolean canAutoPlay, final int position) {
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
            if (!bean.isRead() && !bean.isMe()) {
                int len = downloadList.size();
                if (len > 0) {//有下载
                    MsgAllBean msg = downloadList.get(len - 1);
                    updatePlayStatus(msg, 0, ChatEnum.EPlayStatus.NO_PLAY);
                }
                downloadList.add(bean);

                updatePlayStatus(bean, position, ChatEnum.EPlayStatus.DOWNLOADING);
                AudioPlayManager.getInstance().downloadAudio(context, bean, new DownloadUtil.IDownloadVoiceListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        updatePlayStatus(bean, position, ChatEnum.EPlayStatus.NO_PLAY);
                        startPlayVoice(bean, canAutoPlay, position);

                    }

                    @Override
                    public void onDownloading(int progress) {

                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        updatePlayStatus(bean, position, ChatEnum.EPlayStatus.NO_DOWNLOADED);
                    }
                });
            } else {
                int len = downloadList.size();
                if (len > 0) {//有下载
                    MsgAllBean msg = downloadList.get(len - 1);
                    updatePlayStatus(msg, 0, ChatEnum.EPlayStatus.NO_PLAY);
                }
                startPlayVoice(bean, canAutoPlay, position);
            }
        }
    }

    private void updatePlayStatus(MsgAllBean bean, int position, @ChatEnum.EPlayStatus int status) {
//        LogUtil.getLog().i(TAG, "updatePlayStatus--" + status + "--position=" + position);
        bean = model.amendMsgALlBean(position, bean);
        VoiceMessage voiceMessage = bean.getVoiceMessage();
        if (status == ChatEnum.EPlayStatus.NO_PLAY || status == ChatEnum.EPlayStatus.PLAYING) {//已点击下载，或者正在播
            if (bean.isRead() == false) {
//                msgAction.msgRead(bean.getMsg_id(), true);
                model.updateReadStatus(bean.getMsg_id(), true);
                bean.setRead(true);
            }
        }
        model.updatePlayStatus(voiceMessage.getMsgId(), status);
        model.updateReadStatus(bean.getMsg_id(), true);

        voiceMessage.setPlayStatus(status);
        final MsgAllBean finalBean = bean;
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getView().replaceListDataAndNotify(finalBean);
            }
        });
    }

    private void startPlayVoice(MsgAllBean bean, boolean canAutoPlay, final int position) {
//        LogUtil.getLog().i(TAG, "startPlayVoice--" + "downSize =" + downloadList.size());

        if (downloadList.size() > 1) {
            int size = downloadList.size();
            int p = downloadList.indexOf(bean);
            if (p != size - 1) {
//                LogUtil.getLog().i(TAG, "startPlayVoice--终止下载位置=" + p);
                downloadList.remove(bean);
                return;
            }
        }
        downloadList.remove(bean);

        AudioPlayManager.getInstance().startPlay(context, bean, position, canAutoPlay, new IVoicePlayListener() {
            @Override
            public void onStart(MsgAllBean bean) {
                updatePlayStatus(bean, position, ChatEnum.EPlayStatus.PLAYING);
//                LogUtil.getLog().i("AudioPlayManager", "onStart--" + bean.getVoiceMessage().getUrl());
            }

            @Override
            public void onStop(MsgAllBean bean) {
                updatePlayStatus(bean, position, ChatEnum.EPlayStatus.STOP_PLAY);
//                LogUtil.getLog().i("AudioPlayManager", "onStop--" + bean.getVoiceMessage().getUrl());
            }

            @Override
            public void onComplete(MsgAllBean bean) {
                updatePlayStatus(bean, position, ChatEnum.EPlayStatus.PLAYED);
//                LogUtil.getLog().i("AudioPlayManager", "onComplete--" + bean.getVoiceMessage().getUrl());
            }
        });
    }


    //消息发送
    private void sendMessage(IMsgContent message, @ChatEnum.EMessageType int msgType) {
        MsgAllBean msgAllBean = SocketData.createMessageBean(model.getUid(), model.getGid(), msgType, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), message);
        if (msgAllBean != null) {
            if (!filterMessage(message)) {
                SocketData.sendAndSaveMessage(msgAllBean, false);
            } else {
                SocketData.sendAndSaveMessage(msgAllBean);
            }
            getView().addAndShowSendMessage(msgAllBean);
            MessageManager.getInstance().notifyRefreshMsg(model.isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, model.getUid(), model.getGid(), CoreEnum.ESessionRefreshTag.SINGLE, msgAllBean);
        }
    }

    //消息发送，canSend--是否需要发送

    private MsgAllBean sendMessage(IMsgContent message, @ChatEnum.EMessageType int msgType, boolean canSend) {
        int sendStatus = ChatEnum.ESendStatus.NORMAL;
        if (TextUtils.isEmpty(model.getGid()) && model.getUid() > 0 && Constants.CX_HELPER_UID.longValue() == model.getUid()) {//常信小助手
            sendStatus = ChatEnum.ESendStatus.NORMAL;
        } else {
            if (isUploadType(msgType)) {
                sendStatus = ChatEnum.ESendStatus.PRE_SEND;
            }
        }
        MsgAllBean msgAllBean = SocketData.createMessageBean(model.getUid(), model.getGid(), msgType, sendStatus, SocketData.getFixTime(), message);
        if (msgAllBean != null) {
            SocketData.sendAndSaveMessage(msgAllBean, canSend);
            getView().addAndShowSendMessage(msgAllBean);
            MessageManager.getInstance().notifyRefreshMsg(model.isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, model.getUid(), model.getGid(), CoreEnum.ESessionRefreshTag.SINGLE, msgAllBean);
        }
        return msgAllBean;
    }

    private boolean filterMessage(IMsgContent message) {
        boolean isSend = true;
        if (Constants.CX_HELPER_UID.equals(model.getUid()) || Constants.CX_BALANCE_UID.equals(model.getUid())
                || Constants.CX_FILE_HELPER_UID.equals(model.getUid())) {//常信小助手不需要发送到后台
            isSend = false;
        }
        return isSend;
    }


    void toGallery() {
        if (context == null) {
            return;
        }
        PictureSelector.create((Activity) context)
//                        .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(false)// 是否可预览图片 true or false
                .isCamera(false)// 是否显示拍照按钮 ture or false
                .maxVideoSelectNum(1)
                .compress(true)// 是否压缩 true or false
                .isGif(true)
                .selectArtworkMaster(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    void toCamera() {
        if (context == null) {
            return;
        }
        permission2Util.requestPermissions(activity, new CheckPermission2Util.Event() {
            @Override
            public void onSuccess() {
                // 判断是否正在音视频通话
                if (AVChatProfile.getInstance().isCallIng() || AVChatProfile.getInstance().isCallEstablished()) {
                    if (AVChatProfile.getInstance().isChatType() == AVChatType.VIDEO.getValue()) {
                        ToastUtil.show(context, context.getString(R.string.avchat_peer_busy_video));
                    } else {
                        ToastUtil.show(context, context.getString(R.string.avchat_peer_busy_voice));
                    }
                } else {
                    if (!checkNetConnectStatus()) {
                        return;
                    }
                    if (ViewUtils.isFastDoubleClick()) {
                        return;
                    }
                    Intent intent = new Intent(context, RecordedActivity.class);
                    ((ChatActivity3) context).startActivityForResult(intent, VIDEO_RP);
                }
            }

            @Override
            public void onFail() {

            }
        }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
    }

    void toTransfer() {
        UserBean user = PayEnvironment.getInstance().getUser();
        if (user != null) {
            if (user.getRealNameStat() != 1) {//未认证
                showIdentifyDialog();
                return;
            } else if (user.getPayPwdStat() != 1) {//未设置支付密码
                showSettingPswDialog();
                return;
            }
        }
        UserInfo userInfo = model.getUserInfo();
        String name = "";
        String avatar = "";
        if (userInfo != null) {
            name = userInfo.getName();
            avatar = userInfo.getHead();
        }
        if (context == null) {
            return;
        }
        Intent intent = TransferActivity.newIntent(context, model.getUid(), name, avatar);
        context.startActivity(intent);
    }


    /*
     * 发送消息前，需要检测网络连接状态，网络不可用，不能发送
     * 每条消息发送前，需要检测，语音和小视频录制之前，仍需要检测
     * */
    private boolean checkNetConnectStatus() {
        boolean isOk;
        if (context == null) {
            return false;
        }
        if (!NetUtil.isNetworkConnected()) {
            ToastUtil.show(context, "网络连接不可用，请稍后重试");
            isOk = false;
        } else {
            isOk = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.CONN_STATUS).get4Json(Boolean.class);
            if (!isOk) {
                ToastUtil.show(context, "连接已断开，请稍后再试");
            }
        }
        return isOk;
    }

    void toSystemEnvelope() {
        UserBean user = PayEnvironment.getInstance().getUser();
        if (user != null) {
            if (user.getRealNameStat() != 1) {//未认证
                showIdentifyDialog();
                return;
            } else if (user.getPayPwdStat() != 1) {//未设置支付密码
                showSettingPswDialog();
                return;
            }
        }
        if (model.isGroup()) {
            Intent intentMulti = MultiRedPacketActivity.newIntent(context, model.getGid(), model.getGroup().getUsers().size());
            ((ChatActivity3) context).startActivityForResult(intentMulti, REQUEST_RED_ENVELOPE);
        } else {
            Intent intentMulti = SingleRedPacketActivity.newIntent(context, model.getUid());
            ((ChatActivity3) context).startActivityForResult(intentMulti, REQUEST_RED_ENVELOPE);
        }
    }


    void toLocation() {
        if (activity == null) {
            return;
        }
        LocationActivity.openActivity(activity, false, null);
    }

    void toVideoCall() {
        getView().hideBt();
        DialogHelper.getInstance().createSelectDialog(activity, new ICustomerItemClick() {
            @Override
            public void onClickItemVideo() {// 视频
                gotoVideoActivity(AVChatType.VIDEO.getValue());
            }

            @Override
            public void onClickItemVoice() {// 语音
                gotoVideoActivity(AVChatType.AUDIO.getValue());
            }

            @Override
            public void onClickItemCancle() {

            }
        });
    }

    void toGroupRobot() {
        if (model.getGroup() == null || context == null)
            return;

        context.startActivity(new Intent(context, GroupRobotActivity.class)
                .putExtra(GroupRobotActivity.AGM_GID, model.getGid())
                .putExtra(GroupRobotActivity.AGM_RID, model.getGroup().getRobotid())
        );
    }

    private void toVoice() {
        //申请权限 7.2
        permission2Util.requestPermissions(activity, new CheckPermission2Util.Event() {
            @Override
            public void onSuccess() {
                if (!checkNetConnectStatus()) {
                    return;
                }
                getView().startVoiceUI(null);
            }

            @Override
            public void onFail() {

            }
        }, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    void toCard() {
        if (context == null) {
            return;
        }
        activity.startActivityForResult(new Intent(context, SelectUserActivity.class), SelectUserActivity.RET_CODE_SELECTUSR);
    }

    void toStamp() {
        AlertTouch alertTouch = new AlertTouch();
        alertTouch.init(activity, "请输入戳一下消息", "确定", R.mipmap.ic_chat_actionme, new AlertTouch.Event() {
            @Override
            public void onON() {

            }

            @Override
            public void onYes(String content) {
                if (!TextUtils.isEmpty(content)) {
                    //发送戳一戳消息
                    StampMessage message = SocketData.createStampMessage(SocketData.getUUID(), content);
                    sendMessage(message, ChatEnum.EMessageType.STAMP);
                } else {
                    ToastUtil.show(context, "留言不能为空");
                }
            }
        });
        alertTouch.show();
        alertTouch.setEdHintOrSize(null, 15);
    }

    /**
     * 实名认证提示弹框
     */
    private void showIdentifyDialog() {
        if (context == null) {
            return;
        }
        dialogOne = builder.setTitle("根据国家法律法规要求，你需要进行身份认证后\n，才能继续使用该功能。")
                .setLeftText("取消")
                .setRightText("去认证")
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
                        //去认证(需要先同意协议)
                        dialogOne.dismiss();
                        context.startActivity(new Intent(context, ServiceAgreementActivity.class));
                    }
                })
                .build();
        dialogOne.show();
    }

    public void showSettingPswDialog() {
        DialogDefault dialogSettingPayPsw = new DialogDefault(context, R.style.MyDialogTheme);
        dialogSettingPayPsw
                .setTitleAndSure(true, false)
                .setTitle("温馨提示")
                .setLeft("设置支付密码")
                .setRight("取消")
                .setListener(new DialogDefault.IDialogListener() {
                    @Override
                    public void onSure() {
                        context.startActivity(new Intent(context, SetPaywordActivity.class));
                    }

                    @Override
                    public void onCancel() {

                    }
                });
        dialogSettingPayPsw.show();

    }

    /**
     * 获取账单详情
     */
    private void httpGetTransferDetail(String tradeId, int opType, MsgAllBean msgBean) {
        PayHttpUtils.getInstance().getTransferDetail(tradeId)
                .compose(RxSchedulers.<BaseResponse<TransferDetailBean>>compose())
                .compose(RxSchedulers.<BaseResponse<TransferDetailBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<TransferDetailBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<TransferDetailBean> baseResponse) {
                        if (baseResponse.getData() != null) {
                            ((ChatActivity3) context).dismissLoadingDialog();
                            //如果当前页有数据
                            TransferDetailBean detailBean = baseResponse.getData();
                            Intent intent;
                            if (opType == PayEnum.ETransferOpType.TRANS_SEND) {
                                intent = TransferDetailActivity.newIntent(context, detailBean, tradeId, msgBean.isMe(), GsonUtils.optObject(msgBean));
                            } else {
                                intent = TransferDetailActivity.newIntent(context, detailBean, tradeId, msgBean.isMe());
                            }
                            context.startActivity(intent);
                        } else {

                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse<TransferDetailBean> baseResponse) {
                        super.onHandleError(baseResponse);
                        ToastUtil.show(context, baseResponse.getMessage());
                    }
                });

    }

    public String getTransferInfo(String info, int opType, boolean isMe, String nick) {
        String result = "";
        if (opType == PayEnum.ETransferOpType.TRANS_SEND) {
            if (TextUtils.isEmpty(info)) {
                if (isMe) {
                    result = "转账给" + nick;
                } else {
                    result = "转账给你";
                }
            } else {
                result = info;

            }
        } else if (opType == PayEnum.ETransferOpType.TRANS_RECEIVE) {
            if (TextUtils.isEmpty(info)) {
                if (isMe) {
                    result = "已收款";
                } else {
                    result = "已被领取";
                }
            } else {
                if (isMe) {
                    result = "已收款-" + info;
                } else {
                    result = "已被领取-" + info;
                }
            }
        } else if (opType == PayEnum.ETransferOpType.TRANS_REJECT) {
            if (TextUtils.isEmpty(info)) {
                if (isMe) {
                    result = "已退款";
                } else {
                    result = "已被退款";
                }
            } else {
                if (isMe) {
                    result = "已退款-" + info;
                } else {
                    result = "已被退款-" + info;
                }
            }
        } else if (opType == PayEnum.ETransferOpType.TRANS_PAST) {
            if (TextUtils.isEmpty(info)) {
                if (isMe) {
                    result = "已过期";
                } else {
                    result = "已过期";
                }
            } else {
                if (isMe) {
                    result = "已过期-" + info;
                } else {
                    result = "已过期-" + info;
                }
            }
        }
        return result;
    }

    private void checkHasEnvelopeSendFailed() {
        EnvelopeInfo envelopeInfo = model.queryEnvelopeInfo();
        if (envelopeInfo != null) {
            long createTime = envelopeInfo.getCreateTime();
            if (createTime - System.currentTimeMillis() >= TimeToString.DAY) {//超过24小时
                showEnvelopePastDialog(envelopeInfo);
                deleteEnvelopInfo(envelopeInfo);
            } else {
                // TODO 处理#50702 android.view.WindowManager$BadTokenException
                if (!((ChatActivity3) context).isFinishing()) {
                    showSendEnvelopeDialog(envelopeInfo);
                }
            }
        }
    }


    private void showSendEnvelopeDialog(EnvelopeInfo info) {
        DialogCommon dialogCommon = new DialogCommon(context);
        dialogCommon.setCanceledOnTouchOutside(false);
        String time = TimeToString.getEnvelopeTime(info.getCreateTime());
        String money = info.getAmount() * 1.00 / 100 + "元";
        String content = "您有一个" + time + " 金额为" + money + "的红包已扣款未发送成功,是否重新发送此红包？";
        dialogCommon.setTitleAndSure(true, true)
                .setTitle("温馨提示")
                .setContent(content, false)
                .setLeft("取消发送")
                .setRight("重发红包")
                .setListener(new DialogCommon.IDialogListener() {
                    @Override
                    public void onSure() {
                        RedEnvelopeMessage message = null;
                        deleteEnvelopInfo(info);
                        if (info.getReType() == 0) {
                            message = SocketData.createRbMessage(SocketData.getUUID(), info.getRid(), info.getComment(), info.getReType(), info.getEnvelopeStyle());
                        } else {
//                            message = SocketData.creat(SocketData.getUUID(),info.getRid(),info.getComment(),info.getReType(),info.getEnvelopeStyle());
                        }
                        if (message != null) {
                            sendMessage(message, ChatEnum.EMessageType.RED_ENVELOPE);
                        }
                    }

                    @Override
                    public void onCancel() {
                        deleteEnvelopInfo(info);
                    }
                });
        dialogCommon.show();
    }

    private void showEnvelopePastDialog(EnvelopeInfo info) {
        DialogEnvelopePast dialogCommon = new DialogEnvelopePast(context);
        dialogCommon.setCanceledOnTouchOutside(false);
        String time = TimeToString.MM_DD_HH_MM2(info.getCreateTime());
        String money = info.getAmount() * 1.00 / 100 + "元";
        String content = "您有一个" + time + " 金额为" + money + "的红包未发送成功。已自动退回云红包账户";
        dialogCommon.setContent(content)
                .setListener(new DialogEnvelopePast.IDialogListener() {
                    @Override
                    public void onSure() {
                    }

                    @Override
                    public void onCancel() {

                    }
                });
        dialogCommon.show();
    }

    private void saveMFEnvelope(EnvelopeBean bean) {
        EnvelopeInfo envelopeInfo = new EnvelopeInfo();
        envelopeInfo.setRid(bean.getEnvelopesID());
        envelopeInfo.setAmount(StringUtil.getLong(bean.getEnvelopeAmount()));
        envelopeInfo.setComment(bean.getEnvelopeMessage());
        envelopeInfo.setReType(0);//0 MF  1 SYS
        MsgBean.RedEnvelopeMessage.RedEnvelopeStyle style = MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.NORMAL;
        if (bean.getEnvelopeType() == 1) {//拼手气
            style = MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.LUCK;
        }
        envelopeInfo.setEnvelopeStyle(style.getNumber());
        envelopeInfo.setCreateTime(System.currentTimeMillis());
        envelopeInfo.setGid(model.getGid());
        envelopeInfo.setUid(model.getUid());
        envelopeInfo.setSendStatus(0);
        envelopeInfo.setSign("");
        model.updateEnvelopeInfo(envelopeInfo);
        MessageManager.getInstance().notifyRefreshMsg(model.isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, model.getUid(), model.getGid(), CoreEnum.ESessionRefreshTag.SINGLE, null);
    }

    //删除临时红包信息
    private void deleteEnvelopInfo(EnvelopeInfo envelopeInfo) {
        model.deleteEnvelopeInfo(envelopeInfo.getRid(), model.getGid(), model.getUid(), true);
        MsgAllBean lastMsg = null;
        if (model.getListData() != null) {
            int len = model.getListData().size();
            if (len > 0) {
                lastMsg = model.getListData().get(len - 1);
            }
        }
        MessageManager.getInstance().notifyRefreshMsg(model.isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, model.getUid(), model.getGid(), CoreEnum.ESessionRefreshTag.SINGLE, lastMsg);
    }

    /***
     * 发红包
     */
    void taskPayRb() {
        UserInfo info = UserAction.getMyInfo();
        if (info != null && info.getLockCloudRedEnvelope() == 1) {//红包功能被锁定
            ToastUtil.show(context, "您的云红包功能已暂停使用，如有疑问请咨询官方客服号");
            return;
        }
        payAction.SignatureBean(new CallBack<ReturnBean<SignatureBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SignatureBean>> call, Response<ReturnBean<SignatureBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    SignatureBean sign = response.body().getData();
                    String token = sign.getSign();
                    if (model.isGroup()) {
                        Group group = model.getGroup();
                        int totalSize = 0;
                        if (group != null && group.getUsers() != null) {
                            totalSize = group.getUsers().size();
                        }
                        JrmfRpClient.sendGroupEnvelopeForResult(activity, "" + model.getGid(), "" + UserAction.getMyId(), token,
                                totalSize, info.getName(), info.getHead(), REQ_RP);
                    } else {
                        JrmfRpClient.sendSingleEnvelopeForResult(activity, "" + model.getUid(), "" + info.getUid(), token,
                                info.getName(), info.getHead(), REQ_RP);
                    }
                    LogUtil.writeEnvelopeLog("准备发红包");

                }
            }
        });
    }

    /***
     * 红包收
     */
    private void taskPayRbGet(final MsgAllBean msgbean, final Long toUId, final String rbid) {
        payAction.SignatureBean(new CallBack<ReturnBean<SignatureBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SignatureBean>> call, Response<ReturnBean<SignatureBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    SignatureBean sign = response.body().getData();
                    String token = sign.getSign();

                    GrabRpCallBack callBack = new GrabRpCallBack() {
                        @Override
                        public void grabRpResult(GrabRpBean grabRpBean) {
                            //0 正常状态未领取，1 红包已经被领取，2 红包失效不能领取，3 红包未失效但已经被领完，4 普通红包并且用户点击自己红包
                            int envelopeStatus = grabRpBean.getEnvelopeStatus();
                            if (envelopeStatus == 0 && grabRpBean.isHadGrabRp()) {
//                                MsgAllBean msgAllbean = SocketData.send4RbRev(toUId, toGid, rbid, MsgBean.RedEnvelopeType.MFPAY_VALUE);
//                                showSendObj(msgAllbean);
//                                MessageManager.getInstance().notifyRefreshMsg(isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUId, toGid, CoreEnum.ESessionRefreshTag.SINGLE, msgAllbean);
//                                taskPayRbCheck(msgbean, rbid, MsgBean.RedEnvelopeType.MFPAY_VALUE, "", PayEnum.EEnvelopeStatus.RECEIVED);
                            }
                            if (envelopeStatus == 2 || envelopeStatus == 3) {
                                taskPayRbCheck(msgbean, rbid, MsgBean.RedEnvelopeType.MFPAY_VALUE, "", PayEnum.EEnvelopeStatus.RECEIVED);
                            }
                        }
                    };
                    if (!activity.isActivityValid()) {
                        return;
                    }
                    UserInfo info = UserAction.getMyInfo();
                    if (model.isGroup()) {
                        JrmfRpClient.openGroupRp(activity, "" + info.getUid(), token,
                                info.getName(), info.getHead(), rbid, callBack);
                    } else {
                        JrmfRpClient.openSingleRp(activity, "" + info.getUid(), token,
                                info.getName(), info.getHead(), rbid, callBack);
                    }

                }
            }
        });
    }

    /***
     * 红包是否已经被抢,红包改为失效
     * @param rid
     */
    private void taskPayRbCheck(MsgAllBean msgAllBean, String rid, int reType, String token, int envelopeStatus) {
        if (envelopeStatus != PayEnum.EEnvelopeStatus.NORMAL) {
            msgAllBean.getRed_envelope().setIsInvalid(1);
            msgAllBean.getRed_envelope().setEnvelopStatus(envelopeStatus);
        }
        if (!TextUtils.isEmpty(token)) {
            msgAllBean.getRed_envelope().setAccessToken(token);
        }
        model.updateEnvelope(rid, envelopeStatus, reType, token);
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                getView().replaceListDataAndNotify(msgAllBean);
            }
        });
    }

    /**
     * 进入音视频通话
     *
     * @param aVChatType
     */
    private void gotoVideoActivity(int aVChatType) {
        permission2Util.requestPermissions(activity, new CheckPermission2Util.Event() {
            @Override
            public void onSuccess() {
                if (NetUtil.isNetworkConnected()) {
                    UserInfo userInfo = model.getUserInfo();
                    if (userInfo != null) {
                        EventFactory.CloseMinimizeEvent event = new EventFactory.CloseMinimizeEvent();
                        event.isClose = true;
                        EventBus.getDefault().post(event);
                        Bundle bundle = new Bundle();
                        bundle.putString(Preferences.USER_HEAD_SCULPTURE, userInfo.getHead());
                        if (!TextUtils.isEmpty(userInfo.getMkName())) {
                            bundle.putString(Preferences.USER_NAME, userInfo.getMkName());
                        } else {
                            bundle.putString(Preferences.USER_NAME, userInfo.getName());
                        }
                        bundle.putString(Preferences.NETEASEACC_ID, userInfo.getNeteaseAccid());
                        bundle.putInt(Preferences.VOICE_TYPE, CoreEnum.VoiceType.WAIT);
                        bundle.putInt(Preferences.AVCHA_TTYPE, aVChatType);
                        bundle.putString(Preferences.TOGID, model.getGid());
                        bundle.putLong(Preferences.TOUID, model.getUid());
                        IntentUtil.gotoActivity(context, VideoActivity.class, bundle);
                    }

                } else {
                    showNetworkDialog();
                }
            }

            @Override
            public void onFail() {

            }
        }, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
    }

    /**
     * 显示网络错误提示
     */
    private void showNetworkDialog() {
        AlertYesNo alertYesNo = new AlertYesNo();
        alertYesNo.init(context, null, "当前网络不可用，请检查你的网络设置", "确定", null, new AlertYesNo.Event() {
            @Override
            public void onON() {

            }

            @Override
            public void onYes() {

            }
        });
        alertYesNo.show();
    }

    /**
     * 添加表情、发送自定义表情
     *
     * @version 1.0
     * @createTime 2013-10-22,下午2:16:54
     * @updateTime 2013-10-22,下午2:16:54
     * @createAuthor liujingguo
     * @updateAuthor liujingguo
     * @updateInfo 增加参数 group 表情资源所属组
     */
    protected void sendFace(FaceBean bean) {
        if (FaceView.face_animo.equals(bean.getGroup())) {
            isSendingHypertext = false;

            ShippedExpressionMessage message = SocketData.createFaceMessage(SocketData.getUUID(), bean.getName());
            sendMessage(message, ChatEnum.EMessageType.SHIPPED_EXPRESSION);

        } else if (FaceView.face_emoji.equals(bean.getGroup()) || FaceView.face_lately_emoji.equals(bean.getGroup())) {
            Bitmap bitmap = null;
            if (FaceView.map_FaceEmoji != null) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), Integer.parseInt(FaceView.map_FaceEmoji.get(bean.getName()).toString()));
            } else {
                bitmap = BitmapFactory.decodeResource(context.getResources(), bean.getResId());
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, ExpressionUtil.dip2px(context, ExpressionUtil.DEFAULT_SIZE),
                    ExpressionUtil.dip2px(context, ExpressionUtil.DEFAULT_SIZE), true);
            ImageSpan imageSpan = new ImageSpan(context, bitmap);
            String str = bean.getName();
            SpannableString spannableString = new SpannableString(str);
            spannableString.setSpan(imageSpan, 0, PatternUtil.FACE_EMOJI_LENGTH, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // 插入到光标后位置
            getView().insertEditContent(spannableString);
        } else if (FaceView.face_custom.equals(bean.getGroup())) {
            if ("add".equals(bean.getName())) {
                if (!ViewUtils.isFastDoubleClick()) {
                    getView().hideBt();
                    getView().changeEmojiLevel(0);
                    IntentUtil.gotoActivity(context, AddFaceActivity.class);
                }
            } else {
                if (!checkNetConnectStatus()) {
                    return;
                }
                final String imgMsgId = SocketData.getUUID();
                ImageMessage imageMessage = SocketData.createImageMessage(imgMsgId, bean.getPath(), true);
                MsgAllBean msgAllBean = SocketData.sendFileUploadMessagePre(imgMsgId, model.getUid(), model.getGid(), SocketData.getFixTime(), imageMessage, ChatEnum.EMessageType.IMAGE);
                model.getListData().add(msgAllBean);
                // 不等于常信小助手
                if (!Constants.CX_HELPER_UID.equals(model.getUid())) {
                    final ImgSizeUtil.ImageSize img = ImgSizeUtil.getAttribute(bean.getPath());
                    SocketData.send4Image(imgMsgId, model.getUid(), model.getGid(), bean.getServerPath(), true, img, -1);
                }
                getView().notifyDataAndScrollBottom(true);
                MessageManager.getInstance().notifyRefreshMsg(model.isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, model.getUid(), model.getGid(), CoreEnum.ESessionRefreshTag.SINGLE, msgAllBean);
            }
        }
    }

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i(TAG, "onActivityResult");
        if (resultCode == activity.RESULT_OK) {
            switch (requestCode) {
                case VIDEO_RP:
                    int dataType = data.getIntExtra(RecordedActivity.INTENT_DATA_TYPE, RecordedActivity.RESULT_TYPE_VIDEO);
                    MsgAllBean videoMsgBean = null;
                    if (dataType == RecordedActivity.RESULT_TYPE_VIDEO) {
//                        if (!checkNetConnectStatus()) {
//                            return;
//                        }
                        String file = data.getStringExtra(RecordedActivity.INTENT_PATH);
                        int height = data.getIntExtra(RecordedActivity.INTENT_PATH_HEIGHT, 0);
                        int width = data.getIntExtra(RecordedActivity.INTENT_VIDEO_WIDTH, 0);
                        int time = data.getIntExtra(RecordedActivity.INTENT_PATH_TIME, 0);
                        VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), "file://" + file, getVideoAttBitmap(file), false, time, width, height, file);
                        MsgAllBean msgAllBean = sendMessage(videoMessage, ChatEnum.EMessageType.MSG_VIDEO, false);
                        // 不等于常信小助手
                        if (!Constants.CX_HELPER_UID.equals(model.getUid())) {
                            UpLoadService.onAddVideo(this.context, msgAllBean, false);
                            context.startService(new Intent(context, UpLoadService.class));
                        }
                    } else if (dataType == RecordedActivity.RESULT_TYPE_PHOTO) {
                        if (!checkNetConnectStatus()) {
                            return;
                        }
                        String photoPath = data.getStringExtra(RecordedActivity.INTENT_PATH);
                        String file = photoPath;

                        final boolean isArtworkMaster = requestCode == PictureConfig.REQUEST_CAMERA ? true : data.getBooleanExtra(PictureConfig.IS_ARTWORK_MASTER, false);
                        boolean isGif = FileUtils.isGif(file);
                        if (isArtworkMaster || isGif) {
                            file = photoPath;
                        }
                        final String imgMsgId = SocketData.getUUID();
                        if (TextUtils.isEmpty(file)) {
                            ToastUtil.show("图片异常,请重新选择");
                            return;
                        }
                        ImageMessage imageMessage = SocketData.createImageMessage(imgMsgId, /*"file://" + */file, isArtworkMaster);
                        videoMsgBean = sendMessage(imageMessage, ChatEnum.EMessageType.IMAGE, isArtworkMaster);
                        // 不等于常信小助手
                        if (!Constants.CX_HELPER_UID.equals(model.getUid())) {
                            UpLoadService.onAddImage(videoMsgBean, file, isArtworkMaster);
                            context.startService(new Intent(context, UpLoadService.class));
                        }
                    }
                    getView().notifyDataAndScrollBottom(true);
                    MessageManager.getInstance().notifyRefreshMsg(model.isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, model.getUid(), model.getGid(), CoreEnum.ESessionRefreshTag.SINGLE, videoMsgBean);

                    break;
                case PictureConfig.REQUEST_CAMERA:
                case PictureConfig.CHOOSE_REQUEST:
                    if (!checkNetConnectStatus()) {
                        return;
                    }
                    // 图片选择结果回调
                    List<LocalMedia> obt = PictureSelector.obtainMultipleResult(data);
                    if (obt != null && obt.size() > 0) {
                        LogUtil.getLog().e("=图片选择结果回调===" + GsonUtils.optObject(obt.get(0)));
                    }
                    MsgAllBean imgMsgBean = null;
                    for (LocalMedia localMedia : obt) {
                        String file = localMedia.getCompressPath();
                        if (StringUtil.isNotNull(file)) {
                            final boolean isArtworkMaster = requestCode == PictureConfig.REQUEST_CAMERA ? true : data.getBooleanExtra(PictureConfig.IS_ARTWORK_MASTER, false);
                            boolean isGif = FileUtils.isGif(file);
                            if (isArtworkMaster || isGif) {
                                //  Toast.makeText(this,"原图",Toast.LENGTH_LONG).show();
                                file = localMedia.getPath();
                            }
                            //1.上传图片
                            // alert.show();
                            final String imgMsgId = SocketData.getUUID();
                            // 记录本次上传图片的ID跟本地路径
//                            mTempImgPath.put(imgMsgId, "file://" + file);
                            ImageMessage imageMessage = SocketData.createImageMessage(imgMsgId, /*"file://" +*/ file, isArtworkMaster);//TODO:使用file://路径会使得检测本地路径不存在
//                            imgMsgBean = SocketData.sendFileUploadMessagePre(imgMsgId, model.getUid(), model.getGid(), SocketData.getFixTime(), imageMessage, ChatEnum.EMessageType.IMAGE);
//                            model.getListData().add(imgMsgBean);
                            MsgAllBean imageMsgBean = sendMessage(imageMessage, ChatEnum.EMessageType.IMAGE, false);
                            // 不等于常信小助手
                            if (!Constants.CX_HELPER_UID.equals(model.getUid())) {
//                                UpLoadService.onAddImage(imgMsgId, file, isArtworkMaster, model.getUid(), model.getGid(), -1);
                                UpLoadService.onAddImage(imageMsgBean, file, isArtworkMaster);
                                context.startService(new Intent(context, UpLoadService.class));
                            }
                        } else {
                            String videofile = localMedia.getPath();
                            if (null != videofile) {
                                long length = ImgSizeUtil.getVideoSize(videofile);
                                long duration = Long.parseLong(getVideoAtt(videofile));
                                // 大于50M、5分钟不发送
                                if (ImgSizeUtil.formetFileSize(length) > 50) {
                                    ToastUtil.show(context, "不能选择超过50M的视频");
                                    continue;
                                }
                                if (duration > 5 * 60000) {
                                    ToastUtil.show(context, "不能选择超过5分钟的视频");
                                    continue;
                                }
                                VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), "file://" + videofile, getVideoAttBitmap(videofile), false, duration, Long.parseLong(getVideoAttWeith(videofile)), Long.parseLong(getVideoAttHeigh(videofile)), videofile);
                                videoMsgBean = sendMessage(videoMessage, ChatEnum.EMessageType.MSG_VIDEO, false);
                                // 不等于常信小助手
                                if (!Constants.CX_HELPER_UID.equals(model.getUid())) {
                                    UpLoadService.onAddVideo(context, videoMsgBean, false);
                                    context.startService(new Intent(context, UpLoadService.class));
                                }
                            } else {
                                ToastUtil.show(context, "文件已损坏，请重新选择");
                            }
                        }
                    }
                    MessageManager.getInstance().notifyRefreshMsg(model.isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, model.getUid(), model.getGid(), CoreEnum.ESessionRefreshTag.SINGLE, imgMsgBean);
                    getView().notifyDataAndScrollBottom(true);

                    break;
                case REQ_RP://红包
                    LogUtil.writeEnvelopeLog("云红包回调了");
                    LogUtil.getLog().e("云红包回调了");
                    EnvelopeBean envelopeInfo = JrmfRpClient.getEnvelopeInfo(data);
                    if (!checkNetConnectStatus()) {
                        if (envelopeInfo != null) {
                            saveMFEnvelope(envelopeInfo);
                        }
                        return;
                    }
                    if (envelopeInfo != null) {
                        //  ToastUtil.show(getContext(), "红包的回调" + envelopeInfo.toString());
                        String info = envelopeInfo.getEnvelopeMessage();
                        String rid = envelopeInfo.getEnvelopesID();
                        LogUtil.writeEnvelopeLog("rid=" + rid);
                        LogUtil.getLog().e("rid=" + rid);
                        MsgBean.RedEnvelopeMessage.RedEnvelopeStyle style = MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.NORMAL;
                        if (envelopeInfo.getEnvelopeType() == 1) {//拼手气
                            style = MsgBean.RedEnvelopeMessage.RedEnvelopeStyle.LUCK;
                        }
                        RedEnvelopeMessage message = SocketData.createRbMessage(SocketData.getUUID(), envelopeInfo.getEnvelopesID(), envelopeInfo.getEnvelopeMessage(), MsgBean.RedEnvelopeType.MFPAY.getNumber(), style.getNumber());
                        sendMessage(message, ChatEnum.EMessageType.RED_ENVELOPE);
                    }
                    break;

                case REQUEST_RED_ENVELOPE:
                    CxEnvelopeBean envelopeBean = data.getParcelableExtra("envelope");
                    if (envelopeBean != null) {
                        RedEnvelopeMessage message = SocketData.createSystemRbMessage(SocketData.getUUID(), envelopeBean.getTradeId(), envelopeBean.getActionId(),
                                envelopeBean.getMessage(), MsgBean.RedEnvelopeType.SYSTEM.getNumber(), envelopeBean.getEnvelopeType(), envelopeBean.getSign());
                        sendMessage(message, ChatEnum.EMessageType.RED_ENVELOPE);
                    }
                    break;
                case GroupSelectUserActivity.RET_CODE_SELECTUSR:
                    String uid = data.getStringExtra(GroupSelectUserActivity.UID);
                    String name = data.getStringExtra(GroupSelectUserActivity.MEMBERNAME);
                    if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(name)) {
                        getView().addAtSpan(null, name, Long.valueOf(uid));
                    }
                    break;
            }
        } else if (resultCode == SelectUserActivity.RET_CODE_SELECTUSR) {//选择通讯录中的某个人
            if (!checkNetConnectStatus()) {
                return;
            }
            String json = data.getStringExtra(SelectUserActivity.RET_JSON);
            UserInfo userInfo = GsonUtils.getObject(json, UserInfo.class);
            BusinessCardMessage cardMessage = SocketData.createCardMessage(SocketData.getUUID(), userInfo.getHead(), userInfo.getName(), userInfo.getImid(), userInfo.getUid());
            sendMessage(cardMessage, ChatEnum.EMessageType.BUSINESS_CARD);
        }
    }


    private String getVideoAtt(String mUri) {
        String duration = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
            }
            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)

        } catch (Exception ex) {
            LogUtil.getLog().e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return duration;
    }

    private String getVideoAttWeith(String mUri) {
        String width = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
//                HashMap<String, String> headers = null;
//                if (headers == null)
//                {
//                    headers = new HashMap<String, String>();
//                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
//                }
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
//                mmr.setDataSource(mUri, headers);
            } else {
                //mmr.setDataSource(mFD, mOffset, mLength);
            }
            width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽

        } catch (Exception ex) {
            LogUtil.getLog().e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return width;
    }

    private String getVideoAttHeigh(String mUri) {
        String height = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
//                mmr.setDataSource(mUri, headers);
            } else {
                //mmr.setDataSource(mFD, mOffset, mLength);
            }
            height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高

        } catch (Exception ex) {
            LogUtil.getLog().e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return height;
    }

    private String getVideoAttBitmap(String mUri) {
        File file = null;
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
//                mmr.setDataSource(mUri, headers);
            } else {
                //mmr.setDataSource(mFD, mOffset, mLength);
            }
            file = GroupHeadImageUtil.save2File(mmr.getFrameAtTime());
        } catch (Exception ex) {
            LogUtil.getLog().e("TAG", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }
        return file.getAbsolutePath();
    }

    //是否是需要上传的消息类型：图片，语音，视频，文件等
    private boolean isUploadType(int msgType) {
        if (msgType == ChatEnum.EMessageType.IMAGE || msgType == ChatEnum.EMessageType.VOICE || msgType == ChatEnum.EMessageType.MSG_VIDEO || msgType == ChatEnum.EMessageType.FILE) {
            return true;
        }
        return false;

    }

    //清除
    final boolean clearSessionUnread(boolean isFirst) {
        Session session = model.getSession();
        if (session != null && session.getUnread_count() > 0) {
            if (isFirst) {
                model.setUnreadCount(session.getUnread_count());
            }
            msgDao.sessionReadClean(session);
            if (isFirst) {
                MessageManager.getInstance().setMessageChange(true);
                MessageManager.getInstance().notifyRefreshMsg(model.isGroup() ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, model.getUid(), model.getGid(), CoreEnum.ESessionRefreshTag.SINGLE, null);
            }
            return true;
        }
        return false;
    }

    final boolean updateMsgRead() {
        return msgDao.updateMsgRead(model.getUid(), model.getGid(), true);
    }

    final boolean updateSessionDraftAndAtMessage() {
        boolean hasChange = false;
        Session session = model.getSession();
        if (session != null && !TextUtils.isEmpty(session.getAtMessage())) {
            hasChange = true;
            msgDao.updateSessionAtMsg(model.getGid(), model.getUid());
        }
        if (checkAndSaveDraft()) {
            hasChange = true;
        }
        return hasChange;
    }

    private boolean checkAndSaveDraft() {
        if (model.isGroup() && !MessageManager.getInstance().isGroupValid(model.getGroup())) {//无效群，不存草稿
            return false;
        }
        String df = getView().getEtText();
        boolean hasChange = false;
        if (!TextUtils.isEmpty(model.getDraft())) {
//            if (TextUtils.isEmpty(df) || !draft.equals(df)) {
            hasChange = true;
            msgDao.sessionDraft(model.getGid(), model.getUid(), df);
            model.setDraft(df);
//            }
        } else {
            if (!TextUtils.isEmpty(df)) {
                hasChange = true;
                msgDao.sessionDraft(model.getGid(), model.getUid(), df);
                model.setDraft(df);
            }
        }
        return hasChange;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doAckEvent(AckEvent event) {
        Object data = event.getData();
        if (data instanceof MsgAllBean) {
            LogUtil.getLog().i(TAG, "收到回执--MsgAllBean");
            MsgAllBean msgAllBean = (MsgAllBean) data;
            fixSendTime(msgAllBean.getMsg_id());
            getView().replaceListDataAndNotify(msgAllBean);
        } else if (data instanceof MsgBean.AckMessage) {
            LogUtil.getLog().i(TAG, "收到回执--AckMessage");
            MsgBean.AckMessage bean = (MsgBean.AckMessage) data;
            if (bean.getMsgIdList() != null && bean.getMsgIdList().size() > 0) {
                fixSendTime(bean.getMsgId(0));
            }
            //群聊自己发送的消息直接加入阅后即焚队列
            MsgAllBean msgAllBean = msgDao.getMsgById(bean.getMsgId(0));
            if (model.isGroup()) {
                addSurvivalTime(msgAllBean);
            }
            if (bean.getRejectType() == MsgBean.RejectType.NOT_FRIENDS_OR_GROUP_MEMBER || bean.getRejectType() == MsgBean.RejectType.IN_BLACKLIST) {
                loadAndSetMoreData(false);
            } else {
                if (UpLoadService.getProgress(bean.getMsgId(0)) == null /*|| UpLoadService.getProgress(bean.getMsgId(0)) == 100*/) {//忽略图片上传的刷新,图片上传成功后
                    for (String msgid : bean.getMsgIdList()) {
                        //撤回消息不做刷新
                        if (ChatServer.getCancelList().containsKey(msgid)) {
                            LogUtil.getLog().i(TAG, "onACK: 收到取消回执,等待刷新列表2");
                            return;
                        }
                    }
                }
                loadAndSetMoreData(false);
            }
        }
        //是否是长文本消息
        if (isSendingHypertext) {
            if (sendTexts != null && sendTexts.size() > 0 && textPosition != sendTexts.size() - 1) {
                sendHypertext(sendTexts, textPosition + 1);
            }
        }
    }

    /**
     * 添加阅读即焚消息到队列
     */
    public void addSurvivalTime(MsgAllBean msgBean) {
        if (msgBean == null || BurnManager.getInstance().isContainMsg(msgBean) || msgBean.getSend_state() != ChatEnum.ESendStatus.NORMAL) {
            return;
        }
        if (msgBean.getSurvival_time() > 0 && msgBean.getEndTime() == 0) {
            long date = DateUtils.getSystemTime();
            msgDao.setMsgEndTime((date + msgBean.getSurvival_time() * 1000), date, msgBean.getMsg_id());
            msgBean.setEndTime(date + msgBean.getSurvival_time() * 1000);
            msgBean.setStartTime(date);
            EventBus.getDefault().post(new EventSurvivalTimeAdd(msgBean, null));
            LogUtil.getLog().d("SurvivalTime", "设置阅后即焚消息时间1----> end:" + (date + msgBean.getSurvival_time() * 1000) + "---msgid:" + msgBean.getMsg_id());
        }
    }

    public void addSurvivalTimeAndRead(MsgAllBean msgBean) {
        if (msgBean == null || BurnManager.getInstance().isContainMsg(msgBean) || msgBean.getSend_state() != ChatEnum.ESendStatus.NORMAL) {
            return;
        }
        if (msgBean.getSurvival_time() > 0 && msgBean.getEndTime() == 0 && msgBean.getRead() == 1) {
            long date = DateUtils.getSystemTime();
            msgDao.setMsgEndTime((date + msgBean.getSurvival_time() * 1000), date, msgBean.getMsg_id());
            msgBean.setEndTime(date + msgBean.getSurvival_time() * 1000);
            msgBean.setStartTime(date);
            EventBus.getDefault().post(new EventSurvivalTimeAdd(msgBean, null));
            LogUtil.getLog().d("SurvivalTime", "设置阅后即焚消息时间2----> end:" + (date + msgBean.getSurvival_time() * 1000) + "---msgid:" + msgBean.getMsg_id());
        }
    }


    public void addSurvivalTimeForList(List<MsgAllBean> list) {
        if (list == null && list.size() == 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            MsgAllBean msgbean = list.get(i);
            if (msgbean.getSurvival_time() > 0 && msgbean.getEndTime() == 0) {
                long date = DateUtils.getSystemTime();
                msgDao.setMsgEndTime((date + msgbean.getSurvival_time() * 1000), date, msgbean.getMsg_id());
                msgbean.setEndTime(date + msgbean.getSurvival_time() * 1000);
                msgbean.setStartTime(date);
                LogUtil.getLog().d("SurvivalTime", "设置阅后即焚消息时间3----> end:" + (date + msgbean.getSurvival_time() * 1000) + "---msgid:" + msgbean.getMsg_id());
            }
        }
        EventBus.getDefault().post(new EventSurvivalTimeAdd(null, list));
    }

    //更新图片，视频，文件上传过程刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventUploadImage(EventUpImgLoadEvent event) {
        if (event.getState() == 0) {
            MsgAllBean msgAllbean = (MsgAllBean) event.getMsgAllBean();
            getView().replaceListDataAndNotify(msgAllbean);
        } else if (event.getState() == -1) {
            //处理失败的情况
            MsgAllBean msgAllbean = (MsgAllBean) event.getMsgAllBean();
            getView().replaceListDataAndNotify(msgAllbean);
        } else if (event.getState() == 1) {
            MsgAllBean msgAllbean = (MsgAllBean) event.getMsgAllBean();
            SocketData.sendAndSaveMessage(msgAllbean);
            getView().replaceListDataAndNotify(msgAllbean);
        }
    }


}
