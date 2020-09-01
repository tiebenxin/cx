package net.cb.cb.library.base;

import android.os.Bundle;

import net.cb.cb.library.bean.EventRunState;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.view.AppActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author Liszt
 * @date 2020/8/21
 * Description tcp连接父类，适用于main，chat界面
 */
public class BaseTcpActivity extends AppActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRunState(EventRunState event) {
        LogUtil.getLog().i("TAG", "连接LOG->>>>应用切换前后台:" + event.getRun() + "--time=" + System.currentTimeMillis());
        LogUtil.writeLog("EventRunState" + "--连接LOG--" + "应用切换前后台--" + event.getRun() + "--time=" + System.currentTimeMillis());
        switchAppStatus(event.getRun());
        tcpConnect(event.getRun());

    }

    public void switchAppStatus(boolean isRun) {

    }

    public void tcpConnect(boolean isRun) {

    }
}
