package net.cb.cb.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.AttributeSet;

public class CursorEditText extends AppCompatEditText {
    private CharSequence hint;
    private int hintColor;
    private Paint mPaint;


    public CursorEditText(Context context) {
        super(context);
        init();
    }

    public CursorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CursorEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        hint = getHint();
        setHint("");
        hintColor = getHighlightColor();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(hintColor);
        mPaint.setTextSize(getTextSize());
        mPaint.setTextAlign(Paint.Align.RIGHT);
//        banLongClick();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (TextUtils.isEmpty(hint) || !TextUtils.isEmpty(getText())) {
            return;
        }
        canvas.save();
        Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
        int baseline = (getHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(hint, 0, hint.length(), getWidth() - getPaddingRight() + getScrollX(), baseline, mPaint);
        canvas.restore();
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste) {
            return false;
        }
        return super.onTextContextMenuItem(id);
    }

    private void banLongClick() {
        setLongClickable(false);
        setTextIsSelectable(false);
    }
}
