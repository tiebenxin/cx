package net.cb.cb.library.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.cb.cb.library.R;
import net.cb.cb.library.base.BaseDialog;
import net.cb.cb.library.bean.CityBean;
import net.cb.cb.library.utils.GsonUtils;
import net.cb.cb.library.utils.ToastUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Liszt
 * @date 2020/11/19
 * Description 位置选择器
 */
public class DialogLocationSelector extends BaseDialog {
    List<String> provinceList;
    List<String> cityList;
    Map<String, List<String>> locationMap;
    String province = "";
    String city = "";
    private WheelView wheelProvince;
    private WheelView wheelCity;
    private TextView tvCancel;
    private TextView tvSure;
    private ILocationListener listener;

    public DialogLocationSelector(Context context, String province, String city) {
        this(context, R.style.MyDialogTheme);
        this.province = province;
        this.city = city;
    }

    public DialogLocationSelector(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_location_selector);
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity= Gravity.BOTTOM;
        wheelProvince = findViewById(R.id.wheel_province);
        wheelCity = findViewById(R.id.wheel_city);
        tvCancel = findViewById(R.id.tv_left);
        tvSure = findViewById(R.id.tv_right);
        tvCancel.setOnClickListener(this);
        tvSure.setOnClickListener(this);
        initLocationData();
        //有默认值
        int provinceIndex = 0;
        int cityIndex = 0;
        if (!TextUtils.isEmpty(province)) {
            provinceIndex = provinceList.indexOf(province);
            cityList = locationMap.get(provinceList);
            if (cityList != null) {
                cityIndex = cityList.indexOf(city);
            }
        } else {
            province = provinceList.get(0);
            cityList = locationMap.get(province);
            city = cityList.get(0);
        }
        wheelProvince.setAdapter(new ArrayWheelAdapter(provinceList));
        wheelProvince.setCyclic(false);
        wheelProvince.setCurrentItem(provinceIndex);

        wheelCity.setAdapter(new ArrayWheelAdapter(cityList));
        wheelCity.setCyclic(false);
        wheelCity.setCurrentItem(cityIndex);


        wheelProvince.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                try {
                    updateCityData(provinceList.get(index), "", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        wheelCity.setOnItemSelectedListener(new OnItemSelectedListener() {
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

    @Override
    public void processClick(View view) {
        int id = view.getId();
        if (id == tvCancel.getId()) {
            dismiss();
        } else if (id == tvSure.getId()) {
            if (TextUtils.isEmpty(province) || TextUtils.isEmpty(city)) {
                ToastUtil.show("请选择所在地");
                return;
            }
            if (listener != null) {
                listener.onSure(province, city);
            }
            dismiss();
        }
    }

    private void initLocationData() {
        if (provinceList == null) {
            provinceList = new ArrayList<>();
        }
        if (locationMap == null) {
            locationMap = new HashMap<>();
        }
        if (provinceList.size() <= 0 || locationMap.size() <= 0) {
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

    //更新城市数据
    private void updateCityData(String province, String city, boolean isProvinceChange) {
        if (isProvinceChange) {
            cityList = locationMap.get(province);
            wheelCity.setAdapter(new ArrayWheelAdapter(cityList));
            wheelCity.setCurrentItem(0);
            this.city = cityList.get(0);
            this.province = province;
        } else {
            this.city = city;
            this.province = province;
        }
    }

    public DialogLocationSelector setListener(ILocationListener l) {
        listener = l;
        return this;
    }

    public interface ILocationListener {
        void onSure(String province, String city);
    }

}
