package com.yanlong.im.chat.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.utils.DensityUtil;

public class VoiceView extends LinearLayout {
    private LinearLayout viewOtVoice;
    private TextView txtOtVoice;
    private View viewOtP;
    private View imgOtUnRead;
    private LinearLayout viewMeVoice;
    private View viewMeP;
    private TextView txtMeVoice;
    private ImageView imgOtIcon;
    private ImageView imgMeIcon;

    public ImageView getImgOtIcon() {
        return imgOtIcon;
    }

    public ImageView getImgMeIcon() {
        return imgMeIcon;
    }

    //自动寻找控件
    private void findViews(View rootView) {
        viewOtVoice = (LinearLayout) rootView.findViewById(R.id.view_ot_voice);
        txtOtVoice = (TextView) rootView.findViewById(R.id.txt_ot_voice);
        viewOtP = (View) rootView.findViewById(R.id.view_ot_p);
        imgOtUnRead = (View) rootView.findViewById(R.id.img_ot_unread);
        viewMeVoice = (LinearLayout) rootView.findViewById(R.id.view_me_voice);
        viewMeP = (View) rootView.findViewById(R.id.view_me_p);
        txtMeVoice = (TextView) rootView.findViewById(R.id.txt_me_voice);
        imgOtIcon = (ImageView) rootView.findViewById(R.id.img_ot_icon);
        imgMeIcon = (ImageView) rootView.findViewById(R.id.img_me_icon);


        viewOtVoice.setVisibility(GONE);
        viewMeVoice.setVisibility(GONE);
    }

    public VoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View viewRoot = inflater.inflate(R.layout.view_chat_item_voice, this);
        findViews(viewRoot);

    }

    public void init(final boolean isMe, final int second, boolean isRead) {

        if (isMe) {
            viewMeVoice.setVisibility(VISIBLE);
            viewOtVoice.setVisibility(GONE);
        } else {
            viewMeVoice.setVisibility(GONE);
            viewOtVoice.setVisibility(VISIBLE);
            imgOtUnRead.setVisibility(isRead ? GONE : VISIBLE);
        }
        txtOtVoice.setText(second + "''");
        txtMeVoice.setText(second + "''");

        int s = second > 60 ? 60 : second;
        int wsum = getScreenWidth() - DensityUtil.dip2px(getContext(), 74) * 2;//-DensityUtil.dip2px(getContext(),35);
        float x = DensityUtil.dip2px(getContext(), 60);//viewOtP.getX();
        int w = new Float((wsum - x) / 60 * (s)).intValue();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewMeP.getLayoutParams();
        lp.width = w;
        lp.weight = 1;
        viewMeP.setLayoutParams(lp);

        lp = (LinearLayout.LayoutParams) viewOtP.getLayoutParams();
        lp.width = w;
        viewOtP.setLayoutParams(lp);


    }

    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


}
