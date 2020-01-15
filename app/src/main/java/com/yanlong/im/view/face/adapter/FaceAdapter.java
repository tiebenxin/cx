package com.yanlong.im.view.face.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yanlong.im.R;
import com.yanlong.im.utils.GlideOptionsUtil;
import com.yanlong.im.view.face.AddFaceActivity;
import com.yanlong.im.view.face.FaceView;
import com.yanlong.im.view.face.bean.FaceBean;

import java.util.ArrayList;

/**
 * 表情列表适配器
 * 
 * @Description TODO
 * @author CodeApe
 * @version 1.0
 * @date 2013-11-16
 * @Copyright: Copyright (c) 2013 Shenzhen Tentinet Technology Co., Ltd. Inc.
 *             All rights reserved.
 * 
 */
public class FaceAdapter extends BaseAdapter {

	/** 上下文环境 */
	private Context context;
	/** 表情属性列表 */
	private ArrayList<FaceBean> list_FaceBeans;

	public FaceAdapter(Context context, ArrayList<FaceBean> list_FaceBeans) {
		this.context = context;
		this.list_FaceBeans = list_FaceBeans;

	}

	@Override
	public int getCount() {
		return list_FaceBeans.size();
	}

	@Override
	public Object getItem(int position) {
		return list_FaceBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ItemHolder holder;
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_face, null);
			holder = new ItemHolder();
			holder.image_thum =  convertView.findViewById(R.id.item_face_image_emoji);
			holder.image_big =  convertView.findViewById(R.id.item_face_image_big);
			convertView.setTag(holder);
		} else {
			holder = (ItemHolder) convertView.getTag();
		}

		FaceBean bean = list_FaceBeans.get(position);
		if (bean.getGroup().equals(FaceView.face_custom)) {// 自定义表情
			holder.image_thum.setVisibility(View.GONE);
			holder.image_big.setVisibility(View.VISIBLE);
            if(position==0){
                holder.image_big.setImageResource(list_FaceBeans.get(position).getResId());
            }else{
				Glide.with(context).load(bean.getServerPath()).listener(new RequestListener() {
					@Override
					public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
						return false;
					}

					@Override
					public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
						return false;
					}
				}).apply(GlideOptionsUtil.defImageOptions()).into(holder.image_big);
            }
		} else if (bean.getGroup().equals(FaceView.face_animo)) {
			holder.image_thum.setVisibility(View.GONE);
			holder.image_big.setVisibility(View.VISIBLE);

			Glide.with(context).load(list_FaceBeans.get(position).getResId()).listener(new RequestListener() {
				@Override
				public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
					return false;
				}

				@Override
				public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
					return false;
				}
			}).apply(GlideOptionsUtil.defImageOptions()).into(holder.image_big);

		} else {
			holder.image_thum.setVisibility(View.VISIBLE);
			holder.image_big.setVisibility(View.GONE);
			holder.image_thum.setImageResource(list_FaceBeans.get(position).getResId());
		}

		return convertView;
	}

	/**
	 * 内部容器类
	 * 
	 * @Description TODO
	 * @author CodeApe
	 * @version 1.0
	 * @date 2013-11-23
	 * @Copyright: Copyright (c) 2013 Shenzhen Tentinet Technology Co., Ltd.
	 *             Inc. All rights reserved.
	 * 
	 */
	private class ItemHolder {
		private ImageView image_thum;
		private ImageView image_big;
	}

}
