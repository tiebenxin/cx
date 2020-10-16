package com.yanlong.im.circle;

import com.yanlong.im.circle.bean.MessageFlowItemBean;

import net.cb.cb.library.base.bind.IBaseView;

import java.util.HashMap;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/9/7 0007
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public interface CircleView extends IBaseView {

    void onSuccess(MessageFlowItemBean flowItemBean);

    void uploadSuccess(String url, int type, boolean isVideo, HashMap<String, String> netFile);

    void showMessage(String message);

    void showRedDot(int redPoint);
}
