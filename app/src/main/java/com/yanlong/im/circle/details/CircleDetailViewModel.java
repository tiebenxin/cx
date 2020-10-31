package com.yanlong.im.circle.details;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author Liszt
 * @date 2020/10/26
 * Description  动态详情ViewModel
 */
public class CircleDetailViewModel extends ViewModel {
    //是否触发了输入框
    public MutableLiveData<Boolean> isInputText = new MutableLiveData<>();
    //是否打开了表情面板
    public MutableLiveData<Boolean> isOpenEmoj = new MutableLiveData<>();

    public void init() {
        isInputText.setValue(false);
        isOpenEmoj.setValue(false);
    }

    /**
     * 是否有事件触发
     *
     * @return
     */
    public boolean isOpenValue() {
        return isInputText.getValue() || isOpenEmoj.getValue();

    }

}
