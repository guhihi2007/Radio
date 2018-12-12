package cn.yuntk.radio.ibook.api;

import android.content.Context;


import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.common.TingConstants;
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
        Context application = XApplication.getInstance();
        version = PackageUtils.getVersionName(application);
        version_code = PackageUtils.getVersionCode(application);
        os_version = TelephonyUtils.getBuildLevel()+"";
        device = TelephonyUtils.getPhoneUser();
        metrics = TingConstants.width + "*" + TingConstants.height ;
//        channel = BuildConfig.FLAVOR;
//        if (channel.startsWith("_")) {
//            channel = channel.substring(1, channel.length());
//        }
        memory_size = TelephonyUtils.getAvailablMemorySize();
        install_time = String.valueOf(System.currentTimeMillis());
        project = "BookReader";
        hasSIM = TelephonyUtils.hasSIMCard(application);
        brand = TelephonyUtils.getPhoneBrand();
    }
}