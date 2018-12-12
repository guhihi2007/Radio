package cn.yuntk.radio.ibook.bean;


import java.util.List;

import cn.yuntk.radio.ibook.base.RootBase;

/*小说详情*/
public class BookDetailBean extends PageInfo{

    /**
     * html_id : 6219
     * title : 黄金瞳
     * con : 作者：打眼 典当行工作的小职员庄睿，在一次意外中眼睛发生异变。 美轮美奂的陶瓷，古拙大方的青铜器，惊心动魄的赌石，惠质兰心的漂亮护士，冷若冰霜的豪门千金接踵而来，他的生活也随之产生了天翻地覆的变化。 眼生双瞳，财富人生 PS：感谢网友刘刚分享上传黄金瞳！
     * type : 言情通俗
     * svid : 50
     * zztt : 完结
     * time : 2012/6/16
     * url_list : [{"u":"1","url":"%E9%80%9A%E4%BF%97%E5%B0%8F%E8%AF%B4%2F%E9%BB%84%E9%87%91%E7%9E%B3%2F001.mp3","name":"第001回\u2014黄金瞳"},{"u":"2","url":"%E9%80%9A%E4%BF%97%E5%B0%8F%E8%AF%B4%2F%E9%BB%84%E9%87%91%E7%9E%B3%2F002.mp3","name":"第002回\u2014黄金瞳"},{"u":"3","url":"%E9%80%9A%E4%BF%97%E5%B0%8F%E8%AF%B4%2F%E9%BB%84%E9%87%91%E7%9E%B3%2F003.mp3","name":"第003回\u2014黄金瞳"},{"u":"1314","url":"%E9%80%9A%E4%BF%97%E5%B0%8F%E8%AF%B4%2F%E9%BB%84%E9%87%91%E7%9E%B3%2F1314.mp3","name":"第1314回\u2014黄金瞳"}]
     */

    private int html_id;
    private String title;
    private String con;
    private String type;
    private int svid;
    private String zztt;
    private String time;
    private List<UrlListBean> url_list;

    public int getHtml_id() {
        return html_id;
    }

    public void setHtml_id(int html_id) {
        this.html_id = html_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSvid() {
        return svid;
    }

    public void setSvid(int svid) {
        this.svid = svid;
    }

    public String getZztt() {
        return zztt;
    }

    public void setZztt(String zztt) {
        this.zztt = zztt;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<UrlListBean> getUrl_list() {
        return url_list;
    }

    public void setUrl_list(List<UrlListBean> url_list) {
        this.url_list = url_list;
    }

    public static class UrlListBean extends RootBase {
        /**
         * u : 1
         * url : %E9%80%9A%E4%BF%97%E5%B0%8F%E8%AF%B4%2F%E9%BB%84%E9%87%91%E7%9E%B3%2F001.mp3
         * name : 第001回—黄金瞳
         */

        private String u;
        private String url;
        private String name;
        private String is_download = "0";//0未下载1下载2正在下载
        private String path = "";//下载的路径

        private int listenerStatus = 0;//播放状态 0//未播放 1播放了一段 2已播放完成
        private int duration;//总时长
        private int progress;//进度
        private String displayProgress;//显示百分比

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

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getIs_download() {
            return is_download;
        }

        public void setIs_download(String is_download) {
            this.is_download = is_download;
        }

        public String getU() {
            return u;
        }

        public void setU(String u) {
            this.u = u;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
