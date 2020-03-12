package com.yanlong.im.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yanlong.im.R;

import java.util.List;

import me.kareluo.ui.OptionMenu;

/**
 * @author Liszt
 * @date 2019/12/16
 * Description
 */
public class AdapterPopMenu extends BaseAdapter {

    private final List<OptionMenu> mList;
    private final Context context;
    private final LayoutInflater inflater;
    private IMenuClickListener listener;

    public AdapterPopMenu(List<OptionMenu> l, Context con) {
        mList = l;
        context = con;
        inflater = LayoutInflater.from(con);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OptionMenu bean = mList.get(position);
        MenuViewHolder holder;
        if (convertView == null) {
            holder = new MenuViewHolder(inflater.inflate(R.layout.item_menu, parent));
            convertView = holder.getView();
            convertView.setTag(holder);
        } else {
            holder = (MenuViewHolder) convertView.getTag();
        }
        holder.bindData(bean, position);
        return convertView;
    }

    public void setListener(IMenuClickListener l) {
        listener = l;
    }

    class MenuViewHolder {
        private final View root;
        private final View viewLine;
        private final TextView tvContent;

        public MenuViewHolder(View v) {
            root = v;
            viewLine = v.findViewById(R.id.view_line);
            tvContent = v.findViewById(R.id.tv_content);
        }

        public View getView() {
            return root;
        }

        public void bindData(OptionMenu item, int position) {
            tvContent.setText(item.getTitle());
            if (mList != null) {
                if (mList.size() == 1) {
                    viewLine.setVisibility(View.GONE);
                    tvContent.setBackgroundResource(R.drawable.shape_chat_bubble_all);
                } else {
                    if (position == 0) {
                        viewLine.setVisibility(View.VISIBLE);
                        tvContent.setBackgroundResource(R.drawable.shape_chat_bubble_left);
                    } else if (position == mList.size() - 1) {
                        viewLine.setVisibility(View.GONE);
                        tvContent.setBackgroundResource(R.drawable.shape_chat_bubble_right);
                    } else {
                        tvContent.setBackgroundResource(R.drawable.shape_chat_bubble_center);
                        viewLine.setVisibility(View.VISIBLE);
                    }
                }
            }
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(item);
                    }
                }
            });
        }
    }


    public interface IMenuClickListener {
        void onClick(OptionMenu menu);
    }
}
