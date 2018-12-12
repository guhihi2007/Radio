package cn.yuntk.radio.ad;

/**
 * 启动页activity参数为空时 finish SplashActivity
 */

public class LoadEvent {
    public boolean isFinish;

    public LoadEvent(boolean isFinish) {
        this.isFinish = isFinish;
    }
}
