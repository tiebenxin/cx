package com.yanlong.im.chat.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import com.yanlong.im.R;
import com.yanlong.im.chat.action.MsgAction;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.user.action.UserAction;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;
import com.yanlong.im.user.ui.FriendMainFragment;
import com.yanlong.im.utils.GroupHeadImageUtil;

import net.cb.cb.library.bean.ReturnBean;
import net.cb.cb.library.utils.BtnClickUtil;
import net.cb.cb.library.utils.CallBack;
import net.cb.cb.library.utils.CheckUtil;
import net.cb.cb.library.utils.ClickFilter;
import net.cb.cb.library.utils.DensityUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.UpFileAction;
import net.cb.cb.library.utils.UpFileUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.PySortView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Response;

/***
 * 创建群聊
 */
public class GroupCreateActivity extends AppActivity {
    private net.cb.cb.library.view.HeadView headView;
    private ActionbarView actionbar;
    private LinearLayout viewSearch;
    private android.support.v7.widget.RecyclerView topListView;
    private net.cb.cb.library.view.MultiListView mtListView;
    private PySortView viewType;

    //自动寻找控件
    private void findViews() {
        headView = (net.cb.cb.library.view.HeadView) findViewById(R.id.headView);
        actionbar = headView.getActionbar();
        viewSearch = (LinearLayout) findViewById(R.id.view_search);
        topListView = (android.support.v7.widget.RecyclerView) findViewById(R.id.topListView);
        mtListView = (net.cb.cb.library.view.MultiListView) findViewById(R.id.mtListView);
        viewType = findViewById(R.id.view_type);
    }


    //自动生成的控件事件
    private void initEvent() {
        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        actionbar.setTxtRight("确定");


        ClickFilter.onClick(actionbar.getViewRight(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionbar.getViewRight().setEnabled(false);
                taskCreate();
            }
        });

        mtListView.init(new RecyclerViewAdapter());
        mtListView.getLoadView().setStateNormal();
        //联动
        viewType.setListView(mtListView.getListView());

        //顶部处理
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        topListView.setLayoutManager(linearLayoutManager);
        topListView.setAdapter(new RecyclerViewTopAdapter());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        findViews();
        initEvent();
        initData();
    }

    private void initData() {
        taskListData();

    }

    //自动生成RecyclerViewAdapter
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RCViewHolder> {

        @Override
        public int getItemCount() {
            return listData == null ? 0 : listData.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewHolder hd, int position) {

            final UserInfo bean = listData.get(position);


            hd.txtType.setText(bean.getTag());
            hd.imgHead.setImageURI(Uri.parse("" + bean.getHead()));
           // hd.txtName.setText(bean.getName());
            hd.txtName.setText(bean.getName4Show());

            hd.viewType.setVisibility(View.VISIBLE);
            if (position > 0) {
                UserInfo lastbean = listData.get(position - 1);
                if (lastbean.getTag().equals(bean.getTag())) {
                    hd.viewType.setVisibility(View.GONE);
                }
            }


            hd.ckSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        listDataTop.add(bean);
                    } else {
                        listDataTop.remove(bean);
                    }
                    topListView.getAdapter().notifyDataSetChanged();
                }
            });


        }


        //自动寻找ViewHold
        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewHolder holder = new RCViewHolder(inflater.inflate(R.layout.item_group_create, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewHolder extends RecyclerView.ViewHolder {
            private LinearLayout viewType;
            private TextView txtType;
            private com.facebook.drawee.view.SimpleDraweeView imgHead;
            private TextView txtName;
            private CheckBox ckSelect;

            //自动寻找ViewHold
            public RCViewHolder(View convertView) {
                super(convertView);
                viewType = (LinearLayout) convertView.findViewById(R.id.view_type);
                txtType = (TextView) convertView.findViewById(R.id.txt_type);
                imgHead = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.img_head);
                txtName = (TextView) convertView.findViewById(R.id.txt_name);
                ckSelect = (CheckBox) convertView.findViewById(R.id.ck_select);
            }

        }
    }

    private List<UserInfo> listDataTop = new ArrayList<>();

    //自动生成RecyclerViewAdapter
    class RecyclerViewTopAdapter extends RecyclerView.Adapter<RecyclerViewTopAdapter.RCViewTopHolder> {

        @Override
        public int getItemCount() {
            return listDataTop == null ? 0 : listDataTop.size();
        }

        //自动生成控件事件
        @Override
        public void onBindViewHolder(RCViewTopHolder holder, int position) {

            //6.20 内存消耗过大
            holder.imgHead.setImageURI(Uri.parse(listDataTop.get(position).getHead()));

            //  showThumb( holder.imgHead,listDataTop.get(position).getHead(),10,10);
        }


        //自动寻找ViewHold
        @Override
        public RCViewTopHolder onCreateViewHolder(ViewGroup view, int i) {
            RCViewTopHolder holder = new RCViewTopHolder(inflater.inflate(R.layout.item_group_create_top, view, false));
            return holder;
        }


        //自动生成ViewHold
        public class RCViewTopHolder extends RecyclerView.ViewHolder {
            private com.facebook.drawee.view.SimpleDraweeView imgHead;

            //自动寻找ViewHold
            public RCViewTopHolder(View convertView) {
                super(convertView);
                imgHead = (com.facebook.drawee.view.SimpleDraweeView) convertView.findViewById(R.id.img_head);
            }

        }
    }
    //----------------
    public static void showThumb(SimpleDraweeView draweeView, String url, int resizeWidthDp, int resizeHeightDp) {
        if (url == null || "".equals(url))
            return;
        if (draweeView == null)
            return;
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(DensityUtil.dip2px(draweeView.getContext(), resizeWidthDp), DensityUtil.dip2px(draweeView.getContext(), resizeHeightDp)))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(draweeView.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>())
                .build();
        draweeView.setController(controller);
    }
    //---------------------


    private MsgAction msgACtion = new MsgAction();
    private UserDao userDao = new UserDao();


    private List<UserInfo> listData = new ArrayList<>();

    private void taskListData() {


        listData = userDao.friendGetAll();


        for (int i = 0; i < listData.size(); i++) {
            //UserInfo infoBean:
            viewType.putTag(listData.get(i).getTag(), i);
        }


    }

    private UpFileAction upFileAction = new UpFileAction();

    private void taskCreate() {
        if (listDataTop.size() < 2) {
            ToastUtil.show(getContext(), "人数必须大于3人");
            actionbar.getViewRight().setEnabled(true);
            return;
        }
        final ArrayList<UserInfo> templist = new ArrayList<>();
        templist.addAll(listDataTop);
        templist.add(0,UserAction.getMyInfo());
        String name = "";

        // "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3507975290,3418373437&fm=27&gp=0.jpg";
        int i = templist.size();
        i = i > 9 ? 9 : i;
        //头像地址
        String url[] = new String[i];
        for (int j = 0; j < i; j++) {
            UserInfo userInfo = templist.get(j);
            name += userInfo.getName() + "、";
            url[j] = userInfo.getHead();
        }
        File file = GroupHeadImageUtil.synthesis(url);


        name = name.length() > 0 ? name.substring(0, name.length() - 2) : name;
        name = name.length() > 14 ? name.substring(0, 14) : name;
        name += "的群";
        final String fname = name;

        //  icon="file://"+file.getAbsolutePath();

        upFileAction.upFile(getContext(), new UpFileUtil.OssUpCallback() {
            @Override
            public void success(String icon) {
                msgACtion.groupCreate(fname, icon, templist, new CallBack<ReturnBean<Group>>() {
                    @Override
                    public void onResponse(Call<ReturnBean<Group>> call, Response<ReturnBean<Group>> response) {
                        actionbar.getViewRight().setEnabled(true);
                        if (response.body() == null)
                            return;
                        if (response.body().isOk()) {
                            finish();
                            startActivity(new Intent(getContext(), ChatActivity.class)
                                    .putExtra(ChatActivity.AGM_TOGID, response.body().getData().getGid())
                            );
                        } else {
                            ToastUtil.show(getContext(), response.body().getMsg());

                        }

                    }

                    @Override
                    public void onFailure(Call<ReturnBean<Group>> call, Throwable t) {
                        actionbar.getViewRight().setEnabled(true);
                        super.onFailure(call, t);
                    }
                });
            }

            @Override
            public void fail() {

            }

            @Override
            public void inProgress(long progress, long zong) {

            }
        }, file.getAbsolutePath());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
     //   ((ViewGroup)mtListView.getListView().getParent()) .removeAllViews();
     //   ((ViewGroup)topListView.getParent()) .removeAllViews();

    }
}
