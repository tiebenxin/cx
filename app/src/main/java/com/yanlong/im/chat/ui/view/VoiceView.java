package com.yanlong.im.chat.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;

public class VoiceView extends LinearLayout {
    private LinearLayout viewOtVoice;
    private TextView txtOtVoice;
    private View viewOtP;
    private LinearLayout viewMeVoice;
    private View viewMeP;
    private TextView txtMeVoice;


    //自动寻找控件
    private void findViews(View rootView) {
        viewOtVoice = (LinearLayout) rootView.findViewById(R.id.view_ot_voice);
        txtOtVoice = (TextView) rootView.findViewById(R.id.txt_ot_voice);
        viewOtP = (View) rootView.findViewById(R.id.view_ot_p);
        viewMeVoice = (LinearLayout) rootView.findViewById(R.id.view_me_voice);
        viewMeP = (View) rootView.findViewById(R.id.view_me_p);
        txtMeVoice = (TextView) rootView.findViewById(R.id.txt_me_voice);
    }

    public VoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View viewRoot = inflater.inflate(R.layout.view_chat_item_voice, this);
        findViews(viewRoot);

    }

    public void init(final boolean isMe, final int second) {
        post(new Runnable() {
            @Override
            public void run() {

                if (isMe) {
                    viewMeVoice.setVisibility(VISIBLE);
                    viewOtVoice.setVisibility(GONE);
                } else {
                    viewMeVoice.setVisibility(GONE);
                    viewOtVoice.setVisibility(VISIBLE);
                }
                txtOtVoice.setText(second+"''");
                txtMeVoice.setText(second+"''");

                int s=second>60?60:second;
                int w=new Float( (viewOtVoice.getMeasuredWidth()-viewOtP.getX())/60*(s)).intValue();
                ViewGroup.LayoutParams lp = viewMeP.getLayoutParams();
                lp.width=w;
                viewMeP.setLayoutParams(lp);

                lp = viewOtP.getLayoutParams();
                lp.width=w;
                viewOtP.setLayoutParams(lp);


            }
        });

    }

}
