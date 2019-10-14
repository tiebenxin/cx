package com.yanlong.im.chat.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.socket.MsgBean;

import java.util.List;

/**
 * @anthor Liszt
 * @data 2019/10/11
 * Description 批量处理接收到的消息
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
            System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--总任务数=" + taskCount);
            for (int i = 0; i < length; i++) {
                MsgBean.UniversalMessage.WrapMessage wrapMessage = messages.get(i);
                boolean result = MessageManager.getInstance().dealWithMsg(wrapMessage, true);
                if (result) {
                    taskCount--;
                }
            }
        }
        if (taskCount == 0) {
            System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--任务批量处理完成YES");
            return true;
        } else {
            System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--任务批量处理未完成NO");
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            MessageManager.getInstance().setMessageChange(true);
            MessageManager.getInstance().notifyRefreshMsg();
            System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--任务批量更新完毕，刷新页面");
        }
    }

    public void updateTaskCount() {
        taskCount--;
        System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--更新一次任务数");
        if (taskCount == 0) {
            MessageManager.getInstance().setMessageChange(true);
            MessageManager.getInstance().notifyRefreshMsg();
            System.out.println(TaskDealWithMsgList.class.getSimpleName() + "--任务批量更新完毕，刷新页面");
        }
    }
}
