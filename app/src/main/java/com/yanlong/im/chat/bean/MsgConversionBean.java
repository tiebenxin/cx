package com.yanlong.im.chat.bean;

import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;

/***
 * 消息转换类
 */
public class MsgConversionBean {


    /***
     * 把pd转成greendao需要的bean
     * @param bean
     * @return
     */
    public static MsgAllBean ToBean(MsgBean.UniversalMessage.WrapMessage bean) {
        return ToBean(bean, null);
    }

    public static MsgAllBean ToBean(MsgBean.UniversalMessage.WrapMessage bean, MsgBean.UniversalMessage.Builder msg) {

        //手动处理转换
        MsgAllBean msgAllBean = new MsgAllBean();

        msgAllBean.setTimestamp(bean.getTimestamp());
         msgAllBean.setFrom_uid(bean.getFromUid());
        msgAllBean.setFrom_avatar(bean.getAvatar());
        msgAllBean.setFrom_nickname(bean.getNickname());
        msgAllBean.setGid(bean.getGid());
        if (msg != null) {
            msgAllBean.setRequest_id(msg.getRequestId());
            msgAllBean.setTo_uid(msg.getToUid());
            UserInfo toInfo = DaoUtil.findOne(UserInfo.class, "uid", msg.getToUid());
            if (toInfo != null) {//更新用户信息
                //  msgAllBean.setTo_user(toInfo);
            } else {
                //从网路缓存
            }
        }

        //这里需要处理用户信息
        UserInfo userInfo = DaoUtil.findOne(UserInfo.class, "uid", bean.getFromUid());
        if (userInfo != null) {//更新用户信息
            //   msgAllBean.setFrom_user(userInfo);
        } else {
            //从网路缓存
            bean.getAvatar();
            bean.getNickname();
            //   bean.


        }

        //---------------------


        msgAllBean.setMsg_id(bean.getMsgId());


        switch (bean.getMsgType()) {
            case CHAT:
                ChatMessage chat = new ChatMessage();
                chat.setMsgid(msgAllBean.getMsg_id());
                chat.setMsg(bean.getChat().getMsg());
                msgAllBean.setChat(chat);
                msgAllBean.setMsg_type(1);
                break;
            case IMAGE:
                ImageMessage image = new ImageMessage();
                image.setMsgid(msgAllBean.getMsg_id());
                image.setUrl(bean.getImage().getUrl());
                msgAllBean.setImage(image);
                msgAllBean.setMsg_type(4);
                break;

            case STAMP:// 戳一下消息
                StampMessage stamp = new StampMessage();
                stamp.setMsgid(msgAllBean.getMsg_id());
                stamp.setComment(bean.getStamp().getComment());
                msgAllBean.setStamp(stamp);
                msgAllBean.setMsg_type(2);
                break;
            case TRANSFER:
                TransferMessage transferMessage=new TransferMessage();
                transferMessage.setMsgid(msgAllBean.getMsg_id());
                transferMessage.setId(bean.getTransfer().getId());
                transferMessage.setComment(bean.getTransfer().getComment());
               // transferMessage.setTransaction_amount(""+bean.getTransfer().getTransactionAmount());
                break;
            case BUSINESS_CARD:
                BusinessCardMessage businessCard = new BusinessCardMessage();
                businessCard.setMsgid(msgAllBean.getMsg_id());
                businessCard.setAvatar(bean.getBusinessCard().getAvatar());
                businessCard.setComment(bean.getBusinessCard().getComment());
                businessCard.setNickname(bean.getBusinessCard().getNickname());

                businessCard.setUid(bean.getBusinessCard().getUid());
                msgAllBean.setBusiness_card(businessCard);
                msgAllBean.setMsg_type(5);
                break;
            case RED_ENVELOPER:
                RedEnvelopeMessage envelopeMessage = new RedEnvelopeMessage();
                envelopeMessage.setMsgid(msgAllBean.getMsg_id());
                envelopeMessage.setComment(bean.getRedEnvelope().getComment());
                envelopeMessage.setId(bean.getRedEnvelope().getId());
                envelopeMessage.setRe_type(bean.getRedEnvelope().getReTypeValue());
                envelopeMessage.setStyle(bean.getRedEnvelope().getStyleValue());
                msgAllBean.setRed_envelope(envelopeMessage);
                msgAllBean.setMsg_type(3);
                break;
            case RECEIVE_RED_ENVELOPER:

                msgAllBean.setMsg_type(0);
                MsgNotice rbNotice=new MsgNotice();
                rbNotice.setMsgid(msgAllBean.getMsg_id());
                rbNotice.setNote(bean.getNickname()+"领取红包");
                msgAllBean.setMsgNotice(rbNotice);
                break;

                //需要保存的通知类消息
            case ACCEPT_BE_FRIENDS:// 接收好友请求
                msgAllBean.setMsg_type(0);
                MsgNotice msgNotice=new MsgNotice();
                msgNotice.setMsgid(msgAllBean.getMsg_id());
                msgNotice.setNote(bean.getNickname()+"已加你为好友");
                msgAllBean.setMsgNotice(msgNotice);
                break;
            case ACCEPT_BE_GROUP://接受入群请求
                msgAllBean.setMsg_type(0);
                MsgNotice gNotice=new MsgNotice();
                gNotice.setMsgid(msgAllBean.getMsg_id());
                String names="";
                for (int i=0;i<bean.getAcceptBeGroup().getNoticeMessageCount();i++){
                    names+=bean.getAcceptBeGroup().getNoticeMessage(i).getNickname()+",";
                }
                names=names.length()>0?names.substring(0,names.length()-1):names;
                gNotice.setNote(names+"已加入群");
                msgAllBean.setMsgNotice(gNotice);
                    break;
            case DESTROY_GROUP://群解散
                msgAllBean.setGid(bean.getGid());
                msgAllBean.setMsg_type(0);
                MsgNotice gdelNotice=new MsgNotice();
                gdelNotice.setMsgid(msgAllBean.getMsg_id());
                gdelNotice.setNote("该群已解散");
                msgAllBean.setMsgNotice(gdelNotice);


                break;
            case REMOVE_GROUP_MEMBER:
                //  ToastUtil.show(getApplicationContext(), "删除群成员");
                msgAllBean.setGid(bean.getRemoveGroupMember().getGid());
                msgAllBean.setMsg_type(0);
                MsgNotice grmvNotice=new MsgNotice();

                grmvNotice.setMsgid(msgAllBean.getMsg_id());
                grmvNotice.setNote("您已移除群");
                msgAllBean.setMsgNotice(grmvNotice);
                break;
            case CHANGE_GROUP_MASTER:
                msgAllBean.setGid(bean.getGid());
                msgAllBean.setMsg_type(0);
                MsgNotice gnewAdminNotice=new MsgNotice();
                gnewAdminNotice.setMsgid(msgAllBean.getMsg_id());
                gnewAdminNotice.setNote("该群已转让");//+bean.getChangeGroupMaster().getUid()
                msgAllBean.setMsgNotice(gnewAdminNotice);
                break;
            case OUT_GROUP://退出群

                msgAllBean.setGid(bean.getOutGroup().getGid());

                msgAllBean.setMsg_type(0);
                MsgNotice goutNotice=new MsgNotice();
                goutNotice.setMsgid(msgAllBean.getMsg_id());
                goutNotice.setNote(bean.getNickname()+"退出该群");
                msgAllBean.setMsgNotice(goutNotice);
                break;
            case CHANGE_GROUP_NAME:
                msgAllBean.setMsg_type(0);
                MsgNotice info=new MsgNotice();
                info.setMsgid(msgAllBean.getMsg_id());
                info.setNote("新群名称:"+bean.getChangeGroupName().getName());
                msgAllBean.setMsgNotice(info);
                break;
            case CHANGE_GROUP_ANNOUNCEMENT  :
                msgAllBean.setMsg_type(0);
                MsgNotice ani=new MsgNotice();
                ani.setMsgid(msgAllBean.getMsg_id());
                ani.setNote("群公告:"+bean.getChangeGroupAnnouncement().getAnnouncement());
                msgAllBean.setMsgNotice(ani);
                break;

            default:
                return null;


        }

        return msgAllBean;


    }
}
