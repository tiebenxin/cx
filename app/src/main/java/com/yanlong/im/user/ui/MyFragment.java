package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.pay.ui.LooseChangeActivity;

import net.cb.cb.library.utils.ToastUtil;

/***
 * 我
 */
public class MyFragment extends Fragment {
    private View rootView;
    private LinearLayout viewHead;
    private com.facebook.drawee.view.SimpleDraweeView imgHead;
    private TextView txtName;
    private LinearLayout viewQr;
    private LinearLayout viewMoney;
    private LinearLayout viewWallet;
    private LinearLayout viewCollection;
    private LinearLayout viewSetting;


    //自动寻找控件
    private void findViews(View rootView) {
        viewHead = rootView.findViewById(R.id.view_head);
        imgHead = rootView.findViewById(R.id.img_head);
        txtName = rootView.findViewById(R.id.txt_name);
        viewQr = rootView.findViewById(R.id.view_qr);
        viewMoney = rootView.findViewById(R.id.view_money);
        viewWallet = rootView.findViewById(R.id.view_wallet);
        viewCollection = rootView.findViewById(R.id.view_collection);
        viewSetting = rootView.findViewById(R.id.view_setting);
    }


    //自动生成的控件事件
    private void initEvent() {
        imgHead.setImageURI("https://gss0.bdstatic.com/-4o3dSag_xI4khGkpoWK1HF6hhy/baike/s%3D500/sign=6346256a71310a55c024def487444387/7af40ad162d9f2d3c35c9b76a1ec8a136227ccde.jpg");
        txtName.setText("懒洋洋");
        viewHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageHeadActivity.class);
                startActivity(intent);
            }
        });
        viewSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CommonActivity.class);
                startActivity(intent);
            }
        });
        viewQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent arCodeIntent = new Intent(getActivity(), MyselfQRCodeActivity.class);
                startActivity(arCodeIntent);
            }
        });
        viewMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moneyIntent = new Intent(getActivity(), LooseChangeActivity.class);
                startActivity(moneyIntent);
            }
        });
    }

    public MyFragment() {
        // Required empty public constructor
    }


    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
    /*    args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
/*            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgm_my, null);
        ViewGroup.LayoutParams layparm = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(layparm);
        findViews(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initEvent();
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }
  /*
   private MainActivity getActivityMe() {
        return (MainActivity) getActivity();
    }

    */


}
