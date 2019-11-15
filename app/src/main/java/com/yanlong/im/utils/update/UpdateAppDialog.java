package com.yanlong.im.utils.update;


import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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
    private TextView txtAlertMsg;
    private TextView tvVersionNumber;
    private LinearLayout viewNo;
    private Button btnCl;
    private Button btnUpdate;
    private ProgressBar mProgressNum;
    private Button btnInstall;

    //自动寻找控件
    private void findViews(View rootview) {
        txtAlertMsg = rootview.findViewById(R.id.txt_alert_msg);
        viewNo = rootview.findViewById(R.id.view_no);
        btnCl = rootview.findViewById(R.id.btn_cl);
        btnUpdate = rootview.findViewById(R.id.btn_update);
        mProgressNum =  rootview.findViewById(R.id.progress_num);
        btnInstall = rootview.findViewById(R.id.btn_install);
        tvVersionNumber = rootview.findViewById(R.id.tv_version_number);
    }


    //自动生成的控件事件
    private void initEvent(String newVersionNum, String msg) {
        txtAlertMsg.setText(msg);
        //显示绿色版本号
        if(!TextUtils.isEmpty(newVersionNum)){
            tvVersionNumber.setVisibility(View.VISIBLE);
            tvVersionNumber.setText("V"+newVersionNum);
        }else {
            tvVersionNumber.setVisibility(View.INVISIBLE);
        }
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

        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.onInstall();
            }
        });

    }


    public void dismiss() {
        alertDialog.dismiss();
    }

    public void init(Activity activity, String newVersionNum, String msg, Event e) {
        event = e;
        this.context = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        alertDialog = builder.create();
        alertDialog.setCancelable(false);

        View rootView = View.inflate(context, R.layout.view_alert_update, null);

        alertDialog.setView(rootView);
        findViews(rootView);
        initEvent(newVersionNum, msg);

    }


    public void updateStart(){
        mProgressNum.setVisibility(View.VISIBLE);
        btnUpdate.setVisibility(View.GONE);
        btnUpdate.setClickable(false);
        btnCl.setBackgroundResource(R.drawable.shape_18radius_0fa6ea);
        btnCl.setTextColor(context.getResources().getColor(R.color.white));
    }


    public void updateStop(){
        mProgressNum.setVisibility(View.GONE);
        btnUpdate.setClickable(true);
    }


    public void downloadComplete(){
//        txtAlertTitle.setText("下载完成");
        btnCl.setText("取消");
        btnCl.setBackgroundResource(R.color.white);
        btnCl.setTextColor(context.getResources().getColor(R.color.c_969696));
        txtAlertMsg.setText("下载完成，是否安装?");
        txtAlertMsg.setVisibility(View.VISIBLE);
        mProgressNum.setVisibility(View.GONE);
        btnUpdate.setVisibility(View.GONE);
        btnInstall.setVisibility(View.VISIBLE);

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
        p.width = DensityUtil.dip2px(context, 230);
        p.height = DensityUtil.dip2px(context, 363);
        alertDialog.getWindow().setAttributes(p);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    public interface Event {
        void onON();

        void onUpdate();

        void onInstall();
    }


}
