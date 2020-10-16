package com.yanlong.im.circle;

import com.yanlong.im.circle.bean.MessageInfoBean;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;

import java.util.WeakHashMap;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/9/7 0007
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public interface CircleApi {

    void createNewCircle(WeakHashMap<String, Object> params, CallBack<ReturnBean<MessageInfoBean>> callback);

    void latestData(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback);
}
