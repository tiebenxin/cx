package com.yanlong.im.circle;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.yanlong.im.circle.bean.CircleTitleBean;
import com.yanlong.im.circle.follow.FollowFragment;
import com.yanlong.im.circle.recommend.RecommendFragment;

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
//        mListTitle.add(new CircleTitleBean("最新", false));

        if (mListFragments != null) {
            mListFragments.clear();
        }
        mListFragments.add(new FollowFragment());
        mListFragments.add(new RecommendFragment());
//        mListFragments.add(new NewestFragment());
    }

//    public List<CircleTitleBean> getTitleData() {
//        if (mListTitle == null) {
//            mListTitle = new ArrayList<>();
//            init();
//        }
//        return mListTitle;
//    }

//    public void resetTitleData(int postion) {
//        for (CircleTitleBean bean : mListTitle) {
//            bean.setCheck(false);
//        }
//        mListTitle.get(postion).setCheck(true);
//    }

    public List<Fragment> getListFragment() {
        if (mListFragments == null) {
            mListFragments = new ArrayList<>();
            init();
        }
        return mListFragments;
    }
}
