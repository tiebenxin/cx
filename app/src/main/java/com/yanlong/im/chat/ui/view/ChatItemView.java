package com.yanlong.im.chat.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.ImageMessage;

import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.WebPageActivity;

import java.util.regex.Matcher;

public class ChatItemView extends LinearLayout {
    private TextView txtOtName;
    private TextView txtMeName;
    private TextView txtTime;
    private TextView txtBroadcast;
    private ImageView imgBroadcast;
    private View viewBroadcast;

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
    private ProgressBar imgMeUp;
    private View viewMeUp;
    private TextView txtMeUp;
    private LinearLayout viewOt4;
    /*    private com.facebook.drawee.view.SimpleDraweeView imgOt4;
        private com.facebook.drawee.view.SimpleDraweeView imgMe4;*/
    private ImageView imgOt4;
    private ImageView imgMe4;


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

    private LinearLayout viewMe6;
    private ImageView imgMeTsState;
    private TextView txtMeTsTitle;
    private TextView txtMeTsInfo;
    private TextView txtMeTsBt;
    private ImageView imgMeTsIcon;

    private LinearLayout viewOt6;
    private ImageView imgOtTsState;
    private TextView txtOtTsTitle;
    private TextView txtOtTsInfo;
    private TextView txtOtTsBt;
    private ImageView imgOtTsIcon;

    private VoiceView viewMe7;
    private VoiceView viewOt7;
    private boolean isMe;
    private View viewOtTouch;
    private View viewMeTouch;
    private LinearLayout viewOt8;
    private AppCompatTextView txtOt8;
    private LinearLayout viewMe8;
    private AppCompatTextView txtMe8;

    //自动寻找控件
    private void findViews(View rootView) {

        txtMeName = (TextView) rootView.findViewById(R.id.txt_me_name);
        txtOtName = (TextView) rootView.findViewById(R.id.txt_ot_name);
        txtTime = (TextView) rootView.findViewById(R.id.txt_time);
        txtBroadcast = (TextView) rootView.findViewById(R.id.txt_broadcast);
        imgBroadcast= (ImageView) rootView.findViewById(R.id.img_broadcast);
        viewBroadcast=rootView.findViewById(R.id.view_broadcast);

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
        imgOt4 = rootView.findViewById(R.id.img_ot_4);
        viewMe4 = (LinearLayout) rootView.findViewById(R.id.view_me_4);
        imgMeUp = (ProgressBar) rootView.findViewById(R.id.img_me_up);
        viewMeUp = rootView.findViewById(R.id.view_me_up);
        txtMeUp = (TextView) rootView.findViewById(R.id.txt_me_up);
        imgMe4 = rootView.findViewById(R.id.img_me_4);

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


        viewMe6 = (LinearLayout) rootView.findViewById(R.id.view_me_6);
        imgMeTsState = (ImageView) rootView.findViewById(R.id.img_me_ts_state);
        txtMeTsTitle = (TextView) rootView.findViewById(R.id.txt_me_ts_title);
        txtMeTsInfo = (TextView) rootView.findViewById(R.id.txt_me_ts_info);
        txtMeTsBt = (TextView) rootView.findViewById(R.id.txt_me_ts_bt);
        imgMeTsIcon = (ImageView) rootView.findViewById(R.id.img_me_ts_icon);

        viewOt6 = (LinearLayout) rootView.findViewById(R.id.view_ot_6);
        imgOtTsState = (ImageView) rootView.findViewById(R.id.img_ot_ts_state);
        txtOtTsTitle = (TextView) rootView.findViewById(R.id.txt_ot_ts_title);
        txtOtTsInfo = (TextView) rootView.findViewById(R.id.txt_ot_ts_info);
        txtOtTsBt = (TextView) rootView.findViewById(R.id.txt_ot_ts_bt);
        imgOtTsIcon = (ImageView) rootView.findViewById(R.id.img_ot_ts_icon);

        viewOt7 = (VoiceView) rootView.findViewById(R.id.view_ot_7);
        viewMe7 = (VoiceView) rootView.findViewById(R.id.view_me_7);
        viewOtTouch = rootView.findViewById(R.id.view_me_touch);
        viewMeTouch = rootView.findViewById(R.id.view_ot_touch);

        //小助手消息
        viewOt8 = rootView.findViewById(R.id.view_ot_8);
        txtOt8 = rootView.findViewById(R.id.txt_ot_8);
        viewMe8 = rootView.findViewById(R.id.view_me_8);
        txtMe8 = rootView.findViewById(R.id.txt_me_8);

    }

    public void setOnLongClickListener(OnLongClickListener onLongClick) {


        viewOtTouch.setOnLongClickListener(onLongClick);
        viewMeTouch.setOnLongClickListener(onLongClick);
       /* imgMe4.setOnLongClickListener(onLongClick);
        imgMe4.setOnLongClickListener(onLongClick);

        viewMe7.setOnLongClickListener(onLongClick);*/

    }

    public void setHeadOnLongClickListener(OnLongClickListener onLongClick) {


        //  imgMeHead.setOnLongClickListener(onLongClick);
        imgOtHead.setOnLongClickListener(onLongClick);


    }


    //自动生成的控件事件
    private void initEvent() {


    }

    /***
     * 显示类型
     * @param type
     * @param isMe
     */
    public void setShowType(int type, boolean isMe, String headUrl, String nikeName, String time) {

        this.isMe = isMe;
        if (isMe) {
            viewMe.setVisibility(VISIBLE);
            viewOt.setVisibility(GONE);
        } else {
            viewMe.setVisibility(GONE);
            viewOt.setVisibility(VISIBLE);
        }
        viewBroadcast.setVisibility(GONE);
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
        viewMe6.setVisibility(GONE);
        viewOt6.setVisibility(GONE);
        viewMe7.setVisibility(GONE);
        viewOt7.setVisibility(GONE);
        viewMe8.setVisibility(GONE);
        viewOt8.setVisibility(GONE);
        switch (type) {
            case ChatEnum.EMessageType.MSG_CENCAL://撤回的消息
            case 0://公告
                viewBroadcast.setVisibility(VISIBLE);
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
            case 6:
                viewMe6.setVisibility(VISIBLE);
                viewOt6.setVisibility(VISIBLE);
                break;
            case 7:
                viewMe7.setVisibility(VISIBLE);
                viewOt7.setVisibility(VISIBLE);
                break;
            case 8:
                viewMe1.setVisibility(VISIBLE);
                viewOt1.setVisibility(VISIBLE);
                break;
            case ChatEnum.EMessageType.ASSISTANT:
                viewMe8.setVisibility(VISIBLE);
                viewOt8.setVisibility(VISIBLE);
                break;
        }

        if (headUrl != null) {
            imgMeHead.setImageURI(Uri.parse(headUrl));
            imgOtHead.setImageURI(Uri.parse(headUrl));
        }
        if (nikeName != null) {
            txtMeName.setText(nikeName);
            txtOtName.setText(nikeName);
            txtOtName.setVisibility(VISIBLE);
            //  txtMeName.setVisibility(VISIBLE);
            txtMeName.setVisibility(GONE);
        } else {
            txtOtName.setVisibility(GONE);
            txtMeName.setVisibility(GONE);
        }

        if (time == null) {
            txtTime.setVisibility(GONE);
        } else {
            txtTime.setText(time);
            txtTime.setVisibility(VISIBLE);
        }

        viewMeTouch.setOnClickListener(null);
        viewOtTouch.setOnClickListener(null);
        viewMeTouch.setOnLongClickListener(null);
        viewOtTouch.setOnLongClickListener(null);
    }

    //公告
    public void setData0(String msghtml) {
        imgBroadcast.setVisibility(GONE);
        txtBroadcast.setText(Html.fromHtml(msghtml));
    }

    public void setData0(SpannableStringBuilder stringBuilder) {
        imgBroadcast.setVisibility(GONE);
        txtBroadcast.setText(stringBuilder);
        txtBroadcast.setMovementMethod(LinkMovementMethod.getInstance());
    }
    public void showBroadcastIcon(Boolean isShow,Integer rid){
        imgBroadcast.setVisibility(isShow?VISIBLE:GONE);
        if(rid!=null)
        imgBroadcast.setImageResource(rid);
    }

    //普通消息
    public void setData1(String msg) {
        txtMe1.setText(msg);
        txtOt1.setText(msg);
    }

    //AT消息
    public void setDataAt(String msg) {
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
//            viewMe3.setBackgroundResource(R.drawable.bg_chat_me_rp_h);
//            viewOt3.setBackgroundResource(R.drawable.bg_chat_other_rp_h);
            viewMe3.setBackgroundResource(R.drawable.selector_rp_h_me_touch);
            viewOt3.setBackgroundResource(R.drawable.selector_rp_h_other_touch);
        } else {
            imgMeRbState.setImageResource(R.mipmap.ic_rb_zfb_un);
            imgOtRbState.setImageResource(R.mipmap.ic_rb_zfb_un);
//            viewMe3.setBackgroundResource(R.drawable.bg_chat_me_rp);
//            viewOt3.setBackgroundResource(R.drawable.bg_chat_other_rp);
            viewMe3.setBackgroundResource(R.drawable.selector_rp_me_touch);
            viewOt3.setBackgroundResource(R.drawable.selector_rp_other_touch);
        }

        if (eventRP != null) {
            OnClickListener onk;
            viewMeTouch.setOnClickListener(onk = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventRP.onClick(isInvalid);
                }
            });
            viewOtTouch.setOnClickListener(onk);
        }


        txtMeRbTitle.setText(title);
        txtOtRbTitle.setText(title);

        txtMeRbInfo.setText(info);
        txtOtRbInfo.setText(info);

        if (typeName != null) {
            txtMeRpBt.setText(typeName);
            txtOtRpBt.setText(typeName);
        }

        if (typeIconRes != 0) {
            imgMeRbIcon.setImageResource(typeIconRes);
            imgOtRbIcon.setImageResource(typeIconRes);
        }


    }


    //转账消息
    public void setData6(final boolean isInvalid, String title, String info, String typeName, int typeIconRes, final EventRP eventRP) {
        if (isInvalid) {//失效
            imgMeTsState.setImageResource(R.mipmap.ic_rb_zfb_n);
            imgOtTsState.setImageResource(R.mipmap.ic_rb_zfb_n);
            viewMe6.setBackgroundResource(R.drawable.bg_chat_me_rp_h);
            viewOt6.setBackgroundResource(R.drawable.bg_chat_other_rp_h);
        } else {
            imgMeTsState.setImageResource(R.mipmap.ic_launcher);
            imgOtTsState.setImageResource(R.mipmap.ic_launcher);
            viewMe6.setBackgroundResource(R.drawable.bg_chat_me_rp);
            viewOt6.setBackgroundResource(R.drawable.bg_chat_other_rp);
        }

        if (eventRP != null) {
            OnClickListener onk;
            viewMeTouch.setOnClickListener(onk = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventRP.onClick(isInvalid);
                }
            });
            viewOtTouch.setOnClickListener(onk);
        }


        txtMeTsTitle.setText(title);
        txtOtTsTitle.setText(title);

        txtMeTsInfo.setText(info);
        txtOtTsInfo.setText(info);

        if (typeName != null) {
            txtMeTsBt.setText(typeName);
            txtOtTsBt.setText(typeName);
        }

        if (typeIconRes != 0) {
            imgMeTsIcon.setImageResource(typeIconRes);
            imgOtTsIcon.setImageResource(typeIconRes);
        }
    }

    //语音
    public void setData7(int second, boolean isRead, boolean isPlay, final OnClickListener onk) {
        viewOt7.init(isMe, second, isRead, isPlay);
        viewMe7.init(isMe, second, isRead, isPlay);

      /*  OnClickListener nonk = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMe) {

                    onk.onClick(viewMe7.getImgMeIcon());
                } else {
                    onk.onClick(viewOt7.getImgOtIcon());


                }

            }
        };*/
        viewMeTouch.setOnClickListener(onk);
        viewOtTouch.setOnClickListener(onk);


    }

    //普通消息
    public void setDataAssistant(String msg) {
//        msg = "http://baidu.com\n回复报告白拿的\nhttp://baidu.com\n发改委复合物号单位自己\nhttp://baidu.com";
        if (!StringUtil.isNotNull(msg)) {
            return;
        }
        Matcher matcher = StringUtil.URL.matcher(msg);
        int i = 0;
        int preLast = 0;
        int len = msg.length();
        SpannableStringBuilder builder = new SpannableStringBuilder();

        while (matcher.find()) {
            int groupCount = matcher.groupCount();
            if (groupCount > 0) {
                int start = matcher.start();
                int end = matcher.end();
                if (i == 0) {
                    if (start != 0) {
                        builder.append(msg.substring(0, start));
                        builder.append(setClickableSpan(msg.substring(start, end)));
                    } else {
                        builder.append(setClickableSpan(msg.substring(start, end)));
                    }
                } else {
                    if (end != len - 1) {
                        builder.append(msg.substring(preLast, start));
                        builder.append(setClickableSpan(msg.substring(start, end)));
                    }
                }
                preLast = end;
            }
            i++;
        }
        if (preLast == 0) {
            builder.append(msg.substring(preLast));
            txtMe8.setMovementMethod(LinkMovementMethod.getInstance());
            txtOt8.setMovementMethod(LinkMovementMethod.getInstance());
            txtMe8.setText(builder);
            txtOt8.setText(builder);
        } else {
            txtMe8.setText(msg);
            txtOt8.setText(msg);
        }
    }

    private SpannableString setClickableSpan(final String url) {
        SpannableString span = new SpannableString(url);
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@androidx.annotation.NonNull View view) {
                Intent intent = new Intent(getContext(), WebPageActivity.class);
                intent.putExtra(WebPageActivity.AGM_URL, url);
//                Uri uri = Uri.parse(url);
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                intent.putExtra(Browser.EXTRA_APPLICATION_ID, getContext().getPackageName());
                getContext().startActivity(intent);
            }
        }, 0, url.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.BLUE), 0, url.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }


    public void setFont(Integer size) {
        txtMe1.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        txtOt1.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public interface EventRP {
        void onClick(boolean isInvalid);
    }

    //图片消息
    public void setData4(ImageMessage image, String url, final EventPic eventPic, Integer pg) {
        if (url != null) {
            setData4(image, Uri.parse(url), eventPic, pg);

        }

    }

    public void setData4(final ImageMessage image, final Uri uri, final EventPic eventPic, Integer pg) {
        if (uri != null) {


            final int width = DensityUtil.dip2px(getContext(), 150);
            final int height = DensityUtil.dip2px(getContext(), 180);

            //设定大小
            ViewGroup.LayoutParams lp = viewMeUp.getLayoutParams();
            if (image != null) {
                double mh = image.getHeight();
                double mw = image.getWidth();
                if(mh==0){
                    mh=height;
                }
                if(mw==0){
                    mw=width;
                }

                double cp = 1;
                if (mh > mw) {
                    cp = height / mh;
                } else {
                    cp = width / mw;
                }
                int w = new Double(mw * cp).intValue();
                int h = new Double(mh * cp).intValue();

                imgMe4.setLayoutParams(new FrameLayout.LayoutParams(w, h));

                imgOt4.setLayoutParams(new LinearLayout.LayoutParams(w, h));


                lp.width = w;
                lp.height = h;

            } else {
                lp.width = width;
                lp.height = height;
            }

            viewMeUp.setLayoutParams(lp);


            RequestListener requestListener = new RequestListener() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, final Object model, Target target, boolean isFirstResource) {
                    //加载失败后以静态图加载
                    imgOt4.post(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(getContext()).asBitmap().load(model).into(imgOt4);
                            Glide.with(getContext()).asBitmap().load(model).into(imgMe4);
                        }
                    });


                    return true;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {

                    return false;
                }


            };

            RequestOptions rOptions = new RequestOptions();


            RequestManager in = Glide.with(getContext());

            RequestBuilder rb;
            if (uri.getPath().toLowerCase().endsWith(".gif")) {
                Log.e("gif", "setData4: isgif");
                rb = in.asGif();
                rOptions.priority(Priority.LOW).diskCacheStrategy(DiskCacheStrategy.ALL);
            } else {
                rb = in.asBitmap();
                rOptions.override(width, height)
                        .priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.ALL);
            }


            rb.apply(rOptions).listener(requestListener).load(uri);


            rb.into(imgMe4);
            rb.into(imgOt4);


            //
            if (netState == -1) {
                setImgageProg(0);
            } else {
                setImgageProg(null);
            }


        }
        if (eventPic != null) {

            OnClickListener onk;
            viewMeTouch.setOnClickListener(onk = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    eventPic.onClick(uri.toString());
                }
            });
            viewOtTouch.setOnClickListener(onk);
        }

    }

    public void setImgageProg(Integer pg) {
        if (pg != null && pg != 100) {


            viewMeUp.setVisibility(VISIBLE);
            txtMeUp.setText(pg + "%");


            imgMeErr.setVisibility(GONE);
        } else {
            viewMeUp.setVisibility(GONE);


        }
    }

    public interface EventPic {
        void onClick(String uri);

    }

    //名片消息
    public void setData5(String name, String info, String headUrl, String moreInfo, OnClickListener onk) {
        if (moreInfo != null) {
            txtMe5Bt.setText(moreInfo);
            txtOt5Bt.setText(moreInfo);
        }

        txtMe5Title.setText(name);
        txtMe5Info.setText(info);

        imgMe5.setImageURI(Uri.parse(headUrl));
        txtOt5Title.setText(name);
        txtOt5Info.setText(info);

        imgOt5.setImageURI(Uri.parse(headUrl));

        viewMeTouch.setOnClickListener(onk);
        viewOtTouch.setOnClickListener(onk);
    }


    public ChatItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View viewRoot = inflater.inflate(R.layout.view_chat_item, this);
        findViews(viewRoot);
        initEvent();
    }

    private int netState;

    public void setErr(int state) {
        this.netState = state;
        switch (state) {
            case 0://正常
                imgMeErr.clearAnimation();
                imgMeErr.setVisibility(INVISIBLE);
                break;
            case 1://失败
                imgMeErr.clearAnimation();
                imgMeErr.setVisibility(VISIBLE);
                imgMeErr.setImageResource(R.mipmap.ic_net_err);

                break;
            case 2://等待,发送中
                imgMeErr.setImageResource(R.mipmap.ic_net_load);
                Animation rotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_circle_rotate);
                imgMeErr.startAnimation(rotateAnimation);
                imgMeErr.setVisibility(VISIBLE);


                break;
            case -1://图片待发送
                imgMeErr.clearAnimation();
                imgMeErr.setVisibility(INVISIBLE);
                break;
            default: // 其他状态如-1:待发送

                break;

        }

    }

    public void setOnErr(OnClickListener onk) {
        imgMeErr.setOnClickListener(onk);
    }

    public void setOnHead(OnClickListener onk) {
        imgMeHead.setOnClickListener(onk);
        imgOtHead.setOnClickListener(onk);
    }

    public void selectTextBubble(boolean flag) {
        viewOtTouch.setSelected(flag);
        viewMeTouch.setSelected(flag);

        viewOt1.setSelected(flag);
        viewMe1.setSelected(flag);

        viewOt2.setSelected(flag);
        viewMe2.setSelected(flag);

        viewOt3.setSelected(flag);
        viewMe3.setSelected(flag);

        viewOt4.setSelected(flag);
        viewMe4.setSelected(flag);

        viewOt5.setSelected(flag);
        viewMe5.setSelected(flag);

        viewOt6.setSelected(flag);
        viewMe6.setSelected(flag);

        viewOt7.setSelected(flag);
        viewMe7.setSelected(flag);

        viewOt8.setSelected(flag);
        viewMe8.setSelected(flag);

    }


}
