package com.yanlong.im.chat.bean;

import android.util.Log;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.DaoUtil;
import com.yanlong.im.utils.socket.MsgBean;

import net.cb.cb.library.utils.StringUtil;

import io.realm.RealmList;

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
         MsgDao msgDao=  new MsgDao();
        //手动处理转换
        MsgAllBean msgAllBean = new MsgAllBean();

        msgAllBean.setTimestamp(bean.getTimestamp());
        msgAllBean.setFrom_uid(bean.getFromUid());
        msgAllBean.setFrom_avatar(bean.getAvatar());
        msgAllBean.setFrom_nickname(bean.getNickname());
        msgAllBean.setFrom_group_nickname(bean.getMembername());
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
                msgAllBean.setMsg_type(ChatEnum.EMessageType.TEXT);
                break;
            case IMAGE:
                ImageMessage image = new ImageMessage();
                image.setMsgid(msgAllBean.getMsg_id());
                // Log.d("TAG", "查询到本地图msgid"+msgAllBean.getMsg_id());


                MsgAllBean imgMsg = DaoUtil.findOne(MsgAllBean.class, "msg_id", msgAllBean.getMsg_id());


                if (imgMsg != null) {//7.16 替换成上一次本地的图片路径
                    image.setLocalimg(imgMsg.getImage().getLocalimg());
                    Log.d("TAG", "查询到本地图" + image.getLocalimg());

                }

                image.setOrigin(bean.getImage().getOrigin());
                image.setPreview(bean.getImage().getPreview());
                image.setThumbnail(bean.getImage().getThumbnail());
                image.setWidth(bean.getImage().getWidth());
                image.setHeight(bean.getImage().getHeight());
                image.setSize(bean.getImage().getSize());
                msgAllBean.setImage(image);
                msgAllBean.setMsg_type(ChatEnum.EMessageType.IMAGE);
                break;

            case STAMP:// 戳一下消息
                StampMessage stamp = new StampMessage();
                stamp.setMsgid(msgAllBean.getMsg_id());
                stamp.setComment(bean.getStamp().getComment());
                msgAllBean.setStamp(stamp);
                msgAllBean.setMsg_type(ChatEnum.EMessageType.STAMP);
                break;

            case VOICE:// 语音消息
                VoiceMessage voiceMessage = new VoiceMessage();
                voiceMessage.setMsgid(msgAllBean.getMsg_id());
                voiceMessage.setUrl(bean.getVoice().getUrl());
                voiceMessage.setTime(bean.getVoice().getDuration());
                msgAllBean.setVoiceMessage(voiceMessage);
                msgAllBean.setMsg_type(ChatEnum.EMessageType.VOICE);
                break;
            case TRANSFER:
                TransferMessage transferMessage = new TransferMessage();
                transferMessage.setMsgid(msgAllBean.getMsg_id());
                transferMessage.setId(bean.getTransfer().getId());
                transferMessage.setComment(bean.getTransfer().getComment());
                transferMessage.setTransaction_amount(bean.getTransfer().getTransactionAmount());

                msgAllBean.setTransfer(transferMessage);
                msgAllBean.setMsg_type(ChatEnum.EMessageType.TRANSFER);
                break;
            case BUSINESS_CARD:
                BusinessCardMessage businessCard = new BusinessCardMessage();
                businessCard.setMsgid(msgAllBean.getMsg_id());
                businessCard.setAvatar(bean.getBusinessCard().getAvatar());
                businessCard.setComment(bean.getBusinessCard().getComment());
                businessCard.setNickname(bean.getBusinessCard().getNickname());

                businessCard.setUid(bean.getBusinessCard().getUid());
                msgAllBean.setBusiness_card(businessCard);
                msgAllBean.setMsg_type(ChatEnum.EMessageType.BUSINESS_CARD);
                break;
            case RED_ENVELOPER:
                RedEnvelopeMessage envelopeMessage = new RedEnvelopeMessage();
                envelopeMessage.setMsgid(msgAllBean.getMsg_id());
                envelopeMessage.setComment(bean.getRedEnvelope().getComment());
                envelopeMessage.setId(bean.getRedEnvelope().getId());
                envelopeMessage.setRe_type(bean.getRedEnvelope().getReTypeValue());
                envelopeMessage.setStyle(bean.getRedEnvelope().getStyleValue());
                msgAllBean.setRed_envelope(envelopeMessage);
                msgAllBean.setMsg_type(ChatEnum.EMessageType.RED_ENVELOPE);
                break;
            case RECEIVE_RED_ENVELOPER:

                msgAllBean.setMsg_type(ChatEnum.EMessageType.NOTICE);
                MsgNotice rbNotice = new MsgNotice();
                rbNotice.setMsgid(msgAllBean.getMsg_id());
                rbNotice.setNote(bean.getNickname() + "领取红包");
                msgAllBean.setMsgNotice(rbNotice);
                break;

            //需要保存的通知类消息
            case ACCEPT_BE_FRIENDS:// 接收好友请求
                msgAllBean.setMsg_type(ChatEnum.EMessageType.NOTICE);
                MsgNotice msgNotice = new MsgNotice();
                msgNotice.setMsgid(msgAllBean.getMsg_id());
                msgNotice.setNote(bean.getNickname() + "已加你为好友");
                msgAllBean.setMsgNotice(msgNotice);
                break;
            case ACCEPT_BE_GROUP://接受入群请求
                msgAllBean.setMsg_type(ChatEnum.EMessageType.NOTICE);
                MsgNotice gNotice = new MsgNotice();
                gNotice.setMsgid(msgAllBean.getMsg_id());
                String names = "";
                for (int i = 0; i < bean.getAcceptBeGroup().getNoticeMessageCount(); i++) {
                    //7.13 加入替换自己的昵称
                    if (bean.getAcceptBeGroup().getNoticeMessage(i).getUid() == UserAction.getMyId().longValue()) {
                        names += "你、";
                    } else {
                        String name = bean.getAcceptBeGroup().getNoticeMessage(i).getNickname();
                        Long uid = bean.getAcceptBeGroup().getNoticeMessage(i).getUid();

                        MsgAllBean gmsg =msgDao.msgGetLastGroup4Uid(bean.getGid(), uid);
                        if (gmsg != null) {
                            name = StringUtil.isNotNull(gmsg.getFrom_group_nickname()) ? gmsg.getFrom_group_nickname() : name;
                        }

                        UserInfo userinfo = DaoUtil.findOne(UserInfo.class, "uid", uid);
                        if (userinfo != null) {
                            name = StringUtil.isNotNull(userinfo.getMkName()) ? userinfo.getMkName() : name;
                        }


                        names += "\"<font color='#276baa'>"+name + "</font>\"、";
                    }

                }
                names = names.length() > 0 ? names.substring(0, names.length() - 1) : names;


                String inviterName = bean.getAcceptBeGroup().getInviterName();//邀请者名字
                if (bean.getAcceptBeGroup().getInviter() == UserAction.getMyId().longValue()) {
                    inviterName = "你";
                } else {

                    MsgAllBean gmsg =msgDao.msgGetLastGroup4Uid(bean.getGid(), bean.getAcceptBeGroup().getInviter());
                    if (gmsg != null) {
                        inviterName = StringUtil.isNotNull(gmsg.getFrom_group_nickname()) ? gmsg.getFrom_group_nickname() : inviterName;
                    }

                    UserInfo userinfo = DaoUtil.findOne(UserInfo.class, "uid", bean.getAcceptBeGroup().getInviter());//查询昵称
                    if (userinfo != null) {
                        inviterName = StringUtil.isNotNull(userinfo.getMkName()) ? userinfo.getMkName() : inviterName;
                    }

                    inviterName="\"<font color='#276baa'>"+inviterName+ "</font>\"";

                }


                //A邀请B加入群聊
                //B通过扫码A分享的二维码加入群聊


                String node = "";
                if (bean.getAcceptBeGroup().getJoinTypeValue() == 0) {//扫码
                    node = names + "通过扫" + inviterName + "分享的二维码加入了群聊";
                } else {//被邀请
                    node = inviterName + "邀请" + names + "加入了群聊";
                }

                // String way=bean.getAcceptBeGroup().getJoinTypeValue()==0?"通过xxx扫码":"通过xxx";
                gNotice.setNote(node);
                msgAllBean.setMsgNotice(gNotice);
                break;
            case DESTROY_GROUP://群解散
                msgAllBean.setGid(bean.getGid());
                msgAllBean.setMsg_type(ChatEnum.EMessageType.NOTICE);
                MsgNotice gdelNotice = new MsgNotice();
                gdelNotice.setMsgid(msgAllBean.getMsg_id());
                gdelNotice.setNote("该群已解散");
                msgAllBean.setMsgNotice(gdelNotice);


                break;
            case REMOVE_GROUP_MEMBER:
                //  ToastUtil.show(getApplicationContext(), "删除群成员");
                msgAllBean.setGid(bean.getRemoveGroupMember().getGid());
                msgAllBean.setMsg_type(ChatEnum.EMessageType.NOTICE);
                MsgNotice grmvNotice = new MsgNotice();

                grmvNotice.setMsgid(msgAllBean.getMsg_id());
                grmvNotice.setNote("您已移除群");
                msgAllBean.setMsgNotice(grmvNotice);
                break;
            case CHANGE_GROUP_MASTER:
                msgAllBean.setGid(bean.getGid());
                msgAllBean.setMsg_type(ChatEnum.EMessageType.NOTICE);
                MsgNotice gnewAdminNotice = new MsgNotice();
                gnewAdminNotice.setMsgid(msgAllBean.getMsg_id());
                if (bean.getChangeGroupMaster().getUid() == UserAction.getMyId().longValue()) {
                    gnewAdminNotice.setNote("你已成为新的群主");
                } else {
                    gnewAdminNotice.setNote(bean.getChangeGroupMaster().getMembername() + "已成为新的群主");
                }

                msgAllBean.setMsgNotice(gnewAdminNotice);
                break;
            case OUT_GROUP://退出群

                msgAllBean.setGid(bean.getOutGroup().getGid());

                msgAllBean.setMsg_type(ChatEnum.EMessageType.NOTICE);
                MsgNotice goutNotice = new MsgNotice();
                goutNotice.setMsgid(msgAllBean.getMsg_id());
                goutNotice.setNote(bean.getNickname() + "退出该群");
                msgAllBean.setMsgNotice(goutNotice);
                break;
            case CHANGE_GROUP_NAME:
                msgAllBean.setMsg_type(ChatEnum.EMessageType.NOTICE);
                MsgNotice info = new MsgNotice();
                info.setMsgid(msgAllBean.getMsg_id());
                info.setNote("新群名称:" + bean.getChangeGroupName().getName());
                msgAllBean.setMsgNotice(info);
                break;
            case CHANGE_GROUP_ANNOUNCEMENT:
                msgAllBean.setMsg_type(ChatEnum.EMessageType.NOTICE);
                MsgNotice ani = new MsgNotice();
                ani.setMsgid(msgAllBean.getMsg_id());
                ani.setNote("群公告:" + bean.getChangeGroupAnnouncement().getAnnouncement());
                msgAllBean.setMsgNotice(ani);
                break;
            case AT:
                RealmList<Long> realmList = new RealmList<>();
                realmList.addAll(bean.getAt().getUidList());
                msgAllBean.setMsg_type(ChatEnum.EMessageType.AT);
                AtMessage atMessage = new AtMessage();
                atMessage.setMsg(bean.getAt().getMsg());
                atMessage.setAt_type(bean.getAt().getAtType().getNumber());
                atMessage.setUid(realmList);
                msgAllBean.setAtMessage(atMessage);
                break;
            case ASSISTANT:
                AssistantMessage assistant = new AssistantMessage();
                assistant.setMsg(bean.getAssistant().getMsg());
                msgAllBean.setMsg_type(ChatEnum.EMessageType.ASSISTANT);
                msgAllBean.setAssistantMessage(assistant);
                break;
            case CANCEL://撤回消息

                String rname="";
                if (bean.getFromUid() == UserAction.getMyId().longValue()) {
                    rname="你";
                }else{//对方撤回的消息当通知处理
                    rname="\""+msgDao.getUsername4Show(bean.getGid(),bean.getFromUid())+"\"";
                    //return null;
                }
                msgAllBean.setMsg_type(ChatEnum.EMessageType.MSG_CENCAL);
                MsgCancel msgCel = new MsgCancel();
                msgCel.setMsgid(msgAllBean.getMsg_id());
                msgCel.setNote(rname+"撤回了一条消息");
                msgCel.setMsgidCancel(bean.getCancel().getMsgId());
                msgAllBean.setMsgCancel(msgCel);



                break;
            default:
                return null;
        }

        return msgAllBean;


    }
}
