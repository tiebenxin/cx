package com.yanlong.im.chat.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.utils.socket.MsgBean;

import net.cb.cb.library.CoreEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @anthor Liszt
 * @data 2019/10/11
 * Description 批量处理接收到的消息，针对收到单聊，或者群聊消息，而本地无用户或者群数据，需要异步请求用户或者群数据的情况。
 * 批量消息必须所有消息处理完毕，才能通知刷新session和未读数
 * 风险：当请求用户数据和群数据失败的时候，可能导致任务无法正常处理完
 */
public class TaskDealWithMsgList extends AsyncTask<Void, Integer, Boolean> {
    List<MsgBean.UniversalMessage.WrapMessage> messages;
    List<String> gids = new ArrayList<>();//批量消息接受到群聊id
    List<Long> uids = new ArrayList<>();//批量消息接收到单聊uid
    private int taskCount = 0;//任务总数

    public TaskDealWithMsgList(List<MsgBean.UniversalMessage.WrapMessage> wrapMessageList) {
        messages = wrapMessageList;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (messages != null) {
            int length = messages.size();
            taskCount = length;
            System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--总任务数=" + taskCount);
            for (int i = 0; i < length; i++) {
                MsgBean.UniversalMessage.WrapMessage wrapMessage = messages.get(i);
                System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--gid=" + wrapMessage.getGid() + "--uid=" + wrapMessage.getFromUid() + "--msgId=" + wrapMessage.getMsgId());
                boolean result = MessageManager.getInstance().dealWithMsg(wrapMessage, true, i == length - 1);//最后一条消息，发出通知声音
                if (result) {
                    taskCount--;
//                    System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--更新一次任务数 taskCount=" + taskCount + "--msgId=" + wrapMessage.getMsgId());
                } else {
//                    System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--更新任务数失败" + "--msgId=" + wrapMessage.getMsgId());
                }
            }
        }
        if (taskCount == 0) {
//            System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--任务批量处理完成YES");
            return true;
        } else {
//            System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--任务批量处理未完成NO");
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            MessageManager.getInstance().setMessageChange(true);
            if (checkIsFromSingle()) {
//                System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--任务批量更新完毕，刷新页面,单个刷新");
                if (gids.size() > 0) {
                    MessageManager.getInstance().notifyRefreshMsg(CoreEnum.EChatType.GROUP, null, gids.get(0), CoreEnum.ESessionRefreshTag.SINGLE, null);
                } else {
                    MessageManager.getInstance().notifyRefreshMsg(CoreEnum.EChatType.PRIVATE, uids.get(0), "", CoreEnum.ESessionRefreshTag.SINGLE, null);
                }
            } else {
//                System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--任务批量更新完毕，刷新页面,整体刷新");
                MessageManager.getInstance().notifyRefreshMsg();
            }
            clearIds();
        }
    }

    /*
     * 更新任务数
     * 应用场景：异步加载用户数据或者群数据成功后
     * */
    public void updateTaskCount() {
        taskCount--;
//        System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--异步更新一次任务数 taskCount=" + taskCount);
        if (taskCount == 0) {
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
            clearIds();
//            System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--任务批量更新完毕，刷新页面");
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
}
