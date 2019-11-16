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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.ImgSizeUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;

import java.io.File;
import java.util.Hashtable;
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
    //    private IPreviewImageListener listener;
    private String[] strings = {"发送给朋友", "保存图片", "识别二维码", "取消"};
    private View parentView;


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
        //TODO:当前ZoomView算法不够强大，处理不了高分辨率图片（15M），所以要用LargeImageView来补充显示大图，待优化
        final ZoomImageView ivZoom = contentView.findViewById(R.id.iv_image);
        final LargeImageView ivLarge = contentView.findViewById(R.id.iv_image_large);
        final SubsamplingScaleImageView ivLong = contentView.findViewById(R.id.iv_long);
        final TextView tvViewOrigin = contentView.findViewById(R.id.tv_view_origin);
        ImageView ivDownload = contentView.findViewById(R.id.iv_download);
        LocalMedia media = datas.get(position);
        loadAndShowImage(media, ivZoom, ivLarge, ivLong, ivDownload, tvViewOrigin);
        (container).addView(contentView, 0);
        return contentView;
    }

    private void loadAndShowImage(LocalMedia media, ZoomImageView ivZoom, LargeImageView ivLarge, SubsamplingScaleImageView ivLong, ImageView ivDownload, TextView tvViewOrigin) {
        String path = media.getCompressPath();//缩略图路径
        String originUrl = media.getPath();//原图路径
        boolean isGif = FileUtils.isGif(path);//是否是gif图片
        boolean isOriginal = StringUtil.isNotNull(originUrl);//是否有原图
        boolean isHttp = PictureMimeType.isHttp(path);
        boolean isLong = PictureMimeType.isLongImg(media);
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
            showImage(ivZoom, ivLarge, ivLong, tvViewOrigin, ivDownload, media, isOriginal, hasRead, isHttp, isLong);
        }

        //下载
        boolean finalHasRead = hasRead;
        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivDownload.setEnabled(false);
                if (isOriginal) {
                    if (finalHasRead) {
                        saveImageToLocal(ivZoom, media, isGif, isHttp, isOriginal);
                    } else {
                        downloadOriginImage(originUrl, tvViewOrigin, ivDownload, ivZoom, ivLarge, true);
                    }
                } else {
                    saveImageToLocal(ivZoom, media, isGif, isHttp, isOriginal);
                }
            }
        });

        //查看原图
        tvViewOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvViewOrigin.setEnabled(false);
                tvViewOrigin.setClickable(false);
                downloadOriginImage(media.getPath(), tvViewOrigin, ivDownload, ivZoom, ivLarge, false);
            }
        });
        ivZoom.setOnViewTapListener(new PhotoViewAttacher2.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
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
                showDownLoadDialog(media, ivZoom, isHttp, isOriginal);
                return true;
            }
        });

        ivLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                setDownloadProgress(tvViewOrigin, 100);
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
                showDownLoadDialog(media, ivZoom, isHttp, isOriginal);
                return false;
            }
        });

        ivLong.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDownLoadDialog(media, ivZoom, isHttp, isOriginal);
                return false;
            }
        });
        ivLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (download != null) {//取消当前请求
                    download.cancel();
                }
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
            }
        });
    }

    /*
     * 保存图片到本地
     * */
    private void saveImageToLocal(ZoomImageView ivZoom, LocalMedia media, boolean isGif, boolean isHttp, boolean isOriginal) {
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
                        ToastUtil.show(context, "保存成功");
                    }
                }
            }, 100);
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
                        ToastUtil.show(context, "保存成功");
                    }
                }
            }, 100);


        }
    }

    private void showImage(ZoomImageView ivZoom, LargeImageView ivLarge, SubsamplingScaleImageView ivLong, TextView tvViewOrigin, ImageView ivDownload, LocalMedia media, boolean isOrigin, boolean hasRead, boolean isHttp, boolean isLong) {
        tvViewOrigin.setTag(media.getSize());
        ivZoom.setVisibility(isLong ? View.GONE : View.VISIBLE);
        ivLarge.setVisibility(isLong ? View.VISIBLE : View.GONE);
        showViewOrigin(isHttp, isOrigin, hasRead, tvViewOrigin, media.getSize());
        if (isHttp) {
            if (isLong) {
                if (isOrigin) {
                    loadImageLong(media.getPath(), ivLong, false);
                } else {
                    loadImageLong(media.getCompressPath(), ivLong, false);
                }
            } else {
                if (isOrigin) {
                    if (hasRead) {//原图已读,就显示
                        String cachePath = PictureFileUtils.getFilePathOfImage(media.getPath(), context);
                        if (PictureFileUtils.hasImageCache(cachePath, media.getSize())) {
                            loadImage(media.getCompressPath(), ivZoom, false);
                            //TODO:不设置Alpha 和 visible 就不能响应手势
                            ivLarge.setAlpha(0);
                            ivLarge.setVisibility(View.VISIBLE);
                            ivLarge.setImage(new FileBitmapDecoderFactory(cachePath));
                        } else {
                            loadImage(media.getCompressPath(), ivZoom, true);
                            loadLargeImage(media.getPath(), ivLarge);
                        }
                    } else {
                        if (!TextUtils.isEmpty(media.getCutPath()) && (media.getWidth() > 1080 || media.getHeight() > 1920)) {
                            loadImage(media.getCutPath(), ivZoom, false);
                        } else {
                            loadImage(media.getCompressPath(), ivZoom, false);
                        }
                    }
                } else {
                    ivDownload.setVisibility(View.VISIBLE);
                    loadImage(media.getCompressPath(), ivZoom, false);
                }
            }
        } else {
            ivDownload.setVisibility(View.VISIBLE);
            if (isLong) {
                if (!TextUtils.isEmpty(media.getPath())) {
                    loadImageLong(media.getPath(), ivLong, false);
                } else {
                    loadImageLong(media.getCompressPath(), ivLong, false);
                }
            } else {
                if (!TextUtils.isEmpty(media.getPath())) {
                    loadImage(media.getPath(), ivZoom, true);
                    ivLarge.setAlpha(0);
                    ivLarge.setVisibility(View.VISIBLE);
                    ivLarge.setImage(new FileBitmapDecoderFactory(media.getPath()));
                } else {
                    loadImage(media.getCompressPath(), ivZoom, false);
                }
            }
        }
    }

    private void showViewOrigin(boolean isHttp, boolean isOrigin, boolean hasRead, TextView tvViewOrigin, long size) {
        if (isHttp && isOrigin && !hasRead) {
            tvViewOrigin.setVisibility(View.VISIBLE);
            tvViewOrigin.setText("查看原图(" + ImgSizeUtil.formatFileSize(size) + ")");
        } else {
            tvViewOrigin.setVisibility(View.GONE);
        }
    }

    private boolean isLongImage(int w, int h) {
        double rate = w * 1.00 / h;
        if (rate < 0.2) {
            return true;
        }
        return false;
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
    private void loadImage(String url, ZoomImageView ivZoom, boolean isOrigin) {
        if (!isOrigin) {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .format(DecodeFormat.PREFER_RGB_565);
            Glide.with(ivZoom.getContext())
                    .asBitmap()
                    .load(url)
                    .apply(options)  //480     800
                    .into(new SimpleTarget<Bitmap>(800, 800) {
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
        } else {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .format(DecodeFormat.PREFER_ARGB_8888);
            Glide.with(ivZoom.getContext())
                    .asBitmap()
                    .load(url)
                    .apply(options)  //480     800
                    .into(new SimpleTarget<Bitmap>() {
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
    }

    /*
     * 加载长图片
     * */
    private void loadImageLong(String url, SubsamplingScaleImageView ivLong, boolean isOrigin) {
        if (!isOrigin) {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .format(DecodeFormat.PREFER_RGB_565);
            Glide.with(ivLong.getContext())
                    .asBitmap()
                    .load(url)
                    .apply(options)  //480     800
                    .into(new SimpleTarget<Bitmap>(800, 800) {
                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
//                        dismissDialog();
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                        dismissDialog();
                            displayLongPic(resource, ivLong);
                        }
                    });
        } else {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .format(DecodeFormat.PREFER_ARGB_8888);
            Glide.with(ivLong.getContext())
                    .asBitmap()
                    .load(url)
                    .apply(options)  //480     800
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
//                        dismissDialog();
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                        dismissDialog();
                            displayLongPic(resource, ivLong);
                        }
                    });
        }
    }

    private void loadLargeImage(String url, LargeImageView iv) {
        iv.setAlpha(0);
        iv.setVisibility(View.VISIBLE);
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context)
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
                        iv.setImage(resource);
                    }
                });
    }

    /*
     * 下载原图
     * */
    private void downloadOriginImage(String originUrl, TextView tvViewOrigin, ImageView ivDownload, ZoomImageView ivZoom, LargeImageView ivLarge, boolean needSave) {
        final String filePath = context.getExternalCacheDir().getAbsolutePath() + "/Image/";
        final String fileName = originUrl.substring(originUrl.lastIndexOf("/") + 1);
        File fileSave = new File(filePath + "/" + fileName);//原图保存路径

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
                                ivLarge.setAlpha(0);
                                ivLarge.setVisibility(View.VISIBLE);
                                setDownloadProgress(tvViewOrigin, 100);
                                ivDownload.setEnabled(true);
                                ivLarge.setImage(new FileBitmapDecoderFactory(file.getAbsolutePath()));
//                                loadLargeImage(file.getAbsolutePath(), ivLarge);
                                MyDiskCacheUtils.getInstance().putFileNmae(filePath, fileSave.getAbsolutePath());
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

    /**
     * 长按弹窗提示
     */
    private void showDownLoadDialog(final LocalMedia media, ZoomImageView ivZoom, boolean isHttp, boolean isOriginal) {
        final PopupSelectView popupSelectView = new PopupSelectView(context, strings);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                if (postsion == 0) {//转发
                    String msgId = media.getMsg_id();
                    if (!TextUtils.isEmpty(msgId)) {
                        MsgAllBean msgAllBean = msgDao.getMsgById(msgId);
                        if (msgAllBean != null) {
                            context.startActivity(new Intent(context, MsgForwardActivity.class)
                                    .putExtra(MsgForwardActivity.AGM_JSON, new Gson().toJson(msgAllBean)));
                        }
                    } else {
                        //TODO:无消息id，要不要自己新建一条消息记录，然后发出去？

                    }
                } else if (postsion == 1) {//保存
                    saveImageToLocal(ivZoom, media, false, isHttp, isOriginal);
                } else if (postsion == 2) {//识别二维码
                    scanningImage(media.getPath());
                }
                popupSelectView.dismiss();

            }
        });
        popupSelectView.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);

    }

//    private void setClickListener(IPreviewImageListener l) {
//        listener = l;
//    }
//
//    public interface IPreviewImageListener {
//        void onLongClick(LocalMedia media);
//    }

    /**
     * 扫描二维码图片的方法
     *
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
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
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
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
        LogUtil.getLog().i(TAG, "displayLongPic: 显示长图");

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


}