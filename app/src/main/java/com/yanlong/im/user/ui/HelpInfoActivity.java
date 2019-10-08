package com.yanlong.im.user.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.yanlong.im.R;

import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.HeadView;

public class HelpInfoActivity extends AppActivity {


    private HeadView mHeadView;
    private TextView mTvTitle;
    private TextView mTvContent;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_info);
        initView();
        initEvent();
    }

    private void initView() {
        mHeadView =  findViewById(R.id.headView);
        mTvTitle =  findViewById(R.id.tv_title);
        mTvContent =  findViewById(R.id.tv_content);
        content = getIntent().getStringExtra("content");
        mTvTitle.setText(content);
        if(content.equals("为什么我常聊聊没有声音")){
            mTvContent.setText("启动手机\"设置\"->点击\"通知\"->选择\"常聊聊\"->开启\"允许通知\"。");
        }else if(content.equals("如何下载常聊聊聊天软件?")){
            mTvContent.setText("在应用市场中搜索\"常聊聊\"即可下载常聊聊。");
        }else if(content.equals("如何添加好友?")){
            mTvContent.setText("方式一: 通过搜索好友手机号或常聊聊号添加进入常聊聊->通讯录->添加好友,在搜索框输入手机号或者常聊聊号->在搜索结果点击好友头像,即可添加好友。" +
                    "\n\n方式二:通过扫描好友常聊聊二维码添加\n1、进入常聊聊界面点击右上角【+】->点击【扫一扫】\n2、让好友进入常聊聊->\"我\"界面,点击【我的二维码】" +
                    "\n3、扫描好友常聊聊二维码即可添加");
        }else if(content.equals("好友添加")){
            mTvContent.setText("如果您发送好友请求后,对方收不到添加请求,可建议对方在\"通讯录\"->新的申请中查看是否有您的添加邀请;若没有,建议退出后重新登陆。");
        }else if(content.equals("收发消息")){
            mTvContent.setText("在会话页面点击底部输入框即可输入消息内容,点击发送按钮即可发送消息。");
        }else if(content.equals("账号设置")){
            mTvContent.setText("常聊聊有两种登录方式:\n1.使用短信验证码登录\n2.使用密码登录");
        }else if(content.equals("群聊")){
            mTvContent.setText("在常聊聊页面点击右上角【+】->【发起群聊】->选择好友后->点击【确定】即可。");
        }


    }

    private void initEvent(){
        mHeadView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });

    }

}
