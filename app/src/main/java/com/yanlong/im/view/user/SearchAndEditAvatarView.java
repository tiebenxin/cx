package com.yanlong.im.view.user;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.yanlong.im.R;
import com.yanlong.im.chat.bean.MemberUser;
import com.yanlong.im.chat.ui.view.ControllerLinearList;
import com.yanlong.im.pay.ui.select.IEditAvatarListener;

import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.ScreenUtil;
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
    private final String TAG = getClass().getSimpleName();

    private View viewRoot;
    private ImageView ivSearch;
    private ClearEditText etSearch;
    private EditAvatarAdapter mAdapter;
    private List<EditAvatarBean> userList = new ArrayList<>();
    private IEditAvatarListener listener;
    private LinearLayout llSearch;
    private HorizontalScrollView scrollView;
    private LinearLayout llList;
    private ControllerLinearList viewList;

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
        llSearch = viewRoot.findViewById(R.id.ll_search);
        ivSearch = viewRoot.findViewById(R.id.iv_search);
        etSearch = viewRoot.findViewById(R.id.et_search);

        scrollView = viewRoot.findViewById(R.id.sv_view);
        llList = viewRoot.findViewById(R.id.ll_list);
        viewList = new ControllerLinearList(llList);
        mAdapter = new EditAvatarAdapter(getContext());
        viewList.setAdapter(mAdapter);

        etSearch.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (ViewUtils.isFastDoubleClick()) {
                    return false;
                }
                if (etSearch.getText().length() > 0) {
                    return false;
                } else {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        LogUtil.getLog().i("EditView", "KEYCODE_DEL");
                        deleteUser();
                    }
                }
                return false;
            }
        });
    }

    public EditText getEtSearch() {
        return etSearch;
    }

    public void addUser(MemberUser user) {
        userList.add(new EditAvatarBean(user));
        showSearchIcon(false);
        mAdapter.bindData(userList);
        updateXY();

    }

    public void clear() {
        userList.clear();
        showSearchIcon(true);
        mAdapter.bindData(userList);
        updateSearchWidth();
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

    public void updateXY() {
        int screenWidth = ScreenUtil.getScreenWidth(getContext());
        int recyclerWidth = scrollView.getWidth();
        int maxWidth = screenWidth - 150;
        if (recyclerWidth >= maxWidth) {
            ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
            layoutParams.width = maxWidth;
            scrollView.setLayoutParams(layoutParams);
            scrollRecycler();
        } else {
            ViewGroup.LayoutParams layoutParams = llSearch.getLayoutParams();
            layoutParams.width = screenWidth - recyclerWidth;
            llSearch.setLayoutParams(layoutParams);
        }
    }

    public void updateSearchWidth() {
        int screenWidth = ScreenUtil.getScreenWidth(getContext());
        ViewGroup.LayoutParams layoutParams = llSearch.getLayoutParams();
        layoutParams.width = screenWidth;
        scrollView.setLayoutParams(layoutParams);
    }

    private void scrollRecycler() {
        if (userList.size() > 0) {
            //父布局不拦截子控件触摸事件
            scrollView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_RIGHT);
                }
            }, 100);

        }
    }
}
