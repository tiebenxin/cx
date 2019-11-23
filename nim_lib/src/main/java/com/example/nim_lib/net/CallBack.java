package com.example.nim_lib.net;

import android.view.View;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * 统一处理CallBack的错误处理
 *
 * @author jyj
 * @date 2016/12/23
 */
public abstract class CallBack<T> implements Callback<T> {
    View btnView;

    public CallBack(){
    }

    public CallBack(View btnView) {
        this.btnView = btnView;
        btnView.setEnabled(false);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(btnView!=null){
            btnView.setEnabled(true);
        }

    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {

        if(btnView!=null){
            btnView.setEnabled(true);
        }

        if (t != null)
            t.printStackTrace();
    }

}
