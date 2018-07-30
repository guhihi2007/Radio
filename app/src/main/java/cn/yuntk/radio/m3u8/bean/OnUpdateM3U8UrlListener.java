package cn.yuntk.radio.m3u8.bean;


/**
 * 下载监听
 * Created by HDL on 2017/8/10.
 */

public interface OnUpdateM3U8UrlListener extends BaseListener {

    /**
     * 更新下载连接回调 by gpp
     * @param list
     */
    void onUpdateAddress(String latestUrl);
}
