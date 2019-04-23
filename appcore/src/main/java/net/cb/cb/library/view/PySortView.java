package net.cb.cb.library.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.cb.cb.library.R;
import net.cb.cb.library.utils.ToastUtil;

import java.util.HashMap;

/***
 * 拼音控件
 */
public class PySortView extends LinearLayout {
    private TextView txtSelectView;
    private LinearLayout txtPyIptView;
    private int maxSize = 28;
    private RecyclerView listview;
    //标签,和列号
    private HashMap<String,Integer> tagIndex=new HashMap<>();
    private Event mEvent=new Event() {
        @Override
        public void onChange(String type) {
            if(listview==null)
                return;
            if(tagIndex==null)
                return;

            if(tagIndex.containsKey(type)){
              int i=  tagIndex.get(type);

                //listview.smoothScrollToPosition(i);
                smoothMoveToPosition(listview,i);
            }

        }
    };

    public void setEvent(Event mEvent) {
        this.mEvent = mEvent;
    }


    public void putTag(String tag,int i){
        if(!tagIndex.containsKey(tag))
        tagIndex.put(tag,i);
    }

    public HashMap<String, Integer> getTagIndex() {
        return tagIndex;
    }

    public void setListView(RecyclerView recyclerView){
        listview=recyclerView;
        listview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mShouldScroll = false;
                    smoothMoveToPosition(listview, mToPosition);
                }
            }
        });

    }

    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;

    /**
     * 滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前，使用smoothScrollToPosition
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后，最后一个可见项之前
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                // smoothScrollToPosition 不会有效果，此时调用smoothScrollBy来滑动到指定位置
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }


    public PySortView(final Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View viewRoot = inflater.inflate(R.layout.view_pysort, this);
        txtSelectView = viewRoot.findViewById(R.id.txt_py_select);
        txtPyIptView = viewRoot.findViewById(R.id.view_py_ipt);
        txtSelectView.setVisibility(GONE);
        for (int i = 0; i < maxSize; i++) {

            TextView textView = (TextView) inflater.inflate(R.layout.view_pysort_item, null);

            if (i == 0) {
                textView.setText("↑");
            }else if(i==(maxSize-1)){
                textView.setText("#");
            }else {
                textView.setText("" + (char) (64 + i));
            }
            textView.setTag(textView.getText().toString());


            txtPyIptView.addView(textView);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            layoutParams.weight = 1;


        }


    }

    private float spHeight = 0;
    private int height = 0;
    private int maxheight = 0;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        height = getMeasuredHeight();
        spHeight = getMeasuredHeight() / maxSize;

        maxheight = height - txtSelectView.getMeasuredHeight();
    }


    private String oldTxt="";
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                txtSelectView.setVisibility(VISIBLE);

                Float idx = event.getY() / spHeight;

                idx = idx < 0 ? 0 : idx;
                idx = idx > maxSize ? maxSize - 1 : idx;
                //设置显示文字
                String txt = (String) txtPyIptView.getChildAt(idx.intValue()).getTag();
                txtSelectView.setText(txt);
                //动态设置文字位置
                float y = event.getY();
                y = y < 0 ? 0 : y;
                y = y > maxheight ? maxheight : y;
                txtSelectView.setY(y);

                //处理回掉事件
                if(mEvent!=null){
                    if( !oldTxt.equals(txt)){
                        oldTxt=txt;
                        mEvent.onChange(txt);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                txtSelectView.setVisibility(GONE);
                oldTxt="";
                break;
        }


        return true;
    }

    public interface Event {
        void onChange(String type);
    }
}
