package com.yanlong.im.user.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.utils.PhoneListUtil;

import net.cb.cb.library.bean.QRCodeBean;
import net.cb.cb.library.utils.QRCodeManage;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.TouchUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.zxing.activity.CaptureActivity;

import java.util.List;

/***
 * 添加朋友
 */
public class FriendAddAcitvity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private LinearLayout viewSearch;
    private LinearLayout viewMatch;
    private LinearLayout viewQr;
    private LinearLayout viewWc;



    //自动寻找控件
    private void findViews() {
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        viewSearch = (LinearLayout) findViewById(R.id.view_search);
        viewMatch = (LinearLayout) findViewById(R.id.view_match);
        viewQr = (LinearLayout) findViewById(R.id.view_qr);
        viewWc = (LinearLayout) findViewById(R.id.view_wc);
    }


    //自动生成的控件事件
    private void initEvent() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

        viewMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                go(FriendMatchActivity.class);
            }
        });

        viewQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // 申请权限
                    ActivityCompat.requestPermissions(FriendAddAcitvity.this,
                            new String[]{Manifest.permission.CAMERA},
                            CaptureActivity.REQ_PERM_CAMERA);
                    return;
                }
                // 二维码扫码
                Intent intent = new Intent(FriendAddAcitvity.this, CaptureActivity.class);
                startActivityForResult(intent, CaptureActivity.REQ_QR_CODE);
            }
        });
        viewWc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToastUtil.show(getContext(),"wx");
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);
        findViews();
        initEvent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
            QRCodeBean bean = QRCodeManage.getQRCodeBean(this,scanResult);
            QRCodeManage.goToActivity(this,bean);
        }
    }


}
