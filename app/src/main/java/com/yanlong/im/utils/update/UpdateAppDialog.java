package com.yanlong.im.utils.update;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.yanlong.im.R;

import net.cb.cb.library.utils.ClickFilter;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.ToastUtil;

public class UpdateAppDialog {

    private AlertDialog alertDialog;
    private Event event;

    private Context context;
    private TextView txtAlertMsg;
    private TextView tvFinishNotice;//下载完成文字提示
    private TextView tvVersionNumber;
    private Button btnCl;
    private Button btnUpdate;
    private ProgressBar mProgressNum;
    private Button btnInstall;
    private TextView tvUpdatePercent;//进度条上方百分比图标
    private int lastProgressValue = 0;

    //自动寻找控件
    private void findViews(View rootview) {
        txtAlertMsg = rootview.findViewById(R.id.txt_alert_msg);
        btnCl = rootview.findViewById(R.id.btn_cl);
        btnUpdate = rootview.findViewById(R.id.btn_update);
        mProgressNum =  rootview.findViewById(R.id.progress_num);
        btnInstall = rootview.findViewById(R.id.btn_install);
        tvVersionNumber = rootview.findViewById(R.id.tv_version_number);
        tvUpdatePercent = rootview.findViewById(R.id.tv_update_percent);
        tvFinishNotice = rootview.findViewById(R.id.tv_finish_notice);
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
        ClickFilter.onClick(btnUpdate, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtil.isNetworkConnected()) {
                    event.onUpdate();
                } else {
                    ToastUtil.show(context, "请检查网络连接是否正常");
                }
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
        txtAlertMsg.setVisibility(View.VISIBLE);
        tvFinishNotice.setVisibility(View.GONE);
        mProgressNum.setVisibility(View.VISIBLE);
        tvUpdatePercent.setVisibility(View.VISIBLE);
        btnUpdate.setVisibility(View.GONE);
        btnUpdate.setClickable(false);
        btnCl.setBackgroundResource(R.drawable.shape_18radius_0fa6ea);
        btnCl.setTextColor(context.getResources().getColor(R.color.white));
    }


    public void updateStop(){
        mProgressNum.setVisibility(View.GONE);
        tvUpdatePercent.setVisibility(View.GONE);
        btnUpdate.setVisibility(View.VISIBLE);
        btnUpdate.setClickable(true);
        btnUpdate.setText("重新下载");

    }


    public void downloadComplete(){
        btnCl.setText("取消");
        btnCl.setBackgroundResource(R.color.white);
        btnCl.setTextColor(context.getResources().getColor(R.color.c_969696));
        txtAlertMsg.setVisibility(View.GONE);
        tvFinishNotice.setVisibility(View.VISIBLE);
        mProgressNum.setVisibility(View.GONE);
        tvUpdatePercent.setVisibility(View.GONE);
        btnUpdate.setVisibility(View.GONE);
        btnInstall.setVisibility(View.VISIBLE);

    }


    public void updateProgress(int progress){
        //加限制条件，避免进度条偶现性错乱抖动
        if(progress > lastProgressValue){
            lastProgressValue = progress;
            setPos(progress);
            mProgressNum.setProgress(progress);
            tvUpdatePercent.setText(progress+"%");
        }
    }

    //是否显示取消按钮 (强制更新不显示)
    public void showCancle(boolean show){
        if(show){
            btnCl.setVisibility(View.VISIBLE);
        }else {
            btnCl.setVisibility(View.GONE);
        }

    }


    public void show() {
        //判断活动是否仍然存活 TODO bugly #315427
        if(context!=null && !((Activity)context).isFinishing()){
            alertDialog.show();
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
            alertDialog.getWindow().setGravity(Gravity.CENTER);
            WindowManager manager = alertDialog.getWindow().getWindowManager();
            DisplayMetrics metrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(metrics);
            //设置宽高，高度自适应，宽度屏幕0.65
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.width = (int) (metrics.widthPixels*0.65);
            alertDialog.getWindow().setAttributes(lp);
        }
    }


    /**
     * 设置顶部进度图标显示在对应的位置
     */
    public void setPos(int progress) {
        int w = DensityUtil.dip2px(context, 200);
        LogUtil.getLog().i("w=====", "" + progress);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvUpdatePercent.getLayoutParams();
//        int pro = mProgressNum.getProgress();//进度是不断变化的，原来是1此时可能是2，又去从进度条获取则进度会忽大忽小
        if(progress>=0 && progress<=100){
            //左侧距离： 进度滑动距离 + 左侧margin距离 (由于下标稍远，减5像素拼凑效果)
            params.leftMargin = (w * progress / 100) + DensityUtil.dip2px(context,15)/2 - 5;
            tvUpdatePercent.setLayoutParams(params);
        }
    }


    public interface Event {
        void onON();

        void onUpdate();

        void onInstall();
    }


}
