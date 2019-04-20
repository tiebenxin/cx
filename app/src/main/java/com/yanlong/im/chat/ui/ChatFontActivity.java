package com.yanlong.im.chat.ui;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.yanlong.im.R;

import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.FontSizeView;

/***
 * 聊天字体设置
 */
public class ChatFontActivity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private LinearLayout viewMe;
    private LinearLayout viewMe1;
    private android.support.v7.widget.AppCompatTextView txtMe1;
    private com.facebook.drawee.view.SimpleDraweeView imgMeHead;
    private LinearLayout viewOt;
    private com.facebook.drawee.view.SimpleDraweeView imgOtHead;
    private LinearLayout viewOt1;
    private android.support.v7.widget.AppCompatTextView txtOt1;
    private FontSizeView seekBar;


    //自动寻找控件
    private void findViews() {
        headView = findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        viewMe = findViewById(R.id.view_me);
        viewMe1 = findViewById(R.id.view_me_1);
        txtMe1 = findViewById(R.id.txt_me_1);
        imgMeHead = findViewById(R.id.img_me_head);
        viewOt = findViewById(R.id.view_ot);
        imgOtHead = findViewById(R.id.img_ot_head);
        viewOt1 = findViewById(R.id.view_ot_1);
        txtOt1 = findViewById(R.id.txt_ot_1);
        seekBar = findViewById(R.id.seekBar);
    }


    //自动生成的控件事件
    private void initEvent() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

        seekBar.setChangeCallbackListener(new FontSizeView.OnChangeCallbackListener() {
            @Override
            public void onChangeListener(int position) {
                SharedPreferencesUtil util = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT);
                switch (position){
                    case 0:
                        setTextSize(13);
                        util.save2Json(13);
                        break;
                    case 1:
                        setTextSize(14);
                        util.save2Json(14);
                        break;
                    case 2:
                        setTextSize(15);
                        util.save2Json(15);
                        break;
                    case 3:
                        setTextSize(16);
                        util.save2Json(16);
                        break;
                    case 4:
                        setTextSize(17);
                        util.save2Json(17);
                        break;
                }
                ToastUtil.show(ChatFontActivity.this, position + "");
            }
        });

    }


    private void setTextSize(int size){
        txtMe1.setTextSize(size);
        txtOt1.setTextSize(size);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_font);
        findViews();
        initEvent();
    }
}
