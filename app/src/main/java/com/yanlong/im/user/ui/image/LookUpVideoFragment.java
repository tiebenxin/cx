package com.yanlong.im.user.ui.image;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.utils.MyDiskCache;
import com.yanlong.im.utils.MyDiskCacheUtils;

import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.view.AlertYesNo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import static com.luck.picture.lib.tools.PictureFileUtils.APP_NAME;

/**
 * @author Liszt
 * @date 2020/9/3
 * Description 查看视频fragment
 */
public class LookUpVideoFragment extends Fragment implements TextureView.SurfaceTextureListener {
    private TextureView textureView;
    private ImageView ivBg;
    private ImageView ivPlay;
    private ImageView ivProgress;
    private int downloadState;
    private MediaPlayer mediaPlayer;
    public static boolean isViewPagerSelected = false;
    private Surface mSurface;
    private String path;
    private String msgId;
    private ImageView ivClose;
    private RelativeLayout rlParent;
    private RelativeLayout rlPlayBar;
    private ImageView ivBarPlay;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private SeekBar seekBar;

    public static LookUpVideoFragment newInstance(LocalMedia media,boolean isCurrent) {
        LookUpVideoFragment fragment = new LookUpVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("media", media);
        bundle.putBoolean("isCurrent",isCurrent);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lookup_video,container,false);
        textureView = rootView.findViewById(R.id.texture_view);
        ivBg = rootView.findViewById(R.id.iv_bg);
        ivPlay = rootView.findViewById(R.id.iv_play);
        ivProgress = rootView.findViewById(R.id.iv_progress);
        rlParent = rootView.findViewById(R.id.rl_parent);
        ivClose = rootView.findViewById(R.id.iv_close);
        rlPlayBar = rootView.findViewById(R.id.rl_video_play_con);
        ivBarPlay = rootView.findViewById(R.id.iv_bar_play);
        tvStartTime = rootView.findViewById(R.id.tv_start_time);
        tvEndTime = rootView.findViewById(R.id.tv_start_time);
        seekBar = rootView.findViewById(R.id.seek_bar);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            prepare();
        } else {
            toPauseMethod();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        prepare();
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

    private void prepare() {
        if (isViewPagerSelected && mSurface != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                // 设置需要播放的视频
                mediaPlayer.setDataSource(path);
                //Log.e("ttt--需播放的视频-",path);
                // 把视频画面输出到Surface
                mediaPlayer.setSurface(mSurface);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            } catch (Exception e) {
            }
        }
    }

    public void toPauseMethod() {
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




    private void downVideo(final VideoMessage videoMessage) {
        final File appDir = new File(Environment.getExternalStorageDirectory()+"/"+APP_NAME + "/Mp4/");
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
//                    scanFile(getContext(),fileVideo.getAbsolutePath());
//                    downloadState = 2;
//                    if(showFinishDownloadToast){
//                        ToastUtil.show("保存相册成功");
//                    }
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
        if (event.msg_id.equals(msgId)) {
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
}
