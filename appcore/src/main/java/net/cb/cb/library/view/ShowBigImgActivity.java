package net.cb.cb.library.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import net.cb.cb.library.R;

/***
 * @author jyj
 * @date 2017/6/9
 */
public class ShowBigImgActivity extends AppActivity {
    private com.facebook.drawee.view.SimpleDraweeView imgBig;
    private Button btnCommit;
    public final static String AGM_URI = "uri_pic";
    public final static String POSTION = "postion";
    private int postion = 100;

    //自动寻找控件
    private void findViews() {
        imgBig = findViewById(R.id.img_big);
        btnCommit = findViewById(R.id.btn_commit);
    }


    //自动生成的控件事件
    private void initEvent() {
        imgBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String uri = getIntent().getStringExtra(AGM_URI);
        postion = getIntent().getIntExtra(POSTION, 0);
        uri = uri == null ? "" : uri;
        imgBig.setImageURI(Uri.parse(uri));
        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra(POSTION, postion));
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_show_big_pic);
        findViews();
        initEvent();


    }
}
