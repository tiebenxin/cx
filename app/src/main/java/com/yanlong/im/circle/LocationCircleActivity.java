package com.yanlong.im.circle;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.luck.picture.lib.CreateCircleActivity;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.adapter.CommonRecyclerViewAdapter;
import com.yanlong.im.chat.bean.LocationMessage;
import com.yanlong.im.databinding.ActivityCircleLocationBinding;
import com.yanlong.im.databinding.ItemCircleLocationBinding;
import com.yanlong.im.location.LocationService;
import com.yanlong.im.location.LocationUtils;

import net.cb.cb.library.base.bind.BaseBindActivity;
import net.cb.cb.library.dialog.DialogCommon;
import net.cb.cb.library.utils.CheckPermission2Util;
import net.cb.cb.library.utils.InputUtil;
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

    private CommonRecyclerViewAdapter<LocationMessage, ItemCircleLocationBinding> mAdapter;
    private List<LocationMessage> mList;

    private String mCity = "";
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
        mList = new ArrayList<>();
        mAdapter = new CommonRecyclerViewAdapter<LocationMessage, ItemCircleLocationBinding>(this, R.layout.item_circle_location) {
            @Override
            public void bind(ItemCircleLocationBinding binding, LocationMessage data, int position, RecyclerView.ViewHolder viewHolder) {
                binding.tvName.setText(data.getAddress());
                binding.ivSelect.setVisibility(data.isCheck() ? View.VISIBLE : View.GONE);
                if (TextUtils.isEmpty(data.getAddressDescribe())) {
                    binding.tvAddress.setVisibility(View.GONE);
                } else {
                    binding.tvAddress.setVisibility(View.VISIBLE);
                    binding.tvAddress.setText(data.getAddressDescribe());
                }
                if (data.isCheck()) {
                    binding.tvName.setTextColor(getResources().getColor(R.color.red_400));
                } else {
                    binding.tvName.setTextColor(getResources().getColor(R.color.black));
                }
                binding.layoutRoot.setOnClickListener(o -> {
                    for (LocationMessage bean : mList) {
                        bean.setCheck(false);
                    }
                    data.setCheck(true);
                    notifyDataSetChanged();
                    setResult(data.getAddress());
                });
            }
        };
        bindingView.recyclerView.setLayoutManager(new YLLinearLayoutManager(getContext()));
        mAdapter.setData(mList);
        bindingView.recyclerView.setAdapter(mAdapter);
    }

    private void setResult(String name) {
        Intent intent = new Intent();
        intent.putExtra(CreateCircleActivity.INTENT_LOCATION_NAME, name);
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
        bindingView.recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                showInput(false);
            }
        });
        listener = new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                try {
                    mList.clear();
                    if (bdLocation != null && bdLocation.getPoiList() != null) {
                        mCity = bdLocation.getCity();
                        addLocationCity();
                        for (int i = 0; i < bdLocation.getPoiList().size(); i++) {
                            LocationMessage locationMessage = new LocationMessage();
                            if (i == 0) {
                                locationMessage.setLatitude((int) (bdLocation.getLatitude() * LocationUtils.beishu));
                                locationMessage.setLongitude((int) (bdLocation.getLongitude() * LocationUtils.beishu));
                                locationMessage.setImg(LocationUtils.getLocationUrl2(bdLocation.getLatitude(), bdLocation.getLongitude()));
                            }
                            locationMessage.setAddress(bdLocation.getPoiList().get(i).getName());
                            locationMessage.setAddressDescribe(bdLocation.getPoiList().get(i).getAddr());
                            mList.add(locationMessage);
                        }
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

    private void addLocationCity() {
        LocationMessage locationMessage = new LocationMessage();
        locationMessage.setAddress("不显示位置");
        locationMessage.setCheck(true);
        LocationMessage location = new LocationMessage();
        location.setAddress(mCity);
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
            return;
        }
        InputUtil.hideKeyboard(bindingView.editSearch);
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
                            LocationMessage locationMessage = new LocationMessage();
                            locationMessage.setLatitude((int) (sug.pt.latitude * LocationUtils.beishu));
                            locationMessage.setLongitude((int) (sug.pt.longitude * LocationUtils.beishu));
                            locationMessage.setImg(LocationUtils.getLocationUrl2(sug.pt.latitude, sug.pt.longitude));
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
