package cn.yuntk.radio.ibook.bean;

import com.google.gson.annotations.SerializedName;

/**
 * JavaBean
 * Created by wcy on 2015/12/20.
 */
public class OnlineMusic {
    @SerializedName("pic_big")
    private String pic_big;
    @SerializedName("pic_small")
    private String pic_small;
    @SerializedName("lrclink")
    private String lrclink;
    @SerializedName("song_id")
    private String song_id;//书籍id
    @SerializedName("title")
    private String title;//章节标题
    @SerializedName("ting_uid")
    private String ting_uid;
    @SerializedName("album_title")
    private String album_title;//书籍标题
    @SerializedName("artist_name")
    private String artist_name;
    @SerializedName("svv_id")
    private String svv_id;//书籍svvid
    @SerializedName("data_id")
    private String data_id;//章节data_id
    @SerializedName("mp3_url")
    private String mp3_url;//在线播放地址
    @SerializedName("file_path")
    private String file_path;//本地存放地址
    @SerializedName("book_type")
    private String book_type;//书籍类型


    public String getBook_type() {
        return book_type;
    }

    public void setBook_type(String book_type) {
        this.book_type = book_type;
    }

    public String getMp3_url() {
        return mp3_url;
    }

    public void setMp3_url(String mp3_url) {
        this.mp3_url = mp3_url;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getSvv_id() {
        return svv_id;
    }

    public void setSvv_id(String svv_id) {
        this.svv_id = svv_id;
    }

    public String getData_id() {
        return data_id;
    }

    public void setData_id(String data_id) {
        this.data_id = data_id;
    }

    public String getPic_big() {
        return pic_big;
    }

    public void setPic_big(String pic_big) {
        this.pic_big = pic_big;
    }

    public String getPic_small() {
        return pic_small;
    }

    public void setPic_small(String pic_small) {
        this.pic_small = pic_small;
    }

    public String getLrclink() {
        return lrclink;
    }

    public void setLrclink(String lrclink) {
        this.lrclink = lrclink;
    }

    public String getSong_id() {
        return song_id;
    }

    public void setSong_id(String song_id) {
        this.song_id = song_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTing_uid() {
        return ting_uid;
    }

    public void setTing_uid(String ting_uid) {
        this.ting_uid = ting_uid;
    }

    public String getAlbum_title() {
        return album_title;
    }

    public void setAlbum_title(String album_title) {
        this.album_title = album_title;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }
}
