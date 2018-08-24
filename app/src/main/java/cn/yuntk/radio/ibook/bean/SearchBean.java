package cn.yuntk.radio.ibook.bean;

import java.util.List;

/*搜索实体类*/
public class SearchBean extends PageInfo {

    private List<SearchItemBean> url_list;

    public List<SearchItemBean> getUrl_list() {
        return url_list;
    }

    public void setUrl_list(List<SearchItemBean> url_list) {
        this.url_list = url_list;
    }

}
