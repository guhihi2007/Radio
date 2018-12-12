package cn.yuntk.radio.ibook.common;


import java.util.HashMap;

import cn.yuntk.radio.ibook.base.RootBean;
import cn.yuntk.radio.ibook.base.RootListBean;
import cn.yuntk.radio.ibook.bean.TCBean1;
import cn.yuntk.radio.ibook.bean.TCBean3;
import cn.yuntk.radio.ibook.bean.TCBean4;
import cn.yuntk.radio.ibook.bean.TCBean5;
import cn.yuntk.radio.ibook.bean.TCBean6;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * 创建时间:2018/4/3
 * 创建人:
 * 描述:
 */

public interface ITestApi {

    //一级分类
    @GET(Api.CLASSIFY_1)
    Observable<RootBean<TCBean1>> getBookTypeList(@QueryMap HashMap<String, String> map);

    //点击分类之后的书籍列表
    @GET(Api.CLASSIFY_2)
    Observable<RootListBean<TCBean3>> getAlbumByBookTypeid(@QueryMap HashMap<String, String> map);

    //榜单 书籍列表
    @GET(Api.RANK_INDEX_2)
    Observable<RootListBean<TCBean3>> getAlbumInRank(@QueryMap HashMap<String, String> map);

    //专辑详情
    @GET(Api.ALBUM_DETAIL)
    Observable<RootBean<TCBean4>> getAlbumDetail(@QueryMap HashMap<String, String> map);

    //声音列表
    @GET(Api.ALBUM_TRACK)
    Observable<RootListBean<TCBean5>> getAlbumTracks(@QueryMap HashMap<String, String> map);

    //获取播放地址
    @GET(Api.LISTENER)
    Observable<RootBean<TCBean6>> getPlayCdn(@QueryMap HashMap<String, String> map);

    //获取播放地址
    @GET(Api.TC_SEARCH)
    Observable<RootListBean<TCBean3>> serchAlbumForKeyword(@QueryMap HashMap<String, String> map);


}
