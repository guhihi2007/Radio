package cn.yuntk.radio.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;
import java.util.Locale;

import cn.yuntk.radio.base.Notification;
import cn.yuntk.radio.ui.activity.FMActivity;


/**
 * 获取位置信息的类
 */
public class LocationUtil {
    private Context context;
    private static LocationUtil locationUtil;
    private LocationManager locationManager;
    private List<String> locationMode;//当前具有哪些定位方式
    private Location location;
    private double lat;//维度
    private double lng;//经度
    private String addressLine = "";
    private PriorityMode priorityMode;
    private FMActivity fmActivity;

    private LocationUtil(Context context){
        this.context=context;
        if (context instanceof FMActivity){
            fmActivity=(FMActivity) context;
        }
        init();
    }
    public static synchronized LocationUtil getInstance(Context context){
        return getInstance(context,PriorityMode.GPS);
    }
    public static synchronized LocationUtil getInstance(Context context,PriorityMode priorityMode){
        if (locationUtil==null){
            locationUtil=new LocationUtil(context);
        }
        locationUtil.priorityMode=priorityMode;
        return locationUtil;
    }
    private void init(){
        locationManager=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        locationMode=locationManager.getProviders(true);
    }
    public enum PriorityMode{
        GPS,NETWORK
    }
    public void setPriorityMode(PriorityMode priorityMode){
        this.priorityMode=priorityMode;
    }
    /**
     *
     * @return
     */
    public String getAddressLine(){
        List<Address> result = null;
        try {
            Geocoder gc = new Geocoder(context, Locale.getDefault());
            result = gc.getFromLocation(lat,
                    lng, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(result != null && result.size()>0){
            addressLine=result.get(0).getAddressLine(0);
        }
        return addressLine;
    }
    public String getProvince(){
        String province="";
        if (addressLine.equals("")||addressLine==null){
            getAddressLine();
        }
        if (addressLine.contains("省"))
            province=addressLine.split("省")[0];
        else if (addressLine.contains("市"))
            province=addressLine.split("市")[0];
        return province;
    }

    private LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (locationUtil!=null)
                locationUtil.location=location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    public void removeUpdates(){
        if (locationListener!=null){
            locationManager.removeUpdates(locationListener);
        }
    }

    public void checkPermission(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            sendRequest();
            fmActivity.myNoification(LocationUtil.class.getSimpleName(),"no");
        }else {
            if (locationMode.contains(LocationManager.GPS_PROVIDER)&&priorityMode==PriorityMode.GPS){
                location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }else if (locationMode.size()>0&&priorityMode==PriorityMode.NETWORK){
                location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (location!=null) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }else {
                locationManager.requestLocationUpdates(priorityMode==PriorityMode.GPS ? LocationManager.GPS_PROVIDER:LocationManager.NETWORK_PROVIDER,2000,10,locationListener);
            }
            fmActivity.myNoification(LocationUtil.class.getSimpleName(),"ok");
        }
    }
    private int REQUEST_PERMISSION_CODE=33;
    private void sendRequest(){
        AndPermission.with(context).requestCode(REQUEST_PERMISSION_CODE).permission(
                Manifest.permission.ACCESS_COARSE_LOCATION)
                .callback(permissionListener).start();
    }
    private PermissionListener permissionListener=new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            if(requestCode==REQUEST_PERMISSION_CODE) {
                checkPermission();
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            //没有定位权限，无法获取位置信息
            fmActivity.myNoification(LocationUtil.class.getSimpleName(),"no");
        }
    };
}
