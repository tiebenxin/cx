package net.cb.cb.library.base;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import net.cb.cb.library.R;


/**
 * Created by zgd on 2017/6/16.
 */

public class BaseDialogTwo extends Dialog {
    private Window window;
    protected Context mContext;

    public BaseDialogTwo(int layoutId, @NonNull Context context) {
        super(context, R.style.dialog_default);
        this.mContext = context;
        setContentView(layoutId);
        setCanceledOnTouchOutside(true);     //点击弹窗外，弹窗隐藏
        setWindowAttribute();
        initView();
    }

    protected void initView() {
    }

    private void setWindowAttribute() {
        window = getWindow();
//        window.setWindowAnimations(R.style.dialog_animation);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.getDecorView().setPadding(0,0,0,0);
        wl.gravity = getGravity();
        window.setAttributes(wl);
    }

//    public void setWindowFullAttribute() {
//        window = getWindow();
////        window.setWindowAnimations(R.style.dialog_animation);
//        WindowManager.LayoutParams wl = window.getAttributes();
////        wl.height = UIUtil.getScreenHeight(mContext);
////        wl.width = UIUtil.getScreenWidth(mContext);
//        window.getDecorView().setPadding(0,0,0,0);
//        wl.gravity = Gravity.CENTER;
//        window.setAttributes(wl);
//    }

    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    public void setCenter(){
        window = getWindow();
//        window.setWindowAnimations(R.style.dialog_animation);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        window.getDecorView().setPadding(100,0,100,0);
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
    }
}
