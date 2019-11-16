package com.yanlong.im.chat.ui.view;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.chat.ui.forward.ForwardListAdapter;
import com.yanlong.im.chat.ui.forward.MoreSessionBean;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/***
 * 对话框
 * @author jyj
 * @date 2017/1/5
 */
public class AlertForward {
    private AlertDialog alertDialog;
    private Event event;
    private Context context;
    private ImageView imgHead;
    private TextView txtName;
    private ImageView ivImage;
    private TextView txtMsg;
    private EditText edContent;
    private LinearLayout viewNo;
    private Button btnCl;
    private Button btnOk;
    private RecyclerView recyclerview;



    //自动寻找控件
    private void findViews(View rootView){
        imgHead =  rootView.findViewById(R.id.img_head);
        txtName =  rootView.findViewById(R.id.txt_name);
        ivImage =  rootView.findViewById(R.id.iv_image);
        txtMsg =  rootView.findViewById(R.id.txt_msg);
        edContent =  rootView.findViewById(R.id.ed_content);
        viewNo =  rootView.findViewById(R.id.view_no);
        btnCl =  rootView.findViewById(R.id.btn_cl);
        btnOk =  rootView.findViewById(R.id.btn_ok);
        recyclerview =  rootView.findViewById(R.id.recyclerview);
    }



    //自动生成的控件事件
    private void initEvent(){
        btnCl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }});

        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }});

    }



    //自动生成的控件事件
    private void initEvent(String head,String name,String txt,String imgurl, String y) {

        //imgHead.setImageURI(Uri.parse(head));

        if(MsgForwardActivity.isSingleSelected){
            imgHead.setVisibility(View.VISIBLE);
            txtName.setVisibility(View.VISIBLE);
            recyclerview.setVisibility(View.GONE);

            Glide.with(context).load(head)
                    .apply(GlideOptionsUtil.headImageOptions()).into(imgHead);

            txtName.setText(name);
        }else if(!MsgForwardActivity.isSingleSelected&&MsgForwardActivity.moreSessionBeanList.size()>0){
            imgHead.setVisibility(View.GONE);
            txtName.setVisibility(View.GONE);
            recyclerview.setVisibility(View.VISIBLE);

//            List<MoreSessionBean> moreSessionBeanList= new ArrayList<>();
//            LinearLayoutManager manager = new LinearLayoutManager(context);
//            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
//            recyclerview.setLayoutManager(manager);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 5);
            recyclerview.setLayoutManager(gridLayoutManager);
            ForwardListAdapter forwardListAdapter = new ForwardListAdapter(context, MsgForwardActivity.moreSessionBeanList);
            recyclerview.setAdapter(forwardListAdapter);
        }


        if(StringUtil.isNotNull(txt)){
            txtMsg.setText(txt);
            txtMsg.setVisibility(View.VISIBLE);
        }else{
            txtMsg.setVisibility(View.GONE);
        }

        if(StringUtil.isNotNull(imgurl)){
            Glide.with(context).load(Uri.parse(imgurl)).into(ivImage);

            ivImage.setVisibility(View.VISIBLE);
        }else{
            ivImage.setVisibility(View.GONE);
        }




        btnOk.setText(y);
        btnCl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event.onON();
                dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                event.onYes(edContent.getText().toString());
                dismiss();
            }
        });


    }


    public void dismiss() {
        alertDialog.dismiss();
    }

    public void init(Activity activity,String head,String name,String txt,String imgurl, String y, Event e) {
        event = e;
        this.context = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        alertDialog = builder.create();
        View rootView = View.inflate(context, R.layout.view_alert_forward, null);
        alertDialog.setView(rootView);
        findViews(rootView);
        initEvent(head,name,txt,imgurl, y);
    }


    public void setContent(String content){
        edContent.setText(content);
    }


    public void setEdHintOrSize(String hint,int size){
        if(!TextUtils.isEmpty(hint)){
            edContent.setHint(hint);
        }
        edContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(size)});
    }


    public void show() {
        alertDialog.show();
        WindowManager.LayoutParams p = alertDialog.getWindow().getAttributes();
        // p.height = DensityUtil.dip2px(activity, 226);
        p.width = DensityUtil.dip2px(context, 300);
        alertDialog.getWindow().setAttributes(p);
    }


    public interface Event {
        void onON();

        void onYes(String content);
    }


}
