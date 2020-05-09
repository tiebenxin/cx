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
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.zhaoss.weixinrecorded.R;
import com.zhaoss.weixinrecorded.databinding.ActivityImgShowBinding;
import com.zhaoss.weixinrecorded.util.DimenUtils;
import com.zhaoss.weixinrecorded.util.Utils;
import com.zhaoss.weixinrecorded.util.ViewUtils;
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
        init();
        initEvent();
    }

    private void init() {
        mPath = getIntent().getExtras().getString("imgpath");
        index = getIntent().getExtras().getInt("index");
        from = getIntent().getIntExtra("from", 0);
        mWindowWidth = Utils.getWindowWidth(mContext);
        mWindowHeight = Utils.getWindowHeight(mContext);
        Glide.with(this).load(mPath).into(binding.imgShow);
//        binding.imgShow.setImageURI(Uri.parse(mPath));
        mManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        initColors();
    }

    private void initEvent() {
        //默认工具选中画笔
        binding.mpvView.setEtypeMode(MosaicPaintView.EtypeMode.TUYA);
        binding.imgShowCut.setVisibility(View.GONE);
        binding.llColor.setVisibility(View.VISIBLE);
        binding.mpvView.setPenColor(getResources().getColor(mColors[0]));
        binding.mpvView.setVisibility(View.VISIBLE);
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
        binding.textureViewCut.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                binding.imgShowCut.setMargin(binding.textureViewCut.getLeft(), binding.textureViewCut.getTop(),
                        binding.textureViewCut.getRight() - binding.textureViewCut.getWidth(),
                        binding.textureViewCut.getBottom() - binding.textureViewCut.getHeight());
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
//                changeMode(true);
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
        if (binding.imgShowCut.getVisibility() == View.VISIBLE) {
            int w = v.getWidth();
            int h = v.getHeight();
            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            float[] cutArr = binding.imgShowCut.getCutArr();
//            Bitmap cutBitmap=Bitmap.createBitmap(bmp,(int)cutArr[0],(int)cutArr[1],activity_img_show_cut.getRectWidth(),activity_img_show_cut.getRectHeight());
//            Bitmap cutBitmap=Bitmap.createBitmap(bmp,(int)cutArr[0],activity_img_show_cut.getRectHeight()-(int)cutArr[1],(int)cutArr[2]-(int)cutArr[0],activity_img_show_cut.getRectHeight()-((int)cutArr[3]-(int)cutArr[1]));
            Log.e("TAG", cutArr[0] + "-----" + cutArr[1] + "-----" + cutArr[2] + "-----" + cutArr[3] + "-----");
            Canvas c = new Canvas(bmp);
            c.drawColor(Color.WHITE);
//            /** 如果不设置canvas画布为白色，则生成透明 */
            v.layout(0, 0, w, h);
            v.draw(c);
            int px = (int) DimenUtils.dp2px(60);
            Bitmap cutBitmap = null;

            if ((30 + cutArr[2]) >= bmp.getWidth() || (px + cutArr[3]) > bmp.getHeight()) {
//                cutBitmap=Bitmap.createBitmap(bmp,(int)cutArr[0]+30,(int)cutArr[1]+px,(int)cutArr[2]-((int)cutArr[0]+30),(int)cutArr[3]-(int)cutArr[1]);
                if ((30 + cutArr[2]) >= bmp.getWidth() && (px + cutArr[3]) > bmp.getHeight()) {
                    cutBitmap = Bitmap.createBitmap(bmp, (int) cutArr[0] + 30, (int) cutArr[1] + px, (int) cutArr[2] - ((int) cutArr[0] + 30), (int) cutArr[3] - ((int) cutArr[1] + px));
                } else {
                    if ((30 + cutArr[2]) >= bmp.getWidth()) {
                        cutBitmap = Bitmap.createBitmap(bmp, (int) cutArr[0] + 30, (int) cutArr[1] + px, (int) cutArr[2] - ((int) cutArr[0] + 30), (int) cutArr[3] - (int) cutArr[1]);
                    } else {
                        cutBitmap = Bitmap.createBitmap(bmp, (int) cutArr[0] + 30, (int) cutArr[1] + px, (int) cutArr[2] - (int) cutArr[0], (int) cutArr[3] - ((int) cutArr[1] + px));
                    }
                }
            } else {
                cutBitmap = Bitmap.createBitmap(bmp, (int) cutArr[0] + 30, (int) cutArr[1] + px, (int) cutArr[2] - (int) cutArr[0], (int) cutArr[3] - (int) cutArr[1]);
            }
//            if ((px+cutArr[3])>bmp.getHeight()){
//                cutBitmap=Bitmap.createBitmap(bmp,(int)cutArr[0]+30,(int)cutArr[1]+px,(int)cutArr[2]-(int)cutArr[0],activity_img_show_cut.getRectHeight()-px);
//            }else{
//                cutBitmap=Bitmap.createBitmap(bmp,(int)cutArr[0]+30,(int)cutArr[1]+px,(int)cutArr[2]-(int)cutArr[0],activity_img_show_cut.getRectHeight()-px);
//            }

//            if ((30+(int)cutArr[2])>=bmp.getWidth()){
//                cutBitmap=Bitmap.createBitmap(bmp,(int)cutArr[0]+30,(int)cutArr[1]+px,bmp.getWidth()-((int)cutArr[0]+30),(int)cutArr[3]-(int)cutArr[1]);
//            }else{
//                cutBitmap=Bitmap.createBitmap(bmp,(int)cutArr[0]+30,(int)cutArr[1]+px,(int)cutArr[2]-(int)cutArr[0],(int)cutArr[3]-(int)cutArr[1]);
//            }
//            if ((px+(int)cutArr[3])>bmp.getHeight()){
//                cutBitmap=Bitmap.createBitmap(bmp,(int)cutArr[0]+30,(int)cutArr[1]+px,(int)cutArr[2]-(int)cutArr[0],bmp.getHeight()-((int)cutArr[1]+px));
//            }else{
//                cutBitmap=Bitmap.createBitmap(bmp,(int)cutArr[0]+30,(int)cutArr[1]+px,(int)cutArr[2]-(int)cutArr[0],(int)cutArr[3]-(int)cutArr[1]);
//            }
            return cutBitmap;

        } else {
            int w = v.getWidth();
            int h = v.getHeight();
            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bmp);

            c.drawColor(Color.WHITE);
//            /** 如果不设置canvas画布为白色，则生成透明 */
//
            v.layout(0, 0, w, h);
            v.draw(c);
            return bmp;
        }
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
            binding.mpvView.setEtypeMode(MosaicPaintView.EtypeMode.TUYA);
            binding.imgShowCut.setVisibility(View.GONE);
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
            binding.llColor.setVisibility(View.INVISIBLE);
            binding.imgShowCut.setVisibility(View.GONE);
            binding.rlEditText.setVisibility(View.VISIBLE);
            showSoftInputFromWindow(binding.etTag);
            startAnim(binding.rlEditText.getY(), 0, null);

        } else if (v.getId() == R.id.rb_cut) {// 裁剪
            binding.llColor.setVisibility(View.INVISIBLE);
            if (binding.imgShowCut.getVisibility() == View.VISIBLE) {
                binding.imgShowCut.setVisibility(View.GONE);
            } else {
                binding.imgShowCut.setVisibility(View.VISIBLE);
            }
        } else if (v.getId() == R.id.rb_mosaic) {// 马赛克
            binding.mpvView.setEtypeMode(MosaicPaintView.EtypeMode.GRID);
            binding.llColor.setVisibility(View.INVISIBLE);
            binding.imgShowCut.setVisibility(View.GONE);
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

}
