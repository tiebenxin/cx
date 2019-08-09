package com.yanlong.im.chat.ui.cell;

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

import net.cb.cb.library.utils.TimeToString;

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
    private int currentPosition;
    private ImageView iv_error;
    private View bubbleLayout;

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
                        mCellListener.onEvent(ChatEnum.ECellEventType.LONG_CLICK, model, null);
                    }
                    return true;
                }
            });
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
        loadAvatar();
        setName();
        setTime();
        setSendStatus();
    }

    /*
     * 设置发送状态
     * */
    private void setSendStatus() {
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
        if (currentPosition > 0 && (model.getTimestamp() - mAdapter.getPreMessage(currentPosition - 1).getTimestamp()) < (60 * 1000)) {
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
    private void loadAvatar() {
        if (mContext == null || iv_avatar == null) {
            return;
        }
        RequestOptions options = new RequestOptions();
        options.centerCrop();
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

}
