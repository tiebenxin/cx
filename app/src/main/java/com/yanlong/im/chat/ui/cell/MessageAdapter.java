package com.yanlong.im.chat.ui.cell;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.interf.IActionTagClickListener;
import com.yanlong.im.chat.server.UpLoadService;
import com.yanlong.im.utils.audio.AudioPlayManager;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.view.MultiListView;
import net.cb.cb.library.view.recycler.MultiRecyclerView;

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

    private boolean isShowCheckBox;
    private int unreadCount = 0;
    private List<MsgAllBean> selectedList = new ArrayList<>();

    //msg_id,计时器 将计时器绑定到数据
    private Map<String, Disposable> mTimers = new HashMap<>();
    /********为保证Key-value两个值都是唯一，使用两个map 存储，查找删除方便****/
    //msg_id，position 用来找MsgId对应的position ,保证MsgId 唯一
    private Map<String, Integer> mMsgIdPositions = new HashMap<>();
    //msg_id，Indext 记录计时器中12张图片的Index
    private Map<String, Integer> mTimersIndexs = new HashMap<>();
    private MsgDao msgDao = new MsgDao();
    private IActionTagClickListener actionListener;
    private boolean isOpenRead;
    private MultiRecyclerView listView;


    public MessageAdapter(Context c, ICellEventListener l, boolean isG, MultiRecyclerView listView) {
        context = c;
        this.listView = listView;
        eventListener = l;
        mList = new ArrayList<>();
        isGroup = isG;
        refreshPositions();
    }

    public MessageAdapter setCellFactory(FactoryChatCell factory) {
        factoryChatCell = factory;
        return this;
    }

    public MessageAdapter setTagListener(IActionTagClickListener l) {
        actionListener = l;
        return this;
    }

//    public synchronized void bindData(List<MsgAllBean> list, boolean isMore) {
//        try {
//            int size = mList != null ? mList.size() : 0;
//            if (isMore) {
//                mList.addAll(0, list);
//            } else {
//                mList.clear();
//                notifyItemRangeRemoved(0, size);
//                mList.addAll(list);
//            }
//            notifyItemRangeInserted(0, list.size());
//            refreshPositions();
//
////            this.notifyDataSetChanged();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public synchronized void bindData(List<MsgAllBean> list, boolean isMore) {
        try {
            if (isMore) {
                mList.addAll(0, list);
            } else {
                mList = list;
            }
            LogUtil.getLog().i("阅后LOG","bindData--size="+mList.size());
            refreshPositions();
            this.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 遍历列表，并保存msgid位置
     */
    private void refreshPositions() {
        if (mMsgIdPositions == null) {
            return;
        }
        mMsgIdPositions.clear();
        if (mList != null && mList.size() > 0) {
            for (int position = 0; position < mList.size(); position++) {
                mMsgIdPositions.put(mList.get(position).getMsg_id(), position);
            }
        }
    }


    public boolean isGroup() {
        return isGroup;
    }


    @Override
    public long getItemId(int position) {
        return mList.get(position).getMsg_id().hashCode();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ChatEnum.EChatCellLayout layout = ChatEnum.EChatCellLayout.fromOrdinal(viewType);
        View view = LayoutInflater.from(context).inflate(layout.LayoutId, viewGroup, false);
        ChatCellBase cell = factoryChatCell.createCell(layout, view);
        cell.setReadStatus(isOpenRead);
        cell.setActionClickListener(actionListener);
        if (unreadCount >= 10) {
            cell.setFirstUnreadPosition(getItemCount() - unreadCount);
        }
        return cell;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        MsgAllBean msg = mList.get(position);
        ChatCellBase cellBase = (ChatCellBase) viewHolder;
        cellBase.setReadStatus(isOpenRead);
        //初始化阅后即焚状态
        initSurvialStatus(msg.getMsg_id(), cellBase);
        //开始阅后即焚
        addSurvivalTime(msg);
        if (msg.getSurvival_time() > 0 && msg.getStartTime() > 0 && msg.getEndTime() > 0) {
            ((ChatCellBase) viewHolder).setBellUI(msg.getSurvival_time(), false, msg.isMe());
            bindTimer(msg.getMsg_id(), msg.isMe(), msg.getStartTime(), msg.getEndTime());
            if (msg.getEndTime() < System.currentTimeMillis())
                ((ChatCellBase) viewHolder).setBellId(R.mipmap.icon_st_12);
        } else {
            ((ChatCellBase) viewHolder).setBellUI(msg.getSurvival_time(), true, msg.isMe());
        }
        cellBase.putMessage(mList.get(position), position);
        //TODO 修复#3567 语音播放动画在滚动出一屏幕然后返回，动画仍然处于播放中的问题
        if (msg.getMsg_type() == ChatEnum.EMessageType.VOICE) {
            ChatCellVoice voiceCell = (ChatCellVoice) viewHolder;
            if (msg == null && msg.getVoiceMessage() == null) {
                return;
            }
            voiceCell.setSendStatus(false);
            String url = msg.isMe() ? msg.getVoiceMessage().getLocalUrl() : msg.getVoiceMessage().getUrl();
            voiceCell.updateVoice(AudioPlayManager.getInstance().isPlay(Uri.parse(url)));
        }
    }

    /**
     * 初始化更新阅后即焚状态
     *
     * @param msgId
     * @param cellBase
     */
    private void initSurvialStatus(String msgId, ChatCellBase cellBase) {
        //及时更新阅后即焚状态
        if (mTimersIndexs.containsKey(msgId)) {
            String name = "icon_st_" + Math.min(COUNT, mTimersIndexs.get(msgId) + 1);
            int id = context.getResources().getIdentifier(name, "mipmap", context.getPackageName());
            LogUtil.getLog().i(MessageAdapter.class.getSimpleName(), "SurvivalTime--" + name);
            cellBase.setBellId(id);
        } else {
            String name = "icon_st_" + 1;
            int id = context.getResources().getIdentifier(name, "mipmap", context.getPackageName());
            cellBase.setBellId(id);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull List payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(viewHolder, position, payloads);
        } else {
            MsgAllBean msg = mList.get(position);
            ChatCellBase cellBase = (ChatCellBase) viewHolder;
            //初始化阅后即焚状态
            initSurvialStatus(msg.getMsg_id(), cellBase);
            //开始阅后即焚
            addSurvivalTime(msg);
            if (msg.getSurvival_time() > 0 && msg.getStartTime() > 0 && msg.getEndTime() > 0) {
                ((ChatCellBase) viewHolder).setBellUI(msg.getSurvival_time(), false, msg.isMe());
                bindTimer(msg.getMsg_id(), msg.isMe(), msg.getStartTime(), msg.getEndTime());
                if (msg.getEndTime() < System.currentTimeMillis())
                    ((ChatCellBase) viewHolder).setBellId(R.mipmap.icon_st_12);
            } else {
                ((ChatCellBase) viewHolder).setBellUI(msg.getSurvival_time(), true, msg.isMe());
            }
            if (msg.getMsg_type() == ChatEnum.EMessageType.IMAGE) {
                ChatCellImage imageCell = (ChatCellImage) viewHolder;
                imageCell.updateMessage(msg);
                if (UpLoadService.getProgress(msg.getMsg_id()) != null) {
                    imageCell.updateProgress(msg.getSend_state(), UpLoadService.getProgress(msg.getMsg_id()));
                }
            } else if (msg.getMsg_type() == ChatEnum.EMessageType.VOICE) {
                ChatCellVoice voiceCell = (ChatCellVoice) viewHolder;
                if (msg == null && msg.getVoiceMessage() == null) {
                    return;
                }
                voiceCell.setSendStatus(false);
                String url = msg.isMe() ? msg.getVoiceMessage().getLocalUrl() : msg.getVoiceMessage().getUrl();
//                LogUtil.getLog().i("语音LOG", "notify-" + AudioPlayManager.getInstance().isPlay(Uri.parse(url)) + "--msgId=" + msg.getMsg_id());
                voiceCell.updateVoice(AudioPlayManager.getInstance().isPlay(Uri.parse(url)));
            } else if (msg.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {
                ChatCellVideo videoCell = (ChatCellVideo) viewHolder;
                videoCell.updateMessage(msg);
                if (UpLoadService.getProgress(msg.getMsg_id()) != null) {
                    videoCell.updateProgress(msg.getSend_state(), UpLoadService.getProgress(msg.getMsg_id()));
                }
            } else if (msg.getMsg_type() == ChatEnum.EMessageType.FILE) {
                ChatCellFile fileCell = (ChatCellFile) viewHolder;
                fileCell.updateMessage(msg);
                if (UpLoadService.getProgress(msg.getMsg_id()) != null) {
                    fileCell.updateProgress(msg.getSend_state(), UpLoadService.getProgress(msg.getMsg_id()));
                }
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

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        ChatCellBase cell = (ChatCellBase) holder;
        cell.recycler();
//        if (holder instanceof ChatCellImage){
//            ChatCellImage cell = (ChatCellImage) holder;
//            cell.recycler();
//        }else if (holder instanceof ChatCellVideo){
//            ChatCellVideo cell = (ChatCellVideo) holder;
//            cell.recycler();
//        }
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

    public ChatCellBase getCellByPosition(int position) {
        return eventListener.getChatCellBase(position);
    }

    public synchronized void addMessage(MsgAllBean msg) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(msg);
        LogUtil.getLog().i("阅后LOG", "addMessage--size=" + mList.size());
        refreshPositions();
    }

    public void setMessageList(List<MsgAllBean> msg) {
        mList = msg;
        LogUtil.getLog().i("阅后LOG", "setMessageList--size=" + mList.size());
        refreshPositions();
    }

    public synchronized void addMessageList(int position, List<MsgAllBean> msg) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        mList.addAll(position, msg);
        LogUtil.getLog().i("阅后LOG", "addMessageList--size=" + mList.size());
        refreshPositions();
    }

    public void setUnreadCount(int position) {
        unreadCount = position;
    }


    public void showCheckBox(boolean flag, boolean update) {
        isShowCheckBox = flag;
//        if (update) {
//            notifyDataSetChanged();
//        }
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

    public synchronized void removeItem(MsgAllBean bean) {
        if (mList != null && bean != null) {
            boolean result = mList.remove(bean);
            if (!result) {
                LogUtil.getLog().i("阅后LOG", "移出单个失败");
            }
            LogUtil.getLog().i("阅后LOG", "removeItem--后--size=" + mList.size());
        }
        refreshPositions();
    }

    public synchronized void removeMsgList(List<MsgAllBean> list) {
        if (mList != null && list != null) {
            LogUtil.getLog().i("阅后LOG", "removeMsgList-前--size=" + mList.size());
            boolean result = mList.removeAll(list);
            if (!result) {
                LogUtil.getLog().i("阅后LOG", "移出list失败");
            }
            LogUtil.getLog().i("阅后LOG", "removeMsgList--后--size=" + mList.size());
        }
        refreshPositions();
    }

    public void clearSelectedMsg() {
        if (selectedList != null) {
            selectedList.clear();
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

    public void onDestroy() {
        //清除计时器，避免内存溢出
        for (Disposable timer : mTimers.values()) {
            if (!timer.isDisposed()) timer.dispose();
        }
        mMsgIdPositions.clear();
        mTimers.clear();
    }


    private synchronized void bindTimer(final String msgId, final boolean isMe, final long startTime, final long endTime) {
        try {
            if (mTimers.containsKey(msgId)) {
                return;
            }
            long nowTimeMillis = DateUtils.getSystemTime();
            long period = 0;
            long start = 1;
            if (nowTimeMillis < endTime) {//当前时间还在倒计时结束前

                mTimersIndexs.put(msgId, 1);
                long distance = startTime - nowTimeMillis;//和现在时间相差的毫秒数
                //四舍五入
                period = Math.round(Double.valueOf(endTime - startTime) / COUNT);
                if (distance < 0) {//开始时间小于现在，已经开始了
                    start = -distance / period;

                    mTimersIndexs.put(msgId, (int) start);
                    String name = "icon_st_" + Math.min(COUNT, start + 1);
                    int id = context.getResources().getIdentifier(name, "mipmap", context.getPackageName());
                    updateSurvivalTimeImage(msgId, id);
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
                                    String name = "icon_st_" + Math.min(COUNT, index + 1);
                                    int id = context.getResources().getIdentifier(name, "mipmap", context.getPackageName());
                                    updateSurvivalTimeImage(msgId, id);
//                                    LogUtil.getLog().i("SurvivalTime--CountDownView", "isME=" + index);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                updateSurvivalTimeImage(msgId, R.mipmap.icon_st_12);
                            }
                        }).subscribe();
                mTimers.put(msgId, timer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSurvivalTimeImage(String msgId, int id) {
        try {
            if (mMsgIdPositions != null && mMsgIdPositions.containsKey(msgId)) {
                int position = mMsgIdPositions.get(msgId);
//                ChatCellBase cell = getCellByPosition(position);
//                if (cell != null) {
//                    cell.setBellId(id);
//                } else {
                if (position >= 0 && position < getItemCount())
                    listView.getListView().getAdapter().notifyItemChanged(position);
//                }
            }
        } catch (Exception e) {
        }
    }


    /**
     * 添加阅读即焚消息到队列
     */
    public void addSurvivalTime(MsgAllBean msg) {
        boolean isGroup = isGroup();
        boolean isMe = msg.isMe();
        //单聊 自己发的消息，需等待对方已读
        boolean checkNotGroupAndNotRead = !isGroup && isMe && msg.getRead() != 1;
        if (msg == null || msg.getEndTime() > 0 || msg.getSend_state() != ChatEnum.ESendStatus.NORMAL
                || checkNotGroupAndNotRead) {
            return;
        }
        //单聊使用已读时间作为焚开始时间
        long date = msg.getReadTime();

        //群聊暂时不处理（待后期策略）
        if (isGroup || date == 0) {
            date = DateUtils.getSystemTime();
        }
        if (msg.getSurvival_time() > 0 && msg.getEndTime() == 0) {
            /**非退出即焚,后台操作
             * 群聊发送：发送成功，立即加入阅后即焚
             * 群聊接收：打开聊天界面，表示已读，立即加入阅后即焚
             * 单聊发送：发送成功且对方已读，立即加入阅后即焚
             * 单聊接收：打开聊天界面，表示已读，立即加入阅后即焚
             */
            msg.setEndTime(date + msg.getSurvival_time() * 1000);
            msg.setStartTime(date);
        }
    }

    public void setReadStatus(boolean isOpen) {
        isOpenRead = isOpen;
    }

}
