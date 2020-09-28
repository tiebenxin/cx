package com.yanlong.im.user.ui.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.example.nim_lib.ui.BaseBindActivity;
import com.google.gson.Gson;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.CollectAtMessage;
import com.yanlong.im.chat.bean.CollectChatMessage;
import com.yanlong.im.chat.bean.CollectImageMessage;
import com.yanlong.im.chat.bean.CollectLocationMessage;
import com.yanlong.im.chat.bean.CollectSendFileMessage;
import com.yanlong.im.chat.bean.CollectShippedExpressionMessage;
import com.yanlong.im.chat.bean.CollectVideoMessage;
import com.yanlong.im.chat.bean.CollectVoiceMessage;
import com.yanlong.im.chat.bean.GroupPreviewBean;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.OfflineCollect;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.databinding.ActivityPreviewFileBinding;
import com.yanlong.im.user.bean.CollectionInfo;
import com.yanlong.im.utils.MyDiskCache;
import com.yanlong.im.utils.MyDiskCacheUtils;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.bean.FileBean;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.dialog.DialogCommon2;
import net.cb.cb.library.manager.FileManager;
import net.cb.cb.library.manager.excutor.ExecutorManager;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ThreadUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.springview.container.LoadFooter;
import net.cb.cb.library.view.springview.container.LoadHeader;
import net.cb.cb.library.view.springview.widget.SpringView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmObject;
import retrofit2.Call;
import retrofit2.Response;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static com.luck.picture.lib.tools.PictureFileUtils.APP_NAME;

/**
 * @author Liszt
 * @date 2020/9/14
 * Description 浏览当前会话所有图片，视频，文件
 */
public class PreviewMediaAllActivity2 extends BaseBindActivity<ActivityPreviewFileBinding> implements AdapterMediaAll.ISelectListener {

    public static final int REQUEST_FORWARD = 1 << 4;

    private AdapterMediaAll mAdapter;
    private MsgAction msgAction = new MsgAction();
    private MsgDao msgDao = new MsgDao();
    private String gid;
    private Long toUid;
    private long time;
    private String msgId;
    boolean isSelect = false;
    private List<Object> listData = new ArrayList<>();
    private List<GroupPreviewBean> previewBeans = new ArrayList<>();
    private int currentPosition;
    private int count;
    private boolean hasMoreData;
    private List<MsgAllBean> selectMsg = new ArrayList<>();
    private int firstOffset;
    private GridLayoutManager layoutManager;

    public static Intent newIntent(Context context, String gid, Long toUid, String msgId, long time) {
        Intent intent = new Intent(context, PreviewMediaAllActivity2.class);
        intent.putExtra("gid", gid);
        intent.putExtra("uid", toUid);
        intent.putExtra("msgId", msgId);
        intent.putExtra("time", time);
        return intent;
    }

    @Override
    protected int setView() {
        return R.layout.activity_preview_file;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mAdapter = new AdapterMediaAll(this);
        mAdapter.setListener(this);
        layoutManager = new GridLayoutManager(this, 4);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter.getItemViewType(position) == 0) {
                    return 4;
                }
                return 1;
            }
        });
        bindingView.recyclerView.setLayoutManager(layoutManager);
        bindingView.recyclerView.setItemAnimator(null);
        bindingView.recyclerView.setAdapter(mAdapter);
        bindingView.tvTime.setVisibility(View.GONE);
    }

    @Override
    protected void initEvent() {
        bindingView.headView.getActionbar().setTxtRight("选择");
        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                if (isSelect) {
                    isSelect = false;
                    mAdapter.notifyDataSetChanged();
                    return;
                }
                onBackPressed();
            }

            @Override
            public void onRight() {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                if (isSelect) {
                    if (selectMsg.size() > 0) {

                    } else {

                    }
                    switchSelectMode(false);
                } else {
                    switchSelectMode(true);
                }
            }
        });

        bindingView.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        //获取可视的第一个view
                        currentPosition = layoutManager.findFirstVisibleItemPosition();
                        View topView = layoutManager.getChildAt(currentPosition);
                        if (topView != null) {
                            //获取与该view的底部的偏移量
                            firstOffset = topView.getTop();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        bindingView.springView.setHeader(new LoadHeader());
        bindingView.springView.setFooter(new LoadFooter());
        bindingView.springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                if (listData == null) {
                    return;
                }
                Object o = listData.get(1);//第一位msg
                if (o instanceof MsgAllBean) {
                    MsgAllBean msg = (MsgAllBean) o;
                    loadMore(msg.getTimestamp(), 0);
                }
            }

            @Override
            public void onLoadMore() {
                if (listData == null) {
                    return;
                }
                Object o = listData.get(listData.size() - 1);//最后一位msg
                if (o instanceof MsgAllBean) {
                    MsgAllBean msg = (MsgAllBean) o;
                    loadMore(msg.getTimestamp(), 1);
                }
            }
        });

        bindingView.ivForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                filterMessageValid(selectMsg, 1);
            }
        });
        bindingView.ivCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                filterMessageValid(selectMsg, 2);
            }
        });
        bindingView.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                List<MsgAllBean> removeList = new ArrayList<>();
                removeList.addAll(selectMsg);
                showDeleteDialog(removeList);
            }
        });
        bindingView.ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                filterMessageValid(selectMsg, 3);
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    protected void loadData() {
        Intent intent = getIntent();
        gid = intent.getStringExtra("gid");
        toUid = intent.getLongExtra("uid", 0L);
        time = intent.getLongExtra("time", 0L);
        msgId = intent.getStringExtra("msgId");
        showLoadingDialog();
        Observable.just(0)
                .map(new Function<Integer, List<GroupPreviewBean>>() {
                    @Override
                    public List<GroupPreviewBean> apply(Integer integer) throws Exception {
                        List<GroupPreviewBean> list = msgAction.getAllMediaMsg(gid, toUid, time);
                        count = 0;
                        for (int i = 0; i < list.size(); i++) {
                            GroupPreviewBean bean = list.get(i);
                            listData.add(bean.getTime());
                            listData.addAll(bean.getMsgAllBeans());
                            count += bean.getMsgAllBeans().size();
//                            if (bean.isBetween(time)) {
////                                currentPosition = i;
////                            }
                        }
                        return list;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<GroupPreviewBean>>empty())
                .subscribe(new Consumer<List<GroupPreviewBean>>() {
                    @Override
                    public void accept(List<GroupPreviewBean> list) throws Exception {
                        dismissLoadingDialog();
                        previewBeans = list;
                        mAdapter.bindData(listData);
                        bindingView.headView.setTitle("图片及视频（" + count + ")");
                        bindingView.recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                bindingView.recyclerView.scrollBy(0, Integer.MAX_VALUE);

                            }
                        }, 100);
                    }
                });
    }

    // refreshType 0 下拉刷新，1 上拉加载更多
    @SuppressLint("CheckResult")
    public void loadMore(long time, int refreshType) {
        hasMoreData = false;
        Observable.just(0)
                .map(new Function<Integer, List<GroupPreviewBean>>() {
                    @Override
                    public List<GroupPreviewBean> apply(Integer integer) throws Exception {
                        List<GroupPreviewBean> list = msgAction.getMoreMediaMsg(gid, toUid, time, refreshType);
                        int size = list.size();
                        if (size > 0) {
                            hasMoreData = true;
                        }
                        List<GroupPreviewBean> removeList = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            GroupPreviewBean bean = list.get(i);
                            if (previewBeans.contains(bean)) {
                                int index = previewBeans.indexOf(bean);
                                if (index >= 0 && index < previewBeans.size()) {
                                    removeList.add(bean);
                                    GroupPreviewBean b = previewBeans.get(index);
                                    if (refreshType == 0) {
                                        List<MsgAllBean> result = b.getMsgAllBeans();
                                        result.addAll(0, bean.getMsgAllBeans());
                                        b.setStartTime(bean.getMsgAllBeans().get(0).getTimestamp());
                                        b.setMsgAllBeans(result);
                                        listData.addAll(1, bean.getMsgAllBeans());
                                    } else {
                                        List<MsgAllBean> result = b.getMsgAllBeans();
                                        List<MsgAllBean> temp = bean.getMsgAllBeans();
                                        result.addAll(temp);
                                        b.setEndTime(temp.get(temp.size() - 1).getTimestamp());
                                        b.setMsgAllBeans(result);
                                    }
                                }
                            } else {
                                if (refreshType == 0) {
                                    listData.add(0, bean.getTime());
                                    listData.addAll(1, bean.getMsgAllBeans());
                                } else {
                                    listData.add(bean.getTime());
                                    listData.addAll(bean.getMsgAllBeans());
                                }
                            }
                            count += bean.getMsgAllBeans().size();
                            if (bean.isBetween(time)) {
                                currentPosition = i;
                            }
                        }

                        if (removeList.size() > 0) {
                            list.removeAll(removeList);
                        }
                        return list;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<GroupPreviewBean>>empty())
                .subscribe(new Consumer<List<GroupPreviewBean>>() {
                    @Override
                    public void accept(List<GroupPreviewBean> list) throws Exception {
                        dismissLoadingDialog();
                        boolean needRefresh = true;
                        if (list == null || list.size() <= 0) {
                            needRefresh = false;
                        }
                        bindingView.headView.setTitle("图片及视频（" + count + ")");
//                        if (refreshType == 0) {
//                            if (needRefresh) {
//                                listData.addAll(0, list);
//                            }
//                        } else {
//                            if (needRefresh) {
//                                listData.addAll(list);
//                            }
//                        }
                        if (hasMoreData && refreshType == 0) {
                            mAdapter.bindData(listData);
                            hasMoreData = false;

                            if (currentPosition >= 0) {
                                bindingView.recyclerView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        layoutManager.scrollToPositionWithOffset(currentPosition, firstOffset);
                                    }
                                }, 100);
                            }
                        }


                        bindingView.springView.onFinishFreshAndLoad();
                    }
                });
    }

    @Override
    public void onSelect(MsgAllBean bean) {
        selectMsg.add(bean);
        showBottom(true);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRemove(MsgAllBean bean) {
        selectMsg.remove(bean);
        if (selectMsg.size() == 0) {
            showBottom(false);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPreview(MsgAllBean bean) {
        if (bean != null) {
            scanImageAndVideo(bean.getMsg_id());
        }
    }

    //检测图片，视频消息是否过期，过期则过滤。 action 1 转发，2收藏,3 下载
    @SuppressLint("CheckResult")
    private void filterMessageValid(List<MsgAllBean> sourList, int action) {
        int sourLen = sourList.size();
        Observable.just(0)
                .map(new Function<Integer, List<MsgAllBean>>() {
                    @Override
                    public List<MsgAllBean> apply(Integer integer) throws Exception {
                        if (sourLen > 0) {
                            String[] msgIds = new String[sourLen];
                            for (int i = 0; i < sourLen; i++) {
                                MsgAllBean msgAllBean = sourList.get(i);
                                msgIds[i] = msgAllBean.getMsg_id();
                            }
                            List<MsgAllBean> uploadMessages = msgDao.getUploadMessage(msgIds);
                            if (uploadMessages != null) {
                                int len = uploadMessages.size();
                                if (len > 0) {
                                    ArrayList<FileBean> fileBeans = new ArrayList<>();
                                    Map<String, MsgAllBean> filterMsgList = new HashMap<>();
                                    for (int i = 0; i < len; i++) {
                                        MsgAllBean msgAllBean = uploadMessages.get(i);
                                        String md5 = "";
                                        String url = "";
                                        if (msgAllBean.getImage() != null) {
                                            md5 = UpFileUtil.getInstance().getFilePathMd5(msgAllBean.getImage().getPreview());
                                            url = UpFileUtil.getInstance().getFileUrl(msgAllBean.getImage().getPreview(), msgAllBean.getMsg_type());
                                        } else if (msgAllBean.getVideoMessage() != null) {
                                            //添加第一帧背景图
                                            FileBean fileBean = new FileBean();
                                            fileBean.setMd5(UpFileUtil.getInstance().getFilePathMd5(msgAllBean.getVideoMessage().getBg_url()));
                                            fileBean.setUrl(UpFileUtil.getInstance().getFileUrl(msgAllBean.getVideoMessage().getBg_url(), ChatEnum.EMessageType.IMAGE));
                                            fileBeans.add(fileBean);
                                            //视频源文件
                                            md5 = UpFileUtil.getInstance().getFilePathMd5(msgAllBean.getVideoMessage().getUrl());
                                            url = UpFileUtil.getInstance().getFileUrl(msgAllBean.getVideoMessage().getUrl(), msgAllBean.getMsg_type());
                                        } else if (msgAllBean.getSendFileMessage() != null) {
                                            md5 = UpFileUtil.getInstance().getFilePathMd5(msgAllBean.getSendFileMessage().getUrl());
                                            url = UpFileUtil.getInstance().getFileUrl(msgAllBean.getSendFileMessage().getUrl(), msgAllBean.getMsg_type());
                                        }
                                        if (!TextUtils.isEmpty(md5)) {
                                            FileBean fileBean = new FileBean();
                                            fileBean.setMd5(md5);
                                            fileBean.setUrl(url);
                                            fileBeans.add(fileBean);
                                            filterMsgList.put(md5, msgAllBean);
                                        }
                                    }

                                    if (fileBeans.size() > 0) {
                                        UpFileUtil.getInstance().batchFileCheck(fileBeans, new CallBack<ReturnBean<List<String>>>() {
                                            @Override
                                            public void onResponse(Call<ReturnBean<List<String>>> call, Response<ReturnBean<List<String>>> response) {
                                                super.onResponse(call, response);
                                                if (response != null && response.body() != null) {
                                                    ReturnBean returnButton = response.body();
                                                    if (returnButton != null && returnButton.isOk()) {
                                                        List<String> urls = response.body().getData();
                                                        int size = urls.size();
                                                        if (size == fileBeans.size()) {
                                                            //都未过期
                                                            if (action == 1) {
                                                                filterMsgForward(sourList, false);
                                                            } else if (action == 2) {
                                                                filterMsgCollection(sourList, false);
                                                            } else if (action == 3) {
                                                                downloadFile(sourList, false);
                                                            }
                                                        } else {
                                                            for (int i = 0; i < size; i++) {
                                                                String md5 = urls.get(i);
                                                                filterMsgList.remove(md5);
                                                            }
                                                            if (filterMsgList.size() > 0) {
                                                                Iterator iterator = filterMsgList.keySet().iterator();
                                                                while (iterator.hasNext()) {
                                                                    MsgAllBean bean = filterMsgList.get(iterator.next().toString());
                                                                    sourList.remove(bean);
                                                                }
                                                            }
                                                            if (action == 1) {
                                                                filterMsgForward(sourList, true);//是否过滤掉了过期文件
                                                            } else if (action == 2) {
                                                                filterMsgCollection(sourList, true);
                                                            } else if (action == 3) {
                                                                downloadFile(sourList, true);
                                                            }
                                                        }
                                                    } else {//都是过期的
                                                        if (filterMsgList.size() > 0) {
                                                            Iterator iterator = filterMsgList.keySet().iterator();
                                                            while (iterator.hasNext()) {
                                                                MsgAllBean bean = filterMsgList.get(iterator.next().toString());
                                                                sourList.remove(bean);
                                                            }
                                                        }
                                                        if (action == 1) {
                                                            filterMsgForward(sourList, true);
                                                        } else if (action == 2) {
                                                            filterMsgCollection(sourList, true);
                                                        } else if (action == 3) {
                                                            downloadFile(sourList, true);
                                                        }
                                                    }
                                                } else {
                                                    if (action == 1) {
                                                        ToastUtil.show("转发失败");
                                                    } else if (action == 2) {
                                                        ToastUtil.show("收藏失败");
                                                    } else if (action == 3) {
                                                        ToastUtil.show("下载失败");
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ReturnBean<List<String>>> call, Throwable t) {
                                                super.onFailure(call, t);
                                                if (action == 1) {
                                                    ToastUtil.show("转发失败");
                                                } else if (action == 2) {
                                                    ToastUtil.show("收藏失败");
                                                } else if (action == 3) {
                                                    ToastUtil.show("下载失败");
                                                }
                                            }
                                        });
                                    } else {
                                        if (action == 1) {
                                            filterMsgForward(sourList, false);
                                        } else if (action == 2) {
                                            filterMsgCollection(sourList, false);
                                        }
                                    }
                                } else {
                                    if (action == 1) {
                                        filterMsgForward(sourList, false);
                                    } else if (action == 2) {
                                        filterMsgCollection(sourList, false);
                                    }
                                }
                            } else {
                                if (action == 1) {
                                    filterMsgForward(sourList, false);
                                } else if (action == 2) {
                                    filterMsgCollection(sourList, false);
                                }
                            }
                        }
                        return null;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<MsgAllBean>>empty())
                .subscribe(new Consumer<List<MsgAllBean>>() {
                    @Override
                    public void accept(List<MsgAllBean> list) throws Exception {
                    }
                });
    }

    /**
     * 过滤多选转发消息
     *
     * @param sourList  源数据
     * @param isOverdue 是否已经过滤掉了过期文件(是否含有不存在文件)
     */
    @SuppressLint("CheckResult")
    private void filterMsgForward(final List<MsgAllBean> sourList, boolean isOverdue) {
        int totalSize = selectMsg.size();
        int sourLen = sourList.size();
        Observable.just(0)
                .map(new Function<Integer, List<MsgAllBean>>() {
                    @Override
                    public List<MsgAllBean> apply(Integer integer) throws Exception {
                        if (sourLen > 0) {
                            String[] msgIds = new String[sourLen];
                            for (int i = 0; i < sourLen; i++) {
                                MsgAllBean msgAllBean = sourList.get(i);
                                msgIds[i] = msgAllBean.getMsg_id();
                            }
                            return msgDao.filterMsgForForward(msgIds);
                        } else {
                            return new ArrayList<MsgAllBean>();
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<MsgAllBean>>empty())
                .subscribe(new Consumer<List<MsgAllBean>>() {
                    @Override
                    public void accept(List<MsgAllBean> list) throws Exception {
                        if (list != null) {
                            int len = list.size();
                            if (len > 0) {
                                //只要含有一个正常类型消息
                                if (len == totalSize) {
                                    if (isOverdue) {
                                        showForwardListDialog(list);//存在不支持类型或失效的转发
                                    } else {
                                        toForward(list);//正常类型转发
                                    }
                                } else if (len < totalSize) {
                                    showForwardListDialog(list);//存在不支持类型或失效的转发
                                }
                            } else {
                                //全为不支持类型或失效消息
                                showValidMsgDialog(false);
                            }
                        } else {
                            showValidMsgDialog(false);
                        }
                    }
                });
    }

    /**
     * 过滤多选收藏消息
     *
     * @param sourList  源数据
     * @param isOverdue 是否已经过滤掉了过期文件(是否含有不存在文件)
     */
    @SuppressLint("CheckResult")
    private void filterMsgCollection(final List<MsgAllBean> sourList, boolean isOverdue) {
        int totalSize = selectMsg.size();
        int sourLen = sourList.size();
        Observable.just(0)
                .map(new Function<Integer, List<MsgAllBean>>() {
                    @Override
                    public List<MsgAllBean> apply(Integer integer) throws Exception {
                        if (sourLen > 0) {
                            String[] msgIds = new String[sourLen];
                            for (int i = 0; i < sourLen; i++) {
                                MsgAllBean msgAllBean = sourList.get(i);
                                msgIds[i] = msgAllBean.getMsg_id();
                            }
                            return msgDao.filterMsgForCollection(msgIds);
                        } else {
                            return new ArrayList<MsgAllBean>();
                        }

                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<MsgAllBean>>empty())
                .subscribe(new Consumer<List<MsgAllBean>>() {
                    @Override
                    public void accept(List<MsgAllBean> list) throws Exception {
                        if (list != null) {
                            int len = list.size();
                            if (len > 0) {
                                //只要含有一个正常类型消息
                                if (len == totalSize) {
                                    if (isOverdue) {
                                        toCollectList(list, false);//存在不支持类型或失效的收藏
                                    } else {
                                        toCollectList(list, true);//正常类型收藏
                                    }
                                } else if (len < totalSize) {
                                    toCollectList(list, false);//存在不支持类型或失效的收藏
                                }
                            } else {
                                //全为不支持类型或失效消息
                                showValidMsgDialog(true);
                            }
                        } else {
                            showValidMsgDialog(true);
                        }
                    }
                });
    }

    /**
     * 批量转发提示弹框
     */
    private void showForwardListDialog(List<MsgAllBean> list) {
        if (this.isFinishing()) {
            return;
        }
        DialogCommon dialogValid = new DialogCommon(this);
        dialogValid.setContent("你所选的消息包含了不支持转发的类型或\n或已失效，系统已自动过滤此类型消息。", true)
                .setTitleAndSure(false, true)
                .setRight("继续发送")
                .setLeft("取消")
                .setListener(new DialogCommon.IDialogListener() {
                    @Override
                    public void onSure() {
                        toForward(list);
                        dialogValid.dismiss();
                    }

                    @Override
                    public void onCancel() {

                    }


                }).show();

    }

    private void toForward(List<MsgAllBean> list) {
        if (list != null && list.size() > 0) {
            onForwardActivity(ChatEnum.EForwardMode.ONE_BY_ONE, new Gson().toJson(list));
        }
        clearSelectMsg();
        switchSelectMode(false);
    }

    /**
     * 多选转发
     */
    private void onForwardActivity(@ChatEnum.EForwardMode int model, String json) {
        if (TextUtils.isEmpty(json)) {
            return;
        }
        Intent intent = MsgForwardActivity.newIntent(this, model, json);
        startActivityForResult(intent, REQUEST_FORWARD);
    }


    private void showValidMsgDialog(boolean clear) {
        DialogCommon2 dialogValid = new DialogCommon2(this);
        dialogValid.setContent("你选的消息包含不支持消息或已失效", true)
                .setButtonTxt("确定")
                .hasTitle(false)
                .setListener(new DialogCommon2.IDialogListener() {
                    @Override
                    public void onClick() {
                        dialogValid.dismiss();
                        if (clear) {
                            clearSelectMsg();
                            switchSelectMode(false);
                        }
                    }
                }).show();
    }

    /**
     * 批量收藏  (流程有变化，过滤掉不支持类型后，先调接口，再弹框，目前需求只显示一次)
     *
     * @param list     已过滤后的数据
     * @param isNormal 正常类型true 含有不支持类型false
     */
    public void toCollectList(List<MsgAllBean> list, boolean isNormal) {
        if (list.size() > 0) {
            List<CollectionInfo> dataList = convertCollectBean(list);
            if (dataList != null && dataList.size() > 0) {
                //1 有网收藏
                if (checkNetConnectStatus(1)) {
                    msgAction.offlineAddCollections(dataList, new CallBack<ReturnBean>() {
                        @Override
                        public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                            super.onResponse(call, response);
                            if (response.body() == null) {
                                return;
                            }
                            if (response.body().isOk()) {
                                if (isNormal) {
                                    // data!=null代表有"源文件不存在"情况，提示弹框
                                    if (response.body().getData() != null) {
                                        showCollectListDialog(1);
                                    } else {
                                        ToastUtil.show("收藏成功");
                                    }
                                } else {
                                    //用户选过不支持的类型，因此无论如何都要提示弹框
                                    showCollectListDialog(1);
                                }
                            } else {
                                if (!TextUtils.isEmpty(response.body().getMsg())) {
                                    ToastUtil.show(response.body().getMsg() + "");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ReturnBean> call, Throwable t) {
                            super.onFailure(call, t);
                            ToastUtil.show("收藏失败");
                        }
                    });
                } else {
                    //2 无网收藏
                    //2-1 如果本地收藏列表不存在这条数据，收藏到列表，并保存收藏操作记录
                    for (CollectionInfo info : dataList) {
                        if (msgDao.findLocalCollection(info.getMsgId()) == null) {
                            msgDao.addLocalCollection(info);//保存到本地收藏列表
                            OfflineCollect offlineCollect = new OfflineCollect();
                            offlineCollect.setMsgId(info.getMsgId());
                            offlineCollect.setCollectionInfo(info);
                            msgDao.addOfflineCollectRecord(offlineCollect);//保存到离线收藏记录表
                        }
                    }
                    //2-2 如果本地收藏列表存在这条数据，无需再重复收藏，不做任何操作
                    if (isNormal) {
                        ToastUtil.show("收藏成功");//离线提示
                    } else {
                        //用户选过不支持的类型，因此无论如何都要提示弹框
                        showCollectListDialog(1);
                    }
                }
            }
            clearSelectMsg();
            switchSelectMode(false);
        }
    }

    /**
     * 批量收藏提示弹框
     *
     * @param type 0 默认提示  1 包含收藏成功文案
     */
    private void showCollectListDialog(int type) {
        if (this.isFinishing()) {
            return;
        }
        String content;
        if (type == 1) {
            content = "收藏成功\n\n你所选的消息包含了不支持收藏的类型\n或已失效，系统已自动过滤此类型消息。";
        } else {
            content = "你所选的消息包含了不支持收藏的类型\n或已失效，系统已自动过滤此类型消息。";
        }

        DialogCommon2 dialogValid = new DialogCommon2(this);
        dialogValid.setContent(content, true)
                .setButtonTxt("确定")
                .setListener(new DialogCommon2.IDialogListener() {
                    @Override
                    public void onClick() {
                        dialogValid.dismiss();
                    }
                }).show();
    }

    //消息类批量转换成收藏类
    private List<CollectionInfo> convertCollectBean(List<MsgAllBean> msgAllBeanList) {
        List<CollectionInfo> list = new ArrayList<>();//批量保存
        for (int i = 0; i < msgAllBeanList.size(); i++) {
            if (msgAllBeanList.get(i) != null) {
                //状态正常且满足可收藏类型
                if (msgAllBeanList.get(i).getSend_state() == ChatEnum.ESendStatus.ERROR) {
                    break;
                }
                if (msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.TEXT || msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.AT
                        || msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.VOICE || msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.LOCATION
                        || msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.IMAGE || msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO
                        || msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.FILE || msgAllBeanList.get(i).getMsg_type() == ChatEnum.EMessageType.SHIPPED_EXPRESSION) {
                    String fromUsername = "";//用户名称
                    String fromGid = "";//群组id
                    String fromGroupName = "";//群组名称
                    MsgAllBean msgbean = msgAllBeanList.get(i);
                    if (!TextUtils.isEmpty(msgbean.getFrom_nickname())) {
                        fromUsername = msgbean.getFrom_nickname();
                    } else {
                        fromUsername = "";
                    }
                    if (!TextUtils.isEmpty(msgbean.getGid())) {
                        fromGid = msgbean.getGid();
                    } else {
                        fromGid = "";
                    }
                    if (msgbean.getGroup() != null) {
                        if (!TextUtils.isEmpty(msgbean.getGroup().getName())) {
                            fromGroupName = msgbean.getGroup().getName();
                        } else {
                            fromGroupName = msgDao.getGroupName(msgbean.getGid());//没有群名称，拿自动生成的群昵称给后台
                        }
                    }
                    CollectionInfo collectionInfo = new CollectionInfo();
                    //区分不同消息类型，转换成新的收藏消息结构，作为data传过去
                    if (msgbean.getMsg_type() == ChatEnum.EMessageType.TEXT) {
                        collectionInfo.setData(new Gson().toJson(convertCollectBean(ChatEnum.EMessageType.TEXT, msgbean)));
                    } else if (msgbean.getMsg_type() == ChatEnum.EMessageType.IMAGE) {
                        collectionInfo.setData(new Gson().toJson(convertCollectBean(ChatEnum.EMessageType.IMAGE, msgbean)));
                    } else if (msgbean.getMsg_type() == ChatEnum.EMessageType.SHIPPED_EXPRESSION) {
                        collectionInfo.setData(new Gson().toJson(convertCollectBean(ChatEnum.EMessageType.SHIPPED_EXPRESSION, msgbean)));
                    } else if (msgbean.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {
                        collectionInfo.setData(new Gson().toJson(convertCollectBean(ChatEnum.EMessageType.MSG_VIDEO, msgbean)));
                    } else if (msgbean.getMsg_type() == ChatEnum.EMessageType.VOICE) {
                        collectionInfo.setData(new Gson().toJson(convertCollectBean(ChatEnum.EMessageType.VOICE, msgbean)));
                    } else if (msgbean.getMsg_type() == ChatEnum.EMessageType.LOCATION) {
                        collectionInfo.setData(new Gson().toJson(convertCollectBean(ChatEnum.EMessageType.LOCATION, msgbean)));
                    } else if (msgbean.getMsg_type() == ChatEnum.EMessageType.AT) {
                        collectionInfo.setData(new Gson().toJson(convertCollectBean(ChatEnum.EMessageType.AT, msgbean)));
                    } else if (msgbean.getMsg_type() == ChatEnum.EMessageType.FILE) {
                        CollectSendFileMessage msg = (CollectSendFileMessage) convertCollectBean(ChatEnum.EMessageType.FILE, msgbean);
                        collectionInfo.setData(new Gson().toJson(msg));
                    }
                    collectionInfo.setFromUid(msgbean.getFrom_uid());
                    collectionInfo.setFromUsername(fromUsername);
                    collectionInfo.setType(SocketData.getMessageType(msgbean.getMsg_type()).getNumber());//收藏类型统一改为protobuf类型
                    collectionInfo.setFromGid(fromGid);
                    collectionInfo.setFromGroupName(fromGroupName);
                    collectionInfo.setMsgId(msgbean.getMsg_id());//不同表，id相同
                    collectionInfo.setCreateTime(System.currentTimeMillis() + "");//收藏时间是现在系统时间
                    list.add(collectionInfo);
                }
            }
        }
        return list;
    }

    //转换成新的收藏消息结构
    private RealmObject convertCollectBean(int type, MsgAllBean msgAllBean) {
        if (type == ChatEnum.EMessageType.TEXT) {
            CollectChatMessage collectChatMessage = new CollectChatMessage();
            collectChatMessage.setMsgid(msgAllBean.getChat().getMsgId());
            collectChatMessage.setMsg(msgAllBean.getChat().getMsg());
            return collectChatMessage;
        }
        if (type == ChatEnum.EMessageType.IMAGE) {
            CollectImageMessage collectImageMessage = new CollectImageMessage();
            collectImageMessage.setMsgid(msgAllBean.getImage().getMsgId());
            collectImageMessage.setOrigin(msgAllBean.getImage().getOrigin());
            collectImageMessage.setPreview(msgAllBean.getImage().getPreview());
            collectImageMessage.setThumbnail(msgAllBean.getImage().getThumbnail());
            collectImageMessage.setWidth(msgAllBean.getImage().getWidth());
            collectImageMessage.setHeight(msgAllBean.getImage().getHeight());
            collectImageMessage.setSize(msgAllBean.getImage().getSize());
            collectImageMessage.setLocalimg(msgAllBean.getImage().getLocalimg());
            return collectImageMessage;
        }
        if (type == ChatEnum.EMessageType.SHIPPED_EXPRESSION) {
            CollectShippedExpressionMessage collectShippedExpressionMessage = new CollectShippedExpressionMessage();
            collectShippedExpressionMessage.setMsgId(msgAllBean.getShippedExpressionMessage().getMsgId());
            collectShippedExpressionMessage.setExpression(msgAllBean.getShippedExpressionMessage().getId());
            return collectShippedExpressionMessage;
        }
        if (type == ChatEnum.EMessageType.MSG_VIDEO) {
            CollectVideoMessage collectVideoMessage = new CollectVideoMessage();
            collectVideoMessage.setMsgId(msgAllBean.getVideoMessage().getMsgId());
            collectVideoMessage.setVideoDuration(msgAllBean.getVideoMessage().getDuration());
            collectVideoMessage.setVideoBgURL(msgAllBean.getVideoMessage().getBg_url());
            collectVideoMessage.setVideoURL(msgAllBean.getVideoMessage().getUrl());
            collectVideoMessage.setWidth(msgAllBean.getVideoMessage().getWidth());
            collectVideoMessage.setHeight(msgAllBean.getVideoMessage().getHeight());
            collectVideoMessage.setSize(msgAllBean.getVideoMessage().getDuration());//旧消息没有和这个字段
            return collectVideoMessage;
        }
        if (type == ChatEnum.EMessageType.VOICE) {
            CollectVoiceMessage collectVoiceMessage = new CollectVoiceMessage();
            collectVoiceMessage.setMsgId(msgAllBean.getVoiceMessage().getMsgId());
            collectVoiceMessage.setVoiceURL(msgAllBean.getVoiceMessage().getUrl());
            collectVoiceMessage.setVoiceDuration(msgAllBean.getVoiceMessage().getTime());
            collectVoiceMessage.setLocalUrl(msgAllBean.getVoiceMessage().getLocalUrl());
            return collectVoiceMessage;
        }
        if (type == ChatEnum.EMessageType.LOCATION) {
            CollectLocationMessage collectLocationMessage = new CollectLocationMessage();
            collectLocationMessage.setMsgId(msgAllBean.getLocationMessage().getMsgId());
            collectLocationMessage.setLat(msgAllBean.getLocationMessage().getLatitude());
            collectLocationMessage.setLon(msgAllBean.getLocationMessage().getLongitude());
            collectLocationMessage.setAddr(msgAllBean.getLocationMessage().getAddress());
            collectLocationMessage.setAddressDesc(msgAllBean.getLocationMessage().getAddressDescribe());
            collectLocationMessage.setImg(msgAllBean.getLocationMessage().getImg());
            return collectLocationMessage;
        }
        if (type == ChatEnum.EMessageType.AT) {
            CollectAtMessage collectAtMessage = new CollectAtMessage();
            collectAtMessage.setMsgId(msgAllBean.getAtMessage().getMsgId());
            collectAtMessage.setMsg(msgAllBean.getAtMessage().getMsg());
            return collectAtMessage;
        }
        if (type == ChatEnum.EMessageType.FILE) {
            CollectSendFileMessage collectSendFileMessage = new CollectSendFileMessage();
            collectSendFileMessage.setMsgId(msgAllBean.getSendFileMessage().getMsgId());
            collectSendFileMessage.setFileURL(msgAllBean.getSendFileMessage().getUrl());
            collectSendFileMessage.setFileName(msgAllBean.getSendFileMessage().getFile_name());
            collectSendFileMessage.setFileFormat(msgAllBean.getSendFileMessage().getFormat());
            collectSendFileMessage.setFileSize(msgAllBean.getSendFileMessage().getSize());
            if (!TextUtils.isEmpty(msgAllBean.getSendFileMessage().getLocalPath())) {
                collectSendFileMessage.setCollectLocalPath(msgAllBean.getSendFileMessage().getLocalPath());
            }
            return collectSendFileMessage;
        } else {
            return null;
        }
    }

    /*
     * 发送消息前，需要检测网络连接状态，网络不可用，不能发送
     * 每条消息发送前，需要检测，语音和小视频录制之前，仍需要检测
     * type=0 默认提示 type=1 仅获取断网状态/不提示
     * */
    public boolean checkNetConnectStatus(int type) {
        boolean isOk;
        if (!NetUtil.isNetworkConnected()) {
            if (type == 0) {
                ToastUtil.show(this, "网络连接不可用，请稍后重试");
            }
            isOk = false;
        } else {
            isOk = SocketUtil.getSocketUtil().getOnlineState();
            if (!isOk) {
                if (type == 0) {
                    ToastUtil.show(this, "连接已断开，请稍后再试");
                }
            }
        }
        return isOk;
    }

    public void showBottom(boolean b) {
        if (b) {
            bindingView.ivForward.setEnabled(true);
            bindingView.ivDelete.setEnabled(true);
            bindingView.ivCollection.setEnabled(true);
            bindingView.ivDownload.setEnabled(true);
            bindingView.ivForward.setAlpha(1f);
            bindingView.ivDelete.setAlpha(1f);
            bindingView.ivCollection.setAlpha(1f);
            bindingView.ivDownload.setAlpha(1f);
        } else {
            bindingView.ivForward.setEnabled(false);
            bindingView.ivDelete.setEnabled(false);
            bindingView.ivCollection.setEnabled(false);
            bindingView.ivDownload.setEnabled(false);
            bindingView.ivForward.setAlpha(0.6f);
            bindingView.ivDelete.setAlpha(0.6f);
            bindingView.ivCollection.setAlpha(0.6f);
            bindingView.ivDownload.setAlpha(0.6f);
        }
    }

    private void clearSelectMsg() {
        selectMsg.clear();
    }

    public void switchSelectMode(boolean isOpen) {
        if (isOpen) {
            isSelect = true;
            bindingView.headView.getActionbar().setTxtRight("取消");
            mAdapter.setSelect(isSelect, selectMsg);
            mAdapter.notifyItemRangeChanged(0, listData.size());
            bindingView.llMore.setVisibility(View.VISIBLE);
        } else {
            isSelect = false;
            bindingView.headView.getActionbar().setTxtRight("选择");
            mAdapter.setSelect(isSelect, selectMsg);
            mAdapter.notifyItemRangeChanged(0, listData.size());
            bindingView.llMore.setVisibility(View.GONE);
            clearSelectMsg();
        }
    }

    private void showDeleteDialog(List<MsgAllBean> msgList) {
        if (this.isFinishing()) {
            return;
        }
        DialogCommon dialogDelete = new DialogCommon(this);
        dialogDelete.setTitleAndSure(false, true)
                .setContent("确定删除？", true)
                .setLeft("取消")
                .setRight("删除")
                .setListener(new DialogCommon.IDialogListener() {
                    @Override
                    public void onSure() {
                        if (MyAppLication.INSTANCE().repository != null) {
                            MyAppLication.INSTANCE().repository.deleteMsgList(msgList);
                        }
                        deleteMsgList(msgList);
                    }

                    @Override
                    public void onCancel() {
                    }
                }).show();
    }

    @SuppressLint("CheckResult")
    public void deleteMsgList(List<MsgAllBean> list) {
        listData.removeAll(list);
        int removeSize = list.size();
        count = count - removeSize;
        mAdapter.bindData(listData);
        bindingView.headView.setTitle("图片及视频（" + count + ")");
        switchSelectMode(false);
    }


    /**
     * 显示大图
     *
     * @param msgId
     */
    private void scanImageAndVideo(String msgId) {
        ArrayList<LocalMedia> selectList = new ArrayList<>();
        List<LocalMedia> temp = new ArrayList<>();
        int pos = 0;
        List<MsgAllBean> listdata = msgAction.getMsg4UserImg(gid, toUid);
        for (int i = 0; i < listdata.size(); i++) {
            MsgAllBean msgl = listdata.get(i);
            if (msgId.equals(msgl.getMsg_id())) {
                pos = i;
            }
            LocalMedia lc = new LocalMedia();
            //发送状态正常，则允许收藏 (阅后即焚改为允许收藏)
            if (msgl.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                lc.setCanCollect(true);
            }
            lc.setMsg_id(msgl.getMsg_id());
            if (msgl.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {
                lc.setMimeType(PictureConfig.TYPE_VIDEO);
                String localUrl = msgl.getVideoMessage().getLocalUrl();
                if (StringUtil.isNotNull(localUrl)) {
                    File file = new File(localUrl);
                    if (file.exists()) {
                        lc.setVideoLocalUrl(localUrl);
                    }
                }
                lc.setVideoUrl(msgl.getVideoMessage().getUrl());
                lc.setVideoBgUrl(msgl.getVideoMessage().getBg_url());
                lc.setWidth((int) msgl.getVideoMessage().getWidth());
                lc.setHeight((int) msgl.getVideoMessage().getHeight());
                lc.setDuration(msgl.getVideoMessage().getDuration());
            } else {
                lc.setMimeType(PictureConfig.TYPE_IMAGE);
                lc.setCutPath(msgl.getImage().getThumbnailShow());
                lc.setCompressPath(msgl.getImage().getPreviewShow());
                lc.setPath(msgl.getImage().getOriginShow());
                lc.setSize(msgl.getImage().getSize());
                lc.setWidth(new Long(msgl.getImage().getWidth()).intValue());
                lc.setHeight(new Long(msgl.getImage().getHeight()).intValue());
                lc.setHasRead(msgl.getImage().isReadOrigin());
            }
            temp.add(lc);
        }
        int size = temp.size();
        //取中间100张
        if (size <= 100) {
            selectList.addAll(temp);
        } else {
            if (pos - 50 <= 0) {//取前面
                selectList.addAll(temp.subList(0, 100));
            } else if (pos + 50 >= size) {//取后面
                selectList.addAll(temp.subList(size - 100, size));
            } else {//取中间
                selectList.addAll(temp.subList(pos - 50, pos + 50));
            }
        }
        pos = 0;
        for (int i = 0; i < selectList.size(); i++) {
            if (msgId.equals(selectList.get(i).getMsg_id())) {
                pos = i;
                break;
            }
        }
        Intent intent = new Intent(this, PreviewMediaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", selectList);
        bundle.putInt("position", pos);
        intent.putExtra(PictureConfig.GID, gid);
        intent.putExtra(PictureConfig.TO_UID, toUid);
        intent.putExtra("isFromSelf", true);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void downloadFile(List<MsgAllBean> list, boolean isOverdue) {
        if (list != null && list.size() > 0) {
            showLoadingDialog("下载中...");
            download(list, 0);
        }
    }

    public void download(List<MsgAllBean> list, int position) {
        MsgAllBean bean = list.get(position);
        String downUrl = "";
        File targetFile = null;
        if (bean.getMsg_type() == ChatEnum.EMessageType.IMAGE) {
            if (!TextUtils.isEmpty(bean.getImage().getOrigin())) {
                downUrl = bean.getImage().getOrigin();
            } else {
                downUrl = bean.getImage().getPreview();
            }
            targetFile = getImageFile(downUrl);
        } else if (bean.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {
            downUrl = bean.getVideoMessage().getUrl();
            targetFile = getVideoFile(downUrl);
        }
        if (TextUtils.isEmpty(downUrl) || targetFile == null) {
            return;
        }
        File finalTargetFile = targetFile;
        UpFileAction action = new UpFileAction();
        String finalDownUrl = downUrl;
        File finalTargetFile1 = targetFile;
        ExecutorManager.INSTANCE.getNormalThread().execute(new Runnable() {
            @Override
            public void run() {
//                action.downloadFile(finalDownUrl, PreviewMediaAllActivity2.this, new UpFileUtil.OssUpCallback() {
//                    @Override
//                    public void success(String url) {
//                        LogUtil.getLog().i("DownloadUtil", "下载成功--file=:" + url);
//                        if (position != list.size() - 1) {
//                            download(list, position + 1);
//                        } else {
//                            //下载完成
//                            ThreadUtil.getInstance().runMainThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    dismissLoadingDialog();
//                                    ToastUtil.show("下载完成");
//                                    switchSelectMode(false);
//                                }
//                            });
//                        }
//                        msgDao.fixVideoLocalUrl(bean.getMsg_id(), finalTargetFile.getAbsolutePath());
//                        scanFile(getContext(), finalTargetFile.getAbsolutePath());
//                    }
//
//                    @Override
//                    public void fail() {
//                        LogUtil.getLog().i("DownloadUtil", "Exception下载失败:");
//                        if (position != list.size() - 1) {
//                            download(list, position + 1);
//                        } else {
//                            //下载完成
//                            ToastUtil.show("下载完成");
//                        }
//                    }
//
//                    @Override
//                    public void inProgress(long progress, long total) {
//
//                    }
//                }, finalTargetFile1);

                DownloadUtil.get().downLoadFile(finalDownUrl, finalTargetFile1, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        if (file == null) {
                            LogUtil.getLog().i("DownloadUtil", "下载成功--file=:" + null);
                            return;
                        }
                        LogUtil.getLog().i("DownloadUtil", "下载成功--file=:" + file.getAbsolutePath());
                        if (position != list.size() - 1) {
                            download(list, position + 1);
                        } else {
                            //下载完成
                            ToastUtil.show("下载完成");
                        }
                        msgDao.fixVideoLocalUrl(bean.getMsg_id(), finalTargetFile.getAbsolutePath());
                        scanFile(getContext(), finalTargetFile.getAbsolutePath());

                    }

                    @Override
                    public void onDownloading(int progress) {
//                LogUtil.getLog().i("DownloadUtil", "progress:" + progress);
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        LogUtil.getLog().i("DownloadUtil", "Exception下载失败:" + e.getMessage());
                        if (position != list.size() - 1) {
                            download(list, position + 1);
                        } else {
                            //下载完成
                            ToastUtil.show("下载完成");
                        }
                    }
                });
            }
        });


    }

    private File getVideoFile(String url) {
        final File appDir = new File(Environment.getExternalStorageDirectory() + "/" + APP_NAME + "/Mp4/");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        final String fileName = MyDiskCache.getFileNmae(url) + ".mp4";
        return new File(appDir, fileName);
    }

    public File getImageFile(String url) {
        final String filePath = FileManager.getInstance().getImageCachePath();
//        final String filePath = getExternalCacheDir().getAbsolutePath() + "/Image/";
        final String fileName = url.substring(url.lastIndexOf("/") + 1);
        return new File(filePath + "/" + fileName);//原图保存路径
    }

    public void scanFile(Context context, String filePath) {
        try {
            MediaScannerConnection.scanFile(context, new String[]{filePath}, new String[]{"video/mp4"},
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
