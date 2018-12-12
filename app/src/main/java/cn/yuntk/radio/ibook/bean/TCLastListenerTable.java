package cn.yuntk.radio.ibook.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/** 专辑最后收听表 */
@Entity(nameInDb = "tc_album_last_one")
public class TCLastListenerTable {

    @Id(autoincrement = false)
    private Long bookID;
    private String bookName;

    private int epis;
    private String zname;

    private String is_download = "0";//0未下载1下载成功2正在下载3等待中
    private String path = "";//下载的路径
    private String online;//线上地址
    private int listenerStatus = 0;//播放状态 0//未播放 1播放了一段 2已播放完成
    private int duration;//总时长
    private int progress;//进度
    @Generated(hash = 1643424518)
    public TCLastListenerTable(Long bookID, String bookName, int epis, String zname,
                               String is_download, String path, String online, int listenerStatus,
                               int duration, int progress) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.epis = epis;
        this.zname = zname;
        this.is_download = is_download;
        this.path = path;
        this.online = online;
        this.listenerStatus = listenerStatus;
        this.duration = duration;
        this.progress = progress;
    }
    @Generated(hash = 1596721687)
    public TCLastListenerTable() {
    }
    public Long getBookID() {
        return this.bookID;
    }
    public void setBookID(Long bookID) {
        this.bookID = bookID;
    }
    public String getBookName() {
        return this.bookName;
    }
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
    public int getEpis() {
        return this.epis;
    }
    public void setEpis(int epis) {
        this.epis = epis;
    }
    public String getZname() {
        return this.zname;
    }
    public void setZname(String zname) {
        this.zname = zname;
    }
    public String getIs_download() {
        return this.is_download;
    }
    public void setIs_download(String is_download) {
        this.is_download = is_download;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getOnline() {
        return this.online;
    }
    public void setOnline(String online) {
        this.online = online;
    }
    public int getListenerStatus() {
        return this.listenerStatus;
    }
    public void setListenerStatus(int listenerStatus) {
        this.listenerStatus = listenerStatus;
    }
    public int getDuration() {
        return this.duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getProgress() {
        return this.progress;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }


}
