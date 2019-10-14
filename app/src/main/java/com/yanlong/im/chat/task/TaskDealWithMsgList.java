package com.yanlong.im.chat.task;

import android.os.AsyncTask;

import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.utils.socket.MsgBean;

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
    private int taskCount = 0;//任务总数

    public TaskDealWithMsgList(List<MsgBean.UniversalMessage.WrapMessage> wrapMessageList) {
        messages = wrapMessageList;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (messages != null) {
            int length = messages.size();
            taskCount = length;
//            System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--总任务数=" + taskCount);
            for (int i = 0; i < length; i++) {
                MsgBean.UniversalMessage.WrapMessage wrapMessage = messages.get(i);
                boolean result = MessageManager.getInstance().dealWithMsg(wrapMessage, true, i == length - 1);//最后一条消息，发出通知声音
                if (result) {
                    taskCount--;
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
            MessageManager.getInstance().notifyRefreshMsg();
//            System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--任务批量更新完毕，刷新页面");
        }
    }

    /*
     * 更新任务数
     * 应用场景：异步加载用户数据或者群数据成功后
     * */
    public void updateTaskCount() {
        taskCount--;
//        System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--更新一次任务数");
        if (taskCount == 0) {
            MessageManager.getInstance().setMessageChange(true);
            MessageManager.getInstance().notifyRefreshMsg();
//            System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--任务批量更新完毕，刷新页面");
        }
    }
}
