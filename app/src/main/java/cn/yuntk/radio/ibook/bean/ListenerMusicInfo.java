package cn.yuntk.radio.ibook.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by xue on 2018/8/11.
 * 收听进度
 */
@Entity(nameInDb = "dao_listener_chapter")
public class ListenerMusicInfo implements Serializable {

    static final long serialVersionUID = 64L;

    private String book_id;//书籍id
    @Id(autoincrement = false)
    private String data_id;//章节id
    private String book_title;//书籍标题
    private String title;//章节标题
    private String musicPath;//本地地址
    private String pathOnline;//线上地址
    private String book_chapter_status;//下载状态
    private int duration;//总时长
    private int progress;//进度
    private int listenerStatus;//播放状态 0//未播放 1播放了一段 2已播放完成
    private String mark1;//备用字段

    @Generated(hash = 1369400537)
    public ListenerMusicInfo(String book_id, String data_id, String book_title,
                             String title, String musicPath, String pathOnline,
                             String book_chapter_status, int duration, int progress,
                             int listenerStatus, String mark1) {
        this.book_id = book_id;
        this.data_id = data_id;
        this.book_title = book_title;
        this.title = title;
        this.musicPath = musicPath;
        this.pathOnline = pathOnline;
        this.book_chapter_status = book_chapter_status;
        this.duration = duration;
        this.progress = progress;
        this.listenerStatus = listenerStatus;
        this.mark1 = mark1;
    }

    @Generated(hash = 125259397)
    public ListenerMusicInfo() {
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getData_id() {
        return data_id;
    }

    public void setData_id(String data_id) {
        this.data_id = data_id;
    }

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public String getPathOnline() {
        return pathOnline;
    }

    public void setPathOnline(String pathOnline) {
        this.pathOnline = pathOnline;
    }

    public String getBook_chapter_status() {
        return book_chapter_status;
    }

    public void setBook_chapter_status(String book_chapter_status) {
        this.book_chapter_status = book_chapter_status;
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

    public int getListenerStatus() {
        return listenerStatus;
    }

    public void setListenerStatus(int listenerStatus) {
        this.listenerStatus = listenerStatus;
    }

    public String getMark1() {
        return mark1;
    }

    public void setMark1(String mark1) {
        this.mark1 = mark1;
    }
}
