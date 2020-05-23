package com.yanlong.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanlong.im.R;
import com.yanlong.im.user.bean.DeviceBean;

import java.util.List;

/**
 * @author Liszt
 * @date 2020/5/22
 * Description
 */
public class AdapterDeviceList extends BaseAdapter {

    private List<DeviceBean> list;
    private final LayoutInflater inflater;
    private int model = 0;
    private IDeviceClick listener;


    public AdapterDeviceList(Context c) {
        inflater = LayoutInflater.from(c);

    }

    public void bindData(List<DeviceBean> l) {
        list = l;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        DeviceBean bean = list.get(position);
        DeviceViewHolder holder;
        if (convertView == null) {
            holder = new DeviceViewHolder(inflater.inflate(R.layout.item_device, viewGroup));
            convertView = holder.getView();
            convertView.setTag(holder);
        } else {
            holder = (DeviceViewHolder) convertView.getTag();
        }
        holder.bindData(bean);
        return convertView;
    }

    class DeviceViewHolder {
        private final View root;
        private final TextView tvName;
        private final ImageView ivIcon;

        public DeviceViewHolder(View v) {
            root = v;
            tvName = v.findViewById(R.id.tv_name);
            ivIcon = v.findViewById(R.id.iv_icon);

        }

        public View getView() {
            return root;
        }


        public void bindData(DeviceBean bean) {
            tvName.setText(bean.getName());
            if (model == 1) {
                ivIcon.setImageResource(R.mipmap.ic_btn_goto);

            } else {
                ivIcon.setImageResource(R.mipmap.ic_btn_goto);
            }

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onClick(bean);
                    }
                }
            });
        }

    }

    //设置模式，1编辑模式，0 默认非编辑模式
    public void setModel(int value) {
        model = value;
        notifyDataSetChanged();
    }

    public int getModel() {
        return model;
    }

    public void setListener(IDeviceClick l) {
        listener = l;
    }

    public interface IDeviceClick {
        void onClick(DeviceBean bean);
    }
}
