package net.cb.cb.library.utils;

import net.cb.cb.library.bean.AliObsConfigBean;
import net.cb.cb.library.bean.FileBean;
import net.cb.cb.library.bean.HuaweiObsConfigBean;
import net.cb.cb.library.bean.ReturnBean;

import java.util.ArrayList;
import java.util.WeakHashMap;

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

    /**
     * 单个文件check
     *
     * @param param
     * @return
     */
    @POST("/high-speed/check")
    Call<ReturnBean<String>> fileCheck(@Body WeakHashMap<String, Object> param);

    /**
     * 批量检查文件是否存在
     *
     * @param param
     * @return
     */
    @POST("/high-speed/batch-check")
    Call<ReturnBean<String>> batchFileCheck(@Body ArrayList<FileBean> param);
}
