package com.yanlong.im.circle.follow;

import android.content.Context;

import com.yanlong.im.circle.adapter.CircleFlowAdapter;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.bean.MessageFlowItemBean;

import net.cb.cb.library.base.bind.BasePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/9/7 0007
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class FollowPresenter extends BasePresenter<FollowModel, FollowView> {

    FollowPresenter(Context context) {
        super(context);
    }

    @Override
    public FollowModel bindModel() {
        return new FollowModel();
    }

    public void getFollowData() {
        List<MessageFlowItemBean> flowList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MessageInfoBean messageInfoBean = new MessageInfoBean();
            if (i == 0) {
                messageInfoBean.setContent("这是一条测试数据请不要理会哈保促使哈哈哈哈侃侃这是一条测试数据请不要理会哈保促使哈哈哈哈侃侃这是一条测试数据请不要理会哈保促使哈哈哈哈侃侃这是一条测试数据请不要理会哈保促使哈哈哈哈侃侃这是一条测试数据请不要理会哈保促使哈哈哈哈侃侃这是一条测试数据请不要理会哈保促使哈哈哈哈侃侃这是一条测试数据请不要理会哈保促使哈哈哈哈侃侃1212中");
            } else if (i == 1) {
                messageInfoBean.setContent("是一条测试数据请不要理会哈保促使哈哈哈哈侃");
            } else {
                messageInfoBean.setContent("是一条测试数据请不要理会哈保促使哈哈哈哈侃侃这是一条测试数据请不要理会哈保促使哈哈哈哈侃侃");
            }
            MessageFlowItemBean messageFlowItemBean = new MessageFlowItemBean(CircleFlowAdapter.MESSAGE_DEFAULT, messageInfoBean);
            flowList.add(messageFlowItemBean);
        }
        mView.setFollowData(flowList);
    }

}
