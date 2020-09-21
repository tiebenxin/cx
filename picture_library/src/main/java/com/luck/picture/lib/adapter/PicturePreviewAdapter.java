package com.luck.picture.lib.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.adapter
 * email：893855882@qq.com
 * data：16/12/31
 */
public class PicturePreviewAdapter extends RecyclerView.Adapter<PicturePreviewAdapter.ViewHolder> {
    private Context mContext;
    private List<LocalMedia> folders = new ArrayList<>();

    public PicturePreviewAdapter(Context mContext, List<LocalMedia> list) {
        super();
        this.mContext = mContext;
        this.folders = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_circle_pictrue_preview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final LocalMedia folder = folders.get(position);
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .sizeMultiplier(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(160, 160);
        Glide.with(holder.itemView.getContext())
                .asBitmap()
                .load(folder.getPath())
//                .apply(options)
                .into(new BitmapImageViewTarget(holder.iv_img) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.
                                        create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(8);
                        holder.iv_img.setImageDrawable(circularBitmapDrawable);
                    }
                });
        if (PictureMimeType.ofVideo() == PictureMimeType.isPictureType(folder.getPictureType())) {
            holder.iv_play_video.setVisibility(View.VISIBLE);
        } else {
            holder.iv_play_video.setVisibility(View.GONE);
        }
        holder.rl_delet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                folders.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_img, iv_play_video;
        RelativeLayout rl_delet;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_img = itemView.findViewById(R.id.iv_img);
            iv_play_video = itemView.findViewById(R.id.iv_play_video);
            rl_delet = itemView.findViewById(R.id.rl_delet);
        }
    }
}
