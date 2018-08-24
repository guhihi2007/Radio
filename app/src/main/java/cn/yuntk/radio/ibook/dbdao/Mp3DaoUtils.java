package cn.yuntk.radio.ibook.dbdao;

import android.content.Context;

import cn.yuntk.radio.ibook.XApplication;
import cn.yuntk.radio.ibook.bean.ItemBookBean;
import cn.yuntk.radio.ibook.bean.Music;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Mp3DaoUtils {

    private Context context;

    public Mp3DaoUtils() {
        this.context = XApplication.getsInstance().getApplicationContext();
    }

    /*
     *删除所有数据 记录
     * */
    public void deleteAll() {
        MP3DaoManager dbManager = MP3DaoManager.getInstance(this.context);
        dbManager.deleteAll();
    }
    /*
     *插入数据
     * */
    public void insertDB(Music music) {
       MP3DaoManager dbManager = MP3DaoManager.getInstance(this.context);
       dbManager.insertMusic(music);
//        printDbTable();
    }

    /*
     *查询数据 根据bookid
     * */
    public Music queryListDB(String bookid) {
        Music music = null;
        MP3DaoManager dbManager = MP3DaoManager.getInstance(this.context);
        List<Music> musicList = dbManager.queryMusicList();
        for (Music u : musicList) {
            LogUtils.showLog("queryListDB:" + u.toString());
            if (u.getSongId() == Long.parseLong(bookid)){
                music = u;
            }
        }
        return music;
    }

    /*
     *查询数据 收藏
     * */
    public List<Music> queryListDB_Collect() {
        List<Music> musics = new ArrayList<Music>();
        MP3DaoManager dbManager = MP3DaoManager.getInstance(this.context);
        List<Music> musicList = dbManager.queryMusicList();
        for (Music u : musicList) {
            LogUtils.showLog("queryListDB_Collect:" + u.toString());
            if (!StringUtils.isEmpty(u.getIs_collect())&&u.getIs_collect().equals(Music.Collect_Type.YES)){
                musics.add(u);
            }
        }
        return musics;
    }

    /*
     *查询数据 历史
     * */
    public List<Music> queryListDB_History() {
        List<Music> musics = new ArrayList<Music>();
        MP3DaoManager dbManager = MP3DaoManager.getInstance(this.context);
        List<Music> musicList = dbManager.queryMusicList();
        for (Music u : musicList) {
            LogUtils.showLog("queryListDB_History:" + u.toString());
            if (!StringUtils.isEmpty(u.getIs_history())&&u.getIs_history().equals(Music.History_Type.YES)){
                musics.add(u);
            }
        }
        return musics;
    }

    /*
     *查询数据 下载
     * */
    public List<Music> queryListDB_DownLoad() {
        List<Music> musics = new ArrayList<Music>();
        MP3DaoManager dbManager = MP3DaoManager.getInstance(this.context);
        List<Music> musicList = dbManager.queryMusicList();
        for (Music u : musicList) {
            LogUtils.showLog("queryListDB_DownLoad:" + u.toString());
            if (u.getType() == Music.Type.LOCAL){
                musics.add(u);
            }
        }
        return musics;
    }

    /*
     *查询数据 无限制查询数据库
     * */
    public List<Music> queryListDB() {
        MP3DaoManager dbManager = MP3DaoManager.getInstance(this.context);
        List<Music> musicList = dbManager.queryMusicList();
        return musicList;
    }

    /*
     *根据bookid删除数据
     * */
    public void deleteBtn(String bookid) {
        MP3DaoManager dbManager = MP3DaoManager.getInstance(this.context);
        List<Music> musicList = dbManager.queryMusicList();
        for (Music music : musicList) {
            if (music.getSongId() == Long.parseLong(bookid)) {
                dbManager.deleteMusic(music);
            }
        }
    }

    /*
     *更新数据
     * */
    public void updateBtn(Music music) {
        MP3DaoManager dbManager = MP3DaoManager.getInstance(this.context);
        List<Music> musicList = dbManager.queryMusicList();
        for (Music m:musicList) {
            if (m.getSongId() == music.getSongId()) {
                dbManager.updateMusic(music);
            }
        }
    }

    /*打印数据库数据
    * */
    private void printDbTable(){
        MP3DaoManager dbManager = MP3DaoManager.getInstance(this.context);
        List<Music> musicList = dbManager.queryMusicList();
        int leng = musicList.size();
        for (int i = 0; i<leng;i++){
            Music u = musicList.get(i);
            LogUtils.showLog(":" + u.toString());
        }
    }

    /*
    *ItemBookBean 转换 Music
    * */
    public static List<ItemBookBean> music2book(List<Music> musics){
        List<ItemBookBean> bookBeans = new ArrayList<ItemBookBean>();
        if (musics!=null&&musics.size()!=0){
            for (Music music:musics){
                ItemBookBean bookBean = new ItemBookBean();
                bookBean.setId(music.getSongId()+"");
                bookBean.setTitle(music.getTitle());
                bookBean.setType(music.getAlbum());
                bookBeans.add(bookBean);
            }
        }
        return bookBeans;
    }
}