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
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.ui.FriendMainFragment;
import com.yanlong.im.chat.ui.MsgMainFragment;
import com.yanlong.im.user.ui.MyFragment;

import net.cb.cb.library.bean.EventRefreshMainMsg;
import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.StrikeButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppActivity {
    private android.support.v4.view.ViewPager viewPage;
    private android.support.design.widget.TabLayout bottomTab;

    private Fragment[] fragments;
    private String[] tabs;
    private int[] iconRes;
    private int[] iconHRes;
    private StrikeButton msgsb;

    //自动寻找控件
    private void findViews() {
        viewPage = (android.support.v4.view.ViewPager) findViewById(R.id.viewPage);
        bottomTab = (android.support.design.widget.TabLayout) findViewById(R.id.bottom_tab);
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
            }
            if (i == 1) {
                sb.setNum(0);
            }

            if (i == 0) {//消息数量

                msgsb = sb;
            }


            txt.setText(tabs[i]);
            bottomTab.getTabAt(i).setCustomView(rootView);
        }
        //切换
        bottomTab.getTabAt(1).select();
        bottomTab.getTabAt(0).select();

        //test
        taskLogin();

        //test 启动聊天服务
        //startService(new Intent(getContext(), ChatServer.class));
        //写入通讯录
        taskAddUser();

    }


    private UserAction userAction = new UserAction();
    private boolean testMe = true;

    private void taskLogin() {
        //13222222222l
        //13000000000l
        userAction.login(testMe ? 13000000000l : 13222222222l, "asdfasfd", "12345613", new CallBack<ReturnBean<TokenBean>>() {
            // userAction.login(13222222222l, "asdfasfd","1234511613", new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                String token = response.body().getData().getAccessToken();

                // ToastUtil.show(getContext(),token);
                LogUtil.getLog().i("tag", ">>>>请求的token" + token);
                //启动聊天服务
                startService(new Intent(getContext(), ChatServer.class));
            }
        });
    }

    private void taskAddUser() {
        UserInfo me = new UserInfo();
        me.setHead("http://img.pcgames.com.cn/images/upload/upc/tx/gamedb/1212/20/c0/16744115_16744115_1355974000969.jpg");
        me.setMkName("光辉女郎");
        me.setName("拉克丝");
        me.setUid(100102l);

        me.setuType(testMe ? 1 : 2);
        userAction.updateUserinfo(me);


        UserInfo o1 = new UserInfo();
        o1.setHead("https://gss0.bdstatic.com/-4o3dSag_xI4khGkpoWK1HF6hhy/baike/w%3D268%3Bg%3D0/sign=319c40413101213fcf3349da6cdc51ec/8b82b9014a90f603cf01e8df3112b31bb151edf1.jpg");
        o1.setMkName("迅捷斥候");
        o1.setName("提莫");
        o1.setUid(100104l);
        o1.setuType(testMe ? 2 : 1);
        userAction.updateUserinfo(o1);

        UserInfo o2 = new UserInfo();
        o2.setHead("http://img0.pcgames.com.cn/pcgames/1406/04/3937651_5.jpg");
        o2.setMkName("无极大师");
        o2.setName("易");
        o2.setUid(100105l);
        o2.setuType(2);
        userAction.updateUserinfo(o2);


        UserInfo o3 = new UserInfo();
        o3.setHead("http://img.pcgames.com.cn/images/upload/upc/tx/gamedb/1502/05/c0/2775914_1423120222550.jpg");
        o3.setMkName("阿狸");
        o3.setName("狐狸");
        o3.setUid(100106l);
        o3.setuType(2);
        userAction.updateUserinfo(o3);

        UserInfo o4 = new UserInfo();
        o4.setHead("http://img0.pcgames.com.cn/pcgames/1302/22/2754281_3.png");
        o4.setMkName("德玛西亚之力");
        o4.setName("奎因");
        o4.setUid(100107l);
        o4.setuType(2);
        userAction.updateUserinfo(o4);

        UserInfo o5 = new UserInfo();
        o5.setHead("http://img.pcgames.com.cn/images/upload/upc/tx/gamedb/1212/20/c0/16744508_16744508_1355974570137.jpg");
        o5.setMkName("武器大师");
        o5.setName("贾克斯");
        o5.setUid(100108l);
        o5.setuType(2);
        userAction.updateUserinfo(o5);

        UserInfo o6 = new UserInfo();
        o6.setHead("http://img0.pcgames.com.cn/pcgames/1211/28/2661351_TwistedFate.png");
        o6.setMkName("卡牌大师");
        o6.setName("崔斯特");
        o6.setUid(100109l);
        o6.setuType(2);
        userAction.updateUserinfo(o6);

        UserInfo o7 = new UserInfo();
        o7.setHead("http://img0.pcgames.com.cn/pcgames/1107/06/2258212_Oriana_Square_0.png");
        o7.setMkName("发条");
        o7.setName("奥利安拉");
        o7.setUid(100110l);
        o7.setuType(2);
        userAction.updateUserinfo(o7);

        UserInfo o8 = new UserInfo();
        o8.setHead("http://wangyou.pcgames.com.cn/zhuanti/lol/hero/Lulu/Lulu.jpg");
        o8.setMkName("仙灵女巫");
        o8.setName("露露");
        o8.setUid(100111l);
        o8.setuType(2);
        userAction.updateUserinfo(o8);

        UserInfo o9 = new UserInfo();
        o9.setHead("http://img.pcgames.com.cn/images/upload/upc/tx/gamedb/1212/20/c0/16744014_16744014_1355973835486.jpg");
        o9.setMkName("金属大师");
        o9.setName("莫德凯撒");
        o9.setUid(100112l);
        o9.setuType(2);
        userAction.updateUserinfo(o9);

        UserInfo o10 = new UserInfo();
        o10.setHead("http://wangyou.pcgames.com.cn/zhuanti/lol/hero/Ziggs/Ziggs.png");
        o10.setMkName("爆破鬼才");
        o10.setName("吉格斯");
        o10.setUid(100113l);
        o10.setuType(2);
        userAction.updateUserinfo(o10);

        UserInfo o11 = new UserInfo();
        o11.setHead("http://img.pcgames.com.cn/images/upload/upc/tx/gamedb/1501/07/c0/1592065_1420622563160.png");
        o11.setMkName("放逐之刃");
        o11.setName("瑞文");
        o11.setUid(100114l);
        o11.setuType(2);
        userAction.updateUserinfo(o11);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        findViews();
        initEvent();
    }

    @Override
    protected void onDestroy() {
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
    }

    private MsgDao msgDao = new MsgDao();

    /***
     * 未读消息
     * @return
     */
    private void taskGetMsgNum() {
        if (msgsb == null)
            return;

        msgsb.setNum(msgDao.sessionReadGetAll());
    }


}
