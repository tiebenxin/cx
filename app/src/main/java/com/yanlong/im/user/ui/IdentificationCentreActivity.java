package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import static com.yanlong.im.user.ui.SelectProfessionActivity.SELECT_PROFEESION;

public class IdentificationCentreActivity extends AppActivity implements View.OnClickListener {
    private static final int PROFESSION = 1000;

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
        mTvName.setText(hideName("啦啦啦啦"));
        mTvIdentityNumber.setText(hideIdentity("101011010101010101010"));
    }

    private void initEvent() {
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

        mLlProfession.setOnClickListener(this);
        mLlIdentityCard.setOnClickListener(this);
        mLlPeriodValidity.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_profession:
                Intent professionIntent = new Intent(this,SelectProfessionActivity.class);
                startActivityForResult(professionIntent,PROFESSION);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PROFESSION:
                    String string = data.getStringExtra(SELECT_PROFEESION);
                    if(!TextUtils.isEmpty(string)){
                        mTvProfession.setText(string);
                    }
                    break;

            }
        }

    }

    private String hideName(String name) {
        String string = "";
        if (!TextUtils.isEmpty(name)) {
            int size = name.length();
            if (size > 1) {
                String replace = name.substring(0, size -1);
                int replaceSize = replace.length();
                String starString = "";
                for (int i = 0; i < replaceSize; i++) {
                    starString += "*";
                }
                String newString = name.replace(replace, starString);
                return newString;
            } else {
                return name;
            }
        }
        return string;
    }


    private String hideIdentity(String number) {
        String string = "";
        if (!TextUtils.isEmpty(number)) {
            int size = number.length();
            if (size > 1) {
                String replace = number.substring(1, size -1);
                int replaceSize = replace.length();
                String starString = "";
                for (int i = 0; i < replaceSize; i++) {
                    starString += "*";
                }
                String newString = number.replace(replace, starString);
                return newString;
            } else {
                return number;
            }
        }
        return string;
    }


}


























