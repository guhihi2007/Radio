/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.yuntk.radio.ibook.api;


import java.util.HashMap;

import cn.yuntk.radio.ibook.base.RootBean;
import cn.yuntk.radio.ibook.base.RootListBean;
import cn.yuntk.radio.ibook.bean.TCBean1;
import cn.yuntk.radio.ibook.bean.TCBean3;
import cn.yuntk.radio.ibook.bean.TCBean4;
import cn.yuntk.radio.ibook.bean.TCBean5;
import cn.yuntk.radio.ibook.bean.TCBean6;
import cn.yuntk.radio.ibook.common.Api;
import cn.yuntk.radio.ibook.common.ITestApi;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author yuyh.
 * @date 2016/8/3.
 *
 * 现使用请求
 */
public class BookApi {

    public static BookApi instance;

    private ITestApi service;

    public BookApi(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.APP_BASE_URL_TC)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 添加Rx适配器
                .addConverterFactory(GsonConverterFactory.create()) // 添加Gson转换器
                .client(okHttpClient)
                .build();
        service = retrofit.create(ITestApi.class);
    }

    public static BookApi getInstance(OkHttpClient okHttpClient) {
        if (instance == null)
            instance = new BookApi(okHttpClient);
        return instance;
    }

    //获取首页小说列表
    public Observable<RootListBean<TCBean3>> getAlbumInRank(HashMap<String,String> map) {
        return service.getAlbumInRank(map);
    }

    //获取分类
    public Observable<RootBean<TCBean1>> getBookTypeList(HashMap<String,String> map){
        return service.getBookTypeList(map);
    }

    //获取分类专辑列表
    public Observable<RootListBean<TCBean3>> getAlbumByBookTypeid(HashMap<String,String> map){
        return service.getAlbumByBookTypeid(map);
    }

    //获取专辑详情
    public Observable<RootBean<TCBean4>> getAlbumDetail(HashMap<String,String> map){
        return service.getAlbumDetail(map);
    }

    //获取声音列表
    public Observable<RootListBean<TCBean5>> getAlbumTracks(HashMap<String,String> map){
        return service.getAlbumTracks(map);
    }

    //获取播放地址
    public Observable<RootBean<TCBean6>> getPlayCdn(HashMap<String,String> map){
        return service.getPlayCdn(map);
    }

    //搜索接口
    public Observable<RootListBean<TCBean3>> serchAlbumForKeyword(HashMap<String,String> map){
        return service.serchAlbumForKeyword(map);
    }

}