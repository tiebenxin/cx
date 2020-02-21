package com.yanlong.im.utils;

import android.os.CountDownTimer;
import android.text.TextUtils;

import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.eventbus.EventRefreshMainMsg;
import com.yanlong.im.chat.manager.MessageManager;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.EventRefreshChat;
import net.cb.cb.library.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @创建人 shenxin
 * @创建时间 2019/10/21 0021 10:51
 */
public class TimeUtils {
    private CountDownTimer timer;
    private List<MsgAllBean> msgAllBeans = new ArrayList<>();
    private MsgDao msgDao = new MsgDao();


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

    private synchronized void delete() {
//        LogUtil.getLog().i("SurvivalTime", "delete" + "--time=" + System.currentTimeMillis());
        Iterator<MsgAllBean> it = msgAllBeans.iterator();
        LogUtil.getLog().i("SurvivalTime", "delete" + "--time=" + System.currentTimeMillis() + "--size=" + msgAllBeans.size());
        while (it.hasNext()) {
            MsgAllBean bean = it.next();
            if (bean.getEndTime() <= DateUtils.getSystemTime()) {
//                LogUtil.getLog().d("SurvivalTime", "结束时间:" + bean.getEndTime() + "---------" + "系统时间" + DateUtils.getSystemTime());
                long time = System.currentTimeMillis();
                LogUtil.getLog().i("SurvivalTime", "删除msg前:" + bean.getMsg_id() + "--time=" + time);
                msgDao.msgDel4MsgId(bean.getMsg_id());
//                LogUtil.getLog().i("SurvivalTime", "删除msg后:" + bean.getMsg_id() + "耗时==" + (System.currentTimeMillis() - time));
                updateSession(bean);
                it.remove();
                MessageManager.getInstance().notifyRefreshChat(bean, CoreEnum.ERefreshType.DELETE);
            } else if (bean.getSurvival_time() == -1) {
                LogUtil.getLog().i("SurvivalTime", "退出即焚删除msg:" + bean.getMsg_id());
                msgDao.msgDel4MsgId(bean.getMsg_id());
                updateSession(bean);
                it.remove();
            }
        }
    }

//    private synchronized void delete() {
//        if (msgAllBeans != null && msgAllBeans.size() > 0) {
//            List<MsgAllBean> tempList = msgAllBeans;
//            msgAllBeans.clear();
//            boolean result = msgDao.deleteMsgList(tempList);
//            if (result) {
//                MessageManager.getInstance().notifyRefreshChat(tempList, CoreEnum.ERefreshType.DELETE);
//                updateSession(bean);
//            }
//        }
//        Iterator<MsgAllBean> it = msgAllBeans.iterator();
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
//        }
//    }


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


    public synchronized void addMsgAllBean(MsgAllBean msgAllBean) {
        if (!msgAllBeans.contains(msgAllBean)) {
            LogUtil.getLog().i("SurvivalTime", "addMsgAllBean");
            msgAllBeans.add(msgAllBean);
        }
    }

    //添加阅后即焚消息进入队列
    public synchronized void addMsgAllBeans(List<MsgAllBean> msgs) {
        LogUtil.getLog().i("SurvivalTime", "addMsgAllBeans:" + msgs.size());
        msgAllBeans.addAll(msgs);
    }


    public void cancel() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}