package com.yanlong.im.chat.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.socket.MsgBean;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @anthor Liszt
 * @data 2019/10/11
 * Description 批量处理接收到的消息，针对收到单聊，或者群聊消息，而本地无用户或者群数据，需要异步请求用户或者群数据的情况。
 * 批量消息必须所有消息处理完毕，才能通知刷新session和未读数
 * 风险：当请求用户数据和群数据失败的时候，可能导致任务无法正常处理完
 */
public class TaskDealWithMsgList extends AsyncTask<Void, Integer, Boolean> {
    private final String TAG = TaskDealWithMsgList.class.getSimpleName();
    private MsgDao msgDao = new MsgDao();
    List<MsgBean.UniversalMessage.WrapMessage> messages;
    List<String> gids = new ArrayList<>();//批量消息接受到群聊id
    List<Long> uids = new ArrayList<>();//批量消息接收到单聊uid
    private int taskCount = 0;//任务总数
    Map<String, MsgAllBean> totalMsgList = new HashMap<>();

    public TaskDealWithMsgList(List<MsgBean.UniversalMessage.WrapMessage> wrapMessageList) {
        messages = wrapMessageList;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (messages != null) {
            int length = messages.size();
            taskCount = length;
//            LogUtil.getLog().d("a=", TaskDealWithMsgList.class.getSimpleName() + "--总任务数=" + taskCount + "--当前时间-1=" + System.currentTimeMillis());
            for (int i = 0; i < length; i++) {
                MsgBean.UniversalMessage.WrapMessage wrapMessage = messages.get(i);
//                LogUtil.getLog().d("a=", TaskDealWithMsgList.class.getSimpleName() + "--gid=" + wrapMessage.getGid() + "--uid=" + wrapMessage.getFromUid() + "--msgId=" + wrapMessage.getMsgId());
                boolean result = MessageManager.getInstance().dealWithMsg(wrapMessage, true, i == length - 1);//最后一条消息，发出通知声音
                if (result) {
                    taskCount--;
//                    LogUtil.getLog().d("a=", TaskDealWithMsgList.class.getSimpleName() + "--更新一次任务数 taskCount=" + taskCount + "--msgId=" + wrapMessage.getMsgId());
                } else {
//                    LogUtil.getLog().d("a=", TaskDealWithMsgList.class.getSimpleName() + "--更新任务数失败" + "--msgId=" + wrapMessage.getMsgId());
                }
            }
//            LogUtil.getLog().d("a=", TaskDealWithMsgList.class.getSimpleName() + "--总任务数=" + taskCount + "--当前时间-2=" + System.currentTimeMillis());
        }
        if (taskCount == 0) {
//            LogUtil.getLog().d("a=", TaskDealWithMsgList.class.getSimpleName() + "--任务批量处理完成YES");
            return true;
        } else {
//            LogUtil.getLog().d("a=", TaskDealWithMsgList.class.getSimpleName() + "--任务批量处理未完成NO");
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
//            LogUtil.getLog().d("a=", TaskDealWithMsgList.class.getSimpleName() + "--任务批量处理pending完毕onPostExecute" + "--当前时间=" + System.currentTimeMillis());
            doPendingData();
            notifyUIRefresh();
            LogUtil.getLog().d("a=", TaskDealWithMsgList.class.getSimpleName() + "--任务批量更新完毕onPostExecute，刷新页面=" + System.currentTimeMillis());
        }
    }

    private void notifyUIRefresh() {
        MessageManager.getInstance().setMessageChange(true);
        if (checkIsFromSingle()) {
            if (gids.size() > 0) {
                MessageManager.getInstance().notifyRefreshMsg(CoreEnum.EChatType.GROUP, null, gids.get(0), CoreEnum.ESessionRefreshTag.SINGLE, null);
            } else {
                MessageManager.getInstance().notifyRefreshMsg(CoreEnum.EChatType.PRIVATE, uids.get(0), "", CoreEnum.ESessionRefreshTag.SINGLE, null);
            }

        } else {
            MessageManager.getInstance().notifyRefreshMsg();
        }
        MessageManager.getInstance().notifyRefreshChat();

        clearIds();
    }

    /*
     * 更新任务数
     * 应用场景：异步加载用户数据或者群数据成功后
     * */
    public void updateTaskCount() {
        taskCount--;
//        LogUtil.getLog().d("a=", TaskDealWithMsgList.class.getSimpleName() + "--异步更新一次任务数 taskCount=" + taskCount);
        if (taskCount == 0) {
//            LogUtil.getLog().d("a=", TaskDealWithMsgList.class.getSimpleName() + "--任务批量处理pending完毕updateTaskCount" + "--当前时间=" + System.currentTimeMillis());
            doPendingData();
            notifyUIRefresh();
            LogUtil.getLog().d("a=", TaskDealWithMsgList.class.getSimpleName() + "--任务批量更新完毕updateTaskCount，刷新页面=" + System.currentTimeMillis());
        }
    }

    public void addUid(Long uid) {
        if (uid != null && !uids.contains(uid)) {
            uids.add(uid);
        }
    }

    public void addGid(String gid) {
        if (!TextUtils.isEmpty(gid) && !gids.contains(gid)) {
            gids.add(gid);
        }
    }

    public boolean checkIsFromSingle() {
        int len1 = uids.size();
        int len2 = gids.size();
        if (len1 + len2 == 1) {
            return true;
        } else {
            return false;
        }
    }

    //批量更新后清除数据
    private void clearIds() {
        uids.clear();
        gids.clear();
    }

    private void addMsg(MsgAllBean bean) {
        totalMsgList.put(bean.getMsg_id(), bean);
    }

    private void cancelMsg(String msgId) {
//        totalMsgList.put(bean.getMsg_id(), bean);
        MsgAllBean bean = totalMsgList.get(msgId);

    }

    private void clearMsgList() {
        totalMsgList.clear();
    }

    private void doPendingData() {
        Map<Long, Integer> mapUSession = MessageManager.getInstance().getPendingUserUnreadMap();
        if (mapUSession != null && mapUSession.size() > 0) {
            for (Map.Entry<Long, Integer> entry : mapUSession.entrySet()) {
                MessageManager.getInstance().updateSessionUnread("", entry.getKey(), entry.getValue());
            }
        }

        Map<String, Integer> mapGSession = MessageManager.getInstance().getPendingGroupUnreadMap();
        if (mapGSession != null && mapGSession.size() > 0) {
            for (Map.Entry<String, Integer> entry : mapGSession.entrySet()) {
                MessageManager.getInstance().updateSessionUnread(entry.getKey(), -1L, entry.getValue());
            }
        }

        List<UserInfo> userInfos = MessageManager.getInstance().getPendingUserList();
        if (userInfos != null) {
            int len = userInfos.size();
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    UserInfo info = userInfos.get(i);
                    MessageManager.getInstance().updateUserAvatarAndNick(info.getUid(), info.getHead(), info.getName());
                }
            }
        }

        List<MsgAllBean> msgList = MessageManager.getInstance().getPendingMsgList();
        if (msgList != null) {
            msgDao.insertOrUpdateMsgList(msgList);
        }
        Map<String, MsgAllBean> mapCancel = MessageManager.getInstance().getPendingCancelMap();
        if (mapCancel != null && mapCancel.size() > 0) {
            for (Map.Entry<String, MsgAllBean> entry : mapCancel.entrySet()) {
                MsgAllBean bean = entry.getValue();
                System.out.println(TAG + "--" + bean.getMsg_id() + "-- cancelId=" + bean.getMsgCancel().getMsgidCancel());
                msgDao.msgDel4Cancel(bean.getMsg_id(), bean.getMsgCancel().getMsgidCancel());
            }
        }
        MessageManager.getInstance().clearPendingList();
    }
}
