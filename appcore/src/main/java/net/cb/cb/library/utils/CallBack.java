package net.cb.cb.library.utils;

import android.view.View;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.R;
import net.cb.cb.library.view.MultiListView;

import retrofit2.Call;
import retrofit2.Callback;

/***
 * 统一处理CallBack的错误处理
 *
 * @author jyj
 * @date 2016/12/23
 */
public abstract class CallBack<T> implements Callback<T> {
    MultiListView listView;

    public CallBack(){
    }

    public CallBack(MultiListView listView) {
        this.listView = listView;
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {

        if(listView==null) {
            ToastUtil.show(AppConfig.APP_CONTEXT, R.string.app_link_err);
        }else{
            listView.getLoadView().setStateNoNet(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listView.getEvent().onLoadFail();
                }
            });

        }

        if (t != null)
            t.printStackTrace();
    }

}
