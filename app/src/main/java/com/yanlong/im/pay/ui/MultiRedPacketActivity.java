package com.yanlong.im.pay.ui;

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

public class MultiRedPacketActivity extends AppActivity implements View.OnClickListener {

    private ActionbarView mActionBar;
    private TextView mTvMoneyTitle;
    private EditText mEdMoney;
    private TextView mTvRedPacketTypeTitle;
    private TextView mTvRedPacketType;
    private EditText mEdRedPacketNum;
    private TextView mTvPeopleNumber;
    private EditText mEdContent;
    private TextView mTvMoney;
    private Button mBtnCommit;

    private String [] strings = {"红包记录","取消"};
    private PopupSelectView popupSelectView;
    private int redPacketType = 1; // 1 普通红包  0.手气红包


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_red_packet);
        initView();
        initEvent();
    }

    private void initView() {
        mActionBar = findViewById(R.id.action_bar);
        mTvMoneyTitle = findViewById(R.id.tv_money_title);
        mEdMoney = findViewById(R.id.ed_money);
        mTvRedPacketTypeTitle = findViewById(R.id.tv_red_packet_type_title);
        mTvRedPacketType = findViewById(R.id.tv_red_packet_type);
        mEdRedPacketNum = findViewById(R.id.ed_red_packet_num);
        mTvPeopleNumber = findViewById(R.id.tv_people_number);
        mEdContent = findViewById(R.id.ed_content);
        mTvMoney = findViewById(R.id.tv_money);
        mBtnCommit = findViewById(R.id.btn_commit);

        mBtnCommit.setEnabled(false);
        mActionBar.setTxtLeft("取消");
        mActionBar.getBtnLeft().setVisibility(View.GONE);
        mActionBar.getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        mActionBar.getBtnRight().setVisibility(View.VISIBLE);
        mEdMoney.setFilters(new InputFilter[]{new NumRangeInputFilter(this)});
        mEdRedPacketNum.setFilters(new InputFilter[]{new NumRangeInputFilter(this,100)});
    }

    private void initEvent(){
        mBtnCommit.setOnClickListener(this);
        mTvRedPacketType.setOnClickListener(this);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_red_packet_type:
                redPacketType(redPacketType);
                break;
            case R.id.btn_commit:
                ToastUtil.show(MultiRedPacketActivity.this,"发红包");
                break;
        }
    }

    private void redPacketType(int type){
        if(type == 0){
            redPacketType = 1;
            mTvRedPacketTypeTitle.setText("当前为拼手气红包，改为");
            mTvRedPacketType.setText("普通红包");
        }else{
            redPacketType = 0;
            mTvRedPacketTypeTitle.setText("当前为普通红包，改为");
            mTvRedPacketType.setText("拼手气红包");
        }
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


                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }
}
