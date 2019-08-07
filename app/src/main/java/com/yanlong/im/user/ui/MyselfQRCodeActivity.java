package com.yanlong.im.user.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.zxing.WriterException;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.yanlong.im.R;
import com.yanlong.im.chat.ui.ChatActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.QRCodeManage;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.bean.QRCodeBean;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.ImgSizeUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.PopupSelectView;
import net.cb.cb.library.zxing.activity.CaptureActivity;
import net.cb.cb.library.zxing.encoding.EncodingHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MyselfQRCodeActivity extends AppActivity {
    public static final String TYPE = "type";
    public static final String GROUP_ID = "groupId";
    public static final String GROUP_HEAD = "groupHead";
    public static final String GROUP_NAME = "groupName";

    private HeadView mHeadView;
    private SimpleDraweeView mImgHead;
    private ConstraintLayout mViewMyQrcode;
    private TextView mTvUserName;
    private ImageView mCrCode;
    private PopupSelectView popupSelectView;
    private String[] strings = {"保存图片", "扫描二维码", "分享给好友", "分享给微信好友", "取消"};
    private String QRCode;
    private int type;
    private String groupId;
    private String groupHead;
    private String groupName;
    private String imageUrl;
    private ImgSizeUtil.ImageSize imgsize;//获取图片大小

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
        mViewMyQrcode = findViewById(R.id.view_my_qrcode);
        type = getIntent().getIntExtra(TYPE, 0);
        UMShareAPI.get(this);
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


    private void initData() {
        QRCodeBean qrCodeBean = new QRCodeBean();
        if (type == 0) {
            UserInfo userInfo = UserAction.getMyInfo();
            String uid = userInfo.getUid() + "";
            mImgHead.setImageURI(userInfo.getHead() + "");
            mTvUserName.setText(userInfo.getName() + "");
            qrCodeBean.setHead(QRCodeManage.HEAD);
            qrCodeBean.setFunction(QRCodeManage.ADD_FRIEND_FUNCHTION);
            qrCodeBean.setParameterValue(QRCodeManage.ID, uid);
            QRCode = QRCodeManage.getQRcodeStr(qrCodeBean);
        } else {
            Intent intent = getIntent();
            UserInfo userInfo = UserAction.getMyInfo();

            groupId = intent.getStringExtra(GROUP_ID);
            groupHead = intent.getStringExtra(GROUP_HEAD);
            groupName = intent.getStringExtra(GROUP_NAME);
            mImgHead.setImageURI(groupHead + "");
            mTvUserName.setText(groupName + "");
            qrCodeBean.setHead(QRCodeManage.HEAD);
            qrCodeBean.setFunction(QRCodeManage.ADD_GROUP_FUNCHTION);
            qrCodeBean.setParameterValue(QRCodeManage.ID, groupId);
            qrCodeBean.setParameterValue(QRCodeManage.UID, userInfo.getUid() + "");
            qrCodeBean.setParameterValue(QRCodeManage.TIME, QRCodeManage.getTime(7));
            qrCodeBean.setParameterValue(QRCodeManage.NICK_NAME,userInfo.getName());
            QRCode = QRCodeManage.getQRcodeStr(qrCodeBean);
        }
        try {
            Bitmap bitmap = EncodingHandler.createQRCode(QRCode, DensityUtil.dip2px(this, 300));
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
                        saveBmp2Gallery(getViewBitmap());
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
                        Bitmap2Bytes(getViewBitmap());
                        break;
                    case 3:
                        shareWX(getViewBitmap());
                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }


    private Bitmap getViewBitmap() {
        mViewMyQrcode.buildDrawingCache();
        return mViewMyQrcode.getDrawingCache();
    }


    private void shareWX(Bitmap bitmap) {
        UMImage thumb = new UMImage(this, bitmap);
        new ShareAction(MyselfQRCodeActivity.this)
                .setPlatform(SHARE_MEDIA.WEIXIN)//传入平台
                .withMedia(thumb)//分享内容
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        if (throwable.getMessage().contains("2008")) {
                            ToastUtil.show(context, "请安装微信");
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                })
                .share();
    }

    public void Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        imgsize=new ImgSizeUtil.ImageSize();
        imgsize.setWidth(bm.getWidth());
        imgsize.setHeight(bm.getHeight());

        new UpFileAction().upFile(this, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                //2.发送图片
                imageUrl = url;
                Intent intent = new Intent(MyselfQRCodeActivity.this, SelectUserActivity.class);
                startActivityForResult(intent, SelectUserActivity.RET_CODE_SELECTUSR);
            }

            @Override
            public void fail() {
                ToastUtil.show(getContext(), "二维码生成失败请重试");
            }

            @Override
            public void inProgress(long progress, long zong) {

            }
        }, baos.toByteArray());
    }

    /**
     * @param bmp 获取的bitmap数据
     */
    public void saveBmp2Gallery(Bitmap bmp) {
        if (bmp == null) {
            ToastUtil.show(this, "保存失败");
            return;
        }

        String fileName = null;
        String galleryPath = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                + File.separator + "Camera" + File.separator;

        File file = null;
        FileOutputStream outStream = null;
        try {
            file = new File(galleryPath, System.currentTimeMillis() + ".jpg");
            fileName = file.toString();
            outStream = new FileOutputStream(fileName);
            if (null != outStream) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
            }

        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //通知相册更新
        MediaStore.Images.Media.insertImage(this.getContentResolver(),
                bmp, fileName, null);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        this.sendBroadcast(intent);
        ToastUtil.show(this, "保存成功");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
            QRCodeBean bean = QRCodeManage.getQRCodeBean(this, scanResult);
            QRCodeManage.goToActivity(this, bean);
        } else if (requestCode == SelectUserActivity.RET_CODE_SELECTUSR && resultCode == SelectUserActivity.RET_CODE_SELECTUSR) {
            Bundle bundle = data.getExtras();
            String jsonBean = bundle.getString(SelectUserActivity.RET_JSON);
            UserInfo userInfo = new Gson().fromJson(jsonBean, UserInfo.class);

            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(ChatActivity.AGM_TOUID, userInfo.getUid());
            startActivity(intent);
            //向服务器发送图片
            SocketData.send4Image(userInfo.getUid(), null, imageUrl,imgsize);
        }
    }

}

