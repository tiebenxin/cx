package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yanlong.im.R;

import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.WebPageActivity;

public class SelectLoginActivity extends AppActivity implements View.OnClickListener {

    private Button mBtnLogin;
    private Button mBtnRegister;
    private ImageView mIvWechat;
    private TextView mTvMattersNeedAttention;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_select_login);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue_title));

        initView();
        initEvent();
    }


    private void initView(){
        mBtnLogin =  findViewById(R.id.btn_login);
        mBtnRegister =  findViewById(R.id.btn_register);
        mIvWechat =  findViewById(R.id.iv_wechat);

        mTvMattersNeedAttention = findViewById(R.id.tv_matters_need_attention);
        initTvMNA();
    }


    private void initEvent(){
        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mIvWechat.setOnClickListener(this);

        mTvMattersNeedAttention.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                Intent loginIntent = new Intent(this,PasswordLoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.btn_register:
                Intent registerIntent = new Intent(this,RegisterActivity.class);
                startActivity(registerIntent);
                break;
            case R.id.iv_wechat:

                break;
        }
    }


    private void initTvMNA() {
        final SpannableStringBuilder style = new SpannableStringBuilder();
//        style.append("点击\"注册\"即表示已阅读并同意《用户使用协议》和《隐私权政策》");
        style.append("《用户使用协议》 《隐私权政策》");
        ClickableSpan clickProtocol = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(SelectLoginActivity.this, WebPageActivity.class);
                intent.putExtra(WebPageActivity.AGM_URL,"https://changxin.zhixun6.com/yhxy.html");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor( R.color.blue_600));
                ds.setUnderlineText(false);
            }
        };
        style.setSpan(clickProtocol, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickPolicy = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(SelectLoginActivity.this,WebPageActivity.class);
                intent.putExtra(WebPageActivity.AGM_URL,"https://changxin.zhixun6.com/yszc.html");
                startActivity(intent);

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor( R.color.blue_600));
                ds.setUnderlineText(false);
            }
        };
        style.setSpan(clickPolicy, 9, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTvMattersNeedAttention.setText(style);
        mTvMattersNeedAttention.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
