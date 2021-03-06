package com.hm.cxpay.net;

import android.util.Log;


import com.hm.cxpay.rx.data.BaseResponse;

import net.cb.cb.library.net.NetWorkUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.util.EndConsumerHelper;


/**
 * date on 2017/12/25
 * author ll147996
 * describe
 */

public abstract class BaseObserver<T> implements Observer<T> {

    private static final String TAG = "BaseObserver";

    private Disposable s;


    private NetworkRequestListener listener;
    private boolean isShowProgress;

    public BaseObserver<T> setListener(NetworkRequestListener listener) {
        this.listener = listener;
        return this;
    }

    public BaseObserver() {
        //默认显示Progress
        this(true);

    }

    public BaseObserver(boolean isShowProgress) {
        this.isShowProgress = isShowProgress;
    }


    @Override
    public final void onSubscribe(@NonNull Disposable s) {
        if (EndConsumerHelper.validate(this.s, s, getClass())) {
            this.s = s;
            onStart();
        }
    }

    protected void onStart() {
        if (listener != null) {
            if (!NetWorkUtils.isNetworkConnected()) {
                listener.noNetwork();
            } else {
                listener.start(isShowProgress);
            }
        }
    }


    @Override
    public void onError(Throwable e) {
        String message = e.getMessage();
        if (e instanceof SocketTimeoutException) {
            Log.e(TAG, "请求超时，请检查你的网络状态");
            message = "请求超时";
            if (listener != null) {
                listener.interruptedNetwork();
            }
        } else if (e instanceof ConnectException) {
            Log.e(TAG, "网络中断，请检查你的网络状态");
            message = "网络连接不可用，请稍后重试";
            if (listener != null) {
                listener.interruptedNetwork();
            }
        }else if (e instanceof UnknownHostException) {
            Log.e(TAG, "网络连接不可用，请稍后重试");
            message = "网络连接不可用，请稍后重试";
            if (listener != null) {
                listener.interruptedNetwork();
            }
        } else {
            Log.e(TAG, e.getMessage());
        }
        if (listener != null) {
            listener.end();
        }
        BaseResponse response = new BaseResponse();
        response.setMessage(message);
        response.setCode(-1);
        onNext((T) response);
    }

    @Override
    public void onComplete() {
        if (listener != null) {
            listener.end();
        }
    }

    public final void cancel() {
        Disposable s = this.s;
        this.s = DisposableHelper.DISPOSED;
        s.dispose();
        if (listener != null) {
            listener.end();
        }
    }


}

