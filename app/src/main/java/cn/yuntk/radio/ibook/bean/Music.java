package cn.yuntk.radio.ibook.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * 单曲信息
 * Created by wcy on 2015/11/27.
 */
@Entity(nameInDb = "SystemMessage")
public class Music implements Serializable {
    private static final long serialVersionUID = 536871008;

    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private Long id;

    @Property(nameInDb = "songId")
    private long songId; // [本地]歌曲ID
    @NotNull
    @Property(nameInDb = "type")
    private int type; // 歌曲类型:本地1/网络0 可以判断是否下载
    @Property(nameInDb = "title")
    private String title; // 音乐标题
    @Property(nameInDb = "artist")
    private String artist; // 艺术家
    @Property(nameInDb = "album")
    private String album; // 专辑 小说类型
    @Property(nameInDb = "albumId")
    private long albumId; // [本地]专辑ID
    @Property(nameInDb = "coverPath")
    private String coverPath; // [在线]专辑封面路径
    @NotNull
    @Property(nameInDb = "duration")
    private long duration; // 持续时间
    @Property(nameInDb = "fileName")
    private String fileName; // [本地]文件名
    @NotNull
    @Property(nameInDb = "fileSize")
    private long fileSize; // [本地]文件大小
    @Property(nameInDb = "path")
    private String path; // 播放地址

    @Property(nameInDb = "book_con")
    private String book_con; // 小说简介
    @Property(nameInDb = "zj_title")
    private String zj_title; // 章节标题
    @NotNull
    @Property(nameInDb = "zj_id")
    private int zj_id; // 章节id
    @Property(nameInDb = "mark_1")
    private String mark_1; // 备用字段1 svid
    @Property(nameInDb = "mark_2")
    private String mark_2; // 备用字段2 data_id

    @Transient
    private String mark;//不存储数据库

    @Property(nameInDb = "is_collect")
    private String is_collect; // 是否收藏 0未收藏1收藏
    @Property(nameInDb = "is_history")
    private String is_history; // 是否历史记录 0未记录1记录

    @Generated(hash = 483444878)
    public Music(Long id, long songId, int type, String title, String artist, String album,
                 long albumId, String coverPath, long duration, String fileName, long fileSize, String path,
                 String book_con, String zj_title, int zj_id, String mark_1, String mark_2,
                 String is_collect, String is_history) {
        this.id = id;
        this.songId = songId;
        this.type = type;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.albumId = albumId;
        this.coverPath = coverPath;
        this.duration = duration;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.path = path;
        this.book_con = book_con;
        this.zj_title = zj_title;
        this.zj_id = zj_id;
        this.mark_1 = mark_1;
        this.mark_2 = mark_2;
        this.is_collect = is_collect;
        this.is_history = is_history;
    }

    @Generated(hash = 1263212761)
    public Music() {
    }


    public interface Type {
        int LOCAL = 0;
        int ONLINE = 1;
    }

    public interface Collect_Type {
        String NO = "0";
        String YES = "1";
    }

    public interface History_Type {
        String NO = "0";
        String YES = "1";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Music)) {
            return false;
        }
        Music music = (Music) o;
        if (music.songId > 0 && music.songId == this.songId&&albumId>0&&music.albumId == this.albumId) {
            return true;
        }
        return false;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getBook_con() {
        return this.book_con;
    }

    public void setBook_con(String book_con) {
        this.book_con = book_con;
    }

    public String getZj_title() {
        return this.zj_title;
    }

    public void setZj_title(String zj_title) {
        this.zj_title = zj_title;
    }

    public int getZj_id() {
        return this.zj_id;
    }

    public void setZj_id(int zj_id) {
        this.zj_id = zj_id;
    }

    public String getMark_1() {
        return this.mark_1;
    }

    public void setMark_1(String mark_1) {
        this.mark_1 = mark_1;
    }

    public String getMark_2() {
        return this.mark_2;
    }

    public void setMark_2(String mark_2) {
        this.mark_2 = mark_2;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", songId=" + songId +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", albumId=" + albumId +
                ", coverPath='" + coverPath + '\'' +
                ", duration=" + duration +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", path='" + path + '\'' +
                ", book_con='" + book_con + '\'' +
                ", zj_title='" + zj_title + '\'' +
                ", zj_id=" + zj_id +
                ", mark_1='" + mark_1 + '\'' +
                ", mark_2='" + mark_2 + '\'' +
                ", mark='" + mark + '\'' +
                ", is_collect='" + is_collect + '\'' +
                ", is_history='" + is_history + '\'' +
                '}';
    }

}