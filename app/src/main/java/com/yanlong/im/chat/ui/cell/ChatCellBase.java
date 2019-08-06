package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;

public abstract class ChatCellBase implements View.OnClickListener {

    private final ICellEventListener mCellListener;
    private final View viewRoot;
    private final MessageAdapter mAdapter;
    private MsgAllBean messageBean;
    private TextView tv_time, tv_broadcast, tv_name;
    private SimpleDraweeView iv_avatar;
    private final Context mContext;
    //    private final int currentPosition;

    protected ChatCellBase(Context context, ChatEnum.EChatCellLayout cellLayout, ICellEventListener listener, MessageAdapter adapter, ViewGroup viewGroup) {
        mContext = context;
        mCellListener = listener;
        viewRoot = LayoutInflater.from(context).inflate(cellLayout.LayoutId, viewGroup, false);
        mAdapter = adapter;
        viewRoot.setTag(this);
//        currentPosition = position;
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
    }

    @Override
    public void onClick(View view) {

    }

    protected void showMessage(MsgAllBean message) {
        if (message == null) {
            return;
        }
        loadAvatar();
        setName();
    }

    private void setName() {
        tv_name.setText(messageBean.getFrom_nickname());

    }

    private void loadAvatar() {
        Glide.with(mContext)
                .load(messageBean.getFrom_avatar())
                .into(iv_avatar);

    }


    public View getView() {
        return viewRoot;
    }

    public Context getContext() {
        return mContext;
    }

    public void putMessage(MsgAllBean bean) {
        messageBean = bean;
        showMessage(bean);
    }
}
