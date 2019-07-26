package com.yanlong.im.user.ui;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerViewAccessibilityDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.jrmf360.tools.utils.ScreenUtil;
import com.luck.picture.lib.tools.ScreenUtils;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.UserSeting;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.user.bean.BackgroundImageBean;
import com.yanlong.im.utils.SpaceItemDecoration;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;
import net.cb.cb.library.view.MultiListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建人 shenxin
 * @创建时间 2019/7/26 0026 9:47
 */
public class BackgroundImageActivity extends AppActivity {

    private HeadView headView;
    private MultiListView mtListView;
    private int[] images = {R.mipmap.thumbnail_image1, R.mipmap.thumbnail_image2, R.mipmap.thumbnail_image3,
            R.mipmap.thumbnail_image4, R.mipmap.thumbnail_image5, R.mipmap.thumbnail_image6,
            R.mipmap.thumbnail_image7, R.mipmap.thumbnail_image8, R.mipmap.thumbnail_image9};
    private List<BackgroundImageBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_image);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        headView = findViewById(R.id.headView);
        mtListView = findViewById(R.id.mtListView);
        mtListView.init(new BackgrundImageAdapter());
        mtListView.getLoadView().setStateNormal();
        mtListView.getListView().setLayoutManager(new GridLayoutManager(this, 3));
        int screenWidth = ScreenUtils.getScreenWidth(this); //屏幕宽度
        int itemWidth = ScreenUtil.dp2px(this, 90); //每个item的宽度
        mtListView.getListView().addItemDecoration(new SpaceItemDecoration((screenWidth - itemWidth * 3) / 6, this));

    }

    private void initEvent() {
        headView.getActionbar().setTxtRight("完成");
        headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isSelect()) {
                        new MsgDao().userSetingImage(i + 1);
                        finish();
                    }
                }
            }
        });
    }


    private void initData() {
        UserSeting userSeting = new MsgDao().userSetingGet();
        int back = userSeting.getImageBackground();
        if(back == 0){
            for (int i = 0; i < images.length; i++) {
                BackgroundImageBean imageBean = new BackgroundImageBean();
                imageBean.setImage(images[i]);
                imageBean.setSelect(false);
                list.add(imageBean);
            }
        }else{
            for (int i = 0; i < images.length; i++) {
                BackgroundImageBean imageBean = new BackgroundImageBean();
                imageBean.setImage(images[i]);
                if((back-1) == i){
                    imageBean.setSelect(true);
                }else{
                    imageBean.setSelect(false);
                }
                list.add(imageBean);
            }
        }
    }


    class BackgrundImageAdapter extends RecyclerView.Adapter<BackgrundImageAdapter.BackgroundViewHolder> {

        @Override
        public BackgroundViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = inflater.inflate(R.layout.item_background_image, viewGroup, false);
            return new BackgroundViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BackgroundViewHolder viewHolder, final int i) {
            final BackgroundImageBean imageBean = list.get(i);
            viewHolder.ivThumbnailImage.setImageResource(imageBean.getImage());
            if (imageBean.isSelect()) {
                viewHolder.ckThumbnailImage.setChecked(true);
            } else {
                viewHolder.ckThumbnailImage.setChecked(false);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelect(i);
                }
            });

        }


        private void setSelect(int postion) {
            for (int i = 0; i < list.size(); i++) {
                if (i == postion) {
                    list.get(i).setSelect(true);
                } else {
                    list.get(i).setSelect(false);
                }
            }
            mtListView.notifyDataSetChange();
        }


        @Override
        public int getItemCount() {
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        class BackgroundViewHolder extends RecyclerView.ViewHolder {
            ImageView ivThumbnailImage;
            CheckBox ckThumbnailImage;

            public BackgroundViewHolder(@NonNull View itemView) {
                super(itemView);
                ivThumbnailImage = itemView.findViewById(R.id.iv_thumbnail_image);
                ckThumbnailImage = itemView.findViewById(R.id.ck_thumbnail_image);
            }
        }
    }


}
