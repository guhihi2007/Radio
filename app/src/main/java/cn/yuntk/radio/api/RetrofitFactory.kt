package cn.yuntk.radio.api

import cn.yuntk.radio.Constants.HOST
import cn.yuntk.radio.Constants.YTK_HOST
import com.google.gson.Gson
import okhttp3.Interceptor
import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Author : Gupingping
 * Date : 2018/7/11
 * Mail : gu12pp@163.com
 */
object RetrofitFactory {
    //用于请求收音机频道信息的service
    val instance: Retrofit by lazy {

        Retrofit.Builder()
                .baseUrl(HOST)
                .client(OkHttpClient.Builder()
                        .connectTimeout(9, TimeUnit.SECONDS)
                        .build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 添加Rx适配器
                .addConverterFactory(GsonConverterFactory.create())// 添加Gson转换器
                .build()
    }
    //用于请求收音机广告配置信息的service 区别就是添加了header，后台根据header中的project字段和version字段给广告配置
    //log 过滤 http---- 可以查看请求信息
    val service: Retrofit by lazy {

        Retrofit.Builder()
                .baseUrl(YTK_HOST)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 添加Rx适配器
                .addConverterFactory(GsonConverterFactory.create())// 添加Gson转换器
                .build()
    }
    private val okHttpClient by lazy {
        val logging = LoggingInterceptor(Logger())
        logging.level = LoggingInterceptor.Level.BODY
        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder().addHeader("X-Client-Info", Gson().toJson(HeaderParams())).build()
            chain.proceed(request)
        }
        OkHttpClient().newBuilder()
                .addInterceptor(interceptor)
                .addInterceptor(logging)
                .connectTimeout(9, TimeUnit.SECONDS)
                .build()
    }
}
