package com.yanlong.im.pay.ui.record;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hm.cxpay.R;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.redenvelope.RedDetailsBean;

import net.cb.cb.library.base.AbstractRecyclerAdapter;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.MultiListView;

import java.util.ArrayList;
import java.util.List;

//发出的红包
public class FragmentRedEnvelopeSend extends Fragment {
    private View rootView;
    private RecyclerView mMtListView;
    int currentPage = 1;

    private List<String> list = new ArrayList<>();
    private AdapterRedEnvelopeSend adapter;

    public FragmentRedEnvelopeSend() {

    }


    public static FragmentRedEnvelopeSend newInstance() {
        FragmentRedEnvelopeSend fragment = new FragmentRedEnvelopeSend();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgm_red_packet_record, null);
//        ViewGroup.LayoutParams layparm = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        rootView.setLayoutParams(layparm);
        initView();
//        initData();
        return rootView;
    }

    private void initView() {
        mMtListView = rootView.findViewById(R.id.mtListView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mMtListView.setLayoutManager(manager);
        adapter = new AdapterRedEnvelopeSend(getContext());
        adapter.setItemClickListener(new AbstractRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object bean) {

            }
        });
        mMtListView.setAdapter(adapter);
        getRedEnvelopeDetails();
    }

    /**
     * 获取收到红包记录
     */
    private void getRedEnvelopeDetails() {
        long startTime = ((RedEnvelopeRecordActivity) getActivity()).getCurrentCalendar();
        PayHttpUtils.getInstance().getRedEnvelopeDetails(currentPage, startTime, 2)
                .compose(RxSchedulers.<BaseResponse<RedDetailsBean>>compose())
                .compose(RxSchedulers.<BaseResponse<RedDetailsBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<RedDetailsBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<RedDetailsBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            RedDetailsBean details = baseResponse.getData();
                            if (((RedEnvelopeRecordActivity) getActivity()).getCurrentTab() == 0) {
                                ((RedEnvelopeRecordActivity) getActivity()).initDetails(details, true);
                            }
                            adapter.bindData(details.getItems());
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        super.onHandleError(baseResponse);
                        ToastUtil.show(getActivity(), baseResponse.getMessage());
                    }
                });
    }

    public void updateDetails(){
        getRedEnvelopeDetails();
    }


}
