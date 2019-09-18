package com.yanlong.im.test;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityTestRecyclerBinding;

import net.cb.cb.library.view.AppActivity;

import java.util.ArrayList;
import java.util.List;

public class TestRecyclerActivity extends AppActivity {
    private ActivityTestRecyclerBinding ui;
    private LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_test_recycler);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ui.recyclerView.setLayoutManager(layoutManager);
        AdapterRefreshTest adapterRefreshTest = new AdapterRefreshTest(this);
        adapterRefreshTest.bindData(getStringList());
        ui.recyclerView.setAdapter(adapterRefreshTest);
//        ui.recyclerView.scrollToPosition(adapterRefreshTest.getItemCount() - 1);
        layoutManager.scrollToPosition(adapterRefreshTest.getItemCount() - 1);

    }

    public List<String> getStringList() {
        List<String> list = new ArrayList<>();
        list.add("推荐");
        list.add("视频");
        list.add("热门");
        list.add("社会");
        list.add("娱乐");
        list.add("科技");
        list.add("电影");
        list.add("图片");
        list.add("美图");
        list.add("国际");
        list.add("体育");
        list.add("美女");
        list.add("搞笑");
        list.add("故事");
        list.add("美文");
        list.add("教育");
        list.add("养生");
        list.add("奇葩");
        list.add("趣图");
        list.add("时尚");
        list.add("财经");
        list.add("数码");
        list.add("城市");
        list.add("汽车");
        list.add("军事");
        list.add("段子");
        list.add("健康");
        list.add("正能量");
        list.add("健身");
        list.add("房产");
        list.add("历史");
        list.add("育儿");
        list.add("手机");
        list.add("旅游");
        list.add("宠物");
        list.add("情感");
        list.add("家居");
        list.add("文化");
        list.add("游戏");
        list.add("股票");
        list.add("科学");
        list.add("动漫");
        list.add("摄影");
        list.add("语录");
        list.add("星座");
        list.add("炫酷");
        list.add("收藏");
        list.add("两性");
        list.add("女性");
        list.add("心理");

        return list;
    }

}
