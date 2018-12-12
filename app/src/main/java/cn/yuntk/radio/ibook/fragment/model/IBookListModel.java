package cn.yuntk.radio.ibook.fragment.model;


import cn.yuntk.radio.ibook.base.model.IBaseModel;

public interface IBookListModel extends IBaseModel {
    void getAlbumInRank();//首页榜单
    void getAlbumByBookTypeid();//分类专辑列表
}
