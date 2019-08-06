package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;

import net.cb.cb.library.utils.TimeToString;

public abstract class ChatCellBase implements View.OnClickListener {

    private final ICellEventListener mCellListener;
    private final View viewRoot;
    private final MessageAdapter mAdapter;
    private TextView tv_time, tv_broadcast, tv_name;
    private SimpleDraweeView iv_avatar;
    private final Context mContext;
    boolean isGroup;
    private MsgAllBean model;
    private int currentPosition;
    private ImageView iv_error;

    protected ChatCellBase(Context context, ChatEnum.EChatCellLayout cellLayout, ICellEventListener listener, MessageAdapter adapter, ViewGroup viewGroup) {
        mContext = context;
        mCellListener = listener;
        viewRoot = LayoutInflater.from(context).inflate(cellLayout.LayoutId, viewGroup, false);
        mAdapter = adapter;
        viewRoot.setTag(this);
//        currentPosition = position;
        isGroup = mAdapter.isGroup();
        initView();
        initListener();

    }

    protected void initListener() {

    }

    protected void initView() {
        if (viewRoot == null) {
            return;
        }
        tv_time = viewRoot.findViewById(R.id.tv_time);
        tv_broadcast = viewRoot.findViewById(R.id.tv_broadcast);
        iv_avatar = viewRoot.findViewById(R.id.iv_avatar);
        tv_name = viewRoot.findViewById(R.id.tv_name);
        iv_error = viewRoot.findViewById(R.id.iv_error);

    }

    @Override
    public void onClick(View view) {

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
        if (model.getSend_state() == 0) {
            iv_error.setVisibility(View.GONE);
        }
    }

    /*
     * 设置发送时间
     * */
    protected void setTime() {
        if (currentPosition > 0 && (model.getTimestamp() - mAdapter.getPreMessage(currentPosition - 1).getTimestamp()) < (60 * 1000)) {
            tv_time.setVisibility(View.GONE);
        } else {
            tv_time.setVisibility(View.VISIBLE);
            tv_time.setText(TimeToString.getTimeWx(model.getTimestamp()));
        }
    }

    /*
     * 设置发送者昵称
     * */
    public void setName() {
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
        Glide.with(mContext)
                .load(model.getFrom_avatar())
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

}
