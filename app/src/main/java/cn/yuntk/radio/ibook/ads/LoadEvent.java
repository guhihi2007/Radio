package cn.yuntk.radio.ibook.ads;

/**
 * 启动页activity参数为空时 finish SplashActivity
 * Created by Erosion on 2018/1/3.
 */

public class LoadEvent {
    public boolean isFinish;

    public LoadEvent(boolean isFinish) {
        this.isFinish = isFinish;
    }
}
