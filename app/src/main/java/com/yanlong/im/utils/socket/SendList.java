package com.yanlong.im.utils.socket;

import net.cb.cb.library.utils.LogUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 发送队列
 */
public class SendList {
    private static final String TAG = "SendList";
    //重发次数
    private static int SEND_MAX_NUM = 3;
    //重发时长
    private static long SEND_RE_TIME = 3 * 1000;

    public static Map<String, SendListBean> SEND_LIST = new ConcurrentHashMap<>();

public static MsgBean.UniversalMessage.Builder findMsgById(String keyId){
    if(SEND_LIST.containsKey(keyId)){
        return SEND_LIST.get(keyId).getMsg();
    }
    return null;
}
    /***
     * 添加到列队中监听
     * @param keyId
     * @param msg
     */
    public static void addSendList(String keyId, MsgBean.UniversalMessage.Builder msg) {
        if (SEND_LIST.containsKey(keyId)) {//已经在发送队列中了
            SendListBean sl = SEND_LIST.get(keyId);
            sl.setReSendNum(sl.getReSendNum() + 1);
            LogUtil.getLog().d(TAG, ">>>" + sl.getReSendNum() + "次重发队列" + keyId);
        } else {//首次
            LogUtil.getLog().d(TAG, ">>>添加到发送队列" + keyId);
            SendListBean sl = new SendListBean();
            sl.setFirstTimeSent(System.currentTimeMillis());
            sl.setMsg(msg);
            sl.setReSendNum(0);
            SEND_LIST.put(keyId, sl);
        }

    }



    /***
     * 移除列队,返回发送失败
     * @param keyId
     */
    public static void removeSendList(String keyId){
        if(!SEND_LIST.containsKey(keyId))
            return;

        SocketUtil.getSocketUtil().getEvent().onSendMsgFailure(SEND_LIST.get(keyId).getMsg());
        SEND_LIST.remove(keyId);
    }

    /***
     * 仅移除消息列队
     * @param keyId
     */
    public static void removeSendListJust(String keyId){
        if(!SEND_LIST.containsKey(keyId))
            return;
        SEND_LIST.remove(keyId);
    }


    /***
     * 循环队列
     */
    public static void loopList() {
        Iterator<Map.Entry<String, SendListBean>> entrys = SEND_LIST.entrySet().iterator();
        long now = System.currentTimeMillis();
        while (entrys.hasNext()) {
            Map.Entry<String, SendListBean> entry = entrys.next();
            String kid=entry.getKey();
            SendListBean bean = entry.getValue();

            if(bean.getReSendNum()<SEND_MAX_NUM){ //在正常发送范围之内
                if (now>(bean.getFirstTimeSent()+bean.getReSendNum()*SEND_RE_TIME)){
                    LogUtil.getLog().e(TAG,">>>>符合重发条件"+kid);
                    SocketUtil.getSocketUtil().sendData4Msg(bean.getMsg());
                }
            }else{//超过发送次数,取消队列,返回失败
                LogUtil.getLog().e(TAG,">>>>发送条件次数不符合"+kid);
                removeSendList(kid);



            }




        }


    }


}
