package com.yanlong.im.utils.update;


import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.utils.DensityUtil;

public class UpdateAppDialog {

    private AlertDialog alertDialog;
    private Event event;

    private Context context;
    private TextView txtAlertTitle;
    private TextView txtAlertMsg;
    private LinearLayout viewNo;
    private Button btnCl;
    private Button btnUpdate;
    private ProgressBar mProgressNum;
    private String title;

    //自动寻找控件
    private void findViews(View rootview) {
        txtAlertTitle = rootview.findViewById(R.id.txt_alert_title);
        txtAlertMsg = rootview.findViewById(R.id.txt_alert_msg);
        viewNo = rootview.findViewById(R.id.view_no);
        btnCl = rootview.findViewById(R.id.btn_cl);
        btnUpdate = rootview.findViewById(R.id.btn_update);
        mProgressNum =  rootview.findViewById(R.id.progress_num);
    }


    //自动生成的控件事件
    private void initEvent(String title, String msg) {
        this.title = title;
        txtAlertTitle.setText(title);
        txtAlertMsg.setText(msg);
        btnCl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event.onON();
                dismiss();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event.onUpdate();
            }
        });

    }


    public void dismiss() {
        alertDialog.dismiss();
    }

    public void init(Activity activity, String title, String msg, Event e) {
        event = e;
        this.context = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);

        View rootView = View.inflate(context, R.layout.view_alert_update, null);

        alertDialog.setView(rootView);
        findViews(rootView);
        initEvent(title, msg);

    }


    public void updateStart(){
        txtAlertTitle.setText("开始下载");
        txtAlertMsg.setVisibility(View.GONE);
        mProgressNum.setVisibility(View.VISIBLE);
        btnUpdate.setClickable(false);
    }


    public void updateStop(){
        txtAlertTitle.setText(title+"");
        txtAlertMsg.setVisibility(View.VISIBLE);
        mProgressNum.setVisibility(View.GONE);
        btnUpdate.setClickable(true);
    }


    public void updateProgress(int progress){
        mProgressNum.setProgress(progress);
    }



    public void enforcementUpate() {
        viewNo.setVisibility(View.GONE);
    }


    public void show() {
        alertDialog.show();
        WindowManager.LayoutParams p = alertDialog.getWindow().getAttributes();
        p.width = DensityUtil.dip2px(context, 300);
        alertDialog.getWindow().setAttributes(p);
    }


    public interface Event {
        void onON();

        void onUpdate();
    }


}
