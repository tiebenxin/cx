package com.yanlong.im.chat.ui.chat;


import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.BusinessCardMessage;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.chat.ui.cell.OnControllerClickListener;
import com.yanlong.im.utils.ExpressionUtil;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.utils.SharedPreferencesUtil;

/**
 * Created by Liszt on 2020/1/6.
 * 回复消息控件
 */

public class ControllerReplyMessage {

    private View rootView;
    private OnControllerClickListener listenter;
    private final ImageView ivImage;
    private final ImageView ivCancel;
    private final TextView tvName;
    private final TextView tvContent;

    public ControllerReplyMessage(View v) {
        rootView = v;
        ivImage = v.findViewById(R.id.iv_reply_image);
        ivCancel = v.findViewById(R.id.iv_reply_cancel);
        tvName = v.findViewById(R.id.tv_reply_name);
        tvContent = v.findViewById(R.id.tv_reply_content);
        ivCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击取消回复
                if (listenter != null) {
                    listenter.onClick();
                }
            }
        });
    }

    public void setVisible(boolean visible) {
        rootView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setMessage(MsgAllBean message) {
        if (message == null) {
            return;
        }
        int type = message.getMsg_type();
        if (isTextType(type)) {
            ivImage.setVisibility(View.GONE);
        } else {
            ivImage.setVisibility(View.VISIBLE);
        }
        tvName.setText(message.getFrom_nickname());
        tvContent.setText(getText(message, type));
        if (ivImage.isShown()) {
            setImage(message, type);
        }


    }

    //是否是显示文本消息类型
    private final boolean isTextType(@ChatEnum.EMessageType int type) {
        if (type == ChatEnum.EMessageType.TEXT || type == ChatEnum.EMessageType.AT || type == ChatEnum.EMessageType.STAMP || type == ChatEnum.EMessageType.REPLY) {
            return true;
        }
        return false;
    }

    //获取显示文本内容
    private final SpannableString getText(MsgAllBean message, @ChatEnum.EMessageType int type) {
        String content = "";
        switch (type) {
            case ChatEnum.EMessageType.TEXT:
                content = message.getChat().getMsg();
                break;
            case ChatEnum.EMessageType.AT:
                content = message.getAtMessage().getMsg();
                break;
            case ChatEnum.EMessageType.STAMP:
                content = message.getStamp().getComment();
                break;
            case ChatEnum.EMessageType.REPLY:
                if (message.getReplyMessage().getChatMessage() != null) {
                    content = message.getReplyMessage().getChatMessage().getMsg();
                } else if (message.getReplyMessage().getAtMessage() != null) {
                    content = message.getReplyMessage().getAtMessage().getMsg();
                }
                break;
            case ChatEnum.EMessageType.IMAGE:
                content = "图片";
                break;
            case ChatEnum.EMessageType.VOICE:
                content = "语音";
                break;
            case ChatEnum.EMessageType.SHIPPED_EXPRESSION:
                content = "表情";
                break;
            case ChatEnum.EMessageType.MSG_VIDEO:
                content = "视频";
                break;
            case ChatEnum.EMessageType.BUSINESS_CARD:
                String nick = message.getBusiness_card().getNickname();
                content = nick + "的名片";
                break;
            case ChatEnum.EMessageType.FILE:
                String file = message.getSendFileMessage().getFile_name();
                content = "[文件]" + file;
                break;
        }
        return getSpan(content);

    }


    public void setClickListener(OnControllerClickListener l) {
        listenter = l;
    }

    private SpannableString getSpan(String msg) {
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        SpannableString spannableString = null;
        if (fontSize != null) {
            spannableString = ExpressionUtil.getExpressionString(AppConfig.getContext(), fontSize.intValue(), msg);
        } else {
            spannableString = ExpressionUtil.getExpressionString(AppConfig.getContext(), ExpressionUtil.DEFAULT_SIZE, msg);
        }
        return spannableString;
    }

    private void setImage(MsgAllBean bean, int type) {
        switch (type) {
            case ChatEnum.EMessageType.IMAGE:
                ImageMessage image = bean.getImage();
                Glide.with(ivImage.getContext()).load(image.getThumbnail()).into(ivImage);
                break;
            case ChatEnum.EMessageType.VOICE:
                Glide.with(ivImage.getContext()).load(R.mipmap.ic_reply_voice).into(ivImage);
                break;
            case ChatEnum.EMessageType.FILE:
                ivImage.setImageResource(MessageManager.getInstance().getFileIconRid(bean.getSendFileMessage().getFormat()));
                break;
            case ChatEnum.EMessageType.BUSINESS_CARD:
                BusinessCardMessage card = bean.getBusiness_card();
                Glide.with(ivImage.getContext()).load(card.getAvatar()).into(ivImage);
                break;
            case ChatEnum.EMessageType.MSG_VIDEO:
                VideoMessage video = bean.getVideoMessage();
                Glide.with(ivImage.getContext()).load(video.getBg_url()).into(ivImage);
                break;
        }
    }
}
