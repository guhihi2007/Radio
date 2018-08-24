package cn.yuntk.radio.ibook.api;

import cn.yuntk.radio.ibook.XApplication;
import cn.yuntk.radio.ibook.common.Api;
import cn.yuntk.radio.ibook.util.NetworkUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/9/30 0030.
 * okhttp 请求
 */

public class BaseOkhttp {

    private static final String baseUrlHead = "";

    private static BaseOkhttp baseOkhttp;
    /**
     * Handler
     * okHttp post请求
     * handler 状态码 1失败0成功
     */
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build();


    private BaseOkhttp() {
    }

    public static BaseOkhttp getInstance() {
        if (baseOkhttp == null) {
            baseOkhttp = new BaseOkhttp();
        }
        return baseOkhttp;
    }

    /**
     * get请求 json
     *
     * @param urlPart
     * @param json
     * @param callback
     */
    public void get(String urlPart, String json, final RequestCallback callback){

        String requestUrl = String.format("%s%s?%s", baseUrlHead, urlPart, json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(requestUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onFailure("Not Found", null);
                }
            }
        });

    }

    /**
     * get请求 map
     *
     * @param urlPart
     * @param map
     * @param callback
     */
    public void get(String urlPart, Map<String, String> map, final RequestCallback callback){
        String json = null;
        try {
            json = pinjiePrarms(map);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            callback.onFailure("", e);
            return;
        }
        String requestUrl = String.format("%s%s?%s", baseUrlHead, urlPart, json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(requestUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onFailure("Not Found", null);
                }
            }
        });

    }

    //    get拼接参数
    public String pinjiePrarms(Map<String, String> paramsMap) throws UnsupportedEncodingException {
        StringBuilder tempParams = new StringBuilder();
        int pos = 0;
        for (String key : paramsMap.keySet()) {
            if (pos > 0) {
                tempParams.append("&");
            }
            tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
            pos++;
        }
        return tempParams.toString();
    }

    /**
     * post请求 map为body
     *
     * @param url
     * @param map
     * @param
     */
    public void post(String url, Map<String, Object> map, final RequestCallback callback) {
        if (!NetworkUtils.isConnected(XApplication.getsInstance())) {
//            检查网络连接
            ToastUtil.showToast("请检查网络是否连接！");
            return;
        }

        // FormBody.Builder builder = new FormBody.Builder();
        // FormBody body=new FormBody.Builder().add("key", "value").build();

        /**
         * 创建请求的参数body
         */
        FormBody.Builder builder = new FormBody.Builder();

        /**
         * 遍历key
         */
        if (null != map) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {

                System.out.println("Key = " + entry.getKey() + ", Value = "
                        + entry.getValue());
                builder.add(entry.getKey(), entry.getValue().toString());

            }
        }

        RequestBody body = builder.build();

        final Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onFailure("Not Found", null);
                }
            }
        });

    }

    /**
     * post请求，json数据为body
     *
     * @param url
     * @param json
     * @param callback
     */
    public void postJson(String url, String json, final RequestCallback callback) {

        RequestBody body = RequestBody.create(JSON, json);
        final Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body().string());
                } else {
                    callback.onFailure("Not Found", null);
                }
            }
        });

    }

    /**
     * 获取广告配置*/
    public void getAdConfig(RequestCallback callback){
        get(Api.YTK_ADCONFIG,"",callback);
    }
    /**
     * 获取音频前缀
     * */
    public void getPrefix_str(Map<String,Object> map, RequestCallback callback){
        post(Api.APP_BASE_URL+Api.BOOK_MP3_URL,map,callback);
    }


    public interface RequestCallback {
        void onSuccess(String response);

        void onFailure(String msg, Exception e);
    }

}
