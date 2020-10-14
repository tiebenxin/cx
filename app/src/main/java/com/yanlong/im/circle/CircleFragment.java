package com.yanlong.im.circle;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.AttachmentBean;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.event.EventFactory;
import com.luck.picture.lib.rxbus2.RxBus;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.CircleTitleBean;
import com.yanlong.im.databinding.ActivityCircleBinding;

import net.cb.cb.library.base.bind.BaseBindMvpFragment;
import net.cb.cb.library.dialog.DialogLoadingProgress;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-07
 * @updateAuthor
 * @updateDate
 * @description 朋友圈
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class CircleFragment extends BaseBindMvpFragment<CirclePresenter, ActivityCircleBinding> implements CircleView {

    private ViewPagerAdapter mViewPagerAdapter;
    private EventFactory.CreateCircleEvent.CircleBean mCircleBean;
    private List<AttachmentBean> mList;
    private List<CircleTitleBean> mVotePictrueList;
    private DialogLoadingProgress mLoadingProgress;

    @Override
    protected CirclePresenter createPresenter() {
        return new CirclePresenter(getContext());
    }

    @Override
    public int setLayout() {
        return R.layout.activity_circle;
    }

    public static CircleFragment newInstance() {
        CircleFragment fragment = new CircleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void init() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mViewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mPresenter.getListFragment());
        bindingView.viewPager.setAdapter(mViewPagerAdapter);
        bindingView.viewPager.setOffscreenPageLimit(mPresenter.getListFragment().size());
        bindingView.viewPager.setCurrentItem(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshChat(EventFactory.CreateCircleEvent event) {
        mLoadingProgress = new DialogLoadingProgress(event.context);
        mLoadingProgress.show();
        mCircleBean = event.circleBean;
        if (!TextUtils.isEmpty(mCircleBean.getAttachment())) {
            mList = new Gson().fromJson(mCircleBean.getAttachment(),
                    new TypeToken<List<AttachmentBean>>() {
                    }.getType());
            if (mList != null && mList.size() > 0) {
                switch (mCircleBean.getType()) {
                    case PictureEnum.EContentType.VOICE:
                        mPresenter.uploadFile(mList.get(0).getUrl(), PictureEnum.EContentType.VOICE, false, UpFileAction.PATH.VOICE);
                        break;
                    case PictureEnum.EContentType.VIDEO:
                        mPresenter.uploadFile(mList.get(0).getBgUrl(), PictureEnum.EContentType.VIDEO, false, UpFileAction.PATH.VIDEO);
                        break;
                    case PictureEnum.EContentType.PICTRUE:
                    case PictureEnum.EContentType.VOICE_AND_VOTE:
                    case PictureEnum.EContentType.PICTRUE_AND_VOTE:
                    case PictureEnum.EContentType.VIDEO_AND_VOTE:
                        // 移除加号
                        List<LocalMedia> list = event.getList();
                        for (int i = list.size() - 1; i >= 0; i--) {
                            if (list.get(i).isShowAdd()) {
                                list.remove(i);
                                break;
                            }
                        }
                        mPresenter.batchFileCheck(list);
                        break;
                }
            }
        } else if (!TextUtils.isEmpty(mCircleBean.getVote())) {// 投票
            if (mPresenter.voteIsPictrue(mCircleBean.getVote())) {
                mVotePictrueList = new Gson().fromJson(mCircleBean.getVote(),
                        new TypeToken<List<CircleTitleBean>>() {
                        }.getType());
                mPresenter.batchFileCheck(event.getList());
            } else {
                mPresenter.setParams(mCircleBean);
            }
        } else {
            mPresenter.setParams(mCircleBean);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        dismiss();
    }

    private void dismiss() {
        if (mLoadingProgress != null && mLoadingProgress.isShowing()) {
            mLoadingProgress.dismiss();
        }
    }

    @Override
    public void initEvent() {
        bindingView.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitleBold(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        bindingView.rbFollow.setOnClickListener(o -> {
            setTitleBold(1);
            bindingView.viewPager.setCurrentItem(1);
        });
        bindingView.rbRecommend.setOnClickListener(o -> {
            setTitleBold(0);
            bindingView.viewPager.setCurrentItem(0);
        });
        bindingView.ivCreateCircle.setOnClickListener(o -> {
            PictureSelector.create(getActivity())
                    .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                    .previewImage(false)// 是否可预览图片 true or false
                    .isCamera(true)// 是否显示拍照按钮 ture or false
                    .maxVideoSelectNum(1)
                    .compress(true)// 是否压缩 true or false
                    .isGif(true)
                    .selectArtworkMaster(true)
                    .toResult(PictureConfig.CHOOSE_REQUEST);//结果回调 code
        });
    }

    private void setTitleBold(int position) {
        if (position == 0) {
            bindingView.rbRecommend.setChecked(true);
            bindingView.rbRecommend.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            bindingView.rbFollow.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        } else {
            bindingView.rbFollow.setChecked(true);
            bindingView.rbRecommend.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            bindingView.rbFollow.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
    }

    @Override
    public void onSuccess() {
        dismiss();
        ToastUtil.show("发布成功");
        RxBus.getDefault().post(new EventFactory.CreateSuccessEvent());
    }

    @Override
    public void uploadSuccess(String url, int type, boolean isVideo, HashMap<String, String> netFile) {
        try {
            if (mList != null && mList.size() > 0) {
                AttachmentBean attachmentBean = mList.get(0);
                switch (type) {
                    case PictureEnum.EContentType.VOICE:
                        attachmentBean.setUrl(url);
                        break;
                    case PictureEnum.EContentType.VIDEO:
                        if (isVideo) {
                            attachmentBean.setUrl(url);
                        } else {
                            attachmentBean.setBgUrl(url);
                            mPresenter.uploadFile(attachmentBean.getUrl(), PictureEnum.EContentType.VIDEO, true, UpFileAction.PATH.VIDEO);
                            return;
                        }
                        break;
                    case PictureEnum.EContentType.PICTRUE:
                        for (int i = 0; i < mList.size(); i++) {
                            mList.get(i).setUrl(netFile.get(mList.get(i).getUrl()));
                        }
                        break;
                }
                mCircleBean.setAttachment(new Gson().toJson(mList));
            } else if (mVotePictrueList != null && mVotePictrueList.size() > 0) {
                for (int i = 0; i < mVotePictrueList.size(); i++) {
                    mVotePictrueList.get(i).setContent(netFile.get(mVotePictrueList.get(i).getContent()));
                }
                mCircleBean.setVote(new Gson().toJson(mVotePictrueList));
            }
            mPresenter.setParams(mCircleBean);
        } catch (Exception e) {
        }
    }

    @Override
    public void showMessage(String message) {
        ToastUtil.show(message);
        dismiss();
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments;

        public ViewPagerAdapter(FragmentManager fm, List<Fragment> fagments) {
            super(fm);
            this.mFragments = fagments;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}