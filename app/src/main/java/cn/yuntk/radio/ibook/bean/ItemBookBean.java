package cn.yuntk.radio.ibook.bean;

import cn.yuntk.radio.ibook.util.StringUtils;

/*首页 小说 Item*/
public class ItemBookBean {
    /**
     * title : 你的青梅_她的竹马
     * name : 彩翎豆
     * zztt : (完结)
     * id : 23977
     * type : [言情通俗]
     */

    private String title;
    private String name;
    private String zztt;
    private String id;
    private String type;

    public String getTitle() {
        return StringUtils.getString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return StringUtils.getString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZztt() {
        return StringUtils.getString(zztt);
    }

    public void setZztt(String zztt) {
        this.zztt = zztt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return StringUtils.getString(type);
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "{" +
                "title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", zztt='" + zztt + '\'' +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
