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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

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
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.glide.CustomGlideModule;
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
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.utils.MyDiskCacheUtils;
import com.yanlong.im.utils.QRCodeManage;
import com.yanlong.im.utils.UserUtil;
import com.zhaoss.weixinrecorded.activity.ImageShowActivity;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ScreenUtil;
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
    private String[] strings = {"发送给朋友", "保存图片", "识别图中二维码", "编辑", "取消"};
    private String[] newStrings = {"发送给朋友", "保存图片", "收藏", "识别图中二维码", "编辑", "取消"};
    private String[] gifStrings = {"发送给朋友", "保存图片", "收藏", "识别图中二维码", "取消"};
    private String[] collectStrings = {"发送给朋友", "保存图片", "取消"};
    private View parentView;
    private int preProgress;
    private int fromWhere;//跳转来源 0 默认 1 猜你想要 2 收藏详情  3朋友圈
    private String collectJson = "";//收藏详情点击大图转发需要的数据
    private LocalMedia currentMedia;
    private IPreviewImage mIPreviewImage;

    public AdapterPreviewImage(Activity c, int fromWhere, String collectJson, IPreviewImage iPreviewImage) {
        context = c;
        inflater = LayoutInflater.from(c);
        mIPreviewImage = iPreviewImage;
        this.fromWhere = fromWhere;
        this.collectJson = collectJson;
    }

    public void bindData(List<LocalMedia> l) {
        datas = l;
    }

    public void setCurrentData(LocalMedia media) {
        currentMedia = media;
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
        ZoomImageView ivZoom = contentView.findViewById(R.id.iv_image);
        LargeImageView ivLarge = contentView.findViewById(R.id.iv_image_large);
        SubsamplingScaleImageView ivLong = contentView.findViewById(R.id.iv_long);
        TextView tvViewOrigin = contentView.findViewById(R.id.tv_view_origin);
        ImageView ivDownload = contentView.findViewById(R.id.iv_download);
        ProgressBar pbLoading = contentView.findViewById(R.id.pb_loading);
        LinearLayout llLook = contentView.findViewById(R.id.ll_look);
        LocalMedia media = datas.get(position);
        loadAndShowImage(media, ivZoom, ivLong, ivLarge, ivDownload, tvViewOrigin, pbLoading, llLook);
        (container).addView(contentView, 0);
        return contentView;
    }

    private void loadAndShowImage(LocalMedia media, ZoomImageView ivZoom,
                                  SubsamplingScaleImageView ivLong,
                                  LargeImageView ivLarge,
                                  ImageView ivDownload, TextView tvViewOrigin,
                                  ProgressBar pbLoading, LinearLayout llLook) {
        String path = media.getCompressPath();//最小缩略图路径
        String originUrl = media.getPath();//原图路径
        boolean isOriginal = StringUtil.isNotNull(originUrl);//是否有原图
        boolean isHttp = PictureMimeType.isHttp(path);
        boolean isGif = isGif(media, isHttp, isOriginal);
        boolean isLong = PictureMimeType.isLongImg(media);
        String format = PictureFileUtils.getFileFormatName(media.getCompressPath());
        boolean hasRead = false;
        if (!TextUtils.isEmpty(originUrl)) {
            hasRead = msgDao.ImgReadStatGet(originUrl);
        }
        pbLoading.setVisibility(View.GONE);
        boolean isCurrent = false;
        if (!TextUtils.isEmpty(media.getMsg_id()) && currentMedia != null && !TextUtils.isEmpty(currentMedia.getMsg_id())) {
            isCurrent = media.getMsg_id().equals(currentMedia.getMsg_id());
        }
        try {
            //隐藏大图。因为阿里云限制图片单边不能超过4096，没有必要再用大图显示控件了
//            hideLargeImageView(ivLarge);
            showZoomView(ivZoom, true);
            if (isGif && !media.isCompressed()) {
                showGif(media, ivZoom, tvViewOrigin, pbLoading, isCurrent);
            } else {
                showImage2(ivZoom, ivLong, tvViewOrigin, ivDownload, media, isOriginal, hasRead, isHttp, isLong, pbLoading, llLook, isCurrent);
            }
        } catch (Exception e) {
        }

        //下载
        boolean finalIsCurrent = isCurrent;
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
                            saveImageFromCacheFile(cacheFile, format, ivZoom);
                        } else {
                            downloadOriginImage(!TextUtils.isEmpty(originUrl) ? originUrl : path,
                                    tvViewOrigin, ivDownload, ivZoom, ivLarge,
                                    ivLong, isLong, true, isGif, llLook, finalIsCurrent);
                        }
                    } else {
                        if (PictureFileUtils.hasImageCache(media.getPath(), media.getSize())) {
                            saveImageFromCacheFile(media.getPath(), format, ivZoom);
                        } else if (PictureFileUtils.hasImageCache(media.getCompressPath(), media.getSize())) {
                            saveImageFromCacheFile(media.getCompressPath(), format, ivZoom);
                        } else {
                            downloadOriginImage(originUrl, tvViewOrigin, ivDownload, ivZoom,
                                    ivLarge, ivLong, isLong, true, isGif, llLook, finalIsCurrent);
                        }
                    }
                } else {
                    if (isOriginal) {
                        if (finalHasRead) {
                            saveImageToLocal(ivZoom, ivLong, isLong, media, isGif, isHttp, isOriginal, llLook, finalIsCurrent, tvViewOrigin);
                        } else if (PictureFileUtils.hasImageCache(media.getPath(), media.getSize())) {
                            saveImageFromCacheFile(media.getPath(), format, ivZoom);
                        } else {
                            downloadOriginImage(!TextUtils.isEmpty(originUrl) ? originUrl : path, tvViewOrigin, ivDownload,
                                    ivZoom, ivLarge, ivLong, isLong, true, isGif, llLook, finalIsCurrent);
                        }
                    } else {
                        saveImageToLocal(ivZoom, ivLong, isLong, media, isGif, isHttp, isOriginal, llLook, finalIsCurrent, tvViewOrigin);
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
                downloadOriginImage(media.getPath(), tvViewOrigin, ivDownload, ivZoom, ivLarge,
                        ivLong, isLong, false, isGif, llLook, finalIsCurrent);
            }
        });
        ivZoom.setOnViewTapListener(new PhotoViewAttacher2.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                onCancle();
            }
        });

        ivZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancle();
            }
        });
        ivLong.setOnClickListener(o -> {
            onCancle();
        });

        ivZoom.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDownLoadDialog(media, ivLong, isLong, ivZoom, isHttp, isOriginal, llLook, isGif, finalIsCurrent, tvViewOrigin);
                return true;
            }
        });

        ivLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancle();
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
                showDownLoadDialog(media, ivLong, isLong, ivZoom, isHttp, isOriginal, llLook, isGif, finalIsCurrent, tvViewOrigin);
                return false;
            }
        });
    }

    private void onCancle() {
        if (download != null) {//取消当前请求
            download.cancel();
        }
        if (context != null && !context.isFinishing()) {
            context.finish();
            context.overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
        }
    }

    private void showGif(LocalMedia media, ZoomImageView ivZoom, TextView tvViewOrigin, ProgressBar pbLoading, boolean isCurrent) {
        if (!media.getCutPath().equals(media.getCompressPath())) {
            if (activityIsFinish()) {
                return;
            }
            if (tvViewOrigin != null) {
                tvViewOrigin.setVisibility(View.GONE);
            }
            Glide.with(context).load(media.getCutPath()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    if (pbLoading != null) {
                        pbLoading.setVisibility(View.GONE);
                    }
                    if (e.getMessage().contains("FileNotFoundException")) {
                        if (ivZoom != null) {
                            ivZoom.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ivZoom.setImageResource(R.mipmap.ic_img_past);
                                }
                            }, 100);
                        }
                    } else {
                        if (ivZoom != null) {
                            ivZoom.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (ivZoom == null || ivZoom.getContext() == null || ((Activity) ivZoom.getContext()).isDestroyed()
                                            || ((Activity) ivZoom.getContext()).isFinishing()) {
                                        return;
                                    }
                                    if (isCurrent) {
                                        ToastUtil.show(AppConfig.getContext(), "加载失败,请检查网络");
                                    }
                                }
                            }, 100);
                        }
                    }
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    pbLoading.setVisibility(View.GONE);
                    return false;
                }
            }).into(ivZoom);
        } else {
            showGif(ivZoom, tvViewOrigin, media.getCompressPath(), pbLoading, isCurrent);
        }
    }

    private boolean activityIsFinish() {
        if (context == null || context.isDestroyed() || context.isFinishing()) {
            return true;
        }
        return false;
    }

    /*
     * 保存图片到本地
     * */
    private void saveImageToLocal(ZoomImageView ivZoom, SubsamplingScaleImageView ivLong,
                                  boolean isLong, LocalMedia media, boolean isGif, boolean isHttp,
                                  boolean isOriginal, LinearLayout llLook, boolean isCurrent, TextView tvViewOrigin) {
        if (activityIsFinish()) {
            return;
        }
        String format = PictureFileUtils.getFileFormatName(media.getCompressPath());
        if (isGif) {
            String path = !TextUtils.isEmpty(media.getPath()) ? media.getPath() : media.getCompressPath();
            if (isHttp) {
                String cacheFile = PictureFileUtils.getFilePathOfImage(path, context);
                if (PictureFileUtils.hasImageCache(cacheFile, media.getSize())) {
                    saveImageFromCacheFile(cacheFile, format, ivZoom);
                } else {
                    downloadOriginImage(path, tvViewOrigin, null, ivZoom, null, ivLong, isLong, true, isGif, llLook, isCurrent);
                }
            } else {
                if (PictureFileUtils.hasImageCache(path, media.getSize())) {
                    saveImageFromCacheFile(path, format, ivZoom);
                } else if (PictureFileUtils.hasImageCache(path, media.getSize())) {
                    saveImageFromCacheFile(path, format, ivZoom);
                } else {
                    downloadOriginImage(path, tvViewOrigin, null, ivZoom, null, ivLong, isLong, true, isGif, llLook, isCurrent);
                }
            }
        } else {
            if (!isOriginal) {
                File local = CustomGlideModule.getCacheFile(media.getCompressPath());
                if (local != null && local.exists()) {
                    saveImageFromCacheFile(local.getAbsolutePath(), format, ivZoom);
                } else {
                    saveImageFromDrawable(ivZoom);
                }
            } else {
                if (isHttp) {
                    String cacheFile = PictureFileUtils.getFilePathOfImage(media.getPath(), context);
                    if (PictureFileUtils.hasImageCache(cacheFile, media.getSize())) {
                        saveImageFromCacheFile(cacheFile, format, ivZoom);
                    } else {
                        saveImageFromDrawable(ivZoom);
                    }
                } else {
                    if (PictureFileUtils.hasImageCache(media.getPath(), media.getSize())) {
                        saveImageFromCacheFile(media.getPath(), format, ivZoom);
                    } else if (PictureFileUtils.hasImageCache(media.getCompressPath(), media.getSize())) {
                        saveImageFromCacheFile(media.getCompressPath(), format, ivZoom);
                    } else {
                        String cacheFile = PictureFileUtils.getFilePathOfImage(media.getPath(), context);
                        if (PictureFileUtils.hasImageCache(cacheFile, media.getSize())) {
                            saveImageFromCacheFile(cacheFile, format, ivZoom);
                        } else {
                            saveImageFromDrawable(ivZoom);
                        }
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
            if (ivZoom != null) {
                ivZoom.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isSuccess) {
                            ToastUtil.show(AppConfig.getContext(), "保存成功");
                        }
                    }
                }, 100);
            }
        } else if (drawable instanceof GifDrawable) {

        }
    }

    //从本地缓存中存储到本地
    private void saveImageFromCacheFile(String filePath, String format, ZoomImageView ivZoom) {
        if (!TextUtils.isEmpty(filePath) && context != null && ivZoom != null) {
            boolean isSuccess = PicSaveUtils.saveOriginImage(context, filePath, format);
            if (ivZoom != null) {
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
    }

    private void showImage2(ZoomImageView ivZoom, SubsamplingScaleImageView ivLong,
                            TextView tvViewOrigin, ImageView ivDownload, LocalMedia media,
                            boolean isOrigin, boolean hasRead, boolean isHttp, boolean isLong,
                            ProgressBar pbLoading, LinearLayout llLock, boolean isCurrent) {
        tvViewOrigin.setTag(media.getSize());
        showViewOrigin(isHttp, isOrigin, hasRead, tvViewOrigin, media.getSize(), llLock);
        InitTargetSize targetSize = new InitTargetSize(media, context).invoke();
        if (isHttp) {
            if (isOrigin) {
                if (hasRead) {//原图已读,就显示
                    loadImage(media.getCompressPath(), ivZoom, ivLong, isLong, false, pbLoading, isCurrent, targetSize.width, targetSize.height);
                    if (!TextUtils.isEmpty(media.getPath())) {
                        if (ivZoom != null) {
                            ivZoom.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadImage(media.getPath(), ivZoom, ivLong, isLong, false, pbLoading, isCurrent, targetSize.width, targetSize.height);
                                }
                            }, 50);
                        }
                    }
                    showZoomView(ivZoom, true);
                } else {
                    if (!TextUtils.isEmpty(media.getCutPath())) {
                        loadImage(media.getCutPath(), ivZoom, ivLong, isLong, false, pbLoading, isCurrent, targetSize.width, targetSize.height);
                        loadImage(media.getCompressPath(), ivZoom, ivLong, isLong, false, pbLoading, isCurrent, targetSize.width, targetSize.height);
                    } else {
                        loadImage(media.getCompressPath(), ivZoom, ivLong, isLong, false, pbLoading, isCurrent, targetSize.width, targetSize.height);
                    }
                }
            } else {
                ivDownload.setVisibility(View.VISIBLE);
                loadImage(media.getCutPath(), ivZoom, ivLong, isLong,
                        false, pbLoading, isCurrent, targetSize.width, targetSize.height);
                //延时加载预览图
                if (ivZoom != null) {
                    ivZoom.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadImage(media.getCompressPath(), ivZoom, ivLong, isLong, false, pbLoading, isCurrent, targetSize.width, targetSize.height);
                        }
                    }, 50);
                }
            }
        } else {
            ivDownload.setVisibility(View.VISIBLE);
            String url = !TextUtils.isEmpty(media.getPath()) ? media.getPath() : media.getCompressPath();
            loadImage(url, ivZoom, ivLong, isLong, false, pbLoading, isCurrent, targetSize.width, targetSize.height);
        }
    }

    private void showViewOrigin(boolean isHttp, boolean isOrigin, boolean hasRead, TextView tvViewOrigin, long size, LinearLayout llLook) {
        if (isHttp && isOrigin && !hasRead) {
            tvViewOrigin.setVisibility(View.VISIBLE);
            llLook.setVisibility(View.VISIBLE);
            if (size > 0) {
//                tvViewOrigin.setText("查看原图(" + ImgSizeUtil.formatFileSize(size) + ")");
                tvViewOrigin.setText("查看原图(" + net.cb.cb.library.utils.FileUtils.getFileSizeString(size) + ")");
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

    private void showGif(ZoomImageView ivZoom, TextView tvViewOrigin, String path, ProgressBar pbLoading, boolean isCurrent) {
        if (tvViewOrigin != null) {
            tvViewOrigin.setVisibility(View.GONE);
        }
        if (ivZoom == null || ivZoom.getContext() == null || ((Activity) ivZoom.getContext()).isDestroyed()
                || ((Activity) ivZoom.getContext()).isFinishing()) {
            return;
        }
        RequestOptions gifOptions = new RequestOptions()
                .priority(Priority.LOW)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(ivZoom.getContext())
                .asGif()
                .apply(gifOptions)
                .load(path)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, final Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        if (pbLoading != null) {
                            pbLoading.setVisibility(View.GONE);
                        }
                        if (e.getMessage().contains("FileNotFoundException")) {
                            if (ivZoom != null) {
                                ivZoom.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ivZoom.setImageResource(R.mipmap.ic_img_past);
                                    }
                                }, 100);
                            }
                        } else {
                            if (ivZoom != null) {
                                ivZoom.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (ivZoom == null || ivZoom.getContext() == null || ((Activity) ivZoom.getContext()).isDestroyed()
                                                || ((Activity) ivZoom.getContext()).isFinishing()) {
                                            return;
                                        }
                                        if (isCurrent) {
                                            ToastUtil.show(AppConfig.getContext(), "加载失败,请检查网络");
                                        }
                                    }
                                }, 100);
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        pbLoading.setVisibility(View.GONE);
                        return false;
                    }

                }).into(ivZoom);
    }

    /*
     * 加载图片
     * */
    private void loadImage(String url, ZoomImageView ivZoom,
                           SubsamplingScaleImageView ivLong,
                           boolean isLong, boolean isOrigin, ProgressBar pbLoading,
                           boolean isCurrent, int targetWidth, int targetHeight) {
        if (activityIsFinish()) {
            return;
        }
        RequestListener requestListener = new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                if (pbLoading != null) {
                    pbLoading.setVisibility(View.GONE);
                }
                if (e.getMessage().contains("FileNotFoundException")) {
                    ivZoom.setImageResource(R.mipmap.ic_img_past);
                } else {
                    if (isCurrent) {
                        if (ivZoom != null) {
                            ivZoom.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(AppConfig.getContext(), "加载失败,请检查网络");
                                }
                            }, 100);
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        };
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .format(DecodeFormat.PREFER_ARGB_8888);
        boolean isSizeNormal = targetWidth > 0 && targetHeight > 0;
        if (isSizeNormal) {
            Glide.with(context)//TODO bugly #107911
                    .asBitmap()
                    .load(url)
                    .listener(requestListener)
                    .apply(options)
                    .into(new SimpleTarget<Bitmap>(480, 800) {
                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            if (isLong) {
                                ivLong.setVisibility(View.VISIBLE);
                                ivZoom.setVisibility(View.GONE);
                                displayLongPic(resource, ivLong);
                            } else {
                                ivLong.setVisibility(View.GONE);
                                ivZoom.setVisibility(View.VISIBLE);
                                ivZoom.setImageBitmap(resource);
                            }
                            if (pbLoading != null) {
                                pbLoading.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .listener(requestListener)
                    .apply(options)
                    .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            ivZoom.setImageBitmap(resource);
                            if (pbLoading != null) {
                                pbLoading.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }


    /*
     * 下载原图
     * */
    private void downloadOriginImage(String originUrl, TextView tvViewOrigin, ImageView ivDownload,
                                     ZoomImageView ivZoom, LargeImageView ivLarge, SubsamplingScaleImageView ivLong,
                                     boolean isLong, boolean needSave, boolean isGif, LinearLayout llLook,
                                     boolean isCurrent) {
        if (TextUtils.isEmpty(originUrl)) {
            return;
        }
        if (tvViewOrigin == null) {
            return;
        }
        String format = PictureFileUtils.getFileFormatName(originUrl);
        tvViewOrigin.postDelayed(new Runnable() {
            @Override
            public void run() {
                setDownloadProgress(tvViewOrigin, 0, llLook);
            }
        }, 100);
        if (activityIsFinish()) {
            return;
        }
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
                        if (activityIsFinish()) {
                            return;
                        }
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isGif) {
//                                    showGif(ivZoom, tvViewOrigin, file.getAbsolutePath());
//                                    hideLargeImageView(ivLarge);
                                } else {
//                                    ivLarge.setAlpha(0);
//                                    ivLarge.setVisibility(View.VISIBLE);
                                    setDownloadProgress(tvViewOrigin, 100, llLook);
                                    ivDownload.setEnabled(true);
//                                    ivLarge.setImage(new FileBitmapDecoderFactory(file.getAbsolutePath()));
                                    loadImage(file.getAbsolutePath(), ivZoom, ivLong, isLong, true, null, isCurrent, 0, 0);
                                    showZoomView(ivZoom, true);
//                                loadLargeImage(file.getAbsolutePath(), ivLarge);
                                    MyDiskCacheUtils.getInstance().putFileNmae(filePath, fileSave.getAbsolutePath());
                                }
                                //这边要改成已读
                                msgDao.ImgReadStatSet(originUrl, true);
                            }
                        });
                        if (needSave) {
                            saveImageFromCacheFile(file.getAbsolutePath(), format, ivZoom);
                        }
                    }

                    @Override
                    public void onDownloading(final int progress) {
//                        Log.d(TAG, "onDownloading: " + progress);
                        if (isGif) {
                            return;
                        }
                        if (activityIsFinish()) {
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
                        if (ivZoom != null) {
                            ivZoom.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    preProgress = 0;
                                    ToastUtil.show(AppConfig.getContext(), "加载失败,请检查网络");

                                }
                            }, 100);
                        }
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
    private void showDownLoadDialog(final LocalMedia media, SubsamplingScaleImageView ivLong,
                                    boolean isLong, ZoomImageView ivZoom, boolean isHttp,
                                    boolean isOriginal, LinearLayout llLook, boolean isGif, boolean isCurrent, TextView tvViewOrigin) {
        final PopupSelectView popupSelectView;
        if (activityIsFinish()) {
            return;
        }
        //收藏详情需求又改为只显示3项
        if (fromWhere == PictureConfig.FROM_COLLECT_DETAIL || fromWhere == PictureConfig.FROM_CIRCLE) {
            popupSelectView = new PopupSelectView(context, collectStrings);
        } else {
            if (media.isCanCollect()) {
                if (isGif) {
                    popupSelectView = new PopupSelectView(context, gifStrings);
                } else {
                    popupSelectView = new PopupSelectView(context, newStrings);
                }
            } else if (isGif) {
                popupSelectView = new PopupSelectView(context, gifStrings);
            } else {
                popupSelectView = new PopupSelectView(context, strings);
            }
        }
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                String msgId = media.getMsg_id();
                //收藏详情需求又改为只显示3项
                if (fromWhere == PictureConfig.FROM_COLLECT_DETAIL) {
                    if (postsion == 0) {//收藏详情转发单独处理
                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                            ToastUtil.show(context.getString(R.string.user_disable_message));
                            return;
                        }
                        checkFile(msgId, PictureConfig.FROM_COLLECT_DETAIL, 1, null);
                    } else if (postsion == 1) {//保存
                        saveImageToLocal(ivZoom, ivLong, isLong, media, FileUtils.isGif(media.getCompressPath()), isHttp, isOriginal, llLook, isCurrent, tvViewOrigin);
                    }
                } else {
                    //含有收藏项
                    if (media.isCanCollect()) {
                        if (postsion == 0) {//默认转发
                            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                ToastUtil.show(context.getString(R.string.user_disable_message));
                                return;
                            }
                            checkFile(msgId, PictureConfig.FROM_DEFAULT, 1, null);
                        } else if (postsion == 1) {//保存
                            saveImageToLocal(ivZoom, ivLong, isLong, media, FileUtils.isGif(media.getCompressPath()), isHttp, isOriginal, llLook, isCurrent, tvViewOrigin);
                        } else if (postsion == 2) {//收藏
                            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                ToastUtil.show(context.getString(R.string.user_disable_message));
                                return;
                            }
                            checkFile(msgId, PictureConfig.FROM_DEFAULT, 2, null);
                        } else if (postsion == 3) {//识别二维码
                            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                ToastUtil.show(context.getString(R.string.user_disable_message));
                                return;
                            }
                            // scanningImage(media.getPath());
                            scanningQrImage(media.getCompressPath(), ivZoom);
                        } else if (postsion == 4) {//长按跳编辑界面，编辑完成后，返回新图片的本地路径到PictureExternalPreviewActivity
                            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                ToastUtil.show(context.getString(R.string.user_disable_message));
                                return;
                            }
                            checkFile(msgId, PictureConfig.FROM_DEFAULT, 3, media);
                        }

                    } else {
                        //不含有收藏项
                        if (postsion == 0) {//默认转发
                            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                ToastUtil.show(context.getString(R.string.user_disable_message));
                                return;
                            }
                            if (fromWhere == PictureConfig.FROM_CIRCLE) {
                                checkFile(msgId, PictureConfig.FROM_CIRCLE, 1, null);
                            } else {
                                checkFile(msgId, PictureConfig.FROM_DEFAULT, 1, null);
                            }
                        } else if (postsion == 1) {//保存
                            saveImageToLocal(ivZoom, ivLong, isLong, media, FileUtils.isGif(media.getCompressPath()), isHttp, isOriginal, llLook, isCurrent, tvViewOrigin);
                        } else if (postsion == 2) {//识别二维码
                            if (fromWhere == PictureConfig.FROM_CIRCLE) {
                            } else {
                                if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                    ToastUtil.show(context.getString(R.string.user_disable_message));
                                    return;
                                }
                                scanningQrImage(media.getCompressPath(), ivZoom);
                            }
                        } else if (postsion == 3) {//长按跳编辑界面，编辑完成后，返回新图片的本地路径到PictureExternalPreviewActivity
                            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                ToastUtil.show(context.getString(R.string.user_disable_message));
                                return;
                            }
                            Intent intent = new Intent(context, ImageShowActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("imgpath", media.getCompressPath());
                            bundle.putString("msg_id", msgId);
                            bundle.putInt("img_width", media.getWidth());
                            bundle.putInt("img_height", media.getHeight());
                            intent.putExtras(bundle);
                            context.startActivityForResult(intent, PictureExternalPreviewActivity.IMG_EDIT);
                        }
                    }
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

//        if (bmp.getHeight() > 4000 || bmp.getWidth() > 4000) {
//            if (bmp.getHeight() > bmp.getWidth()) {
//
//                float sp = 4000.0f / bmp.getHeight();
//                bmp = scaleBitmap(bmp, sp);
//            } else {
//                float sp = 4000.0f / bmp.getWidth();
//                bmp = scaleBitmap(bmp, sp);
//            }
//        }

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


    /**
     * 转发还是收藏
     *
     * @param msgId
     */
    private void checkFile(String msgId, int fromWhere, int type, LocalMedia media) {
        mIPreviewImage.onClick(msgId, fromWhere, type, media);
    }

    private void initSize(LocalMedia media, int targetWidth, int targetHeight) {
        int realW = media.getWidth();
        int realH = media.getHeight();
        int screenWidth = ScreenUtil.getScreenWidth(context);
        int screenHeight = ScreenUtil.getScreenHeight(context);
        if (realH > 0) {
            double scale = (realW * 1.00) / realH;
            if (scale < 0.33) {//长图
                targetWidth = realW;
                targetHeight = realH;
            } else {
                if (realW > screenWidth && realW < screenWidth * 2) {
                    targetWidth = screenWidth;
                    targetHeight = (int) (targetWidth / scale);
                } else if (realH > screenHeight) {
                    targetHeight = screenHeight;
                    targetWidth = (int) (targetHeight * scale);
                } else if (realW == realH) {
                    targetWidth = screenWidth;
                    targetHeight = screenWidth;
                } else {
                    targetWidth = realW;
                    targetHeight = (int) (targetWidth / scale);
                }
            }
        } else {
            targetWidth = realW;
            targetHeight = realW;
        }
    }

    private static class InitTargetSize {
        private int width;
        private int height;
        private final LocalMedia media;
        private Context context;

        public InitTargetSize(LocalMedia media, Context context) {
            this.media = media;
            this.context = context;
        }

        public InitTargetSize invoke() {
            int realW = media.getWidth();
            int realH = media.getHeight();
            int screenWidth = ScreenUtil.getScreenWidth(context);
            int screenHeight = ScreenUtil.getScreenHeight(context);
            if (realH > 0) {
                double scale = (realW * 1.00) / realH;
                if (scale < 0.33) {//长图
                    width = realW;
                    height = realH;
                } else {
                    if (realW > screenWidth && realW < screenWidth * 2) {
                        width = screenWidth;
                        height = (int) (width / scale);
                    } else if (realH > screenHeight) {
                        height = screenHeight;
                        width = (int) (height * scale);
                    } else if (realW == realH) {
                        width = screenWidth;
                        height = screenWidth;
                    } else {
                        width = realW;
                        height = (int) (width / scale);
                    }
                }
            } else {
                width = realW;
                height = realW;
            }
            return this;
        }
    }

}
