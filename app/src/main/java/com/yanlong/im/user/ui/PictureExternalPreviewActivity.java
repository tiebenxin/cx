package com.yanlong.im.user.ui;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
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
import com.luck.picture.lib.tools.ScreenUtils;
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
import com.yanlong.im.utils.QRCodeManage;

import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.TestView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
    private ImageButton left_back;
    private TextView tv_title;
    private PreviewViewPager viewPager;
    private List<LocalMedia> images = new ArrayList<>();
    private int position = 0;
    private String directory_path;
    private SimpleFragmentAdapter adapter;
    private LayoutInflater inflater;
    private RxPermissions rxPermissions;
    private loadDataThread loadDataThread;
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
        directory_path ="/DCIM/Camera/";//getIntent().getStringExtra(PictureConfig.DIRECTORY_PATH);
        images = (List<LocalMedia>) getIntent().getSerializableExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);
        left_back.setOnClickListener(this);

       // imgLarge = findViewById(com.luck.picture.lib.R.id.img_large);
     /*   txtBig = findViewById(com.luck.picture.lib.R.id.txt_big);
        txtBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBigImage(indexPath);
            }
        });*/
       /* imgLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgLarge.setVisibility(View.GONE);

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
                ToastUtil.show(getApplicationContext(),"加载失败,请重试");
                dismissDialog();
            }
        });*/
        initViewPageAdapterData();
    }

    String indexPath;

    public void showBigImage(LargeImageView imgLarge,String path) {
       imgLarge.setAlpha(0);
        imgLarge.setVisibility(View.VISIBLE);

        showPleaseDialog();

        boolean isHttp = PictureMimeType.isHttp(path);
        if (isHttp) {
            loadDataThread = new loadDataThread(path, 2);
            loadDataThread.start();
        } else {
            // 有可能本地图片
            try {
               /*String dirPath = PictureFileUtils.createDir(PictureExternalPreviewActivity.this,
                        System.currentTimeMillis() + ".png", directory_path);
                PictureFileUtils.copyFile(path, dirPath);*/

                Log.d("showBigImage", "showBigImage: "+path);
                imgLarge.setImage(new FileBitmapDecoderFactory(path));


            } catch (Exception e) {

                dismissDialog();
                e.printStackTrace();
            }
        }





    }

    private void initViewPageAdapterData() {
        tv_title.setText(position + 1 + "/" + images.size());
        adapter = new SimpleFragmentAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        indexPath = images.get(position).getPath();
       /* if(StringUtil.isNotNull(indexPath)){
            txtBig.setVisibility(View.VISIBLE);
        }else{
            txtBig.setVisibility(View.GONE);
        }*/

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_title.setText(position + 1 + "/" + images.size());
                indexPath = images.get(position).getPath();
               /* if(StringUtil.isNotNull(indexPath)){
                    txtBig.setVisibility(View.VISIBLE);
                }else{
                    txtBig.setVisibility(View.GONE);
                }*/
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

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View contentView = inflater.inflate(com.luck.picture.lib.R.layout.picture_image_preview, container, false);
            // 常规图控件
            final PhotoView imageView = contentView.findViewById(com.luck.picture.lib.R.id.preview_image);

            // 长图控件
            final SubsamplingScaleImageView longImg = contentView.findViewById(com.luck.picture.lib.R.id.longImg);
            final LargeImageView imgLarge = contentView.findViewById(com.luck.picture.lib.R.id.img_large);
            final TextView txtBig= contentView.findViewById(com.luck.picture.lib.R.id.txt_big);
            final String imgpath=images.get(position).getPath();
            imgLarge.setTag(imgpath);
            txtBig.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtBig.setVisibility(View.GONE);
                    showBigImage(imgLarge,imgpath);
                    //这边要改成已读
                }
            });


                if(StringUtil.isNotNull(imgpath )){
                    txtBig.setVisibility(View.VISIBLE);
                }else{
                    txtBig.setVisibility(View.GONE);
                }

            LocalMedia media = images.get(position);
            //7.18
           /* if(media.getPath().toLowerCase().contains(".gif")){
                media.setPictureType("image/gif");
            }
*/

            if (media != null) {
                final String pictureType = media.getPictureType();
                final String path;
               /* if (media.isCut() && !media.isCompressed()) {
                    // 裁剪过
                    path = media.getCutPath();
                } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                    // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                    path = media.getCompressPath();
                } else {
                    path = media.getPath();
                }*/
               //7.22 优先展现压缩
                path = media.getCompressPath();
                boolean isHttp = PictureMimeType.isHttp(path);
                // 可以长按保存并且是网络图片显示一个对话框
                if (isHttp) {
                    showPleaseDialog();
                }


                boolean isGif = FileUtils.isGif(media.getPath());//PictureMimeType.isGif(pictureType);
                final boolean eqLongImg = PictureMimeType.isLongImg(media);
                imageView.setVisibility(eqLongImg && !isGif ? View.GONE : View.VISIBLE);
                longImg.setVisibility(eqLongImg && !isGif ? View.VISIBLE : View.GONE);
                // 压缩过的gif就不是gif了
                if (isGif && !media.isCompressed()) {
                    Log.v("Glide", "显示gif图");
                    RequestOptions gifOptions = new RequestOptions()
                            .priority(Priority.NORMAL)
                            .diskCacheStrategy(DiskCacheStrategy.NONE);
                    Glide.with(PictureExternalPreviewActivity.this)
                            .asGif()
                            .apply(gifOptions)
                            .load(path)
                            .listener(new RequestListener<GifDrawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model
                                        , Target<GifDrawable> target, boolean isFirstResource) {
                                    dismissDialog();
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
                } else {
                    RequestOptions options = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                    Log.v("Glide", "显示普通图" + path);
                    Glide.with(PictureExternalPreviewActivity.this)
                            .asBitmap()
                            .load(path)
                            .apply(options)  //480     800
                            .into(new SimpleTarget<Bitmap>(800,800) {
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
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
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
                });
            }

            //查看大图------------------------
            imgLarge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // imgLarge.setVisibility(View.GONE);
                    finish();

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
                    ToastUtil.show(getApplicationContext(),"加载失败,请重试");
                    dismissDialog();
                }
            });

            (container).addView(contentView, 0);
            return contentView;
        }
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
            loadDataThread = new loadDataThread(path, 1);
            loadDataThread.start();
        } else {
            // 有可能本地图片
            try {
                String dirPath = PictureFileUtils.createDir(PictureExternalPreviewActivity.this,
                        System.currentTimeMillis() + ".png", directory_path);
                //PictureFileUtils.copyFile(path, dirPath);
                Result result = scanningImage(dirPath);
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
        showPleaseDialog();
        boolean isHttp = PictureMimeType.isHttp(path);
        if (isHttp) {
            loadDataThread = new loadDataThread(path, 0);
            loadDataThread.start();
        } else {
            // 有可能本地图片
            try {
                String dirPath = PictureFileUtils.createDir(PictureExternalPreviewActivity.this,
                        System.currentTimeMillis() + ".png", directory_path);
                PictureFileUtils.copyFile(path, dirPath);
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
    public class loadDataThread extends Thread {
        private String path;
        private int type;

        public loadDataThread(String path, int type) {
            super();
            this.path = path;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                showLoadingImage(path, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showLoadingImage(String urlPath, int type) {
        showLoadingImage( urlPath,  type,null);
    }
    // 下载图片保存至手机
    public void showLoadingImage(String urlPath, int type,Object obj) {
        try {
            URL u = new URL(urlPath);
            String path = PictureFileUtils.createDir(PictureExternalPreviewActivity.this,
                    System.currentTimeMillis() + ".png", directory_path);
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
            if (type == 0) {
                Message message = handler.obtainMessage();
                message.what = 200;
                message.obj = path;
                handler.sendMessage(message);
            } else if(type==2){//显示大图
                Message message = handler.obtainMessage();
                message.what = 400;
                message.obj = obj;
                handler.sendMessage(message);
            }else if(type==1) {
                Message message = handler.obtainMessage();
                message.what = 300;
                message.obj = path;
                handler.sendMessage(message);
            }


        } catch (IOException e) {
            ToastManage.s(mContext, getString(com.luck.picture.lib.R.string.picture_save_error) + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    String path = (String) msg.obj;
                    ToastManage.s(mContext, getString(com.luck.picture.lib.R.string.picture_save_success) + "\n" + path);
                    dismissDialog();
                    break;
                case 400:

                    LargeImageView imgLarge=(LargeImageView)msg.obj;
                    String biPath = (String) imgLarge.getTag();
                    showBigImage(imgLarge,biPath);
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
