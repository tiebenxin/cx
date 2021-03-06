package com.yanlong.im.chat.ui.groupmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nim_lib.ui.BaseBindActivity;
import com.hm.cxpay.bean.EnvelopeDetailBean;
import com.hm.cxpay.bean.GrabEnvelopeBean;
import com.hm.cxpay.dailog.DialogEnvelope;
import com.hm.cxpay.global.PayEnum;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.utils.UIUtils;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgNotice;
import com.yanlong.im.chat.bean.NoRedEnvelopesBean;
import com.yanlong.im.chat.bean.RedEnvelopeMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.GroupSelectUserActivity;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.databinding.ActivityNoRedBagBinding;
import com.yanlong.im.databinding.ItemNoRedbagBinding;
import com.yanlong.im.pay.ui.record.SingleRedPacketDetailsActivity;
import com.yanlong.im.pay.ui.select.ViewAllowMemberActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ThreadUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-12-10
 * @updateAuthor
 * @updateDate
 * @description 未领取的零钱红包
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class NoRedBagActivity extends BaseBindActivity<ActivityNoRedBagBinding> {

    private CommonRecyclerViewAdapter<MsgAllBean, ItemNoRedbagBinding> mViewAdapter;
    private String mGid;
    private MsgDao msgDao = new MsgDao();
    private MsgAction mMsgAction;
    private boolean canGetRedPacket = true;//检测我是否有资格领取红包(是否被禁领红包)

    @Override
    protected int setView() {
        return R.layout.activity_no_red_bag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mViewAdapter = new CommonRecyclerViewAdapter<MsgAllBean, ItemNoRedbagBinding>(this, R.layout.item_no_redbag) {

            @Override
            public void bind(ItemNoRedbagBinding binding, MsgAllBean msgAllBean, int position, RecyclerView.ViewHolder viewHolder) {
                UIUtils.loadAvatar(msgAllBean.getFrom_avatar(), binding.imgHead);
                binding.txtName.setText(msgAllBean.getFrom_nickname());
                binding.tvTime.setText(TimeToString.YYYY_MM_DD_HH_MM_SS(msgAllBean.getTimestamp()));
                RedEnvelopeMessage message = msgAllBean.getRed_envelope();
                int reType = message.getRe_type().intValue();//红包类型
                String type = "";
                if (reType == MsgBean.RedEnvelopeType.SYSTEM_VALUE) {
                    type = "零钱红包";
                }
                binding.txtOtRpBt.setText(type);
                binding.txtOtRbTitle.setText(message.getComment());
                binding.txtOtRbInfo.setText(getEnvelopeInfo(message.getEnvelopStatus(), message.isHasPermission()));
                //红包领取状态
                if (message.getIsInvalid() == 0) {
                    binding.imgOtRbState.setImageResource(R.mipmap.ic_rb_zfb_un);
                    binding.layoutRedBag.setBackgroundResource(R.mipmap.ic_rb_not_received);
                } else {
                    binding.imgOtRbState.setImageResource(R.mipmap.ic_rb_zfb_n);
                    binding.layoutRedBag.setBackgroundResource(R.mipmap.ic_rb_received);
                }
                if (!msgAllBean.isMe() && !message.isHasPermission() && message.getCanReview() == 1) {
                    binding.tvViewMore.setVisibility(View.VISIBLE);
                } else {
                    binding.tvViewMore.setVisibility(View.GONE);
                }
                binding.layoutRedBag.setOnClickListener(o -> {
                    if (ViewUtils.isFastDoubleClick()) {
                        return;
                    }
                    if (message.getIsInvalid() == 0) {
                        if (canGetRedPacket) {
                            receiveEnvelope(msgAllBean);
                        } else {
                            ToastUtil.show("你已被禁止领取该群红包");
                        }
                    } else {
                        if (!message.isHasPermission()) {
                            if (canGetRedPacket) {
                                receiveEnvelope(msgAllBean);
                            } else {
                                ToastUtil.show("你已被禁止领取该群红包");
                            }
                        } else {
                            ToastUtil.show("已领取该红包");
                        }
                    }
                });
            }
        };

        bindingView.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bindingView.recyclerView.setAdapter(mViewAdapter);
    }

    @Override
    protected void initEvent() {
        setActionBarLeft(bindingView.headView);
    }

    @Override
    protected void loadData() {
        mMsgAction = new MsgAction();
        mGid = getIntent().getStringExtra(GroupSelectUserActivity.GID);
        loadMsg();
        getCantOpenUpRedMembers();
    }

    @SuppressLint("CheckResult")
    private void loadMsg() {
        Observable.just(0)
                .map(new Function<Integer, List<MsgAllBean>>() {
                    @Override
                    public List<MsgAllBean> apply(Integer integer) throws Exception {
                        if (UserAction.getMyId() == null) {
                            return null;
                        }
                        return msgDao.selectValidEnvelopeMsg(mGid, UserAction.getMyId().longValue());
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<MsgAllBean>>empty())
                .subscribe(new Consumer<List<MsgAllBean>>() {
                    @Override
                    public void accept(List<MsgAllBean> list) throws Exception {
                        if (list != null) {
                            mViewAdapter.setData(list);
                        }
                    }
                });

    }

    private void receiveEnvelope(MsgAllBean bean) {
        RedEnvelopeMessage message = bean.getRed_envelope();
        if (message.getRe_type() == 1) {//零钱红包
            if (!message.isHasPermission()) {
                if (TextUtils.isEmpty(message.getAccessToken())) {
                    grabRedEnvelopeNoAllow(bean, message.getTraceId(), message.getRe_type(), message.getEnvelopStatus());
                } else {
                    boolean isAllow = false;
                    if (bean.isMe() /*|| message.getEnvelopStatus() == PayEnum.EEnvelopeStatus.ERROR*/) {
                        isAllow = true;
                    } else {
                        if (message.getCanReview() == 1) {
                            isAllow = true;
                        }
                    }
                    getEnvelopeDetail(message.getTraceId(), message.getAccessToken(), message.getEnvelopStatus(), bean, isAllow, message.isHasPermission());
                }
            } else {
                if (!TextUtils.isEmpty(message.getAccessToken())) {
                    showEnvelopeDialog(message.getAccessToken(), message.getEnvelopStatus(), bean, message.getRe_type());
                } else {
                    grabRedEnvelope(bean, message.getTraceId(), message.getRe_type());
                }
            }
        }
    }


    private void showEnvelopeDialog(String token, int status, MsgAllBean msgBean, int reType) {
        DialogEnvelope dialogEnvelope = new DialogEnvelope(NoRedBagActivity.this, com.hm.cxpay.R.style.MyDialogTheme);
        dialogEnvelope.setEnvelopeListener(new DialogEnvelope.IEnvelopeListener() {
            @Override
            public void onOpen(long rid, int envelopeStatus, boolean isLast) {
                //TODO: 开红包后，先发送领取红包消息给服务端，然后更新红包状态，最后保存领取红包通知消息到本地
                taskPayRbCheck(msgBean, rid + "", reType, token, getOpenEnvelopeStatus(envelopeStatus));
                if (envelopeStatus == 1) {//抢到了
                    if (!msgBean.isMe()) {
                        SocketData.sendReceivedEnvelopeMsg(msgBean.getFrom_uid(), mGid, rid + "", reType, isLast);//发送抢红包消息
                    }
                    MsgNotice message = SocketData.createMsgNoticeOfRb(SocketData.getUUID(), msgBean.getFrom_uid(), mGid, rid + "");
                    MsgAllBean msgAllBean = SocketData.createMessageBean(msgBean.getTo_uid(), msgBean.getGid(), ChatEnum.EMessageType.NOTICE, ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), message);
                    MessageManager.getInstance().saveMessage(msgAllBean);
                    mViewAdapter.remove(msgBean);
                    notifyRefreshChat();
                }
            }

            @Override
            public void viewRecord(long rid, String token, int style) {
                getRedEnvelopeDetail(msgBean, rid, token, reType, style == 0, false);
            }

            @Override
            public void viewAllowUser() {
                Intent intent = ViewAllowMemberActivity.newIntent(NoRedBagActivity.this, msgBean.getGid(), MessageManager.getInstance().getMemberIds(msgBean.getRed_envelope().getAllowUsers()));
                startActivity(intent);
            }
        });
        RedEnvelopeMessage message = msgBean.getRed_envelope();
        dialogEnvelope.setInfo(token, status, msgBean.getFrom_avatar(), msgBean.getFrom_nickname(), getEnvelopeId(message.getId(), message.getTraceId()), message.getComment(), message.getStyle());
        dialogEnvelope.show();
    }

    //获取拆红包后，红包状态
    private int getOpenEnvelopeStatus(int stat) {
        int status = PayEnum.EEnvelopeStatus.NORMAL;
        if (stat == 0) {//1 正常待领取状态
            status = PayEnum.EEnvelopeStatus.NORMAL;
        }
        if (stat == 1) {//1 领取
            status = PayEnum.EEnvelopeStatus.RECEIVED;
        } else if (stat == 2) {//已领完
            status = PayEnum.EEnvelopeStatus.RECEIVED_FINISHED;
        } else if (stat == 3) {//已过期
            status = PayEnum.EEnvelopeStatus.PAST;
        } else if (stat == 4) {//领到
            status = PayEnum.EEnvelopeStatus.RECEIVED;
        }
        return status;
    }

    //获取拆红包后，红包状态
    private int getOpenEnvelopeStatus(EnvelopeDetailBean bean) {
        int status = PayEnum.EEnvelopeStatus.NORMAL;
        //过期
        if (PayEnvironment.getInstance().getFixTime() * 1000 - bean.getTime() >= TimeToString.DAY) {
            status = PayEnum.EEnvelopeStatus.PAST;
            return status;
        }
        if (bean.getType() == 0) {//普通红包
            if (bean.getRecvList() != null) {
                int size = bean.getRecvList().size();
                if (size > 0) {
                    int count = bean.getCnt();
                    if (count == size) {
                        return PayEnum.EEnvelopeStatus.RECEIVED_FINISHED;
                    }
                }
            }
        } else {//拼手气红包
            if (bean.getRecvList() != null) {
                int size = bean.getRecvList().size();
                if (size > 0) {
                    int count = bean.getCnt();
                    if (count == size) {
                        return PayEnum.EEnvelopeStatus.RECEIVED_FINISHED;
                    }
                }
            }
        }
        return status;
    }

    public long getEnvelopeId(String rid, long tradeId) {
        long result = tradeId;
        if (tradeId == 0 && !TextUtils.isEmpty(rid)) {
            try {
                result = Long.parseLong(rid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    //获取红包详情
    public void getRedEnvelopeDetail(MsgAllBean msgBean, long rid, String token, int reType, boolean isNormalStyle, boolean hasPermission) {
        if (TextUtils.isEmpty(token)) {
            String from = "";
            if (!TextUtils.isEmpty(mGid)) {
                from = mGid;
            }
            if (TextUtils.isEmpty(from)) {
                return;
            }
            PayHttpUtils.getInstance().grabRedEnvelope(rid, from)
                    .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>compose())
                    .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>handleResult())
                    .subscribe(new FGObserver<BaseResponse<GrabEnvelopeBean>>() {
                        @Override
                        public void onHandleSuccess(BaseResponse<GrabEnvelopeBean> baseResponse) {
                            if (baseResponse.isSuccess()) {
                                GrabEnvelopeBean bean = baseResponse.getData();
                                if (bean != null) {
                                    if (isNormalStyle) {//普通玩法红包需要保存
                                        taskPayRbCheck(msgBean, rid + "", reType, bean.getAccessToken(), PayEnum.EEnvelopeStatus.NORMAL);
                                    }
                                    getEnvelopeDetail(rid, token, msgBean.getRed_envelope().getEnvelopStatus(), msgBean, true, hasPermission);
                                }
                            } else {
                                ToastUtil.show(getContext(), baseResponse.getMessage());
                            }
                        }

                        @Override
                        public void onHandleError(BaseResponse baseResponse) {
                            if (baseResponse.getCode() == -21000) {
                            } else {
                                ToastUtil.show(getContext(), baseResponse.getMessage());
                            }
                        }
                    });
        } else {
            getEnvelopeDetail(rid, token, msgBean.getRed_envelope().getEnvelopStatus(), msgBean, true, hasPermission);
        }
    }

    private void getEnvelopeDetail(long rid, String token, int envelopeStatus, MsgAllBean msgBean, boolean isAllow, boolean hasPermission) {
        PayHttpUtils.getInstance().getEnvelopeDetail(rid, token, 0)
                .compose(RxSchedulers.<BaseResponse<EnvelopeDetailBean>>compose())
                .compose(RxSchedulers.<BaseResponse<EnvelopeDetailBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<EnvelopeDetailBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<EnvelopeDetailBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            EnvelopeDetailBean bean = baseResponse.getData();
                            if (bean != null) {
                                if (!hasPermission && (bean.getRecvList() != null && bean.getRecvList().size() > 0)) {
                                    if (bean.getRecvList().size() == bean.getCnt()) {
                                        updateEnvelopeDetail(msgBean, rid + "", msgBean.getRed_envelope().getRe_type(), token, PayEnum.EEnvelopeStatus.RECEIVED_FINISHED, 1);
                                        mViewAdapter.remove(msgBean);
                                    } else {
                                        updateEnvelopeDetail(msgBean, rid + "", msgBean.getRed_envelope().getRe_type(), token, PayEnum.EEnvelopeStatus.RECEIVED_UNDONE, 1);
                                    }
                                } else {
                                    if (envelopeStatus == PayEnum.EEnvelopeStatus.NORMAL && envelopeStatus != getOpenEnvelopeStatus(bean)) {
                                        taskPayRbCheck(msgBean, rid + "", msgBean.getRed_envelope().getRe_type(), token, getOpenEnvelopeStatus(bean));
                                    }
                                }
                                bean.setChatType(1);
                                bean.setEnvelopeStatus(envelopeStatus);
                                if (!isAllow && (bean.getRecvList() != null && bean.getRecvList().size() <= 0)) {

                                } else {
                                    Intent intent = SingleRedPacketDetailsActivity.newIntent(NoRedBagActivity.this, bean);
                                    startActivity(intent);
                                }
                            }
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        if (baseResponse.getCode() == -21000) {
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }
                    }
                });
    }

    //抢红包，获取token
    public void grabRedEnvelope(MsgAllBean msgBean, long rid, int reType) {
        String from = "";
        if (!TextUtils.isEmpty(mGid)) {
            from = mGid;
        }
        if (TextUtils.isEmpty(from)) {
            return;
        }
        PayHttpUtils.getInstance().grabRedEnvelope(rid, from)
                .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>compose())
                .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<GrabEnvelopeBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<GrabEnvelopeBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            GrabEnvelopeBean bean = baseResponse.getData();
                            if (bean != null) {
                                int status = getGrabEnvelopeStatus(bean.getStat());
                                updateEnvelopeToken(msgBean, rid + "", reType, bean.getAccessToken(), status);
                                showEnvelopeDialog(bean.getAccessToken(), status, msgBean, reType);
                            }
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        if (baseResponse.getCode() == -21000) {
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }
                    }
                });
    }

    //获取抢红包后，红包状态
    private int getGrabEnvelopeStatus(int stat) {
        int status = PayEnum.EEnvelopeStatus.NORMAL;
        if (stat == 1) {//1 未领取
            status = PayEnum.EEnvelopeStatus.NORMAL;
        } else if (stat == 2) {//已领完
            status = PayEnum.EEnvelopeStatus.RECEIVED_FINISHED;
        } else if (stat == 3) {//已过期
            status = PayEnum.EEnvelopeStatus.PAST;
        } else if (stat == 4) {////未领到，出错了
            status = PayEnum.EEnvelopeStatus.ERROR;
        }
        return status;
    }


    //抢红包后，更新红包token
    private void updateEnvelopeToken(MsgAllBean msgAllBean, final String rid, int reType, String token, int envelopeStatus) {
        if (!TextUtils.isEmpty(token)) {
            msgAllBean.getRed_envelope().setAccessToken(token);
            msgAllBean.getRed_envelope().setEnvelopStatus(envelopeStatus);
            if (envelopeStatus > 0) {
                msgAllBean.getRed_envelope().setIsInvalid(1);
            }
        }
        msgDao.redEnvelopeOpen(rid, envelopeStatus, reType, token);
        if (msgAllBean.getRed_envelope().isHasPermission()) {
            if (envelopeStatus != 0) {
                mViewAdapter.remove(msgAllBean);
            }
        } else {
            mViewAdapter.notifyItemChange(msgAllBean);
        }
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
        msgDao.redEnvelopeOpen(rid, envelopeStatus, reType, token);
    }


    /***
     * 红包是否已经被抢,红包改为失效
     * @param rid
     */
    private void updateEnvelopeDetail(MsgAllBean msgAllBean, String rid, int reType, String token, int envelopeStatus, int canReview) {
        if (envelopeStatus != PayEnum.EEnvelopeStatus.NORMAL) {
            msgAllBean.getRed_envelope().setIsInvalid(1);
            msgAllBean.getRed_envelope().setEnvelopStatus(envelopeStatus);
        }
        if (!TextUtils.isEmpty(token)) {
            msgAllBean.getRed_envelope().setAccessToken(token);
        }
        if (canReview == 1) {
            msgAllBean.getRed_envelope().setCanReview(canReview);
        }
        msgDao.updateEnvelopeDetail(rid, envelopeStatus, reType, token, canReview);
    }

    public void notifyRefreshChat() {
        EventBus.getDefault().post(new EventRefreshChat<>());
    }


    /**
     * 获取禁领红包群成员列表
     */
    private void getCantOpenUpRedMembers() {
        mMsgAction.getCantOpenUpRedMembers(mGid, new CallBack<ReturnBean<List<NoRedEnvelopesBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<NoRedEnvelopesBean>>> call, Response<ReturnBean<List<NoRedEnvelopesBean>>> response) {
                super.onResponse(call, response);
                if (response.body() != null && response.body().isOk()) {
                    List<NoRedEnvelopesBean> list = response.body().getData();
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getUid() == UserAction.getMyInfo().getUid().longValue()) {
                                canGetRedPacket = false;
                            }
                        }
                    } else {
                        canGetRedPacket = true;
                    }
                } else {
                    ToastUtil.show(NoRedBagActivity.this, "获取禁领红包群成员列表失败");
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<NoRedEnvelopesBean>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    private String getEnvelopeInfo(@PayEnum.EEnvelopeStatus int envelopStatus, boolean hasPermission) {
        String info = "";
        if (!hasPermission) {
            info = "权限限制，不可领取";
        } else {
            switch (envelopStatus) {
                case PayEnum.EEnvelopeStatus.NORMAL:
                case PayEnum.EEnvelopeStatus.ERROR:
                    info = "领取红包";
                    break;
                case PayEnum.EEnvelopeStatus.RECEIVED:
                    info = "已领取";
                    break;
                case PayEnum.EEnvelopeStatus.RECEIVED_FINISHED:
                    info = "已被领完";
                    break;
                case PayEnum.EEnvelopeStatus.PAST:
                    info = "已过期";
                    break;
            }
        }
        return info;
    }

    //抢定向红包，获取token
    public void grabRedEnvelopeNoAllow(MsgAllBean msgBean, long rid, int reType, final int envelopeStatus) {
        String from = mGid;
        if (TextUtils.isEmpty(from)) {
            return;
        }
        PayHttpUtils.getInstance().grabRedEnvelope(rid, from)
                .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>compose())
                .compose(RxSchedulers.<BaseResponse<GrabEnvelopeBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<GrabEnvelopeBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<GrabEnvelopeBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            GrabEnvelopeBean bean = baseResponse.getData();
                            int status = envelopeStatus;
                            if (bean != null) {
                                if (status == PayEnum.EEnvelopeStatus.NORMAL) {
                                    status = getGrabEnvelopeStatus(bean.getStat());
                                }
                                updateEnvelopeToken(msgBean, rid + "", reType, bean.getAccessToken(), status);
                                getEnvelopeDetail(rid, bean.getAccessToken(), envelopeStatus, msgBean, msgBean.isMe() ? true : false, false);
                            }
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }
                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        if (baseResponse.getCode() == -21000) {
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }
                    }
                });
    }

}
