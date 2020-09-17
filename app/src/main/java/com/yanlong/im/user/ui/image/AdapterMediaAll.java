package com.yanlong.im.user.ui.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.VideoMessage;

import net.cb.cb.library.base.AbstractRecyclerAdapter;
import net.cb.cb.library.base.AbstractViewHolder;

/**
 * @author Liszt
 * @date 2020/9/16
 * Description 查看所有图片，视频，文件
 */
public class AdapterMediaAll extends AbstractRecyclerAdapter<MsgAllBean> {
    private boolean isSelect = false;

    public AdapterMediaAll(Context ctx) {
        super(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MediaAllHolder(mInflater.inflate(R.layout.item_preview_image, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MsgAllBean msgAllBean = mBeanList.get(position);
        MediaAllHolder viewHolder = (MediaAllHolder) holder;
        viewHolder.bindData(msgAllBean);

    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
        notifyDataSetChanged();
    }

    class MediaAllHolder extends AbstractViewHolder<MsgAllBean> {

        private final ImageView ivImage;
        private final LinearLayout llCheck;
        private final TextView tvCheck;
        private final TextView tvGif;
        private final TextView tvLong;
        private final TextView tvDuration;

        public MediaAllHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            llCheck = itemView.findViewById(R.id.ll_check);
            tvCheck = itemView.findViewById(R.id.tv_check);
            tvGif = itemView.findViewById(R.id.tv_gif);
            tvLong = itemView.findViewById(R.id.tv_long_chart);
            tvDuration = itemView.findViewById(R.id.tv_duration);

        }

        @Override
        public void bindData(MsgAllBean bean) {
            if (isSelect) {
                llCheck.setVisibility(View.VISIBLE);
            } else {
                llCheck.setVisibility(View.GONE);
            }
            tvGif.setVisibility(View.VISIBLE);
            tvGif.setText(DateUtils.timeStamp2Date(bean.getTimestamp(), "yyyy/MM/dd"));
            if (bean.getMsg_type() == ChatEnum.EMessageType.IMAGE) {
                tvDuration.setVisibility(View.GONE);
                ImageMessage imageMessage = bean.getImage();
                String url = imageMessage.getThumbnail();
                RequestOptions options = new RequestOptions().centerCrop();
                Glide.with(getContext()).load(url).apply(options).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (e.getMessage().contains("FileNotFoundException")) {
                            ivImage.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    ivImage.setImageResource(R.mipmap.ic_img_past);
                                    //                                    if (getContext() == null) {
//                                        return;
//                                    }
//                                    Glide.with(getContext()).load(R.mipmap.ic_img_past).apply(options).into(ivImage);
                                }
                            }, 100);

                        } else {

                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(ivImage);
            } else if (bean.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {
                tvDuration.setVisibility(View.VISIBLE);
                VideoMessage videoMessage = bean.getVideoMessage();
                Glide.with(getContext()).load(videoMessage.getBg_url()).into(ivImage);
                tvDuration.setText(DateUtils.timeParse(videoMessage.getDuration()));
            }
        }
    }
}
