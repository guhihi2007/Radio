package cn.yuntk.radio.ibook.bean;

import java.util.List;

/** 自己组装的分类 */
public class TCBean1_1 {

    private int num;//数量
    private String type;//ys有声小说 ps评书
    private String content;
    private List<TCBean2> types;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<TCBean2> getTypes() {
        return types;
    }

    public void setTypes(List<TCBean2> types) {
        this.types = types;
    }
}
