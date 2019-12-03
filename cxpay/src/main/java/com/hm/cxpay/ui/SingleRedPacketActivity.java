package com.hm.cxpay.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.hm.cxpay.R;
import com.hm.cxpay.base.BasePayActivity;
import com.hm.cxpay.databinding.ActivitySingleRedPacketBinding;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.redenvelope.RedSendBean;
import com.hm.cxpay.utils.UIUtils;

import net.cb.cb.library.utils.NumRangeInputFilter;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.PopupSelectView;

//发送单个红包界面
public class SingleRedPacketActivity extends BasePayActivity {

    private String[] strings = {"红包记录", "取消"};
    private PopupSelectView popupSelectView;
    private ActivitySingleRedPacketBinding ui;
    private long uid;

    public static Intent newIntent(Context context, long uid) {
        Intent intent = new Intent(context, SingleRedPacketActivity.class);
        intent.putExtra("uid", uid);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_single_red_packet);
        Intent intent = getIntent();
        uid = intent.getLongExtra("uid", -1);
        initView();
        initEvent();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        ui.headView.getActionbar().setTxtLeft("取消");
        ui.btnCommit.setEnabled(false);//默认不能点击
        ui.headView.getActionbar().getBtnLeft().setVisibility(View.GONE);
        ui.headView.getActionbar().getBtnRight().setImageResource(R.mipmap.ic_more);
        ui.headView.getActionbar().getBtnRight().setVisibility(View.VISIBLE);
        ui.edMoney.setFilters(new InputFilter[]{new NumRangeInputFilter(this)});
    }

    private void initEvent() {
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                initPopup();
            }
        });

        ui.edMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if (!TextUtils.isEmpty(string)) {
                    ui.edMoney.setEnabled(true);
                    ui.tvMoney.setText(string);
                } else {
                    ui.edMoney.setEnabled(false);
                    ui.tvMoney.setText("0.00");
                }

            }
        });

        ui.btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.show(SingleRedPacketActivity.this, "发红包");
                String note = UIUtils.getRedEnvelopeContent(ui.edContent);
            }
        });

    }

    private void initPopup() {
        hideKeyboard();
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(ui.headView.getActionbar(), Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 0:
//                        Intent intent = new Intent(SingleRedPacketActivity.this,RedpacketRecordActivity.class);
//                        startActivity(intent);
                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }

    /**
     * 发送单个红包
     */
    private void sendRedEnvelope(long money, String psw, String note, long bankCardId) {
        if (uid <= 0) {
            return;
        }
        PayHttpUtils.getInstance().sendRedEnvelopeToUser(money, 1, psw, 0, bankCardId, note, uid)
                .compose(RxSchedulers.<BaseResponse<RedSendBean>>compose())
                .compose(RxSchedulers.<BaseResponse<RedSendBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<RedSendBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<RedSendBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            RedSendBean sendBean = baseResponse.getData();
                            if (sendBean != null && sendBean.getCode() == 1) {//code  1表示成功，2失败，99处理中

                            } else {

                            }

                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
                        ToastUtil.show(getContext(), baseResponse.getMessage());
                    }
                });
    }

}
