package net.cb.cb.library.utils;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/***
 * 播放图片动画
 */
public class AnimationPic {
    private ValueAnimator animator;
    private int[] res;
    private ImageView view;
    private int defRes;
    private long time;

    public void init(final ImageView mview, final int[] res,int defRes, long time) {
        if(animator!=null){
            stop(view);
        }
        this.res = res;
        this.view = mview;
        this.defRes=defRes;
       this.time=time;
        play();
        Log.d("xxx", "init: ");
        animator.start();
    }

    private void play(){
        animator = ValueAnimator.ofInt(0, res.length);
        animator.setDuration(time);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int v = (int) animation.getAnimatedValue();

                view.setImageResource(res[v]);
              //  Log.d("xxx", "addUpdateListener: ");
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
    }

    public void start(ImageView mview){
        this.view = mview;
        Log.d("xxx", "start: "+mview);
        play();
        animator.start();
    }

    public void stop(ImageView mview){
        Log.d("xxx", "stop: "+animator);
        if(animator==null)
            return;
        if(mview!=view)
            return;
        animator.cancel();
        view.setImageResource(defRes);
    }
}
