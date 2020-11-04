package com.yanlong.im.user.ui.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.glide.CustomGlideModule;
import com.luck.picture.lib.photoview.PhotoViewAttacher2;
import com.luck.picture.lib.photoview.ZoomImageView;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.utils.PicSaveUtils;
import com.luck.picture.lib.view.PopupSelectView;
import com.luck.picture.lib.view.bigImg.factory.FileBitmapDecoderFactory;
import com.luck.picture.lib.zxing.decoding.RGBLuminanceSource;
import com.yalantis.ucrop.util.FileUtils;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.eventbus.EventCollectImgOrVideo;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.utils.MyDiskCacheUtils;
import com.yanlong.im.utils.QRCodeManage;
import com.yanlong.im.utils.UserUtil;
import com.zhaoss.weixinrecorded.activity.ImageShowActivity;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.manager.FileManager;
import net.cb.cb.library.utils.DownloadUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.ScreenUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Hashtable;

import okhttp3.Call;

/**
 * @author Liszt
 * @date 2020/9/3
 * Description 查看图片fragment
 */
public class LookUpPhotoFragment extends BaseMediaFragment {
    private final String TAG = getClass().getSimpleName();
    public String[] strings = {"发送给朋友", "保存图片", "识别图中二维码", "编辑", "取消"};
    public String[] newStrings = {"发送给朋友", "保存图片", "收藏", "识别图中二维码", "编辑", "取消"};
    public String[] gifStrings = {"发送给朋友", "保存图片", "收藏", "识别图中二维码", "取消"};
    public String[] collectStrings = {"发送给朋友", "保存图片", "取消"};
    private ZoomImageView ivImage;
    private TextView tvViewOrigin;
    private ImageView ivDownload;
    private ProgressBar pbLoading;
    private LinearLayout llLook;
    private LocalMedia media;
    private Call downloadCall;
    private int preProgress;
    private MsgDao msgDao = new MsgDao();
    private boolean isGif;
    private String collectJson;
    private boolean isHttp;
    private boolean isOriginal;
    private int fromWhere;
    private int targetHeight;
    private int targetWidth;
    private ImageView ivPreview;


    public static LookUpPhotoFragment newInstance(LocalMedia media, int from) {
        LookUpPhotoFragment fragment = new LookUpPhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("media", media);
        bundle.putInt("from", from);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_lookup_photo, container, false);
        ivImage = rootView.findViewById(R.id.iv_image);
        tvViewOrigin = rootView.findViewById(R.id.tv_view_origin);
        ivDownload = rootView.findViewById(R.id.iv_download);
        pbLoading = rootView.findViewById(R.id.pb_loading);
        llLook = rootView.findViewById(R.id.ll_look);
        ivPreview = rootView.findViewById(R.id.iv_preview);
        initData();
        return rootView;
    }

    private void initData() {
        media = getArguments().getParcelable("media");
        fromWhere = getArguments().getInt("from");
        String thumbUrl = media.getCutPath();//缩略图路径
        String previewUrl = media.getCompressPath();//预览图路径
        String originUrl = media.getPath();//原图路径
        tvViewOrigin.setTag(media.getSize());
        //是否有原图
        isOriginal = StringUtil.isNotNull(originUrl);
        isHttp = PictureMimeType.isHttp(previewUrl);
        boolean hasRead = media.isHasRead();
        isGif = isGif(media, isHttp, isOriginal);
        getSize();
        if (isGif) {
            showLookOrigin(false);
            String url = !TextUtils.isEmpty(originUrl) ? originUrl : previewUrl;
            File local = CustomGlideModule.getCacheFile(url);
            if (local == null) {
                loadGif(url);
            } else {
                loadGif(local.getAbsolutePath());
            }
        } else {
            if (isHttp) {
                if (isOriginal && hasRead) {
                    showLookOrigin(false);
                    loadImage(thumbUrl);
                    ivImage.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadImage(originUrl);
                        }
                    }, 10);
                } else {
                    if (isOriginal && !hasRead) {
                        showLookOrigin(true);
                    } else {
                        showLookOrigin(false);
                    }
                    loadImage(thumbUrl);
                    ivImage.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadImage(previewUrl);
                        }
                    }, 10);
                }
            } else {
                showLookOrigin(false);
                loadImage(!TextUtils.isEmpty(originUrl) ? originUrl : previewUrl);
            }
        }
    }

    private void showLookOrigin(boolean b) {
        llLook.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivImage.setOnViewTapListener(new PhotoViewAttacher2.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                onCancel();
            }
        });

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancel();
            }
        });

        //长按弹出dialog
        ivImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDownLoadDialog(media);
                return true;
            }
        });

        //查看原图
        llLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadOriginImage(!TextUtils.isEmpty(media.getPath()) ? media.getPath() : media.getCompressPath(), false, isGif);
            }
        });

        //下载
        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadOriginImage(!TextUtils.isEmpty(media.getPath()) ? media.getPath() : media.getCompressPath(), true, isGif);
            }
        });

        ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                ((PreviewMediaActivity) getActivity()).startPreviewAll(media.getMsg_id());
            }
        });
    }

    private void loadImage(String url) {
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
                    ivImage.setImageResource(R.mipmap.ic_img_past);
                } else {
                    if (isCurrent(media.getPosition())) {
                        ivImage.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show(AppConfig.getContext(), "加载失败,请检查网络");
                            }
                        }, 100);
                    }
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        };
        boolean isSizeNormal = targetWidth > 0 && targetHeight > 0;
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .format(DecodeFormat.PREFER_ARGB_8888);
        if (isSizeNormal) {
            Glide.with(getActivity())
                    .asBitmap()
                    .load(url)
                    .listener(requestListener)
                    .apply(options)
                    .into(new SimpleTarget<Bitmap>(targetWidth, targetHeight) {
                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            ivImage.setImageBitmap(resource);
                            if (pbLoading != null) {
                                pbLoading.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            Glide.with(getActivity())
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
                            ivImage.setImageBitmap(resource);
                            if (pbLoading != null) {
                                pbLoading.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    private void loadGif(String url) {
        if (activityIsFinish()) {
            return;
        }
        RequestOptions gifOptions = new RequestOptions()
                .priority(Priority.LOW)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(getActivity())
                .asGif()
                .apply(gifOptions)
                .load(url)
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, final Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        if (pbLoading != null) {
                            pbLoading.setVisibility(View.GONE);
                        }
                        if (e.getMessage().contains("FileNotFoundException")) {
                            ivImage.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ivImage.setImageResource(R.mipmap.ic_img_past);
                                }
                            }, 100);

                        } else {
                            if (isCurrent(media.getPosition())) {
                                ivImage.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (ivImage == null || ivImage.getContext() == null || ((Activity) ivImage.getContext()).isDestroyed()
                                                || ((Activity) ivImage.getContext()).isFinishing()) {
                                            return;
                                        }
                                        ToastUtil.show(AppConfig.getContext(), "加载失败,请检查网络");
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

                }).into(ivImage);
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

    private void onCancel() {
        if (downloadCall != null) {//取消当前请求
            downloadCall.cancel();
        }
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().finish();
            getActivity().overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
        }
    }

    /*
     * 下载原图
     * */
    private void downloadOriginImage(String url, boolean needSave, boolean isGif) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (tvViewOrigin == null) {
            return;
        }
        String format = PictureFileUtils.getFileFormatName(url);
        if (isHttp) {
            tvViewOrigin.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setDownloadProgress(tvViewOrigin, 0);
                }
            }, 100);
            if (activityIsFinish()) {
                return;
            }
            final String fileName = url.substring(url.lastIndexOf("/") + 1);
            final String filePath = FileManager.getInstance().getImageCachePath();
//        final String filePath = getActivity().getExternalCacheDir().getAbsolutePath() + "/Image/";
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
                    downloadCall = DownloadUtil.get().download(url, filePath, fileName, new DownloadUtil.OnDownloadListener() {
                        @Override
                        public void onDownloadSuccess(final File file) {
                            if (activityIsFinish()) {
                                return;
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isGif) {
                                    } else {
                                        setDownloadProgress(tvViewOrigin, 100);
                                        ivDownload.setEnabled(true);
                                        loadImage(file.getAbsolutePath());
                                        MyDiskCacheUtils.getInstance().putFileNmae(filePath, fileSave.getAbsolutePath());
                                    }
                                    //这边要改成已读
                                    media.setHasRead(true);
                                    msgDao.ImgReadStatSet(media.getMsg_id(), true);
                                }
                            });
                            if (needSave) {
                                saveImageFromCacheFile(file.getAbsolutePath(), format);
                            }
                        }

                        @Override
                        public void onDownloading(final int progress) {
                            if (isGif) {
                                return;
                            }
                            if (activityIsFinish()) {
                                return;
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setDownloadProgress(tvViewOrigin, progress);
                                }
                            });

                        }

                        @Override
                        public void onDownloadFailed(Exception e) {
                            ivDownload.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ivImage.postDelayed(new Runnable() {
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
        } else {
            saveImageFromCacheFile(url, format);
        }
    }

    private boolean activityIsFinish() {
        if (getActivity() == null || getActivity().isDestroyed() || getActivity().isFinishing()) {
            return true;
        }
        return false;
    }

    /*
     * 更新下载进度
     * */
    public void setDownloadProgress(TextView tvViewOrigin, int progress) {
        if (preProgress > progress) {
            return;
        }
        preProgress = progress;
        if (tvViewOrigin == null) {
            return;
        }
        tvViewOrigin.setText("已完成 " + progress + "%");
        if (progress == 100) {
            tvViewOrigin.setVisibility(View.GONE);
            showLookOrigin(false);
            preProgress = 0;
        }
    }

    //从本地缓存中存储到本地
    private void saveImageFromCacheFile(String filePath, String format) {
        if (!TextUtils.isEmpty(filePath) && getActivity() != null) {
            boolean isSuccess = PicSaveUtils.saveOriginImage(getActivity(), filePath, format);
            ivImage.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isSuccess) {
                        ToastUtil.show(AppConfig.getContext(), "保存成功");
                    }
                }
            }, 100);
        }
    }

    /**
     * 长按弹窗提示
     */
    private void showDownLoadDialog(final LocalMedia media) {
        final PopupSelectView popupSelectView;
        if (activityIsFinish()) {
            return;
        }
        //收藏详情需求又改为只显示3项
        if (fromWhere == PictureConfig.FROM_COLLECT_DETAIL) {
            popupSelectView = new PopupSelectView(getActivity(), collectStrings);
        } else {
            if (media.isCanCollect()) {
                if (isGif) {
                    popupSelectView = new PopupSelectView(getActivity(), gifStrings);
                } else {
                    popupSelectView = new PopupSelectView(getActivity(), newStrings);
                }
            } else if (isGif) {
                popupSelectView = new PopupSelectView(getActivity(), gifStrings);
            } else {
                popupSelectView = new PopupSelectView(getActivity(), strings);
            }
        }
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int position) {
                String msgId = media.getMsg_id();
                //收藏详情需求又改为只显示3项
                if (fromWhere == PictureConfig.FROM_COLLECT_DETAIL) {
                    if (position == 0) {//收藏详情转发单独处理
                        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                            ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                            return;
                        }
                        ((PreviewMediaActivity) getActivity()).checkFile(msgId, fromWhere, CoreEnum.EActionType.FORWARD, media);
                    } else if (position == 1) {//保存
                        saveImageToLocal(ivImage, media);
                    }
                } else {
                    //含有收藏项
                    if (media.isCanCollect()) {
                        if (position == 0) {//默认转发
                            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                                return;
                            }
                            ((PreviewMediaActivity) getActivity()).checkFile(msgId, fromWhere, CoreEnum.EActionType.FORWARD, media);
                        } else if (position == 1) {//保存
                            saveImageToLocal(ivImage, media);
                        } else if (position == 2) {//收藏
                            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                                return;
                            }
                            ((PreviewMediaActivity) getActivity()).checkFile(msgId, fromWhere, CoreEnum.EActionType.COLLECTION, media);
                        } else if (position == 3) {//识别二维码
                            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                                return;
                            }
                            scanningQrImage(media.getCompressPath(), ivImage);
                        } else if (position == 4) {//长按跳编辑界面，编辑完成后，返回新图片的本地路径到PictureExternalPreviewActivity
                            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                                return;
                            }
                            ((PreviewMediaActivity) getActivity()).checkFile(msgId, fromWhere, CoreEnum.EActionType.EDIT, media);
                        }
                    } else {
                        //不含有收藏项
                        if (position == 0) {//默认转发
                            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                                return;
                            }
                            ((PreviewMediaActivity) getActivity()).checkFile(msgId, fromWhere, CoreEnum.EActionType.FORWARD, media);
                        } else if (position == 1) {//保存
                            saveImageToLocal(ivImage, media);
                        } else if (position == 2) {//识别二维码
                            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                                return;
                            }
                            scanningQrImage(media.getCompressPath(), ivImage);
                        } else if (position == 3) {//长按跳编辑界面，编辑完成后，返回新图片的本地路径到PictureExternalPreviewActivity
                            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                                ToastUtil.show(getActivity().getString(R.string.user_disable_message));
                                return;
                            }
                            ((PreviewMediaActivity) getActivity()).checkFile(msgId, fromWhere, CoreEnum.EActionType.EDIT, media);
                        }
                    }
                }
                popupSelectView.dismiss();

            }
        });
        popupSelectView.showAtLocation(ivImage, Gravity.BOTTOM, 0, 0);

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
        options.inJustDecodeBounds = false; // 获取新的大小
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
                    QRCodeManage.toZhifubao(getActivity(), result);
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
                QRCodeManage.toZhifubao(getActivity(), result);

            }
        } catch (Exception e) {
            ToastUtil.show(AppConfig.getContext(), "识别二维码失败");
            e.printStackTrace();
        }
    }

    /*
     * 保存图片到本地
     * */
    private void saveImageToLocal(ZoomImageView ivZoom, LocalMedia media) {
        if (activityIsFinish()) {
            return;
        }
        String format = PictureFileUtils.getFileFormatName(media.getCompressPath());
        if (isGif) {
            if (isHttp) {
//                String cacheFile = PictureFileUtils.getFilePathOfImage(media.getPath(), getActivity());
                String cacheFile =FileManager.getInstance().createImagePathByUrl(media.getPath());
                if (PictureFileUtils.hasImageCache(cacheFile, media.getSize())) {
                    saveImageFromCacheFile(cacheFile, format, ivZoom);
                } else {
                    downloadOriginImage(!TextUtils.isEmpty(media.getPath()) ? media.getPath() : media.getCompressPath(), true, true);
                }
            } else {
                if (PictureFileUtils.hasImageCache(media.getPath(), media.getSize())) {
                    saveImageFromCacheFile(media.getPath(), format, ivZoom);
                } else if (PictureFileUtils.hasImageCache(media.getCompressPath(), media.getSize())) {
                    saveImageFromCacheFile(media.getCompressPath(), format, ivZoom);
                } else {
                    downloadOriginImage(!TextUtils.isEmpty(media.getPath()) ? media.getPath() : media.getCompressPath(), true, true);
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
//                    String cacheFile = PictureFileUtils.getFilePathOfImage(media.getPath(), getActivity());
                    String cacheFile = FileManager.getInstance().createImagePathByUrl(media.getPath());
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
//                        String cacheFile = PictureFileUtils.getFilePathOfImage(media.getPath(), getActivity());
                        String cacheFile = FileManager.getInstance().createImagePathByUrl(media.getPath());
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


    //从本地缓存中存储到本地
    private void saveImageFromCacheFile(String filePath, String format, ZoomImageView ivZoom) {
        if (!TextUtils.isEmpty(filePath) && !activityIsFinish() && ivZoom != null) {
            boolean isSuccess = PicSaveUtils.saveOriginImage(getActivity(), filePath, format);
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

    //从控件中获取bitmap存储到本地
    private void saveImageFromDrawable(ZoomImageView ivZoom) {
        Drawable drawable = ivZoom.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            boolean isSuccess = PicSaveUtils.saveImgLoc(getActivity(), bitmap, "");
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

    private void getSize() {
        int realW = media.getWidth();
        int realH = media.getHeight();
        int screenWidth = ScreenUtil.getScreenWidth(getActivity());
        int screenHeight = ScreenUtil.getScreenHeight(getActivity());
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

    private boolean isBigSize() {
        boolean result = false;
        int realW = media.getWidth();
        int realH = media.getHeight();
        int screenWidth = ScreenUtil.getScreenWidth(getActivity());
        int screenHeight = ScreenUtil.getScreenHeight(getActivity());
        if (realH > 0) {
            double scale = (realW * 1.00) / realH;
            if (realW > screenWidth) {
                result = true;
            } else if (realH > screenHeight) {
                result = true;
            }
        }
        return result;
    }
}
