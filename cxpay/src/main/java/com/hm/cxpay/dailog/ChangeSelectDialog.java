package com.hm.cxpay.dailog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hm.cxpay.R;


/**
 * @类名：零钱->通用选择对话框
 * @Date：2020/3/25
 * @by zjy
 * @备注：
 */

public class ChangeSelectDialog extends Dialog {

    public Context context;
    private TextView tvContent;//标题
    private TextView tvLeft;//左侧按钮
    private TextView tvRight;//右侧按钮
    private View.OnClickListener leftListener;//左侧监听
    private View.OnClickListener rightListener;//右侧监听

    private String title;//标题文字
    private String leftText;//左侧按钮文字
    private String rightText;//右侧按钮文字

    //重写三个构造方法，传入需要的上下文参数
    public ChangeSelectDialog(@NonNull Builder builder) {
        super(builder.context);
        context = builder.context;
        title = builder.title;
        leftText = builder.leftText;
        rightText = builder.rightText;
        leftListener = builder.leftListener;
        rightListener = builder.rightListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载布局 布局样式相同，直接复用
        View view = View.inflate(context, R.layout.dialog_set_payword,null);
        setContentView(view);
        setCanceledOnTouchOutside(false);//默认点击对话框外不消失
        //窗口参数设置
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//解决圆角shape背景无效问题
        window.setGravity(Gravity.CENTER);
        //布局相关配置
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager manager = window.getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        //设置宽高，高度自适应，宽度屏幕0.8
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.width = (int) (metrics.widthPixels*0.8);
        window.setAttributes(lp);
        //初始化view
        tvContent = view.findViewById(R.id.tv_content);
        tvLeft = view.findViewById(R.id.tv_exit);
        tvRight = view.findViewById(R.id.tv_set);
        //设置标题文字
        tvContent.setText(title);
        //设置左侧文字
        tvLeft.setText(leftText);
        //设置右侧文字
        tvRight.setText(rightText);
        //左侧点击
        tvLeft.setOnClickListener(leftListener);
        //右侧点击
        tvRight.setOnClickListener(rightListener);
    }

    //建造者模式
    public static final class Builder{
        private Context context;
        private String title;
        private String leftText;
        private String rightText;
        private View.OnClickListener leftListener;
        private View.OnClickListener rightListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setLeftText(String leftText) {
            this.leftText = leftText;
            return this;
        }

        public Builder setRightText(String rightText) {
            this.rightText = rightText;
            return this;
        }

        public Builder setLeftOnClickListener(View.OnClickListener listener){
            leftListener = listener;
            return this;
        }

        public Builder setRightOnClickListener(View.OnClickListener listener){
            rightListener = listener;
            return this;
        }

        public ChangeSelectDialog build(){
            return new ChangeSelectDialog(this);
        }
    }

}
