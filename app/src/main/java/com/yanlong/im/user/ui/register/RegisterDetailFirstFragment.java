package com.yanlong.im.user.ui.register;

import android.graphics.Color;
import android.view.View;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.yanlong.im.R;
import com.yanlong.im.databinding.FragmentRegisterFirstBinding;

import java.util.Calendar;
import java.util.Date;


/**
 * @author Liszt
 * @date 2020/11/16
 * Description
 */
public class RegisterDetailFirstFragment extends BaseRegisterFragment<FragmentRegisterFirstBinding> {

    @Override
    public int getLayoutId() {
        return R.layout.fragment_register_first;
    }

    @Override
    public void init() {
        mViewBinding.ivLeft.setVisibility(View.GONE);
        mViewBinding.ivRight.setVisibility(View.VISIBLE);
    }

    @Override
    public void initListener() {
        mViewBinding.ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onNext();
                }
            }
        });

        mViewBinding.llMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mViewBinding.llWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mViewBinding.tvBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTimePicker();
            }
        });
    }


    //时间选择器。选择生日
    private void initTimePicker() {
        Calendar defaultCalendar = Calendar.getInstance();
        defaultCalendar.set(2000, 0, 1);//2019-1-1
        Calendar start = Calendar.getInstance();
        start.set(1950, 0, 1);//2019-1-1
        Calendar end = Calendar.getInstance();

        //时间选择器
        TimePickerView pvTime = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                ((RegisterDetailActivity) getActivity()).getDetailBean().setBirthday(calendar.getTimeInMillis());
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setDate(defaultCalendar)
                .setRangDate(start, end)
                .setCancelText("取消")
                .setCancelColor(Color.parseColor("#878787"))
                .setSubmitText("确定")
                .setSubmitColor(Color.parseColor("#32b152"))
                .build();
        pvTime.show();
    }

    @Override
    public void updateDetailUI() {
        if (detailBean != null) {

        }
    }
}
