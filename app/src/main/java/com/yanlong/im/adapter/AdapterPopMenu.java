package com.yanlong.im.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanlong.im.R;

import java.util.List;

import me.kareluo.ui.OptionMenu;

/**
 * @author Liszt
 * @date 2019/12/16
 * Description
 */
public class AdapterPopMenu extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<OptionMenu> mList;
    private final Context context;
    private final LayoutInflater inflater;
    private IMenuClickListener listener;

    public AdapterPopMenu(List<OptionMenu> l, Context con) {
        mList = l;
        context = con;
        inflater = LayoutInflater.from(con);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MenuViewHolder(inflater.inflate(R.layout.item_menu, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        MenuViewHolder holder = (MenuViewHolder) viewHolder;
        OptionMenu bean = mList.get(position);
        holder.bindData(bean, position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void setListener(IMenuClickListener l) {
        listener = l;
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        private final View root;
        private final TextView tvContent;
        private final ImageView ivPicture;
        private final View viewLine;

        public MenuViewHolder(View v) {
            super(v);
            root = v;
            tvContent = v.findViewById(R.id.tv_content);
            ivPicture = v.findViewById(R.id.iv_picture);
            viewLine = v.findViewById(R.id.view_line);

        }

        public View getView() {
            return root;
        }

        public void bindData(OptionMenu item, int position) {
            tvContent.setText(item.getTitle());
            if (item.getTitle().equals("复制")) {
                ivPicture.setImageResource(R.mipmap.ic_chat_bubble_copy);
            } else if (item.getTitle().equals("回复")) {
                ivPicture.setImageResource(R.mipmap.ic_chat_bubble_huifu);
            } else if (item.getTitle().equals("转发")) {
                ivPicture.setImageResource(R.mipmap.ic_chat_bubble_zhuanfa);
            } else if (item.getTitle().equals("收藏")) {
                ivPicture.setImageResource(R.mipmap.ic_chat_bubble_coll);
            } else if (item.getTitle().equals("删除")) {
                ivPicture.setImageResource(R.mipmap.ic_chat_bubble_delete);
            } else if (item.getTitle().equals("多选")) {
                ivPicture.setImageResource(R.mipmap.ic_chat_bubble_duoxuan);
            } else if (item.getTitle().equals("听筒播放")) {
                ivPicture.setImageResource(R.mipmap.ic_chat_bubble_copy);
            } else if (item.getTitle().equals("撤回")) {
                ivPicture.setImageResource(R.mipmap.ic_chat_bubble_back);
            }
            if (mList.size() > 4 && position < 4) {
                viewLine.setVisibility(View.VISIBLE);
            } else {
                viewLine.setVisibility(View.GONE);
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
