package com.yanlong.im.utils.socket;

import android.text.TextUtils;

import com.tencent.bugly.crashreport.CrashReport;
import com.yanlong.im.MyAppLication;

import net.cb.cb.library.constant.BuglyTag;
import net.cb.cb.library.utils.LogUtil;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

import static com.yanlong.im.utils.socket.SocketUtil.BUGLY_TAG_SEND_DATA;

/**
 * @author Liszt
 * @date 2020/2/27
 * Description 包写入器
 */
public class AsyncPacketWriter {
    private final String TAG = AsyncPacketWriter.class.getSimpleName();

    private final Executor executor = ExecutorManager.INSTANCE.getWriteThread();
    private final SSLSocketChannel2 socketChannel;

    public AsyncPacketWriter(SSLSocketChannel2 channel) {
        socketChannel = channel;
    }

    public void write(byte[] data, MsgBean.UniversalMessage.Builder msgTag, String requestId) {
        executor.execute(new WriteTask(data, msgTag, requestId));
    }


    private class WriteTask implements Runnable {
        private final long sendTime = System.currentTimeMillis();
        private final byte[] data;
        private final MsgBean.UniversalMessage.Builder msgTag;
        private final String requestId;


        private WriteTask(byte[] data, final MsgBean.UniversalMessage.Builder msgTag, String requestId) {
            this.data = data;
            this.msgTag = msgTag;
            this.requestId = requestId;
        }

        @Override
        public void run() {
            int state = 0;
            try {
                ByteBuffer writeBuf = ByteBuffer.allocate(data.length);
                writeBuf.put(data);
                writeBuf.flip();
                LogUtil.getLog().i(TAG, ">>>发送长度:" + data.length);
                if (data.length < 1024) {
                    LogUtil.getLog().i(TAG, ">>>发送:" + SocketPacket.bytesToHex(data));
                }

                state = socketChannel.write(writeBuf);
                writeBuf.clear();
                LogUtil.getLog().i(TAG, ">>>发送状态:" + state);
                // TODO 回执上传成功，需要清除回执缓存队列，不在重发
                if (msgTag == null) {
                    if (!TextUtils.isEmpty(requestId)) {
                        SendList.removeSendListJust(requestId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                CrashReport.setUserSceneTag(MyAppLication.getInstance().getApplicationContext(), BUGLY_TAG_SEND_DATA); // 上报后的Crash会显示该标签
                CrashReport.postCatchedException(e.fillInStackTrace());  // bugly会将这个throwable上报
                LogUtil.getLog().e(TAG, ">>>发送失败" + SocketPacket.bytesToHex(data));
                LogUtil.writeLog(">>>发送失败" + SocketPacket.bytesToHex(data) + " Exception:" + e.getMessage() + ">>>发送状态:" + state);
                // 上传异常数据
                CrashReport.putUserData(MyAppLication.getInstance().getApplicationContext(), BuglyTag.BUGLY_TAG_2, " Exception:" + e.getMessage() + ">>>发送状态:" + state);
                //取消发送队列,返回失败
                if (msgTag != null) {
                    SendList.removeSendList(msgTag.getRequestId());
                }

                SocketUtil.getSocketUtil().stop(false);
                SocketUtil.getSocketUtil().startSocket();
            }
        }
    }
}
