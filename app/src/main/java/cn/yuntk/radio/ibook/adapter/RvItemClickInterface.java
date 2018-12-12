package cn.yuntk.radio.ibook.adapter;


import cn.yuntk.radio.ibook.base.RootBase;

public interface RvItemClickInterface<T extends RootBase> {

    void onItemClick(T t);

}
