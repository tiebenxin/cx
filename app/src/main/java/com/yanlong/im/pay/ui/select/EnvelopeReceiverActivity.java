package com.yanlong.im.pay.ui.select;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hm.cxpay.bean.FromUserBean;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.Group;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.dao.MsgDao;
import com.yanlong.im.databinding.ActivityEnvelopeReceiverBinding;

import net.cb.cb.library.utils.ThreadUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Liszt
 * @date 2020/8/20
 * Description 选择谁可以领取红包
 */
@Route(path = "/app/envelopeReceiver")
public class EnvelopeReceiverActivity extends AppActivity {

    private ActivityEnvelopeReceiverBinding ui;
    private String gid;
    private final MsgDao msgDao = new MsgDao();
    private AdapterSelectMember mAdapter;
    private ArrayList<FromUserBean> toUserList;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ui = DataBindingUtil.setContentView(this, R.layout.activity_envelope_receiver);
        gid = getIntent().getStringExtra("gid");
        toUserList = getIntent().getParcelableArrayListExtra("data");
        initData();
        initView();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toUserList != null) {
            toUserList = null;
        }
        if (mAdapter != null) {
            mAdapter = null;
        }
    }

    private void initView() {
        if (mAdapter != null) {
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            ui.listView.setLayoutManager(manager);
            ui.listView.setAdapter(mAdapter);
        }
        ui.headView.getActionbar().setTxtRight("确定");
        ui.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                int mode = mAdapter.getMode();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                intent.putExtra("mode", mode);
                if (mode == 1) {
                    bundle.putParcelableArrayList("data", mAdapter.getSelectList());
                }
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        ui.viewEditAvatar.setListener(new IEditAvatarListener() {

            @Override
            public void remove(MemberUser user) {
                if (mAdapter != null && user != null) {
                    ThreadUtil.getInstance().runMainThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.removeMember(user);
                        }
                    });
                }
            }

            @Override
            public void add(MemberUser user) {

            }

            @Override
            public void clear() {

            }
        });

        mAdapter.setListener(new IEditAvatarListener() {
            @Override
            public void remove(MemberUser user) {
                ThreadUtil.getInstance().runMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ui.viewEditAvatar.removeUser(user);
                    }
                });
            }

            @Override
            public void add(MemberUser user) {
                ThreadUtil.getInstance().runMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ui.viewEditAvatar.addUser(user);
                    }
                });
            }

            @Override
            public void clear() {
                ThreadUtil.getInstance().runMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ui.viewEditAvatar.clear();
                    }
                });
            }
        });
    }

    private void initData() {
        if (TextUtils.isEmpty(gid)) {
            return;
        }
        group = msgDao.getSortGroup(gid);
        if (group == null || group.getUsers() == null) {
            return;
        }
        mAdapter = new AdapterSelectMember(this, group);
        mAdapter.bindData(group.getUsers());
        if (toUserList != null && toUserList.size() > 0 && group.getUsers() != null) {
            convertUserList(toUserList);
        }

    }

    @SuppressLint("CheckResult")
    private void convertUserList(List<FromUserBean> toList) {
        Observable.just(0).map(new Function<Integer, List<MemberUser>>() {
            @Override
            public List<MemberUser> apply(Integer integer) throws Exception {
                List<MemberUser> selectMember = new ArrayList<>();
                if (group.getUsers() != null && group.getUsers().size() > 0) {
                    for (FromUserBean bean : toList) {
                        for (MemberUser user : group.getUsers()) {
                            if (bean.getUid() == user.getUid()) {
                                selectMember.add(user);
                            }
                        }
                    }
                }
                return selectMember;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<MemberUser>>empty())
                .subscribe(new Consumer<List<MemberUser>>() {
                    @Override
                    public void accept(List<MemberUser> list) throws Exception {
                        if (list != null) {
                            mAdapter.setSelectList(list);
                        }
                    }
                });

    }
}
