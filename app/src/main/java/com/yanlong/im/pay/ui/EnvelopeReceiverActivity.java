package com.yanlong.im.pay.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.yanlong.im.R;
import com.yanlong.im.databinding.ActivityEnvelopeReceiverBinding;

import net.cb.cb.library.view.AppActivity;

/**
 * @author Liszt
 * @date 2020/8/20
 * Description 选择谁可以领取红包
 */
public class EnvelopeReceiverActivity extends AppActivity {

    private ActivityEnvelopeReceiverBinding ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_envelope_receiver);
    }
}
