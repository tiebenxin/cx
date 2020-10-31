package com.yanlong.im.circle;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.luck.picture.lib.circle.CreateCircleActivity;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.chat.bean.LocationCircleMessage;
import com.yanlong.im.databinding.ActivityCircleLocationBinding;
import com.yanlong.im.databinding.ItemCircleLocationBinding;
import com.yanlong.im.location.LocationService;
import com.yanlong.im.location.LocationUtils;

import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.YLLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-09-11
 * @updateAuthor
 * @updateDate
 * @description 所在位置
 * @copyright copyright(c)2020 ChangSha YouMeng Technology Co., Ltd. Inc. All rights reserved.
 */
@Route(path = LocationCircleActivity.path)
public class LocationCircleActivity extends BaseBindActivity<ActivityCircleLocationBinding> {
    public static final String path = "/circle/LocationCircleActivity";

    private CommonRecyclerViewAdapter<LocationCircleMessage, ItemCircleLocationBinding> mAdapter;
    private List<LocationCircleMessage> mList;
    private List<LocationCircleMessage> mFirstList;
    public static final String ADDRESS_NAME = "address_name";
    public static final String ADDRESS_DESC = "address_desc";
    public static final String CITY_NAME = "city_name";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    private double mLatitude, mLongitude;
    private boolean isCheck = false;
    private String mCity = "", mAddress = "", mAddressDesc = "";
    private DialogCommon dialogCommon = null;
    private CheckPermission2Util permission2Util = new CheckPermission2Util();

    // 地图相关
    private LocationService locService;
    private BDAbstractLocationListener listener;
    private SuggestionSearch mSuggestionSearch;

    @Override
    protected int setView() {
        return R.layout.activity_circle_location;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mAddress = getIntent().getExtras().getString(ADDRESS_NAME);
        mAddressDesc = getIntent().getExtras().getString(ADDRESS_DESC);
        mCity = getIntent().getExtras().getString(CITY_NAME);
        mLatitude = getIntent().getExtras().getDouble(LATITUDE);
        mLongitude = getIntent().getExtras().getDouble(LONGITUDE);

        mList = new ArrayList<>();
        mFirstList = new ArrayList<>();
        mAdapter = new CommonRecyclerViewAdapter<LocationCircleMessage, ItemCircleLocationBinding>(this, R.layout.item_circle_location) {
            @Override
            public void bind(ItemCircleLocationBinding binding, LocationCircleMessage data, int position, RecyclerView.ViewHolder viewHolder) {
                binding.tvName.setText(data.getAddress());
                binding.ivSelect.setVisibility(data.isCheck() ? View.VISIBLE : View.GONE);
                if (TextUtils.isEmpty(data.getAddressDescribe())) {
                    binding.tvAddress.setVisibility(View.GONE);
                } else {
                    binding.tvAddress.setVisibility(View.VISIBLE);
                    binding.tvAddress.setText(data.getAddressDescribe());
                }
                if (data.isCheck()) {
                    binding.tvName.setTextColor(getResources().getColor(R.color.gray_33b));
                    binding.ivLocation.setImageResource(R.mipmap.ic_circle_location_check);

                } else {
                    binding.tvName.setTextColor(getResources().getColor(R.color.gray_343));
                    binding.ivLocation.setImageResource(R.mipmap.ic_circle_location_g);
                }
                binding.layoutRoot.setOnClickListener(o -> {
                    for (LocationCircleMessage bean : mList) {
                        bean.setCheck(false);
                    }
                    data.setCheck(true);
                    notifyDataSetChanged();
                    setResult(data.getAddress(), data.getAddressDescribe(), data.getLatitude(), data.getLongitude());
                });
            }
        };
        bindingView.recyclerView.setLayoutManager(new YLLinearLayoutManager(getContext()));
        mAdapter.setData(mList);
        bindingView.recyclerView.setAdapter(mAdapter);
    }

    private void setResult(String name, String desc, double latitude, double longitude) {
        Intent intent = new Intent();
        intent.putExtra(CreateCircleActivity.INTENT_LOCATION_NAME, name);
        intent.putExtra(CreateCircleActivity.INTENT_LOCATION_DESC, desc);
        intent.putExtra(CreateCircleActivity.CITY_NAME, mCity);
        intent.putExtra(CreateCircleActivity.LATITUDE, latitude);
        intent.putExtra(CreateCircleActivity.LONGITUDE, longitude);
        setResult(RESULT_OK, intent);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initEvent() {
        bindingView.headView.getActionbar().setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {

            }
        });
        listener = new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                try {
                    mList.clear();
                    if (bdLocation != null && bdLocation.getPoiList() != null) {
                        mCity = bdLocation.getCity();

                        addLocationCity(bdLocation);
                        for (int i = 0; i < bdLocation.getPoiList().size(); i++) {
                            LocationCircleMessage locationMessage = new LocationCircleMessage();
                            locationMessage.setLatitude(bdLocation.getLatitude());
                            locationMessage.setLongitude(bdLocation.getLongitude());
                            if (!TextUtils.isEmpty(mAddress) && bdLocation.getPoiList().get(i).getName().equals(mAddress)) {
                                locationMessage.setCheck(true);
                                isCheck = true;
                            }
                            locationMessage.setAddress(bdLocation.getPoiList().get(i).getName());
                            locationMessage.setAddressDescribe(bdLocation.getPoiList().get(i).getAddr());
                            mList.add(locationMessage);
                        }
                        if (!isCheck && !TextUtils.isEmpty(mAddress)) {
                            LocationCircleMessage locationMessage = new LocationCircleMessage();
                            locationMessage.setLatitude(mLatitude);
                            locationMessage.setLongitude(mLongitude);
                            locationMessage.setCheck(true);
                            locationMessage.setAddress(mAddress);
                            locationMessage.setAddressDescribe(mAddressDesc);
                            mList.add(1, locationMessage);
                        }
                        mFirstList.addAll(mList);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {

                } finally {
                    if (locService != null) {
                        locService.stop();//定位成功后停止点位
                    }
                }
            }
        };
        bindingView.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onSearch();
            }
        });
    }

    @Override
    protected void loadData() {
        locService = ((MyAppLication) getApplication()).locationService;
        if (locService != null) {
            LocationClientOption mOption = locService.getDefaultLocationClientOption();
            mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
            mOption.setCoorType("bd09ll");
            locService.setLocationOption(mOption);
            locService.registerListener(listener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permission2Util.onRequestPermissionsResult();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        checkLocationEnabled();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locService != null) {
            locService.unregisterListener(listener);
            locService.stop();
        }
        if (mSuggestionSearch != null) {
            mSuggestionSearch.destroy();
        }
    }

    private void addLocationCity(BDLocation bdLocation) {
        LocationCircleMessage locationMessage = new LocationCircleMessage();
        locationMessage.setAddress("不显示位置");
        if (TextUtils.isEmpty(mAddress)) {
            locationMessage.setCheck(true);
        }
        LocationCircleMessage location = new LocationCircleMessage();
        if (mAddress.equals(mCity)) {
            location.setCheck(true);
        }
        location.setAddress(mCity);
        location.setLatitude(bdLocation.getLatitude());
        location.setLongitude(bdLocation.getLongitude());
        mList.add(locationMessage);
        mList.add(location);
    }

    /**
     * 检查位置信息
     */
    private void checkLocationEnabled() {
        if (!LocationUtils.isLocationEnabled2(this)) {
            if (dialogCommon == null) {
                dialogCommon = new DialogCommon(this);
                dialogCommon.setCanceledOnTouchOutside(false);
                dialogCommon.setTitleAndSure(true, true)
                        .setTitle("提示")
                        .setContent("请在手机设置中打开GPS和无线网络获取最新位置", false)
                        .setLeft("取消")
                        .setRight("去设置")
                        .setListener(new DialogCommon.IDialogListener() {
                            @Override
                            public void onSure() {
                                //跳转到位置设置
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }

                            @Override
                            public void onCancel() {
                                dialogCommon.dismiss();
                                finish();
                            }
                        });
            }
            dialogCommon.show();
        } else {
            if (dialogCommon != null && dialogCommon.isShowing())
                dialogCommon.dismiss();
            if (locService != null) {
                locService.start();
            }
        }
    }

    /**
     * poi搜索
     */
    private void onSearch() {
        String key = bindingView.editSearch.getText().toString().trim();
        if (!StringUtil.isNotNull(key)) {
            mList.clear();
            mList.addAll(mFirstList);
            mAdapter.notifyDataSetChanged();
            return;
        }
        // 建议搜索
        mSuggestionSearch = SuggestionSearch.newInstance();
        OnGetSuggestionResultListener listener2 = new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                //处理sug检索结果
                if (suggestionResult != null && "NO_ERROR".equals(suggestionResult.error.name())
                        && suggestionResult.getAllSuggestions() != null && suggestionResult.getAllSuggestions().size() > 0) {
                    mList.clear();
                    List<SuggestionResult.SuggestionInfo> list = suggestionResult.getAllSuggestions();
                    for (int i = 0; i < list.size(); i++) {
                        SuggestionResult.SuggestionInfo sug = list.get(i);
                        if (sug != null && sug.pt != null) {
                            LocationCircleMessage locationMessage = new LocationCircleMessage();
                            locationMessage.setLatitude(sug.pt.latitude);
                            locationMessage.setLongitude(sug.pt.longitude);
                            locationMessage.setAddress(sug.getKey());
                            locationMessage.setAddressDescribe(sug.getCity() + sug.getDistrict() + sug.getAddress());
                            mList.add(locationMessage);
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }
        };

        mSuggestionSearch.setOnGetSuggestionResultListener(listener2);
        mSuggestionSearch.requestSuggestion(new SuggestionSearchOption()
                .city(mCity)
                .keyword(key));
    }
}
