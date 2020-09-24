package com.yanlong.im.circle.mycircle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yanlong.im.R;
import com.yanlong.im.circle.adapter.MyFollowAdapter;
import com.yanlong.im.circle.bean.FriendUserBean;

import java.util.List;

/**
 * @类名：谁看过我/我看过谁
 * @Date：2020/9/24
 * @by zjy
 * @备注：
 */
public class MyMeetingFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private MyFollowAdapter adapter;
    private List<FriendUserBean> mList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_meeting, container, false);
        init(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void init(View view){
        recyclerView = view.findViewById(R.id.recycler_view);
    }


}
