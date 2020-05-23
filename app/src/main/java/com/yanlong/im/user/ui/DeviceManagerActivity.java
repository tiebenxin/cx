package com.yanlong.im.user.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.yanlong.im.R;
import com.yanlong.im.adapter.AdapterDeviceList;
import com.yanlong.im.chat.ui.view.ControllerLinearList;
import com.yanlong.im.databinding.ActivityDeviceManagerBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.DeviceBean;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @author Liszt
 * @date 2020/5/22
 * Description
 */
public class DeviceManagerActivity extends AppActivity {

    private ActivityDeviceManagerBinding ui;
    private ControllerLinearList viewDeviceList;
    private AdapterDeviceList adapter;
    private UserAction userAction = new UserAction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_device_manager);
        ui.headView.getActionbar().setTxtRight("编辑");
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onRight() {
                if (adapter.getModel() == 1) {
                    ui.headView.getActionbar().setTxtRight("编辑");
                    adapter.setModel(0);
                } else {
                    ui.headView.getActionbar().setTxtRight("完成");
                    adapter.setModel(1);
                }
            }
        });
        viewDeviceList = new ControllerLinearList(ui.llDevice);
        adapter = new AdapterDeviceList(this);
        adapter.setListener(new AdapterDeviceList.IDeviceClick() {
            @Override
            public void onClick(DeviceBean bean) {
                if (adapter.getModel() == 1) {//删除
                    deleDevice(bean);
                } else {

                }
            }
        });
        viewDeviceList.setAdapter(adapter);
        getAllDevice();
    }

    private void getAllDevice() {
        userAction.getAllDevice(new CallBack<ReturnBean<List<DeviceBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<DeviceBean>>> call, Response<ReturnBean<List<DeviceBean>>> response) {
                super.onResponse(call, response);
                if (response != null && response.body() != null && response.isSuccessful()) {
                    List<DeviceBean> list = response.body().getData();
                    adapter.bindData(list);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<DeviceBean>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    private void deleDevice(DeviceBean bean) {
        userAction.deleteDevice(bean.getDevice(), new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }
}
