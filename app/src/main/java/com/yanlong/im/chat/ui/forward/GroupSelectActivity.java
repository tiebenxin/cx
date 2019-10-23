package com.yanlong.im.chat.ui.forward;

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
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.view.AlertForward;
import com.yanlong.im.databinding.ActivityGroupSaveBinding;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.GsonUtils;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import java.util.ArrayList;
import java.util.List;

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
        taskMySaved();
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
                    // ToastUtil.show(context, msgAllBean.getChat().getMsg()+"---\n"+content);

//                    Long toUId = bean.getFrom_uid();
//                    String toGid = bean.getGid();
//                    SocketData.send4Chat(uid, gid, msgAllBean.getChat().getMsg());
//                    if (StringUtil.isNotNull(content)) {
//                        SocketData.send4Chat(uid, gid, content);
//                    }
//                    finish();
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
                    // ToastUtil.show(context, msgAllBean.getImage().getThumbnail()+"---\n"+content);
//                    Long toUId = bean.getFrom_uid();
//                    String toGid = bean.getGid();
                    ImageMessage imagesrc = msgAllBean.getImage();
                    SocketData.send4Image(uid, gid, imagesrc.getOrigin(), imagesrc.getPreview(), imagesrc.getThumbnail(), new Long(imagesrc.getWidth()).intValue(), new Long(imagesrc.getHeight()).intValue(), new Long(imagesrc.getSize()).intValue());
                    msgDao.ImgReadStatSet(imagesrc.getOrigin(), true);
                    if (StringUtil.isNotNull(content)) {
                        SocketData.send4Chat(uid, gid, content);
                    }
                    setResult(RESULT_OK);
                    finish();

                }
            });

        } else if (msgAllBean.getAtMessage() != null) {

            alertForward.init(GroupSelectActivity.this, avatar, nick, msgAllBean.getAtMessage().getMsg(), null, "发送", new AlertForward.Event() {
                @Override
                public void onON() {

                }

                @Override
                public void onYes(String content) {
                    // ToastUtil.show(context, msgAllBean.getChat().getMsg()+"---\n"+content);

//                    Long toUId = bean.getFrom_uid();
//                    String toGid = bean.getGid();
//                    SocketData.send4Chat(uid, gid, msgAllBean.getAtMessage().getMsg());
//                    if (StringUtil.isNotNull(content)) {
//                        SocketData.send4Chat(uid, gid, content);
//                    }
//                    finish();

                    sendMessage(uid, gid, msgAllBean.getAtMessage().getMsg(), content);
                }
            });
        }
//        else if(msgAllBean.getVideoMessage() != null){
//            alertForward.init(GroupSelectActivity.this, avatar, nick, msgAllBean.getAtMessage().getMsg(), null, "发送", new AlertForward.Event() {
//                @Override
//                public void onON() {
//
//                }
//
//                @Override
//                public void onYes(String content) {
//
//
//                    sendMessage(uid, gid, msgAllBean.getVideoMessage().getMsg(), content);
//                }
//            });
//        }
        else if (msgAllBean.getVideoMessage() != null) {
            alertForward.init(GroupSelectActivity.this, avatar, nick, null, msgAllBean.getVideoMessage().getBg_url(), "发送", new AlertForward.Event() {
                @Override
                public void onON() {

                }

                @Override
                public void onYes(String content) {

                    MsgAllBean  sendMesage = SocketData.转发送视频整体信息(uid, gid, msgAllBean.getVideoMessage());

                    if (StringUtil.isNotNull(content)) {
                        sendMesage = SocketData.send4Chat(uid, gid, content);
                    }
                    ToastUtil.show(GroupSelectActivity.this,"转发成功");
                    finish();
//                    doSendSuccess();
                    notifyRefreshMsg( gid,uid,content);
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
        SocketData.send4Chat(msgUid, msgGid, msgMsg);
        if (StringUtil.isNotNull(comments)) {
            SocketData.send4Chat(msgUid, msgGid, comments);
        }
        setResult(RESULT_OK);
        finish();
    }
    private void notifyRefreshMsg(String toGid, long toUid,String content) {
        MessageManager.getInstance().setMessageChange(true);
        MessageManager.getInstance().notifyRefreshMsg(!TextUtils.isEmpty(toGid) ? CoreEnum.EChatType.GROUP : CoreEnum.EChatType.PRIVATE, toUid, toGid, CoreEnum.ESessionRefreshTag.SINGLE, content);
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
         //   holder.imgHead.setImageURI(groupInfoBean.getAvatar() + "");
            Glide.with(context).load(groupInfoBean.getAvatar())
                    .apply(GlideOptionsUtil.headImageOptions()).into(holder.imgHead);

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

}
