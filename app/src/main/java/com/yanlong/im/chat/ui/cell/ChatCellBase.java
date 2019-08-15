package com.yanlong.im.chat.ui.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.utils.TimeToString;

import java.util.ArrayList;
import java.util.List;

import me.kareluo.ui.OptionMenu;

import static android.view.View.VISIBLE;

public abstract class ChatCellBase implements View.OnClickListener {

    public final ICellEventListener mCellListener;
    private final View viewRoot;
    public final MessageAdapter mAdapter;
    private TextView tv_time, tv_name;
    private SimpleDraweeView iv_avatar;
    public final Context mContext;
    public boolean isGroup;
    public MsgAllBean model;
    public int currentPosition;
    private ImageView iv_error;
    public View bubbleLayout;
    private List<OptionMenu> menus;

    @ChatEnum.EMessageType
    int messageType;

    boolean isMe;

    protected ChatCellBase(Context context, ChatEnum.EChatCellLayout cellLayout, ICellEventListener listener, MessageAdapter adapter, ViewGroup viewGroup) {
        mContext = context;
        mCellListener = listener;
        viewRoot = LayoutInflater.from(context).inflate(cellLayout.LayoutId, viewGroup, false);
        mAdapter = adapter;
        viewRoot.setTag(this);
        isGroup = mAdapter.isGroup();
        initView();
        initListener();
    }

    protected void initListener() {
        if (bubbleLayout != null) {
            bubbleLayout.setOnClickListener(this);

            bubbleLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mCellListener != null) {
                        mCellListener.onEvent(ChatEnum.ECellEventType.LONG_CLICK, model, menus, bubbleLayout);
                    }
                    return true;
                }
            });
        }

        if (iv_avatar != null && !isMe) {
            iv_avatar.setOnClickListener(this);
        }
        if (iv_error != null) {
            iv_error.setOnClickListener(this);
        }
    }

    protected void initView() {
        if (viewRoot == null) {
            return;
        }
        tv_time = viewRoot.findViewById(R.id.tv_time);
        iv_avatar = viewRoot.findViewById(R.id.iv_avatar);
        tv_name = viewRoot.findViewById(R.id.tv_name);
        iv_error = viewRoot.findViewById(R.id.iv_error);
        bubbleLayout = viewRoot.findViewById(R.id.ll_bubble);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == bubbleLayout.getId()) {
            onBubbleClick();
        } else if (id == iv_avatar.getId()) {
            if (mCellListener != null && !isMe) {
                mCellListener.onEvent(ChatEnum.ECellEventType.AVATAR_CLICK, model, new Object());
            }
        } else if (id == iv_error.getId()) {
            if (mCellListener != null) {
                mCellListener.onEvent(ChatEnum.ECellEventType.RESEND_CLICK, model, new Object());
            }
        }
    }

    /*
     * 显示消息内容
     * */
    protected void showMessage(MsgAllBean message) {
        if (message == null) {
            return;
        }
        model = message;
        messageType = message.getMsg_type();
        isMe = message.isMe();
        setSendStatus();
        loadAvatar();
        setName();
        setTime();
        initMenu();
    }

    private void initMenu() {
        menus = new ArrayList<>();
        switch (model.getMsg_type()) {
            case ChatEnum.EMessageType.NOTICE:
                break;
            case ChatEnum.EMessageType.TEXT:
                menus.add(new OptionMenu("复制"));
                menus.add(new OptionMenu("转发"));
                menus.add(new OptionMenu("删除"));
                break;
            case ChatEnum.EMessageType.STAMP:
                menus.add(new OptionMenu("删除"));
                break;
            case ChatEnum.EMessageType.RED_ENVELOPE:
                menus.add(new OptionMenu("删除"));
                break;
            case ChatEnum.EMessageType.IMAGE:
                menus.add(new OptionMenu("转发"));
                menus.add(new OptionMenu("删除"));
                break;
            case ChatEnum.EMessageType.BUSINESS_CARD:
                menus.add(new OptionMenu("删除"));
                break;
            case ChatEnum.EMessageType.VOICE:
                menus.add(new OptionMenu("删除"));
                MsgDao msgDao = new MsgDao();
                if (msgDao.userSetingGet().getVoicePlayer() == 0) {
                    menus.add(0, new OptionMenu("听筒播放"));
                } else {
                    menus.add(0, new OptionMenu("扬声器播放"));
                }
                break;
            case ChatEnum.EMessageType.AT:
                menus.add(new OptionMenu("复制"));
                menus.add(new OptionMenu("转发"));
                menus.add(new OptionMenu("删除"));
                break;
            case ChatEnum.EMessageType.ASSISTANT:
                break;

        }
        if (isMe && model.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
            if (model.getFrom_uid() != null && model.getFrom_uid().longValue() == UserAction.getMyId().longValue()) {
                if (System.currentTimeMillis() - model.getTimestamp() < 2 * 60 * 1000) {//两分钟内可以删除
                    menus.add(new OptionMenu("撤回"));
                }
            }
        }
    }

    /*
     * 设置发送状态
     * */
    public void setSendStatus() {
        if (iv_error != null && model.isMe()) {
            switch (model.getSend_state()) {
                case ChatEnum.ESendStatus.ERROR:
                    iv_error.clearAnimation();
                    iv_error.setImageResource(R.mipmap.ic_net_err);
                    iv_error.setVisibility(VISIBLE);
                    break;
                case ChatEnum.ESendStatus.PRE_SEND:
                    iv_error.clearAnimation();
                    iv_error.setVisibility(View.GONE);
                    break;
                case ChatEnum.ESendStatus.NORMAL:
                    iv_error.clearAnimation();
                    iv_error.setVisibility(View.GONE);
                    break;
                case ChatEnum.ESendStatus.SENDING:
                    iv_error.setImageResource(R.mipmap.ic_net_load);
                    Animation rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_circle_rotate);
                    iv_error.startAnimation(rotateAnimation);
                    iv_error.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    /*
     * 设置发送时间
     * */
    protected void setTime() {
        if (tv_time == null) {
            return;
        }
        if (currentPosition > 0 && (model.getTimestamp() - mAdapter.getPositionMessage(currentPosition - 1).getTimestamp()) < (60 * 1000)) {
            tv_time.setVisibility(View.GONE);
        } else {
            tv_time.setVisibility(VISIBLE);
            tv_time.setText(TimeToString.getTimeWx(model.getTimestamp()));
        }
    }

    /*
     * 设置发送者昵称
     * */
    public void setName() {
        if (tv_name == null) {
            return;
        }
        if (!isGroup) {
            tv_name.setVisibility(View.GONE);
        } else {
            if (model.isMe()) {
                tv_name.setVisibility(View.GONE);
            } else {
                tv_name.setText(model.getFrom_nickname());
            }
        }
    }

    /*
     * 加载发送者头像
     * */
    @SuppressLint("CheckResult")
    private void loadAvatar() {
        if (mContext == null || iv_avatar == null) {
            return;
        }
        RequestOptions options = new RequestOptions();
        options.centerCrop();
        options.error(R.drawable.ic_info_head);
        Glide.with(mContext)
                .load(model.getFrom_avatar())
                .apply(options)
                .into(iv_avatar);

    }

    /*
     * 获取viewRoot
     * */
    public View getView() {
        return viewRoot;
    }

    public Context getContext() {
        return mContext;
    }


    /*
     * 初始化MsgAllBean, currentPosition
     * */
    public void putMessage(MsgAllBean bean, int p) {
        model = bean;
        this.currentPosition = p;
        showMessage(bean);
    }

    public void onBubbleClick() {

    }

    /*
     * 更新消息model,更新menu，主要针对图片消息
     * */
    public void updateMessage(MsgAllBean bean) {
        model = bean;
        updateMenu();
    }

    protected void updateMenu() {
        if (isMe && model.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
            if (model.getFrom_uid() != null && model.getFrom_uid().longValue() == UserAction.getMyId().longValue()) {
                if (System.currentTimeMillis() - model.getTimestamp() < 2 * 60 * 1000) {//两分钟内可以删除
                    menus.add(new OptionMenu("撤回"));
                }
            }
        }
    }

}
