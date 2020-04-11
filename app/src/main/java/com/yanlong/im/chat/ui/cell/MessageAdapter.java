package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.EventSurvivalTimeAdd;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.server.UpLoadService;
import com.yanlong.im.utils.BurnManager;
import com.yanlong.im.utils.audio.AudioPlayManager;

import net.cb.cb.library.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MessageAdapter extends RecyclerView.Adapter {
    int COUNT = 12;


    private final Context context;
    private final ICellEventListener eventListener;
    private List<MsgAllBean> mList;
    private FactoryChatCell factoryChatCell;
    private final boolean isGroup;//是否群聊

    private Map<Integer, View> viewMap = new HashMap<>();
    private Map<Integer, ChatCellBase> cellMap = new HashMap<>();
    private boolean isShowCheckBox;
    private int unreadCount = 0;
    private List<MsgAllBean> selectedList = new ArrayList<>();

    //msg_id,计时器 将计时器绑定到数据
    private Map<String, Disposable> mTimers = new HashMap<>();
    /********为保证Key-value两个值都是唯一，使用两个map 存储，查找删除方便****/
    //position，msg_id 记住位置对应的Msg_id,用来找Position和保证mMsgIdPositions的position 唯一
    private Map<Integer, String> mPositionMsgIds = new HashMap<>();
    //msg_id，position 用来找MsgId对应的position ,保证MsgId 唯一
    private Map<String, Integer> mMsgIdPositions = new HashMap<>();
    //msg_id，Indext 记录计时器中12张图片的Index
    private Map<String, Integer> mTimersIndexs = new HashMap<>();
    private MsgDao msgDao = new MsgDao();


    public MessageAdapter(Context c, ICellEventListener l, boolean isG) {
        context = c;
        eventListener = l;
        mList = new ArrayList<>();
        isGroup = isG;

    }

    public MessageAdapter setCellFactory(FactoryChatCell factory) {
        factoryChatCell = factory;
        return this;
    }

    public void bindData(List<MsgAllBean> list, boolean isMore) {
        if (isMore) {
            mList.addAll(0, list);
        } else {
            mList = list;
        }
        this.notifyDataSetChanged();
    }

    public boolean isGroup() {
        return isGroup;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ChatEnum.EChatCellLayout layout = ChatEnum.EChatCellLayout.fromOrdinal(viewType);
        View view = LayoutInflater.from(context).inflate(layout.LayoutId, viewGroup, false);
        ChatCellBase cell = factoryChatCell.createCell(layout, view);
        return cell;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        MsgAllBean msg = mList.get(position);
        ChatCellBase cellBase = (ChatCellBase) viewHolder;
        savePositions(msg.getMsg_id(), position, msg.isMe(), cellBase);
        addSurvivalTime(msg);
        cellBase.putMessage(mList.get(position), position);
        viewMap.put(position, cellBase.itemView);
        cellMap.put(position, cellBase);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull List payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(viewHolder, position, payloads);
        } else {
            MsgAllBean msg = mList.get(position);
            savePositions(msg.getMsg_id(), position, msg.isMe(), (ChatCellBase) viewHolder);
            addSurvivalTime(msg);
            if (msg.getSurvival_time() > 0 && msg.getStartTime() > 0 && msg.getEndTime() > 0) {
                ((ChatCellBase) viewHolder).setBellUI(msg.getSurvival_time(), false, msg.isMe());
                bindTimer(msg.getMsg_id(), msg.isMe(), msg.getStartTime(), msg.getEndTime());
            } else {
                ((ChatCellBase) viewHolder).setBellUI(msg.getSurvival_time(), true, msg.isMe());
            }
            if (msg.getMsg_type() == ChatEnum.EMessageType.IMAGE) {
                ChatCellImage imageCell = (ChatCellImage) viewHolder;
                imageCell.updateMessage(msg);
                int progress = UpLoadService.getProgress(msg.getMsg_id());
                imageCell.updateProgress(msg.getSend_state(), progress);
            } else if (msg.getMsg_type() == ChatEnum.EMessageType.VOICE) {
                ChatCellVoice voiceCell = (ChatCellVoice) viewHolder;
                if (msg == null && msg.getVoiceMessage() == null) {
                    return;
                }
                voiceCell.setSendStatus(false);
                String url = msg.isMe() ? msg.getVoiceMessage().getLocalUrl() : msg.getVoiceMessage().getUrl();
                voiceCell.updateVoice(AudioPlayManager.getInstance().isPlay(Uri.parse(url)));
            } else if (msg.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {
                ChatCellVideo videoCell = (ChatCellVideo) viewHolder;
                videoCell.updateMessage(msg);
                int progress = UpLoadService.getProgress(msg.getMsg_id());
                videoCell.updateProgress(msg.getSend_state(), progress);
            } else {
                onBindViewHolder(viewHolder, position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;

    }

    @Override
    public int getItemViewType(int position) {
        if (mList != null && mList.size() > position) {
            return mList.get(position).getChatCellLayoutId().ordinal();
        }
        return super.getItemViewType(position);
    }

    //获取某位置消息
    public MsgAllBean getMessage(int position) {
        if (mList != null && mList.size() > position) {
            return mList.get(position);
        }
        return null;
    }

    //局部刷新
    public void updateItemAndRefresh(MsgAllBean bean) {
        int position = mList.indexOf(bean);
        if (position >= 0 && position < mList.size()) {
            mList.remove(position);
            mList.add(position, bean);
//            mList.set(position,bean);
            this.notifyItemChanged(position, position);
        }
    }

    public View getItemViewByPosition(int position) {
        if (!viewMap.isEmpty()) {
            return viewMap.get(position);
        }
        return null;
    }

    public ChatCellBase getCellByPosition(int position) {
        if (!cellMap.isEmpty()) {
            return cellMap.get(position);
        }
        return null;
    }

    public void addMessage(MsgAllBean msg) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(msg);
    }

    public void setMessageList(List<MsgAllBean> msg) {
        mList = msg;
    }

    public void addMessageList(int position, List<MsgAllBean> msg) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.addAll(position, msg);
    }

    public void setUnreadCount(int position) {
        unreadCount = position;
    }


    public void showCheckBox(boolean flag, boolean update) {
        isShowCheckBox = flag;
        if (update) {
            notifyDataSetChanged();
        }
    }

    public boolean isShowCheckBox() {
        return isShowCheckBox;
    }

    public List<MsgAllBean> getSelectedMsg() {
        return selectedList;
    }

    public List<MsgAllBean> getMsgList() {
        return mList;
    }

    public int getPosition(MsgAllBean bean) {
        int index = -1;
        if (bean != null && mList != null) {
            index = mList.indexOf(bean);
        }
        return index;
    }

    public void removeItem(MsgAllBean bean) {
        if (mList != null && bean != null) {
            mList.remove(bean);
        }
    }

    public void removeMsgList(List<MsgAllBean> list) {
        if (mList != null && list != null) {
            mList.removeAll(list);
        }
    }

    //更新数据
    public int updateMessage(MsgAllBean bean) {
        int index = -1;
        if (bean == null || mList == null) {
            return index;
        }
        index = mList.indexOf(bean);
        if (index >= 0) {
            mList.set(index, bean);
        }
        return index;

    }

    public void onDestory() {
        //清除计时器，避免内存溢出
        for (Disposable timer : mTimers.values()) {
            timer.dispose();
            timer = null;
        }
        mTimers.clear();
        mTimers = null;
    }


    private synchronized void bindTimer(final String msgId, final boolean isMe, final long startTime, final long endTime) {
        try {
            if (mTimers.containsKey(msgId)) {
                return;
            }
            long nowTimeMillis = DateUtils.getSystemTime();
            long period = 0;
            long start = 1;
            mTimersIndexs.put(msgId, 1);
            if (nowTimeMillis < endTime) {//当前时间还在倒计时结束前
                long distance = startTime - nowTimeMillis;//和现在时间相差的毫秒数
                //四舍五入
                period = Math.round(Double.valueOf(endTime - startTime) / COUNT);
                if (distance < 0) {//开始时间小于现在，已经开始了
                    start = -distance / period;
                }
                start = Math.max(1, start);
                //延迟initialDelay个unit单位后，以period为周期，依次发射count个以start为初始值并递增的数字。
                //eg:发送数字1~10，每间隔200毫秒发射一个数据 intervalRange(1, 10, 0, 200, TimeUnit.MILLISECONDS);
                //发送数字0~11，每间隔period/COUNT毫秒发射一个数据,延迟distance毫秒
                Disposable timer = Flowable.intervalRange(start, COUNT - start + 1, 0, period, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(new Consumer<Long>() {
                            @Override
                            public void accept(Long index) throws Exception {
                                try {
                                    mTimersIndexs.put(msgId, index.intValue());
                                    long time = nowTimeMillis - DateUtils.getSystemTime();
                                    String name = "icon_st_" + Math.min(COUNT, index + 1);
                                    int id = context.getResources().getIdentifier(name, "mipmap", context.getPackageName());
                                    updateSurvivalTimeImage(msgId, id, isMe);
                                    LogUtil.getLog().i("CountDownView", "isME=" + index);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                updateSurvivalTimeImage(msgId, R.mipmap.icon_st_12, isMe);
                            }
                        }).subscribe();
                mTimers.put(msgId, timer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSurvivalTimeImage(String msgId, int id, boolean isMe) {
        if (mMsgIdPositions.containsKey(msgId)) {
            int position = mMsgIdPositions.get(msgId);
            ChatCellBase cell = getCellByPosition(position);
            if (cell != null) {
                cell.setBellId(id);
            }
        }
    }

    /**
     * 保存msgid位置
     *
     * @param msgId
     * @param position
     */
    private void savePositions(String msgId, int position, boolean isMe, ChatCellBase cellBase) {
        //已经有MsgId包含该位置，则删除上一个，保证唯一性，更新时
        if (mMsgIdPositions.containsValue(position)) {
            mMsgIdPositions.remove(mPositionMsgIds.get(position));
        }
        //mPositionMsgIds只记录，不处理
        mPositionMsgIds.put(position, msgId);
        mMsgIdPositions.put(msgId, position);
        //及时更新阅后即焚状态
        if (mTimersIndexs.containsKey(msgId)) {
            String name = "icon_st_" + Math.min(COUNT, mTimersIndexs.get(msgId) + 1);
            int id = context.getResources().getIdentifier(name, "mipmap", context.getPackageName());
            if (mMsgIdPositions.containsKey(msgId)) {
                cellBase.setBellId(id);
            }
        }
    }

    /**
     * 添加阅读即焚消息到队列
     */
    public void addSurvivalTime(MsgAllBean msgbean) {
        boolean isGroup = isGroup();
        boolean isMe = msgbean.isMe();
        //单聊 自己发的消息，需等待对方已读
        boolean checkNotGroupAndNotRead = !isGroup && isMe && msgbean.getRead() != 1;
        if (msgbean == null || BurnManager.getInstance().isContainMsg(msgbean) || msgbean.getSend_state() != ChatEnum.ESendStatus.NORMAL
                || checkNotGroupAndNotRead) {
            return;
        }
        //单聊使用已读时间作为焚开始时间
        long date = msgbean.getReadTime();

        //群聊暂时不处理（待后期策略）
        if (isGroup || date == 0) {
            date = DateUtils.getSystemTime();
        }
        if (msgbean.getSurvival_time() > 0 && msgbean.getEndTime() == 0) {
            msgDao.setMsgEndTime((date + msgbean.getSurvival_time() * 1000), date, msgbean.getMsg_id());
            msgbean.setEndTime(date + msgbean.getSurvival_time() * 1000);
            msgbean.setStartTime(date);
            EventBus.getDefault().post(new EventSurvivalTimeAdd(msgbean, null));
            LogUtil.getLog().d("SurvivalTime", "设置阅后即焚消息时间1----> end:" + (date + msgbean.getSurvival_time() * 1000) + "---msgid:" + msgbean.getMsg_id());
        }
    }

}
