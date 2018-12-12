package cn.yuntk.radio.api

import cn.yuntk.radio.bean.ChannelSubBean
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Author : Gupingping
 * Date : 2018/7/11
 * Mail : gu12pp@163.com
 */
interface FMService {
    @POST("1.3.8.json")
    @FormUrlEncoded
    fun getChannel(@Field("level") level: String, @Field("pageIndex") pageIndex: String,
                   @Field("parentId") channelCode: String, @Field("rootId") rootId: String): Observable<ChannelSubBean>

}
