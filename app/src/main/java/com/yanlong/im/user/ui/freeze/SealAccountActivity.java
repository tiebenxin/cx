package com.yanlong.im.user.ui.freeze;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.nim_lib.ui.BaseBindActivity;
import com.luck.picture.lib.tools.Constant;
import com.luck.picture.lib.tools.DoubleUtils;
import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivitySealAccountBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.ui.LoginActivity;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.manager.Constants;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.DeviceUtils;
import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.RunUtils;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.ActionbarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-7-6
 * @updateAuthor
 * @updateDate
 * @description 违规说明
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
@Route(path = SealAccountActivity.path)
public class SealAccountActivity extends BaseBindActivity<ActivitySealAccountBinding> implements View.OnClickListener {
    public static final String path = "/SealAccount/SealAccountActivity";

    @Autowired(name = Constants.STATUS)
    boolean status;
    private boolean isClear = true;// 是否清除Token

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshBalance(EventFactory.UpdateAppealStatusEvent event) {
        this.status = event.status;
    }

    @Override
    protected int setView() {
        return R.layout.activity_seal_account;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ARouter.getInstance().inject(this);

    }

    @Override
    protected void initEvent() {
        bindingView.btnTempLogin.setOnClickListener(this);
        bindingView.btnUnsealing.setOnClickListener(this);
        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isClear) {
            cleanInfo();
        }
    }

    /***
     * 清理信息
     */
    public void cleanInfo() {
        new SharedPreferencesUtil(SharedPreferencesUtil.SPName.TOKEN).clear();
        LogUtil.getLog().i("Token", "清除token");
    }

    @Override
    public void onClick(View v) {
        if (DoubleUtils.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_temp_login:// 临时登录
                tempLogin();
                break;
            case R.id.btn_unsealing:// 账号申诉
                if (status) {
                    IntentUtil.gotoActivity(this, AppealIngActivity.class);
                } else {
                    IntentUtil.gotoActivity(this, AppealAccountActivity.class);
                }
                break;
        }
    }

    /**
     * 临时登录
     */
    private void tempLogin() {
        new RunUtils(new RunUtils.Enent() {
            String devId;

            @Override
            public void onRun() {
                devId = UserAction.getDevId(getContext());
            }

            @Override
            public void onMain() {
                WeakHashMap<String, Object> params = new WeakHashMap<>();
                params.put("deviceDetail", DeviceUtils.getIMEI(AppConfig.getContext()));// 终端唯一标识
                params.put("deviceName", DeviceUtils.getDeviceName());// 终端名称
                params.put("devid", devId);// 设备ID
                params.put("installChannel", StringUtil.getChannelName(AppConfig.getContext()));
                params.put("phoneModel", DeviceUtils.getPhoneModel());
                params.put("platform", Constants.ANDROID);// 标识 android ios
                new UserAction().tempLogin(params, new CallBack<ReturnBean<TokenBean>>() {
                    @Override
                    public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                        super.onResponse(call, response);
                        if (response != null && response.body().isOk()) {
                            // 关闭登录界面
                            EventBus.getDefault().post(new EventFactory.ExitActivityEvent());
                            toMain();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnBean<TokenBean>> call, Throwable t) {
                        super.onFailure(call, t);
                    }
                });
            }
        }).run();
    }

    private void toMain() {
        isClear = false;
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(MainActivity.IS_LOGIN, true);
        startActivity(intent);
        finish();
    }
}