package com.yanlong.im.user.ui.image;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.nim_lib.ui.BaseBindActivity;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.databinding.ActivityPreviewFileBinding;
import com.yanlong.im.databinding.ItemPreviewFileBinding;

import java.util.List;

/**
 * @author Liszt
 * @date 2020/9/14
 * Description 浏览当前会话所有图片，视频，文件
 */
public class PreviewFileActivity extends BaseBindActivity<ActivityPreviewFileBinding> {

    private CommonRecyclerViewAdapter mAdapter;
    private MsgAction msgAction = new MsgAction();
    private String gid;
    private Long toUid;
    private long time;

    public static Intent newIntent(Context context, String gid, Long toUid, long time) {
        Intent intent = new Intent(context, PreviewFileActivity.class);
        intent.putExtra("gid", gid);
        intent.putExtra("uid", toUid);
        intent.putExtra("time", time);
        return intent;
    }

    @Override
    protected int setView() {
        return R.layout.activity_preview_file;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mAdapter = new CommonRecyclerViewAdapter<MsgAllBean, ItemPreviewFileBinding>(this, R.layout.item_preview_file) {

            @Override
            public void bind(ItemPreviewFileBinding binding, MsgAllBean data, int position, RecyclerView.ViewHolder viewHolder) {

            }
        };
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void loadData() {
        Intent intent = getIntent();
        gid = intent.getStringExtra("gid");
        toUid = intent.getLongExtra("uid", 0L);
        time = intent.getLongExtra("time", 0L);
        List<MsgAllBean> listdata = msgAction.getMsg4UserImg(gid, toUid);
    }
}
