package com.yanlong.im.chat;

import android.support.annotation.IntDef;

import com.yanlong.im.R;

import net.cb.cb.library.CoreEnum;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.yanlong.im.chat.ChatEnum.EMessageType.ASSISTANT;
import static com.yanlong.im.chat.ChatEnum.EMessageType.AT;
import static com.yanlong.im.chat.ChatEnum.EMessageType.BUSINESS_CARD;
import static com.yanlong.im.chat.ChatEnum.EMessageType.IMAGE;
import static com.yanlong.im.chat.ChatEnum.EMessageType.NOTICE;
import static com.yanlong.im.chat.ChatEnum.EMessageType.RED_ENVELOPE;
import static com.yanlong.im.chat.ChatEnum.EMessageType.STAMP;
import static com.yanlong.im.chat.ChatEnum.EMessageType.TEXT;
import static com.yanlong.im.chat.ChatEnum.EMessageType.TRANSFER;
import static com.yanlong.im.chat.ChatEnum.EMessageType.VOICE;
import static com.yanlong.im.chat.ChatEnum.ESendStatus.PRE_SEND;

public class ChatEnum {
    /*
     * 聊天布局枚举
     * */
    public enum EChatCellLayout {
        // 文本消息，接收和发送布局
        TEXT_RECEIVED(R.layout.cell_txt_received),
        TEXT_SEND(R.layout.cell_txt_send),

        //图片消息
        IMAGE_RECEIVED(R.layout.cell_img_received),
        IMAGE_SEND(R.layout.cell_img_send),

        //语音消息
        VOICE_RECEIVED(R.layout.cell_txt_received),
        VOICE_SEND(R.layout.cell_txt_send),

        //视频消息
//        VIDEO_RECEIVED(R.layout.cell_txt_received),
//        VIDEO_SEND(R.layout.cell_txt_send),

        //位置消息
//        MAP_RECEIVED(R.layout.cell_txt_received),
//        MAP_SEND(R.layout.cell_txt_send),

//        VOTE_RECEIVED(R.layout.cell_txt_received),
//        VOTE_SEND(R.layout.cell_txt_send),

        //动态表情消息
//        EMOTICON_RECEIVED(R.layout.cell_txt_received),
//        EMOTICON_SEND(R.layout.cell_txt_send),

        //名片消息
        CARD_RECEIVED(R.layout.cell_txt_received),
        CARD_SEND(R.layout.cell_txt_send),

        //红包消息
        RED_ENVELOPE_RECEIVED(R.layout.cell_txt_received),
        RED_ENVELOPE_SEND(R.layout.cell_txt_send),


        //合并转发
//        MULTI_RECEIVED(R.layout.cell_txt_received),
//        MULTI_SEND(R.layout.cell_txt_send),

        //通知消息
        NOTICE(R.layout.cell_notice);


        public final int LayoutId;

        EChatCellLayout(int layoutId) {
            this.LayoutId = layoutId;
        }

        public static EChatCellLayout fromOrdinal(int ordinal) {
            EChatCellLayout result = null;
            for (EChatCellLayout item : EChatCellLayout.values()) {
                if (item.ordinal() == ordinal) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("EChatCellLayout - fromOrdinal");
            }
            return result;
        }

        public static EChatCellLayout fromLayoutId(int layoutId) {
            EChatCellLayout result = null;
            for (EChatCellLayout item : EChatCellLayout.values()) {
                if (item.LayoutId == layoutId) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("EChatCellLayout - fromLayoutId");
            }
            return result;
        }

        public static int size() {
            return values().length;
        }
    }

    /*
     * cell 点击事件类型
     * */
    @IntDef({CoreEnum.ESureType.NO, CoreEnum.ESureType.YES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ECellEventType {
        int TXT_CLICK = 0; //点击文本消息
        int IMAGE_CLICK = 1;//点击图片
        int CARD_CLICK = 2;//点击名片
        int RED_ENVELOPE_CLICK = 3;//点击红包
    }


    /*
     * 消息type
     * */
    @IntDef({NOTICE, TEXT, STAMP, RED_ENVELOPE, IMAGE, BUSINESS_CARD, TRANSFER, VOICE, AT, ASSISTANT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EMessageType {
        int NOTICE = 0; //公告
        int TEXT = 1;//文本
        int STAMP = 2;//戳一下
        int RED_ENVELOPE = 3;//红包
        int IMAGE = 4;//图片
        int BUSINESS_CARD = 5;//名片
        int TRANSFER = 6;//转发
        int VOICE = 7;//语音
        int AT = 8;//艾特@消息
        int ASSISTANT = 9;//小助手
    }

    /*
     * 发送状态
     * 0:正常,1:错误,2:发送中 -1 预发送
     * */
    @IntDef({PRE_SEND,})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ESendStatus {
        int PRE_SEND = -1; //预发送
        int NORMAL = 0; //正常
        int ERROR = 1;//错误
        int SENDING = 2;//发送中
    }
}
