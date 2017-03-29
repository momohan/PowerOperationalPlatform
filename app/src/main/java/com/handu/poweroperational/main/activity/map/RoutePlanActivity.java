package com.handu.poweroperational.main.activity.map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.handu.poweroperational.R;
import com.handu.poweroperational.base.BaseActivity;
import com.handu.poweroperational.utils.baidu_map.overlayutil.BikingRouteOverlay;
import com.handu.poweroperational.utils.baidu_map.overlayutil.DrivingRouteOverlay;
import com.handu.poweroperational.utils.baidu_map.overlayutil.MassTransitRouteOverlay;
import com.handu.poweroperational.utils.baidu_map.overlayutil.TransitRouteOverlay;
import com.handu.poweroperational.utils.baidu_map.overlayutil.WalkingRouteOverlay;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.app.ProgressDialog.STYLE_SPINNER;
import static com.handu.poweroperational.main.activity.map.Type.DRIVING_ROUTE;
import static com.handu.poweroperational.main.activity.map.Type.TRANSIT_ROUTE;
import static com.handu.poweroperational.main.activity.map.Type.WALKING_ROUTE;

public class RoutePlanActivity extends BaseActivity implements BaiduMap.OnMapClickListener,
        OnGetRoutePlanResultListener {

    @Bind(R.id.rg_top)
    RadioGroup rgTop;
    private ProgressDialog dialog = null;
    public static final String START_PLACE = "start";
    public static final String END_PLACE = "end";
    public static final String CITY = "city";
    @Bind(R.id.map)
    MapView mMapView;
    private LatLng start;
    private LatLng end;
    private String city;
    // 定位相关
    private LocationClient mLocClient;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    boolean isFirstLoc = true; // 是否首次定位
    boolean isFirstSearch = true;//是否首次规划路线
    // 浏览路线节点相关
    private Button mBtnPre; // 上一个节点
    private Button mBtnNext; // 下一个节点
    private int nodeIndex = -1; // 节点索引,供浏览节点时使用
    private RouteLine route;
    private MassTransitRouteLine massRoute;

    // 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
    // 如果不处理touch事件，则无需继承，直接使用MapView即可
    private BaiduMap mBaiduMap;
    // 搜索相关
    private RoutePlanSearch mSearch;    // 搜索模块，也可去掉地图模块独立使用
    private WalkingRouteResult nowResultWalk;
    private BikingRouteResult nowResultBike;
    private TransitRouteResult nowResultTransit;
    private DrivingRouteResult nowResultDrive;
    private MassTransitRouteResult nowResultMass;
    private int nowSearchType = -1; // 当前进行的检索，供判断浏览节点时结果使用。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan);
        ButterKnife.bind(this);
        initView();
        initMap();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //在activity执行onDestroy时执行mapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void initView() {
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(STYLE_SPINNER);
        dialog.setMessage("正在规划路径中...");
        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = this.getIntent();
        start = intent.getParcelableExtra(START_PLACE);
        end = intent.getParcelableExtra(END_PLACE);
        city = intent.getStringExtra(CITY);
        rgTop.setOnCheckedChangeListener((group, checkedId) -> {
            searchButtonProcess(checkedId);
            for (int i = 0; i < rgTop.getChildCount(); i++) {
                View view = rgTop.getChildAt(i);
                if (view instanceof RadioButton) {
                    RadioButton bt = (RadioButton) view;
                    if (bt.getId() != checkedId)
                        bt.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                    else bt.setTextColor(ContextCompat.getColor(mContext, R.color.red_light));
                }
            }
        });
        rgTop.check(R.id.drive);
    }

    private void initMap() {
        // 地图初始化
        mBaiduMap = mMapView.getMap();
        // 地图点击事件处理
        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setCompassPosition(new Point(70, 380));
        // 地图点击事件处理
        mBaiduMap.setIndoorEnable(true);
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, null,
                accuracyCircleFillColor, accuracyCircleStrokeColor));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                // map view 销毁后不在处理新接收的位置
                if (location == null || mMapView == null) {
                    return;
                }
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100)
                        .latitude(location.getLatitude())
                        .longitude(location.getLongitude())
                        .build();
                mBaiduMap.setMyLocationData(locData);
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                if (isFirstLoc) {
                    isFirstLoc = false;
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(18.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        });
        LocationClientOption mOption = new LocationClientOption();
        mOption.setOpenGps(true); // 打开gps
        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        mOption.setScanSpan(30 * 1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        mOption.setNeedDeviceDirect(true);//可选，设置是否需要设备方向结果
        mOption.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mOption.setIsNeedLocationDescribe(false);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mOption.SetIgnoreCacheException(true);//可选，默认false，设置是否收集CRASH信息，默认收集
        mOption.setIsNeedAltitude(true);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        mLocClient.setLocOption(mOption);
        mLocClient.start();
    }

    /**
     * 发起路线规划搜索示例
     *
     * @param id
     */
    private void searchButtonProcess(int id) {
        if (isFirstSearch) {
            isFirstSearch = false;
            return;
        }
        dialog.show();
        // 重置浏览节点的路线数据
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaiduMap.clear();
        // 处理搜索按钮响应
        // 设置起终点信息，对于tranist search 来说，城市名无意义
        PlanNode stNode = PlanNode.withLocation(start);
        PlanNode enNode = PlanNode.withLocation(end);
        // 实际使用中请对起点终点城市进行正确的设定
        if (id == R.id.mass) {
            PlanNode stMassNode = PlanNode.withLocation(start);
            PlanNode enMassNode = PlanNode.withLocation(end);
            mSearch.masstransitSearch(new MassTransitRoutePlanOption().from(stMassNode).to(enMassNode));
            nowSearchType = 0;
        } else if (id == R.id.drive) {
            mSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(stNode).to(enNode));
            nowSearchType = 1;
        } else if (id == R.id.transit) {
            mSearch.transitSearch((new TransitRoutePlanOption())
                    .from(stNode).city(city).to(enNode));
            nowSearchType = 2;
        } else if (id == R.id.walk) {
            mSearch.walkingSearch((new WalkingRoutePlanOption())
                    .from(stNode).to(enNode));
            nowSearchType = 3;
        } else if (id == R.id.bike) {
            mSearch.bikingSearch((new BikingRoutePlanOption())
                    .from(stNode).to(enNode));
            nowSearchType = 4;
        }
    }

    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = null;

        if (nowSearchType != 0 && nowSearchType != -1) {
            // 非跨城综合交通
            if (route == null || route.getAllStep() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.pre) {
                return;
            }
            // 设置节点索引
            if (v.getId() == R.id.next) {
                if (nodeIndex < route.getAllStep().size() - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.pre) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            // 获取节结果信息
            step = route.getAllStep().get(nodeIndex);
            if (step instanceof DrivingRouteLine.DrivingStep) {
                nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrance().getLocation();
                nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
            } else if (step instanceof WalkingRouteLine.WalkingStep) {
                nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrance().getLocation();
                nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
            } else if (step instanceof TransitRouteLine.TransitStep) {
                nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrance().getLocation();
                nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
            } else if (step instanceof BikingRouteLine.BikingStep) {
                nodeLocation = ((BikingRouteLine.BikingStep) step).getEntrance().getLocation();
                nodeTitle = ((BikingRouteLine.BikingStep) step).getInstructions();
            }
        } else if (nowSearchType == 0) {
            // 跨城综合交通  综合跨城公交的结果判断方式不一样

            if (massRoute == null || massRoute.getNewSteps() == null) {
                return;
            }
            if (nodeIndex == -1 && v.getId() == R.id.pre) {
                return;
            }
            boolean isSamecity = nowResultMass.getOrigin().getCityId() == nowResultMass.getDestination().getCityId();
            int size = 0;
            if (isSamecity) {
                size = massRoute.getNewSteps().size();
            } else {
                for (int i = 0; i < massRoute.getNewSteps().size(); i++) {
                    size += massRoute.getNewSteps().get(i).size();
                }
            }

            // 设置节点索引
            if (v.getId() == R.id.next) {
                if (nodeIndex < size - 1) {
                    nodeIndex++;
                } else {
                    return;
                }
            } else if (v.getId() == R.id.pre) {
                if (nodeIndex > 0) {
                    nodeIndex--;
                } else {
                    return;
                }
            }
            if (isSamecity) {
                // 同城
                step = massRoute.getNewSteps().get(nodeIndex).get(0);
            } else {
                // 跨城
                int num = 0;
                for (int j = 0; j < massRoute.getNewSteps().size(); j++) {
                    num += massRoute.getNewSteps().get(j).size();
                    if (nodeIndex - num < 0) {
                        int k = massRoute.getNewSteps().get(j).size() + nodeIndex - num;
                        step = massRoute.getNewSteps().get(j).get(k);
                        break;
                    }
                }
            }
            if (step != null) {
                nodeLocation = ((MassTransitRouteLine.TransitStep) step).getStartLocation();
                nodeTitle = ((MassTransitRouteLine.TransitStep) step).getInstructions();
            }
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        // 移动节点至中心
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        TextView popupText = new TextView(this);
        popupText.setBackgroundResource(R.color.black);
        popupText.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        popupText.setPadding(10, 10, 10, 10);
        popupText.setText(nodeTitle);
        mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        dialog.dismiss();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            result.getSuggestAddrInfo();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);

            if (result.getRouteLines().size() > 1) {
                nowResultWalk = result;

                MyTransitDlg myTransitDlg = new MyTransitDlg(this,
                        result.getRouteLines(),
                        WALKING_ROUTE);
                myTransitDlg.setOnItemInDlgClickListener(position -> {
                    route = nowResultWalk.getRouteLines().get(position);
                    WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(nowResultWalk.getRouteLines().get(position));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                });
                myTransitDlg.show();

            } else if (result.getRouteLines().size() == 1) {
                // 直接显示
                route = result.getRouteLines().get(0);
                WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        dialog.dismiss();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            result.getSuggestAddrInfo();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            if (result.getRouteLines().size() > 1) {
                nowResultTransit = result;

                MyTransitDlg myTransitDlg = new MyTransitDlg(this,
                        result.getRouteLines(),
                        TRANSIT_ROUTE);
                myTransitDlg.setOnItemInDlgClickListener(position -> {
                    route = nowResultTransit.getRouteLines().get(position);
                    TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(nowResultTransit.getRouteLines().get(position));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                });
                myTransitDlg.show();
            } else if (result.getRouteLines().size() == 1) {
                // 直接显示
                route = result.getRouteLines().get(0);
                TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }
    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
        dialog.dismiss();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点模糊，获取建议列表
            result.getSuggestAddrInfo();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nowResultMass = result;

            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            // 列表选择
            MyTransitDlg myTransitDlg = new MyTransitDlg(this,
                    result.getRouteLines(),
                    Type.MASS_TRANSIT_ROUTE);
            nowResultMass = result;
            myTransitDlg.setOnItemInDlgClickListener(position -> {
                MyMassTransitRouteOverlay overlay = new MyMassTransitRouteOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                massRoute = nowResultMass.getRouteLines().get(position);
                overlay.setData(nowResultMass.getRouteLines().get(position));

                MassTransitRouteLine line = nowResultMass.getRouteLines().get(position);
                overlay.setData(line);
                if (nowResultMass.getOrigin().getCityId() == nowResultMass.getDestination().getCityId()) {
                    // 同城
                    overlay.setSameCity(true);
                } else {
                    // 跨城
                    overlay.setSameCity(false);
                }
                overlay.addToMap();
                overlay.zoomToSpan();
            });
            myTransitDlg.show();
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        dialog.dismiss();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            result.getSuggestAddrInfo();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            if (result.getRouteLines().size() > 1) {
                nowResultDrive = result;

                MyTransitDlg myTransitDlg = new MyTransitDlg(this,
                        result.getRouteLines(),
                        DRIVING_ROUTE);
                myTransitDlg.setOnItemInDlgClickListener(position -> {
                    route = nowResultDrive.getRouteLines().get(position);
                    DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(nowResultDrive.getRouteLines().get(position));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                });
                myTransitDlg.show();

            } else if (result.getRouteLines().size() == 1) {
                route = result.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
        dialog.dismiss();
    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {
        dialog.dismiss();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            result.getSuggestAddrInfo();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            if (result.getRouteLines().size() > 1) {
                nowResultBike = result;

                MyTransitDlg myTransitDlg = new MyTransitDlg(this,
                        result.getRouteLines(),
                        DRIVING_ROUTE);
                myTransitDlg.setOnItemInDlgClickListener(position -> {
                    route = nowResultBike.getRouteLines().get(position);
                    BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaiduMap);
                    mBaiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(nowResultBike.getRouteLines().get(position));
                    overlay.addToMap();
                    overlay.zoomToSpan();
                });
                myTransitDlg.show();

            } else if (result.getRouteLines().size() == 1) {
                route = result.getRouteLines().get(0);
                BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
            }
        }
    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return null;
        }
    }

    private class MyBikingRouteOverlay extends BikingRouteOverlay {
        MyBikingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return null;
        }
    }

    private class MyMassTransitRouteOverlay extends MassTransitRouteOverlay {
        MyMassTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            return null;
        }


    }

    @Override
    public void onMapClick(LatLng point) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }

    // 响应DLg中的List item 点击
    interface OnItemInDlgClickListener {
        void onItemClick(int position);
    }

    // 供路线选择的Dialog
    class MyTransitDlg extends Dialog {

        private List<? extends RouteLine> mTransitRouteLines;
        private ListView transitRouteList;
        private RouteLineAdapter mTransitAdapter;

        OnItemInDlgClickListener onItemInDlgClickListener;

        MyTransitDlg(Context context, int theme) {
            super(context, theme);
        }

        MyTransitDlg(Context context, List<? extends RouteLine> transitRouteLines, Type
                type) {
            this(context, 0);
            mTransitRouteLines = transitRouteLines;
            mTransitAdapter = new RouteLineAdapter(context, mTransitRouteLines, type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transit_dialog);

            transitRouteList = (ListView) findViewById(R.id.transitList);
            transitRouteList.setAdapter(mTransitAdapter);

            transitRouteList.setOnItemClickListener((parent, view, position, id) -> {
                onItemInDlgClickListener.onItemClick(position);
                mBtnPre.setVisibility(View.VISIBLE);
                mBtnNext.setVisibility(View.VISIBLE);
                dismiss();

            });
        }

        void setOnItemInDlgClickListener(OnItemInDlgClickListener itemListener) {
            onItemInDlgClickListener = itemListener;
        }

    }

    class RouteLineAdapter extends BaseAdapter {

        private List<? extends RouteLine> routeLines;
        private LayoutInflater layoutInflater;
        private Type mType;

        RouteLineAdapter(Context context, List<? extends RouteLine> routeLines, Type type) {
            this.routeLines = routeLines;
            layoutInflater = LayoutInflater.from(context);
            mType = type;
        }

        @Override
        public int getCount() {
            return routeLines.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NodeViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.activity_transit_item, null);
                holder = new NodeViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.transitName);
                holder.lightNum = (TextView) convertView.findViewById(R.id.lightNum);
                holder.dis = (TextView) convertView.findViewById(R.id.dis);
                convertView.setTag(holder);
            } else {
                holder = (NodeViewHolder) convertView.getTag();
            }

            switch (mType) {
                case TRANSIT_ROUTE:
                case WALKING_ROUTE:
                case BIKING_ROUTE:
                    holder.name.setText("路线" + (position + 1));
                    int time = routeLines.get(position).getDuration();
                    if (time / 3600 == 0) {
                        holder.lightNum.setText("大约需要：" + time / 60 + "分钟");
                    } else {
                        holder.lightNum.setText("大约需要：" + time / 3600 + "小时" + (time % 3600) / 60 + "分钟");
                    }
                    holder.dis.setText("距离大约是：" + routeLines.get(position).getDistance() + "米");
                    break;

                case DRIVING_ROUTE:
                    DrivingRouteLine drivingRouteLine = (DrivingRouteLine) routeLines.get(position);
                    holder.name.setText("线路" + (position + 1));
                    holder.lightNum.setText("红绿灯数：" + drivingRouteLine.getLightNum());
                    holder.dis.setText("拥堵距离为：" + drivingRouteLine.getCongestionDistance() + "米");
                    break;
                case MASS_TRANSIT_ROUTE:
                    MassTransitRouteLine massTransitRouteLine = (MassTransitRouteLine) routeLines.get(position);
                    holder.name.setText("线路" + (position + 1));
                    holder.lightNum.setText("预计达到时间：" + massTransitRouteLine.getArriveTime());
                    holder.dis.setText("总票价：" + massTransitRouteLine.getPrice() + "元");
                    break;

                default:
                    break;
            }

            return convertView;
        }

        private class NodeViewHolder {

            private TextView name;
            private TextView lightNum;
            private TextView dis;
        }
    }
}
