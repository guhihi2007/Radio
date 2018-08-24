package cn.yuntk.radio.ibook.bean;

import java.util.List;

/*首页列表 最近更新*/
public class BookListBean extends PageInfo{

    /**
     * index_time : 100
     * time_list : []
     * */

    private int index_time;

    private int index_ps;

    private int index_ys;

    private int index_lz;

    private List<ItemBookBean> time_list;

    private List<ItemBookBean> ps_list;

    private List<ItemBookBean> ys_list;

    private List<ItemBookBean> lz_list;

    public int getIndex_time() {
        return index_time;
    }

    public void setIndex_time(int index_time) {
        this.index_time = index_time;
    }

    public List<ItemBookBean> getTime_list() {
        return time_list;
    }

    public void setTime_list(List<ItemBookBean> time_list) {
        this.time_list = time_list;
    }

    public int getIndex_ps() {
        return index_ps;
    }

    public void setIndex_ps(int index_ps) {
        this.index_ps = index_ps;
    }

    public int getIndex_ys() {
        return index_ys;
    }

    public void setIndex_ys(int index_ys) {
        this.index_ys = index_ys;
    }

    public int getIndex_lz() {
        return index_lz;
    }

    public void setIndex_lz(int index_lz) {
        this.index_lz = index_lz;
    }

    public List<ItemBookBean> getPs_list() {
        return ps_list;
    }

    public void setPs_list(List<ItemBookBean> ps_list) {
        this.ps_list = ps_list;
    }

    public List<ItemBookBean> getYs_list() {
        return ys_list;
    }

    public void setYs_list(List<ItemBookBean> ys_list) {
        this.ys_list = ys_list;
    }

    public List<ItemBookBean> getLz_list() {
        return lz_list;
    }

    public void setLz_list(List<ItemBookBean> lz_list) {
        this.lz_list = lz_list;
    }
}
