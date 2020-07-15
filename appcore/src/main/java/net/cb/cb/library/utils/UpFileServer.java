package net.cb.cb.library.utils;

import net.cb.cb.library.bean.AliObsConfigBean;
import net.cb.cb.library.bean.HuaweiObsConfigBean;
import net.cb.cb.library.bean.ReturnBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/***
 * 文件上传
 * @author jyj
 * @date 2016/12/20
 */
public interface UpFileServer {

    @POST("/api/pad/v1/hwParam")
    Call<ReturnBean<HuaweiObsConfigBean>> haweiObs();


    @POST("/user/get-oss-security-token")
    Call<ReturnBean<AliObsConfigBean>> aliObs();

    @POST("/app/log/upload")
    Observable<ResponseBody> uploadLog(@Body RequestBody requestBody);
}
