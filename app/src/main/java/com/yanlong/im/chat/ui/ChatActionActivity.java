package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.protobuf.InvalidProtocolBufferException;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgConversionBean;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.MediaBackUtil;
import com.yanlong.im.utils.socket.MsgBean;

import net.cb.cb.library.bean.EventExitChat;
import net.cb.cb.library.view.AppActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * 音视频接听界面
 */
public class ChatActionActivity extends AppActivity {
    public static final String AGM_DATA = "data";
    private ImageView imgHead;
    private TextView txtName;
    private TextView txtMsg;
    private LinearLayout viewNo;
    private LinearLayout viewYes;
    private Vibrator vibrator;
    private MsgAllBean msgAllbean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_action);
        findViews();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (vibrator != null) {
            vibrator.cancel();
        }

    }

    //自动寻找控件
    private void findViews() {
        imgHead = findViewById(R.id.img_head);
        txtName = findViewById(R.id.txt_name);
        txtMsg = findViewById(R.id.txt_msg);
        viewNo = findViewById(R.id.view_no);
        viewYes = findViewById(R.id.view_yes);
    }


    //自动生成的控件事件
    private void initEvent() {

        byte[] data = getIntent().getByteArrayExtra(AGM_DATA);
        try {
            MsgBean.UniversalMessage.WrapMessage wrapMessage = MsgBean.UniversalMessage.WrapMessage.parseFrom(data);
            msgAllbean = MsgConversionBean.ToBean(wrapMessage);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        //显示发起人头像
        if(!TextUtils.isEmpty(msgAllbean.getFrom_avatar())){
            Glide.with(this).load(msgAllbean.getFrom_avatar())
                    .apply(GlideOptionsUtil.headImageOptions()).into(imgHead);
        }else {
            if(!TextUtils.isEmpty(msgAllbean.getFrom_user().getHead())){
                Glide.with(this).load(msgAllbean.getFrom_user().getHead())
                        .apply(GlideOptionsUtil.headImageOptions()).into(imgHead);
            }else {
                Glide.with(this).load(R.mipmap.ic_info_head)
                        .apply(GlideOptionsUtil.headImageOptions()).into(imgHead);
            }
        }
        //优先显示发起人群昵称，没有则显示用户昵称
        if(!TextUtils.isEmpty(msgAllbean.getGid())){
            txtName.setText(TextUtils.isEmpty(msgAllbean.getFrom_group_nickname()) ? msgAllbean.getFrom_nickname() : msgAllbean.getFrom_group_nickname());
        }else {
            txtName.setText(msgAllbean.getFrom_user().getName());
        }
        if(!TextUtils.isEmpty(msgAllbean.getStamp().getComment()) && !msgAllbean.getStamp().getComment().trim().equals("")){ //不为纯空字符串
            txtMsg.setVisibility(View.VISIBLE);
            txtMsg.setText(msgAllbean.getStamp().getComment());
        }else {
            txtMsg.setVisibility(View.GONE);
        }
        viewNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventExitChat());
                if(!TextUtils.isEmpty(msgAllbean.getGid())){
                    startActivity(new Intent(getContext(), ChatActivity.class)
                            .putExtra(ChatActivity.AGM_TOGID, msgAllbean.getGid()));
                }else {
                    startActivity(new Intent(getContext(), ChatActivity.class)
                            .putExtra(ChatActivity.AGM_TOUID, msgAllbean.getFrom_uid()));
                }
                finish();
            }
        });
        playVibration();
    }

    //振动
    private void playVibration() {

        vibrator = MediaBackUtil.playVibration(getContext(), 2000);
    }


}
