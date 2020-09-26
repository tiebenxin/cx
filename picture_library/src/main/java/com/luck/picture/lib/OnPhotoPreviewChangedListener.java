package com.luck.picture.lib;

import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * @author Geoff
 * @date 2020/9/23
 * Description
 */
public interface OnPhotoPreviewChangedListener {
    /**
     * 已选Media回调
     *
     * @param selectImages
     */
    void onUpdateChange(List<LocalMedia> selectImages);

    /**
     * 图片预览回调
     *
     * @param media
     * @param position
     */
    void onPicturePrviewClick(LocalMedia media, int position);
}
