package com.hm.cxpay.dailog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hm.cxpay.R;
import com.hm.cxpay.global.PayEnum;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.bank.BankBean;
import com.hm.cxpay.ui.redenvelope.GrabEnvelopeBean;
import com.hm.cxpay.ui.redenvelope.OpenEnvelopeBean;
import com.hm.cxpay.utils.UIUtils;
import com.hm.cxpay.widget.PswView;
import com.hm.cxpay.widget.RedAmina;

import net.cb.cb.library.base.BaseDialog;
import net.cb.cb.library.utils.ToastUtil;

/**
 * @author Liszt
 * @date 2019/12/3
 * Description 红包dialog
 */
public class DialogEnvelope extends BaseDialog {

    private ImageView ivClose;
    private ImageView ivAvatar;
    private TextView tvName;
    private TextView tvInfo;
    private ImageView ivOpen;
    private TextView tvMore;
    private String avatar;
    private String nick;
    private long tradeId;
    private String token;
    private int status;
    private String note;
    private IEnvelopeListener listener;

    public DialogEnvelope(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void initView() {
        setContentView(R.layout.fgm_redpacket_dialog);
        ivClose = findViewById(R.id.img_cls);
        ivAvatar = findViewById(R.id.img_uhead);
        tvName = findViewById(R.id.txt_uname);
        tvInfo = findViewById(R.id.txt_rb_info);
        ivOpen = findViewById(R.id.img_open);
        tvMore = findViewById(R.id.txt_more);

        ivClose.setOnClickListener(this);
        ivOpen.setOnClickListener(this);
    }

    @Override
    public void processClick(View view) {
        int id = view.getId();
        if (id == ivClose.getId()) {
            dismiss();
        } else if (id == ivOpen.getId()) {
            playAnim();
            openRedEnvelope(tradeId, token);
        }
    }

    /*
     * token , 红包准入token
     * status, 红包状态，1，正常可以抢
     * */
    public void setInfo(String token, int status, String avatar, String nick, long tradeId, String note) {
        this.token = token;
        this.status = status;
        this.avatar = avatar;
        this.nick = nick;
        this.tradeId = tradeId;
        this.note = note;
        updateUI(status);
    }

    private void updateUI(int envelopeStatus) {
        Glide.with(getContext()).load(avatar).into(ivAvatar);
        tvName.setText(nick);
        if (envelopeStatus == 1) {//正常，可以抢
            ivOpen.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(note)) {
                tvInfo.setText(note);
            } else {
                tvInfo.setText("恭喜发财，大吉大利");
            }
        } else if (envelopeStatus == 2) {//已经领完
            ivOpen.setVisibility(View.GONE);
            tvInfo.setText("手慢了，红包已经派完");

        } else if (envelopeStatus == 3) {//已经过期
            ivOpen.setVisibility(View.GONE);
            tvInfo.setText("红包已过期");
        } else if (envelopeStatus == 4) {//已经抢过了
            ivOpen.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(note)) {
                tvInfo.setText(note);
            } else {
                tvInfo.setText("恭喜发财，大吉大利");
            }
        }
    }

    //拆红包，获取token
    public void openRedEnvelope(final long tradeId, String token) {
        if (TextUtils.isEmpty(token)) {
            return;
        }
        PayHttpUtils.getInstance().openRedEnvelope(tradeId, token)
                .compose(RxSchedulers.<BaseResponse<OpenEnvelopeBean>>compose())
                .compose(RxSchedulers.<BaseResponse<OpenEnvelopeBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<OpenEnvelopeBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<OpenEnvelopeBean> baseResponse) {
                        stopAnim();
                        if (baseResponse.isSuccess()) {
                            OpenEnvelopeBean bean = baseResponse.getData();
                            if (bean != null) {
                                updateUIAfterOpen(bean);
                                if (listener != null) {
                                    listener.onOpen(tradeId, bean.getStat());
                                }
                            }
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
                        if (baseResponse.getCode() == -21000) {
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }
                    }
                });
    }

    /***
     * 动画处理
     */
    private void playAnim() {
        // ObjectAnimator
        RedAmina anim = new RedAmina();
        ivOpen.startAnimation(anim);
    }

    public void stopAnim() {
        ivOpen.clearAnimation();
    }

    public void updateUIAfterOpen(OpenEnvelopeBean bean) {
        ivOpen.setVisibility(View.GONE);
        int result = bean.getStat();
        if (result == 1) {//抢到
            tvInfo.setText("已领取" + UIUtils.getYuan(bean.getAmt()) + "元");
        } else if (result == 2) {//已领完
            tvInfo.setText("手慢了，红包已经派完");
        } else if (result == 3) {//已过期
            tvInfo.setText("红包已过期");
        } else if (result == 4) {//已领过
            tvInfo.setText("已领取" + UIUtils.getYuan(bean.getAmt()) + "元");
        }
    }

    public void setEnvelopeListener(IEnvelopeListener l) {
        listener = l;
    }

    public interface IEnvelopeListener {
        void onOpen(long rid, int envelopeStatus);

//        void onCancel(String token);
    }


}
