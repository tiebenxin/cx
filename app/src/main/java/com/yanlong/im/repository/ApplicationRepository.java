package com.yanlong.im.repository;


import com.yanlong.im.chat.bean.Session;
import com.yanlong.im.data.local.ApplicationLocalDataSource;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @createAuthor Raleigh.Luo
 * @createDate 2020/4/8 0008
 * @description application仓库
 */
public class ApplicationRepository {
    private ApplicationLocalDataSource localDataSource;
    public RealmResults<Session> sessions;
    private List<SessionChangeListener> mSessionChangeListeners =new ArrayList<>();

    public ApplicationRepository() {
        localDataSource = new ApplicationLocalDataSource();
        startObserver();
    }
    public void addSessionChangeListener(SessionChangeListener sessionChangeListener){
        mSessionChangeListeners.add(sessionChangeListener);
    }

    public void startObserver() {
        sessions = getSesisons();
        /**集合通知OrderedRealmCollectionChangeListener
         * 该对象保存有关受删除，插入和更改影响的索引的信息。
         *
         * 前两个删除和插入记录已添加到集合中或从集合中删除的对象的索引。在将对象添加到Realm或Realm删除对象时会考虑到这一点。
         * 对于RealmResults，当您过滤特定值并且对象已更改以使其现在与查询匹配或不再匹配时，这也适用。
         */
        sessions.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Session>>() {
            @Override
            public void onChange(RealmResults<Session> sessions, OrderedCollectionChangeSet changeSet) {
                /*****null表示异步查询第一次返回。*******************************************************************************************/
                if (changeSet == null) {
//                    notifyDataSetChanged();
                    //1.异步更新所有detail
                    localDataSource.updateSessionDetail();
                    //通知监听器
                    for(SessionChangeListener sessionChangeListener : mSessionChangeListeners){
                        sessionChangeListener.init();
                    }
                    return;
                }
                ArrayList<String> sids = new ArrayList<String>();
                ArrayList<Integer> positions = new ArrayList<>();
//                //更新session详情
//                localDataSource.updateSessionDetail();
                /*****删除了数据，对于删除，必须以相反的顺序通知适配器。*******************************************************************************************/
                {
                    OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                    sids.clear();
                    positions.clear();
                    for (int index = deletions.length - 1; index >= 0; index--) {
                        OrderedCollectionChangeSet.Range range = deletions[index];
                        for (int i = 0; i < range.length; i++) {
                            int position=range.startIndex + i;
                            sids.add(sessions.get(position).getSid());
                            positions.add(position);
                        }

//                    notifyItemRangeRemoved(range.startIndex, range.length);
                    }
                    //1.删除-不需要更新detail
                    //2.通知监听器
                    for(SessionChangeListener listener:mSessionChangeListeners){
                        listener.delete(positions,sids);
                    }
                }

                /*****增加了数据*******************************************************************************************/
                {
                    OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                    sids.clear();
                    positions.clear();
                    //获取更新信息
                    for (OrderedCollectionChangeSet.Range range : insertions) {
                        for (int i = 0; i < range.length; i++) {
                            int position=range.startIndex + i;
                            sids.add(sessions.get(position).getSid());
                            positions.add(position);
                        }
//                    notifyItemRangeInserted(range.startIndex, range.length);
                    }

                    if (sids.size() > 0) {
                        //1.更新增加数据的detail详情
                        localDataSource.updateSessionDetail(sids.toArray(new String[sids.size()]));
                        //2.通知监听器
                        for(SessionChangeListener listener:mSessionChangeListeners){
                            listener.insert(positions,sids);
                        }
                    }

                }
                /*****数据更改*******************************************************************************************/
                {
                    OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                    sids.clear();
                    positions.clear();
                    //获取更新信息
                    for (OrderedCollectionChangeSet.Range range : modifications) {
                        for (int i = 0; i < range.length; i++) {
                            int position=range.startIndex + i;
                            sids.add(sessions.get(position).getSid());
                            positions.add(position);
                        }

//                    notifyItemRangeChanged(range.startIndex, range.length);
                    }

                    if (sids.size() > 0) {
                        //1.更新增加更改的detail详情
                        localDataSource.updateSessionDetail(sids.toArray(new String[sids.size()]));
                        //2.通知监听器
                        for(SessionChangeListener listener:mSessionChangeListeners){
                            listener.change(positions,sids);
                        }
                    }
                }

            }
        });
    }

    public Realm getRealm() {
        return localDataSource.getRealm();
    }

    /**
     * 获取session 列表-异步
     *
     * @return
     */
    public RealmResults<Session> getSesisons() {
        return localDataSource.getSession();
    }


    public void onDestory() {
        sessions.removeAllChangeListeners();
        mSessionChangeListeners.clear();
        localDataSource.onDestory();
    }

    public interface SessionChangeListener {
        void delete(ArrayList<Integer> position,ArrayList<String> sids);

        void insert(ArrayList<Integer> position,ArrayList<String> sids);

        void change(ArrayList<Integer> position,ArrayList<String> sids);

        void init();

    }
}


