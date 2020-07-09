package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jrmf360.tools.utils.ThreadUtil;
import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.utils.DialogUtils;
import com.yanlong.im.utils.PasswordTextWather;
import com.yanlong.im.utils.UserUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.dialog.DialogCommon2;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CallBack4Btn;
import net.cb.cb.library.utils.CheckUtil;
import net.cb.cb.library.utils.ClickFilter;
import net.cb.cb.library.utils.ErrorCode;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.RunUtils;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Response;

public class PasswordLoginActivity extends AppActivity implements View.OnClickListener {
    private HeadView mHeadView;
    private EditText mEtPhoneContent;
    private EditText mEtPasswordContent;
    private TextView mTvIdentifyingCode;
    private Button mBtnLogin;
    private UserAction userAction = new UserAction();
    private TextView tvForgetPassword;
    private int count = 0;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshBalance(EventFactory.ExitActivityEvent event) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_passworfd_login);
        initView();
        initEvent();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mEtPhoneContent = findViewById(R.id.et_phone_content);
        mEtPasswordContent = findViewById(R.id.et_password_content);
        mTvIdentifyingCode = findViewById(R.id.tv_identifying_code);
        mBtnLogin = findViewById(R.id.btn_login);
        mHeadView.getActionbar().setTxtRight("注册");
        tvForgetPassword = findViewById(R.id.tv_forget_password);

        String phone = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.PHONE).get4Json(String.class);
        String imid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.IM_ID).get4Json(String.class);
        if (StringUtil.isNotNull(imid)) {
            phone = imid;
        }
        mEtPhoneContent.setText(phone);
    }

    /**
     * 显示或隐藏密码
     *
     * @param view
     */
    public void showOrHidePassword(View view) {
        ImageView ivEye = (ImageView) view;
        int level = ivEye.getDrawable().getLevel();
        if (level == 0) {//隐藏转显示
            mEtPasswordContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivEye.setImageLevel(1);
            //光标定位到最后
            mEtPasswordContent.setSelection(mEtPasswordContent.getText().length());
        } else {//显示转隐藏
            mEtPasswordContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivEye.setImageLevel(0);
            //光标定位到最后
            mEtPasswordContent.setSelection(mEtPasswordContent.getText().length());
        }
    }

    private void initEvent() {
        mEtPasswordContent.addTextChangedListener(new PasswordTextWather(mEtPasswordContent, this));
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                Intent intent = new Intent(PasswordLoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        mTvIdentifyingCode.setOnClickListener(this);
        ClickFilter.onClick(tvForgetPassword, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPasswordIntent = new Intent(PasswordLoginActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
            }
        });

        ClickFilter.onClick(mBtnLogin, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputUtil.hideKeyboard(PasswordLoginActivity.this);
                if (!checkNetConnectStatus()) {
                    return;
                }
                login();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_identifying_code:
                Intent intent = new Intent(this, IdentifyingCodeActivity.class);
                startActivity(intent);
                finish();
                break;
        }

    }

    private void initDialog() {
        AlertYesNo alertYesNo = new AlertYesNo();
        alertYesNo.init(this, "找回密码", "密码错误,找回或重置密码?", "找回密码", "取消", new AlertYesNo.Event() {
            @Override
            public void onON() {

            }

            @Override
            public void onYes() {
                go(ForgotPasswordActivity.class);
            }
        });
        alertYesNo.show();
    }


    private void login() {
        final String phone = mEtPhoneContent.getText().toString();
        final String password = mEtPasswordContent.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(this, "请输入账号");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.show(this, "请输入密码");
            return;
        }

        new RunUtils(new RunUtils.Enent() {
            String devId;

            @Override
            public void onRun() {
                devId = UserAction.getDevId(getContext());
            }

            @Override
            public void onMain() {

                if (!CheckUtil.isMobileNO(phone)) {
                    userAction.login4Imid(phone, password, devId, new CallBack4Btn<ReturnBean<TokenBean>>(mBtnLogin) {
                        @Override
                        public void onResp(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                            LogUtil.getLog().i("youmeng", "PasswordLoginActivity------->login--imid-->onResp");
                            if (response.body() == null) {
                                ToastUtil.show(context, "登录异常");
                                return;
                            }
                            if (response.body().isOk()) {
                                SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FIRST_TIME);
                                preferencesUtil.save2Json(true);
                                TokenBean tokenBean = response.body().getData();
                                if (tokenBean.isDeactivating()) {
                                    showLogoutAccountDialog();
                                    return;
                                } else {
                                    // 更新用户状态
                                    UserUtil.saveUserStatus(response.body().getData().getUid(), CoreEnum.EUserType.DEFAULT);
                                    toMain();
                                }
                            }
                            if (response.body().getCode().longValue() == 10002) {
                                if (count == 0) {
                                    ToastUtil.show(context, "密码错误");
                                } else {
                                    initDialog();
                                }
                                count += 1;
                                return;
                            } else if (response.body().getCode().longValue() == 10088) {//非安全设备
                                showNewDeviceDialog(phone, response.body().getMsg());
                            } else if (response.body().getCode().longValue() == ErrorCode.ERROR_CODE_10006) {// 被封号
                                DialogUtils.instance().sealAccountDilaog(PasswordLoginActivity.this, response.body().getData());
                            } else {
                                ToastUtil.show(getContext(), response.body().getMsg());
                            }
                        }

                        @Override
                        public void onFail(Call<ReturnBean<TokenBean>> call, Throwable t) {
                            super.onFail(call, t);
                            LogUtil.getLog().i("youmeng", "PasswordLoginActivity------->login--phone-->onFail");
                        }
                    });

                } else {
                    userAction.login(phone, password, devId, new CallBack4Btn<ReturnBean<TokenBean>>(mBtnLogin) {
                        @Override
                        public void onResp(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                            LogUtil.getLog().i("youmeng", "PasswordLoginActivity------->login---->onResp");
                            if (response.body() == null) {
                                ToastUtil.show(context, "登录异常");
                                return;
                            }
                            if (response.body().isOk()) {

                                SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FIRST_TIME);
                                preferencesUtil.save2Json(true);
                                TokenBean tokenBean = response.body().getData();
                                if (tokenBean.isDeactivating()) {
                                    showLogoutAccountDialog();
                                    return;
                                } else {
                                    // 更新用户状态
                                    UserUtil.saveUserStatus(response.body().getData().getUid(), CoreEnum.EUserType.DEFAULT);
                                    toMain();
                                }
                            }
                            if (response.body().getCode().longValue() == 10002) {
                                if (count == 0) {
                                    ToastUtil.show(context, "密码错误");
                                } else {
                                    initDialog();
                                }
                                count += 1;
                            } else if (response.body().getCode().longValue() == 10088) {//非安全设备
                                showNewDeviceDialog(phone, response.body().getMsg());
                            } else if (response.body().getCode().longValue() == ErrorCode.ERROR_CODE_10006) {// 被封号
                                DialogUtils.instance().sealAccountDilaog(PasswordLoginActivity.this, response.body().getData());
                            } else {
                                ToastUtil.show(getContext(), response.body().getMsg());
                            }
                        }

                        @Override
                        public void onFail(Call<ReturnBean<TokenBean>> call, Throwable t) {
                            super.onFail(call, t);
                            LogUtil.getLog().i("youmeng", "PasswordLoginActivity------->login---->onFail");
                        }
                    });
                }
            }
        }).run();

    }

    private void goIdentifyCodeActivity(String phone) {
        Intent intent = new Intent(this, IdentifyingCodeActivity.class);
        intent.putExtra(IdentifyingCodeActivity.PHONE, phone);
        startActivity(intent);
    }

    public void showNewDeviceDialog(String phone, String content) {
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                DialogCommon2 dialogNewDevice = new DialogCommon2(PasswordLoginActivity.this);
                dialogNewDevice.setContent(content/*"您正在新设备上登录常信，为确保账号安全请使用验证码登录"*/, true)
                        .setButtonTxt("确定")
                        .hasTitle(false)
                        .setListener(new DialogCommon2.IDialogListener() {
                            @Override
                            public void onClick() {
                                goIdentifyCodeActivity(phone);
                            }
                        }).show();
            }
        });

    }

    public boolean checkNetConnectStatus() {
        boolean isOk = true;
        if (!NetUtil.isNetworkConnected()) {
            ToastUtil.show(this, "网络连接不可用，请稍后重试");
            isOk = false;
        }
        return isOk;
    }


    //账号注销提示
    private void showLogoutAccountDialog() {
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                DialogCommon dialogConfirm = new DialogCommon(PasswordLoginActivity.this);
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
                toMain();
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    private void toMain() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(MainActivity.IS_LOGIN, true);
        startActivity(intent);
    }


}
