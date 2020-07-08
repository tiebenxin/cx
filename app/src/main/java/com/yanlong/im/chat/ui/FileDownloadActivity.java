package com.yanlong.im.chat.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.SendFileMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.MyDiskCacheUtils;

import net.cb.cb.library.bean.EventFileRename;
import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.FileConfig;
import net.cb.cb.library.utils.FileUtils;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import org.greenrobot.eventbus.EventBus;

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
    private TextView tvFileSize;//文件大小

    private String fileFormat ="";//文件类型
    private String fileName ="";//文件名
    private String fileUrl ="";//文件url
    private String fileMsgId ="";//文件消息id
    private Activity activity;//当前活动实例

    private String msgString;//传过来msgAllBean转化的JSON字符串，方便传递
    private MsgAllBean msgAllBean;//传过来的msgAllBean
    private SendFileMessage sendFileMessage;

    private int downloadStatus = 3; //0 下载中 1 下载完成 2 下载失败 3 未下载前
    private static int currentFileProgressValue = 0;//记录当前文件下载任务的进度
    private Handler handler;//如果下载过程中退出，下次进来需要轮训查询下载进度
    private boolean ifAutoDownload = false;//是否自动下载

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (currentFileProgressValue < 100) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvDownload.setText("下载中 " + currentFileProgressValue + "%");
                                downloadStatus = 0;
                            }
                        });
                        handler.postDelayed(this, 200);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvDownload.setText("打开文件");
                                downloadStatus = 1;
                            }
                        });
                    }
                }
            }, 200);
        }
    };

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
            msgString = getIntent().getExtras().getString("file_msg");
            ifAutoDownload = getIntent().getExtras().getBoolean("auto_download");
        }
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        tvFileName = findViewById(R.id.tv_file_name);
        ivFileImage = findViewById(R.id.iv_file_image);
        tvDownload = findViewById(R.id.tv_download);
        tvFileSize = findViewById(R.id.tv_file_size);
        handler = new Handler();
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
        if(!TextUtils.isEmpty(msgString)){
            msgAllBean = new Gson().fromJson(msgString, MsgAllBean.class);
            sendFileMessage = msgAllBean.getSendFileMessage();
        }

        //显示文件名
        if(!TextUtils.isEmpty(sendFileMessage.getFile_name())){
            fileName = sendFileMessage.getFile_name();
            //若有同名文件，则重命名，保存最终真实文件名，如123.txt若有重名则依次保存为123.txt(1) 123.txt(2)
            //若没有同名文件，则按默认新文件来保存
            fileName = FileUtils.getFileRename(fileName);
            tvFileName.setText(fileName);
        }
        //根据文件类型，显示图标
        if(!TextUtils.isEmpty(sendFileMessage.getFormat())){
            fileFormat = sendFileMessage.getFormat();
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
        //获取文件消息id
        if(!TextUtils.isEmpty(sendFileMessage.getMsgId())){
            fileMsgId = sendFileMessage.getMsgId();
        }

        //获取文件消息大小
        if(sendFileMessage.getSize()!=0L){
            tvFileSize.setText("文件大小 "+FileUtils.getFileSizeString(sendFileMessage.getSize()));
        }

        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(downloadStatus == 3){
                    startDownload();
                }else if(downloadStatus == 1){
                    openAndroidFile(FileConfig.PATH_DOWNLOAD+fileName);
                }
            }
        });
        if(ifAutoDownload){
            startDownload();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //说明还有文件正在下载
        if(currentFileProgressValue !=0 && currentFileProgressValue !=100){
            new Thread(runnable).start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
        //退出重置状态
        tvDownload.setText("点击下载");
        downloadStatus = 3;//默认状态：未下载
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

    //开始下载
    private void startDownload(){
        //获取url，自动开始下载文件，并打开
        if(!TextUtils.isEmpty(sendFileMessage.getUrl())){
            fileUrl = sendFileMessage.getUrl();
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
                        ToastUtil.showLong(activity,"下载成功! \n文件已保存："+FileConfig.PATH_DOWNLOAD+"目录下");
                        tvDownload.setText("打开文件");
                        downloadStatus = 1;
                        //下载成功后
                        //1 数据库本地保存一个新增属性-真实文件名，主要用于多个同名文件区分保存，防止重名，方便后续聊天界面直接打开重名文件
                        MsgAllBean reMsg = DaoUtil.findOne(MsgAllBean.class, "msg_id", fileMsgId);
                        reMsg.getSendFileMessage().setRealFileRename(fileName);
                        DaoUtil.update(reMsg);
                        //2 通知ChatActivity刷新该文件消息
                        EventFileRename eventFileRename = new EventFileRename();
                        sendFileMessage.setRealFileRename(fileName);
                        eventFileRename.setMsgAllBean(msgAllBean);
                        EventBus.getDefault().post(eventFileRename);
                        currentFileProgressValue = 100;
                        //如果是自动下载，完成后需要自行打开
                        if(ifAutoDownload){
                            openAndroidFile(FileConfig.PATH_DOWNLOAD+fileName);
                        }
                    }

                    @Override
                    public void onDownloading(int progress) {
                        LogUtil.getLog().i("DownloadUtil", "progress:" + progress);
                        tvDownload.setText("下载中 "+progress+"%");
                        downloadStatus = 0;
                        currentFileProgressValue = progress;

                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        ToastUtil.show("文件下载失败");
                        tvDownload.setText("下载失败");
                        LogUtil.getLog().i("DownloadUtil", "Exception下载失败:" + e.getMessage());
                        downloadStatus = 2;
                        currentFileProgressValue = 0;
                    }
                });

            } catch (Exception e) {
                ToastUtil.show("文件下载失败");
                tvDownload.setText("下载失败");
                LogUtil.getLog().i("DownloadUtil", "Exception:" + e.getMessage());
                downloadStatus = 2;
                currentFileProgressValue = 0;
            }
        }
    }

}
