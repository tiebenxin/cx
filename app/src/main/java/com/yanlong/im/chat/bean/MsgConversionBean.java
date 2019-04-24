package com.yanlong.im.chat.bean;

import com.yanlong.im.utils.socket.MsgBean;



public class MsgConversionBean {


    /***
     * 把pd转成greendao需要的bean
     * @param umsg
     * @return
     */
    public static MsgAllBean ToBean(MsgBean.UniversalMessage umsg) {
        MsgBean.UniversalMessage bean = umsg;
        //手动处理转换
        MsgAllBean msgAllBean=new MsgAllBean();
        msgAllBean.setRequest_id(bean.getRequestId());
        msgAllBean.setTimestamp(bean.getTimestamp());
        msgAllBean.setFrom_uid(bean.getFromUid());

        msgAllBean.setTo_uid(bean.getToUid());
        msgAllBean.setTo_gid(bean.getToGid());

        msgAllBean.setMsg_id(bean.getMsgId());
        msgAllBean.setMsg_type(bean.getMsgType().getNumber());


        switch (bean.getMsgType()){
            case CHAT:
                ChatMessage chat=new ChatMessage();
                chat.setMsg(bean.getChat().getMsg());
                msgAllBean.setChat(chat);
                break;
            case IMAGE:
                ImageMessage image=new ImageMessage();

                image.setUrl(bean.getImage().getUrl());
                msgAllBean.setImage(image);
                break;


        }

        return msgAllBean;


    }
}
