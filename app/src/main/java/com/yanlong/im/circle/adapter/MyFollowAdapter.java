package com.yanlong.im.circle.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.hm.cxpay.dailog.CommonSelectDialog;
import com.yanlong.im.R;
import com.yanlong.im.circle.bean.FriendUserBean;
import com.yanlong.im.circle.mycircle.FriendTrendsActivity;
import com.yanlong.im.circle.mycircle.TempAction;
import com.yanlong.im.user.ui.UserInfoActivity;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.TimeToString;
import net.cb.cb.library.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * @类名：我关注的人/关注我的人适配器 (含上拉加载)
 * @Date：2019/12/9
 * @by zjy
 * @备注：
 */

public class MyFollowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

    private int type;//0 我关注的人(关注我的人) 1 我看过谁 2 谁看过我 3 不看TA

    private LayoutInflater inflater;
    private Activity activity;
    private List<FriendUserBean> dataList;//列表数据
    private RequestOptions mRequestOptions;
    private CommonSelectDialog dialog;
    private CommonSelectDialog.Builder builder;
    private TempAction action;

    public MyFollowAdapter(Activity activity, List<FriendUserBean> dataList,int type) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.type = type;
        this.dataList = new ArrayList<>();
        if(dataList!=null && dataList.size()>0){
            this.dataList.addAll(dataList);
        }
        init();
    }

    //初始化相关设置
    private void init() {
        action = new TempAction();
        //图片相关设置
        mRequestOptions = RequestOptions.centerInsideTransform()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .placeholder(com.yanlong.im.R.drawable.ic_info_head)
                .error(com.yanlong.im.R.drawable.ic_info_head)
                .centerCrop();
        builder = new CommonSelectDialog.Builder(activity);
    }


    //刷新数据
    public void updateList(List<FriendUserBean> list) {
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
        if (viewHolder instanceof MyFollowAdapter.ContentHolder) {
            ContentHolder holder = (ContentHolder) viewHolder;
            if (dataList != null && dataList.size() > 0) {
                if (dataList.get(position) != null) {
                    FriendUserBean userInfo = dataList.get(position);
                    //昵称
                    if (!TextUtils.isEmpty(userInfo.getNickname())) {
                        holder.tvName.setText(userInfo.getNickname());
                    }else {
                        holder.tvName.setText("该用户未设置昵称");
                    }
                    if(type==0){
                        //最新一条说说
                        if (!TextUtils.isEmpty(userInfo.getContent())) {
                            holder.tvNote.setText(userInfo.getContent());
                        }else {
                            holder.tvNote.setText("暂无最新动态");
                        }
                        holder.tvNote.setVisibility(View.VISIBLE);
                    } else if (type == 3) {
                        holder.tvNote.setVisibility(View.GONE);
                    } else {
                        if (userInfo.getLastTime()!=0) {
                            holder.tvNote.setText("最近访问："+TimeToString.getTimeForCollect(userInfo.getLastTime()));
                        }else {
                            holder.tvNote.setText("最近没有访问");
                        }
                        holder.tvNote.setVisibility(View.VISIBLE);
                    }

                    //头像
                    if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                        Glide.with(activity)
                                .load(userInfo.getAvatar())
                                .apply(mRequestOptions)
                                .into(holder.ivHeader);
                    }else {
                        Glide.with(activity)
                                .load(R.drawable.ic_info_head)
                                .apply(mRequestOptions)
                                .into(holder.ivHeader);
                    }
                    if(type==1){
                        //显示删除访问记录
                        holder.tvFollow.setVisibility(View.GONE);
                        holder.tvDeleteNotSee.setVisibility(View.GONE);
                        holder.tvDeleteRecord.setVisibility(View.VISIBLE);
                        holder.tvDeleteRecord.setOnClickListener(v -> showDeleteDialog(userInfo.getUid(),position,1));
                    }else if (type == 3) {
                        holder.tvFollow.setVisibility(View.GONE);
                        holder.tvDeleteNotSee.setVisibility(View.VISIBLE);
                        holder.tvDeleteRecord.setVisibility(View.GONE);
                        holder.tvDeleteNotSee.setOnClickListener(v -> showDeleteDialog(userInfo.getUid(), position,2));
                    } else {
                        holder.tvFollow.setVisibility(View.VISIBLE);
                        holder.tvDeleteNotSee.setVisibility(View.GONE);
                        holder.tvDeleteRecord.setVisibility(View.GONE);
                        //关注状态   刚进来全部是已关注，1 已关注 2 未关注 3 相互关注
                        if(type==2){
                            if (userInfo.getFollowStat() == 3) {
                                holder.tvFollow.setText("相互关注");
                                holder.tvFollow.setBackgroundResource(com.yanlong.im.R.drawable.shape_5radius_solid_527ea2);
                            } else if (userInfo.getFollowStat() == 1) {
                                holder.tvFollow.setText("已关注");
                                holder.tvFollow.setBackgroundResource(com.yanlong.im.R.drawable.shape_5radius_solid_d8d8d8);
                            } else {
                                holder.tvFollow.setText("关注TA");
                                holder.tvFollow.setBackgroundResource(com.yanlong.im.R.drawable.shape_5radius_solid_32b053);
                            }
                        }else {
                            if (userInfo.getStat() == 3) {
                                holder.tvFollow.setText("相互关注");
                                holder.tvFollow.setBackgroundResource(com.yanlong.im.R.drawable.shape_5radius_solid_527ea2);
                            } else if (userInfo.getStat() == 1) {
                                holder.tvFollow.setText("已关注");
                                holder.tvFollow.setBackgroundResource(com.yanlong.im.R.drawable.shape_5radius_solid_d8d8d8);
                            } else {
                                holder.tvFollow.setText("关注TA");
                                holder.tvFollow.setBackgroundResource(com.yanlong.im.R.drawable.shape_5radius_solid_32b053);
                            }
                        }
                        //关注操作
                        holder.tvFollow.setOnClickListener(v -> {
                            if (holder.tvFollow.getText().equals("已关注")) {
                                httpCancelFollow(userInfo.getUid(), position, holder.tvFollow);
                            } else if (holder.tvFollow.getText().equals("关注TA")) {
                                httpToFollow(userInfo.getUid(), position, holder.tvFollow);
                            } else {
                                ToastUtil.show("已相互关注");
                            }
                        });
                    }
                    holder.layoutItem.setOnClickListener(v -> {
                        //没注销的用户才允许跳朋友圈
                        if (!TextUtils.isEmpty(userInfo.getNickname()) || !TextUtils.isEmpty(userInfo.getAvatar())) {
                            Intent intent = new Intent(activity, FriendTrendsActivity.class);
                            intent.putExtra("uid",userInfo.getUid());
                            activity.startActivity(intent);
                        }else {
                            ToastUtil.show("该用户已注销");
                        }
                    });
                    holder.ivHeader.setOnClickListener(v -> {
                        //没注销的用户才允许点头像看详细资料
                        if (!TextUtils.isEmpty(userInfo.getNickname()) || !TextUtils.isEmpty(userInfo.getAvatar())) {
                            activity.startActivity(new Intent(activity, UserInfoActivity.class)
                                    .putExtra(UserInfoActivity.ID, userInfo.getUid())
                                    .putExtra(UserInfoActivity.JION_TYPE_SHOW, 1)
                                    .putExtra(UserInfoActivity.SHOW_TRENDS,true));
                        }else {
                            ToastUtil.show("该用户已注销");
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
            View itemView = inflater.inflate(R.layout.item_follow_person, parent, false);
            return new MyFollowAdapter.ContentHolder(itemView);
        } else {
            View itemView = inflater.inflate(R.layout.main_footer_layout, parent, false);
            return new MyFollowAdapter.FootHolder(itemView);
        }
    }

    // 子项
    private class ContentHolder extends RecyclerView.ViewHolder {
        private ImageView ivHeader;
        private TextView tvFollow;
        private TextView tvName;
        private TextView tvNote;
        private TextView tvDeleteRecord;
        private TextView tvDeleteNotSee;
        private RelativeLayout layoutItem;

        public ContentHolder(View itemView) {
            super(itemView);
            ivHeader = itemView.findViewById(R.id.iv_header);
            tvFollow = itemView.findViewById(R.id.tv_follow);
            tvName = itemView.findViewById(R.id.tv_name);
            tvNote = itemView.findViewById(R.id.tv_note);
            tvDeleteRecord = itemView.findViewById(R.id.tv_delete_record);
            tvDeleteNotSee = itemView.findViewById(R.id.tv_delete_not_see);
            layoutItem = itemView.findViewById(R.id.layout_item);
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
//        notifyItemChanged(dataList.size(),R.id.footer_layout);//TODO 优化待验证->可以考虑只刷尾部
        notifyDataSetChanged();
    }

    /**
     * 发请求->关注
     */
    private void httpToFollow(long uid,int position, TextView tvFollow) {
        action.httpToFollow(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("关注成功");
                    dataList.get(position).setStat(1);
                    notifyItemChanged(position,tvFollow);
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("关注失败");
            }
        });
    }

    /**
     * 发请求->取消关注
     */
    private void httpCancelFollow(long uid, int position, TextView tvFollow) {
        action.httpCancelFollow(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("取消关注成功");
                    dataList.get(position).setStat(2);
                    notifyItemChanged(position,tvFollow);
                }else {
                    ToastUtil.show("取消关注失败");
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("取消关注失败");
            }
        });
    }

    /**
     * 发请求->删除访问记录
     */
    private void httpDeleteVisitRecord(long uid, int position) {
        action.httpDeleteVisitRecord(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("删除成功");
                    dataList.remove(position);//删除数据源,移除集合中当前下标的数据
                    notifyItemRemoved(position);//刷新被删除的地方
                    notifyItemRangeChanged(position,getItemCount()); //刷新被删除数据，以及其后面的数据
                }else {
                    ToastUtil.show("删除失败");
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("删除失败");
            }
        });
    }

    /**
     * 发请求->移除不看的人
     */
    private void httpDeleteNotSee(long uid, int position) {
        action.httpDeleteNotSee(uid, new CallBack<ReturnBean>() {
            @Override
            public void onResponse(Call<ReturnBean> call, Response<ReturnBean> response) {
                super.onResponse(call, response);
                if (response.body() == null) {
                    return;
                }
                if (response.body().isOk()){
                    ToastUtil.show("移除成功");
                    dataList.remove(position);//删除数据源,移除集合中当前下标的数据
                    notifyItemRemoved(position);//刷新被删除的地方
                    notifyItemRangeChanged(position,getItemCount()); //刷新被删除数据，以及其后面的数据
                }else {
                    ToastUtil.show("移除失败");
                }
            }

            @Override
            public void onFailure(Call<ReturnBean> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtil.show("移除失败");
            }
        });
    }


    /**
     * 提示弹框
     * @param uid
     * @param position
     * @param type 1是否删除访问记录 2是否取消不看TA
     */
    private void showDeleteDialog(long uid,int position,int type) {
        dialog = builder.setTitle(type==1 ? "是否确认删除?" : "是否确认移除?")
                .setShowLeftText(true)
                .setRightText("确认")
                .setLeftText("取消")
                .setRightOnClickListener(v -> {
                    if(type==1){
                        httpDeleteVisitRecord(uid,position);
                    }else {
                        httpDeleteNotSee(uid,position);
                    }
                    dialog.dismiss();
                })
                .setLeftOnClickListener(v ->
                        dialog.dismiss()
                )
                .build();
        dialog.show();
    }

}
