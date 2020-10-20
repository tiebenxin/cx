package com.yanlong.im.circle.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.luck.picture.lib.entity.AttachmentBean;
import com.yanlong.im.R;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.utils.StringUtil;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-10
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class ShowImagesAdapter extends BaseQuickAdapter<AttachmentBean, BaseViewHolder> {

    public ShowImagesAdapter() {
        super(R.layout.item_circle_image);
    }

    @Override
    protected void convert(BaseViewHolder helper, AttachmentBean attachmentBean) {
        ImageView ivImg = helper.getView(R.id.iv_img);
        Glide.with(mContext).load(StringUtil.loadThumbnail(attachmentBean.getUrl()))
                .apply(GlideOptionsUtil.circleImageOptions()).into(ivImg);
    }
}
