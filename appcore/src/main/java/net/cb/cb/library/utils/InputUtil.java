package net.cb.cb.library.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class InputUtil {
    public static void hideKeyboard(View view) {
        LogUtil.getLog().e("hideKeyboard===");
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static void showKeyboard(View view) {
        LogUtil.getLog().e("showKeyboard===");
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(view, 0);

    }
    public static void hideKeyboard(Activity activity) {
        LogUtil.getLog().e("hideKeyboard===");
        InputMethodManager manager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
