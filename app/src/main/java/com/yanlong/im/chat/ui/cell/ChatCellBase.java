package com.yanlong.im.chat.ui.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.AtMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.interf.IActionTagClickListener;
import com.yanlong.im.chat.interf.IMenuSelectListener;
import com.yanlong.im.user.action.UserAction;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.TimeToString;

import java.util.ArrayList;
import java.util.List;

import me.kareluo.ui.OptionMenu;

import static android.view.View.VISIBLE;

public abstract class ChatCellBase extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final ICellEventListener mCellListener;
    private final View viewRoot;
    public final MessageAdapter mAdapter;
    private TextView tv_time, tv_name;
    private ImageView iv_avatar;
    public final Context mContext;
    public boolean isGroup;
    public MsgAllBean model;
    public int currentPosition;
    private ImageView iv_error;
    public View bubbleLayout;
    private List<OptionMenu> menus;

    IMenuSelectListener menuListener = new IMenuSelectListener() {
        @Override
        public void onSelected() {
            updateSelectedBG(false);
        }
    };

    @ChatEnum.EMessageType
    int messageType;

    boolean isMe;
    private CheckBox ckSelect;
    private AppCompatImageView ivBell;
    private int newMsgPosition;
    private View viewRead;
    private TextView tvRead;
    private TextView tvReadTime;
    public IActionTagClickListener actionTagClickListener;
    private boolean isOpenRead = true;//是否开启已读开关

    protected ChatCellBase(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(view);
        mContext = context;
        mCellListener = listener;
        this.viewRoot = view;
        mAdapter = adapter;
        isGroup = mAdapter.isGroup();
        initView();
    }

    protected void setActionClickListener(IActionTagClickListener l) {
        actionTagClickListener = l;
    }


    protected void initListener() {
        if (bubbleLayout != null) {
            bubbleLayout.setOnClickListener(this);

            bubbleLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mCellListener != null) {
                        updateSelectedBG(true);
                        checkCancelMenu();//临时检测撤回menu
                        mCellListener.onEvent(ChatEnum.ECellEventType.LONG_CLICK, model, menus, bubbleLayout, menuListener);
                    }
                    return true;
                }
            });
        }

        if (iv_avatar != null && !isMe) {
            iv_avatar.setOnClickListener(this);
            if (isGroup) {
                iv_avatar.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mCellListener != null) {
                            mCellListener.onEvent(ChatEnum.ECellEventType.AVATAR_LONG_CLICK, model, new Object());
                        }
                        return true;
                    }
                });
            }
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
        bubbleLayout = viewRoot.findViewById(R.id.view_bubble);
        ckSelect = viewRoot.findViewById(R.id.ck_select);
        ivBell = viewRoot.findViewById(R.id.iv_bell);

        viewRead = viewRoot.findViewById(R.id.view_read);
        tvRead = viewRoot.findViewById(R.id.tv_read);
        tvReadTime = viewRoot.findViewById(R.id.tv_read_time);

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
        initListener();
        setSendStatus(false);
        loadAvatar();
        setName();
        setTime();
        initMenu();
        setCheckView();
        initBell();
        initRead();
    }


    private void initMenu() {
        menus = new ArrayList<>();
        switch (model.getMsg_type()) {
            case ChatEnum.EMessageType.NOTICE:
                break;
            case ChatEnum.EMessageType.TEXT:
            case ChatEnum.EMessageType.AT:
                menus.add(new OptionMenu("复制"));
                menus.add(new OptionMenu("转发"));
                menus.add(new OptionMenu("删除"));
                break;
            case ChatEnum.EMessageType.STAMP:
            case ChatEnum.EMessageType.BUSINESS_CARD:
            case ChatEnum.EMessageType.RED_ENVELOPE:
            case ChatEnum.EMessageType.ASSISTANT:
                menus.add(new OptionMenu("删除"));
                break;
            case ChatEnum.EMessageType.IMAGE:
                menus.add(new OptionMenu("转发"));
                menus.add(new OptionMenu("删除"));
                break;
            case ChatEnum.EMessageType.VOICE:
                MsgDao msgDao = new MsgDao();
                if (msgDao.userSetingGet().getVoicePlayer() == 0) {
                    menus.add(0, new OptionMenu("听筒播放"));
                } else {
                    menus.add(0, new OptionMenu("扬声器播放"));
                }
                menus.add(new OptionMenu("删除"));
                break;

        }
    }

    private void checkCancelMenu() {
        //云红包不能撤回
        if (isMe && model.getSend_state() == ChatEnum.ESendStatus.NORMAL && model.getMsg_type() != ChatEnum.EMessageType.RED_ENVELOPE && !isAtBanedCancel(model)) {
            if (model.getFrom_uid() != null && model.getFrom_uid().longValue() == UserAction.getMyId().longValue()) {
                if (System.currentTimeMillis() - model.getTimestamp() < 2 * 60 * 1000) {//两分钟内可以删除
                    menus.add(new OptionMenu("撤回"));
                }
            }
        }
    }

    //是否禁止撤销at消息,群主自己发的群公告，不能撤消
    private boolean isAtBanedCancel(MsgAllBean bean) {
        if (bean.getMsg_type() == ChatEnum.EMessageType.AT) {
            AtMessage message = bean.getAtMessage();
            if (message.getAt_type() == ChatEnum.EAtType.ALL && message.getUid().size() == 0) {
                return true;
            }
        }
        return false;
    }

    /*
     * 设置发送状态
     * */
    public void setSendStatus(boolean isShowLoad) {
        if (iv_error != null && model.isMe()) {
            switch (model.getSend_state()) {
                case ChatEnum.ESendStatus.ERROR:
                    iv_error.clearAnimation();
                    iv_error.setImageResource(R.mipmap.ic_net_err);
                    iv_error.setVisibility(VISIBLE);
                    break;
                case ChatEnum.ESendStatus.PRE_SEND:
                    if (isShowLoad) {
                        iv_error.setImageResource(R.mipmap.ic_net_load);
                        Animation rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_circle_rotate);
                        iv_error.startAnimation(rotateAnimation);
                        iv_error.setVisibility(View.VISIBLE);
                    } else {
                        iv_error.clearAnimation();
                        iv_error.setVisibility(View.GONE);
                    }
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
        if (currentPosition > 0 && (model.getTimestamp() - mAdapter.getMessage(currentPosition - 1).getTimestamp()) < (60 * 1000)) {
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
        iv_avatar.setImageResource(R.mipmap.ic_info_head);
        Glide.with(mContext)
                .asBitmap()
                .load(model.getFrom_avatar())
//                .apply(GlideOptionsUtil.headImageOptions())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        iv_avatar.setImageBitmap(resource);
                    }
                });
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
            if (model.getFrom_uid() != null && model.getFrom_uid().longValue() == UserAction.getMyId().longValue() && model.getMsg_type() != ChatEnum.EMessageType.RED_ENVELOPE) {
                if (System.currentTimeMillis() - model.getTimestamp() < 2 * 60 * 1000) {//两分钟内可以删除
                    menus.add(new OptionMenu("撤回"));
                }
            }
        }
    }

    private void updateSelectedBG(boolean flag) {
        if (bubbleLayout != null) {
            bubbleLayout.setSelected(flag);
        }
    }

    private void initBell() {
        if (model.getSurvival_time() > 0 && model.getStartTime() > 0 && model.getEndTime() > 0) {
            //阅后即焚
//            bindTimer(msgbean.getMsg_id(), msgbean.isMe(), msgbean.getStartTime(), msgbean.getEndTime());
            setBellUI(model.getSurvival_time(), false, isMe);
        } else {
            setBellUI(model.getSurvival_time(), true, isMe);
        }


    }

    //设置阅后即焚时钟UI
    public void setBellUI(int type, boolean isRecovery, boolean isMe) {
        if (ivBell == null) {
            return;
        }
        if (isMe) {
            if (isRecovery) {
                ivBell.setImageResource(R.mipmap.icon_st_1);
            }
            if (type == -1) {
                ivBell.setVisibility(View.VISIBLE);
            } else if (type == 0) {
                ivBell.setVisibility(View.GONE);
            } else {
                ivBell.setVisibility(View.VISIBLE);
            }
        } else {
            if (isRecovery) {
                ivBell.setImageResource(R.mipmap.icon_st_1);
            }
            if (type == -1) {
                ivBell.setVisibility(View.VISIBLE);
            } else if (type == 0) {
                ivBell.setVisibility(View.GONE);
            } else {
                ivBell.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setCheckView() {
        if (ckSelect == null) {
            return;
        }
        if (mAdapter.isShowCheckBox()) {
            ckSelect.setVisibility(VISIBLE);
        } else {
            ckSelect.setVisibility(View.GONE);
        }
    }

    private void setNewMsgPostion(int position) {
        newMsgPosition = position;

    }

    //初始化已读及已读时间
    private void initRead() {
        if (viewRead == null) {
            return;
        }
        if (isMe && isOpenRead && model.getSend_state() == ChatEnum.ESendStatus.NORMAL && model.getRead() == 1 && model.getReadTime() > 0) {
            viewRead.setVisibility(VISIBLE);
            tvRead.setText("已读");
            tvReadTime.setText(TimeToString.HH_MM(model.getReadTime()));
        } else {
            viewRead.setVisibility(View.GONE);
        }
    }

    public void setBellId(int rid) {
        LogUtil.getLog().i("SurvivalTime", "--setBellId--rid=" + rid);
        if (ivBell != null) {
            ivBell.setImageResource(rid);
        }
    }

    public void setReadStatus(boolean isOpen) {
        isOpenRead = isOpen;
    }

}
