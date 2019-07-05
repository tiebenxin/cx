package com.yanlong.im.user.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jrmf360.walletlib.JrmfWalletClient;
import com.yanlong.im.R;
import com.yanlong.im.pay.action.PayAction;
import com.yanlong.im.pay.bean.SignatureBean;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.utils.QRCodeManage;

import net.cb.cb.library.bean.QRCodeBean;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.zxing.activity.CaptureActivity;

import retrofit2.Call;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
/***
 * 我
 */
public class MyFragment extends Fragment {
    private View rootView;
    private LinearLayout viewHead;
    private SimpleDraweeView imgHead;
    private TextView txtName;
    private LinearLayout viewQr;
    private LinearLayout viewMoney;
    private LinearLayout viewWallet;
    private LinearLayout viewCollection;
    private LinearLayout viewSetting;
    private TextView mTvInfo;
    private LinearLayout mViewScanQrcode;
    private LinearLayout mViewHelp;

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
        mTvInfo = rootView.findViewById(R.id.tv_info);
        mViewScanQrcode = rootView.findViewById(R.id.view_scan_qrcode);
        mViewHelp = rootView.findViewById(R.id.view_help);
    }


    //自动生成的控件事件
    private void initEvent() {
        viewHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyselfInfoActivity.class);
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

            }
        });
        mViewScanQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // 申请权限
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CaptureActivity.REQ_PERM_CAMERA);
                    return;
                }
                // 二维码扫码
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent, CaptureActivity.REQ_QR_CODE);
            }
        });
        mViewHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HelpActivity.class));
            }
        });
        viewWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskWallet();

            }
        });
    }



    private void initData() {
        UserInfo userInfo = UserAction.getMyInfo();
        if (userInfo != null) {
            imgHead.setImageURI(userInfo.getHead() + "");
            txtName.setText(userInfo.getName());
            mTvInfo.setText("常聊号: " + userInfo.getImid() + "");
        }
    }


    public MyFragment() {
        // Required empty public constructor
    }


    public static MyFragment newInstance() {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
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
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
            QRCodeBean bean = QRCodeManage.getQRCodeBean(getActivity(),scanResult);
            QRCodeManage.goToActivity(getActivity(),bean);
            //将扫描出的信息显示出来
        }
    }

    private PayAction payAction=new PayAction();
    //钱包
    private void taskWallet() {
        payAction.SignatureBean(new CallBack<ReturnBean<SignatureBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<SignatureBean>> call, Response<ReturnBean<SignatureBean>> response) {
                if (response.body() == null)
                    return;
                if (response.body().isOk()) {
                    String token = response.body().getData().getSign();
                    UserInfo minfo = UserAction.getMyInfo();
                    JrmfWalletClient.intentWallet(getActivity(), ""+ UserAction.getMyId(), token, minfo.getName(), minfo.getHead());
                }
            }
        });
    }
}
