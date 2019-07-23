package com.yanlong.im.chat.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.utils.socket.SocketData;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


public class UpLoadService extends Service {
    public static Queue<UpProgress> queue = new LinkedList<>();
    public static HashMap<String ,Integer> pgms=new HashMap<>();
    private UpFileAction upFileAction = new UpFileAction();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);


    }

    public static Integer getProgress(String msgId){

        if(pgms.containsKey(msgId)){
            int pg=pgms.get(msgId);
            Log.d("getProgress", "getProgress: "+msgId+"  val:"+pg);
           return pg;
        }

        return null;
    }
    private static void updataProgress(String msgId,Integer pg){
        pgms.put(msgId,pg);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (queue.size() > 0) {
                    UpProgress upProgress = queue.poll();
                    Log.d("上传", "上传: " + upProgress.getId());
                    upFileAction.upFileSyn(getApplicationContext(), upProgress.getCallback(), upProgress.getFile());
                }
                stopSelf();
                Log.d("上传", "上传结束");
            }
        }).start();

    }

    public static void onAdd(final String id, String file, final UpFileUtil.OssUpCallback myback) {
        final UpProgress upProgress = new UpProgress();
        upProgress.setId(id);
      //  upProgress.setProgress(0);
        upProgress.setFile(file);
        updataProgress(id,0);
        upProgress.setCallback(new UpFileUtil.OssUpCallback() {
            @Override
            public void success(final String url) {

               // upProgress.setProgress(100);
                updataProgress(id,100);

                myback.success(url);
            }

            @Override
            public void fail() {
                //alert.dismiss();
                // ToastUtil.show(getContext(), "上传失败,请稍候重试");

              //  upProgress.setProgress(100);
                updataProgress(id,100);

                myback.fail();
            }

            @Override
            public void inProgress(long progress, long zong) {
                int pg =new Double( progress / (zong + 0.0f) * 100.0).intValue();

               // upProgress.setProgress(new Double(pg);
                updataProgress(id,pg);

                myback.inProgress(upProgress.getProgress(), 0);
            }
        });


        queue.offer(upProgress);
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