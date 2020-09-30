package com.yanlong.im.circle.adapter;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.luck.picture.lib.PictureEnum;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.MessageInfoBean;
import com.yanlong.im.circle.bean.VoteBean;
import com.yanlong.im.utils.GlideOptionsUtil;

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

    /**
     * @param type    1文字 2图片
     * @param answer  未投票-1，其他则为itemId:1-4
     * @param voteSum 投票总数
     */
    public VoteAdapter(int type, int answer, int voteSum, List<MessageInfoBean.VoteAnswerBean.SumDataListBean> voteAnswerList) {
        super(type == PictureEnum.EVoteType.TXT ? R.layout.view_vote_item : R.layout.view_vote_item_pictrue);
        mType = type;
        mAnswer = answer;
        mVoteSum = voteSum;
        mVoteAnswerList = voteAnswerList;
    }

    @Override
    protected void convert(BaseViewHolder helper, VoteBean.Item item) {
        ProgressBar progressBar = helper.getView(R.id.pb_progress);
        TextView tvPercentage = helper.getView(R.id.tv_percentage);
        if (mType == PictureEnum.EVoteType.TXT) {
            helper.setText(R.id.tv_title, positionConvert(helper.getAdapterPosition()) + item.getItem());
            if (mAnswer == (helper.getAdapterPosition() + 1)) {
                progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.pb_vote_progress_green));
                tvPercentage.setTextColor(mContext.getResources().getColor(R.color.green_500));
            } else {
                progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.pb_vote_progress_gray));
                tvPercentage.setTextColor(mContext.getResources().getColor(R.color.c_474747));
            }
        } else {
            LinearLayout layoutVoteBg = helper.getView(R.id.layout_vote_bg);
            helper.setText(R.id.tv_title, positionConvert(helper.getAdapterPosition()));
            ImageView ivPictrue = helper.getView(R.id.iv_picture);
            Glide.with(mContext)
                    .asBitmap()
                    .load(item.getItem())
                    .apply(GlideOptionsUtil.headImageOptions())
                    .into(ivPictrue);
            if (mAnswer == (helper.getAdapterPosition() + 1)) {
                layoutVoteBg.setBackground(mContext.getResources().getDrawable(R.drawable.shape_vote_pictrue_green));
                progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.pb_voice_pictrue_green));
                tvPercentage.setTextColor(mContext.getResources().getColor(R.color.green_500));
            } else {
                layoutVoteBg.setBackground(mContext.getResources().getDrawable(R.drawable.shape_vote_pictrue_gray));
                progressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.pb_voice_pictrue_gray));
                tvPercentage.setTextColor(mContext.getResources().getColor(R.color.c_474747));
            }
        }
        try {
            if (mVoteAnswerList != null && mVoteAnswerList.size() > 0) {
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
                tvPercentage.setText("0.0%");
            }
        } catch (Exception e) {
            progressBar.setProgress(0);
            tvPercentage.setText("0.0%");
        }
        helper.addOnClickListener(R.id.layout_vote_pictrue, R.id.layout_vote_txt);
    }

    private String positionConvert(int postion) {
        String value = "A  ";
        switch (postion) {
            case 0:
                value = "A  ";
                break;
            case 1:
                value = "B  ";
                break;
            case 2:
                value = "C  ";
                break;
            case 3:
                value = "D  ";
                break;
        }
        return value;
    }
}
