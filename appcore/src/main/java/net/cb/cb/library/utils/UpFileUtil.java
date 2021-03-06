package net.cb.cb.library.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.callback.OSSRetryCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.ImagePersistRequest;
import com.alibaba.sdk.android.oss.model.ImagePersistResult;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.OSSResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.google.gson.Gson;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.bean.FileBean;
import net.cb.cb.library.bean.ReturnBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import retrofit2.Call;
import retrofit2.Response;

/***
 * 文件上传备用
 * @author jyj
 * @date 2016/12/20
 */
public class UpFileUtil {

    private final String TAG = "UpFileUtil";
    private static UpFileUtil instance;

    //  private final String P_STSSERVER = "http://sts.aliyuncs.com";

    //   private final String P_BUCKETNAME = "e7-test";
    //  private final String P_ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
    //  private static final String OSS_ACCESS_KEY_ID = "STS.NK9H2WVQ7m1omvBj6rvW7pBJd";
    //  private static final String OSS_ACCESS_KEY_SECRET = "C69qyuu4y9YNtsEkNTXFo9yvrGdySJxnpfxPzhAEakQx";
    //  private static final String OSS_ACCESS_KEY_TOKEN = "CAIShgJ1q6Ft5B2yfSjIr4iMA4jju44W2vOEb1DzjjYnetgbn4fhhjz2IH1IeHVgB+wcsP4xn2BV7PgflqZiQplBQkrLKMp1q4ha6h/5v0UfTwrwv9I+k5SANTW5OXyShb3vAYjQSNfaZY3aCTTtnTNyxr3XbCirW0ffX7SClZ9gaKZ4PGS/diEURq0VRG1YpdQdKGHaONu0LxfumRCwNkdzvRdmgm4Njsbay8aHuB3Flw+4mK1H5aaJe8D9NJk9Yc8lCobph7YvJpCsinAAt0J4k45tl7FB9Dv9udWQPkJc+R3uMZCPr4EzcF8nOvJkQfAf/KWmy6Bi2uvIjML51hJJLTuOxugq6A7JGoABIuPFVFt2gARCZTZlkzkCz8h9bcYCqeqK7wWNrFz7Ur25D7hjk33tLJ6LvZNIkRZWr60NdzneF8pfiT1bj9vvSVlz483HjRVIbkccVGqacxoSWc7+T0GR8vtC6GrBbN8UFq3IjZcvknoIJBvUsUlaWxQP8BCuxFxU7HelQ1wyCv4=";

    private OSS oss;

    private SimpleDateFormat simpleDateFormat;
    private UpFileServer server;
    private PutObjectRequest putObjectRequest;
    private String endEx = "";

    public UpFileUtil() {
        server = NetUtil.getNet().create(UpFileServer.class);
    }

    public static UpFileUtil getInstance() {
        if (instance == null) {
            return new UpFileUtil();
        }
        return instance;

    }

    private void getOSs(Context context, String keyid, String secret, String token, String endpoint) {


//该配置类如果不设置，会有默认配置，具体可看该类

        ClientConfiguration conf = new ClientConfiguration();

        conf.setConnectionTimeout(15 * 1000);// 连接超时，默认15秒

        conf.setSocketTimeout(15 * 1000);// socket超时，默认15秒

        conf.setMaxConcurrentRequest(5);// 最大并发请求数，默认5个

        conf.setMaxErrorRetry(2);// 失败后最大重试次数，默认2次

        //推荐使用OSSAuthCredentialsProvider。token过期可以及时更新

    /*    OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(P_STSSERVER);

        oss = new OSSClient(context, P_ENDPOINT, credentialProvider);*/


        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(keyid, secret, token);

        oss = new OSSClient(context, endpoint, credentialProvider);

        if (simpleDateFormat == null) {

            simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        }

    }

    /**
     * 上传图片 上传文件
     *
     * @param context          application上下文对象
     * @param ossUpCallback    成功的回调
     * @param imgPath          图片的本地路径
     * @param isLocalTakeVideo 是否拍照视频
     */
    public void upFile(final String path, final Context context, final String keyid,
                       final String secret, final String token, final String endpoint, final String btName,
                       final UpFileUtil.OssUpCallback ossUpCallback, final String imgPath, final byte[] imgbyte,
                       final boolean isLocalTakeVideo) {

        getOSs(context, keyid, secret, token, endpoint);
        if (StringUtil.isNotNull(imgPath)) {
            // 获取文件的md5值，用于判断文件是否上传过
            RxJavaUtil.run(new RxJavaUtil.OnRxAndroidListener<String>() {

                @Override
                public String doInBackground() throws Throwable {
                    return Md5Util.getFileMD5(new File(imgPath));// 获取文件MD5唯一值
                }

                @Override
                public void onFinish(final String md5Rresult) {
                    if (TextUtils.isEmpty(md5Rresult)) {
                        ossUpCallback.fail();
                        return;
                    }
                    if (imgPath != null) {
                        int sEx = imgPath.lastIndexOf(".");

                        if (sEx > 0) {
                            endEx = imgPath.substring(sEx);
                        }
                    }
                    final String img_name;
                    //pc同步消息，只能用固定域名
                    if (isPcMsgPath(path)) {
                        img_name = endEx;
                    } else {
                        img_name = md5Rresult + endEx;
                    }

                    final String objkey = path + img_name;
                    if (StringUtil.isNotNull(imgPath)) {
                        putObjectRequest = new PutObjectRequest(btName, objkey, imgPath);
                    } else {
                        putObjectRequest = new PutObjectRequest(btName, objkey, imgbyte);
                    }
                    if (FileUtils.isNeedsMd5(path) && !TextUtils.isEmpty(imgPath)) {// 聊天消息文件需要极速秒传
                        if (FileUtils.isLocalTake(imgPath) || isLocalTakeVideo) {// 本地拍照、录视频不需要调用fileCheck接口，因为每次拍摄的不一样
                            setMd5Callback(md5Rresult, ossUpCallback, btName, objkey, context);
                        } else {
                            // 请求接口判断文件是否上传过
                            WeakHashMap<String, Object> param = new WeakHashMap<>();
                            param.put("md5", md5Rresult);
                            param.put("url", objkey);
                            NetUtil.getNet().exec(
                                    server.fileCheck(param)
                                    , new CallBack<ReturnBean<String>>() {
                                        @Override
                                        public void onResponse(Call<ReturnBean<String>> call, Response<ReturnBean<String>> response) {
                                            if (response.body() != null && response.body().isOk()) {
                                                // 上传过则直接拿服务器返回的地址
                                                ossUpCallback.success(oss.presignPublicObjectURL(btName, objkey));
                                            } else {
                                                setMd5Callback(md5Rresult, ossUpCallback, btName, objkey, context);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ReturnBean<String>> call, Throwable t) {
                                            super.onFailure(call, t);
                                            ossUpCallback.fail();
                                            LogUtil.writeLog("上传失败--response=null");
                                        }
                                    });
                        }
                    } else {
                        if (FileUtils.isNeedsMd5(path)) {// 保存文件MD5值并上传文件
                            setMd5Callback(md5Rresult, ossUpCallback, btName, objkey, context);
                        } else {// 不保存文件MD5值，只上传文件
                            setCallback(ossUpCallback, btName, objkey, context);
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    ossUpCallback.fail();
                }
            });
        } else {
            final String img_name;
            //pc同步消息，只能用固定域名
            if (isPcMsgPath(path)) {
                img_name = endEx;
            } else {
                img_name = UUID.randomUUID().toString() + endEx;
            }

            final String objkey = path + img_name;
            putObjectRequest = new PutObjectRequest(btName, objkey, imgbyte);
            setCallback(ossUpCallback, btName, objkey, context);
        }
    }

    /**
     * 用于极速秒传，目前只做消息聊天中的文件，
     * 描述：一、先把文件生成md5；二、检查md5是否存在（判断文件是否上传过）；三、设置服务端的回调；四、不存在则上传文件/存在直接本地拼地址返回
     *
     * @param path             上传oss的路径
     * @param context
     * @param keyid            阿里访问id
     * @param secret           秘钥
     * @param token
     * @param endpoint         文件后缀
     * @param btName
     * @param ossUpCallback
     * @param imgPath          图片本地路径
     * @param imgbyte
     * @param isLocalTakeVideo 是拍摄还是相册选择 默认是false
     */
//    private void uploadMd5File(final String path, final Context context, final String keyid, final String secret,
//                               final String token, final String endpoint, final String btName, final OssUpCallback ossUpCallback,
//                               final String imgPath, final byte[] imgbyte, final boolean isLocalTakeVideo) {
//
//        getOSs(context, keyid, secret, token, endpoint);
//        // 获取文件的md5值，用于判断文件是否上传过
//        RxJavaUtil.run(new RxJavaUtil.OnRxAndroidListener<String>() {
//
//            @Override
//            public String doInBackground() throws Throwable {
//                return Md5Util.getFileMD5(new File(imgPath));// 获取文件MD5唯一值
//            }
//
//            @Override
//            public void onFinish(final String md5Rresult) {
//                if (TextUtils.isEmpty(md5Rresult)) {
//                    ossUpCallback.fail();
//                    return;
//                }
//                if (imgPath != null) {
//                    int sEx = imgPath.lastIndexOf(".");
//
//                    if (sEx > 0) {
//                        endEx = imgPath.substring(sEx);
//                    }
//                }
//                final String img_name;
//                //pc同步消息，只能用固定域名
//                if (isPcMsgPath(path)) {
//                    img_name = endEx;
//                } else {
//                    img_name = md5Rresult + endEx;
//                }
//
//                final String objkey = path + img_name;
//                if (StringUtil.isNotNull(imgPath)) {
//                    putObjectRequest = new PutObjectRequest(btName, objkey, imgPath);
//                } else {
//                    putObjectRequest = new PutObjectRequest(btName, objkey, imgbyte);
//                }
//                if (FileUtils.isLocalTake(imgPath) || isLocalTakeVideo) {// 本地拍照、录视频不需要调用fileCheck接口，因为每次拍摄的不一样
//                    setMd5Callback(md5Rresult, ossUpCallback, btName, objkey, context);
//                } else {
//                    // 请求接口判断文件是否上传过
//                    WeakHashMap<String, Object> param = new WeakHashMap<>();
//                    param.put("md5", md5Rresult);
//                    param.put("url", objkey);
//                    NetUtil.getNet().exec(
//                            server.fileCheck(param)
//                            , new CallBack<ReturnBean<String>>() {
//                                @Override
//                                public void onResponse(Call<ReturnBean<String>> call, Response<ReturnBean<String>> response) {
//                                    if (response.body() != null && response.body().isOk()) {
//                                        // 上传过则直接拿服务器返回的地址
//                                        ossUpCallback.success(oss.presignPublicObjectURL(btName, objkey));
//                                    } else {
//                                        setMd5Callback(md5Rresult, ossUpCallback, btName, objkey, context);
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<ReturnBean<String>> call, Throwable t) {
//                                    super.onFailure(call, t);
//                                    ossUpCallback.fail();
//                                    LogUtil.writeLog("上传失败--response=null");
//                                }
//                            });
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                ossUpCallback.fail();
//            }
//        });
//    }

    /**
     * 批量检查文件是否存在
     *
     * @param list
     * @param callBack
     */
    public void batchFileCheck(ArrayList<FileBean> list, final CallBack<ReturnBean<List<String>>> callBack) {
        if (list == null || list.size() == 0) {
            return;
        }
        NetUtil.getNet().exec(
                server.batchFileCheck(list)
                , new CallBack<ReturnBean<List<String>>>() {
                    @Override
                    public void onResponse(Call<ReturnBean<List<String>>> call, Response<ReturnBean<List<String>>> response) {
                        super.onResponse(call, response);
                        callBack.onResponse(call, response);
                    }

                    @Override
                    public void onFailure(Call<ReturnBean<List<String>>> call, Throwable t) {
                        super.onFailure(call, t);
                        callBack.onFailure(call, t);
                    }
                });
    }

    /**
     * 获取文件路径的文件名
     *
     * @param path
     * @return
     */
    public String getFilePathMd5(String path) {
        try {
            String md5 = "";
            if (TextUtils.isEmpty(path)) {
                return "";
            }
            if (path.contains("/below-20k")) {
                path = path.replace("/below-20k", "");
            } else if (path.contains("/below-200k")) {
                path = path.replace("/below-200k", "");
            }
            md5 = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
            return md5;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取文件后缀
     *
     * @param path
     * @param msgType
     * @return
     */
    public String getFileUrl(String path, int msgType) {
        try {
            String url;
            if (TextUtils.isEmpty(path)) {
                return "";
            }
            if (path.contains("/below-20k")) {
                path = path.replace("/below-20k", "");
            } else if (path.contains("/below-200k")) {
                path = path.replace("/below-200k", "");
            }
            url = path.substring(path.lastIndexOf("/"));

            String tempPath = path.substring(0, path.lastIndexOf("/"));
            url = tempPath.substring(tempPath.lastIndexOf("/") + 1) + url;
            return url;
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 获取文件全路径
     *
     * @param path
     * @return
     */
    public String getFileUrl(String path) {
        try {
            String url;
            if (TextUtils.isEmpty(path)) {
                return "";
            }
            if (path.contains("/below-20k")) {
                path = path.replace("/below-20k", "");
            } else if (path.contains("/below-200k")) {
                path = path.replace("/below-200k", "");
            }
            url = path.substring(path.lastIndexOf(".com/") + 5);
            return url;
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 获取文件名 例如 ：md5.jpg
     *
     * @param path
     * @return
     */
    public String getFileName(String path) {
        try {
            String url;
            if (TextUtils.isEmpty(path)) {
                return "";
            }
            if (path.contains("/below-20k")) {
                path = path.replace("/below-20k", "");
            } else if (path.contains("/below-200k")) {
                path = path.replace("/below-200k", "");
            }
            url = path.substring(path.lastIndexOf("/"));

//            String tempPath = path.substring(0, path.lastIndexOf("/"));
//            url = tempPath.substring(tempPath.lastIndexOf("/") + 1) + url;
            return url;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取缩略图
     *
     * @param path
     * @return
     */
    public String getThumbUrl(String path, String fileName) {
        try {
            String url;
            if (TextUtils.isEmpty(path) || TextUtils.isEmpty(fileName)) {
                return "";
            }
            if (path.contains("/below-20k")) {
                path = path.replace("/below-20k", "");
            } else if (path.contains("/below-200k")) {
                path = path.replace("/below-200k", "");
            }
            url = path.substring(0, path.indexOf(".com") + 5);
            return url + fileName;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取缩略图
     *
     * @param path
     * @return
     */
    public String getThumbUrl(String path) {
        try {
            String url;
            if (TextUtils.isEmpty(path)) {
                return "";
            }
            if (path.contains("/below-20k")) {
                path = path.replace("/below-20k", "");
            } else if (path.contains("/below-200k")) {
                path = path.replace("/below-200k", "");
            }
            url = path.replace("image", "thumb");
            return url;
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 需要保存文件MD5值并上传文件
     *
     * @param md5Rresult    md5值
     * @param ossUpCallback 上传回调
     * @param btName        文件名
     * @param objkey        文件路径
     * @param context
     */
    private void setMd5Callback(String md5Rresult, final OssUpCallback ossUpCallback, final String btName,
                                final String objkey, final Context context) {
        // 设置服务端回调
        Map<String, String> callbackParam = new HashMap<>();
        Map<String, Object> bodyParam = new HashMap<>();
        bodyParam.put("mimeType", endEx);
        bodyParam.put("md5", md5Rresult);
        Long uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UID).get4Json(Long.class);
        if (uid != null) {
            bodyParam.put("uid", uid);
        }
        callbackParam.put("callbackUrl", AppConfig.getUploadBack());
        callbackParam.put("callbackBody", new Gson().toJson(bodyParam));
        callbackParam.put("callbackBodyType", "application/json");
        putObjectRequest.setCallbackParam(callbackParam);

        putObjectRequest.setRetryCallback(new OSSRetryCallback() {
            @Override
            public void onRetryCallback() {
                Log.v(TAG, "重试回调------------------>");
            }
        });

        putObjectRequest.setProgressCallback(new OSSProgressCallback() {

            @Override
            public void onProgress(Object request, long currentSize, long totalSize) {
                ossUpCallback.inProgress(currentSize, totalSize);
            }

        });

        //6.11 图片上传引起界面刷新
        oss.asyncPutObject(putObjectRequest, new OSSCompletedCallback() {

            @Override
            public void onSuccess(OSSRequest request, OSSResult result) {
                ossUpCallback.success(oss.presignPublicObjectURL(btName, objkey));
            }

            @Override
            public void onFailure(OSSRequest request, ClientException clientException, ServiceException serviceException) {
                ossUpCallback.fail();
                LogUtil.getLog().e("uplog", "---->上传异常:" + clientException.getMessage() + "\n" + serviceException.getRawMessage());
                try {
                    ToastUtil.show(context, "上传失败");
                    LogUtil.writeLog("上传失败--" + clientException.getMessage() + "\n" + serviceException.getRawMessage());
                } catch (Exception e) {

                }
            }
        });
    }

    /**
     * 不存文件MD5值只上传文件
     *
     * @param ossUpCallback
     * @param btName
     * @param objkey
     * @param context
     */
    private void setCallback(final OssUpCallback ossUpCallback, final String btName,
                             final String objkey, final Context context) {
        putObjectRequest.setRetryCallback(new OSSRetryCallback() {
            @Override
            public void onRetryCallback() {
                Log.v(TAG, "重试回调------------------>");
            }
        });

        putObjectRequest.setProgressCallback(new OSSProgressCallback() {

            @Override
            public void onProgress(Object request, long currentSize, long totalSize) {
                ossUpCallback.inProgress(currentSize, totalSize);
            }
        });

        //6.11 图片上传引起界面刷新
        oss.asyncPutObject(putObjectRequest, new OSSCompletedCallback() {

            @Override
            public void onSuccess(OSSRequest request, OSSResult result) {
                ossUpCallback.success(oss.presignPublicObjectURL(btName, objkey));
            }

            @Override
            public void onFailure(OSSRequest request, ClientException clientException, ServiceException serviceException) {
                ossUpCallback.fail();
                LogUtil.getLog().e("uplog", "---->上传异常:" + clientException.getMessage() + "\n" + serviceException.getRawMessage());
                LogUtil.writeLog("上传失败--" + clientException.getMessage() + "\n" + serviceException.getRawMessage());
                try {
                    ToastUtil.show(context, "上传失败");
                } catch (Exception e) {

                }
            }
        });
    }


    public interface OssUpCallback {

        void success(String url);

        void fail();

        void inProgress(long progress, long total);

    }

    //图片上传回调
    public interface OssImageUpCallback extends OssUpCallback {
        void success(String url, String thumb);
    }

    //是否是pc同步消息
    private boolean isPcMsgPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        if (path.contains("file/msg")) {
            return true;
        }
        return false;
    }


    /**
     * 重新处理图片，转存，以便延长缩略图生命周期
     *
     * @param context    application上下文对象
     * @param path       文件存储空间，例如图片为 /image
     * @param bucketName bucketName
     * @param fileName   文件名称 例如md5.jpg
     */
    public void saveAsFile(final String path, final Context context, final String keyId, final String secret, final String token, final String endpoint, final String bucketName, final UpFileUtil.OssUpCallback ossUpCallback, final String url, final String fileName) {
        getOSs(context, keyId, secret, token, endpoint);
        String fromBucket = bucketName;
        String fromObjectKey = getFileUrl(url);
        String toBucket = bucketName;
        final String toObjectKey = path + fileName;
        String action = "image/resize,m_lfit,w_500/quality,Q_20";

        //图片持久化请求
        ImagePersistRequest imagePersistRequest = new ImagePersistRequest(fromBucket, fromObjectKey, toBucket, toObjectKey, action);
        //6.11 图片上传引起界面刷新
        oss.asyncImagePersist(imagePersistRequest, new OSSCompletedCallback<ImagePersistRequest, ImagePersistResult>() {
            @Override
            public void onSuccess(ImagePersistRequest request, ImagePersistResult result) {
                LogUtil.getLog().i(TAG, "asyncImagePersist--success");
                ossUpCallback.success(getThumbUrl(url, toObjectKey));
            }

            @Override
            public void onFailure(ImagePersistRequest request, ClientException clientException, ServiceException serviceException) {
                LogUtil.getLog().i(TAG, "asyncImagePersist--fail");
                ossUpCallback.fail();
            }
        });
    }


    /**
     * 下载文件
     *
     * @param context
     * @param keyId
     * @param secret
     * @param token
     * @param endpoint
     * @param bucketName
     * @param ossUpCallback
     * @param url
     * @param fileSave
     */
    public void downloadFile(final Context context, final String keyId, final String secret, final String token, final String endpoint, final String bucketName, final UpFileUtil.OssUpCallback ossUpCallback, final String url, final File fileSave) {
        getOSs(context, keyId, secret, token, endpoint);
        String fromBucket = bucketName;
        String fromObjectKey = getFileUrl(url);

        //图片持久化请求
        GetObjectRequest getObjectRequest = new GetObjectRequest(fromBucket, fromObjectKey);
        //6.11 图片上传引起界面刷新
        oss.asyncGetObject(getObjectRequest, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {

            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                //开始读取数据。
                long length = result.getContentLength();
                byte[] buffer = new byte[(int) length];
                int readCount = 0;
                while (readCount < length) {
                    try {
                        readCount += result.getObjectContent().read(buffer, readCount, (int) length - readCount);
                    } catch (Exception e) {
                        OSSLog.logInfo(e.toString());
                    }
                }
                //将下载后的文件存放在指定的本地路径。
                try {
                    FileOutputStream fout = new FileOutputStream(fileSave.getAbsoluteFile());
                    fout.write(buffer);
                    fout.close();
                    ossUpCallback.success(fileSave.getAbsolutePath());
                } catch (Exception e) {
                    OSSLog.logInfo(e.toString());
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException, ServiceException serviceException) {
                ossUpCallback.fail();

            }
        });
    }

}

