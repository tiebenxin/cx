package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.PasswordTextWather;

import net.cb.cb.library.bean.EventLoginOut;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.ClearEditText;
import net.cb.cb.library.view.HeadView;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @创建人 shenxin
 * @创建时间 2019/8/6 0006 15:25
 */
public class SetingPasswordActitity extends AppActivity {
    public static final String TYPE = "type"; // 1.弹出提示框退出 0.正常退出
    private HeadView headView;
    private ClearEditText edPassword;
    private ClearEditText edVerifyPassword;
    private Button btnCommit;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seting_password);
        initView();
        initEvent();
    }


    private void initView() {
        headView = findViewById(R.id.headView);
        edPassword = findViewById(R.id.ed_password);
        edVerifyPassword = findViewById(R.id.ed_verify_password);
        btnCommit = findViewById(R.id.btn_commit);
        edPassword.addTextChangedListener(new PasswordTextWather(edPassword,context));
        edVerifyPassword.addTextChangedListener(new PasswordTextWather(edVerifyPassword,context));
        type = getIntent().getIntExtra(TYPE,0);
    }

    private void initEvent() {
        headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭软键盘
                InputUtil.hideKeyboard(SetingPasswordActitity.this);
                commit();
            }
        });
    }
    /**
     * 显示或隐藏密码
     *
     * @param view
     */
    public void showOrHidePassword(View view) {
        ImageView ivEye = (ImageView) view;
        EditText editText = null;
        switch (view.getId()) {
            case R.id.iv_password_show_or_hide_password:
                editText = edPassword;
                break;
            case R.id.iv_verify_password_or_hide_password:
                editText = edVerifyPassword;
                break;
        }
        if (editText != null) {
            int level = ivEye.getDrawable().getLevel();
            if (level == 0) {//隐藏转显示
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivEye.setImageLevel(1);
                //光标定位到最后
                editText.setSelection(editText.getText().length());
            } else {//显示转隐藏
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivEye.setImageLevel(0);
                //光标定位到最后
                editText.setSelection(editText.getText().length());
            }
        }
    }
    private void commit() {

        String password = edPassword.getText().toString();
        String nextPassword = edVerifyPassword.getText().toString();
        if(TextUtils.isEmpty(password)){
            ToastUtil.show(context,"请填写密码");
            return;
        }
        if(password.length() < 6){
            ToastUtil.show(context,"密码不能少于六位");
            return;
        }
        if(TextUtils.isEmpty(nextPassword)){
            ToastUtil.show(context,"请填写验证密码");
            return;
        }
        if(!password.equals(nextPassword)){
            ToastUtil.show(context,"两次密码不一致");
            return;
        }
        taskInitPassword(password);
    }

    private void taskInitPassword(String password){
        new UserAction().initUserPassword(password, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if(response.body() == null){
                    return;
                }
                ToastUtil.show(context,response.body().getMsg());
                if(response.body().isOk()){
                    if(type == 0){
                        taskExit();
                        finish();
                    }else{
                        AlertYesNo alertYesNo = new AlertYesNo();
                        alertYesNo.init(SetingPasswordActitity.this, "设置密码", "设置密码成功", "确定退出", "取消", new AlertYesNo.Event() {
                            @Override
                            public void onON() {
                                finish();
                            }

                            @Override
                            public void onYes() {
                                taskExit();
                                finish();
                            }
                        });
                        alertYesNo.show();
                    }
                }
            }
        });
    }


    /***
     * 退出
     */
    private void taskExit() {
        finish();
        new UserAction().loginOut();
        EventBus.getDefault().post(new EventLoginOut(1));
    }


}
