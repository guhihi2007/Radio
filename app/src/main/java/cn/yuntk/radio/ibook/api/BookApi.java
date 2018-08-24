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

import cn.yuntk.radio.ibook.bean.BookDetailBean;
import cn.yuntk.radio.ibook.bean.BookListBean;
import cn.yuntk.radio.ibook.bean.ClassifyListBean;
import cn.yuntk.radio.ibook.bean.Mp3urlBean;
import cn.yuntk.radio.ibook.bean.PsListBean;
import cn.yuntk.radio.ibook.common.Api;
import cn.yuntk.radio.ibook.common.ITestApi;

import java.util.Map;

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
                .baseUrl(Api.APP_BASE_URL)
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

//    public Observable<Recommend> getRecommend(String gender) {
//        return service.getRecomend(gender);
//    }

    //获取小说列表
    public Observable<BookListBean> getRecommend(String flag) {
        switch (flag){
            case Api.BOOK_LIST1:
                return service.getBookList1();
            case Api.BOOK_LIST2:
                return service.getBookList2();
            case Api.BOOK_LIST3:
                return service.getBookList3();
            case Api.BOOK_LIST4:
                return service.getBookList4();
                default:
                    return service.getBookList1();
        }
    }

    //    获取分类
    public Observable<ClassifyListBean> getClassifyList(){
        return service.getClassifyList();
    }
    //    获取评书列表
    public Observable<PsListBean> getPsList(String id){
        return service.getPsList(id);
    }
    //      获取一本小说详情
    public Observable<BookDetailBean> getBookInfo(String id){
        return service.getBookInfo(id);
    }
    //    获取一个音频地址
    public Observable<Mp3urlBean> getMp3Url(Map<String,String> params){
        return service.getMp3Url(params);
    }

}