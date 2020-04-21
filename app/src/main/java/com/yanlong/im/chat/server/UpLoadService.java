package com.yanlong.im.chat.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.SendFileMessage;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.bean.VideoUploadBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.bean.EventUpFileLoadEvent;
import net.cb.cb.library.bean.EventUpImgLoadEvent;
import net.cb.cb.library.bean.EventUploadImg;
import net.cb.cb.library.utils.ImgSizeUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


public class UpLoadService extends Service {
    public static Queue<UpProgress> queue = new LinkedList<>();
    public static HashMap<String, Integer> pgms = new HashMap<>();
    private UpFileAction upFileAction = new UpFileAction();

    private static String netBgUrl;
    private static long oldUptime = 0;
    private static MsgDao msgDao = new MsgDao();
    private static String TAG = UpLoadService.class.getName();
    // 用于视屏重发
    public static Map<String, VideoUploadBean> mVideoMaps = new ConcurrentHashMap<>();
    // 重发次数
    private static int SEND_MAX_NUM = 3;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public static Integer getProgress(String msgId) {
        if (pgms.containsKey(msgId)) {
            int pg = pgms.get(msgId);
            return pg;
        }

        return null;
    }

    private static void updateProgress(String msgId, Integer pg) {
        Integer progress = pgms.get(msgId);
        if (progress == null || pg > progress) {
            pgms.put(msgId, pg);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (queue.size() > 0) {
                    UpProgress upProgress = queue.poll();
                    LogUtil.getLog().d("ChatActivityTemp--上传", "上传: " + upProgress.getId());
                    upFileAction.upFileSyn(UpFileAction.PATH.IMG, getApplicationContext(), upProgress.getCallback(), upProgress.getFile());
                }
                stopSelf();
                LogUtil.getLog().d("ChatActivityTemp-上传", "上传结束");
            }
        }).start();

    }

//    @Deprecated
//    public static void onAddImage(final String id, String file, final Boolean isOriginal, final Long toUId, final String toGid, final long time) {
//        final UpProgress upProgress = new UpProgress();
//        upProgress.setId(id);
//        upProgress.setFile(file);
//        updateProgress(id, new Random().nextInt(5) + 1);//发送图片后默认给个进度，显示阴影表示正在上传
//        final ImgSizeUtil.ImageSize img = ImgSizeUtil.getAttribute(file);
//        upProgress.setCallback(new UpFileUtil.OssUpCallback() {
//
//            @Override
//            public void success(final String url) {
//                EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
//                updateProgress(id, 100);
//                eventUpImgLoadEvent.setMsgid(id);
//                eventUpImgLoadEvent.setState(1);
//                eventUpImgLoadEvent.setUrl(url);
//                eventUpImgLoadEvent.setOriginal(isOriginal);
//                Object msgbean = SocketData.send4Image(id, toUId, toGid, url, isOriginal, img, time);
//
//                eventUpImgLoadEvent.setMsgAllBean(msgbean);
//                EventBus.getDefault().post(eventUpImgLoadEvent);
//
//            }
//
//            @Override
//            public void fail() {
//                EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
//                //  LogUtil.getLog().d("tag", "fail : ===============>"+id);
//
//                System.out.println(UpLoadService.class.getSimpleName() + "--");
//                updateProgress(id, 0);
//                eventUpImgLoadEvent.setMsgid(id);
//                eventUpImgLoadEvent.setState(-1);
//                eventUpImgLoadEvent.setUrl("");
//                eventUpImgLoadEvent.setOriginal(isOriginal);
//                eventUpImgLoadEvent.setMsgAllBean(msgDao.fixStataMsg(id, ChatEnum.ESendStatus.ERROR));//写库
//                EventBus.getDefault().post(eventUpImgLoadEvent);
//            }
//
//            @Override
//            public void inProgress(long progress, long zong) {
//                if (System.currentTimeMillis() - oldUptime < 100) {
//                    return;
//                }
//                EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
//                oldUptime = System.currentTimeMillis();
//
//                int pg = new Double(progress / (zong + 0.0f) * 100.0).intValue();
//
//                updateProgress(id, pg);
//                eventUpImgLoadEvent.setMsgid(id);
//                eventUpImgLoadEvent.setState(0);
//                eventUpImgLoadEvent.setUrl("");
//                eventUpImgLoadEvent.setOriginal(isOriginal);
//                EventBus.getDefault().post(eventUpImgLoadEvent);
//            }
//        });
//        queue.offer(upProgress);
//    }

    public static void onAddImage(final MsgAllBean msg, String file, final Boolean isOriginal) {
        if (msg == null) {
            return;
        }
        final UpProgress upProgress = new UpProgress();
        upProgress.setId(msg.getMsg_id());
        upProgress.setFile(file);
        updateProgress(msg.getMsg_id(), new Random().nextInt(5) + 1);//发送图片后默认给个进度，显示阴影表示正在上传
        final ImgSizeUtil.ImageSize img = ImgSizeUtil.getAttribute(file);
        upProgress.setCallback(new UpFileUtil.OssUpCallback() {

            @Override
            public void success(final String url) {
                EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
                updateProgress(msg.getMsg_id(), 100);
                eventUpImgLoadEvent.setMsgid(msg.getMsg_id());
                eventUpImgLoadEvent.setState(1);
                eventUpImgLoadEvent.setUrl(url);
                eventUpImgLoadEvent.setOriginal(isOriginal);

                ImageMessage image = msg.getImage();
                ImageMessage imageMessage = SocketData.createImageMessage(msg.getMsg_id(), file, url, image.getWidth(), image.getHeight(), isOriginal, false, image.getSize());
                msg.setImage(imageMessage);
                eventUpImgLoadEvent.setMsgAllBean(msg);
                EventBus.getDefault().post(eventUpImgLoadEvent);

            }

            @Override
            public void fail() {
                EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
                //  LogUtil.getLog().d("tag", "fail : ===============>"+id);
                updateProgress(msg.getMsg_id(), 0);
                eventUpImgLoadEvent.setMsgid(msg.getMsg_id());
                eventUpImgLoadEvent.setState(-1);
                eventUpImgLoadEvent.setUrl("");
                eventUpImgLoadEvent.setOriginal(isOriginal);
                eventUpImgLoadEvent.setMsgAllBean(msgDao.fixStataMsg(msg.getMsg_id(), ChatEnum.ESendStatus.ERROR));//写库
                EventBus.getDefault().post(eventUpImgLoadEvent);
            }

            @Override
            public void inProgress(long progress, long zong) {
                if (System.currentTimeMillis() - oldUptime < 100) {
                    return;
                }
                EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
                oldUptime = System.currentTimeMillis();
                int pg = new Double(progress / (zong + 0.0f) * 100.0).intValue();
                updateProgress(msg.getMsg_id(), pg);
                eventUpImgLoadEvent.setMsgid(msg.getMsg_id());
                eventUpImgLoadEvent.setMsgAllBean(msg);
                eventUpImgLoadEvent.setState(0);
                eventUpImgLoadEvent.setUrl("");
                eventUpImgLoadEvent.setOriginal(isOriginal);
                EventBus.getDefault().post(eventUpImgLoadEvent);
            }
        });
        queue.offer(upProgress);
    }


    /**
     * 上传图片 (重载)
     * @param msg               消息体
     * @param file              图片本地路径
     * @param isOriginal        是否为原图
     * @param needSendToFriend  是否需要转发
     */
    public static void onAddImage(final MsgAllBean msg, String file, final Boolean isOriginal, boolean needSendToFriend) {
        if (msg == null) {
            return;
        }
        final UpProgress upProgress = new UpProgress();
        upProgress.setId(msg.getMsg_id());
        upProgress.setFile(file);
        updateProgress(msg.getMsg_id(), new Random().nextInt(5) + 1);//发送图片后默认给个进度，显示阴影表示正在上传
        upProgress.setCallback(new UpFileUtil.OssUpCallback() {

            @Override
            public void success(final String url) {
                EventUploadImg eventUploadImg = new EventUploadImg();
                eventUploadImg.setState(1);
                ImageMessage image = msg.getImage();//上传成功后重新设置图片网络url
                ImageMessage imageMessage = SocketData.createImageMessage(msg.getMsg_id(), file, url, image.getWidth(), image.getHeight(), isOriginal, false, image.getSize());
                msg.setImage(imageMessage);
                eventUploadImg.setMsgAllBean(msg);
                EventBus.getDefault().post(eventUploadImg);

            }

            @Override
            public void fail() {
                EventUploadImg eventUploadImg = new EventUploadImg();
                eventUploadImg.setState(-1);
                EventBus.getDefault().post(eventUploadImg);
            }

            @Override
            public void inProgress(long progress, long zong) {

            }
        });
        queue.offer(upProgress);
    }

//
//    /**
//     * 发送文件
//     *
//     * @param id       msgID
//     * @param filePath 文件路径
//     * @param fileName 文件名
//     * @param fileSize 文件大小
//     * @param format   后缀类型
//     * @param toUId    接收人ID
//     * @param toGid    群ID
//     * @param time     发送时间
//     */
//    @Deprecated
//    public static void onAddFile(Context mContext, final String id, String filePath, String fileName, final Long fileSize, String format, final Long toUId, final String toGid, final long time) {
//        // 上传文件时，默认给1-5的上传进度，解决一开始上传不显示进度问题
////        updateProgress(id, new Random().nextInt(5) + 1);
//
//        UpFileAction upFileAction = new UpFileAction();
//        upFileAction.upFile(UpFileAction.PATH.FILE, mContext, new UpFileUtil.OssUpCallback() {
//            @Override
//            public void success(String url) {
//                LogUtil.getLog().d(TAG, "success : 文件上传成功===============>" + filePath);
//                EventUpFileLoadEvent eventUpFileLoadEvent = new EventUpFileLoadEvent();
//                updateProgress(id, 100);
//                eventUpFileLoadEvent.setMsgid(id);
//                eventUpFileLoadEvent.setState(1);
//                eventUpFileLoadEvent.setUrl(url);
//                //发送文件消息到服务器，传递给目标用户
//                Object msgbean = SocketData.sendFile(id, url, toUId, toGid, fileName, fileSize, format, time, filePath);
//                eventUpFileLoadEvent.setMsgAllBean(msgbean);
//                EventBus.getDefault().post(eventUpFileLoadEvent);
//            }
//
//            @Override
//            public void fail() {
//                EventUpFileLoadEvent eventUpFileLoadEvent = new EventUpFileLoadEvent();
//                updateProgress(id, 0);
//                LogUtil.getLog().d(TAG, "fail : 文件上传失败===============>" + id);
//                eventUpFileLoadEvent.setMsgid(id);
//                eventUpFileLoadEvent.setState(-1);
//                eventUpFileLoadEvent.setUrl("");
//                eventUpFileLoadEvent.setMsgAllBean(msgDao.fixStataMsg(id, ChatEnum.ESendStatus.ERROR));//写库
//                EventBus.getDefault().post(eventUpFileLoadEvent);
//            }
//
//            @Override
//            public void inProgress(long progress, long zong) {
//                if (System.currentTimeMillis() - oldUptime < 100) {
//                    return;
//                }
//                EventUpFileLoadEvent eventUpFileLoadEvent = new EventUpFileLoadEvent();
//                oldUptime = System.currentTimeMillis();
//                int pg = new Double(progress / (zong + 0.0f) * 100.0).intValue();
//                LogUtil.getLog().d(TAG, "inProgress : 文件上传进度===============>" + pg);
//                updateProgress(id, pg);
//                eventUpFileLoadEvent.setMsgid(id);
//                eventUpFileLoadEvent.setState(0);
//                eventUpFileLoadEvent.setUrl("");
//                EventBus.getDefault().post(eventUpFileLoadEvent);
//            }
//        }, filePath);
//    }

    /**
     * 发送文件
     */
    public static void onAddFile(Context mContext, MsgAllBean bean) {
        // 上传文件时，默认给1-5的上传进度，解决一开始上传不显示进度问题
//        updateProgress(id, new Random().nextInt(5) + 1);
        if (bean == null) {
            return;
        }
        SendFileMessage fileMessage = bean.getSendFileMessage();
        if (fileMessage == null || TextUtils.isEmpty(fileMessage.getLocalPath())) {
            return;
        }
        UpFileAction upFileAction = new UpFileAction();
        upFileAction.upFile(UpFileAction.PATH.FILE, mContext, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                LogUtil.getLog().d(TAG, "success : 文件上传成功===============>" + fileMessage.getLocalPath());
                EventUpImgLoadEvent eventUpFileLoadEvent = new EventUpImgLoadEvent();
                updateProgress(bean.getMsg_id(), 100);
                eventUpFileLoadEvent.setMsgid(bean.getMsg_id());
                eventUpFileLoadEvent.setState(1);
                eventUpFileLoadEvent.setUrl(url);
                //上传成功后，更新数据
                fileMessage.setUrl(url);
                bean.setSendFileMessage(fileMessage);
                eventUpFileLoadEvent.setMsgAllBean(bean);
                EventBus.getDefault().post(eventUpFileLoadEvent);
            }

            @Override
            public void fail() {
                EventUpImgLoadEvent eventUpFileLoadEvent = new EventUpImgLoadEvent();
                updateProgress(bean.getMsg_id(), 0);
                LogUtil.getLog().d(TAG, "fail : 文件上传失败===============>" + bean.getMsg_id());
                eventUpFileLoadEvent.setMsgid(bean.getMsg_id());
                eventUpFileLoadEvent.setState(-1);
                eventUpFileLoadEvent.setUrl("");
                eventUpFileLoadEvent.setMsgAllBean(msgDao.fixStataMsg(bean.getMsg_id(), ChatEnum.ESendStatus.ERROR));//写库
                EventBus.getDefault().post(eventUpFileLoadEvent);
            }

            @Override
            public void inProgress(long progress, long zong) {
                if (System.currentTimeMillis() - oldUptime < 100) {
                    return;
                }
                EventUpImgLoadEvent eventUpFileLoadEvent = new EventUpImgLoadEvent();
                oldUptime = System.currentTimeMillis();
                int pg = new Double(progress / (zong + 0.0f) * 100.0).intValue();
                LogUtil.getLog().d(TAG, "inProgress : 文件上传进度===============>" + pg);
                updateProgress(bean.getMsg_id(), pg);
                eventUpFileLoadEvent.setMsgid(bean.getMsg_id());
                eventUpFileLoadEvent.setMsgAllBean(bean);
                eventUpFileLoadEvent.setState(0);
                eventUpFileLoadEvent.setUrl("");
                EventBus.getDefault().post(eventUpFileLoadEvent);
            }
        }, fileMessage.getLocalPath());
    }


//    /**
//     * 发送视屏
//     *
//     * @param mContext     上下文
//     * @param file         视屏文件
//     * @param bgUrl        预览图
//     * @param isOriginal   是否是原图
//     * @param toUId        接收人ID
//     * @param toGid        群ID
//     * @param time         发送时间
//     * @param videoMessage 视屏对象
//     * @param isRest       是否重发
//     */
//    @Deprecated
//    public static void onAddVideo(final Context mContext, final String id, final String file, String bgUrl, final Boolean isOriginal,
//                                  final Long toUId, final String toGid, final long time, final VideoMessage videoMessage, boolean isRest) {
//        if (mVideoMaps != null && !isRest) {
//            // 先添加到集合中，上传失败用于重发
//            mVideoMaps.put(id, new VideoUploadBean(mContext, id, file, bgUrl, isOriginal, toUId, toGid, time, videoMessage, 0));
//            // 上传预览图时，默认给1-5的上传进度，解决一开始上传不显示进度问题
//            updateProgress(id, new Random().nextInt(5) + 1);
//        }
//        uploadImageOfVideo(mContext, bgUrl, new UpLoadCallback() {
//            @Override
//            public void success(String url) {
//                LogUtil.getLog().d(TAG, "视频预览图上传成功了---------");
//                if (mVideoMaps.get(id) != null) {
//                    mVideoMaps.get(id).setSendNum(0);
//                }
//                netBgUrl = url;
//                uploadVideo(mContext, id, file, bgUrl, isOriginal, toUId, toGid, time, videoMessage, isRest);
//            }
//
//            @Override
//            public void fail() {
//                int sendNum = 0;
//                if (mVideoMaps != null && mVideoMaps.get(id) != null) {
//                    sendNum = mVideoMaps.get(id).getSendNum() + 1;
//                    mVideoMaps.get(id).setSendNum(sendNum);
//                }
//                if (mVideoMaps == null || mVideoMaps.get(id) == null || sendNum > SEND_MAX_NUM) {
//                    mVideoMaps.remove(id);
//                    EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
//                    LogUtil.getLog().d(TAG, "fail : 视频预览图上传失败了 ===============>" + id);
//                    updateProgress(id, 100);
//                    eventUpImgLoadEvent.setMsgid(id);
//                    eventUpImgLoadEvent.setState(-1);
//                    eventUpImgLoadEvent.setUrl("");
//                    eventUpImgLoadEvent.setOriginal(isOriginal);
//                    eventUpImgLoadEvent.setMsgAllBean(msgDao.fixStataMsg(id, 1));//写库
//                    EventBus.getDefault().post(eventUpImgLoadEvent);
//                } else {
//                    LogUtil.getLog().d(TAG, "fail : 视频预览图重发了======" + sendNum + "=========>" + id);
//                    loopImageList();
//                }
//            }
//        });
//    }


    /**
     * 发送视屏
     *
     * @param mContext   上下文
     * @param msgAllBean 消息对象
     * @param isRest     是否重发
     */
    public static void onAddVideo(final Context mContext, MsgAllBean msgAllBean, boolean isRest) {
        if (mVideoMaps != null && !isRest) {
            // 先添加到集合中，上传失败用于重发
            mVideoMaps.put(msgAllBean.getMsg_id(), new VideoUploadBean(mContext, msgAllBean.getMsg_id(), msgAllBean, 0));
            // 上传预览图时，默认给1-5的上传进度，解决一开始上传不显示进度问题
            updateProgress(msgAllBean.getMsg_id(), new Random().nextInt(5) + 1);
        }
        VideoMessage videoMessage = msgAllBean.getVideoMessage();
        if (videoMessage == null || TextUtils.isEmpty(videoMessage.getBg_url())) {
            return;
        }
        uploadImageOfVideo(mContext, videoMessage.getBg_url(), new UpLoadCallback() {
            @Override
            public void success(String url) {
                LogUtil.getLog().d(TAG, "视频预览图上传成功了---------");
                if (mVideoMaps.get(msgAllBean.getMsg_id()) != null) {
                    mVideoMaps.get(msgAllBean.getMsg_id()).setSendNum(0);
                }
                netBgUrl = url;
                uploadVideo(mContext, msgAllBean, videoMessage);
            }

            @Override
            public void fail() {
                int sendNum = 0;
                if (mVideoMaps != null && mVideoMaps.get(msgAllBean.getMsg_id()) != null) {
                    sendNum = mVideoMaps.get(msgAllBean.getMsg_id()).getSendNum() + 1;
                    mVideoMaps.get(msgAllBean.getMsg_id()).setSendNum(sendNum);
                }
                if (mVideoMaps == null || mVideoMaps.get(msgAllBean.getMsg_id()) == null || sendNum > SEND_MAX_NUM) {
                    mVideoMaps.remove(msgAllBean.getMsg_id());
                    EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
                    LogUtil.getLog().d(TAG, "fail : 视频预览图上传失败了 ===============>" + msgAllBean.getMsg_id());
                    updateProgress(msgAllBean.getMsg_id(), 100);
                    eventUpImgLoadEvent.setMsgid(msgAllBean.getMsg_id());
                    eventUpImgLoadEvent.setState(-1);
                    eventUpImgLoadEvent.setUrl("");
                    eventUpImgLoadEvent.setMsgAllBean(msgDao.fixStataMsg(msgAllBean.getMsg_id(), 1));//写库
                    EventBus.getDefault().post(eventUpImgLoadEvent);
                } else {
                    LogUtil.getLog().d(TAG, "fail : 视频预览图重发了======" + sendNum + "=========>" + msgAllBean.getMsg_id());
                    loopImageList();
                }
            }
        });
    }

    /**
     * 上传视屏
     *
     * @param mContext     上下文
     * @param videoMessage 视屏对象
     */
    private static void uploadVideo(final Context mContext, final MsgAllBean bean, VideoMessage videoMessage) {
        if (bean == null || videoMessage == null || TextUtils.isEmpty(videoMessage.getLocalUrl())) {
            return;
        }
        UpFileAction upFileAction = new UpFileAction();
        upFileAction.upFile(UpFileAction.PATH.VIDEO, mContext, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                if (mVideoMaps != null) {
                    mVideoMaps.remove(bean.getMsg_id());
                }
                LogUtil.getLog().d(TAG, "success : 视频上传成功===============>" + videoMessage.getLocalUrl());
                EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
                updateProgress(bean.getMsg_id(), 100);
                eventUpImgLoadEvent.setMsgid(bean.getMsg_id());
                eventUpImgLoadEvent.setState(1);
                eventUpImgLoadEvent.setUrl(url);

//                Object msgbean = SocketData.sendVideo(bean.getMsg_id(), toUId, toGid, url, netBgUrl, isOriginal, time, (int) videoMessage.getWidth(),
//                        (int) videoMessage.getHeight(), videoMessage.getLocalUrl());
//                ((MsgAllBean) msgbean).getVideoMessage().setLocalUrl(videoMessage.getLocalUrl());
                videoMessage.setBg_url(netBgUrl);
                videoMessage.setUrl(url);
                bean.setVideoMessage(videoMessage);
                eventUpImgLoadEvent.setMsgAllBean(bean);
                EventBus.getDefault().post(eventUpImgLoadEvent);
            }

            @Override
            public void fail() {

                int sendNum = 0;
                if (mVideoMaps != null && mVideoMaps.get(bean.getMsg_id()) != null) {
                    sendNum = mVideoMaps.get(bean.getMsg_id()).getSendNum() + 1;
                    mVideoMaps.get(bean.getMsg_id()).setSendNum(sendNum);
                }
                if (mVideoMaps == null || mVideoMaps.get(bean.getMsg_id()) == null || sendNum > SEND_MAX_NUM) {
                    mVideoMaps.remove(bean.getMsg_id());
                    EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
                    LogUtil.getLog().d(TAG, "fail : 视频上传失败===============>" + bean.getMsg_id());
                    updateProgress(bean.getMsg_id(), 100);
                    eventUpImgLoadEvent.setMsgid(bean.getMsg_id());
                    eventUpImgLoadEvent.setState(-1);
                    eventUpImgLoadEvent.setUrl("");
                    eventUpImgLoadEvent.setMsgAllBean(msgDao.fixStataMsg(bean.getMsg_id(), 1));//写库
                    EventBus.getDefault().post(eventUpImgLoadEvent);
                } else {
                    LogUtil.getLog().d(TAG, "fail : 视频重发了======" + sendNum + "=========>" + bean.getMsg_id());
                    loopVideoList();
                }
            }

            @Override
            public void inProgress(long progress, long zong) {
                if (System.currentTimeMillis() - oldUptime < 100) {
                    return;
                }
                EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
                oldUptime = System.currentTimeMillis();

                int pg = new Double(progress / (zong + 0.0f) * 100.0).intValue();
                LogUtil.getLog().d(TAG, "inProgress : 视频上传进度===============>" + pg);
                updateProgress(bean.getMsg_id(), pg);
                eventUpImgLoadEvent.setMsgid(bean.getMsg_id());
                eventUpImgLoadEvent.setMsgAllBean(bean);
                eventUpImgLoadEvent.setState(0);
                eventUpImgLoadEvent.setUrl("");
                EventBus.getDefault().post(eventUpImgLoadEvent);
            }
        }, videoMessage.getLocalUrl());
    }

//    /**
//     * 上传视屏
//     *
//     * @param mContext     上下文
//     * @param file         视屏文件
//     * @param bgUrl        预览图
//     * @param isOriginal   是否是原图
//     * @param toUId        接收人ID
//     * @param toGid        群ID
//     * @param time         发送时间
//     * @param videoMessage 视屏对象
//     * @param isRest       是否重发
//     */
//    @Deprecated
//    private static void uploadVideo(final Context mContext, final String id, final String file, String bgUrl, final Boolean isOriginal,
//                                    final Long toUId, final String toGid, final long time, final VideoMessage videoMessage, boolean isRest) {
//        UpFileAction upFileAction = new UpFileAction();
//        upFileAction.upFile(UpFileAction.PATH.VIDEO, mContext, new UpFileUtil.OssUpCallback() {
//            @Override
//            public void success(String url) {
//                if (mVideoMaps != null) {
//
//                    mVideoMaps.remove(id);
//                }
//                LogUtil.getLog().d(TAG, "success : 视频上传成功===============>" + file);
//                EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
//                updateProgress(id, 100);
//                eventUpImgLoadEvent.setMsgid(id);
//                eventUpImgLoadEvent.setState(1);
//                eventUpImgLoadEvent.setUrl(url);
//                eventUpImgLoadEvent.setOriginal(isOriginal);
//                Object msgbean = SocketData.sendVideo(id, toUId, toGid, url, netBgUrl, isOriginal, time, (int) videoMessage.getWidth(),
//                        (int) videoMessage.getHeight(), videoMessage.getLocalUrl());
//                ((MsgAllBean) msgbean).getVideoMessage().setLocalUrl(videoMessage.getLocalUrl());
//
//                eventUpImgLoadEvent.setMsgAllBean(msgbean);
//                EventBus.getDefault().post(eventUpImgLoadEvent);
//            }
//
//            @Override
//            public void fail() {
//
//                int sendNum = 0;
//                if (mVideoMaps != null && mVideoMaps.get(id) != null) {
//                    sendNum = mVideoMaps.get(id).getSendNum() + 1;
//                    mVideoMaps.get(id).setSendNum(sendNum);
//                }
//                if (mVideoMaps == null || mVideoMaps.get(id) == null || sendNum > SEND_MAX_NUM) {
//                    mVideoMaps.remove(id);
//                    EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
//                    LogUtil.getLog().d(TAG, "fail : 视频上传失败===============>" + id);
//                    updateProgress(id, 100);
//                    eventUpImgLoadEvent.setMsgid(id);
//                    eventUpImgLoadEvent.setState(-1);
//                    eventUpImgLoadEvent.setUrl("");
//                    eventUpImgLoadEvent.setOriginal(isOriginal);
//                    eventUpImgLoadEvent.setMsgAllBean(msgDao.fixStataMsg(id, 1));//写库
//                    EventBus.getDefault().post(eventUpImgLoadEvent);
//                } else {
//                    LogUtil.getLog().d(TAG, "fail : 视频重发了======" + sendNum + "=========>" + id);
//                    loopVideoList();
//                }
//            }
//
//            @Override
//            public void inProgress(long progress, long zong) {
//                if (System.currentTimeMillis() - oldUptime < 100) {
//                    return;
//                }
//                EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
//                oldUptime = System.currentTimeMillis();
//
//                int pg = new Double(progress / (zong + 0.0f) * 100.0).intValue();
//                LogUtil.getLog().d(TAG, "inProgress : 视频上传进度===============>" + pg);
//                updateProgress(id, pg);
//                eventUpImgLoadEvent.setMsgid(id);
//                eventUpImgLoadEvent.setState(0);
//                eventUpImgLoadEvent.setUrl("");
//                eventUpImgLoadEvent.setOriginal(isOriginal);
//                EventBus.getDefault().post(eventUpImgLoadEvent);
//            }
//        }, file);
//    }


    /**
     * 上传视频预览图
     *
     * @param mContext
     * @param file
     * @param upLoadCallback
     */
    private static void uploadImageOfVideo(Context mContext, String file, final UpLoadCallback upLoadCallback) {
        UpFileAction upFileAction = new UpFileAction();
        upFileAction.upFile(UpFileAction.PATH.VIDEO, mContext, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                upLoadCallback.success(url);
            }

            @Override
            public void fail() {
                upLoadCallback.fail();
            }

            @Override
            public void inProgress(long progress, long zong) {
//                LogUtil.getLog().d(TAG, progress + "上传视频预览图---------" + zong);
            }
        }, file);

    }


    /**
     * 循环重发 预览图
     */
    private static void loopImageList() {
        Iterator<Map.Entry<String, VideoUploadBean>> entrys = mVideoMaps.entrySet().iterator();
        while (entrys.hasNext()) {
            Map.Entry<String, VideoUploadBean> entry = entrys.next();
            VideoUploadBean bean = entry.getValue();
            if (bean.getMsg() != null) {
                onAddVideo(bean.getContext(), bean.getMsg(), true);
            } /*else {
                onAddVideo(bean.getContext(), bean.getId(), bean.getFile(), bean.getBgUrl(), bean.getOriginal(), bean.getToUId(),
                        bean.getToGid(), bean.getTime(), bean.getVideoMessage(), true);
            }*/

        }
    }

    /**
     * 循环重发 视屏
     */
    private static void loopVideoList() {
        Iterator<Map.Entry<String, VideoUploadBean>> entrys = mVideoMaps.entrySet().iterator();
        while (entrys.hasNext()) {
            Map.Entry<String, VideoUploadBean> entry = entrys.next();
            VideoUploadBean bean = entry.getValue();
            if (bean.getMsg() != null) {
                uploadVideo(bean.getContext(), bean.getMsg(), bean.getMsg().getVideoMessage());
            } /*else {
                uploadVideo(bean.getContext(), bean.getId(), bean.getFile(), bean.getBgUrl(), bean.getOriginal(), bean.getToUId(),
                        bean.getToGid(), bean.getTime(), bean.getVideoMessage(), true);
            }*/
        }
    }

    public interface UpLoadCallback {

        void success(String url);

        void fail();

    }

    public static class UpProgress {
        private String id;
        private String file;
        private int progress;
        private UpFileUtil.OssUpCallback callback;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public UpFileUtil.OssUpCallback getCallback() {
            return callback;
        }

        public void setCallback(UpFileUtil.OssUpCallback callback) {
            this.callback = callback;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }
    }

}
