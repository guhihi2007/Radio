package cn.yuntk.radio.ibook.bean;


import cn.yuntk.radio.ibook.base.RootBase;

/** 书籍列表 */

public class TCBean3 extends RootBase {

    /**
     * bookID : 30221
     * bookName : 来自阴间的新娘
     * bookPhoto : http://www.tingchina.com/cover/yousheng/来自阴间的新娘_尖儿.gif
     * hostName : 尖儿
     * intro : 邻居结婚的当天晚上，新娘却因我而死...
     * playNum : 3446
     */

    private int bookID;
    private String bookName;
    private String bookPhoto;
    private String hostName;
    private String intro;
    private int playNum;

    private String bookType;

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookPhoto() {
        return bookPhoto;
    }

    public void setBookPhoto(String bookPhoto) {
        this.bookPhoto = bookPhoto;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getPlayNum() {
        return playNum;
    }

    public void setPlayNum(int playNum) {
        this.playNum = playNum;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }
}
