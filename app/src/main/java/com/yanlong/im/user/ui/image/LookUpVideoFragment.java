package com.yanlong.im.user.ui.image;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.view.PopupSelectView;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.eventbus.EventCollectImgOrVideo;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.databinding.FragmentLookupVideoBinding;
import com.yanlong.im.utils.MyDiskCache;
import com.yanlong.im.utils.MyDiskCacheUtils;

import net.cb.cb.library.bean.FileBean;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.GsonUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

import static android.view.View.VISIBLE;
import static android.widget.RelativeLayout.CENTER_IN_PARENT;
import static com.luck.picture.lib.tools.PictureFileUtils.APP_NAME;

/**
 * @author Liszt
 * @date 2020/9/3
 * Description 查看视频fragment
 */
public class LookUpVideoFragment extends BaseMediaFragment implements TextureView.SurfaceTextureListener, MediaPlayer.OnVideoSizeChangedListener, View.OnClickListener {
    private final String TAG = "video_log--" + getClass().getSimpleName();

    private String[] strings = new String[]{"发送给朋友", "保存视频", "取消"};
    private String[] newStrings = {"发送给朋友", "收藏", "保存视频", "取消"};

    private MediaPlayer mediaPlayer;
    private Surface mSurface;
    private String path;
    private LocalMedia media;
    private int from;
    private int surfaceWidth;
    private int surfaceHeight;
    private int downloadState;
    private int mCurrentPosition = 0;//当前播放位置
    private FragmentLookupVideoBinding ui;
    private Timer mTimer;
    private int videoDuration;//视频时长
    private VideoPlayViewModel viewModel = new VideoPlayViewModel();
    private boolean canCollect = true;//是否显示收藏项
    private CommonSelectDialog dialogFour;
    private MsgDao msgDao = new MsgDao();
    private MsgAllBean msgAllBean;


    public static LookUpVideoFragment newInstance(LocalMedia media, int from) {
        LookUpVideoFragment fragment = new LookUpVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("media", media);
        bundle.putInt("from", from);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ui = DataBindingUtil.inflate(inflater, R.layout.fragment_lookup_video, container, false);
        View rootView = ui.getRoot();
        ui.textureView.setSurfaceTextureListener(this);
        initObserver();
        initData();
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ui.rlParent.setVisibility(View.INVISIBLE);
        ui.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    LogUtil.getLog().i(TAG, "onProgressChanged--progress=" + progress);
                    mCurrentPosition = progress;
                    setSeekTo(progress);
                    viewModel.isLoading.setValue(true);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                LogUtil.getLog().i(TAG, "onStartTrackingTouch");
                pausePlay();
                cancelTimer();
                updatePlayStatus(false);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (mTimer == null) {
                        if (mediaPlayer == null) {
                            mediaPlayer = new MediaPlayer();
                        }
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(path);
                        mediaPlayer.setLooping(false);
                        mediaPlayer.prepareAsync();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mediaPlayer.seekTo(mCurrentPosition);
                                mediaPlayer.start();
                                getCurrentProgress();
                            }
                        });
                    } else {
                        mediaPlayer.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        ui.textureView.setOnClickListener(this);
        ui.textureView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDownLoadDialog();
                return true;
            }
        });
        ui.ivBarPlay.setOnClickListener(this);
        ui.ivPlay.setOnClickListener(this);
        ui.ivClose.setOnClickListener(this);
        loadVideoBg();
    }

    private void loadVideoBg() {
        if (!TextUtils.isEmpty(media.getVideoBgUrl())) {
            ui.ivBg.setVisibility(View.VISIBLE);
            Glide.with(this).load(media.getVideoBgUrl()).into(ui.ivBg);
        }
    }

    @Override
    public void onPause() {
        pausePlay();
        cancelTimer();
        updatePlayStatus(false);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        updatePlayStatus(false);
        cancelTimer();
        destroyPlay();
        reset();
        viewModel.destroy();
        super.onDestroyView();
    }

    private void initData() {
        viewModel.init();
        media = getArguments().getParcelable("media");
        from = getArguments().getInt("from");
        String localUrl = media.getVideoLocalUrl();
        if (!TextUtils.isEmpty(localUrl)) {
            path = localUrl;
        } else {
            path = media.getVideoUrl();
            if (path.startsWith("http") || path.startsWith("https")) {
                downloadVideo(path, media.getMsg_id(), false);
            }
        }
        if (!TextUtils.isEmpty(media.getMsg_id())) {
            msgAllBean = msgDao.getMsgById(media.getMsg_id());
        }
    }

    private void initObserver() {
        viewModel.isPlaying.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    ui.ivPlay.setVisibility(View.INVISIBLE);
                    setPlayBarIcon(R.mipmap.video_play_con_pause);
                } else {
                    ui.ivPlay.setVisibility(View.VISIBLE);
                    setPlayBarIcon(R.mipmap.video_play_con_play);
                }
            }
        });

        viewModel.isLoading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean) {
                    ui.ivPlay.setVisibility(View.INVISIBLE);
                    Animation rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_circle_rotate);
                    ui.ivProgress.startAnimation(rotateAnimation);
                    ui.ivProgress.setVisibility(VISIBLE);
                    setPlayBarIcon(R.mipmap.video_play_con_play);
                } else {
                    ui.ivProgress.clearAnimation();
                    ui.ivProgress.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            prepareMediaPlayer();
        } else {
            pausePlay();
            cancelTimer();
            reset();
            updatePlayStatus(false);
//            setAutoPlay(false);
        }
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        prepareMediaPlayer();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void prepareMediaPlayer() {
        LogUtil.getLog().i("video_log", "prepareMediaPlayer");
        if (media == null || !isCurrent(media.getPosition()) || (viewModel.isPlaying.getValue() != null && viewModel.isPlaying.getValue())) {
            return;
        }
        LogUtil.getLog().i("video_log", "prepareMediaPlayer--w未开始");

        if (mSurface != null) {
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                    startPlay();
                    if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                        surfaceWidth = ui.textureView.getWidth();
                        surfaceHeight = ui.textureView.getHeight();
                    } else {
                        surfaceWidth = ui.textureView.getHeight();
                        surfaceHeight = ui.textureView.getWidth();
                    }
                } else {
                    if (!isPressHome() && isAutoPlay()) {
                        startPlay();
                    } else {
                        loadVideoBg();
                    }
                }
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        LogUtil.getLog().i(TAG, "onPrepared");
                        if (viewModel.isPlaying.getValue() != null && !viewModel.isPlaying.getValue() && getActivity() != null && !getActivity().isFinishing()) {
                            if (isAutoPlay()) {
                                LogUtil.getLog().i(TAG, "onPrepared--1");
                                mediaPlayer.start();
                                getCurrentProgress();
                            }
                            //初始化时长及进度条
                            videoDuration = mp.getDuration();
                            ui.seekBar.setMax(videoDuration);
                            setSeekTo(mCurrentPosition);
                            setTime(videoDuration / 1000, ui.tvEndTime);
                        }
                    }
                });
                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == mp.MEDIA_INFO_VIDEO_RENDERING_START) {
                            viewModel.isLoading.setValue(false);
                            ui.seekBar.setVisibility(View.VISIBLE);
                            //隐藏缩略图
                            ui.ivBg.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        LogUtil.getLog().i(TAG, "onCompletion");
                        if (mediaPlayer != null && getActivity() != null && !getActivity().isFinishing()) {
                            if (viewModel != null && viewModel.isPlaying != null && viewModel.isPlaying.getValue()) {
                                LogUtil.getLog().i(TAG, "onCompletion--isPlaying-终止");
                                updatePlayStatus(false);
                                mCurrentPosition = videoDuration;
                                ui.seekBar.setProgress(videoDuration);
                            }
                        }
                        pausePlay();
                        if (mTimer != null) {
                            cancelTimer();
                            reset();
                        }
                    }
                });
                mediaPlayer.setOnVideoSizeChangedListener(this);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.getLog().i(TAG, e.toString());
            }
        }
    }

    private void startPlay() throws IOException {
        if (mediaPlayer == null || mSurface == null || TextUtils.isEmpty(path)) {
            return;
        }
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置需要播放的视频
        mediaPlayer.setDataSource(path);
        LogUtil.getLog().i(TAG, "setDataSource--path=" + path);
        // 把视频画面输出到Surface
        mediaPlayer.setSurface(mSurface);
        mediaPlayer.setLooping(false);
        mediaPlayer.prepareAsync();
        mediaPlayer.seekTo(0);
        if (isAutoPlay()) {
            mediaPlayer.start();
        }
    }

    private void pausePlay() {
        LogUtil.getLog().i("video_log", "pausePlay");
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void downloadVideo(String url, String msgId, boolean showToast) {
        LogUtil.getLog().i(TAG, "downloadVideo--msgId=" + msgId + "--url=" + url);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (isCurrent(media.getPosition())) {
            viewModel.isLoading.setValue(true);
        }
        final File appDir = new File(Environment.getExternalStorageDirectory() + "/" + APP_NAME + "/Mp4/");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        final String fileName = MyDiskCache.getFileNmae(url) + ".mp4";
        final File fileVideo = new File(appDir, fileName);

        try {
            DownloadUtil.get().downLoadFile(url, fileVideo, new DownloadUtil.OnDownloadListener() {
                @Override
                public void onDownloadSuccess(File file) {
                    media.setVideoLocalUrl(fileVideo.getAbsolutePath());
                    if (!TextUtils.isEmpty(msgId)) {
                        msgDao.fixVideoLocalUrl(msgId, fileVideo.getAbsolutePath());
                    }
                    MyDiskCacheUtils.getInstance().putFileNmae(appDir.getAbsolutePath(), fileVideo.getAbsolutePath());
//                    scanFile(getContext(),fileVideo.getAbsolutePath());
                    downloadState = 2;
                    if (showToast) {
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
        if (event.msg_id.equals(media.getMsg_id())) {
            showDialog(event.name);
        }
    }

    private void showDialog(String name) {
        AlertYesNo alertYesNo = new AlertYesNo();
        alertYesNo.init(getActivity(), null, "\"" + name + "\"" + "撤回了一条消息",
                "确定", null, new AlertYesNo.Event() {
                    @Override
                    public void onON() {

                    }

                    @Override
                    public void onYes() {
                        getActivity().finish();
                    }
                });
        alertYesNo.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeVideoSize();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        changeVideoSize();
    }

    public void changeVideoSize() {
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
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
        ui.textureView.setLayoutParams(layoutParams);
    }

    private void setPlayBarIcon(int rid) {
        ui.ivBarPlay.setImageResource(rid);
    }

    @Override
    public void onClick(View v) {
        if (DoubleUtils.isFastDoubleClick()) {
            return;
        }
        if (v.getId() == ui.rlBottom.getId()) {
            ui.rlParent.setVisibility(View.VISIBLE);
        } else if (v.getId() == ui.textureView.getId() /*|| v.getId() == ui.rlParent.getId()*/) {
            if (ui.rlParent.getVisibility() == View.VISIBLE) {
                ui.rlParent.setVisibility(View.INVISIBLE);
            } else {
                ui.rlParent.setVisibility(View.VISIBLE);
            }
        } else if (v.getId() == ui.ivPlay.getId() || v.getId() == ui.ivBarPlay.getId()) {
            LogUtil.getLog().i(TAG, "点击播放按钮");
            if (null != mediaPlayer) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    cancelTimer();
                    updatePlayStatus(false);
                } else {
                    setPressHome(false);
                    if (viewModel.isPlaying.getValue() != null && !viewModel.isPlaying.getValue()) {
//                        reset();
                        replay();
                    } else {
                        replay();
                    }
                }
            }
        } else if (v.getId() == ui.ivClose.getId()) {
            destroyPlay();
            getActivity().finish();
        }
    }

    private void destroyPlay() {
        if (null != mediaPlayer) {
            LogUtil.getLog().i(TAG, "destroyPlay");
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    //恢复播放
    private void replay() {
        if (TextUtils.isEmpty(path) || mSurface == null) {
            return;
        }
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setSurface(mSurface);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    setSeekTo(mCurrentPosition);
                    mediaPlayer.start();
                    //初始化时长及进度条
                    videoDuration = mp.getDuration();
                    ui.seekBar.setMax(videoDuration);
                    setSeekTo(mCurrentPosition);
                    setTime(videoDuration / 1000, ui.tvEndTime);
                    getCurrentProgress();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {

                    return true;
                }
            });
            setSeekTo(mCurrentPosition);
            mediaPlayer.start();
        } catch (Exception e) {
            LogUtil.getLog().d("TAG", e.getMessage());
        }
    }

    private void setSeekTo(int time) {
        if (mediaPlayer == null) {
            return;
        }
        LogUtil.getLog().i(TAG, "setSeekTo--time=" + time);
        mediaPlayer.seekTo(time);
    }

    private void setTime(int time, TextView tv) {
//        if (tv.getId() == ui.tvStartTime.getId()) {
//            LogUtil.getLog().i(TAG, "time=" + time);
//        }
        int mHour = time / 3600;
        int mMin = time % 3600 / 60;
        int mSecond = time % 60;
        if (mHour > 0) {
            tv.setText(String.format(Locale.CHINESE, "%02d:%02d:%02d", mHour, mMin, mSecond));
        } else {
            tv.setText(String.format(Locale.CHINESE, "%02d:%02d", mMin, mSecond));
        }
    }

    //获取当前播放进度并更新
    private void getCurrentProgress() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    if (currentPosition > mCurrentPosition) {
                        mCurrentPosition = currentPosition;
                    }
//                    LogUtil.getLog().i(TAG, "mTimer--currentPosition=" + currentPosition /*+ "--currentProgress=" + currentProgress*/);
                    ui.tvStartTime.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (viewModel.isLoading.getValue() != null && viewModel.isLoading.getValue()) {
                                viewModel.isLoading.setValue(false);
                            }
                            if (viewModel.isPlaying.getValue() != null && viewModel.isPlaying.getValue()) {
                                if (currentPosition > 0 && currentPosition < videoDuration) {
                                    ui.seekBar.setProgress(mCurrentPosition);
                                }/* else {
                                    updatePlayStatus(false);
                                }*/
                                setTime(mCurrentPosition / 1000, ui.tvStartTime);
                            } else {
                                updatePlayStatus(true);
                            }
                        }
                    }, 1);
                } else {
                    viewModel.isPlaying.setValue(false);
                }
            }
        }, 0, 300);

    }

    //取消播放进度
    private void cancelTimer() {
        if (mTimer != null) {
            LogUtil.getLog().i(TAG, "cancelTimer");
            mTimer.cancel();
            mTimer = null;
        }
    }

    //更新播放状态
    private void updatePlayStatus(boolean b) {
        LogUtil.getLog().i(TAG, "updatePlayStatus--" + b);
        if (viewModel != null) {
            viewModel.isPlaying.setValue(b);
        }
    }

    private void reset() {
        LogUtil.getLog().i(TAG, "reset");
        mCurrentPosition = 0;
    }


    //弹出长按弹窗
    private void showDownLoadDialog() {
        if (activityIsFinish()) {
            return;
        }
        final PopupSelectView popupSelectView;
        if (canCollect) {
            popupSelectView = new PopupSelectView(getActivity(), newStrings);
        } else {
            popupSelectView = new PopupSelectView(getActivity(), strings);
        }
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                if (canCollect) {
                    if (postsion == 0) {
                        if (from == PictureConfig.FROM_COLLECT_DETAIL) {
                            checkFileIsExist(msgAllBean, media.getContent(), false);
                        } else {
                            checkFileIsExist(msgAllBean, "", false);
                        }
                    } else if (postsion == 1) {
                        checkFileIsExist(msgAllBean, "", true);
                    } else if (postsion == 2) {
                        insertVideoToMediaStore(getActivity(), path, System.currentTimeMillis(), mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight(), mediaPlayer.getDuration());
                        //点击保存视频，若已经下载完成则提示"成功"；若没有下载完成，则无操作，等待下载完成后提示"成功"
                        if (downloadState == 2) {
                            ToastUtil.show(getActivity(), "保存相册成功");
                        } else {
//                            showFinishDownloadToast = true;
                        }
                    } else {

                    }
                } else {
                    if (postsion == 0) {
                        if (from == PictureConfig.FROM_COLLECT_DETAIL) {
                            checkFileIsExist(msgAllBean, media.getContent(), false);
                        } else {
                            checkFileIsExist(msgAllBean, "", false);
                        }
                    } else if (postsion == 1) {
                        insertVideoToMediaStore(getContext(), path, System.currentTimeMillis(), mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight(), mediaPlayer.getDuration());
                        if (downloadState == 2) {
                            ToastUtil.show(getActivity(), "保存相册成功");
                        } else {
//                           boolean showFinishDownloadToast = true;
                        }
                    } else {

                    }

                }
                popupSelectView.dismiss();

            }
        });
        popupSelectView.showAtLocation(ui.textureView, Gravity.BOTTOM, 0, 0);

    }


    //isCollect 转发还是收藏
    private void checkFileIsExist(MsgAllBean msgBean, String collectJson, boolean isCollect) {
        if (msgBean == null) {
            ToastUtil.show("消息已被删除或者被焚毁，不能转发");
            return;
        }
        if (msgBean.getMsg_type() == ChatEnum.EMessageType.IMAGE || msgBean.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO
                || msgBean.getMsg_type() == ChatEnum.EMessageType.FILE) {
            ArrayList<FileBean> list = new ArrayList<>();
            FileBean fileBean = new FileBean();
            if (msgBean.getImage() != null) {
                fileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgBean.getImage().getPreview()));
                fileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgBean.getImage().getPreview(), msgBean.getMsg_type()));
            } else if (msgBean.getVideoMessage() != null) {
                FileBean itemFileBean = new FileBean();
                itemFileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgBean.getVideoMessage().getBg_url()));
                itemFileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgBean.getVideoMessage().getBg_url(), ChatEnum.EMessageType.IMAGE));
                list.add(itemFileBean);
                fileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgBean.getVideoMessage().getUrl()));
                fileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgBean.getVideoMessage().getUrl(), msgBean.getMsg_type()));
            } else if (msgBean.getSendFileMessage() != null) {
                fileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgBean.getSendFileMessage().getUrl()));
                fileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgBean.getSendFileMessage().getUrl(), msgBean.getMsg_type()));
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
                            if (isCollect) {
                                //收藏
                                EventCollectImgOrVideo eventCollectImgOrVideo = new EventCollectImgOrVideo();
                                eventCollectImgOrVideo.setMsgId(media.getMsg_id());
                                EventBus.getDefault().post(eventCollectImgOrVideo);
                            } else {
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
            if (isCollect) {
                EventCollectImgOrVideo eventCollectImgOrVideo = new EventCollectImgOrVideo();
                eventCollectImgOrVideo.setMsgId(media.getMsg_id());
                EventBus.getDefault().post(eventCollectImgOrVideo);
            } else {
                onRetransmission(msgBean, collectJson);
            }
        }
    }

    public void insertVideoToMediaStore(Context context, String filePath, long createTime, int width, int height, long duration) {
        if (!checkFile(filePath)) {
            if (downloadState != 1) {
                downloadVideo(media.getMsg_id(), path, true);
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

    private void onRetransmission(MsgAllBean msgBean, String collectJson) {
        if (!TextUtils.isEmpty(collectJson)) {
            if (NetUtil.isNetworkConnected()) {
                getActivity().startActivity(new Intent(getActivity(), MsgForwardActivity.class).putExtra(MsgForwardActivity.AGM_JSON, collectJson).putExtra("from_collect", true));
            } else {
                ToastUtil.show("请检查网络连接是否正常");
            }
        } else {
            startActivity(new Intent(getContext(), MsgForwardActivity.class)
                    .putExtra(MsgForwardActivity.AGM_JSON, GsonUtils.optObject(msgBean)));
        }
    }

    /**
     * 单选转发/收藏失效消息提示弹框
     */
    private void showMsgFailDialog() {
        CommonSelectDialog.Builder builder = new CommonSelectDialog.Builder(getActivity());
        dialogFour = builder.setTitle("你所选的消息已失效")
                .setShowLeftText(false)
                .setRightText("确定")
                .setRightOnClickListener(v -> {
                    dialogFour.dismiss();
                })
                .build();
        dialogFour.show();
    }

    private boolean activityIsFinish() {
        if (getActivity() == null || getActivity().isDestroyed() || getActivity().isFinishing()) {
            return true;
        }
        return false;
    }
}
