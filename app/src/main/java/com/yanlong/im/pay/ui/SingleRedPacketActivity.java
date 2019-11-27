package com.yanlong.im.pay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.utils.NumRangeInputFilter;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.PopupSelectView;

public class SingleRedPacketActivity extends AppActivity {

    private ActionbarView mActionBar;
    private EditText mEdMoney;
    private EditText mEdContent;
    private TextView mTvMoney;
    private Button mBtnCommit;
    private String [] strings = {"红包记录","取消"};
    private PopupSelectView popupSelectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_red_packet);
        initView();
        initEvent();
    }

    private void initView() {
        mActionBar = findViewById(R.id.action_bar);
        mEdMoney = findViewById(R.id.ed_money);
        mEdContent = findViewById(R.id.ed_content);
        mTvMoney = findViewById(R.id.tv_money);
        mBtnCommit = findViewById(R.id.btn_commit);

        mBtnCommit.setEnabled(false);
        mActionBar.setTxtLeft("取消");
        mActionBar.getBtnLeft().setVisibility(View.GONE);
        mActionBar.getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        mActionBar.getBtnRight().setVisibility(View.VISIBLE);
        mEdMoney.setFilters(new InputFilter[]{new NumRangeInputFilter(this)});
    }

    private void initEvent(){
        mActionBar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                initPopup();
            }
        });

        mEdMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                 String string = s.toString();
                 if(!TextUtils.isEmpty(string)){
                     mBtnCommit.setEnabled(true);
                     mTvMoney.setText(string);
                 }else{
                     mBtnCommit.setEnabled(false);
                     mTvMoney.setText("0.00");
                 }

            }
        });

        mBtnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(SingleRedPacketActivity.this,"发红包");
            }
        });

    }

    private void initPopup() {
        hideKeyboard();
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(mActionBar, Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 0:
                        Intent intent = new Intent(SingleRedPacketActivity.this,RedpacketRecordActivity.class);
                        startActivity(intent);
                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }

}
