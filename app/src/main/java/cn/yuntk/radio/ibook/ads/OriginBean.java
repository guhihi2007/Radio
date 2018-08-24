package cn.yuntk.radio.ibook.ads;

/**
 * Created by Gpp on 2018/1/24.
 */

public class OriginBean {
    AD.AdOrigin origin;
    int precent;

    public AD.AdOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(AD.AdOrigin origin) {
        this.origin = origin;
    }

    public int getPrecent() {
        return precent;
    }

    public void setPrecent(int precent) {
        this.precent = precent;
    }

    @Override
    public String toString() {
        return "OriginBean{" +
                "origin=" + origin +
                ", precent=" + precent +
                '}';
    }
}
