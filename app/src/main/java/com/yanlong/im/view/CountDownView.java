package com.yanlong.im.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.luck.picture.lib.tools.DateUtils;
import com.yanlong.im.R;

import net.cb.cb.library.utils.LogUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @创建人 shenxin
 * @创建时间 2019/11/5 0005 13:48
 */
public class CountDownView extends LinearLayout {
    private View view;
    private Context context;
    private ImageView imCountDown;
    private Disposable timer;
    private int preTime;
    //12张图
    private int COUNT=12;


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




    public void timerStop() {
//        LogUtil.getLog().i("CountDownView", "timerStop--timer=" + (timer == null));
        imCountDown.setImageResource(R.mipmap.icon_st_1);
        if (timer != null&&!timer.isDisposed()) {
            timer.dispose();
            timer = null;
        }
    }


    private void initView(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.view_count_down, this);
        imCountDown = view.findViewById(R.id.im_count_down);
        imCountDown.setImageResource(R.mipmap.icon_st_1);
    }

    //这是倒计时执行方法
    public void setRunTimer(long startTime, long endTime) {
        LogUtil.getLog().i("CountDownView", "setRunTimer=" + startTime + "---" + endTime);
        long nowTimeMillis= DateUtils.getSystemTime();
        long period=0;
        long start=1;
        if(nowTimeMillis<endTime) {//当前时间还在倒计时结束前
            long distance = startTime - nowTimeMillis;//和现在时间相差的毫秒数
            //四舍五入
            period=Math.round(Double.valueOf(endTime-startTime)/COUNT);
            if (distance < 0) {//开始时间小于现在，已经开始了
                start=-distance/period;
            }
            start=Math.max(1,start);
            //延迟initialDelay个unit单位后，以period为周期，依次发射count个以start为初始值并递增的数字。
            //eg:发送数字1~10，每间隔200毫秒发射一个数据 intervalRange(1, 10, 0, 200, TimeUnit.MILLISECONDS);
            //发送数字0~11，每间隔period/COUNT毫秒发射一个数据,延迟distance毫秒
            Log.e("raleigh_test","start="+nowTimeMillis+",distance="+distance);
            if(timer!=null&&!timer.isDisposed()){
                timer.dispose();
            }
            timer=null;
            timer = Flowable.intervalRange(start, COUNT-start+1, 0, period, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Consumer<Long>() {
                        @Override
                        public void accept(Long index) throws Exception {
                            long time=nowTimeMillis-DateUtils.getSystemTime();
                            Log.e("raleigh_test","value="+index+",nowdis="+time);
                            if (imCountDown != null) {
                                String name="icon_st_"+Math.min(COUNT,index+1);
                                imCountDown.setImageResource(context.getResources().getIdentifier(name,"mipmap",context.getPackageName()));
                                LogUtil.getLog().i("CountDownView", "isME=" + index);
                            }
                        }
                    }).doOnComplete(new Action() {
                        @Override
                        public void run() throws Exception {
                        }
                    }).subscribe();
        }
    }
}
