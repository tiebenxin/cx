package com.yanlong.im.chat.ui.forward;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nim_lib.config.Preferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.LocationMessage;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.ShippedExpressionMessage;
import com.yanlong.im.chat.bean.SingleMeberInfoBean;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.ui.view.AlertForward;
import com.yanlong.im.databinding.ActivityGroupSaveBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.wight.avatar.MultiImageView;

import net.cb.cb.library.utils.GsonUtils;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.yanlong.im.chat.ui.forward.MsgForwardActivity.AGM_JSON;

/***
 * 转发群聊选择列表
 */
public class GroupSelectActivity extends AppActivity implements IForwardListener {
    public static final String GROUP_JSON = "JSON";

    private ActionbarView actionbar;
    private List<Group> groupInfoBeans;
    private ActivityGroupSaveBinding ui;
    private MsgAllBean msgAllBean;
    private MsgDao msgDao = new MsgDao();
    private MsgAllBean sendMesage;
    private int mCount = 0;
    private List<Group> ListOne;//含有全部禁言的数据
    private int needRequestNums = 0;//需要http请求的次数
    private int finalRequestNums = 0;//实际http请求的次数
    private SingleMeberInfoBean singleMeberInfoBean;// 单个群成员信息，主要查看是否被单人禁言
    private boolean isVertical = true;//竖图(true)还是横图(false)  默认竖图
    private ArrayList<MsgAllBean> msgList;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_group_save);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        String json = getIntent().getStringExtra(AGM_JSON);
        mode = getIntent().getIntExtra("mode", 0);
        if (mode == ChatEnum.EForwardMode.ONE_BY_ONE) {
            Gson gson = new Gson();
            msgList = gson.fromJson(json, new TypeToken<List<MsgAllBean>>() {
            }.getType());
        } else {
            msgAllBean = GsonUtils.getObject(json, MsgAllBean.class);
        }
        mCount = getIntent().getIntExtra(Preferences.DATA, 0);
        findViews();
        initEvent();
        initData();
    }

    //自动寻找控件
    private void findViews() {
        actionbar = ui.headView.getActionbar();
        ui.headView.setTitle("选择转发的群聊");
    }


    //自动生成的控件事件
    private void initEvent() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        actionbar.getTxtRight().setOnClickListener(o -> {
            finish();
        });
        ui.mtListView.getLoadView().setStateNormal();
    }

    private void initData() {
        if (!MsgForwardActivity.isSingleSelected) {
            if (mCount == 0) {
                actionbar.setTxtRight("完成");
            } else {
                actionbar.setTxtRight("完成(" + mCount + ")");
            }
        }
        groupInfoBeans = new ArrayList<>();
        ui.mtListView.init(new RecyclerViewAdapter());
//        taskMySaved();
        loadSavedGroup();
    }

    @SuppressLint("CheckResult")
    private void loadSavedGroup() {
        Observable.just(0)
                .map(new Function<Integer, List<Group>>() {
                    @Override
                    public List<Group> apply(Integer integer) throws Exception {
                        return msgDao.getMySavedGroup(false);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<Group>>empty())
                .subscribe(new Consumer<List<Group>>() {
                    @Override
                    public void accept(List<Group> list) throws Exception {
                        if (list == null || list.size() <= 0) {
                            ui.mtListView.getLoadView().setStateNoData(R.mipmap.ic_nodate);
                            return;
                        }
                        if (groupInfoBeans != null && groupInfoBeans.size() > 0) {
                            groupInfoBeans.clear();
                        }
                        groupInfoBeans.addAll(list);
                        ListOne = new ArrayList<>();
                        //1 先统计全员禁言的群，过滤掉不显示
                        for (int i = 0; i < groupInfoBeans.size(); i++) {
                            if (groupInfoBeans.get(i).getWordsNotAllowed() == 1) {
                                //如果我是群主或者管理员则不过滤
                                if (!isAdmin(groupInfoBeans.get(i)) && !isAdministrators(groupInfoBeans.get(i))) {
                                    ListOne.add(groupInfoBeans.get(i));
                                }
                            }
                        }
                        if (ListOne.size() > 0) {
                            groupInfoBeans.removeAll(ListOne);
                        }
                        ui.mtListView.notifyDataSetChange();
                    }
                });
    }

    @Override
    public void onForward(final long uid, final String gid, String avatar, String nick) {
        if (msgAllBean == null)
            return;
        AlertForward alertForward = new AlertForward();
        String txt = "";
        String imageUrl = "";
        if (msgAllBean.getChat() != null) {//转换文字
            txt = msgAllBean.getChat().getMsg();
        } else if (msgAllBean.getImage() != null) {
            imageUrl = msgAllBean.getImage().getThumbnail();
            if (msgAllBean.getImage().getHeight() >= msgAllBean.getImage().getWidth()) {
                isVertical = true;
            } else {
                isVertical = false;
            }
        } else if (msgAllBean.getAtMessage() != null) {
            txt = msgAllBean.getAtMessage().getMsg();
        } else if (msgAllBean.getVideoMessage() != null) {
            imageUrl = msgAllBean.getVideoMessage().getBg_url();
            if (msgAllBean.getVideoMessage().getHeight() >= msgAllBean.getVideoMessage().getWidth()) {
                isVertical = true;
            } else {
                isVertical = false;
            }
        } else if (msgAllBean.getLocationMessage() != null) {
//            imageUrl= LocationUtils.getLocationUrl(msgAllBean.getLocationMessage().getLatitude(),msgAllBean.getLocationMessage().getLongitude());
            txt = "[位置]" + msgAllBean.getLocationMessage().getAddress();
        } else if (msgAllBean.getShippedExpressionMessage() != null) {
            imageUrl = msgAllBean.getShippedExpressionMessage().getId();
        }

        alertForward.init(GroupSelectActivity.this, msgAllBean.getMsg_type(), avatar, nick, txt, imageUrl, "发送", gid, isVertical, new AlertForward.Event() {
            @Override
            public void onON() {

            }

            @Override
            public void onYes(String content) {
                send(content, uid, gid);
                ToastUtil.show(GroupSelectActivity.this, getResources().getString(net.cb.cb.library.R.string.forward_success));
                finish();
            }
        });
        alertForward.show();

    }

    //处理逻辑
    private void send(String content, long uid, String gid) {
        if (msgAllBean.getChat() != null) {//转换文字
            sendMessage(uid, gid, msgAllBean.getChat().getMsg(), content);
        } else if (msgAllBean.getImage() != null) {
            ImageMessage imagesrc = msgAllBean.getImage();
            if (msgAllBean.getFrom_uid() == UserAction.getMyId().longValue()) {
                imagesrc.setReadOrigin(true);
            }
            ImageMessage imageMessage = SocketData.createImageMessage(SocketData.getUUID(), imagesrc.getOrigin(), imagesrc.getPreview(), imagesrc.getThumbnail(), imagesrc.getWidth(), imagesrc.getHeight(), !TextUtils.isEmpty(imagesrc.getOrigin()), imagesrc.isReadOrigin(), imagesrc.getSize());
            MsgAllBean allBean = SocketData.createMessageBean(uid, gid, msgAllBean.getMsg_type(), ChatEnum.ESendStatus.SENDING, SocketData.getFixTime(), imageMessage);
            if (allBean != null) {
                SocketData.sendAndSaveMessage(allBean);
                sendMesage = allBean;
            }
            sendLeaveMessage(content, uid, gid);
            ToastUtil.show(GroupSelectActivity.this, getResources().getString(net.cb.cb.library.R.string.forward_success));
            setResult(RESULT_OK);
        } else if (msgAllBean.getAtMessage() != null) {
            sendMessage(uid, gid, msgAllBean.getAtMessage().getMsg(), content);
        } else if (msgAllBean.getVideoMessage() != null) {
            VideoMessage video = msgAllBean.getVideoMessage();
            VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), video.getBg_url(), video.getUrl(), video.getDuration(), video.getWidth(), video.getHeight(), video.isReadOrigin());
            MsgAllBean allBean = SocketData.createMessageBean(uid, gid, msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), videoMessage);
            if (allBean != null) {
                SocketData.sendAndSaveMessage(allBean);
                sendMesage = allBean;
            }
            sendLeaveMessage(content, uid, gid);
            ToastUtil.show(GroupSelectActivity.this, getResources().getString(net.cb.cb.library.R.string.forward_success));
            setResult(RESULT_OK);
        } else if (msgAllBean.getLocationMessage() != null) {
            LocationMessage location = msgAllBean.getLocationMessage();
            LocationMessage locationMessage = SocketData.createLocationMessage(SocketData.getUUID(), location);
            MsgAllBean allBean = SocketData.createMessageBean(uid, gid, msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), locationMessage);
            if (allBean != null) {
                SocketData.sendAndSaveMessage(allBean);
                sendMesage = allBean;
            }
            sendLeaveMessage(content, uid, gid);
        } else if (msgAllBean.getShippedExpressionMessage() != null) {
            ShippedExpressionMessage message = SocketData.createFaceMessage(SocketData.getUUID(), msgAllBean.getShippedExpressionMessage().getId());
            MsgAllBean allBean = SocketData.createMessageBean(uid, gid, ChatEnum.EMessageType.SHIPPED_EXPRESSION, ChatEnum.ESendStatus.SENDING,
                    SocketData.getFixTime(), message);
            if (allBean != null) {
                SocketData.sendAndSaveMessage(allBean);
                sendMesage = allBean;
            }
            sendLeaveMessage(content, uid, gid);
        }
    }

    /*
     * msg 转发消息内容
     * comments 转发留言
     * */
    private void sendMessage(long msgUid, String msgGid, String msgMsg, String comments) {

        ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), msgMsg);
        MsgAllBean allBean = SocketData.createMessageBean(msgUid, msgGid, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.SENDING, SocketData.getFixTime(), chatMessage);
        if (allBean != null) {
            SocketData.sendAndSaveMessage(allBean);
            sendMesage = allBean;
        }
        sendLeaveMessage(comments, msgUid, msgGid);
        setResult(RESULT_OK);
    }

    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return null == groupInfoBeans ? 0 : groupInfoBeans.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, int position) {
            final Group groupInfoBean = groupInfoBeans.get(position);
            // 头像集合
            List<String> headList = new ArrayList<>();

            String imageHead = groupInfoBean.getAvatar();
            if (imageHead != null && !imageHead.isEmpty() && StringUtil.isNotNull(imageHead)) {
                headList.add(imageHead);
                holder.imgHead.setList(headList);
            } else {
                loadGroupHeads(groupInfoBean.getGid(), holder.imgHead);
            }

            // holder.txtName.setText(groupInfoBean.getName());
            //holder.imgHead.setImageURI(groupInfoBean.getAvatar() + "");
            holder.txtName.setText(/*groupInfoBean.getName()*/msgDao.getGroupName(groupInfoBean.getGid()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.putExtra(GROUP_JSON, GsonUtils.optObject(groupInfoBean));
//                    setResult(RESULT_OK, intent);
                    if (ViewUtils.isFastDoubleClick()) {
                        return;
                    }
                    MsgForwardActivity.addOrDeleteMoreSessionBeanList(false, -1L, groupInfoBean.getGid(), groupInfoBean.getAvatar(), msgDao.getGroupName(groupInfoBean.getGid()));
                    Intent intent = new Intent();
                    intent.putExtra("gid",groupInfoBean.getGid());
                    setResult(RESULT_OK,intent);
                    finish();
//                    if (MsgForwardActivity.isSingleSelected) {
//                        onForward(-1L, groupInfoBean.getGid(), groupInfoBean.getAvatar(), /*groupInfoBean.getName()*/msgDao.getGroupName(groupInfoBean.getGid()));
//                    } else {
//                        if (groupInfoBean.isSelect()) {
//                            groupInfoBeans.get(position).setSelect(false);
//                            holder.ivSelect.setSelected(false);
//
//                            MsgForwardActivity.addOrDeleteMoreSessionBeanList(false, -1L, groupInfoBean.getGid(), groupInfoBean.getAvatar(), msgDao.getGroupName(groupInfoBean.getGid()));
//                        } else {
//
//                            if (MsgForwardActivity.moreSessionBeanList.size() >= MsgForwardActivity.maxNumb) {
//                                ToastUtil.show(context, "最多选择" + MsgForwardActivity.maxNumb + "个");
//                                return;
//                            }
//
//                            groupInfoBeans.get(position).setSelect(true);
//                            holder.ivSelect.setSelected(true);
//                            MsgForwardActivity.addOrDeleteMoreSessionBeanList(true, -1L, groupInfoBean.getGid(), groupInfoBean.getAvatar(), msgDao.getGroupName(groupInfoBean.getGid()));
//                        }
//                    }
                }
            });


            if (getItemCount() == (position + 1)) {
                holder.txtNum.setText(getItemCount() + "个群聊");
                holder.txtNum.setVisibility(View.VISIBLE);
            } else {
                holder.txtNum.setVisibility(View.GONE);
            }


            if (MsgForwardActivity.isSingleSelected) {
                holder.ivSelect.setVisibility(View.GONE);
            } else {
                holder.ivSelect.setVisibility(View.VISIBLE);

                boolean hasSelect = MsgForwardActivity.findMoreSessionBeanList(-1L, groupInfoBean.getGid());
//                LogUtil.getLog().e(getAdapterPosition()+"======hasSelect=="+hasSelect);
                if (hasSelect) {
                    groupInfoBeans.get(position).setSelect(true);
                    holder.ivSelect.setSelected(true);
                } else {
                    groupInfoBeans.get(position).setSelect(false);
                    holder.ivSelect.setSelected(false);
                }
            }
        }

        /**
         * 加载群头像
         *
         * @param gid
         * @param imgHead
         */
        public synchronized void loadGroupHeads(String gid, MultiImageView imgHead) {
            Group gginfo = msgDao.getGroup4Id(gid);
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

        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_group_save, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private ImageView ivSelect;
            private MultiImageView imgHead;
            private TextView txtName;
            private TextView txtNum;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                imgHead = convertView.findViewById(R.id.img_head);
                txtName = convertView.findViewById(R.id.txt_name);
                txtNum = convertView.findViewById(R.id.txt_num);
                ivSelect = convertView.findViewById(R.id.iv_select);
            }

        }
    }

    /*
     * 发送留言消息
     * */
    private void sendLeaveMessage(String content, long toUid, String toGid) {
        if (StringUtil.isNotNull(content)) {
            ChatMessage chat = SocketData.createChatMessage(SocketData.getUUID(), content);
            MsgAllBean messageBean = SocketData.createMessageBean(toUid, toGid, ChatEnum.EMessageType.TEXT, ChatEnum.ESendStatus.SENDING, SocketData.getFixTime(), chat);
            if (messageBean != null) {
                SocketData.sendAndSaveMessage(messageBean);
                sendMesage = messageBean;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void posting(SelectNumbEvent event) {
        if ("0".equals(event.type)) {
            actionbar.setTxtRight("完成");
        } else {
            actionbar.setTxtRight("完成(" + event.type + ")");
        }
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
