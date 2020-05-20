package com.yanlong.im.view.face;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.nim_lib.config.Preferences;
import com.example.nim_lib.ui.BaseBindActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.databinding.ActivityAddFaceBinding;
import com.yanlong.im.databinding.ItemFaceViewBinding;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.view.face.bean.FaceBean;
import com.yanlong.im.view.face.wight.RecycleGridDivider;

import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.utils.IntentUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SpUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-01-08
 * @updateAuthor
 * @updateDate
 * @description 添加表情
 * @copyright copyright(c)2020 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class AddFaceActivity extends BaseBindActivity<ActivityAddFaceBinding> {

    private CommonRecyclerViewAdapter<FaceBean, ItemFaceViewBinding> mViewAdapter;
    private List<FaceBean> mList = new ArrayList<>();
    private boolean isEdit = false;
    public static final int REQUEST_CODE =100;

    @Override
    protected int setView() {
        return R.layout.activity_add_face;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        bindingView.headView.getActionbar().setTxtRight("编辑");
        mViewAdapter = new CommonRecyclerViewAdapter<FaceBean, ItemFaceViewBinding>(this, R.layout.item_face_view) {

            @Override
            public void bind(ItemFaceViewBinding binding, FaceBean faceBean,
                             int position, RecyclerView.ViewHolder viewHolder) {
                if (position == 0) {
                    binding.imgFace.setImageResource(faceBean.getResId());
                } else {
                    Glide.with(AddFaceActivity.this).load(faceBean.getPath()).listener(new RequestListener() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).apply(GlideOptionsUtil.imageOptions()).into(binding.imgFace);
                }
                if (isEdit) {
                    if (position != 0) {
                        binding.imSelect.setVisibility(View.VISIBLE);
                    } else {
                        binding.imSelect.setVisibility(View.GONE);
                    }
                    if (faceBean.isCheck()) {
                        binding.imSelect.setImageResource(R.drawable.bg_cheack_green_s);
                    } else {
                        binding.imSelect.setImageResource(R.drawable.bg_cheack_green_e);
                    }
                } else {
                    binding.imSelect.setVisibility(View.GONE);
                }
                binding.imSelect.setOnClickListener(o -> {
                    faceBean.setCheck(!faceBean.isCheck());
                    notifyDataSetChanged();
                });
                binding.imgFace.setOnClickListener(o -> {
                    if (position == 0) {
                        if (!ViewUtils.isFastDoubleClick()) {
                            gotoPictureSelector();
                        }
                    } else {
                        if (isEdit) {
                            faceBean.setCheck(!faceBean.isCheck());
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        };
        bindingView.recyclerView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        bindingView.recyclerView.addItemDecoration(new RecycleGridDivider(1, getResources().getColor(R.color.gray_200)));
        bindingView.recyclerView.setAdapter(mViewAdapter);
        addFace(R.mipmap.img_add_face_photo, "add", "","");
        getFaceData();
        mViewAdapter.setData(mList);
    }

    @Override
    protected void initEvent() {
        setActionBarLeft(bindingView.headView);
        bindingView.headView.getActionbar().getTxtRight().setOnClickListener(o -> {
            if ("编辑".equals(bindingView.headView.getActionbar().getTxtRight().getText().toString())) {
                bindingView.headView.getActionbar().setTxtRight("取消");
                bindingView.layoutBottom.setVisibility(View.VISIBLE);
                isEdit = true;
            } else {
                bindingView.headView.getActionbar().setTxtRight("编辑");
                bindingView.layoutBottom.setVisibility(View.GONE);
                isEdit = false;
            }
            mViewAdapter.notifyDataSetChanged();
        });
        bindingView.txtDelete.setOnClickListener(o -> {
            if (ViewUtils.isFastDoubleClick()) {
                return;
            }
            if (mList != null) {
                boolean isCheck = false;
                for (int i = mList.size() - 1; i >= 0; i--) {
                    FaceBean faceBean = mList.get(i);
                    if (faceBean.isCheck()) {
                        isCheck = true;
                        mList.remove(faceBean);
                    }
                }
                if (isCheck) {
                    saveFace();
                    mViewAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.show(this, "请先选择删除的表情");
                }
            }
        });
    }

    @Override
    protected void loadData() {

    }

    private void gotoPictureSelector() {
        PictureSelector.create(AddFaceActivity.this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .isCamera(false)// 是否显示拍照按钮 ture or false
                .compress(true)// 是否压缩 true or false
                .enableCrop(false)
                .freeStyleCropEnabled(false)
                .rotateEnabled(false)
                .isGif(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    /**
     *表情预览
     * @param name
     * @param path
     */
    private void gotoFacePreview(String name, String path) {
        boolean isExist = false;
        for (FaceBean faceBean : mList) {
            if (faceBean.getName().equals(name)) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            Bundle bundle = new Bundle();
            bundle.putString(Preferences.FACE_PATH,path);
            bundle.putString(Preferences.FACE_NAME,name);
            IntentUtil.gotoActivityForResult(AddFaceActivity.this,FacePreviewActivity.class,bundle,REQUEST_CODE);
        } else {
            ToastUtil.show(this, "表情已存在");
        }
    }

    /**
     * 添加表情
     *
     * @param resId
     * @param name
     * @param serverPath
     */
    private void addFace(int resId, String name, String path,String serverPath) {
        FaceBean bean = new FaceBean();
        bean.setGroup("custom");
        bean.setResId(resId);
        bean.setName(name);
        bean.setPath(path);
        bean.setServerPath(serverPath);
        if (resId == 0) {
            mList.add(1, bean);
            ToastUtil.showToast(this, "已添加到表情面板",0);
        } else {
            mList.add(bean);
        }
        mViewAdapter.notifyDataSetChanged();
        if(resId==0){
            saveFace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    String path = PictureSelector.obtainMultipleResult(data).get(0).getCompressPath();
                    // 例如 LocalMedia 里面返回两种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    File file = new File(path);
                    if (file != null) {
                        gotoFacePreview(file.getName(), path);
                    }
                    break;
                case REQUEST_CODE:
                    String serverPath = data.getExtras().getString(Preferences.FACE_SERVER_PATH);
                    String locationPath = data.getExtras().getString(Preferences.FACE_PATH);
                    String name = data.getExtras().getString(Preferences.FACE_NAME);

                    addFace(0, name, locationPath,serverPath);
                    break;
            }
        }
    }

    private void saveFace() {

        if (mList != null) {
            SpUtil spUtil = SpUtil.getSpUtil();
            if (UserAction.getMyId().intValue() != -1) {
                spUtil.putSPValue(UserAction.getMyId().intValue() + Preferences.FACE_DATA, new Gson().toJson(mList));
            } else {
                Long uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UID).get4Json(Long.class);
                spUtil.putSPValue(uid + Preferences.FACE_DATA, new Gson().toJson(mList));
            }
            EventBus.getDefault().post(new EventFactory.FaceUpdateEvent());
        }
    }

    private void getFaceData() {
        SpUtil spUtil = SpUtil.getSpUtil();
        String value = "";
        if (UserAction.getMyId().intValue() != -1) {
            value = spUtil.getSPValue(UserAction.getMyId().intValue() + Preferences.FACE_DATA, "");
        } else {
            Long uid = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.UID).get4Json(Long.class);
            value = spUtil.getSPValue(uid + Preferences.FACE_DATA, "");
        }
        if (!TextUtils.isEmpty(value)) {
            List<FaceBean> list = new Gson().fromJson(value, new TypeToken<List<FaceBean>>() {
            }.getType());
            list.remove(0);
            mList.addAll(list);
        }
    }
}
