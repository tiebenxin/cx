package com.yanlong.im.circle.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
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

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.yanlong.im.R;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.circle.bean.InteractMessage;
import com.yanlong.im.circle.bean.NewTrendDetailsBean;
import com.yanlong.im.circle.details.CircleDetailsActivity;
import com.yanlong.im.circle.mycircle.MyCircleAction;
import com.yanlong.im.circle.mycircle.FriendTrendsActivity;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserBean;
import com.yanlong.im.utils.ExpressionUtil;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.yanlong.im.circle.adapter.CircleFlowAdapter.MESSAGE_DEFAULT;
import static com.yanlong.im.circle.adapter.CircleFlowAdapter.MESSAGE_VOTE;
import static com.yanlong.im.circle.follow.FollowFragment.IS_OPEN;

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
    private UserBean userBean;
    private MsgDao msgDao;

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
        userBean = (UserBean) new UserAction().getMyInfo();
        msgDao = new MsgDao();
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
        if (viewHolder instanceof ContentHolder) {
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
                    //点击事件
                    holder.ivHeader.setOnClickListener(v -> {
                        Intent intent = new Intent(activity, FriendTrendsActivity.class);
                        intent.putExtra("uid",bean.getFromUid());
                        activity.startActivity(intent);
                    });
                    holder.layoutItem.setOnClickListener(v -> {
                        if(bean.getInteractType()==0){//如果是关注，点击整个子项跳朋友圈
                            Intent intent = new Intent(activity, FriendTrendsActivity.class);
                            intent.putExtra("uid",bean.getFromUid());
                            activity.startActivity(intent);
                            dataList.get(position).setGreyColor(true);
                            notifyItemChanged(position);
                            msgDao.updateMsgGreyColor(bean.getMsgId());
                        }else {
                            //跳详情
                            httpGetNewDetails(bean.getMomentId(),bean.getMomentUid(),position,bean.getMsgId());
                        }
                    });
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
                    if(bean.isGreyColor()){//是否已点击并置灰，默认false，点击后true，本地记录属性
                        spanBuilder.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.c_9A9A9A)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }else {
                        spanBuilder.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.c_484848)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                    //操作->加粗
                    start = end;
                    if(bean.getInteractType()==0){//关注
                        spanBuilder.append(" 关注 ");//加两个空格
                        end = end + 4;//继续给下一个字体设置样式
                        holder.ivImg.setVisibility(View.GONE);
                        holder.ivPlay.setVisibility(View.GONE);
                        holder.tvTxt.setVisibility(View.GONE);
                        holder.layoutVoice.setVisibility(View.GONE);
                    }else if(bean.getInteractType()==1 || bean.getInteractType()==6){//赞 + 赞了我的评论
                        spanBuilder.append(" 赞 ");
                        end = end + 3;
                        //赞了->文字(暂未返回文字内容)
                        if(bean.getResourceType()==0){
                            holder.ivImg.setVisibility(View.GONE);
                            holder.ivPlay.setVisibility(View.GONE);
                            holder.layoutVoice.setVisibility(View.GONE);
                            if (!TextUtils.isEmpty(bean.getResource())) {
                                holder.tvTxt.setVisibility(View.VISIBLE);
                                holder.tvTxt.setText(getSpan(bean.getResource()));
                            } else {
                                holder.tvTxt.setVisibility(View.GONE);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                holder.layoutOne.setForeground(null);
                            }
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
                            if(bean.isGreyColor()){//图片视频置灰效果
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    holder.layoutOne.setForeground(activity.getDrawable(R.color.c_80ffffff));
                                }
                            }else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    holder.layoutOne.setForeground(null);
                                }
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
                            //语音仅换图标，无需置灰
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                holder.layoutOne.setForeground(null);
                            }
                            if(bean.isGreyColor()){
                                holder.layoutVoice.setBackgroundResource(R.drawable.shape_5radius_solid_d8d8d8);
                            }else {
                                holder.layoutVoice.setBackgroundResource(R.drawable.shape_5radius_solid_73c16f);
                            }
                        }
                    }else if(bean.getInteractType()==2 || bean.getInteractType()==3 || bean.getInteractType()==5){//评论、回复、删除评论
                        if(bean.getInteractType()==2){
                            spanBuilder.append(" 评论 ");
                        }else if(bean.getInteractType()==3){
                            spanBuilder.append(" 回复 ");
                        }else {
                            spanBuilder.append(" 删除 ");
                        }
                        end = end + 4;
                        //评论->文字
                        if(bean.getResourceType()==0){
                            holder.ivImg.setVisibility(View.GONE);
                            holder.ivPlay.setVisibility(View.GONE);
                            holder.tvTxt.setVisibility(View.GONE);
                            holder.layoutVoice.setVisibility(View.GONE);
                            if (!TextUtils.isEmpty(bean.getResource())) {
                                holder.tvTxt.setVisibility(View.VISIBLE);
                                holder.tvTxt.setText(getSpan(bean.getResource()));
                            } else {
                                holder.tvTxt.setVisibility(View.GONE);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                holder.layoutOne.setForeground(null);
                            }
                        }else if(bean.getResourceType()==1 || bean.getResourceType()==3){
                            //评论->图片、视频
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
                            if(bean.isGreyColor()){//图片视频置灰效果
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    holder.layoutOne.setForeground(activity.getDrawable(R.color.c_80ffffff));
                                }
                            }else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    holder.layoutOne.setForeground(null);
                                }
                            }
                        }else if(bean.getResourceType()==2){
                            //评论->语音
                            holder.ivImg.setVisibility(View.GONE);
                            holder.ivPlay.setVisibility(View.GONE);
                            holder.tvTxt.setVisibility(View.GONE);
                            holder.layoutVoice.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(bean.getResource())) {
                                holder.tvVoiceSec.setText(bean.getResource()+"s");
                            } else {
                                holder.tvVoiceSec.setText("0s");
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                holder.layoutOne.setForeground(null);
                            }
                            if(bean.isGreyColor()){
                                holder.layoutVoice.setBackgroundResource(R.drawable.shape_5radius_solid_d8d8d8);
                            }else {
                                holder.layoutVoice.setBackgroundResource(R.drawable.shape_5radius_solid_73c16f);
                            }
                        }
                    }else if(bean.getInteractType()==4){//投票
                        spanBuilder.append(" 投票 ");
                        end = end + 4;
                        //投票->文字
                        holder.ivImg.setVisibility(View.GONE);
                        holder.ivPlay.setVisibility(View.GONE);
                        holder.tvTxt.setVisibility(View.VISIBLE);
                        holder.layoutVoice.setVisibility(View.GONE);
                        if (!TextUtils.isEmpty(bean.getResource())) {
                            holder.tvTxt.setText(getSpan(bean.getResource()));
                        } else {
                            holder.tvTxt.setText("暂无投票内容");
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            holder.layoutOne.setForeground(null);
                        }
                    }
                    spanBuilder.setSpan(15, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    spanBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    if(bean.isGreyColor()){
                        spanBuilder.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.c_9A9A9A)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }else {
                        spanBuilder.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.c_343434)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                    //操作后->普通黑体
                    start = end;
                    if(bean.getInteractType()==0){//关注
                        spanBuilder.append("了我");
                        end = end + 2;
                    }else if(bean.getInteractType()==1){//赞
                        spanBuilder.append("了我的瞬间");
                        end = end + 5;
                    }else if(bean.getInteractType()==2){//评论
                        spanBuilder.append("了我的瞬间：");
                        end = end + 6;
                    }else if(bean.getInteractType()==3){//回复
                        spanBuilder.append("了我的评论：");
                        end = end + 6;
                    }else if(bean.getInteractType()==4){//投票
                        spanBuilder.append("给了");
                        if(bean.getInteractId()==1){
                            spanBuilder.append("A");
                        }else if(bean.getInteractId()==2){
                            spanBuilder.append("B");
                        }else if(bean.getInteractId()==3){
                            spanBuilder.append("C");
                        }else {
                            spanBuilder.append("D");
                        }
                        end = end + 3;
                    }else if(bean.getInteractType()==5){//删除评论
                        spanBuilder.append("了一条评论：");
                        end = end + 6;
                    }else {//赞了我的评论
                        spanBuilder.append("了我的评论");
                        end = end + 5;
                    }
                    spanBuilder.setSpan(15, 0, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    if(bean.isGreyColor()){
                        spanBuilder.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.c_9A9A9A)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }else {
                        spanBuilder.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.c_484848)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                    //内容->普通黑体
                    start = end;
                    if(bean.getInteractType()==2 || bean.getInteractType()==3 || bean.getInteractType()==5){
                        if(!TextUtils.isEmpty(bean.getContent())){
                            spanBuilder.append(getSpan(bean.getContent()));
                            end = end + bean.getContent().length();
                        }
                    }
                    spanBuilder.setSpan(15, 0, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    if(bean.isGreyColor()){
                        spanBuilder.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.c_9A9A9A)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }else {
                        spanBuilder.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.c_484848)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
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
        private TextView tvContent;
        private ImageView ivImg;
        private ImageView ivPlay;
        private TextView tvTxt;
        private LinearLayout layoutVoice;
        private TextView tvVoiceSec;
        private RelativeLayout layoutItem;
        private RelativeLayout layoutOne;

        public ContentHolder(View itemView) {
            super(itemView);
            ivHeader = itemView.findViewById(R.id.iv_header);
            tvContent = itemView.findViewById(R.id.tv_content);
            ivImg = itemView.findViewById(R.id.iv_img);
            ivPlay = itemView.findViewById(R.id.iv_play);
            tvTxt = itemView.findViewById(R.id.tv_txt);
            layoutVoice = itemView.findViewById(R.id.layout_voice);
            tvVoiceSec = itemView.findViewById(R.id.tv_voice_sec);
            layoutItem = itemView.findViewById(R.id.layout_item);
            layoutOne = itemView.findViewById(R.id.layout_one);
        }
    }

    //跳到动态详情
    private void gotoCircleDetailsActivity(NewTrendDetailsBean bean) {
        Postcard postcard = ARouter.getInstance().build(CircleDetailsActivity.path);
        postcard.withBoolean(IS_OPEN, false);
        postcard.withString(CircleDetailsActivity.FROM, "MyInteract");//来自我的互动
        postcard.withBoolean(CircleDetailsActivity.SOURCE_TYPE, bean.getMyFollow()==1?true:false);//是否关注
        postcard.withString(CircleDetailsActivity.ITEM_DATA, new Gson().toJson(bean));
        if(!TextUtils.isEmpty(bean.getOtherMomentVo().getVote())){//是否含有投票
            postcard.withInt(CircleDetailsActivity.ITEM_DATA_TYPE, MESSAGE_VOTE);
        }else {
            postcard.withInt(CircleDetailsActivity.ITEM_DATA_TYPE, MESSAGE_DEFAULT);
        }
        postcard.navigation();
    }

    //新的获取动态详情接口(含头像、评论列表)
    public void httpGetNewDetails(Long momentId, Long momentUid,int position,String msgId){
        new MyCircleAction().httpGetNewDetails(momentId, momentUid, new CallBack<ReturnBean<NewTrendDetailsBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<NewTrendDetailsBean>> call, Response<ReturnBean<NewTrendDetailsBean>> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    if(response.body().getData()!=null){
                        NewTrendDetailsBean bean = response.body().getData();
                        //跳详情
                        gotoCircleDetailsActivity(bean);
                        //如果是删除评论，直接提示
                        if(dataList.get(position).getInteractType()==5){
                            ToastUtil.show("该评论已删除");
                        }
                    }
                }else {
                    //如果动态已经被删除或更改权限，直接提示已经被隐藏了
                    if(response.body().getCode()==100104){
                        ToastUtil.show(response.body().getMsg());
                    }else {
                        ToastUtil.show("获取动态详情失败");
                    }
                }
                //如果没点击过，则置灰并保存点击过状态
                if(dataList.get(position).isGreyColor()==false){
                    dataList.get(position).setGreyColor(true);
                    notifyItemChanged(position);
                    msgDao.updateMsgGreyColor(msgId);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean<NewTrendDetailsBean>> call, Throwable t) {
                super.onFailure(call, t);
                LogUtil.getLog().e(t.getMessage());
                ToastUtil.show("获取动态详情失败");
            }
        });
    }


    private SpannableString getSpan(String msg) {
        Integer fontSize = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);
        SpannableString spannableString = null;
        if (fontSize != null) {
            spannableString = ExpressionUtil.getExpressionString(activity, fontSize.intValue(), msg);
        } else {
            spannableString = ExpressionUtil.getExpressionString(activity, ExpressionUtil.DEFAULT_SIZE, msg);
        }
        return spannableString;
    }

}
