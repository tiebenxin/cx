package com.yanlong.im.chat.ui.forward;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.databinding.FragmentForwardSessionBinding;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.base.BaseMvpFragment;

import java.util.List;

/**
 * @anthor Liszt
 * @data 2019/8/10
 * Description  转发roster选择列表
 */
public class ForwardRosterFragment extends BaseMvpFragment<ForwardModel, ForwardView, ForwardPresenter> implements ForwardView {

    private FragmentForwardSessionBinding ui;
    private UserDao userDao = new UserDao();
    private MsgDao msgDao = new MsgDao();
    private AdapterForwardRoster adapter;
    private IForwardRosterListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ui = DataBindingUtil.inflate(inflater, R.layout.fragment_forward_session, container, false);
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

    private void initAdapter() {
        adapter = new AdapterForwardRoster(getActivity());
        ui.listView.init(adapter);
        ui.listView.getLoadView().setStateNormal();
        adapter.initDao(userDao, msgDao);
        adapter.setForwardListener(listener);
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
        adapter.bindData(list);
    }
}
