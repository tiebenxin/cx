package com.yanlong.im.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yanlong.im.R;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import retrofit2.Call;
import retrofit2.Response;

public class MyselfInfoActivity extends AppActivity implements View.OnClickListener {
    private static final int NICENAME = 1000;
    private static final int PRODUCT = 2000;
    private static final int SEX = 3000;
    private static final int IMAGE_HEAD = 4000;

    private SimpleDraweeView mImgHead;
    private LinearLayout mViewBlacklist;
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
    private UserInfo userInfo;
    private UserAction userAction;
    private ImageView mIvProductNumber;
    private int sex;
    private String imageHead;
    private String imid;
    private String nickName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself_info);
        initView();
        initEvent();
        initData();
    }


    private void initView() {
        mImgHead = findViewById(R.id.img_head);
        mViewBlacklist = findViewById(R.id.view_blacklist);
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
    }


    private void initEvent() {
        mViewNickname.setOnClickListener(this);
        mViewProductNumber.setOnClickListener(this);
        mViewSex.setOnClickListener(this);
        mViewIdentity.setOnClickListener(this);
        mViewHead.setOnClickListener(this);
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
    }

    private void initData() {
        userAction = new UserAction();
        userInfo = UserAction.getMyInfo();
        imageHead = userInfo.getHead();
        imid = userInfo.getImid();
        nickName = userInfo.getName();
        sex = userInfo.getSex();

        mImgHead.setImageURI(imageHead + "");
        mTvNickname.setText(nickName);
        if (!TextUtils.isEmpty(imid)) {
            mTvProductNumber.setText(imid);
            mIvProductNumber.setVisibility(View.GONE);
            mViewProductNumber.setClickable(false);

        } else {
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
                mTvSex.setText("保密");
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_nickname:
                Intent nicknameIntent = new Intent(MyselfInfoActivity.this, CommonSetingActivity.class);
                nicknameIntent.putExtra(CommonSetingActivity.TITLE, "昵称");
                nicknameIntent.putExtra(CommonSetingActivity.REMMARK, "设置昵称");
                nicknameIntent.putExtra(CommonSetingActivity.HINT, "昵称");
                startActivityForResult(nicknameIntent, NICENAME);
                break;
            case R.id.view_product_number:
                Intent productIntent = new Intent(MyselfInfoActivity.this, CommonSetingActivity.class);
                productIntent.putExtra(CommonSetingActivity.TITLE, "夸夸号");
                productIntent.putExtra(CommonSetingActivity.REMMARK, "夸夸号");
                productIntent.putExtra(CommonSetingActivity.HINT, "可以使用5~15个字符 数字(必须以字母开头)");
                productIntent.putExtra(CommonSetingActivity.REMMARK1, "夸夸号只能设置一次");
                startActivityForResult(productIntent, PRODUCT);
                break;
            case R.id.view_sex:
                Intent sexIntent = new Intent(MyselfInfoActivity.this, SelectSexActivity.class);
                startActivityForResult(sexIntent, SEX);
                break;
            case R.id.view_identity:
                Intent identityIntent = new Intent(MyselfInfoActivity.this, IdentificationCentreActivity.class);
                startActivity(identityIntent);
                break;
            case R.id.view_head:
                Intent headIntent = new Intent(MyselfInfoActivity.this, ImageHeadActivity.class);
                headIntent.putExtra(ImageHeadActivity.IMAGE_HEAD,imageHead);
                startActivityForResult(headIntent,IMAGE_HEAD);
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
                    mTvNickname.setText(content);
                    nickName = content;
                    taskUserInfoSet(null,null,nickName,null);
                    break;
                case SEX:
                    if (!TextUtils.isEmpty(content)) {
                        mTvSex.setText(content);
                        if(content.equals("男")) {
                            sex = 1;
                        }else{
                            sex = 2;
                        }
                    }
                    taskUserInfoSet(null,null,null,sex);
                    break;
                case PRODUCT:
                    if(!TextUtils.isEmpty(content)){
                        imid = content;
                        mTvProductNumber.setText(imid);
                        mIvProductNumber.setVisibility(View.GONE);
                        mViewProductNumber.setClickable(false);
                    }
                    taskUserInfoSet(imid,null,null,null);
                    break;
                case IMAGE_HEAD:
                    if(!TextUtils.isEmpty(content)){
                        imageHead = content;
                        mImgHead.setImageURI(content);
                    }
                    break;
            }

        }
    }


    private void taskUserInfoSet(String imid,String avatar,String nickname,Integer gender){
        userAction.myInfoSet(imid, avatar, nickname, gender, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if(response.body() == null){
                    return;
                }
                ToastUtil.show(MyselfInfoActivity.this,response.body().getMsg());
            }
        });
    }


}






















