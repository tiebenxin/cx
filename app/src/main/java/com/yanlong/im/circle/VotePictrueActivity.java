package com.yanlong.im.circle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.SpannableString;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.circle.CreateCircleActivity;
import com.luck.picture.lib.circle.OnPhotoPreviewChangedListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.adapter.PicturePreviewAdapter;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.ScreenUtils;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.CircleTitleBean;
import com.yanlong.im.databinding.ActivityVotePictrueBinding;
import com.yanlong.im.utils.ExpressionUtil;

import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.utils.ImgSizeUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-07
 * @updateAuthor
 * @updateDate
 * @description 图片题目
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
@Route(path = VotePictrueActivity.path)
public class VotePictrueActivity extends BaseBindActivity<ActivityVotePictrueBinding> implements OnPhotoPreviewChangedListener {
    public static final String path = "/circle/VotePictrueActivity";

    private final int MAX_NUMBER = 4;
    private List<LocalMedia> mList = new ArrayList<>();
    private PicturePreviewAdapter mPictureAdapter;

    @Override
    protected int setView() {
        return R.layout.activity_vote_pictrue;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mList = new ArrayList<>();
        mPictureAdapter = new PicturePreviewAdapter(this, mList, this);
        bindingView.recyclerView.addItemDecoration(new GridSpacingItemDecoration(4,
                ScreenUtils.dip2px(this, 10), false));
        bindingView.recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        bindingView.recyclerView.setAdapter(mPictureAdapter);
    }

    @Override
    protected void initEvent() {
        bindingView.headView.getActionbar().setTxtRight("完成");
        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                if (TextUtils.isEmpty(bindingView.etTitle.getText().toString().trim())) {
                    ToastUtil.show("请输入投票标题或描述");
                    return;
                }
                if (mList != null && mList.size() < 3) {
                    ToastUtil.show("请添加两张及以上图片");
                    return;
                }
                Intent intent = new Intent();
                List<CircleTitleBean> list = new ArrayList<>();
//                for (int i = mList.size() - 1; i >= 0; i--) {
//                    if (mList.get(i).isShowAdd()) {
//                        mList.remove(i);
//                    } else {
//                        ImgSizeUtil.ImageSize imageSize = ImgSizeUtil.getAttribute(mList.get(i).getPath());
//                        if (imageSize != null) {
//                            list.add(new CircleTitleBean(mList.get(i).getPath(),
//                                    imageSize.getSize(), imageSize.getWidth(), imageSize.getHeight()));
//                        }
//                    }
//                }
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).isShowAdd()) {
                        mList.remove(i);
                    } else {
                        ImgSizeUtil.ImageSize imageSize = ImgSizeUtil.getAttribute(mList.get(i).getPath());
                        if (imageSize != null) {
                            list.add(new CircleTitleBean(mList.get(i).getPath(),
                                    imageSize.getSize(), imageSize.getWidth(), imageSize.getHeight()));
                        }
                    }
                }
                intent.putExtra(CreateCircleActivity.VOTE_LOCATION_IMG, new Gson().toJson(mList));
                intent.putExtra(CreateCircleActivity.VOTE_TXT, new Gson().toJson(list));
                intent.putExtra(CreateCircleActivity.VOTE_TXT_TITLE, bindingView.etTitle.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void loadData() {
        String title = getIntent().getStringExtra(CreateCircleActivity.VOTE_TXT_TITLE);
        String txtJson = getIntent().getStringExtra(CreateCircleActivity.VOTE_LOCATION_IMG);
        bindingView.etTitle.setText(getSpan(title));
        bindingView.etTitle.setSelection(bindingView.etTitle.getText().toString().length());
        bindingView.etTitle.requestFocus();
        if (!TextUtils.isEmpty(txtJson)) {
            List<LocalMedia> list = new Gson().fromJson(txtJson,
                    new TypeToken<List<LocalMedia>>() {
                    }.getType());
            if (list == null || list.size() == 0) {
                addShowAdd();
            } else {
                mList.clear();
                mList.addAll(list);
                boolean isAdd = false;
                if (list.size() < 4) {
                    for (LocalMedia localMedia : list) {
                        if (localMedia.isShowAdd()) {
                            isAdd = true;
                            break;
                        }
                    }
                    if (!isAdd) {
                        addShowAdd();
                    }
                }
            }
            mPictureAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 富文本
     *
     * @param msg
     * @return
     */
    private SpannableString getSpan(String msg) {
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        SpannableString spannableString = null;
        if (fontSize != null) {
            spannableString = ExpressionUtil.getExpressionString(this, fontSize.intValue(), msg);
        } else {
            spannableString = ExpressionUtil.getExpressionString(this, ExpressionUtil.DEFAULT_SIZE, msg);
        }
        return spannableString;
    }

    @Override
    public void onUpdateChange(List<LocalMedia> selectImages) {
        // 判断添加按钮是否存在
        boolean isFlg = false;
        for (LocalMedia localMedia : selectImages) {
            if (localMedia.isShowAdd()) {
                isFlg = true;
            }
        }
        if (!isFlg) {
            addShowAdd();
        }
        mPictureAdapter.notifyDataSetChanged();
    }

    private void addShowAdd() {
        LocalMedia localMedia = new LocalMedia();
        localMedia.setShowAdd(true);
        mList.add(localMedia);
    }

    @Override
    public void onPicturePreviewClick(LocalMedia media, int position) {
        if (media.isShowAdd()) {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                    .previewImage(false)// 是否可预览图片 true or false
                    .isCamera(true)// 是否显示拍照按钮 ture or false
                    .maxVideoSelectNum(1)
                    .compress(true)// 是否压缩 true or false
                    .isGif(true)
                    .selectArtworkMaster(true)
                    .selectionMedia(getLocalMedia())
                    .maxSelectNum(MAX_NUMBER)
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调 code
        } else {

        }
    }

    private List<LocalMedia> getLocalMedia() {
        List<LocalMedia> list = new ArrayList<>();
        if (mList != null && mList.size() > 0) {
            for (LocalMedia localMedia : mList) {
                if (!localMedia.isShowAdd()) {
                    list.add(localMedia);
                }
            }
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:// 图片回调
                    List<LocalMedia> selectImages = (List<LocalMedia>) data.getSerializableExtra(PictureConfig.EXTRA_RESULT_SELECTION);
                    if (selectImages != null && selectImages.size() > 0) {
                        // 预览图片更新
                        mList.clear();
                        mList.addAll(selectImages);
                        if (mList.size() < MAX_NUMBER) {
                            addShowAdd();
                        }
                        mPictureAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }
}