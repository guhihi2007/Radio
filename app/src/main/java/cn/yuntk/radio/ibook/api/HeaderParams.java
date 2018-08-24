package cn.yuntk.radio.ibook.api;

import android.content.Context;

import cn.yuntk.radio.BuildConfig;
import cn.yuntk.radio.ibook.XApplication;
import cn.yuntk.radio.ibook.common.Constants;
import cn.yuntk.radio.ibook.util.PackageUtils;
import cn.yuntk.radio.ibook.util.TelephonyUtils;

public class HeaderParams {
    public String uuid;
    String imei;
    String version;
    int version_code;
    String os_version;
    String device;//手机品牌
    String metrics;
    String channel;
    Long memory_size;//内存大小
    String install_time;//安装时间
    String project;
    boolean hasSIM; //有无SIM卡
    String brand;//手机品牌


    public HeaderParams() {
        Context application = XApplication.getsInstance();
        version = PackageUtils.getVersionName(application);
        version_code = PackageUtils.getVersionCode(application);
        os_version = TelephonyUtils.getBuildLevel() + "";
        device = TelephonyUtils.getPhoneUser();
        metrics = Constants.width + "*" + Constants.height;
        channel = BuildConfig.FLAVOR.substring(BuildConfig.FLAVOR.indexOf("_"), BuildConfig.FLAVOR.length());//g
        memory_size = TelephonyUtils.getAvailablMemorySize();
        install_time = String.valueOf(System.currentTimeMillis());
//        project = BuildConfig.APPLICATION_ID;
        project = "cn.yuntk.radio";
        hasSIM = TelephonyUtils.hasSIMCard(application);
        brand = TelephonyUtils.getPhoneBrand();
    }
}