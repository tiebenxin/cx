package com.yanlong.im.chat.ui;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yanlong.im.R;

import net.cb.cb.library.view.AppActivity;

public class VideoPlayActivity extends AppActivity implements View.OnClickListener {
    private InputMethodManager manager;
    private  TextureView textureView;
    private SurfaceTexture surfaceTexture;
    private String path;
    private RelativeLayout activity_video_rel_con;
    private ImageView activity_video_img_con,activity_video_big_con,activity_video_img_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(R.layout.activity_video_play);

        path=getIntent().getExtras().getString("videopath");
        initView();
        initEvent();
    }

    private void initEvent() {
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                surfaceTexture = surface;
                initMediaPlay(surface);
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
        });
        textureView.setOnClickListener(this);
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
    }

    private void initView() {
        textureView=findViewById(R.id.textureView);
        activity_video_rel_con=findViewById(R.id.activity_video_rel_con);
        activity_video_img_con=findViewById(R.id.activity_video_img_con);
        activity_video_big_con=findViewById(R.id.activity_video_big_con);
        activity_video_img_close=findViewById(R.id.activity_video_img_close);
    }

    private MediaPlayer mMediaPlayer;
    private void initMediaPlay(SurfaceTexture surface){

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setSurface(new Surface(surface));
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.prepareAsync();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null!=mMediaPlayer){
            mMediaPlayer.pause();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (null!=mMediaPlayer){
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null!=mMediaPlayer){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
                    }
                }
                break;
            case R.id.activity_video_img_con:
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
}
