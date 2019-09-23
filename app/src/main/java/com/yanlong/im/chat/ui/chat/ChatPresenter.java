package com.yanlong.im.chat.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.jrmf360.rplib.JrmfRpClient;
import com.jrmf360.rplib.bean.TransAccountBean;
import com.jrmf360.rplib.utils.callback.TransAccountCallBack;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.VoiceMessage;
import com.yanlong.im.chat.ui.ChatActivity;
import com.yanlong.im.pay.action.PayAction;
import com.yanlong.im.pay.bean.SignatureBean;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.base.BasePresenter;
import net.cb.cb.library.base.DBOptionObserver;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.RunUtils;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.view.MsgEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @anthor Liszt
 * @data 2019/9/19
 * Description
 */
public class ChatPresenter extends BasePresenter<ChatModel, ChatView> implements SocketEvent {
    private final String TAG = "ChatActivity";
    public final static int MIN_TEXT = 1000;//
    //红包和转账
    public static final int REQ_RP = 9653;
    public static final int REQ_TRANS = 9653;

    private List<String> sendTexts;//文本分段发送
    private List<MsgAllBean> downloadList = new ArrayList<>();//下载列表
    private Map<String, MsgAllBean> uploadMap = new HashMap<>();//上传列表
    private List<MsgAllBean> uploadList = new ArrayList<>();//上传列表
    private Context context;
    private boolean isSendingHypertext;
    private int textPosition;
    private final PayAction payAction = new PayAction();

    public void init(Context con) {
        context = con;
    }

    public void loadAndSetData() {
        Observable<List<MsgAllBean>> observable = model.loadMessages();
        observable.subscribe(new DBOptionObserver<List<MsgAllBean>>() {
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

    @Override
    public void onHeartbeat() {

    }

    @Override
    public void onACK(MsgBean.AckMessage bean) {

    }

    @Override
    public void onMsg(MsgBean.UniversalMessage bean) {

    }

    @Override
    public void onSendMsgFailure(MsgBean.UniversalMessage.Builder bean) {

    }

    @Override
    public void onLine(boolean state) {

    }

    public void initUnreadCount() {
        getView().initUnreadCount(model.getUnreadCount());
    }

    //重新发送消息
    private void resendMessage(MsgAllBean msgBean) {
        //从数据拉出来,然后再发送
        MsgAllBean reMsg = DaoUtil.findOne(MsgAllBean.class, "msg_id", msgBean.getMsg_id());

        try {
            LogUtil.getLog().d(TAG, "点击重复发送" + reMsg.getMsg_id() + "--" + reMsg.getTimestamp());
            if (reMsg.getMsg_type() == ChatEnum.EMessageType.IMAGE) {//图片重发处理7.31
                String file = reMsg.getImage().getLocalimg();
                if (!TextUtils.isEmpty(file)) {
                    boolean isArtworkMaster = StringUtil.isNotNull(reMsg.getImage().getOrigin()) ? true : false;
                    ImageMessage image = SocketData.createImageMessage(reMsg.getMsg_id(), file, isArtworkMaster);
                    MsgAllBean imgMsgBean = SocketData.sendFileUploadMessagePre(reMsg.getMsg_id(), model.getUid(), model.getGid(), reMsg.getTimestamp(), image, ChatEnum.EMessageType.IMAGE);
                    getView().replaceListDataAndNotify(imgMsgBean);
                    getView().startUploadServer(reMsg, file, isArtworkMaster);
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
            } else {
                //点击发送的时候如果要改变成发送中的状态
                reMsg.setSend_state(ChatEnum.ESendStatus.SENDING);
                DaoUtil.update(reMsg);
                MsgBean.UniversalMessage.Builder bean = MsgBean.UniversalMessage.parseFrom(reMsg.getSend_data()).toBuilder();
                SocketUtil.getSocketUtil().sendData4Msg(bean);
                loadAndSetData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void uploadVoice(String file, final MsgAllBean bean) {
        uploadMap.put(bean.getMsg_id(), bean);
        uploadList.add(bean);
        updateSendStatus(ChatEnum.ESendStatus.SENDING, bean);
        new UpFileAction().upFile(UpFileAction.PATH.VOICE, context, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                Log.v(ChatActivity.class.getSimpleName(), "上传语音成功--" + url);
                VoiceMessage voice = bean.getVoiceMessage();
                voice.setUrl(url);
                SocketData.sendMessage(bean);
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

    public void doSendText(MsgEditText edtChat, boolean isGroup) {
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
                MsgAllBean msgAllbean = SocketData.send4At(model.getUid(), model.getGid(), text, 1, edtChat.getUserIdList());
//                showSendObj(msgAllbean);
                loadAndSetData();
                edtChat.getText().clear();
            } else {
                MsgAllBean msgAllbean = SocketData.send4At(model.getUid(), model.getGid(), text, 0, edtChat.getUserIdList());
//                showSendObj(msgAllbean);
                loadAndSetData();
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
                    MsgAllBean msgAllbean = SocketData.send4Chat(model.getUid(), model.getGid(), text);
//                    showSendObj(msgAllbean);
                    loadAndSetData();
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
        SocketData.send4Chat(model.getUid(), model.getGid(), list.get(position));
        loadAndSetData();
//        MsgAllBean msgAllbean = SocketData.send4Chat(model.getUid(), model.getGid(), list.get(position));
//        showSendObj(msgAllbean);
    }

    private void taskTestSend(final int count) {
        ToastUtil.show(context, "连续发送" + count + "测试开始");
        new RunUtils(new RunUtils.Enent() {
            @Override
            public void onRun() {

                try {
                    for (int i = 1; i <= count; i++) {
                        if (i % 10 == 0)
                            SocketData.send4Chat(model.getUid(), model.getGid(), "连续测试发送" + i + "-------");
                        else
                            SocketData.send4Chat(model.getUid(), model.getGid(), "连续测试发送" + i);

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


}
