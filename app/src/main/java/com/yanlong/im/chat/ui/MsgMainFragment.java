package com.yanlong.im.chat.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.FriendAddAcitvity;
import com.yanlong.im.user.ui.HelpActivity;

import net.cb.cb.library.bean.EventRefreshMainMsg;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AlertSelectView;
import net.cb.cb.library.view.PopView;
import net.cb.cb.library.view.StrikeButton;
import net.cb.cb.library.zxing.activity.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        View pView = getLayoutInflater().inflate(R.layout.view_pop_main, null);
        findViewsPop(pView);
        popView.init(getContext(), pView);
    }


    private PopView popView = new PopView();

    //自动生成的控件事件
    private void initEvent() {
        mtListView.init(new RecyclerViewAdapter());
        mtListView.getLoadView().setStateNormal();


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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
            //将扫描出的信息显示出来
            ToastUtil.show(getActivityMe(), scanResult);
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

    private MainActivity getActivityMe() {
        return (MainActivity) getActivity();
    }


    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return listData == null ? 0 : listData.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, int position) {
            final Session bean = listData.get(position);

            String icon = "";
            String title = "";
            String info = "";
            MsgAllBean msginfo;
            if (bean.getType() == 0) {//单人


                UserInfo finfo = userDao.findUserInfo(bean.getFrom_uid());
                icon =finfo.getHead();
                title=finfo.getName();

                //获取最后一条消息
                 msginfo = msgDao.msgGetLast4FUid(bean.getFrom_uid());
                info=msginfo.getMsg_typeStr();
            } else if (bean.getType() == 1) {//群
                icon="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1557295193046&di=3078b122f0e9919363f1dc6b1ef69aa8&imgtype=0&src=http%3A%2F%2Fbpic.588ku.com%2Felement_origin_min_pic%2F00%2F91%2F20%2F7656f16057ce9da.jpg";
                //获取最后一条群消息
                msginfo = msgDao.msgGetLast4Gid(bean.getGid());
                title="群"+bean.getGid();
                info=msginfo.getMsg_typeStr();
            }

            holder.imgHead.setImageURI(Uri.parse(icon));
            holder.txtName.setText(title);
            holder.txtInfo.setText(info);
            holder.sb.setButtonBackground(R.color.transparent);
            holder.sb.setNum(bean.getUnread_count());

            holder.txtTime.setText(TimeToString.YYYY_MM_DD_HH_MM_SS(bean.getUp_time()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), ChatActivity.class)
                            .putExtra(ChatActivity.AGM_TOUID,bean.getFrom_uid())
                            .putExtra(ChatActivity.AGM_TOGID,bean.getGid())
                    );

                }
            });

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
            private TextView txtName;
            private TextView txtInfo;
            private TextView txtTime;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                imgHead = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.img_head);
                sb=convertView.findViewById(R.id.sb);
                txtName = (TextView) convertView.findViewById(R.id.txt_name);
                txtInfo = (TextView) convertView.findViewById(R.id.txt_info);
                txtTime = (TextView) convertView.findViewById(R.id.txt_time);
            }

        }
    }

    private MsgDao msgDao = new MsgDao();
    private UserDao userDao=new UserDao();
    private List<Session> listData = new ArrayList<>();

    private void taskListData() {
        listData = msgDao.sessionGetAll();
        mtListView.notifyDataSetChange();
    }


}
