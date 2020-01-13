package com.yanlong.im.location;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.google.gson.Gson;
import com.yanlong.im.MyAppLication;
import com.yanlong.im.R;
import com.yanlong.im.chat.bean.LocationMessage;
import com.yanlong.im.chat.bean.MsgAllBean;
import com.yanlong.im.chat.ui.forward.MsgForwardActivity;
import com.yanlong.im.dialog.MapDialog;
import com.yanlong.im.listener.BaseListener;
import com.yanlong.im.utils.DataUtils;
import com.yanlong.im.view.MaxHeightRecyclerView;

import net.cb.cb.library.utils.GsonUtils;
import net.cb.cb.library.utils.InputUtil;
import net.cb.cb.library.utils.LogUtil;
import net.cb.cb.library.utils.StringUtil;
import net.cb.cb.library.utils.ToastUtil;
import net.cb.cb.library.utils.ViewUtils;
import net.cb.cb.library.view.ActionbarView;
import net.cb.cb.library.view.AppActivity;
import net.cb.cb.library.view.ClearEditText;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * zgd 位置 地图搜索
 * 20191214
 */

public class LocationActivity extends AppActivity {
    //    private HeadView headView;
    private ActionbarView actionbar;
    private MapView mapview;
    private TextView search_tv;
    private LinearLayout view_search;

    private LinearLayout addr_ll;
    private TextView addr_tv;
    private TextView addr_desc_tv;
    private ImageView navigation_iv;
    private ImageView curr_location_iv;

    private LinearLayout search_ll;
    private ClearEditText edtSearch;
    private TextView cancel_tv;

    private BaiduMap mBaiduMap;
    private LocationService locService;
    private BDAbstractLocationListener listener;

    private RecyclerView recyclerview;
    private List<LocationMessage> locationList;
    private LocationPoiAdapter locationPoiAdapter;

    private MaxHeightRecyclerView recyclerviewSearch;
    private List<LocationMessage> locationListSearch;
    private LocationPoiAdapter locationPoiAdapterSearch;

    private ImageView center_location_iv;

    private Boolean isShow = true;
    private String city = "长沙市";//默认城市
    private int latitude = 28136296;//默认定位
    private int longitude = 112953042;//默认定位
    private String addr = "";//
    private String addrDesc = "";//
    private MsgAllBean msgAllBean;
    private Boolean dragging = false;// 拖拽中 Dragging  draggableing
    private float zoom = 18.0f;


    public static void openActivity(Activity activity, Boolean isShow, MsgAllBean bean) {
        if (!LocationPersimmions.checkPermissions(activity)) {
            return;
        }
        if (!LocationUtils.isLocationEnabled(activity)) {
            ToastUtil.show(activity, "请打开定位服务");
            return;
        }
        Intent intent = new Intent(activity, LocationActivity.class);
        intent.putExtra("isShow", isShow);
        if (bean != null) {
            intent.putExtra("MsgAllBean", GsonUtils.optObject(bean));
        }
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue_title));

        findViews();
        initEvent();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapview.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        locService.unregisterListener(listener);
        locService.stop();
        mapview.onDestroy();
    }


    //自动寻找控件
    private void findViews() {
//        headView = findViewById(R.id.headView);
//        actionbar = headView.getActionbar();
        actionbar = findViewById(R.id.actionbar_view);
        actionbar.setTitle("位置");
        mapview = findViewById(R.id.mapview);
        search_tv = findViewById(R.id.search_tv);
        view_search = findViewById(R.id.view_search);

        search_ll = findViewById(R.id.search_ll);
        search_ll.setVisibility(View.GONE);
        edtSearch = findViewById(R.id.edt_search);
        cancel_tv = findViewById(R.id.cancel_tv);

        addr_ll = findViewById(R.id.addr_ll);
        addr_ll.setVisibility(View.GONE);
        addr_tv = findViewById(R.id.addr_tv);
        addr_desc_tv = findViewById(R.id.addr_desc_tv);
        navigation_iv = findViewById(R.id.navigation_iv);
        curr_location_iv = findViewById(R.id.curr_location_iv);

        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setVisibility(View.GONE);

        locationList = new ArrayList<>();
        locationPoiAdapter = new LocationPoiAdapter(context, locationList);
        recyclerview.setAdapter(locationPoiAdapter);
        locationPoiAdapter.setListener(new BaseListener() {
            @Override
            public void onSuccess(Object object) {
                super.onSuccess(object);
                if (object != null) {
                    LocationMessage locationMessage = (LocationMessage) object;
                    if (locationMessage.getLatitude() == -1 || locationMessage.getLongitude() == -1) {
                        getPoi(true, city, locationMessage.getAddress());
                    } else {
                        setLocationBitmap(false, locationMessage.getLatitude() / LocationUtils.beishu, locationMessage.getLongitude() / LocationUtils.beishu);
                    }
                }
            }
        });

        locationListSearch = new ArrayList<>();
        recyclerviewSearch = findViewById(R.id.recyclerview_search);
        locationPoiAdapterSearch = new LocationPoiAdapter(context, locationListSearch);
        recyclerviewSearch.setAdapter(locationPoiAdapterSearch);
        locationPoiAdapterSearch.setListener(new BaseListener() {
            @Override
            public void onSuccess(Object object) {
                super.onSuccess(object);
                if (object != null) {
                    LocationMessage locationMessage = (LocationMessage) object;
                    if (locationMessage.getLatitude() == -1 || locationMessage.getLongitude() == -1) {
                        getPoi(true, city, locationMessage.getAddress());
                    } else {

                        actionbar.setVisibility(View.VISIBLE);
                        view_search.setVisibility(View.VISIBLE);

                        search_ll.setVisibility(View.GONE);

                        locationList.clear();
                        locationList.addAll(locationListSearch);
                        locationPoiAdapter.position = locationPoiAdapterSearch.position;
                        recyclerview.getAdapter().notifyDataSetChanged();
                        recyclerview.scrollToPosition(locationPoiAdapter.position);

                        setLocationBitmap(false, locationMessage.getLatitude() / LocationUtils.beishu, locationMessage.getLongitude() / LocationUtils.beishu);
                    }
                }
            }
        });

        center_location_iv = findViewById(R.id.center_location_iv);
        center_location_iv.setVisibility(View.GONE);
    }


    //自动生成的控件事件
    private void initEvent() {
        actionbar.getBtnLeft().setVisibility(View.GONE);
        actionbar.setTxtLeft("取消");
        isShow = getIntent().getBooleanExtra("isShow", true);
        String msgAllBeanStr = getIntent().getStringExtra("MsgAllBean");
        if (StringUtil.isNotNull(msgAllBeanStr)) {
            msgAllBean = GsonUtils.getObject(msgAllBeanStr, MsgAllBean.class);
            if (msgAllBean != null && msgAllBean.getLocationMessage() != null) {
                latitude = msgAllBean.getLocationMessage().getLatitude();
                longitude = msgAllBean.getLocationMessage().getLongitude();
                addr = msgAllBean.getLocationMessage().getAddress();
                addrDesc = msgAllBean.getLocationMessage().getAddressDescribe();
            }
        }


        actionbar.setOnListenEvent(new ActionbarView.ListenEvent() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onRight() {
                if (isShow) {
                    startActivity(new Intent(getContext(), MsgForwardActivity.class)
                            .putExtra(MsgForwardActivity.AGM_JSON, GsonUtils.optObject(msgAllBean)));
                } else {
                    if (locationList.size() > locationPoiAdapter.position) {
                        LocationMessage message = locationList.get(locationPoiAdapter.position);
                        if (message.getLatitude() == -1 || message.getLongitude() == -1) {
                            getPoi(false, city, locationList.get(locationPoiAdapter.position).getAddress());
                        } else {
                            EventBus.getDefault().post(new LocationSendEvent(message));

                            finish();
                        }
                    } else {
                        ToastUtil.show(context, "请选择定位的地址");
                    }
                }
            }
        });

        //百度地图参数
        mBaiduMap = mapview.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18f));
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                dragging = true;
                if (mapStatus != null) {
//                    if(!isShow&&zoom==mapStatus.zoom){
//                        center_location_iv.setVisibility(View.VISIBLE);
//                    }
                    zoom = mapStatus.zoom;
                }
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
//                LogUtil.getLog().e("=location===onMapStatusChangeFinish==="+GsonUtils.optObject(mapStatus));
                if (mapStatus != null) {
                    if (zoom == mapStatus.zoom) {
                        if (!isShow && dragging && mapStatus != null && mapStatus.target != null) {
//                            fanSearch(mapStatus.target.latitude, mapStatus.target.longitude);
                            searchNeayBy(mapStatus.target.latitude, mapStatus.target.longitude);
                        }
                    }else {
                        //缩放地图 不做改变
                    }
                }
                dragging = false;
//                center_location_iv.setVisibility(View.GONE);
            }
        });

        locService = ((MyAppLication) getApplication()).locationService;
        LocationClientOption mOption = locService.getDefaultLocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        mOption.setCoorType("bd09ll");
        locService.setLocationOption(mOption);

        setLocationBitmap(false, latitude / LocationUtils.beishu, longitude / LocationUtils.beishu);//设置默认定位

        listener = new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {

                try {
//                    LogUtil.getLog().e("=location====" + GsonUtils.optObject(bdLocation));
                    if (bdLocation != null && bdLocation.getPoiList() != null) {
                        city = bdLocation.getCity();

                        if (isShow) {
                            setLocationBitmap(true, bdLocation.getLatitude(), bdLocation.getLongitude());
                        } else {
                            locationList.clear();
                            locationPoiAdapter.position = 0;
                            for (int i = 0; i < bdLocation.getPoiList().size(); i++) {
                                LocationMessage locationMessage = new LocationMessage();
                                if (i == 0) {
                                    locationMessage.setLatitude((int) (bdLocation.getLatitude() * LocationUtils.beishu));
                                    locationMessage.setLongitude((int) (bdLocation.getLongitude() * LocationUtils.beishu));
                                    locationMessage.setImg(LocationUtils.getLocationUrl2(bdLocation.getLatitude(), bdLocation.getLongitude()));
                                }
                                locationMessage.setAddress(bdLocation.getPoiList().get(i).getName());
                                locationMessage.setAddressDescribe(bdLocation.getPoiList().get(i).getAddr());
                                locationList.add(locationMessage);

                                getPoi(false, city, bdLocation.getPoiList().get(i).getName());
                            }
                            recyclerview.getAdapter().notifyDataSetChanged();
                            recyclerview.setVisibility(View.VISIBLE);
                            setLocationBitmap(false, bdLocation.getLatitude(), bdLocation.getLongitude());
                        }

                        locService.stop();//定位成功后停止点位
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        locService.registerListener(listener);

        if (isShow) {
            view_search.setVisibility(View.GONE);
            addr_ll.setVisibility(View.VISIBLE);
            addr_tv.setText(addr);
            addr_desc_tv.setText(addrDesc);
//            curr_location_iv.setVisibility(View.GONE);
            actionbar.getBtnRight().setVisibility(View.VISIBLE);
            actionbar.getBtnRight().setImageResource(R.mipmap.ic_chat_more);
        } else {
            addr_ll.setVisibility(View.GONE);
            actionbar.setTxtRight("发送");
            locService.start();
        }


        search_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionbar.setVisibility(View.GONE);
                view_search.setVisibility(View.GONE);

                search_ll.setVisibility(View.VISIBLE);
//                search_ll.setBackgroundResource(R.color.transparent_33);
                edtSearch.requestFocus();
                InputUtil.showKeyboard(edtSearch);

            }
        });

        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionbar.setVisibility(View.VISIBLE);
                view_search.setVisibility(View.VISIBLE);

                search_ll.setVisibility(View.GONE);
                InputUtil.hideKeyboard(edtSearch);
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    search();
                }
                return false;
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtSearch.getText().toString().length() == 0) {
                    //搜索关键字为0的时候，重新显示全部消息
//                    locService.start();
                    locationListSearch.clear();
                    locationPoiAdapterSearch.position = 0;
                    recyclerviewSearch.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        curr_location_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtils.isFastDoubleClick()) {
                    return;
                }
                locService.start();
            }
        });

        navigation_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapDialog mapDialog = new MapDialog(LocationActivity.this, new BaseListener() {
                    @Override
                    public void onSuccess(String str) {
                        super.onSuccess(str);
                        try {
                            if ("baidu".equals(str)) {
                                if (!DataUtils.isInstallApk(LocationActivity.this, "com.baidu.BaiduMap")) {
                                    ToastUtil.show("请先安装百度地图");
                                    return;
                                }

                                Uri uri = Uri.parse("baidumap://map/direction?destination="
                                        + "latlng:" + latitude / LocationUtils.beishu + "," + longitude / LocationUtils.beishu + "|name:" + addr + "&mode=driving");
                                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                            } else if ("gaode".equals(str)) {
                                if (!DataUtils.isInstallApk(LocationActivity.this, "com.autonavi.minimap")) {
                                    ToastUtil.show("请先安装高德地图");
                                    return;
                                }
                                //直接导航
//                                Uri uri = Uri.parse("androidamap://navi?sourceApplication=appname"
//                                        + "&poiname="+addr +"&lat=" + latitude/LocationUtils.beishu + "&lon=" + longitude/LocationUtils.beishu + "&dev=0&style=2");

                                //规划路线
                                Uri uri = Uri.parse("amapuri://route/plan/?did=BGVIS2"
                                        + "&dlat=" + LocationUtils.bdToGc("lat", latitude / LocationUtils.beishu, longitude / LocationUtils.beishu)
                                        + "&dlon=" + LocationUtils.bdToGc("lon", latitude / LocationUtils.beishu, longitude / LocationUtils.beishu)
                                        + "&dname=" + addr + "&dev=0&t=0");

                                startActivity(new Intent(Intent.ACTION_VIEW, uri)); // 启动调用
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                mapDialog.show();
            }
        });
    }


    private void setLocationBitmap(Boolean isMyLocation, double latitude, double longitude) {
        LogUtil.getLog().e("===location====" + latitude + "====" + longitude);
        LatLng point = new LatLng(latitude, longitude);
        // 构建Marker图标
        BitmapDescriptor bitmap = null;
        if (isMyLocation) {
            bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.location_circle_big); // 非推算结果
        } else {
            mBaiduMap.clear();
            bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.location_two); // 非推算结果
        }

        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
    }


    //搜索
    private void search() {
        String key = edtSearch.getText().toString();
        LogUtil.getLog().e("=location===key=" + key);
        if (!StringUtil.isNotNull(key)) {
            return;
        }

        InputUtil.hideKeyboard(edtSearch);

        locationListSearch.clear();
        locationPoiAdapterSearch.position = 0;
        recyclerviewSearch.getAdapter().notifyDataSetChanged();

        //建议搜索
        SuggestionSearch mSuggestionSearch = SuggestionSearch.newInstance();
        OnGetSuggestionResultListener listener2 = new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                LogUtil.getLog().e("=location===建议搜索==suggestionResult=" + GsonUtils.optObject(suggestionResult));
                //处理sug检索结果
                if (suggestionResult != null && "NO_ERROR".equals(suggestionResult.error.name())
                        && suggestionResult.getAllSuggestions() != null && suggestionResult.getAllSuggestions().size() > 0) {
                    search_ll.setBackgroundResource(R.color.white);
                    List<SuggestionResult.SuggestionInfo> list = suggestionResult.getAllSuggestions();
                    boolean hasSetBitmap = false;
                    for (int i = 0; i < list.size(); i++) {
                        SuggestionResult.SuggestionInfo sug = list.get(i);
                        if (sug != null && sug.pt != null) {
                            LocationMessage locationMessage = new LocationMessage();
                            locationMessage.setLatitude((int) (sug.pt.latitude * LocationUtils.beishu));
                            locationMessage.setLongitude((int) (sug.pt.longitude * LocationUtils.beishu));
                            locationMessage.setImg(LocationUtils.getLocationUrl2(sug.pt.latitude, sug.pt.longitude));
                            locationMessage.setAddress(sug.getKey());
                            locationMessage.setAddressDescribe(sug.getCity() + sug.getDistrict() + sug.getAddress());
                            locationListSearch.add(locationMessage);

//                            if(!hasSetBitmap){
//                                setLocationBitmap(sug.pt.latitude,sug.pt.longitude);
//                                hasSetBitmap=true;
//                            }
                        }
                    }

                    recyclerviewSearch.getAdapter().notifyDataSetChanged();
//                    recyclerview2.setVisibility(View.VISIBLE);

                }
            }
        };

        mSuggestionSearch.setOnGetSuggestionResultListener(listener2);
        mSuggestionSearch.requestSuggestion(new SuggestionSearchOption()
                .city(city)
                .keyword(key));

    }


    //获取经纬度
    private void getPoi(boolean isAddBitmap, String city, String address) {
        if (!StringUtil.isNotNull(city) || !StringUtil.isNotNull(city)) {
            return;
        }

        // 通过GeoCoder的实例方法得到GerCoder对象
        GeoCoder mGeoCoder = GeoCoder.newInstance();
        // 为GeoCoder设置监听事件
        mGeoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            // 这个方法是将坐标转化为具体地址
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
                LogUtil.getLog().e("=location===地理编码搜索=arg0=" + GsonUtils.optObject(arg0));
            }

            // 将具体的地址转化为坐标
            @Override
            public void onGetGeoCodeResult(GeoCodeResult arg1) {
                LogUtil.getLog().e("=location===地理编码搜索=arg1=" + GsonUtils.optObject(arg1));
                if (arg1 != null && "NO_ERROR".equals(arg1.error.name()) && arg1.getLocation() != null) {
                    for (int i = 0; i < locationList.size(); i++) {
                        if (address.equals(locationList.get(i).getAddress())) {
                            locationList.get(i).setLatitude((int) (arg1.getLocation().latitude * LocationUtils.beishu));
                            locationList.get(i).setLongitude((int) (arg1.getLocation().longitude * LocationUtils.beishu));
                            locationList.get(i).setImg(LocationUtils.getLocationUrl2(arg1.getLocation().latitude, arg1.getLocation().longitude));
                            break;
                        }
                    }

                    if (isAddBitmap) {
                        setLocationBitmap(false, arg1.getLocation().latitude, arg1.getLocation().longitude);
                    }
                }
            }
        });
        //地理编码搜索   必须设置在监听后面，否则监听无法回调。  得到GenCodeOption对象
        mGeoCoder.geocode(new GeoCodeOption()
                .city(city)
                .address(address));
    }

    //根据经纬度获取位置信息
    private void fanSearch(double latitude, double longitude) {
        LogUtil.getLog().e("===location===反Geo搜索=latitude=" + latitude + "==longitude=" + longitude);
        // 搜索模块 初始化搜索模块，注册事件监听
        GeoCoder mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                //反搜索
//                LogUtil.getLog().e("===location===反Geo搜索=onGetReverseGeoCodeResult=" + GsonUtils.optObject(reverseGeoCodeResult));
                if (reverseGeoCodeResult != null && "NO_ERROR".equals(reverseGeoCodeResult.error.name())) {
                    locationList.clear();
                    locationPoiAdapter.position = 0;
                    recyclerview.getAdapter().notifyDataSetChanged();

                    setLocationBitmap(false, latitude, longitude);
                    LocationMessage locationMessage = new LocationMessage();
                    locationMessage.setLatitude((int) (latitude * LocationUtils.beishu));
                    locationMessage.setLongitude((int) (longitude * LocationUtils.beishu));
                    locationMessage.setImg(LocationUtils.getLocationUrl2(latitude, longitude));
                    locationMessage.setAddress(reverseGeoCodeResult.getBusinessCircle());
                    locationMessage.setAddressDescribe(reverseGeoCodeResult.getAddress());
                    locationList.add(locationMessage);

                    if (reverseGeoCodeResult.getPoiList() != null) {
                        for (int i = 0; i < reverseGeoCodeResult.getPoiList().size(); i++) {
                            if (reverseGeoCodeResult.getPoiList().get(i).getLocation() != null) {
                                PoiInfo poiInfo = reverseGeoCodeResult.getPoiList().get(i);
                                LocationMessage locationMessageTemp = new LocationMessage();
                                locationMessageTemp.setLatitude((int) (poiInfo.getLocation().latitude * LocationUtils.beishu));
                                locationMessageTemp.setLongitude((int) (poiInfo.getLocation().longitude * LocationUtils.beishu));
                                locationMessageTemp.setImg(LocationUtils.getLocationUrl2(poiInfo.getLocation().latitude, poiInfo.getLocation().longitude));
                                locationMessageTemp.setAddress(poiInfo.getName());
                                locationMessageTemp.setAddressDescribe(poiInfo.getAddress());
                                locationList.add(locationMessageTemp);
                            }
                        }
                    }
                    recyclerview.getAdapter().notifyDataSetChanged();
                }
            }
        });

        // 反Geo搜索
        ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
        reverseGeoCodeOption.location(new LatLng(latitude, longitude));
        reverseGeoCodeOption.radius(1000);
        reverseGeoCodeOption.pageSize(10);
        mSearch.reverseGeoCode(reverseGeoCodeOption);
    }


    //拖拽
    private void searchNeayBy(double latitude, double longitude) {
        locationList.clear();
        locationPoiAdapter.position = 0;
//        recyclerview.getAdapter().notifyDataSetChanged();
        //"写字楼、住宅、餐厅、公交、学校"
        searchNeayBy("写字楼", latitude, longitude);
        searchNeayBy("住宅", latitude, longitude);
        searchNeayBy("餐厅", latitude, longitude);
        searchNeayBy("学校", latitude, longitude);
        searchNeayBy("公交", latitude, longitude);
    }

    //搜索周边地理位置
    private void searchNeayBy(String key, double latitude, double longitude) {
        PoiSearch mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                LogUtil.getLog().e(key + "=location===搜索周边=poiResult=" + GsonUtils.optObject(poiResult));
                if (poiResult != null && "NO_ERROR".equals(poiResult.error.name()) && poiResult.getAllPoi() != null) {
                    List<PoiInfo> poiInfoList = poiResult.getAllPoi();
                    for (int i = 0; i < poiInfoList.size(); i++) {
                        if (poiResult.getAllPoi().get(i).getLocation() != null) {
                            LocationMessage locationMessage = new LocationMessage();
                            locationMessage.setLatitude((int) (poiResult.getAllPoi().get(i).getLocation().latitude * LocationUtils.beishu));
                            locationMessage.setLongitude((int) (poiResult.getAllPoi().get(i).getLocation().longitude * LocationUtils.beishu));
                            locationMessage.setImg(LocationUtils.getLocationUrl2(poiResult.getAllPoi().get(i).getLocation().latitude
                                    , poiResult.getAllPoi().get(i).getLocation().longitude));
                            locationMessage.setAddress(poiResult.getAllPoi().get(i).getName());
                            locationMessage.setAddressDescribe(poiResult.getAllPoi().get(i).getAddress());
                            locationList.add(locationMessage);
                        }
                    }

                }

                if (locationList.size() > 0) {
                    setLocationBitmap(false, locationList.get(0).getLatitude() / LocationUtils.beishu
                            , locationList.get(0).getLongitude() / LocationUtils.beishu);
                }
                recyclerview.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            }
        });

        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.keyword(key);
        option.location(new LatLng(latitude, longitude));
        option.radius(1000);
        option.pageCapacity(2);
        mPoiSearch.searchNearby(option);
    }
}
