package cn.yuntk.radio.api;

import android.content.Context;

import cn.yuntk.radio.BuildConfig;
import cn.yuntk.radio.Constants;
import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.utils.PackageUtils;
import cn.yuntk.radio.utils.TelephonyUtils;


public class HeaderParams {
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
        Context application = XApplication.getInstance();
        version = PackageUtils.getVersionName(application);
        version_code = PackageUtils.getVersionCode(application);
        os_version = TelephonyUtils.getBuildLevel() + "";
        device = TelephonyUtils.getPhoneUser();
        metrics = Constants.INSTANCE.getWidth() + "*" + Constants.INSTANCE.getHeight();
        channel = BuildConfig.FLAVOR.substring(BuildConfig.FLAVOR.indexOf("_"), BuildConfig.FLAVOR.length());
        memory_size = TelephonyUtils.getAvailableMemorySize();
        install_time = String.valueOf(System.currentTimeMillis());
        project = BuildConfig.APPLICATION_ID;
        hasSIM = TelephonyUtils.hasSIMCard(application);
        brand = TelephonyUtils.getPhoneBrand();
    }
}