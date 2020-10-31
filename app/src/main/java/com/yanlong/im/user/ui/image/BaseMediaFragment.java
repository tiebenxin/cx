package com.yanlong.im.user.ui.image;

import androidx.fragment.app.Fragment;

import net.cb.cb.library.utils.LogUtil;

/**
 * @author Liszt
 * @date 2020/9/10
 * Description
 */
public class BaseMediaFragment extends Fragment {
    private int currentPosition;
    private boolean isPressHome = false;//是否按了home键
    private boolean isAutoPlay = false;//是否自动播放


    public boolean isCurrent(int position) {
        return currentPosition == position;
    }


    public void setCurrentPosition(int position) {
        currentPosition = position;
    }

    public void setPressHome(boolean b) {
        isPressHome = b;
    }

    public boolean isPressHome() {
        LogUtil.getLog().i("video_log", "isPressHome=" + isPressHome);
        return isPressHome;
    }

    public boolean isAutoPlay() {
        return isAutoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
    }
}
