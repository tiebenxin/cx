package com.yanlong.im.chat.ui.forward;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SingleMeberInfoBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.databinding.FragmentForwardSessionBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.base.BaseMvpFragment;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @author Liszt
 * @date 2019/8/10
 * Description  转发session选择列表 最近聊天列表
 */

public class ForwardSessionFragment extends BaseMvpFragment<ForwardModel, ForwardView, ForwardPresenter> implements ForwardView {

    private FragmentForwardSessionBinding ui;
    private UserDao userDao = new UserDao();
    private MsgDao msgDao = new MsgDao();
    private ForwardSessionAdapter adapter;
    private IForwardListener listener;
    private List<Session> sessionsList;
    private List<Session> ListOne;//含有全部禁言的数据
    private List<Session> ListThree;//含有个人会话的数据

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
            presenter.loadAndSetData(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initAdapter() {
//        adapter = new AdapterForwardSession(getActivity());
        adapter = new ForwardSessionAdapter(getActivity(), ((MsgForwardActivity) getActivity()).getViewModel());
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


    @Override
    public void setSessionData(List<Session> sessions) {
        if (sessions == null) {
            return;
        }
        sessionsList = sessions;
        ListOne = new ArrayList<>();
        ListThree = new ArrayList<>();
        //1 先统计全员禁言的群，过滤掉不显示
        for(int i=0; i<sessionsList.size();i++){
            if(!TextUtils.isEmpty(sessionsList.get(i).getGid())){
                Group group = msgDao.groupNumberGet(sessionsList.get(i).getGid());
                if (group.getWordsNotAllowed() == 1) {
                    //如果我是群主或者管理员则不过滤
                    if(!isAdmin(group) && !isAdministrators(group)){
                        ListOne.add(sessionsList.get(i));
                    }
                }
            }
            //过滤掉空白的异常会话
            if(sessionsList.get(i).getFrom_uid()!=(-1)){
                ListThree.add(sessionsList.get(i));
            }
        }
        if(ListOne.size()>0){
            sessionsList.removeAll(ListOne);
        }
//        //2 然后过滤掉将我禁言的群，不再每条会话发请求(过于耗时)，改为点击再提示
//        for(int i=0; i<sessionsList.size();i++){
//            if(!TextUtils.isEmpty(sessionsList.get(i).getGid())){
//                getSingleMemberInfo(sessionsList.get(i).getGid(),i);
//            }
//        }
        List<Session> temp = searchSessionBykey(sessionsList, MsgForwardActivity.searchKey);
        adapter.bindData(temp);
        ui.listView.init(adapter);
    }

    @Override
    public void setRosterData(List<UserInfo> list) {

    }

    public void setForwardListener(IForwardListener l) {
        listener = l;
    }


    private List<Session> searchSessionBykey(List<Session> sessions, String key) {
//        LogUtil.getLog().e("======转发搜索====最近聊天====key=="+key);
        if (!StringUtil.isNotNull(key)) {
            return sessions;
        }

        List<Session> temp = new ArrayList<>();
        for (Session bean : sessions) {
            String name = "";
            if (bean.getType() == 0) {//单人
                UserInfo finfo = userDao.findUserInfo(bean.getFrom_uid());
                name = finfo.getName4Show();
            } else if (bean.getType() == 1) {//群
                //Group ginfo = msgDao.getGroup4Id(bean.getGid());
                name = msgDao.getGroupName(bean.getGid());

            }

            if (StringUtil.isNotNull(name) && (name.contains(key))) {
//                LogUtil.getLog().e("====name==="+name);
                bean.setUnread_count(0);
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
        List<Session> temp = searchSessionBykey(sessionsList, MsgForwardActivity.searchKey);
        adapter.bindData(temp);
        ui.listView.init(adapter);

    }

    /**
     * 判断是否是管理员
     */
    private boolean isAdministrators(Group group) {
        boolean isManager = false;
        if (group.getViceAdmins() != null && group.getViceAdmins().size() > 0) {
            for (Long user : group.getViceAdmins()) {
                if (user.equals(UserAction.getMyId())) {
                    isManager = true;
                    break;
                }
            }
        }
        return isManager;
    }
    /**
     * 判断是否是群主
     */
    private boolean isAdmin(Group group) {
        if (!StringUtil.isNotNull(group.getMaster()))
            return false;
        return group.getMaster().equals("" + UserAction.getMyId());
    }


}
