package com.yanlong.im;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.chat.server.ChatServer;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.NewVersionBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.ui.FriendMainFragment;
import com.yanlong.im.chat.ui.MsgMainFragment;
import com.yanlong.im.user.ui.LoginActivity;
import com.yanlong.im.user.ui.MyFragment;
import com.yanlong.im.user.ui.PasswordLoginActivity;
import com.yanlong.im.utils.update.UpdateManage;

import net.cb.cb.library.AppConfig;
import net.cb.cb.library.bean.EventLoginOut;
import net.cb.cb.library.bean.EventLoginOut4Conflict;
import net.cb.cb.library.bean.EventRefreshMainMsg;
import net.cb.cb.library.bean.EventRunState;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.AppFrontBackHelper;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.AlertYesNo;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.StrikeButton;
import net.cb.cb.library.view.ViewPagerSlide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppActivity {
    private ViewPagerSlide viewPage;
    private android.support.design.widget.TabLayout bottomTab;

    private Fragment[] fragments;
    private String[] tabs;
    private int[] iconRes;
    private int[] iconHRes;
    private StrikeButton sbmsg;
    private StrikeButton sbfriend;
    private StrikeButton sbme;

    //自动寻找控件
    private void findViews() {
        viewPage = findViewById(R.id.viewPage);
        bottomTab = findViewById(R.id.bottom_tab);
    }




    public ViewPagerSlide getViewPage() {
        return viewPage;
    }

    //自动生成的控件事件
    private void initEvent() {
        fragments = new Fragment[]{MsgMainFragment.newInstance(), FriendMainFragment.newInstance(), MyFragment.newInstance()};
        tabs = new String[]{"消息", "通讯录", "我"};
        iconRes = new int[]{R.mipmap.ic_msg, R.mipmap.ic_frend, R.mipmap.ic_me};
        iconHRes = new int[]{R.mipmap.ic_msg_h, R.mipmap.ic_frend_h, R.mipmap.ic_me_h};

        viewPage.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments[i];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
        bottomTab.setupWithViewPager(viewPage);
        bottomTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPage.setCurrentItem(tab.getPosition());
                for (int i = 0; i < bottomTab.getTabCount(); i++) {
                    View rootView = bottomTab.getTabAt(i).getCustomView();
                    LinearLayout viewItem = (LinearLayout) rootView.findViewById(R.id.view_item);
                    StrikeButton sb = (net.cb.cb.library.view.StrikeButton) rootView.findViewById(R.id.sb);
                    TextView txt = (TextView) rootView.findViewById(R.id.txt);
                    if (i == tab.getPosition()) { // 选中状态
                        sb.setButtonBackground(iconHRes[i]);
                        txt.setTextColor(getResources().getColor(R.color.green_500));
                    } else {// 未选中状态
                        sb.setButtonBackground(iconRes[i]);
                        txt.setTextColor(getResources().getColor(R.color.gray_400));
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // 提供自定义的布局添加Tab
        for (int i = 0; i < fragments.length; i++) {
            View rootView = getLayoutInflater().inflate(R.layout.tab_item, null);
            TextView txt = (TextView) rootView.findViewById(R.id.txt);
            StrikeButton sb = (net.cb.cb.library.view.StrikeButton) rootView.findViewById(R.id.sb);
            if (i == 2) {
                sb.setSktype(1);
                //设置值
                sb.setNum(0);
                sbme = sb;
            }
            if (i == 1) {
                sb.setSktype(1);
                sb.setNum(0);
                sbfriend = sb;
            }

            if (i == 0) {//消息数量

                sbmsg = sb;
            }


            txt.setText(tabs[i]);
            bottomTab.getTabAt(i).setCustomView(rootView);
        }
        //切换
        bottomTab.getTabAt(1).select();
        bottomTab.getTabAt(0).select();


        // 启动聊天服务
        startService(new Intent(getContext(), ChatServer.class));

        //监听应用
  /*      AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(getApplication(), new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                //应用切到前台处理
                startService(new Intent(getContext(), ChatServer.class));
            }

            @Override
            public void onBack() {
                //应用切到后台处理
                stopService(new Intent(getContext(), ChatServer.class));

            }
        });*/



    }


    private UserAction userAction = new UserAction();
    private boolean testMe = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        findViews();
        initEvent();
        uploadApp();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(getContext(), ChatServer.class));

        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskGetMsgNum();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRefresh(EventRefreshMainMsg event) {
        taskGetMsgNum();
        taskGetFriendNum();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventLoginOut(EventLoginOut event) {
        userAction.cleanInfo();

        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventRunState(EventRunState event) {
        LogUtil.getLog().i("TAG",">>>>EventRunState:"+event.getRun());
      if(event.getRun()){

          startService(new Intent(getContext(), ChatServer.class));
      }else{
          stopService(new Intent(getContext(), ChatServer.class));
      }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventLoginOutconflict(EventLoginOut4Conflict event) {
        UserInfo userInfo =  UserAction.getMyInfo();
        new SharedPreferencesUtil(SharedPreferencesUtil.SPName.IMAGE_HEAD).save2Json(userInfo.getHead()+"");
        new SharedPreferencesUtil(SharedPreferencesUtil.SPName.PHONE).save2Json(userInfo.getPhone()+"");
        userAction.cleanInfo();
        startActivity(new Intent(getContext(), MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        );

        AlertYesNo alertYesNo = new AlertYesNo();
        String phone= new SharedPreferencesUtil(SharedPreferencesUtil.SPName.PHONE).get4Json(String.class);
        alertYesNo.init(this, "提示", "您的账号"+phone+"已经在另一台设备上登录。如果不是您本人操作,请尽快修改密码", "确定", null, new AlertYesNo.Event() {
            @Override
            public void onON() {
                startActivity(new Intent(getContext(), LoginActivity.class));
                finish();
            }

            @Override
            public void onYes() {
                startActivity(new Intent(getContext(), LoginActivity.class));
                finish();
            }
        });
        alertYesNo.show();

    }


    private void uploadApp(){
        taskNewVersion();
    }


    private MsgDao msgDao = new MsgDao();

    /***
     * 未读消息
     * @return
     */
    private void taskGetMsgNum() {
        if (sbmsg == null)
            return;

        sbmsg.setNum(msgDao.sessionReadGetAll());
    }

    /***
     * 好友或者群申请数量
     */
    private void taskGetFriendNum() {
        //  ToastUtil.show(getContext(),"更新好友的提示数量");
        int sum = 0;
        sum += msgDao.remidGet("friend_apply");
        // sum+=msgDao.remidGet("friend_apply");
        //  sum+=msgDao.remidGet("friend_apply");
        sbfriend.setNum(sum);

    }

    private void taskNewVersion(){
        userAction.getNewVersion(new CallBack<ReturnBean<NewVersionBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<NewVersionBean>> call, Response<ReturnBean<NewVersionBean>> response) {
                if(response.body() == null && response.body().getData() == null){
                    return;
                }
                if(response.body().isOk()){
                    NewVersionBean bean =  response.body().getData();
                    UpdateManage updateManage = new UpdateManage(context,MainActivity.this);
                    if(response.body().getData().getForceUpdate() == 0){
                        updateManage.uploadApp(bean.getVersion(),bean.getContent(),bean.getUrl(),false);
                    }else{
                        updateManage.uploadApp(bean.getVersion(),bean.getContent(),bean.getUrl(),true);
                    }
                }
            }
        });
    }


}
