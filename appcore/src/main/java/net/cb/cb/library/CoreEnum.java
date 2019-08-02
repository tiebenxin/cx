package net.cb.cb.library;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
 * 常量封装类，类似于枚举
 * */
public class CoreEnum {

    @IntDef({ESureType.NO, ESureType.YES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ESureType {
        int NO = 0;
        int YES = 1;
    }

}
