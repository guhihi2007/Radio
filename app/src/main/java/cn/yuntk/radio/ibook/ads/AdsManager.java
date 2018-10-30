package cn.yuntk.radio.ibook.ads;

import com.google.gson.Gson;
import com.yuntk.ibook.ads.AdsConfig;

import cn.yuntk.radio.ibook.api.BaseOkhttp;
import cn.yuntk.radio.ibook.bean.ListenerUKABean;
import cn.yuntk.radio.ibook.common.Constants;
import cn.yuntk.radio.ibook.util.GsonUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;
import cn.yuntk.radio.ibook.util.StringUtils;

/**
 * Created by Erosion on 2017/11/14.
 */

public class AdsManager implements BaseOkhttp.RequestCallback{

    /**
     * 广点通广告配置接口
     */
    public void getState() {
        BaseOkhttp.getInstance().getAdConfig(this);
    }

    @Override
    public void onSuccess(String response) {
        Gson gson = new Gson();
        AdsConfig result;
        try {
            result = gson.fromJson(response,AdsConfig.class);
            if (null == result) return;
            LogUtils.showLog("AdsStatus 请求成功");
        }catch (Exception e){
            e.printStackTrace();
            return;
        }


        try {
            SharedPreferencesUtil.getInstance().putString(ADConstants.MUSIC_DETAIL, gson.toJson(result.getData().getMusic_detail()));
            LogUtils.e("NetClient----","getMusic_detail==="+ gson.toJson(result.getData().getMusic_detail()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            SharedPreferencesUtil.getInstance().putString(ADConstants.HOME_PAGE_LIST1, gson.toJson(result.getData().getHome_page()));
            SharedPreferencesUtil.getInstance().putString(ADConstants.HOME_PAGE_LIST2, gson.toJson(result.getData().getHome_page()));
            SharedPreferencesUtil.getInstance().putString(ADConstants.HOME_PAGE_LIST3, gson.toJson(result.getData().getHome_page()));
            SharedPreferencesUtil.getInstance().putString(ADConstants.HOME_PAGE_LIST4, gson.toJson(result.getData().getHome_page()));
            LogUtils.e("AdsManager----","getHome_page==="+ gson.toJson(result.getData().getHome_page()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            SharedPreferencesUtil.getInstance().putString(ADConstants.HOME_PAGE_NEW, gson.toJson(result.getData().getHome_page_new()));
            LogUtils.e("AdsManager----","getHome_page_new==="+ gson.toJson(result.getData().getHome_page_new()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            SharedPreferencesUtil.getInstance().putString(ADConstants.CATEGORY_PAGE, gson.toJson(result.getData().getCategory_page()));
            LogUtils.e("AdsManager----","getCategory_page==="+ gson.toJson(result.getData().getCategory_page()));

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            SharedPreferencesUtil.getInstance().putString(ADConstants.LISTENING_PAGE, gson.toJson(result.getData().getListening_page()));
            LogUtils.e("AdsManager----","getListening_page==="+ gson.toJson(result.getData().getListening_page()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            SharedPreferencesUtil.getInstance().putString(ADConstants.START_PAGE, gson.toJson(result.getData().getStart_page()));
            LogUtils.e("AdsManager----","getStart_page==="+ gson.toJson(result.getData().getListening_page()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    @Override
    public void onFailure(String msg, Exception e) {
        LogUtils.showLog("AdsManager 请求失败");
    }

    public void getUAConfig(){
        BaseOkhttp.getInstance().getUAConfig(new BaseOkhttp.RequestCallback() {
            @Override
            public void onSuccess(String response) {
                LogUtils.showLog("getUAConfig 请求成功"+response);
                if (!StringUtils.isEmpty(response)){
                    ListenerUKABean ukaBean = GsonUtils.parseObject(response,ListenerUKABean.class);
                    if (ukaBean.getCode() == 0){
                        SharedPreferencesUtil.getInstance().putString(Constants.UAConfig_,ukaBean.getData().getListenUKA());
                    }
                }
            }

            @Override
            public void onFailure(String msg, Exception e) {
                LogUtils.showLog("getUAConfig 请求失败");
            }
        });
    }
}