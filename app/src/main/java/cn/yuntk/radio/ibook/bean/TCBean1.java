package cn.yuntk.radio.ibook.bean;


import java.util.List;

import cn.yuntk.radio.ibook.base.RootBase;

/** 一级分类 */
public class TCBean1 extends RootBase {

    private List<TCBean2> ys;
    private List<TCBean2> ps;

    public List<TCBean2> getYs() {
        return ys;
    }

    public void setYs(List<TCBean2> ys) {
        this.ys = ys;
    }

    public List<TCBean2> getPs() {
        return ps;
    }

    public void setPs(List<TCBean2> ps) {
        this.ps = ps;
    }
}
