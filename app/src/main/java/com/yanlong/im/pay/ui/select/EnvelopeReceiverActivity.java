package com.yanlong.im.pay.ui.select;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.databinding.ActivityEnvelopeReceiverBinding;

import net.cb.cb.library.view.AppActivity;

/**
 * @author Liszt
 * @date 2020/8/20
 * Description 选择谁可以领取红包
 */
public class EnvelopeReceiverActivity extends AppActivity {

    private ActivityEnvelopeReceiverBinding ui;
    private String gid;
    private final MsgDao msgDao = new MsgDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_envelope_receiver);
        gid = getIntent().getStringExtra("gid");
        initData();


    }

    private void initData() {
        if (TextUtils.isEmpty(gid)) {
            return;
        }
        Group group = msgDao.getGroup4Id(gid);
    }
}
