package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.utils.DialogUtils;
import com.yanlong.im.utils.UserUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.CloseActivityEvent;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CallBack4Btn;
import net.cb.cb.library.utils.CheckUtil;
import net.cb.cb.library.utils.ClickFilter;
import net.cb.cb.library.utils.CountDownUtil;
import net.cb.cb.library.utils.ErrorCode;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.RunUtils;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.ThreadUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Response;

public class IdentifyingCodeActivity extends AppActivity implements View.OnClickListener {
    public final static String PHONE = "phone";
    private EditText mEtPhoneContent;
    private EditText mEtIdentifyingCodeContent;
    private TextView mTvPassword;
    private Button mBtnLogin;
    private TextView mTvGetVerificationCode;
    private UserAction userAction;
    private HeadView mHeadView;
    private String phone;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshBalance(EventFactory.ExitActivityEvent event) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setContentView(R.layout.activity_identifying_code);
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CountDownUtil.cancelTimer();
    }

    private void initView() {
        mEtPhoneContent = findViewById(R.id.et_phone_content);
        mEtIdentifyingCodeContent = findViewById(R.id.et_identifying_code_content);
        mTvGetVerificationCode = findViewById(R.id.tv_get_verification_code);
        mTvPassword = findViewById(R.id.tv_password);
        mBtnLogin = findViewById(R.id.btn_login);
        mHeadView = findViewById(R.id.headView);
        phone = getIntent().getStringExtra(PHONE);
        if (!TextUtils.isEmpty(phone)) {
            mEtPhoneContent.setText(phone);
            if(phone.length()<=11){ //TODO bugly #284425
                mEtPhoneContent.setSelection(phone.length());
            }
        }

    }

    private void initEvent() {
        mTvPassword.setOnClickListener(this);
        mTvGetVerificationCode.setOnClickListener(this);
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        ClickFilter.onClick(mBtnLogin, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void initData() {
        userAction = new UserAction();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_password:
                Intent intent = new Intent(this, PasswordLoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_get_verification_code:
                initCountDownUtil();
                break;
        }
    }

    private void initCountDownUtil() {
        final String phone = mEtPhoneContent.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(IdentifyingCodeActivity.this, "请填写手机号码");
            return;
        }
        if (!CheckUtil.isMobileNO(phone)) {
            ToastUtil.show(this, "手机号格式不正确");
            return;
        }
        CountDownUtil.getTimer(60, mTvGetVerificationCode, "获取验证码", this, new CountDownUtil.CallTask() {
            @Override
            public void task() {
                taskGetSms(phone);
            }
        });
        //点击获取验证码后，光标自行跳转
        mEtIdentifyingCodeContent.requestFocus();
    }


    private void login() {
        InputUtil.hideKeyboard(this);
        final String phone = mEtPhoneContent.getText().toString();
        final String code = mEtIdentifyingCodeContent.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(this, "请输入账号");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            ToastUtil.show(this, "请输入验证码");
            return;
        }
        if (!CheckUtil.isMobileNO(phone)) {
            ToastUtil.show(this, "手机号格式不正确");
            return;
        }
        LogUtil.getLog().i("youmeng", "IdentifyingCodeActivity------->getDevId");
        new RunUtils(new RunUtils.Enent() {
            String devId;

            @Override
            public void onRun() {
                devId = UserAction.getDevId(getContext());
            }

            @Override
            public void onMain() {
                userAction.login4Captch(phone, code, devId, new CallBack4Btn<ReturnBean<TokenBean>>(mBtnLogin) {

                    @Override
                    public void onResp(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                        LogUtil.getLog().i("youmeng", "IdentifyingCodeActivity------->login----->onResp");
                        if (response.body() == null) {
                            return;
                        }
                        if (response.body().isOk()) {
                            SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FIRST_TIME);
                            preferencesUtil.save2Json(true);
                            TokenBean tokenBean = response.body().getData();
                            if (tokenBean.isDeactivating()) {
                                showLogoutAccountDialog();
                                return;
                            }else {
                                // 更新用户状态
                                UserUtil.saveUserStatus(response.body().getData().getUid(), CoreEnum.EUserType.DEFAULT);
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra(MainActivity.IS_LOGIN, true);
                                startActivity(intent);
                            }
                        } else if (response.body().getCode().longValue() == 10004) {//手机号未注册
                            ToastUtil.show(getContext(), response.body().getMsg());
                        } else if (response.body().getCode().longValue() == ErrorCode.ERROR_CODE_10006) {// 被封号
                            DialogUtils.instance().sealAccountDilaog(IdentifyingCodeActivity.this, response.body().getData());
                        } else {
                            ToastUtil.show(getContext(), response.body().getMsg());
                        }
                    }

                    @Override
                    public void onFail(Call<ReturnBean<TokenBean>> call, Throwable t) {
                        super.onFail(call, t);
                        LogUtil.getLog().i("youmeng", "IdentifyingCodeActivity------->login----->onFail");
                    }
                });

            }
        }).run();

    }


    private void taskGetSms(String phone) {
        userAction.smsCaptchaGet(phone, "login", new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    ToastUtil.show(context, "登录异常");
                    return;
                }
                if (!response.body().isOk()) {
                    CountDownUtil.cancelTimer();
                }
                ToastUtil.show(IdentifyingCodeActivity.this, response.body().getMsg());
            }
        });
    }

    //账号注销提示
    private void showLogoutAccountDialog() {
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                DialogCommon dialogConfirm = new DialogCommon(IdentifyingCodeActivity.this);
                dialogConfirm.setTitleAndSure(false, false)
                        .setLeft("取消注销并登录")
                        .setRight("取消")
                        .setContent("您的账号在申请注销过程中，如果您想取消注销申请，点击'取消注销并登录'", true)
                        .setListener(new DialogCommon.IDialogListener() {
                            @Override
                            public void onSure() {
                                cancelDeactivate();
                            }

                            @Override
                            public void onCancel() {

                            }
                        }).show();
            }
        });
    }

    //取消注销账号
    private void cancelDeactivate() {
        userAction.cancelDeactivate(new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(MainActivity.IS_LOGIN, true);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

}