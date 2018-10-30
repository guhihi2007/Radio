package cn.yuntk.radio.ibook.common;

/**
 * Created by john
 * on 2016/11/15.
 */
public class Constants {

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

    public static final String ISNIGHT = "isNight";

    //播放器设置
    public static final String BOOK_DETAIL="book_detail";//书籍详情 包含目录
    public static final String BOOK_URL="book_mp3_url";//音频前缀
    public static final String PLAY_POSITION = "play_position";
    public static final String PLAY_MODE = "play_mode";
    public static final String SPLASH_URL = "splash_url";
    public static final String NIGHT_MODE = "night_mode";
    public static final String BOOK_ID = "near_book_id";//保存最新打开的小说id
    public static final String STORYTELLING_MUSIC_="storytelling_music";//保存正在播放的 音频内容
    public static final String BOOK_SVV_ID ="book_svv_id";//保存最新听书vvid
    public static final String BOOK_SEARCH_HISTORY="book_search_history";//保存搜索记录
    public static final String BOOK_STATUS="book_status_book_id";//保存被倒序的书籍id
    public static final String BOOK_DOWNLOAD_RECORD1="book_download_record1";//下载记录

    //    进入详情页面的参数
    public static final String PLAY_PAGE_TYPE = "play_page_type";
    public static final String PLAY_PAGE_DATA_ID = "play_page_data_id";
    public static final String PLAY_PAGE_SVV_ID="play_page_svv_id";
    public static final String PLAY_PAGE_TITLE="play_page_title";
    public static final String PLAY_PAGE_TITLE_NAME="play_page_title_name";
    public static final String PLAY_PAGE_BOOK_ID="play_page_book_id";
    public static final String PLAY_PAGE_CON="play_page_con";
    public static final String PLAY_PAGE_DATA_ID_IN_HISTORY = "play_page_data_id_in_history";
    public static final String PLAY_PAGE_TITLE_NAME_IN_HISTORY="play_page_title_name_in_history";
    public static final String CLICK_POSITION= "click_position";//目录列表点击位置记录


    //刷新收藏
    public static final String UPDATA_COLLECT = "updata_collect";
    //刷新历史 下载
    public static final String UPDATA_FRAGMENT4 = "updata_fragment4";

    public static final String FEEDBACK_KEY = "24941049";
    public static final String FEEDBACK_SECRET = "c9ad5accdbcbb14ca4769d8d50383f19";

    //    音频前缀
    public static final String YTK_PREFIX_STR="ytk_prefix_str";

    //  User-Agen
    public static final String UAConfig = "wotingpingshu/1.1.8";
    public static final String UAConfig_ = "uaconfig_";
}
