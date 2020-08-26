package com.yanlong.im.chat.bean;

import android.text.TextUtils;

import com.google.gson.JsonArray;

import net.cb.cb.library.utils.GsonUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * @author Liszt
 * @date 2019/8/6
 * Description 小助手消息
 */
public class AssistantMessage extends RealmObject implements IMsgContent {
    @PrimaryKey
    private String msgId;
    private String msg;// 为老版本兼容
    private int version;// 默认是老版本 1表示新版本
    private String title;// 标题
    private long time;// 时间
    private String content;// 内容
    private String signature;// 落款
    private long signature_time;// 落款时间
    @Ignore
    private int dispatch_type;// 0:分发给多端|1:分发给手机端
    @Ignore
    private List<Long> uid_list;// 接收人列表，未指定则发送给所有人
    private String items;
    @Ignore
    List<LabelItem> labelItems;// 子项列表

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public long getSignature_time() {
        return signature_time;
    }

    public void setSignature_time(long signature_time) {
        this.signature_time = signature_time;
    }

    public int getDispatch_type() {
        return dispatch_type;
    }

    public void setDispatch_type(int dispatch_type) {
        this.dispatch_type = dispatch_type;
    }

    public List<Long> getUid_list() {
        return uid_list;
    }

    public void setUid_list(List<Long> uid_list) {
        this.uid_list = uid_list;
    }

    public List<LabelItem> getLabelItems() {
        if (labelItems == null && !TextUtils.isEmpty(items)) {
            try {
                labelItems = new ArrayList<>();
                JSONArray array = new JSONArray(items);
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        LabelItem item = GsonUtils.getObject(array.getString(i), LabelItem.class);
                        if (item != null) {
                            labelItems.add(item);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return labelItems;
    }

    public void setLabelItems(List<LabelItem> labelItems) {
        this.labelItems = labelItems;
    }

    public String getItems() {
        if (TextUtils.isEmpty(items) && labelItems != null) {
            JsonArray jsonArray = new JsonArray();
            for (int i = 0; i < labelItems.size(); i++) {
                LabelItem item = labelItems.get(i);
                String s = GsonUtils.optObject(item);
                if (!TextUtils.isEmpty(s)) {
                    jsonArray.add(s);
                }
            }
            items = jsonArray.toString();
        }
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }
}
