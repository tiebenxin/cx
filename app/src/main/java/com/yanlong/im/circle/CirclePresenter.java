package com.yanlong.im.circle;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.luck.picture.lib.event.EventFactory;
import com.yanlong.im.circle.bean.CircleTitleBean;
import com.yanlong.im.circle.follow.FollowFragment;
import com.yanlong.im.circle.recommend.RecommendFragment;

import net.cb.cb.library.base.bind.BasePresenter;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;

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
public class CirclePresenter extends BasePresenter<CircleModel, CircleView> {

    private List<CircleTitleBean> mListTitle = new ArrayList<>();
    private List<Fragment> mListFragments = new ArrayList<>();

    CirclePresenter(Context context) {
        super(context);
        init();
    }

    @Override
    public CircleModel bindModel() {
        return new CircleModel();
    }

    public void init() {
        if (mListTitle != null) {
            mListTitle.clear();
        }
        mListTitle.add(new CircleTitleBean("推荐", true));
        mListTitle.add(new CircleTitleBean("关注", false));

        if (mListFragments != null) {
            mListFragments.clear();
        }
        mListFragments.add(new FollowFragment());
        mListFragments.add(new RecommendFragment());
    }

    public List<Fragment> getListFragment() {
        if (mListFragments == null) {
            mListFragments = new ArrayList<>();
            init();
        }
        return mListFragments;
    }

    public void setParams(EventFactory.CreateCircleEvent.CircleBean circleBean) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("content", circleBean.getContent());
        params.put("type", circleBean.getType());
        params.put("visibility", circleBean.getVisibility());
        if (!TextUtils.isEmpty(circleBean.getPosition())) {
            params.put("position", circleBean.getPosition());
            params.put("latitude", circleBean.getLatitude());
            params.put("longitude", circleBean.getLongitude());
        }
        if (!TextUtils.isEmpty(circleBean.getAttachment())) {
            params.put("attachment", circleBean.getAttachment());
        }
        createNewCircle(params);
    }

    /**
     * 创建发布动态的接口
     *
     * @param params 入参
     */
    public void createNewCircle(WeakHashMap<String, Object> params) {
        mModel.createNewCircle(params, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.code() == 200) {
                    mView.onSuccess();
                } else {
                    mView.showMessage(response.message());
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                mView.showMessage("发布动态失败");
            }
        });
    }

    /**
     * 上传语音文件
     *
     * @param file
     */
    public void uploadFile(String file) {
        new UpFileAction().upFile(UpFileAction.PATH.VOICE, mContext, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                mView.uploadSuccess(url);
                LogUtil.getLog().e("1212", "上传语音成功--" + url);
            }

            @Override
            public void fail() {
            }

            @Override
            public void inProgress(long progress, long zong) {
            }
        }, file);
    }
}
