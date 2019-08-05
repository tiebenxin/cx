package com.yanlong.im.user.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
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
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.luck.picture.lib.PictureBaseActivity;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ToastManage;
import com.luck.picture.lib.view.PopupSelectView;
import com.luck.picture.lib.view.bigImg.BlockImageLoader;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;
import com.luck.picture.lib.zxing.decoding.RGBLuminanceSource;
import com.luck.picture.lib.view.bigImg.LargeImageView;
import com.luck.picture.lib.view.bigImg.factory.FileBitmapDecoderFactory;
import com.yalantis.ucrop.util.FileUtils;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.utils.QRCodeManage;

import net.cb.cb.library.utils.ImgSizeUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：邮箱->893855882@qq.com
 * data：17/01/18
 */
public class PictureExternalPreviewActivity extends PictureBaseActivity implements View.OnClickListener {
    private static String TAG = "PictureExternalPreviewActivity";
    private ImageButton left_back;
    private TextView tv_title;
    private PreviewViewPager viewPager;
    private List<LocalMedia> images = new ArrayList<>();
    private int position = 0;
    private String directory_path;
    private SimpleFragmentAdapter adapter;
    private LayoutInflater inflater;
    private RxPermissions rxPermissions;
    private LoadDataThread loadDataThread;
    private String[] strings = {"识别二维码", "保存图片", "取消"};
    // private LargeImageView imgLarge;
    //private View txtBig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(com.luck.picture.lib.R.layout.picture_activity_external_preview);
        inflater = LayoutInflater.from(this);
        tv_title = (TextView) findViewById(com.luck.picture.lib.R.id.picture_title);
        left_back = (ImageButton) findViewById(com.luck.picture.lib.R.id.left_back);
        viewPager = (PreviewViewPager) findViewById(com.luck.picture.lib.R.id.preview_pager);
        position = getIntent().getIntExtra(PictureConfig.EXTRA_POSITION, 0);
        directory_path = "/DCIM/Camera/";//getIntent().getStringExtra(PictureConfig.DIRECTORY_PATH);
        images = (List<LocalMedia>) getIntent().getSerializableExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);
        left_back.setOnClickListener(this);
        initAndPermissions();

    }

    //权限申请和初始化
    private void initAndPermissions() {

        if (rxPermissions == null) {
            rxPermissions = new RxPermissions(PictureExternalPreviewActivity.this);
        }
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            initViewPageAdapterData();
                        } else {
                            ToastManage.s(mContext, getString(com.luck.picture.lib.R.string.picture_jurisdiction));
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    String indexPath;

    public void showBigImage(LargeImageView imgLarge, String path) {
        imgLarge.setAlpha(0);
        imgLarge.setVisibility(View.VISIBLE);

        showPleaseDialog();

        boolean isHttp = PictureMimeType.isHttp(path);
        if (isHttp) {
            loadDataThread = new LoadDataThread(path, 2, imgLarge);
            loadDataThread.start();
        } else {
            // 有可能本地图片
            try {


                Log.d("showBigImage", "showBigImage: " + path);
                imgLarge.setImage(new FileBitmapDecoderFactory(path));


            } catch (Exception e) {


                e.printStackTrace();
            } finally {
                dismissDialog();
            }
        }


    }

    private void initViewPageAdapterData() {
        tv_title.setText(position + 1 + "/" + images.size());
        adapter = new SimpleFragmentAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        indexPath = images.get(position).getPath();


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_title.setText(position + 1 + "/" + images.size());
                indexPath = images.get(position).getPath();

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //点击返回
        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        finish();
        overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
    }

    public class SimpleFragmentAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //当前图片路径

        private MsgDao msgDao = new MsgDao();

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View contentView = inflater.inflate(com.luck.picture.lib.R.layout.picture_image_preview, container, false);
            // 常规图控件
            final PhotoView imageView = contentView.findViewById(com.luck.picture.lib.R.id.preview_image);

            // 长图控件
            final SubsamplingScaleImageView longImg = contentView.findViewById(com.luck.picture.lib.R.id.longImg);
            final LargeImageView imgLarge = contentView.findViewById(com.luck.picture.lib.R.id.img_large);
            final TextView txtBig = contentView.findViewById(com.luck.picture.lib.R.id.txt_big);




            //1.先显示中图

            LocalMedia media = images.get(position);


            final String path = media.getCompressPath();
            boolean isGif = FileUtils.isGif(path);
            boolean isHttp = PictureMimeType.isHttp(path);
            // 可以长按保存并且是网络图片显示一个对话框
            if (isHttp) {
                showPleaseDialog();
            }


            final boolean eqLongImg = PictureMimeType.isLongImg(media);
            imageView.setVisibility(eqLongImg && !isGif ? View.GONE : View.VISIBLE);
            longImg.setVisibility(eqLongImg && !isGif ? View.VISIBLE : View.GONE);
            // 压缩过的gif就不是gif了
            if (isGif && !media.isCompressed()) {
                showGif(imageView, txtBig, path);
            } else {
                showImg(imageView, longImg, path, eqLongImg);
            }
            imgEvent(imageView, longImg, path);


            //2.是否是原图

            final String imgpath = images.get(position).getPath();
            Log.d("atg", "----:imgpath " + imgpath);
            imgLarge.setTag(imgpath);
            boolean isOriginal = false;//原图
            isOriginal = StringUtil.isNotNull(imgpath);


            if (isOriginal && (!isGif)) {//是原图,但是不是gif
                //3.是否已读原图
                boolean readStat = msgDao.ImgReadStatGet(imgpath);

                imgLargeEvent(txtBig,imgLarge, imgpath);

                if (readStat) {//原图已读,就显示
                    txtBig.setVisibility(View.GONE);
                    txtBig.callOnClick();
                } else {
                    txtBig.setVisibility(View.VISIBLE);
                    txtBig.setText("查看原图(" + ImgSizeUtil.formatFileSize(images.get(position).getSize()) + ")");
                }



            } else {
                txtBig.setVisibility(View.GONE);
            }


            (container).addView(contentView, 0);
            return contentView;
        }

        //图片事件
        private void imgEvent(PhotoView imageView, SubsamplingScaleImageView longImg, String path) {
            imageView.setOnViewTapListener(new OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    finish();
                    overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
                }
            });
            longImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
                }
            });
            imageView.setOnLongClickListener(onLongClick(path));
        }

        //大图事件
        private void imgLargeEvent(final View txtBig, final LargeImageView imgLarge, final String imgpath) {
            txtBig.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtBig.setVisibility(View.GONE);
                    showBigImage(imgLarge, imgpath);
                    //这边要改成已读
                    msgDao.ImgReadStatSet(imgpath, true);
                }
            });
            //查看大图------------------------
            imgLarge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // imgLarge.setVisibility(View.GONE);
                    onBackPressed();

                }
            });
            imgLarge.setOnLoadStateChangeListener(new BlockImageLoader.OnLoadStateChangeListener() {
                @Override
                public void onLoadStart(int loadType, Object param) {

                }

                @Override
                public void onLoadFinished(int loadType, Object param, boolean success, Throwable throwable) {


                }
            });
            imgLarge.setOnImageLoadListener(new BlockImageLoader.OnImageLoadListener() {
                @Override
                public void onBlockImageLoadFinished() {

                    imgLarge.setAlpha(1);
                    dismissDialog();
                    // ToastUtil.show(getApplicationContext(),"加载完成");
                }

                @Override
                public void onLoadImageSize(int imageWidth, int imageHeight) {

                }

                @Override
                public void onLoadFail(Exception e) {
                    ToastUtil.show(getApplicationContext(), "加载失败,请重试");
                    dismissDialog();
                }
            });
            imgLarge.setOnLongClickListener(onLongClick(imgpath));
        }

        //显示普通图片
        private void showImg(final PhotoView imageView, final SubsamplingScaleImageView longImg, String path, final boolean eqLongImg) {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Log.v("Glide", "显示普通图" + path);
            Glide.with(PictureExternalPreviewActivity.this)
                    .asBitmap()
                    .load(path)
                    .apply(options)  //480     800
                    .into(new SimpleTarget<Bitmap>(800, 800) {
                        /* .into(new SimpleTarget<Bitmap>(ScreenUtils.getScreenWidth(PictureExternalPreviewActivity.this),
                                 ScreenUtils.getScreenHeight(PictureExternalPreviewActivity.this)) {*/
                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            dismissDialog();
                        }

                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Log.v("Glide", "onResourceReady");
                            dismissDialog();
                            if (eqLongImg) {
                                displayLongPic(resource, longImg);
                            } else {
                                imageView.setImageBitmap(resource);
                            }
                        }
                    });
        }

        //显示gif图片
        private void showGif(final PhotoView imageView, TextView txtBig, String path) {
            Log.v("Glide", "显示gif图");
            txtBig.setVisibility(View.GONE);
            RequestOptions gifOptions = new RequestOptions()
                    .priority(Priority.LOW)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(PictureExternalPreviewActivity.this)
                    .asGif()
                    .apply(gifOptions)
                    .load(path)
                    .listener(new RequestListener<GifDrawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, final Object model
                                , Target<GifDrawable> target, boolean isFirstResource) {
                            dismissDialog();
                            imageView.post(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(getApplicationContext()).asBitmap().load(model).into(imageView);
                                }
                            });

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GifDrawable resource, Object model
                                , Target<GifDrawable> target, DataSource dataSource,
                                                       boolean isFirstResource) {
                            dismissDialog();
                            return false;
                        }
                    })
                    .into(imageView);
        }


    }

    private View.OnLongClickListener onLongClick(final String path) {

        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (rxPermissions == null) {
                    rxPermissions = new RxPermissions(PictureExternalPreviewActivity.this);
                }
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (aBoolean) {
                                    showDownLoadDialog(path);
                                } else {
                                    ToastManage.s(mContext, getString(com.luck.picture.lib.R.string.picture_jurisdiction));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
                return true;
            }
        };
    }


    /**
     * 加载长图
     *
     * @param bmp
     * @param longImg
     */
    private void displayLongPic(Bitmap bmp, SubsamplingScaleImageView longImg) {
        longImg.setQuickScaleEnabled(true);
        longImg.setZoomEnabled(true);
        longImg.setPanEnabled(true);
        longImg.setDoubleTapZoomDuration(100);
        longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        longImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        longImg.setImage(ImageSource.cachedBitmap(bmp), new ImageViewState(0, new PointF(0, 0), 0));
    }

    /**
     * 下载图片提示
     */
    private void showDownLoadDialog(final String path) {
        final PopupSelectView popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                if (postsion == 0) {
                    scanningQrImage(path);
                } else if (postsion == 1) {
                    saveImage(path);
                }
                popupSelectView.dismiss();

            }
        });
        popupSelectView.showAtLocation(tv_title, Gravity.BOTTOM, 0, 0);

    }


    private Result scanningQrImage(String path) {
        boolean isHttp = PictureMimeType.isHttp(path);
        if (isHttp) {
            loadDataThread = new LoadDataThread(path, 1, null);
            loadDataThread.start();
        } else {
            Log.d(TAG, "scanningQrImage: path" + path);
            // 有可能本地图片
            try {

                // String dirPath = PictureFileUtils.createDir(PictureExternalPreviewActivity.this,System.currentTimeMillis() + ".png", directory_path);
                //PictureFileUtils.copyFile(path, dirPath);
                if (path.toLowerCase().startsWith("file://")) {
                    path = path.replace("file://", "");
                }

                // Log.d(TAG, "scanningQrImage: dirPath"+dirPath);
                Result result = scanningImage(path);
                QRCodeManage.toZhifubao(this, result);
            } catch (Exception e) {
                ToastUtil.show(mContext, "识别二维码失败");
                dismissDialog();
                e.printStackTrace();
            }
        }


        return null;
    }


    Bitmap scanBitmap;

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
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
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


    private void saveImage(String path) {
        Log.d("TAG", "------------showLoadingImage$:saveImage " + path);
        showPleaseDialog();
        boolean isHttp = PictureMimeType.isHttp(path);
        if (isHttp) {
            loadDataThread = new LoadDataThread(path, 0, null);
            loadDataThread.start();
        } else {
            if (path.toLowerCase().startsWith("file://")) {
                path = path.replace("file://", "");
            }
            // 有可能本地图片
            try {
                String fileName = getFileExt(path);
                String dirPath = PictureFileUtils.createDir(PictureExternalPreviewActivity.this,
                        fileName, directory_path);
                PictureFileUtils.copyFile(path, dirPath);
                //刷新相册的广播
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(new File(dirPath));
                intent.setData(uri);
                getApplicationContext().sendBroadcast(intent);

                ToastManage.s(mContext, getString(com.luck.picture.lib.R.string.picture_save_success) + "\n" + dirPath);
                dismissDialog();
            } catch (IOException e) {
                ToastManage.s(mContext, getString(com.luck.picture.lib.R.string.picture_save_error) + "\n" + e.getMessage());
                dismissDialog();
                e.printStackTrace();
            }
        }
        dialog.dismiss();
    }


    // 进度条线程
    public class LoadDataThread extends Thread {
        private String path;
        private Object obj;
        private int type;

        public LoadDataThread(String path, int type, Object obj) {
            super();
            Log.d("TAG", "------------LoadDataThread: " + obj);
            this.path = path;
            this.type = type;
            this.obj = obj;
        }

        @Override
        public void run() {
            try {
                showLoadingImage(path, type, obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showLoadingImage(String urlPath, int type) {
        showLoadingImage(urlPath, type, null);
    }

    // 下载图片保存至手机
    public void showLoadingImage(String urlPath, int type, Object obj) {
        try {
            Log.d(TAG, "showLoadingImage: " + urlPath);
            URL u = new URL(urlPath);
            //网路图片本地化
            String fileName = getFileExt(urlPath);
            String path = PictureFileUtils.createDir(PictureExternalPreviewActivity.this,
                    fileName, null);
            Log.d(TAG, "showLoadingImage path: " + path);
            saveFileLocl(u, path);

            if (type == 0) {
                Message message = handler.obtainMessage();
                message.what = 200;
                message.obj = path;
                handler.sendMessage(message);
            } else if (type == 2) {//显示大图
                Message message = handler.obtainMessage();
                message.what = 400;
                message.obj = obj;
                ((View) obj).setTag(path);

                handler.sendMessage(message);
            } else if (type == 1) {
                Message message = handler.obtainMessage();
                message.what = 300;
                message.obj = path;
                handler.sendMessage(message);
            }


        } catch (IOException e) {
            // ToastManage.s(mContext, getString(com.luck.picture.lib.R.string.picture_save_error) + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /***
     * 获取文件后缀
     * @param urlPath
     * @return
     */
    private String getFileExt(String urlPath) {
        String fName = urlPath.trim();
        //http://e7-test.oss-cn-beijing.aliyuncs.com/Android/20190802/fe85b909-0bea-4155-a92a-d78052e8638c.png/below-200k
        int index = fName.lastIndexOf("/");
        if (fName.lastIndexOf(".") > index) {
            return fName.substring(index + 1);
        } else {
            String name = fName.substring(fName.lastIndexOf("/", index - 1) + 1);
            name = name.replace("/", "_");
            return name;
        }
    }

    /***
     * 文件保存本地
     * @param u
     * @param path
     * @throws IOException
     */
    private void saveFileLocl(URL u, String path) throws IOException {
        if (!new File(path).exists()) {
            byte[] buffer = new byte[1024 * 8];
            int read;
            int ava = 0;
            long start = System.currentTimeMillis();
            BufferedInputStream bin;
            bin = new BufferedInputStream(u.openStream());
            BufferedOutputStream bout = new BufferedOutputStream(
                    new FileOutputStream(path));
            while ((read = bin.read(buffer)) > -1) {
                bout.write(buffer, 0, read);
                ava += read;
                long speed = ava / (System.currentTimeMillis() - start);
            }
            bout.flush();
            bout.close();
            Log.d(TAG, "showLoadingImage: 不存在,创建");
        } else {
            Log.d(TAG, "showLoadingImage: 存在");
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    String path = (String) msg.obj;
                    // ToastManage.s(mContext, getString(com.luck.picture.lib.R.string.picture_save_success) + "\n" + path);
                    //dismissDialog();
                    saveImage(path);
                    break;
                case 400:

                    LargeImageView imgLarge = (LargeImageView) msg.obj;
                    Log.d("atg", "----:imgLarge " + imgLarge);
                    if (imgLarge != null) {
                        Log.d("atg", "----:imgLarge getTag" + imgLarge.getTag());
                        String biPath = (String) imgLarge.getTag();
                        showBigImage(imgLarge, biPath);
                    }

                    break;
                case 300:
                    String qrPath = (String) msg.obj;
                    Result result = scanningImage(qrPath);
                    QRCodeManage.toZhifubao(PictureExternalPreviewActivity.this, result);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, com.luck.picture.lib.R.anim.a3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadDataThread != null) {
            handler.removeCallbacks(loadDataThread);
            loadDataThread = null;
        }
    }
}
