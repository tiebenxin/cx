package com.yanlong.im.chat.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import net.cb.cb.library.view.PopView;
import net.cb.cb.library.zxing.activity.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import io.realm.RealmChangeListener;
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

    private MsgDao msgDao = new MsgDao();
    private UserDao userDao = new UserDao();
    //    private MsgAction msgAction = new MsgAction();
//    private List<Session> listData = new ArrayList<>();
    private MainViewModel viewModel;
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

        mtListView.getLoadView().setStateNormal();
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
        //application的viewModel
        viewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(MyAppLication.getInstance())).get(MainViewModel.class);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onResume() {
        viewModel.isNeedCloseSwipe.setValue(true);
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        MyAppLication.INSTANCE().addSessionChangeListener(sessionChangeListener);
        viewModel.onStart();
        //初始化观察器
        initObserver();
    }

    @Override
    public void onStop() {
        MyAppLication.INSTANCE().removeSessionChangeListener(sessionChangeListener);
        viewModel.onStop();
        super.onStop();
    }

    private ApplicationRepository.SessionChangeListener sessionChangeListener = new ApplicationRepository.SessionChangeListener() {
        @Override
        public void init(RealmResults<Session> sessions) {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void delete(ArrayList<Integer> position, ArrayList<String> sids) {
            viewModel.isNeedCloseSwipe.setValue(true);
        }

        @Override
        public void insert(ArrayList<Integer> position, ArrayList<String> sids) {
            viewModel.isNeedCloseSwipe.setValue(true);
        }

        @Override
        public void update(ArrayList<Integer> position, ArrayList<String> sids) {

        }

        @Override
        public void change(RealmResults<Session> sessions) {
        }
    };

    /**
     * 初始化观察器
     */
    private void initObserver() {
        //监听列表数据变化
        viewModel.sessionMores.addChangeListener(new RealmChangeListener<RealmResults<SessionDetail>>() {
            @Override
            public void onChange(RealmResults<SessionDetail> sessionMore) {
                if (sessionMore != null) {
                    if (viewModel.sessions == null || viewModel.sessions.size() == 0) {
                        mtListView.getListView().getAdapter().notifyDataSetChanged();
                    } else {
                        mtListView.getListView().getAdapter().notifyItemRangeChanged(1, viewModel.sessions.size());
                    }
                }
            }
        });
        //监听删除操作项
        viewModel.currentDeletePosition.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer position) {
                if (position >= 0 && position < viewModel.sessions.size()) {
                    viewModel.deleteItem(position);
                    //通知更新
                    MessageManager.getInstance().notifyRefreshMsg();//更新main界面未读数
                    getView().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                        mtListView.getListView().getAdapter().notifyItemRemoved(position + 1);//范围刷新
                            if (viewModel.sessions != null) {
                                if (viewModel.sessions.size() > 0)
                                    mtListView.getListView().getAdapter().notifyItemRangeChanged(1, viewModel.sessions.size());
                                if (viewModel.sessions.size() == 0) {
                                    mtListView.getListView().getAdapter().notifyDataSetChanged();
                                }
                            }
                        }
                    }, 50);
                }
            }
        });
        viewModel.isNeedCloseSwipe.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean value) {
                if (value) {
//                    if (viewModel.currentSwipeDeletePosition > 0 && viewModel.currentSwipeDeletePosition < mAdapter.getItemCount()) {
//                        mtListView.getListView().getAdapter().notifyItemChanged(viewModel.currentSwipeDeletePosition);
                    mtListView.getListView().getAdapter().notifyDataSetChanged();//TODO
                    //关闭后恢复值
                    viewModel.currentSwipeDeletePosition = -1;
                    viewModel.isNeedCloseSwipe.setValue(false);
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放数据对象
        viewModel.currentDeletePosition.removeObservers(this);
        viewModel.onDestory();
        SocketUtil.getSocketUtil().removeEvent(socketEvent);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventRefreshMainMsg event) {
        if (MessageManager.getInstance().isMessageChange()) {
            MessageManager.getInstance().setMessageChange(false);
            int refreshTag = event.getRefreshTag();
//            if (refreshTag == CoreEnum.ESessionRefreshTag.DELETE) {
//                //阅后即焚 -更新
//                viewModel.updateItemSessionDetail();
//                LogUtil.getLog().d("a==", "MsgMainFragment --删除session");
//                MessageManager.getInstance().deleteSessionAndMsg(event.getUid(), event.getGid());
//                MessageManager.getInstance().notifyRefreshMsg();//更新main界面未读数
//            } else
//
            if (refreshTag == CoreEnum.ESessionRefreshTag.ALL) {
                //阅后即焚 -更新
                viewModel.updateItemSessionDetail();
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

}
