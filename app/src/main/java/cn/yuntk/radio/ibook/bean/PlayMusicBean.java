package cn.yuntk.radio.ibook.bean;

/*正在播放的音频*/
public class PlayMusicBean {

    private Long id;
    private long songId; // [本地]歌曲ID
    private int type; // 歌曲类型:本地1/网络0 可以判断是否下载
    private String title; // 音乐标题
    private String artist; // 艺术家
    private String album; // 专辑
    private long albumId; // [本地]专辑ID
    private String coverPath; // [在线]专辑封面路径
    private long duration; // 持续时间
    private String fileName; // [本地]文件名
    private long fileSize; // [本地]文件大小
    private String path; // 播放地址
    private String book_con; // 小说简介
    private String zj_title; // 章节标题
    private int zj_id; // 章节id
    private String mark_1; // 备用字段1
    private String mark_2; // 备用字段2
    private String mark;//不存储数据库
    private String is_collect; // 是否收藏 0未收藏1收藏
    private String is_history; // 是否历史记录 0未记录1记录
    private long current_progress;//当前进度

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBook_con() {
        return book_con;
    }

    public void setBook_con(String book_con) {
        this.book_con = book_con;
    }

    public String getZj_title() {
        return zj_title;
    }

    public void setZj_title(String zj_title) {
        this.zj_title = zj_title;
    }

    public int getZj_id() {
        return zj_id;
    }

    public void setZj_id(int zj_id) {
        this.zj_id = zj_id;
    }

    public String getMark_1() {
        return mark_1;
    }

    public void setMark_1(String mark_1) {
        this.mark_1 = mark_1;
    }

    public String getMark_2() {
        return mark_2;
    }

    public void setMark_2(String mark_2) {
        this.mark_2 = mark_2;
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

    public long getCurrent_progress() {
        return current_progress;
    }

    public void setCurrent_progress(long current_progress) {
        this.current_progress = current_progress;
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
                ", current_progress=" + current_progress +
                '}';
    }
}
