package net.cb.cb.library.audio;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public class IAdioTouch implements View.OnTouchListener {
    private Context context;
    private MTouchListener listener;

    public IAdioTouch(Context context,MTouchListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                AudioRecordManager.getInstance(context).startRecord();
                if(listener != null){
                    listener.onDown();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isCancelled(v, event)) {
                    AudioRecordManager.getInstance(context).willCancelRecord();
                } else {
                    AudioRecordManager.getInstance(context).continueRecord();
                }
                if(listener != null){
                    listener.onMove();
                }
                break;
            case MotionEvent.ACTION_UP:
                AudioRecordManager.getInstance(context).stopRecord();
                AudioRecordManager.getInstance(context).destroyRecord();
                if(listener != null){
                    listener.onUp();
                }
                break;
        }
        return false;
    }


    private boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        if (event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth() || event.getRawY() < location[1] - 40) {
            return true;
        }
        return false;
    }


    public interface MTouchListener{
        void onDown();

        void onMove();

        void onUp();
    }

}
