package com.yanlong.im.circle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.luck.picture.lib.OnPhotoPreviewChangedListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.adapter.PicturePreviewAdapter;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.ScreenUtils;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityVotePictrueBinding;

import net.cb.cb.library.base.bind.BaseBindActivity;
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
        addShowAdd();
        mPictureAdapter = new PicturePreviewAdapter(this, mList, this);
        bindingView.recyclerView.addItemDecoration(new GridSpacingItemDecoration(3,
                ScreenUtils.dip2px(this, 10), false));
        bindingView.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
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
            }
        });
    }

    @Override
    protected void loadData() {

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
    public void onPicturePrviewClick(LocalMedia media, int position) {
        if (media.isShowAdd()) {

            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
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