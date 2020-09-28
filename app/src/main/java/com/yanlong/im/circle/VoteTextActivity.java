package com.yanlong.im.circle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.CreateCircleActivity;
import com.luck.picture.lib.tools.DoubleUtils;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.circle.bean.CircleTitleBean;
import com.yanlong.im.databinding.ActivityVoteTextBinding;
import com.yanlong.im.databinding.ItemVoteTxtBinding;

import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.utils.ToastUtil;
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
 * @description 文字题目
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
@Route(path = VoteTextActivity.path)
public class VoteTextActivity extends BaseBindActivity<ActivityVoteTextBinding> {
    public static final String path = "/circle/VoteTextActivity";

    CommonRecyclerViewAdapter<CircleTitleBean, ItemVoteTxtBinding> mAdapter;
    private List<CircleTitleBean> mList;
    private int focuIndex = -1;// 记录焦点的位置

    @Override
    protected int setView() {
        return R.layout.activity_vote_text;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mList = new ArrayList<>();
        mList.add(new CircleTitleBean(""));
        mList.add(new CircleTitleBean(""));
        mAdapter = new CommonRecyclerViewAdapter<CircleTitleBean, ItemVoteTxtBinding>(this, R.layout.item_vote_txt) {
            @Override
            public void bind(ItemVoteTxtBinding binding, CircleTitleBean data, int position, RecyclerView.ViewHolder viewHolder) {
                switch (position) {
                    case 0:
                        binding.etValue.setHint("选项A");
                        data.setTitle("A");
                        break;
                    case 1:
                        binding.etValue.setHint("选项B");
                        data.setTitle("B");
                        break;
                    case 2:
                        binding.etValue.setHint("选项C");
                        data.setTitle("C");
                        break;
                    case 3:
                        binding.etValue.setHint("选项D");
                        data.setTitle("D");
                        break;
                }
                binding.etValue.setTag(data.getTitle());
                binding.etValue.setText(data.getContent());
                binding.etValue.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        //取出ViewHolder中的tag和本条数据的uid进行比较，如果不相等就代表该ViewHolder是复用的，监听里不做处理
                        if (binding.etValue.getTag().toString().equals(data.getTitle())
                                && binding.etValue.hasFocus() && !TextUtils.isEmpty(s.toString().trim())) {
                            data.setContent(s.toString());
                        }
                    }
                });
                binding.etValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            setFocuIndex(position);
                        } else {
                            setFocuIndex(-1);
                        }
                    }
                });
                binding.ivDelete.setOnClickListener(o -> {
                    if (mList.size() > 2) {
                        mList.remove(position);
                        notifyDataSetChanged();
                    } else {
                        ToastUtil.show("最少要有两个选项");
                    }
                    setVisibleAdd();
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
                Intent intent = new Intent();
                intent.putExtra(CreateCircleActivity.VOTE_TXT, new Gson().toJson(mList));
                intent.putExtra(CreateCircleActivity.VOTE_TXT_TITLE, bindingView.etTitle.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        bindingView.tvAdd.setOnClickListener(o -> {
            if (!DoubleUtils.isFastDoubleClick()) {
                mList.add(new CircleTitleBean(""));
                mAdapter.notifyDataSetChanged();
                setVisibleAdd();
            }
        });
    }

    private void setVisibleAdd() {
        if (mList.size() > 3) {
            bindingView.tvAdd.setVisibility(View.GONE);
            bindingView.viewLine.setVisibility(View.GONE);
        } else {
            bindingView.tvAdd.setVisibility(View.VISIBLE);
            bindingView.viewLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void loadData() {
        String title = getIntent().getStringExtra(CreateCircleActivity.VOTE_TXT_TITLE);
        String txtJson = getIntent().getStringExtra(CreateCircleActivity.VOTE_TXT);
        bindingView.etTitle.setText(title);
        if (!TextUtils.isEmpty(txtJson)) {
            mList.clear();
            List<CircleTitleBean> list = new Gson().fromJson(txtJson,
                    new TypeToken<List<CircleTitleBean>>() {
                    }.getType());
            mList.addAll(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    public int getFocuIndex() {
        return focuIndex;
    }

    public void setFocuIndex(int focuIndex) {
        this.focuIndex = focuIndex;
    }
}