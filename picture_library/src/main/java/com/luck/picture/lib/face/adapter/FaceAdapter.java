package com.luck.picture.lib.face.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.face.FaceView;
import com.luck.picture.lib.face.bean.FaceBean;

import java.util.ArrayList;

/**
 * 表情列表适配器
 *
 * @author CodeApe
 * @version 1.0
 * @Description TODO
 * @date 2013-11-16
 * @Copyright: Copyright (c) 2013 Shenzhen Tentinet Technology Co., Ltd. Inc.
 * All rights reserved.
 */
public class FaceAdapter extends BaseAdapter {

    /**
     * 上下文环境
     */
    private Context context;
    /**
     * 表情属性列表
     */
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
            holder.image_thum = convertView.findViewById(R.id.item_face_image_emoji);
            holder.image_big = convertView.findViewById(R.id.item_face_image_big);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        FaceBean bean = list_FaceBeans.get(position);
        holder.image_thum.setVisibility(View.VISIBLE);
        holder.image_big.setVisibility(View.GONE);
        if (FaceView.map_FaceEmoji != null) {
            holder.image_thum.setImageResource(Integer.parseInt(FaceView.map_FaceEmoji.get(bean.getName()).toString()));
        } else {
            // 资源ID可能会变，偶尔会出现异常图标出现在最近使用列表
            holder.image_thum.setImageResource(bean.getResId());
        }

        return convertView;
    }

    /**
     * 内部容器类
     *
     * @author CodeApe
     * @version 1.0
     * @Description TODO
     * @date 2013-11-23
     * @Copyright: Copyright (c) 2013 Shenzhen Tentinet Technology Co., Ltd.
     * Inc. All rights reserved.
     */
    private class ItemHolder {
        private ImageView image_thum;
        private ImageView image_big;
    }

}
