package com.yanlong.im.chat.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
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
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.FriendAddAcitvity;
import com.yanlong.im.user.ui.HelpActivity;
import com.yanlong.im.utils.QRCodeManage;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.bean.EventRefreshMainMsg;
import net.cb.cb.library.bean.QRCodeBean;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.PopView;
import net.cb.cb.library.view.StrikeButton;
import net.cb.cb.library.zxing.activity.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/***
 * 首页消息
 */
public class MsgMainFragment extends Fragment {
    private View rootView;
    private net.cb.cb.library.view.ActionbarView actionBar;
    private net.cb.cb.library.view.ClearEditText edtSearch;
    private net.cb.cb.library.view.MultiListView mtListView;

    private LinearLayout viewPopGroup;
    private LinearLayout viewPopAdd;
    private LinearLayout viewPopQr;
    private LinearLayout viewPopHelp;
    private View viewNetwork;

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
                        actionBar.setTitle(state ? "消息" : "消息(连接中...)");

                        viewNetwork.setVisibility(state ? View.GONE : View.VISIBLE);
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
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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

    @Override
    public void onStart() {
        super.onStart();
        //7.18 进来的时候显示有网的状态
        socketEvent.onLine(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
            QRCodeBean bean = QRCodeManage.getQRCodeBean(getActivityMe(), scanResult);
            QRCodeManage.goToActivity(getActivityMe(), bean);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketUtil.getSocketUtil().removeEvent(socketEvent);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventRefreshMainMsg event) {
        taskListData();
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
        taskListData();
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

        //自动生成控件事件
        @Override
        public void onBindViewHolder(final RCViewHolder holder, int position) {
            final Session bean = listData.get(position);

            String icon = "";
            String title = "";
            String info = "";
            MsgAllBean msginfo = null;
            if (bean.getType() == 0) {//单人


                UserInfo finfo = userDao.findUserInfo(bean.getFrom_uid());
                if (finfo != null) {
                    icon = finfo.getHead();
                    title = finfo.getName4Show();
                }


                //获取最后一条消息
                msginfo = msgDao.msgGetLast4FUid(bean.getFrom_uid());
                if (msginfo != null) {
                    info = msginfo.getMsg_typeStr();
                }

                if (StringUtil.isNotNull(bean.getDraft())) {
                    info = "草稿:" + bean.getDraft();
                }
                holder.txtInfo.setText(info);

            } else if (bean.getType() == 1) {//群
                Group ginfo = msgDao.getGroup4Id(bean.getGid());
                if (ginfo != null) {
                    icon = ginfo.getAvatar();
                    //获取最后一条群消息
                    msginfo = msgDao.msgGetLast4Gid(bean.getGid());
                    title = ginfo.getName();
                    if (msginfo != null) {
                        if (msginfo.getMsg_type() == 0) {//通知不要加谁发的消息
                            info = msginfo.getMsg_typeStr();
                        } else {
                            String name = "";
                            if (msginfo.getFrom_uid().longValue() != UserAction.getMyId().longValue()) {//自己的不加昵称
                                name = msginfo.getFrom_nickname() + " : ";
                                UserInfo fuser = msginfo.getFrom_user();

                                if (fuser != null && StringUtil.isNotNull(fuser.getMkName())) {
                                    name = fuser.getMkName() + " : ";

                                }
                            }

                            info = name + msginfo.getMsg_typeStr();
                        }

                    }
                } else {
                    Log.e("taf", "11来消息的时候没有创建群");
                }

                int type = bean.getMessageType();
                switch (type) {
                    case 0:
                        if (StringUtil.isNotNull(bean.getAtMessage())) {
                            if (msginfo.getMsg_type() == 8) {
                                SpannableStringBuilder style = new SpannableStringBuilder();
                                style.append("[有人@你]" + bean.getAtMessage());
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_600));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                holder.txtInfo.setText(style);
                            } else {
                                SpannableStringBuilder style = new SpannableStringBuilder();
                                style.append("[有人@你]" + info);
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_600));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                holder.txtInfo.setText(style);
                            }
                        }
                        break;
                    case 1:
                        if (StringUtil.isNotNull(bean.getAtMessage())) {
                            if (msginfo.getMsg_type() == 8) {
                                SpannableStringBuilder style = new SpannableStringBuilder();
                                style.append("[@所有人]" + bean.getAtMessage());
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_600));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                holder.txtInfo.setText(style);
                            } else {
                                SpannableStringBuilder style = new SpannableStringBuilder();
                                style.append("[@所有人]" + info);
                                ForegroundColorSpan protocolColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red_600));
                                style.setSpan(protocolColorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                holder.txtInfo.setText(style);
                            }
                        }
                        break;
                    case 2:
                        if (StringUtil.isNotNull(bean.getDraft())) {
                            info = "草稿:" + bean.getDraft();
                        }
                        holder.txtInfo.setText(info);
                        break;
                    default:
                        holder.txtInfo.setText(info);
                        break;
                }
            }


            holder.imgHead.setImageURI(Uri.parse(icon));
            holder.txtName.setText(title);
            holder.sb.setButtonBackground(R.color.transparent);
            holder.sb.setNum(bean.getUnread_count());

            holder.txtTime.setText(TimeToString.getTimeWx(bean.getUp_time()));


            holder.viewIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), ChatActivity.class)
                            .putExtra(ChatActivity.AGM_TOUID, bean.getFrom_uid())
                            .putExtra(ChatActivity.AGM_TOGID, bean.getGid())
                    );

                }
            });
            holder.btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.swipeLayout.quickClose();
                    taskDelSissen(bean.getFrom_uid(), bean.getGid());
                }
            });
            holder.viewIt.setBackgroundColor(bean.getIsTop() == 0 ? Color.WHITE : Color.parseColor("#f1f1f1"));

        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(getLayoutInflater().inflate(R.layout.item_msg_session, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private com.facebook.drawee.view.SimpleDraweeView imgHead;
            private StrikeButton sb;
            private View viewIt;
            private Button btnDel;
            private SwipeMenuLayout swipeLayout;
            private TextView txtName;
            private TextView txtInfo;
            private TextView txtTime;

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
            }

        }
    }


    private MsgDao msgDao = new MsgDao();
    private UserDao userDao = new UserDao();
    private UserAction userAction = new UserAction();
    private MsgAction msgAction = new MsgAction();
    private List<Session> listData = new ArrayList<>();

    private int didIndex = 0;//当前缓存的顺序
    private List<String> dids = new ArrayList<>();//缓存所有未缓存的信息

    private void taskListData() {
        if (isSearchMode) {
            return;
        }
        listData = msgDao.sessionGetAll(true);

        //缓存所有未缓存的群信息
        dids = new ArrayList<>();
        didIndex = 0;
        for (Session s : listData) {
            String gid = s.getGid();
            if (StringUtil.isNotNull(gid)) {//缓存群的信息
                Group group = msgDao.getGroup4Id(gid);
                if (group == null) {
                    dids.add(gid);
                    msgAction.groupInfo(gid, new CallBack<ReturnBean<Group>>() {
                        @Override
                        public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                            didIndex++;
                            if (didIndex == dids.size()) {
                                mtListView.notifyDataSetChange();
                            }
                        }
                    });
                }
            } else {//缓存个人的信息
                Long fuid = s.getFrom_uid();
                UserInfo uinfo = userDao.findUserInfo(fuid);
                if (uinfo == null) {
                    dids.add(fuid.toString());


                    userAction.getUserInfoAndSave(fuid, new CallBack<ReturnBean<UserInfo>>() {
                        @Override
                        public void onResponse(Call<ReturnBean<UserInfo>> call, Response<ReturnBean<UserInfo>> response) {
                            didIndex++;
                            if (didIndex == dids.size()) {
                                mtListView.notifyDataSetChange();
                            }
                        }
                    });
                }

            }


        }

        if (didIndex == dids.size()) {
            mtListView.notifyDataSetChange();
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
                title = ginfo.getName();
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


    private void taskDelSissen(Long from_uid, String gid) {
        msgDao.sessionDel(from_uid, gid);
        msgDao.msgDel(from_uid, gid);
        EventBus.getDefault().post(new EventRefreshMainMsg());
        taskListData();
    }


}
