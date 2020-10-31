package net.cb.cb.library.view;

import android.content.Context;
import androidx.core.widget.NestedScrollView;
import android.util.AttributeSet;

/**
 * 包路径：com.hanming.education.view
 * 类描述：滑动监听 
 * 创建时间：2019/12/02  19:11
 * 修改人：
 * 修改时间：2019/12/02  19:11
 * 修改备注：
 */
public class EScrollView extends NestedScrollView {
    private OnScrolListener listener;

    public void setOnScrolListener(OnScrolListener listener) {
        this.listener = listener;
    }

    public EScrollView(Context context) {
        super(context);
    }

    public EScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //设置接口
    public interface OnScrolListener {
        void onScroll(int scrollY);
    }

    //重写原生onScrollChanged方法，将参数传递给接口，由接口传递出去
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener != null) {
            //这里我只传了垂直滑动的距离
            listener.onScroll(t);
        }
    }


}
