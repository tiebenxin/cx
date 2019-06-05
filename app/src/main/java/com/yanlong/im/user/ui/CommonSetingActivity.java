package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

public class CommonSetingActivity extends AppActivity {
    public final static String TITLE = "title"; //标题栏
    public final static String HINT = "hint"; //提醒
    public final static String CONTENT = "content";//传回内容
    public final static String REMMARK = "remark";
    public final static String REMMARK1 = "remark1";
    public final static String SETING = "seting"; //设置输入栏的内容
    public final static String TYPE_LINE = "type"; //默认0 单行 1 多行
    public final static String SIZE = "SIZE";//限制字数长度
    public final static String INPUT_TYPE = "input_type";//输入类型

    private HeadView mHeadView;
    private TextView mTvTitle;
    private EditText mEdContent;
    private TextView mTvContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int type = intent.getIntExtra(TYPE_LINE,0);
        if(type == 0){
            setContentView(R.layout.activity_common_seting);
        }else{
            setContentView(R.layout.activity_common_seting_multi);
        }
        initView();
        initEvent();

    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mTvTitle = findViewById(R.id.tv_title);
        mEdContent = findViewById(R.id.ed_content);
        mTvContent = findViewById(R.id.tv_content);
        mHeadView.getActionbar().setTxtRight("完成");
        Intent intent = getIntent();

        String title = intent.getStringExtra(TITLE);
        if (!TextUtils.isEmpty(title)) {
            mHeadView.getActionbar().setTitle(title);
        }

        String hint = intent.getStringExtra(HINT);
        if (!TextUtils.isEmpty(hint)) {
            mEdContent.setHint(hint);
        }

        String remark = intent.getStringExtra(REMMARK);
        if (!TextUtils.isEmpty(remark)) {
            mTvTitle.setText(remark);
        }

        String remark1 = intent.getStringExtra(REMMARK1);
        if (!TextUtils.isEmpty(remark1)) {
            mTvContent.setVisibility(View.VISIBLE);
            mTvContent.setText(remark1);
        } else {
            mTvContent.setVisibility(View.GONE);
            mTvContent.setText(remark1);
        }

        String seting = intent.getStringExtra(SETING);
        if (!TextUtils.isEmpty(seting)) {
            mEdContent.setText(seting);
        }

        int size = intent.getIntExtra(SIZE,70);
        mEdContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(size)});

    }


    private void initEvent() {
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                String content = mEdContent.getText().toString();
                Intent intent = new Intent();
                intent.putExtra(CONTENT,content);
                setResult(RESULT_OK,intent);
                onBackPressed();
            }
        });
    }


}
