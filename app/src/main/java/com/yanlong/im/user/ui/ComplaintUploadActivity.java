package com.yanlong.im.user.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.drawee.view.SimpleDraweeView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.PopupSelectView;


import java.io.File;

import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @创建人 shenxin
 * @创建时间 2019/8/9 0009 11:57
 */
public class ComplaintUploadActivity extends AppActivity {
    public static final String COMPLATION_TYPE = "complaintType";
    public static final String GID = "gid";
    public static final String UID = "uid";

    private HeadView headView;
    private EditText edContent;
    private Button btnCommit;
    private SimpleDraweeView imageView;
    private String imageUrl;
    private int complaintType;
    private String gid;
    private String uid;
    private String[] strings = {"拍照", "相册", "取消"};
    private PopupSelectView popupSelectView;
    private CheckPermission2Util permission2Util = new CheckPermission2Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_upload);
        initView();
        initEvent();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        edContent = findViewById(R.id.ed_content);
        btnCommit = findViewById(R.id.btn_commit);
        imageView = findViewById(R.id.image_view);

        complaintType = getIntent().getIntExtra(COMPLATION_TYPE,0);
        gid = getIntent().getStringExtra(GID);
        uid = getIntent().getStringExtra(UID);
    }

    private void initEvent() {
        headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopup();
            }
        });
    }


    private void commit() {
        String content = edContent.getText().toString();
        if (!TextUtils.isEmpty(content) || !TextUtils.isEmpty(imageUrl)) {
            new UserAction().userComplaint(complaintType, content, imageUrl, gid, uid, new CallBack<ReturnBean>() {
                @Override
                public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                    super.onResponse(call, response);
                    if(response.body() == null){
                        return;
                    }
                    if(response.body().isOk()){
                        ToastUtil.show(context,"投诉成功");
                        finish();
                    }

                }
            });
        }else{
            ToastUtil.show(context,"请上传违规图片或填写违规内容");
        }
    }


    private void initPopup() {
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(headView, Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 0:
                        permission2Util.requestPermissions(ComplaintUploadActivity.this, new CheckPermission2Util.Event() {
                            @Override
                            public void onSuccess() {
                                PictureSelector.create(ComplaintUploadActivity.this)
                                        .openCamera(PictureMimeType.ofImage())
                                        .compress(true)
                                        .freeStyleCropEnabled(false)
                                        .rotateEnabled(false)
                                        .forResult(PictureConfig.CHOOSE_REQUEST);
                            }

                            @Override
                            public void onFail() {

                            }
                        }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
                        break;
                    case 1:
                        PictureSelector.create(ComplaintUploadActivity.this)
                                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                .previewImage(false)// 是否可预览图片 true or false
                                .isCamera(false)// 是否显示拍照按钮 ture or false
                                .compress(true)// 是否压缩 true or false
                                .freeStyleCropEnabled(false)
                                .rotateEnabled(false)
                                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    final String file = PictureSelector.obtainMultipleResult(data).get(0).getCompressPath();
                    Uri uri = Uri.fromFile(new File(file));
                    alert.show();
                    imageView.setImageURI(uri);
                    new UpFileAction().upFile(UpFileAction.PATH.COMPLAINT,getContext(), new UpFileUtil.OssUpCallback() {
                        @Override
                        public void success(String url) {
                            alert.dismiss();
                            imageUrl = url;
                        }

                        @Override
                        public void fail() {
                            alert.dismiss();
                            ToastUtil.show(getContext(), "上传失败!");
                        }

                        @Override
                        public void inProgress(long progress, long zong) {

                        }
                    }, file);
                    break;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permission2Util.onRequestPermissionsResult();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
