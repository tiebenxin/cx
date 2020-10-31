package net.cb.cb.library.base;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Liszt on 2019/9/18.
 */

public abstract class AbstractViewHolder<T> extends RecyclerView.ViewHolder {


    public AbstractViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindData(T bean);

}