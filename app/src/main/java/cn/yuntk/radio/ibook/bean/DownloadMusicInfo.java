package cn.yuntk.radio.ibook.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * Created by hzwangchenyan on 2017/8/11.
 */
@Entity(nameInDb = "dao_download_book_chapter")
public class DownloadMusicInfo  implements Serializable {

    static final long serialVersionUID = 48L;

    private String book_title;//书籍标题
    private String book_type;//书籍类型
    private String title;//章节标题
    private String musicPath;//本地地址
    private String pathOnline;//线上地址
    private String book_id;//书籍id

    @Id(autoincrement = false)
    @Property(nameInDb = "data_id")
    private String data_id;//章节id

    @Property(nameInDb = "book_chapter_status")
    private String book_chapter_status;//下载状态

    @Property(nameInDb = "mark1")
    private String mark1;//下载状态 0等待中1成功2失败3下载中

    @Transient
    private int taskid = -1;//相对列表的位置

    public DownloadMusicInfo(String book_title, String book_type, String title,
                             String book_id, String data_id,
                             String musicPath, String pathOnline) {
        this.book_title = book_title;
        this.book_type = book_type;
        this.title = title;
        this.book_id = book_id;
        this.data_id = data_id;
        this.musicPath = musicPath;
        this.pathOnline = pathOnline;
    }

    @Generated(hash = 1296873479)
    public DownloadMusicInfo(String book_title, String book_type, String title,
                             String musicPath, String pathOnline, String book_id, String data_id,
                             String book_chapter_status, String mark1) {
        this.book_title = book_title;
        this.book_type = book_type;
        this.title = title;
        this.musicPath = musicPath;
        this.pathOnline = pathOnline;
        this.book_id = book_id;
        this.data_id = data_id;
        this.book_chapter_status = book_chapter_status;
        this.mark1 = mark1;
    }

    @Generated(hash = 9658227)
    public DownloadMusicInfo() {
    }

    public String getTitle() {
        return title;
    }

    public String getBook_id() {
        return book_id;
    }

    public String getData_id() {
        return data_id;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public String getPathOnline() {
        return pathOnline;
    }

    public String getBook_title() {
        return book_title;
    }

    public String getBook_type() {
        return book_type;
    }

    public String getBook_chapter_status() {
        return book_chapter_status;
    }

    public void setBook_chapter_status(String book_chapter_status) {
        this.book_chapter_status = book_chapter_status;
    }

    public String getMark1() {
        return mark1;
    }

    public void setMark1(String mark1) {
        this.mark1 = mark1;
    }

    public int getTaskid() {
        return taskid;
    }

    public void setTaskid(int taskid) {
        this.taskid = taskid;
    }

    @Override
    public String toString() {
        return "{" +
                "\"book_title\":\"" + book_title + "\"" +
                ", \"book_type\":\"" + book_type + "\"" +
                ", \"title\":\"" + title + "\"" +
                ", \"musicPath\":\"" + musicPath + "\"" +
                ", \"pathOnline\":\"" + pathOnline + "\"" +
                ", \"book_id\":\"" + book_id + "\"" +
                ", \"data_id\":\"" + data_id + "\"" +
                ", \"mark1\":\"" + mark1 + "\"" +
                ", \"book_chapter_status\":\"" + book_chapter_status + "\"" +
                ", \"taskid\":\"" + taskid + "\"" +
                "}";
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public void setBook_type(String book_type) {
        this.book_type = book_type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public void setPathOnline(String pathOnline) {
        this.pathOnline = pathOnline;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public void setData_id(String data_id) {
        this.data_id = data_id;
    }
}
