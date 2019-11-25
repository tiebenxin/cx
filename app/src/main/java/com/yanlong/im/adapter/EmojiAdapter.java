//package com.yanlong.im.adapter;
//
//import android.support.annotation.NonNull;
//import android.support.v4.view.PagerAdapter;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.GridLayout;
//import android.widget.TextView;
//
//import com.yanlong.im.R;
//
//import net.cb.cb.library.view.MsgEditText;
//
//import java.util.List;
//
//public class EmojiAdapter extends PagerAdapter {
//
//    private List<View> datas;
//    private MsgEditText edtChat;
//
//    public EmojiAdapter(List<View> list,MsgEditText medtChat) {
//        datas=list;
//        edtChat=medtChat;
//    }
//    @Override
//    public int getCount() {
//        return datas.size();
//    }
//
//    @Override
//    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//        return view==object;
//    }
//
//    @NonNull
//    @Override
//    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        View view=datas.get(position);
//        //        //todo  emoji表情处理
//        container.addView(view);
//        return view;
//    }
//
//    @Override
//    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        container.removeView(datas.get(position));
//    }
//}
