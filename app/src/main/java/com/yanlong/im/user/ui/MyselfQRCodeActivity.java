package com.yanlong.im.user.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.zxing.WriterException;
import com.yanlong.im.R;
import com.yanlong.im.utils.QRCodeManage;

import net.cb.cb.library.bean.QRCodeBean;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.PopupSelectView;
import net.cb.cb.library.zxing.activity.CaptureActivity;
import net.cb.cb.library.zxing.encoding.EncodingHandler;

public class MyselfQRCodeActivity extends AppActivity {
    private HeadView mHeadView;
    private SimpleDraweeView mImgHead;
    private TextView mTvUserName;
    private ImageView mCrCode;
    private PopupSelectView popupSelectView;
    private String[] strings = {"保存图片", "扫描二维码", "分享给好友", "分享给微信好友", "取消"};
    private String QRCode = "YLIM://ADDFRIEND?id=123456&name=hahaha";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_qrcode);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mImgHead = findViewById(R.id.img_head);
        mTvUserName = findViewById(R.id.tv_user_name);
        mCrCode = findViewById(R.id.cr_code);
        mHeadView.getActionbar().getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        mHeadView.getActionbar().getBtnRight().setVisibility(View.VISIBLE);
    }

    private void initEvent() {
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                initPopup();
            }
        });
    }


    private void initData(){
        try {
            Bitmap bitmap = EncodingHandler.createQRCode(QRCode, DensityUtil.dip2px(this,300));
            mCrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void initPopup() {
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(mImgHead, Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 0:

                        break;
                    case 1:
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            // 申请权限
                            ActivityCompat.requestPermissions(MyselfQRCodeActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    CaptureActivity.REQ_PERM_CAMERA);
                            return;
                        }
                        // 二维码扫码
                        Intent intent = new Intent(MyselfQRCodeActivity.this, CaptureActivity.class);
                        startActivityForResult(intent, CaptureActivity.REQ_QR_CODE);
                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                }
                popupSelectView.dismiss();
            }
        });
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

