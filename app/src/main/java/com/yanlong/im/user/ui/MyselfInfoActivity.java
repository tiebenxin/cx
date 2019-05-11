package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yanlong.im.R;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

public class MyselfInfoActivity extends AppActivity implements View.OnClickListener {
    private static final int NICENAME = 1000;
    private static final int PRODUCT = 2000;
    private static final int SEX = 3000;

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
    private LinearLayout mViewHead;
    private HeadView mHeadView;
    private UserInfo userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_info);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        mImgHead = findViewById(R.id.img_head);
        mViewBlacklist = findViewById(R.id.view_blacklist);
        mViewHead = findViewById(R.id.view_head);
        mTvPhone = findViewById(R.id.tv_phone);
        mViewNickname = findViewById(R.id.view_nickname);
        mTvNickname = findViewById(R.id.tv_nickname);
        mViewProductNumber = findViewById(R.id.view_product_number);
        mTvProductNumber = findViewById(R.id.tv_product_number);
        mViewSex = findViewById(R.id.view_sex);
        mTvSex = findViewById(R.id.tv_sex);
        mViewIdentity = findViewById(R.id.view_identity);
        mTvIdentity = findViewById(R.id.tv_identity);
        mHeadView =  findViewById(R.id.headView);
    }


    private void initEvent() {
        mViewBlacklist.setOnClickListener(this);
        mViewNickname.setOnClickListener(this);
        mViewProductNumber.setOnClickListener(this);
        mViewSex.setOnClickListener(this);
        mViewIdentity.setOnClickListener(this);
        mViewHead.setOnClickListener(this);
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
    }

    private void initData(){
        userInfo = new UserInfo();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_blacklist:

                break;
            case R.id.view_nickname:
                Intent nicknameIntent = new Intent(MyselfInfoActivity.this, CommonSetingActivity.class);
                nicknameIntent.putExtra(CommonSetingActivity.TITLE, "昵称");
                nicknameIntent.putExtra(CommonSetingActivity.REMMARK, "设置昵称");
                nicknameIntent.putExtra(CommonSetingActivity.HINT, "昵称");
                startActivityForResult(nicknameIntent, NICENAME);
                break;
            case R.id.view_product_number:
                Intent productIntent = new Intent(MyselfInfoActivity.this, CommonSetingActivity.class);
                productIntent.putExtra(CommonSetingActivity.TITLE, "夸夸号");
                productIntent.putExtra(CommonSetingActivity.REMMARK, "夸夸号");
                productIntent.putExtra(CommonSetingActivity.HINT, "可以使用5~15个字符 数字(必须以字母开头)");
                productIntent.putExtra(CommonSetingActivity.REMMARK1, "夸夸号只能设置一次");
                startActivityForResult(productIntent, PRODUCT);
                break;
            case R.id.view_sex:
                Intent sexIntent = new Intent(MyselfInfoActivity.this,SelectSexActivity.class);
                startActivityForResult(sexIntent,SEX);
                break;
            case R.id.view_identity:
                Intent identityIntent = new Intent(MyselfInfoActivity.this, IdentificationCentreActivity.class);
                startActivity(identityIntent);
                break;
            case R.id.view_head:
                Intent headIntent = new Intent(MyselfInfoActivity.this, ImageHeadActivity.class);
                startActivity(headIntent);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String content = data.getStringExtra(CommonSetingActivity.CONTENT);
            switch (requestCode) {
                case NICENAME:
                    mTvNickname.setText(content);
                    break;
                case SEX:
                    if(!TextUtils.isEmpty(content)){
                        mTvSex.setText(content);
                    }
                    break;
            }
        }
    }






}






















