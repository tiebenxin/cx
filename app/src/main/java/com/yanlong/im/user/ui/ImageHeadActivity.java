package com.yanlong.im.user.ui;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.PopupSelectView;

import java.io.File;

import retrofit2.Call;
import retrofit2.Response;


public class ImageHeadActivity extends AppActivity {
    public final static String IMAGE_HEAD = "imageHead";
    private HeadView mHeadView;
    private SimpleDraweeView mSdImageHead;
    private PopupSelectView popupSelectView;
    private String[] strings = {"拍照", "相册", "取消"};
    private String imageHead;

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
     //   mSdImageHead.setImageURI(imageHead + "");
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

    private void initPopup() {
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(mSdImageHead, Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 0:
                        PictureSelector.create(ImageHeadActivity.this)
                                .openCamera(PictureMimeType.ofImage())
                                .compress(true)
                                .enableCrop(true)
                                .withAspectRatio(1, 1)
                                .freeStyleCropEnabled(true)
                                .cropWH(1, 1)
                                .rotateEnabled(false)
                                .forResult(PictureConfig.CHOOSE_REQUEST);
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
                                .freeStyleCropEnabled(true)
                                .cropWH(1, 1)
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
        if (resultCode == RESULT_OK) switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                // 图片选择结果回调
                String file = PictureSelector.obtainMultipleResult(data).get(0).getCompressPath();
                // 例如 LocalMedia 里面返回两种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true



                Uri uri = Uri.fromFile(new File(file));
                mSdImageHead.setImageURI(uri);

                Log.v("testuri", "file:" + file);
                Log.v("testuri", "uri:" + Uri.fromFile(new File(file)));
//                    taskUserInfoSet(null,
//                            "https://gd3.alicdn.com/imgextra/i4/0/O1CN01abxzGk1J3fryfJVqz_!!0-item_pic.jpg_400x400.jpg",
//                            null, null);
                break;
        }
    }


    private void taskUserInfoSet(String imid, String avatar, String nickname, Integer gender) {
        new UserAction().userInfoSet(imid, avatar, nickname, gender, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                //ToastUtil.show(ImageHeadActivity.this,response.body().getMsg());
            }
        });
    }

}
