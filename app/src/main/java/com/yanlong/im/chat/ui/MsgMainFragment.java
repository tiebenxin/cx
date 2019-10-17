package com.yanlong.im.chat.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.GroupImageHead;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.FriendAddAcitvity;
import com.yanlong.im.user.ui.HelpActivity;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.GroupHeadImageUtil;
import com.yanlong.im.utils.QRCodeManage;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventNetStatus;

import com.yanlong.im.chat.eventbus.EventRefreshMainMsg;

import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.PopView;
import net.cb.cb.library.view.StrikeButton;
import net.cb.cb.library.zxing.activity.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/***
 * 首页消息
 */
public class MsgMainFragment extends Fragment {
    private View rootView;
    private net.cb.cb.library.view.ActionbarView actionBar;
    private net.cb.cb.library.view.ClearEditText edtSearch;
    private View viewSearch;
    private net.cb.cb.library.view.MultiListView mtListView;

    private LinearLayout viewPopGroup;
    private LinearLayout viewPopAdd;
    private LinearLayout viewPopQr;
    private LinearLayout viewPopHelp;
    private View viewNetwork;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewNetwork.setVisibility(SocketUtil.getSocketUtil().getOnLineState() ? View.GONE : View.VISIBLE);

        }
    };

    //自动寻找控件
    private void findViewsPop(View rootView) {
        viewPopGroup = (LinearLayout) rootView.findViewById(R.id.view_pop_group);
        viewPopAdd = (LinearLayout) rootView.findViewById(R.id.view_pop_add);
        viewPopQr = (LinearLayout) rootView.findViewById(R.id.view_pop_qr);
        viewPopHelp = (LinearLayout) rootView.findViewById(R.id.view_pop_help);

    }

    //自动寻找控件
    private void findViews(View rootView) {
        actionBar = (net.cb.cb.library.view.ActionbarView) rootView.findViewById(R.id.actionBar);
        edtSearch = (net.cb.cb.library.view.ClearEditText) rootView.findViewById(R.id.edt_search);
        viewSearch = rootView.findViewById(R.id.view_search);
        mtListView = (net.cb.cb.library.view.MultiListView) rootView.findViewById(R.id.mtListView);
        viewNetwork = rootView.findViewById(R.id.view_network);

        View pView = getLayoutInflater().inflate(R.layout.view_pop_main, null);
        findViewsPop(pView);
        popView.init(getContext(), pView);
    }


    private PopView popView = new PopView();
    private SocketEvent socketEvent;

    //自动生成的控件事件
    private void initEvent() {
        mtListView.init(new RecyclerViewAdapter());
        mtListView.getLoadView().setStateNormal();

        //滚动处理-------------------------------------
        //1. 需要整理成util
        //2.需要创建一个相对layout,并且改变控件顺序,设置 mtListView底部对齐

        mtListView.getListView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                int vset = rv.computeVerticalScrollOffset();

                if (viewSearch.getTag() != null && vset > (int) viewSearch.getTag()) {
                    vset = (int) viewSearch.getTag();
                }

                viewSearch.setTranslationY(-vset);


            }
        });
        final Runnable uiRun = new Runnable() {
            @Override
            public void run() {

                ViewGroup.LayoutParams lp = viewSearch.getLayoutParams();
                int h = viewSearch.getMeasuredHeight();
                if (lp instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams lps = ((ViewGroup.MarginLayoutParams) lp);
                    h += lps.topMargin + lps.bottomMargin;
                    viewSearch.setTag(h);
                }
                mtListView.getListView().setPadding(0, h, 0, 0);
                //这里marpin设置为-h
                ViewGroup.MarginLayoutParams lp2 = (ViewGroup.MarginLayoutParams) mtListView.getLayoutParams();
                lp2.topMargin = -h;
                mtListView.setLayoutParams(lp2);

                mtListView.getListView().setClipToPadding(false);

                mtListView.getListView().scrollBy(0, -h);

            }
        };

        viewSearch.post(uiRun);

        //-------------------------------

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
                        Log.d("tyad", "run: state" + state);
                        actionBar.getLoadBar().setVisibility(state ? View.GONE : View.VISIBLE);
                        // actionBar.setTitle(state ? "消息" : "消息(连接中...)");
                        actionBar.setTitle(state ? "消息" : "消息");
                        resetNetWorkView(state ? CoreEnum.ENetStatus.SUCCESS_ON_SERVER : CoreEnum.ENetStatus.ERROR_ON_SERVER);


                    }
                });

            }
        });
        //socketEvent.onLine( SocketUtil.getSocketUtil().getOnLineState());


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

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    taskSearch();
                } else if (event != null && (KeyEvent.KEYCODE_ENTER == event.getKeyCode() || KeyEvent.ACTION_DOWN == event.getAction())) {
                    taskSearch();
                }
                return false;
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtSearch.getText().toString().length() == 0) {
                    isSearchMode = false;
                    taskListData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    public void hidePopView() {
        if (popView != null) {
            popView.dismiss();
        }
    }


    private void resetNetWorkView(@CoreEnum.ENetStatus int status) {
        LogUtil.getLog().i(MsgMainFragment.class.getSimpleName(), "resetNetWorkView--status=" + status);
        switch (status) {
            case CoreEnum.ENetStatus.ERROR_ON_NET:
                viewNetwork.setVisibility(View.VISIBLE);
                break;
            case CoreEnum.ENetStatus.SUCCESS_ON_NET:
                if (NetUtil.isNetworkConnected()) {//无网络链接，无效指令
                    viewNetwork.setVisibility(View.GONE);
                }
                removeHandler();
                break;
            case CoreEnum.ENetStatus.ERROR_ON_SERVER:
                if (viewNetwork.getVisibility() == View.GONE) {
                    viewNetwork.postDelayed(runnable, 10 * 1000);
                }
                break;
            case CoreEnum.ENetStatus.SUCCESS_ON_SERVER:
                viewNetwork.setVisibility(View.GONE);
                removeHandler();
                break;
            default:
                viewNetwork.setVisibility(View.GONE);
                removeHandler();
                break;

        }
    }

    private void removeHandler() {
        if (viewNetwork != null && runnable != null) {
            viewNetwork.removeCallbacks(runnable);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //7.18 进来的时候显示有网的状态
        //  socketEvent.onLine(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
            QRCodeManage.goToPage(getContext(), scanResult);

//            QRCodeBean bean = QRCodeManage.getQRCodeBean(getActivityMe(), scanResult);
//            QRCodeManage.goToActivity(getActivityMe(), bean);
            //将扫描出的信息显示出来
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
        if (getArguments() != null) {
/*            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
        EventBus.getDefault().register(this);
        taskListData();
//        getSessionsAndRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketUtil.getSocketUtil().removeEvent(socketEvent);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventRefreshMainMsg event) {
        if (MessageManager.getInstance().isMessageChange()) {
            MessageManager.getInstance().setMessageChange(false);
            if (event.getRefreshTag() == CoreEnum.ESessionRefreshTag.ALL) {
//                System.out.println(MsgMainFragment.class.getSimpleName() + "-- 刷新Session-ALL");
                taskListData();
            } else {
                refreshPosition(event.getGid(), event.getUid(), event.getMsgAllBean(), event.getSession(), event.isRefreshTop());
                System.out.println(MsgMainFragment.class.getSimpleName() + "-- 刷新Session-SINGLE");

            }
        }
    }


    /*
     * 刷新单一位置
     * */
    @SuppressLint("CheckResult")
    private void refreshPosition(String gid, Long uid, MsgAllBean bean, Session s, boolean isRefreshTop) {
        Observable.just(0)
                .map(new Function<Integer, Session>() {
                    @Override
                    public Session apply(Integer integer) throws Exception {
                        if (s == null) {
                            Session session = msgDao.sessionGet(gid, uid);
                            if (bean != null) {
                                session.setMessage(bean);
                            }
                            prepareSession(session, false);
                            return session;
                        } else {
                            if (bean != null) {
                                s.setMessage(bean);
                            }
                            prepareSession(s, true);
                            return s;
                        }

                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<Session>empty())
                .subscribe(new Consumer<Session>() {
                    @Override
                    public void accept(Session session) throws Exception {
                        if (listData != null) {
                            int index = listData.indexOf(session);
                            if (index >= 0) {
                                Session s = listData.get(index);
                                if (isRefreshTop) {//是否刷新置顶
                                    if (session.getIsTop() == 1) {//修改了置顶状态
//                                        System.out.println(MsgMainFragment.class.getSimpleName() + "--刷新置顶消息 旧session=" + s.getIsTop() + "--新session=" + session.getIsTop());
                                        listData.remove(index);
                                        listData.add(0, session);//放在首位
                                        mtListView.getListView().getAdapter().notifyItemRangeChanged(0, index + 1);//范围刷新
                                    } else {//取消置顶
                                        listData.set(index, session);
                                        sortSession(index == 0);
                                        int newIndex = listData.indexOf(session);//获取重排后新位置
                                        int start = index > newIndex ? newIndex : index;//谁小，取谁
                                        int count = Math.abs(newIndex - index) + 1;
                                        mtListView.getListView().getAdapter().notifyItemRangeChanged(start, count);////范围刷新,刷新旧位置和新位置之间即可
//                                        System.out.println(MsgMainFragment.class.getSimpleName() + "--刷新取消置顶消息--start=" + start + "--count=" + count);
                                    }
                                } else {
//                                    System.out.println(MsgMainFragment.class.getSimpleName() + "--刷新普通消息 旧session=" + s.getSid() + "--新session=" + session.getSid());
                                    listData.set(index, session);
                                    if (s != null && s.getUp_time().equals(session.getUp_time())) {//时间未更新，所以不要重新排序
                                        mtListView.getListView().getAdapter().notifyItemChanged(index, index);
                                    } else {//有时间更新,需要重排
                                        sortSession(index == 0);
                                        int newIndex = listData.indexOf(session);
                                        int start = index > newIndex ? newIndex : index;//谁小，取谁
                                        int count = Math.abs(newIndex - index) + 1;
                                        mtListView.getListView().getAdapter().notifyItemRangeChanged(start, count);//范围刷新
//                                        System.out.println(MsgMainFragment.class.getSimpleName() + "--时间刷新重排--start=" + start + "--count=" + count);
                                    }
                                }
                            } else {
//                                System.out.println(MsgMainFragment.class.getSimpleName() + "--刷新普通消息0" + "--新session=" + session.getSid());
                                int position = insertSession(session);
                                if (position == 0) {
//                                    mtListView.getListView().getAdapter().notifyDataSetChanged();
                                    mtListView.notifyDataSetChange();
                                } else {
                                    mtListView.getListView().getAdapter().notifyItemRangeInserted(position, 1);
                                    mtListView.getListView().scrollToPosition(0);
                                }
                            }
                        } else {
//                            System.out.println(MsgMainFragment.class.getSimpleName() + "--刷新普通消息null" + "--新session=" + session.getSid());
//                                listData.add(0, session);//新会话，插入刷新，考虑置顶
                            int position = insertSession(session);
                            if (position == 0) {
//                                mtListView.getListView().getAdapter().notifyDataSetChanged();
                                mtListView.notifyDataSetChange();

                            } else {
                                mtListView.getListView().getAdapter().notifyItemRangeInserted(position, 1);
                                mtListView.getListView().scrollToPosition(0);
                            }
                        }
                    }
                });
    }


    /*
     * 重新排序,置顶和非置顶分别重排
     * @param isTop 当前要更新的session 是否在列表第一位置（置顶）
     * */
    private void sortSession(boolean isTop) {
        if (listData != null) {
            int len = listData.size();
            if (len > 0) {
                Session first = null;
                if (!isTop) {
                    first = listData.get(0);
                } else {
                    if (len >= 2) {
                        first = listData.get(1);
                    }
                }
                if (first != null && first.getIsTop() == 1) {//有置顶
                    List<Session> topList = new ArrayList<>();
                    List<Session> list = new ArrayList<>();
                    for (int i = 0; i < len; i++) {
                        Session session = listData.get(i);
                        if (session.getIsTop() == 1) {
                            topList.add(session);
                        } else {
                            list.add(session);
                        }
                    }
                    listData.clear();
                    if (topList.size() > 0) {
                        Collections.sort(topList);
                        listData.addAll(topList);
                    }
                    if (list.size() > 0) {
                        Collections.sort(list);
                        listData.addAll(list);
                    }
                } else {//无置顶
                    Collections.sort(listData);
                }
            }
        }
    }

    /*
     * 插入位置需要考虑置顶
     * */
    private int insertSession(Session s) {
        int position = 0;//需要插入位置
        if (listData != null) {
            int len = listData.size();
            boolean hasTop = false;
            for (int i = 0; i < len; i++) {
                Session session = listData.get(i);
                if (session.getIsTop() != 1) {
                    position = i;
                    break;//结束循环
                } else {
                    hasTop = true;
                }
            }
            if (hasTop && position == 0) {//全是置顶
                position = len;
            }
            listData.add(position, s);
        } else {
            listData = new ArrayList<>();
            listData.add(s);
        }

        return position;
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

    @Override
    public void onResume() {
        super.onResume();
//        taskListData();
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


    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {


        @Override
        public int getItemCount() {
            return listData == null ? 0 : listData.size();
        }

        @Override
        public void onBindViewHolder(@NonNull RCViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (payloads.isEmpty()) {
                onBindViewHolder(holder, position);
            } else {
                int type = (int) payloads.get(0);
                switch (type) {
                    case 0:
                        break;
                    case 1:
                        break;
                }
                onBindViewHolder(holder, position);
            }
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(final RCViewHolder holder, int position) {
            final Session bean = listData.get(position);
            String icon = bean.getAvatar();
            String title = bean.getName();
            MsgAllBean msginfo = bean.getMessage();
            String name = bean.getSenderName();

            String info = "";
            if (msginfo != null) {
                info = msginfo.getMsg_typeStr();
            }
            if (bean.getType() == 0) {//单人
                if (StringUtil.isNotNull(bean.getDraft())) {
                    //                    info = "草稿:" + bean.getDraft();
                    SpannableStringBuilder style = new SpannableStringBuilder();
                    style.append("[草稿]" + bean.getDraft());
                    ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                    style.setSpan(protocolColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.txtInfo.setText(style);
                } else {
                    holder.txtInfo.setText(info);
                }

                Glide.with(getActivity()).load(icon)
                        .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);

            } else if (bean.getType() == 1) {//群

                if (!TextUtils.isEmpty(info) && !TextUtils.isEmpty(name)) {
                    info = name + info;
                }
                int type = bean.getMessageType();
                switch (type) {
                    case 0:
                        if (StringUtil.isNotNull(bean.getAtMessage())) {
                            if (msginfo != null && msginfo.getMsg_type() == ChatEnum.EMessageType.AT) {
                                SpannableStringBuilder style = new SpannableStringBuilder();
                                style.append("[有人@你]" + bean.getAtMessage());
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                holder.txtInfo.setText(style);
                            } else {
                                SpannableStringBuilder style = new SpannableStringBuilder();
                                style.append("[有人@你]" + info);
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                holder.txtInfo.setText(style);
                            }
                        }
                        break;
                    case 1:
                        if (StringUtil.isNotNull(bean.getAtMessage())) {
                            if (msginfo == null || msginfo.getMsg_type() == null) {
                                return;
                            }
                            if (msginfo.getMsg_type() == ChatEnum.EMessageType.AT) {
                                SpannableStringBuilder style = new SpannableStringBuilder();
                                style.append("[@所有人]" + bean.getAtMessage());
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                holder.txtInfo.setText(style);
                            } else {
                                SpannableStringBuilder style = new SpannableStringBuilder();
                                style.append("[@所有人]" + info);
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                holder.txtInfo.setText(style);
                            }
                        }
                        break;
                    case 2:
                        if (StringUtil.isNotNull(bean.getDraft())) {
//                            info = "草稿:" + bean.getDraft();
                            SpannableStringBuilder style = new SpannableStringBuilder();
                            style.append("[草稿]" + bean.getDraft());
                            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                            style.setSpan(protocolColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            holder.txtInfo.setText(style);
                        } else {
                            holder.txtInfo.setText(info);
                        }
                        break;
                    default:
                        holder.txtInfo.setText(info);
                        break;
                }

                if (StringUtil.isNotNull(icon)) {
                    Glide.with(getActivity()).load(icon)
                            .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);
                } else {
                    if (bean.getType() == 1) {
                        String imgUrl = "";
                        try {
                            imgUrl = ((GroupImageHead) DaoUtil.findOne(GroupImageHead.class, "gid", bean.getGid())).getImgHeadUrl();
                        } catch (Exception e) {
                            creatAndSaveImg(bean, holder.imgHead);
                        }

                        Log.e("TAG", "----------" + imgUrl.toString());
                        if (StringUtil.isNotNull(imgUrl)) {
                            Glide.with(getActivity()).load(imgUrl)
                                    .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);
                        } else {

                            creatAndSaveImg(bean, holder.imgHead);

                        }
                    } else {
                        Glide.with(getActivity()).load(icon)
                                .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);
                    }
                }


            }


            holder.txtName.setText(title);
            holder.sb.setButtonBackground(R.color.transparent);
            holder.sb.setNum(bean.getUnread_count(), false);
            if (bean.getIsMute() == 1) {
                if (msginfo != null && !msginfo.isRead()) {
                    holder.iv_disturb_unread.setVisibility(View.VISIBLE);
                    holder.iv_disturb_unread.setBackgroundResource(R.drawable.shape_disturb_unread_bg);
                } else {
                    holder.iv_disturb_unread.setVisibility(View.GONE);
                }
            } else {
                holder.iv_disturb_unread.setVisibility(View.GONE);
            }

            holder.txtTime.setText(TimeToString.getTimeWx(bean.getUp_time()));


            holder.viewIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), ChatActivity.class)
                            .putExtra(ChatActivity.AGM_TOUID, bean.getFrom_uid())
                            .putExtra(ChatActivity.AGM_TOGID, bean.getGid())
                    );
//                    if (bean.getUnread_count() > 0) {
//                        MessageManager.getInstance().setMessageChange(true);
//                    }

                }
            });
            holder.btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.swipeLayout.quickClose();
                    taskDelSession(bean.getFrom_uid(), bean.getGid());
                }
            });
//            holder.viewIt.setBackgroundColor(bean.getIsTop() == 0 ? Color.WHITE : Color.parseColor("#f1f1f1"));
            holder.viewIt.setBackgroundColor(bean.getIsTop() == 0 ? Color.WHITE : Color.parseColor("#ececec"));
            holder.iv_disturb.setVisibility(bean.getIsMute() == 0 ? View.GONE : View.VISIBLE);

        }

        private void creatAndSaveImg(Session bean, ImageView imgHead) {
            Group gginfo = msgDao.getGroup4Id(bean.getGid());
            if (gginfo != null) {
                int i = gginfo.getUsers().size();
                i = i > 9 ? 9 : i;
                //头像地址
                String url[] = new String[i];
                for (int j = 0; j < i; j++) {
                    UserInfo userInfo = gginfo.getUsers().get(j);
                    url[j] = userInfo.getHead();
                }
                File file = GroupHeadImageUtil.synthesis(getContext(), url);
                Glide.with(getActivity()).load(file)
                        .apply(GlideOptionsUtil.headImageOptions()).into(imgHead);

                MsgDao msgDao = new MsgDao();
                msgDao.groupHeadImgCreate(gginfo.getGid(), file.getAbsolutePath());
            }
        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(getLayoutInflater().inflate(R.layout.item_msg_session, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private ImageView imgHead;
            private StrikeButton sb;

            private View viewIt;
            private Button btnDel;
            private SwipeMenuLayout swipeLayout;
            private TextView txtName;
            private TextView txtInfo;
            private TextView txtTime;
            private final ImageView iv_disturb, iv_disturb_unread;
//            private final TextView tv_num;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                imgHead = convertView.findViewById(R.id.img_head);
                swipeLayout = convertView.findViewById(R.id.swipeLayout);
                sb = convertView.findViewById(R.id.sb);
                viewIt = convertView.findViewById(R.id.view_it);
                btnDel = convertView.findViewById(R.id.btn_del);
                txtName = convertView.findViewById(R.id.txt_name);
                txtInfo = convertView.findViewById(R.id.txt_info);
                txtTime = convertView.findViewById(R.id.txt_time);
                iv_disturb = convertView.findViewById(R.id.iv_disturb);
//                tv_num = convertView.findViewById(R.id.tv_num);
                iv_disturb_unread = convertView.findViewById(R.id.iv_disturb_unread);
            }

        }

    }

    private String creatAndSaveImg(String gid) {
        Group gginfo = msgDao.getGroup4Id(gid);
        int i = gginfo.getUsers().size();
        i = i > 9 ? 9 : i;
        //头像地址
        String url[] = new String[i];
        for (int j = 0; j < i; j++) {
            UserInfo userInfo = gginfo.getUsers().get(j);
//            if (j == i - 1) {
//                name += userInfo.getName();
//            } else {
//                name += userInfo.getName() + "、";
//            }
            url[j] = userInfo.getHead();
        }
        File file = GroupHeadImageUtil.synthesis(getContext(), url);
        MsgDao msgDao = new MsgDao();
        msgDao.groupHeadImgCreate(gginfo.getGid(), file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    private MsgDao msgDao = new MsgDao();
    private UserDao userDao = new UserDao();
    private UserAction userAction = new UserAction();
    private MsgAction msgAction = new MsgAction();
    private List<Session> listData = new ArrayList<>();
    private List<Object> groups = new ArrayList<>();
    private List<MsgAllBean> msgAllBeansList = new ArrayList<>();

    private int didIndex = 0;//当前缓存的顺序
    private List<String> dids = new ArrayList<>();//缓存所有未缓存的信息

    @SuppressLint("CheckResult")
    private void taskListData() {
        if (isSearchMode) {
            return;
        }
//        System.out.println("MsgMainFragment --开始获取session数据" + System.currentTimeMillis());
        Observable.just(0)
                .map(new Function<Integer, List<Session>>() {
                    @Override
                    public List<Session> apply(Integer integer) throws Exception {
                        listData = msgDao.sessionGetAll(true);
//                        System.out.println("MsgMainFragment --结束获取session数据" + System.currentTimeMillis());
                        doListDataSort();
//                        System.out.println("MsgMainFragment --结束准备session数据" + System.currentTimeMillis());
                        return listData;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<Session>>empty())
                .subscribe(new Consumer<List<Session>>() {
                    @Override
                    public void accept(List<Session> list) throws Exception {
                        mtListView.notifyDataSetChange();
//                        System.out.println("MsgMainFragment --获取session数据后刷新" + System.currentTimeMillis());
                    }
                });

    }

    private void getSessionsAndRefresh() {
        listData = MessageManager.getInstance().getCacheSession();
        mtListView.notifyDataSetChange();

    }

    private void doListDataSort() {
        if (listData != null) {
            int len = listData.size();
            for (int i = 0; i < len; i++) {
                Session session = listData.get(i);
                prepareSession(session, false);

            }
        }
    }

    /*
     * 准备session
     * @param isNew 是否是新数据，（主要针对置顶，免打扰）
     * */
    private void prepareSession(Session session, boolean isNew) {
        if (session == null) {
            return;
        }
        if (session.getType() == 1) {
            Group group = msgDao.getGroup4Id(session.getGid());
            if (group != null) {
                session.setName(msgDao.getGroupName(group));
                session.setIsMute(group.getNotNotify());
                session.setHasInitDisturb(true);
                session.setAvatar(group.getAvatar());

            } else {
                session.setName(msgDao.getGroupName(session.getGid()));
            }
            MsgAllBean msg = session.getMessage();
            if (msg == null) {
                msg = msgDao.msgGetLast4Gid(session.getGid());
            }
            if (msg != null) {
                session.setMessage(msg);
                if (msg.getMsg_type() == ChatEnum.EMessageType.NOTICE || msg.getMsg_type() == ChatEnum.EMessageType.MSG_CENCAL) {//通知不要加谁发的消息
                    session.setSenderName("");
                } else {
                    if (msg.getFrom_uid().longValue() != UserAction.getMyId().longValue()) {//自己的不加昵称
                        //8.9 处理群昵称
                        String name = msgDao.getUsername4Show(msg.getGid(), msg.getFrom_uid(), msg.getFrom_nickname(), msg.getFrom_group_nickname()) + " : ";
                        session.setSenderName(name);
                    }
                }
            }
        } else {
            UserInfo info = userDao.findUserInfo(session.getFrom_uid());
            if (info != null) {
                session.setName(info.getName4Show());
                session.setIsMute(info.getDisturb());
                session.setHasInitDisturb(true);
                session.setAvatar(info.getHead());
            }
            MsgAllBean msg = session.getMessage();
            if (msg == null) {
                msg = msgDao.msgGetLast4FUid(session.getFrom_uid());
            }
            if (msg != null) {
                session.setMessage(msg);
            }
        }
    }

    /***
     * 搜索模式
     */
    private boolean isSearchMode = false;

    private void taskSearch() {
        isSearchMode = true;
        InputUtil.hideKeyboard(edtSearch);
        String key = edtSearch.getText().toString();
        if (key.length() <= 0)
            return;
        List<Session> temp = new ArrayList<>();
        for (Session bean : listData) {
            String title = "";
            String info = "";
            MsgAllBean msginfo;
            if (bean.getType() == 0) {//单人


                UserInfo finfo = userDao.findUserInfo(bean.getFrom_uid());

                title = finfo.getName4Show();

                //获取最后一条消息
                msginfo = msgDao.msgGetLast4FUid(bean.getFrom_uid());
                if (msginfo != null) {
                    info = msginfo.getMsg_typeStr();
                }

            } else if (bean.getType() == 1) {//群
                Group ginfo = msgDao.getGroup4Id(bean.getGid());

                //获取最后一条群消息
                msginfo = msgDao.msgGetLast4Gid(bean.getGid());
                title = /*ginfo.getName()*/msgDao.getGroupName(bean.getGid());
                if (msginfo != null) {
                    info = msginfo.getMsg_typeStr();
                }
            }

            if (title.contains(key) || info.contains(key)) {
                bean.setUnread_count(0);
                temp.add(bean);
            }
        }
        listData = temp;

        mtListView.notifyDataSetChange();
    }


    private void taskDelSession(Long from_uid, String gid) {
        MessageManager.getInstance().deleteSessionAndMsg(from_uid, gid);
        MessageManager.getInstance().notifyRefreshMsg();
        taskListData();
    }


}
