package cn.yuntk.radio.ibook.api;

/**
 * 创建时间:2018/4/3
 * 创建人: yb
 * 描述:基础实体类
 * 原封装实体类
 */

public class HttpResultEntity<T> {
    //  判断标示
    private int ret;
    //    提示信息
    private String msg;
    //显示数据（用户需要关心的数据）
    private T data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    public boolean isSuccessful() {
        return ret == 1;
    }
}
