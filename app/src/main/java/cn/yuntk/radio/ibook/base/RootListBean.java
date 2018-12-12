package cn.yuntk.radio.ibook.base;

import java.util.List;

/** 列表实体基类 */
public class RootListBean<T extends RootBase> extends RootJson {

    private int status;
    private String msg;
    private List<T> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
