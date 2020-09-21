package com.yanlong.im.circle;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import androidx.annotation.NonNull;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityCircleBinding;

import net.cb.cb.library.base.bind.BaseBindMvpFragment;
import net.cb.cb.library.utils.ScreenUtil;
import net.cb.cb.library.view.EScrollView;

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

    //    private CommonRecyclerViewAdapter<CircleTitleBean, ViewCircleTypeBinding> mAdapter;
    private ViewPagerAdapter mViewPagerAdapter;

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
//        mAdapter = new CommonRecyclerViewAdapter<CircleTitleBean, ViewCircleTypeBinding>(getActivity(), R.layout.view_circle_type) {
//            @Override
//            public void bind(ViewCircleTypeBinding binding, CircleTitleBean data, int position, RecyclerView.ViewHolder viewHolder) {
//                binding.tvName.setText(data.getTitle());
//                if (data.isCheck()) {
//                    binding.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
//                    binding.tvName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//                } else {
//                    binding.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//                    binding.tvName.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//                }
//                binding.tvName.setOnClickListener(o -> {
//                    if (!data.isCheck()) {
//                        mPresenter.resetTitleData(position);
//                        notifyDataSetChanged();
//                        bindingView.viewPager.setCurrentItem(position);
//                    }
//                });
//            }
//        };
//        mAdapter.setData(mPresenter.getTitleData());
//        bindingView.recyclerTitle.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        bindingView.recyclerTitle.setAdapter(mAdapter);

        bindingView.refreshLayout.setRefreshHeader(new MaterialHeader(getActivity()));

        mViewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), mPresenter.getListFragment());
        bindingView.viewPager.setAdapter(mViewPagerAdapter);
        bindingView.viewPager.setOffscreenPageLimit(mPresenter.getListFragment().size());
        bindingView.viewPager.setCurrentItem(0);
    }

    @Override
    public void initEvent() {
        bindingView.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
//                bindingView.viewPager.setCurrentItem(position);
                setTitleBold(position);
                if (!getActivity().isFinishing()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 重置高度
                            bindingView.viewPager.resetHeight(position);
                        }
                    }, 200);
                }
//                mPresenter.resetTitleData(position);
//                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        bindingView.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                bindingView.refreshLayout.finishRefresh();
                bindingView.refreshLayout.finishLoadMore();
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
        bindingView.scrollView.setOnScrolListener(new EScrollView.OnScrolListener() {
            @Override
            public void onScroll(int scrollY) {
                if (ScreenUtil.dip2px(getContext(), 215) <= scrollY) {
                    bindingView.layoutTitle.setVisibility(View.VISIBLE);
                } else {
                    bindingView.layoutTitle.setVisibility(View.GONE);
                }
            }
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
            bindingView.rbRecommend.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            bindingView.rbFollow.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        } else {
            bindingView.rbRecommend.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            bindingView.rbFollow.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
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