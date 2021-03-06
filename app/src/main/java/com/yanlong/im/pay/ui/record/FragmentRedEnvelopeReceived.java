package com.yanlong.im.pay.ui.record;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hm.cxpay.bean.RedEnvelopeItemBean;
import com.hm.cxpay.net.FGObserver;
import com.hm.cxpay.net.PayHttpUtils;
import com.hm.cxpay.rx.RxSchedulers;
import com.hm.cxpay.rx.data.BaseResponse;
import com.hm.cxpay.bean.RedDetailsBean;
import com.yanlong.im.R;
import com.yanlong.im.user.bean.UserInfo;
import com.yanlong.im.user.dao.UserDao;

import net.cb.cb.library.base.AbstractRecyclerAdapter;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.view.MultiListView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Liszt
 * @date 2019/12/2
 * Description 接收到的红包
 */
public class FragmentRedEnvelopeReceived extends Fragment {

    private View rootView;
    private MultiListView mMtListView;
    int currentPage = 1;
    private AdapterRedEnvelopeReceived adapter;
    private long totalCount;
    private List<RedEnvelopeItemBean> mDataList;
    private UserDao userDao = new UserDao();

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
        initView();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getRedEnvelopeDetails();
    }

    private void initView() {
        mMtListView = rootView.findViewById(com.hm.cxpay.R.id.mtListView);
        adapter = new AdapterRedEnvelopeReceived(getActivity());
        adapter.setItemClickListener(new AbstractRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object bean) {
                if (bean instanceof RedEnvelopeItemBean) {
                    RedEnvelopeItemBean b = (RedEnvelopeItemBean) bean;
                    Intent intent = SingleRedPacketDetailsActivity.newIntent(getActivity(), b.getTradeId(), 1);
                    startActivity(intent);
                }
            }
        });
        mMtListView.init(adapter);

        mMtListView.setEvent(new MultiListView.Event() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                getRedEnvelopeDetails();
            }

            @Override
            public void onLoadMore() {
                if (totalCount > 0 && currentPage * 20 >= totalCount) {
                    return;
                }
                currentPage++;
                getRedEnvelopeDetails();
            }

            @Override
            public void onLoadFail() {

            }
        });
    }


    /**
     * 获取收到红包记录
     */
    private void getRedEnvelopeDetails() {
        long startTime = ((RedEnvelopeRecordActivity) getActivity()).getCurrentCalendar();
        PayHttpUtils.getInstance().getRedEnvelopeDetails(currentPage, startTime, 7)
                .compose(RxSchedulers.<BaseResponse<RedDetailsBean>>compose())
                .compose(RxSchedulers.<BaseResponse<RedDetailsBean>>handleResult())
                .subscribe(new FGObserver<BaseResponse<RedDetailsBean>>() {
                    @Override
                    public void onHandleSuccess(BaseResponse<RedDetailsBean> baseResponse) {
                        if (baseResponse.isSuccess()) {
                            RedDetailsBean details = baseResponse.getData();
                            if (details != null) {
                                totalCount = details.getTotal();
                                if (((RedEnvelopeRecordActivity) getActivity()).getCurrentTab() == 0) {
                                    ((RedEnvelopeRecordActivity) getActivity()).initDetails(details, true);
                                } else {
                                    ((RedEnvelopeRecordActivity) getActivity()).initRecDetailBean(details);
                                }
                                if (details.getItems() != null) {
                                    resetName(details.getItems());
                                }
                            }
                        } else {
                            ToastUtil.show(getContext(), baseResponse.getMessage());
                        }

                    }

                    @Override
                    public void onHandleError(BaseResponse baseResponse) {
                        ToastUtil.show(getActivity(), baseResponse.getMessage());
                    }
                });
    }

    public void updateDetails() {
        currentPage = 1;
        getRedEnvelopeDetails();
    }

    @SuppressLint("CheckResult")
    private void resetName(List<RedEnvelopeItemBean> list) {
        Observable.just(0)
                .map(new Function<Integer, List<RedEnvelopeItemBean>>() {
                    @Override
                    public List<RedEnvelopeItemBean> apply(Integer integer) throws Exception {
                        int size = list.size();
                        for (int i = 0; i < size; i++) {
                            RedEnvelopeItemBean envelopeItemBean = list.get(i);
                            if (envelopeItemBean.getFromUser() != null) {
                                UserInfo userInfo = userDao.findUserInfo(envelopeItemBean.getFromUser().getUid());
                                if (userInfo != null && !TextUtils.isEmpty(userInfo.getMkName())) {
                                    envelopeItemBean.getFromUser().setNickname(userInfo.getMkName());
                                }
                            }
                        }
                        return list;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(Observable.<List<RedEnvelopeItemBean>>empty())
                .subscribe(new Consumer<List<RedEnvelopeItemBean>>() {
                    @Override
                    public void accept(List<RedEnvelopeItemBean> results) throws Exception {
                        if (currentPage == 1) {
                            mDataList = results;
                        } else {
                            mDataList.addAll(results);
                        }
                        adapter.bindData(mDataList);
                        mMtListView.notifyDataSetChange();
                    }
                });
    }


}
