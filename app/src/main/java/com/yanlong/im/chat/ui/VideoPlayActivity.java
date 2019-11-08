package com.yanlong.im.chat.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luck.picture.lib.view.PopupSelectView;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.utils.MyDiskCache;
import com.yanlong.im.utils.MyDiskCacheUtils;

import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.AppActivity;

import java.io.File;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.RelativeLayout.CENTER_IN_PARENT;

public class VideoPlayActivity extends AppActivity implements View.OnClickListener, SurfaceHolder.Callback,MediaPlayer.OnVideoSizeChangedListener {
    private InputMethodManager manager;
    private SurfaceView textureView;
    private SurfaceTexture surfaceTexture;
    private String path;
    private String msgAllBean;
    private RelativeLayout activity_video_rel_con;
    private ImageView activity_video_img_con,activity_video_big_con,activity_video_img_close;
    private TextView activity_video_count_time,activity_video_current_time;
    private SeekBar activity_video_seek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(R.layout.activity_video_play);

        path=getIntent().getExtras().getString("videopath");
        msgAllBean=(String) getIntent().getExtras().get("videomsg");
        initView();
        initEvent();
    }

    private void downVideo(final MsgAllBean msgAllBean, final VideoMessage videoMessage) {

        final File appDir = new File(getExternalCacheDir().getAbsolutePath() + "/Mp4/");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        final String fileName = MyDiskCache.getFileNmae(msgAllBean.getVideoMessage().getUrl()) + ".mp4";
        final File fileVideo = new File(appDir, fileName);
//        videoMessage.setLocalUrl(fileVideo.getAbsolutePath());
        new Thread() {
            @Override
            public void run() {
                try {

                    DownloadUtil.get().download(msgAllBean.getVideoMessage().getUrl(), appDir.getAbsolutePath(), fileName, new DownloadUtil.OnDownloadListener() {
                        @Override
                        public void onDownloadSuccess(File file) {
//                            Intent intent = new Intent(VideoPlayActivity.this, VideoPlayActivity.class);
//                            intent.putExtra("videopath", fileVideo.getAbsolutePath());
//                            Message message = new Message();
//                            Bundle bundle = new Bundle();
//                            bundle.putString("msgid", msgAllBean.getVideoMessage().getMsgId());
//                            bundle.putString("url", fileVideo.getAbsolutePath());
//                            message.setData(bundle);
//                            handler.sendMessage(message);
                            videoMessage.setLocalUrl(fileVideo.getAbsolutePath());
                            MsgDao dao = new MsgDao();
                            dao.fixVideoLocalUrl(msgAllBean.getVideoMessage().getMsgId(),fileVideo.getAbsolutePath());
//                        msgAllBean.setVideoMessage(videoMessage);
//                        MsgAllBean imgMsgBean = SocketData.sendFileUploadMessagePre(reMsg.getMsg_id(), toUId, toGid, reMsg.getTimestamp(), image, ChatEnum.EMessageType.IMAGE);
//                        VideoMessage videoMessageSD = SocketData.createVideoMessage(imgMsgId, "file://" + file, videoMessage.getBg_url(),false,videoMessage.getDuration(),videoMessage.getWidth(),videoMessage.getHeight(),file);
//                            startActivity(intent);
                            MyDiskCacheUtils.getInstance().putFileNmae(appDir.getAbsolutePath(), fileVideo.getAbsolutePath());
                        }

                        @Override
                        public void onDownloading(int progress) {

                        }

                        @Override
                        public void onDownloadFailed(Exception e) {

                        }
                    });

                } catch (Exception e) {

                }

            }
        }.start();

    }



    private void initEvent() {
        findViewById(R.id.rl_video_play_con).setOnClickListener(this);
//        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
//            @Override
//            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//                surfaceTexture = surface;
//                initMediaPlay(surface);
//            }
//            @Override
//            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//
//            }
//            @Override
//            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//                return false;
//            }
//            @Override
//            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//
//            }
//        });
//        textureView.set
        textureView.setOnClickListener(this);
        textureView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDownLoadDialog();
                return false;
            }
        });
//        textureView.setOnClickListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (activity_video_rel_con.getVisibility()==View.VISIBLE){
//                    activity_video_rel_con.setVisibility(View.INVISIBLE);
//                }else{
//                    activity_video_rel_con.setVisibility(View.VISIBLE);
//                }
//                return true;
//            }
//        });
        activity_video_img_con.setOnClickListener(this);
        activity_video_big_con.setOnClickListener(this);
        activity_video_img_close.setOnClickListener(this);
        activity_video_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mMediaPlayer.seekTo(progress*mMediaPlayer.getDuration()/100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMediaPlayer.start();
            }
        });
//        initMediaPlay(textureView);
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            NumberFormat numberFormat = NumberFormat.getPercentInstance();
            numberFormat.setMinimumFractionDigits(0);
            DecimalFormat df=new DecimalFormat("0.00");
            String result= df.format((double) currentTime/mMediaPlayer.getDuration());
            activity_video_seek.setProgress((int)(Double.parseDouble(result)*100));
            if (currentTime/1000<10){
                activity_video_current_time.setText("00:0"+currentTime/1000);
            }else{
                activity_video_current_time.setText("00:"+currentTime/1000);
            }
        }
    };
    private int currentTime=0;
    private  Timer timer;
    private void getProgress() {

        timer= new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                if (null!=mMediaPlayer){
                    try {
                        currentTime = mMediaPlayer.getCurrentPosition();
                        handler.sendEmptyMessage(419);
                    }catch (Exception e){
                        if (null!=timer)
                        timer.cancel();
                    }
                }
//                activity_video_seek.setProgress(p);
            }
        }, 0, 1000);
    }
    private void initView() {
        textureView=findViewById(R.id.textureView);
        textureView.getHolder().addCallback(this);
        activity_video_rel_con=findViewById(R.id.activity_video_rel_con);
        activity_video_img_con=findViewById(R.id.activity_video_img_con);
        activity_video_big_con=findViewById(R.id.activity_video_big_con);
        activity_video_img_close=findViewById(R.id.activity_video_img_close);
        activity_video_seek=findViewById(R.id.activity_video_seek);
        activity_video_count_time=findViewById(R.id.activity_video_count_time);
        activity_video_current_time=findViewById(R.id.activity_video_current_time);

        activity_video_rel_con.setVisibility(View.INVISIBLE);
    }
    private int surfaceWidth;
    private int surfaceHeight;
    private MediaPlayer mMediaPlayer;
//    private void initMediaPlay(SurfaceTexture surface){
    private void initMediaPlay(SurfaceHolder surfaceHolder){

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(path);
//            mMediaPlayer.setSurface(new Surface(surface));
            mMediaPlayer.setDisplay(surfaceHolder);
//            mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            mMediaPlayer.setLooping(false);

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
//                    int duration=mMediaPlayer.getDuration();
//                    if (duration<=0){
//                        ToastUtil.show(getApplicationContext(),"文件损坏，请退出重新点击进入");
//                        File file=new File(path);
//                        if (null!=file&&file.exists()){
//                            file.delete();
//                        }
//                        finish();
//                    }
                    MsgAllBean msgAllBeanForm= new Gson().fromJson(msgAllBean,MsgAllBean.class);
                    if (path.contains("http://")){
                        downVideo(msgAllBeanForm,msgAllBeanForm.getVideoMessage());
                    }
//                    changeVideoSize();
                    if (mMediaPlayer.getDuration()/1000<10){
                        activity_video_count_time.setText("00:0"+(mMediaPlayer.getDuration()/1000)+"");
                    }else{
                        activity_video_count_time.setText("00:"+(mMediaPlayer.getDuration()/1000)+"");
                    }

                    getProgress();
                }
            });
            mMediaPlayer.prepareAsync();

            if(getResources().getConfiguration().orientation== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                surfaceWidth=textureView.getWidth();
                surfaceHeight=textureView.getHeight();
            }else {
                surfaceWidth=textureView.getHeight();
                surfaceHeight=textureView.getWidth();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.pause();
                activity_video_big_con.setVisibility(View.VISIBLE);
                activity_video_img_con.setBackground(getDrawable(R.mipmap.video_play_con_play));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null!=mMediaPlayer){
            mMediaPlayer.pause();
            activity_video_img_con.setBackground(getDrawable(R.mipmap.video_play_con_play));
        }
        if (null!=timer){
            timer.cancel();
            timer=null;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        if (null!=mMediaPlayer){
//            mMediaPlayer.start();
//        }
//        if (null!=timer){
//            timer.purge();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null!=mMediaPlayer){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
        if (null!=timer){
            timer.cancel();
            timer=null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_video_play_con:
                activity_video_rel_con.setVisibility(View.VISIBLE);
                break;
            case R.id.textureView:
                if (activity_video_rel_con.getVisibility()==View.VISIBLE){
                    activity_video_rel_con.setVisibility(View.INVISIBLE);
                }else{
                    activity_video_rel_con.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.activity_video_big_con:
                if (null!=mMediaPlayer){
                    if (mMediaPlayer.isPlaying()){
                        mMediaPlayer.pause();
                    }else{
                        mMediaPlayer.start();
                        activity_video_big_con.setVisibility(View.INVISIBLE);
                    }
                    activity_video_img_con.setBackground(getDrawable(R.mipmap.video_play_con_pause));
                }
                break;
            case R.id.activity_video_img_con:
                if (null!=mMediaPlayer){
                    if (mMediaPlayer.isPlaying()){
                        mMediaPlayer.pause();
                        activity_video_big_con.setVisibility(View.VISIBLE);
                        activity_video_img_con.setBackground(getDrawable(R.mipmap.video_play_con_play));
                    }else{
                        mMediaPlayer.start();
                        activity_video_big_con.setVisibility(View.INVISIBLE);
                        activity_video_img_con.setBackground(getDrawable(R.mipmap.video_play_con_pause));
                        getProgress();
                    }
                }
                break;
            case R.id.activity_video_img_close:
                if (null!=mMediaPlayer){
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer=null;
                }
                finish();
                break;
        }
    }

    /**
     * 下载图片提示
     */
    private String[] strings=new String[]{"发送给朋友","保存视频","取消"};
    private void showDownLoadDialog() {
        final PopupSelectView popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                if (postsion == 0) {
                    onRetransmission(msgAllBean);
                } else if (postsion == 1) {
                    insertVideoToMediaStore(getContext(),path,System.currentTimeMillis(),mMediaPlayer.getVideoWidth(),mMediaPlayer.getVideoHeight(),mMediaPlayer.getDuration());
                    ToastUtil.show(VideoPlayActivity.this,"保存成功");
                }else{

                }
                popupSelectView.dismiss();

            }
        });
        popupSelectView.showAtLocation(textureView, Gravity.BOTTOM, 0, 0);

    }
    private void onRetransmission(String msgbean) {
        startActivity(new Intent(getContext(), MsgForwardActivity.class)
                .putExtra(MsgForwardActivity.AGM_JSON, msgbean));
    }

    public static void insertVideoToMediaStore(Context context, String filePath, long createTime, int width, int height, long duration) {
        if (!checkFile(filePath))
            return;
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
        if (mFile.exists()){
            result = true;
        }
        Log.e("TAG", "文件不存在 path = " + filePath);
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

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

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
        float max;
        if (getResources().getConfiguration().orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //竖屏模式下按视频宽度计算放大倍数值
            max = Math.max((float) videoWidth / (float) surfaceWidth,(float) videoHeight / (float) surfaceHeight);
        } else{
            //横屏模式下按视频高度计算放大倍数值
            max = Math.max(((float) videoWidth/(float) surfaceHeight),(float) videoHeight/(float) surfaceWidth);
        }

        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
        videoWidth = (int) Math.ceil((float) videoWidth / max);
        videoHeight = (int) Math.ceil((float) videoHeight / max);

        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
        RelativeLayout.LayoutParams layoutParams= new RelativeLayout.LayoutParams(videoWidth, videoHeight);
        layoutParams.addRule(CENTER_IN_PARENT);
        textureView.setLayoutParams(layoutParams);
//        textureView.set
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        changeVideoSize();
    }
}
