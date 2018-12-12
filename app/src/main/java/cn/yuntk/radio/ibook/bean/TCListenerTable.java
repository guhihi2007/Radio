package cn.yuntk.radio.ibook.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/** 章节收听进度表 */
@Entity(nameInDb = "tc_album_track_list")
public class TCListenerTable implements Serializable {

    static final long serialVersionUID = 175L;

    @Id(autoincrement = true)
    private Long localid;
    private Long bookID;
    private String bookname;
    private int epis;
    private String zname;

    private String is_download = "0";//0未下载1下载成功2正在下载3等待中
    private String path = "";//下载的路径
    private String online;//线上地址
    private int listenerStatus = 0;//播放状态 0//未播放 1播放了一段 2已播放完成
    private int duration;//总时长
    private int progress;//进度
    private String displayProgress;//显示百分比

    private String is_collect; // 是否收藏 0未收藏1收藏
    private String is_history; // 是否历史记录 0未记录1记录
    private String is_local;//是否本地 0不是1已下载

    private String remark1;//备用字段1
    private String remark2;//备用字段2
    @Generated(hash = 864531661)
    public TCListenerTable(Long localid, Long bookID, String bookname, int epis,
                           String zname, String is_download, String path, String online,
                           int listenerStatus, int duration, int progress, String displayProgress,
                           String is_collect, String is_history, String is_local, String remark1,
                           String remark2) {
        this.localid = localid;
        this.bookID = bookID;
        this.bookname = bookname;
        this.epis = epis;
        this.zname = zname;
        this.is_download = is_download;
        this.path = path;
        this.online = online;
        this.listenerStatus = listenerStatus;
        this.duration = duration;
        this.progress = progress;
        this.displayProgress = displayProgress;
        this.is_collect = is_collect;
        this.is_history = is_history;
        this.is_local = is_local;
        this.remark1 = remark1;
        this.remark2 = remark2;
    }
    @Generated(hash = 1405807861)
    public TCListenerTable() {
    }
    public Long getLocalid() {
        return this.localid;
    }
    public void setLocalid(Long localid) {
        this.localid = localid;
    }
    public Long getBookID() {
        return this.bookID;
    }
    public void setBookID(Long bookID) {
        this.bookID = bookID;
    }
    public String getBookname() {
        return this.bookname;
    }
    public void setBookname(String bookname) {
        this.bookname = bookname;
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
    public String getDisplayProgress() {
        return this.displayProgress;
    }
    public void setDisplayProgress(String displayProgress) {
        this.displayProgress = displayProgress;
    }
    public String getIs_collect() {
        return this.is_collect;
    }
    public void setIs_collect(String is_collect) {
        this.is_collect = is_collect;
    }
    public String getIs_history() {
        return this.is_history;
    }
    public void setIs_history(String is_history) {
        this.is_history = is_history;
    }
    public String getIs_local() {
        return this.is_local;
    }
    public void setIs_local(String is_local) {
        this.is_local = is_local;
    }
    public String getRemark1() {
        return this.remark1;
    }
    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }
    public String getRemark2() {
        return this.remark2;
    }
    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }


}
