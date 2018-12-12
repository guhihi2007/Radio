package cn.yuntk.radio.ad;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.yuntk.radio.Constants;
import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ui.activity.SplashActivity;
import cn.yuntk.radio.utils.GsonUtils;
import cn.yuntk.radio.utils.LogUtils;
import cn.yuntk.radio.utils.NetworkUtils;
import cn.yuntk.radio.utils.SPUtil;


/**
 * Created by Gpp on 2018/1/22.
 */

public class AdManager<T> implements OnInsertADListener, AdReportListener {
    public static final String TAG = "controller";
    private Activity activity;
    private String page;
    private ImageView logo;
    private ImageView splashHolder;
    private FrameLayout container;
    private TextView skipView;
    //banner切换scheduledExecutorService timerTask
    private ScheduledExecutorService scheduledExecutorService;
    private ExecutorService pool;
    private TypeBean pageBean;
    private AbsADParent adView;
    private boolean isLoading;
    private long insertPages;
    public static boolean needShowNative;
    private boolean isRunning = true;
    private static long lastExecuted;
    private int retryCounts;
    private HashMap<AD.AdType, List<OriginBean>> map;
    private FrameLayout nativeAdLayout;

    AdManager(Activity activity, String page, FrameLayout container, ImageView logo,
              TextView skipView, boolean isLoading, FrameLayout nativeAdLayout, ImageView splashHolder
    ) {
        this.activity = activity;
        this.page = page;
        this.container = container;
        this.logo = logo;
        this.splashHolder = splashHolder;
        this.skipView = skipView;
        this.isLoading = isLoading;
        this.nativeAdLayout = nativeAdLayout;
        pool = Executors.newFixedThreadPool(1);
    }


    public void show() {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                pageBean = GsonUtils.parseObject(SPUtil.getInstance().getString(page), TypeBean.class);

                LogUtils.e(TAG, "pool run pageBean==" + SPUtil.getInstance().getString(page));
                showByType();
            }
        });
    }


    private void showByType() {
        //获取当前页面要展示的广告类型和源
        map = getShowTypeAndOrigin();

        if (map.size() == 0 && page.equals(Constants.START_PAGE)) {


            LogUtils.e(TAG, "START_PAGE无配置数据");

            goHome();

            return;

        }

        for (AD.AdType type : map.keySet()) {

            List<OriginBean> originList = map.get(type);

            LogUtils.e(TAG, "for---->" + type);

            changeAdByTime(type, originList);

        }


    }

    private void changeAdByTime(final AD.AdType type, final List<OriginBean> originList) {


        switch (type) {

            case BANNER:

                executeBanner(type, originList);

                break;
            case INSET:

                executeInsert(type, originList);

                break;
            case SPLASH:

                if (!pageBean.getSpread_screen().getStatus() ||
                        !NetworkUtils.isConnected(XApplication.getInstance())) {

                    LogUtils.e(TAG, "SPLASH-----开关关闭,或者无网络不执行");

                    goHome();

                    return;

                }


                showByOrigin(getAdOriginByPercent(originList), type);


                break;

            case NATIVE:

                LogUtils.e(TAG, "executeNative---->" + type);

                executeNative(type, originList);

                break;
        }
    }

    private void executeNative(AD.AdType type, List<OriginBean> originList) {

        if (!pageBean.getNative_advertising().getStatus() ||
                !NetworkUtils.isConnected(XApplication.getInstance())) {

            LogUtils.e(TAG, "Native--" + page + "---开关关闭，或者无网络不执行");

            return;
        }
        long period = SPUtil.getInstance().getLong(page + Constants.AD_NATIVE_PERIOD);
        long time = System.currentTimeMillis();
        long last = SPUtil.getInstance().getLong(page + Constants.AD_NATIVE_PERIOD);
        long offset = time - last;
        LogUtils.i(TAG, "页面:" + page + ",当前时间(" + getDate(time) + ")-上次展示时间(" + getDate(last) + "):" + time + "-" + last + "=" + offset);
        LogUtils.i(TAG, "页面:" + page + ",读取广告周期:" + period + "-" + offset + "=" + (period - offset));
        LogUtils.i(TAG, "页面:" + page + ",需要显示吗:" + (time - last >= period));

        if (time - last >= period) {
            LogUtils.e(TAG, "INSET---时间到显示");
            showByOrigin(getAdOriginByPercent(originList), type);
        } else {
            LogUtils.e(TAG, "INSET---时间未到不显示");
        }
    }

    private void executeInsert(AD.AdType type, List<OriginBean> originList) {
        if (!pageBean.getInsert_screen().getStatus() ||
                !NetworkUtils.isConnected(XApplication.getInstance())) {

            LogUtils.e(TAG, "INSET--" + page + "---开关关闭，或者无网络不执行");

            return;
        }


        //阅读页面不继续，根据回调的页数展示插屏广告
//        if (Constants.LISTENING_PAGE.equals(page)) {
//
//            LogUtils.e(TAG, "INSET---LISTENING_PAGE");
//            if (activity instanceof ReadActivityNew) {
//                ((ReadActivityNew) activity).setOnInsertADListener(this);
//            }
//            //配置了第0页展示,即进入立即展示
//            if (pageBean.getInsert_screen().getTimes() == 0) {
//
//                //阅读页banner同时加载会不出广告，延时500ms
//
//
//                ComicApplication.getMainThreadHandler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//                        showByOrigin(getAdOriginByPercent(originList), type);
//
//                    }
//                }, 500);
//
//
//            }
//            return;
//
//        }
        long period = SPUtil.getInstance().getLong(page + Constants.AD_INSERT_SHOW_PERIOD);
        long time = System.currentTimeMillis();
        long last = SPUtil.getInstance().getLong(page + Constants.AD_INSERT_LAST_SHOW);
        long offset = time - last;
        LogUtils.i(TAG, "页面:" + page + ",当前时间(" + getDate(time) + ")-上次展示时间(" + getDate(last) + "):" + time + "-" + last + "=" + offset);
        LogUtils.i(TAG, "页面:" + page + ",读取插屏广告周期:" + period + "-" + offset + "=" + (period - offset));
        LogUtils.i(TAG, "页面:" + page + ",需要显示吗:" + (time - last >= period));

        if (time - last >= period) {

            LogUtils.e(TAG, "INSET---时间到显示");

            showByOrigin(getAdOriginByPercent(originList), type);

        } else {
            //记录此次显示时间
            SPUtil.getInstance().putLong(page + Constants.AD_INSERT_LAST_SHOW, System.currentTimeMillis());

            LogUtils.e(TAG, "INSET---时间未到不显示");

        }
    }

    private String getDate(long time) {
        return new SimpleDateFormat("HH:mm:ss", Locale.CHINA).format(time);
    }

    private void goHome() {

        if (isLoading) {

            LogUtils.i(TAG, "From BackGround");

            activity.finish();

        } else {

            LogUtils.e(TAG, "From Start");

            XApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((SplashActivity) activity).checkIn();
                }
            }, 2000);

        }
    }

    private void executeBanner(final AD.AdType type, final List<OriginBean> originList) {
        if (pageBean.getBanner_screen() != null) {

            if (!NetworkUtils.isConnected(XApplication.getInstance())) {
                LogUtils.e(TAG, "BANNER-----无网络不执行");

                return;
            }
            //开关关闭不执行
            if (!pageBean.getBanner_screen().getStatus()) {

                LogUtils.e(TAG, "BANNER-----开关关闭");

//            ComicApplication.getInstance().setHaveAD(false);

                return;
            }

//        ComicApplication.getInstance().setHaveAD(true);

            scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

//        开启定时切换
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                    XApplication.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//
//                        if (!ComicApplication.isReaderActivityShow && Constants.LISTENING_PAGE.equals(page)) {
//                            //阅读页不可见，不继续
//                            return;
//                        }
//                        if (!ComicApplication.isBookDetailActivityShow && Constants.BOOK_DETAIL.equals(page)) {
//                            //书籍详情页不可见，不继续
//                            return;
//                        }

                            if (isRunning) {

                                LogUtils.e(TAG, "BANNER-----定时切换广告源");

                                SPUtil.getInstance().putLong(page + Constants.AD_BANNER_LAST_CHANGE, System.currentTimeMillis());

                                showByOrigin(getAdOriginByPercent(originList), type);
                            }

                        }
                    }, 0);
                }
            };
            try {
                scheduledExecutorService.scheduleWithFixedDelay(timerTask, 0, pageBean.getBanner_screen().getChange_times() * 1000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {

                e.printStackTrace();
            }

            //如果后台配置了关闭广告时间，执行关闭
            closeBannerByTiming();
        }
    }

    /**
     * 关闭广告时间到了，执行关闭
     */
    private void closeBannerByTiming() {

        if (pageBean.getBanner_screen().getTimes() != 0) {

            long closeTime = pageBean.getBanner_screen().getTimes();

            LogUtils.i(TAG, closeTime + " 秒后关闭Banner广告," + pageBean.getBanner_screen().getChange_times() + "秒切换广告源" + "---" + page + "");

            XApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    isRunning = false;

//                    ComicApplication.getInstance().setHaveAD(false);

                    cancelTask();

                }
            }, closeTime * 1000);
        }
    }


    /**
     * Random 选取广告源
     * 目前只做了2种广告源随机
     */
    private AD.AdOrigin getAdOriginByPercent(List<OriginBean> originList) {

        if (originList.size() == 1) {

            return originList.get(0).getOrigin();

        }

        OriginBean first = originList.get(0);

        OriginBean sec = originList.get(1);

        Random random = new Random();

        int next = random.nextInt(101);


        if (first.getPrecent() < sec.getPrecent()) {

            return next < first.getPrecent() ? first.getOrigin() : sec.getOrigin();

        } else {

            return next <= sec.getPrecent() ? sec.getOrigin() : first.getOrigin();
        }

    }


    //根据广告广告源显示相应的广告类型
    private void showByOrigin(AD.AdOrigin origin, AD.AdType type) {

        switch (origin) {
            case gdt:
                adView = new GDT_AD();
                adView.setSkipVew(skipView);
                LogUtils.i(TAG, "AdManager showByOrigin   广点通-- " + page + " ---" + type);
                break;
            case google:
                LogUtils.i(TAG, "AdManager showByOrigin   Google-- " + page + " ---" + type);
                adView = new Google_AD();
                break;
        }

        adView.setActivity(activity);
        adView.setContainer(container);
        adView.setNativeAdLayout(nativeAdLayout);

//        if (page.equals(Constants.LISTENING_PAGE)) {

//            adView.setNight(ReadSettingManager.getInstance().isNightMode());
//        }
        adView.setPage(page);
        adView.setLogo(logo);
        adView.setSplashHolder(splashHolder);
        adView.setLoading(isLoading);
        adView.setListener(this);
        //有网进入阅读页，后关闭网络，banner会显示无网络图片
        if (!NetworkUtils.isConnected(XApplication.getInstance())) {
            if (null != container) {
                container.setVisibility(View.GONE);
                LogUtils.i(TAG, "关闭网络，不显示banner容器");
            }
            return;
        }

        if (!isRunning && AD.AdType.BANNER.equals(type)) {

            LogUtils.i(TAG, "已经到时间关闭了，不能展示广告~~");

            return;
        }
        adView.showAdView(type);
    }

    //获取当前页面需要显示的广告类型，和广告源，添加到map
    public HashMap<AD.AdType, List<OriginBean>> getShowTypeAndOrigin() {

        HashMap<AD.AdType, List<OriginBean>> map = new HashMap<>();
        //1.先获取页面的广告类型
        StatusBean spreadScreenBean = null;

        StatusBean bannerScreenBean = null;

        StatusBean insertScreenBean = null;

        StatusBean nativeScreenBean = null;
        if (pageBean != null) {
            spreadScreenBean = pageBean.getSpread_screen();

            bannerScreenBean = pageBean.getBanner_screen();

            insertScreenBean = pageBean.getInsert_screen();

            nativeScreenBean = pageBean.getNative_advertising();
        }

        if (spreadScreenBean != null) {
            map.put(AD.AdType.SPLASH, getOriginBean(spreadScreenBean));
            try {
                SPUtil.getInstance().putBoolean(Constants.AD_SPLASH_STATUS, spreadScreenBean.getStatus());
                SPUtil.getInstance().putLong(Constants.AD_SPREAD_PERIOD, spreadScreenBean.getTimes());
            } catch (Exception ignored) {

            }
        }

        if (bannerScreenBean != null) {
            map.put(AD.AdType.BANNER, getOriginBean(bannerScreenBean));
        }

        if (insertScreenBean != null) {
            map.put(AD.AdType.INSET, getOriginBean(insertScreenBean));
            LogUtils.i(TAG, "页面:" + page + ",插屏广告周期:" + insertScreenBean.getTimes() * 1000);
            try {
                SPUtil.getInstance().putLong(page + Constants.AD_INSERT_SHOW_PERIOD, insertScreenBean.getTimes() * 1000);
            } catch (Exception ignored) {

            }
        }

        if (nativeScreenBean != null) {
            map.put(AD.AdType.NATIVE, getOriginBean(nativeScreenBean));
            try {
                SPUtil.getInstance().putLong(Constants.AD_NATIVE_PERIOD, nativeScreenBean.getTimes());
            } catch (Exception ignored) {

            }
        }
        return map;
    }

    //获取广告源对应的percent
    private List<OriginBean> getOriginBean(StatusBean statusBean) {
        List<OriginBean> list = new ArrayList<>();

        String origin = statusBean.getAd_origin();

        String percent = statusBean.getAd_percent();

        String replace = origin.replace("_", "");

        int times = origin.length() - replace.length();

        for (int i = 0; i <= times; i++) {

            OriginBean originBean = new OriginBean();

            int lastIndex = origin.lastIndexOf("_");

            String sub = origin.substring(lastIndex + 1, origin.length());
            try {
                originBean.setOrigin(AD.AdOrigin.valueOf(sub));

            } catch (Exception e) {
                //后台广告配置的值，在定义的AD.AdOrigin找不到对应的
            }

            if (lastIndex > 0) {
                origin = origin.substring(0, lastIndex);
            }
            int lastIndexPercent = percent.lastIndexOf("_");

            String subPercent = percent.substring(lastIndexPercent + 1, percent.length());

            try {
                originBean.setPrecent(Integer.valueOf(subPercent));

            } catch (Exception e) {
                //后台广告配置的值，在定义的AD.AdOrigin找不到对应的

            }
            if (lastIndexPercent > 0) {
                percent = percent.substring(0, lastIndexPercent);
            }

            list.add(originBean);

        }
        return list;
    }


    public void destroy() {

        cancelTask();

        if (adView != null) {

            adView.destroy(AD.AdType.INSET);
            adView.destroy(AD.AdType.NATIVE);
        }

        if (pool != null) {
            pool.shutdown();
            pool = null;
        }

    }

    public void destroy(AD.AdType type) {
        adView.destroy(type);
    }

    private void cancelTask() {

        isRunning = false;
        if (scheduledExecutorService != null) {
            if (!scheduledExecutorService.isShutdown() || !scheduledExecutorService.isTerminated()) {
                scheduledExecutorService.shutdown();
            }
        }
        if (!page.equals(Constants.START_PAGE)) {
            if (adView != null) {

                adView.destroy(AD.AdType.BANNER);
            }

            if (container != null && container.isShown()) {

                container.removeAllViews();

                container.setVisibility(View.GONE);

                container = null;

                LogUtils.i(TAG, "" + page + " 关闭Banner广告");

            }
        }
    }


    @Override
    public void clickNextPage(boolean showNative) {
        ++insertPages;
        LogUtils.e(TAG, "click Next Page ==" + insertPages);
        //Native广告
        if (showNative) {
            needShowNative = true;
            showByOrigin(getAdOriginByPercent(map.get(AD.AdType.NATIVE)), AD.AdType.NATIVE);
        } else {
            needShowNative = false;
            if (adView != null)
                adView.destroy(AD.AdType.NATIVE);
        }

        //插屏广告
        if (null != pageBean && null != pageBean.getInsert_screen()) {

            if (pageBean.getInsert_screen().getTimes() == insertPages
                    && pageBean.getInsert_screen().getStatus()) {

                LogUtils.i(TAG, "Pages ==" + pageBean.getInsert_screen().getTimes());

                showByOrigin(getAdOriginByPercent(map.get(AD.AdType.INSET)), AD.AdType.INSET);

            }
        }

    }


    @Override
    public void onNoAD(String eventID, String source, String typeName) {

        lastExecuted = System.currentTimeMillis();

        if (eventID.contains("interstitial")) {
            insertPages = 0;
        }
//        dataReport("noAD", source, eventID, typeName);

    }

    @Override
    public void onShowAD(String eventID, String source, String typeName) {

        retryCounts = 0;

        if (eventID.contains("interstitial")) {
            insertPages = 0;
        }
//        dataReport("adPresent", source, eventID, typeName);

    }

    //广点通请求失败重新请求
    @Override
    public void onFailedAD(int code, AD.AdOrigin origin) {

//        retryRequest_Banner(code, origin);
    }

    @Override
    public void onClickAD(String eventID, String source, String typeName) {
        if (eventID.contains("interstitial")) {
            insertPages = 0;
        }
//        dataReport("adClicked", source, eventID, typeName);

        //如果后台开启了点一次就不再展示banner
        if (!page.equals(Constants.LISTENING_PAGE)) {
            return;
        }
        if (!eventID.toLowerCase().contains("banner")) {
            return;
        }
//        UserEntity userEntity = GreenDaoHelper.INSTANCE.getCurrentUser();
//        LogUtils.e(TAG, "阅读页Banner广告点击开关--开启==" + userEntity);
//
//        if (userEntity != null && userEntity.getIsADClickClose()) {
//
//            LogUtils.e(TAG, "阅读页Banner广告点击开关--开启");
//
//            cancelTask();
//
//            SPUtil.getInstance().putBoolean(Constants.AD_BANNER_NEVER_SHOW, true);
//
//            ComicApplication.getInstance().setHaveAD(false);
//
//            //发送广播,切换章节后全屏绘制文字
//            EventBus.getDefault().post(new AdCloseEvent(true, false));
//        }
    }

    @Override
    public void onClosedAD() {

        insertPages = 0;

    }

    private void dataReport(String event, String ad_source, String eventID, String name) {
        Map<String, String> map = new HashMap<>();
        map.put("event", event);
        map.put("ad_source", ad_source);
        map.put("action_type", "ad_report");
    }

    private void retryRequest_Banner(final int code, final AD.AdOrigin origin) {

//        //广点通的604失败才重新请求
//        if (AD.AdOrigin.gdt.name().equals(origin.name()) && code != 604 && code != 102006) {
//            return;
//        }
//        //阅读页面不可见，不重试
//        if (!isReaderActivityShow && ComicApplication.getInstance().isBackGround) {
//
//            LogUtils.i(TAG, "Activity " + page + " 不可见,不重试");
//
//            return;
//        }
//
//        if (!isBookDetailActivityShow && ComicApplication.getInstance().isBackGround) {
//
//            LogUtils.i(TAG, "Activity " + page + " 不可见,不重试");
//
//            return;
//        }
//
//        if (System.currentTimeMillis() - SPUtil.getInstance().getLong(page + Constants.AD_BANNER_LAST_CHANGE)
//                > pageBean.getBanner_screen().getChange_times() * 1000) {
//
//            LogUtils.i(TAG, "到切换广告源时间，不重试~~");
//
//            return;
//        }
//
//        retryCounts++;
//
//        LogUtils.i(TAG, "retryRequest_Banner，重新请求广告次数==" + retryCounts);
//
//        if (retryCounts == 5) {
//
//
//            retryCounts = 0;
//
//            AD.AdOrigin New = (origin == AD.AdOrigin.gdt ? AD.AdOrigin.bd : AD.AdOrigin.gdt);
//
//            LogUtils.i(TAG, "请求失败达到5次，切换广告源==" + New.name());
//
//            showByOrigin(New, AD.AdType.BANNER);
//
//            return;
//        }
//
//        long now = System.currentTimeMillis();
//
//        long delay = 10 * 1000;
//
//        if (System.currentTimeMillis() - lastExecuted >= 10 * 1000) {
//
//            LogUtils.i(TAG, "重新请求广告==" + origin.name());
//
//            showByOrigin(origin, AD.AdType.BANNER);
//
//            return;
//        }
//
//        getMainThreadHandler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                LogUtils.i(TAG, "延时重新请求广告==" + origin.name());
//
//                showByOrigin(origin, AD.AdType.BANNER);
//
//            }
//        }, delay - (now - lastExecuted));
    }


    public void activityDestroy() {
        activity = null;

    }
}

