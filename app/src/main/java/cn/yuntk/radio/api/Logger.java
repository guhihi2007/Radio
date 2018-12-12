package cn.yuntk.radio.api;


import cn.yuntk.radio.utils.LogUtils;

/**
 * Author : Gupingping
 * Date : 2018/9/28
 * QQ : 464955343
 */
public class Logger implements LoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        LogUtils.e("http----: " ,message);
    }
}
