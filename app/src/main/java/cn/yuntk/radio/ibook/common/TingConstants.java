package cn.yuntk.radio.ibook.common;

/**
 * Created by john
 * on 2016/11/15.
 */
public class TingConstants {

    public static final int PAGE_SIZE = 10;

    //获取屏幕宽度、高度
    public static int width = 0;
    public static int height = 0;
    public static float screenDensity = 0;

    public static String provinceName = "广东省";
    public static String cityName = "深圳市";
    public static String areaName = "罗湖区";
    public static double latitude = 22.546092;
    public static double longitude = 114.12146;
    public static String IS_LOGIN = "loginInfo";

    public static String T_BUGLY_APPKEY ="b101a17a71";

    public static final String ISNIGHT = "isNight";

    //播放器设置
    public static final String ALBUM_DETAIL ="album_detail";//专辑详情
    public static final String ALBUM_TRACK="album_track";//专辑目录

    public static final String BOOK_STATUS="book_status_book_id";//保存被倒序的书籍id

    public static final String PLAY_POSITION = "play_position";//播放位置
    public static final String PLAY_MODE = "play_mode";
    public static final String NIGHT_MODE = "night_mode";
    public static final String BOOK_ID = "near_book_id";//保存最新打开的小说id
    public static final String BOOK_SEARCH_HISTORY="book_search_history";//保存搜索记录
    public static final String BOOK_DOWNLOAD_RECORD1="book_download_record1";//下载记录

    //    进入详情页面的参数
    public static final String PLAY_PAGE_BOOK_ID="play_page_book_id";//专辑id
    public static final String PLAY_PAGE_TYPE = "play_page_type";//专辑类型 小说1 相声2
    public static final String PLAY_PAGE_TITLE="play_page_title";//专辑名字
    public static final String PLAY_PAGE_TITLE_NAME="play_page_title_name";//章节名字
    public static final String PLAY_PAGE_EPISODES="play_page_episodes";//章节id
    public static final String PLAY_PAGE_CON="play_page_con";//专辑简介
    public static final String PLAY_PAGE_PIC="play_page_pic";//专辑图片
    public static final String PLAY_CLICK_POSITION= "play_click_position";//目录列表点击位置记录(播放器播放列表播放位置)


    public static final String UPDATA_COLLECT = "updata_collect";//刷新收藏
    public static final String UPDATA_HISTORY = "updata_history";//刷新历史
    public static final String UPDATA_LISTENER_PROGRESS = "updata_listener_progress";//更新收听进度

    public static final String FEEDBACK_KEY = "24941049";
    public static final String FEEDBACK_SECRET = "c9ad5accdbcbb14ca4769d8d50383f19";

    //   音频前缀
    public static final String YTK_PREFIX_STR="ytk_prefix_str";
    //   音频播放速度等级
    public static final String LISTENER_SPEED = "listener_speed";
    //  User-Agen
    public static final String UAConfig = "wotingpingshu/1.1.8";
    public static final String UAConfig_ = "uaconfig_";


    //    全本小说大全微下载链接
    public static final String QBXS_Simple_download = "http://a.app.qq.com/o/simple.jsp?pkgname=cn.yuntk.novel.all";
    //    热门听书大全微下载链接
    public static  final String RMTS_Simple_download = "http://a.app.qq.com/o/simple.jsp?pkgname=com.yuntk.listener";
    //    懒人听小说微下载链接
    public static final String LRTXS_Simple_download = "http://a.app.qq.com/o/simple.jsp?pkgname=com.yuntk.ibook";

    //  缓存命名
    public static final String ACACHE_CLASSIFY ="acache_classify";//分类

}
