package cn.yuntk.radio.api

import cn.yuntk.radio.ad.AdsConfig
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Author : Gupingping
 * Date : 2018/7/11
 * Mail : gu12pp@163.com
 * 云天空 广告配置接口
 */
interface YtkService {
    @GET("/ytkapplicaton/anListenAppConfig")
    fun getAppConfig(@Query("name") name: String,
                     @Query("channel") channel: String,
                     @Query("version") version: String): Observable<AdsConfig>

}
