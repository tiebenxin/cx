package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yanlong.im.R;

import net.cb.cb.library.view.AppActivity;

public class MyselfInfoActivity extends AppActivity implements View.OnClickListener {

    private SimpleDraweeView mImgHead;
    private LinearLayout mViewBlacklist;
    private TextView mTvPhone;
    private LinearLayout mViewNickname;
    private TextView mTvNickname;
    private LinearLayout mViewProductNumber;
    private TextView mTvProductNumber;
    private LinearLayout mViewSex;
    private TextView mTvSex;
    private LinearLayout mViewIdentity;
    private TextView mTvIdentity;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_info);
        initView();
        initEvent();
    }

    private void initView(){
        mImgHead =  findViewById(R.id.img_head);
        mViewBlacklist =  findViewById(R.id.view_blacklist);
        mTvPhone =  findViewById(R.id.tv_phone);
        mViewNickname =  findViewById(R.id.view_nickname);
        mTvNickname =  findViewById(R.id.tv_nickname);
        mViewProductNumber =  findViewById(R.id.view_product_number);
        mTvProductNumber =  findViewById(R.id.tv_product_number);
        mViewSex =  findViewById(R.id.view_sex);
        mTvSex =  findViewById(R.id.tv_sex);
        mViewIdentity =  findViewById(R.id.view_identity);
        mTvIdentity =  findViewById(R.id.tv_identity);
    }


    private void initEvent(){
        mViewBlacklist.setOnClickListener(this);
        mViewNickname.setOnClickListener(this);
        mViewProductNumber.setOnClickListener(this);
        mViewSex.setOnClickListener(this);
        mViewIdentity.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.view_blacklist:

                break;
            case R.id.view_nickname:

                break;
            case R.id.view_product_number:

                break;
            case R.id.view_sex:

                break;
            case R.id.view_identity:

                break;
        }
    }
}






















