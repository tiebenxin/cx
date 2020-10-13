package com.yanlong.im.circle.recommend;

import com.yanlong.im.circle.bean.MessageInfoBean;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;

import java.util.List;
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
public interface RecommendApi {

    void getRecommendList(WeakHashMap<String, Object> params, CallBack<ReturnBean<List<MessageInfoBean>>> callback);

    void comentLike(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback);

    void comentCancleLike(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback);

    void followAdd(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback);

    void followCancle(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback);

    void addSee(WeakHashMap<String, Object> params, CallBack<ReturnBean> callback);

    void queryById(WeakHashMap<String, Object> params, CallBack<ReturnBean<MessageInfoBean>> callback);
}
