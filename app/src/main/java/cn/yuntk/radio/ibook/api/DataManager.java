package cn.yuntk.radio.ibook.api;

import android.content.Context;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import cn.yuntk.radio.BuildConfig;
import cn.yuntk.radio.ibook.common.CommonSubcriber;
import cn.yuntk.radio.ibook.common.DataCallBack;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.loger.Loger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 创建时间:2018/4/3
 * 创建人: yb
 * 描述:
 *
 * 原封装请求
 */

public class DataManager {
    private final int DATA_NULL_CODE = 10000;
    private Retrofit retrofit;

    private DataManager() {
        retrofit();
    }

    private static class DataManagerHolder {
        private static DataManager instance = new DataManager();
    }

    public static DataManager getInstance() {
        return DataManagerHolder.instance;
    }

    private void retrofit() {
        //手动创建一个OkHttpClient并设置超时时间缓存等设置
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);
//        builder.addInterceptor(new CookieInterceptor(true, basePar.getUrl()));
//        builder.addInterceptor(new MyLogginINterceptor());
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(getHttpLoggingInterceptor());
        }


        /*创建retrofit对象*/
        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("")
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public <T> void doHttpRequest(Observable<HttpResultEntity<T>> observable, final DataCallBack<T> callBack) {
        observable.compose(defaultTransformer()).compose(life(callBack)).flatMap(new Function<HttpResultEntity<T>,Observable<T>>() {
            @Override
            public Observable<T> apply(final HttpResultEntity<T> result) throws Exception {
                return Observable.create(new ObservableOnSubscribe<T>() {
                    @Override
                    public void subscribe(ObservableEmitter<T> e) throws Exception {
                        Loger.w("thread name = " + Thread.currentThread().getName());
                        if (result != null) {
                            if (result.isSuccessful()) {
                                e.onNext(result.getData());
                            } else {
//                                e.onError(new RuntimeException(result.getMsg()));
                                callBack.onError(result.getRet(),result.getMsg());
                            }
                        }
                    }
                });
            }
        }).subscribe(new CommonSubcriber<>(callBack));
    }

    private ObservableTransformer defaultTransformer() {
        return new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    private ObservableTransformer life(DataCallBack callBack) {
        final Context context = callBack.getContext();
        return context != null && context instanceof RxAppCompatActivity ?
                ((RxAppCompatActivity) context).bindUntilEvent(ActivityEvent.PAUSE) :
                new ObservableTransformer() {
            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream;
            }
        };
    }

    /**
     * 日志输出
     * 自行判定是否添加
     *
     * @return
     */
    private HttpLoggingInterceptor getHttpLoggingInterceptor() {
        //日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Loger.w("Retrofit====Message:" + message);
            }
        });
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }

   public static class MyLogginINterceptor implements Interceptor {
        String TAG = MyLogginINterceptor.class.getSimpleName();
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long startTime = System.currentTimeMillis();
            okhttp3.Response response = chain.proceed(chain.request());
            long endTime = System.currentTimeMillis();
            long duration=endTime-startTime;
            okhttp3.MediaType mediaType = response.body().contentType();
            String content = response.body().string();//打印之后流关闭了
            LogUtils.showLog(TAG+"\n");
            LogUtils.showLog(TAG+"----------Start----------------");
            LogUtils.showLog(TAG+"| "+request.toString());
            String method=request.method();
            if("POST".equals(method)){
                StringBuilder sb = new StringBuilder();
                if (request.body() instanceof FormBody) {
                    FormBody body = (FormBody) request.body();
                    for (int i = 0; i < body.size(); i++) {
                        sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                    }
                    sb.delete(sb.length() - 1, sb.length());
                    LogUtils.showLog(TAG+"| RequestParams:{"+sb.toString()+"}");
                }
            }
            LogUtils.showLog(TAG+"| Response:" + content);
            LogUtils.showLog(TAG+"----------End:"+duration+"毫秒----------");
            return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();
        }
    }
}