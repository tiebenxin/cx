package com.yanlong.im.pay.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hm.cxpay.databinding.ActivityRedEnvelopeDetailBinding;
import com.hm.cxpay.widget.wheel.DateTimeWheelDialog;

import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.view.AppActivity;

import java.util.Calendar;
import java.util.Date;

/**
 * @anthor Liszt
 * @data 2019/12/2
 * Description
 */
@Route(path = "/app/redEnvelopeDetailsActivity")
public class RedEnvelopeDetailsActivity extends AppActivity {

    private ActivityRedEnvelopeDetailBinding ui;
    private Date currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, com.hm.cxpay.R.layout.activity_red_envelope_detail);
        ui.tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeWheelDialog timeWheelDialog = createDialog(2);
                timeWheelDialog.show();

            }
        });

    }

    private DateTimeWheelDialog createDialog(int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2019);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        Date startDate = calendar.getTime();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2099);
        Date endDate = calendar.getTime();

        DateTimeWheelDialog dialog = new DateTimeWheelDialog(this);
//        dialog.setShowCount(7);
//        dialog.setItemVerticalSpace(24);
        dialog.show();
        dialog.setTitle("选择时间");
        int config = DateTimeWheelDialog.SHOW_YEAR_MONTH_DAY_HOUR_MINUTE;
        switch (type) {
            case 1:
                config = DateTimeWheelDialog.SHOW_YEAR;
                break;
            case 2:
                config = DateTimeWheelDialog.SHOW_YEAR_MONTH;
                break;
            case 3:
                config = DateTimeWheelDialog.SHOW_YEAR_MONTH_DAY;
                break;
            case 4:
                config = DateTimeWheelDialog.SHOW_YEAR_MONTH_DAY_HOUR;
                break;
            case 5:
                config = DateTimeWheelDialog.SHOW_YEAR_MONTH_DAY_HOUR_MINUTE;
                break;
        }
        dialog.configShowUI(config);
        dialog.setCancelButton("取消", null);
        dialog.setOKButton("确定", new DateTimeWheelDialog.OnClickCallBack() {
            @Override
            public boolean callBack(View v, @NonNull Date selectedDate) {
                currentDate = selectedDate;
                ui.tvTime.setText(TimeToString.getSelectMouth(selectedDate.getTime()));
                return false;
            }
        });
        dialog.setDateArea(startDate, endDate, true);
        if (currentDate == null) {
            currentDate = new Date();
        }
        dialog.updateSelectedDate(currentDate);
        return dialog;
    }
}
