package com.yanlong.im.chat.task;

import android.os.AsyncTask;

import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.dao.MsgDao;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.NetUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @anthor Liszt
 * @data 2019/10/11
 * Description 加载保存群数据
 */
public class TaskLoadSavedGroup extends AsyncTask<Void, Integer, Boolean> {
    private MsgDao msgDao = new MsgDao();
    private final List<Group> groups;

    TaskLoadSavedGroup(List<Group> g) {
        groups = g;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        return true;
    }

    private void loadGroups(String[] gids) {
        new MsgAction().getGroupsByIds(gids, new CallBack<ReturnBean<List<Group>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<Group>>> call, Response<ReturnBean<List<Group>>> response) {
                if (response != null && response.body() != null && response.body().isOk()){
                    List<Group> groups = response.body().getData();
                    if (groups != null){
                        msgDao.saveGroups(groups,true);
                    }
                }
            }
        });
    }


}
