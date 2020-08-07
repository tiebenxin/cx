package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.MsgTagHandler;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgNotice;
import com.yanlong.im.utils.HtmlTransitonUtils;
import com.yanlong.im.utils.PatternUtil;
import com.yanlong.im.utils.socket.MsgBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 通知消息, 撤回消息
 * */
public class ChatCellNotice extends ChatCellBase {
    private final int RELINQUISH_TIME = 5;// 5分钟内显示重新编辑
    private final String REST_EDIT = "重新编辑";

    private TextView tv_content;
    private ImageView iv_icon;

    protected ChatCellNotice(Context context, View view, ICellEventListener listener, MessageAdapter adapter) {
        super(context, view, listener, adapter);
    }

    @Override
    protected void initView() {
        super.initView();
        tv_content = getView().findViewById(R.id.tv_note);
        iv_icon = getView().findViewById(R.id.iv_broadcast);
    }

    @Override
    protected void showMessage(MsgAllBean message) {
        super.showMessage(message);
        if (messageType == ChatEnum.EMessageType.NOTICE) {
            MsgNotice notice = message.getMsgNotice();
            if (notice.getMsgType() != null) {
                if (notice.getMsgType() == MsgNotice.MSG_TYPE_DEFAULT
                        || notice.getMsgType() == ChatEnum.ENoticeType.RED_ENVELOPE_RECEIVED_SELF
                        || notice.getMsgType() == ChatEnum.ENoticeType.BLACK_ERROR
                        || notice.getMsgType() == ChatEnum.ENoticeType.GROUP_BAN_WORDS
                        || notice.getMsgType() == ChatEnum.ENoticeType.FREEZE_ACCOUNT
                        || notice.getMsgType() == ChatEnum.ENoticeType.SEAL_ACCOUNT) {
                    tv_content.setText(Html.fromHtml(message.getMsgNotice().getNote()));
                } else {
                    if (notice.getMsgType() == ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED || notice.getMsgType() == ChatEnum.ENoticeType.RECEIVE_SYS_ENVELOPE
                            || notice.getMsgType() == ChatEnum.ENoticeType.SYS_ENVELOPE_RECEIVED_SELF
                            || notice.getMsgType() == ChatEnum.ENoticeType.SNAPSHOT_SCREEN || notice.getMsgType() == ChatEnum.ENoticeType.DEFAULT_USER) {
                        tv_content.setText(Html.fromHtml(notice.getNote(), null,
                                new MsgTagHandler(getContext(), true, message.getMsg_id(), actionTagClickListener)));
                    } else {
                        tv_content.setText(new HtmlTransitonUtils().getSpannableString(mContext, message.getMsgNotice().getNote(), message.getMsgNotice().getMsgType(),0));
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
            }
        } else if (messageType == ChatEnum.EMessageType.MSG_CANCEL) {
            iv_icon.setVisibility(View.GONE);
            String content = message.getMsgCancel().getNote();
            Long mss = System.currentTimeMillis() - message.getTimestamp();
            long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
            boolean isCustoerFace = false;
            Integer cancelMsgType = message.getMsgCancel().getCancelContentType();
            if (!TextUtils.isEmpty(content) && content.length() == PatternUtil.FACE_CUSTOMER_LENGTH) {// 自定义表情不给重新编辑
                Pattern patten = Pattern.compile(PatternUtil.PATTERN_FACE_CUSTOMER, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
                Matcher matcher = patten.matcher(content);
                if (matcher.matches()) {
                    isCustoerFace = true;
                }
            }
            if (isMe && cancelMsgType != null && (cancelMsgType == ChatEnum.EMessageType.TEXT || cancelMsgType == ChatEnum.EMessageType.AT)
                    && minutes < RELINQUISH_TIME && !TextUtils.isEmpty(content) && !isCustoerFace && message.getMsgCancel().getUid()!=null && message.getMsgCancel().getUid().longValue()==message.getFrom_uid().longValue()) {
                //存的时候把空格处理<br>，否则会被Html格式化
                String contents = message.getMsgCancel().getCancelContent().replace("\n", "<br>");
                content = content + "<cancel content='" + contents + "'> 重新编辑</cancel>";
                tv_content.setText(Html.fromHtml(content, null,
                        new MsgTagHandler(getContext(), true, message.getMsg_id(), actionTagClickListener)));
            } else {
                if (message.getMsgCancel().getMsgType() == MsgNotice.MSG_TYPE_DEFAULT) {
                    tv_content.setText(Html.fromHtml(message.getMsgCancel().getNote()));
                } else {
                    //A撤自己的消息，新版显示群主/管理员身份
                    if(message.getMsgCancel().getUid()!=null){
                        if(message.getMsgCancel().getUid().longValue()==0L || message.getMsgCancel().getUid().longValue()==message.getFrom_uid().longValue()){
                            if (message.getMsgCancel().getRole() == MsgBean.CancelMessage.Role.MASTER_VALUE){
                                tv_content.setText(new HtmlTransitonUtils().getSpannableString(mContext, message.getMsgCancel().getNote(), message.getMsgCancel().getMsgType(),1));
                            }else if(message.getMsgCancel().getRole() == MsgBean.CancelMessage.Role.VICE_ADMIN_VALUE){
                                tv_content.setText(new HtmlTransitonUtils().getSpannableString(mContext, message.getMsgCancel().getNote(), message.getMsgCancel().getMsgType(),2));
                            }else {
                                tv_content.setText(new HtmlTransitonUtils().getSpannableString(mContext, message.getMsgCancel().getNote(), message.getMsgCancel().getMsgType(),0));
                            }
                        }else {
                            //A撤回了B的消息，携带被撤回人的uid
                            if (message.getMsgCancel().getRole() == MsgBean.CancelMessage.Role.MASTER_VALUE) {
                                tv_content.setText("群主" + message.getMsgCancel().getNote());
                            } else {
                                tv_content.setText("管理员" + message.getMsgCancel().getNote());
                            }
                        }
                    }else {
                        //A撤自己的消息，保留原有逻辑不变
                        tv_content.setText(new HtmlTransitonUtils().getSpannableString(mContext, message.getMsgCancel().getNote(), message.getMsgCancel().getMsgType(),0));
                    }
                }
            }
        } else if (messageType == ChatEnum.EMessageType.CHANGE_SURVIVAL_TIME) {
            iv_icon.setVisibility(View.GONE);
            if (message.getMsgCancel() != null) {
                tv_content.setText(Html.fromHtml(message.getMsgCancel().getNote()));
            }
        }
        tv_content.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
