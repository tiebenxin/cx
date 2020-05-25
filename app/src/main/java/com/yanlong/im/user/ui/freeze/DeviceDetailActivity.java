package com.yanlong.im.user.ui.freeze;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityDeviceDetailBinding;
import com.yanlong.im.user.bean.DeviceBean;

import net.cb.cb.library.view.AppActivity;

/**
 * @author Liszt
 * @date 2020/5/25
 * Description
 */
public class DeviceDetailActivity extends AppActivity {

    private ActivityDeviceDetailBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_device_detail);
        Intent intent = getIntent();
        DeviceBean deviceBean = intent.getParcelableExtra("data");
        if (deviceBean != null) {
            initData(deviceBean);
        }
    }

    private void initData(DeviceBean deviceBean) {
        ui.tvDeviceName.setText(deviceBean.getName());
        ui.tvDeviceOs.setText(deviceBean.getDetail());
    }
}
