package com.yanlong.im.circle.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.luck.picture.lib.entity.AttachmentBean;
import com.luck.picture.lib.widget.SquareRelativeLayout;
import com.yanlong.im.R;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.utils.ScreenUtil;
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
        RelativeLayout rlParent = helper.getView(R.id.rl_parent);
        ImageView ivImg = helper.getView(R.id.iv_img);
        CardView cardView = helper.getView(R.id.card_view);
        resize(rlParent, cardView);
        String path = StringUtil.loadThumbnail(attachmentBean.getUrl());
        if (isGif(path)) {
            Glide.with(mContext).load(path).listener(new RequestListener() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(ivImg);
        } else {
            Glide.with(mContext).load(path)
                    .apply(GlideOptionsUtil.circleImageOptions()).into(ivImg);
        }
    }

    public boolean isGif(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.toLowerCase().contains(".gif")) {
                return true;
            }
        }
        return false;
    }

    public void resize(RelativeLayout rlParent, CardView view) {
        int pictureSize = (int) mContext.getResources().getDimension(R.dimen.dimen_81);
        SquareRelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(pictureSize, pictureSize);
        view.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(pictureSize, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.setMargins(0, ScreenUtil.dip2px(mContext, 6), ScreenUtil.dip2px(mContext, 6), 0);
        rlParent.setLayoutParams(layoutParams2);
    }
}
