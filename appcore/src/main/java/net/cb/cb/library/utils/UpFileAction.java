package net.cb.cb.library.utils;

import android.content.Context;

import net.cb.cb.library.bean.AliObsConfigBean;
import net.cb.cb.library.bean.ReturnBean;

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


                                    UpFileUtil.getInstance().upFile(context, configBean.getAccessKeyId(), configBean.getAccessKeySecret(), configBean.getSecurityToken(), configBean.getEndpoint(),
                                            configBean.getBucket(), callback, filePath, fileByte);
                                }
                            }).start();
                        }else{
                            ToastUtil.show(context,"上传失败");
                        }


                    }
                });


    }

}

