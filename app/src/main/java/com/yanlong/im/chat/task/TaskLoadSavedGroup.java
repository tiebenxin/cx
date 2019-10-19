package com.yanlong.im.chat.task;

import android.os.AsyncTask;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.dao.MsgDao;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.GsonUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public TaskLoadSavedGroup(List<Group> g) {
        groups = g;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (groups != null) {
            int len = groups.size();
            if (len > 0) {
//                String[] groupArr = new String[len];
                List<String> list = new ArrayList<>();
                JsonArray array = new JsonArray();
                for (int i = 0; i < len; i++) {
                    Group g = groups.get(i);
//                    groupArr[i] = g.getGid();
                    list.add(g.getGid());
                    array.add(g.getGid());
                }
//                Map<String, List<String>> map = new HashMap<>();
//                map.put("gids", list);
//                JsonObject object = new JsonObject();
//                object.add("gids",array);
                loadGroups(array.toString());
            }
        }
        return true;
    }

    private void loadGroups(String gids) {
        new MsgAction().getGroupsByIds(gids, new CallBack<ReturnBean<List<Group>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<Group>>> call, Response<ReturnBean<List<Group>>> response) {
                if (response != null && response.body() != null && response.body().isOk()) {
                    List<Group> groups = response.body().getData();
                    if (groups != null) {
                        msgDao.saveGroups(groups);
                        msgDao.updateNoSaveGroup(groups);
                    }
                }
            }
        });
    }
}
