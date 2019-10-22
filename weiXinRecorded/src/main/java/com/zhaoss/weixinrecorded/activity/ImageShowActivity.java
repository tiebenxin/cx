package com.zhaoss.weixinrecorded.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.zhaoss.weixinrecorded.R;

public class ImageShowActivity  extends BaseActivity {
    private ImageView activity_img_show_img,iv_show_next,iv_show_delete;
    private String path;
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
        findViewById(R.id.iv_show_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("showResult", true);
                intent.putExtra("showPath", path);
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
    }

    private void initView() {
        activity_img_show_img=findViewById(R.id.activity_img_show_img);
        iv_show_next=findViewById(R.id.iv_show_next);
        iv_show_delete=findViewById(R.id.iv_show_delete);
    }

}
