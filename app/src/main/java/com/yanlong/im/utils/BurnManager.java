package com.yanlong.im.utils;

import android.os.CountDownTimer;
import android.text.TextUtils;

import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.utils.LogUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @创建人 shenxin
 * @创建时间 2019/10/21 0021 10:51
 * 阅后即焚管理类
 */
public class BurnManager {
    private static BurnManager INSTANCE;
    private CountDownTimer timer;
    private List<MsgAllBean> msgAllBeans = new ArrayList<>();
    private List<String> historyMsgIds = new ArrayList<>();
    private MsgDao msgDao = new MsgDao();
    //需要刷新session的群id
    private List<String> gids = new ArrayList<>();
    //需要刷新session的uid
    private List<Long> uids = new ArrayList<>();
    //是否需要刷新chat。即当前chat在前台
    private boolean needRefreshChat = false;
    boolean isDelete = false;//是否正在删除

    public static BurnManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BurnManager();
        }
        return INSTANCE;
    }


    //这是倒计时执行方法
    public void RunTimer() {
        timer = new CountDownTimer(24 * 60 * 60 * 1000, 1000) {
            @Override
            public void onFinish() {
                cancel();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                delete();
            }
        }.start();
    }

//    private synchronized void delete() {
////        LogUtil.getLog().i("SurvivalTime", "delete" + "--time=" + System.currentTimeMillis());
//        Iterator<MsgAllBean> it = msgAllBeans.iterator();
//        LogUtil.getLog().i("SurvivalTime", "delete" + "--time=" + System.currentTimeMillis() + "--size=" + msgAllBeans.size());
//        while (it.hasNext()) {
//            MsgAllBean bean = it.next();
//            if (bean.getEndTime() <= DateUtils.getSystemTime()) {
////                LogUtil.getLog().d("SurvivalTime", "结束时间:" + bean.getEndTime() + "---------" + "系统时间" + DateUtils.getSystemTime());
//                long time = System.currentTimeMillis();
//                LogUtil.getLog().i("SurvivalTime", "删除msg前:" + bean.getMsg_id() + "--time=" + time);
//                msgDao.msgDel4MsgId(bean.getMsg_id());
////                LogUtil.getLog().i("SurvivalTime", "删除msg后:" + bean.getMsg_id() + "耗时==" + (System.currentTimeMillis() - time));
//                updateSession(bean);
//                it.remove();
//                MessageManager.getInstance().notifyRefreshChat(bean, CoreEnum.ERefreshType.DELETE);
//            } else if (bean.getSurvival_time() == -1) {
//                LogUtil.getLog().i("SurvivalTime", "退出即焚删除msg:" + bean.getMsg_id());
//                msgDao.msgDel4MsgId(bean.getMsg_id());
//                updateSession(bean);
//                it.remove();
//            }
//            LogUtil.getLog().i("SurvivalTime", "不符合销毁条件:" + msgAllBeans.size());
//        }
//    }

    private synchronized void delete() {
        needRefreshChat = false;
        if (msgAllBeans != null && msgAllBeans.size() > 0) {
//            LogUtil.getLog().i("SurvivalTime", "消息size:" + msgAllBeans.size());
            List<MsgAllBean> tempList = new ArrayList<>();
            Iterator<MsgAllBean> it = msgAllBeans.iterator();
            while (it.hasNext()) {
                MsgAllBean bean = it.next();
                //可能会有批量加入的，未录入msgID的
                addHistoryId(historyMsgIds, bean.getMsg_id());
                if (bean.getEndTime() <= DateUtils.getSystemTime()) {
                    LogUtil.getLog().i("SurvivalTime", "阅后即焚msg:" + bean.getMsg_id());
                    addTemp(tempList, bean);
                    addSession(bean);
                    it.remove();
                    needRefreshChat = true;
                } else if (bean.getSurvival_time() == -1) {
                    LogUtil.getLog().i("SurvivalTime", "退出即焚删除msg:" + bean.getMsg_id());
                    addTemp(tempList, bean);
                    addSession(bean);
                    it.remove();
                }
            }
            if (tempList.size() > 0) {
                boolean result = msgDao.deleteMsgList(tempList);
                LogUtil.getLog().i("SurvivalTime", "批量删除size=" + tempList.size());

                if (result) {
                    if (needRefreshChat) {
                        MessageManager.getInstance().notifyRefreshChat(tempList, CoreEnum.ERefreshType.DELETE);
                    }
                    MessageManager.getInstance().notifyRefreshMsg(CoreEnum.EChatType.PRIVATE, 0L, "", CoreEnum.ESessionRefreshTag.ALL, null);
                    int gLen = gids.size();
                    int uLen = uids.size();
//                    if (gLen + uLen == 1) {
//                        LogUtil.getLog().i("SurvivalTime", "刷新单个会话:" + gLen + "--" + uLen);
//                        if (gLen > 0) {
//                            MessageManager.getInstance().notifyRefreshMsg(CoreEnum.EChatType.GROUP, -1L, gids.get(0), CoreEnum.ESessionRefreshTag.ALL, null);
//                        } else if (uLen > 0) {
//                            MessageManager.getInstance().notifyRefreshMsg(CoreEnum.EChatType.PRIVATE, uids.get(0), "", CoreEnum.ESessionRefreshTag.SINGLE, null);
//                        }
//
//                    } else if (gLen + uLen > 0) {
//                        LogUtil.getLog().i("SurvivalTime", "刷新所有会话:" + gLen + "--" + uLen);
//                        MessageManager.getInstance().notifyRefreshMsg();
//                    }

                    gids.clear();
                    uids.clear();
                }
                tempList.clear();
            }
        }

    }

    private void addTemp(List<MsgAllBean> tempList, MsgAllBean bean) {
        if (!tempList.contains(bean)) {
//            LogUtil.getLog().i("SurvivalTime", "add-temp:" + bean.getMsg_id());
            tempList.add(bean);
        }
    }

    private void addHistoryId(List<String> historyMsgIds, String msg_id) {
        if (!historyMsgIds.contains(msg_id)) {
            historyMsgIds.add(msg_id);
        }
    }


    //更新会话列表消息
    private void updateSession(MsgAllBean msgAllBean) {
        String gid = msgAllBean.getGid();
        if (TextUtils.isEmpty(gid)) {
            Long uid = msgAllBean.isMe() ? msgAllBean.getTo_uid() : msgAllBean.getFrom_uid();
//            MsgAllBean uidMsgAllBean = msgDao.msgGetLast4FUid(uid);
            MessageManager.getInstance().notifyRefreshMsg(CoreEnum.EChatType.PRIVATE, uid, gid, CoreEnum.ESessionRefreshTag.SINGLE, null);
        } else {
//            MsgAllBean gidMsgAllBean = msgDao.msgGetLast4Gid(gid);
            Long uid = msgAllBean.isMe() ? msgAllBean.getTo_uid() : msgAllBean.getTo_uid();
            MessageManager.getInstance().notifyRefreshMsg(CoreEnum.EChatType.GROUP, uid, gid, CoreEnum.ESessionRefreshTag.SINGLE, null);
        }
    }

    //更新会话列表消息
    private void addSession(MsgAllBean msgAllBean) {
        String gid = msgAllBean.getGid();
        if (TextUtils.isEmpty(gid)) {
            Long uid = msgAllBean.isMe() ? msgAllBean.getTo_uid() : msgAllBean.getFrom_uid();
            if (uid != null && !uids.contains(uid)) {
                uids.add(uid);
            }
        } else {
            if (!gids.contains(gid)) {
                gids.add(gid);
            }
        }
    }


    public synchronized void addMsgAllBean(MsgAllBean msgAllBean) {
        if (!historyMsgIds.contains(msgAllBean.getMsg_id())) {
//            LogUtil.getLog().i("SurvivalTime", "addMsgAllBean--status==" + msgAllBean.getSend_state());
            msgAllBeans.add(msgAllBean);
            if (historyMsgIds.size() >= 500) {
                historyMsgIds.remove(0);
            }
            historyMsgIds.add(msgAllBean.getMsg_id());
        }
    }

    //添加阅后即焚消息进入队列
    public synchronized void addMsgAllBeans(List<MsgAllBean> msgs) {
//        LogUtil.getLog().i("SurvivalTime", "addMsgAllBeans:" + msgs.size());
        msgAllBeans.addAll(msgs);
    }

    public boolean isContainMsg(MsgAllBean bean) {
        boolean isContainMsg=historyMsgIds.contains(bean.getMsg_id());
        //已加入队列的，进行最初赋值
        if(isContainMsg&&(bean.getStartTime()==0||bean.getEndTime()==0)){
            int index=msgAllBeans.indexOf(bean);
            if(index>=0){
                MsgAllBean historyBean=msgAllBeans.get(index);
                bean.setStartTime(historyBean.getStartTime());
                bean.setEndTime(historyBean.getEndTime());
            }
        }
        return isContainMsg;
    }

    /**
     * 用户退出登录需清除阅后即焚数据
     */
    public void clear(){
        msgAllBeans.clear();
        historyMsgIds.clear();
        gids.clear();
        uids.clear();
        cancel();
    }
    public void cancel() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}