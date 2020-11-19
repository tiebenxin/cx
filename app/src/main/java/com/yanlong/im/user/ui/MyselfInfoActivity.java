package com.yanlong.im.user.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.utils.DateUtil;
import com.yanlong.im.R;
import com.yanlong.im.chat.eventbus.EventRefreshUser;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.EventMyUserInfo;
import com.yanlong.im.user.bean.IUser;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.ui.register.RegisterDetailActivity;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.utils.UserUtil;

import net.cb.cb.library.CoreEnum;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.dialog.DialogLocationSelector;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

public class MyselfInfoActivity extends AppActivity implements View.OnClickListener {
    private static final int NICENAME = 1000;
    private static final int PRODUCT = 2000;
    private static final int SEX = 3000;
    private static final int IMAGE_HEAD = 4000;
    private static final int IDENTITY = 5000;

    private ImageView mImgHead;
    private LinearLayout layoutChangePhoneNum;//更换手机号
    private TextView mTvPhone;
    private LinearLayout mViewNickname;
    private TextView mTvNickname;
    private LinearLayout mViewProductNumber;
    private TextView mTvProductNumber;
    private LinearLayout mViewSex;
    private TextView mTvSex;
    private LinearLayout mViewIdentity;
    private TextView mTvIdentity;
    private LinearLayout mViewHead;
    private HeadView mHeadView;
    private IUser userInfo;
    private UserAction userAction;
    private ImageView mIvProductNumber;
    private int sex;
    private String imageHead;
    private String imid;
    private String nickName;
    private String oldImid;
    private int authStat;
    private View viewBirthday;
    private TextView tvBirthday;
    private View viewLocation;
    private TextView tvLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_info);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initView() {
        mImgHead = findViewById(R.id.img_head);
        layoutChangePhoneNum = findViewById(R.id.layout_change_phone_num);
        mViewHead = findViewById(R.id.view_head);
        mTvPhone = findViewById(R.id.tv_phone);
        mViewNickname = findViewById(R.id.view_nickname);
        mTvNickname = findViewById(R.id.tv_nickname);
        mViewProductNumber = findViewById(R.id.view_product_number);
        mTvProductNumber = findViewById(R.id.tv_product_number);
        mViewSex = findViewById(R.id.view_sex);
        mTvSex = findViewById(R.id.tv_sex);
        mViewIdentity = findViewById(R.id.view_identity);
        mTvIdentity = findViewById(R.id.tv_identity);
        mHeadView = findViewById(R.id.headView);
        mIvProductNumber = findViewById(R.id.iv_product_number);
        viewBirthday = findViewById(R.id.view_birthday);
        tvBirthday = findViewById(R.id.tv_birthday);
        viewLocation = findViewById(R.id.view_location);
        tvLocation = findViewById(R.id.tv_location);
    }


    private void initEvent() {
        mViewNickname.setOnClickListener(this);
        mViewProductNumber.setOnClickListener(this);
        mViewSex.setOnClickListener(this);
        mViewIdentity.setOnClickListener(this);
        mViewHead.setOnClickListener(this);
        layoutChangePhoneNum.setOnClickListener(this);
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        viewBirthday.setOnClickListener(this);
        viewLocation.setOnClickListener(this);
    }

    private void initData() {
        userAction = new UserAction();
        userInfo = UserAction.getMyInfo();
        if (userInfo != null) {
            imageHead = userInfo.getHead();
            mTvPhone.setText(userInfo.getPhone() + "");
            oldImid = userInfo.getOldimid();
            imid = userInfo.getImid();
            nickName = userInfo.getName();
            sex = userInfo.getSex();
            authStat = userInfo.getAuthStat();
            Glide.with(this).load(imageHead)
                    .apply(GlideOptionsUtil.headImageOptions()).into(mImgHead);
            mTvNickname.setText(nickName);
            if (!oldImid.equals(imid)) {
                mTvProductNumber.setText(imid);
                mIvProductNumber.setVisibility(View.GONE);
                mViewProductNumber.setClickable(false);

            } else {
                mTvProductNumber.setText("未设置");
                mIvProductNumber.setVisibility(View.VISIBLE);
                mViewProductNumber.setClickable(true);
            }
            switch (sex) {
                case 1:
                    mTvSex.setText("男");
                    break;
                case 2:
                    mTvSex.setText("女");
                    break;
                default:
                    mTvSex.setText("未知");
                    break;
            }
            switch (authStat) {
                case 0:
                    mTvIdentity.setText("未认证");
                    break;
                case 1:
                    mTvIdentity.setText("已认证");
                    break;
                case 2:
                    mTvIdentity.setText("已认证");
                    break;
            }
            if (userInfo.getBirthday() != -1) {
                tvBirthday.setText(DateUtil.formatDate(userInfo.getBirthday(), DateUtil.DATE_PATTERN_YMD_STANDARD_CHINESE));
            } else {
                tvBirthday.setText("未设置");
            }
            if (!TextUtils.isEmpty(userInfo.getLocation())) {
                String[] location = userInfo.getLocation().split(",");
                if (location != null && location.length == 2) {
                    tvLocation.setText(location[1]);
                } else {
                    tvLocation.setText(userInfo.getLocation());
                }
            } else {
                tvLocation.setText("未设置");
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (UserUtil.getUserStatus() == CoreEnum.EUserType.DISABLE) {// 封号
            ToastUtil.show(getResources().getString(R.string.user_disable_message));
            return;
        }
        switch (v.getId()) {
            case R.id.view_nickname:
                if (isSystemUser()) {
                    return;
                }
                Intent nicknameIntent = new Intent(MyselfInfoActivity.this, CommonSetingActivity.class);
                nicknameIntent.putExtra(CommonSetingActivity.TITLE, "昵称");
                nicknameIntent.putExtra(CommonSetingActivity.REMMARK, "设置昵称");
                nicknameIntent.putExtra(CommonSetingActivity.HINT, "昵称");
                nicknameIntent.putExtra(CommonSetingActivity.SIZE, 16);
                nicknameIntent.putExtra(CommonSetingActivity.SETING, nickName);
                startActivityForResult(nicknameIntent, NICENAME);
                break;
            case R.id.view_product_number:
                Intent productIntent = new Intent(MyselfInfoActivity.this, CommonSetingActivity.class);
                productIntent.putExtra(CommonSetingActivity.TITLE, "常信号");
                productIntent.putExtra(CommonSetingActivity.REMMARK, "常信号");
                productIntent.putExtra(CommonSetingActivity.HINT, "可以使用6~16个字符 数字(必须以字母开头)");
                productIntent.putExtra(CommonSetingActivity.REMMARK1, "常信号只能设置一次");
                productIntent.putExtra(CommonSetingActivity.SIZE, 16);
                productIntent.putExtra(CommonSetingActivity.SPECIAL, 1);
                startActivityForResult(productIntent, PRODUCT);
                break;
            case R.id.view_sex:
//                Intent sexIntent = new Intent(MyselfInfoActivity.this, SelectSexActivity.class);
//                sexIntent.putExtra(SelectSexActivity.SEX, sex);
//                startActivityForResult(sexIntent, SEX);
                break;
            case R.id.view_identity:
//                if (authStat == 0) {
//                    Intent identityIntent = new Intent(MyselfInfoActivity.this, IdentityAttestationActitiy.class);
//                    startActivityForResult(identityIntent, IDENTITY);
//
//                } else {
//                    Intent identityIntent = new Intent(MyselfInfoActivity.this, IdentificationUserActivity.class);
//                    startActivity(identityIntent);
//                }
                break;
            case R.id.view_head:
                if (isSystemUser()) {
                    return;
                }
                Intent headIntent = new Intent(MyselfInfoActivity.this, ImageHeadActivity.class);
                headIntent.putExtra(ImageHeadActivity.IMAGE_HEAD, imageHead);
                startActivityForResult(headIntent, IMAGE_HEAD);
                break;
            case R.id.layout_change_phone_num:
                startActivity(new Intent(MyselfInfoActivity.this, ChangePhoneNumActivity.class));
                break;
            case R.id.view_birthday:
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                if (isSystemUser()) {
                    return;
                }
                showBirthDayDialog();
                break;
            case R.id.view_location:
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                if (isSystemUser()) {
                    return;
                }
                showLocationDialog();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String content = data.getStringExtra(CommonSetingActivity.CONTENT);
            switch (requestCode) {
                case NICENAME:
                    taskUserInfoSet(null, null, content, null, null, null);
                    break;
                case SEX:
                    int contentSex;
                    if (content.equals("男")) {
                        contentSex = 1;
                    } else {
                        contentSex = 2;
                    }
                    taskUserInfoSet(null, null, null, contentSex, null, null);
                    break;
                case PRODUCT:
                    taskUserInfoSet(content, null, null, null, null, null);
                    break;
                case IMAGE_HEAD:
                    if (!TextUtils.isEmpty(content)) {
                        imageHead = content;
                    }
                    break;
                case IDENTITY:
                    mTvIdentity.setText("已认证");
                    break;
            }

        }
    }


    private void taskUserInfoSet(final String imid, final String avatar, final String nickname, final Integer gender, Long birthday, String location) {

        userAction.myInfoSet(imid, avatar, nickname, gender, birthday, location, null, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    if (!TextUtils.isEmpty(imid)) {
                        MyselfInfoActivity.this.imid = imid;
                        mTvProductNumber.setText(imid);
                        mIvProductNumber.setVisibility(View.GONE);
                        mViewProductNumber.setClickable(false);
                    }

                    if (!TextUtils.isEmpty(nickname)) {
                        MyselfInfoActivity.this.nickName = nickname;
                        mTvNickname.setText(nickname);
                    }
                    if (gender != null) {
                        MyselfInfoActivity.this.sex = gender;
                        if (gender == 1) {
                            sex = 1;
                            mTvSex.setText("男");
                        } else if (gender == 2) {
                            sex = 2;
                            mTvSex.setText("女");
                        } else {
                            mTvSex.setText("未知");
                        }
                    }
                }
//                if (!TextUtils.isEmpty(imid)) {
//                    ToastUtil.show(MyselfInfoActivity.this, "该常信号已存在，设置失败");
//                } else {
//                    ToastUtil.show(MyselfInfoActivity.this, response.body().getMsg());
//                }
                ToastUtil.show(MyselfInfoActivity.this, response.body().getMsg());
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventMyUserInfo event) {
        if (event.type == 1) {
            UserInfo userInfo = event.getUserInfo();
            imageHead = userInfo.getHead();
            Glide.with(this).load(userInfo.getHead())
                    .apply(GlideOptionsUtil.headImageOptions()).into(mImgHead);
        }
    }

    /**
     * 更新用户信息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshUser(EventRefreshUser event) {
        initData();
    }

    //是否是系统用户，系统用户不可修改用户信息
    private boolean isSystemUser() {
        if (userInfo == null) {
            return false;
        }
        if (UserUtil.isSystemUser(userInfo.getUid())) {
            return true;
        }
        return false;
    }


    //时间选择器。选择生日
    private void showBirthDayDialog() {
        if (userInfo != null) {
            Calendar current = Calendar.getInstance();
            Calendar defaultCalendar = Calendar.getInstance();
            if (userInfo.getBirthday() == -1) {//未设置
                defaultCalendar.set(current.get(Calendar.YEAR) - 20, 0, 1);//默认20岁
            } else {
                defaultCalendar.setTimeInMillis(userInfo.getBirthday());
            }

            Calendar start = Calendar.getInstance();
            start.set(current.get(Calendar.YEAR) - 100, 0, 1);//1920-1-1
            Calendar end = Calendar.getInstance();
            end.set(current.get(Calendar.YEAR) - 10, 11, 31);//2010-12-31

            //时间选择器
            TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    tvBirthday.setText(DateUtil.formatDate(calendar.getTimeInMillis(), DateUtil.DATE_PATTERN_YMD_STANDARD_CHINESE));
                    taskUserInfoSet(null, null, null, null, calendar.getTimeInMillis(), null);
                }
            })
                    .setType(new boolean[]{true, true, true, false, false, false})
                    .setDate(defaultCalendar)
                    .setRangDate(start, end)
                    .setCancelText("取消")
                    .setCancelColor(Color.parseColor("#878787"))
                    .setSubmitText("确定")
                    .setSubmitColor(Color.parseColor("#32b152"))
                    .build();
            pvTime.show();
        }
    }

    private void showLocationDialog() {
        String province = "";
        String city = "";
        if (!TextUtils.isEmpty(userInfo.getLocation())) {
            String[] location = userInfo.getLocation().split(",");
            if (location != null && location.length == 2) {
                province = location[0];
                city = location[1];
            }
        }
        DialogLocationSelector locationDialog = new DialogLocationSelector(this, province, city);
        locationDialog.setListener(new DialogLocationSelector.ILocationListener() {
            @Override
            public void onSure(String province, String city) {
                tvLocation.setText(city);
                taskUserInfoSet(null, null, null, null, null, province + "," + city);
            }
        }).show();

    }


}






















