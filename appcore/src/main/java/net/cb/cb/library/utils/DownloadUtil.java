package net.cb.cb.library.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.download.FileDownLoadListener;
import com.android.volley.toolbox.download.FileDownloader;
import com.kye.net.NetRequestHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadUtil {
    private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;
    public final String TAG = "DownloadUtil";

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    public DownloadUtil() {
        okHttpClient = new OkHttpClient();
    }


    /**
     * @param url          下载连接
     * @param destFileDir  下载的文件储存目录
     * @param destFileName 下载文件名称
     * @param listener     下载监听
     */
    public Call download(final String url, final String destFileDir, final String destFileName, final OnDownloadListener listener) {
        Call call;
        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();

        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //异步请求
        call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败监听回调
                listener.onDownloadFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                //储存下载文件的目录
                File dir = new File(destFileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, destFileName);

                try {

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = new Double(sum * 1.0d / total * 100d).intValue();
                        //下载中更新进度条
                        listener.onDownloading(progress);
                    }
                    fos.flush();
                    //下载完成
                    listener.onDownloadSuccess(file);
                    Log.v(TAG, file.getAbsolutePath() + "--下载完成");

                } catch (Exception e) {
                    listener.onDownloadFailed(e);
                } finally {

                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {

                    }

                }
            }
        });
        return call;
    }


    /**
     * @param url          下载连接
     * @param destFileDir  下载的文件储存目录
     * @param destFileName 下载文件名称
     */
    public void download(final String url, final String destFileDir, final String destFileName) {

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();

        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //异步请求
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败监听回调
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                //储存下载文件的目录
                File dir = new File(destFileDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, destFileName);

                try {

                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        //下载中更新进度条
                    }
                    fos.flush();
                    Log.v("DownloadUtil", file.getAbsolutePath() + "--下载完成");
                    //下载完成
                } catch (Exception e) {

                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {

                    }

                }
            }
        });
    }


    public interface OnDownloadListener {

        /**
         * 下载成功之后的文件
         */
        void onDownloadSuccess(File file);

        /**
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载异常信息
         */
        void onDownloadFailed(Exception e);
    }

    public interface IDownloadVoiceListener {

        /**
         * 下载成功之后的文件
         */
        void onDownloadSuccess(File file);

        /**
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载异常信息
         */
        void onDownloadFailed(Exception e);
    }

    /**
     * 文件开始下载.支持断点续传功能
     *
     * @param downloadUrl 下载连接
     * @param savePath    保存路径
     * @param listener    下载监听
     */
    public void downLoadFile(String downloadUrl, File savePath, final OnDownloadListener listener) {
        try {
            if (TextUtils.isEmpty(downloadUrl)) {
                return;
            }
            // 1, 开始下载
            NetRequestHelper netRequestHelper = NetRequestHelper.getInstance();
            FileDownloader.DownloadController downloadController = netRequestHelper.getDownloadController(savePath, downloadUrl);
            // 2, 如果当前下载任务正在执行，则无序重新建立下载任务
            FileDownLoadListener<Void> downLoadListener = generateDownloadLIstener(listener);
            if (downloadController != null) {
                // a, 重新设置监听器
                if (!downloadController.isDownloading()) {
                    try {
                        Field field = downloadController.getClass().getDeclaredField("mListener");
                        if (null != field) {
                            field.setAccessible(true);
                            field.set(downloadController, downLoadListener);
                        }
                    } catch (Exception e) {
                        LogUtil.getLog().e(TAG, e.getMessage());
                    }

                    // b, 继续下载
                    downloadController.resume();
                }
            } else {
                // 3, 否则重新建立下载任务
                NetRequestHelper.getInstance().addDownLoadFile(savePath, downloadUrl, downLoadListener, true);
            }


        } catch (Exception e) {
            LogUtil.getLog().e(TAG, e.getMessage());
        }
    }

    @NonNull
    private FileDownLoadListener<Void> generateDownloadLIstener(final OnDownloadListener listener) {
        return new FileDownLoadListener<Void>() {
            @Override
            public void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(Void response) {
                super.onSuccess(response);
                listener.onDownloadSuccess(null);
            }

            @Override
            public void onError(VolleyError error) {
                super.onError(error);
                listener.onDownloadFailed(error);
            }

            @Override
            public void onCancel() {
                super.onCancel();
            }

            @Override
            public void onProgressChange(long fileSize, long downloadedSize) {
                float currentPercent = downloadedSize / (float) fileSize * 100;
                listener.onDownloading((int) currentPercent);
            }

            @Override
            public void onRetry() {
                super.onRetry();
            }
        };

    }
}
