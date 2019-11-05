package com.yanlong.im.chat.ui.forward;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.ChatMessage;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.view.AlertForward;
import com.yanlong.im.databinding.ActivityGroupSaveBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.GroupHeadImageUtil;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.GsonUtils;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_group_save);
        String json = getIntent().getStringExtra(AGM_JSON);
        msgAllBean = GsonUtils.getObject(json, MsgAllBean.class);
        findViews();
        initEvent();
        initData();
    }

    //自动寻找控件
    private void findViews() {
        actionbar = ui.headView.getActionbar();
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

        ui.mtListView.getLoadView().setStateNormal();
    }

    private void initData() {
        groupInfoBeans = new ArrayList<>();
        ui.mtListView.init(new RecyclerViewAdapter());
//        taskMySaved();
        loadSavedGroup();
    }


    private void taskMySaved() {
        new MsgAction().getMySaved(new CallBack<ReturnBean<List<Group>>>(ui.mtListView) {
            @Override
            public void onResponse(Call<ReturnBean<List<Group>>> call, Response<ReturnBean<List<Group>>> response) {
                if (response.body() == null || !response.body().isOk()) {
                    ui.mtListView.getLoadView().setStateNoData(R.mipmap.ic_nodate);
                    return;
                }
                groupInfoBeans.addAll(response.body().getData());
                ui.mtListView.notifyDataSetChange(response);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void loadSavedGroup() {
        Observable.just(0)
                .map(new Function<Integer, List<Group>>() {
                    @Override
                    public List<Group> apply(Integer integer) throws Exception {
                        return msgDao.getMySavedGroup();
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
                        ui.mtListView.notifyDataSetChange();
                    }
                });
    }

    @Override
    public void onForward(final long uid, final String gid, String avatar, String nick) {
        if (msgAllBean == null)
            return;
        AlertForward alertForward = new AlertForward();
        if (msgAllBean.getChat() != null) {//转换文字
            alertForward.init(GroupSelectActivity.this, avatar, nick, msgAllBean.getChat().getMsg(), null, "发送", new AlertForward.Event() {
                @Override
                public void onON() {

                }

                @Override
                public void onYes(String content) {
                    sendMessage(uid, gid, msgAllBean.getChat().getMsg(), content);

                }
            });
        } else if (msgAllBean.getImage() != null) {

            alertForward.init(GroupSelectActivity.this, avatar, nick, null, msgAllBean.getImage().getThumbnail(), "发送", new AlertForward.Event() {
                @Override
                public void onON() {

                }

                @Override
                public void onYes(String content) {
                    ImageMessage imagesrc = msgAllBean.getImage();
                    if (msgAllBean.getFrom_uid() == UserAction.getMyId().longValue()) {
                        imagesrc.setReadOrigin(true);
                    }
                    ImageMessage imageMessage = SocketData.createImageMessage(SocketData.getUUID(), imagesrc.getOrigin(), imagesrc.getPreview(), imagesrc.getThumbnail(), imagesrc.getWidth(), imagesrc.getHeight(), false, imagesrc.isReadOrigin());
                    MsgAllBean allBean = SocketData.createMessageBean(uid, gid, msgAllBean.getMsg_type(), ChatEnum.ESendStatus.SENDING, SocketData.getFixTime(), imageMessage);
                    if (allBean != null) {
                        SocketData.sendAndSaveMessage(allBean);
                        sendMesage = allBean;
                    }
//                    sendMesage = SocketData.send4Image(toUid, toGid, imagesrc.getOrigin(), imagesrc.getPreview(), imagesrc.getThumbnail(), new Long(imagesrc.getWidth()).intValue(), new Long(imagesrc.getHeight()).intValue(), new Long(imagesrc.getSize()).intValue());
//                    msgDao.ImgReadStatSet(imagesrc.getOrigin(), imagesrc.isReadOrigin());
                    sendLeaveMessage(content, uid, gid);
                    ToastUtil.show(GroupSelectActivity.this, "转发成功");
                    setResult(RESULT_OK);
                    finish();
                    notifyRefreshMsg(gid, uid);
                }
            });

        } else if (msgAllBean.getAtMessage() != null) {

            alertForward.init(GroupSelectActivity.this, avatar, nick, msgAllBean.getAtMessage().getMsg(), null, "发送", new AlertForward.Event() {
                @Override
                public void onON() {

                }

                @Override
                public void onYes(String content) {
                    sendMessage(uid, gid, msgAllBean.getAtMessage().getMsg(), content);
                }
            });
        } else if (msgAllBean.getVideoMessage() != null) {
            alertForward.init(GroupSelectActivity.this, avatar, nick, null, msgAllBean.getVideoMessage().getBg_url(), "发送", new AlertForward.Event() {
                @Override
                public void onON() {

                }

                @Override
                public void onYes(String content) {
//                    MsgAllBean sendMesage = SocketData.转发送视频整体信息(uid, gid, msgAllBean.getVideoMessage());
//
//                    if (StringUtil.isNotNull(content)) {
//                        sendMesage = SocketData.send4Chat(uid, gid, content);
//                    }
                    VideoMessage video = msgAllBean.getVideoMessage();
                    VideoMessage videoMessage = SocketData.createVideoMessage(SocketData.getUUID(), video.getBg_url(), video.getUrl(), video.getDuration(), video.getWidth(), video.getHeight(), video.isReadOrigin());
                    MsgAllBean allBean = SocketData.createMessageBean(uid, gid, msgAllBean.getMsg_type(), ChatEnum.ESendStatus.NORMAL, SocketData.getFixTime(), videoMessage);
                    if (allBean != null) {
                        SocketData.sendAndSaveMessage(allBean);
                        sendMesage = allBean;
                    }
                    sendLeaveMessage(content, uid, gid);
                    ToastUtil.show(GroupSelectActivity.this, "转发成功");
                    setResult(RESULT_OK);
                    finish();
                    notifyRefreshMsg(gid, uid);
//                    ToastUtil.show(GroupSelectActivity.this,"转发成功");
//                    setResult(RESULT_OK);
////                    doSendSuccess();
//                    notifyRefreshMsg( gid,uid,content);
//                    finish();
                }
            });

        }
        alertForward.show();

    }

    /*
     * msg 转发消息内容
     * comments 转发留言
     * */
    private void sendMessage(long msgUid, String msgGid, String msgMsg, String comments) {

//        SocketData.send4Chat(msgUid, msgGid, msgMsg);
//        if (StringUtil.isNotNull(comments)) {
//            SocketData.send4Chat(msgUid, msgGid, comments);
//        }
        ChatMessage chatMessage = SocketData.createChatMessage(SocketData.getUUID(), msgMsg);
        MsgAllBean allBean = SocketData.createMessageBean(msgUid, msgGid, msgAllBean.getMsg_type(), ChatEnum.ESendStatus.SENDING, SocketData.getFixTime(), chatMessage);
        if (allBean != null) {
            SocketData.sendAndSaveMessage(allBean);
            sendMesage = allBean;
        }
        sendLeaveMessage(comments, msgUid, msgGid);
        setResult(RESULT_OK);
        finish();
        notifyRefreshMsg(msgGid, msgUid);
    }

    private void notifyRefreshMsg(String toGid, long toUid) {
        MessageManager.getInstance().setMessageChange(true);
        MessageManager.getInstance().notifyRefreshMsg(!TextUtils.isEmpty(toGid) ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUid, toGid, CoreEnum.ESessionRefreshTag.SINGLE, sendMesage);
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

            String imageHead = groupInfoBean.getAvatar();
            if (imageHead != null && !imageHead.isEmpty() && StringUtil.isNotNull(imageHead)) {
                Glide.with(context).load(imageHead).apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);
            } else {
                String url = msgDao.groupHeadImgGet(groupInfoBean.getGid());
                if (StringUtil.isNotNull(url)) {
                    Glide.with(context).load(url).apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);
                } else {
                    creatAndSaveImg(groupInfoBean, holder.imgHead);
                }
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
                    onForward(-1L, groupInfoBean.getGid(), groupInfoBean.getAvatar(), /*groupInfoBean.getName()*/msgDao.getGroupName(groupInfoBean.getGid()));
                }
            });


            if (getItemCount() == (position + 1)) {
                holder.txtNum.setText(getItemCount() + "个群聊");
                holder.txtNum.setVisibility(View.VISIBLE);
            } else {
                holder.txtNum.setVisibility(View.GONE);
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
            private ImageView imgHead;
            private TextView txtName;
            private TextView txtNum;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                imgHead = convertView.findViewById(R.id.img_head);
                txtName = convertView.findViewById(R.id.txt_name);
                txtNum = convertView.findViewById(R.id.txt_num);
            }

        }
    }

    private void creatAndSaveImg(Group bean, ImageView imgHead) {
        Group gginfo = bean;
        int i = gginfo.getUsers().size();
        i = i > 9 ? 9 : i;
        //头像地址
        String url[] = new String[i];
        for (int j = 0; j < i; j++) {
            MemberUser userInfo = gginfo.getUsers().get(j);
//            if (j == i - 1) {
//                name += userInfo.getName();
//            } else {
//                name += userInfo.getName() + "、";
//            }
            url[j] = userInfo.getHead();
        }
        File file = GroupHeadImageUtil.synthesis(getContext(), url);
        Glide.with(context).load(file)
                .apply(GlideOptionsUtil.headImageOptions()).into(imgHead);

        MsgDao msgDao = new MsgDao();
        msgDao.groupHeadImgCreate(gginfo.getGid(), file.getAbsolutePath());
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

}
