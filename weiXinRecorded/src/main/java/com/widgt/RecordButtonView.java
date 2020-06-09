package com.widgt;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.listener.CaptureListener;
import com.listener.IRecordListener;
import com.zhaoss.weixinrecorded.R;


/**
 * @author Liszt
 * @date 2020/5/29
 * Description 录制视频按钮
 */
public class RecordButtonView extends FrameLayout implements CaptureListener {
    private Context mContext;
    private CaptureButton btCapture;
    private TextView tvNotice;
    private ReturnButton btReturn;
    private TypeButton btCancel;
    private TypeButton btSure;
    private IRecordListener listener;
    private boolean isFirst;
    private int layout_width;
    private View llResult;

    public RecordButtonView(@NonNull Context context) {
        this(context, null);
    }

    public RecordButtonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordButtonView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layout_width = outMetrics.widthPixels;
        } else {
            layout_width = outMetrics.widthPixels / 2;
        }
        initView();
    }

    private void initView() {
        setWillNotDraw(false);
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_record_view, this);
        btCapture = view.findViewById(R.id.bt_capture);
        tvNotice = view.findViewById(R.id.tv_notice);
        btReturn = view.findViewById(R.id.bt_return);
        btCancel = view.findViewById(R.id.bt_cancel);
        btSure = view.findViewById(R.id.bt_sure);
        llResult = view.findViewById(R.id.ll_result);
        resetUI(true);
        btCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onCancel();
                }
                startAlphaAnimation();
            }
        });

        btSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onSure();
                }
                startAlphaAnimation();

            }
        });

        btReturn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onReturn();
                }
            }
        });
    }

    public void setListener(IRecordListener l) {
        listener = l;
    }

    public void startAlphaAnimation() {
        if (isFirst) {
            ObjectAnimator animator_txt_tip = ObjectAnimator.ofFloat(tvNotice, "alpha", 0f, 1f, 1f, 0f);
            animator_txt_tip.setDuration(3500);
            animator_txt_tip.start();
            isFirst = false;
        }
    }

    public void startTypeBtnAnimator() {
        //拍照录制结果后的动画
        btCapture.setVisibility(GONE);
        llResult.setVisibility(VISIBLE);
        btCancel.setVisibility(VISIBLE);
        btSure.setVisibility(VISIBLE);
        btCancel.setClickable(false);
        btSure.setClickable(false);
//        ObjectAnimator animator_cancel = ObjectAnimator.ofFloat(btCancel, "translationX", layout_width / 4, 0);
//        ObjectAnimator animator_confirm = ObjectAnimator.ofFloat(btSure, "translationX", -layout_width / 4, 0);
        ObjectAnimator animator_cancel = ObjectAnimator.ofFloat(btCancel, "translationX", 0, -layout_width / 4);
        ObjectAnimator animator_confirm = ObjectAnimator.ofFloat(btSure, "translationX", 0, layout_width / 4);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator_cancel, animator_confirm);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btCancel.setClickable(true);
                btSure.setClickable(true);
            }
        });
        set.setDuration(200);
        set.start();
    }

    public void resetUI(boolean flag) {
        if (flag) {
            tvNotice.setVisibility(VISIBLE);
            btReturn.setVisibility(VISIBLE);
            btCapture.setVisibility(VISIBLE);
            if (llResult.isShown()) {
                llResult.setVisibility(GONE);
            }
            btCapture.setCaptureListener(this);
        } else {
            tvNotice.setVisibility(GONE);
            btReturn.setVisibility(GONE);
            btCapture.setVisibility(GONE);
        }
    }

    public void resetNotice(boolean flag) {
        if (flag) {
            tvNotice.setVisibility(VISIBLE);
            btReturn.setVisibility(VISIBLE);

        } else {
            tvNotice.setVisibility(GONE);
            btReturn.setVisibility(GONE);
        }
    }

    public void recordEnd() {
        btCapture.recordEnd();
    }

    @Override
    public void takePictures() {
        if (listener != null) {
            listener.takePictures();
        }
    }

    @Override
    public void recordShort(long time) {
        if (listener != null) {
            listener.recordShort(time);
        }
    }

    @Override
    public void recordStart() {
        if (listener != null) {
            listener.recordStart();
        }
        resetNotice(false);
    }

    @Override
    public void recordEnd(long time) {
        if (listener != null) {
            listener.recordEnd(time);
        }
        startAlphaAnimation();
        startTypeBtnAnimator();
    }

    @Override
    public void recordZoom(float zoom) {
        if (listener != null) {
            listener.recordZoom(zoom);
        }
    }

    @Override
    public void recordError() {
        if (listener != null) {
            listener.recordError();
        }
    }

    public void onResume() {
        btCapture.resetState();
    }
}
