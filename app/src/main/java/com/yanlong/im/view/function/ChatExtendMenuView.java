package com.yanlong.im.view.function;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;

import java.util.List;

/**
 * @author Liszt
 * @date 2020/1/11
 * Description
 */
public class ChatExtendMenuView extends LinearLayout {

    private View viewRoot;
    private ViewPager viewPager;
    private RadioGroup rgDots;
    private List<FunctionItemModel> mList;
    private int pagerCount = 1;
    private OnFunctionListener listener;

    public ChatExtendMenuView(Context context) {
        this(context, null);
    }

    public ChatExtendMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatExtendMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        viewRoot = LayoutInflater.from(getContext()).inflate(R.layout.view_function_extand, this, true);
//        this.addView(viewRoot);
        viewPager = viewRoot.findViewById(R.id.view_pager);
        rgDots = viewRoot.findViewById(R.id.rb_dots);
    }


    public void bindDate(List<FunctionItemModel> l) {
        mList = l;
        if (mList != null) {
            if (mList.size() > 8) {
                pagerCount = 2;
            } else {
                pagerCount = 1;
            }
        } else {
            return;
        }

        initDots();
        initPager();
    }

    private void initPager() {
//        ArrayList<View> viewList = new ArrayList<>();
//        int size = mList.size();
//        for (int i = 0; i < pagerCount; i++) {
//            FrameLayout frameLayout=new FrameLayout(getContext());
//            RecyclerView recyclerView = new RecyclerView(getContext());
//            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
//            recyclerView.setLayoutManager(layoutManager);
//            AdapterFunctionView adapterFunctionView = new AdapterFunctionView(getContext());
//            if (i == 0) {
//                if (size > 8) {
//                    adapterFunctionView.bindData(mList.subList(0, 8));
//                } else {
//                    adapterFunctionView.bindData(mList);
//                }
//                recyclerView.setAdapter(adapterFunctionView);
//            } else {
//                adapterFunctionView.bindData(mList.subList(8, mList.size()));
//                recyclerView.setAdapter(adapterFunctionView);
//            }
//            adapterFunctionView.setFunctionListner(listener);
//            viewList.add(recyclerView);
//        }
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(1);

        if (pagerCount > 1) {
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    if (rgDots != null && i >= 0 && i <= pagerCount) {
                        rgDots.check(i);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
        }


    }

    public class ViewPagerAdapter extends PagerAdapter {
        private Context context;

        public ViewPagerAdapter(Context context) {
            this.context = context;

        }

        @Override
        public int getCount() {
            return pagerCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = View.inflate(context, R.layout.view_pager_item_layout, null);

            container.addView(view);

            RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
            recyclerView.setLayoutManager(layoutManager);
            AdapterFunctionView adapterFunctionView = new AdapterFunctionView(getContext());
            if (position == 0) {
                if (pagerCount > 8) {
                    adapterFunctionView.bindData(mList.subList(0, 8));
                } else {
                    adapterFunctionView.bindData(mList);
                }
                recyclerView.setAdapter(adapterFunctionView);
            } else {
                adapterFunctionView.bindData(mList.subList(8, mList.size()));
                recyclerView.setAdapter(adapterFunctionView);
            }
            adapterFunctionView.setFunctionListner(listener);
            return view;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }

    }

    private void initDots() {
        if (pagerCount > 1) {
            rgDots.setVisibility(VISIBLE);
            rgDots.removeAllViews();
            for (int i = 0; i < pagerCount; i++) {
                RadioButton radioButton = new RadioButton(getContext());
                radioButton.setId(i);
                radioButton.setButtonDrawable(R.drawable.radio_dot_selector);
                radioButton.setEnabled(false);
                RadioGroup.LayoutParams radioParams = new RadioGroup.LayoutParams(20, 20);
                radioParams.leftMargin = 10;
                radioButton.setLayoutParams(radioParams);
                rgDots.addView(radioButton);
            }
            rgDots.check(0);
        } else {
            rgDots.setVisibility(GONE);
        }
    }

    public interface OnFunctionListener {

        void onClick(@ChatEnum.EFunctionId int id);
    }


    public void setListener(OnFunctionListener l) {
        listener = l;
    }


}
