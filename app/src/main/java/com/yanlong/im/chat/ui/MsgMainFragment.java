package com.yanlong.im.chat.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yanlong.im.MainActivity;
import com.yanlong.im.MainViewModel;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.eventbus.EventRefreshMainMsg;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.repository.ApplicationRepository;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.FriendAddAcitvity;
import com.yanlong.im.user.ui.HelpActivity;
import com.yanlong.im.utils.QRCodeManage;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventNetStatus;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.MultiListView;
import net.cb.cb.library.view.PopView;
import net.cb.cb.library.zxing.activity.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;

/**
 * 首页消息
 *
 * @version V1.0
 * @createAuthor （test4j）
 * @createDate 2019-4-12
 * @updateAuthor （Geoff）
 * @updateDate 2019-12-2
 * @description 视频通话
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class MsgMainFragment extends Fragment {

    private View rootView;
    private net.cb.cb.library.view.ActionbarView actionBar;
    private net.cb.cb.library.view.MultiListView mtListView;
    private MsgMainFragmentAdapter mAdapter;

    private LinearLayout viewPopGroup;
    private LinearLayout viewPopAdd;
    private LinearLayout viewPopQr;
    private LinearLayout viewPopHelp;
    private View mHeadView;

    public static int showPosition = 0;//双击未读消息置顶-展示的位置 (切换账号需要重置，防crash)

    private MsgDao msgDao = new MsgDao();
    private UserDao userDao = new UserDao();
    //    private MsgAction msgAction = new MsgAction();
//    private List<Session> listData = new ArrayList<>();
    private MainViewModel viewModel = null;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mAdapter.viewNetwork != null) {
                mAdapter.viewNetwork.setVisibility(View.GONE);
            }
        }
    };

    Runnable showRunnable = new Runnable() {
        @Override
        public void run() {
            //解决断网后又马上连网造成的提示显示异常问题
            if (!NetUtil.isNetworkConnected()) {
                if (mAdapter.viewNetwork != null) {
                    mAdapter.viewNetwork.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    private void findViewsPop(View rootView) {
        viewPopGroup = rootView.findViewById(R.id.view_pop_group);
        viewPopAdd = rootView.findViewById(R.id.view_pop_add);
        viewPopQr = rootView.findViewById(R.id.view_pop_qr);
        viewPopHelp = rootView.findViewById(R.id.view_pop_help);

    }

    private void findViews(View rootView) {
        actionBar = rootView.findViewById(R.id.actionBar);
        mtListView = rootView.findViewById(R.id.mtListView);

        mHeadView = View.inflate(getContext(), R.layout.view_head_main_message, null);

        View pView = getLayoutInflater().inflate(R.layout.view_pop_main, null);
        findViewsPop(pView);
        popView.init(getContext(), pView);
    }

    private PopView popView = new PopView();
    private SocketEvent socketEvent;

    private void initEvent() {
        mAdapter = new MsgMainFragmentAdapter(getActivity(), viewModel, mHeadView);
        mtListView.init(mAdapter);
        mtListView.setEvent(new MultiListView.Event() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                MyAppLication.INSTANCE().repository.loadMoreSessions();
            }

            @Override
            public void onLoadFail() {

            }
        });

        SocketUtil.getSocketUtil().addEvent(socketEvent = new SocketEvent() {
            @Override
            public void onHeartbeat() {

            }

            @Override
            public void onACK(MsgBean.AckMessage bean) {

            }

            @Override
            public void onMsg(MsgBean.UniversalMessage bean) {

            }

            @Override
            public void onSendMsgFailure(MsgBean.UniversalMessage.Builder bean) {

            }

            @Override
            public void onLine(final boolean state) {
                getActivityMe().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.getLog().d("tyad", "run: state=" + state);
                        AppConfig.setOnline(state);
                        actionBar.getLoadBar().setVisibility(state ? View.GONE : View.VISIBLE);
                        if (!state && getActivityMe().isActivityStop()) {
                            return;
                        }
                        resetNetWorkView(state ? CoreEnum.ENetStatus.SUCCESS_ON_SERVER : CoreEnum.ENetStatus.ERROR_ON_SERVER);
                        viewModel.onlineState.setValue(state);
                    }
                });

            }
        });

        actionBar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {

            }

            @Override
            public void onRight() {
                int x = DensityUtil.dip2px(getContext(), -92);
                int y = DensityUtil.dip2px(getContext(), 5);
                popView.getPopupWindow().showAsDropDown(actionBar.getBtnRight(), x, y);

            }
        });

        viewPopAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FriendAddAcitvity.class));
                popView.dismiss();
            }
        });
        viewPopGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GroupCreateActivity.class));
                popView.dismiss();
            }
        });
        viewPopQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // 申请权限
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CaptureActivity.REQ_PERM_CAMERA);
                    return;
                }
                // 二维码扫码
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent, CaptureActivity.REQ_QR_CODE);

                popView.dismiss();
            }
        });
        viewPopHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), HelpActivity.class));
                popView.dismiss();
            }
        });
    }

    public void hidePopView() {
        if (popView != null) {
            popView.dismiss();
        }
    }

    private void resetNetWorkView(@CoreEnum.ENetStatus int status) {
        try {
//            LogUtil.getLog().i(MsgMainFragment.class.getSimpleName(), "resetNetWorkView--status=" + status);
            if (mAdapter == null || mAdapter.viewNetwork == null) {
                return;
            }
            switch (status) {
                case CoreEnum.ENetStatus.ERROR_ON_NET:
                    if (mAdapter.viewNetwork.getVisibility() == View.GONE) {
                        mAdapter.viewNetwork.postDelayed(showRunnable, 15 * 1000);
                    }
                    break;
                case CoreEnum.ENetStatus.SUCCESS_ON_NET:
                    if (NetUtil.isNetworkConnected()) {//无网络链接，无效指令
                        mAdapter.viewNetwork.setVisibility(View.GONE);
                    }
                    removeHandler();
                    break;
                case CoreEnum.ENetStatus.ERROR_ON_SERVER:
                    if (mAdapter.viewNetwork.getVisibility() == View.GONE) {
                        mAdapter.viewNetwork.postDelayed(showRunnable, 10 * 1000);
                    }
                    break;
                case CoreEnum.ENetStatus.SUCCESS_ON_SERVER:
                    mAdapter.viewNetwork.setVisibility(View.GONE);
                    removeHandler();
                    break;
                default:
                    mAdapter.viewNetwork.setVisibility(View.GONE);
                    removeHandler();
                    break;

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void removeHandler() {
        if (mAdapter.viewNetwork != null && runnable != null) {
            mAdapter.viewNetwork.removeCallbacks(runnable);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
            QRCodeManage.goToPage(getContext(), scanResult);
        }
    }

    public MsgMainFragment() {
        // Required empty public constructor
    }


    public static MsgMainFragment newInstance() {
        MsgMainFragment fragment = new MsgMainFragment();
        Bundle args = new Bundle();
    /*    args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MainActivity的viewModel
        viewModel = new MainViewModel();
        EventBus.getDefault().register(this);
        MyAppLication.INSTANCE().addSessionChangeListener(sessionChangeListener);
        viewModel.initSession(null);
        //初始化观察器
        initObserver();

    }


    @Override
    public void onResume() {
        viewModel.isNeedCloseSwipe.setValue(true);
        //需要触发下，Fragment可能被设置了预加载
        if(!viewModel.isShowLoadAnim.getValue())viewModel.isShowLoadAnim.setValue(false);
        super.onResume();
    }

    private ApplicationRepository.SessionChangeListener sessionChangeListener = new ApplicationRepository.SessionChangeListener() {
        @Override
        public void init(RealmResults<Session> sessions, List<String> sids) {
            //每次session初始化，都需要重新赋值
            viewModel.initSession(sids);
            Log.e("raleigh_test","init sessions size="+sessions.size());
        }

        @Override
        public void delete(int[] positions) {
            viewModel.isNeedCloseSwipe.setValue(true);
        }

        @Override
        public void insert(int[] positions, List<String> sids) {
            viewModel.isNeedCloseSwipe.setValue(true);
            viewModel.allSids.addAll(sids);
            viewModel.isAllSidsChange.setValue(true);
            Log.e("raleigh_test","insert size="+viewModel.sessions.size());
        }

        @Override
        public void update(int[] positions, List<String> sids) {
            mtListView.getListView().getAdapter().notifyItemRangeChanged(1, viewModel.sessions.size());
        }
    };
    private OrderedRealmCollectionChangeListener sessionMoresListener = new OrderedRealmCollectionChangeListener<RealmResults<SessionDetail>>() {
        @Override
        public void onChange(RealmResults<SessionDetail> sessionDetails, OrderedCollectionChangeSet changeSet) {
            /***必须先更新位置信息*********************************************************/
            viewModel.sessionMoresPositions.clear();
            for (int i = 0; i < viewModel.sessionMores.size(); i++) {
                viewModel.sessionMoresPositions.put(viewModel.sessionMores.get(i).getSid(), i);
            }
            if(viewModel.isShowLoadAnim.getValue()&&sessionDetails.size() >= Math.min(50,viewModel.sessions.size())){
                //只有第一次加载才会出现，有50条（可调整）数据，短时间内应该是看不到白板情况，可关闭进度条了
                viewModel.isShowLoadAnim.setValue(false);
            }
            /*****第一次初始化******************************************************************************************/
            if(changeSet.getState()== OrderedCollectionChangeSet.State.INITIAL){
                mtListView.getListView().getAdapter().notifyDataSetChanged();
                Log.e("raleigh_test","init sessionDetails size="+sessionDetails.size());
            }

            /*****增加了数据-需要更新全部*******************************************************************************************/
            if (changeSet.getInsertionRanges().length > 0) {
                mtListView.getListView().getAdapter().notifyItemRangeChanged(1, viewModel.sessions.size());
                Log.e("raleigh_test","add sessionDetails size="+sessionDetails.size());
            }
            /*****删除了数据，*******************************************************************************************/
            if (changeSet.getDeletionRanges().length > 0) {
                if(viewModel.sessions.size()==0){
                    mtListView.getListView().getAdapter().notifyDataSetChanged();
                }else{
                    mtListView.getListView().getAdapter().notifyItemRangeChanged(1, viewModel.sessions.size());
                }
                Log.e("raleigh_test","delete sessionDetails size="+sessionDetails.size());
            }
            /*****更新了数据*******************************************************************************************/
            int[] modifications = changeSet.getChanges();
            //获取更新信息
            for (int position : modifications) {
                String sid = sessionDetails.get(position).getSid();
                if (MyAppLication.INSTANCE().repository.sessionSidPositons.containsKey(sid)) {
                    int startId = MyAppLication.INSTANCE().repository.sessionSidPositons.get(sid);
                    mtListView.getListView().getAdapter().notifyItemRangeChanged(startId + 1, 1);
                } else {
                    mtListView.getListView().getAdapter().notifyItemRangeChanged(1, viewModel.sessions.size());
                }
                Log.e("raleigh_test","change sessionDetails size="+sessionDetails.size());
            }
            //详情未全部加载时，1秒后再次请求
//            if(sessionDetails.size() < viewModel.sessions.size()){
//                handler.removeCallbacks(updateSessionMoreAgain);
//                handler.postDelayed(updateSessionMoreAgain,1000);
//            }
        }
    };
    private Runnable updateSessionMoreAgain=new Runnable() {
        @Override
        public void run() {
            viewModel.updateSessionMore();
            if(viewModel.sessionMores!=null)
                viewModel.sessionMores.addChangeListener(sessionMoresListener);
        }
    };

    private Handler handler=new Handler();
    /**
     * 初始化观察器
     */
    private void initObserver() {
        //监听删除操作项
        viewModel.currentDeleteSid.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String sid) {
                //删除session
                MyAppLication.INSTANCE().repository.deleteSession(sid);
            }
        });
        viewModel.isNeedCloseSwipe.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean value) {
                if (value) {
//                    if (viewModel.currentSwipeDeletePosition > 0 && viewModel.currentSwipeDeletePosition < mAdapter.getItemCount()) {
//                        mtListView.getListView().getAdapter().notifyItemChanged(viewModel.currentSwipeDeletePosition);
                    mtListView.getListView().getAdapter().notifyDataSetChanged();//TODO
                    viewModel.isNeedCloseSwipe.setValue(false);
                }
            }
        });
        viewModel.isAllSidsChange.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                viewModel.updateSessionMore();
                //监听列表数据变化
                if(viewModel.sessionMores!=null)
                    viewModel.sessionMores.addChangeListener(sessionMoresListener);

            }
        });
        viewModel.isShowLoadAnim.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean){//显示列表加载动画
                    mtListView.getLoadView().setStateLoading();
                    //必须在setEvent后调用
                    mtListView.getSwipeLayout().setEnabled(true);
                }else{//关闭列表加载动画
                    mtListView.getLoadView().setStateNormal();
                    //必须在setEvent后调用
                    mtListView.getSwipeLayout().setEnabled(false);
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        MyAppLication.INSTANCE().removeSessionChangeListener(sessionChangeListener);
        super.onDestroy();
        //释放数据对象
        viewModel.onDestory(this);
        SocketUtil.getSocketUtil().removeEvent(socketEvent);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventRefreshMainMsg event) {
        if (MessageManager.getInstance().isMessageChange()) {
            MessageManager.getInstance().setMessageChange(false);
            int refreshTag = event.getRefreshTag();
            //刷新 单个记录 by sid
            if (refreshTag == CoreEnum.ESessionRefreshTag.SINGLE) {
                String sid=event.getSid();//刷新页面-暂时是为了及时刷新草稿用的
                if (!TextUtils.isEmpty(sid)&&MyAppLication.INSTANCE().repository.sessionSidPositons.containsKey(sid)) {
                    int startId = MyAppLication.INSTANCE().repository.sessionSidPositons.get(sid);
                    mtListView.getListView().getAdapter().notifyItemRangeChanged(startId + 1, 1);
                }
//            }else if (refreshTag == CoreEnum.ESessionRefreshTag.ALL) {
//                // 更新所有详情
//                MyAppLication.INSTANCE().repository.updateSessionDetail(viewModel.allSids.toArray(new String[viewModel.allSids.size()]));
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgm_msg_main, null);
        ViewGroup.LayoutParams layparm = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(layparm);
        findViews(rootView);
        initEvent();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //  initEvent();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventNetStatus(EventNetStatus event) {
        resetNetWorkView(event.getStatus());
    }

    private MainActivity mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();

    }

    private MainActivity getActivityMe() {
        if (mainActivity == null) {
            return (MainActivity) getActivity();
        }
        return mainActivity;
    }

    //滑动到未读消息项
    public void moveToUnread() {
        List<Integer> positionList = new ArrayList<>();//保存有未读消息的位置
        if (viewModel.sessions.size() > 0) {
            for (int i = 0; i < viewModel.sessions.size(); i++) {
                Session bean = viewModel.sessions.get(i);
                if (bean.getUnread_count() > 0) {
                    positionList.add(i);
                }
            }
        }
        //从首位置开始，多次双击，按未读消息会话的顺序滑动，依次滑动至最后一项，然后重置位置
        if (positionList.size() > 0) {
            mtListView.getLayoutManager().scrollToPositionWithOffset(positionList.get(showPosition) + 1, 0);
            if (showPosition < positionList.size() - 1) {
                showPosition++;
            } else {
                showPosition = 0;
            }
        }
    }
}
