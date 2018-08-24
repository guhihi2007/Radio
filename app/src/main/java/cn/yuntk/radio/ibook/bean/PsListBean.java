package cn.yuntk.radio.ibook.bean;

import java.util.List;

//评书列表
public class PsListBean extends PageInfo{

    /**
     * title : 袁阔成
     * typeid : 4
     * url_list : [{"u":"168","title":"三国演义(365回)","name":"袁阔成","zt":"(完结)"},{"u":"198","title":"封神演义（200回）","name":"袁阔成","zt":"(完结)"},{"u":"10452","title":"三国演义(电视版)365回全集","name":"袁阔成","zt":"(完结)"},{"u":"188","title":"水泊梁山（100回）","name":"袁阔成","zt":"(完结)"},{"u":"184","title":"薛刚反唐（100回）","name":"袁阔成","zt":"(完结)"},{"u":"167","title":"楚汉相争(50回)","name":"袁阔成","zt":"(完结)"},{"u":"3221","title":"碧眼金蟾又名(彭公案)","name":"袁阔成","zt":"(完结)"},{"u":"186","title":"西楚霸王（50回）","name":"袁阔成","zt":"(完结)"},{"u":"208","title":"彭公案","name":"袁阔成","zt":"(完结)"},{"u":"193","title":"林海雪原（45回）","name":"袁阔成","zt":"(完结)"},{"u":"206","title":"敌后武工队","name":"袁阔成","zt":"(完结)"},{"u":"194","title":"烈火金刚（36回）","name":"袁阔成","zt":"(完结)"},{"u":"207","title":"十二金钱镖","name":"袁阔成","zt":"(完结)"},{"u":"200","title":"长坂雄风（27回）","name":"袁阔成","zt":"(完结)"},{"u":"211","title":"野火春风斗古城 全80回","name":"袁阔成","zt":"(完结)"},{"u":"210","title":"抗日烽火 袁阔成 全42回","name":"袁阔成","zt":"(完结)"},{"u":"203","title":"三气周瑜(22回)","name":"袁阔成","zt":"(完结)"},{"u":"192","title":"平原枪声（40回）","name":"袁阔成","zt":"(完结)"},{"u":"196","title":"金钱镖（52回）","name":"袁阔成","zt":"(完结)"},{"u":"201","title":"暴风骤雨（37回）","name":"袁阔成","zt":"(完结)"},{"u":"169","title":"乾隆系列(9回)","name":"袁阔成","zt":"(完结)"},{"u":"8144","title":"艳阳天","name":"袁阔成","zt":"(完结)"},{"u":"190","title":"乾隆与纪晓岚（11回）","name":"袁阔成","zt":"(完结)"},{"u":"209","title":"野火春风斗古城(63年版)","name":"袁阔成","zt":"(完结)"},{"u":"195","title":"红岩魂（40回）","name":"袁阔成","zt":"(完结)"},{"u":"189","title":"群英会（29回）","name":"袁阔成","zt":"(完结)"},{"u":"199","title":"赤胆忠心（36回）","name":"袁阔成","zt":"(完结)"},{"u":"185","title":"肖飞买药（2回）","name":"袁阔成","zt":"(完结)"},{"u":"191","title":"彭公三河断奇案（5回）","name":"袁阔成","zt":"(完结)"},{"u":"20801","title":"烈火金刚","name":"袁阔成","zt":"(完结)"},{"u":"171","title":"袁阔成与三国演义","name":"袁阔成","zt":"(完结)"},{"u":"181","title":"转战陕北（10回）","name":"袁阔成","zt":"(完结)"},{"u":"175","title":"三战吕布","name":"袁阔成","zt":"(完结)"},{"u":"183","title":"胸中自有雄兵百万（10回）","name":"袁阔成","zt":"(完结)"},{"u":"3194","title":"在希望的田野上(20回)","name":"袁阔成","zt":"(完结)"},{"u":"180","title":"刘备过江","name":"袁阔成","zt":"(完结)"},{"u":"174","title":"桃花庄","name":"袁阔成","zt":"(完结)"},{"u":"176","title":"舌战小炉匠","name":"袁阔成","zt":"(完结)"},{"u":"187","title":"商鞅变法（2回）","name":"袁阔成","zt":"(完结)"},{"u":"197","title":"古城会(4回)","name":"袁阔成","zt":"(完结)"},{"u":"177","title":"桥头镇","name":"袁阔成","zt":"(完结)"},{"u":"205","title":"刺龟山（1回）","name":"袁阔成","zt":"(完结)"},{"u":"202","title":"黎明之前（1回）","name":"袁阔成","zt":"(完结)"},{"u":"204","title":"举火招贤（1回）","name":"袁阔成","zt":"(完结)"},{"u":"170","title":"惠兆龙_棋高一着","name":"袁阔成","zt":"(完结)"},{"u":"178","title":"刘宗珉飞马渡汉江","name":"袁阔成","zt":"(完结)"},{"u":"179","title":"江姐上船","name":"袁阔成","zt":"(完结)"},{"u":"182","title":"义送摇旗（2回）","name":"袁阔成","zt":"(完结)"},{"u":"172","title":"灞桥挑袍","name":"袁阔成","zt":"(完结)"},{"u":"173","title":"许云峰赴宴","name":"袁阔成","zt":"(完结)"}]
     */

    private String title;
    private int typeid;
    private List<PsUrlItemBean> url_list;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTypeid() {
        return typeid;
    }

    public void setTypeid(int typeid) {
        this.typeid = typeid;
    }

    public List<PsUrlItemBean> getUrl_list() {
        return url_list;
    }

    public void setUrl_list(List<PsUrlItemBean> url_list) {
        this.url_list = url_list;
    }

}
