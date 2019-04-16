package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.yanlong.im.R;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.PopupSelectView;

public class UploadIdentityActivity extends AppActivity implements View.OnClickListener {
    private static final int FRONT = 1000;
    private static final int CONTRARY = 2000;

    private HeadView mHeadView;
    private ImageView mIvFront;
    private ImageView mIvContrary;
    private Button mBtnCommit;
    private String[] strings = {"拍照", "相册", "取消"};
    private PopupSelectView popupSelectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_identity);
        initView();
        initEvent();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mIvFront = findViewById(R.id.iv_front);
        mIvContrary = findViewById(R.id.iv_contrary);
        mBtnCommit = findViewById(R.id.btn_commit);
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
        mIvFront.setOnClickListener(this);
        mIvContrary.setOnClickListener(this);
        mBtnCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_front:
                initPopup(1);
                break;
            case R.id.iv_contrary:
                initPopup(2);
                break;
            case R.id.btn_commit:

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FRONT:
                    // 图片选择结果回调
                    String file = PictureSelector.obtainMultipleResult(data).get(0).getCompressPath();
                    ToastUtil.show(this, file);

                    break;
                case CONTRARY:
                    // 图片选择结果回调
                    String file1 = PictureSelector.obtainMultipleResult(data).get(0).getCompressPath();
                    ToastUtil.show(this, file1);

                    break;
            }
        }
    }


    private void initPopup(final int type) {
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(mIvFront, Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 0:
                        if (type == 1) {
                            PictureSelector.create(UploadIdentityActivity.this)
                                    .openCamera(PictureMimeType.ofImage())
                                    .compress(true)
                                    .forResult(FRONT);
                        } else {
                            PictureSelector.create(UploadIdentityActivity.this)
                                    .openCamera(PictureMimeType.ofImage())
                                    .compress(true)
                                    .forResult(CONTRARY);
                        }
                        break;
                    case 1:
                        if (type == 1) {
                            PictureSelector.create(UploadIdentityActivity.this)
                                    .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                                    .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                    .previewImage(false)// 是否可预览图片 true or false
                                    .isCamera(false)// 是否显示拍照按钮 ture or false
                                    .compress(true)// 是否压缩 true or false
                                    .forResult(FRONT);//结果回调onActivityResult code
                        } else {
                            PictureSelector.create(UploadIdentityActivity.this)
                                    .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                                    .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                    .previewImage(false)// 是否可预览图片 true or false
                                    .isCamera(false)// 是否显示拍照按钮 ture or false
                                    .compress(true)// 是否压缩 true or false
                                    .forResult(CONTRARY);//结果回调onActivityResult code
                        }
                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }


}
