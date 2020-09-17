package com.yanlong.im.user.ui.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.nim_lib.ui.BaseBindActivity;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.GroupPreviewBean;
import com.yanlong.im.databinding.ActivityPreviewFileBinding;
import com.yanlong.im.databinding.ItemPreviewFileBinding;

import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Liszt
 * @date 2020/9/14
 * Description 浏览当前会话所有图片，视频，文件
 */
public class PreviewMediaAllActivity extends BaseBindActivity<ActivityPreviewFileBinding> {

    private CommonRecyclerViewAdapter mAdapter;
    private MsgAction msgAction = new MsgAction();
    private String gid;
    private Long toUid;
    private long time;
    private String msgId;
    boolean isSelect = false;
    private List<GroupPreviewBean> previewBeans;

    public static Intent newIntent(Context context, String gid, Long toUid, String msgId, long time) {
        Intent intent = new Intent(context, PreviewMediaAllActivity.class);
        intent.putExtra("gid", gid);
        intent.putExtra("uid", toUid);
        intent.putExtra("msgId", msgId);
        intent.putExtra("time", time);
        return intent;
    }

    @Override
    protected int setView() {
        return R.layout.activity_preview_file;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mAdapter = new CommonRecyclerViewAdapter<GroupPreviewBean, ItemPreviewFileBinding>(this, R.layout.item_preview_file) {

            @Override
            public void bind(ItemPreviewFileBinding binding, GroupPreviewBean data, int position, RecyclerView.ViewHolder viewHolder) {
                binding.tvTime.setText(data.getTime());
                GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
                binding.recyclerView.setLayoutManager(layoutManager);
                AdapterMediaAll adapter = new AdapterMediaAll(getContext());
                adapter.setSelect(isSelect);
                adapter.bindData(data.getMsgAllBeans());
                binding.recyclerView.setAdapter(adapter);
            }
        };
        bindingView.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bindingView.recyclerView.setAdapter(mAdapter);
        bindingView.tvTime.setVisibility(View.GONE);
    }

    @Override
    protected void initEvent() {
        bindingView.headView.getActionbar().setTxtRight("多选");
        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                isSelect = !isSelect;
                bindingView.headView.getActionbar().setTxtRight(isSelect ? "确定" : "多选");
                mAdapter.notifyDataSetChanged();
            }
        });

        bindingView.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    protected void loadData() {
        Intent intent = getIntent();
        gid = intent.getStringExtra("gid");
        toUid = intent.getLongExtra("uid", 0L);
        time = intent.getLongExtra("time", 0L);
        msgId = intent.getStringExtra("msgId");
        showLoadingDialog();
        Observable.just(0)
                .map(new Function<Integer, List<GroupPreviewBean>>() {
                    @Override
                    public List<GroupPreviewBean> apply(Integer integer) throws Exception {
                        return msgAction.getAllMediaMsg(gid, toUid, time);
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.add(Calendar.MONTH, -1);
//                        return msgAction.getAllMediaMsg(gid, toUid, calendar.getTimeInMillis());
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<GroupPreviewBean>>empty())
                .subscribe(new Consumer<List<GroupPreviewBean>>() {
                    @Override
                    public void accept(List<GroupPreviewBean> list) throws Exception {
                        dismissLoadingDialog();
                        previewBeans = list;
                        mAdapter.setData(list);
                        bindingView.recyclerView.smoothScrollToPosition(list.size() - 1);
                    }
                });

    }
}
