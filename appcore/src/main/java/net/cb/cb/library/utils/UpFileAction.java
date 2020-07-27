package net.cb.cb.library.utils;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.bean.AliObsConfigBean;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.inter.IUploadListener;
import net.cb.cb.library.net.FileUploadObserver;
import net.cb.cb.library.net.RequestUploadFileBody;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/***
 * @author jyj
 * @date 2016/12/20
 */
public class UpFileAction {
    public static enum PATH {
        HEAD, HEAD_GROUP, COMPLAINT, FEEDBACK, IMG, VOICE, HEAD_GROUP_CHANGE, VIDEO, FILE, PC_MSG
    }

    private UpFileServer server;

    public UpFileAction() {
        server = NetUtil.getNet().create(UpFileServer.class);
    }


    /*    public void haweiObs(CallBack<ReturnBean<HuaweiObsConfigBean>> callback) {
            NetUtil.getNet().exec(
                    server.haweiObs()
                    , callback);
        }*/

    /***
     * 文件上传
     * @param context
     * @param callback
     * @param filePath
     */
    public void upFile(PATH type, Context context, UpFileUtil.OssUpCallback callback, String filePath) {
        upFile(type, context, callback, filePath, null, false);
    }


    public void upFile(PATH type, Context context, UpFileUtil.OssUpCallback callback, String filePath, boolean isLocalTake) {
        upFile(type, context, callback, filePath, null, isLocalTake);
    }

    public void upFile(String id, PATH type, Context context, UpFileUtil.OssUpCallback callback, String filePath) {
        upFile(type, context, callback, filePath, null, id);
    }

    //置顶固定文件名，使用pc消息同步
    public void upFile(String id, PATH type, String fileName, Context context, UpFileUtil.OssUpCallback callback, String filePath) {
        upFile(type, context, callback, filePath, null, id, fileName);
    }

    public void upFile(PATH type, Context context, UpFileUtil.OssUpCallback callback, byte[] fileByte) {
        upFile(type, context, callback, null, fileByte, false);
    }

    public String getPath(PATH type, String id) {
        Date data = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String pt = "";
        switch (type) {
            case IMG:
                data.setTime(System.currentTimeMillis());
                pt = "image/";
                break;
            case COMPLAINT:
                pt = AppConfig.getUpPath() + "/misc/complaint/";
                break;
            case FEEDBACK:
                pt = AppConfig.getUpPath() + "/misc/feedback/";
                break;
            case VOICE:
                pt = "voice/";
                break;
            case HEAD_GROUP_CHANGE:
            case HEAD_GROUP:
            case HEAD:
                pt = AppConfig.getUpPath() + "/avatar/android/" + id + "/";
                break;
            case VIDEO:
                pt = "video/";
                break;
            case FILE:
                pt = "file/";
                break;
            case PC_MSG:
                pt = AppConfig.getUpPath() + "/file/msg/" + id + "/" + simpleDateFormat.format(data);
                break;
            default:
                data.setTime(System.currentTimeMillis());
                pt = "/" + AppConfig.getUpPath() + "/android/";
                break;

        }
        return pt;
    }

    public String getPath(PATH type, String id, String fileName) {
        String pt = "";
        switch (type) {
            case PC_MSG:
                pt = AppConfig.getUpPath() + "/file/msg/" + id + "/" + fileName;
                break;
            default:
                pt = "/" + AppConfig.getUpPath() + "/android/" + fileName;
                break;

        }
        return pt;
    }

    private Long startTime = 0L;

    private void upFile(final PATH type, final Context context, final UpFileUtil.OssUpCallback callback, final String filePath, final byte[] fileByte, final boolean isLocalTake) {
        startTime = SystemClock.currentThreadTimeMillis();
        NetUtil.getNet().exec(
                server.aliObs()
                , new CallBack<ReturnBean<AliObsConfigBean>>() {
                    @Override
                    public void onResponse(Call<ReturnBean<AliObsConfigBean>> call, Response<ReturnBean<AliObsConfigBean>> response) {
                        if (response.body() == null) {
                            callback.fail();
                            LogUtil.writeLog("上传失败--response=null");
                            return;
                        }
                        if (response.body().isOk()) {
                            final AliObsConfigBean configBean = response.body().getData();
                            if (!StringUtil.isNotNull(configBean.getSecurityToken())) {

                                callback.fail();
                                return;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    long timeCost = SystemClock.currentThreadTimeMillis() - startTime;
                                    String endpoint;
                                    endpoint = getFixCdn(configBean.getCdnEndpoint(), configBean.getEndpoint());

//                                    if (PATH.VIDEO == type) {
//                                        endpoint = getFixCdn(configBean.getCdnEndpoint());
//                                    } else {
//                                        endpoint = configBean.getEndpoint();
//                                    }
                                    UpFileUtil.getInstance().upFile(getPath(type, ""), context, configBean.getAccessKeyId(),
                                            configBean.getAccessKeySecret(), configBean.getSecurityToken(), endpoint,
                                            configBean.getBucket(), callback, filePath, fileByte, isLocalTake);

                                    UpLoadUtils.getInstance().upLoadLog(timeCost + "--------" + configBean.toString());
                                }
                            }).start();
                        } else {
                            ToastUtil.show(context, "上传失败");
                            LogUtil.writeLog("上传失败--" + response.body().toString());
                        }


                    }

                    @Override
                    public void onFailure(Call<ReturnBean<AliObsConfigBean>> call, Throwable t) {
                        super.onFailure(call, t);
                        callback.fail();
                        long timeCost = SystemClock.currentThreadTimeMillis() - startTime;
                        UpLoadUtils.getInstance().upLoadLog(timeCost + "--------失败" + call.request().body().toString());
                        if (call != null) {
                            LogUtil.writeLog("上传失败--" + call.request().body().toString());
                        }
                    }
                });


    }


    private void upFile(final PATH type, final Context context, final UpFileUtil.OssUpCallback callback, final String filePath, final byte[] fileByte, final String id) {
        startTime = SystemClock.currentThreadTimeMillis();
        NetUtil.getNet().exec(
                server.aliObs()
                , new CallBack<ReturnBean<AliObsConfigBean>>() {
                    @Override
                    public void onResponse(Call<ReturnBean<AliObsConfigBean>> call, Response<ReturnBean<AliObsConfigBean>> response) {
                        if (response.body() == null) {
                            LogUtil.writeLog("上传失败--response=null");
                            callback.fail();
                            return;
                        }
                        if (response.body().isOk()) {
                            final AliObsConfigBean configBean = response.body().getData();
                            if (!StringUtil.isNotNull(configBean.getSecurityToken())) {

                                callback.fail();
                                return;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    long timeCost = SystemClock.currentThreadTimeMillis() - startTime;
                                    String endpoint;
//                                    String textToken="CAISmQJ1q6Ft5B2yfSjIr4nRCOvagOxqwLPZMGfB3TIGb9trm5TGuzz2IHtLfXhvAu8Zs/oyn29Z5/sflqZiQplBQkrLKMp1q4ha6h/51G8UT3bwv9I+k5SANTW5OXyShb3vAYjQSNfaZY3aCTTtnTNyxr3XbCirW0ffX7SClZ9gaKZ4PGS/diEURq0VRG1YpdQdKGHaONu0LxfumRCwNkdzvRdmgm4Njsbay8aHuB3Flw+4mK1H5aaJe8j7NZcyZcgvC4rsg7UrL5CsinAAt0J4k45tl7FB9Dv9udWQPkJc+R3uMZCPqYI2fVAiOfdnRfMf86mtyKBiyeXXlpXqzRFWJv1SUCnZS42mzdHNBOSzLNE9eKYM8cVEal1OXRqAAakBtm9ZuHW+cnfVxK4PJgmkPwBpMXLZ99oYyk+5E8jbZ4ArgAtdawN2i/syq8GrlHbVwOkvgeeF+nesQdgbKb86a7ZTHsawGZjzvi+xN6FlQwXMw2EA1tb/Wokcz3+EUxE3RLt6CuQ7PNxk65mvIgWqiyFLUozRV3sAUbElSds+";
//                                    UpFileUtil.getInstance().upFile(getPath(type), context, configBean.getAccessKeyId(),
//                                            configBean.getAccessKeySecret(), "", configBean.getEndpoint(),
//                                            configBean.getBucket(), callback, filePath, fileByte);

                                    endpoint = getFixCdn(configBean.getCdnEndpoint(), configBean.getEndpoint());

//                                    if (PATH.VIDEO == type) {
//                                        endpoint = getFixCdn(configBean.getCdnEndpoint());
//                                    } else {
//                                        endpoint = configBean.getEndpoint();
//                                    }
                                    UpFileUtil.getInstance().upFile(getPath(type, id), context, configBean.getAccessKeyId(),
                                            configBean.getAccessKeySecret(), configBean.getSecurityToken(), endpoint,
                                            configBean.getBucket(), callback, filePath, fileByte,false);

                                    UpLoadUtils.getInstance().upLoadLog(timeCost + "--------" + configBean.toString());
                                }
                            }).start();
                        } else {
                            ToastUtil.show(context, "上传失败");
                            LogUtil.writeLog("上传失败--" + response.body().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnBean<AliObsConfigBean>> call, Throwable t) {
                        super.onFailure(call, t);
                        callback.fail();
                        long timeCost = SystemClock.currentThreadTimeMillis() - startTime;
                        UpLoadUtils.getInstance().upLoadLog(timeCost + "--------失败" + call.request().body().toString());
                        if (call != null) {
                            LogUtil.writeLog("上传失败--" + call.request().body().toString());
                        }
                    }
                });


    }

    private void upFile(final PATH type, final Context context, final UpFileUtil.OssUpCallback callback, final String filePath, final byte[] fileByte, final String id, final String fileName) {
        startTime = SystemClock.currentThreadTimeMillis();
        NetUtil.getNet().exec(
                server.aliObs()
                , new CallBack<ReturnBean<AliObsConfigBean>>() {
                    @Override
                    public void onResponse(Call<ReturnBean<AliObsConfigBean>> call, Response<ReturnBean<AliObsConfigBean>> response) {
                        if (response.body() == null) {
                            LogUtil.writeLog("上传失败--response = null");
                            callback.fail();
                            return;
                        }
                        if (response.body().isOk()) {
                            final AliObsConfigBean configBean = response.body().getData();
                            if (!StringUtil.isNotNull(configBean.getSecurityToken())) {

                                callback.fail();
                                return;
                            }
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    long timeCost = SystemClock.currentThreadTimeMillis() - startTime;
                                    String endpoint;

                                    endpoint = getFixCdn(configBean.getCdnEndpoint(), configBean.getEndpoint());

//                                    if (PATH.VIDEO == type) {
//                                        endpoint = getFixCdn(configBean.getCdnEndpoint());
//                                    } else {
//                                        endpoint = configBean.getEndpoint();
//                                    }
                                    UpFileUtil.getInstance().upFile(getPath(type, id, fileName), context, configBean.getAccessKeyId(),
                                            configBean.getAccessKeySecret(), configBean.getSecurityToken(), endpoint,
                                            configBean.getBucket(), callback, filePath, fileByte,false);

                                    UpLoadUtils.getInstance().upLoadLog(timeCost + "--------" + configBean.toString());
                                }
                            }).start();
                        } else {
                            ToastUtil.show(context, "上传失败");
                            if (call != null) {
                                LogUtil.writeLog("上传失败--" + call.request().body().toString());
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<ReturnBean<AliObsConfigBean>> call, Throwable t) {
                        super.onFailure(call, t);
                        callback.fail();
                        long timeCost = SystemClock.currentThreadTimeMillis() - startTime;
                        UpLoadUtils.getInstance().upLoadLog(timeCost + "--------失败" + call.request().body().toString());
                        if (call != null) {
                            LogUtil.writeLog("上传失败--" + call.request().body().toString());
                        }

                    }
                });


    }


    CountDownLatch signal;

    public void upFileSyn(final PATH type, final Context context, final UpFileUtil.OssUpCallback callback, String filePath) {

        if (filePath.startsWith("file://")) {
            filePath = filePath.replace("file://", "");
        }
        final String filep = filePath;
        signal = new CountDownLatch(1);


        NetUtil.getNet().exec(
                server.aliObs()
                , new CallBack<ReturnBean<AliObsConfigBean>>() {
                    @Override
                    public void onResponse(Call<ReturnBean<AliObsConfigBean>> call, final Response<ReturnBean<AliObsConfigBean>> response) {
                        Log.d("cc", "upFileSyn: onResponse");
                        if (response.body() == null) {
                            callback.fail();
                            signal.countDown();
                            return;
                        }
                        if (response.body().isOk()) {
                            final AliObsConfigBean configBean = response.body().getData();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String endpoint;
                                    if (PATH.VIDEO == type) {
                                        endpoint = getFixCdn(configBean.getCdnEndpoint(), configBean.getEndpoint());
                                    } else {
                                        endpoint = configBean.getEndpoint();
                                    }
                                    UpFileUtil.getInstance().upFile(getPath(type, ""), context, configBean.getAccessKeyId(),
                                            configBean.getAccessKeySecret(), configBean.getSecurityToken(), endpoint,
                                            configBean.getBucket(), new UpFileUtil.OssUpCallback() {

                                                @Override
                                                public void success(String url) {
                                                    Log.d("cc", "upFileSyn: success");
                                                    signal.countDown();
                                                    callback.success(url);
                                                }

                                                @Override
                                                public void fail() {
                                                    signal.countDown();
                                                    callback.fail();
                                                }

                                                @Override
                                                public void inProgress(long progress, long zong) {
                                                    callback.inProgress(progress, zong);
                                                }
                                            }, filep, null,false);
                                }
                            }).start();


                        } else {
                            signal.countDown();
                            callback.fail();
                            ToastUtil.show(context, "上传失败");
                        }


                    }

                    @Override
                    public void onFailure(Call<ReturnBean<AliObsConfigBean>> call, Throwable t) {
                        signal.countDown();
                        callback.fail();
                        super.onFailure(call, t);
                    }
                });


        try {
            signal.await();
            Log.d("", "upFileSyn: await");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String getFixCdn(String cdn, String endpoint) {
        if (!TextUtils.isEmpty(cdn)) {
            if (cdn.contains("oss-accelerate.aliyuncs.com")) {
                return "http://oss-accelerate.aliyuncs.com";
            }
            return cdn;
        }
        return endpoint;
    }

    public void uploadLogFile(File file, String date, final IUploadListener listener) {
        if (file == null || !file.exists() || TextUtils.isEmpty(date)) {
            listener.onFailed();
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        builder.addFormDataPart("log-date", date);
        builder.addFormDataPart("log-file", file.getName(), requestBody);
        FileUploadObserver<ResponseBody> fileUploadObserver = new FileUploadObserver<ResponseBody>() {
            @Override
            public void onUpLoadSuccess(ResponseBody responseBody) {
                if (responseBody != null) {
                    try {
                        listener.onSuccess(responseBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    listener.onFailed();
                }
            }

            @Override
            public void onUpLoadFail(Throwable e) {
                e.printStackTrace();
                listener.onFailed();

            }

            @Override
            public void onProgress(int progress) {
//                listener.onProgress(progress);

            }
        };
        Observable<ResponseBody> observable = NetUtil.getNet().getUpFileServer().uploadLog(new RequestUploadFileBody(builder.build(), fileUploadObserver));
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fileUploadObserver);
    }


}

