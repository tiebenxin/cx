package com.yanlong.im.user.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hm.cxpay.bean.UserBean;
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.hm.cxpay.global.PayEnvironment;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.BindPhoneNumActivity;
import com.hm.cxpay.ui.LooseChangeActivity;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.eventbus.EventRefreshUser;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.pay.action.PayAction;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.EventCheckVersionBean;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.bean.VersionBean;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.QRCodeManage;
import com.yanlong.im.utils.UserUtil;
import com.yanlong.im.utils.update.UpdateManage;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.manager.Constants;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ClickFilter;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.NetUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.zxing.activity.CaptureActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/***
 * 我
 */
public class MyFragment extends Fragment {
    private View rootView;
    private LinearLayout viewHead;
    private ImageView imgHead;
    private TextView txtName;
    private LinearLayout viewQr;
    private LinearLayout viewMoney;
    private LinearLayout viewWallet;
    private LinearLayout viewCollection;
    private LinearLayout viewSetting;
    private TextView mTvInfo;
    private LinearLayout mViewScanQrcode;
    private LinearLayout mViewHelp;
    private TextView tvNewVersions;
    private LinearLayout viewService;
    private Context context;
    private CommonSelectDialog.Builder builder;
    private CommonSelectDialog dialogOne;//通用提示选择弹框：实名认证提示
    private CommonSelectDialog dialogTwo;//通用提示选择弹框：绑定手机号

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
        tvNewVersions = rootView.findViewById(R.id.tv_new_versions);
        viewService = rootView.findViewById(R.id.view_service);

        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.NEW_VESRSION);
        VersionBean bean = sharedPreferencesUtil.get4Json(VersionBean.class);
        if (bean != null && !TextUtils.isEmpty(bean.getVersion())) {
            if (new UpdateManage(getContext(), getActivity()).check(bean.getVersion())) {
                tvNewVersions.setVisibility(View.VISIBLE);
            } else {
                tvNewVersions.setVisibility(View.GONE);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void checkVersion(EventCheckVersionBean eventCheckVersionBean) {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.NEW_VESRSION);
        VersionBean bean = sharedPreferencesUtil.get4Json(VersionBean.class);
        if (bean != null && !TextUtils.isEmpty(bean.getVersion())) {
            if (new UpdateManage(getContext(), getActivity()).check(bean.getVersion())) {
                tvNewVersions.setVisibility(View.VISIBLE);
            } else {
                tvNewVersions.setVisibility(View.GONE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshUser(EventRefreshUser event) {
        LogUtil.getLog().d("a=", MyFragment.class.getSimpleName() + "刷新用户信息");
        initData((com.yanlong.im.user.bean.UserBean) UserAction.getMyInfo());
    }


    //自动生成的控件事件
    private void initEvent() {
        viewMoney.setVisibility(View.VISIBLE);//关闭零钱
        viewWallet.setVisibility(View.GONE);//关闭云红包

        builder = new CommonSelectDialog.Builder(context);
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
        //涉及网络请求均加入防重复点击
        ClickFilter.onClick(viewMoney, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PayEnvironment.getInstance().getUser() != null) {
                    checkUserStatus(PayEnvironment.getInstance().getUser());
                } else {
                    httpGetUserInfo();
                }
            }
        });
        mViewScanQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                    ToastUtil.show(getResources().getString(R.string.user_disable_message));
                    return;
                }
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
        //云红包
        viewWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                taskWallet();
            }
        });

        //常信客服
        viewService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCxService();
            }
        });
        // 收藏
        viewCollection.setOnClickListener(o -> {
            if (ViewUtils.isFastDoubleClick()) {
                return;
            }
            Intent intent = new Intent(getActivity(), CollectionActivity.class);
            intent.putExtra("from", CollectionActivity.FROM_DEFAULT);
            startActivity(intent);
        });
    }

    private void checkCxService() {
        UserInfo userInfo = new UserDao().findUserInfo(Constants.CX888_UID);
        if (userInfo != null && userInfo.getuType() == ChatEnum.EUserType.FRIEND) {
            toChatActivity();
        } else {
            if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
                ToastUtil.show(getResources().getString(R.string.user_disable_message));
                return;
            }
            taskAddFriend(Constants.CX888_UID);
        }
    }


    private void initData(com.yanlong.im.user.bean.UserBean userInfo) {
        if (userInfo != null) {
            if (imgHead != null) {
                Glide.with(this).load(userInfo.getHead() + "").apply(GlideOptionsUtil.headImageOptions()).into(imgHead);
            }
            if (txtName != null) {
                txtName.setText(userInfo.getName());
            }
            if (mTvInfo != null) {
                mTvInfo.setText("常信号: " + userInfo.getImid() + "");
            }
        } else {
//            LogUtil.getLog().d("a=", MyFragment.class.getSimpleName() + "刷新用户信息失败");
//            taskGetUserInfo4Id();

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
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgm_my, null);
        ViewGroup.LayoutParams layparm = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(layparm);
        findViews(rootView);
        context = getActivity();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
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
        initData((com.yanlong.im.user.bean.UserBean) UserAction.getMyInfo());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
            QRCodeManage.goToPage(getContext(), scanResult);

//            QRCodeBean bean = QRCodeManage.getQRCodeBean(getActivity(), scanResult);
//            QRCodeManage.goToActivity(getActivity(), bean);
            //将扫描出的信息显示出来
        }
    }

    private PayAction payAction = new PayAction();

    //钱包
//    private void taskWallet() {
//        IUser info = UserAction.getMyInfo();
//        if (info == null) {
//            return;
//        }
//        if (info != null && info.getLockCloudRedEnvelope() == 1) {//红包功能被锁定
//            ToastUtil.show(getActivity(), "您的云红包功能已暂停使用，如有疑问请咨询官方客服号");
//            return;
//        }
//        payAction.SignatureBean(new CallBack<ReturnBean<SignatureBean>>() {
//            @Override
//            public void onResponse(Call<ReturnBean<SignatureBean>> call, Response<ReturnBean<SignatureBean>> response) {
//                if (response.body() == null)
//                    return;
//                if (response.body().isOk()) {
//                    String token = response.body().getData().getSign();
//                    if (getActivity() != null && !getActivity().isFinishing()) {
//                        JrmfWalletClient.intentWallet(getActivity(), "" + UserAction.getMyId(), token, info.getName(), info.getHead());
//                    }
//                }
//            }
//        });
//    }


    private void taskAddFriend(Long id) {
        if (NetUtil.isNetworkConnected()) {
            new UserAction().friendApply(id, "", null, new CallBack<ReturnBean>() {
                @Override
                public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                    if (response.body() == null) {
                        if (getActivity() != null && !getActivity().isFinishing()) {
                            ToastUtil.show(getActivity(), "请检查当前网络");
                        }
                        return;
                    }
                    if (response.body().isOk()) {
                        toChatActivity();
                    } else {
                        if (getActivity() != null && !getActivity().isFinishing()) {
                            ToastUtil.show(getActivity(), "请检查当前网络");
                        }
                    }
                }
            });
        } else {
            ToastUtil.show(getActivity(), "请检查当前网络");
        }
    }

    private void toChatActivity() {
        //CX888 uid=100121
        // TODO 修复 #35206 java.lang.NullPointerException
        if (getActivity() != null && !getActivity().isFinishing()) {
            startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(ChatActivity.AGM_TOUID, Constants.CX888_UID));
        }
    }

    /**
     * 实名认证提示弹框
     */
    private void showIdentifyDialog() {
        dialogOne = builder.setTitle("根据国家法律法规要求，你需要进行\n身份认证后，才能继续使用该功能。")
                .setLeftText("取消")
                .setRightText("去认证")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消
                        dialogOne.dismiss();
                    }
                })
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //去认证(需要先同意协议)
                        dialogOne.dismiss();
                        startActivity(new Intent(context, ServiceAgreementActivity.class));
                    }
                })
                .build();
        dialogOne.show();
    }

    /**
     * 请求->获取用户信息
     */
    private void httpGetUserInfo() {
        IUser info = UserAction.getMyInfo();
        if (info == null) {
            return;
        }
        PayHttpUtils.getInstance().getUserInfo(info.getUid())
                .compose(RxSchedulers.<BaseResponse<UserBean>>compose())
                .compose(RxSchedulers.<BaseResponse<UserBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<UserBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<UserBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            UserBean userBean = null;
                            if (baseResponse.getData() != null) {
                                userBean = baseResponse.getData();
                            } else {
                                userBean = new UserBean();
                            }
                            PayEnvironment.getInstance().setUser(userBean);
                            checkUserStatus(userBean);
                        } else {
                            ToastUtil.show(context, baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse<UserBean> baseResponse) {
                        ToastUtil.show(context, baseResponse.getMessage());
                    }
                });
    }

    /**
     * 改为两层判断：是否同意隐私协议->是否实名认证->是否绑定手机号
     */
    private void checkUserStatus(UserBean userBean) {
        //1 已实名认证
        if (userBean.getRealNameStat() == 1) {
            //1-1 已完成绑定手机号
            startActivity(new Intent(getActivity(), LooseChangeActivity.class));
//            if (userBean.getPhoneBindStat() == 1) {
//                startActivity(new Intent(getActivity(), LooseChangeActivity.class));
//            } else {
//                //1-2 未完成绑定手机号
//                showBindPhoneNumDialog();
//            }
        } else {
            //2 未实名认证->分三步走流程(1 同意->2 实名认证->3 绑定手机号)
            showIdentifyDialog();
        }
    }

    /**
     * 是否绑定手机号弹框
     */
    private void showBindPhoneNumDialog() {
        dialogTwo = builder.setTitle("您还没有绑定手机号码\n请先绑定后再进行操作。")
                .setLeftText("取消")
                .setRightText("去绑定")
                .setLeftOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消
                        dialogTwo.dismiss();
                    }
                })
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //去绑定
                        startActivity(new Intent(context, BindPhoneNumActivity.class));
                        dialogTwo.dismiss();
                    }
                })
                .build();
        dialogTwo.show();
    }

}
