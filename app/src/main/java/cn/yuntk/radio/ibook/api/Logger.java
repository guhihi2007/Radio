package cn.yuntk.radio.ibook.api;

import android.util.Log;

/**
 * @author yuyh.
 * @date 2016/12/13.
 */
public class Logger implements LoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        Log.e("http----: " ,message);
    }
}
