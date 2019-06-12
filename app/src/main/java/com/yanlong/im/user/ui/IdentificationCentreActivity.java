package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.yanlong.im.R;
import com.yanlong.im.pay.ui.RedpacketRecordActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.IdCardBean;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

import static com.yanlong.im.user.ui.SelectProfessionActivity.JOB_TYPE;
import static com.yanlong.im.user.ui.SelectProfessionActivity.SELECT_PROFEESION;

public class IdentificationCentreActivity extends AppActivity implements View.OnClickListener {
    private static final int PROFESSION = 1000;
    private static final int CARD_PHOTO = 2000;

    private HeadView mHeadView;
    private TextView mTvName;
    private TextView mTvIdentityNumber;
    private LinearLayout mLlProfession;
    private TextView mTvProfession;
    private LinearLayout mLlIdentityCard;
    private TextView mTvIdentityCard;
    private LinearLayout mLlPeriodValidity;
    private TextView mTvPeriodValidity;
    private UserAction userAction;
    private TextView mTvIdType;
    private String jobType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification_centre);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        userAction = new UserAction();
        mHeadView = findViewById(R.id.headView);
        mTvName = findViewById(R.id.tv_name);
        mTvIdentityNumber = findViewById(R.id.tv_identity_number);
        mTvIdType = findViewById(R.id.tv_id_type);
        mLlProfession = findViewById(R.id.ll_profession);
        mTvProfession = findViewById(R.id.tv_profession);
        mLlIdentityCard = findViewById(R.id.ll_identity_card);
        mTvIdentityCard = findViewById(R.id.tv_identity_card);
        mLlPeriodValidity = findViewById(R.id.ll_period_validity);
        mTvPeriodValidity = findViewById(R.id.tv_period_validity);
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


    private void initData() {
        taskIdCardInfo();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_profession:
                Intent professionIntent = new Intent(this, SelectProfessionActivity.class);
                professionIntent.putExtra(JOB_TYPE, jobType);
                startActivityForResult(professionIntent, PROFESSION);
                break;
            case R.id.ll_period_validity:
                initTimePicker();
                break;
            case R.id.ll_identity_card:
                Intent uploadIdentityIntent = new Intent(this, UploadIdentityActivity.class);
                startActivityForResult(uploadIdentityIntent, CARD_PHOTO);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PROFESSION:
                    String string = data.getStringExtra(SELECT_PROFEESION);
                    if (!TextUtils.isEmpty(string)) {
                        taskSetJobType(string);
                    }
                    break;
                case CARD_PHOTO:
                    mTvIdentityCard.setText("已认证");
                    break;
            }
        }

    }


    private void initTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year,month-1,day);
        //时间选择器
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                taskSetExpiryDate(year+"-"+month+"-"+day);
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setDate(calendar)
                .build();

        pvTime.show();
    }


    private void taskSetExpiryDate(final String expiryDate) {
        userAction.setExpiryDate(expiryDate, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(context, response.body().getMsg());
                if (response.body().isOk()) {
                    mTvPeriodValidity.setText(expiryDate);
                }
            }
        });
    }


    private void taskSetJobType(final String jobType) {
        userAction.setJobType(jobType, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                ToastUtil.show(context, response.body().getMsg());
                if (response.body().isOk()) {
                    mTvProfession.setText(jobType);
                }
            }
        });
    }


    private void taskIdCardInfo() {
        userAction.getIdCardInfo(new CallBack<ReturnBean<IdCardBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<IdCardBean>> call, Response<ReturnBean<IdCardBean>> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    IdCardBean bean = response.body().getData();
                    if (bean != null) {
                        mTvName.setText(bean.getName() + "");
                        mTvIdentityNumber.setText(bean.getIdNumber() + "");
                        mTvIdType.setText(bean.getIdType() + "");
                        mTvProfession.setText(bean.getJobType() + "");
                        if(TextUtils.isEmpty(bean.getExpiryDate())){
                            mTvPeriodValidity.setText("未设置");
                        }else{
                            mTvPeriodValidity.setText(bean.getExpiryDate());
                        }
                        if(bean.getStat() == 2){
                            mTvIdentityCard.setText("已认证");
                        }else{
                            mTvIdentityCard.setText("未认证");
                        }

                        jobType = bean.getJobType();
                    } else {
                        ToastUtil.show(context, response.body().getMsg());
                    }
                }
            }
        });
    }

}


























