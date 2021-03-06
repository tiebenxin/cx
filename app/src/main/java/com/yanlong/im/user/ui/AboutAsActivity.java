package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.NewVersionBean;
import com.yanlong.im.user.bean.VersionBean;
import com.yanlong.im.utils.update.UpdateManage;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.constant.Route;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.VersionUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.WebPageActivity;

import retrofit2.Call;
import retrofit2.Response;

public class AboutAsActivity extends AppActivity {

    private HeadView mHeadView;
    private ImageView mIvLogo;
    private TextView mTvVersionNumber;
    private LinearLayout mLlCheckVersions;
    private LinearLayout mLlService;
    private TextView tvNewVersions;
    private TextView tvBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_as);
        initView();
        initEvent();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        mIvLogo = findViewById(R.id.iv_logo);
        mTvVersionNumber = findViewById(R.id.tv_version_number);
        mLlCheckVersions = findViewById(R.id.ll_check_versions);
        mLlService = findViewById(R.id.ll_service);
        mTvVersionNumber.setText("常信     " + VersionUtil.getVerName(this));
        tvNewVersions = findViewById(R.id.tv_new_versions);
        tvBottom = findViewById(R.id.tv_matters_need_attention);
        initTvMNA();
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.NEW_VESRSION);
        VersionBean bean = sharedPreferencesUtil.get4Json(VersionBean.class);
        if (bean != null && !TextUtils.isEmpty(bean.getVersion())) {
            if (new UpdateManage(context, AboutAsActivity.this).check(bean.getVersion())) {
                tvNewVersions.setVisibility(View.VISIBLE);
            } else {
                tvNewVersions.setVisibility(View.GONE);
            }
        }
    }


    private void initEvent() {
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

        mLlCheckVersions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskNewVersion();
            }
        });

        mLlService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go(FeedbackActivity.class);
            }
        });
    }


    private void taskNewVersion() {
        new UserAction().getNewVersion(StringUtil.getChannelName(context), new CallBack<ReturnBean<NewVersionBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<NewVersionBean>> call, Response<ReturnBean<NewVersionBean>> response) {
                if (response.body() == null || response.body().getData() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    NewVersionBean bean = response.body().getData();
                    if (!new UpdateManage(context, AboutAsActivity.this).check(bean.getVersion())) {
                        ToastUtil.show(context, "已经是最新版本");
                        return;
                    }
                    UpdateManage updateManage = new UpdateManage(context, AboutAsActivity.this);
                    if (!TextUtils.isEmpty(bean.getMinEscapeVersion()) && VersionUtil.isLowerVersion(context, bean.getMinEscapeVersion())) {
                        updateManage.uploadApp(bean.getVersion(), bean.getContent(), bean.getUrl(), true);
                    } else {
                        updateManage.uploadApp(bean.getVersion(), bean.getContent(), bean.getUrl(), false);
                    }
                }
            }
        });
    }

    private void initTvMNA() {
        final SpannableStringBuilder style = new SpannableStringBuilder();
//        style.append("点击\"注册\"即表示已阅读并同意《用户使用协议》和《隐私权政策》");
        style.append("《用户使用协议》 《隐私权政策》");
        ClickableSpan clickProtocol = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(AboutAsActivity.this, WebPageActivity.class);
                intent.putExtra(WebPageActivity.AGM_URL, "https://changxin.zhixun6.com/yhxy.html");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue_600));
                ds.setUnderlineText(false);
            }
        };
        style.setSpan(clickProtocol, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickPolicy = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(AboutAsActivity.this, WebPageActivity.class);
                intent.putExtra(WebPageActivity.AGM_URL, Route.URL_PRIVACY);
                startActivity(intent);

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue_600));
                ds.setUnderlineText(false);
            }
        };
        style.setSpan(clickPolicy, 9, 16, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvBottom.setText(style);
        tvBottom.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
