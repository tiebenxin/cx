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
    private List<Session> ListTwo;//含有将我禁言的数据
    private List<Session> ListThree;//含有个人会话的数据
    private int needRequestNums = 0;//需要http请求的次数
    private int finalRequestNums = 0;//实际http请求的次数
    private SingleMeberInfoBean singleMeberInfoBean;// 单个群成员信息，主要查看是否被单人禁言

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
        ListTwo = new ArrayList<>();
        ListThree = new ArrayList<>();
        //1 先统计全员禁言的群，过滤掉不显示
        for(int i=0; i<sessionsList.size();i++){
            if(!TextUtils.isEmpty(sessionsList.get(i).getGid())){
                Group group = msgDao.groupNumberGet(sessionsList.get(i).getGid());
                if(group.getWordsNotAllowed()==1){
                    ListOne.add(sessionsList.get(i));
                }
            }
            if(sessionsList.get(i).getFrom_uid()!=(-1)){
                ListThree.add(sessionsList.get(i));
            }
        }
        if(ListOne.size()>0){
            sessionsList.removeAll(ListOne);
        }
        //再统计所有个人会话，无需发请求判断是否被禁言，得到最终需要请求的次数
        if(ListThree.size()>0){
            needRequestNums = sessionsList.size()-ListThree.size();
        }
        //2 然后过滤掉将我禁言的群
        for(int i=0; i<sessionsList.size();i++){
            if(!TextUtils.isEmpty(sessionsList.get(i).getGid())){
                getSingleMemberInfo(sessionsList.get(i).getGid(),i);
            }
        }
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
     * 获取单个群成员信息
     * 备注：这里用于查询并过滤将我禁言的群
     */
    private void getSingleMemberInfo(String toGid,int position) {
        new UserAction().getSingleMemberInfo(toGid, Integer.parseInt(UserAction.getMyId() + ""), new CallBack<ReturnBean<SingleMeberInfoBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SingleMeberInfoBean>> call, Response<ReturnBean<SingleMeberInfoBean>> response) {
                super.onResponse(call, response);
                finalRequestNums++;
                if (response != null && response.body() != null && response.body().isOk()) {
                    singleMeberInfoBean = response.body().getData();
                    //被单人禁言的时间
                    if (singleMeberInfoBean.getShutUpDuration() != 0) {
                        ListTwo.add(sessionsList.get(position));
                    }
                    //如果执行到最后一次请求，即全部走完，再判断是否存在需要过滤掉的将我禁言的群
                    if(finalRequestNums == needRequestNums){
                        if(ListTwo.size()>0){
                            sessionsList.removeAll(ListTwo);
                        }
                        List<Session> temp = searchSessionBykey(sessionsList, MsgForwardActivity.searchKey);
                        adapter.bindData(temp);
                        ui.listView.init(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<SingleMeberInfoBean>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show(getActivity(), t.getMessage());
                finalRequestNums++;
                //如果执行到最后一次请求，即全部走完，再判断是否存在需要过滤掉的将我禁言的群
                if(finalRequestNums == needRequestNums){
                    if(ListTwo.size()>0){
                        sessionsList.removeAll(ListTwo);
                    }
                    List<Session> temp = searchSessionBykey(sessionsList, MsgForwardActivity.searchKey);
                    adapter.bindData(temp);
                    ui.listView.init(adapter);
                }
            }
        });
    }


}
