package com.yanlong.im.circle.details;

import com.yanlong.im.circle.bean.MessageFlowItemBean;

import net.cb.cb.library.base.bind.IBaseView;

import java.util.ArrayList;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/9/7 0007
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public interface CircleDetailsView extends IBaseView {

    void setFollowData(ArrayList<MessageFlowItemBean> list);
}
