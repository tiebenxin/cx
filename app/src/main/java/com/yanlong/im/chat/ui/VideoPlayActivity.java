package com.yanlong.im.chat.ui;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.view.PopupSelectView;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.CollectVideoMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.eventbus.EventCollectImgOrVideo;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.utils.MyDiskCache;
import com.yanlong.im.utils.MyDiskCacheUtils;

import net.cb.cb.library.bean.FileBean;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

import static android.widget.RelativeLayout.CENTER_IN_PARENT;
import static com.luck.picture.lib.tools.PictureFileUtils.APP_NAME;

/**
 * @version V1.0
 * @createAuthor yangqing
 * @createDate 2019-10-16
 * @updateAuthor（Geoff）
 * @updateDate 2019-11-01
 * @description 小视频播放
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class VideoPlayActivity extends AppActivity implements View.OnClickListener, SurfaceHolder.Callback, MediaPlayer.OnVideoSizeChangedListener {
    private InputMethodManager manager;
    private SurfaceView textureView;
    private ImageView img_bg;
    private ImageView img_progress;
    private String mPath;
    private String bgUrl;
    private String msg_id;
    private String msgAllBean;
    private RelativeLayout activity_video_rel_con;
    private ImageView activity_video_img_con, activity_video_big_con, activity_video_img_close;
    private TextView activity_video_count_time, activity_video_current_time;
    private SeekBar activity_video_seek;
    private int surfaceWidth;
    private int surfaceHeight;
    private MediaPlayer mMediaPlayer = null;

    private int mHour, mMin, mSecond;
    private int mTempTime = 0;
    private int mCurrentTime = 0;
    private int mLastTime = 0;
    private Timer mTimer;
    private boolean pressHOME = false;//监测是否按了HOME键
    private int from = 0;//跳转来源 0 默认 1 猜你想要 2 收藏详情
    private boolean canCollect = false;//是否显示收藏项
    private boolean isPlayFinished = false;//是否播放完成 (播放完成禁止进度条胡乱抖动)
    private String collectJson = "";//收藏详情点击视频转发需要的数据
    private boolean showFinishDownloadToast = false;//是否显示下载完成提示
    private int downloadState = 0;//当前视频下载状态 0 无操作/下载失败 1 下载中 2 下载完成
    private VideoMessage videoMessage;
    private CommonSelectDialog dialogFour;//单选转发/收藏失效消息提示弹框
    private CommonSelectDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(R.layout.activity_video_play);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mPath = getIntent().getExtras().getString("videopath");
        msgAllBean = (String) getIntent().getExtras().get("videomsg");
        msg_id = getIntent().getExtras().getString("msg_id");
        bgUrl = getIntent().getExtras().getString("bg_url");
        canCollect = getIntent().getExtras().getBoolean("can_collect");
        collectJson = getIntent().getStringExtra(PictureConfig.COLLECT_JSON);
        if (getIntent().getExtras().getInt("from") != PictureConfig.FROM_DEFAULT) {
            from = getIntent().getExtras().getInt("from");
        }
        initView();
        initEvent();
        Animation rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_circle_rotate);
        img_progress.startAnimation(rotateAnimation);
        if (!TextUtils.isEmpty(bgUrl)) {
            Glide.with(this).load(bgUrl).into(img_bg);
        }
        if (!TextUtils.isEmpty(msgAllBean)) {
            if (from == PictureConfig.FROM_COLLECT_DETAIL) {
                CollectVideoMessage collectVideoMessage = new Gson().fromJson(msgAllBean, CollectVideoMessage.class);
                if (mPath.contains("http://")) {
                    //直接复用视频下载，由于收藏消息结构变化，CollectVideoMessage临时拼凑成VideoMessage
                    videoMessage = new VideoMessage();
                    videoMessage.setUrl(collectVideoMessage.getVideoURL());
                    videoMessage.setMsgId(collectVideoMessage.getMsgId());
                    downVideo(videoMessage);
                }
            } else {
                MsgAllBean msgAllBeanForm = new Gson().fromJson(msgAllBean, MsgAllBean.class);
                if (mPath.contains("http://")) {
                    if (msgAllBeanForm.getVideoMessage() != null) {
                        videoMessage = msgAllBeanForm.getVideoMessage();
                        downVideo(videoMessage);
                    }
                }
            }
        }
        MessageManager.getInstance().setCanStamp(false);

        builder = new CommonSelectDialog.Builder(this);
    }


    private void downVideo(final VideoMessage videoMessage) {

        final File appDir = new File(Environment.getExternalStorageDirectory() + "/" + APP_NAME + "/Mp4/");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        final String fileName = MyDiskCache.getFileNmae(videoMessage.getUrl()) + ".mp4";
        final File fileVideo = new File(appDir, fileName);

        try {
            DownloadUtil.get().downLoadFile(videoMessage.getUrl(), fileVideo, new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(File file) {
                    videoMessage.setLocalUrl(fileVideo.getAbsolutePath());
                    MsgDao dao = new MsgDao();
                    dao.fixVideoLocalUrl(videoMessage.getMsgId(), fileVideo.getAbsolutePath());
                    MyDiskCacheUtils.getInstance().putFileNmae(appDir.getAbsolutePath(), fileVideo.getAbsolutePath());
                    scanFile(getContext(), fileVideo.getAbsolutePath());
                    downloadState = 2;
                    if (showFinishDownloadToast) {
                        ToastUtil.show("保存相册成功");
                    }
                }

                @Override
                public void onDownloading(int progress) {
                    LogUtil.getLog().i("DownloadUtil", "progress:" + progress);
                    downloadState = 1;
                }

                @Override
                public void onDownloadFailed(Exception e) {
                    LogUtil.getLog().i("DownloadUtil", "Exception下载失败:" + e.getMessage());
                    downloadState = 0;
                }
            });

        } catch (Exception e) {
            LogUtil.getLog().i("DownloadUtil", "Exception:" + e.getMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stopVideoEvent(EventFactory.StopVideoEvent event) {
        if (event.msg_id.equals(msg_id)) {
            showDialog(event.name);
        }
    }

    private void showDialog(String name) {
        AlertYesNo alertYesNo = new AlertYesNo();
        alertYesNo.init(VideoPlayActivity.this, null, "\"" + name + "\"" + "撤回了一条消息",
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

    private void initEvent() {
        findViewById(R.id.rl_video_play_con).setOnClickListener(this);

        textureView.setOnClickListener(this);
        textureView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                showDownLoadDialog();
                return false;
            }
        });
        activity_video_img_con.setOnClickListener(this);
        activity_video_big_con.setOnClickListener(this);
        activity_video_img_close.setOnClickListener(this);
        activity_video_seek.setVisibility(View.INVISIBLE);
        activity_video_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress * mMediaPlayer.getDuration() / 100);
                    isPlayFinished = false;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.pause();
                activity_video_big_con.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mTimer == null) { //如果恢复播放后，直接拖动进度条，此时没有继续计时，则需要重新计时
                    try {
                        if (mMediaPlayer == null) {
                            mMediaPlayer = new MediaPlayer();
                        }
                        mMediaPlayer.reset();
                        mMediaPlayer.setDataSource(mPath);
                        mMediaPlayer.setDisplay(textureView.getHolder());
                        mMediaPlayer.setLooping(false);
                        mMediaPlayer.prepareAsync();
                        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mMediaPlayer.seekTo(mLastTime);
                                mMediaPlayer.start();
                                getProgress();
                            }
                        });
                        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError(MediaPlayer mp, int what, int extra) {

                                return false;
                            }
                        });
                    } catch (Exception e) {
                        LogUtil.getLog().d("TAG", e.getMessage());
                    }
                } else {
                    mMediaPlayer.start();
                }
                activity_video_img_con.setBackground(getDrawable(R.mipmap.video_play_con_pause));
            }
        });
//        initMediaPlay(textureView);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!isFinishing() && mMediaPlayer != null) {
                double result = 0;
                if (mCurrentTime > 0) { //TODO 抖动是因为MediaPlayer.getDuration()方法有时候会获取长度为0，属于官方bug
                    result = (double) mCurrentTime / mMediaPlayer.getDuration();
//                    LogUtil.getLog().i("TAG", "mCurrentTime:" + mCurrentTime + "  mMediaPlayer.getDuration():" + mMediaPlayer.getDuration());
                }
                if (isPlayFinished) {
                    activity_video_seek.setProgress(100);
                } else {
                    activity_video_seek.setProgress(Double.valueOf(result * 100).intValue());
                }
//                DecimalFormat df = new DecimalFormat("0.00");
//                String result = df.format((double) mCurrentTime / mMediaPlayer.getDuration());
//                if(isPlayFinished){
//                    activity_video_seek.setProgress(100);
//                }else {
//                    activity_video_seek.setProgress((int) (Double.parseDouble(result) * 100));
//                }
                mCurrentTime = mCurrentTime / 1000;
                mHour = mCurrentTime / 3600;
                mMin = mCurrentTime % 3600 / 60;
                mSecond = mCurrentTime % 60;

                if (mHour > 0) {
                    activity_video_current_time.setText(String.format(Locale.CHINESE, "%02d:%02d:%02d", mHour, mMin, mSecond));
                } else {
                    activity_video_current_time.setText(String.format(Locale.CHINESE, "%02d:%02d", mMin, mSecond));
                }
            }
        }
    };

    private void getProgress() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {

                if (null != mMediaPlayer) {
                    try {
                        // TODO OPPO等个别手机获取不到最后一秒
                        mCurrentTime = mMediaPlayer.getCurrentPosition();
                        // TODO 处理OPPO手机无法播放到最后一秒问题
//                        if (mLastTime >= mCurrentTime) {
//                            if ((mMediaPlayer.getDuration() - mCurrentTime) < 1000) {
//                                mCurrentTime = mMediaPlayer.getDuration();
//                                activity_video_seek.setProgress(1);
//                            }
//                        }
                        mLastTime = mCurrentTime;
                        if (!isFinishing()) {
                            handler.sendEmptyMessage(0);
                        }
                    } catch (Exception e) {
                        if (null != mTimer)
                            mTimer.cancel();
                    }
                }
            }
        }, 0, 300);
    }

    private void initView() {
        textureView = findViewById(R.id.textureView);
        textureView.getHolder().addCallback(this);
        activity_video_rel_con = findViewById(R.id.activity_video_rel_con);
        activity_video_img_con = findViewById(R.id.activity_video_img_con);
        activity_video_big_con = findViewById(R.id.activity_video_big_con);
        activity_video_img_close = findViewById(R.id.activity_video_img_close);
        activity_video_seek = findViewById(R.id.activity_video_seek);
        activity_video_count_time = findViewById(R.id.activity_video_count_time);
        activity_video_current_time = findViewById(R.id.activity_video_current_time);
        img_bg = findViewById(R.id.img_bg);
        img_progress = findViewById(R.id.img_progress);
        activity_video_rel_con.setVisibility(View.INVISIBLE);
    }

    private void initMediaPlay(SurfaceHolder surfaceHolder) {
        img_bg.setVisibility(View.VISIBLE);
        if (mMediaPlayer == null) {
            try {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(mPath);
                mMediaPlayer.setDisplay(surfaceHolder);
                mMediaPlayer.setLooping(false);

                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if (!isFinishing()) {
                            mMediaPlayer.start();
                            // 转成秒
                            mTempTime = mMediaPlayer.getDuration() / 1000;
                            mHour = mTempTime / 3600;
                            mMin = mTempTime % 3600 / 60;
                            mSecond = mTempTime % 60;
                            if (mHour > 0) {
                                activity_video_count_time.setText(String.format(Locale.CHINESE, "%02d:%02d:%02d", mHour, mMin, mSecond));
                            } else {
                                activity_video_count_time.setText(String.format(Locale.CHINESE, "%02d:%02d", mMin, mSecond));
                            }
                            getProgress();
                        }
                    }
                });
                mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == mp.MEDIA_INFO_VIDEO_RENDERING_START) {
                            img_progress.clearAnimation();
                            img_progress.setVisibility(View.GONE);
                            activity_video_seek.setVisibility(View.VISIBLE);
                            //隐藏缩略图
                            img_bg.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
                mMediaPlayer.prepareAsync();

                if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    surfaceWidth = textureView.getWidth();
                    surfaceHeight = textureView.getHeight();
                } else {
                    surfaceWidth = textureView.getHeight();
                    surfaceHeight = textureView.getWidth();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mMediaPlayer.pause();
                    if (!isFinishing()) {
                        activity_video_big_con.setVisibility(View.VISIBLE);
                        activity_video_img_con.setBackground(getDrawable(R.mipmap.video_play_con_play));
                        isPlayFinished = true;
                    }
                }
            });
            mMediaPlayer.setOnVideoSizeChangedListener(this);
        } else {
            if (pressHOME) {
                mMediaPlayer.seekTo(mLastTime);
                activity_video_big_con.setVisibility(View.VISIBLE);
                activity_video_img_con.setBackground(getDrawable(R.mipmap.video_play_con_play));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mMediaPlayer) {
            mMediaPlayer.pause();
            activity_video_big_con.setVisibility(View.VISIBLE);
            activity_video_img_con.setBackground(getDrawable(R.mipmap.video_play_con_play));
        }
        if (null != mTimer) {
            mTimer.cancel();
            mTimer = null;
        }
    }

//    @Override //锁屏逻辑
//    protected void onRestart() {
//        super.onRestart();
//        if(!pressHOME){
//            getProgress();
//        }
//    }

    /**
     * 单选转发/收藏失效消息提示弹框
     */
    private void showMsgFailDialog() {
        dialogFour = builder.setTitle("你所选的消息已失效")
                .setShowLeftText(false)
                .setRightText("确定")
                .setRightOnClickListener(v -> {
                    dialogFour.dismiss();
                })
                .build();
        dialogFour.show();
    }

    @Override //HOME键逻辑
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        pressHOME = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mMediaPlayer) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (null != mTimer) {
            mTimer.cancel();
            mTimer = null;
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        MessageManager.getInstance().setCanStamp(true);
    }

    @Override
    public void onClick(View v) {
        if (DoubleUtils.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.rl_video_play_con:
                activity_video_rel_con.setVisibility(View.VISIBLE);
                break;
            case R.id.textureView:
                if (activity_video_rel_con.getVisibility() == View.VISIBLE) {
                    activity_video_rel_con.setVisibility(View.INVISIBLE);
                } else {
                    activity_video_rel_con.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.activity_video_big_con:
            case R.id.activity_video_img_con:
                if (null != mMediaPlayer) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        activity_video_big_con.setVisibility(View.VISIBLE);
                        activity_video_img_con.setBackground(getDrawable(R.mipmap.video_play_con_play));
                        if (null != mTimer) {
                            mTimer.cancel();
                            mTimer = null;
                        }
                    } else {
                        if (isPlayFinished) {
                            mLastTime = 0;
                            replay();
                            isPlayFinished = false;
                        } else {
                            replay();
                        }
                        activity_video_big_con.setVisibility(View.INVISIBLE);
                        activity_video_img_con.setBackground(getDrawable(R.mipmap.video_play_con_pause));
                        pressHOME = false;
                    }
                }
                break;
            case R.id.activity_video_img_close:
                if (null != mMediaPlayer) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
                finish();
                break;
        }
    }

    /**
     * 下载图片提示
     */
    private String[] strings = new String[]{"发送给朋友", "保存视频", "取消"};
    private String[] newStrings = {"发送给朋友", "收藏", "保存视频", "取消"};


    private void showDownLoadDialog() {
        final PopupSelectView popupSelectView;
        if (canCollect) {
            popupSelectView = new PopupSelectView(VideoPlayActivity.this, newStrings);
        } else {
            popupSelectView = new PopupSelectView(VideoPlayActivity.this, strings);
        }
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                if (canCollect) {
                    if (postsion == 0) {
                        if (from == PictureConfig.FROM_COLLECT_DETAIL) {
                            checkFileIsExist(msgAllBean, collectJson,false);
                        } else {
                            checkFileIsExist(msgAllBean, "",false);
                        }
                    } else if (postsion == 1) {
                        checkFileIsExist(msgAllBean, "",true);
                    } else if (postsion == 2) {
                        insertVideoToMediaStore(getContext(), mPath, System.currentTimeMillis(), mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight(), mMediaPlayer.getDuration());
                        //点击保存视频，若已经下载完成则提示"成功"；若没有下载完成，则无操作，等待下载完成后提示"成功"
                        if (downloadState == 2) {
                            ToastUtil.show(VideoPlayActivity.this, "保存相册成功");
                        } else {
                            showFinishDownloadToast = true;
                        }
                    } else {

                    }
                } else {
                    if (postsion == 0) {
                        if (from == PictureConfig.FROM_COLLECT_DETAIL) {
                            checkFileIsExist(msgAllBean, collectJson,false);
                        } else {
                            checkFileIsExist(msgAllBean, "",false);
                        }
                    } else if (postsion == 1) {
                        insertVideoToMediaStore(getContext(), mPath, System.currentTimeMillis(), mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight(), mMediaPlayer.getDuration());
                        if (downloadState == 2) {
                            ToastUtil.show(VideoPlayActivity.this, "保存相册成功");
                        } else {
                            showFinishDownloadToast = true;
                        }
                    } else {

                    }

                }
                popupSelectView.dismiss();

            }
        });
        popupSelectView.showAtLocation(textureView, Gravity.BOTTOM, 0, 0);

    }

    //isCollect 转发还是收藏
    private void checkFileIsExist(String msgBean, String collectJson, boolean isCollect) {

        if (TextUtils.isEmpty(msgBean)) {
            ToastUtil.show("消息已被删除或者被焚毁，不能转发");
            return;
        }
        MsgAllBean msgbean = new Gson().fromJson(msgBean, MsgAllBean.class);
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
                fileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgbean.getImage().getPreview()));
            } else if (msgbean.getVideoMessage() != null) {
                FileBean itemFileBean = new FileBean();
                itemFileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgbean.getVideoMessage().getBg_url()));
                itemFileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgbean.getVideoMessage().getBg_url()));
                list.add(itemFileBean);
                fileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgbean.getVideoMessage().getUrl()));
                fileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgbean.getVideoMessage().getUrl()));
            } else if (msgbean.getSendFileMessage() != null) {
                fileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgbean.getSendFileMessage().getUrl()));
                fileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgbean.getSendFileMessage().getUrl()));
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
                            if(isCollect){
                                //收藏
                                EventCollectImgOrVideo eventCollectImgOrVideo = new EventCollectImgOrVideo();
                                MsgAllBean msgAllBeanForm = new Gson().fromJson(msgAllBean, MsgAllBean.class);
                                eventCollectImgOrVideo.setMsgId(msgAllBeanForm.getMsg_id());
                                EventBus.getDefault().post(eventCollectImgOrVideo);
                            }else {
                                onRetransmission(msgBean, collectJson);
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
            if(isCollect){
                EventCollectImgOrVideo eventCollectImgOrVideo = new EventCollectImgOrVideo();
                MsgAllBean msgAllBeanForm = new Gson().fromJson(msgAllBean, MsgAllBean.class);
                eventCollectImgOrVideo.setMsgId(msgAllBeanForm.getMsg_id());
                EventBus.getDefault().post(eventCollectImgOrVideo);
            }else {
                onRetransmission(msgBean, collectJson);
            }
        }
    }

    private void onRetransmission(String msgbean, String collectJson) {
        if (!TextUtils.isEmpty(collectJson)) {
            if (NetUtil.isNetworkConnected()) {
                context.startActivity(new Intent(context, MsgForwardActivity.class)
                        .putExtra(MsgForwardActivity.AGM_JSON, collectJson).putExtra("from_collect", true));
            } else {
                ToastUtil.show("请检查网络连接是否正常");
            }
        } else {
            startActivity(new Intent(getContext(), MsgForwardActivity.class)
                    .putExtra(MsgForwardActivity.AGM_JSON, msgbean));
        }
    }

    public void insertVideoToMediaStore(Context context, String filePath, long createTime, int width, int height, long duration) {
        if (!checkFile(filePath)) {
            if (downloadState != 1) {
                downVideo(videoMessage);
            }
            return;
        }
        createTime = getTimeWrap(createTime);
        ContentValues values = initCommonContentValues(filePath, createTime);
        values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, createTime);
        if (duration > 0)
            values.put(MediaStore.Video.VideoColumns.DURATION, duration);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (width > 0) values.put(MediaStore.Video.VideoColumns.WIDTH, width);
            if (height > 0) values.put(MediaStore.Video.VideoColumns.HEIGHT, height);
        }
        values.put(MediaStore.MediaColumns.MIME_TYPE, getVideoMimeType(filePath));
        context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

    // 检测文件存在
    private static boolean checkFile(String filePath) {
        //boolean result = FileUtil.fileIsExist(filePath);
        boolean result = false;
        File mFile = new File(filePath);
        if (mFile.exists()) {
            result = true;
        }
        LogUtil.getLog().e("TAG", "文件不存在 mPath = " + filePath);
        return result;
    }

    // 获得转化后的时间
    private static long getTimeWrap(long time) {
        if (time <= 0) {
            return System.currentTimeMillis();
        }
        return time;
    }

    // 获取video的mine_type,暂时只支持mp4,3gp
    private static String getVideoMimeType(String path) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith("mp4") || lowerPath.endsWith("mpeg4")) {
            return "video/mp4";
        } else if (lowerPath.endsWith("3gp")) {
            return "video/3gp";
        }
        return "video/mp4";
    }

    // 获取照片的mine_type
    private static String getPhotoMimeType(String path) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith("jpg") || lowerPath.endsWith("jpeg")) {
            return "image/jpeg";
        } else if (lowerPath.endsWith("png")) {
            return "image/png";
        } else if (lowerPath.endsWith("gif")) {
            return "image/gif";
        }
        return "image/jpeg";
    }

    private static ContentValues initCommonContentValues(String filePath, long time) {
        ContentValues values = new ContentValues();
        File saveFile = new File(filePath);
        long timeMillis = getTimeWrap(time);
        values.put(MediaStore.MediaColumns.TITLE, saveFile.getName());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, saveFile.getName());
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, timeMillis);
        values.put(MediaStore.MediaColumns.DATE_ADDED, timeMillis);
        values.put(MediaStore.MediaColumns.DATA, saveFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.SIZE, saveFile.length());
        return values;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initMediaPlay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeVideoSize();
    }

    public void changeVideoSize() {
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();
        //根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
        float percent;
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //竖屏模式下按视频宽度计算放大倍数值
            percent = Math.max((float) videoWidth / (float) surfaceWidth, (float) videoHeight / (float) surfaceHeight);
        } else {
            //横屏模式下按视频高度计算放大倍数值
            percent = Math.max(((float) videoWidth / (float) surfaceHeight), (float) videoHeight / (float) surfaceWidth);
        }

        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
        videoWidth = (int) Math.ceil((float) videoWidth / percent);
        videoHeight = (int) Math.ceil((float) videoHeight / percent);

        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(videoWidth, videoHeight);
        layoutParams.addRule(CENTER_IN_PARENT);
        textureView.setLayoutParams(layoutParams);
    }

//    public void changeVideoSize() {
//        int videoWidth = mMediaPlayer.getVideoWidth();
//        int videoHeight = mMediaPlayer.getVideoHeight();
//        int deviceWidth = getResources().getDisplayMetrics().widthPixels;
//        int deviceHeight = getResources().getDisplayMetrics().heightPixels;
//        LogUtil.getLog().i(VideoPlayActivity.class.getSimpleName(), "changeVideoSize--" + "deviceWidth=" + deviceWidth +
//                "--deviceHeight=" + deviceHeight + "--videoWidth=" + videoWidth + "--videoHeight=" + videoHeight);
//
//        //根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
//        float percent;
//        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//            percent = (float) deviceWidth / (float) deviceHeight; //竖屏状态下宽度小与高度,求比
//        } else {
//            percent = (float) deviceHeight / (float) deviceWidth; //横屏状态下高度小与宽度,求比
//        }
//
//        if (videoWidth > videoHeight) { //判断视频的宽大于高,那么我们就优先满足视频的宽度铺满屏幕的宽度,然后在按比例求出合适比例的高度
//            videoWidth = deviceWidth;//将视频宽度等于设备宽度,让视频的宽铺满屏幕
//            videoHeight = (int) (deviceWidth * percent);//设置了视频宽度后,在按比例算出视频高度
//        } else {  //判断视频的高大于宽,那么我们就优先满足视频的高度铺满屏幕的高度,然后在按比例求出合适比例的宽度
//            if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {//竖屏
//                videoHeight = deviceHeight;
//                /**
//                 * 接受在宽度的轻微拉伸来满足视频铺满屏幕的优化
//                 */
//                float videoPercent = (float) videoWidth / (float) videoHeight;//求视频比例 注意是宽除高 与 上面的devicePercent 保持一致
//                float differenceValue = Math.abs(videoPercent - percent);//相减求绝对值
//                LogUtil.getLog().e(VideoPlayActivity.class.getSimpleName(), "devicePercent=" + percent);
//                LogUtil.getLog().e(VideoPlayActivity.class.getSimpleName(), "videoPercent=" + videoPercent);
//                LogUtil.getLog().e(VideoPlayActivity.class.getSimpleName(), "differenceValue=" + differenceValue);
//                if (differenceValue < 0.3) { //如果小于0.3比例,那么就放弃按比例计算宽度直接使用屏幕宽度
//                    videoWidth = deviceWidth;
//                } else {
//                    videoWidth = (int) (videoWidth / percent);//注意这里是用视频宽度来除
//                }
//            } else { //横屏
//                videoHeight = deviceHeight;
//                videoWidth = (int) (deviceHeight * percent);
//            }
//        }
//        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(videoWidth, videoHeight);
//        layoutParams.addRule(CENTER_IN_PARENT);
//        textureView.setLayoutParams(layoutParams);
//    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        changeVideoSize();
    }

    //恢复播放
    private void replay() {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mPath);
            mMediaPlayer.setDisplay(textureView.getHolder());
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.seekTo(mLastTime);
                    mMediaPlayer.start();
                    getProgress();
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {

                    return true;
                }
            });
        } catch (Exception e) {
            LogUtil.getLog().d("TAG", e.getMessage());
        }
    }

    //TODO android更新媒体库这么麻烦，搞了一下午，吐了，终于实现
    public void scanFile(Context context, String filePath) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{filePath}, new String[]{"video/mp4"},
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
