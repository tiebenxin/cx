package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
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
import com.yanlong.im.chat.bean.MsgBean;
import com.yanlong.im.chat.ui.view.ChatItemView;

import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.SoftKeyBoardListener;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.ShowBigImgActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
    private void sendObj(MsgBean bean){
        msgListData.add(bean);
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
        //发送普通消息
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //test
                MsgBean bean = new MsgBean();
                bean.setType(1);
                bean.setMe(true);
                bean.setContext(edtChat.getText().toString());

                edtChat.getText().clear();

                sendObj(bean);
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
                        MsgBean bean=new MsgBean();
                        bean.setType(3);
                        bean.setMe(true);
                        bean.setContext("zhifubao");


                        sendObj(bean);
                    }
                });
        //戳一下
        viewAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        MsgBean bean=new MsgBean();
                        bean.setType(2);
                        bean.setMe(true);
                        bean.setContext("nihao");


                        sendObj(bean);
                    }
                });
        //名片
        viewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MsgBean bean=new MsgBean();
                bean.setType(5);
                bean.setMe(true);
                bean.setContext("名片");


                sendObj(bean);
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
        if(viewFunc.getVisibility()==View.VISIBLE){
            viewFunc.setVisibility(View.GONE);
            return;
        }
        if(viewEmoji.getVisibility()==View.VISIBLE){
            viewEmoji.setVisibility(View.GONE);
            btnEmj.setImageLevel(0);
            return;
        }
        super.onBackPressed();
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
                    // 例如 LocalMedia 里面返回两种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                  //  ToastUtil.show(context, file);

                    //发送图片
                    MsgBean bean=new MsgBean();
                    bean.setType(4);
                    bean.setMe(true);
                    bean.setContext(Uri.fromFile(new File(file)).toString());

                    sendObj(bean);

                    break;
            }
        }
    }

    //显示大图
    private void showBigPic(String  uri){
        List<LocalMedia> selectList=new ArrayList<>();
        LocalMedia lc=new LocalMedia();
        lc.setPath(uri);
        selectList.add(lc);
        PictureSelector.create(ChatActivity.this).themeStyle(R.style.picture_default_style).openExternalPreview(0, selectList);

    }
    private List<MsgBean> msgListData = new ArrayList<>();

    //test
    {
        for (int i = 0; i < 30; i++) {
            MsgBean bean = new MsgBean();
            bean.setContext("魔我劳动法是动大家啊黑哦发卷哦哎多1111111大家哦家的那节基督教啊你喝酒消息");
            bean.setMe(false);
            bean.setType(1);
            msgListData.add(bean);
        }
    }

    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return msgListData == null ? 0 : msgListData.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder holder, int position) {
            MsgBean msgbean = msgListData.get(position);

            switch (position) {
                case 0:
                    holder.viewChatItem.setShowType(0, msgbean.isMe(), null, "昵称",null);
                    holder.viewChatItem.setData0("这个是公告wenzhidhengdnndnfndadnnafndakerldjafldjlkajfeljraskjklejkarle消息");
                    break;
                case 1:
                    holder.viewChatItem.setShowType(1, msgbean.isMe(), "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称","2019-8-6");
                    holder.viewChatItem.setData1("对方发来一条消息wenzhidhengdnndnfndadnnafndakerldjafldjlkajfeljraskjklejkarle消息");
                    break;
                case 2:
                    holder.viewChatItem.setShowType(2, msgbean.isMe(), "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称",null);
                    holder.viewChatItem.setData2("干嘛这是一套很长很长很长的很长很长很长的很长很长很长的消息消息");
                    break;

                case 3:
                    holder.viewChatItem.setShowType(3, msgbean.isMe(), "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称",null);
                    holder.viewChatItem.setData3(true, "红包哦wenzhidhengdnndnfndadnnafndakerldjafldjlkajfeljraskjklejkarle消息", "这哦没打wenzhidhengdnndnfndadnnafndakerldjafldjlkajfeljraskjklejkarle消息", "", 0, new ChatItemView.EventRP() {
                        @Override
                        public void onClick(boolean isInvalid) {
                            ToastUtil.show(getContext(), "拆红包:" + isInvalid);
                        }
                    });
                    break;
                case 4:
                    holder.viewChatItem.setShowType(4, false, null, "昵称",null);
                    holder.viewChatItem.setData4("https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", new ChatItemView.EventPic() {
                        @Override
                        public void onClick(String uri) {
                          //  ToastUtil.show(getContext(), "大图:" + uri);
                            showBigPic(uri);
                        }
                    });
                    break;
                case 5:
                    holder.viewChatItem.setShowType(1, true, "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称","2019-8-6");
                    holder.viewChatItem.setData1("对方发来一条消息wenzhidhengdnndnfndadnnafndakerldjafldjlkajfeljraskjklejkarle消息");
                    break;
                case 6:
                    holder.viewChatItem.setShowType(2, true, "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称",null);
                    holder.viewChatItem.setData2("干嘛这是一套很长很长很长的很长很长很长的很长很长很长的消息消息");
                    break;

                case 7:
                    holder.viewChatItem.setShowType(3, true, "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称",null);
                    holder.viewChatItem.setData3(true, "红包哦wenzhidhengdnndnfndadnnafndakerldjafldjlkajfeljraskjklejkarle消息", "这哦没打wenzhidhengdnndnfndadnnafndakerldjafldjlkajfeljraskjklejkarle消息", "", 0, null);
                    break;
                case 8:
                    holder.viewChatItem.setShowType(4, true, null, "昵称",null);
                    holder.viewChatItem.setData4("https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", null);
                    break;
                case 9:
                    holder.viewChatItem.setShowType(5, false, "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称",null);
                    holder.viewChatItem.setData5("推荐人","信息","https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", null,null);
                    break;
                case 10:
                    holder.viewChatItem.setShowType(5, true, "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称",null);
                    holder.viewChatItem.setData5("推荐人","信息","https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "xxxx",null);
                    break;

                default:

                    switch (msgbean.getType()) {
                        case 0:
                            holder.viewChatItem.setShowType(0, msgbean.isMe(), null, "昵称",null);
                            holder.viewChatItem.setData0(msgbean.getContext());
                            break;
                        case 1:
                            holder.viewChatItem.setShowType(1, msgbean.isMe(), "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称","2019-8-6");
                            holder.viewChatItem.setData1(msgbean.getContext());
                            break;
                        case 2:
                            holder.viewChatItem.setShowType(2, msgbean.isMe(), "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称","2019-8-6");
                            holder.viewChatItem.setData2(msgbean.getContext());
                            break;

                        case 3:
                            holder.viewChatItem.setShowType(3, msgbean.isMe(), "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称","2019-8-6");
                            holder.viewChatItem.setData3(false,"test红包",msgbean.getContext(),null,0,null);
                            break;

                        case 4:
                            holder.viewChatItem.setShowType(4, msgbean.isMe(), "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称","2019-8-6");
                            holder.viewChatItem.setData4(msgbean.getContext(), new ChatItemView.EventPic() {
                                @Override
                                public void onClick(String uri) {
                                   // ToastUtil.show(getContext(), "大图:" + uri);
                                    showBigPic(uri);
                                }
                            });
                        case 5:
                            holder.viewChatItem.setShowType(5, msgbean.isMe(), "https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3366592641,3460284008&fm=58&bpow=1280&bpoh=853", "昵称","2019-8-6");
                            holder.viewChatItem.setData5(msgbean.getContext(), "xxx", "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3257916220,1170341024&fm=27&gp=0.jpg", null, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ToastUtil.show(getContext(),"添加好友需要详情页面");
                                }
                            });
                            break;

                    }


                    break;

            }


        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_chat_com, view, false));
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

}
