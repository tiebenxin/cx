package com.yanlong.im.circle.follow;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.event.EventFactory;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.circle.adapter.CircleFlowAdapter;
import com.yanlong.im.circle.bean.CircleCommentBean;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.recommend.RecommendFragment;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.base.bind.BasePresenter;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.FileCacheUtil;
import net.cb.cb.library.utils.SpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

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
public class FollowPresenter extends BasePresenter<FollowModel, FollowView> {

    private UserDao userDao = new UserDao();
    private MsgDao msgDao = new MsgDao();
    /***
     * 统一处理mkname
     */
    private Map<Long, UserInfo> userMap = new HashMap<>();

    public FollowPresenter(Context context) {
        super(context);
    }

    @Override
    public FollowModel bindModel() {
        return new FollowModel();
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
     * 获取关注列表
     *
     * @param currentPage 页码
     * @param pageSize    页数
     */
    public void getFollowMomentList(int currentPage, int pageSize) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("currentPage", currentPage);
        params.put("pageSize", pageSize);
        mModel.getFollowMomentList(params, new CallBack<ReturnBean<List<MessageInfoBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<MessageInfoBean>>> call, Response<ReturnBean<List<MessageInfoBean>>> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    List<MessageFlowItemBean> flowList = new ArrayList<>();
                    if (response.body() != null && response.body().getData() != null) {
                        for (MessageInfoBean messageInfoBean : response.body().getData()) {
                            resetName(messageInfoBean);
                            flowList.add(createFlowItemBean(messageInfoBean));
                        }
                    }
                    mView.onSuccess(flowList);
                    if (currentPage == 1) {// 添加缓存
                        FileCacheUtil.putFirstPageCache(UserAction.getMyId() + "getFollowMomentList",
                                new Gson().toJson(response.body().getData()));
                    }
                } else {
                    mView.onShowMessage(getFailMessage(response.body()));
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<MessageInfoBean>>> call, Throwable t) {
                super.onFailure(call, t);
                if (currentPage == 1) {
                    String content = FileCacheUtil.getFirstPageCache(UserAction.getMyId() + "getFollowMomentList");
                    if (!TextUtils.isEmpty(content)) {
                        List<MessageInfoBean> infoList = new Gson().fromJson(content,
                                new TypeToken<List<MessageInfoBean>>() {
                                }.getType());
                        List<MessageFlowItemBean> flowList = new ArrayList<>();
                        for (MessageInfoBean messageInfoBean : infoList) {
                            flowList.add(createFlowItemBean(messageInfoBean));
                        }
                        mView.onSuccess(flowList);
                    }
                }
                mView.onShowMessage("刷新失败");
            }
        });
    }

    private void resetName(MessageInfoBean bean) {
        if (bean.getUid() == null) {
            return;
        }
        UserInfo userInfo;
        if (userMap.containsKey(bean.getUid())) {
            userInfo = userMap.get(bean.getUid());
            if (!TextUtils.isEmpty(userInfo.getMkName())) {
                bean.setNickname(userInfo.getMkName());
            }
        } else {
            userInfo = userDao.findUserInfo(bean.getUid());
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getMkName())) {
                bean.setNickname(userInfo.getMkName());
                userMap.put(bean.getUid(), userInfo);
            }
        }
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

    private MessageFlowItemBean createFlowItemBean(MessageInfoBean messageInfoBean) {
        MessageFlowItemBean flowItemBean = null;
        if (messageInfoBean != null && messageInfoBean.getType() != null) {
            switch (messageInfoBean.getType()) {
                case PictureEnum.EContentType.VOTE:
                case PictureEnum.EContentType.PICTRUE_AND_VOTE:
                case PictureEnum.EContentType.VOICE_AND_VOTE:
                case PictureEnum.EContentType.VIDEO_AND_VOTE:
//                case PictureEnum.EContentType.PICTRUE_AND_VIDEO_VOTE:
                    flowItemBean = new MessageFlowItemBean(CircleFlowAdapter.MESSAGE_VOTE, messageInfoBean);
                    break;
                case PictureEnum.EContentType.VIDEO:
                case PictureEnum.EContentType.VIDEO_AND_PICTRUE:
                case PictureEnum.EContentType.PICTRUE_AND_VIDEO_VOTE:
                    flowItemBean = new MessageFlowItemBean(CircleFlowAdapter.MESSAGE_VIDEO, messageInfoBean);
                    break;
                default:
                    flowItemBean = new MessageFlowItemBean(CircleFlowAdapter.MESSAGE_DEFAULT, messageInfoBean);
                    break;
            }
        }
        return flowItemBean;
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
                    mView.onSuccess(postion, false, response.message());
                    //关注单个用户，推荐列表及时更新
                    EventFactory.UpdateFollowStateEvent event = new EventFactory.UpdateFollowStateEvent();
                    event.type = 1;
                    event.uid = followId.longValue();
                    EventBus.getDefault().post(event);
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
                    mView.onSuccess(postion, true, response.message());
                    //取消关注单个用户，推荐列表及时更新
                    EventFactory.UpdateFollowStateEvent event = new EventFactory.UpdateFollowStateEvent();
                    event.type = 0;
                    event.uid = followId.longValue();
                    EventBus.getDefault().post(event);
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
     * 评论
     *
     * @param content   评论|回复内容
     * @param momentId  说说ID
     * @param momentUid 说说发布者
     * @param replyUid  被回复人(评论时传0|回复时传被回复人UID)
     */
    public void circleComment(String content, Long momentId, Long momentUid, Long replyUid) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("content", content);
        params.put("momentId", momentId);
        params.put("momentUid", momentUid);
        params.put("replyUid", replyUid);
        mModel.circleComment(params, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    mView.onCommentSuccess(true);
                } else {
                    mView.onShowMessage(getFailMessage(response.body()));
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                mView.onShowMessage("发送失败");
            }
        });
    }

    /**
     * 评论列表
     *
     * @param currentPage 页码
     * @param pageSize    页数
     * @param momentId    说说ID
     * @param momentUid   说说发布者
     * @param myLikeStat  该条说说是我发布的，是否获取我对该条说说的点赞状态(0或不传不返回,1返回)
     * @param addBrowse   第一页时，是否需要添加浏览量(0:否,1:是)
     * @param position    点击广场哪一项哪项跳进的详情
     */
    public void circleCommentList(int currentPage, int pageSize, Long momentId, Long momentUid, int myLikeStat, int addBrowse, int position) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("currentPage", currentPage);
        params.put("pageSize", pageSize);
        params.put("momentId", momentId);
        params.put("momentUid", momentUid);
        params.put("myLikeStat", myLikeStat);
        params.put("addBrowse", addBrowse);
        mModel.circleCommentList(params, new CallBack<ReturnBean<CircleCommentBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<CircleCommentBean>> call, Response<ReturnBean<CircleCommentBean>> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    mView.onCommentSuccess(response.body().getData());
                } else {
                    mView.onShowMessage(getFailMessage(response.body()));
                    if (response.body()!=null && response.body().getCode()!=null && response.body().getCode().longValue()== 100104) {
                        EventFactory.DeleteItemTrend event = new EventFactory.DeleteItemTrend();
                        event.position = position;
                        EventBus.getDefault().post(event);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<CircleCommentBean>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    /**
     * 删除评论
     *
     * @param id        评论ID
     * @param momentId  说说ID
     * @param momentUid 说说发布者
     * @param postion   位置
     */
    public void delComment(Long id, Long momentId, Long momentUid, int postion) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("id", id);
        params.put("momentId", momentId);
        params.put("momentUid", momentUid);
        mModel.delComment(params, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    mView.onCommentSuccess(false);
                } else {
                    mView.onShowMessage(getFailMessage(response.body()));
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                mView.onShowMessage("删除失败");
            }
        });
    }

    /**
     * 删除说说
     *
     * @param momentId 说说ID
     */
    public void circleDelete(Long momentId) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("momentId", momentId);
        mModel.circleDelete(params, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    // 是刚发的则删除缓存
                    SpUtil spUtil = SpUtil.getSpUtil();
                    String value = spUtil.getSPValue(RecommendFragment.REFRESH_COUNT, "");
                    if (!TextUtils.isEmpty(value)) {
                        MessageInfoBean infoBean = new Gson().fromJson(value, MessageInfoBean.class);
                        if (momentId.intValue() == infoBean.getId().intValue()) {
                            spUtil.putSPValue(RecommendFragment.REFRESH_COUNT, "");
                            EventBus.getDefault().post(new EventFactory.RefreshRecomendEvent());
                            if (mContext != null) {
                                ((Activity) mContext).finish();
                            }
                        }
                    }
                } else {
                    mView.onShowMessage(getFailMessage(response.body()));
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                mView.onShowMessage("删除失败");
            }
        });
    }

    /**
     * 顶部未读消息悬浮
     */
    public void getUnreadMsg() {
        //是否有未读互动消息
        if (msgDao.getUnreadMsgList() != null && msgDao.getUnreadMsgList().size() > 0) {
            String avatar = "";
            int size = msgDao.getUnreadMsgList().size();
            if (msgDao.getUnreadMsgList().get(0) != null) {
                if (!TextUtils.isEmpty(msgDao.getUnreadMsgList().get(0).getAvatar())) {
                    avatar = msgDao.getUnreadMsgList().get(0).getAvatar();
                }
            }
            mView.showUnreadMsg(size, avatar);
        } else {
            mView.showUnreadMsg(0, "");
        }
    }
}
