package com.yanlong.im.user.ui.image;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.R;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.databinding.ActivityPreviewBinding;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_preview);
        mediaList = getIntent().getParcelableArrayListExtra("data");
        currentPosition = getIntent().getIntExtra("position", 0);
        gid = getIntent().getStringExtra("gid");
        toUid = getIntent().getLongExtra(PictureConfig.TO_UID, 0L);
        initData();
        MessageManager.getInstance().initPreviewID(gid, toUid);
        MessageManager.getInstance().setCanStamp(false);
    }

    private void initData() {
        initPager();
    }

    private void initPager() {
        ui.viewPager.setAdapter(new MediaPagerAdapter(getSupportFragmentManager()));
        ui.viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                setCurrent(position);
                super.onPageSelected(position);
            }
        });
        setCurrent(currentPosition);
        ui.viewPager.setCurrentItem(currentPosition);
    }

    private void setCurrent(int currentPosition) {
        if (fragmentMap != null && fragmentMap.get(currentPosition) != null) {
            fragmentMap.get(currentPosition).setCurrentPosition(currentPosition);
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
                } else {//图片
                    fragment = LookUpPhotoFragment.newInstance(media, PictureConfig.FROM_DEFAULT);
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


}
