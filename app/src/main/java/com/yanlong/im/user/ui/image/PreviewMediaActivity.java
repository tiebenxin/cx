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

import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.R;
import com.yanlong.im.chat.manager.MessageManager;
import com.yanlong.im.databinding.ActivityPreviewBinding;

import java.util.List;

/**
 * @author Liszt
 * @date 2020/9/3
 * Description
 */
public class PreviewMediaActivity extends FragmentActivity {

    private ActivityPreviewBinding ui;
    private List<LocalMedia> mediaList;
    private int currentPosition;
    private SparseArray<Fragment> fragmentMap = new SparseArray<>();

    public String[] strings = {"发送给朋友", "保存图片", "识别图中二维码", "编辑", "取消"};
    public String[] newStrings = {"发送给朋友", "保存图片", "收藏", "识别图中二维码", "编辑", "取消"};
    public String[] gifStrings = {"发送给朋友", "保存图片", "收藏", "识别图中二维码", "取消"};
    public String[] collectStrings = {"发送给朋友", "保存图片", "取消"};
    private String gid;
    private Long toUid;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_preview);
        mediaList = getIntent().getParcelableArrayListExtra("data");
        currentPosition = getIntent().getIntExtra("position", 0);
        gid = getIntent().getStringExtra("gid");
        toUid = getIntent().getLongExtra(PictureConfig.TO_UID, 0L);
        initData();
        MessageManager.getInstance().initPreviewID(gid, toUid);

    }

    private void initData() {
        initPager();
    }

    private void initPager() {
        ui.viewPager.setAdapter(new MediaPagerAdapter(getSupportFragmentManager()));
        ui.viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
            }
        });
        ui.viewPager.setCurrentItem(currentPosition);
    }

    private class MediaPagerAdapter extends FragmentPagerAdapter {


        public MediaPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            LocalMedia media = mediaList.get(position);
            currentPosition = position;
            fragment = fragmentMap.get(position);
            if (fragment == null) {
                if (media.getMimeType() == PictureConfig.TYPE_VIDEO) {//视频
                    fragment = LookUpVideoFragment.newInstance(media, currentPosition == position);
                } else {//图片
                    fragment = LookUpPhotoFragment.newInstance(media, currentPosition == position, PictureConfig.FROM_DEFAULT);
                }
                fragmentMap.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (object instanceof Fragment) {
                fragmentMap.put(position, (Fragment) object);
            }
        }

        @Override
        public int getCount() {
            return mediaList == null ? 0 : mediaList.size();
        }
    }


}
