package com.yanlong.im.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yanlong.im.R;
import com.yanlong.im.utils.QRCodeManage;

import net.cb.cb.library.bean.QRCodeBean;
import net.cb.cb.library.utils.LogUtil;


public class BtnFragment extends Fragment {
    private View rootView;

    public BtnFragment() {
        // Required empty public constructor
    }


    public static BtnFragment newInstance() {
        BtnFragment fragment = new BtnFragment();
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
        rootView = inflater.inflate(R.layout.fgm_test_btn, null);
        ViewGroup.LayoutParams layparm = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeBean bean = QRCodeManage.getQRCodeBean(getActivity(),"YLIM://ADDGROUP?id=xxx&kk=10111");
                LogUtil.getLog().e("test","head"+bean.getHead()+"-----"+"function"+bean.getFunction()+"-----"+bean.getParameterValue(QRCodeManage.ID));
                LogUtil.getLog().e("test",QRCodeManage.getQRcodeStr(bean));
            }
        });

        rootView.setLayoutParams(layparm);
        // findViews(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

      //  initEvent();
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
