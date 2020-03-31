package com.hm.cxpay.net;

import android.util.Log;

import com.hm.cxpay.rx.data.BaseResponse;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.TouchUtil;

import okhttp3.ResponseBody;

/**
 * date on 2018/1/12
 * author Liszt
 * describe
 */

public abstract class FGObserver<T> extends BaseObserver<T> {

    private static final String TAG = "FGObserver";

    public FGObserver() {
        super();
    }

    public FGObserver(boolean isShowProgress) {
        super(isShowProgress);
    }

    @Override
    public void onNext(T response) {
        if (response instanceof BaseResponse) {
            BaseResponse res = (BaseResponse) response;
            if (Code.OK == res.getCode()) {
                onHandleSuccess(response);
            } else {
                onHandleError(response);
            }
        } else if (response instanceof ResponseBody) {
            onHandleSuccess(response);
        }else if (response instanceof Exception){
            onHandleError(response);
        }
    }

    public abstract void onHandleSuccess(T t);

    public void onHandleError(T t) {
        if (t instanceof BaseResponse) {
            Log.e(TAG, ((BaseResponse) t).getMessage());
            ToastUtil.show(((BaseResponse) t).getMessage());
        }
    }

}
