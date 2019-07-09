package com.yanlong.im.user.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.view.SimpleDraweeView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.EventMyUserInfo;
import com.yanlong.im.user.bean.UserInfo;

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

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Response;


public class ImageHeadActivity extends AppActivity {
    public final static String IMAGE_HEAD = "imageHead";
    private HeadView mHeadView;
    private SimpleDraweeView mSdImageHead;
    private PopupSelectView popupSelectView;
    private PopupSelectView saveImagePopup;
    private String[] strings = {"拍照", "相册", "取消"};
    private String[] saveImages = {"保存头像","取消"};
    private String imageHead;
    private CheckPermission2Util permission2Util = new CheckPermission2Util();
    private Button mBtnImageHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_head);
        initView();
        initEvent();
    }

    private void initView() {
        imageHead = getIntent().getStringExtra(IMAGE_HEAD);
        mHeadView = findViewById(R.id.headView);
        mSdImageHead = findViewById(R.id.sd_image_head);
        mSdImageHead.setImageURI(imageHead + "");
        mHeadView.getActionbar().getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        mHeadView.getActionbar().getBtnRight().setVisibility(View.VISIBLE);
        mBtnImageHead = findViewById(R.id.btn_image_head);
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
        mBtnImageHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopup();
            }
        });

        mSdImageHead.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

              //  initSaveImage();

                List<LocalMedia> selectList = new ArrayList<>();
                LocalMedia lc = new LocalMedia();
                lc.setPath(imageHead);
                selectList.add(lc);
                PictureSelector.create(ImageHeadActivity.this).themeStyle(R.style.picture_default_style)
                        .isGif(true).openExternalPreview(0, selectList);
                return false;
            }
        });

    }

    private void initSaveImage(){
        saveImagePopup = new PopupSelectView(this,saveImages);
        saveImagePopup.showAtLocation(mSdImageHead, Gravity.BOTTOM, 0, 0);
        saveImagePopup.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion){
                    case 0:

                        break;
                }
                saveImagePopup.dismiss();
            }
        });
    }


    private void initPopup() {
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(mSdImageHead, Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 0:
                        permission2Util.requestPermissions(ImageHeadActivity.this, new CheckPermission2Util.Event() {
                            @Override
                            public void onSuccess() {
                                PictureSelector.create(ImageHeadActivity.this)
                                        .openCamera(PictureMimeType.ofImage())
                                        .compress(true)
                                        .enableCrop(true)
                                        .withAspectRatio(1, 1)
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
                        PictureSelector.create(ImageHeadActivity.this)
                                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                .previewImage(false)// 是否可预览图片 true or false
                                .isCamera(false)// 是否显示拍照按钮 ture or false
                                .compress(true)// 是否压缩 true or false
                                .enableCrop(true)
                                .withAspectRatio(1, 1)
                                .freeStyleCropEnabled(false)
                                .rotateEnabled(false)
                                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }

    private UpFileAction upFileAction = new UpFileAction();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    final String file = PictureSelector.obtainMultipleResult(data).get(0).getCompressPath();
                    // 例如 LocalMedia 里面返回两种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    Uri uri = Uri.fromFile(new File(file));

                    alert.show();
                    mSdImageHead.setImageURI(uri);
                    upFileAction.upFile(getContext(), new UpFileUtil.OssUpCallback() {
                        @Override
                        public void success(String url) {
                            alert.dismiss();
                            taskUserInfoSet(null, url, null, null);
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


    private void taskUserInfoSet(String imid, final String avatar, String nickname, Integer gender) {
        new UserAction().myInfoSet(imid, avatar, nickname, gender, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                imageHead = avatar;
                if (avatar != null) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setHead(avatar);
                    EventBus.getDefault().post(new EventMyUserInfo(userInfo, EventMyUserInfo.ALTER_HEAD));
                }
                ToastUtil.show(ImageHeadActivity.this, response.body().getMsg());
            }
        });
    }

}
