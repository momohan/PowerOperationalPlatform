package com.handu.poweroperational.main.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.handu.poweroperational.main.application.PowerOperationalApplicationLike;
import com.handu.poweroperational.utils.AppLogger;
import com.handu.poweroperational.utils.Tools;

public class LocationUploadService extends Service {

    private LocationService locationService;
    private double latitude;//纬度
    private double longitude;//经度

    @Override
    public void onCreate() {
        super.onCreate();
        //获取locationService实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，
        //都是通过此种方式获取locationService实例的
        locationService = PowerOperationalApplicationLike.locationService;
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.registerListener(mListener);
    }

    @Override
    public void onDestroy() {
        locationService.unregisterListener(mListener); //注销定位掉监听
        locationService.stop(); //停止定位服务
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppLogger.e("LocationUploadService：onStartCommand");
        //启动定位服务
        locationService.start();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 定位结果回调，重写onReceiveLocation方法
     */
    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            getLocationInfo(bdLocation);
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
            AppLogger.e("连接成功");
        }
    };

    /**
     * 获取定位详细信息
     *
     * @param location
     */
    private void getLocationInfo(BDLocation location) {

        if (null != location && location.getLocType() != BDLocation.TypeServerError) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            PowerOperationalApplicationLike.location = location;
            StringBuilder sb = new StringBuilder(256);
            sb.append("时间 : ");
            /**
             * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
             * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
             */
            sb.append(location.getTime());
            sb.append("\n定位类型 : ");// *****定位类型说明*****
            sb.append(location.getLocTypeDescription());
            sb.append("\n纬度 : ");// 纬度
            sb.append(location.getLatitude());
            sb.append("\n经度 : ");// 经度
            sb.append(location.getLongitude());
            sb.append("\n半径 : ");// 半径
            sb.append(location.getRadius());
            sb.append("\n国家码 : ");// 国家码
            sb.append(location.getCountryCode());
            sb.append("\n国家名称 : ");// 国家名称
            sb.append(location.getCountry());
            sb.append("\n城市编码 : ");// 城市编码
            sb.append(location.getCityCode());
            sb.append("\n城市 : ");// 城市
            sb.append(location.getCity());
            sb.append("\n区 : ");// 区
            sb.append(location.getDistrict());
            sb.append("\n街道 : ");// 街道
            sb.append(location.getStreet());
            sb.append("\n地址信息 : ");// 地址信息
            sb.append(location.getAddrStr());
            sb.append("\n用户室内外判断结果: ");// *****返回用户室内外判断结果*****
            sb.append(location.getUserIndoorState());
            sb.append("\n方向: ");
            sb.append(location.getDirection());// 方向
            sb.append("\n位置语义化信息: ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            sb.append("\nPOI信息: ");// POI信息
            if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                for (int i = 0; i < location.getPoiList().size(); i++) {
                    Poi poi = location.getPoiList().get(i);
                    sb.append(poi.getName()).append(";");
                }
            }
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\n速度 : ");
                sb.append(location.getSpeed());// 速度 单位：km/h
                sb.append("\n卫星数目 : ");
                sb.append(location.getSatelliteNumber());// 卫星数目
                sb.append("\n海拔高度 : ");
                sb.append(location.getAltitude());// 海拔高度 单位：米
                sb.append("\ngps质量判断 : ");
                sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                sb.append("\ngps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                // 运营商信息
                if (location.hasAltitude()) {// *****如果有海拔高度*****
                    sb.append("\n海拔 : ");
                    sb.append(location.getAltitude());// 单位：米
                }
                sb.append("\n运营商信息 : ");// 运营商信息
                sb.append(location.getOperators());
                sb.append("\n网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\n离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\n服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                Tools.toastError("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\n网络不同导致定位失败，请检查网络是否通畅");
                Tools.toastError("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\n无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                Tools.toastError("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            AppLogger.e("\n经纬度：" + location.getLongitude() + "，" + location.getLatitude() + "\n" + location.getAddrStr() + "，" + location.getLocationDescribe());
//            AppLogger.e(sb.toString());
        }
    }
}
