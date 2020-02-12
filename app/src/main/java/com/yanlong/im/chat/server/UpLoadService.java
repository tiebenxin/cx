package com.yanlong.im.chat.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.VideoMessage;
import com.yanlong.im.chat.bean.VideoUploadBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.bean.EventUpFileLoadEvent;
import net.cb.cb.library.bean.EventUpImgLoadEvent;
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
                    LogUtil.getLog().d("ChatActivity--上传", "上传: " + upProgress.getId());
                    upFileAction.upFileSyn(UpFileAction.PATH.IMG, getApplicationContext(), upProgress.getCallback(), upProgress.getFile());
                }
                stopSelf();
                LogUtil.getLog().d("ChatActivity-上传", "上传结束");
            }
        }).start();

    }

    public static void onAdd(final String id, String file, final Boolean isOriginal, final Long toUId, final String toGid, final long time) {
        final UpProgress upProgress = new UpProgress();
        upProgress.setId(id);
        upProgress.setFile(file);
        updateProgress(id, new Random().nextInt(5) + 1);//发送图片后默认给个进度，显示阴影表示正在上传
        final ImgSizeUtil.ImageSize img = ImgSizeUtil.getAttribute(file);
        upProgress.setCallback(new UpFileUtil.OssUpCallback() {

            @Override
            public void success(final String url) {
                EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
                updateProgress(id, 100);
                eventUpImgLoadEvent.setMsgid(id);
                eventUpImgLoadEvent.setState(1);
                eventUpImgLoadEvent.setUrl(url);
                eventUpImgLoadEvent.setOriginal(isOriginal);
                Object msgbean = SocketData.send4Image(id, toUId, toGid, url, isOriginal, img, time);

                eventUpImgLoadEvent.setMsgAllBean(msgbean);
                EventBus.getDefault().post(eventUpImgLoadEvent);

            }

            @Override
            public void fail() {
                EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
                //  LogUtil.getLog().d("tag", "fail : ===============>"+id);

                System.out.println(UpLoadService.class.getSimpleName() + "--");
                updateProgress(id, 0);
                eventUpImgLoadEvent.setMsgid(id);
                eventUpImgLoadEvent.setState(-1);
                eventUpImgLoadEvent.setUrl("");
                eventUpImgLoadEvent.setOriginal(isOriginal);
                eventUpImgLoadEvent.setMsgAllBean(msgDao.fixStataMsg(id, ChatEnum.ESendStatus.ERROR));//写库
                EventBus.getDefault().post(eventUpImgLoadEvent);
            }

            @Override
            public void inProgress(long progress, long zong) {
                if (System.currentTimeMillis() - oldUptime < 100) {
                    return;
                }
                EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
                // LogUtil.getLog().d("tag", "inProgress : ===============>"+id);
                oldUptime = System.currentTimeMillis();

                int pg = new Double(progress / (zong + 0.0f) * 100.0).intValue();

                updateProgress(id, pg);
                eventUpImgLoadEvent.setMsgid(id);
                eventUpImgLoadEvent.setState(0);
                eventUpImgLoadEvent.setUrl("");
                eventUpImgLoadEvent.setOriginal(isOriginal);
                EventBus.getDefault().post(eventUpImgLoadEvent);
            }
        });
        queue.offer(upProgress);
    }


    /**
     * 发送文件
     * @param id        msgID
     * @param file      文件路径
     * @param fileName  文件名
     * @param fileSize  文件大小
     * @param format    后缀类型
     * @param toUId     接收人ID
     * @param toGid     群ID
     * @param time      发送时间
     */
    public static void onAddFile(Context mContext,final String id, String file,String fileName,final Long fileSize,String format, final Long toUId, final String toGid, final long time) {
        // 上传文件时，默认给1-5的上传进度，解决一开始上传不显示进度问题
        updateProgress(id, new Random().nextInt(5) + 1);

        UpFileAction upFileAction = new UpFileAction();
        upFileAction.upFile(UpFileAction.PATH.FILE, mContext, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                LogUtil.getLog().d(TAG, "success : 文件上传成功===============>" + file);
                EventUpFileLoadEvent eventUpFileLoadEvent = new EventUpFileLoadEvent();
                updateProgress(id, 100);
                eventUpFileLoadEvent.setMsgid(id);
                eventUpFileLoadEvent.setState(1);
                eventUpFileLoadEvent.setUrl(url);
                Object msgbean = SocketData.sendFile(id,url,toUId,toGid,fileName,fileSize,format, time);
                eventUpFileLoadEvent.setMsgAllBean(msgbean);
                EventBus.getDefault().post(eventUpFileLoadEvent);
            }

            @Override
            public void fail() {
                EventUpFileLoadEvent eventUpFileLoadEvent = new EventUpFileLoadEvent();
                updateProgress(id, 0);
                LogUtil.getLog().d(TAG, "fail : 文件上传失败===============>" + id);
                eventUpFileLoadEvent.setMsgid(id);
                eventUpFileLoadEvent.setState(-1);
                eventUpFileLoadEvent.setUrl("");
                eventUpFileLoadEvent.setMsgAllBean(msgDao.fixStataMsg(id, ChatEnum.ESendStatus.ERROR));//写库
                EventBus.getDefault().post(eventUpFileLoadEvent);
            }

            @Override
            public void inProgress(long progress, long zong) {
                if (System.currentTimeMillis() - oldUptime < 100) {
                    return;
                }
                EventUpFileLoadEvent eventUpFileLoadEvent = new EventUpFileLoadEvent();
                oldUptime = System.currentTimeMillis();
                int pg = new Double(progress / (zong + 0.0f) * 100.0).intValue();
                LogUtil.getLog().d(TAG, "inProgress : 文件上传进度===============>" + pg);
                updateProgress(id, pg);
                eventUpFileLoadEvent.setMsgid(id);
                eventUpFileLoadEvent.setState(0);
                eventUpFileLoadEvent.setUrl("");
                EventBus.getDefault().post(eventUpFileLoadEvent);
            }
        }, file);
    }


    /**
     * 发送视屏
     *
     * @param mContext     上下文
     * @param id           msgID
     * @param file         视屏文件
     * @param bgUrl        预览图
     * @param isOriginal   是否是原图
     * @param toUId        接收人ID
     * @param toGid        群ID
     * @param time         发送时间
     * @param videoMessage 视屏对象
     * @param isRest       是否重发
     */
    public static void onAddVideo(final Context mContext, final String id, final String file, String bgUrl, final Boolean isOriginal,
                                  final Long toUId, final String toGid, final long time, final VideoMessage videoMessage, boolean isRest) {

        if (mVideoMaps != null && !isRest) {
            // 先添加到集合中，上传失败用于重发
            mVideoMaps.put(id, new VideoUploadBean(mContext, id, file, bgUrl, isOriginal, toUId, toGid, time, videoMessage, 0));
            // 上传预览图时，默认给1-5的上传进度，解决一开始上传不显示进度问题
            updateProgress(id, new Random().nextInt(5) + 1);
        }
        uploadImageOfVideo(mContext, bgUrl, new UpLoadCallback() {
            @Override
            public void success(String url) {
                LogUtil.getLog().d(TAG,  "视频预览图上传成功了---------" );
                if (mVideoMaps.get(id) != null) {
                    mVideoMaps.get(id).setSendNum(0);
                }
                netBgUrl = url;
                uploadVideo(mContext, id, file, bgUrl, isOriginal, toUId, toGid, time, videoMessage, isRest);
            }

            @Override
            public void fail() {
                int sendNum = 0;
                if (mVideoMaps != null && mVideoMaps.get(id) != null) {
                    sendNum = mVideoMaps.get(id).getSendNum() + 1;
                    mVideoMaps.get(id).setSendNum(sendNum);
                }
                if (mVideoMaps == null || mVideoMaps.get(id) == null || sendNum > SEND_MAX_NUM) {
                    mVideoMaps.remove(id);
                    EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
                    LogUtil.getLog().d(TAG, "fail : 视频预览图上传失败了 ===============>" + id);
                    updateProgress(id, 100);
                    eventUpImgLoadEvent.setMsgid(id);
                    eventUpImgLoadEvent.setState(-1);
                    eventUpImgLoadEvent.setUrl("");
                    eventUpImgLoadEvent.setOriginal(isOriginal);
                    eventUpImgLoadEvent.setMsgAllBean(msgDao.fixStataMsg(id, 1));//写库
                    EventBus.getDefault().post(eventUpImgLoadEvent);
                } else {
                    LogUtil.getLog().d(TAG, "fail : 视频预览图重发了======"+sendNum+"=========>" + id);
                    loopImageList();
                }
            }
        });
    }

    /**
     * 上传视屏
     *
     * @param mContext     上下文
     * @param id           msgID
     * @param file         视屏文件
     * @param bgUrl        预览图
     * @param isOriginal   是否是原图
     * @param toUId        接收人ID
     * @param toGid        群ID
     * @param time         发送时间
     * @param videoMessage 视屏对象
     * @param isRest       是否重发
     */
    private static void uploadVideo(final Context mContext, final String id, final String file, String bgUrl, final Boolean isOriginal,
                                    final Long toUId, final String toGid, final long time, final VideoMessage videoMessage, boolean isRest) {
        UpFileAction upFileAction = new UpFileAction();
        upFileAction.upFile(UpFileAction.PATH.VIDEO, mContext, new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                if (mVideoMaps != null) {
                    mVideoMaps.remove(id);
                }
                LogUtil.getLog().d(TAG, "success : 视频上传成功===============>" + file);
                EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
                updateProgress(id, 100);
                eventUpImgLoadEvent.setMsgid(id);
                eventUpImgLoadEvent.setState(1);
                eventUpImgLoadEvent.setUrl(url);
                eventUpImgLoadEvent.setOriginal(isOriginal);
                Object msgbean = SocketData.sendVideo(id, toUId, toGid, url, netBgUrl, isOriginal, time, (int) videoMessage.getWidth(),
                        (int) videoMessage.getHeight(), videoMessage.getLocalUrl());
                ((MsgAllBean) msgbean).getVideoMessage().setLocalUrl(videoMessage.getLocalUrl());

                eventUpImgLoadEvent.setMsgAllBean(msgbean);
                EventBus.getDefault().post(eventUpImgLoadEvent);
            }

            @Override
            public void fail() {

                int sendNum = 0;
                if (mVideoMaps != null && mVideoMaps.get(id) != null) {
                    sendNum = mVideoMaps.get(id).getSendNum() + 1;
                    mVideoMaps.get(id).setSendNum(sendNum);
                }
                if (mVideoMaps == null || mVideoMaps.get(id) == null || sendNum > SEND_MAX_NUM) {
                    mVideoMaps.remove(id);
                    EventUpImgLoadEvent eventUpImgLoadEvent = new EventUpImgLoadEvent();
                    LogUtil.getLog().d(TAG, "fail : 视频上传失败===============>" + id);
                    updateProgress(id, 100);
                    eventUpImgLoadEvent.setMsgid(id);
                    eventUpImgLoadEvent.setState(-1);
                    eventUpImgLoadEvent.setUrl("");
                    eventUpImgLoadEvent.setOriginal(isOriginal);
                    eventUpImgLoadEvent.setMsgAllBean(msgDao.fixStataMsg(id, 1));//写库
                    EventBus.getDefault().post(eventUpImgLoadEvent);
                } else {
                    LogUtil.getLog().d(TAG, "fail : 视频重发了======"+sendNum+"=========>" + id);
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
                updateProgress(id, pg);
                eventUpImgLoadEvent.setMsgid(id);
                eventUpImgLoadEvent.setState(0);
                eventUpImgLoadEvent.setUrl("");
                eventUpImgLoadEvent.setOriginal(isOriginal);
                EventBus.getDefault().post(eventUpImgLoadEvent);
            }
        }, file);
    }

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
            onAddVideo(bean.getContext(), bean.getId(), bean.getFile(), bean.getBgUrl(), bean.getOriginal(), bean.getToUId(),
                    bean.getToGid(), bean.getTime(), bean.getVideoMessage(), true);
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
            uploadVideo(bean.getContext(), bean.getId(), bean.getFile(), bean.getBgUrl(), bean.getOriginal(), bean.getToUId(),
                    bean.getToGid(), bean.getTime(), bean.getVideoMessage(), true);
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
