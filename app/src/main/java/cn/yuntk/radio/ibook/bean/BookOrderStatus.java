package cn.yuntk.radio.ibook.bean;
/*书籍顺序状态*/
public class BookOrderStatus {

    private String bookId;
    private String isReverse;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getIsReverse() {
        return isReverse;
    }

    public void setIsReverse(String isReverse) {
        this.isReverse = isReverse;
    }
}
