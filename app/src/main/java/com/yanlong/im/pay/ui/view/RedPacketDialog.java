package com.yanlong.im.pay.ui.view;

import android.animation.ObjectAnimator;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yanlong.im.R;

public class RedPacketDialog extends DialogFragment {

    private ImageView imgCls;
    private com.facebook.drawee.view.SimpleDraweeView imgUhead;
    private TextView txtUname;
    private TextView txtRbInfo;
    private ImageView imgOpen;
    private TextView txtMore;

    private int type=0;
    private String headUrl;
    private String uname;
    private String info;
    private View.OnClickListener onk;
    //自动寻找控件
    private void findViews(View rootView) {
        imgCls = (ImageView) rootView.findViewById(R.id.img_cls);
        imgUhead = (com.facebook.drawee.view.SimpleDraweeView) rootView.findViewById(R.id.img_uhead);
        txtUname = (TextView) rootView.findViewById(R.id.txt_uname);
        txtRbInfo = (TextView) rootView.findViewById(R.id.txt_rb_info);
        imgOpen = (ImageView) rootView.findViewById(R.id.img_open);
        txtMore = (TextView) rootView.findViewById(R.id.txt_more);
    }


    //自动生成的控件事件
    private void initEvent() {
        imgCls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        imgUhead.setImageURI(Uri.parse(headUrl));
        txtUname.setText(uname);

        if(type==0){
            txtRbInfo.setText(info);
            imgOpen.setVisibility(View.VISIBLE);
            imgOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playAnim(onk);
                }
            });
            txtMore.setVisibility(View.GONE);
        }else {
            txtMore.setVisibility(View.VISIBLE);
            imgOpen.setVisibility(View.GONE);
            txtMore.setOnClickListener(onk);
        }


    }

    public void show4open(FragmentManager manager,String headUrl, String uname, String info, final View.OnClickListener onk) {
        type=0;
        this.headUrl=headUrl;
        this.uname=uname;
        this.info=info;
        this.onk=onk;
       if(this.isAdded()) {
           initEvent();
       }else{
           show(manager,"redTag");
       }


    }

    public void show4opened(FragmentManager manager,String headUrl, String uname, String info, final View.OnClickListener onkMore) {
        type=1;
        this.headUrl=headUrl;
        this.uname=uname;
        this.info=info;
        this.onk=onkMore;
        if(this.isAdded()) {
            initEvent();
        }else {
            show(manager, "redTag");
        }

    }

    /***
     * 动画处理
     */
    private void playAnim(final View.OnClickListener onk) {
       // ObjectAnimator
        RedAmina amina = new RedAmina();

        imgOpen.startAnimation(amina);

        amina.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onk.onClick(imgOpen);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

      //  amina.start();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(net.cb.cb.library.R.layout.fgm_redpacket_dialog, container, false);
        this.setCancelable(false);
        findViews(v);
        initEvent();
        return v;
    }
}
