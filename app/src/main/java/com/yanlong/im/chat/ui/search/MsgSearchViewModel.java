package com.yanlong.im.chat.ui.search;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.yanlong.im.repository.MsgSearchRepository;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/5/22 0022
 * @description
 */
public class MsgSearchViewModel extends ViewModel {
    private MsgSearchRepository repository = new MsgSearchRepository();
    public LiveData<String> key = new MutableLiveData<String>();
    public void search(String key){

    }
    public void onDestory(LifecycleOwner owner) {
        key.removeObservers(owner);
        repository.onDestory();
    }
}
