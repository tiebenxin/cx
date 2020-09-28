package com.yanlong.im.circle.follow;

import android.content.Context;

import com.yanlong.im.circle.adapter.CircleFlowAdapter;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;

import net.cb.cb.library.base.bind.BasePresenter;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.Response;

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

    private List<MessageFlowItemBean> flowList = new ArrayList<>();

    FollowPresenter(Context context) {
        super(context);
    }

    @Override
    public FollowModel bindModel() {
        return new FollowModel();
    }

    /**
     * 获取关注列表
     *
     * @param currentPage 页码
     * @param pageSize    页数
     */
    public void getFollowMomentList(int currentPage, int pageSize) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("currentPage", currentPage);
        params.put("pageSize", pageSize);
        mModel.getFollowMomentList(params, new CallBack<ReturnBean<List<MessageInfoBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<MessageInfoBean>>> call, Response<ReturnBean<List<MessageInfoBean>>> response) {
                super.onResponse(call, response);
                if (response.code() == 200) {
                    if (response.body() != null && response.body().getData() != null) {
                        if (currentPage == 1) {
                            flowList.clear();
                        }
                        for (MessageInfoBean messageInfoBean : response.body().getData()) {
//                            MessageFlowItemBean messageFlowItemBean = null;
//                            switch (messageInfoBean.getType()) {
//                                case PictureEnum.EContentType.VOTE:
//                                case PictureEnum.EContentType.PICTRUE_AND_VOTE:
//                                case PictureEnum.EContentType.VOICE_AND_VOTE:
//                                case PictureEnum.EContentType.VIDEO_AND_VOTE:
//                                case PictureEnum.EContentType.PICTRUE_AND_VIDEO_VOTE:
//                                    messageFlowItemBean = new MessageFlowItemBean(CircleFlowAdapter.MESSAGE_DEFAULT, messageInfoBean);
//                                    break;
//                                default:
//                                    messageFlowItemBean = new MessageFlowItemBean(CircleFlowAdapter.MESSAGE_DEFAULT, messageInfoBean);
//                                    break;
//                            }
                            MessageFlowItemBean messageFlowItemBean = new MessageFlowItemBean(CircleFlowAdapter.MESSAGE_DEFAULT, messageInfoBean);
                            flowList.add(messageFlowItemBean);
                        }
                        mView.onSuccess(flowList);
                    }
                } else {
                    mView.onShowMessage(response.message());
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<MessageInfoBean>>> call, Throwable t) {
                super.onFailure(call, t);
                mView.onShowMessage("刷新失败");
            }
        });
    }
}
