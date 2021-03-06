package com.yanlong.im.circle.recommend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureEnum;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.circle.adapter.CircleFlowAdapter;
import com.yanlong.im.circle.bean.InteractMessage;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.base.bind.BasePresenter;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.FileCacheUtil;
import net.cb.cb.library.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020/9/7 0007
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class RecommendPresenter extends BasePresenter<RecommendModel, RecommendView> {

    private UserDao userDao = new UserDao();
    private MsgDao msgDao = new MsgDao();

    RecommendPresenter(Context context) {
        super(context);
    }

    @Override
    public RecommendModel bindModel() {
        return new RecommendModel();
    }

    public MessageFlowItemBean createFlowItemBean(MessageInfoBean messageInfoBean) {
        MessageFlowItemBean flowItemBean = null;
        if (messageInfoBean != null) {
            switch (messageInfoBean.getType()) {
                case PictureEnum.EContentType.VOTE:
                case PictureEnum.EContentType.PICTRUE_AND_VOTE:
                case PictureEnum.EContentType.VOICE_AND_VOTE:
                case PictureEnum.EContentType.VIDEO_AND_VOTE:
                case PictureEnum.EContentType.PICTRUE_AND_VIDEO_VOTE:
                    flowItemBean = new MessageFlowItemBean(CircleFlowAdapter.MESSAGE_VOTE, messageInfoBean, System.currentTimeMillis());
                    break;
                default:
                    flowItemBean = new MessageFlowItemBean(CircleFlowAdapter.MESSAGE_DEFAULT, messageInfoBean, System.currentTimeMillis());
                    break;
            }
        }
        return flowItemBean;
    }

    /**
     * 判断用户是否为好友
     *
     * @param uid
     * @return 0 是好友 1 不是
     */
    public int getUserType(Long uid) {
        // 用户类型 0:陌生人或者群友,1:自己,2:通讯录,3黑名单,4小助手
        int type = userDao.findUserInfo4Friend(uid) == null ? 1 : 0;
        return type;
    }

    /**
     * 获取推荐列表
     *
     * @param nextId      页码
     * @param pageSize    页数
     * @param serviceType 业务类型(0:向下拉取|1:向上拉取)
     */
    public void getRecommendMomentList(Long nextId, int pageSize, int serviceType) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("nextId", nextId);
        params.put("pageSize", pageSize);
        params.put("gender", UserAction.getMyInfo().getSex());// 性别(0:未知|1:男|2:女)
        params.put("serviceType", serviceType);
        mModel.getRecommendList(params, new CallBack<ReturnBean<List<MessageInfoBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<MessageInfoBean>>> call, Response<ReturnBean<List<MessageInfoBean>>> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    List<MessageFlowItemBean> flowList = new ArrayList<>();
                    if (response.body() != null && response.body().getData() != null) {
                        for (MessageInfoBean messageInfoBean : response.body().getData()) {
                            flowList.add(createFlowItemBean(messageInfoBean));
                        }
                    }
                    mView.onSuccess(flowList, serviceType);
                    if (serviceType == 0) {// 添加缓存
                        FileCacheUtil.putFirstPageCache(UserAction.getMyId() + "getRecommendMomentList",
                                new Gson().toJson(response.body().getData()));
                    }
                } else {
                    if (response.body() != null) {
                        mView.onShowMessage(response.body().getMsg());
                    } else {
                        mView.onShowMessage("获取推荐列表失败");
                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<MessageInfoBean>>> call, Throwable t) {
                super.onFailure(call, t);
                if (serviceType == 0) {
                    String content = FileCacheUtil.getFirstPageCache(UserAction.getMyId() + "getRecommendMomentList");
                    if (!TextUtils.isEmpty(content)) {
                        List<MessageInfoBean> infoList = new Gson().fromJson(content,
                                new TypeToken<List<MessageInfoBean>>() {
                                }.getType());
                        List<MessageFlowItemBean> flowList = new ArrayList<>();
                        for (MessageInfoBean messageInfoBean : infoList) {
                            flowList.add(createFlowItemBean(messageInfoBean));
                        }
                        mView.onSuccess(flowList, serviceType);
                    }
                }
                mView.onShowMessage("刷新失败");
            }
        });
    }

    /**
     * 点赞
     *
     * @param momentId  说说ID
     * @param momentUid 说说发布者
     * @param postion   父类位置
     */
    public void comentLike(Long momentId, Long momentUid, int postion) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("momentId", momentId);
        params.put("momentUid", momentUid);
        mModel.comentLike(params, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    mView.onLikeSuccess(postion, response.message());
                } else {
                    mView.onShowMessage(getFailMessage(response.body()));
                    //如果动态已经被删除，则通知刷新
                    if (response.body() != null && response.body().getCode() == 100104) {
                        mView.onDeleteItem(postion);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                mView.onShowMessage("刷新失败");
            }
        });
    }

    /**
     * 取消点赞
     *
     * @param momentId  说说ID
     * @param momentUid 说说发布者
     * @param postion   父类位置
     */
    public void comentCancleLike(Long momentId, Long momentUid, int postion) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("momentId", momentId);
        params.put("momentUid", momentUid);
        mModel.comentCancleLike(params, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    mView.onLikeSuccess(postion, response.message());
                } else {
                    mView.onShowMessage(getFailMessage(response.body()));
                    //如果动态已经被删除，则通知刷新
                    if (response.body().getCode() == 100104) {
                        mView.onDeleteItem(postion);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                mView.onShowMessage("刷新失败");
            }
        });
    }

    /**
     * 关注
     *
     * @param followId 关注人UID
     * @param postion  位置
     */
    public void followAdd(Long followId, int postion) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("followId", followId);
        mModel.followAdd(params, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    mView.onSuccess(postion, true, response.message());
                } else {
                    mView.onShowMessage(getFailMessage(response.body()));
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                mView.onShowMessage("关注失败");
            }
        });
    }

    /**
     * 取消关注
     *
     * @param followId 关注人UID
     * @param postion  位置
     */
    public void followCancle(Long followId, int postion) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("followId", followId);
        mModel.followCancle(params, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    mView.onSuccess(postion, false, response.message());
                } else {
                    mView.onShowMessage(getFailMessage(response.body()));
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                mView.onShowMessage("取消关注失败");
            }
        });
    }

    /**
     * 添加我不看的人
     *
     * @param forbidUid 不看的人UID
     */
    public void addSee(Long forbidUid) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("forbidUid", forbidUid);
        mModel.addSee(params, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
//                    mView.addSeeSuccess(response.message());
                    filterData(forbidUid);
                } else {
                    mView.onShowMessage(getFailMessage(response.body()));
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                mView.onShowMessage("设置失败");
            }
        });
    }

    /**
     * 获取单条朋友圈
     *
     * @param momentId  说说ID
     * @param momentUid 说说发布者
     * @param position  位置
     */
    public void queryById(Long momentId, Long momentUid, int position) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("momentId", momentId);
        params.put("momentUid", momentUid);
        mModel.queryById(params, new CallBack<ReturnBean<MessageInfoBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<MessageInfoBean>> call, Response<ReturnBean<MessageInfoBean>> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    if (response.body() != null && response.body().getData() != null) {
                        mView.onSuccess(position, createFlowItemBean(response.body().getData()));
                    }
                } else {
                    mView.onShowMessage(getFailMessage(response.body()));
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<MessageInfoBean>> call, Throwable t) {
                super.onFailure(call, t);
                mView.onShowMessage("刷新失败");
            }
        });
    }

    /**
     * 投票接口
     *
     * @param itemId 投票选项ID，1-4
     * @param vid    说说ID
     * @param vUid   投票发布者
     */
    public void voteAnswer(int itemId, int parentPostion, Long vid, Long vUid) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("itemId", itemId);
        params.put("vid", vid);
        params.put("vUid", vUid);
        mModel.voteAnswer(params, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    mView.onVoteSuccess(parentPostion, response.message());
                } else {
                    mView.onShowMessage(getFailMessage(response.body()));
                    //如果动态已经被删除，则通知刷新
                    if (response.body().getCode() == 100104) {
                        mView.onDeleteItem(parentPostion);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                mView.onShowMessage("刷新失败");
            }
        });
    }


    /**
     * 顶部未读消息悬浮
     */
    public void getUnreadMsg() {
        List<InteractMessage> list = msgDao.getUnreadMsgList();
        //是否有未读互动消息
        if (list != null && list.size() > 0) {
            String avatar = "";
            int size = list.size();
            if (list.get(0) != null) {
                if (!TextUtils.isEmpty(list.get(0).getAvatar())) {
                    avatar = list.get(0).getAvatar();
                }
            }
            mView.showUnreadMsg(size, avatar);
        } else {
            mView.showUnreadMsg(0, "");
        }
    }

    //过滤不看TA用户的数据
    @SuppressLint("CheckResult")
    private void filterData(long uid) {
        Observable.just(0)
                .map(new Function<Integer, List<MessageFlowItemBean>>() {
                    @Override
                    public List<MessageFlowItemBean> apply(Integer integer) throws Exception {
                        List<MessageFlowItemBean> data = mModel.getData();
                        List<MessageFlowItemBean> temp = new ArrayList<>();
                        if (data != null) {
                            for (MessageFlowItemBean bean : data) {
                                MessageInfoBean infoBean = (MessageInfoBean) bean.getData();
                                if (infoBean.getUid() != null && infoBean.getUid().longValue() != uid) {
                                    temp.add(bean);
                                }
                            }
                        }
                        return temp;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<MessageFlowItemBean>>empty())
                .subscribe(new Consumer<List<MessageFlowItemBean>>() {
                    @Override
                    public void accept(List<MessageFlowItemBean> list) throws Exception {
                        mView.onSuccess(list, -1);
                    }
                });
    }

}
