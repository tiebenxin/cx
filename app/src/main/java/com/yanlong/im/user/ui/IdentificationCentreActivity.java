package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

public class IdentificationCentreActivity extends AppActivity implements View.OnClickListener {

    private HeadView mHeadView;
    private TextView mTvName;
    private TextView mTvIdentityNumber;
    private LinearLayout mLlProfession;
    private TextView mTvProfession;
    private LinearLayout mLlIdentityCard;
    private TextView mTvIdentityCard;
    private LinearLayout mLlPeriodValidity;
    private TextView mTvPeriodValidity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification_centre);
        initView();
        initEvent();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mTvName = findViewById(R.id.tv_name);
        mTvIdentityNumber = findViewById(R.id.tv_identity_number);
        mLlProfession = findViewById(R.id.ll_profession);
        mTvProfession = findViewById(R.id.tv_profession);
        mLlIdentityCard = findViewById(R.id.ll_identity_card);
        mTvIdentityCard = findViewById(R.id.tv_identity_card);
        mLlPeriodValidity = findViewById(R.id.ll_period_validity);
        mTvPeriodValidity = findViewById(R.id.tv_period_validity);
    }

    private void initEvent(){
        mLlProfession.setOnClickListener(this);
        mLlIdentityCard.setOnClickListener(this);
        mLlPeriodValidity.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    private String hideName(String name){
        String string = "";
        if(!TextUtils.isEmpty(name)){

        }



        return string;
    }

}


























