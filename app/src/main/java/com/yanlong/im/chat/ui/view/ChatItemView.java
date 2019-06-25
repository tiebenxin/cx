package com.yanlong.im.chat.ui.view;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;

public class ChatItemView extends LinearLayout {
    private TextView txtOtName;
    private TextView txtMeName;
    private TextView txtTime;
    private TextView txtBroadcast;
    private LinearLayout viewOt;
    private com.facebook.drawee.view.SimpleDraweeView imgOtHead;
    private LinearLayout viewOt1;
    private android.support.v7.widget.AppCompatTextView txtOt1;
    private LinearLayout viewOt2;
    private android.support.v7.widget.AppCompatTextView txtOt2;
    private LinearLayout viewOt3;
    private ImageView imgOtRbState;
    private TextView txtOtRbTitle;
    private TextView txtOtRbInfo;
    private TextView txtOtRpBt;
    private ImageView imgOtRbIcon;
    private LinearLayout viewMe;
    private LinearLayout viewMe1;
    private android.support.v7.widget.AppCompatTextView txtMe1;
    private LinearLayout viewMe2;
    private android.support.v7.widget.AppCompatTextView txtMe2;
    private LinearLayout viewMe3;
    private ImageView imgMeRbState;
    private TextView txtMeRbTitle;
    private TextView txtMeRbInfo;
    private TextView txtMeRpBt;
    private ImageView imgMeRbIcon;
    private ImageView imgMeErr;
    private com.facebook.drawee.view.SimpleDraweeView imgMeHead;

    private LinearLayout viewMe4;
    private LinearLayout viewOt4;
    private com.facebook.drawee.view.SimpleDraweeView imgOt4;
    private com.facebook.drawee.view.SimpleDraweeView imgMe4;


    private LinearLayout viewOt5;
    private com.facebook.drawee.view.SimpleDraweeView imgOt5;
    private TextView txtOt5Title;
    private TextView txtOt5Info;
    private TextView txtOt5Bt;

    private LinearLayout viewMe5;
    private com.facebook.drawee.view.SimpleDraweeView imgMe5;
    private TextView txtMe5Title;
    private TextView txtMe5Info;
    private TextView txtMe5Bt;

    //自动寻找控件
    private void findViews(View rootView) {

        txtMeName = (TextView) rootView.findViewById(R.id.txt_me_name);
        txtOtName = (TextView) rootView.findViewById(R.id.txt_ot_name);
        txtTime = (TextView) rootView.findViewById(R.id.txt_time);
        txtBroadcast = (TextView) rootView.findViewById(R.id.txt_broadcast);
        viewOt = (LinearLayout) rootView.findViewById(R.id.view_ot);
        imgOtHead = (com.facebook.drawee.view.SimpleDraweeView) rootView.findViewById(R.id.img_ot_head);
        viewOt1 = (LinearLayout) rootView.findViewById(R.id.view_ot_1);
        txtOt1 = (android.support.v7.widget.AppCompatTextView) rootView.findViewById(R.id.txt_ot_1);
        viewOt2 = (LinearLayout) rootView.findViewById(R.id.view_ot_2);
        txtOt2 = (android.support.v7.widget.AppCompatTextView) rootView.findViewById(R.id.txt_ot_2);
        viewOt3 = (LinearLayout) rootView.findViewById(R.id.view_ot_3);
        imgOtRbState = (ImageView) rootView.findViewById(R.id.img_ot_rb_state);
        txtOtRbTitle = (TextView) rootView.findViewById(R.id.txt_ot_rb_title);
        txtOtRbInfo = (TextView) rootView.findViewById(R.id.txt_ot_rb_info);
        txtOtRpBt = (TextView) rootView.findViewById(R.id.txt_ot_rp_bt);
        imgOtRbIcon = (ImageView) rootView.findViewById(R.id.img_ot_rb_icon);
        viewMe = (LinearLayout) rootView.findViewById(R.id.view_me);
        viewMe1 = (LinearLayout) rootView.findViewById(R.id.view_me_1);
        txtMe1 = (android.support.v7.widget.AppCompatTextView) rootView.findViewById(R.id.txt_me_1);
        viewMe2 = (LinearLayout) rootView.findViewById(R.id.view_me_2);
        txtMe2 = (android.support.v7.widget.AppCompatTextView) rootView.findViewById(R.id.txt_me_2);
        viewMe3 = (LinearLayout) rootView.findViewById(R.id.view_me_3);
        imgMeRbState = (ImageView) rootView.findViewById(R.id.img_me_rb_state);
        txtMeRbTitle = (TextView) rootView.findViewById(R.id.txt_me_rb_title);
        txtMeRbInfo = (TextView) rootView.findViewById(R.id.txt_me_rb_info);
        txtMeRpBt = (TextView) rootView.findViewById(R.id.txt_me_rp_bt);
        imgMeRbIcon = (ImageView) rootView.findViewById(R.id.img_me_rb_icon);
        imgMeHead = (com.facebook.drawee.view.SimpleDraweeView) rootView.findViewById(R.id.img_me_head);
        imgMeErr = (ImageView) rootView.findViewById(R.id.img_me_err);

        viewOt4 = (LinearLayout) rootView.findViewById(R.id.view_ot_4);
        imgOt4 = (com.facebook.drawee.view.SimpleDraweeView) rootView.findViewById(R.id.img_ot_4);
        viewMe4 = (LinearLayout) rootView.findViewById(R.id.view_me_4);
        imgMe4 = (com.facebook.drawee.view.SimpleDraweeView) rootView.findViewById(R.id.img_me_4);

        viewOt5 = (LinearLayout) rootView.findViewById(R.id.view_ot_5);
        imgOt5 = (com.facebook.drawee.view.SimpleDraweeView) rootView.findViewById(R.id.img_ot_5);
        txtOt5Title = (TextView) rootView.findViewById(R.id.txt_ot_5_title);
        txtOt5Info = (TextView) rootView.findViewById(R.id.txt_ot_5_info);
        txtOt5Bt = (TextView) rootView.findViewById(R.id.txt_ot_5_bt);

        viewMe5 = (LinearLayout) rootView.findViewById(R.id.view_me_5);
        imgMe5 = (com.facebook.drawee.view.SimpleDraweeView) rootView.findViewById(R.id.img_me_5);
        txtMe5Title = (TextView) rootView.findViewById(R.id.txt_me_5_title);
        txtMe5Info = (TextView) rootView.findViewById(R.id.txt_me_5_info);
        txtMe5Bt = (TextView) rootView.findViewById(R.id.txt_me_5_bt);




    }


    //自动生成的控件事件
    private void initEvent() {

    }

    /***
     * 显示类型
     * @param type
     * @param isMe
     */
    public void setShowType(int type, boolean isMe, String headUrl,String nikeName, String time) {

        if (isMe) {
            viewMe.setVisibility(VISIBLE);
            viewOt.setVisibility(GONE);
        } else {
            viewMe.setVisibility(GONE);
            viewOt.setVisibility(VISIBLE);
        }
        txtBroadcast.setVisibility(GONE);
      //  imgMeErr.setVisibility(GONE);
        viewMe1.setVisibility(GONE);
        viewOt1.setVisibility(GONE);
        viewMe2.setVisibility(GONE);
        viewOt2.setVisibility(GONE);
        viewMe3.setVisibility(GONE);
        viewOt3.setVisibility(GONE);
        viewMe4.setVisibility(GONE);
        viewOt4.setVisibility(GONE);
        viewMe5.setVisibility(GONE);
        viewOt5.setVisibility(GONE);

        switch (type) {
            case 0://公告
                txtBroadcast.setVisibility(VISIBLE);
                viewMe.setVisibility(GONE);
                viewOt.setVisibility(GONE);
                break;
            case 1:
                viewMe1.setVisibility(VISIBLE);
                viewOt1.setVisibility(VISIBLE);
                break;
            case 2:
                viewMe2.setVisibility(VISIBLE);
                viewOt2.setVisibility(VISIBLE);
                break;
            case 3:
                viewMe3.setVisibility(VISIBLE);
                viewOt3.setVisibility(VISIBLE);
                break;
            case 4:
                viewMe4.setVisibility(VISIBLE);
                viewOt4.setVisibility(VISIBLE);
                break;
            case 5:
                viewMe5.setVisibility(VISIBLE);
                viewOt5.setVisibility(VISIBLE);
                break;
        }

        if (headUrl != null) {
            imgMeHead.setImageURI(Uri.parse(headUrl));
            imgOtHead.setImageURI(Uri.parse(headUrl));
        }
        if(nikeName!=null){
            txtMeName.setText(nikeName);
            txtOtName.setText(nikeName);
            txtOtName.setVisibility(VISIBLE);
            txtMeName.setVisibility(VISIBLE);
        }else{
            txtOtName.setVisibility(INVISIBLE);
            txtMeName.setVisibility(INVISIBLE);
        }

        if (time == null) {
            txtTime.setVisibility(GONE);
        } else {
            txtTime.setText(time);
            txtTime.setVisibility(VISIBLE);
        }

    }

    //公告
    public void setData0(String msghtml) {
        txtBroadcast.setText(Html.fromHtml(msghtml));
    }

    //普通消息
    public void setData1(String msg) {
        txtMe1.setText(msg);
        txtOt1.setText(msg);
    }

    //戳一下消息
    public void setData2(String msg) {

        String textSource = "<font color='#079892'>戳一下　</font>" + msg;

        txtMe2.setText(Html.fromHtml(textSource));
        txtOt2.setText(txtMe2.getText());
    }

    //红包消息
    public void setData3(final boolean isInvalid, String title, String info, String typeName, int typeIconRes, final EventRP eventRP) {
        if (isInvalid) {//失效
            imgMeRbState.setImageResource(R.mipmap.ic_rb_zfb_n);
            imgOtRbState.setImageResource(R.mipmap.ic_rb_zfb_n);
            viewMe3.setBackgroundResource(R.drawable.bg_chat_me_rp_h);
            viewOt3.setBackgroundResource(R.drawable.bg_chat_other_rp_h);
        } else {
            imgMeRbState.setImageResource(R.mipmap.ic_rb_zfb_un);
            imgOtRbState.setImageResource(R.mipmap.ic_rb_zfb_un);
            viewMe3.setBackgroundResource(R.drawable.bg_chat_me_rp);
            viewOt3.setBackgroundResource(R.drawable.bg_chat_other_rp);
        }

        if (eventRP != null) {
            OnClickListener onk;
            viewMe3.setOnClickListener(onk = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventRP.onClick(isInvalid);
                }
            });
            viewOt3.setOnClickListener(onk);
        }


        txtMeRbTitle.setText(title);
        txtOtRbTitle.setText(title);

        txtMeRbInfo.setText(info);
        txtOtRbInfo.setText(info);

       if(typeName!=null){
           txtMeRpBt.setText(typeName);
           txtOtRpBt.setText(typeName);
       }

       if(typeIconRes!=0){
           imgMeRbIcon.setImageResource(typeIconRes);
           imgOtRbIcon.setImageResource(typeIconRes);
       }



    }

    public void setFont(Integer size) {
        txtMe1.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        txtOt1.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public interface EventRP {
        void onClick(boolean isInvalid);
    }

    //图片消息
    public void setData4(String url, final EventPic eventPic) {
        if (url != null) {
            setData4(Uri.parse(url), eventPic);

        }

    }

    public void setData4(final Uri uri, final EventPic eventPic) {
        if (uri != null) {
            imgMe4.setImageURI(uri);
            imgOt4.setImageURI(uri);
        }
        if (eventPic != null) {

            OnClickListener onk;
            imgMe4.setOnClickListener(onk = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventPic.onClick(uri.toString());
                }
            });
            imgOt4.setOnClickListener(onk);
        }

    }

    public interface EventPic {
        void onClick(String uri);
    }

    //名片消息
    public void setData5(String name, String info,String headUrl, String moreInfo,OnClickListener onk) {
        if(moreInfo!=null){
            txtMe5Bt.setText(moreInfo);
            txtOt5Bt.setText(moreInfo);
        }

        txtMe5Title.setText(name);
        txtMe5Info.setText(info);

        imgMe5.setImageURI(Uri.parse(headUrl));
        txtOt5Title.setText(name);
        txtOt5Info.setText(info);

        imgOt5.setImageURI(Uri.parse(headUrl));

        viewOt5.setOnClickListener(onk);
        viewMe5.setOnClickListener(onk);
    }


    public ChatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View viewRoot = inflater.inflate(R.layout.view_chat_item, this);
        findViews(viewRoot);
        initEvent();
    }

    public void setErr(int state){
        switch (state){
            case 0://正常
                imgMeErr.setVisibility(GONE);
               break;
            case 1://失败
                imgMeErr.setVisibility(VISIBLE);
                imgMeErr.setImageResource(R.mipmap.ic_net_err);
                break;
            case 2://等待
                imgMeErr.setVisibility(VISIBLE);
                imgMeErr.setImageResource(R.mipmap.ic_chat_more);
                break;

        }

    }
    public void setOnErr(OnClickListener onk){
        imgMeErr.setOnClickListener(onk);
    }

public void setOnHead(OnClickListener onk){
        imgMeHead.setOnClickListener(onk);
        imgOtHead.setOnClickListener(onk);
}


}
