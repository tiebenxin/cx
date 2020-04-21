package com.yanlong.im.chat.ui.forward;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.databinding.FragmentForwardSessionBinding;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.base.BaseMvpFragment;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Liszt
 * @date 2019/8/10
 * Description  转发roster选择列表 通讯录
 */
public class ForwardRosterFragment extends BaseMvpFragment<ForwardModel, ForwardView, ForwardPresenter> implements ForwardView {

    private FragmentForwardSessionBinding ui;
    private ForwardRosterAdapter adapter;
    private IForwardRosterListener listener;
    private List<UserInfo> userlist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ui = DataBindingUtil.inflate(inflater, R.layout.fragment_forward_session, container, false);
        EventBus.getDefault().register(this);
        return ui.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        if (presenter != null) {
            presenter.loadAndSetData(false);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initAdapter() {
        adapter = new ForwardRosterAdapter(getActivity(), ((MsgForwardActivity) getActivity()).getViewModel());
        ui.listView.init(adapter);
        ui.listView.getLoadView().setStateNormal();
        adapter.setForwardListener(listener);

        ui.sortView.setLinearLayoutManager(ui.listView.getLayoutManager());
        ui.sortView.setListView(ui.listView.getListView());
    }

    @Override
    public ForwardModel createModel() {
        return new ForwardModel();
    }

    @Override
    public ForwardView createView() {
        return this;
    }

    @Override
    public ForwardPresenter createPresenter() {
        return new ForwardPresenter();
    }


    public void setForwardListener(IForwardRosterListener l) {
        listener = l;
    }

    @Override
    public void setSessionData(List<Session> list) {

    }

    @Override
    public void setRosterData(List<UserInfo> list) {
        if (list == null) {
            return;
        }

        userlist = list;
        List<UserInfo> temp = searchSessionBykey(userlist, MsgForwardActivity.searchKey);
        adapter.bindData(temp);
        userParseString(list);
        ui.listView.init(adapter);
    }


    private List<UserInfo> searchSessionBykey(List<UserInfo> list, String key) {
        LogUtil.getLog().e("======转发搜索====通讯录====key==" + key);
        if (!StringUtil.isNotNull(key)) {
            return list;
        }

        List<UserInfo> temp = new ArrayList<>();
        for (UserInfo bean : list) {
            String name = bean.getName4Show();
            if (StringUtil.isNotNull(name) && (name.contains(key))) {
//                LogUtil.getLog().e("====name==="+name);
                temp.add(bean);
            }
        }
//        LogUtil.getLog().e("====temp==="+temp.size());
        return temp;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void posting(SingleOrMoreEvent event) {
        ui.listView.init(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void posting(SearchKeyEvent event) {
        List<UserInfo> temp = searchSessionBykey(userlist, MsgForwardActivity.searchKey);
        adapter.bindData(temp);
        userParseString(temp);
        ui.listView.init(adapter);
    }

    /**
     * 获取用户的首字母列表
     *
     * @return
     */
    @SuppressLint("CheckResult")
    public void userParseString(List<UserInfo> friends) {
        Observable.just(0)
                .map(new Function<Integer, List<String>>() {
                    @Override
                    public List<String> apply(Integer integer) throws Exception {
                        List<String> list = null;
                        try {
                            //数据库中存储的是Z1，便于排序
                            if (friends != null) {
                                list = new ArrayList<>();
                                for (int i = 0; i < friends.size(); i++) {
                                    String tag = friends.get(i).getTag();
                                    list.add(tag);
                                    ui.sortView.putTag(tag, i);
                                }
                            }
                        } catch (Exception e) {
                        }
                        return list;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<String>>empty())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> list) throws Exception {
                        if (list != null) {
                            ui.sortView.addItemView(list);
                        }
                    }
                });

    }
}
