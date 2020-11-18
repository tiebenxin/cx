package net.cb.cb.library.bean;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import net.cb.cb.library.base.BaseBean;

import java.util.List;

/**
 * @author Liszt
 * @date 2020/11/17
 * Description 城市数据
 */
public class CityBean extends BaseBean {
    private String province;
    @SerializedName("city_list")
    private List<String> cityList;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public List<String> getCityList() {
        return cityList;
    }

    public void setCityList(List<String> cityList) {
        this.cityList = cityList;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (TextUtils.isEmpty(province) || TextUtils.isEmpty(((CityBean) obj).province)) {
            return false;
        }
        return province.equals(((CityBean) obj).province);
    }
}
