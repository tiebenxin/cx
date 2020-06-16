package com.yanlong.im.user.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hm.cxpay.net.HttpChannel;
import com.jrmf360.tools.utils.ThreadUtil;
import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.NewVersionBean;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.bean.VersionBean;
import com.yanlong.im.user.ui.baned.BanedAccountActivity;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.PasswordTextWather;
import com.yanlong.im.utils.update.UpdateManage;

import net.cb.cb.library.BuildConfig;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.constant.AppHostUtil;
import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.dialog.DialogCommon2;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CallBack4Btn;
import net.cb.cb.library.utils.CheckUtil;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.RunUtils;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SoftKeyBoardListener;
import net.cb.cb.library.utils.SpUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.VersionUtil;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.PopupSelectView;

import retrofit2.Call;
import retrofit2.Response;

import static com.yanlong.im.user.ui.IdentifyingCodeActivity.PHONE;

public class LoginActivity extends AppActivity implements View.OnClickListener {

    private ImageView mImgHead;
    private TextView mTvPhoneNumber;
    private EditText mEtPasswordContent;
    private TextView mTvIdentifyingCode;
    private Button mBtnLogin;
    private TextView mTvForgetPassword;
    private TextView mTvMore;
    private PopupSelectView popupSelectView;
    private String[] strings = {"切换账号", "注册", "取消"};
    private String phone;
    private int count = 0;
    //记录软键盘高度
    private String KEY_BOARD = "keyboard_setting";
    //软键盘高度
    private int mKeyboardHeight = 0;
    private LinearLayout llIP;
    private TextView tvIP, tvIPName;
    private Spinner spinner;
    private long[] mHits;
    private boolean isShowIPSelector;//是否显示ip选择器
    private UserAction userAction = new UserAction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        taskNewVersion();
    }

    private void initView() {
        mImgHead = findViewById(R.id.img_head);
        mTvPhoneNumber = findViewById(R.id.tv_phone_number);
        mEtPasswordContent = findViewById(R.id.et_password_content);
        mTvIdentifyingCode = findViewById(R.id.tv_identifying_code);
        mBtnLogin = findViewById(R.id.btn_login);
        mTvForgetPassword = findViewById(R.id.tv_forget_password);
        mTvMore = findViewById(R.id.tv_more);

        llIP = findViewById(R.id.ll_ip);
        tvIP = findViewById(R.id.tv_ip);
        tvIPName = findViewById(R.id.tv_ip_name);
        spinner = findViewById(R.id.spinner);
        if (BuildConfig.BUILD_TYPE.equals("debug") || BuildConfig.BUILD_TYPE.equals("pre")) {
            initSpinner();
            SpUtil spUtil = SpUtil.getSpUtil();
            isShowIPSelector = spUtil.getSPValue("isConfigIP", false);
            showIPUI();
        } else {
            llIP.setVisibility(View.GONE);
        }
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

    private void initSpinner() {
        final String[] spinnerItems = {"测试服", "预发布服", "生产服"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_text, spinnerItems);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpUtil spUtil = SpUtil.getSpUtil();
                int type = position + 1;
                spUtil.putSPValue("ipType", type);
                switchService(type);
                showIPUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void switchService(@ChatEnum.EServiceType int type) {
        String host;
        switch (type) {
            case ChatEnum.EServiceType.DEBUG:
                host = BuildConfig.HOST_DEV;
                break;
            case ChatEnum.EServiceType.BETA:
                host = BuildConfig.HOST_PRE;
                break;
            case ChatEnum.EServiceType.RELEASE:
                host = BuildConfig.HOST_RELEASE;
                break;
            default:
                host = BuildConfig.API_HOST;
                break;
        }
        AppHostUtil.setHostUrl(host);
        HttpChannel.getInstance().resetHost();
        userAction = new UserAction();
    }

    private void showIPUI() {
        llIP.setVisibility(isShowIPSelector ? View.VISIBLE : View.GONE);
        if (isShowIPSelector) {
            tvIPName.setText(getServiceName());
            tvIP.setText(AppHostUtil.getTcpHost());
        }
    }


    private void initEvent() {
        mTvIdentifyingCode.setOnClickListener(this);
        mTvForgetPassword.setOnClickListener(this);
        mTvMore.setOnClickListener(this);
        mEtPasswordContent.addTextChangedListener(new PasswordTextWather(mEtPasswordContent, this));
        /*ClickFilter.onClick(mBtnLogin, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });*/
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputUtil.hideKeyboard(LoginActivity.this);
//                if (!checkNetConnectStatus()) {
//                    return;
//                }
                login();
            }
        });
        //处理键盘
        SoftKeyBoardListener kbLinst = new SoftKeyBoardListener(this);
        kbLinst.setOnSoftKeyBoardChangeListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int h) {
                int maxHeigh = getResources().getDimensionPixelSize(R.dimen.chat_fuction_panel_max_height);
                //每次保存软键盘的高度
                if (mKeyboardHeight != h && h <= maxHeigh) {
                    SharedPreferences sharedPreferences = getSharedPreferences(KEY_BOARD, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putInt(KEY_BOARD, h).apply();
                    mKeyboardHeight = h;
                }
            }

            @Override
            public void keyBoardHide(int h) {
            }
        });

        //点击头像五次
        if (BuildConfig.BUILD_TYPE.equals("debug") || BuildConfig.BUILD_TYPE.equals("pre")) {
            mImgHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDisplaySettingButton();
                }
            });
        }
    }

    private void initData() {
        phone = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.PHONE).get4Json(String.class);
        String imageHead = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.IMAGE_HEAD).get4Json(String.class);

        String imid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.IM_ID).get4Json(String.class);
        if (StringUtil.isNotNull(imid)) {
            phone = imid;
        }
        mTvPhoneNumber.setText(phone);
        Glide.with(this).load(imageHead).apply(GlideOptionsUtil.headImageOptions()).into(mImgHead);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_identifying_code:
                goIdentifyCodeActivity();
                break;
            case R.id.tv_forget_password:
                Intent forgotPasswordIntent = new Intent(this, ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
                break;
            case R.id.tv_more:
                initPopup();
                break;
        }
    }

    private void goIdentifyCodeActivity() {
        Intent intent = new Intent(this, IdentifyingCodeActivity.class);
        intent.putExtra(PHONE, phone);
        startActivity(intent);
    }

    private void initPopup() {
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(mImgHead, Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 0:
                        go(PasswordLoginActivity.class);
                        break;
                    case 1:
                        go(RegisterActivity.class);
                        break;
                }
                popupSelectView.dismiss();
            }
        });
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
        // mBtnLogin.setEnabled(false);
        final String password = mEtPasswordContent.getText().toString();
        final String phone = mTvPhoneNumber.getText().toString();


        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(this, "请输入账号");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtil.show(this, "请输入密码");
            return;
        }

//        if (!CheckUtil.isMobileNO(phone)) {
//            ToastUtil.show(this, "手机号格式不正确");
//            return;
//        }

        LogUtil.getLog().i("youmeng", "LoginActivity------->getDevId");
        new RunUtils(new RunUtils.Enent() {
            String devId;

            @Override
            public void onRun() {
                devId = UserAction.getDevId(getContext());
            }

            @Override
            public void onMain() {
                if (CheckUtil.isMobileNO(phone)) {
                    userAction.login(phone, password, devId, new CallBack4Btn<ReturnBean<TokenBean>>(mBtnLogin) {
                        @Override
                        public void onResp(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                            LogUtil.getLog().i("youmeng", "LoginActivity------->login----phone---->onResp");
                            if (response.body() == null) {
                                ToastUtil.show(context, "登录异常");
                                return;
                            }
                            if (response.body().isOk()) {
                                TokenBean tokenBean = response.body().getData();
                                if (tokenBean.isDeactivating()) {
                                    showLogoutAccountDialog();
                                    return;
                                } else {
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
                            } else if (response.body().getCode().longValue() == 10004) {//账号未注册
                                ToastUtil.show(getContext(), response.body().getMsg());
                            } else if (response.body().getCode().longValue() == 10088) {//非安全设备
                                showNewDeviceDialog(response.body().getMsg());
                            } else {
                                ToastUtil.show(getContext(), response.body().getMsg());
                            }
                        }

                        @Override
                        public void onFail(Call<ReturnBean<TokenBean>> call, Throwable t) {
                            super.onFail(call, t);
                            LogUtil.getLog().i("youmeng", "LoginActivity------->login-------->onFail");
                        }
                    });
                } else {
                    userAction.login4Imid(phone, password, devId, new CallBack4Btn<ReturnBean<TokenBean>>(mBtnLogin) {

                        @Override
                        public void onResp(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                            LogUtil.getLog().i("youmeng", "PasswordLoginActivity------->login--imid-->onResp");
                            if (response.body() == null) {
                                ToastUtil.show(context, "登录异常");
                                return;
                            }
                            if (response.body().isOk()) {
                                TokenBean tokenBean = response.body().getData();
                                if (tokenBean.isDeactivating()) {
                                    showLogoutAccountDialog();
                                    return;
                                } else {
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
                            } else if (response.body().getCode().longValue() == 10004) {//账号不存在
                                ToastUtil.show(getContext(), response.body().getMsg());
                            } else if (response.body().getCode().longValue() == 10088) {//非安全设备
                                showNewDeviceDialog(response.body().getMsg());
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

    private String getServiceName() {
        SpUtil spUtil = SpUtil.getSpUtil();
        int type = spUtil.getSPValue("ipType", ChatEnum.EServiceType.DEFAULT);
        if (type == ChatEnum.EServiceType.DEFAULT) {
            switch (BuildConfig.BUILD_TYPE) {
                case "debug":
                    return "测试服";
                case "pre":
                    return "预发布服";
                case "release":
                    return "生产服";
            }
        } else {
            switchService(type);
            switch (type) {
                case ChatEnum.EServiceType.DEBUG:
                    return "测试服";
                case ChatEnum.EServiceType.BETA:
                    return "预发布服";
                case ChatEnum.EServiceType.RELEASE:
                    return "生产服";
            }
        }
        return "";
    }

    public void onDisplaySettingButton() {
        if (mHits == null) {
            mHits = new long[5];
        }
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);//把从第二位至最后一位之间的数字复制到第一位至倒数第一位
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();//记录一个时间
        if (SystemClock.uptimeMillis() - mHits[0] <= 1000) {//一秒内连续点击。
            mHits = null;    //这里说明一下，我们在进来以后需要还原状态，否则如果点击过快，第六次，第七次 都会不断进来触发该效果。重新开始计数即可
            isShowIPSelector = !isShowIPSelector;
            showIPUI();
            SpUtil spUtil = SpUtil.getSpUtil();
            spUtil.putSPValue("isConfigIP", isShowIPSelector);
        }
    }

    public void showNewDeviceDialog(String content) {
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                DialogCommon2 dialogNewDevice = new DialogCommon2(LoginActivity.this);
                dialogNewDevice.setContent(content/*"您正在新设备上登录常信，为确保账号安全请使用验证码登录"*/, true)
                        .setButtonTxt("确定")
                        .hasTitle(false)
                        .setListener(new DialogCommon2.IDialogListener() {
                            @Override
                            public void onClick() {
                                goIdentifyCodeActivity();
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

    /**
     * 发请求---判断是否需要更新
     */
    private void taskNewVersion() {
        userAction.getNewVersion(StringUtil.getChannelName(context), new CallBack<ReturnBean<NewVersionBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<NewVersionBean>> call, Response<ReturnBean<NewVersionBean>> response) {
                if (response.body() == null || response.body().getData() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    NewVersionBean bean = response.body().getData();
                    UpdateManage updateManage = new UpdateManage(context, LoginActivity.this);
                    //强制更新
                    if (bean.getForceUpdate() != 0) {
                        //有最低不需要强制升级版本
                        if (!TextUtils.isEmpty(bean.getMinEscapeVersion()) && VersionUtil.isLowerVersion(context, bean.getMinEscapeVersion())) {
                            updateManage.uploadApp(bean.getVersion(), bean.getContent(), bean.getUrl(), true, true);
                        } else {
                            updateManage.uploadApp(bean.getVersion(), bean.getContent(), bean.getUrl(), false, true);
                        }
                    } else {
                        //缓存最新版本
                        SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.NEW_VESRSION);
                        VersionBean versionBean = new VersionBean();
                        versionBean.setVersion(bean.getVersion());
                        preferencesUtil.save2Json(versionBean);
                        //非强制更新（新增一层判断：如果是大版本，则需要直接改为强制更新）
                        if (VersionUtil.isBigVersion(context, bean.getVersion()) || (!TextUtils.isEmpty(bean.getMinEscapeVersion()) && VersionUtil.isLowerVersion(context, bean.getMinEscapeVersion()))) {
                            updateManage.uploadApp(bean.getVersion(), bean.getContent(), bean.getUrl(), true, true);
                        } else {
                            updateManage.uploadApp(bean.getVersion(), bean.getContent(), bean.getUrl(), false, true);
                            //如有新版本，首页底部提示红点
                        }
                    }
                }
            }
        });
    }

    //账号注销提示
    private void showLogoutAccountDialog() {
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                DialogCommon dialogConfirm = new DialogCommon(LoginActivity.this);
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


    //账号封禁提示
    private void showBanedAccountDialog() {
        ThreadUtil.getInstance().runMainThread(new Runnable() {
            @Override
            public void run() {
                DialogCommon dialogConfirm = new DialogCommon(LoginActivity.this);
                dialogConfirm.setTitleAndSure(false, true)
                        .setLeft("取消")
                        .setRight("确定")
                        .setContent("因该账号被投诉并确有违规行为，已被永久限制登录。点击确定提取账号内财产。", true)
                        .setListener(new DialogCommon.IDialogListener() {
                            @Override
                            public void onSure() {
                                go(BanedAccountActivity.class);
                            }

                            @Override
                            public void onCancel() {

                            }
                        }).show();
            }
        });
    }
}

