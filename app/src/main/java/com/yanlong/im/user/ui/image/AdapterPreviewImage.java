package com.yanlong.im.user.ui.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.photoview.PhotoViewAttacher2;
import com.luck.picture.lib.photoview.ZoomImageView;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.utils.PicSaveUtils;
import com.luck.picture.lib.view.PopupSelectView;
import com.luck.picture.lib.view.bigImg.BlockImageLoader;
import com.luck.picture.lib.view.bigImg.LargeImageView;
import com.luck.picture.lib.view.bigImg.factory.FileBitmapDecoderFactory;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;
import com.luck.picture.lib.zxing.decoding.RGBLuminanceSource;
import com.yalantis.ucrop.util.FileUtils;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.utils.MyDiskCacheUtils;
import com.yanlong.im.utils.QRCodeManage;
import com.zhaoss.weixinrecorded.activity.ImageShowActivity;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.ImgSizeUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

import okhttp3.Call;

/**
 * @author Liszt
 * @date 2019/11/11
 * Description
 */
public class AdapterPreviewImage extends PagerAdapter {
    private final String TAG = AdapterPreviewImage.class.getSimpleName();


    private List<LocalMedia> datas;
    private final Activity context;
    private LayoutInflater inflater;
    private MsgDao msgDao = new MsgDao();
    private Call download;
    //    private IPreviewImageListener listener;
    private String[] strings = {"发送给朋友", "保存图片", "识别二维码", "编辑", "取消"};
    private View parentView;
    private int preProgress;


    public AdapterPreviewImage(Activity c) {
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
        //TODO:当前ZoomView算法不够强大，处理不了高分辨率图片（15M），所以要用LargeImageView来补充显示大图，待优化
        final ZoomImageView ivZoom = contentView.findViewById(R.id.iv_image);
        final LargeImageView ivLarge = contentView.findViewById(R.id.iv_image_large);
//        final SubsamplingScaleImageView ivLong = contentView.findViewById(R.id.iv_long);
        final TextView tvViewOrigin = contentView.findViewById(R.id.tv_view_origin);
        ImageView ivDownload = contentView.findViewById(R.id.iv_download);
        ProgressBar pbLoading = contentView.findViewById(R.id.pb_loading);
        LinearLayout llLook = contentView.findViewById(R.id.ll_look);
        LocalMedia media = datas.get(position);
        loadAndShowImage(media, ivZoom, ivLarge, /*ivLong,*/ ivDownload, tvViewOrigin, pbLoading, llLook);
        (container).addView(contentView, 0);
        return contentView;
    }

    private void loadAndShowImage(LocalMedia media, ZoomImageView ivZoom, LargeImageView ivLarge, ImageView ivDownload, TextView tvViewOrigin, ProgressBar pbLoading, LinearLayout llLook) {
        String path = media.getCompressPath();//最小缩略图路径
        String originUrl = media.getPath();//原图路径
        boolean isOriginal = StringUtil.isNotNull(originUrl);//是否有原图
        boolean isHttp = PictureMimeType.isHttp(path);
        boolean isGif = isGif(media, isHttp, isOriginal);
        boolean isLong = PictureMimeType.isLongImg(media);
        boolean hasRead = false;
        if (!TextUtils.isEmpty(originUrl)) {
            hasRead = msgDao.ImgReadStatGet(originUrl);
        }
        pbLoading.setVisibility(View.VISIBLE);
        if (isGif && !media.isCompressed()) {
            showGif(media, ivZoom, tvViewOrigin, pbLoading);
        } else {
            showImage(ivZoom, ivLarge, tvViewOrigin, ivDownload, media, isOriginal, hasRead, isHttp, isLong, pbLoading, llLook);
        }

        //下载
        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                ivDownload.setEnabled(false);
                boolean finalHasRead = false;
                if (isOriginal && !TextUtils.isEmpty(originUrl)) {//重新获取已读数据
                    finalHasRead = msgDao.ImgReadStatGet(originUrl);
                }
                if (isGif) {
                    if (isHttp) {
                        String cacheFile = PictureFileUtils.getFilePathOfImage(media.getPath(), context);
                        if (PictureFileUtils.hasImageCache(cacheFile, media.getSize())) {
                            saveImageFromCacheFile(cacheFile, ivZoom);
                        } else {
                            downloadOriginImage(!TextUtils.isEmpty(originUrl) ? originUrl : path, tvViewOrigin, ivDownload, ivZoom, ivLarge, true, isGif, llLook);
                        }
                    } else {
                        if (PictureFileUtils.hasImageCache(media.getPath(), media.getSize())) {
                            saveImageFromCacheFile(media.getPath(), ivZoom);
                        } else if (PictureFileUtils.hasImageCache(media.getCompressPath(), media.getSize())) {
                            saveImageFromCacheFile(media.getCompressPath(), ivZoom);
                        } else {
                            downloadOriginImage(originUrl, tvViewOrigin, ivDownload, ivZoom, ivLarge, true, isGif, llLook);
                        }
                    }
                } else {
                    if (isOriginal) {
                        if (finalHasRead) {
                            saveImageToLocal(ivZoom, media, isGif, isHttp, isOriginal, llLook);
                        } else if (PictureFileUtils.hasImageCache(media.getPath(), media.getSize())) {
                            saveImageFromCacheFile(media.getPath(), ivZoom);
                        } else {
                            downloadOriginImage(!TextUtils.isEmpty(originUrl) ? originUrl : path, tvViewOrigin, ivDownload, ivZoom, ivLarge, true, isGif, llLook);
                        }
                    } else {
                        saveImageToLocal(ivZoom, media, isGif, isHttp, isOriginal, llLook);
                    }
                }
            }
        });

        //查看原图
        llLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvViewOrigin.setEnabled(false);
                tvViewOrigin.setClickable(false);
                downloadOriginImage(media.getPath(), tvViewOrigin, ivDownload, ivZoom, ivLarge, false, isGif, llLook);
            }
        });
        ivZoom.setOnViewTapListener(new PhotoViewAttacher2.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
//                System.out.println(TAG + "-- ivZoom--onViewTap");
                if (download != null) {//取消当前请求
                    download.cancel();
                }
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
            }
        });

        ivZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                System.out.println(TAG + "-- ivZoom--onClick");
                if (download != null) {//取消当前请求
                    download.cancel();
                }
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
            }
        });

        ivZoom.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDownLoadDialog(media, ivZoom, isHttp, isOriginal, llLook);
                return true;
            }
        });

        ivLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                System.out.println(TAG + "-- ivLarge--onClick");
                if (download != null) {//取消当前请求
                    download.cancel();
                }
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
            }
        });

        ivLarge.setOnLoadStateChangeListener(new BlockImageLoader.OnLoadStateChangeListener() {
            @Override
            public void onLoadStart(int loadType, Object param) {

            }

            @Override
            public void onLoadFinished(int loadType, Object param, boolean success, Throwable throwable) {


            }
        });
        ivLarge.setOnImageLoadListener(new BlockImageLoader.OnImageLoadListener() {
            @Override
            public void onBlockImageLoadFinished() {
                ivLarge.setAlpha(1);
//                dismissDialog();
//                setDownloadProgress(tvViewOrigin, 100, llLook);
                // ToastUtil.show(getApplicationContext(),"加载完成");
            }

            @Override
            public void onLoadImageSize(int imageWidth, int imageHeight) {

            }

            @Override
            public void onLoadFail(Exception e) {
                ToastUtil.show(context, "加载失败,请重试");
//                dismissDialog();
            }
        });

        ivLarge.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDownLoadDialog(media, ivZoom, isHttp, isOriginal, llLook);
                return false;
            }
        });
    }

    private void showGif(LocalMedia media, ZoomImageView ivZoom, TextView tvViewOrigin, ProgressBar pbLoading) {
        if (!media.getCutPath().equals(media.getCompressPath())) {
            Glide.with(context).load(media.getCutPath()).error(Glide.with(context).load(media.getCompressPath())).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    ivZoom.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(AppConfig.getContext(), "加载失败,请检查网络");

                        }
                    }, 100);
                    pbLoading.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    pbLoading.setVisibility(View.GONE);
                    return false;
                }
            }).into(ivZoom);
        } else {
            showGif(ivZoom, tvViewOrigin, media.getCompressPath(), pbLoading);
        }
    }

    /*
     * 保存图片到本地
     * */
    private void saveImageToLocal(ZoomImageView ivZoom, LocalMedia media, boolean isGif, boolean isHttp, boolean isOriginal, LinearLayout llLook) {
        if (isGif) {
            if (isHttp) {
                String cacheFile = PictureFileUtils.getFilePathOfImage(media.getPath(), context);
                if (PictureFileUtils.hasImageCache(cacheFile, media.getSize())) {
                    saveImageFromCacheFile(cacheFile, ivZoom);
                } else {
                    downloadOriginImage(!TextUtils.isEmpty(media.getPath()) ? media.getPath() : media.getCompressPath(), null, null, ivZoom, null, true, isGif, llLook);
                }
            } else {
                if (PictureFileUtils.hasImageCache(media.getPath(), media.getSize())) {
                    saveImageFromCacheFile(media.getPath(), ivZoom);
                } else if (PictureFileUtils.hasImageCache(media.getCompressPath(), media.getSize())) {
                    saveImageFromCacheFile(media.getCompressPath(), ivZoom);
                } else {
                    downloadOriginImage(!TextUtils.isEmpty(media.getPath()) ? media.getPath() : media.getCompressPath(), null, null, ivZoom, null, true, isGif, llLook);
                }
            }
        } else {
            if (!isOriginal) {
                saveImageFromDrawable(ivZoom);
            } else {
                if (isHttp) {
                    String cacheFile = PictureFileUtils.getFilePathOfImage(media.getPath(), context);
                    if (PictureFileUtils.hasImageCache(cacheFile, media.getSize())) {
                        saveImageFromCacheFile(cacheFile, ivZoom);
                    } else {
                        saveImageFromDrawable(ivZoom);
                    }
                } else {
                    if (PictureFileUtils.hasImageCache(media.getPath(), media.getSize())) {
                        saveImageFromCacheFile(media.getPath(), ivZoom);
                    } else if (PictureFileUtils.hasImageCache(media.getCompressPath(), media.getSize())) {
                        saveImageFromCacheFile(media.getCompressPath(), ivZoom);
                    } else {
                        saveImageFromDrawable(ivZoom);
                    }
                }
            }
        }
    }

    //从控件中获取bitmap存储到本地
    private void saveImageFromDrawable(ZoomImageView ivZoom) {
        Drawable drawable = ivZoom.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            boolean isSuccess = PicSaveUtils.saveImgLoc(context, bitmap, "");
            ivZoom.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isSuccess) {
                        ToastUtil.show(AppConfig.getContext(), "保存成功");
                    }
                }
            }, 100);
        } else if (drawable instanceof GifDrawable) {

        }
    }

    //从本地缓存中存储到本地
    private void saveImageFromCacheFile(String filePath, ZoomImageView ivZoom) {
        if (!TextUtils.isEmpty(filePath)) {
            boolean isSuccess = PicSaveUtils.saveOriginImage(context, filePath);
            ivZoom.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isSuccess) {
                        ToastUtil.show(AppConfig.getContext(), "保存成功");
                    }
                }
            }, 100);


        }
    }

    private void showImage(ZoomImageView ivZoom, LargeImageView ivLarge, TextView tvViewOrigin, ImageView ivDownload, LocalMedia media, boolean isOrigin, boolean hasRead, boolean isHttp, boolean isLong, ProgressBar pbLoading, LinearLayout llLock) {
        tvViewOrigin.setTag(media.getSize());
        showViewOrigin(isHttp, isOrigin, hasRead, tvViewOrigin, media.getSize(), llLock);
        if (isHttp) {
            if (isOrigin) {
                if (hasRead) {//原图已读,就显示
                    String cachePath = PictureFileUtils.getFilePathOfImage(media.getPath(), context);
                    if (PictureFileUtils.hasImageCache(cachePath, media.getSize())) {
                        loadImage(media.getCompressPath(), ivZoom, false, pbLoading);
                        //TODO:不设置Alpha 和 visible 就不能响应手势
                        ivLarge.setAlpha(0);
                        ivLarge.setVisibility(View.VISIBLE);
                        ivLarge.setImage(new FileBitmapDecoderFactory(cachePath));
                        showZoomView(ivZoom, false);
                    } else {
                        loadImage(media.getCompressPath(), ivZoom, true, pbLoading);
//                        loadLargeImage(media.getPath(), ivLarge, ivZoom);
                    }
                } else {
                    hideLargeImageView(ivLarge);
                    if (!TextUtils.isEmpty(media.getCutPath()) /*&& (media.getWidth() > 1080 || media.getHeight() > 1920)*/) {
                        loadImage(media.getCutPath(), ivZoom, false, pbLoading);
                        loadImage(media.getCompressPath(), ivZoom, false, pbLoading);
                    } else {
                        loadImage(media.getCompressPath(), ivZoom, false, pbLoading);
                    }
                }
            } else {
                hideLargeImageView(ivLarge);
                ivDownload.setVisibility(View.VISIBLE);
                loadImage(media.getCutPath(), ivZoom, false, pbLoading);
                loadImage(media.getCompressPath(), ivZoom, false, pbLoading);
            }
        } else {
            ivDownload.setVisibility(View.VISIBLE);
            boolean hasLoadThumbnail = false;
            String url = !TextUtils.isEmpty(media.getPath()) ? media.getPath() : media.getCompressPath();
            if ((media.getWidth() > 1080 || media.getHeight() > 1920)) {
                loadImageThumbnail(url, ivZoom, pbLoading);
                hasLoadThumbnail = true;
            }
            if (!hasLoadThumbnail) {//没加载过缩略图，先隐藏ivZoom
                showZoomView(ivZoom, false);
            }
            if (hasLoadThumbnail) {//图片过大需要加载缩略图
                if (!TextUtils.isEmpty(media.getPath())) {
                    ivLarge.setAlpha(0);
                    ivLarge.setVisibility(View.VISIBLE);
                    ivLarge.setImage(new FileBitmapDecoderFactory(media.getPath()));
                } else {
                    ivLarge.setAlpha(0);
                    ivLarge.setVisibility(View.VISIBLE);
                    ivLarge.setImage(new FileBitmapDecoderFactory(media.getCompressPath()));
                }
            } else {
                hideLargeImageView(ivLarge);
                showZoomView(ivZoom, true);
                loadImage(url, ivZoom, true, pbLoading);

            }
            if (hasLoadThumbnail) {//加载过缩略图，后隐藏ivZoom
                showZoomView(ivZoom, false);
            }
//            System.out.println(TAG + "--ivZoom=" + ivZoom.getVisibility() + "--ivLarge=" + ivLarge.getVisibility());
        }
    }

    private void showViewOrigin(boolean isHttp, boolean isOrigin, boolean hasRead, TextView tvViewOrigin, long size, LinearLayout llLook) {
        if (isHttp && isOrigin && !hasRead) {
            tvViewOrigin.setVisibility(View.VISIBLE);
            llLook.setVisibility(View.VISIBLE);
            if (size > 0) {
                tvViewOrigin.setText("查看原图(" + ImgSizeUtil.formatFileSize(size) + ")");
            } else {
                tvViewOrigin.setText("查看原图");
            }
        } else {
            tvViewOrigin.setVisibility(View.GONE);
            llLook.setVisibility(View.GONE);
        }
    }

    private boolean isLongImage(int w, int h) {
        double rate = w * 1.00 / h;
        if (rate < 0.2) {
            return true;
        }
        return false;
    }

    private void showGif(ZoomImageView ivZoom, TextView tvViewOrigin, String path, ProgressBar pbLoading) {
        if (tvViewOrigin != null) {
            tvViewOrigin.setVisibility(View.GONE);
        }
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
                        pbLoading.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model
                            , Target<GifDrawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        pbLoading.setVisibility(View.GONE);
                        return false;
                    }

                }).into(ivZoom);
    }

    /*
     * 加载图片
     * */
    private void loadImage(String url, ZoomImageView ivZoom, boolean isOrigin, ProgressBar pbLoading) {
//        System.out.println(TAG + "--loadImage--" + url);
        if (!isOrigin) {
            RequestOptions options = new RequestOptions()
                    .disallowHardwareConfig()//不使用ARGB_8888这种高质量的解析图片
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .format(DecodeFormat.PREFER_RGB_565);
            Glide.with(ivZoom.getContext())
                    .asBitmap()
                    .load(url)
                    .apply(options)  //480     800
                    .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            pbLoading.setVisibility(View.GONE);
                            ivZoom.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(AppConfig.getContext(), "加载失败,请检查网络");

                                }
                            }, 100);
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                        dismissDialog();
//                            System.out.println(TAG + "--ivZoom=" + resource.getWidth() + "--" + resource.getHeight());
                            ivZoom.setImageBitmap(resource);
                            pbLoading.setVisibility(View.GONE);
                        }
                    });
        } else {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .format(DecodeFormat.PREFER_ARGB_8888);
            Glide.with(ivZoom.getContext())
                    .asBitmap()
                    .load(url)
                    .apply(options)  //480     800
                    .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            pbLoading.setVisibility(View.GONE);
//                        dismissDialog();
                            ivZoom.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(AppConfig.getContext(), "加载失败,请检查网络");

                                }
                            }, 100);
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                        dismissDialog();
//                            System.out.println(TAG + "--ivZoom=" + resource.getWidth() + "--" + resource.getHeight());
                            ivZoom.setImageBitmap(resource);
                            pbLoading.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void loadImageThumbnail(String url, ZoomImageView ivZoom, ProgressBar pbLoading) {
//        System.out.println(TAG + "--loadImageThumbnail--" + url);
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(384, 540)
                .format(DecodeFormat.PREFER_RGB_565);
        Glide.with(ivZoom.getContext())
                .asBitmap()
                .load(url)
                .apply(options)  //480     800
                .thumbnail(0.1f)
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        pbLoading.setVisibility(View.GONE);
//                        dismissDialog();
                        ivZoom.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show(AppConfig.getContext(), "加载失败,请检查网络");

                            }
                        }, 100);
                    }

                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                        dismissDialog();
                        ivZoom.setImageBitmap(resource);
                        pbLoading.setVisibility(View.GONE);
                    }
                });
    }


    /*
     * 下载原图
     * */
    private void downloadOriginImage(String originUrl, TextView tvViewOrigin, ImageView ivDownload, ZoomImageView ivZoom, LargeImageView ivLarge, boolean needSave, boolean isGif, LinearLayout llLook) {
        if (TextUtils.isEmpty(originUrl)) {
            return;
        }
        tvViewOrigin.postDelayed(new Runnable() {
            @Override
            public void run() {
                setDownloadProgress(tvViewOrigin, 0, llLook);
            }
        }, 100);
        final String filePath = context.getExternalCacheDir().getAbsolutePath() + "/Image/";
        final String fileName = originUrl.substring(originUrl.lastIndexOf("/") + 1);
        File fileSave = new File(filePath + "/" + fileName);//原图保存路径
        if (!isGif) {
            if (fileSave.exists()) {
                long fsize = (long) tvViewOrigin.getTag();
                long fsize2 = fileSave.length();
                boolean broken = fsize2 < fsize;
                if (broken) {//缓存清理
                    fileSave.delete();
                    new File(fileSave.getAbsolutePath() + FileBitmapDecoderFactory.cache_name).delete();
                }
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
                                if (isGif) {
//                                    showGif(ivZoom, tvViewOrigin, file.getAbsolutePath());
//                                    hideLargeImageView(ivLarge);
                                } else {
                                    ivLarge.setAlpha(0);
                                    ivLarge.setVisibility(View.VISIBLE);
                                    setDownloadProgress(tvViewOrigin, 100, llLook);
                                    ivDownload.setEnabled(true);
                                    ivLarge.setImage(new FileBitmapDecoderFactory(file.getAbsolutePath()));
                                    showZoomView(ivZoom, false);
//                                loadLargeImage(file.getAbsolutePath(), ivLarge);
                                    MyDiskCacheUtils.getInstance().putFileNmae(filePath, fileSave.getAbsolutePath());
                                }
                                //这边要改成已读
                                msgDao.ImgReadStatSet(originUrl, true);
                            }
                        });
                        if (needSave) {
                            saveImageFromCacheFile(file.getAbsolutePath(), ivZoom);
                        }
                    }

                    @Override
                    public void onDownloading(final int progress) {
//                        Log.d(TAG, "onDownloading: " + progress);
                        if (isGif) {
                            return;
                        }
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setDownloadProgress(tvViewOrigin, progress, llLook);
                            }
                        });

                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                        ivDownload.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ivZoom.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        preProgress = 0;
                                        ToastUtil.show(AppConfig.getContext(), "加载失败,请检查网络");

                                    }
                                }, 100);
                            }
                        }, 100);
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
    public void setDownloadProgress(TextView tvViewOrigin, int progress, LinearLayout llLook) {
        if (preProgress > progress) {
            return;
        }
        preProgress = progress;
        if (tvViewOrigin == null) {
            return;
        }
        LogUtil.getLog().i(TAG, "progress=" + progress);
        tvViewOrigin.setText("已完成 " + progress + "%");
        if (progress == 100) {
            tvViewOrigin.setVisibility(View.GONE);
            llLook.setVisibility(View.GONE);
            preProgress = 0;
        }
    }

    /**
     * 长按弹窗提示
     */
    private void showDownLoadDialog(final LocalMedia media, ZoomImageView ivZoom, boolean isHttp, boolean isOriginal, LinearLayout llLook) {
        final PopupSelectView popupSelectView = new PopupSelectView(context, strings);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                String msgId = media.getMsg_id();
                if (postsion == 0) {//转发
                    if (!TextUtils.isEmpty(msgId)) {
                        MsgAllBean msgAllBean = msgDao.getMsgById(msgId);
                        if (msgAllBean != null) {
                            context.startActivity(new Intent(context, MsgForwardActivity.class)
                                    .putExtra(MsgForwardActivity.AGM_JSON, new Gson().toJson(msgAllBean)));
                        } else {
                            ToastUtil.show("消息已被删除或者被焚毁，不能转发");
                        }
                    } else {
                        //TODO:无消息id，要不要自己新建一条消息记录，然后发出去？

                    }
                } else if (postsion == 1) {//保存
                    saveImageToLocal(ivZoom, media, FileUtils.isGif(media.getCompressPath()), isHttp, isOriginal, llLook);
                } else if (postsion == 2) {//识别二维码
                    // scanningImage(media.getPath());
                    scanningQrImage(media.getCompressPath(), ivZoom);
                } else if (postsion == 3) {//长按跳编辑界面，编辑完成后，返回新图片的本地路径到PictureExternalPreviewActivity
                    Intent intent = new Intent(context, ImageShowActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("imgpath", media.getCompressPath());
                    bundle.putString("msg_id", msgId);
                    bundle.putInt("img_width", media.getWidth());
                    bundle.putInt("img_height", media.getHeight());
                    intent.putExtras(bundle);
                    context.startActivityForResult(intent, PictureExternalPreviewActivity.IMG_EDIT);
                }
                popupSelectView.dismiss();

            }
        });
        popupSelectView.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);

    }

    /**
     * 扫描二维码图片的方法
     *
     * @param path
     * @return
     */
    public Result scanningImage(String path, Bitmap bitmap) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        Bitmap scan = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
//        int sampleSize = (int) (options.outHeight / (float) 200);
//        if (sampleSize <= 0)
//            sampleSize = 1;
        //  options.inSampleSize = sampleSize;

        Bitmap scanBitmap = null;
        if (bitmap != null) {
            scanBitmap = bitmap;
        } else {
            scanBitmap = BitmapFactory.decodeFile(path, options);
        }
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    //扫描二维码图片的方法
    private void scanningQrImage(String path, ZoomImageView ivZoom) {
        try {
            LogUtil.getLog().e("=====path=" + path);
            boolean isHttp = PictureMimeType.isHttp(path);
            if (isHttp) {
                LogUtil.getLog().e("=======网络图片==");
                //从缓存中读取bitmap
                ivZoom.setDrawingCacheEnabled(true);
                ivZoom.buildDrawingCache();
                Bitmap bitmap = ivZoom.getDrawingCache();
                if (bitmap != null) {
                    LogUtil.getLog().e("=======网络图片=bitmap不为空=");
                    Result result = scanningImage(path, bitmap);
                    QRCodeManage.toZhifubao(context, result);
                } else {
                    ToastUtil.show(AppConfig.getContext(), "识别二维码失败");
                }
            } else {
                LogUtil.getLog().d(TAG, "scanningQrImage: path" + path);
                // 有可能本地图片
                if (path.toLowerCase().startsWith("file://")) {
                    path = path.replace("file://", "");
                }
                // LogUtil.getLog().d(TAG, "scanningQrImage: dirPath"+dirPath);
                Result result = scanningImage(path, null);
                QRCodeManage.toZhifubao(context, result);

            }
        } catch (Exception e) {
            ToastUtil.show(AppConfig.getContext(), "识别二维码失败");
            e.printStackTrace();
        }
    }


    public void setPopParentView(View view) {
        parentView = view;
    }

    /**
     * 加载长图
     *
     * @param bmp
     * @param longImg
     */
    private void displayLongPic(Bitmap bmp, SubsamplingScaleImageView longImg) {
//        LogUtil.getLog().i(TAG, "displayLongPic: 显示长图");

        if (bmp.getHeight() > 4000 || bmp.getWidth() > 4000) {
            if (bmp.getHeight() > bmp.getWidth()) {

                float sp = 4000.0f / bmp.getHeight();
                bmp = scaleBitmap(bmp, sp);
            } else {
                float sp = 4000.0f / bmp.getWidth();
                bmp = scaleBitmap(bmp, sp);
            }
        }


        longImg.setQuickScaleEnabled(true);
        longImg.setZoomEnabled(true);
        longImg.setPanEnabled(true);
        longImg.setDoubleTapZoomDuration(100);
        longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        longImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        longImg.setImage(ImageSource.cachedBitmap(bmp), new ImageViewState(0, new PointF(0, 0), 0));
    }

    /**
     * 缩放
     *
     * @param origin
     * @param ratio
     * @return
     */
    private Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        // origin.recycle();
        return newBM;
    }

    public void showZoomView(ZoomImageView iv, boolean show) {
//        System.out.println(TAG + "--showZoomView = " + show);
        iv.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    public void hideLargeImageView(LargeImageView iv) {
        if (iv == null) {
            return;
        }
        if (iv.getVisibility() == View.VISIBLE) {
            iv.setAlpha(1);
            iv.setVisibility(View.GONE);
        }
    }

    private boolean isGif(LocalMedia media, boolean isHttp, boolean isOrigin) {
        String path = media.getCompressPath();//缩略图路径
        String originUrl = media.getPath();//原图路径
        boolean isGif = false;
        if (isOrigin) {
            isGif = FileUtils.isGif(originUrl);
        } else {
            if (isHttp) {
                if (path.contains("below-200k")) {
                    int index = path.lastIndexOf("/");
                    String url = path.substring(0, index);
                    if (!TextUtils.isEmpty(url)) {
                        isGif = FileUtils.isGif(url);
                    }
                } else {
                    isGif = FileUtils.isGif(path);
                }
            } else {
                isGif = FileUtils.isGif(path);
            }
        }
        return isGif;
    }


}
