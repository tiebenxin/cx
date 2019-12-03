package com.yanlong.im.pay.ui.record;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.ui.redenvelope.RedDetailsBean;
import com.yanlong.im.R;

import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.MultiListView;

/**
 * @anthor Liszt
 * @data 2019/12/2
 * Description 接收到的红包
 */
public class FragmentRedEnvelopeReceived extends Fragment {

    private View rootView;
    private MultiListView recyclerView;
    int currentPage = 0;
    private AdapterRedEnvelopeReceived adapter;

    public static FragmentRedEnvelopeReceived newInstance() {
        FragmentRedEnvelopeReceived fragment = new FragmentRedEnvelopeReceived();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_content, null);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new AdapterRedEnvelopeReceived(getActivity());
        recyclerView.init(adapter);
        getRedEnvelopeDetails();
    }


    /**
     * 获取收到红包记录
     */
    private void getRedEnvelopeDetails() {
        long startTime = ((RedEnvelopeDetailsActivity) getActivity()).getCurrentCalendar();
        PayHttpUtils.getInstance().getRedEnvelopeDetails(currentPage, startTime, 7)
                .compose(RxSchedulers.<BaseResponse<RedDetailsBean>>compose())
                .compose(RxSchedulers.<BaseResponse<RedDetailsBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<RedDetailsBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<RedDetailsBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            RedDetailsBean details = baseResponse.getData();
                            if (((RedEnvelopeDetailsActivity) getActivity()).getCurrentTab() == 0) {
                                ((RedEnvelopeDetailsActivity) getActivity()).initDetails(details, true);
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


}
