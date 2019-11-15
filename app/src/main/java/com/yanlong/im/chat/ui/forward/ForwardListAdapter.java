package com.yanlong.im.chat.ui.forward;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yanlong.im.R;
import com.yanlong.im.utils.GlideOptionsUtil;
import net.cb.cb.library.bean.TestBean;
import java.util.List;


/**
 * Created by zgd on 2017/7/20.
 */
public class ForwardListAdapter extends BaseQuickAdapter<MoreSessionBean, BaseViewHolder> {
    private Context context;

    public ForwardListAdapter(@LayoutRes int layoutResId, @Nullable Context context, @Nullable List<MoreSessionBean> data) {
        super(layoutResId, data);
    }

    public ForwardListAdapter(@Nullable Context context, @Nullable List<MoreSessionBean> data) {
        this(R.layout.item_forward_list, context, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final MoreSessionBean item) {
//        helper.setText(R.id.name,"");
//        helper.setBackgroundRes(R.id.name,R.mipmap.ic_home_dashang_1);

        ImageView img_head=helper.getView(R.id.img_head);
        Glide.with(context).load(item.getAvatar())
                .apply(GlideOptionsUtil.headImageOptions()).into(img_head);
    }
}
