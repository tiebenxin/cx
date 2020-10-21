package com.yanlong.im.circle.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.luck.picture.lib.PictureEnum;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.bean.VoteBean;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.view.RadiusCardView;

import net.cb.cb.library.utils.ScreenUtil;
import net.cb.cb.library.utils.StringUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-28
 * @updateAuthor
 * @updateDate
 * @description
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
public class VoteAdapter extends BaseQuickAdapter<VoteBean.Item, BaseViewHolder> {

    private List<MessageInfoBean.VoteAnswerBean.SumDataListBean> mVoteAnswerList;

    private int mType;// 1文字 2图片
    private int mAnswer;// 未投票-1，其他则为itemId:1-4
    private int mVoteSum;// 投票总数
    private int columnsCount;// 列数
    private boolean isMe;// 是否是自己

    /**
     * @param type    1文字 2图片
     * @param answer  未投票-1，其他则为itemId:1-4
     * @param voteSum 投票总数
     */
    public VoteAdapter(int columns, int type, int answer, int voteSum,
                       List<MessageInfoBean.VoteAnswerBean.SumDataListBean> voteAnswerList, boolean isMe) {
        super(type == PictureEnum.EVoteType.TXT ? R.layout.view_vote_item : R.layout.view_vote_item_pictrue);
        this.columnsCount = columns;
        mType = type;
        mAnswer = answer;
        mVoteSum = voteSum;
        mVoteAnswerList = voteAnswerList;
        this.isMe = isMe;
    }

    @Override
    protected void convert(BaseViewHolder helper, VoteBean.Item item) {
        ProgressBar progressBar = helper.getView(R.id.pb_progress);
        TextView tvPercentage = helper.getView(R.id.tv_percentage);
        TextView tvTitle = helper.getView(R.id.tv_title);
        RadioButton rbButtom = helper.getView(R.id.rb_buttom);

        if (mType == PictureEnum.EVoteType.TXT) {
            if (mAnswer == (helper.getAdapterPosition() + 1)) {
                progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.pb_vote_txt_green));
                tvPercentage.setTextColor(mContext.getResources().getColor(R.color.green_500));
                tvTitle.setText(item.getItem());
                tvTitle.setTextColor(mContext.getResources().getColor(R.color.green_500));
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.img_vote_yes);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                tvTitle.setCompoundDrawablePadding(ScreenUtil.dip2px(mContext, 10));
                tvTitle.setCompoundDrawables(drawable, null, null, null);
            } else {
                tvTitle.setTextColor(mContext.getResources().getColor(R.color.c_474747));
                tvTitle.setText(positionConvert(helper.getAdapterPosition()) + "     " + item.getItem());
                progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.pb_vote_txt_gray));
                tvPercentage.setTextColor(mContext.getResources().getColor(R.color.c_474747));
            }
        } else {
            RelativeLayout relativeLayout = helper.getView(R.id.relative_layout);
            ImageView ivPicture = helper.getView(R.id.iv_picture);
            int relativeHeight, pictureHeight;
            if (columnsCount == 2) {
                relativeHeight = (int) mContext.getResources().getDimension(R.dimen.dimen_101);
                pictureHeight = (int) mContext.getResources().getDimension(R.dimen.dimen_101);
            } else {
                relativeHeight = (int) mContext.getResources().getDimension(R.dimen.dimen_81);
                pictureHeight = (int) mContext.getResources().getDimension(R.dimen.dimen_81);
            }
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(relativeHeight, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, ScreenUtil.dip2px(mContext, 6), ScreenUtil.dip2px(mContext, 6), 0);
            relativeLayout.setLayoutParams(layoutParams);

            RadiusCardView.LayoutParams picParams = new RadiusCardView.LayoutParams(pictureHeight, pictureHeight);
            ivPicture.setLayoutParams(picParams);

            RelativeLayout layoutVoteBg = helper.getView(R.id.layout_vote_bg);
            ImageView ivPictrue = helper.getView(R.id.iv_picture);
            String path = StringUtil.loadThumbnail(item.getItem());
            if (isGif(path)) {
                Glide.with(mContext).load(path).listener(new RequestListener() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(ivPictrue);
            } else {
                Glide.with(mContext)
                        .asBitmap()
                        .load(path)
                        .apply(GlideOptionsUtil.imageOptions())
                        .into(ivPictrue);
            }
            if (mAnswer == (helper.getAdapterPosition() + 1)) {
                layoutVoteBg.setBackground(mContext.getResources().getDrawable(R.drawable.shape_vote_pictrue_green));
                progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.pb_vote_progress_green));
                tvPercentage.setTextColor(mContext.getResources().getColor(R.color.green_500));

                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.img_vote_yes);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
                tvTitle.setCompoundDrawablePadding(ScreenUtil.dip2px(mContext, 15));
                tvTitle.setCompoundDrawables(drawable, null, null, null);
            } else {
                tvTitle.setText(positionConvert(helper.getAdapterPosition()));
                layoutVoteBg.setBackground(mContext.getResources().getDrawable(R.drawable.shape_vote_pictrue_gray));
                progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.pb_vote_progress_gray));
                tvPercentage.setTextColor(mContext.getResources().getColor(R.color.c_474747));
            }
            if (mAnswer == -1 && !isMe) {
                rbButtom.setText("  选项" + positionConvert(helper.getAdapterPosition()));
                rbButtom.setVisibility(View.VISIBLE);
                tvPercentage.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);
            } else {
                rbButtom.setVisibility(View.GONE);
                tvPercentage.setVisibility(View.VISIBLE);
                tvTitle.setVisibility(View.VISIBLE);
            }
        }
        try {
            if (mVoteAnswerList != null && mVoteAnswerList.size() > 0 && (mAnswer != -1 || isMe)) {
                double result = 0.0;
                for (MessageInfoBean.VoteAnswerBean.SumDataListBean bean : mVoteAnswerList) {
                    if ((helper.getAdapterPosition() + 1) == bean.getId()) {
                        result = bean.getCnt() / Double.parseDouble(mVoteSum + "") * 100;
                        break;
                    }
                }
                progressBar.setProgress((int) result);
                BigDecimal bd = new BigDecimal(result);
                tvPercentage.setText(bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue() + "%");
            } else {
                progressBar.setProgress(0);
                if (mAnswer != -1 || isMe) {
                    tvPercentage.setText("0.0%");
                } else {
                    tvPercentage.setText("");
                }
            }
        } catch (Exception e) {
            progressBar.setProgress(0);
            if (isMe) {
                tvPercentage.setText("0.0%");
            } else {
                tvPercentage.setText("");
            }
        }
        helper.addOnClickListener(R.id.iv_picture, R.id.layout_vote_txt, R.id.layout_vote_bg);
    }

    private String positionConvert(int postion) {
        String value = "A";
        switch (postion) {
            case 0:
                value = "A";
                break;
            case 1:
                value = "B";
                break;
            case 2:
                value = "C";
                break;
            case 3:
                value = "D";
                break;
        }
        return value;
    }

    public boolean isGif(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.toLowerCase().contains(".gif")) {
                return true;
            }
        }
        return false;
    }
}
