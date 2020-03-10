package com.yanlong.im.share;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.yanlong.im.chat.ui.forward.MsgForwardActivity;

import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.FileUtils;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.AppActivity;

import io.reactivex.annotations.NonNull;

/**
 * @author Liszt
 * @date 2020/3/7
 * Description  外部数据承接
 */
public class CXEntryActivity extends AppActivity {
    private CheckPermission2Util permission2Util = new CheckPermission2Util();
    private String oneShareImgPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLoadingDialog();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            String action = intent.getAction();
            if (Intent.ACTION_SEND.equals(action)) {
                if (extras != null) {
                    getSysImgShare(extras);
                }
            } else {

            }
            startActivity(new Intent(this, MsgForwardActivity.class));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissLoadingDialog();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //获取系统相册分享的图片(暂时仅支持单张图片分享)
    private void getSysImgShare(Bundle extras) {
        //TODO 担心有权限问题，加一层保险起见
        permission2Util.requestPermissions(CXEntryActivity.this, new CheckPermission2Util.Event() {
            @Override
            public void onSuccess() {
                if (extras.containsKey(Intent.EXTRA_STREAM)) {
                    try {
                        Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
                        oneShareImgPath = FileUtils.getFilePathByUri(CXEntryActivity.this, uri);
//                            ToastUtil.show("拿到了图片路径");
                    } catch (Exception e) {
                        LogUtil.getLog().e(e.toString());
                    }
                }
            }

            @Override
            public void onFail() {
                ToastUtil.show("需要同意访问权限");
            }
        }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permission2Util.onRequestPermissionsResult();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
