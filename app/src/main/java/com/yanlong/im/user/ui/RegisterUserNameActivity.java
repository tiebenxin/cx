package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @创建人 shenxin
 * @创建时间 2019/8/6 0006 14:03
 */
public class RegisterUserNameActivity extends AppActivity {

    private HeadView headView;
    private EditText etSetingUserName;
    private Button btnCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user_name);
        initView();
        initEvent();
    }


    private void initView() {
        headView =  findViewById(R.id.headView);
        etSetingUserName =  findViewById(R.id.et_seting_user_name);
        btnCommit =  findViewById(R.id.btn_commit);
    }

    private void initEvent(){
        headView.getActionbar().getBtnLeft().setVisibility(View.GONE);
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserName();
            }
        });
    }


    private void setUserName(){
        String userName = etSetingUserName.getText().toString();
        userName = userName.trim();
        if (!TextUtils.isEmpty(userName)){
            //截取前两位判断开头是否为emoji
            if(userName.length()>=2){
                String emoji = userName.substring(0,2);
                if(StringUtil.ifContainEmoji(emoji)){
                    userName = " "+userName;
                }
            }
        }else {
            ToastUtil.show(RegisterUserNameActivity.this, "昵称不能为空或使用空字符");
            return;
        }
        taskUserInfoSet(userName);
    }


    private void taskUserInfoSet(String nickname) {
       new UserAction().myInfoSet(null, null, nickname, null, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                if(response.body().isOk()){
                    ToastUtil.show(context,"注册成功");
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


}
