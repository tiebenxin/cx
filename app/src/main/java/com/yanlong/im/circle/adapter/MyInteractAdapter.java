package com.yanlong.im.circle.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.InteractMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @类名：我的互动适配器
 * @Date：2020/10/12
 * @by zjy
 * @备注：
 */

public class MyInteractAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private Activity activity;
    private List<InteractMessage> dataList;//列表数据
    private RequestOptions mRequestOptions;

    public MyInteractAdapter(Activity activity, List<InteractMessage> dataList) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.dataList = new ArrayList<>();
        if(dataList!=null && dataList.size()>0){
            this.dataList.addAll(dataList);
        }
        init();
    }

    //初始化相关设置
    private void init() {
        //图片相关设置
        mRequestOptions = RequestOptions.centerInsideTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .placeholder(com.yanlong.im.R.drawable.ic_info_head)
                .error(com.yanlong.im.R.drawable.ic_info_head)
                .centerCrop();
    }

    //item总数
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    //刷新数据
    public void updateList(List<InteractMessage> list) {
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }


    //具体显示逻辑
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        //子项
        if (viewHolder instanceof MyInteractAdapter.ContentHolder) {
            ContentHolder holder = (ContentHolder) viewHolder;
            if (dataList != null && dataList.size() > 0) {
                if (dataList.get(position) != null) {
                    InteractMessage bean = dataList.get(position);
                    //头像
                    if (!TextUtils.isEmpty(bean.getAvatar())) {
                        Glide.with(activity)
                                .load(bean.getAvatar())
                                .apply(mRequestOptions)
                                .into(holder.ivHeader);
                    } else {
                        Glide.with(activity)
                                .load(R.drawable.ic_info_head)
                                .apply(mRequestOptions)
                                .into(holder.ivHeader);
                    }
                    //互动内容显示
                    SpannableStringBuilder spanBuilder = new SpannableStringBuilder();
                    int start = 0;
                    int end;
                    //昵称->蓝字
                    if (!TextUtils.isEmpty(bean.getNickname())){
                        spanBuilder.append(bean.getNickname());
                        end = bean.getNickname().length();
                    }else {
                        spanBuilder.append("未知用户");
                        end = 4;
                    }
                    spanBuilder.setSpan(15, 0, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spanBuilder.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.c_4886C5)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    //操作->加粗
                    start = end;
                    if(bean.getInteractType()==0){
                        spanBuilder.append(" 关注 ");//加两个空格
                        end = end + 4;//继续给下一个字体设置样式

                    }else if(bean.getInteractType()==1){
                        spanBuilder.append(" 赞 ");
                        end = end + 2;
                        //赞了->文字(暂未返回任何东西)
                        if(bean.getResourceType()==0){
                            holder.ivImg.setVisibility(View.GONE);
                            holder.ivPlay.setVisibility(View.GONE);
                            holder.tvTxt.setVisibility(View.GONE);
                            holder.layoutVoice.setVisibility(View.GONE);
                        }else if(bean.getResourceType()==1 || bean.getResourceType()==3){
                            //赞了->图片、视频
                            if (!TextUtils.isEmpty(bean.getResource())) {
                                Glide.with(activity)
                                        .load(bean.getResource())
                                        .apply(mRequestOptions)
                                        .into(holder.ivImg);
                            } else {
                                Glide.with(activity)
                                        .load(R.mipmap.default_image)
                                        .apply(mRequestOptions)
                                        .into(holder.ivImg);
                            }
                            holder.ivImg.setVisibility(View.VISIBLE);
                            holder.tvTxt.setVisibility(View.GONE);
                            holder.layoutVoice.setVisibility(View.GONE);
                            if(bean.getResourceType()==1){
                                holder.ivPlay.setVisibility(View.GONE);
                            }else {
                                holder.ivPlay.setVisibility(View.VISIBLE);
                            }
                        }else if(bean.getResourceType()==2){
                            //赞了->语音
                            holder.ivImg.setVisibility(View.GONE);
                            holder.ivPlay.setVisibility(View.GONE);
                            holder.tvTxt.setVisibility(View.GONE);
                            holder.layoutVoice.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(bean.getResource())) {
                                holder.tvVoiceSec.setText(bean.getResource()+"s");
                            } else {
                                holder.tvVoiceSec.setText("0s");
                            }
                        }
                    }
                    spanBuilder.setSpan(15, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spanBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spanBuilder.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.c_343434)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    //操作后->普通黑体
                    start = end;
                    if(bean.getInteractType()==0){
                        spanBuilder.append("了我");
                        end = end + 2;
                    }else if(bean.getInteractType()==1){
                        spanBuilder.append("了我的瞬间");
                        end = end + 5;
                    }
                    spanBuilder.setSpan(15, 0, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spanBuilder.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.c_484848)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    //最终显示内容
                    holder.tvContent.setText(spanBuilder);
                }
            }
        }
    }


    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View itemView = inflater.inflate(R.layout.item_my_interact, parent, false);
        return new MyInteractAdapter.ContentHolder(itemView);
    }

    // 子项
    private class ContentHolder extends RecyclerView.ViewHolder {
        private ImageView ivHeader;
//        private TextView tvWho;
//        private TextView tvAction;
//        private TextView tvActionContent;
        private TextView tvContent;
        private ImageView ivImg;
        private ImageView ivPlay;
        private TextView tvTxt;
        private LinearLayout layoutVoice;
        private TextView tvVoiceSec;

        private RelativeLayout layoutItem;

        public ContentHolder(View itemView) {

            super(itemView);
            ivHeader = itemView.findViewById(R.id.iv_header);
//            tvWho = itemView.findViewById(R.id.tv_who);
//            tvAction = itemView.findViewById(R.id.tv_action);
//            tvActionContent = itemView.findViewById(R.id.tv_action_content);
            tvContent = itemView.findViewById(R.id.tv_content);
            ivImg = itemView.findViewById(R.id.iv_img);
            ivPlay = itemView.findViewById(R.id.iv_play);
            tvTxt = itemView.findViewById(R.id.tv_txt);
            layoutVoice = itemView.findViewById(R.id.layout_voice);
            tvVoiceSec = itemView.findViewById(R.id.tv_voice_sec);

        }
    }

}
