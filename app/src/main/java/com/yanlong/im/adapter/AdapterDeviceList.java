package com.yanlong.im.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yanlong.im.R;

import java.util.List;

/**
 * @author Liszt
 * @date 2020/5/22
 * Description
 */
public class AdapterDeviceList extends BaseAdapter {

    private List<String> list;

    public void bindData(List<String> l) {
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
        return null;
    }

    class DeviceViewHolder{

        private final TextView tvName;

        public DeviceViewHolder(View v){
            tvName = v.findViewById(R.id.tv_name);
        }

    }
}
