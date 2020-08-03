package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.NewVersionBean;
import com.yanlong.im.user.bean.VersionBean;
import com.yanlong.im.utils.update.UpdateAppDialog;
import com.yanlong.im.utils.update.UpdateManage;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.FileUtils;
import net.cb.cb.library.utils.InstallAppUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.VersionUtil;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.WebPageActivity;

import retrofit2.Call;
import retrofit2.Response;

public class SelectLoginActivity extends AppActivity implements View.OnClickListener {

    private Button mBtnLogin;
    private Button mBtnRegister;
    private ImageView mIvWechat;
    private TextView mTvMattersNeedAttention;
    private InstallAppUtil installAppUtil;
    private UpdateAppDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_select_login);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue_title));

        initView();
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        taskNewVersion();
    }

    private void initView(){
        mBtnLogin =  findViewById(R.id.btn_login);
        mBtnRegister =  findViewById(R.id.btn_register);
        mIvWechat =  findViewById(R.id.iv_wechat);

        mTvMattersNeedAttention = findViewById(R.id.tv_matters_need_attention);
        initTvMNA();
    }


    private void initEvent(){
        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mIvWechat.setOnClickListener(this);

        mTvMattersNeedAttention.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                Intent loginIntent = new Intent(this,PasswordLoginActivity.class);
                startActivity(loginIntent);
                break;
            case R.id.btn_register:
                Intent registerIntent = new Intent(this,RegisterActivity.class);
                startActivity(registerIntent);
                break;
            case R.id.iv_wechat:

                break;
        }
    }


    private void initTvMNA() {
        final SpannableStringBuilder style = new SpannableStringBuilder();
//        style.append("点击\"注册\"即表示已阅读并同意《用户使用协议》和《隐私权政策》");
        style.append("《用户使用协议》 《隐私权政策》");
        ClickableSpan clickProtocol = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(SelectLoginActivity.this, WebPageActivity.class);
                intent.putExtra(WebPageActivity.AGM_URL,"https://changxin.zhixun6.com/yhxy.html");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor( R.color.blue_600));
                ds.setUnderlineText(false);
            }
        };
        style.setSpan(clickProtocol, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickPolicy = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(SelectLoginActivity.this,WebPageActivity.class);
                intent.putExtra(WebPageActivity.AGM_URL,"https://changxin.zhixun6.com/yszc.html");
                startActivity(intent);

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor( R.color.blue_600));
                ds.setUnderlineText(false);
            }
        };
        style.setSpan(clickPolicy, 9, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mTvMattersNeedAttention.setText(style);
        mTvMattersNeedAttention.setMovementMethod(LinkMovementMethod.getInstance());
    }


    /**
     * 发请求---判断是否需要更新
     */
    private void taskNewVersion() {
        new UserAction().getNewVersion(StringUtil.getChannelName(context), new CallBack<ReturnBean<NewVersionBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<NewVersionBean>> call, Response<ReturnBean<NewVersionBean>> response) {
                if (response.body() == null || response.body().getData() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    NewVersionBean bean = response.body().getData();
                    UpdateManage updateManage = new UpdateManage(context, SelectLoginActivity.this);
                    //判断是否已经下载过新版本的安装包，有则直接安装，无需再重复下载
                    if(!TextUtils.isEmpty(bean.getVersion())){
                        if(FileUtils.fileIsExist(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/changxin_"+bean.getVersion()+".apk") ){
                            if(dialog==null){
                                dialog = new UpdateAppDialog();
                                dialog.init(SelectLoginActivity.this, bean.getVersion(), "", new UpdateAppDialog.Event() {
                                    @Override
                                    public void onON() {

                                    }

                                    @Override
                                    public void onUpdate() {

                                    }

                                    @Override
                                    public void onInstall() {
                                        if(installAppUtil==null){
                                            installAppUtil = new InstallAppUtil();
                                        }
                                        installAppUtil.install(SelectLoginActivity.this, getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)+"/changxin_"+bean.getVersion()+".apk");
                                    }
                                });
                                dialog.downloadComplete();
                                if (VersionUtil.isBigVersion(context, bean.getVersion()) || (!TextUtils.isEmpty(bean.getMinEscapeVersion()) && VersionUtil.isLowerVersion(context, bean.getMinEscapeVersion()))){
                                    dialog.showCancle(false);
                                }else {
                                    dialog.showCancle(true);
                                }
                            }
                            dialog.show();
                        }else {
                            //强制更新
                            if (bean.getForceUpdate() != 0) {
                                //有最低不需要强制升级版本
                                if (!TextUtils.isEmpty(bean.getMinEscapeVersion()) && VersionUtil.isLowerVersion(context, bean.getMinEscapeVersion())) {
                                    updateManage.uploadApp(bean.getVersion(), bean.getContent(), bean.getUrl(), true, true);
                                } else {
                                    updateManage.uploadApp(bean.getVersion(), bean.getContent(), bean.getUrl(), false, true);
                                }
                            } else {
                                //缓存最新版本
                                SharedPreferencesUtil preferencesUtil = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.NEW_VESRSION);
                                VersionBean versionBean = new VersionBean();
                                versionBean.setVersion(bean.getVersion());
                                preferencesUtil.save2Json(versionBean);
                                //非强制更新（新增一层判断：如果是大版本，则需要直接改为强制更新）
                                if (VersionUtil.isBigVersion(context, bean.getVersion()) || (!TextUtils.isEmpty(bean.getMinEscapeVersion()) && VersionUtil.isLowerVersion(context, bean.getMinEscapeVersion()))) {
                                    updateManage.uploadApp(bean.getVersion(), bean.getContent(), bean.getUrl(), true, true);
                                } else {
                                    updateManage.uploadApp(bean.getVersion(), bean.getContent(), bean.getUrl(), false, true);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
