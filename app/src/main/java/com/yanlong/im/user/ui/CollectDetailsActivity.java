package com.yanlong.im.user.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.nim_lib.controll.AVChatProfile;
import com.google.gson.Gson;
import com.hm.cxpay.utils.DateUtils;
import com.netease.nimlib.sdk.avchat.constant.AVChatType;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.ui.VideoPlayActivity;
import com.yanlong.im.chat.ui.chat.ChatActivity;
import com.yanlong.im.location.LocationUtils;
import com.yanlong.im.user.bean.CollectionInfo;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.view.face.FaceView;

import net.cb.cb.library.utils.FileUtils;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

import java.io.File;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * @类名：收藏详情
 * @Date：2020/4/28
 * @by zjy
 * @备注：
 */
public class CollectDetailsActivity extends AppActivity {

    private HeadView mHeadView;
    private ActionbarView actionbar;
    private CollectionInfo collectionInfo;
    private TextView tvFrom;
    private TextView tvTime;
    private TextView tvContent;
    private ImageView ivPic;
    private ImageView ivPlay;
    private ImageView ivExpress;//表情
    private TextView tvVoiceTime;
    private RelativeLayout layoutText;
    private RelativeLayout layoutPic;
    private LinearLayout layoutVoice;
    private RelativeLayout layoutFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_details);
        initView();
        getExtras();
        initEvent();
    }

    private void initView() {
        mHeadView = findViewById(R.id.headView);
        tvFrom = findViewById(R.id.tv_from);
        tvTime = findViewById(R.id.tv_time);
        tvContent = findViewById(R.id.tv_content);
        layoutText = findViewById(R.id.layout_text);
        layoutPic = findViewById(R.id.layout_pic);
        layoutVoice = findViewById(R.id.layout_voice);
        layoutFile = findViewById(R.id.layout_file);
        ivPic = findViewById(R.id.iv_pic);
        ivPlay = findViewById(R.id.iv_play);
        ivExpress = findViewById(R.id.iv_express);
        tvVoiceTime = findViewById(R.id.tv_voice_time);
        actionbar = mHeadView.getActionbar();
        actionbar.getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        actionbar.getBtnRight().setVisibility(VISIBLE);

    }

    private void initEvent() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onRight() {
                ToastUtil.show("弹框!");
            }
        });
    }

    //获取传递过来的数据
    private void getExtras() {
        if (getIntent() != null) {
            if (getIntent().getStringExtra("json_data") != null) {
                collectionInfo = new Gson().fromJson(getIntent().getStringExtra("json_data"), CollectionInfo.class);
                if (!TextUtils.isEmpty(collectionInfo.getData())) {
                    MsgAllBean bean = new Gson().fromJson(collectionInfo.getData(), MsgAllBean.class);
                    //显示用户名或群名
                    if (!TextUtils.isEmpty(collectionInfo.getFromGroupName())) {
                        tvFrom.setText("群聊-" + collectionInfo.getFromGroupName());
                    } else if (!TextUtils.isEmpty(collectionInfo.getFromUsername())) {
                        tvFrom.setText("用户-" + collectionInfo.getFromUsername());
                    } else {
                        tvFrom.setText("未知来源");
                    }
                    //收藏时间
                    if (!TextUtils.isEmpty(collectionInfo.getCreateTime())) {
                        tvTime.setText(TimeToString.getTimeForCollect(Long.parseLong(collectionInfo.getCreateTime()))+"收藏");
                    } else {
                        tvTime.setText("");
                    }
                    //不同类型
                    switch (collectionInfo.getType()) {
                        case ChatEnum.EMessageType.TEXT: //文字
                            layoutText.setVisibility(VISIBLE);//显示文字相关布局，隐藏其他类型相关布局
                            layoutVoice.setVisibility(GONE);
                            layoutPic.setVisibility(GONE);
                            layoutFile.setVisibility(GONE);
//                            layoutLocation.setVisibility(GONE);
                            if (bean != null) {
                                if (bean.getChat() != null) {
                                    if (!TextUtils.isEmpty(bean.getChat().getMsg())) {
                                        tvContent.setText(bean.getChat().getMsg());
                                    } else {
                                        tvContent.setText("");
                                    }
                                }
                            }
                            break;
                        case ChatEnum.EMessageType.IMAGE: //图片
                            layoutText.setVisibility(GONE);//显示图片相关布局，隐藏其他类型相关布局
                            layoutVoice.setVisibility(GONE);
                            layoutPic.setVisibility(VISIBLE);
                            layoutFile.setVisibility(GONE);
                            ivPic.setVisibility(VISIBLE);
                            ivExpress.setVisibility(GONE);
                            ivPlay.setVisibility(GONE);
//                            layoutLocation.setVisibility(GONE);
                            if (bean != null) {
                                if (bean.getImage() != null) { //显示预览图或者缩略图
                                    if (!TextUtils.isEmpty(bean.getImage().getPreview())) {
                                        Glide.with(CollectDetailsActivity.this).load(bean.getImage().getPreview())
                                                .apply(GlideOptionsUtil.headImageOptions()).into(ivPic);
                                    } else if (!TextUtils.isEmpty(bean.getImage().getThumbnail())) {
                                        Glide.with(CollectDetailsActivity.this).load(bean.getImage().getThumbnail())
                                                .apply(GlideOptionsUtil.headImageOptions()).into(ivPic);
                                    }
                                }
                            }
                            break;
                        case ChatEnum.EMessageType.SHIPPED_EXPRESSION: //大表情
                            layoutText.setVisibility(GONE);//显示大表情相关布局，隐藏其他类型相关布局
                            layoutVoice.setVisibility(GONE);
                            layoutPic.setVisibility(VISIBLE);
                            layoutFile.setVisibility(GONE);
                            ivPic.setVisibility(GONE);
                            ivExpress.setVisibility(VISIBLE);
                            ivPlay.setVisibility(GONE);
                            if (bean != null) {
                                if (bean.getShippedExpressionMessage() != null) {
                                    if (!TextUtils.isEmpty(bean.getShippedExpressionMessage().getId())) {
                                        Glide.with(CollectDetailsActivity.this).load(Integer.parseInt(FaceView.map_FaceEmoji.get(bean.getShippedExpressionMessage().getId()).toString())).apply(GlideOptionsUtil.headImageOptions()).into(ivExpress);
                                    }
                                }
                            }
                            break;
                        case ChatEnum.EMessageType.MSG_VIDEO: //短视频消息
                            layoutText.setVisibility(GONE);//显示短视频相关布局，隐藏其他类型相关布局
                            layoutVoice.setVisibility(GONE);
                            layoutPic.setVisibility(VISIBLE);
                            layoutFile.setVisibility(GONE);
                            ivPic.setVisibility(VISIBLE);
                            ivExpress.setVisibility(GONE);
                            ivPlay.setVisibility(VISIBLE);
                            if (bean != null) {
                                if (bean.getVideoMessage() != null) {
                                    if (!TextUtils.isEmpty(bean.getVideoMessage().getBg_url())) {
                                        Glide.with(CollectDetailsActivity.this).load(bean.getVideoMessage().getBg_url())
                                                .apply(GlideOptionsUtil.headImageOptions()).into(ivPic);
                                    }
                                }
                            }
                            ivPlay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (bean != null) {
                                        clickVideo(bean);
                                    }
                                }
                            });
                            break;
                        case ChatEnum.EMessageType.VOICE: //语音
                            layoutText.setVisibility(GONE);//显示语音相关布局，隐藏其他类型相关布局
                            layoutVoice.setVisibility(VISIBLE);
                            layoutPic.setVisibility(GONE);
                            layoutFile.setVisibility(GONE);
                            if (bean != null) {
                                if (bean.getVoiceMessage() != null) {
                                    if (bean.getVoiceMessage().getTime() != 0) {
                                        tvVoiceTime.setText(DateUtils.getSecondFormatTime(Long.valueOf(bean.getVoiceMessage().getTime() + "")));
                                    }
                                }
                            }
                            break;
                        case ChatEnum.EMessageType.LOCATION: //位置消息
//                            if (bean != null) {
//                                if (bean.getLocationMessage() != null) {
//                                    if (!TextUtils.isEmpty(bean.getLocationMessage().getAddress())) {
//                                        binding.tvLocationName.setText(bean.getLocationMessage().getAddress());
//                                    }
//                                    if (!TextUtils.isEmpty(bean.getLocationMessage().getAddressDescribe())) {
//                                        binding.tvLocationDesc.setText(bean.getLocationMessage().getAddressDescribe());
//                                    }
//                                    if (!TextUtils.isEmpty(bean.getLocationMessage().getImg())) {
//                                        Glide.with(CollectionActivity.this)
//                                                .asBitmap()
//                                                .load(bean.getLocationMessage().getImg())
//                                                .into(new SimpleTarget<Bitmap>() {
//                                                    @Override
//                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                                        binding.ivLocation.setImageBitmap(resource);
//                                                    }
//                                                });
//                                    } else {
//                                        String baiduImageUrl = LocationUtils.getLocationUrl(bean.getLocationMessage().getLatitude(), bean.getLocationMessage().getLongitude());
//                                        Glide.with(CollectionActivity.this)
//                                                .asBitmap()
//                                                .load(baiduImageUrl)
//                                                .into(new SimpleTarget<Bitmap>() {
//                                                    @Override
//                                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                                        binding.ivLocation.setImageBitmap(resource);
//                                                    }
//                                                });
//                                    }
//                                }
//                            }
                            break;
                        case ChatEnum.EMessageType.AT: //艾特@消息
                            layoutText.setVisibility(VISIBLE);//显示文字相关布局，隐藏其他类型相关布局
                            layoutVoice.setVisibility(GONE);
                            layoutPic.setVisibility(GONE);
                            layoutFile.setVisibility(GONE);
                            if (bean != null) {
                                if (bean.getAtMessage() != null) {
                                    if (!TextUtils.isEmpty(bean.getAtMessage().getMsg())) {
                                        tvContent.setText(bean.getAtMessage().getMsg());
                                    }
                                }
                            }
                            break;
                        case ChatEnum.EMessageType.FILE: //文件
//                            if (bean != null) {
//                                if (bean.getSendFileMessage() != null) {
//                                    if (!TextUtils.isEmpty(bean.getSendFileMessage().getFile_name())) {
//                                        binding.tvFileName.setText(bean.getSendFileMessage().getFile_name());
//                                    }
//                                    if (!TextUtils.isEmpty(bean.getSendFileMessage().getFormat())) {
//                                        String fileFormat = bean.getSendFileMessage().getFormat();
//                                        if (fileFormat.equals("txt")) {
//                                            binding.ivFilePic.setImageResource(R.mipmap.ic_txt);
//                                        } else if (fileFormat.equals("xls") || fileFormat.equals("xlsx")) {
//                                            binding.ivFilePic.setImageResource(R.mipmap.ic_excel);
//                                        } else if (fileFormat.equals("ppt") || fileFormat.equals("pptx") || fileFormat.equals("pdf")) { //PDF暂用此图标
//                                            binding.ivFilePic.setImageResource(R.mipmap.ic_ppt);
//                                        } else if (fileFormat.equals("doc") || fileFormat.equals("docx")) {
//                                            binding.ivFilePic.setImageResource(R.mipmap.ic_word);
//                                        } else if (fileFormat.equals("rar") || fileFormat.equals("zip")) {
//                                            binding.ivFilePic.setImageResource(R.mipmap.ic_zip);
//                                        } else if (fileFormat.equals("exe")) {
//                                            binding.ivFilePic.setImageResource(R.mipmap.ic_exe);
//                                        } else {
//                                            binding.ivFilePic.setImageResource(R.mipmap.ic_unknow);
//                                        }
//                                    }
//                                    if (bean.getSendFileMessage().getSize() != 0L) {
//                                        binding.tvFileSize.setText(FileUtils.getFileSizeString(bean.getSendFileMessage().getSize()));
//                                    }
//                                }
//                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private void clickVideo(MsgAllBean msg) {
        if (AVChatProfile.getInstance().isCallIng() || AVChatProfile.getInstance().isCallEstablished()) {
            if (AVChatProfile.getInstance().isChatType() == AVChatType.VIDEO.getValue()) {
                ToastUtil.show(CollectDetailsActivity.this, getString(R.string.avchat_peer_busy_video));
            } else {
                ToastUtil.show(CollectDetailsActivity.this, getString(R.string.avchat_peer_busy_voice));
            }
        } else {
            String localUrl = msg.getVideoMessage().getLocalUrl();
            if (StringUtil.isNotNull(localUrl)) {
                File file = new File(localUrl);
                if (!file.exists()) {
                    localUrl = msg.getVideoMessage().getUrl();
                }
            } else {
                localUrl = msg.getVideoMessage().getUrl();
            }
            Intent intent = new Intent(CollectDetailsActivity.this, VideoPlayActivity.class);
            intent.putExtra("videopath", localUrl);
            intent.putExtra("videomsg", new Gson().toJson(msg));
            intent.putExtra("msg_id", msg.getMsg_id());
            intent.putExtra("bg_url", msg.getVideoMessage().getBg_url());
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);

        }
    }
}
