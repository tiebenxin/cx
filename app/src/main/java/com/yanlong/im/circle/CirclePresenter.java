package com.yanlong.im.circle;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.event.EventFactory;
import com.yanlong.im.circle.adapter.CircleFlowAdapter;
import com.yanlong.im.circle.bean.CircleTitleBean;
import com.yanlong.im.circle.bean.MessageFlowItemBean;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.bean.VoteBean;
import com.yanlong.im.circle.follow.FollowFragment;
import com.yanlong.im.circle.recommend.RecommendFragment;

import net.cb.cb.library.base.bind.BasePresenter;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.utils.UpLoadFileUtil;

import java.util.ArrayList;
import java.util.HashMap;
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
public class CirclePresenter extends BasePresenter<CircleModel, CircleView> {

    private List<CircleTitleBean> mListTitle = new ArrayList<>();
    private List<Fragment> mListFragments = new ArrayList<>();
    private final String FILE_NAME = ".jpg";
    private final String FILE_NAME_GIF = ".gif";
    private final String FILE_DIRECTORY = "image/";
    private HashMap<String, String> mNetFile = new HashMap<>();

    CirclePresenter(Context context) {
        super(context);
        init();
    }

    @Override
    public CircleModel bindModel() {
        return new CircleModel();
    }

    public void init() {
        if (mListTitle != null) {
            mListTitle.clear();
        }
        mListTitle.add(new CircleTitleBean("推荐", true));
        mListTitle.add(new CircleTitleBean("关注", false));

        if (mListFragments != null) {
            mListFragments.clear();
        }
        mListFragments.add(new RecommendFragment());
        mListFragments.add(new FollowFragment());
    }

    public List<Fragment> getListFragment() {
        if (mListFragments == null) {
            mListFragments = new ArrayList<>();
            init();
        }
        return mListFragments;
    }

    public void setParams(EventFactory.CreateCircleEvent.CircleBean circleBean) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("content", circleBean.getContent());
        params.put("type", circleBean.getType());
        params.put("visibility", circleBean.getVisibility());
        if (!TextUtils.isEmpty(circleBean.getPosition())) {
            params.put("position", circleBean.getPosition());
            params.put("latitude", circleBean.getLatitude());
            params.put("longitude", circleBean.getLongitude());
            params.put("city", circleBean.getCity());
        }
        if (!TextUtils.isEmpty(circleBean.getAttachment())) {
            params.put("attachment", circleBean.getAttachment());
        }
        if (!TextUtils.isEmpty(circleBean.getVote())) {
            List<CircleTitleBean> list = new Gson().fromJson(circleBean.getVote(),
                    new TypeToken<List<CircleTitleBean>>() {
                    }.getType());
            VoteBean voteBean = new VoteBean();
            List<VoteBean.Item> votes = new ArrayList<>();
            if (voteIsPictrue(circleBean.getVote())) {
                voteBean.setType(2);
            } else {
                voteBean.setType(1);
            }
            for (CircleTitleBean circleTitleBean : list) {
                votes.add(new VoteBean.Item(circleTitleBean.getContent(), circleTitleBean.getSize()));
            }
            voteBean.setItems(votes);
            params.put("vote", new Gson().toJson(voteBean));
        }

        createNewCircle(params);
    }

    public boolean voteIsPictrue(String voteJson) {
        List<CircleTitleBean> list = new Gson().fromJson(voteJson,
                new TypeToken<List<CircleTitleBean>>() {
                }.getType());
        boolean isImg = false;
        if (list != null && list.size() > 0) {
            if (list.get(0).getContent().contains(".jpg")) {
                isImg = true;
            }
        }
        return isImg;
    }

    /**
     * 创建发布动态的接口
     *
     * @param params 入参
     */
    public void createNewCircle(WeakHashMap<String, Object> params) {
        mModel.createNewCircle(params, new CallBack<ReturnBean<MessageInfoBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<MessageInfoBean>> call, Response<ReturnBean<MessageInfoBean>> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    mView.onSuccess(createFlowItemBean(response.body().getData()));
                } else {
                    mView.showMessage(getFailMessage(response.body()));
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<MessageInfoBean>> call, Throwable t) {
                super.onFailure(call, t);
                mView.showMessage("发布动态失败");
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
     * 上传文件
     *
     * @param file    文件路径
     * @param type    类型
     * @param isVideo 是否是视频  视频用到
     */
    public void uploadFile(String file, int type, boolean isVideo, UpFileAction.PATH path) {
        new UpFileAction().upFile(path, mContext, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                mView.uploadSuccess(url, type, isVideo, null);
            }

            @Override
            public void fail() {
                mView.showMessage("上传失败");
            }

            @Override
            public void inProgress(long progress, long zong) {
            }
        }, file);
    }

    /**
     * 批量上传文件
     *
     * @param type
     * @param mediaList 集合
     */
    public void batchUploadFile(int type, List<LocalMedia> mediaList) {
        UpLoadFileUtil.getInstance().upLoadFile(mContext, mediaList, true,
                UpFileAction.PATH.COMENT_IMG, new UpLoadFileUtil.OnUploadFileListener() {
                    @Override
                    public void onUploadFile(HashMap<String, String> netFile) {
                        if (mNetFile.size() > 0) {
                            netFile.putAll(mNetFile);
                        }
                        mView.uploadSuccess("", type, false, netFile);

                    }

                    @Override
                    public void onFail() {
                        mView.showMessage("图片上传失败");
                    }
                });
    }

    /**
     * 文件批量检查
     *
     * @param mediaList 图片集合
     */
//    public void batchFileCheck(List<LocalMedia> mediaList) {
//        mNetFile.clear();
//        // 获取文件的md5值，用于判断文件是否上传过
//        RxJavaUtil.run(new RxJavaUtil.OnRxAndroidListener<ArrayList<FileBean>>() {
//
//            @Override
//            public ArrayList<FileBean> doInBackground() throws Throwable {
//                ArrayList<FileBean> fileBeans = new ArrayList<>();
//                for (LocalMedia localMedia : mediaList) {
//                    FileBean fileBean = new FileBean();
//                    String md5 = Md5Util.getFileMD5(new File(localMedia.getPath()));
//                    fileBean.setMd5(md5);
//                    if (localMedia.getPath().endsWith(FILE_NAME_GIF)) {
//                        fileBean.setUrl(FILE_DIRECTORY + md5 + FILE_NAME_GIF);
//                    } else {
//                        fileBean.setUrl(FILE_DIRECTORY + md5 + FILE_NAME);
//                    }
//
//                    fileBean.setLocationPath(localMedia.getPath());
//                    fileBeans.add(fileBean);
//                }
//
//                return fileBeans;// 获取文件MD5唯一值
//            }
//
//            @Override
//            public void onFinish(final ArrayList<FileBean> fileBeans) {
//                UpFileUtil.getInstance().batchFileCheck(fileBeans, new CallBack<ReturnBean<List<String>>>() {
//                    @Override
//                    public void onResponse(Call<ReturnBean<List<String>>> call, Response<ReturnBean<List<String>>> response) {
//                        super.onResponse(call, response);
//                        if (response.body() != null && response.body().isOk()) {
//                            if (response.body().getData() != null && response.body().getData().size() > 0) {
//                                List<String> list = response.body().getData();
//                                for (String md5 : list) {
//                                    for (FileBean fileBean : fileBeans) {
//                                        if (md5.equals(fileBean.getMd5()) && mediaList.size() > 0) {
//                                            for (int i = mediaList.size() - 1; i >= 0; i--) {
//                                                LocalMedia localMedia = mediaList.get(i);
//                                                if (fileBean.getLocationPath().equals(localMedia.getPath())) {
//                                                    // 自己拼接图片地址，已经上传过
//                                                    mNetFile.put(localMedia.getPath(), Constants.OSS_REALM_NAME + fileBean.getUrl());
//                                                    mediaList.remove(i);
//                                                    break;
//                                                }
//                                            }
//                                            break;
//                                        }
//                                    }
//                                }
//                                // 继续上传没有上传过的文件
//                                if (mediaList.size() > 0) {
//                                    batchUploadFile(PictureEnum.EContentType.PICTRUE, mediaList);
//                                } else if (mNetFile.size() > 0) {
//                                    mView.uploadSuccess("", PictureEnum.EContentType.PICTRUE, false, mNetFile);
//                                }
//
//                            } else {
//                                batchUploadFile(PictureEnum.EContentType.PICTRUE, mediaList);
//                            }
//                        } else {
//                            batchUploadFile(PictureEnum.EContentType.PICTRUE, mediaList);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ReturnBean<List<String>>> call, Throwable t) {
//                        super.onFailure(call, t);
//                        batchUploadFile(PictureEnum.EContentType.PICTRUE, mediaList);
//                    }
//                });
//            }
//
//            @Override
//            public void onError(Throwable e) {
//            }
//        });
//    }

    /**
     * 获取是否有红点
     */
    public void latestData() {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        mModel.latestData(params, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (checkSuccess(response.body())) {
                    try {
                        LinkedTreeMap<String, Double> hashMap = (LinkedTreeMap<String, Double>) response.body().getData();
                        mView.showRedDot(hashMap.get("redPoint").intValue());
                    } catch (Exception e) {
                        mView.showRedDot(0);
                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }
}
