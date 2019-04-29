package com.yanlong.im.chat.bean;

import com.yanlong.im.utils.socket.MsgBean;



public class MsgConversionBean {


    /***
     * 把pd转成greendao需要的bean
     * @param bean
     * @return
     */
    public static MsgAllBean ToBean(MsgBean.UniversalMessage.WrapMessage bean) {

        //手动处理转换
        MsgAllBean msgAllBean=new MsgAllBean();
     //   msgAllBean.setRequest_id(bean.getRequestId());
        msgAllBean.setTimestamp(bean.getTimestamp());
        msgAllBean.setFrom_uid(bean.getFromUid());
        msgAllBean.setGid(bean.getGid());
      //  msgAllBean.setTo_uid(bean.getToUid());
        //这里需要处理用户信息

        //---------------------



        msgAllBean.setMsg_id(bean.getMsgId());



        switch (bean.getMsgType()){
            case CHAT:
                ChatMessage chat=new ChatMessage();
                chat.setMsg(bean.getChat().getMsg());
                msgAllBean.setChat(chat);
                msgAllBean.setMsg_type(1);
                break;
            case IMAGE:
                ImageMessage image=new ImageMessage();

                image.setUrl(bean.getImage().getUrl());
                msgAllBean.setImage(image);
                msgAllBean.setMsg_type(4);
            case REQUEST_FRIEND:
                msgAllBean.setMsg_type(0);
                break;
            case UNRECOGNIZED:
                msgAllBean.setMsg_type(0);
                break;
            case ACCEPT_BE_FRIENDS:
                msgAllBean.setMsg_type(0);
                break;
            case STAMP:// 戳一下消息
                StampMessage stamp=new StampMessage();
                stamp.setComment(bean.getStamp().getComment());
                msgAllBean.setStamp(stamp);
                msgAllBean.setMsg_type(2);
                break;
            case TRANSFER:
                break;
            case BUSINESS_CARD:
                BusinessCardMessage businessCard=new BusinessCardMessage();
                businessCard.setAvatar(bean.getBusinessCard().getAvatar());
                businessCard.setComment(bean.getBusinessCard().getComment());
                businessCard.setNickname(bean.getBusinessCard().getNickname());
                msgAllBean.setBusiness_card(businessCard);
                msgAllBean.setMsg_type(5);
                break;
            case RED_ENVELOPER:
                RedEnvelopeMessage envelopeMessage=new RedEnvelopeMessage();
                envelopeMessage.setComment(bean.getRedEnvelope().getComment());
                envelopeMessage.setId(bean.getRedEnvelope().getId());
                envelopeMessage.setRe_type(bean.getRedEnvelope().getReTypeValue());
                msgAllBean.setRed_envelope(envelopeMessage);
                msgAllBean.setMsg_type(3);
                break;
            case RECEIVE_RED_ENVELOPER:
                ReceiveRedEnvelopeMessage receiveRedEnvelopeMessage=new ReceiveRedEnvelopeMessage();
                receiveRedEnvelopeMessage.setId(bean.getReceiveRedEnvelope().getId());
                msgAllBean.setReceive_red_envelope(receiveRedEnvelopeMessage);
                msgAllBean.setMsg_type(3);
                break;


        }

        return msgAllBean;


    }
}
