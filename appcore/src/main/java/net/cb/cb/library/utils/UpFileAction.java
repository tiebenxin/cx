package net.cb.cb.library.utils;

import android.content.Context;
import android.util.Log;

import net.cb.cb.library.bean.AliObsConfigBean;
import net.cb.cb.library.bean.ReturnBean;

import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Response;

/***
 * @author jyj
 * @date 2016/12/20
 */
public class UpFileAction {
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
    public void upFile(Context context, UpFileUtil.OssUpCallback callback, String filePath) {
        upFile(context, callback, filePath, null);
    }

    public void upFile(Context context, UpFileUtil.OssUpCallback callback, byte[] fileByte) {
        upFile(context, callback, null, fileByte);
    }


    private void upFile(final Context context, final UpFileUtil.OssUpCallback callback, final String filePath, final byte[] fileByte) {

        NetUtil.getNet().exec(
                server.aliObs()
                , new CallBack<ReturnBean<AliObsConfigBean>>() {
                    @Override
                    public void onResponse(Call<ReturnBean<AliObsConfigBean>> call, Response<ReturnBean<AliObsConfigBean>> response) {
                        if (response.body() == null) {
                            return;
                        }
                        if (response.body().isOk()) {
                          final  AliObsConfigBean configBean = response.body().getData();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    UpFileUtil.getInstance().upFile(context, configBean.getAccessKeyId(),
                                            configBean.getAccessKeySecret(), configBean.getSecurityToken(), configBean.getEndpoint(),
                                            configBean.getBucket(), callback, filePath, fileByte);
                                }
                            }).start();
                        }else{
                            ToastUtil.show(context,"上传失败");
                        }


                    }
                });


    }
    CountDownLatch signal;

    public void upFileSyn(final Context context, final UpFileUtil.OssUpCallback callback, final String filePath) {

        signal = new CountDownLatch(1);


                NetUtil.getNet().exec(
                        server.aliObs()
                        , new CallBack<ReturnBean<AliObsConfigBean>>() {
                            @Override
                            public void onResponse(Call<ReturnBean<AliObsConfigBean>> call, final Response<ReturnBean<AliObsConfigBean>> response) {
                                Log.d("cc", "upFileSyn: onResponse");
                                if (response.body() == null) {
                                    signal.countDown();
                                    return;
                                }
                                if (response.body().isOk()) {
                                    final  AliObsConfigBean configBean = response.body().getData();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            UpFileUtil.getInstance().upFile(context, configBean.getAccessKeyId(),
                                                    configBean.getAccessKeySecret(), configBean.getSecurityToken(), configBean.getEndpoint(),
                                                    configBean.getBucket(), new UpFileUtil.OssUpCallback(){

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
                                                            callback.inProgress(progress,zong);
                                                        }
                                                    }, filePath, null);
                                        }
                                    }).start();


                                }else{
                                    signal.countDown();
                                    ToastUtil.show(context,"上传失败");
                                }


                            }

                            @Override
                            public void onFailure(Call<ReturnBean<AliObsConfigBean>> call, Throwable t) {
                                signal.countDown();
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

}

