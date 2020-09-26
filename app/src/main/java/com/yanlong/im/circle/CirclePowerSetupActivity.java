package com.yanlong.im.circle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.luck.picture.lib.CreateCircleActivity;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.circle.bean.CirclePowerBean;
import com.yanlong.im.databinding.ActivityCirclePowerSetupBinding;
import com.yanlong.im.databinding.ItemCirclePowerSetupBinding;

import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.YLLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-07
 * @updateAuthor
 * @updateDate
 * @description 朋友圈权限设置
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
@Route(path = CirclePowerSetupActivity.path)
public class CirclePowerSetupActivity extends BaseBindActivity<ActivityCirclePowerSetupBinding> {
    public static final String path = "/circle/CirclePowerSetupActivity";

    private CommonRecyclerViewAdapter<CirclePowerBean, ItemCirclePowerSetupBinding> mAdapter;
    private List<CirclePowerBean> mList = new ArrayList<>();

    @Override
    protected int setView() {
        return R.layout.activity_circle_power_setup;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mAdapter = new CommonRecyclerViewAdapter<CirclePowerBean, ItemCirclePowerSetupBinding>(this, R.layout.item_circle_power_setup) {
            @Override
            public void bind(ItemCirclePowerSetupBinding binding, CirclePowerBean data, int position, RecyclerView.ViewHolder viewHolder) {
                binding.tvName.setText(data.getTitle());
                binding.tvNote.setText(data.getNote());
                if (data.isCheck()) {
                    binding.ivSelect.setImageResource(R.drawable.bg_cheack_green_s);
                } else {
                    binding.ivSelect.setImageResource(R.drawable.bg_cheack_green_e);
                }
                binding.layoutRoot.setOnClickListener(o -> {
                    for (CirclePowerBean bean : mList) {
                        bean.setCheck(false);
                    }
                    data.setCheck(!data.isCheck());
                    notifyDataSetChanged();
                });
            }
        };
        bindingView.recyclerView.setLayoutManager(new YLLinearLayoutManager(this));
        mAdapter.setData(mList);
        bindingView.recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initEvent() {
        bindingView.headView.getActionbar().setTxtRight("完成");
        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                String value = "";
                for (CirclePowerBean bean : mList) {
                    if (bean.isCheck()) {
                        value = bean.getTitle();
                        break;
                    }
                }
                setResult(value);
            }
        });
    }

    private void setResult(String name) {
        Intent intent = new Intent();
        intent.putExtra(CreateCircleActivity.INTENT_POWER, name);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void loadData() {
        for (int i = 0; i < 4; i++) {
            CirclePowerBean circlePowerBean = new CirclePowerBean();
            if (i == 0) {
                circlePowerBean.setCheck(true);
                circlePowerBean.setTitle("广场可见");
                circlePowerBean.setNote("所有人可见");
            } else if (i == 1) {
                circlePowerBean.setTitle("仅好友可见");
                circlePowerBean.setNote("仅好友和我关注的人可见");
            } else if (i == 2) {
                circlePowerBean.setTitle("仅陌生人可见");
                circlePowerBean.setNote("仅陌生人可见");
            } else if (i == 3) {
                circlePowerBean.setTitle("自己可见");
                circlePowerBean.setNote("自己可见");
            }
            mList.add(circlePowerBean);
        }
        mAdapter.setData(mList);
    }
}