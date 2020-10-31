package com.luck.picture.lib;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-23
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class PictureEnum {

    /**
     * 录音常量
     */
    @IntDef({EAudioType.NO_AUDIO, EAudioType.ING_AUDIO, EAudioType.STOP_AUDIO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EAudioType {
        int NO_AUDIO = 0;// 未录音
        int ING_AUDIO = 1;// 正在录音
        int STOP_AUDIO = 2;// 停止录音
    }

    /**
     * 说说类型(0:无|1:图片|2:语音|4:视频|5:包含图片和视频|8:投票|9:包含图片和投票|10:包含语音和投票|12:包含视频和投票|13:包含图片、视频和投票)
     */
    @IntDef({EContentType.TXT, EContentType.PICTRUE, EContentType.VOICE, EContentType.VIDEO, EContentType.VIDEO_AND_PICTRUE, EContentType.VOTE,
            EContentType.PICTRUE_AND_VOTE, EContentType.VOICE_AND_VOTE, EContentType.VIDEO_AND_VOTE, EContentType.PICTRUE_AND_VIDEO_VOTE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EContentType {
        int TXT = 0;// 文本
        int PICTRUE = 1;// 图片
        int VOICE = 2;// 语音
        int VIDEO = 4;// 视频
        int VIDEO_AND_PICTRUE = 5;// 包含图片和视频
        int VOTE = 8;// 投票
        int PICTRUE_AND_VOTE = 9;// 包含图片和投票
        int VOICE_AND_VOTE = 10;// 包含语音和投票
        int VIDEO_AND_VOTE = 12;// 包含视频和投票
        int PICTRUE_AND_VIDEO_VOTE = 13;// 包含图片、视频和投票
    }

    @IntDef({EVoteType.TXT, EVoteType.PICTRUE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EVoteType {
        int TXT = 1;// 文字
        int PICTRUE = 2;// 图片
    }

    @IntDef({ELikeType.NO, ELikeType.YES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ELikeType {
        int NO = 0;// 文字
        int YES = 1;// 图片
    }
}
