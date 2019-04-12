package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yanlong.im.R;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.PopupSelectView;

public class MyselfQRCodeActivity extends AppActivity {
    private HeadView mHeadView;
    private SimpleDraweeView mImgHead;
    private TextView mTvUserName;
    private SimpleDraweeView mCrCode;
    private PopupSelectView popupSelectView;
    private String[] strings = {"保存图片", "扫描二维码", "分享给好友", "分享给微信好友", "取消"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_qrcode);
        initView();
        initEvent();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mImgHead = findViewById(R.id.img_head);
        mTvUserName = findViewById(R.id.tv_user_name);
        mCrCode = findViewById(R.id.cr_code);
    }

    private void initEvent() {
        mHeadView.getActionbar().setTxtRight("---");
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


    private void initPopup() {
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(mImgHead, Gravity.BOTTOM,0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                ToastUtil.show(MyselfQRCodeActivity.this, string);
                popupSelectView.dismiss();
            }
        });
    }

}

