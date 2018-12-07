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

import com.google.gson.Gson;
import cn.yuntk.radio.ibook.XApplication;
import cn.yuntk.radio.ibook.util.PackageUtils;
import cn.yuntk.radio.ibook.util.TelephonyUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Retrofit2 Header拦截器。用于保存和设置Cookies
 *
 * @author yuyh.
 * @date 16/8/6.
 */
public final class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String url = original.url().toString();

        Request request = original.newBuilder()
//                .addHeader("User-Agent", "ZhuiShuShenQi/3.40[preload=false;locale=zh_CN;clientidbase=android-nvidia]") // 不能转UTF-8
//                .addHeader("User-Agent", Constants.UAConfig+" (iPhone; iOS "+ Build.VERSION.RELEASE+"; Scale/2.00)") // 不能转UTF-8
                .removeHeader("User-Agent")
                .addHeader("User-Agent", PackageUtils.getUserAgent()) // 不能转UTF-8
                .addHeader("Accept-Encoding","gzip, deflate")
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .addHeader("Connection","keep-alive")
                .addHeader("Accept","*/*")
                .addHeader("Accept-Language","zh-Hans-CN;q=1")
                .build();
        return chain.proceed(request);

//        return chain.proceed(original);
    }
}
