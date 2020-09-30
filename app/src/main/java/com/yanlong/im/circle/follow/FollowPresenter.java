package com.yanlong.im.circle.follow;

import android.content.Context;

import com.luck.picture.lib.PictureEnum;
import com.yanlong.im.circle.adapter.CircleFlowAdapter;
import com.yanlong.im.circle.bean.CircleCommentBean;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.base.bind.BasePresenter;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;

import java.util.ArrayList;
import java.util.List;
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
                if (response.code() == 200) {
                    List<MessageFlowItemBean> flowList = new ArrayList<>();
                    if (response.body() != null && response.body().getData() != null) {
                        for (MessageInfoBean messageInfoBean : response.body().getData()) {
                            flowList.add(createFlowItemBean(messageInfoBean));
                        }
                    }
                    mView.onSuccess(flowList);
                } else {
                    mView.onShowMessage(response.message());
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<MessageInfoBean>>> call, Throwable t) {
                super.onFailure(call, t);
                mView.onShowMessage("刷新失败");
            }
        });
    }

    /**
     * 获取推荐列表
     *
     * @param nextId   页码
     * @param pageSize 页数
     */
    public void getRecommendMomentList(int nextId, int pageSize) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("nextId", nextId);
        params.put("pageSize", pageSize);
        mModel.getRecommendList(params, new CallBack<ReturnBean<List<MessageInfoBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<MessageInfoBean>>> call, Response<ReturnBean<List<MessageInfoBean>>> response) {
                super.onResponse(call, response);
                if (response.code() == 200) {
                    List<MessageFlowItemBean> flowList = new ArrayList<>();
                    if (response.body() != null && response.body().getData() != null) {
                        for (MessageInfoBean messageInfoBean : response.body().getData()) {
                            flowList.add(createFlowItemBean(messageInfoBean));
                        }
                    }
                    mView.onSuccess(flowList);
                } else {
                    mView.onShowMessage(response.message());
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<MessageInfoBean>>> call, Throwable t) {
                super.onFailure(call, t);
                mView.onShowMessage("刷新失败");
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
                if (response.code() == 200) {
                    if (response.body() != null && response.body().getData() != null) {
                        mView.onSuccess(position, createFlowItemBean(response.body().getData()));
                    }
                } else {
                    mView.onShowMessage(response.message());
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
        if (messageInfoBean != null) {
            switch (messageInfoBean.getType()) {
                case PictureEnum.EContentType.VOTE:
                case PictureEnum.EContentType.PICTRUE_AND_VOTE:
                case PictureEnum.EContentType.VOICE_AND_VOTE:
                case PictureEnum.EContentType.VIDEO_AND_VOTE:
                case PictureEnum.EContentType.PICTRUE_AND_VIDEO_VOTE:
                    flowItemBean = new MessageFlowItemBean(CircleFlowAdapter.MESSAGE_VOTE, messageInfoBean);
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
                if (response.code() == 200) {
                    mView.onVoteSuccess(parentPostion, response.message());
                } else {
                    mView.onShowMessage(response.message());
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
                if (response.code() == 200) {
                    mView.onLikeSuccess(postion, response.message());
                } else {
                    mView.onShowMessage(response.message());
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
                if (response.code() == 200) {
                    mView.onLikeSuccess(postion, response.message());
                } else {
                    mView.onShowMessage(response.message());
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
                if (response.code() == 200) {
                    mView.onSuccess(postion, response.message());
                } else {
                    mView.onShowMessage(response.message());
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
                if (response.code() == 200) {
                    mView.onSuccess(postion, response.message());
                } else {
                    mView.onShowMessage(response.message());
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
                if (response.code() == 200) {
                    mView.onSuccess(0, response.message());
                } else {
                    mView.onShowMessage(response.message());
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
     */
    public void circleCommentList(int currentPage, int pageSize, Long momentId, Long momentUid, int myLikeStat) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("currentPage", currentPage);
        params.put("pageSize", pageSize);
        params.put("momentId", momentId);
        params.put("momentUid", momentUid);
        params.put("myLikeStat", myLikeStat);
        mModel.circleCommentList(params, new CallBack<ReturnBean<List<CircleCommentBean>>>() {
            @Override
            public void onResponse(Call<ReturnBean<List<CircleCommentBean>>> call, Response<ReturnBean<List<CircleCommentBean>>> response) {
                super.onResponse(call, response);
                if (response.code() == 200) {
                    mView.onCommentSuccess(response.body().getData());
                } else {
                    mView.onShowMessage(response.message());
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<List<CircleCommentBean>>> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }
}
