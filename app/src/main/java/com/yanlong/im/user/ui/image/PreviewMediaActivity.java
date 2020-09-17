package com.yanlong.im.user.ui.image;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.R;
import com.yanlong.im.chat.ChatEnum;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.eventbus.EventReceiveImage;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.databinding.ActivityPreviewBinding;

import net.cb.cb.library.event.EventFactory;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.AlertYesNo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Liszt
 * @date 2020/9/3
 * Description 图片视频浏览
 */
public class PreviewMediaActivity extends FragmentActivity {

    private ActivityPreviewBinding ui;
    private List<LocalMedia> mediaList;
    private int currentPosition;
    private SparseArray<BaseMediaFragment> fragmentMap = new SparseArray<>();
    private String gid;
    private Long toUid;
    private MsgDao msgDao = new MsgDao();
    private MediaPagerAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_preview);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mediaList = getIntent().getParcelableArrayListExtra("data");
        currentPosition = getIntent().getIntExtra("position", 0);
        gid = getIntent().getStringExtra("gid");
        toUid = getIntent().getLongExtra(PictureConfig.TO_UID, 0L);
        initData();
        MessageManager.getInstance().initPreviewID(gid, toUid);
        MessageManager.getInstance().setCanStamp(false);
    }

    @Override //HOME键逻辑
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        setPressHome();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        initPager();
    }

    private void initPager() {
        mAdapter = new MediaPagerAdapter(getSupportFragmentManager());
        ui.viewPager.setAdapter(mAdapter);
        ui.viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                setCurrent(position, false);
                super.onPageSelected(position);
            }
        });
        setCurrent(currentPosition);
        ui.viewPager.setCurrentItem(currentPosition);
    }

    private void setCurrent(int currentPosition, boolean autoPlay) {
        if (fragmentMap != null) {
            if (fragmentMap.get(currentPosition) != null) {
                fragmentMap.get(currentPosition).setCurrentPosition(currentPosition);
                fragmentMap.get(currentPosition).setAutoPlay(autoPlay);
            }
            if (fragmentMap.get(currentPosition - 1) != null) {
                fragmentMap.get(currentPosition - 1).setAutoPlay(false);
            }
            if (fragmentMap.get(currentPosition + 1) != null) {
                fragmentMap.get(currentPosition + 1).setAutoPlay(false);
            }
        }
    }

    private void setCurrent(int currentPosition) {
        if (fragmentMap != null && fragmentMap.get(currentPosition) != null) {
            fragmentMap.get(currentPosition).setCurrentPosition(currentPosition);
        }
    }

    private void setPressHome() {
        if (fragmentMap != null && fragmentMap.get(currentPosition) != null) {
            fragmentMap.get(currentPosition).setPressHome(true);
        }
    }

    private class MediaPagerAdapter extends FragmentPagerAdapter {


        public MediaPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            BaseMediaFragment fragment;
            LocalMedia media = mediaList.get(position);
            media.setPosition(position);
            fragment = fragmentMap.get(position);
            if (fragment == null) {
                if (media.getMimeType() == PictureConfig.TYPE_VIDEO) {//视频
                    fragment = LookUpVideoFragment.newInstance(media, PictureConfig.FROM_DEFAULT);
                    if (fragmentMap.size() == 0) {
                        fragment.setAutoPlay(true);
                    } else {
                        fragment.setAutoPlay(false);
                    }
                    fragment.setCurrentPosition(currentPosition);
                } else {//图片
                    fragment = LookUpPhotoFragment.newInstance(media, PictureConfig.FROM_DEFAULT);
                    fragment.setCurrentPosition(currentPosition);
                }
                fragmentMap.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (object instanceof BaseMediaFragment) {
                fragmentMap.put(position, (BaseMediaFragment) object);
            }
        }

        @Override
        public int getCount() {
            return mediaList == null ? 0 : mediaList.size();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshChat(EventFactory.ClosePictureEvent event) {
        if (mediaList != null && event != null) {
            LocalMedia media = new LocalMedia();
            media.setMsg_id(event.msg_id);
            if (mediaList.contains(media)) {
                showDialog(event.name);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefreshChat(EventReceiveImage event) {
        if (mediaList != null && event != null) {
            String gid = event.getGid();
            long uid = event.getToUid();
            if (!TextUtils.isEmpty(gid) && !TextUtils.isEmpty(this.gid) && gid.equals(this.gid)) {
                updateMessageList();
            } else if (toUid != null && toUid.longValue() == uid) {
                updateMessageList();
            }
        }
    }

    private void showDialog(String name) {
        AlertYesNo alertYesNo = new AlertYesNo();
        alertYesNo.init(PreviewMediaActivity.this, null, "\"" + name + "\"" + "撤回了一条消息",
                "确定", null, new AlertYesNo.Event() {
                    @Override
                    public void onON() {

                    }

                    @Override
                    public void onYes() {
                        finish();
                    }
                });
        alertYesNo.show();
    }


    //更新浏览列表
    private void updateMessageList() {
        LogUtil.getLog().i("video_log", "--updateMessageList");
        if (mediaList != null) {
            int len = mediaList.size();
            LocalMedia localMedia = mediaList.get(len - 1);
            String msgId = localMedia.getMsg_id();
            MsgAllBean msgAllBean = msgDao.getMsgById(msgId);
            if (msgAllBean != null) {
                List<LocalMedia> temp = new ArrayList<>();
                List<MsgAllBean> listData = new MsgAction().getMsg4UserImgNew(gid, toUid, msgAllBean.getTimestamp());
                for (int i = 0; i < listData.size(); i++) {
                    MsgAllBean msg = listData.get(i);
                    LocalMedia lc = new LocalMedia();
                    //发送状态正常，则允许收藏 (阅后即焚改为允许收藏)
                    if (msg.getSend_state() == ChatEnum.ESendStatus.NORMAL) {
                        lc.setCanCollect(true);
                    }
                    lc.setPosition(len + i);
                    lc.setMsg_id(msg.getMsg_id());
                    if (msg.getMsg_type() == ChatEnum.EMessageType.MSG_VIDEO) {
                        lc.setMimeType(PictureConfig.TYPE_VIDEO);
                        String localUrl = msg.getVideoMessage().getLocalUrl();
                        if (StringUtil.isNotNull(localUrl)) {
                            File file = new File(localUrl);
                            if (file.exists()) {
                                lc.setVideoLocalUrl(localUrl);
                            }
                        }
                        lc.setVideoUrl(msg.getVideoMessage().getUrl());
                        lc.setVideoBgUrl(msg.getVideoMessage().getBg_url());
                        lc.setWidth((int) msg.getVideoMessage().getWidth());
                        lc.setHeight((int) msg.getVideoMessage().getHeight());
                        lc.setDuration(msg.getVideoMessage().getDuration());
                    } else {
                        lc.setMimeType(PictureConfig.TYPE_IMAGE);
                        lc.setCutPath(msg.getImage().getThumbnailShow());
                        lc.setCompressPath(msg.getImage().getPreviewShow());
                        lc.setPath(msg.getImage().getOriginShow());
                        lc.setSize(msg.getImage().getSize());
                        lc.setWidth(new Long(msg.getImage().getWidth()).intValue());
                        lc.setHeight(new Long(msg.getImage().getHeight()).intValue());
                        lc.setHasRead(msg.getImage().isReadOrigin());
                    }
                    temp.add(lc);
                }
                int size = temp.size();
                if (size > 0) {
                    mediaList.addAll(temp);
                    ui.viewPager.setCurrentItem(currentPosition);
                    setCurrent(currentPosition);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void startPreviewAll(String msgId) {
        MsgAllBean msgAllBean = msgDao.getMsgById(msgId);
        if (msgAllBean == null) {
            return;
        }
        Intent intent = PreviewMediaAllActivity.newIntent(this, gid, toUid, msgId, msgAllBean.getTimestamp());
        startActivity(intent);

    }
}
