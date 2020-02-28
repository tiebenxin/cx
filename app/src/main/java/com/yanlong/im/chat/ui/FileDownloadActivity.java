package com.yanlong.im.chat.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.utils.MyDiskCacheUtils;

import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.FileConfig;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import java.io.File;

/**
 * @类名：文件消息下载页面
 * @Date：2020/2/27
 * @by zjy
 * @备注：
 */
public class FileDownloadActivity extends AppActivity {

    private HeadView headView;
    private ActionbarView actionbar;
    private TextView tvFileName;
    private ImageView ivFileImage;//文件图片
    private TextView tvDownload;//更新下载进度

    private String fileFormat ="";//文件类型
    private String fileName ="";//文件名
    private String fileUrl ="";//文件url
    private Activity activity;//当前活动实例


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);
        activity = this;
        initView();
        getExtra();
        initData();
    }

    private void getExtra() {
        if(getIntent()!=null){
            //显示文件名
            if(!TextUtils.isEmpty(getIntent().getStringExtra("file_name"))){
                fileName = getIntent().getStringExtra("file_name");
                tvFileName.setText(fileName);
            }
            //根据文件类型，显示图标
            if(!TextUtils.isEmpty(getIntent().getStringExtra("file_format"))){
                fileFormat = getIntent().getStringExtra("file_format");
                if(fileFormat.equals("txt")){
                    ivFileImage.setImageResource(R.mipmap.ic_txt);
                }else if(fileFormat.equals("xls") || fileFormat.equals("xlsx")){
                    ivFileImage.setImageResource(R.mipmap.ic_excel);
                }else if(fileFormat.equals("ppt") || fileFormat.equals("pptx") || fileFormat.equals("pdf")){ //PDF暂用此图标
                    ivFileImage.setImageResource(R.mipmap.ic_ppt);
                }else if(fileFormat.equals("doc") || fileFormat.equals("docx")){
                    ivFileImage.setImageResource(R.mipmap.ic_word);
                }else if(fileFormat.equals("rar") || fileFormat.equals("zip")){
                    ivFileImage.setImageResource(R.mipmap.ic_zip);
                }else if(fileFormat.equals("exe")){
                    ivFileImage.setImageResource(R.mipmap.ic_exe);
                }else {
                    ivFileImage.setImageResource(R.mipmap.ic_unknow);
                }
            }
            //获取url，自动开始下载文件，并打开
            if(!TextUtils.isEmpty(getIntent().getStringExtra("file_url"))){
                fileUrl = getIntent().getStringExtra("file_url");
                //指定下载路径文件夹，若不存在则创建
                File fileDir = new File(FileConfig.PATH_DOWNLOAD);
                if (!fileDir.exists()) {
                    fileDir.mkdir();
                }
                File file = new File(fileDir, fileName);
                try {
                    DownloadUtil.get().downLoadFile(fileUrl, file, new DownloadUtil.OnDownloadListener() {
                        @Override
                        public void onDownloadSuccess(File file) {
                            ToastUtil.showLong(activity,"下载成功! \n文件已保存："+FileConfig.PATH_DOWNLOAD+"目录");
                            tvDownload.setText("下载完成 100%");
                            //如果用户退出当前界面，则只提示已经完成；若仍在当前界面，则打开文件
                            if(activity==null || activity.isFinishing()){
                            }else {
                                openAndroidFile(FileConfig.PATH_DOWNLOAD+fileName);
                            }
                        }

                        @Override
                        public void onDownloading(int progress) {
                            LogUtil.getLog().i("DownloadUtil", "progress:" + progress);
                            tvDownload.setText("下载中 "+progress+"%");

                        }

                        @Override
                        public void onDownloadFailed(Exception e) {
                            ToastUtil.show("文件下载失败");
                            tvDownload.setText("下载失败");
                            LogUtil.getLog().i("DownloadUtil", "Exception下载失败:" + e.getMessage());
                        }
                    });

                } catch (Exception e) {
                    ToastUtil.show("文件下载失败");
                    tvDownload.setText("下载失败");
                    LogUtil.getLog().i("DownloadUtil", "Exception:" + e.getMessage());
                }
            }
        }
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        tvFileName = findViewById(R.id.tv_file_name);
        ivFileImage = findViewById(R.id.iv_file_image);
        tvDownload = findViewById(R.id.tv_download);

    }

    private void initData() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onRight() {

            }
        });
    }
    /**
     * 选择已有程序打开文件
     *
     * @param filepath
     * @备注  todo 暂时只有2个地方用到，后续如果用的地方较多，再抽取到工具类FileUtil
     */
    public void openAndroidFile(String filepath) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 7.0以上加上文件检查权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // 申请权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    return;
                }
            }
            File file = new File(filepath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(Intent.ACTION_VIEW);//动作，查看
            // 7.0适配问题
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
                intent.setDataAndType(FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file), net.cb.cb.library.utils.FileUtils.getMIMEType(file));//设置类型
            }else{
                intent.setDataAndType(Uri.fromFile(file), net.cb.cb.library.utils.FileUtils.getMIMEType(file));//设置类型
            }
            startActivity(intent);

        } catch (ActivityNotFoundException e) {
            ToastUtil.show("附件不能打开，请下载相关软件！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
