package com.yanlong.im.chat.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yanlong.im.MainActivity;
import com.yanlong.im.MainViewModel;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.bean.SessionDetail;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.eventbus.EventRefreshMainMsg;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.FriendAddAcitvity;
import com.yanlong.im.user.ui.HelpActivity;
import com.yanlong.im.utils.ExpressionUtil;
import com.yanlong.im.utils.QRCodeManage;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;
import com.yanlong.im.wight.avatar.MultiImageView;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventNetStatus;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.EllipsizedTextView;
import net.cb.cb.library.view.PopView;
import net.cb.cb.library.view.StrikeButton;
import net.cb.cb.library.zxing.activity.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private RecyclerViewAdapter mAdapter;

    private LinearLayout viewPopGroup;
    private LinearLayout viewPopAdd;
    private LinearLayout viewPopQr;
    private LinearLayout viewPopHelp;
    private View mHeadView;
    private boolean onlineState = true;//判断网络状态 true在线 false离线

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
        mAdapter = new RecyclerViewAdapter(mHeadView);
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
                        onlineState = state;
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
        viewModel = new ViewModelProvider(getActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(MyAppLication.getInstance())).get(MainViewModel.class);
        EventBus.getDefault().register(this);
        //初始化观察器
        initObserver();
    }

    /**
     * 初始化观察器
     */
    private void initObserver() {
        //监听列表数据变化
        viewModel.sessionMores.addChangeListener(new RealmChangeListener<RealmResults<SessionDetail>>() {
            @Override
            public void onChange(RealmResults<SessionDetail> sessionMore) {
                if (sessionMore != null) {
                    Log.e("raleigh_test", "sessionMore.observe" + sessionMore.size());
                    mtListView.getListView().getAdapter().notifyItemRangeChanged(1, viewModel.sessions.size());
                }
//                checkSessionData(list);
            }
        });
        //监听删除操作项
        viewModel.currentDeletePosition.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer position) {
                long uid = viewModel.sessions.get(position).getFrom_uid();
                String gid = viewModel.sessions.get(position).getGid();
                //数据库中删除数据项
                viewModel.deleteItem(position);
                //通知更新
                MessageManager.getInstance().deleteSessionAndMsg(uid, gid);
                MessageManager.getInstance().notifyRefreshMsg();//更新main界面未读数
                getView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mtListView.getListView().getAdapter().notifyItemRemoved(position + 1);//范围刷新
                    }
                }, 50);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放数据对象
        viewModel.onDestory();
        SocketUtil.getSocketUtil().removeEvent(socketEvent);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventRefreshMainMsg event) {
        if (MessageManager.getInstance().isMessageChange()) {
            MessageManager.getInstance().setMessageChange(false);
            int refreshTag = event.getRefreshTag();
           if (refreshTag == CoreEnum.ESessionRefreshTag.DELETE) {
                //阅后即焚 -更新
                viewModel.updateItemSessionDetail();
                LogUtil.getLog().d("a==", "MsgMainFragment --删除session");
                MessageManager.getInstance().deleteSessionAndMsg(event.getUid(), event.getGid());
                MessageManager.getInstance().notifyRefreshMsg();//更新main界面未读数
            }else if(refreshTag == CoreEnum.ESessionRefreshTag.ALL){
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

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public static final int TYPE_HEADER = 0;
        public static final int TYPE_NORMAL = 1;
        private View mHeaderView;
        public View viewNetwork;

        public RecyclerViewAdapter(View headerView) {
            mHeaderView = headerView;
            notifyItemInserted(0);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup view, int viewType) {
            if (mHeaderView != null && viewType == TYPE_HEADER)
                return new HeadViewHolder(mHeaderView);
            RCViewHolder holder = new RCViewHolder(getLayoutInflater().inflate(R.layout.item_msg_session, view, false));
            return holder;
        }

        @Override
        public int getItemViewType(int position) {
            if (mHeaderView == null) return TYPE_NORMAL;
            if (position == 0) return TYPE_HEADER;
            return TYPE_NORMAL;
        }

        @Override
        public int getItemCount() { // TODO　增加文件头，默认的位置加1
            return viewModel.sessions == null ? 1 : viewModel.sessions.size() + 1;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
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
        public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
            if (viewHolder instanceof RCViewHolder) {
                RCViewHolder holder = (RCViewHolder) viewHolder;
                final Session bean = viewModel.sessions.get(position - 1);
                String icon = "";
                String title = "";
                MsgAllBean msginfo = null;
                String name = "";
                List<String> avatarList = null;
                if (viewModel.sessionMoresPositions.containsKey(bean.getSid())) {
                    Integer index = viewModel.sessionMoresPositions.get(bean.getSid());
                    if (index != null && index >= 0) {
                        //从session详情对象中获取
                        icon = viewModel.sessionMores.get(index).getAvatar();
                        title = viewModel.sessionMores.get(index).getName();
                        msginfo = viewModel.sessionMores.get(index).getMessage();
                        name = viewModel.sessionMores.get(index).getSenderName();
                        String avatarListString = viewModel.sessionMores.get(index).getAvatarList();
                        if (avatarListString != null) {
                            avatarList = Arrays.asList(avatarListString.split(","));
                        }

                    }
                }

                // 头像集合
                List<String> headList = new ArrayList<>();

                String info = "";
                if (msginfo != null) {
                    info = msginfo.getMsg_typeStr();
                }
                int type = bean.getMessageType();
                if (bean.getType() == 0) {//单人
                    if (type == ChatEnum.ESessionType.ENVELOPE_FAIL) {
                        SpannableString style = new SpannableString("[红包发送失败]" + info);
                        ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                        style.setSpan(protocolColorSpan, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        showMessage(holder.txtInfo, info, style);
                    } else {
                        if (StringUtil.isNotNull(bean.getDraft())) {
                            SpannableString style = new SpannableString("[草稿]" + bean.getDraft());
                            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                            style.setSpan(protocolColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            showMessage(holder.txtInfo, bean.getDraft(), style);
                        } else {
                            showMessage(holder.txtInfo, info, null);
                        }
                    }
                    headList.add(icon);
                    holder.imgHead.setList(headList);

                } else if (bean.getType() == 1) {//群
                    if (type == 0) {
                        if (!TextUtils.isEmpty(bean.getAtMessage()) && !TextUtils.isEmpty(name)) {
                            info = name + bean.getAtMessage();
                        } else {
                            info = name + info;

                        }
                    } else if (type == 1) {
                        if (!TextUtils.isEmpty(bean.getAtMessage()) && !TextUtils.isEmpty(name)) {
                            info = bean.getAtMessage();
                            if (StringUtil.isNotNull(info) && info.startsWith("@所有人")) {
                                info = info.replace("@所有人", "");
                            }
                            info = name + info;
                        } else {
                            info = name + info;
                        }
                    } else if (msginfo != null && (ChatEnum.EMessageType.CHANGE_SURVIVAL_TIME + "").equals(msginfo.getMsg_type() + "")) {
                        //阅后即焚不通知 不显示谁发的 肯定是群主修改的
                        // info=info;
                    } else if (!TextUtils.isEmpty(info) && !TextUtils.isEmpty(name)) {//草稿除外
                        if (msginfo != null && (ChatEnum.EMessageType.AT + "").equals(msginfo.getMsg_type() + "")
                                && StringUtil.isNotNull(info) && info.startsWith("@所有人")) {
                            info = info.replace("@所有人", "");
                        }
                        info = name + info;
                    }
                    // 处理公告...问题
                    info = info.replace("\r\n", "  ");

                    switch (type) {
                        case 0:
                            if (StringUtil.isNotNull(bean.getAtMessage())) {
                                if (msginfo != null && msginfo.getMsg_type() == ChatEnum.EMessageType.AT) {
                                    SpannableString style = new SpannableString("[有人@我]" + info);
                                    ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                    style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    showMessage(holder.txtInfo, info, style);
                                } else {
                                    SpannableString style = new SpannableString("[有人@我]" + info);
                                    ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                    style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    showMessage(holder.txtInfo, info, style);
                                }
                            }
                            break;
                        case 1:
                            if (StringUtil.isNotNull(bean.getAtMessage())) {
                                if (msginfo != null && msginfo.getMsg_type() == ChatEnum.EMessageType.AT) {
                                    SpannableString style = new SpannableString("[有人@我]" + info);
                                    ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                    style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    showMessage(holder.txtInfo, info, style);
                                } else {
                                    SpannableString style = new SpannableString("[@所有人]" + info);
                                    ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                    style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    showMessage(holder.txtInfo, info, style);
                                }
                            }
                            break;
                        case 2:
                            if (StringUtil.isNotNull(bean.getDraft())) {
                                SpannableString style = new SpannableString("[草稿]" + bean.getDraft());
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                                style.setSpan(protocolColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                showMessage(holder.txtInfo, bean.getDraft(), style);
                            } else {
                                showMessage(holder.txtInfo, info, null);

                            }
                            break;
                        case 3:
                            SpannableString style = new SpannableString("[红包发送失败]" + info);
                            ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_all_notify));
                            style.setSpan(protocolColorSpan, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            showMessage(holder.txtInfo, info, style);
                            break;
                        default:
                            showMessage(holder.txtInfo, info, null);
                            break;

                    }

                    if (StringUtil.isNotNull(icon)) {
                        headList.add(icon);
                        holder.imgHead.setList(headList);
                    } else {
                        if (avatarList != null && avatarList.size() > 0) {
                            holder.imgHead.setList(avatarList);
                        } else {
                            loadGroupHeads(bean, holder.imgHead);
                        }
                    }
                }

                holder.txtName.setText(title);
                if (bean.isSystemUser()) {
                    //系统会话
                    holder.txtName.setTextColor(getResources().getColor(R.color.blue_title));
                    holder.usertype_tv.setVisibility(View.VISIBLE);
                } else {
                    holder.txtName.setTextColor(getResources().getColor(R.color.black));
                    holder.usertype_tv.setVisibility(View.GONE);
                }
                setUnreadCountOrDisturb(holder, bean, msginfo);

                holder.txtTime.setText(TimeToString.getTimeWx(bean.getUp_time()));


                holder.viewIt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getContext(), ChatActivity.class)
                                .putExtra(ChatActivity.AGM_TOUID, bean.getFrom_uid())
                                .putExtra(ChatActivity.AGM_TOGID, bean.getGid())
                                .putExtra(ChatActivity.ONLINE_STATE, onlineState)
                        );
                    }
                });
                holder.btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.swipeLayout.quickClose();
                        //删除数据
                        viewModel.currentDeletePosition.setValue(position - 1);
                    }
                });
                holder.viewIt.setBackgroundColor(bean.getIsTop() == 0 ? Color.WHITE : Color.parseColor("#ececec"));
                holder.iv_disturb.setVisibility(bean.getIsMute() == 0 ? View.INVISIBLE : View.VISIBLE);
            } else if (viewHolder instanceof HeadViewHolder) {
                HeadViewHolder headHolder = (HeadViewHolder) viewHolder;
                headHolder.edtSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), MsgSearchActivity.class);
                        intent.putExtra("online_state", onlineState);
                        intent.putExtra("conversition_data", new Gson().toJson(viewModel.sessions));
                        startActivity(intent);
                    }
                });
                viewNetwork = headHolder.viewNetwork;
            }

        }

        private void setUnreadCountOrDisturb(RCViewHolder holder, Session bean, MsgAllBean msg) {
            holder.sb.setButtonBackground(R.color.transparent);
            if (bean.getIsMute() == 1) {
                if (msg != null && !msg.isRead()) {
                    holder.iv_disturb_unread.setVisibility(View.VISIBLE);
                    holder.iv_disturb_unread.setBackgroundResource(R.drawable.shape_disturb_unread_bg);
                    holder.sb.setVisibility(View.GONE);
                } else {
                    holder.iv_disturb_unread.setVisibility(View.GONE);
                    holder.sb.setVisibility(View.VISIBLE);
                    holder.sb.setNum(bean.getUnread_count(), false);
                }
            } else {
                holder.iv_disturb_unread.setVisibility(View.GONE);
                holder.sb.setVisibility(View.VISIBLE);
                holder.sb.setNum(bean.getUnread_count(), false);
            }
        }

        /**
         * 富文本显示最后一條内容
         *
         * @param txtInfo
         * @param message
         * @param spannableString
         */
        protected void showMessage(TextView txtInfo, String message, SpannableString spannableString) {
            if (spannableString == null) {
                if (StringUtil.isNotNull(message) && message.startsWith("@所有人  ")) {
                    message = message.replace("@所有人  ", "");
                }
                spannableString = ExpressionUtil.getExpressionString(getContext(), ExpressionUtil.DEFAULT_SMALL_SIZE, message);
            } else {
                spannableString = ExpressionUtil.getExpressionString(getContext(), ExpressionUtil.DEFAULT_SMALL_SIZE, spannableString);
            }
            txtInfo.setText(spannableString, TextView.BufferType.SPANNABLE);
            txtInfo.invalidate();
        }

        /**
         * 加载群头像
         *
         * @param bean
         * @param imgHead
         */
        public synchronized void loadGroupHeads(Session bean, MultiImageView imgHead) {
//            LogUtil.getLog().d(MsgMainFragment.class.getSimpleName(), "loadGroupAvatar--" + bean.getGid());
            Group gginfo = msgDao.getGroup4Id(bean.getGid());
            if (gginfo != null) {
                int i = gginfo.getUsers().size();
                i = i > 9 ? 9 : i;
                //头像地址
                List<String> headList = new ArrayList<>();
                for (int j = 0; j < i; j++) {
                    MemberUser userInfo = gginfo.getUsers().get(j);
                    headList.add(userInfo.getHead());
                }
                imgHead.setList(headList);
            }
        }


        public class RCViewHolder extends RecyclerView.ViewHolder {
            private MultiImageView imgHead;
            private StrikeButton sb;

            private View viewIt;
            private Button btnDel;
            private SwipeMenuLayout swipeLayout;
            private TextView txtName;
            private EllipsizedTextView txtInfo;
            private TextView txtTime;
            private final ImageView iv_disturb, iv_disturb_unread;
            //            private final TextView tv_num;
            private TextView usertype_tv;

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
                usertype_tv = convertView.findViewById(R.id.usertype_tv);
            }
        }

        public class HeadViewHolder extends RecyclerView.ViewHolder {

            private net.cb.cb.library.view.ClearEditText edtSearch;
            private View viewNetwork;

            public HeadViewHolder(View convertView) {
                super(convertView);
                edtSearch = convertView.findViewById(R.id.edt_search);
                viewNetwork = convertView.findViewById(R.id.view_network);
            }
        }

    }

}
