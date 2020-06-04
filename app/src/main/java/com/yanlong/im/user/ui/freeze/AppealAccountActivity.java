package com.yanlong.im.user.ui.freeze;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityAppealAccountBinding;
import com.yanlong.im.user.bean.ImageBean;
import com.yanlong.im.user.ui.FeedbackShowImageActivity;
import com.yanlong.im.utils.GlideOptionsUtil;

import net.cb.cb.library.view.AppActivity;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_appeal_account);

    }


    private class ComplaintUploadAdatper extends RecyclerView.Adapter<ComplaintUploadAdatper.ComplaintUploadViewHolder> {

        public void addImage(ImageBean imageBean) {
            if (list.size() == 3) {
                list.remove(2);
                list.add(list.size(), imageBean);
            } else {
                list.add(list.size() - 1, imageBean);
            }
            ui.recyclerView.getAdapter().notifyDataSetChanged();
        }

        public void remove(int postion) {
            if (list.size() == 3) {
                if (list.get(2).getType() == 0) {
                    list.remove(postion);
                } else {
                    list.remove(postion);
                    ImageBean imageBean = new ImageBean();
                    imageBean.setType(0);
                    list.add(imageBean);
                }
            } else {
                list.remove(postion);
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
//                        initPopup();
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

}
