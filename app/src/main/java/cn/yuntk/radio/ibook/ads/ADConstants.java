package cn.yuntk.radio.ibook.ads;


import cn.yuntk.radio.BuildConfig;

/**
 * Created by Gpp on 2018/1/17.
 */

public class ADConstants {

    /************************广点通*************************/
//    public static final String GDT_APP_KEY = "1107514091"; //GDT广告appID
//    public static final String GDT_INSERT_ID = "6030233727068589";//插屏
//    public static final String GDT_SPLASH_ID = "7080634777460578";//开屏
//    public static final String GDT_BANNER_ID = "3000335737867567";//横幅
//    public static final String GDT_NATIVE_ID = "2030839797162516";//原生广告
    public static final String GDT_APP_KEY = BuildConfig.GDT_APP_KEY; //GDT广告appID
    public static final String GDT_INSERT_ID = BuildConfig.GDT_INSERT_ID;//插屏
    public static final String GDT_SPLASH_ID = BuildConfig.GDT_SPLASH_ID;//开屏
    public static final String GDT_BANNER_ID =BuildConfig.GDT_BANNER_ID;//横幅
    public static final String GDT_NATIVE_ID = BuildConfig.GDT_NATIVE_AD;//原生广告
    /************************广点通*************************/

    /************************Google广告*************************/
//    public static final String AD_GOOGLE_APPID = "ca-app-pub-2144172051563531~4754476693";
//    public static final String AD_GOOGLE_BANNER = "ca-app-pub-2144172051563531/7705015613";
//    public static final String AD_GOOGLE_INSERT = "ca-app-pub-2144172051563531/1877973860";
//    public static final String AD_GOOGLE_REWARD = "ca-app-pub-2144172051563531/5437944924";
//    public static final String AD_GOOGLE_NATIVE = "ca-app-pub-2144172051563531/6234979328";
    public static final String AD_GOOGLE_APPID = BuildConfig.AD_GOOGLE_APPID;
    public static final String AD_GOOGLE_BANNER = BuildConfig.AD_GOOGLE_BANNER;
    public static final String AD_GOOGLE_INSERT = BuildConfig.AD_GOOGLE_INSERT;
    public static final String AD_GOOGLE_REWARD = "ca-app-pub-2144172051563531/5437944924";
    public static final String AD_GOOGLE_NATIVE = BuildConfig.GDT_NATIVE_AD;
    /************************Google广告*************************/

    /************************页面命名 存储广告显示配置*************************/
    public static final String MUSIC_DETAIL = "music_detail";//详情页面
    public static final String HOME_PAGE_NEW = "home_page_new";//主页插屏
    public static final String HOME_PAGE_LIST1 = "home_page_list1";//主页列表1
    public static final String HOME_PAGE_LIST2 = "home_page_list2";//主页列表2
    public static final String HOME_PAGE_LIST3 = "home_page_list3";//主页列表3
    public static final String HOME_PAGE_LIST4 = "home_page_list4";//主页列表4

    public static final String CATEGORY_PAGE = "category_page";//分类页面
    public static final String LISTENING_PAGE = "listening_page";//播放页面
    public static final String START_PAGE = "start_page";
    /************************页面命名*************************/

    /************************开屏广告*************************/
    public static final String AD_APP_LOAD_TIME = "ad_app_load_time";//App启动时间
    public static final String AD_APP_BACKGROUND_TIME = "ad_app_background_time";//App退到后台时间
    public static final String AD_SPREAD_PERIOD = "ad_spread_period";//开屏后台设置的时间间隔
    public static final String AD_SPLASH_STATUS = "ad_splash_status";//开屏开关
    /************************开屏广告*************************/


    /************************插屏广告*************************/
    public static final String AD_INSERT_SHOW_PERIOD = "ad_insert_change_period";//插屏广告显示间隔
    public static final String AD_INSERT_LAST_SHOW = "ad_insert_last_origin";//插屏广告上展示时间
    /************************插屏广告*************************/
    /**
     * 是否开启了页面banner定时器
     * */
    public static final String AD_BANNER_IS_TIMER= "ad_banner_is_timer";

    public static final String AD_BANNER_LAST_CHANGE = "AD_BANNER_LAST_CHANGE";

    /************************原生广告*************************/
    public static final String AD_NATIVE_SHOW_PERIOD = "ad_native_change_period";//原生广告显示间隔
    public static final String AD_NATIVE_LAST_SHOW = "ad_native_last_origin";//原生广告上ci展示时间
    /************************原生广告*************************/

    /************************是否购买免广告*************************/
    public static final String AD_VIP = "ad_vip";
    public static final String AD_BANNER_NEVER_SHOW = "ad_banner_never_show";
    /************************进入阅读页时，网络状态*************************/

}
