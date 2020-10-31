package com.yanlong.im.user.ui.image;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author Liszt
 * @date 2020/9/10
 * Description
 */
public class VideoPlayViewModel extends ViewModel {
    public MutableLiveData<Boolean> isPlaying = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public void init(){
        isPlaying.setValue(false);
        isLoading.setValue(false);
    }

    public void destroy(){
        isPlaying.setValue(null);
        isLoading.setValue(null);
    }

}
