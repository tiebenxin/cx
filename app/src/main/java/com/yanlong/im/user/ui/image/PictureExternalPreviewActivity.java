package com.yanlong.im.user.ui.image;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.luck.picture.lib.PictureBaseActivity;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ToastManage;
import com.luck.picture.lib.utils.PicSaveUtils;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.luck.picture.lib.zxing.decoding.RGBLuminanceSource;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.eventbus.EventCollectImgOrVideo;
import com.yanlong.im.chat.eventbus.EventReceiveImage;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.utils.QRCodeManage;
import com.zhaoss.weixinrecorded.activity.ImageShowActivity;

import net.cb.cb.library.bean.FileBean;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.view.AlertYesNo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Response;

/**
 * author：luck
 * project：PictureSelector  使用scheme 调用  "scheme://picture/mainDetail"
 * package：com.luck.picture.ui
 * email：邮箱->893855882@qq.com
 * data：17/01/18
 */
public class PictureExternalPreviewActivity extends PictureBaseActivity implements View.OnClickListener, IPreviewImage {
    private static String TAG = "PictureExternalPreviewActivity";
    public static int IMG_EDIT = 0;//长按图片编辑
    private ImageButton left_back;
    private TextView tv_title;
    private PreviewViewPager viewPager;
    private List<LocalMedia> images = new ArrayList<>();
    private int position = 0;
    private RxPermissions rxPermissions;
    private LoadDataThread loadDataThread;
    private int fromWhere;//跳转来源 0 默认 1 猜你想要 2 收藏详情
    private MsgDao msgDao = new MsgDao();
    private String gid;
    private Long toUid;
    private AdapterPreviewImage mAdapter;
    String indexPath;
    private String collectJson = "";//收藏详情点击大图转发需要的数据
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(com.luck.picture.lib.R.layout.picture_activity_external_preview);
        EventBus.getDefault().register(this);
        tv_title = (TextView) findViewById(com.luck.picture.lib.R.id.picture_title);
        left_back = (ImageButton) findViewById(com.luck.picture.lib.R.id.left_back);
        viewPager = (PreviewViewPager) findViewById(com.luck.picture.lib.R.id.preview_pager);
        position = getIntent().getIntExtra(PictureConfig.EXTRA_POSITION, 0);
        images = (List<LocalMedia>) getIntent().getSerializableExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);
        fromWhere = getIntent().getIntExtra(PictureConfig.FROM_WHERE, PictureConfig.FROM_DEFAULT);
        collectJson = getIntent().getStringExtra(PictureConfig.COLLECT_JSON);
        Intent intent = getIntent();
        position = intent.getIntExtra(PictureConfig.EXTRA_POSITION, 0);
        images = (List<LocalMedia>) intent.getSerializableExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);
        gid = intent.getStringExtra(PictureConfig.GID);
        toUid = intent.getLongExtra(PictureConfig.TO_UID, 0L);
        left_back.setOnClickListener(this);
        MessageManager.getInstance().initPreviewID(gid, toUid);
        initAndPermissions();
        activity = this;
        builder = new CommonSelectDialog.Builder(activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检测是否有权限
    }

    //权限申请和初始化
    private void initAndPermissions() {
        if (rxPermissions == null) {
            rxPermissions = new RxPermissions(PictureExternalPreviewActivity.this);
        }
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            initViewPageAdapterData();
                        } else {
                            ToastManage.s(mContext, getString(com.luck.picture.lib.R.string.picture_jurisdiction));
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessageManager.getInstance().clearPreviewId();
    }

    private void initViewPageAdapterData() {
        if (images != null && images.size() > 0) {
            tv_title.setText(position + 1 + "/" + images.size());
            mAdapter = new AdapterPreviewImage(this, fromWhere, collectJson, this);
            mAdapter.setPopParentView(tv_title);
            mAdapter.setCurrentData(images.get(position));
            mAdapter.bindData(images);
            viewPager.setAdapter(mAdapter);
            viewPager.setCurrentItem(position);
            indexPath = images.get(position).getPath();


            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    tv_title.setText(position + 1 + "/" + images.size());
                    mAdapter.setCurrentData(images.get(position));
                    indexPath = images.get(position).getPath();
                    PictureExternalPreviewActivity.this.position = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            //点击返回
            viewPager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshChat(EventFactory.ClosePictureEvent event) {
        if (images != null && event != null) {
            for (LocalMedia localMedia : images) {
                if (event.msg_id.equals(localMedia.getMsg_id())) {
                    showDialog(event.name);
                    break;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshChat(EventReceiveImage event) {
        if (images != null && event != null) {
            String gid = event.getGid();
            long uid = event.getToUid();
            if (!TextUtils.isEmpty(gid) && !TextUtils.isEmpty(this.gid) && gid.equals(this.gid)) {
                updateMessageList();
            } else if (toUid != null && toUid.longValue() == uid) {
                updateMessageList();
            }
        }
    }

    private void showDialog(String name) {
        AlertYesNo alertYesNo = new AlertYesNo();
        alertYesNo.init(PictureExternalPreviewActivity.this, null, "\"" + name + "\"" + "撤回了一条消息",
                "确定", null, new AlertYesNo.Event() {
                    @Override
                    public void onON() {

                    }

                    @Override
                    public void onYes() {
                        finish();
                    }
                });
        alertYesNo.show();
    }

    @Override
    public void onClick(View v) {
        finish();
        overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
    }


    Bitmap scanBitmap;

    /**
     * 扫描二维码图片的方法
     *
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
//        int sampleSize = (int) (options.outHeight / (float) 200);
//        if (sampleSize <= 0)
//            sampleSize = 1;
        //  options.inSampleSize = sampleSize;
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void saveImage(String path) {
        LogUtil.getLog().e("TAG", "------------showLoadingImage$:saveImage " + path);

        boolean isHttp = PictureMimeType.isHttp(path);
        if (isHttp) {
            LogUtil.getLog().e("TAG", "------------showLoadingImage$:saveImage " + "http");
            showPleaseDialog();
            loadDataThread = new LoadDataThread(path, 0, null);
            loadDataThread.start();
        } else {
            if (path.toLowerCase().startsWith("file://")) {
                path = path.replace("file://", "");
            }
            // 有可能本地图片
            try {
                String[] paths = null;
                String spiltPath = null;
                if (path.contains("_below")) {
                    paths = path.split("_below");
                    spiltPath = paths[0];
                    File file = new File(path);
                    file.renameTo(new File(spiltPath));
                    path = spiltPath;
                }
                LogUtil.getLog().e("TAG", "------------showLoadingImage$:saveImage__path__" + path + "--------" + spiltPath);
                String fileName = getFileExt(path);
                String spiltFileName = null;
                if (fileName.contains("_below")) {
                    paths = path.split("_below");
                    spiltFileName = paths[0];
                }
                LogUtil.getLog().e("TAG", "------------showLoadingImage$:saveImage__path__" + fileName);
                //TODO:为什么要copy到相册？相册更新可以是自定义文件路径，执行MediaStore.Images.Media.insertImage会在相册中产生两张图片
                //刷新相册的广播
//                if (DeviceUtils.isViVoAndOppo()) {
//                    String dirPath = PictureFileUtils.createDir(mContext, fileName, "/Pictures");
//                    PictureFileUtils.copyFile(fileName, dirPath);
//                    MediaStore.Images.Media.insertImage(mContext.getContentResolver(), path, fileName, null);
//                    LogUtil.getLog().d("a=", "DeviceUtils" + "--保存图片到相册--" + dirPath);
//                    PicSaveUtils.sendBroadcast(new File(dirPath), getApplicationContext());
//                } else {
//                    PicSaveUtils.sendBroadcast(new File(path), getApplicationContext());
//                    LogUtil.getLog().d("a=", "" + "--保存图片到相册--" + path);
//                }
                PicSaveUtils.sendBroadcast(new File(path), getApplicationContext());
                ToastManage.s(mContext, "保存成功");
                dismissDialog();
            } catch (Exception e) {
                ToastManage.s(mContext, getString(com.luck.picture.lib.R.string.picture_save_error) + "\n" + e.getMessage());
                dismissDialog();
                e.printStackTrace();
            }
        }
        dismissDialog();
    }

    private CommonSelectDialog dialogFour;//单选转发/收藏失效消息提示弹框
    private CommonSelectDialog.Builder builder;

    /**
     * 单选转发/收藏失效消息提示弹框
     */
    private void showMsgFailDialog() {
        if(activity==null || activity.isFinishing()){
            return;
        }
        dialogFour = builder.setTitle("你所选的消息已失效")
                .setShowLeftText(false)
                .setRightText("确定")
                .setRightOnClickListener(v -> {
                    dialogFour.dismiss();
                })
                .build();
        dialogFour.show();
    }

    /**
     * 转发前先检查文件是否过期
     *
     * @param msgId
     * @param fromWhere
     * @param type  1 转发 2 收藏 3 图片编辑
     */
    @Override
    public void onClick(String msgId, int fromWhere, int type , LocalMedia media) {

        MsgAllBean msgbean = null;

        if (!TextUtils.isEmpty(msgId)) {
            msgbean = msgDao.getMsgById(msgId);
        }
        if (msgbean == null) {
            ToastUtil.show("消息已被删除或者被焚毁，不能转发");
            return;
        }

        if (msgbean.getMsg_type() == ChatEnum.EMessageType.IMAGE || msgbean.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO
                || msgbean.getMsg_type() == ChatEnum.EMessageType.FILE) {
            ArrayList<FileBean> list = new ArrayList<>();
            FileBean fileBean = new FileBean();
            if (msgbean.getImage() != null) {
                fileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgbean.getImage().getPreview()));
                fileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgbean.getImage().getPreview(), msgbean.getMsg_type()));
            } else if (msgbean.getVideoMessage() != null) {
                FileBean itemFileBean = new FileBean();
                itemFileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgbean.getVideoMessage().getBg_url()));
                itemFileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgbean.getVideoMessage().getBg_url(), ChatEnum.EMessageType.IMAGE));
                list.add(itemFileBean);
                fileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgbean.getVideoMessage().getUrl()));
                fileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgbean.getVideoMessage().getUrl(), msgbean.getMsg_type()));
            } else if (msgbean.getSendFileMessage() != null) {
                fileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgbean.getSendFileMessage().getUrl()));
                fileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgbean.getSendFileMessage().getUrl(), msgbean.getMsg_type()));
            }
            list.add(fileBean);
            UpFileUtil.getInstance().batchFileCheck(list, new CallBack<ReturnBean<List<String>>>() {
                @Override
                public void onResponse(Call<ReturnBean<List<String>>> call, Response<ReturnBean<List<String>>> response) {
                    super.onResponse(call, response);
                    if (response.body() != null && response.body().isOk()) {
                        if (response.body().getData() != null && response.body().getData().size() != list.size()) {
                            showMsgFailDialog();
                        } else {
                            if(type==1){
                                sendToFriend(msgId, fromWhere);
                            }else if(type==2){
                                EventCollectImgOrVideo eventCollectImgOrVideo = new EventCollectImgOrVideo();
                                eventCollectImgOrVideo.setMsgId(msgId);
                                EventBus.getDefault().post(eventCollectImgOrVideo);
                            }else {
                                Intent intent = new Intent(PictureExternalPreviewActivity.this, ImageShowActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("imgpath", media.getCompressPath());
                                bundle.putString("msg_id", msgId);
                                bundle.putInt("img_width", media.getWidth());
                                bundle.putInt("img_height", media.getHeight());
                                intent.putExtras(bundle);
                                startActivityForResult(intent, IMG_EDIT);
                            }
                        }

                    } else {
                        showMsgFailDialog();
                    }
                }

                @Override
                public void onFailure(Call<ReturnBean<List<String>>> call, Throwable t) {
                    super.onFailure(call, t);
                    showMsgFailDialog();
                }
            });
        } else {
            sendToFriend(msgId, fromWhere);
        }
    }

    /**
     * 转发
     *
     * @param msgId
     */
    private void sendToFriend(String msgId, int fromWhere) {
        if (fromWhere == PictureConfig.FROM_COLLECT_DETAIL) {
            if (NetUtil.isNetworkConnected()) {
                startActivity(new Intent(this, MsgForwardActivity.class)
                        .putExtra(MsgForwardActivity.AGM_JSON, collectJson).putExtra("from_collect", true));
            } else {
                ToastUtil.show("请检查网络连接是否正常");
            }
        } else {
            if (!TextUtils.isEmpty(msgId)) {
                MsgAllBean msgAllBean = msgDao.getMsgById(msgId);
                if (msgAllBean != null) {
                    startActivity(new Intent(this, MsgForwardActivity.class)
                            .putExtra(MsgForwardActivity.AGM_JSON, new Gson().toJson(msgAllBean)));
                } else {
                    ToastUtil.show("消息已被删除或者被焚毁，不能转发");
                }
            }
        }
    }

    // 进度条线程
    public class LoadDataThread extends Thread {
        private String path;
        private Object obj;
        private int type;

        public LoadDataThread(String path, int type, Object obj) {
            super();
            LogUtil.getLog().d("TAG", "------------LoadDataThread: " + obj);
            this.path = path;
            this.type = type;
            this.obj = obj;
        }

        @Override
        public void run() {
            try {
                showLoadingImage(path, type, obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // 下载图片保存至手机
    public void showLoadingImage(String urlPath, int type, Object obj) {
        try {
            LogUtil.getLog().d(TAG, "showLoadingImage: " + urlPath);
            URL u = new URL(urlPath);
//            //网路图片本地化
            String fileName = PicSaveUtils.getFileExt(urlPath);
            String path = PictureFileUtils.createDir(PictureExternalPreviewActivity.this,
                    fileName, null);
            PicSaveUtils.saveFileLocl(u, path);

            if (type == 0) {
                Message message = handler.obtainMessage();
                message.what = 200;
                message.obj = path;
                handler.sendMessage(message);
            } else if (type == 2) {//显示大图
                Message message = handler.obtainMessage();
                message.what = 400;
                message.obj = obj;
                ((View) obj).setTag(path);

                handler.sendMessage(message);
            } else if (type == 1) {
                Message message = handler.obtainMessage();
                message.what = 300;
                message.obj = path;
                handler.sendMessage(message);
            }


        } catch (IOException e) {
            // ToastManage.s(mContext, getString(com.luck.picture.lib.R.string.picture_save_error) + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /***
     * 获取文件后缀
     * @param urlPath
     * @return
     */
    private String getFileExt(String urlPath) {
        String fName = urlPath.trim();
        //http://e7-test.oss-cn-beijing.aliyuncs.com/Android/20190802/fe85b909-0bea-4155-a92a-d78052e8638c.png/below-200k
        int index = fName.lastIndexOf("/");
        if (fName.lastIndexOf(".") > index) {
            return fName.substring(index + 1);
        } else {
            String name = fName.substring(fName.lastIndexOf("/", index - 1) + 1);
            name = name.replace("/", "_");
            return name;
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    String path = (String) msg.obj;
                    // ToastManage.s(mContext, getString(com.luck.picture.lib.R.string.picture_save_success) + "\n" + path);
                    //dismissDialog();
                    saveImage(path);
                    break;
                case 300:
                    String qrPath = (String) msg.obj;
                    Result result = scanningImage(qrPath);
                    QRCodeManage.toZhifubao(PictureExternalPreviewActivity.this, result);
                    break;
            }
        }
    };

    /*
     * 更新下载进度
     * */
    public void setDownloadProgress(TextView txtBig, int progress) {
        txtBig.setText("已完成 " + progress + "%");
        if (progress == 100) {
            txtBig.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (loadDataThread != null && handler != null) {
            handler.removeCallbacks(loadDataThread);
            loadDataThread = null;
            handler = null;
        }
        finish();
        overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
    }

    @Override
    protected void onDestroy() {
        viewPager.setAdapter(null);
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMG_EDIT) {
                if (data != null) {
                    //拿到编辑后新图片的本地路径，走转发逻辑
                    String path = data.getStringExtra("showPath");
                    Bundle bundle = new Bundle();
                    bundle.putString("edit_pic_path", path);
                    Intent intent = MsgForwardActivity.newIntent(PictureExternalPreviewActivity.this, ChatEnum.EForwardMode.EDIT_PIC, bundle);
                    startActivity(intent);
                }
            }
        }
    }

    //消息LOG更新图片列表
    private void updateMessageList() {
        LogUtil.getLog().i("图片--test", "--updateMessageList");
        if (images != null) {
            int len = images.size();
            LocalMedia localMedia = images.get(len - 1);
            String msgId = localMedia.getMsg_id();
            MsgAllBean msgAllBean = msgDao.getMsgById(msgId);
            if (msgAllBean != null) {
                List<LocalMedia> temp = new ArrayList<>();
                List<MsgAllBean> listdata = new MsgAction().getMsg4UserImgNew(gid, toUid, msgAllBean.getTimestamp());
                for (int i = 0; i < listdata.size(); i++) {
                    MsgAllBean msgl = listdata.get(i);
                    LocalMedia lc = new LocalMedia();
                    lc.setCutPath(msgl.getImage().getThumbnailShow());
                    lc.setCompressPath(msgl.getImage().getPreviewShow());
                    lc.setPath(msgl.getImage().getOriginShow());
                    lc.setSize(msgl.getImage().getSize());
                    lc.setWidth(new Long(msgl.getImage().getWidth()).intValue());
                    lc.setHeight(new Long(msgl.getImage().getHeight()).intValue());
                    lc.setMsg_id(msgl.getMsg_id());
                    //发送状态正常，则允许收藏 (阅后即焚改为允许收藏)
                    if (msgl.getSend_state() != ChatEnum.ESendStatus.ERROR) {
                        lc.setCanCollect(true);
                    }
                    temp.add(lc);
                }
                int size = temp.size();
                if (size > 0) {
                    images.addAll(temp);
                    mAdapter.setCurrentData(images.get(position));
                    mAdapter.bindData(images);
                    viewPager.setAdapter(mAdapter);
                    viewPager.setCurrentItem(position);
                }
            }
        }

    }
}
