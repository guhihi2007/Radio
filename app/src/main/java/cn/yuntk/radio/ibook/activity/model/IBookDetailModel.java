package cn.yuntk.radio.ibook.activity.model;


import cn.yuntk.radio.ibook.base.model.IBaseModel;

public interface IBookDetailModel extends IBaseModel {
    void getAlbumDetail();//获取专辑详情
    void getAlbumTracks();//获取声音列表
}
