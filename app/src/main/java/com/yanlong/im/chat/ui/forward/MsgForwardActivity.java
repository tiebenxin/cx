package com.yanlong.im.chat.ui.forward;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.google.gson.Gson;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.ui.view.AlertForward;
import com.yanlong.im.databinding.ActivityMsgForwardBinding;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.CustomTabView;

/***
 * 消息转换
 */
public class MsgForwardActivity extends AppActivity implements IForwardListener {
    public static final String AGM_JSON = "JSON";
    private ActionbarView actionbar;
    private ActivityMsgForwardBinding ui;
    private UserDao userDao = new UserDao();
    private MsgDao msgDao = new MsgDao();
    private MsgAllBean msgAllBean;


    @CustomTabView.ETabPosition
    private int currentPager = CustomTabView.ETabPosition.LEFT;


    //自动寻找控件
    private void findViews() {
        actionbar = ui.headView.getActionbar();
    }


    //自动生成的控件事件
    private void initEvent() {
        String json = getIntent().getStringExtra(AGM_JSON);
        msgAllBean = new Gson().fromJson(json, MsgAllBean.class);

        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

        ui.tabView.setTabSelectListener(new CustomTabView.OnTabSelectListener() {
            @Override
            public void onLeft() {
                showFragment(CustomTabView.ETabPosition.LEFT);
            }

            @Override
            public void onRight() {
                showFragment(CustomTabView.ETabPosition.RIGHT);
            }
        });

    }

    private void showFragment(@CustomTabView.ETabPosition int tab) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment newFragment = fragmentManager.findFragmentByTag(tab + "");
        if (newFragment == null) {
            newFragment = createFragment(tab);
        }
        if (newFragment == null) {
            return;
        }
        prepareFragment(newFragment);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fl_content, newFragment, tab + "");
        ft.attach(newFragment);
        ft.commitAllowingStateLoss();
        currentPager = tab;
    }

    private void prepareFragment(Fragment fragment) {
        if (fragment instanceof ForwardSessionFragment) {
            ForwardSessionFragment sessionFragment = (ForwardSessionFragment) fragment;
            sessionFragment.setForwardListener(this);
        } else if (fragment instanceof ForwardRosterFragment) {
            ForwardRosterFragment rosterFragment = (ForwardRosterFragment) fragment;
            rosterFragment.setForwardListener(new IForwardRosterListener() {
                @Override
                public void onSelectMuc() {

                }

                @Override
                public void onForward(long uid, String gid, String avatar, String nick) {
                    MsgForwardActivity.this.onForward(uid, gid, avatar, nick);
                }
            });
        }
    }

    private Fragment createFragment(@CustomTabView.ETabPosition int tab) {
        switch (tab) {
            case CustomTabView.ETabPosition.LEFT:
                return new ForwardSessionFragment();
            case CustomTabView.ETabPosition.RIGHT:
                return new ForwardRosterFragment();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_msg_forward);
//        setContentView(R.layout.activity_msg_forward);
        findViews();
        initEvent();
        showFragment(currentPager);

    }

    @Override
    public void onForward(final long toUid, final String toGid, String mIcon, String mName) {
        if (msgAllBean == null)
            return;
        AlertForward alertForward = new AlertForward();
        if (msgAllBean.getChat() != null) {//转换文字
            alertForward.init(MsgForwardActivity.this, mIcon, mName, msgAllBean.getChat().getMsg(), null, "发送", new AlertForward.Event() {
                @Override
                public void onON() {

                }

                @Override
                public void onYes(String content) {
                    // ToastUtil.show(context, msgAllBean.getChat().getMsg()+"---\n"+content);

//                    Long toUId = bean.getFrom_uid();
//                    String toGid = bean.getGid();
                    SocketData.send4Chat(toUid, toGid, msgAllBean.getChat().getMsg());
                    if (StringUtil.isNotNull(content)) {
                        SocketData.send4Chat(toUid, toGid, content);
                    }

                    finish();
                }
            });
        } else if (msgAllBean.getImage() != null) {

            alertForward.init(MsgForwardActivity.this, mIcon, mName, null, msgAllBean.getImage().getThumbnail(), "发送", new AlertForward.Event() {
                @Override
                public void onON() {

                }

                @Override
                public void onYes(String content) {
                    // ToastUtil.show(context, msgAllBean.getImage().getThumbnail()+"---\n"+content);
//                    Long toUId = bean.getFrom_uid();
//                    String toGid = bean.getGid();
                    ImageMessage imagesrc = msgAllBean.getImage();
                    SocketData.send4Image(toUid, toGid, imagesrc.getOrigin(), imagesrc.getPreview(), imagesrc.getThumbnail(), new Long(imagesrc.getWidth()).intValue(), new Long(imagesrc.getHeight()).intValue(), new Long(imagesrc.getSize()).intValue());
                    msgDao.ImgReadStatSet(imagesrc.getOrigin(), true);
                    if (StringUtil.isNotNull(content)) {
                        SocketData.send4Chat(toUid, toGid, content);
                    }
                    finish();

                }
            });

        } else if (msgAllBean.getAtMessage() != null) {

            alertForward.init(MsgForwardActivity.this, mIcon, mName, msgAllBean.getAtMessage().getMsg(), null, "发送", new AlertForward.Event() {
                @Override
                public void onON() {

                }

                @Override
                public void onYes(String content) {
                    // ToastUtil.show(context, msgAllBean.getChat().getMsg()+"---\n"+content);

//                    Long toUId = bean.getFrom_uid();
//                    String toGid = bean.getGid();
                    SocketData.send4Chat(toUid, toGid, msgAllBean.getAtMessage().getMsg());
                    if (StringUtil.isNotNull(content)) {
                        SocketData.send4Chat(toUid, toGid, content);
                    }
                    finish();
                }
            });
        }


        alertForward.show();

    }

    //自动生成RecyclerViewAdapter
//    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {
//
//        @Override
//        public int getItemCount() {
//            return listData == null ? 0 : listData.size();
//        }
//
//        //自动生成控件事件
//        @Override
//        public void onBindViewHolder(RCViewHolder holder, int position) {
//            final Session bean = listData.get(position);
//
//            String icon = "";
//            String title = "";
//
//            if (bean.getType() == 0) {//单人
//
//
//                UserInfo finfo = userDao.findUserInfo(bean.getFrom_uid());
//                if (finfo != null) {
//                    icon = finfo.getHead();
//                    title = finfo.getName4Show();
//                }
//
//
//            } else if (bean.getType() == 1) {//群
//                Group ginfo = msgDao.getGroup4Id(bean.getGid());
//                if (ginfo != null) {
//                    icon = ginfo.getAvatar();
//                    //获取最后一条群消息
//
//                    title = ginfo.getName();
//
//                } else {
//
//                }
//
//            }
//
//
//            holder.imgHead.setImageURI(Uri.parse(icon));
//
//            holder.txtName.setText(title);
//
//            final String mIcon = icon;
//            final String mName = title;
//
//            holder.viewIt.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // ToastUtil.show(getContext(), "wow");
//                    if (msgAllBean == null)
//                        return;
//                    AlertForward alertForward = new AlertForward();
//                    if (msgAllBean.getChat() != null) {//转换文字
//
//
//                        alertForward.init(MsgForwardActivity.this, mIcon, mName, msgAllBean.getChat().getMsg(), null, "发送", new AlertForward.Event() {
//                            @Override
//                            public void onON() {
//
//                            }
//
//                            @Override
//                            public void onYes(String content) {
//                                // ToastUtil.show(context, msgAllBean.getChat().getMsg()+"---\n"+content);
//
//                                Long toUId = bean.getFrom_uid();
//                                String toGid = bean.getGid();
//                                SocketData.send4Chat(toUId, toGid, msgAllBean.getChat().getMsg());
//                                if (StringUtil.isNotNull(content)) {
//                                    SocketData.send4Chat(toUId, toGid, content);
//                                }
//
//                                finish();
//                            }
//                        });
//                    } else if (msgAllBean.getImage() != null) {
//
//                        alertForward.init(MsgForwardActivity.this, mIcon, mName, null, msgAllBean.getImage().getThumbnail(), "发送", new AlertForward.Event() {
//                            @Override
//                            public void onON() {
//
//                            }
//
//                            @Override
//                            public void onYes(String content) {
//                                // ToastUtil.show(context, msgAllBean.getImage().getThumbnail()+"---\n"+content);
//                                Long toUId = bean.getFrom_uid();
//                                String toGid = bean.getGid();
//                                ImageMessage imagesrc = msgAllBean.getImage();
//                                SocketData.send4Image(toUId, toGid, imagesrc.getOrigin(), imagesrc.getPreview(), imagesrc.getThumbnail(), new Long(imagesrc.getWidth()).intValue(), new Long(imagesrc.getHeight()).intValue(), new Long(imagesrc.getSize()).intValue());
//                                msgDao.ImgReadStatSet(imagesrc.getOrigin(), true);
//                                if (StringUtil.isNotNull(content)) {
//                                    SocketData.send4Chat(toUId, toGid, content);
//                                }
//                                finish();
//
//                            }
//                        });
//
//                    } else if (msgAllBean.getAtMessage() != null) {
//
//                        alertForward.init(MsgForwardActivity.this, mIcon, mName, msgAllBean.getAtMessage().getMsg(), null, "发送", new AlertForward.Event() {
//                            @Override
//                            public void onON() {
//
//                            }
//
//                            @Override
//                            public void onYes(String content) {
//                                // ToastUtil.show(context, msgAllBean.getChat().getMsg()+"---\n"+content);
//
//                                Long toUId = bean.getFrom_uid();
//                                String toGid = bean.getGid();
//                                SocketData.send4Chat(toUId, toGid, msgAllBean.getAtMessage().getMsg());
//                                if (StringUtil.isNotNull(content)) {
//                                    SocketData.send4Chat(toUId, toGid, content);
//                                }
//                                finish();
//                            }
//                        });
//                    }
//
//
//                    alertForward.show();
//
//                }
//            });
//
//
//        }
//
//
//        //自动寻找ViewHold
//        @Override
//        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
//            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_msg_forward, view, false));
//            return holder;
//        }
//
//
//        //自动生成ViewHold
//        public class RCViewHolder extends RecyclerView.ViewHolder {
//            private LinearLayout viewIt;
//            private com.facebook.drawee.view.SimpleDraweeView imgHead;
//            private TextView txtName;
//
//            //自动寻找ViewHold
//            public RCViewHolder(View convertView) {
//                super(convertView);
//                viewIt = (LinearLayout) convertView.findViewById(R.id.view_it);
//                imgHead = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.img_head);
//                txtName = (TextView) convertView.findViewById(R.id.txt_name);
//            }
//
//        }
//    }


//    private List<Session> listData = new ArrayList<>();

//    private void taskListData() {
//
//        listData = msgDao.sessionGetAll(false);
//
//
//        mtListView.notifyDataSetChange();
//
//
//    }

}
