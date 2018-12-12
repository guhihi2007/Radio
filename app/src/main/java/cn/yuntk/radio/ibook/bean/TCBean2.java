package cn.yuntk.radio.ibook.bean;

/** 一级分类 */
public class TCBean2 {

        /**
         * "bookTypeId":135,
         *  "name":"玄幻奇幻"
        * */
   private int bookTypeId;
   private String name;
   private String parent;

    public int getBookTypeId() {
        return bookTypeId;
    }

    public void setBookTypeId(int bookTypeId) {
        this.bookTypeId = bookTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
