package net.cb.cb.library.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import net.cb.cb.library.R;
import net.cb.cb.library.base.BaseDialog;

/**
 * @author Liszt
 * @date 2019/12/19
 * Description 加载dialog
 */
public class DialogLoadingProgress extends BaseDialog {

    private ImageView ivProgress;
    private TextView tvContent;

    public DialogLoadingProgress(Context context) {
        this(context, R.style.MyDialogTheme);
    }

    public DialogLoadingProgress(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_loading_progress);
        tvContent = findViewById(R.id.tv_content);
        ivProgress = findViewById(R.id.iv_progress);
        RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(getContext()).load(R.drawable.offline_loading).apply(options).into(ivProgress);
    }

    @Override
    public void processClick(View view) {

    }

    public void setContent(String s) {
        if (tvContent != null && !TextUtils.isEmpty(s)) {
            tvContent.setText(s);
        }
    }
}
