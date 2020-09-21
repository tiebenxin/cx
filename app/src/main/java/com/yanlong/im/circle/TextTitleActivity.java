package com.yanlong.im.circle;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityTextTitleBinding;

import net.cb.cb.library.base.bind.BaseBindActivity;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-07
 * @updateAuthor
 * @updateDate
 * @description 文字题目
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
@Route(path = TextTitleActivity.path)
public class TextTitleActivity extends BaseBindActivity<ActivityTextTitleBinding> {
    public static final String path = "/circle/TextTitleActivity";

    @Override
    protected int setView() {
        return R.layout.activity_text_title;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected void initEvent() {
        setActionBarLeft(bindingView.headView);
    }

    @Override
    protected void loadData() {

    }
}