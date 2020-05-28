package net.cb.cb.library.utils;

import android.text.TextUtils;
import android.view.View;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.R;
import net.cb.cb.library.view.MultiListView;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

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
    MultiListView listView;
    View btnView;
    private boolean showErrorMsg = true;//默认显示错误信息

    public CallBack() {
    }

    public CallBack(boolean isShowErrorMsg) {
        showErrorMsg = isShowErrorMsg;
    }

    public CallBack(MultiListView listView) {
        this.listView = listView;
    }

    public CallBack(View btnView) {
        this.btnView = btnView;
        btnView.setEnabled(false);
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (btnView != null) {
            btnView.setEnabled(true);
        }

    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (t != null) {
            LogUtil.getLog().e("==响应异常=解析异常==" + t.getMessage());
        }
        //TODO #3708 异常环境登录失败后置灰，无法再次点击登录，是因为下面捕获UnknownHostException异常直接return了，这段代码放前面即可
        if (btnView != null) {
            btnView.setEnabled(true);
        }
        if (t instanceof UnknownHostException || t instanceof ConnectException || t instanceof SocketTimeoutException) {
            return;
        }
        if (showErrorMsg && (t != null && !TextUtils.isEmpty(t.getMessage()) && !t.getMessage().equals("Canceled"))) {
            ToastUtil.show(AppConfig.APP_CONTEXT, t.getMessage());
        }

        if (listView != null) {
            listView.getLoadView().setStateNoNet(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 处理空指针问题
                    if (listView.getEvent() != null) {
                        listView.getEvent().onLoadFail();
                    }
                }
            });

        }
        if (t != null)
            t.printStackTrace();
    }

}
