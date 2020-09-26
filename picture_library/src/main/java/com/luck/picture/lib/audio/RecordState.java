package com.luck.picture.lib.audio;

/**
 * 包路径：com.hanming.education.audio
 * 类描述： 
 * 创建时间：2019/11/22  16:26
 * 修改人：
 * 修改时间：2019/11/22  16:26
 * 修改备注：
 */
public enum RecordState {
    // 开始录音
    START,
    // 录音中
    RECORDING,
    // 用户准备取消
    TO_CANCEL,
    // 最长录音时间开到
    TO_TIMEOUT,
}
