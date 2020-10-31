package com.luck.picture.lib;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.luck.picture.lib.adapter.SimpleFragmentAdapter;
import com.luck.picture.lib.anim.OptAnimationLoader;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.EventEntity;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.observable.ImagesObservable;
import com.luck.picture.lib.rxbus2.RxBus;
import com.luck.picture.lib.rxbus2.Subscribe;
import com.luck.picture.lib.rxbus2.ThreadMode;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.ToastManage;
import com.luck.picture.lib.tools.VoiceUtils;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropMulti;
import com.yalantis.ucrop.model.CutInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.luck.picture.lib.config.PictureConfig.IS_ARTWORK_MASTER;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：893855882@qq.com
 * data：16/12/31
 */
public class PicturePreviewActivity extends PictureBaseActivity implements
        View.OnClickListener, Animation.AnimationListener, SimpleFragmentAdapter.OnCallBackActivity {
    private ImageView picture_left_back;
    private TextView tv_img_num, tv_title, tv_ok;
    private PreviewViewPager viewPager;
    private RelativeLayout id_ll_ok;
    private int position;
    private LinearLayout ll_check;
    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMedia> selectImages = new ArrayList<>();
    private TextView check;
    private TextView tvEdit;//编辑

    private SimpleFragmentAdapter adapter;
    private Animation animation;
    private boolean refresh;
    private int index;
    private int screenWidth;
    private Handler mHandler;
    public final int EDIT_FROM_ALBUM = 1;//相册预览编辑
    private int fromWhere = 0;//跳转来源 0 默认 1 猜你想要
    private CheckBox cbOrigin;
    private boolean isOrigin;

    /**
     * EventBus 3.0 回调
     *
     * @param obj
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBus(EventEntity obj) {
        switch (obj.what) {
            case PictureConfig.CLOSE_PREVIEW_FLAG:
                // 压缩完后关闭预览界面
                dismissDialog();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 150);
                break;
            case PictureConfig.GUESS_YOU_LIKE:
                Intent intent = PictureSelector.putIntentResult(obj.medias);
                intent.putExtra(IS_ARTWORK_MASTER, true);
                setResult(RESULT_OK, intent);
                // 压缩完后关闭预览界面
                dismissDialog();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                }, 150);
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.picture_preview);
        if (!RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().register(this);
        }
        mHandler = new Handler();
        screenWidth = ScreenUtils.getScreenWidth(this);
        animation = OptAnimationLoader.loadAnimation(this, R.anim.modal_in);
        animation.setAnimationListener(this);
        picture_left_back = (ImageView) findViewById(R.id.picture_left_back);
        viewPager = (PreviewViewPager) findViewById(R.id.preview_pager);
        ll_check = (LinearLayout) findViewById(R.id.ll_check);
        id_ll_ok = findViewById(R.id.id_ll_ok);
        check = (TextView) findViewById(R.id.check);
        picture_left_back.setOnClickListener(this);
        tv_ok = (TextView) findViewById(R.id.tv_ok);
        id_ll_ok.setOnClickListener(this);
        tv_img_num = (TextView) findViewById(R.id.tv_img_num);
        tv_title = (TextView) findViewById(R.id.picture_title);
        tvEdit = findViewById(R.id.tv_edit);
        cbOrigin = findViewById(R.id.cb_original);
        position = getIntent().getIntExtra(PictureConfig.EXTRA_POSITION, 0);
        if (position < 0) {
            position = 0;
        }
        fromWhere = getIntent().getIntExtra(PictureConfig.FROM_WHERE, PictureConfig.FROM_DEFAULT);
        isOrigin = getIntent().getBooleanExtra(PictureConfig.IS_ARTWORK_MASTER, false);
        cbOrigin.setChecked(isOrigin);
        tv_ok.setText(numComplete ? getString(R.string.picture_done_front_num,
                0, config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum)
                : getString(R.string.picture_please_select));

        tv_img_num.setSelected(config.checkNumMode ? true : false);

        selectImages = (List<LocalMedia>) getIntent().
                getSerializableExtra(PictureConfig.EXTRA_SELECT_LIST);
        boolean is_bottom_preview = getIntent().
                getBooleanExtra(PictureConfig.EXTRA_BOTTOM_PREVIEW, false);
        if (is_bottom_preview) {
            // 底部预览按钮过来
            images = (List<LocalMedia>) getIntent().
                    getSerializableExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);
        } else {
            images = ImagesObservable.getInstance().readLocalMedias();
        }
        initViewPageAdapterData();
        ll_check.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void onClick(View view) {
                if (images != null && images.size() > 0) {
                    LocalMedia image = images.get(viewPager.getCurrentItem());
                    String pictureType = selectImages.size() > 0 ?
                            selectImages.get(0).getPictureType() : "";
                    if (!TextUtils.isEmpty(pictureType)) {
                        boolean toEqual = PictureMimeType.
                                mimeToEqual(pictureType, image.getPictureType());
                        if (!toEqual) {
                            ToastManage.s(mContext, getString(R.string.picture_rule));
                            return;
                        }

                    }
                    // 刷新图片列表中图片状态
                    boolean isChecked;
                    if (!check.isSelected()) {
                        if (PictureMimeType.isVideo(pictureType) && (selectImages != null && selectImages.size() > 0)) {
                            String str = getString(R.string.picture_message_video_max_num, 1);
                            ToastManage.s(PicturePreviewActivity.this, str);
                            return;
                        }
                        isChecked = true;
                        check.setSelected(true);
                        check.startAnimation(animation);
                    } else {
                        isChecked = false;
                        check.setSelected(false);
                    }
                    if (selectImages.size() >= config.maxSelectNum && isChecked) {
                        ToastManage.s(mContext, getString(R.string.picture_message_max_num, config.maxSelectNum));
                        check.setSelected(false);
                        return;
                    }
                    if (isChecked) {
                        VoiceUtils.playVoice(mContext, config.openClickSound);
                        // 如果是单选，则清空已选中的并刷新列表(作单一选择)
                        if (config.selectionMode == PictureConfig.SINGLE) {
                            singleRadioMediaImage();
                        }
                        selectImages.add(image);
                        image.setNum(selectImages.size());
                        if (config.checkNumMode) {
                            check.setText(String.valueOf(image.getNum()));
                        }
                    } else {
                        for (LocalMedia media : selectImages) {
                            if (media.getPath().equals(image.getPath())) {
                                selectImages.remove(media);
                                subSelectPosition();
                                notifyCheckChanged(media);
                                break;
                            }
                        }
                    }
                    onSelectNumChange(true);
                }
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                isPreviewEggs(config.previewEggs, position, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int i) {
                position = i;
                tv_title.setText(position + 1 + "/" + images.size());
                LocalMedia media = images.get(position);
                String pictureType = media.getPictureType();
                boolean isGif = PictureMimeType.isGif(pictureType);
                boolean isVideo = PictureMimeType.isVideo(pictureType);
                if (isGif || isVideo) {
                    tvEdit.setVisibility(View.INVISIBLE);
                } else {
                    tvEdit.setVisibility(View.VISIBLE);
                }
                index = media.getPosition();
                if (!config.previewEggs) {
                    if (config.checkNumMode) {
                        check.setText(media.getNum() + "");
                        notifyCheckChanged(media);
                    }
                    onImageChecked(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //新增图片编辑
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //如果当前图片存在于已选中列表则编辑后需要更新已选列表的数据；否则不需要更新已选列表的数据
                int editIndex = getSelectedPosition(images.get(position));
                ARouter.getInstance().build("/weixinrecorded/ImageShowActivity").withString("imgpath", images.get(position).getPath()).withInt("index", editIndex).withInt("img_width", images.get(position).getWidth()).withInt("img_height", images.get(position).getHeight()).navigation(PicturePreviewActivity.this, EDIT_FROM_ALBUM);
            }
        });

        cbOrigin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isOrigin = isChecked;
                updateOrigin();
            }
        });
    }

    /**
     * 这里没实际意义，好处是预览图片时 滑动到屏幕一半以上可看到下一张图片是否选中了
     *
     * @param previewEggs          是否显示预览友好体验
     * @param positionOffsetPixels 滑动偏移量
     */
    private void isPreviewEggs(boolean previewEggs, int position, int positionOffsetPixels) {
        if (previewEggs) {
            if (images.size() > 0 && images != null) {
                LocalMedia media;
                int num;
                if (positionOffsetPixels < screenWidth / 2) {
                    media = images.get(position);
                    check.setSelected(isSelected(media));
                    if (config.checkNumMode) {
                        num = media.getNum();
                        check.setText(num + "");
                        notifyCheckChanged(media);
                        onImageChecked(position);
                    }
                } else {
                    media = images.get(position + 1);
                    check.setSelected(isSelected(media));
                    if (config.checkNumMode) {
                        num = media.getNum();
                        check.setText(num + "");
                        notifyCheckChanged(media);
                        onImageChecked(position + 1);
                    }
                }
            }
        }
    }

    /**
     * 单选图片
     */
    private void singleRadioMediaImage() {
        if (selectImages != null
                && selectImages.size() > 0) {
            LocalMedia media = selectImages.get(0);
            RxBus.getDefault()
                    .post(new EventEntity(PictureConfig.UPDATE_FLAG,
                            selectImages, media.getPosition()));
            selectImages.clear();
        }
    }

    /**
     * 初始化ViewPage数据
     */
    private void initViewPageAdapterData() {
        tv_title.setText(position + 1 + "/" + images.size());
        adapter = new SimpleFragmentAdapter(images, this, this);
        adapter.setShowEditListner(new SimpleFragmentAdapter.ShowEditListner() {
            @Override
            public void showEditItem(boolean ifShow) {
                if (ifShow) {
                    tvEdit.setVisibility(View.VISIBLE);
                } else {
                    tvEdit.setVisibility(View.GONE);
                }
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        onSelectNumChange(false);
        onImageChecked(position);
        if (images.size() > 0) {
            LocalMedia media = images.get(position);
            if (media != null) {
                index = media.getPosition();
                if (config.checkNumMode) {
                    tv_img_num.setSelected(true);
                    check.setText(media.getNum() + "");
                    notifyCheckChanged(media);
                }
                boolean isGif = PictureMimeType.isGif(media.getPictureType());
                boolean isVideo = PictureMimeType.isVideo(media.getPictureType());
                if (isGif || isVideo) {
                    tvEdit.setVisibility(View.INVISIBLE);
                } else {
                    tvEdit.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 选择按钮更新
     */
    private void notifyCheckChanged(LocalMedia imageBean) {
        if (config.checkNumMode) {
            check.setText("");
            for (LocalMedia media : selectImages) {
                if (media.getPath().equals(imageBean.getPath())) {
                    imageBean.setNum(media.getNum());
                    check.setText(String.valueOf(imageBean.getNum()));
                }
            }
        }
    }

    /**
     * 更新选择的顺序
     */
    private void subSelectPosition() {
        for (int index = 0, len = selectImages.size(); index < len; index++) {
            LocalMedia media = selectImages.get(index);
            media.setNum(index + 1);
        }
    }

    /**
     * 判断当前图片是否选中
     *
     * @param position
     */
    public void onImageChecked(int position) {
        if (images != null && images.size() > 0) {
            LocalMedia media = images.get(position);
            check.setSelected(isSelected(media));
        } else {
            check.setSelected(false);
        }
    }

    /**
     * 当前图片是否选中
     *
     * @param image
     * @return
     */
    public boolean isSelected(LocalMedia image) {
        for (LocalMedia media : selectImages) {
            if (media.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前图片在已选列表中的位置(方便编辑后刷新)
     *
     * @param image
     * @return
     */
    public int getSelectedPosition(LocalMedia image) {
        for (int i = 0; i < selectImages.size(); i++) {
            if (selectImages.get(i).getPath().equals(image.getPath())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 更新图片选择数量
     */

    public void onSelectNumChange(boolean isRefresh) {
        this.refresh = isRefresh;
        boolean enable = selectImages.size() != 0;
        if (enable) {
            tv_ok.setSelected(true);
            id_ll_ok.setEnabled(true);
            if (numComplete) {
                tv_ok.setText(getString(R.string.picture_done_front_num, selectImages.size(),
                        config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum));
            } else {
                if (refresh) {
                    tv_img_num.startAnimation(animation);
                }
                tv_img_num.setVisibility(View.VISIBLE);
                tv_img_num.setText(String.valueOf(selectImages.size()));
                tv_ok.setText(getString(R.string.picture_completed));
            }
        } else {
            id_ll_ok.setEnabled(false);
            tv_ok.setSelected(false);
            if (numComplete) {
                tv_ok.setText(getString(R.string.picture_done_front_num, 0,
                        config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum));
            } else {
                tv_img_num.setVisibility(View.INVISIBLE);
                tv_ok.setText(getString(R.string.picture_please_select));
            }
        }
        updateSelector(refresh);
    }

    /**
     * 更新图片列表选中效果
     *
     * @param isRefresh
     */
    private void updateSelector(boolean isRefresh) {
        if (isRefresh) {
            EventEntity obj = new EventEntity(PictureConfig.UPDATE_FLAG, selectImages, index);
            RxBus.getDefault().post(obj);
        }
    }

    /**
     * 更新图片选择原图
     *
     */
    private void updateOrigin() {
        EventEntity obj = new EventEntity(PictureConfig.SELECT_ORIGINAL, selectImages, index);
        RxBus.getDefault().post(obj);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        updateSelector(refresh);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.picture_left_back) {
            onBackPressed();
        }
        if (id == R.id.id_ll_ok) {
            // 如果设置了图片最小选择数量，则判断是否满足条件
            int size = selectImages.size();
            LocalMedia image = selectImages.size() > 0 ? selectImages.get(0) : null;
            String pictureType = image != null ? image.getPictureType() : "";
            if (config.minSelectNum > 0) {
                if (size < config.minSelectNum && config.selectionMode == PictureConfig.MULTIPLE) {
                    boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);
                    @SuppressLint("StringFormatMatches")
                    String str = eqImg ? getString(R.string.picture_min_img_num, config.minSelectNum) : getString(R.string.picture_min_video_num, config.minSelectNum);
                    ToastManage.s(mContext, str);
                    return;
                }
            }
            if (config.enableCrop && pictureType.startsWith(PictureConfig.IMAGE)) {
                if (config.selectionMode == PictureConfig.SINGLE) {
                    originalPath = image.getPath();
                    startCrop(originalPath);
                } else {
                    // 是图片和选择压缩并且是多张，调用批量压缩
                    ArrayList<String> cuts = new ArrayList<>();
                    for (LocalMedia media : selectImages) {
                        cuts.add(media.getPath());
                    }
                    startCrop(cuts);
                }
            } else {
                onResult(selectImages);
            }
        }
    }

    @Override
    public void onResult(List<LocalMedia> images) {
        if (fromWhere == 0) {//默认情况，不影响原有逻辑
            RxBus.getDefault().post(new EventEntity(PictureConfig.PREVIEW_DATA_FLAG, images));
        } else {
            if (images.size() > 0) {
                compressImageAndSendMsg(images);
            }
        }
        // 如果开启了压缩，先不关闭此页面，PictureImageGridActivity压缩完在通知关闭
        if (!config.isCompress) {
            onBackPressed();
        } else {
            showPleaseDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UCropMulti.REQUEST_MULTI_CROP:
                    List<CutInfo> list = UCropMulti.getOutput(data);
                    setResult(RESULT_OK, new Intent().putExtra(UCropMulti.EXTRA_OUTPUT_URI_LIST,
                            (Serializable) list));
                    finish();
                    break;
                case UCrop.REQUEST_CROP:
                    if (data != null) {
                        setResult(RESULT_OK, data);
                    }
                    finish();
                    break;
                case EDIT_FROM_ALBUM:
                    if (!TextUtils.isEmpty(data.getStringExtra("showPath"))) {
                        images.get(position).setPath(data.getStringExtra("showPath"));

                        adapter.notifyDataSetChanged();//刷新显示编辑后的图片
                        //不为-1，说明selectImages中存在，即已选图片编辑后，需要刷新数据
                        if (data.getIntExtra("index", -1) != -1) {
                            int tempIndex = data.getIntExtra("index", -1);
                            selectImages.get(tempIndex).setPath(data.getStringExtra("showPath"));
                        } else {//没有选中，直接选中
                            ll_check.performClick();
                        }
                    }
                    break;
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable throwable = (Throwable) data.getSerializableExtra(UCrop.EXTRA_ERROR);
            ToastManage.s(mContext, throwable.getMessage());
        }
    }


    @Override
    public void onBackPressed() {
        closeActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().unregister(this);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
    }

    @Override
    public void onActivityBackPressed() {
        onBackPressed();
    }
}
