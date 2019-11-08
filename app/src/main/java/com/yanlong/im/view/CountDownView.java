package com.yanlong.im.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.R;

/**
 * @创建人 shenxin
 * @创建时间 2019/11/5 0005 13:48
 */
public class CountDownView extends LinearLayout {
    private View view;
    private Context context;
    private ImageView imCountDown;
    private boolean isMe;


    public CountDownView(Context context,boolean isMe){
        super(context);
        this.isMe = isMe;
        initView(context);
    }

    public CountDownView(Context context) {
        super(context);
        initView(context);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.view_count_down, this);
        imCountDown = view.findViewById(R.id.im_count_down);
//        if(isMe){
//            imCountDown.setImageResource(R.mipmap.icon_me_1);
//        }else {
//            imCountDown.setImageResource(R.mipmap.icon_other_1);
//        }
    }


//    public void setImagePostion(long startTime, long endTime) {
//        long nowTime = DateUtils.getSystemTime();
//        int time = (int) ((endTime - nowTime) / ((endTime - startTime) / 12));
//        if(isMe){
//            isME(time);
//        }else{
//            isOther(time);
//        }
//    }


//    private void isME(int type){
//        switch (type){
//            case 12:
//                imCountDown.setImageResource(R.mipmap.icon_me_1);
//                break;
//            case 11:
//                imCountDown.setImageResource(R.mipmap.icon_me_2);
//                break;
//            case 10:
//                imCountDown.setImageResource(R.mipmap.icon_me_3);
//                break;
//            case 9:
//                imCountDown.setImageResource(R.mipmap.icon_me_4);
//                break;
//            case 8:
//                imCountDown.setImageResource(R.mipmap.icon_me_5);
//                break;
//            case 7:
//                imCountDown.setImageResource(R.mipmap.icon_me_6);
//                break;
//            case 6:
//                imCountDown.setImageResource(R.mipmap.icon_me_7);
//                break;
//            case 5:
//                imCountDown.setImageResource(R.mipmap.icon_me_8);
//                break;
//            case 4:
//                imCountDown.setImageResource(R.mipmap.icon_me_9);
//                break;
//            case 3:
//                imCountDown.setImageResource(R.mipmap.icon_me_10);
//                break;
//            case 2:
//                imCountDown.setImageResource(R.mipmap.icon_me_11);
//                break;
//            case 1:
//                imCountDown.setImageResource(R.mipmap.icon_me_12);
//                break;
//            default:
//                imCountDown.setImageResource(R.mipmap.icon_me_1);
//                break;
//        }
//    }
//
//    private void isOther(int type){
//        switch (type){
//            case 12:
//                imCountDown.setImageResource(R.mipmap.icon_other_1);
//                break;
//            case 11:
//                imCountDown.setImageResource(R.mipmap.icon_other_2);
//                break;
//            case 10:
//                imCountDown.setImageResource(R.mipmap.icon_other_3);
//                break;
//            case 9:
//                imCountDown.setImageResource(R.mipmap.icon_other_4);
//                break;
//            case 8:
//                imCountDown.setImageResource(R.mipmap.icon_other_5);
//                break;
//            case 7:
//                imCountDown.setImageResource(R.mipmap.icon_other_6);
//                break;
//            case 6:
//                imCountDown.setImageResource(R.mipmap.icon_other_7);
//                break;
//            case 5:
//                imCountDown.setImageResource(R.mipmap.icon_other_8);
//                break;
//            case 4:
//                imCountDown.setImageResource(R.mipmap.icon_other_9);
//                break;
//            case 3:
//                imCountDown.setImageResource(R.mipmap.icon_other_10);
//                break;
//            case 2:
//                imCountDown.setImageResource(R.mipmap.icon_other_11);
//                break;
//            case 1:
//                imCountDown.setImageResource(R.mipmap.icon_other_12);
//                break;
//            default:
//                imCountDown.setImageResource(R.mipmap.icon_other_1);
//                break;
//        }
//    }





}
