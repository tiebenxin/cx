package com.widgt;

/**
 * @author Liszt
 * @date 2020/6/6
 * Description
 */
public interface CameraCallBack {
    /*
     * 照片拍摄成功
     * */
    void takePhoneSuccess(String imagePath);


    //视频录制成功
    void recordSuccess();
}
