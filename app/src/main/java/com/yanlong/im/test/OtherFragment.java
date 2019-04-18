package com.yanlong.im.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yanlong.im.R;
import com.yanlong.im.pay.ui.view.RedPacketDialog;
import com.yanlong.im.test.bean.Test2Bean;

import com.yanlong.im.utils.DaoUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.PySortView;

import java.util.List;
import java.util.UUID;


public class OtherFragment extends Fragment {
    private View rootView;
    private net.cb.cb.library.view.PySortView pySort;



    //自动寻找控件
    private void findViews(View rootView){
        pySort = (net.cb.cb.library.view.PySortView) rootView.findViewById(R.id.pySort);
    }

    public OtherFragment() {
        // Required empty public constructor
    }


    public static OtherFragment newInstance() {
        OtherFragment fragment = new OtherFragment();
        Bundle args = new Bundle();
    /*    args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
/*            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgm_test_other, null);
        ViewGroup.LayoutParams layparm = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(layparm);
         findViews(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       initEvent();

    }

    private void initEvent() {
        pySort.setEvent(new PySortView.Event() {
            @Override
            public void onChange(String type) {
                ToastUtil.show(getContext(),type);
            }
        });

        Test2Bean testBean=new Test2Bean();
        testBean.setName(UUID.randomUUID().toString());


        DaoUtil.get().getDaoSession().getTest2BeanDao().insert(testBean);
        ToastUtil.show(getContext(),""+testBean.getId());

        List<Test2Bean> list = DaoUtil.get().getDaoSession().getTest2BeanDao().queryBuilder().list();
        list.size();


        final RedPacketDialog redd=new RedPacketDialog();

        redd.show4open(getFragmentManager(), "https://ss0.baidu.com/73x1bjeh1BF3odCf/it/u=2534985070,2613606008&fm=85&s=4B3481425B151BED4070FFBB03008003", "wow", "哒哒哒", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show(getContext(),"走你");
                redd.show4opened(getFragmentManager(), "", "xx", "ed", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.show(getContext(),"more");
                    }
                });
            }
        });


    }


    @Override
    public void onDetach() {
        super.onDetach();

    }
  /*
   private MainActivity getActivityMe() {
        return (MainActivity) getActivity();
    }

    */




}
