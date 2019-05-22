package net.cb.cb.library.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.OSSResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import net.cb.cb.library.AppConfig;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
 * 文件上传备用
 * @author jyj
 * @date 2016/12/20
 */
public class UpFileUtil {

    private final String TAG = "UpFileUtil";


    private static UpFileUtil instance;

    private final String P_ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";//主机地址（OSS文档中有提到）

    private final String P_STSSERVER = AppConfig.getUrlHost() + "get_aliyun_oss_sts";

    private final String P_BUCKETNAME = "文件夹名字";

    public static final String OSS_ACCESS_KEY_ID = "<yourAccessKeyId>";
    ;
    public static final String OSS_ACCESS_KEY_SECRET = "<yourAccessKeySecret>";
    private static final String OSS_ACCESS_KEY_TOKEN = "token";
    private OSS oss;

    private SimpleDateFormat simpleDateFormat;

    public UpFileUtil() {

    }

    public static UpFileUtil getInstance() {

        if (instance == null) {

            if (instance == null) {

                return new UpFileUtil();

            }

        }

        return instance;

    }

    private void getOSs(Context context) {


//该配置类如果不设置，会有默认配置，具体可看该类

        ClientConfiguration conf = new ClientConfiguration();

        conf.setConnectionTimeout(15 * 1000);// 连接超时，默认15秒

        conf.setSocketTimeout(15 * 1000);// socket超时，默认15秒

        conf.setMaxConcurrentRequest(5);// 最大并发请求数，默认5个

        conf.setMaxErrorRetry(2);// 失败后最大重试次数，默认2次

        //推荐使用OSSAuthCredentialsProvider。token过期可以及时更新

    /*    OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(P_STSSERVER);

        oss = new OSSClient(context, P_ENDPOINT, credentialProvider);*/


        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(OSS_ACCESS_KEY_ID, OSS_ACCESS_KEY_SECRET, OSS_ACCESS_KEY_TOKEN);

        oss = new OSSClient(context, P_ENDPOINT, credentialProvider);


        if (simpleDateFormat == null) {

            simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        }

    }

    /**
     * 上传图片 上传文件
     *
     * @param context       application上下文对象
     * @param ossUpCallback 成功的回调
     * @param img_name      上传到oss后的文件名称，图片要记得带后缀 如：.jpg
     * @param imgPath       图片的本地路径
     */

    public void upImage(Context context, final UpFileUtil.OssUpCallback ossUpCallback, final String img_name, String imgPath) {

        getOSs(context);

        final Date data = new Date();

        data.setTime(System.currentTimeMillis());

        PutObjectRequest putObjectRequest = new PutObjectRequest(P_BUCKETNAME, simpleDateFormat.format(data) + "/" + img_name, imgPath);

        putObjectRequest.setProgressCallback(new OSSProgressCallback() {

            @Override
            public void onProgress(Object request, long currentSize, long totalSize) {
                ossUpCallback.inProgress(currentSize, totalSize);
            }


        });

        oss.asyncPutObject(putObjectRequest, new OSSCompletedCallback() {

            @Override
            public void onSuccess(OSSRequest request, OSSResult result) {
                ossUpCallback.successImg(oss.presignPublicObjectURL(P_BUCKETNAME, simpleDateFormat.format(data) + "/" + img_name));
            }

            @Override
            public void onFailure(OSSRequest request, ClientException clientException, ServiceException serviceException) {
                ossUpCallback.successImg(null);
            }


        });

    }

    /**
     * 上传图片 上传流
     *
     * @param context       application上下文对象
     * @param ossUpCallback 成功的回调
     * @param img_name      上传到oss后的文件名称，图片要记得带后缀 如：.jpg
     * @param imgbyte       图片的byte数组
     */

    public void upImage(Context context, final UpFileUtil.OssUpCallback ossUpCallback, final String img_name, byte[] imgbyte) {

        getOSs(context);

        final Date data = new Date();

        data.setTime(System.currentTimeMillis());

        PutObjectRequest putObjectRequest = new PutObjectRequest(P_BUCKETNAME, simpleDateFormat.format(data) + "/" + img_name, imgbyte);

        putObjectRequest.setProgressCallback(new OSSProgressCallback() {

            @Override
            public void onProgress(Object request, long currentSize, long totalSize) {
                ossUpCallback.inProgress(currentSize, totalSize);
            }


        });

        oss.asyncPutObject(putObjectRequest, new OSSCompletedCallback() {

            @Override
            public void onSuccess(OSSRequest request, OSSResult result) {
                ossUpCallback.successImg(oss.presignPublicObjectURL(P_BUCKETNAME, simpleDateFormat.format(data) + "/" + img_name));

            }

            @Override
            public void onFailure(OSSRequest request, ClientException clientException, ServiceException serviceException) {
                ossUpCallback.successImg(null);

            }


        });

    }

    /**
     * 上传视频
     *
     * @param context       application上下文对象
     * @param ossUpCallback 成功的回调
     * @param video_name    上传到oss后的文件名称，视频要记得带后缀 如：.mp4
     * @param video_path    视频的本地路径
     */

    public void upVideo(Context context, final UpFileUtil.OssUpCallback ossUpCallback, final String video_name, String video_path) {

        getOSs(context);

        final Date data = new Date();

        data.setTime(System.currentTimeMillis());

        PutObjectRequest putObjectRequest = new PutObjectRequest(P_BUCKETNAME, simpleDateFormat.format(data) + "/" + video_name, video_path);

        putObjectRequest.setProgressCallback(new OSSProgressCallback() {

            @Override
            public void onProgress(Object request, long currentSize, long totalSize) {
                ossUpCallback.inProgress(currentSize, totalSize);
            }


        });

        oss.asyncPutObject(putObjectRequest, new OSSCompletedCallback() {

            @Override
            public void onSuccess(OSSRequest request, OSSResult result) {
                ossUpCallback.successVideo(oss.presignPublicObjectURL(P_BUCKETNAME, simpleDateFormat.format(data) + "/" + video_name));

            }

            @Override
            public void onFailure(OSSRequest request, ClientException clientException, ServiceException serviceException) {
                ossUpCallback.successVideo(null);

            }


        });

    }

    public interface OssUpCallback {

        void successImg(String img_url);

        void successVideo(String video_url);

        void inProgress(long progress, long zong);

    }

}

