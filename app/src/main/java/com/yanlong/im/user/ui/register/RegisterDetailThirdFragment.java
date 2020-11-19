package com.yanlong.im.user.ui.register;

import android.text.TextUtils;
import android.view.View;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.RegisterDetailBean;
import com.yanlong.im.databinding.FragmentRegisterThirdBinding;

import net.cb.cb.library.bean.CityBean;
import net.cb.cb.library.utils.GsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Liszt
 * @date 2020/11/16
 * Description 位置
 */
public class RegisterDetailThirdFragment extends BaseRegisterFragment<FragmentRegisterThirdBinding> {
    List<String> provinceList = new ArrayList<>();
    List<String> cityList = new ArrayList<>();
    Map<String, List<String>> locationMap = new HashMap<>();
    String province = "";
    private ArrayWheelAdapter provinceAdapter;
    private ArrayWheelAdapter cityAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_register_third;
    }

    @Override
    public void init() {
        mViewBinding.ivLeft.setVisibility(View.VISIBLE);
        mViewBinding.ivRight.setVisibility(View.VISIBLE);
        if (infoStat == 2) {
            mViewBinding.ivBack.setVisibility(View.VISIBLE);
        } else {
            mViewBinding.ivBack.setVisibility(View.GONE);
        }
        initLocationData();
        mViewBinding.wheelProvince.setCyclic(false);
        mViewBinding.wheelCity.setCyclic(false);
        RegisterDetailBean detailBean = ((RegisterDetailActivity) getActivity()).getDetailBean();
        if (!TextUtils.isEmpty(detailBean.getLocation())) {
            mViewBinding.wheelProvince.setAdapter(new ArrayWheelAdapter(provinceList));
            String location = detailBean.getLocation();
            String[] strings = location.split(",");
            if (strings.length != 2) {
                return;
            }
            String province = strings[0];
            String city = strings[1];
            int provinceIndex = provinceList.indexOf(province);
            if (provinceIndex < 0) {
                return;
            }
            mViewBinding.wheelProvince.setCurrentItem(provinceIndex);
            cityList = locationMap.get(province);
            if (cityList != null) {
                mViewBinding.wheelCity.setAdapter(new ArrayWheelAdapter(cityList));
                int cityIndex = cityList.indexOf(city);
                if (cityIndex < 0) {
                    return;
                }
                mViewBinding.wheelCity.setCurrentItem(cityIndex);
            }
        } else {
            //省份数据
            mViewBinding.wheelProvince.setAdapter(new ArrayWheelAdapter(provinceList));
            mViewBinding.wheelProvince.setCurrentItem(0);
            province = provinceList.get(0);
            //地级市数据
            cityList = locationMap.get(provinceList.get(0));
            mViewBinding.wheelCity.setAdapter(new ArrayWheelAdapter(cityList));
            mViewBinding.wheelCity.setCurrentItem(0);
            updateCityData(province, cityList.get(0), false);
        }
    }

    private void initLocationData() {
        if (provinceList.size() <= 0 || locationMap.size() == 0) {
            String cityJson = GsonUtils.getCityJson(getContext());
            if (!TextUtils.isEmpty(cityJson)) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<CityBean>>() {
                }.getType();
                List<CityBean> cityBeans = gson.fromJson(cityJson, type);
                for (int i = 0; i < cityBeans.size(); i++) {
                    CityBean cityBean = cityBeans.get(i);
                    provinceList.add(cityBean.getProvince());
                    locationMap.put(cityBean.getProvince(), cityBean.getCityList());
                }
            }
        }
    }

    @Override
    public void initListener() {
        mViewBinding.ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBack();
                }
            }
        });
        mViewBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onExit();
                }
            }
        });
        mViewBinding.ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onNext();
                }
            }
        });
        mViewBinding.wheelProvince.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                try {
                    updateCityData(provinceList.get(index), "", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mViewBinding.wheelCity.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                try {
                    updateCityData(province, cityList.get(index), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //更新城市数据
    private void updateCityData(String province, String city, boolean isProvinceChange) {
        if (isProvinceChange) {
            List<String> cityList = locationMap.get(province);
            mViewBinding.wheelCity.setAdapter(new ArrayWheelAdapter(cityList));
            mViewBinding.wheelCity.setCurrentItem(0);
            city = cityList.get(0);
            this.province = province;
        }
        ((RegisterDetailActivity) getActivity()).getDetailBean().setLocation(province + "," + city);

    }

    @Override
    public void updateDetailUI(RegisterDetailBean bean) {
        if (bean == null || locationMap == null || provinceList == null || mViewBinding == null) {
            return;
        }
        initAndSetCity(bean);
    }

    private void initAndSetCity(RegisterDetailBean bean) {
        try {
            String location = bean.getLocation();
            String[] strings = location.split(",");
            if (strings.length != 2) {
                return;
            }
            String province = strings[0];
            String city = strings[1];
            int provinceIndex = provinceList.indexOf(province);
            if (provinceIndex < 0) {
                return;
            }
            mViewBinding.wheelProvince.setCurrentItem(provinceIndex);
            cityList = locationMap.get(province);
            if (cityList != null) {
                int cityIndex = cityList.indexOf(city);
                if (cityIndex < 0) {
                    return;
                }
                mViewBinding.wheelCity.setCurrentItem(cityIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
