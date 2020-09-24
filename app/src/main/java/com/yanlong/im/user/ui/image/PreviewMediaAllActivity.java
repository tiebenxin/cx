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
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.databinding.ActivityPreviewFileBinding;
import com.yanlong.im.databinding.ItemPreviewFileBinding;

import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.recycler.SuperSwipeRefreshLayout;
import net.cb.cb.library.view.springview.container.DefaultFooter;
import net.cb.cb.library.view.springview.container.DefaultHeader;
import net.cb.cb.library.view.springview.container.LoadFooter;
import net.cb.cb.library.view.springview.container.LoadHeader;
import net.cb.cb.library.view.springview.widget.SpringView;

import java.util.ArrayList;
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
    private int currentPosition;
    private int count;
    private boolean hasMoreData;

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
        bindingView.springView.setHeader(new LoadHeader());
        bindingView.springView.setFooter(new LoadFooter());
        bindingView.springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                if (previewBeans == null) {
                    return;
                }
                loadMore(previewBeans.get(0).getStartTime(), 0);
            }

            @Override
            public void onLoadMore() {
                if (previewBeans == null) {
                    return;
                }
                loadMore(previewBeans.get(previewBeans.size() - 1).getEndTime(), 1);
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
                        List<GroupPreviewBean> list = msgAction.getAllMediaMsg(gid, toUid, time);
                        count = 0;
                        for (int i = 0; i < list.size(); i++) {
                            GroupPreviewBean bean = list.get(i);
                            count += bean.getMsgAllBeans().size();
                            if (bean.isBetween(time)) {
                                currentPosition = i;
                            }
                        }
                        return list;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<GroupPreviewBean>>empty())
                .subscribe(new Consumer<List<GroupPreviewBean>>() {
                    @Override
                    public void accept(List<GroupPreviewBean> list) throws Exception {
                        dismissLoadingDialog();
                        previewBeans = list;
                        mAdapter.setData(previewBeans);
                        bindingView.headView.setTitle("图片及视频（" + count + ")");
                        bindingView.recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (currentPosition >= 0) {
                                    bindingView.recyclerView.scrollToPosition(currentPosition);
                                } else {
                                    bindingView.recyclerView.scrollToPosition(previewBeans.size() - 1);
                                }
                            }
                        }, 300);
                    }
                });
    }

    // refreshType 0 下拉刷新，1 上拉加载更多
    @SuppressLint("CheckResult")
    public void loadMore(long time, int refreshType) {
        hasMoreData = false;
        Observable.just(0)
                .map(new Function<Integer, List<GroupPreviewBean>>() {
                    @Override
                    public List<GroupPreviewBean> apply(Integer integer) throws Exception {
                        List<GroupPreviewBean> list = msgAction.getMoreMediaMsg(gid, toUid, time, refreshType);
                        int size = list.size();
                        if (size > 0) {
                            hasMoreData = true;
                        }
                        List<GroupPreviewBean> removeList = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            GroupPreviewBean bean = list.get(i);
                            if (previewBeans.contains(bean)) {
                                int index = previewBeans.indexOf(bean);
                                if (index >= 0 && index < previewBeans.size()) {
                                    removeList.add(bean);
                                    GroupPreviewBean b = previewBeans.get(index);
                                    if (refreshType == 0) {
                                        List<MsgAllBean> result = b.getMsgAllBeans();
                                        result.addAll(0, bean.getMsgAllBeans());
                                        b.setStartTime(bean.getMsgAllBeans().get(0).getTimestamp());
                                        b.setMsgAllBeans(result);
                                    } else {
                                        List<MsgAllBean> result = b.getMsgAllBeans();
                                        List<MsgAllBean> temp = bean.getMsgAllBeans();
                                        result.addAll(temp);
                                        b.setEndTime(temp.get(temp.size() - 1).getTimestamp());
                                        b.setMsgAllBeans(result);
                                    }
                                }
                            }
                            count += bean.getMsgAllBeans().size();
                            if (bean.isBetween(time)) {
                                currentPosition = i;
                            }
                        }

                        if (removeList.size() > 0) {
                            list.removeAll(removeList);
                        }
                        return list;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<GroupPreviewBean>>empty())
                .subscribe(new Consumer<List<GroupPreviewBean>>() {
                    @Override
                    public void accept(List<GroupPreviewBean> list) throws Exception {
                        dismissLoadingDialog();
                        boolean needRefresh = true;
                        if (list == null || list.size() <= 0) {
                            needRefresh = false;
                        }
                        bindingView.headView.setTitle("图片及视频（" + count + ")");
                        if (refreshType == 0) {
                            if (needRefresh) {
                                previewBeans.addAll(0, list);
                            }
                        } else {
                            if (needRefresh) {
                                previewBeans.addAll(list);
                            }
                        }
                        if (hasMoreData) {
                            mAdapter.setData(previewBeans);
                            hasMoreData = false;
                        }
                        bindingView.springView.onFinishFreshAndLoad();
                    }
                });
    }
}
