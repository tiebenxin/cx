package com.yanlong.im.user.ui.register;

import android.view.View;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.RegisterDetailBean;
import com.yanlong.im.databinding.FragmentRegisterSecondBinding;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Liszt
 * @date 2020/11/16
 * Description 身高
 */
public class RegisterDetailSecondFragment extends BaseRegisterFragment<FragmentRegisterSecondBinding> {
    private int start = 50;//最小高度 50cm
    private int end = 301;//最大高度 300cm
    private int target = 170;//默认高度 170cm
    private int count;
    private List<Integer> optionItems;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_register_second;
    }

    @Override
    public void init() {
        mViewBinding.ivLeft.setVisibility(View.VISIBLE);
        mViewBinding.ivRight.setVisibility(View.VISIBLE);
        count = end - start;
        optionItems = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            optionItems.add(i + start);
        }
        mViewBinding.wheelView.setAdapter(new ArrayWheelAdapter(optionItems));
        mViewBinding.wheelView.setCurrentItem(target - start);
        ((RegisterDetailActivity) getActivity()).getDetailBean().setHeight(target);
    }

    @Override
    public void initListener() {
        mViewBinding.ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });
        mViewBinding.ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onNext();
                }
            }
        });
        mViewBinding.wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                if (optionItems != null && index >= 0 && index < optionItems.size()) {
                    int height = optionItems.get(index);
                    ((RegisterDetailActivity) getActivity()).getDetailBean().setHeight(height);
                }
            }
        });
    }


    @Override
    public void updateDetailUI(RegisterDetailBean bean) {
        try {
            if (bean == null) {
                return;
            }
            if (bean.getHeight() > 0) {
                int index = bean.getHeight() - start;
                mViewBinding.wheelView.setCurrentItem(index);
            }
        } catch (Exception e) {
        }
    }
}
