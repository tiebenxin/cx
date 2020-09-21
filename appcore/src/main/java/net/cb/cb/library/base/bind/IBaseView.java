package net.cb.cb.library.base.bind;

import android.app.Activity;
import android.content.Intent;

import com.alibaba.android.arouter.facade.Postcard;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/9/7
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public interface IBaseView {

    Postcard getPostcard(String path);

    void toActivity(String path);

    void toActivity(String path, boolean doFinish);

    void toActivity(Postcard postcard, boolean doFinish);

    void toActivityWithCallback(Activity activity, String path, int requestCode);

    void toActivityWithCallback(Activity activity, Postcard postcard, int requestCode);

    void setResultFinish(int resultCode);

    void setResultFinish(Intent intent, int resultCode);

    void delayTime(Runnable runnable, long delayMillis);
}
