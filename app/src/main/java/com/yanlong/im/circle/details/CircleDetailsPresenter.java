package com.yanlong.im.circle.details;

import android.content.Context;

import com.yanlong.im.circle.adapter.CircleFlowAdapter;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;

import net.cb.cb.library.base.bind.BasePresenter;

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
public class CircleDetailsPresenter extends BasePresenter<CircleDetailsModel, CircleDetailsView> {

    private ArrayList<MessageFlowItemBean> mFlowList = new ArrayList<>();

    CircleDetailsPresenter(Context context) {
        super(context);
    }

    @Override
    public CircleDetailsModel bindModel() {
        return new CircleDetailsModel();
    }

    public void getFollowData() {
        for (int i = 0; i < 1; i++) {
            MessageInfoBean messageInfoBean = new MessageInfoBean();
            messageInfoBean.setContent("呀咔咔咔勤苦呀咔咔咔勤苦呀咔咔咔勤苦呀咔咔咔勤苦呀咔咔咔勤苦呀咔咔咔勤苦呀咔咔咔勤苦呀咔咔咔勤苦呀咔咔咔勤苦呀咔咔咔勤苦呀咔咔咔勤苦");
            MessageFlowItemBean messageFlowItemBean = new MessageFlowItemBean(CircleFlowAdapter.MESSAGE_DEFAULT, messageInfoBean);
            mFlowList.add(messageFlowItemBean);
        }
        mView.setFollowData(mFlowList);
    }
}
