package cn.yuntk.radio.ibook.common;

import cn.yuntk.radio.ibook.bean.BookDetailBean;
import cn.yuntk.radio.ibook.bean.BookListBean;
import cn.yuntk.radio.ibook.bean.ClassifyListBean;
import cn.yuntk.radio.ibook.bean.Mp3urlBean;
import cn.yuntk.radio.ibook.bean.PsListBean;
import cn.yuntk.radio.ibook.bean.SearchBean;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 创建时间:2018/4/3
 * 创建人: yb
 * 描述:
 */

public interface ITestApi {

    /**
     * app接口
     */
    @GET(Api.BOOK_LIST1)
    Observable<BookListBean> getBookList1();

    @GET(Api.BOOK_LIST2)
    Observable<BookListBean> getBookList2();

    @GET(Api.BOOK_LIST3)
    Observable<BookListBean> getBookList3();

    @GET(Api.BOOK_LIST4)
    Observable<BookListBean> getBookList4();

    @GET(Api.BOOK_CLASSIFY)
    Observable<ClassifyListBean> getClassifyList();

    @FormUrlEncoded
    @POST(Api.BOOK_MP3_URL)
    Observable<Mp3urlBean> getMp3Url(@FieldMap Map<String, String> params);

    @GET("a_html/{id}.html")
    Observable<BookDetailBean> getBookInfo(@Path("id") String id);

    @GET("/a_list/{id}_hot.html")
    Observable<PsListBean> getPsList(@Path("id") String id);

    @GET("/so.asp")
    Observable<SearchBean> searchBookForKeyword(@Query("keyword") String key);

}
