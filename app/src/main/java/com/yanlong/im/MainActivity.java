package com.yanlong.im;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanlong.im.chat.server.ChatServer;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.TokenBean;
import com.yanlong.im.user.ui.FriendMainFragment;
import com.yanlong.im.chat.ui.MsgMainFragment;
import com.yanlong.im.user.ui.MyFragment;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.StrikeButton;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppActivity {
    private android.support.v4.view.ViewPager viewPage;
    private android.support.design.widget.TabLayout bottomTab;

    private Fragment[] fragments;
    private String[] tabs;
    private int[] iconRes;
    private int[] iconHRes;

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
            }
            //设置值
            sb.setNum(1);


            txt.setText(tabs[i]);
            bottomTab.getTabAt(i).setCustomView(rootView);
        }
        //切换
        bottomTab.getTabAt(1).select();
        bottomTab.getTabAt(0).select();

        //test
        taskLogin();

        //test 启动聊天服务
      //  startService(new Intent(getContext(), ChatServer.class));


    }


    private UserAction userAction=new UserAction();
    private void taskLogin() {
        userAction.login(13000000000l, "asdfasfd","12345613", new CallBack<ReturnBean<TokenBean>>() {
            @Override
            public void onResponse(Call<ReturnBean<TokenBean>> call, Response<ReturnBean<TokenBean>> response) {
                String token=  response.body().getData().getAccessToken();

                ToastUtil.show(getContext(),token);
                LogUtil.getLog().i("tag",">>>>请求的token"+token);
                //启动聊天服务
                startService(new Intent(getContext(), ChatServer.class));
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initEvent();
    }


}
