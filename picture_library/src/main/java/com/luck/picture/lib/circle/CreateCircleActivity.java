package com.luck.picture.lib.circle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.OnPhotoSelectChangedListener;
import com.luck.picture.lib.PictureBaseActivity;
import com.luck.picture.lib.PictureEnum;
import com.luck.picture.lib.PicturePreviewActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.R;
import com.luck.picture.lib.adapter.PictureAlbumDirectoryAdapter;
import com.luck.picture.lib.adapter.PictureImageGridAdapter;
import com.luck.picture.lib.adapter.PicturePreviewAdapter;
import com.luck.picture.lib.audio.AudioPlayManager;
import com.luck.picture.lib.audio.AudioPlayUtil;
import com.luck.picture.lib.audio.AudioRecorder;
import com.luck.picture.lib.audio.IAudioPlayListener;
import com.luck.picture.lib.audio.IAudioPlayProgressListener;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.dialog.CustomDialog;
import com.luck.picture.lib.entity.AttachmentBean;
import com.luck.picture.lib.entity.EventEntity;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.event.EventFactory;
import com.luck.picture.lib.face.FaceView;
import com.luck.picture.lib.face.FaceViewPager;
import com.luck.picture.lib.face.bean.FaceBean;
import com.luck.picture.lib.immersive.ImmersiveManage;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.observable.ImagesObservable;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.rxbus2.RxBus;
import com.luck.picture.lib.rxbus2.Subscribe;
import com.luck.picture.lib.rxbus2.ThreadMode;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.luck.picture.lib.tools.ToastManage;
import com.luck.picture.lib.utils.CheckPermission2Util;
import com.luck.picture.lib.utils.DateUtil;
import com.luck.picture.lib.utils.ExpressionUtil;
import com.luck.picture.lib.utils.GroupHeadImageUtil;
import com.luck.picture.lib.utils.InputUtil;
import com.luck.picture.lib.utils.PatternUtil;
import com.luck.picture.lib.utils.SoftKeyBoardListener;
import com.luck.picture.lib.widget.CustomerEditText;
import com.luck.picture.lib.widget.DonutProgress;
import com.luck.picture.lib.widget.FolderPopWindow;
import com.luck.picture.lib.widget.PhotoPopupWindow;
import com.luck.picture.lib.widget.PhotoPopupWindow.OnItemClickListener;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropMulti;
import com.yalantis.ucrop.model.CutInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author：luck
 * @date：2018/1/27 19:12
 * @描述: Media 选择页面
 */
public class CreateCircleActivity extends PictureBaseActivity implements View.OnClickListener, PictureAlbumDirectoryAdapter.OnItemClickListener,
        OnPhotoSelectChangedListener, OnItemClickListener, IAudioPlayProgressListener, OnPhotoPreviewChangedListener {
    private final String TAG = CreateCircleActivity.class.getSimpleName();
    private static final int SHOW_DIALOG = 0;
    private static final int DISMISS_DIALOG = 1;
    private static final int REQUEST_CODE_LOCATION = 100;
    private static final int REQUEST_CODE_POWER = 200;
    private static final int REQUEST_CODE_VOTE_TXT = 300;
    private static final int REQUEST_CODE_VOTE_PICTRUE = 400;
    private final int MAX_COUNT = 500;// 最大字数
    public static final String INTENT_LOCATION_NAME = "intent_location_name";
    public static final String INTENT_LOCATION_DESC = "intent_location_desc";
    public static final String CITY_NAME = "city_name";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String INTENT_POWER = "intent_power";
    public static final String VOTE_TXT = "vote_txt";
    public static final String VOTE_LOCATION_IMG = "vote_location_img";
    public static final String VOTE_TXT_TITLE = "vote_txt_title";
    private final String ADDRESS_NOT_SHOW = "不显示位置";
    private String mCityName = "", mLocationDesc = "", mTxtJson = "", mImgJson = "";
    private ImageView picture_left_back, iv_face, iv_picture, iv_vote, iv_voice, iv_delete_voice,
            iv_voice_play, iv_delete_location, iv_delete_vote;
    private TextView picture_title, picture_right, picture_tv_ok, tv_empty,
            picture_tv_img_num, picture_id_preview, tv_PlayPause, tv_Stop, tv_Quit,
            tv_musicStatus, tv_musicTotal, tv_musicTime, tv_location, tv_power,
            tv_content_vote, tv_picture_vote, tv_max_number, tv_time;
    private CheckBox cb_original;
    private ProgressBar pb_progress;
    private CustomerEditText etContent;
    private RelativeLayout rl_picture_title, layout_audio;
    private LinearLayout id_ll_ok, layout_vote, layoutFunc, layout_voice, layout_vote_content;
    private FrameLayout frame_content;
    private RecyclerView picture_recycler, recycler_picture_prview;
    private PictureImageGridAdapter adapter;
    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMediaFolder> foldersList = new ArrayList<>();
    private FolderPopWindow folderWindow;
    private Animation animation = null;
    private boolean anim = false;
    private RxPermissions rxPermissions;
    private PhotoPopupWindow popupWindow;
    private LocalMediaLoader mediaLoader;
    private MediaPlayer mediaPlayer;
    private SeekBar musicSeekBar;
    private boolean isPlayAudio = false;
    private CustomDialog audioDialog;
    private int audioH;
    private double mLatitude, mLongitude;
    private int mKeyboardHeight = 0;// 记录软键盘的高度
    private int mFuncHeight = 0;// 功能面板默认高度

    // 录音相关
    private boolean isOpenSoft = false;// 是否打开软件盘
    private boolean isShowFace = true;// 是否显示表情
    private boolean isPlaying = false;// 是否在播放录音
    private boolean isRecording, isExistVoice = false;// 是否在录音\是否存在录音
    private int mAudioState = 0;//0未录音 1在录音 2停止录音
    private final int MAX_SECOND = 90;// 录制最大90秒
    private AudioRecorder recorder;// 录音对象
    private String currentAudioFile;// 录音文件
    private DonutProgress dp_action;// 录音进度条
    private ImageView iv_action, iv_reset, iv_confirm, iv_voice_bg;
    private TextView tv_voice_time, tv_voice_tip;
    private CheckPermission2Util permission2Util = new CheckPermission2Util();

    // 选择的图片集合
    private List<LocalMedia> mList = new ArrayList<>();
    // 投票图片
    private List<LocalMedia> mVoteList = new ArrayList<>();
    private PicturePreviewAdapter mPictureAdapter;
    private boolean isRestHeight = true;// 是否重设高度

    private FaceView circle_view_faceview;

    private boolean isArtworkMaster;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_DIALOG:
                    showPleaseDialog();
                    break;
                case DISMISS_DIALOG:
                    dismissDialog();
                    break;
            }
        }
    };
    private int trendModel;
    private boolean hasChangeModel;
    private boolean isVoteTextEditSuccess;//是否文字投票编辑成功
    private boolean isVoteImageEditSuccess;//是否图片投票编辑成功

    @Override
    protected void closeActivity() {
    }

    /**
     * EventBus 3.0 回调
     *
     * @param obj
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBus(EventEntity obj) {
        switch (obj.what) {
            case PictureConfig.UPDATE_FLAG:
                // 预览时勾选图片更新回调
                List<LocalMedia> selectImages = obj.medias;
                anim = selectImages.size() > 0 ? true : false;
                int position = obj.position;
                Log.i("刷新下标:", String.valueOf(position));
                adapter.bindSelectImages(selectImages);
                adapter.notifyItemChanged(position);

                break;
            case PictureConfig.PREVIEW_DATA_FLAG:
                List<LocalMedia> medias = obj.medias;
                if (medias.size() > 0) {
                    // 取出第1个判断是否是图片，视频和图片只能二选一，不必考虑图片和视频混合
                    String pictureType = medias.get(0).getPictureType();
                    if (config.isCompress && pictureType.startsWith(PictureConfig.IMAGE)) {
                        compressImage(medias);
                    } else {
                        RxBus.getDefault().post(new EventEntity(PictureConfig.CLOSE_PREVIEW_FLAG));
                        onResult(medias);
                    }
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshChat(EventFactory.CreateSuccessEvent event) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().register(this);
        }
        ImmersiveManage.immersiveAboveAPI23(this
                , getResources().getColor(R.color.white)
                , colorPrimary
                , true);
        rxPermissions = new RxPermissions(this);
        if (config.camera) {
            if (savedInstanceState == null) {
                rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (aBoolean) {
                                    onTakePhoto();
                                } else {
                                    ToastManage.s(mContext, getString(R.string.picture_camera));
                                    closeActivity();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
            }
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                    , WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.picture_empty);
        } else {
            setContentView(R.layout.activity_create_circle);
            initView(savedInstanceState);
        }
        initEvent();
    }

    private void initEvent() {
        mFuncHeight = ScreenUtils.dip2px(CreateCircleActivity.this, 230);
        SoftKeyBoardListener kbLinst = new SoftKeyBoardListener(this);
        kbLinst.setOnSoftKeyBoardChangeListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int h) {
                isOpenSoft = true;
                isRestHeight = true;
                frame_content.setVisibility(View.VISIBLE);
                resetingBtn();
                setRecyclerViewHeight(h);
            }

            @Override
            public void keyBoardHide(int h) {
                isOpenSoft = false;
                if (isShowFace) {
                    frame_content.setVisibility(View.GONE);
                }
                isRestHeight = true;
                setRecyclerViewHeight(mFuncHeight);
            }

            @Override
            public void keyBoardChange(int h) {
                setRecyclerViewHeight(mKeyboardHeight + h);
            }
        });

        // 表情点击事件
        circle_view_faceview.setOnItemClickListener(new FaceViewPager.FaceClickListener() {

            @Override
            public void OnItemClick(FaceBean bean) {
                if ((etContent.getText().toString() + bean.getName()).length() > MAX_COUNT) {
                    showTaost("最多可以输入" + MAX_COUNT + "字符");
                } else {
                    etContent.addEmojSpan(CreateCircleActivity.this, bean.getName());
                }
            }
        });
        // 删除表情按钮
        circle_view_faceview.setOnDeleteListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int selection = etContent.getSelectionStart();
                    String msg = etContent.getText().toString().trim();
                    if (selection >= 1) {
                        if (selection >= PatternUtil.FACE_EMOJI_LENGTH) {
                            String emoji = msg.substring(selection - PatternUtil.FACE_EMOJI_LENGTH, selection);
                            if (PatternUtil.isExpression(emoji)) {
                                etContent.getText().delete(selection - PatternUtil.FACE_EMOJI_LENGTH, selection);
                                return;
                            }
                        }
                        etContent.getText().delete(selection - 1, selection);
                    }
                } catch (Exception e) {
                }
            }
        });
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                tv_max_number.setText(s.length() + "/" + MAX_COUNT);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (s.length() > MAX_COUNT) {
//                    etContent.setSelection(MAX_COUNT);
//                    showTaost("最多可以输入" + MAX_COUNT + "字符");
//                }
            }
        });
    }

    private void setRecyclerViewHeight(int height) {
        if (height > 0 && isRestHeight) {
            mKeyboardHeight = height;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            frame_content.setLayoutParams(layoutParams);
        }
    }

    /**
     * init views
     */
    private void initView(Bundle savedInstanceState) {
        cb_original = findViewById(R.id.cb_original);
        rl_picture_title = (RelativeLayout) findViewById(R.id.rl_picture_title);
        picture_left_back = (ImageView) findViewById(R.id.picture_left_back);
        picture_title = (TextView) findViewById(R.id.picture_title);
        picture_right = (TextView) findViewById(R.id.picture_right);
        picture_tv_ok = (TextView) findViewById(R.id.picture_tv_ok);
        picture_id_preview = (TextView) findViewById(R.id.picture_id_preview);
        picture_tv_img_num = (TextView) findViewById(R.id.picture_tv_img_num);
        picture_recycler = (RecyclerView) findViewById(R.id.picture_recycler);
        recycler_picture_prview = findViewById(R.id.recycler_picture_prview);
        layout_vote_content = findViewById(R.id.layout_vote_content);
        id_ll_ok = (LinearLayout) findViewById(R.id.id_ll_ok);
        layoutFunc = findViewById(R.id.view_func);
        tv_empty = (TextView) findViewById(R.id.tv_empty);
        etContent = findViewById(R.id.et_content);
        tv_location = findViewById(R.id.tv_location);
        tv_power = findViewById(R.id.tv_power);
        frame_content = findViewById(R.id.frame_content);
        iv_picture = findViewById(R.id.iv_picture);
        iv_face = findViewById(R.id.iv_face);
        layout_vote = findViewById(R.id.layout_vote);
        layout_audio = findViewById(R.id.layout_audio);
        iv_vote = findViewById(R.id.iv_vote);
        iv_voice = findViewById(R.id.iv_voice);
        tv_max_number = findViewById(R.id.tv_max_number);
        tv_content_vote = findViewById(R.id.tv_content_vote);
        tv_picture_vote = findViewById(R.id.tv_picture_vote);
        circle_view_faceview = findViewById(R.id.circle_view_faceview);

        iv_delete_vote = findViewById(R.id.iv_delete_vote);
        layout_voice = findViewById(R.id.layout_voice);
        iv_delete_voice = findViewById(R.id.iv_delete_voice);
        pb_progress = findViewById(R.id.pb_progress);
        tv_time = findViewById(R.id.tv_time);
        iv_voice_play = findViewById(R.id.iv_voice_play);
        iv_delete_location = findViewById(R.id.iv_delete_location);

        dp_action = findViewById(R.id.dp_action);
        iv_action = findViewById(R.id.iv_action);
        iv_reset = findViewById(R.id.iv_reset);
        iv_confirm = findViewById(R.id.iv_confirm);
        tv_voice_time = findViewById(R.id.tv_voice_time);
        tv_voice_tip = findViewById(R.id.tv_voice_tip);
        iv_voice_bg = findViewById(R.id.iv_voice_bg);
        iv_picture.setImageLevel(1);
        isNumComplete(numComplete);
        if (config.mimeType == PictureMimeType.ofAll()) {
            popupWindow = new PhotoPopupWindow(this);
            popupWindow.setOnItemClickListener(this);
        }

        if (config.isArtworkMaster) {
            cb_original.setVisibility(View.VISIBLE);
            cb_original.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        CreateCircleActivity.this.isArtworkMaster = true;
                    } else {
                        CreateCircleActivity.this.isArtworkMaster = false;
                    }
                    setOriginImageSize();

                }
            });
        } else {
            cb_original.setVisibility(View.GONE);
        }
        tv_content_vote.setOnClickListener(this);
        tv_picture_vote.setOnClickListener(this);
        tv_location.setOnClickListener(this);
        tv_power.setOnClickListener(this);
        iv_vote.setOnClickListener(this);
        iv_voice.setOnClickListener(this);
        dp_action.setOnClickListener(this);
        iv_reset.setOnClickListener(this);
        iv_confirm.setOnClickListener(this);
        iv_delete_voice.setOnClickListener(this);
        picture_id_preview.setOnClickListener(this);
        iv_voice_play.setOnClickListener(this);
        iv_delete_location.setOnClickListener(this);
        iv_delete_vote.setOnClickListener(this);

        if (config.mimeType == PictureMimeType.ofAudio()) {
            picture_id_preview.setVisibility(View.GONE);
            audioH = ScreenUtils.getScreenHeight(mContext)
                    + ScreenUtils.getStatusBarHeight(mContext);
        } else {
            picture_id_preview.setVisibility(config.mimeType == PictureConfig.TYPE_VIDEO
                    ? View.GONE : View.VISIBLE);
        }
        picture_left_back.setOnClickListener(this);
        picture_right.setOnClickListener(this);
        id_ll_ok.setOnClickListener(this);
        iv_picture.setOnClickListener(this);
        iv_face.setOnClickListener(this);
//        picture_title.setOnClickListener(this);
//        String title = config.mimeType == PictureMimeType.ofAudio() ?
//                getString(R.string.picture_all_audio)
//                : getString(R.string.picture_camera_roll);
//        picture_title.setText(title);
        folderWindow = new FolderPopWindow(this, config.mimeType);
        folderWindow.setPictureTitleView(picture_title);
        folderWindow.setOnItemClickListener(this);
        picture_recycler.setHasFixedSize(true);
        picture_recycler.addItemDecoration(new GridSpacingItemDecoration(config.imageSpanCount,
                ScreenUtils.dip2px(this, 2), false));
        picture_recycler.setLayoutManager(new GridLayoutManager(this, config.imageSpanCount));
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        ((SimpleItemAnimator) picture_recycler.getItemAnimator())
                .setSupportsChangeAnimations(false);
        mediaLoader = new LocalMediaLoader(this, config.mimeType, config.isGif, config.videoMaxSecond, config.videoMinSecond);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            mHandler.sendEmptyMessage(SHOW_DIALOG);
                            readLocalMedia();
                        } else {
                            ToastManage.s(mContext, getString(R.string.picture_jurisdiction));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        tv_empty.setText(config.mimeType == PictureMimeType.ofAudio() ?
                getString(R.string.picture_audio_empty)
                : getString(R.string.picture_empty));
        StringUtils.tempTextFont(tv_empty, config.mimeType);
        if (savedInstanceState != null) {
            // 防止拍照内存不足时activity被回收，导致拍照后的图片未选中
            selectionMedias = PictureSelector.obtainSelectorList(savedInstanceState);
        }
        adapter = new PictureImageGridAdapter(mContext, config);
        adapter.setOnPhotoSelectChangedListener(CreateCircleActivity.this);
        adapter.bindSelectImages(selectionMedias);
        picture_recycler.setAdapter(adapter);
        String titleText = picture_title.getText().toString().trim();
        if (config.isCamera) {
            config.isCamera = StringUtils.isCamera(titleText);
        }

        mPictureAdapter = new PicturePreviewAdapter(this, mList, this);
        recycler_picture_prview.addItemDecoration(new GridSpacingItemDecoration(4,
                ScreenUtils.dip2px(this, 5), false));
        recycler_picture_prview.setLayoutManager(new GridLayoutManager(this, 4));
        recycler_picture_prview.setAdapter(mPictureAdapter);
    }

    private void setOriginImageSize() {
        if (isArtworkMaster) {
            String size = getImageSize();
            if (!TextUtils.isEmpty(size)) {
                cb_original.setText("原图（" + size + ")");
            } else {
                cb_original.setText("原图");
            }
        } else {
            cb_original.setText("原图");

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            List<LocalMedia> selectedImages = adapter.getSelectedImages();
            PictureSelector.saveSelectorList(outState, selectedImages);
        }
    }

    /**
     * none number style
     */
    private void isNumComplete(boolean numComplete) {
        picture_tv_ok.setText(numComplete ? getString(R.string.picture_done_front_num,
                0, config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum)
                : getString(R.string.picture_please_select));
        if (!numComplete) {
            animation = AnimationUtils.loadAnimation(this, R.anim.modal_in);
        }
        animation = numComplete ? null : AnimationUtils.loadAnimation(this, R.anim.modal_in);
    }

    /**
     * get LocalMedia s
     */
    protected void readLocalMedia() {
        mediaLoader.loadAllMedia(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                if (folders.size() > 0) {
                    foldersList = folders;
                    LocalMediaFolder folder = folders.get(0);
                    folder.setChecked(true);
                    List<LocalMedia> localImg = folder.getImages();
                    // 这里解决有些机型会出现拍照完，相册列表不及时刷新问题
                    // 因为onActivityResult里手动添加拍照后的照片，
                    // 如果查询出来的图片大于或等于当前adapter集合的图片则取更新后的，否则就取本地的
                    if (localImg.size() >= images.size()) {
                        images = localImg;
                        folderWindow.bindFolder(folders);
                    }
                }
                if (adapter != null) {
                    if (images == null) {
                        images = new ArrayList<>();
                    }
                    adapter.bindImagesData(images);
//                    tv_empty.setVisibility(images.size() > 0
//                            ? View.INVISIBLE : View.VISIBLE);
                }
                mHandler.sendEmptyMessage(DISMISS_DIALOG);
            }
        });
    }

    /**
     * open camera
     */
    public void startCamera() {
        // 防止快速点击，但是单独拍照不管
        if (!DoubleUtils.isFastDoubleClick() || config.camera) {
            switch (config.mimeType) {
                case PictureConfig.TYPE_ALL:
                    // 如果是全部类型下，单独拍照就默认图片 (因为单独拍照不会new此PopupWindow对象)
                    if (popupWindow != null) {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        if (mList != null && mList.size() > 0) {
                            LocalMedia media = mList.get(0);
                            if (PictureMimeType.isVideo(media.getPictureType())) {
                                popupWindow.setTakeMode(PhotoPopupWindow.ETakeModel.VIDEO);
                            } else {
                                popupWindow.setTakeMode(PhotoPopupWindow.ETakeModel.PHOTO);
                            }
                        } else {
                            popupWindow.setTakeMode(PhotoPopupWindow.ETakeModel.ALL);
                        }
                        popupWindow.showAsDropDown(rl_picture_title);
                    } else {
                        startOpenCamera();
                    }
                    break;
                case PictureConfig.TYPE_IMAGE:
                    // 拍照
                    startOpenCamera();
                    break;
                case PictureConfig.TYPE_VIDEO:
                    // 录视频
                    startOpenCameraVideo();
                    break;
                case PictureConfig.TYPE_AUDIO:
                    // 录音
                    startOpenCameraAudio();
                    break;
            }
        }
    }

    /**
     * start to camera、preview、crop
     */
    public void startOpenCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            int type = config.mimeType == PictureConfig.TYPE_ALL ? PictureConfig.TYPE_IMAGE : config.mimeType;
            File cameraFile = PictureFileUtils.createCameraFile(this,
                    type,
                    outputCameraPath, config.suffixType);
            cameraPath = cameraFile.getAbsolutePath();
            Uri imageUri = parUri(cameraFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
        }
    }

    /**
     * start to camera、video
     */
    public void startOpenCameraVideo() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File cameraFile = PictureFileUtils.createCameraFile(this, config.mimeType ==
                            PictureConfig.TYPE_ALL ? PictureConfig.TYPE_VIDEO : config.mimeType,
                    outputCameraPath, config.suffixType);
            cameraPath = cameraFile.getAbsolutePath();
            Uri imageUri = parUri(cameraFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, config.recordVideoSecond);
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, config.videoQuality);
            startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
        }
    }

    /**
     * start to camera audio
     */
    public void startOpenCameraAudio() {
        rxPermissions.request(Manifest.permission.RECORD_AUDIO).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    Intent cameraIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
                    }
                } else {
                    ToastManage.s(mContext, getString(R.string.picture_audio));
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    /**
     * 生成uri
     *
     * @param cameraFile
     * @return
     */
    private Uri parUri(File cameraFile) {
        Uri imageUri;
        String authority = getPackageName() + ".provider";
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(mContext, authority, cameraFile);
        } else {
            imageUri = Uri.fromFile(cameraFile);
        }
        return imageUri;
    }

    private void showTaost(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }

    private void createNewCircle() {
        String content = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(content) && TextUtils.isEmpty(currentAudioFile) && (mList == null || mList.size() <= 0)) {
            etContent.requestFocus();
            showTaost("请输入动态内容");
            return;
        }
        int visibility = 0;// 可见度(0:广场可见|1:好友可见|2:陌生人可见|3:自己可见)
        switch (tv_power.getText().toString()) {
            case "广场可见":
                visibility = 0;
                break;
            case "仅好友可见":
                visibility = 1;
                break;
            case "仅陌生人可见":
                visibility = 2;
                break;
            case "自己可见":
                visibility = 3;
                break;
        }

        EventFactory.CreateCircleEvent createCircleEvent = new EventFactory.CreateCircleEvent();
        EventFactory.CreateCircleEvent.CircleBean circleBean = new EventFactory.CreateCircleEvent.CircleBean();
        circleBean.setContent(content);
        circleBean.setVisibility(visibility);
        circleBean.setType(PictureEnum.EContentType.TXT);
        if (!TextUtils.isEmpty(tv_location.getText().toString()) &&
                !tv_location.getText().toString().equals(getString(R.string.you_location))) {
            circleBean.setPosition(tv_location.getText().toString());
            circleBean.setCity(mCityName);
            circleBean.setLatitude(mLatitude + "");
            circleBean.setLongitude(mLongitude + "");
        }
        // 附件
        Gson gson = new Gson();
        List<AttachmentBean> list = new ArrayList<>();
        if (!TextUtils.isEmpty(currentAudioFile)) {
            AttachmentBean attachmentBean = new AttachmentBean();
            attachmentBean.setUrl(currentAudioFile);
            attachmentBean.setDuration(getSendTime(time));
            attachmentBean.setType(PictureEnum.EContentType.VOICE);
            list.add(attachmentBean);
            circleBean.setAttachment(gson.toJson(list));
            circleBean.setType(PictureEnum.EContentType.VOICE);
        } else if (mList != null && mList.size() > 0) {// 图片或视频
            for (LocalMedia localMedia : mList) {
                if (!localMedia.isShowAdd()) {
                    AttachmentBean attachment = new AttachmentBean();
                    attachment.setUrl(localMedia.getPath());
                    attachment.setWidth(localMedia.getWidth());
                    attachment.setHeight(localMedia.getHeight());
                    if (PictureMimeType.isPictureType(localMedia.getPictureType()) == PictureMimeType.ofImage()) {
                        attachment.setType(PictureEnum.EContentType.PICTRUE);
                        circleBean.setType(PictureEnum.EContentType.PICTRUE);
                    } else {
                        attachment.setBgUrl((getVideoAttBitmap(localMedia.getPath())));
                        attachment.setType(PictureEnum.EContentType.VIDEO);
                        circleBean.setType(PictureEnum.EContentType.VIDEO);
                    }
                    list.add(attachment);
                }
            }
            if (PictureEnum.EContentType.PICTRUE == circleBean.getType()) {
                // 移除加号
                for (int i = mList.size() - 1; i >= 0; i--) {
                    if (mList.get(i).isShowAdd()) {
                        mList.remove(i);
                        break;
                    }
                }
                createCircleEvent.setList(mList);
            }
            circleBean.setAttachment(gson.toJson(list));
        }
        if (!TextUtils.isEmpty(mTxtJson) || !TextUtils.isEmpty(mImgJson)) {
            if (!TextUtils.isEmpty(mTxtJson)) {
                circleBean.setVote(mTxtJson);
            } else {
                circleBean.setVote(mImgJson);
                createCircleEvent.setList(mVoteList);
            }
            if (!TextUtils.isEmpty(currentAudioFile)) {
                circleBean.setType(PictureEnum.EContentType.VOICE_AND_VOTE);
            } else if (mList != null && mList.size() > 0) {
                if (PictureMimeType.isPictureType(mList.get(0).getPictureType()) == PictureMimeType.ofImage()) {
                    circleBean.setType(PictureEnum.EContentType.PICTRUE_AND_VOTE);
                } else {
                    circleBean.setType(PictureEnum.EContentType.VIDEO_AND_VOTE);
                }
            } else {
                circleBean.setType(PictureEnum.EContentType.VOTE);
            }
        }
        createCircleEvent.context = this;
        createCircleEvent.circleBean = circleBean;
        EventBus.getDefault().post(createCircleEvent);
    }

    /**
     * 获取视频第一帧
     *
     * @param mUri
     * @return
     */
    private String getVideoAttBitmap(String mUri) {
        String path = "";
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        try {
            if (mUri != null) {
                FileInputStream inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());
            } else {
            }
            while (mmr.getFrameAtTime() == null) {
                Thread.sleep(1000);
            }
            File file = GroupHeadImageUtil.save2File(this, mmr.getFrameAtTime());
            if (file != null) {
                path = file.getAbsolutePath();
            }
        } catch (Exception ex) {
        } finally {
            mmr.release();
        }
        return path;
    }

    private void resetingBtn() {
        iv_picture.setImageLevel(0);
        iv_vote.setImageLevel(0);
        iv_voice.setImageLevel(0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.picture_left_back) {
            if (folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                finish();
                overridePendingTransition(0, R.anim.fade_out);
            }
        } else if (id == R.id.picture_right) {// 发布
            if (!DoubleUtils.isFastDoubleClick()) {
                createNewCircle();
            }
        } else if (id == R.id.picture_title) {
            if (folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                if (images != null && images.size() > 0) {
                    folderWindow.showAsDropDown(rl_picture_title);
                    List<LocalMedia> selectedImages = adapter.getSelectedImages();
                    folderWindow.notifyDataCheckedStatus(selectedImages);
                }
            }
        } else if (id == R.id.picture_id_preview) {
            List<LocalMedia> previewList = new ArrayList<>();
            for (LocalMedia media : adapter.getSelectedImages()) {
                previewList.add(media);
            }
            previewImage(previewList, adapter.getSelectedImages(), 0);
            overridePendingTransition(R.anim.a5, 0);
        } else if (id == R.id.tv_location) {
            if (!DoubleUtils.isFastDoubleClick()) {
                Postcard postcard = ARouter.getInstance().build("/circle/LocationCircleActivity");
                String city = tv_location.getText().toString();
                if (city.equals(getString(R.string.you_location))) {
                    city = "";
                }
                postcard.withString("address_name", city);
                postcard.withString("address_desc", mLocationDesc);
                postcard.withString("city_name", mCityName);
                postcard.withDouble("latitude", mLatitude);
                postcard.withDouble("longitude", mLongitude);
                postcard.navigation(this, REQUEST_CODE_LOCATION);
            }
        } else if (id == R.id.iv_delete_location) {// 删除位置
            Drawable drawable = getResources().getDrawable(R.mipmap.ic_circle_location_gray);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
            tv_location.setCompoundDrawables(drawable, null, null, null);
            tv_location.setTextColor(getResources().getColor(R.color.color_b1b));
            tv_location.setText(getString(R.string.you_location));
            iv_delete_location.setVisibility(View.GONE);
        } else if (id == R.id.tv_power) {
            if (!DoubleUtils.isFastDoubleClick()) {
                Postcard postcard = ARouter.getInstance().build("/circle/CirclePowerSetupActivity");
                postcard.navigation(this, REQUEST_CODE_POWER);
            }
        } else if (id == R.id.dp_action) {// 录音
            toVoice();
        } else if (id == R.id.iv_reset) {// 取消录音
            resetAudio(true);
        } else if (id == R.id.iv_confirm) {// 录音完成
            if (time < 1000) {
                showTaost("录制时间不能小于1秒");
                return;
            }
            etContent.requestFocus();
            InputUtil.showKeyboard(etContent);
            resetingBtn();

            layout_voice.setVisibility(View.VISIBLE);
            tv_time.setText(getPlayTime(time));
            resetAudio(false);
        } else if (id == R.id.iv_delete_voice) {// 删除语音
            layout_voice.setVisibility(View.GONE);
            resetAudio(true);
        } else if (id == R.id.iv_voice_play) {// 播放语音
            if (!TextUtils.isEmpty(currentAudioFile)) {
                isPlaying = true;
                AudioPlayUtil.startAudioPlay(this, currentAudioFile, pb_progress, iv_voice_play, this);
            }
        } else if (id == R.id.iv_voice) {// 语音
            if (layout_voice.getVisibility() == View.VISIBLE || mList.size() > 0 || mVoteList.size() > 0) {
                showTaost(getResources().getString(R.string.voice_message_wrong));
                return;
            }
            changeTrendModel(ETrendModel.VOICE);
            if (isOpenSoft) {
                isShowFace = false;
                InputUtil.hideKeyboard(etContent);
            }
            frame_content.setVisibility(View.VISIBLE);
            iv_picture.setImageLevel(0);
            iv_vote.setImageLevel(0);
            iv_voice.setImageLevel(1);
            layout_audio.setVisibility(View.VISIBLE);
            layout_vote.setVisibility(View.GONE);
            picture_recycler.setVisibility(View.GONE);
            circle_view_faceview.setVisibility(View.GONE);
            delayMillis();
        } else if (id == R.id.iv_picture) {// 图片
            if (isExistVoice || mVoteList.size() > 0) {
                showTaost(getResources().getString(R.string.voice_message_wrong));
                return;
            }
            if (hasChangeModel) {
                toGallery();
                changeTrendModel(ETrendModel.PICTURE);
            } else {
                changeTrendModel(ETrendModel.PICTURE);
            }
            if (isOpenSoft) {
                isShowFace = false;
                InputUtil.hideKeyboard(etContent);
            }
            frame_content.setVisibility(View.VISIBLE);
            setRecyclerViewHeight(mFuncHeight);
            picture_recycler.setVisibility(View.VISIBLE);
            circle_view_faceview.setVisibility(View.GONE);
            layout_vote.setVisibility(View.GONE);
            layout_audio.setVisibility(View.GONE);
            iv_picture.setImageLevel(1);
            iv_vote.setImageLevel(0);
            iv_voice.setImageLevel(0);
            delayMillis();
        } else if (id == R.id.iv_vote) {// 投票
            changeTrendModel(ETrendModel.VOTE);
            if (isOpenSoft) {
                isShowFace = true;
                InputUtil.hideKeyboard(etContent);
            }
            frame_content.setVisibility(View.VISIBLE);
            setRecyclerViewHeight(mFuncHeight);
            layout_vote.setVisibility(View.VISIBLE);
            layout_audio.setVisibility(View.GONE);
            picture_recycler.setVisibility(View.GONE);
            circle_view_faceview.setVisibility(View.GONE);
            iv_picture.setImageLevel(0);
            iv_vote.setImageLevel(1);
            iv_voice.setImageLevel(0);
            delayMillis();
        } else if (id == R.id.iv_delete_vote) {// 删除投票
            isVoteTextEditSuccess = false;
            isVoteImageEditSuccess = false;
            mTxtJson = "";
            mImgJson = "";
            mVoteList.clear();
            etContent.setText("");
            layout_vote_content.setVisibility(View.GONE);
        } else if (id == R.id.tv_content_vote) {// 文字投票
            if (isVoteImageEditSuccess) {
                return;
            }
            if (!DoubleUtils.isFastDoubleClick()) {
                Postcard postcard = ARouter.getInstance().build("/circle/VoteTextActivity");
                postcard.withString(VOTE_TXT_TITLE, etContent.getText().toString());
                postcard.withString(VOTE_TXT, mTxtJson);
                postcard.navigation(this, REQUEST_CODE_VOTE_TXT);
            }
        } else if (id == R.id.tv_picture_vote) {// 图片投票
            if (isVoteTextEditSuccess) {
                return;
            }
            if (!DoubleUtils.isFastDoubleClick()) {
                if (isExistVoice || mList.size() > 0) {
                    showTaost(getResources().getString(R.string.voice_message_wrong));
                    return;
                }
                Postcard postcard = ARouter.getInstance().build("/circle/VotePictrueActivity");
                postcard.withString(VOTE_TXT_TITLE, etContent.getText().toString());
                postcard.withString(VOTE_LOCATION_IMG, new Gson().toJson(mVoteList));
                postcard.navigation(this, REQUEST_CODE_VOTE_PICTRUE);
            }
        } else if (id == R.id.iv_face) {// 表情
            changeTrendModel(ETrendModel.EMOJI);
            if (frame_content.getVisibility() == View.GONE) {
                frame_content.setVisibility(View.VISIBLE);
            }
            isShowFace = !isShowFace;
            showOrHideInput();
            layout_vote.setVisibility(View.GONE);
            layout_audio.setVisibility(View.GONE);
            picture_recycler.setVisibility(View.GONE);
            circle_view_faceview.setVisibility(View.VISIBLE);

        } else if (id == R.id.id_ll_ok) {
            List<LocalMedia> images = adapter.getSelectedImages();
            LocalMedia image = images.size() > 0 ? images.get(0) : null;
            String pictureType = image != null ? image.getPictureType() : "";
            // 如果设置了图片最小选择数量，则判断是否满足条件
            int size = images.size();
            boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);
            if (config.minSelectNum > 0 && config.selectionMode == PictureConfig.MULTIPLE) {
                if (size < config.minSelectNum) {
                    @SuppressLint("StringFormatMatches") String str = eqImg ? getString(R.string.picture_min_img_num, config.minSelectNum)
                            : getString(R.string.picture_min_video_num, config.minSelectNum);
                    ToastManage.s(mContext, str);
                    return;
                }
            }
            if (config.enableCrop && eqImg) {
                if (config.selectionMode == PictureConfig.SINGLE) {
                    originalPath = image.getPath();
                    startCrop(originalPath);
                } else {
                    // 是图片和选择压缩并且是多张，调用批量压缩
                    ArrayList<String> medias = new ArrayList<>();
                    for (LocalMedia media : images) {
                        medias.add(media.getPath());
                    }
                    startCrop(medias);
                }
            } else if (config.isCompress && eqImg) {
                // 图片才压缩，视频不管
                if (isArtworkMaster) {
                    compressImage(images, true);
                } else {
                    compressImage(images);
                }
            } else {
                onResult(images);
            }
        }
    }

    private void toVoice() {
        permission2Util.requestPermissions(CreateCircleActivity.this, new CheckPermission2Util.Event() {
            @Override
            public void onSuccess() {
                startVoice();
            }

            @Override
            public void onFail() {

            }
        }, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    private void startVoice() {
        setRecyclerViewHeight(mFuncHeight);
        switch (mAudioState) {
            case PictureEnum.EAudioType.NO_AUDIO: // 开始录音
                isExistVoice = true;
                iv_action.setVisibility(View.VISIBLE);
                tv_voice_tip.setText("正在录音");
                dp_action.setMax(100 * 10 * MAX_SECOND);
                iv_voice_bg.setImageResource(R.drawable.bg_audio_recorder_ing);
                iv_action.setImageResource(R.drawable.bg_audio_recorder_green);
                mAudioHandler.removeCallbacks(mAudioRunnable);
                time = 0;
                mAudioHandler.postDelayed(mAudioRunnable, 100);
                startRecord();
                mAudioState = 1;
                break;
            case PictureEnum.EAudioType.ING_AUDIO:// 停止
                iv_action.setVisibility(View.VISIBLE);
                iv_action.setImageResource(R.mipmap.ic_start_recorder);
                iv_voice_bg.setImageResource(R.drawable.bg_audio_recorder);
                dp_action.setDonut_progress("" + 0);
                stopRecord();
                AudioPlayUtil.stopAudioPlay();
                mAudioHandler.removeCallbacks(mAudioRunnable);
                tv_voice_tip.setText("点击播放");
                iv_reset.setVisibility(View.VISIBLE);
                iv_confirm.setVisibility(View.VISIBLE);
                mAudioState = 2;
                break;
            case PictureEnum.EAudioType.STOP_AUDIO:// 播放
                iv_action.setVisibility(View.VISIBLE);
                iv_voice_bg.setImageResource(R.drawable.bg_audio_recorder);
                dp_action.setDonut_progress("" + 0);
                if (!TextUtils.isEmpty(currentAudioFile)) {
                    isPlaying = !isPlaying;
                    if (isPlaying) {
                        iv_action.setImageResource(R.mipmap.ic_audio_start);
                        tv_voice_tip.setText("正在播放");
                        AudioPlayUtil.startAudioPlay(this, currentAudioFile, null, null, this);
                    } else {
                        tv_voice_tip.setText("点击播放");
                        iv_action.setImageResource(R.mipmap.ic_start_recorder);
                        AudioPlayUtil.stopAudioPlay();
                    }
                }
                break;
        }
    }

    /**
     * 延迟0.1秒显示内容
     */
    private void delayMillis() {
        try {
            if (!isFinishing()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_face.setImageLevel(0);
                        isShowFace = true;
                        frame_content.setVisibility(View.VISIBLE);
                    }
                }, 100);
            }
        } catch (Exception e) {

        }
    }

    private void addShowAdd() {
        LocalMedia localMedia = new LocalMedia();
        localMedia.setShowAdd(true);
        mList.add(localMedia);
    }

    private void showOrHideInput() {
        resetingBtn();
        if (isShowFace) {
            iv_face.setImageLevel(0);
            etContent.requestFocus();
            InputUtil.showKeyboard(etContent);
        } else {
            iv_face.setImageLevel(1);
            InputUtil.hideKeyboard(etContent);
        }
    }

    private String getAudioFile() {
        File dir = new File(getExternalFilesDir(null).getAbsolutePath(), AudioRecorder.AUDIO_FILE_NAME);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, DateUtil.getCurrTime(DateUtil.DATE_PATTERN_yyyyMMddHHmmss) + ".aac");
        return file.getAbsolutePath();
    }

    private void resetAudio(boolean isDelete) {
        iv_action.setVisibility(View.GONE);
        if (mAudioHandler != null) {
            mAudioHandler.removeCallbacks(mAudioRunnable);
        }
        if (isPlaying) {
            AudioPlayManager.getInstance().stopPlay();
            AudioPlayUtil.stopAudioPlay();
            isPlaying = false;
        }
        dp_action.setDonut_progress("" + 0);
        tv_voice_time.setText("0s");
        tv_voice_tip.setText("点击开始录音");
        iv_reset.setVisibility(View.GONE);
        iv_confirm.setVisibility(View.GONE);
        if (isDelete && !TextUtils.isEmpty(currentAudioFile)) {
            File file = new File(currentAudioFile);
            if (file.exists()) {
                file.delete();
            }
            currentAudioFile = "";
            isExistVoice = false;
        }
        mAudioState = 0;
    }

    private void startRecord() {
        // FIXME: 2018/10/10 权限是否有权限，没权限提示错误，并返回
        isRecording = true;
        updateRecordingUI();
        if (recorder == null) {
            recorder = new AudioRecorder(this);
        }
        currentAudioFile = getAudioFile();
        recorder.startRecord(currentAudioFile);
    }

    private void stopRecord() {
        if (!isRecording) {
            return;
        }
        if (recorder != null) {
            recorder.stopRecord();
        }
        isRecording = false;
        updateRecordingUI();
    }

    private void previewImage(List<LocalMedia> previewImages, List<LocalMedia> selectedImages, int position) {
        Intent intent = new Intent(this, PicturePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) previewImages);
        bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, (Serializable) selectedImages);
        bundle.putBoolean(PictureConfig.EXTRA_BOTTOM_PREVIEW, true);
        bundle.putInt(PictureConfig.EXTRA_POSITION, position);
        bundle.putInt(PictureConfig.FROM_WHERE, PictureConfig.FROM_DEFAULT);//跳转来源 0 默认 1 猜你想要 2 收藏详情
        intent.putExtras(bundle);
        startActivityForResult(intent,
                config.selectionMode == PictureConfig.SINGLE ? UCrop.REQUEST_CROP : UCropMulti.REQUEST_MULTI_CROP);
    }

    /**
     * 播放音频
     *
     * @param path
     */
    private void audioDialog(final String path) {
        audioDialog = new CustomDialog(mContext,
                LinearLayout.LayoutParams.MATCH_PARENT, audioH,
                R.layout.picture_audio_dialog, R.style.Theme_dialog);
        audioDialog.getWindow().setWindowAnimations(R.style.Dialog_Audio_StyleAnim);
        tv_musicStatus = (TextView) audioDialog.findViewById(R.id.tv_musicStatus);
        tv_musicTime = (TextView) audioDialog.findViewById(R.id.tv_musicTime);
        musicSeekBar = (SeekBar) audioDialog.findViewById(R.id.musicSeekBar);
        tv_musicTotal = (TextView) audioDialog.findViewById(R.id.tv_musicTotal);
        tv_PlayPause = (TextView) audioDialog.findViewById(R.id.tv_PlayPause);
        tv_Stop = (TextView) audioDialog.findViewById(R.id.tv_Stop);
        tv_Quit = (TextView) audioDialog.findViewById(R.id.tv_Quit);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initPlayer(path);
            }
        }, 30);
        tv_PlayPause.setOnClickListener(new audioOnClick(path));
        tv_Stop.setOnClickListener(new audioOnClick(path));
        tv_Quit.setOnClickListener(new audioOnClick(path));
        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        audioDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stop(path);
                    }
                }, 30);
                try {
                    if (audioDialog != null
                            && audioDialog.isShowing()) {
                        audioDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        handler.post(runnable);
        audioDialog.show();
    }

    //  通过 Handler 更新 UI 上的组件状态
    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayer != null) {
                    tv_musicTime.setText(DateUtils.timeParse(mediaPlayer.getCurrentPosition()));
                    musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                    musicSeekBar.setMax(mediaPlayer.getDuration());
                    tv_musicTotal.setText(DateUtils.timeParse(mediaPlayer.getDuration()));
                    handler.postDelayed(runnable, 200);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Handler mAudioHandler = new Handler(Looper.getMainLooper());
    private long time = 0;
    private Runnable mAudioRunnable = new Runnable() {
        @Override
        public void run() {
            time = time + 100;
            dp_action.setDonut_progress("" + time);
            tv_voice_time.setText(getTime(time));
            if (time < 100 * 10 * MAX_SECOND) {
                mAudioHandler.postDelayed(this, 100);
            } else {
                iv_action.setImageResource(R.mipmap.ic_start_recorder);
                iv_voice_bg.setImageResource(R.drawable.bg_audio_recorder);
                dp_action.setDonut_progress("" + 0);
                stopRecord();
                mAudioHandler.removeCallbacks(mAudioRunnable);
                tv_voice_tip.setText("点击播放");
                iv_reset.setVisibility(View.VISIBLE);
                iv_confirm.setVisibility(View.VISIBLE);
                mAudioState = 2;
            }
        }
    };

    private String getTime(long count) {
        long min = count / (60 * 1000);
        long sec = count / 1000 % 60;
        if (min > 0) {
            sec = min * 60 + sec;
        }
        return sec + "s";
    }

    private long getSendTime(long count) {
        long min = count / (60 * 1000);
        long sec = count / 1000 % 60;
        if (min > 0) {
            sec = min * 60 + sec;
        }
        return sec;
    }

    private String getPlayTime(long count) {
        String time = "";
        long min = count / (60 * 1000);
        long sec = count / 1000 % 60;
        if (min < 10) {
            time = time + "0" + min;
        } else {
            time = time + min;
        }
        if (sec < 10) {
            time = time + ":0" + sec;
        } else {
            time = time + ":" + sec;
        }
        return time;
    }

    /**
     * 初始化音频播放组件
     *
     * @param path
     */
    private void initPlayer(String path) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            playAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isOpenSoft) {
            etContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    etContent.requestFocus();
                    InputUtil.showKeyboard(etContent);
                }
            }, 300);
        }
    }

    @Override
    public void onStart(Uri var1) {

    }

    @Override
    public void onStop(Uri var1) {

    }

    @Override
    public void onComplete(Uri var1) {
        iv_action.setVisibility(View.VISIBLE);
        iv_action.setImageResource(R.mipmap.ic_start_recorder);
        iv_voice_bg.setImageResource(R.drawable.bg_audio_recorder);
        tv_voice_tip.setText("点击播放");
        iv_reset.setVisibility(View.VISIBLE);
        iv_confirm.setVisibility(View.VISIBLE);
        mAudioState = 2;
    }

    @Override
    public void onProgress(int progress) {

    }

    /**
     * 播放音频点击事件
     */
    public class audioOnClick implements View.OnClickListener {
        private String path;

        public audioOnClick(String path) {
            super();
            this.path = path;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.tv_PlayPause) {
                playAudio();
            }
            if (id == R.id.tv_Stop) {
                tv_musicStatus.setText(getString(R.string.picture_stop_audio));
                tv_PlayPause.setText(getString(R.string.picture_play_audio));
                stop(path);
            }
            if (id == R.id.tv_Quit) {
                handler.removeCallbacks(runnable);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stop(path);
                    }
                }, 30);
                try {
                    if (audioDialog != null
                            && audioDialog.isShowing()) {
                        audioDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 播放音频
     */
    private void playAudio() {
        if (mediaPlayer != null) {
            musicSeekBar.setProgress(mediaPlayer.getCurrentPosition());
            musicSeekBar.setMax(mediaPlayer.getDuration());
        }
        String ppStr = tv_PlayPause.getText().toString();
        if (ppStr.equals(getString(R.string.picture_play_audio))) {
            tv_PlayPause.setText(getString(R.string.picture_pause_audio));
            tv_musicStatus.setText(getString(R.string.picture_play_audio));
            playOrPause();
        } else {
            tv_PlayPause.setText(getString(R.string.picture_play_audio));
            tv_musicStatus.setText(getString(R.string.picture_pause_audio));
            playOrPause();
        }
        if (isPlayAudio == false) {
            handler.post(runnable);
            isPlayAudio = true;
        }
    }

    /**
     * 停止播放
     *
     * @param path
     */
    public void stop(String path) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 暂停播放
     */
    public void playOrPause() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(String folderName, List<LocalMedia> images) {
        boolean camera = StringUtils.isCamera(folderName);
        camera = config.isCamera ? camera : false;
        adapter.setShowCamera(camera);
//        picture_title.setText(folderName);
        adapter.bindImagesData(images);
        folderWindow.dismiss();
    }

    @Override
    public void onTakePhoto() {
        // 启动相机拍照,先判断手机是否有拍照权限
        rxPermissions.request(Manifest.permission.CAMERA).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    startCamera();
                } else {
                    ToastManage.s(mContext, getString(R.string.picture_camera));
                    if (config.camera) {
                        closeActivity();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onChange(List<LocalMedia> selectImages) {
        if (trendModel == ETrendModel.VOTE) {
            return;
        }
        changeImageNumber(selectImages);
        if (isArtworkMaster) {
            setOriginImageSize();
        }
        if (selectImages != null && selectImages.size() > 0) {
            mList.clear();
            mList.addAll(selectImages);
            if (mList.size() < 9) {
                addShowAdd();
            }
            mPictureAdapter.notifyDataSetChanged();
            recycler_picture_prview.setVisibility(View.VISIBLE);
        } else {
            mList.clear();
            recycler_picture_prview.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPictureClick(LocalMedia media, int position) {
//        List<LocalMedia> images = adapter.getImages();
//        startPreview(images, position);
        List<LocalMedia> previewList = new ArrayList<>();
        int index = -1;
        previewList.addAll(adapter.getSelectedImages());
        for (int i = 0; i < adapter.getSelectedImages().size(); i++) {
            LocalMedia localMedia = adapter.getSelectedImages().get(i);
            if (localMedia.getPath().equals(media.getPath())) {//是否在被选中数组中
                index = i;
                break;
            }
        }
        Log.i("1212", "index:" + index);
        if (index == -1) {//没有找到，没有被选中
            previewList.add(media);
            index = previewList.size() - 1;
        }
        isRestHeight = false;
        previewImage(previewList, adapter.getSelectedImages(), index);
    }

    @Override
    public void onUpdateChange(List<LocalMedia> selectImages) {
        if (adapter != null) {
            List<LocalMedia> list = new ArrayList<>();
            for (LocalMedia localMedia : selectImages) {
                if (!localMedia.isShowAdd()) {
                    list.add(localMedia);
                }
            }
            adapter.bindSelectImages(list);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPicturePreviewClick(LocalMedia media, int position) {
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
                    .selectionMedia(adapter.getSelectedImages())
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调 code
        } else {
            List<LocalMedia> previewList = new ArrayList<>();
            int index = -1;
            previewList.addAll(adapter.getSelectedImages());
            for (int i = 0; i < adapter.getSelectedImages().size(); i++) {
                LocalMedia localMedia = adapter.getSelectedImages().get(i);
                if (localMedia.getPath().equals(media.getPath())) {//是否在被选中数组中
                    index = i;
                    break;
                }
            }
            if (index == -1) {//没有找到，没有被选中
                previewList.add(media);
                index = previewList.size() - 1;
            }
            isRestHeight = false;
            previewImage(previewList, adapter.getSelectedImages(), index);
        }
    }

    /**
     * change image selector state
     *
     * @param selectImages
     */
    public void changeImageNumber(List<LocalMedia> selectImages) {
        // 如果选择的视频没有预览功能
        String pictureType = selectImages.size() > 0
                ? selectImages.get(0).getPictureType() : "";
        if (config.mimeType == PictureMimeType.ofAudio()) {
            picture_id_preview.setVisibility(View.GONE);
        } else {
            boolean isVideo = PictureMimeType.isVideo(pictureType);
            boolean eqVideo = config.mimeType == PictureConfig.TYPE_VIDEO;
            picture_id_preview.setVisibility(isVideo || eqVideo ? View.GONE : View.VISIBLE);
        }
        boolean enable = selectImages.size() != 0;
        if (enable) {
            id_ll_ok.setEnabled(true);
            picture_id_preview.setEnabled(true);
            picture_id_preview.setSelected(true);
            picture_tv_ok.setSelected(true);
            if (numComplete) {
                picture_tv_ok.setText(getString
                        (R.string.picture_done_front_num, selectImages.size(),
                                config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum));
            } else {
                if (!anim) {
                    picture_tv_img_num.startAnimation(animation);
                }
                picture_tv_img_num.setVisibility(View.VISIBLE);
                picture_tv_img_num.setText(String.valueOf(selectImages.size()));
                picture_tv_ok.setText(getString(R.string.picture_completed));
                anim = false;
            }
        } else {
            id_ll_ok.setEnabled(false);
            picture_id_preview.setEnabled(false);
            picture_id_preview.setSelected(false);
            picture_tv_ok.setSelected(false);
            if (numComplete) {
                picture_tv_ok.setText(getString(R.string.picture_done_front_num, 0,
                        config.selectionMode == PictureConfig.SINGLE ? 1 : config.maxSelectNum));
            } else {
                picture_tv_img_num.setVisibility(View.INVISIBLE);
                picture_tv_ok.setText(getString(R.string.picture_please_select));
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            List<LocalMedia> medias = new ArrayList<>();
            LocalMedia media;
            String imageType;
            switch (requestCode) {
                case UCrop.REQUEST_CROP:
                    Uri resultUri = UCrop.getOutput(data);
                    String cutPath = resultUri.getPath();
                    if (adapter != null) {
                        // 取单张裁剪已选中图片的path作为原图
                        List<LocalMedia> mediaList = adapter.getSelectedImages();
                        media = mediaList != null && mediaList.size() > 0 ? mediaList.get(0) : null;
                        if (media != null) {
                            originalPath = media.getPath();
                            media = new LocalMedia(originalPath, media.getDuration(), false,
                                    media.getPosition(), media.getNum(), config.mimeType);
                            media.setCutPath(cutPath);
                            media.setCut(true);
                            imageType = PictureMimeType.createImageType(cutPath);
                            media.setPictureType(imageType);
                            medias.add(media);
                            handlerResult(medias);
                        }
                    } else if (config.camera) {
                        // 单独拍照
                        media = new LocalMedia(cameraPath, 0, false,
                                config.isCamera ? 1 : 0, 0, config.mimeType);
                        media.setCut(true);
                        media.setCutPath(cutPath);
                        imageType = PictureMimeType.createImageType(cutPath);
                        media.setPictureType(imageType);
                        medias.add(media);
                        handlerResult(medias);
                    }
                    break;
                case UCropMulti.REQUEST_MULTI_CROP:
                    List<CutInfo> mCuts = UCropMulti.getOutput(data);
                    for (CutInfo c : mCuts) {
                        media = new LocalMedia();
                        imageType = PictureMimeType.createImageType(c.getPath());
                        media.setCut(true);
                        media.setPath(c.getPath());
                        media.setCutPath(c.getCutPath());
                        media.setPictureType(imageType);
                        media.setMimeType(config.mimeType);
                        medias.add(media);
                    }
                    handlerResult(medias);
                    break;
                case PictureConfig.REQUEST_CAMERA:
                    if (config.mimeType == PictureMimeType.ofAudio()) {
                        cameraPath = getAudioPath(data);
                    }
                    // on take photo success
                    final File file = new File(cameraPath);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    String toType = PictureMimeType.fileToType(file);
                    if (config.mimeType != PictureMimeType.ofAudio()) {
                        int degree = PictureFileUtils.readPictureDegree(file.getAbsolutePath());
                        rotateImage(degree, file);
                    }
                    // 生成新拍照片或视频对象
                    media = new LocalMedia();
                    media.setPath(cameraPath);

                    boolean eqVideo = toType.startsWith(PictureConfig.VIDEO);
                    int duration = eqVideo ? PictureMimeType.getLocalVideoDuration(cameraPath) : 0;
                    String pictureType = "";
                    if (config.mimeType == PictureMimeType.ofAudio()) {
                        pictureType = "audio/mpeg";
                        duration = PictureMimeType.getLocalVideoDuration(cameraPath);
                    } else {
                        pictureType = eqVideo ? PictureMimeType.createVideoType(cameraPath)
                                : PictureMimeType.createImageType(cameraPath);
                    }
                    media.setPictureType(pictureType);
                    media.setDuration(duration);
                    media.setMimeType(config.mimeType);

                    // 因为加入了单独拍照功能，所有如果是单独拍照的话也默认为单选状态
                    if (config.camera) {
                        // 如果是单选 拍照后直接返回
                        boolean eqImg = toType.startsWith(PictureConfig.IMAGE);
                        if (config.enableCrop && eqImg) {
                            // 去裁剪
                            originalPath = cameraPath;
                            startCrop(cameraPath);
                        } else if (config.isCompress && eqImg) {
                            // 去压缩
                            medias.add(media);
                            compressImage(medias);
                            if (adapter != null) {
                                images.add(0, media);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            // 不裁剪 不压缩 直接返回结果
                            medias.add(media);
                            onResult(medias);
                        }
                    } else {
                        // 多选 返回列表并选中当前拍照的
                        images.add(0, media);
                        if (adapter != null) {
                            List<LocalMedia> selectedImages = adapter.getSelectedImages();
                            // 没有到最大选择量 才做默认选中刚拍好的
                            if (selectedImages.size() < config.maxSelectNum) {
                                pictureType = selectedImages.size() > 0 ? selectedImages.get(0).getPictureType() : "";
                                boolean toEqual = PictureMimeType.mimeToEqual(pictureType, media.getPictureType());
                                // 类型相同或还没有选中才加进选中集合中
                                if (toEqual || selectedImages.size() == 0) {
                                    if (selectedImages.size() < config.maxSelectNum) {
                                        // 如果是单选，则清空已选中的并刷新列表(作单一选择)
                                        if (config.selectionMode == PictureConfig.SINGLE) {
                                            singleRadioMediaImage();
                                        }
                                        selectedImages.add(media);
                                        adapter.bindSelectImages(selectedImages);
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                    if (adapter != null) {
                        // 解决部分手机拍照完Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
                        // 不及时刷新问题手动添加
                        manualSaveFolder(media);
//                        tv_empty.setVisibility(images.size() > 0
//                                ? View.INVISIBLE : View.VISIBLE);
                    }

                    if (config.mimeType != PictureMimeType.ofAudio()) {
                        int lastImageId = getLastImageId(eqVideo);
                        if (lastImageId != -1) {
                            removeImage(lastImageId, eqVideo);
                        }
                    }
                    break;
                case REQUEST_CODE_LOCATION:// 设置位置
                    String address = data.getStringExtra(INTENT_LOCATION_NAME);
                    mLocationDesc = data.getStringExtra(INTENT_LOCATION_DESC);
                    mCityName = data.getStringExtra(CITY_NAME);
                    mLatitude = data.getDoubleExtra(LATITUDE, -1);
                    mLongitude = data.getDoubleExtra(LONGITUDE, -1);
                    Drawable drawable;
                    if (ADDRESS_NOT_SHOW.equals(address)) {
                        address = getString(R.string.you_location);
                        tv_location.setTextColor(getResources().getColor(R.color.color_b1b));
                        drawable = getResources().getDrawable(R.mipmap.ic_circle_location_gray);
                        iv_delete_location.setVisibility(View.GONE);
                    } else {
                        tv_location.setTextColor(getResources().getColor(R.color.green_500));
                        drawable = getResources().getDrawable(R.mipmap.ic_circle_location_check);
                        iv_delete_location.setVisibility(View.VISIBLE);
                    }
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                    tv_location.setCompoundDrawables(drawable, null, null, null);
                    tv_location.setText(address);
                    break;
                case REQUEST_CODE_POWER:// 设置权限
                    address = data.getStringExtra(INTENT_POWER);
                    tv_power.setText(address);
                    break;
                case REQUEST_CODE_VOTE_TXT:// 文字投票
                    isVoteTextEditSuccess = true;
                    String title = data.getStringExtra(VOTE_TXT_TITLE);
                    mTxtJson = data.getStringExtra(VOTE_TXT);
                    etContent.setText(getSpan(title));
                    etContent.setSelection(etContent.getText().toString().length());
                    etContent.requestFocus();
                    layout_vote_content.setVisibility(View.VISIBLE);
                    break;
                case REQUEST_CODE_VOTE_PICTRUE:// 图片投票
                    isVoteImageEditSuccess = true;
                    title = data.getStringExtra(VOTE_TXT_TITLE);
                    mImgJson = data.getStringExtra(VOTE_TXT);
                    String imgJson = data.getStringExtra(VOTE_LOCATION_IMG);
                    if (!TextUtils.isEmpty(imgJson)) {
                        mVoteList = new Gson().fromJson(imgJson,
                                new TypeToken<List<LocalMedia>>() {
                                }.getType());
                    }
                    etContent.setText(getSpan(title));
                    etContent.setSelection(etContent.getText().toString().length());
                    etContent.requestFocus();
                    layout_vote_content.setVisibility(View.VISIBLE);
                    break;
                case PictureConfig.CHOOSE_REQUEST:// 图片回调
                    List<LocalMedia> selectImages = (List<LocalMedia>) data.getSerializableExtra(PictureConfig.EXTRA_RESULT_SELECTION);
                    if (selectImages != null && selectImages.size() > 0) {
                        // 底部图片更新
                        if (adapter != null) {
                            adapter.bindSelectImages(selectImages);
                            adapter.notifyDataSetChanged();
                        }
                        // 预览图片更新
                        mList.clear();
                        mList.addAll(selectImages);
                        if (mList.size() < 9) {
                            addShowAdd();
                        }
                        mPictureAdapter.notifyDataSetChanged();
                        recycler_picture_prview.setVisibility(View.VISIBLE);
                    } else {
                        recycler_picture_prview.setVisibility(View.GONE);
                    }
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (config.camera) {
                closeActivity();
            }
            if (requestCode == REQUEST_CODE_VOTE_PICTRUE) {
                if (adapter != null && adapter.getSelectedImages() != null) {
                    adapter.getSelectedImages().clear();
                    adapter.notifyDataSetChanged();
                }
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable throwable = (Throwable) data.getSerializableExtra(UCrop.EXTRA_ERROR);
            ToastManage.s(mContext, throwable.getMessage());
        }
    }

    /**
     * 富文本
     *
     * @param msg
     * @return
     */
    private SpannableString getSpan(String msg) {
        SpannableString spannableString = ExpressionUtil.getExpressionString(this, ExpressionUtil.DEFAULT_SIZE, msg);
        return spannableString;
    }

    /**
     * 单选图片
     */
    private void singleRadioMediaImage() {
        if (adapter != null) {
            List<LocalMedia> selectImages = adapter.getSelectedImages();
            if (selectImages != null
                    && selectImages.size() > 0) {
                selectImages.clear();
            }
        }
    }


    /**
     * 手动添加拍照后的相片到图片列表，并设为选中
     *
     * @param media
     */
    private void manualSaveFolder(LocalMedia media) {
        try {
            createNewFolder(foldersList);
            LocalMediaFolder folder = getImageFolder(media.getPath(), foldersList);
            LocalMediaFolder cameraFolder = foldersList.size() > 0 ? foldersList.get(0) : null;
            if (cameraFolder != null && folder != null) {
                // 相机胶卷
                cameraFolder.setFirstImagePath(media.getPath());
                cameraFolder.setImages(images);
                cameraFolder.setImageNum(cameraFolder.getImageNum() + 1);
                // 拍照相册
                int num = folder.getImageNum() + 1;
                folder.setImageNum(num);
                folder.getImages().add(0, media);
                folder.setFirstImagePath(cameraPath);
                folderWindow.bindFolder(foldersList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().unregister(this);
        }
        ImagesObservable.getInstance().clearLocalMedia();
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
        if (mediaPlayer != null && handler != null) {
            handler.removeCallbacks(runnable);
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (recorder != null && mAudioHandler != null) {
            mAudioHandler.removeCallbacks(mAudioRunnable);
            recorder.stopRecord();
        }
        time = 0;
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                // 拍照
                startOpenCamera();
                break;
            case 1:
                // 录视频
                startOpenCameraVideo();
                break;
        }
    }

    public String getImageSize() {
        List<LocalMedia> list = adapter.getSelectedImages();
        String size = "";
        if (list != null && list.size() > 0) {
            int len = list.size();
            int totalSize = 0;
            for (int i = 0; i < len; i++) {
                LocalMedia media = list.get(i);
                File file = new File(media.getPath());
                if (file != null) {
                    totalSize += file.length();
                }
            }
            if (totalSize > 0 && totalSize < 1024) { //byte
                size = totalSize + " b";
            } else if (totalSize >= 1024 && totalSize < 0.1 * 1024 * 1024) {//1k到0.1M
                int d = totalSize / 1024;
                size = d + " k";
            } else if (totalSize >= 0.1 * 1024 * 1024) {//M
                double d = totalSize * 1.0 / (1024 * 1024);
                DecimalFormat df = new DecimalFormat("0.0");//格式化小数
                size = df.format(d) + " M";
            }
        }
        return size;
    }

    /*
     *创建动态模式
     * */
    @IntDef({ETrendModel.PICTURE, ETrendModel.VOICE, ETrendModel.VOTE, ETrendModel.EMOJI})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ETrendModel {
        int PICTURE = 0; // 图片动态
        int VOICE = 1; // 语音
        int VOTE = 2; // 投票
        int EMOJI = 3; // 表情
    }

    private void changeTrendModel(int m) {
        if (trendModel == m) {
            return;
        }
        trendModel = m;
        hasChangeModel = true;
    }

    private void toGallery() {
        PictureSelector.create(CreateCircleActivity.this)
                .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .isCamera(false)// 是否显示拍照按钮 ture or false
                .maxVideoSelectNum(1)
                .maxSelectNum(4)
                .compress(true)// 是否压缩 true or false
                .isGif(true)
                .selectArtworkMaster(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调 code
    }

    public void updateRecordingUI() {
        if (isRecording) {
            etContent.setEnabled(false);
            picture_left_back.setEnabled(false);
            picture_right.setEnabled(false);
            iv_vote.setEnabled(false);
            iv_picture.setEnabled(false);
            iv_face.setEnabled(false);
            tv_location.setEnabled(false);
            tv_power.setEnabled(false);
        } else {
            etContent.setEnabled(true);
            picture_left_back.setEnabled(true);
            picture_right.setEnabled(true);
            iv_vote.setEnabled(true);
            iv_picture.setEnabled(true);
            iv_face.setEnabled(true);
            tv_location.setEnabled(true);
            tv_power.setEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRecording) {
            toVoice();
//            resetAudio(true);
//            stopRecord();
        }
    }
}
