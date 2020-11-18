package com.yanlong.im.user.ui.register;

import android.Manifest;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.yanlong.im.MainActivity;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.RegisterDetailBean;
import com.yanlong.im.databinding.FragmentRegisterFifthBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserBean;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.PopupSelectView;

import retrofit2.Call;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


/**
 * @author Liszt
 * @date 2020/11/16
 * Description 头像
 */
public class RegisterDetailFifthFragment extends BaseRegisterFragment<FragmentRegisterFifthBinding> {
    private CheckPermission2Util permission2Util = new CheckPermission2Util();
    private String[] strings = {"拍照", "从手机相册中选择", "取消"};
    private UserDao dao = new UserDao();


    @Override
    public int getLayoutId() {
        return R.layout.fragment_register_fifth;
    }

    @Override
    public void init() {
        mViewBinding.ivLeft.setVisibility(View.VISIBLE);
        mViewBinding.ivRight.setVisibility(View.INVISIBLE);
        mViewBinding.tvGo.setEnabled(false);
    }

    @Override
    public void initListener() {
        mViewBinding.ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });

        //进入常信
        mViewBinding.tvGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                uploadInfo();
            }
        });

        //拍照
        mViewBinding.ivTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                initPopup();
            }
        });
    }


    @Override
    public void updateDetailUI(RegisterDetailBean bean) {
        if (bean == null || getActivity() == null || mViewBinding == null) {
            return;
        }
        if (!TextUtils.isEmpty(bean.getAvatar())) {
            loadAvatar(bean.getAvatar());
            updateGoUI(true);
        } else {
            updateGoUI(false);

        }
    }

    public void loadAvatar(String avatar) {
        Glide.with(getActivity()).load(avatar).into(mViewBinding.ivTakePhoto);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permission2Util.onRequestPermissionsResult();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    final String file = PictureSelector.obtainMultipleResult(data).get(0).getCompressPath();
                    uploadAvatar(file);
                    break;
            }
        }
    }

    //上传头像
    public void uploadAvatar(String file) {

        new UpFileAction().upFile(UserAction.getMyId() + "", UpFileAction.PATH.HEAD, getContext(), new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String url) {
                ((RegisterDetailActivity) getActivity()).getDetailBean().setAvatar(url);
//                updateMyUserInfo(((RegisterDetailActivity) getActivity()).getDetailBean());

                mViewBinding.ivTakePhoto.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadAvatar(url);
                        updateGoUI(true);
                    }
                }, 100);

            }

            @Override
            public void fail() {
                ToastUtil.show(getContext(), "上传失败!");
            }

            @Override
            public void inProgress(long progress, long zong) {

            }
        }, file);
    }

//    private void updateMyUserInfo(RegisterDetailBean bean) {
//        UserBean mUserInfo = dao.findUserBean(UserAction.getMyId());
//        if (mUserInfo != null) {
//            mUserInfo.setName(bean.getNick());
//            mUserInfo.setHead(bean.getAvatar());
//            dao.updateUserBean(mUserInfo);
//        }
//    }

    private void initPopup() {
        PopupSelectView popupSelectView = new PopupSelectView(getActivity(), strings);
        popupSelectView.showAtLocation(mViewBinding.ivTakePhoto, Gravity.BOTTOM, 0, 0);
        popupSelectView.setListener(new PopupSelectView.OnClickItemListener() {
            @Override
            public void onItem(String string, int position) {
                switch (position) {
                    case 0:
                        permission2Util.requestPermissions(getActivity(), new CheckPermission2Util.Event() {
                            @Override
                            public void onSuccess() {
                                PictureSelector.create(getActivity())
                                        .openCamera(PictureMimeType.ofImage())
                                        .compress(true)
                                        .enableCrop(true)
                                        .withAspectRatio(1, 1)
                                        .freeStyleCropEnabled(false)
                                        .rotateEnabled(false)
                                        .forResult(PictureConfig.CHOOSE_REQUEST);
                            }

                            @Override
                            public void onFail() {

                            }
                        }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
                        break;
                    case 1:
                        PictureSelector.create(getActivity())
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

                        break;
                }
                popupSelectView.dismiss();
            }
        });
    }

    public void updateGoUI(boolean isEnable) {
        if (isEnable) {
            mViewBinding.tvGo.setEnabled(true);
            mViewBinding.tvGo.setTextColor(AppConfig.getColor(R.color.white));
            mViewBinding.tvGo.setBackgroundResource(R.drawable.bg_btn_green);
        } else {
            mViewBinding.tvGo.setEnabled(false);
            mViewBinding.tvGo.setTextColor(AppConfig.getColor(R.color.black));
            mViewBinding.tvGo.setBackgroundResource(R.drawable.shape_birthday_bg);
        }
    }

    private void uploadInfo() {
        if (getActivity() == null) {
            return;
        }
        RegisterDetailBean bean = ((RegisterDetailActivity) getActivity()).getDetailBean();
        new UserAction().updateMyInfo(null, bean.getAvatar(), bean.getNick(), bean.getSex(), bean.getBirthday(), bean.getLocation(), bean.getHeight(), new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()) {
                    ToastUtil.show("注册成功");
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }
}
