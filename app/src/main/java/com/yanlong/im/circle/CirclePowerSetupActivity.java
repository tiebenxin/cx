package com.yanlong.im.circle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.luck.picture.lib.circle.CreateCircleActivity;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.circle.bean.CirclePowerBean;
import com.yanlong.im.circle.details.CircleDetailsActivity;
import com.yanlong.im.circle.server.CircleServer;
import com.yanlong.im.databinding.ActivityCirclePowerSetupBinding;
import com.yanlong.im.databinding.ItemCirclePowerSetupBinding;

import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.YLLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import retrofit2.Call;
import retrofit2.Response;

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
    private Long mMomentId;
    private int mVisible;

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

        mMomentId = getIntent().getLongExtra(CircleDetailsActivity.MOMENT_ID, 0l);
        mVisible = getIntent().getIntExtra(CircleDetailsActivity.VISIBLE, 0);

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
                if (mMomentId != null && mMomentId > 0) {
                    updateVisibility(StringUtil.getVisible(value), value);
                } else {
                    setResult(value);
                }
            }
        });
    }

    /**
     * 修改说说可见度
     *
     * @param visibility 可见度(0:广场可见|1:好友可见|2:陌生人可见|3:自己可见)
     * @param value
     */
    private void updateVisibility(int visibility, String value) {
        CircleServer server = NetUtil.getNet().create(CircleServer.class);
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("momentId", mMomentId);
        params.put("visibility", visibility);
        NetUtil.getNet().exec(server.updateVisibility(params), new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    ToastUtil.show("修改成功");
                    setResult(value);
                } else {
                    ToastUtil.show(getFailMessage(response.body()));
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("修改失败");
            }
        });
    }

    public boolean checkSuccess(ReturnBean returnBean) {
        if (returnBean != null && returnBean.getCode() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getFailMessage(ReturnBean returnBean) {
        String result = "";
        if (returnBean != null) {
            result = returnBean.getMsg();
        }
        return result;
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
            if (mMomentId != null && mMomentId > 0) {
                if (mVisible == i) {
                    circlePowerBean.setCheck(true);
                }
            } else if (i == 0) {
                circlePowerBean.setCheck(true);
            }
            mList.add(circlePowerBean);
        }
        mAdapter.setData(mList);
    }
}