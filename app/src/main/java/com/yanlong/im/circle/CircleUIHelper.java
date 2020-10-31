package com.yanlong.im.circle;

import com.luck.picture.lib.PictureEnum;
import com.yanlong.im.circle.adapter.CircleFlowAdapter;

/**
 * @author Liszt
 * @date 2020/10/30
 * Description
 */
public class CircleUIHelper {

    public static int getHolderType(@PictureEnum.EContentType int type) {
        switch (type) {
            case PictureEnum.EContentType.VOTE:
            case PictureEnum.EContentType.PICTRUE_AND_VOTE:
            case PictureEnum.EContentType.VOICE_AND_VOTE:
            case PictureEnum.EContentType.VIDEO_AND_VOTE:
            case PictureEnum.EContentType.PICTRUE_AND_VIDEO_VOTE:
                return CircleFlowAdapter.MESSAGE_VOTE;
            default:
                return CircleFlowAdapter.MESSAGE_DEFAULT;
        }
    }
}
