package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.MsgTestBean;
import com.yanlong.im.chat.ui.view.ChatItemView;
import com.yanlong.im.pay.ui.SingleRedPacketActivity;
import com.yanlong.im.user.ui.UserInfoActivity;
import com.yanlong.im.utils.socket.MsgBean;
import com.yanlong.im.utils.socket.SocketData;
import com.yanlong.im.utils.socket.SocketEvent;
import com.yanlong.im.utils.socket.SocketUtil;

import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.SharedPreferencesUtil;
import net.cb.cb.library.utils.SoftKeyBoardListener;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private net.cb.cb.library.view.MultiListView mtListView;
    private ImageView btnVoice;
    private EditText edtChat;
    private ImageView btnEmj;
    private ImageView btnFunc;
    private GridLayout viewFunc;
    private GridLayout viewEmoji;
    private LinearLayout viewPic;
    private LinearLayout viewCamera;
    private LinearLayout viewRb;
    private LinearLayout viewRbZfb;
    private LinearLayout viewAction;
    private LinearLayout viewTransfer;
    private LinearLayout viewCard;
    private View viewChatBottom;
    private View imgEmojiDel;
    private Button btnSend;

    private Integer font_size;
    //消息监听事件
    private SocketEvent msgEvent = new SocketEvent() {
        @Override
        public void onHeartbeat() {

        }

        @Override
        public void onACK(MsgBean.AckMessage bean) {

        }

        @Override
        public void onMsg(final com.yanlong.im.utils.socket.MsgBean.UniversalMessage msgBean) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //从数据库读取消息
                    taskRefreshMessage();
                }
            });


        }

        @Override
        public void onSendMsgFailure(final MsgBean.UniversalMessage.Builder bean) {
            LogUtil.getLog().e("TAG", "发送失败" + bean.getRequestId());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.show(context, "发送失败" + bean.getRequestId());

                }
            });
        }
    };

    //自动寻找控件
    private void findViews() {
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        mtListView = (net.cb.cb.library.view.MultiListView) findViewById(R.id.mtListView);
        btnVoice = (ImageView) findViewById(R.id.btn_voice);
        edtChat = (EditText) findViewById(R.id.edt_chat);
        btnEmj = (ImageView) findViewById(R.id.btn_emj);
        btnFunc = (ImageView) findViewById(R.id.btn_func);
        viewFunc = (GridLayout) findViewById(R.id.view_func);
        viewEmoji = (GridLayout) findViewById(R.id.view_emoji);
        viewPic = (LinearLayout) findViewById(R.id.view_pic);
        viewCamera = (LinearLayout) findViewById(R.id.view_camera);
        viewRb = (LinearLayout) findViewById(R.id.view_rb);
        viewRbZfb = (LinearLayout) findViewById(R.id.view_rb_zfb);
        viewAction = (LinearLayout) findViewById(R.id.view_action);
        viewTransfer = (LinearLayout) findViewById(R.id.view_transfer);
        viewCard = (LinearLayout) findViewById(R.id.view_card);
        viewChatBottom = findViewById(R.id.view_chat_bottom);
        imgEmojiDel = findViewById(R.id.img_emoji_del);
        btnSend = findViewById(R.id.btn_send);

    }

    //发送并滑动到列表底部
    private void showSendObj(MsgAllBean msgAllbean) {

        msgListData.add(msgAllbean);
        mtListView.getListView().getAdapter().notifyDataSetChanged();

        mtListView.getListView().smoothScrollToPosition(msgListData.size());
    }

    //自动生成的控件事件
    private void initEvent() {
        actionbar.getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        actionbar.getBtnRight().setVisibility(View.VISIBLE);
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                if (true) {//群聊,单聊
                    go(GroupInfoActivity.class);
                } else {
                    go(ChatInfoActivity.class);
                }

            }
        });

        //设置字体大小
        font_size = new SharedPreferencesUtil(SharedPreferencesUtil.SPName.FONT_CHAT).get4Json(Integer.class);

        //注册消息监听
        SocketUtil.getSocketUtil().addEvent(msgEvent);
        //发送普通消息
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //发送普通消息
                MsgAllBean msgAllbean = SocketData.send4Chat(100102l, null, edtChat.getText().toString());
                showSendObj(msgAllbean);

            }
        });
        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    btnSend.setVisibility(View.VISIBLE);
                } else {
                    btnSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewFunc.getVisibility() == View.VISIBLE) {
                    hideBt();
                } else {
                    showBtType(0);
                }

            }
        });
        btnEmj.setTag(0);
        btnEmj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewEmoji.getVisibility() == View.VISIBLE) {

                    hideBt();
                    InputUtil.showKeyboard(edtChat);


                    btnEmj.setImageLevel(0);
                } else {


                    showBtType(1);
                    btnEmj.setImageLevel(1);
                }


            }
        });
        //emoji表情处理
        for (int i = 0; i < viewEmoji.getChildCount(); i++) {
            if (viewEmoji.getChildAt(i) instanceof TextView) {
                final TextView tv = (TextView) viewEmoji.getChildAt(i);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        edtChat.getText().insert(edtChat.getSelectionEnd(), tv.getText());
                    }
                });
            }

        }
        imgEmojiDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int keyCode = KeyEvent.KEYCODE_DEL;
                KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
                KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
                edtChat.onKeyDown(keyCode, keyEventDown);
                edtChat.onKeyUp(keyCode, keyEventUp);

            }
        });


        viewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(ChatActivity.this)
                        .openCamera(PictureMimeType.ofImage())
                        .compress(true)
                        .forResult(PictureConfig.CHOOSE_REQUEST);

            }
        });
        viewPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(ChatActivity.this)
                        .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                        .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(false)// 是否可预览图片 true or false
                        .isCamera(false)// 是否显示拍照按钮 ture or false
                        .compress(true)// 是否压缩 true or false
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }
        });
        //支付宝红包
        viewRbZfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ToastUtil.show(getContext(), "显示红包");

                Intent intent = new Intent(ChatActivity.this, SingleRedPacketActivity.class);
                startActivity(intent);
            }
        });
        //戳一下
        viewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //发送普通消息
                MsgAllBean msgAllbean = SocketData.send4action(100000l, null, edtChat.getText().toString());
                showSendObj(msgAllbean);
            }
        });
        //名片
        viewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgAllBean msgAllbean = SocketData.send4card(100000l, null, "http://baidu.com", "昵称", "其他资料");
                showSendObj(msgAllbean);
            }
        });


        mtListView.init(new RecyclerViewAdapter());
        mtListView.getLoadView().setStateNormal();

       /* mtListView.getListView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(true){
                    hideBt();
                }
                if(true){
                InputUtil.hideKeyboard(edtChat);
                }
            }
        });*/

        //处理键盘
        SoftKeyBoardListener kbLinst = new SoftKeyBoardListener(this);
        kbLinst.setOnSoftKeyBoardChangeListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int h) {
                hideBt();
                viewChatBottom.setPadding(0, 0, 0, h);


                btnEmj.setImageLevel(0);
            }

            @Override
            public void keyBoardHide(int h) {
                viewChatBottom.setPadding(0, 0, 0, 0);


            }
        });

taskRefreshMessage();
    }

    /***
     * 底部显示面板
     */
    private void showBtType(final int type) {

        btnEmj.setImageLevel(0);
        InputUtil.hideKeyboard(edtChat);

        viewFunc.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideBt();
                switch (type) {
                    case 0://功能面板
                        //第二种解决方案

                        viewFunc.setVisibility(View.VISIBLE);

                        break;
                    case 1://emoji面板

                        viewEmoji.setVisibility(View.VISIBLE);

                        break;
                }
            }
        }, 50);
    }

    /***
     * 隐藏底部所有面板
     */
    private void hideBt() {

        viewFunc.setVisibility(View.GONE);
        viewEmoji.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (viewFunc.getVisibility() == View.VISIBLE) {
            viewFunc.setVisibility(View.GONE);
            return;
        }
        if (viewEmoji.getVisibility() == View.VISIBLE) {
            viewEmoji.setVisibility(View.GONE);
            btnEmj.setImageLevel(0);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消监听
        SocketUtil.getSocketUtil().removeEvent(msgEvent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        findViews();
        initEvent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    String file = PictureSelector.obtainMultipleResult(data).get(0).getCompressPath();
                    //1.上传图片

                    //2.发送图片

                    MsgAllBean msgAllbean = SocketData.send4Image(100000l, null, "https://flutter-io.cn/asset/flutter-red-square-100.png");
                    showSendObj(msgAllbean);

                    break;
            }
        }
    }

    //显示大图
    private void showBigPic(String uri) {
        List<LocalMedia> selectList = new ArrayList<>();
        LocalMedia lc = new LocalMedia();
        lc.setPath(uri);
        selectList.add(lc);
        PictureSelector.create(ChatActivity.this).themeStyle(R.style.picture_default_style).openExternalPreview(0, selectList);

    }

    private List<MsgAllBean> msgListData = new ArrayList<>();


    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return msgListData == null ? 0 : msgListData.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, int position) {
            MsgAllBean msgbean = msgListData.get(position);


            switch (msgbean.getMsg_type()) {
                case 0:
                   // holder.viewChatItem.setShowType(0, msgbean.isMe(), null, "昵称", null);
                  //  holder.viewChatItem.setData0(msgbean.getChat().getMsg());
                    break;
                       case 1:
                            holder.viewChatItem.setShowType(1, msgbean.isMe(), "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称", "2019-8-6");
                            holder.viewChatItem.setData1(msgbean.getChat().getMsg());
                            break;
                        case 2:
                            holder.viewChatItem.setShowType(2, msgbean.isMe(), "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称", "2019-8-6");
                            holder.viewChatItem.setData2(msgbean.getStamp().getComment());
                            break;

                        case 3:
                            holder.viewChatItem.setShowType(3, msgbean.isMe(), "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称", "2019-8-6");
                            holder.viewChatItem.setData3(false, "test红包", msgbean.getRed_envelope().getComment(), null, 0, null);
                            break;

                        case 4:
                            holder.viewChatItem.setShowType(4, msgbean.isMe(), "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称", "2019-8-6");
                            holder.viewChatItem.setData4(msgbean.getImage().getUrl(), new ChatItemView.EventPic() {
                                @Override
                                public void onClick(String uri) {
                                     ToastUtil.show(getContext(), "大图:" + uri);
                                    showBigPic(uri);
                                }
                            });
                            break;
                        case 5:
                            holder.viewChatItem.setShowType(5, msgbean.isMe(), "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称", "2019-8-6");
                            holder.viewChatItem.setData5(msgbean.getBusiness_card().getNickname(),
                                    msgbean.getBusiness_card().getComment(),
                                    msgbean.getBusiness_card().getAvatar(), null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ToastUtil.show(getContext(), "添加好友需要详情页面");
                                    go(UserInfoActivity.class);
                                }
                            });
                            break;


            }


        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_chat_com, view, false));
            if (font_size != null)
                holder.viewChatItem.setFont(font_size);
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private com.yanlong.im.chat.ui.view.ChatItemView viewChatItem;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                viewChatItem = (com.yanlong.im.chat.ui.view.ChatItemView) convertView.findViewById(R.id.view_chat_item);
            }
        }
    }


    private MsgAction msgAction = new MsgAction();

    private void taskRefreshMessage() {
         msgListData = msgAction.getMsg4User(100102l);
         mtListView.getListView().getAdapter().notifyDataSetChanged();
    }

}
