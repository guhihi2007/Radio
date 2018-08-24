package cn.yuntk.radio.ibook.bean;

/**
 * Created by hzwangchenyan on 2017/8/11.
 */
public class DownloadMusicInfo {

    private String book_title;//书籍标题
    private String book_type;//书籍类型
    private String title;//章节标题
    private String musicPath;//本地地址
    private String pathOnline;//线上地址
    private String book_id;//书籍id
    private String data_id;//章节id

    public DownloadMusicInfo(String book_title, String book_type, String title, String book_id, String data_id, String musicPath, String pathOnline) {
        this.book_title = book_title;
        this.book_type = book_type;
        this.title = title;
        this.book_id = book_id;
        this.data_id = data_id;
        this.musicPath = musicPath;
        this.pathOnline = pathOnline;
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
                "}";
    }
}
