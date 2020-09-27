package com.yanlong.im.circle.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.TrendBean;
import com.yanlong.im.circle.details.CircleDetailsActivity;
import com.yanlong.im.circle.mycircle.TempAction;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.inter.ICommonSelectClickListner;
import net.cb.cb.library.inter.ITrendClickListner;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.DialogHelper;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.yanlong.im.circle.follow.FollowFragment.IS_OPEN;

/**
 * @类名：我的动态(我的朋友圈)适配器 (含上拉加载)
 * @Date：2019/12/9
 * @by zjy
 * @备注：
 */

public class MyTrendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //头部尾部数量(可直接修改数量控制头部和尾部)
    private final static int HEAD_COUNT = 0;
    private final static int FOOT_COUNT = 1;
    //区分布局类型
    private final static int TYPE_HEAD = 0;
    private final static int TYPE_CONTENT = 1;
    private final static int TYPE_FOOTER = 2;

    // 当前加载状态，默认为隐藏底部
    private int loadState = 4;
    // 正在加载
    public final int LOADING = 1;
    // 加载更多
    public final int LOADING_MORE = 2;
    // 加载到底
    public final int LOADING_END = 3;
    // 隐藏底部
    public final int LOADING_GONE = 4;

    private int type;//1 我的朋友圈 2 别人的朋友圈
    private List<String> listOne = Arrays.asList("置顶","取消置顶");
    private List<String> listTwo = Arrays.asList("广场可见","仅好友可见","仅陌生人可见","自己可见");

    private LayoutInflater inflater;
    private Activity activity;
    private List<TrendBean> dataList;//动态列表数据
    private Drawable dislike;
    private Drawable like;
    private TempAction action;
//    private RequestOptions mRequestOptions;

    public MyTrendsAdapter(Activity activity, List<TrendBean> dataList, int type) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.type = type;
        this.dataList = new ArrayList<>();
        if(dataList!=null && dataList.size()>0){
            this.dataList.addAll(dataList);
        }
        init();
        //图片相关设置
//        mRequestOptions = RequestOptions.centerInsideTransform()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .skipMemoryCache(false)
//                .placeholder(R.drawable.ic_info_head)
//                .error(R.drawable.ic_info_head)
//                .centerCrop();
    }

    //初始化相关设置
    private void init() {
        dislike = activity.getResources().getDrawable(R.mipmap.ic_circle_give,null);
        like = activity.getResources().getDrawable(R.mipmap.ic_circle_like,null);
        dislike.setBounds(0, 0, dislike.getMinimumWidth(), dislike.getMinimumHeight());
        like.setBounds(0, 0, like.getMinimumWidth(), like.getMinimumHeight());
        action = new TempAction();
    }

    //刷新数据
    public void updateList(List<TrendBean> list) {
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    //加载更多
    public void addMoreList(List list) {
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    //列表内容数量
    public int getContentSize() {
        return dataList.size();
    }


    @Override
    public int getItemViewType(int position) {
        int contentSize = getContentSize();
        if (HEAD_COUNT != 0 && position == 0) { // 头部
            return TYPE_HEAD;
        } else if (FOOT_COUNT != 0 && position == HEAD_COUNT + contentSize) { // 尾部
            return TYPE_FOOTER;
        } else {
            return TYPE_CONTENT; // 内容
        }
    }

    //item总数
    @Override
    public int getItemCount() {
        return dataList.size() + HEAD_COUNT + FOOT_COUNT;
    }


    //具体显示逻辑
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        //子项
        if (viewHolder instanceof MyTrendsAdapter.ContentHolder) {
            ContentHolder holder = (ContentHolder) viewHolder;
            if (dataList != null && dataList.size() > 0) {
                if (dataList.get(position) != null) {
                    TrendBean bean = dataList.get(position);
                    //时间
                    if(!TextUtils.isEmpty(bean.getCreateTime())){
                        holder.tvTime.setText(TimeToString.YYYY_MM_DD_HH_MM(Long.parseLong(bean.getCreateTime())));
                    }
                    //内容
                    if(!TextUtils.isEmpty(bean.getContent())){
                        holder.tvContent.setText(bean.getContent());
                    }
                    //位置
                    if(!TextUtils.isEmpty(bean.getCity())){
                        holder.tvLocation.setText(bean.getCity());
                    }
                    //点赞数 评论数
                    holder.tvLike.setText(bean.getLikeCount()+"");
                    holder.tvComment.setText(bean.getCommentCount()+"");
                    //说说可见度
                    if(bean.getVisibility()==0){
                        holder.tvCanSee.setText("广场可见");
                    }else if(bean.getVisibility()==1){
                        holder.tvCanSee.setText("好友可见");
                    }else if(bean.getVisibility()==1){
                        holder.tvCanSee.setText("陌生人可见");
                    }else {
                        holder.tvCanSee.setText("自己可见");
                    }
                    //设置-> 置顶 权限 删除
                    holder.ivSetup.setOnClickListener(v -> {
                        DialogHelper.getInstance().createTrendDialog(activity, new ITrendClickListner() {
                            @Override
                            public void clickIsTop() {
                                DialogHelper.getInstance().createCommonSelectListDialog(activity, listOne, new ICommonSelectClickListner() {
                                    @Override
                                    public void selectOne() {
                                        //置顶
                                    }

                                    @Override
                                    public void selectTwo() {
                                        //取消置顶
                                    }

                                    @Override
                                    public void selectThree() {

                                    }

                                    @Override
                                    public void selectFour() {

                                    }

                                    @Override
                                    public void onCancle() {

                                    }
                                });
                            }

                            @Override
                            public void clickAuthority() {

                            }

                            @Override
                            public void clickDelete() {

                            }

                            @Override
                            public void clickCancle() {

                            }
                        });

                    });
                    //跳详情
                    holder.layoutItem.setOnClickListener(v -> gotoCircleDetailsActivity(false));
                    //是否置顶
                    if(bean.getIsTop()==0){
                        holder.ivIstop.setVisibility(View.GONE);
                        holder.tvIstop.setVisibility(View.GONE);
                    }else {
                        holder.ivIstop.setVisibility(View.VISIBLE);
                        holder.tvIstop.setVisibility(View.VISIBLE);
                    }
                    //是否点赞
                    if(bean.getLike()==0){
                        holder.tvLike.setCompoundDrawables(dislike,null,null,null);
                    }else {
                        holder.tvLike.setCompoundDrawables(like,null,null,null);
                    }
                    holder.tvLike.setOnClickListener(v -> {
                        if(bean.getLike()==0){
                            httpLike(bean.getId(),bean.getUid(),holder.tvLike,position,bean.getLikeCount());
                        }else {
                            httpCancleLike(bean.getId(),bean.getUid(),holder.tvLike,position,bean.getLikeCount());
                        }
                    });
                }
            }
        } else {
            //加载更多-尾部
            FootHolder holder = (FootHolder) viewHolder;
            switch (loadState) {
                case LOADING:
                    holder.footerLayout.setVisibility(View.VISIBLE);
                    holder.loadingMore.setVisibility(View.GONE);
                    holder.loading.setVisibility(View.VISIBLE);
                    holder.loadingNoMore.setVisibility(View.GONE);
                    break;
                case LOADING_MORE:
                    holder.footerLayout.setVisibility(View.VISIBLE);
                    holder.loadingMore.setVisibility(View.VISIBLE);
                    holder.loading.setVisibility(View.GONE);
                    holder.loadingNoMore.setVisibility(View.GONE);
                    break;
                case LOADING_END:
                    holder.footerLayout.setVisibility(View.VISIBLE);
                    holder.loadingMore.setVisibility(View.GONE);
                    holder.loading.setVisibility(View.GONE);
                    holder.loadingNoMore.setVisibility(View.VISIBLE);
                    break;
                case LOADING_GONE:
                    holder.footerLayout.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        //进行判断显示类型，来创建返回不同的View
        if (position == TYPE_CONTENT) {
            View itemView = inflater.inflate(R.layout.item_trend, parent, false);
            return new MyTrendsAdapter.ContentHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.main_footer_layout, parent, false);
            return new MyTrendsAdapter.FootHolder(itemView);
        }
    }

    // 子项
    private class ContentHolder extends RecyclerView.ViewHolder {
        private TextView tvTime;
        private TextView tvCanSee;
        private TextView tvContent;
        private TextView tvLocation;
        private TextView tvLike;
        private TextView tvComment;
        private ImageView ivSetup;
        private ImageView ivIstop;
        private TextView tvIstop;
        private RelativeLayout layoutItem;

        public ContentHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvCanSee = itemView.findViewById(R.id.tv_can_see);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvLocation = itemView.findViewById(R.id.tv_location);
            layoutItem = itemView.findViewById(R.id.layout_item);
            tvLike = itemView.findViewById(R.id.tv_like);
            tvComment = itemView.findViewById(R.id.tv_comment);
            ivSetup = itemView.findViewById(R.id.iv_setup);
            ivIstop = itemView.findViewById(R.id.iv_istop);
            tvIstop = itemView.findViewById(R.id.tv_istop);
        }
    }

    // 尾部
    class FootHolder extends RecyclerView.ViewHolder {
        private TextView loading;
        private TextView loadingMore;
        private TextView loadingNoMore;
        private LinearLayout footerLayout;

        public FootHolder(View itemView) {
            super(itemView);
            loading = itemView.findViewById(R.id.loading);
            loadingMore = itemView.findViewById(R.id.loading_more);
            loadingNoMore = itemView.findViewById(R.id.loading_no_more);
            footerLayout = itemView.findViewById(R.id.footer_layout);
        }
    }

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    /**
     * 发请求->关注
     */
//    private void httpToFollow(long uid,int position, TextView tvFollow) {
//        new TempAction().httpToFollow(uid, new CallBack<ReturnBean>() {
//            @Override
//            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
//                super.onResponse(call, response);
//                if (response.body() == null) {
//                    return;
//                }
//                if (response.body().isOk()){
//                    ToastUtil.show("关注成功");
//                    dataList.get(position).setStat(1);
//                    notifyItemChanged(position,tvFollow);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ReturnBean> call, Throwable t) {
//                super.onFailure(call, t);
//                ToastUtil.show("关注失败");
//            }
//        });
//    }


    private void gotoCircleDetailsActivity(boolean isOpen) {
        Postcard postcard = ARouter.getInstance().build(CircleDetailsActivity.path);
        postcard.withBoolean(IS_OPEN, isOpen);
        postcard.navigation();
    }

    /**
     * 发请求->点赞
     */
    private void httpLike(long id,long uid, TextView tvLike,int position,int oldCount) {
        action.httpLike(id,uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("点赞成功");
                    dataList.get(position).setLike(1);
                    dataList.get(position).setLikeCount(oldCount+1);
                    notifyItemChanged(position,tvLike);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("点赞失败");
            }
        });
    }

    /**
     * 发请求->取消点赞
     */
    private void httpCancleLike(long id,long uid, TextView tvLike,int position,int oldCount) {
        action.httpCancleLike(id,uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("已取消点赞");
                    dataList.get(position).setLike(0);
                    dataList.get(position).setLikeCount(oldCount-1);
                    notifyItemChanged(position,tvLike);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("取消点赞失败");
            }
        });
    }

    /**
     * 发请求->置顶
     */
    private void httpIsTop(long id,long uid, TextView tvLike,int position) {
//        action.httpIsTop(id,uid, new CallBack<ReturnBean>() {
//            @Override
//            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
//                super.onResponse(call, response);
//                if (response.body() == null) {
//                    return;
//                }
//                if (response.body().isOk()){
//                    ToastUtil.show("删除成功");
//                    dataList.get(position).setLike(1);
//                    notifyItemChanged(position,tvLike);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ReturnBean> call, Throwable t) {
//                super.onFailure(call, t);
//                ToastUtil.show("删除失败");
//            }
//        });
    }

    /**
     * 发请求->删除动态
     */
    private void httpDelete(long id,long uid, TextView tvLike,int position) {
//        action.httpDelete(id,uid, new CallBack<ReturnBean>() {
//            @Override
//            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
//                super.onResponse(call, response);
//                if (response.body() == null) {
//                    return;
//                }
//                if (response.body().isOk()){
//                    ToastUtil.show("删除成功");
//                    dataList.get(position).setLike(1);
//                    notifyItemChanged(position,tvLike);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ReturnBean> call, Throwable t) {
//                super.onFailure(call, t);
//                ToastUtil.show("删除失败");
//            }
//        });
    }
}
