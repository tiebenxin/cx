package com.zhaoss.weixinrecorded.activity;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.yalantis.ucrop.UCrop;
import com.zhaoss.weixinrecorded.R;
import com.zhaoss.weixinrecorded.databinding.ActivityImgShowBinding;
import com.zhaoss.weixinrecorded.util.Utils;
import com.zhaoss.weixinrecorded.view.MosaicPaintView;
import com.zhaoss.weixinrecorded.view.TouchView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2019-11-15
 * @updateAuthor
 * @updateDate
 * @description 图片编辑
 * @copyright copyright(c)2019 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
@Route(path = "/weixinrecorded/ImageShowActivity")
public class ImageShowActivity extends BaseActivity implements View.OnClickListener {

    ActivityImgShowBinding binding;
    private String mPath;
    private int index;
    private int mWindowWidth;
    private int mWindowHeight;
    private int mDp100;
    private int[] mDrawableBg = new int[]{R.drawable.color2, R.drawable.color1, R.drawable.color3, R.drawable.color4, R.drawable.color5};
    private int[] mColors = new int[]{R.color.color2, R.color.color1, R.color.color3, R.color.color4, R.color.color5};
    private int mCurrentColorPosition = 0;
    private InputMethodManager mManager;
    private int from;//从相机拍摄过来的，需要删除缓存图片图片

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_img_show, null, false);
        setContentView(binding.getRoot());
        init(false);
        initEvent();
    }

    private void init(boolean isCut) {
        if (!isCut) {
            mPath = getIntent().getExtras().getString("imgpath");
            index = getIntent().getExtras().getInt("index");
            from = getIntent().getIntExtra("from", 0);
            initColors();
        }

        mWindowWidth = Utils.getWindowWidth(mContext);
        mWindowHeight = Utils.getWindowHeight(mContext);
        Glide.with(this).load(mPath).into(binding.imgShow);
        mManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        setHeight();
    }

    /**
     * 设置画笔、裁剪的画布的高度
     */
    private void setHeight() {
        binding.imgShow.postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        binding.imgShow.getHeight());
                binding.mpvView.setLayoutParams(layoutParams);
            }
        }, 500);// 延迟500毫秒用于获取到图片容器的高度
    }

    private void initEvent() {
        // 默认工具选中画笔
        binding.mpvView.setEtypeMode(MosaicPaintView.EtypeMode.TUYA);
        binding.mpvView.setPenColor(getResources().getColor(mColors[0]));
        binding.mpvView.setVisibility(View.VISIBLE);
        binding.llColor.setVisibility(View.VISIBLE);
        binding.rbPen.setChecked(true);

        binding.etTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.tvTag.setText(s.toString());
            }
        });
        binding.tvFinish.setOnClickListener(this);
        binding.tvClose.setOnClickListener(this);
        binding.tvFinishVideo.setOnClickListener(this);
        binding.rlClose.setOnClickListener(this);
        binding.ivShowDelete.setOnClickListener(this);
        binding.rbPen.setOnClickListener(this);
        binding.rlBack.setOnClickListener(this);
        binding.rbText.setOnClickListener(this);
        binding.rbCut.setOnClickListener(this);
        binding.rbMosaic.setOnClickListener(this);
        binding.mpvView.setLister(new IClickCallLister() {
            @Override
            public void onClickLister(boolean isShow) {
                if (isShow) {
                    binding.layoutContent.setVisibility(View.VISIBLE);
                    binding.layoutTitle.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutContent.setVisibility(View.GONE);
                    binding.layoutTitle.setVisibility(View.GONE);
                }
            }
        });
    }

    private void addTextToWindow() {
        mDp100 = (int) getResources().getDimension(R.dimen.dp100);
        TouchView touchView = new TouchView(getApplicationContext());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(binding.tvTag.getWidth(), binding.tvTag.getHeight());
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        touchView.setLayoutParams(layoutParams);
        Bitmap bitmap = Bitmap.createBitmap(binding.tvTag.getWidth(), binding.tvTag.getHeight(), Bitmap.Config.ARGB_8888);
        binding.tvTag.draw(new Canvas(bitmap));
        touchView.setBackground(new BitmapDrawable(bitmap));

        touchView.setLimitsX(0, mWindowWidth);
        touchView.setLimitsY(0, mWindowHeight - mDp100 / 2);
        touchView.setOnLimitsListener(new TouchView.OnLimitsListener() {
            @Override
            public void OnOutLimits(float x, float y) {
                binding.tvHintDelete.setTextColor(Color.RED);
            }

            @Override
            public void OnInnerLimits(float x, float y) {
                binding.tvHintDelete.setTextColor(Color.WHITE);
            }
        });
        touchView.setOnTouchListener(new TouchView.OnTouchListener() {
            @Override
            public void onDown(TouchView view, MotionEvent event) {
                binding.tvHintDelete.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMove(TouchView view, MotionEvent event) {

            }

            @Override
            public void onUp(TouchView view, MotionEvent event) {
                binding.tvHintDelete.setVisibility(View.GONE);
                if (view.isOutLimits()) {
                    binding.showRlBig.removeView(view);
                }
            }
        });

        binding.showRlBig.addView(touchView);

        binding.etTag.setText("");
        binding.tvTag.setText("");
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.BLACK);
        //  如果不设置canvas画布为白色，则生成透明
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

    private String saveImage(Bitmap bmp, int quality) {
        if (bmp == null) {
            return null;
        }
        File appDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (appDir == null) {
            return null;
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (from == 1) {
                deleteFileAndParent(mPath);
            }
        }
        return null;
    }

    private void initColors() {

        int dp20 = (int) getResources().getDimension(R.dimen.dp20);
        int dp25 = (int) getResources().getDimension(R.dimen.dp25);

        for (int x = 0; x < mDrawableBg.length; x++) {
            RelativeLayout relativeLayout = new RelativeLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            relativeLayout.setLayoutParams(layoutParams);

            View view = new View(this);
            view.setBackgroundDrawable(getResources().getDrawable(mDrawableBg[x]));
            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(dp20, dp20);
            layoutParams1.addRule(RelativeLayout.CENTER_IN_PARENT);
            view.setLayoutParams(layoutParams1);
            relativeLayout.addView(view);

            final View view2 = new View(this);
            view2.setBackgroundResource(R.mipmap.color_click);
            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(dp25, dp25);
            layoutParams2.addRule(RelativeLayout.CENTER_IN_PARENT);
            view2.setLayoutParams(layoutParams2);
            if (x != 0) {
                view2.setVisibility(View.GONE);
            }
            relativeLayout.addView(view2);

            final int position = x;
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCurrentColorPosition != position) {
                        view2.setVisibility(View.VISIBLE);
                        ViewGroup parent = (ViewGroup) v.getParent();
                        ViewGroup childView = (ViewGroup) parent.getChildAt(mCurrentColorPosition);
                        childView.getChildAt(1).setVisibility(View.GONE);
//                        tv_video.setNewPaintColor(getResources().getColor(mColors[position]));
                        binding.mpvView.setPenColor(getResources().getColor(mColors[position]));
                        mCurrentColorPosition = position;
                    }
                }
            });

            binding.llColor.addView(relativeLayout, x);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (binding.rlEditText.getVisibility() == View.VISIBLE) {
                    binding.rlEditText.setVisibility(View.GONE);
                } else {
                    finish();
                }
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_finish) {// 文字输入完成
            if (null != binding.etTag.getText() && binding.etTag.getText().toString().length() > 0) {
                addTextToWindow();
            }
            binding.rlEditText.setVisibility(View.GONE);
            hiddenPopSoft();
        } else if (v.getId() == R.id.tv_close) {// 关闭
            binding.rlEditText.setVisibility(View.GONE);
            hiddenPopSoft();
        } else if (v.getId() == R.id.tv_finish_video) {// 完成
            Intent intent = new Intent();
            Bitmap bitmap = loadBitmapFromView(binding.showRlBig);
            String savePath = saveImage(bitmap, 100);
            intent.putExtra("showResult", true);
            intent.putExtra("showPath", savePath);
            intent.putExtra("index", index);
            setResult(RESULT_OK, intent);
            finish();
        } else if (v.getId() == R.id.rl_close) {// 返回
            finish();
        } else if (v.getId() == R.id.iv_show_delete) {
            Intent intent = new Intent();
            intent.putExtra("showResult", false);
            intent.putExtra("showPath", "");
            setResult(RESULT_OK, intent);
            finish();
        } else if (v.getId() == R.id.rb_pen) {// 画笔
            binding.mpvView.setEnabled(true);
            binding.mpvView.setEtypeMode(MosaicPaintView.EtypeMode.TUYA);
            if (binding.llColor.getVisibility() == View.VISIBLE) {
                binding.llColor.setVisibility(View.INVISIBLE);
            } else {
                binding.llColor.setVisibility(View.VISIBLE);
                binding.mpvView.setPenColor(getResources().getColor(mColors[0]));
                binding.mpvView.setVisibility(View.VISIBLE);
            }
        } else if (v.getId() == R.id.rl_back) {// 清除上一次画笔
            if (null != binding.mpvView) {
                if (binding.mpvView.canUndo()) {
                    binding.mpvView.undo();
                }
            }
        } else if (v.getId() == R.id.rb_text) {// 輸入文字
            binding.mpvView.setEnabled(true);
            binding.llColor.setVisibility(View.INVISIBLE);
            binding.rlEditText.setVisibility(View.VISIBLE);
            showSoftInputFromWindow(binding.etTag);
            startAnim(binding.rlEditText.getY(), 0, null);
        } else if (v.getId() == R.id.rb_cut) {// 裁剪
            binding.rbCut.setChecked(false);
            startCrop(mPath);
        } else if (v.getId() == R.id.rb_mosaic) {// 马赛克
            binding.mpvView.setEnabled(true);
            binding.mpvView.setEtypeMode(MosaicPaintView.EtypeMode.GRID);
            binding.llColor.setVisibility(View.INVISIBLE);
            binding.mpvView.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(mPath)) {
                binding.imgShow.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(binding.imgShow.getDrawingCache());
                binding.imgShow.setDrawingCacheEnabled(false);

                binding.mpvView.setSrcPath(bitmap, mPath);
            }
        }
    }

    public void hiddenPopSoft() {
        mManager.hideSoftInputFromWindow(binding.etTag.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    private void startAnim(float start, float end, AnimatorListenerAdapter listenerAdapter) {

        ValueAnimator va = ValueAnimator.ofFloat(start, end).setDuration(200);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                binding.rlEditText.setY(value);
            }
        });
        if (listenerAdapter != null) va.addListener(listenerAdapter);
        va.start();
    }

    private void deleteFileAndParent(String path) {
        try {
            File file = new File(path);
            if (file != null && file.exists()) {
//                System.out.println("文件删除--" + file.getAbsolutePath());
                File fileParent = file.getParentFile();
                file.delete();
                if (fileParent != null && fileParent.exists() && fileParent.getAbsolutePath().toLowerCase().contains("changxin")) {
//                    System.out.println("文件删除--根目录--" + fileParent.getAbsolutePath());
                    fileParent.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转去裁剪
     *
     * @param originalPath
     */
    protected void startCrop(String originalPath) {
        if (TextUtils.isEmpty(originalPath)) {
            return;
        }
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(getResources().getColor(R.color.bar_grey));
        options.setStatusBarColor(getResources().getColor(R.color.bar_grey));
        options.setToolbarWidgetColor(getResources().getColor(R.color.white));
        // 如果希望暗显层中有一个圆，请将其设置为true
        options.setCircleDimmedLayer(false);
        // 是否显示裁剪框
        options.setShowCropFrame(true);
        // 是否显示裁剪框网格
        options.setShowCropGrid(true);
        // 设置裁剪的图片质量，取值0-100
        options.setCompressionQuality(90);
        // 是否隐藏底部容器，默认显示
        options.setHideBottomControls(true);
        // 是否能调整裁剪框
        options.setFreeStyleCropEnabled(true);
        boolean isHttp = PictureMimeType.isHttp(originalPath);
        String imgType = PictureMimeType.getLastImgType(originalPath);
        Uri uri = isHttp ? Uri.parse(originalPath) : Uri.fromFile(new File(originalPath));
        UCrop.of(uri, Uri.fromFile(new File(PictureFileUtils.getDiskCacheDir(this),
                System.currentTimeMillis() + imgType)))
                .withAspectRatio(1, 1)// 裁剪比例
                .withOptions(options)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            try {
                // 图片选择结果回调
                Uri resultUri = UCrop.getOutput(data);
                mPath = resultUri.getPath();
                if (TextUtils.isEmpty(mPath)) {
                    return;
                }
                init(true);
            } catch (Exception e) {

            }
        }
    }

    public interface IClickCallLister {
        void onClickLister(boolean isShow);
    }
}
