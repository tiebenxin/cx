package com.yanlong.im.user.ui.freeze;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.ScreenUtils;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityAppealAccountBinding;
import com.yanlong.im.user.bean.ImageBean;
import com.yanlong.im.user.ui.FeedbackShowImageActivity;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.PopupSelectView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Liszt
 * @date 2020/6/3
 * Description 账号申诉
 */
public class AppealAccountActivity extends AppActivity {
    public static final int SHOW_IMAGE = 9038;
    private ActivityAppealAccountBinding ui;
    private List<ImageBean> list = new ArrayList<>();
    private ComplaintUploadAdapter adapter;
    private PopupSelectView popupSelectView;
    private String[] strings = {"手机相册", "拍照", "取消"};
    private CheckPermission2Util permission2Util = new CheckPermission2Util();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_appeal_account);

        initData();

        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        ui.recyclerView.addItemDecoration(new GridSpacingItemDecoration(3,
                ScreenUtils.dip2px(this, 10), false));
        ui.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ComplaintUploadAdapter();
        ui.recyclerView.setAdapter(adapter);
        ui.edContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ui.tvWords.setText(s.toString().length()+"/300");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void initData() {
        ImageBean imageBean = new ImageBean();
        imageBean.setType(0);
        list.add(imageBean);
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
                            for (int i = 0; i < list.size(); i++) {
                                final String file = PictureSelector.obtainMultipleResult(data).get(i).getCompressPath();
                                final Uri uri = Uri.fromFile(new File(file));
                                if (!alert.isShown()) {
                                    alert.show();
                                }
                                new UpFileAction().upFile(UpFileAction.PATH.FEEDBACK, getContext(), new UpFileUtil.OssUpCallback() {
                                    @Override
                                    public void success(final String url) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                alert.dismiss();
                                                ImageBean imageBean = new ImageBean();
                                                imageBean.setType(1);
                                                imageBean.setUrl(url);
                                                imageBean.setPath(uri);
                                                adapter.addImage(imageBean);
                                            }
                                        });
                                    }

                                    @Override
                                    public void fail() {
                                        alert.dismiss();
                                        ToastUtil.show(getContext(), "上传失败!");
                                    }

                                    @Override
                                    public void inProgress(long progress, long zong) {

                                    }
                                }, file);
                            }
                        }


                    }

                    break;
                case SHOW_IMAGE:
                    int postion = data.getIntExtra(FeedbackShowImageActivity.POSTION, 0);
                    adapter.remove(postion);
                    break;
            }
        }
    }


    private class ComplaintUploadAdapter extends RecyclerView.Adapter<ComplaintUploadAdapter.ComplaintUploadViewHolder> {

        public void addImage(ImageBean imageBean) {
            if (list.size() == 3) {
                list.remove(2);
                list.add(list.size(), imageBean);
            } else {
                list.add(list.size() - 1, imageBean);
            }
            ui.recyclerView.getAdapter().notifyDataSetChanged();
        }

        public void remove(int position) {
            if (list.size() == 3) {
                if (list.get(2).getType() == 0) {
                    list.remove(position);
                } else {
                    list.remove(position);
                    ImageBean imageBean = new ImageBean();
                    imageBean.setType(0);
                    list.add(imageBean);
                }
            } else {
                list.remove(position);
            }
            this.notifyDataSetChanged();
        }


        public int getNum() {
            if (list == null && list.size() >= 0) {
                return 0;
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getType() == 0) {
                        return list.size() - 1;
                    }
                }
                return list.size();
            }
        }

        @Override
        public ComplaintUploadViewHolder onCreateViewHolder(@android.support.annotation.NonNull ViewGroup viewGroup, int i) {
            View view = inflater.inflate(R.layout.item_feedback, viewGroup, false);
            return new ComplaintUploadViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@android.support.annotation.NonNull ComplaintUploadViewHolder viewHolder, final int i) {

            ImageBean imageBean = list.get(i);
            if (imageBean.getType() == 0) {
                viewHolder.imageView.setImageResource(R.mipmap.icon_image_add);
                // viewHolder.imageView.setImageURI("android.resource://" + getPackageName() + "/" + R.mipmap.icon_image_add);
                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initPopup();
                    }
                });
            } else {
                Glide.with(context).load(imageBean.getPath())
                        .apply(GlideOptionsUtil.defImageOptions1()).into(viewHolder.imageView);

                viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AppealAccountActivity.this, FeedbackShowImageActivity.class);
                        intent.putExtra(FeedbackShowImageActivity.URL, list.get(i).getUrl());
                        intent.putExtra(FeedbackShowImageActivity.POSTION, i);
                        intent.putExtra(FeedbackShowImageActivity.TYPE, 1);
                        startActivityForResult(intent, SHOW_IMAGE);
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            if (list != null && list.size() > 0) {
                return list.size();
            }
            return 0;
        }


        class ComplaintUploadViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;

            public ComplaintUploadViewHolder(@android.support.annotation.NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image_view);
            }
        }
    }

    private void initPopup() {
        popupSelectView = new PopupSelectView(this, strings);
        popupSelectView.showAtLocation(ui.headView, Gravity.BOTTOM, 0, 0);
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
                                .maxSelectNum(3 - adapter.getNum())
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

}
