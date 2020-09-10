package com.yanlong.im.user.ui.image;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * @author Liszt
 * @date 2020/9/10
 * Description
 */
public class VideoPlayViewModel extends ViewModel {
    public MutableLiveData<Boolean> isPlaying = new MutableLiveData<>();

}
