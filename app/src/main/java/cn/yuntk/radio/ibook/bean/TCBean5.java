package cn.yuntk.radio.ibook.bean;


import cn.yuntk.radio.ibook.base.RootBase;

/** 声音列表实体类 */
public class TCBean5 extends RootBase {

    /**
     * epis : 0
     * zname : 0001第0000章_开场.mp3
     * timesize : 22
     * filesize : 95757
     */


    private int bookID;
    private String bookname;
    private String typeid;//获取播放地址时候的专辑type
    private String bookPhoto;//专辑封面
    private String intro;//专辑简介
    private String hostName;//播讲
    private int playNum;//播放量

    private int epis;
    private String zname;
    private String timesize;
    private String filesize;

    private String is_download = "0";//0未下载1下载成功2正在下载3等待中
    private String path = "";//下载的路径
    private int listenerStatus = 0;//播放状态 0//未播放 1播放了一段 2已播放完成
    private int duration;//总时长
    private int progress;//进度
    private String displayProgress;//显示百分比

    private String is_collect; // 是否收藏 0未收藏1收藏
    private String is_history; // 是否历史记录 0未记录1记录
    private String is_local;//是否本地 0不是1已下载
    private String online;//线上地址

    private String remark1;//备用字段1
    private String remark2;//备用字段2

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getTypeid() {
        return typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public String getBookPhoto() {
        return bookPhoto;
    }

    public void setBookPhoto(String bookPhoto) {
        this.bookPhoto = bookPhoto;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPlayNum() {
        return playNum;
    }

    public void setPlayNum(int playNum) {
        this.playNum = playNum;
    }

    public String getIs_collect() {
        return is_collect;
    }

    public void setIs_collect(String is_collect) {
        this.is_collect = is_collect;
    }

    public String getIs_history() {
        return is_history;
    }

    public void setIs_history(String is_history) {
        this.is_history = is_history;
    }

    public String getIs_local() {
        return is_local;
    }

    public void setIs_local(String is_local) {
        this.is_local = is_local;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    public int getEpis() {
        return epis;
    }

    public void setEpis(int epis) {
        this.epis = epis;
    }

    public String getZname() {
        return zname;
    }

    public void setZname(String zname) {
        this.zname = zname;
    }

    public String getTimesize() {
        return timesize;
    }

    public void setTimesize(String timesize) {
        this.timesize = timesize;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getIs_download() {
        return is_download;
    }

    public void setIs_download(String is_download) {
        this.is_download = is_download;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getListenerStatus() {
        return listenerStatus;
    }

    public void setListenerStatus(int listenerStatus) {
        this.listenerStatus = listenerStatus;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getDisplayProgress() {
        return displayProgress;
    }

    public void setDisplayProgress(String displayProgress) {
        this.displayProgress = displayProgress;
    }

    @Override
    public String toString() {
        return "{" +
                "epis=" + epis +
                ", zname='" + zname + '\'' +
                ", timesize='" + timesize + '\'' +
                ", filesize='" + filesize + '\'' +
                ", is_download='" + is_download + '\'' +
                ", path='" + path + '\'' +
                ", listenerStatus=" + listenerStatus +
                ", duration=" + duration +
                ", progress=" + progress +
                ", displayProgress='" + displayProgress + '\'' +
                '}';
    }
}
