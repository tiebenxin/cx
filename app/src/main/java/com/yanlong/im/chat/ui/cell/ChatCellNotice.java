package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.MsgTagHandler;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgNotice;
import com.yanlong.im.chat.interf.IActionTagClickListener;
import com.yanlong.im.utils.HtmlTransitonUtils;

import net.cb.cb.library.AppConfig;

/*
 * 通知消息, 撤回消息
 * */
public class ChatCellNotice extends ChatCellBase {

    private TextView tv_content;
    private ImageView iv_icon;

    protected ChatCellNotice(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_content = getView().findViewById(R.id.tv_broadcast);
        iv_icon = getView().findViewById(R.id.iv_broadcast);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        if (messageType == ChatEnum.EMessageType.NOTICE) {
            MsgNotice notice = message.getMsgNotice();
            if (notice.getMsgType() == MsgNotice.MSG_TYPE_DEFAULT
                    || notice.getMsgType() == ChatEnum.ENoticeType.RED_ENVELOPE_RECEIVED_SELF
                    || notice.getMsgType() == ChatEnum.ENoticeType.BLACK_ERROR
                    || notice.getMsgType() == ChatEnum.ENoticeType.GROUP_BAN_WORDS) {
                tv_content.setText(Html.fromHtml(message.getMsgNotice().getNote()));
            } else {
                if (notice.getMsgType() == ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED || notice.getMsgType() == ChatEnum.ENoticeType.RECEIVE_SYS_ENVELOPE
                        || notice.getMsgType() == ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED_SELF
                        || notice.getMsgType() == ChatEnum.ENoticeType.SNAPSHOT_SCREEN) {
                    tv_content.setText(Html.fromHtml(notice.getNote(), null,
                            new MsgTagHandler(AppConfig.getContext(), true, message.getMsg_id(), (IActionTagClickListener) getContext())));
                } else {
                    tv_content.setText(new HtmlTransitonUtils().getSpannableString(mContext, message.getMsgNotice().getNote(), message.getMsgNotice().getMsgType()));
                }
            }

            //如果是红包消息类型则显示红包图
            if (message.getMsgNotice().getMsgType() != null && (notice.getMsgType() == ChatEnum.ENoticeType.RED_ENVELOPE_RECEIVED
                    || notice.getMsgType() == ChatEnum.ENoticeType.RECEIVE_RED_ENVELOPE
                    || notice.getMsgType() == ChatEnum.ENoticeType.RED_ENVELOPE_RECEIVED_SELF
                    || notice.getMsgType() == ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED
                    || notice.getMsgType() == ChatEnum.ENoticeType.RECEIVE_SYS_ENVELOPE
                    || notice.getMsgType() == ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED_SELF)) {
                iv_icon.setVisibility(View.VISIBLE);
            } else {
                iv_icon.setVisibility(View.GONE);
            }
        } else if (messageType == ChatEnum.EMessageType.MSG_CANCEL) {
            iv_icon.setVisibility(View.GONE);
            tv_content.setText(Html.fromHtml(message.getMsgCancel().getNote()));
            if (message.getMsgCancel().getMsgType() == MsgNotice.MSG_TYPE_DEFAULT) {
                tv_content.setText(Html.fromHtml(message.getMsgCancel().getNote()));
            } else {
                tv_content.setText(new HtmlTransitonUtils().getSpannableString(mContext, message.getMsgCancel().getNote(), message.getMsgCancel().getMsgType()));
            }
            iv_icon.setVisibility(View.GONE);
        } else if (messageType == ChatEnum.EMessageType.CHANGE_SURVIVAL_TIME) {
            iv_icon.setVisibility(View.GONE);
            if (message.getMsgCancel() != null) {
                tv_content.setText(Html.fromHtml(message.getMsgCancel().getNote()));
            }
        } else if (messageType == ChatEnum.EMessageType.TRANSFER_NOTICE) {
            iv_icon.setVisibility(View.GONE);
            if (message.getMsgCancel() != null) {
                tv_content.setText(Html.fromHtml(message.getMsgCancel().getNote()));
            }
        }
    }
}
