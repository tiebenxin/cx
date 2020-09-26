package com.yanlong.im.circle.mycircle;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.CircleTrendsBean;
import com.yanlong.im.databinding.ActivityMyCircleBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserBean;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.utils.ViewUtils;

import java.io.File;

import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Response;

import static com.yanlong.im.circle.mycircle.MyFollowActivity.DEFAULT_PAGE_SIZE;

/**
 * @类名：我的动态(我的朋友圈)
 * @Date：2020/9/25
 * @by zjy
 * @备注：
 */

public class MyCircleActivity extends BaseBindActivity<ActivityMyCircleBinding> {


    private int page = 1;//默认第一页

    private UserBean userBean;
    private TempAction action;
    private CheckPermission2Util permission2Util = new CheckPermission2Util();
    private UpFileAction upFileAction;


    @Override
    protected int setView() {
        return R.layout.activity_my_circle;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        action = new TempAction();
    }

    @Override
    protected void initEvent() {
        setActionBarLeft(bindingView.headView);
        bindingView.layoutMyFollow.setOnClickListener(v -> {
            if (ViewUtils.isFastDoubleClick()) {
                return;
            }
            Intent intent = new Intent(MyCircleActivity.this, MyFollowActivity.class);
            startActivity(intent);
        });
        bindingView.layoutFollowMe.setOnClickListener(v -> {
            if (ViewUtils.isFastDoubleClick()) {
                return;
            }
            Intent intent = new Intent(MyCircleActivity.this, FollowMeActivity.class);
            startActivity(intent);
        });
        bindingView.layoutWhoSeeMe.setOnClickListener(v -> {
            if (ViewUtils.isFastDoubleClick()) {
                return;
            }
            Intent intent = new Intent(MyCircleActivity.this, MyMeetingActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void loadData() {
        showTopLayout();
        httpGetMyTrends();
        //点击布局切换背景
        bindingView.layoutTop.setOnClickListener(v -> permission2Util.requestPermissions(MyCircleActivity.this, new CheckPermission2Util.Event() {
            @Override
            public void onSuccess() {
                PictureSelector.create(MyCircleActivity.this)
                        .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(false)// 是否可预览图片 true or false
                        .isCamera(false)// 是否显示拍照按钮 ture or false
                        .compress(true)// 是否压缩 true or false
                        .enableCrop(true)
                        .withAspectRatio(1, 1)
                        .freeStyleCropEnabled(false)
                        .rotateEnabled(false)
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

            }

            @Override
            public void onFail() {
                ToastUtil.show("请允许访问权限");
            }
        }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}));
    }

    //展示头部数据
    private void showTopLayout() {
        userBean = (UserBean) new UserAction().getMyInfo();
        if(userBean!=null){
            //头像 昵称 常信号 关注 被关注 看过我
            if(!TextUtils.isEmpty(userBean.getHead())){
                Glide.with(MyCircleActivity.this)
                        .load(userBean.getHead())
                        .into(bindingView.ivHeader);
            }
            if(!TextUtils.isEmpty(userBean.getName())){
                bindingView.tvName.setText(userBean.getName());
            }else {
                bindingView.tvName.setText("未知用户名");
            }
            if(!TextUtils.isEmpty(userBean.getImid())){
                bindingView.tvImid.setText("常信号："+userBean.getImid());
            }
        }
    }

    /**
     * 发请求->获取我的动态(说说主页及列表)
     */
    private void httpGetMyTrends() {
        action.httpGetMyTrends(page, DEFAULT_PAGE_SIZE, new CallBack<ReturnBean<CircleTrendsBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<CircleTrendsBean>> call, Response<ReturnBean<CircleTrendsBean>> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    if(response.body().getData()!=null){
                        CircleTrendsBean bean = response.body().getData();
                        //第一页拿部分数据，我关注的，关注我的，看过我的总数
                        if(page==1){
                            bindingView.tvMyFollowNum.setText(bean.getMyFollowCount()+"");
                            bindingView.tvFollowMeNum.setText(bean.getFollowMyCount()+"");
                            bindingView.tvWhoSeeMeNum.setText(bean.getAccessCount()+"");
                            if(!TextUtils.isEmpty(bean.getBgImage())){
                                bindingView.ivBackground.setVisibility(View.VISIBLE);
                                changeTextColor(true);
                                Glide.with(MyCircleActivity.this).load(bean.getBgImage())
                                        .apply(GlideOptionsUtil.defImageOptions1()).into(bindingView.ivBackground);
                            }else {
                                bindingView.ivBackground.setVisibility(View.GONE);
                                changeTextColor(false);
                            }
                        }
                    }
                }else {
                    ToastUtil.show("获取我的动态失败");
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<CircleTrendsBean>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("获取我的动态失败");
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permission2Util.onRequestPermissionsResult();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    final String file = PictureSelector.obtainMultipleResult(data).get(0).getCompressPath();
                    // 例如 LocalMedia 里面返回两种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    Uri uri = Uri.fromFile(new File(file));
                    alert.show();
                    //显示背景图
                    Glide.with(this).load(uri)
                            .apply(GlideOptionsUtil.defImageOptions1()).into(bindingView.ivBackground);
                    bindingView.ivBackground.setVisibility(View.VISIBLE);
                    changeTextColor(true);
                    //上传背景图
                    if(upFileAction==null){
                        upFileAction = new UpFileAction();
                    }
                    upFileAction.upFile(UserAction.getMyId() + "", UpFileAction.PATH.IMG, getContext(), new UpFileUtil.OssUpCallback() {
                        @Override
                        public void success(String url) {
                            alert.dismiss();
                            //通知更新背景图
                            httpSetBackground(url);
                        }

                        @Override
                        public void fail() {
                            alert.dismiss();
                            ToastUtil.show(getContext(), "背景图上传失败!");
                        }

                        @Override
                        public void inProgress(long progress, long zong) {

                        }
                    }, file);
                    break;
            }
        }
    }

    /**
     * 发请求->更新背景图
     * @param url
     */
    private void httpSetBackground(String url) {
        action.httpSetBackground(url, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("更新背景图成功");
                }else {
                    ToastUtil.show("更新背景图失败");
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("更新背景图失败");
            }
        });
    }

    /**
     * 是否显示无数据占位图
     * @param ifShow
     */
    private void showNoDataLayout(boolean ifShow) {
        if (ifShow) {
            bindingView.recyclerView.setVisibility(View.GONE);
            bindingView.noDataLayout.setVisibility(View.VISIBLE);
        } else {
            bindingView.recyclerView.setVisibility(View.VISIBLE);
            bindingView.noDataLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 改变顶部文字颜色
     * @param hadBackground 是否有背景图
     */
    private void changeTextColor(boolean hadBackground){
        if(hadBackground){
            bindingView.tvName.setTextColor(getResources().getColor(R.color.white));
            bindingView.tvImid.setTextColor(getResources().getColor(R.color.white));
        }else {
            bindingView.tvName.setTextColor(getResources().getColor(R.color.c_363636));
            bindingView.tvImid.setTextColor(getResources().getColor(R.color.c_868686));
        }
    }
}
