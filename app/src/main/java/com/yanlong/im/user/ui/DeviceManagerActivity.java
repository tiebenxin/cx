package com.yanlong.im.user.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;

import com.yanlong.im.R;
import com.yanlong.im.chat.ui.view.ControllerLinearList;
import com.yanlong.im.databinding.ActivityDeviceManagerBinding;

import net.cb.cb.library.view.AppActivity;

/**
 * @author Liszt
 * @date 2020/5/22
 * Description
 */
public class DeviceManagerActivity extends AppActivity {

    private ActivityDeviceManagerBinding ui;
    private ControllerLinearList viewDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_device_manager);
        viewDeviceList = new ControllerLinearList(ui.llDevice);
    }
}
