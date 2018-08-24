package cn.yuntk.radio.ibook.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.io.File;
import java.lang.reflect.Method;

public class TelephonyUtils {
    private static TelephonyManager tm = null;

    private static TelephonyUtils util = new TelephonyUtils();

    private static Context mContext;

    private TelephonyUtils() {
    }

    public static TelephonyUtils init(Context context) {
        if (mContext == null) {
            mContext = context;

            tm = (TelephonyManager)
                    mContext.getSystemService(Context.TELEPHONY_SERVICE);
        }
        return util;
    }

    @SuppressLint("MissingPermission")
    public static String getDeviceId() {
        return tm.getDeviceId();
    }

    /**
     * 获取Telephony ID
     *
     * @param context
     * @return
     */
    public static String getTelephonyId(Context context) {
        String telephonyId = null;

        TelephonyManager tm =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String tmDevice, tmSerial, androidId;

        // 获取设备ID
        tmDevice = tm.getDeviceId();
        if (tmDevice == null) {
            tmDevice = "";
        }

        // 获取SIM序列号
        tmSerial = tm.getSimSerialNumber();
        if (tmSerial == null || "".equals(tmSerial)) {
            tmSerial = System.currentTimeMillis() + "";
        }
        // 获取AndroidID
        androidId = android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        telephonyId = tmSerial + androidId + tmDevice.hashCode();

        if (StringUtils.isBlank(telephonyId)) {
            telephonyId = System.currentTimeMillis() + "";
        } else if (telephonyId.length() > 24) {
            telephonyId = telephonyId.substring(0, 24);
        }

        return telephonyId;
    }

    ///获取IMEI
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
        return imei;
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAndroidVersion(Context context) {
        return "[手机品牌：" + Build.BRAND + "],[手机型号：" + Build.MODEL + "],[AndroidSdkVersion:"
                + Build.VERSION.SDK + "],[AndroidOSVersion:"
                + Build.VERSION.RELEASE + "]";
    }

    @SuppressLint("MissingPermission")
    public String getPhoneInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
        sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
        sb.append("\nLine1Number = " + tm.getLine1Number());
        sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
        sb.append("\nNetworkType = " + tm.getNetworkType());
        sb.append("\nPhoneType = " + tm.getPhoneType());
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        sb.append("\nSimOperator = " + tm.getSimOperator());
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        sb.append("\nSimState = " + tm.getSimState());
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());

        return sb.toString();
    }

    //设备序列号
    public static String getSerialNumber() {

        String serial = null;

        try {

            Class<?> c = Class.forName("android.os.SystemProperties");

            Method get = c.getMethod("get", String.class);

            serial = (String) get.invoke(c, "ro.serialno");

        } catch (Exception e) {

            e.printStackTrace();
        }
        return serial;
    }


    /**
     * 获取设备的唯一标识，deviceId
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = null;
        try {
            deviceId = tm.getDeviceId();
        } catch(Exception e){

        }
        if (deviceId == null) {
            return null;
        } else {
            return deviceId;
        }
    }


    /**
     * 获取手机品牌
     *
     * @return
     */
    public static String getPhoneBrand() {
        return Build.BRAND;
    }

    /**
     * 获取设备用户名称
     */
    public static String getPhoneUser() {
        return Build.USER;
    }

    /**
     * 获取手机型号
     *
     * @return
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机Android API等级（22、23 ...）
     *
     * @return
     */
    public static int getBuildLevel() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机Android 版本（4.4、5.0、5.1 ...）
     *
     * @return
     */
    public static String getBuildVersion() {
        return Build.VERSION.RELEASE;
    }
    /**判断手机 平板*/
    public static Integer isTablet(Context context) {
        Integer isTabletCode = 0;
        if ((context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            isTabletCode = 20002;
        }else{
            isTabletCode = 20001;
        }
        return isTabletCode;
    }

    //	获取屏幕宽高、
    public static void getDisplayMetricsInfo(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        int screenWidth = dm.widthPixels;//像素宽

        int screenHeigh = dm.heightPixels;//像素高

        LogUtils.showLog("screenWidth:" + screenWidth + ":screenHeigh:" + screenHeigh);

    }
    /**
     * 获得状态栏的高度(无关 android:theme 获取状态栏高度)
     * @param mContext
     * @return
     */
    public static int getStatusHeight(Context mContext) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            return mContext.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
        }
        return -1;
    }

    /***
     * 判断是否有SIM卡
     *
     * @return
     */
    public static boolean hasSIMCard(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        int state = mTelephonyManager.getSimState();
        if (TelephonyManager.SIM_STATE_READY == state) {
            return true;
        }
        return false;
    }


    /**
     * SDCARD是否存
     */
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /***
     * 获取可用存储空间
     * 返回单位：MB
     *
     * @return
     */
    public static long getAvailablMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize / (1048 * 1024);
        } else {
            return getAvailableInternalMemorySize();
        }
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize / (1048 * 1024);
    }

}