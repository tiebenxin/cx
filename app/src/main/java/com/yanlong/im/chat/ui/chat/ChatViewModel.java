package com.yanlong.im.chat.ui.chat;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class ChatViewModel extends ViewModel {
    //是否触发了输入框
    public MutableLiveData<Boolean> isInputText = new MutableLiveData<>();
    //是否打开了表情面板
    public MutableLiveData<Boolean> isOpenEmoj = new MutableLiveData<>();
    //是否打开了功能面板
    public MutableLiveData<Boolean> isOpenFuction = new MutableLiveData<>();
    //是否打开了点击说话
    public MutableLiveData<Boolean> isOpenSpeak = new MutableLiveData<>();
    public ChatViewModel(){
        //主线程使用setValue,子线程使用postValue赋值
        isInputText.setValue(false);
        isOpenEmoj.setValue(false);
        isOpenFuction.setValue(false);
        isOpenSpeak.setValue(false);
    }

    /**
     * 重置其他状态值
     * 1.data＝null，所有状态值重置为false
     * 2.data不为null,data本身不重置
     * @param data
     */
    public void recoveryOtherValue(MutableLiveData<Boolean> data){
        recoveryValue(data,isInputText);
        recoveryValue(data,isOpenEmoj);
        recoveryValue(data,isOpenFuction);
        recoveryValue(data,isOpenSpeak);
    }
    private void recoveryValue(MutableLiveData<Boolean> data,MutableLiveData<Boolean> orignalData){
        if((data==null||data!=orignalData)&&orignalData.getValue()){
            orignalData.setValue(false);
        }
    }

    /**
     * 是否有事件触发
     * @return
     */
    public boolean isOpenValue(){
        return isInputText.getValue()||isOpenEmoj.getValue()||
                isOpenFuction.getValue()||isOpenSpeak.getValue();

    }
}
