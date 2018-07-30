package cn.yuntk.radio.api

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
    //    private var client: OkHttpClient? = null

    private const val HOST = "http://fm.gerenhao.com/api/open/voice/union/select/"

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


//    init {
//        client = OkHttpClient.Builder()
//                .connectTimeout(9, TimeUnit.SECONDS)
//                .build()
//
//        instance = Retrofit.Builder()
//                .baseUrl(HOST)
//                .client(client!!)
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 添加Rx适配器
//                .addConverterFactory(GsonConverterFactory.create())// 添加Gson转换器
//                .build()
//    }
}
