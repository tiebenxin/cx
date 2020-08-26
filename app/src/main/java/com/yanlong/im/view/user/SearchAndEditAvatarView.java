package com.yanlong.im.view.user;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.pay.ui.select.IEditAvatarListener;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ClearEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Liszt
 * @date 2020/8/25
 * Description 选择联系人，编辑(增删)头像，及搜索联系人
 */
public class SearchAndEditAvatarView extends LinearLayout {

    private View viewRoot;
    private RecyclerView recyclerView;
    private ImageView ivSearch;
    private ClearEditText etSearch;
    private AdapterEditAvatar mAdapter;
    private List<EditAvatarBean> userList = new ArrayList<>();
    private IEditAvatarListener listener;

    public SearchAndEditAvatarView(Context context) {
        this(context, null);
    }

    public SearchAndEditAvatarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchAndEditAvatarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        viewRoot = LayoutInflater.from(getContext()).inflate(R.layout.view_edit_avatar, this, true);
        recyclerView = viewRoot.findViewById(R.id.recycler_view);
        ivSearch = viewRoot.findViewById(R.id.iv_search);
        etSearch = viewRoot.findViewById(R.id.et_search);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        mAdapter = new AdapterEditAvatar(getContext());
        recyclerView.setAdapter(mAdapter);
        etSearch.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (etSearch.getText().length() > 0) {
                    return false;
                } else {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (ViewUtils.isFastDoubleClick()) {
                            return false;
                        }
                        LogUtil.getLog().i("EditView", "KEYCODE_DEL");
                        deleteUser();
                    }
                }
                return false;
            }
        });
    }

    public void addUser(MemberUser user) {
        userList.add(new EditAvatarBean(user));
        showSearchIcon(false);
        mAdapter.bindData(userList);
    }

    public void clear() {
        userList.clear();
        showSearchIcon(true);
        mAdapter.bindData(userList);
    }

    //按删除键删除最后一位
    private void deleteUser() {
        if (userList != null || userList.size() > 0) {
            int len = userList.size();
            if (len > 0) {
                EditAvatarBean bean = userList.get(len - 1);
                if (bean.getDeleteCount() > 1) {
                    int count = bean.getDeleteCount() - 1;
                    bean.setDeleteCount(count);
                } else {
                    userList.remove(bean);
                    if (userList.size() == 0) {
                        showSearchIcon(true);
                    }
                    if (listener != null) {
                        listener.remove(bean.getUser());
                    }
                }
                mAdapter.bindData(userList);
            }
        }
    }

    public void removeUser(MemberUser user) {
        EditAvatarBean bean = new EditAvatarBean(user);
        userList.remove(bean);
        mAdapter.bindData(userList);
    }

    public void setListener(IEditAvatarListener l) {
        listener = l;
    }

    public void showSearchIcon(boolean b) {
        ivSearch.setVisibility(b ? VISIBLE : GONE);
    }


}
