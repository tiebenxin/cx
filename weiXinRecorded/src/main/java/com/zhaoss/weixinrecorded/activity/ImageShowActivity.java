package com.zhaoss.weixinrecorded.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zhaoss.weixinrecorded.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.security.AccessController.getContext;

public class ImageShowActivity  extends BaseActivity {
    private ImageView activity_img_show_img,iv_show_next,iv_show_delete;
    private String path;
    private RelativeLayout activity_show_rl_big,rl_pen,rl_back;
    private LinearLayout ll_color;
    private com.zhaoss.weixinrecorded.view.MyPaintView mypaintview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_show);
        path=(String) getIntent().getExtras().get("imgpath");
        initView();
        initEvent();
        activity_img_show_img.setImageURI(Uri.parse(path));
    }

    private void initEvent() {
        initColors();
        findViewById(R.id.iv_show_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bitmap bitmap= loadBitmapFromView(activity_show_rl_big);
                String savePath= saveImage(bitmap,100);
                intent.putExtra("showResult", true);
                intent.putExtra("showPath", savePath);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        findViewById(R.id.iv_show_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("showResult", false);
                intent.putExtra("showPath", "");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        rl_pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_color.getVisibility()==View.VISIBLE){
                    ll_color.setVisibility(View.INVISIBLE);
                }else{
                    ll_color.setVisibility(View.VISIBLE);
                }

            }
        });
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null!=mypaintview){
                    if (mypaintview.canUndo()){
                        mypaintview.undo();
                    }
                }
            }
        });
    }

    private void initView() {
        activity_img_show_img=findViewById(R.id.activity_img_show_img);
        iv_show_next=findViewById(R.id.iv_show_next);
        iv_show_delete=findViewById(R.id.iv_show_delete);
        activity_show_rl_big=findViewById(R.id.activity_show_rl_big);
        rl_pen=findViewById(R.id.rl_pen);
        ll_color=findViewById(R.id.ll_color);
        mypaintview=findViewById(R.id.activity_show_mypaintview);
        rl_back = findViewById(R.id.rl_back);
    }
    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

    private static String saveImage(Bitmap bmp, int quality) {
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
        }
        return null;
    }
    private int[] drawableBg = new int[]{R.drawable.color1, R.drawable.color2, R.drawable.color3, R.drawable.color4, R.drawable.color5};
    private int[] colors = new int[]{R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5};
    private int currentColorPosition;
    private void initColors() {

        int dp20 = (int) getResources().getDimension(R.dimen.dp20);
        int dp25 = (int) getResources().getDimension(R.dimen.dp25);

        for (int x = 0; x < drawableBg.length; x++) {
            RelativeLayout relativeLayout = new RelativeLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            relativeLayout.setLayoutParams(layoutParams);

            View view = new View(this);
            view.setBackgroundDrawable(getResources().getDrawable(drawableBg[x]));
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
                    if (currentColorPosition != position) {
                        view2.setVisibility(View.VISIBLE);
                        ViewGroup parent = (ViewGroup) v.getParent();
                        ViewGroup childView = (ViewGroup) parent.getChildAt(currentColorPosition);
                        childView.getChildAt(1).setVisibility(View.GONE);
//                        tv_video.setNewPaintColor(getResources().getColor(colors[position]));
                        mypaintview.setPenColor(getResources().getColor(colors[position]));
                        currentColorPosition = position;
                    }
                    mypaintview.setVisibility(View.VISIBLE);
                }
            });

            ll_color.addView(relativeLayout, x);
        }
    }
}
