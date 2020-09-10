package com.yanlong.im.user.ui.image;

import android.support.v4.app.Fragment;

/**
 * @author Liszt
 * @date 2020/9/10
 * Description
 */
public class BaseMediaFragment extends Fragment {
    private int currentPosition;


    public boolean isCurrent(int position) {
        return currentPosition == position;
    }


    public void setCurrentPosition(int position) {
        currentPosition = position;
    }
}
