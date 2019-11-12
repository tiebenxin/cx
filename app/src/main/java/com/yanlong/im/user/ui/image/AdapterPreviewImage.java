package com.yanlong.im.user.ui.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.photoview.PhotoViewAttacher2;
import com.luck.picture.lib.photoview.ZoomImageView;
import com.luck.picture.lib.utils.PicSaveUtils;
import com.luck.picture.lib.view.bigImg.LargeImageView;
import com.luck.picture.lib.view.bigImg.factory.FileBitmapDecoderFactory;
import com.yalantis.ucrop.util.FileUtils;
import com.yanlong.im.R;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.utils.MyDiskCacheUtils;

import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.ImgSizeUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;

import java.io.File;
import java.util.List;

import okhttp3.Call;

/**
 * @anthor Liszt
 * @data 2019/11/11
 * Description
 */
public class AdapterPreviewImage extends PagerAdapter {
    private final String TAG = AdapterPreviewImage.class.getSimpleName();


    private List<LocalMedia> datas;
    private final Context context;
    private LayoutInflater inflater;
    private MsgDao msgDao = new MsgDao();
    private Call download;


    public AdapterPreviewImage(Context c) {
        context = c;
        inflater = LayoutInflater.from(c);

    }

    public void bindData(List<LocalMedia> l) {
        datas = l;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((View) object);
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        try {
            super.finishUpdate(container);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View contentView = inflater.inflate(com.luck.picture.lib.R.layout.item_preview_picture, container, false);
        final ZoomImageView ivZoom = contentView.findViewById(R.id.iv_image);
        final TextView tvViewOrigin = contentView.findViewById(R.id.tv_view_origin);
        ImageView ivDownload = contentView.findViewById(R.id.iv_download);
        LocalMedia media = datas.get(position);
        loadAndShowImage(media, ivZoom, ivDownload, tvViewOrigin);

        (container).addView(contentView, 0);

        return contentView;
    }

    private void loadAndShowImage(LocalMedia media, ZoomImageView ivZoom, ImageView ivDownload, TextView tvViewOrigin) {
        String path = media.getCompressPath();//缩略图路径
        String originUrl = media.getPath();//原图路径
        boolean isGif = FileUtils.isGif(path);//是否是gif图片
        boolean isOriginal = StringUtil.isNotNull(originUrl);//是否有原图
        boolean isHttp = PictureMimeType.isHttp(path);
//        final boolean isLong = PictureMimeType.isLongImg(media);
        boolean hasRead = false;
        if (!TextUtils.isEmpty(originUrl)) {
            hasRead = msgDao.ImgReadStatGet(originUrl);

        }

        if (isGif && !media.isCompressed()) {
            if (!media.getCutPath().equals(media.getCompressPath())) {
                Glide.with(context).load(media.getCutPath()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        showGif(ivZoom, tvViewOrigin, path);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        showGif(ivZoom, tvViewOrigin, path);
                        return false;
                    }
                }).into(ivZoom);
            } else {
                showGif(ivZoom, tvViewOrigin, path);
            }

        } else {
            showImage(ivZoom, tvViewOrigin, ivDownload, media, isOriginal, hasRead, isHttp);
        }

        //下载
        boolean finalHasRead = hasRead;
        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOriginal) {
                    if (finalHasRead) {
                        saveImageToLocal(ivZoom, media, isGif, isHttp, isOriginal);
                    } else {
                        downloadOriginImage(originUrl, tvViewOrigin, ivDownload, ivZoom, true);
                    }
                } else {

                }
            }
        });

        //查看原图
        tvViewOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadOriginImage(media.getPath(), tvViewOrigin, ivDownload, ivZoom, false);
            }
        });
        ivZoom.setOnViewTapListener(new PhotoViewAttacher2.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
            }
        });


    }

    /*
     * 保存图片到本地
     * */
    private void saveImageToLocal(ZoomImageView ivZoom, LocalMedia media, boolean isGif, boolean isHttp, boolean isOriginal) {
        Drawable drawable = ivZoom.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            PicSaveUtils.saveImgLoc(context, bitmap, "");
            ivZoom.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.show(context, "保存成功");
                }
            },100);
        }

    }

    private void showImage(ZoomImageView ivZoom, TextView tvViewOrigin, ImageView ivDownload, LocalMedia media, boolean isOrigin, boolean hasRead, boolean isHttp) {
        if (isHttp) {
            if (isOrigin) {
                tvViewOrigin.setTag(media.getSize());
                if (hasRead) {//原图已读,就显示
                    tvViewOrigin.setVisibility(View.GONE);
                    loadImage(media.getPath(), ivZoom);
                } else {
                    tvViewOrigin.setVisibility(View.VISIBLE);
                    tvViewOrigin.setText("查看原图(" + ImgSizeUtil.formatFileSize(media.getSize()) + ")");
                    loadImage(media.getCompressPath(), ivZoom);
                }
            } else {
                tvViewOrigin.setVisibility(View.GONE);
                ivDownload.setVisibility(View.VISIBLE);
                loadImage(media.getCompressPath(), ivZoom);
            }
        } else {
            tvViewOrigin.setVisibility(View.GONE);
            ivDownload.setVisibility(View.VISIBLE);
            loadImage(media.getPath(), ivZoom);
        }
    }

    private void showGif(ZoomImageView ivZoom, TextView tvViewOrigin, String path) {
        tvViewOrigin.setVisibility(View.GONE);
        RequestOptions gifOptions = new RequestOptions()
                .priority(Priority.LOW)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
                .asGif()
                .apply(gifOptions)
                .load(path)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, final Object model
                            , Target<GifDrawable> target, boolean isFirstResource) {
//                        dismissDialog();
                        ivZoom.post(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(context).asBitmap().load(model).into(ivZoom);
                            }
                        });

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model
                            , Target<GifDrawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
//                        dismissDialog();
                        return false;
                    }

                }).into(ivZoom);
    }

    /*
     * 加载图片
     * */
    private void loadImage(String url, ZoomImageView ivZoom) {
        System.out.println(TAG + "--url=" + url);
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(ivZoom.getContext())
                .asBitmap()
                .load(url)
                .apply(options)  //480     800
                .into(new SimpleTarget<Bitmap>(800, 800) {
                    /* .into(new SimpleTarget<Bitmap>(ScreenUtils.getScreenWidth(PictureExternalPreviewActivity.this),
                             ScreenUtils.getScreenHeight(PictureExternalPreviewActivity.this)) {*/
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
//                        dismissDialog();
                    }

                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                        dismissDialog();
                        ivZoom.setImageBitmap(resource);
                    }
                });
    }

    /*
     * 下载原图
     * */
    private void downloadOriginImage(String originUrl, TextView tvViewOrigin, ImageView ivDownload, ZoomImageView ivZoom, boolean needSave) {
        final String filePath = context.getExternalCacheDir().getAbsolutePath() + "/Image/";
        final String fileName = originUrl.substring(originUrl.lastIndexOf("/") + 1);
        File fileSave = new File(filePath + "/" + fileName);

        if (fileSave.exists()) {
            long fsize = (long) tvViewOrigin.getTag();
            long fsize2 = fileSave.length();
            boolean broken = fsize2 < fsize;
            if (broken) {//缓存清理
                fileSave.delete();
                new File(fileSave.getAbsolutePath() + FileBitmapDecoderFactory.cache_name).delete();
            }
        }

        //TODO 下载要做取消 9.5
        new Thread(new Runnable() {
            @Override
            public void run() {

                download = DownloadUtil.get().download(originUrl, filePath, fileName, new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(final File file) {

                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setDownloadProgress(tvViewOrigin, 100);
                                ivDownload.setEnabled(true);
                                loadImage(file.getAbsolutePath(), ivZoom);
                                MyDiskCacheUtils.getInstance().putFileNmae(filePath, fileSave.getAbsolutePath());
                                //这边要改成已读
                                msgDao.ImgReadStatSet(originUrl, true);
                            }
                        });
                        if (needSave) {
                            saveImageToLocal(ivZoom, null, false, true, true);
                        }
                    }

                    @Override
                    public void onDownloading(final int progress) {
//                        Log.d(TAG, "onDownloading: " + progress);
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setDownloadProgress(tvViewOrigin, progress);
                            }
                        });

                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        new File(filePath + "/" + fileName).delete();
                        new File(filePath + "/" + fileName + FileBitmapDecoderFactory.cache_name).delete();
                        e.printStackTrace();
                    }
                });
            }


        }).start();
    }

    /*
     * 更新下载进度
     * */
    public void setDownloadProgress(TextView tvViewOrigin, int progress) {
        tvViewOrigin.setText("已完成 " + progress + "%");
        if (progress == 100) {
            tvViewOrigin.setVisibility(View.GONE);
        }
    }
}
