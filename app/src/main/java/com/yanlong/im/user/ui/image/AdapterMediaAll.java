package com.yanlong.im.user.ui.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ImageMessage;
import com.yanlong.im.chat.bean.LabelItem;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.SendFileMessage;
import com.yanlong.im.chat.bean.VideoMessage;

import net.cb.cb.library.base.AbstractRecyclerAdapter;
import net.cb.cb.library.base.AbstractViewHolder;
import net.cb.cb.library.utils.FileUtils;
import net.cb.cb.library.utils.ToastUtil;

import java.util.List;

/**
 * @author Liszt
 * @date 2020/9/16
 * Description 查看所有图片，视频，文件
 */
public class AdapterMediaAll extends AbstractRecyclerAdapter<Object> {
    private boolean isSelect = false;
    private List<MsgAllBean> selectList;
    private ISelectListener listener;

    public AdapterMediaAll(Context ctx) {
        super(ctx);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new MediaTitleHolder(mInflater.inflate(R.layout.item_media_title, parent, false));
        } else if (viewType == 1) {
            return new MediaAllHolder(mInflater.inflate(R.layout.item_preview_image, parent, false));
        } else if (viewType == 2) {
            return new MediaFileHolder(mInflater.inflate(R.layout.item_media_file, parent, false));
        } else {
            return new MediaTitleHolder(mInflater.inflate(R.layout.item_media_title, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            Object object = mBeanList.get(position);
            if (object instanceof String) {
                MediaTitleHolder titleHolder = (MediaTitleHolder) holder;
                titleHolder.bindData((String) object);
            }
        } else if (holder.getItemViewType() == 1) {
            Object object = mBeanList.get(position);
            if (object instanceof MsgAllBean) {
                MediaAllHolder viewHolder = (MediaAllHolder) holder;
                viewHolder.bindData((MsgAllBean) object);
            }
        } else if (holder.getItemViewType() == 2) {
            Object object = mBeanList.get(position);
            if (object instanceof MsgAllBean) {
                MediaFileHolder viewHolder = (MediaFileHolder) holder;
                viewHolder.bindData((MsgAllBean) object);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mBeanList != null) {
            Object o = mBeanList.get(position);
            if (o instanceof String) {//title
                return 0;
            } else if (o instanceof MsgAllBean) {//msg
                MsgAllBean bean = (MsgAllBean) o;
                if (bean.getMsg_type() == ChatEnum.EMessageType.IMAGE || bean.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {
                    return 1;
                } else if (bean.getMsg_type() == ChatEnum.EMessageType.FILE) {
                    return 2;

                }
            }
        }
        return 0;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select, List<MsgAllBean> list) {
        isSelect = select;
        selectList = list;
//        notifyDataSetChanged();
    }

    //图片视频ViewHolder
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
                if (selectList.contains(bean)) {
                    tvCheck.setSelected(true);
                } else {
                    tvCheck.setSelected(false);
                }
            } else {
                llCheck.setVisibility(View.GONE);
            }
//            tvGif.setVisibility(View.VISIBLE);
//            tvGif.setText(DateUtils.timeStamp2Date(bean.getTimestamp(), "yyyy/MM/dd"));
            if (bean.getMsg_type() == ChatEnum.EMessageType.IMAGE) {
                tvDuration.setVisibility(View.GONE);
                ImageMessage imageMessage = bean.getImage();
                String url = imageMessage.getThumbnail();
                if (PictureMimeType.isImageGif(url)) {
                    tvGif.setVisibility(View.VISIBLE);
                } else {
                    tvGif.setVisibility(View.GONE);
                }
                String tag = (String) ivImage.getTag(R.id.tag_img);
                RequestOptions options = new RequestOptions().centerCrop().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.RESOURCE).dontAnimate();
                if (TextUtils.equals(tag, url)) {
                    glideImage(tag, options);
                } else {
                    glideImage(url, options);
                }

            } else if (bean.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {
                tvGif.setVisibility(View.GONE);
                tvDuration.setVisibility(View.VISIBLE);
                VideoMessage videoMessage = bean.getVideoMessage();
                String tag = (String) ivImage.getTag(R.id.tag_img);
                String url = videoMessage.getBg_url();
                RequestOptions options = new RequestOptions().centerCrop().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.RESOURCE).dontAnimate();
                if (TextUtils.equals(tag, url)) {
                    glideImage(tag, options);
                } else {
                    glideImage(url, options);
                }
                tvDuration.setText(DateUtils.timeParse(videoMessage.getDuration()));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onPreview(bean);
                    }
                }
            });

            llCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvCheck.isSelected()) {
                        selectList.remove(bean);
                        tvCheck.setSelected(false);
                        if (listener != null) {
                            listener.onRemove(bean);
                        }
                    } else {
                        if (selectList.size() < 9) {
                            if (!selectList.contains(bean)) {
                                selectList.add(bean);
                            }
                            tvCheck.setSelected(true);
                            if (listener != null) {
                                listener.onSelect(bean);
                            }
                        } else {
                            ToastUtil.show("最多选择9个");
                        }
                    }
                }
            });
        }

        private void glideVideo(String url) {
            Glide.with(getContext()).load(url).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    if (e.getMessage().contains("FileNotFoundException")) {
                        ivImage.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ivImage.setImageResource(R.mipmap.ic_img_past);
                            }
                        }, 100);

                    } else {

                    }
                    return false;
                }


                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    ivImage.setTag(R.id.tag_img, url);
                    return false;
                }
            }).into(ivImage);
        }

        private void glideImage(String url, RequestOptions options) {
            Glide.with(getContext()).asDrawable().load(url).apply(options).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    if (e.getMessage().contains("FileNotFoundException")) {
                        ivImage.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ivImage.setImageResource(R.mipmap.ic_img_past);
                            }
                        }, 100);

                    } else {

                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    ivImage.setTag(R.id.tag_img, url);
                    return false;
                }
            }).into(ivImage);
        }

    }

    //标题ViewHolder
    public class MediaTitleHolder extends AbstractViewHolder<String> {

        private final TextView tvTitle;

        public MediaTitleHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }

        @Override
        public void bindData(String bean) {
            if (!TextUtils.isEmpty(bean)) {
                tvTitle.setText(bean);
            }
        }
    }

    //图片视频ViewHolder
    class MediaFileHolder extends AbstractViewHolder<MsgAllBean> {

        private final LinearLayout llCheck;
        private final TextView tvCheck;
        private final TextView tvName;
        private final TextView tvSize;

        public MediaFileHolder(View itemView) {
            super(itemView);
            llCheck = itemView.findViewById(R.id.ll_check);
            tvCheck = itemView.findViewById(R.id.tv_check);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSize = itemView.findViewById(R.id.tv_size);

        }

        @Override
        public void bindData(MsgAllBean bean) {
            if (isSelect) {
                llCheck.setVisibility(View.VISIBLE);
                if (selectList.contains(bean)) {
                    tvCheck.setSelected(true);
                } else {
                    tvCheck.setSelected(false);
                }
            } else {
                llCheck.setVisibility(View.GONE);
            }
            if (bean.getMsg_type() == ChatEnum.EMessageType.FILE) {
                SendFileMessage fileMessage = bean.getSendFileMessage();
                tvName.setText(fileMessage.getFile_name());
                tvSize.setText(FileUtils.getFileSizeString(fileMessage.getSize()));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onPreview(bean);
                    }
                }
            });

            llCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvCheck.isSelected()) {
                        selectList.remove(bean);
                        tvCheck.setSelected(false);
                        if (listener != null) {
                            listener.onSelect(bean);
                        }
                    } else {
                        if (selectList.size() < 9) {
                            selectList.add(bean);
                            tvCheck.setSelected(true);
                            if (listener != null) {
                                listener.onRemove(bean);
                            }
                        } else {
                            ToastUtil.show("最多选择9个");
                        }
                    }
                }
            });
        }

    }


    public interface ISelectListener {
        void onSelect(MsgAllBean bean);

        void onRemove(MsgAllBean bean);

        void onPreview(MsgAllBean bean);
    }

    public void setListener(ISelectListener l) {
        listener = l;
    }
}
