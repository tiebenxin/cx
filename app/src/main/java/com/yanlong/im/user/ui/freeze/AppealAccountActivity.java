package com.yanlong.im.user.ui.freeze;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.nim_lib.action.VideoAction;
import com.example.nim_lib.bean.TokenBean;
import com.example.nim_lib.ui.BaseBindActivity;
import com.example.nim_lib.util.SharedPreferencesUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.databinding.ActivityAppealAccountBinding;
import com.yanlong.im.databinding.ItemAppealAccountBinding;
import com.yanlong.im.databinding.ItemFaceViewBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.ImageBean;
import com.yanlong.im.user.ui.FeedbackActivity;
import com.yanlong.im.user.ui.FeedbackShowImageActivity;
import com.yanlong.im.utils.CommonUtils;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.view.face.AddFaceActivity;
import com.yanlong.im.view.face.bean.FaceBean;

import net.cb.cb.library.BuildConfig;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.constant.AppHostUtil;
import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.manager.Constants;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CallBack4Btn;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.utils.NetIntrtceptor;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.utils.UpLoadFileUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.PopupSelectView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-7-6
 * @updateAuthor
 * @updateDate
 * @description 账号申诉
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
@Route(path = AppealAccountActivity.path)
public class AppealAccountActivity extends BaseBindActivity<ActivityAppealAccountBinding> implements View.OnClickListener {
    public static final String path = "/AppealAccount/AppealAccountActivity";

    public static final int SHOW_IMAGE = 9038;
    private PopupSelectView popupSelectView;
    private String[] strings = {"手机相册", "拍照", "取消"};
    private CheckPermission2Util permission2Util = new CheckPermission2Util();
    private CommonRecyclerViewAdapter<LocalMedia, ItemAppealAccountBinding> mAdapter;
    private List<LocalMedia> mList = new ArrayList<>();

    @Override
    protected int setView() {
        return R.layout.activity_appeal_account;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        mAdapter = new CommonRecyclerViewAdapter<LocalMedia, ItemAppealAccountBinding>(this, R.layout.item_appeal_account) {

            @Override
            public void bind(ItemAppealAccountBinding binding, LocalMedia localMedia,
                             int position, RecyclerView.ViewHolder viewHolder) {
                if (localMedia.isShowAdd()) {
                    binding.rlDelet.setVisibility(View.GONE);
                    binding.ivImg.setImageResource(R.mipmap.ic_add_violation);
                } else {
                    binding.rlDelet.setVisibility(View.VISIBLE);
                    Glide.with(binding.ivImg)
                            .load(getMediaPath(localMedia))
                            .apply(GlideOptionsUtil.imageOptions())
                            .into(binding.ivImg);
                }
                binding.rlDelet.setOnClickListener(o -> {
                    if (!DoubleUtils.isFastDoubleClick()) {
                        getList().remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getList().size());
                        checkShowAdd();
                    }
                });
                binding.ivImg.setOnClickListener(o -> {
                    if (localMedia.isShowAdd()) {
                        showInput(false);
                        initPopup();
                    } else {
                        Intent intent = new Intent(AppealAccountActivity.this, FeedbackShowImageActivity.class);
                        intent.putExtra(FeedbackShowImageActivity.URL, getMediaPath(localMedia));
                        intent.putExtra(FeedbackShowImageActivity.POSTION, position);
                        intent.putExtra(FeedbackShowImageActivity.TYPE, 2);
                        startActivityForResult(intent, SHOW_IMAGE);
                    }
                });
            }
        };
        addShowAdd();
        mAdapter.setData(mList);
        bindingView.recyclerView.addItemDecoration(new GridSpacingItemDecoration(3,
                ScreenUtils.dip2px(this, 10), false));
        bindingView.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        bindingView.recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initEvent() {
        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        bindingView.edContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bindingView.tvWords.setText(s.toString().length() + "/300");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        bindingView.btnCommit.setOnClickListener(this);
    }

    @Override
    protected void loadData() {

    }

    /**
     * 移除或添加
     */
    private void checkShowAdd() {
        if (mList.size() == 2) {
            for (int i = mList.size() - 1; i >= 0; i--) {
                if (mList.get(i).isShowAdd()) {
                    mList.remove(i);
                    break;
                }
            }
        } else {
            boolean isShow = false;
            for (LocalMedia localMedia : mList) {
                if (localMedia.isShowAdd()) {
                    isShow = true;
                    break;
                }
            }
            if (!isShow) {
                addShowAdd();
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void addShowAdd() {
        LocalMedia localMedia = new LocalMedia();
        localMedia.setShowAdd(true);
        mList.add(localMedia);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    if (null != data) {
                        List<LocalMedia> list = PictureSelector.obtainMultipleResult(data);
                        if (null != list && list.size() > 0) {
                            mList.addAll(0, list);
                            checkShowAdd();
                        }
                    }
                    break;
            }
        }
    }

    private void initPopup() {
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(bindingView.headView, Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int postsion) {
                switch (postsion) {
                    case 1:
                        permission2Util.requestPermissions(AppealAccountActivity.this, new CheckPermission2Util.Event() {
                            @Override
                            public void onSuccess() {
                                PictureSelector.create(AppealAccountActivity.this)
                                        .openCamera(PictureMimeType.ofImage())
                                        .compress(true)
                                        .freeStyleCropEnabled(false)
                                        .rotateEnabled(false)
                                        .forResult(PictureConfig.CHOOSE_REQUEST);
                            }

                            @Override
                            public void onFail() {

                            }
                        }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
                        break;
                    case 0:
                        PictureSelector.create(AppealAccountActivity.this)
                                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                .maxSelectNum(1)
                                .previewImage(false)// 是否可预览图片 true or false
                                .isCamera(false)// 是否显示拍照按钮 ture or false
                                .compress(true)// 是否压缩 true or false
                                .freeStyleCropEnabled(false)
                                .rotateEnabled(false)
                                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (DoubleUtils.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_commit:
                String content = bindingView.edContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show("请输入申诉内容");
                    bindingView.edContent.requestFocus();
                    return;
                }
                DialogCommon dialogCommon = new DialogCommon(this);
                dialogCommon.setCanceledOnTouchOutside(false);
                dialogCommon.setTitleAndSure(true, true)
                        .setTitle("提示")
                        .setContent("确定提交申诉？", true)
                        .setListener(new DialogCommon.IDialogListener() {
                            @Override
                            public void onSure() {
                                boolean isPic = false;
                                for (LocalMedia localMedia : mList) {
                                    if (!localMedia.isShowAdd()) {
                                        isPic = true;
                                    }
                                }
                                if (isPic) {
                                    UpLoadFileUtil.getInstance().upLoadFile(AppealAccountActivity.this, mList, new UpLoadFileUtil.OnUploadFileListener() {
                                        @Override
                                        public void onUploadFile(HashMap<String, String> netFile) {
                                            String file = "";
                                            for (int i = 0; i < mList.size(); i++) {
                                                file = file + netFile.get(getMediaPath(mList.get(i))) + ",";
                                            }
                                            file = file.substring(0, file.length() - 1);
                                            AppHostUtil.setHostUrl(BuildConfig.HOST_DEV);
                                            commit(content, file);
                                        }

                                        @Override
                                        public void onFail() {
                                            ToastUtil.show("图片上传失败");
                                        }
                                    });
                                } else {
                                    commit(content, "");
                                }
                            }

                            @Override
                            public void onCancel() {
                                dialogCommon.dismiss();
                            }
                        });
                dialogCommon.show();
                break;
        }
    }

    private void commit(String content, String files) {
        WeakHashMap<String, Object> params = new WeakHashMap<>();
        params.put("content", content);
        if (!TextUtils.isEmpty(files)) {
            params.put("pic", files);
        }
        new UserAction().userAppeal(params, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response != null && response.body().isOk()) {
                    EventFactory.UpdateAppealStatusEvent event = new EventFactory.UpdateAppealStatusEvent();
                    event.status = true;
                    EventBus.getDefault().post(event);
                    IntentUtil.gotoActivityAndFinish(AppealAccountActivity.this, AppealIngActivity.class);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    private String getMediaPath(LocalMedia localMedia) {
        String path = localMedia.getPath();
        if (TextUtils.isEmpty(path)) {
            path = localMedia.getCutPath();
        }
        return path;
    }
}
