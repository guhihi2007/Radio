package cn.yuntk.radio.ibook.dbdao;

import android.content.Context;
import android.util.Log;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.bean.DownloadBookInfo;
import cn.yuntk.radio.ibook.dbdao.local.DaoSession;
import cn.yuntk.radio.ibook.dbdao.local.DownloadBookInfoDao;


/** 下载音频的书籍信息 */
public class DownloadBookInfoManager {

    private static DownloadBookInfoManager mInstance;
    private static Context mContext;
    private DownloadBookInfoDao musicDao;

    public DownloadBookInfoDao getMusicDao() {
        return musicDao;
    }

    private DownloadBookInfoManager() {
    }

    public static DownloadBookInfoManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized ((DownloadMusicInfoManager.class)) {
                mContext = context;
                mInstance = new DownloadBookInfoManager();
                DaoSession daoSession = XApplication.getDaoSession(mContext);
                mInstance.musicDao = daoSession.getDownloadBookInfoDao();
            }
        }
        return mInstance;
    }


    /** 供外界调用得增删改查 */
    /*
     *删除所有数据 记录
     * */
    public void deleteAll() {
        mInstance.deleteAllData();
    }

    /**
     *根据bookid删除数据
     * */
    public void deleteBtn(DownloadBookInfo bookInfo) {
        mInstance.deleteData(bookInfo);
    }

    /**
     *插入数据
     * */
    public void insertDB(DownloadBookInfo music) {
        mInstance.insertData(music);
    }

    /**
     *查询数据 根据bookid
     * */
    public DownloadBookInfo queryListDB(String bookid) {
        DownloadBookInfo music = null;
        List<DownloadBookInfo> musicList = mInstance.queryDataList();
        for (DownloadBookInfo u : musicList) {
            if (u.getId().equals(bookid)){
                music = u;
            }
        }
        return music;
    }

    /**
     *查询所有下载的书籍
     * */
    public List<DownloadBookInfo> queryListDB() {
        return mInstance.queryDataList();
    }
    /**
     *更新数据
     * */
    public void updateBtn(DownloadBookInfo music) {
        List<DownloadBookInfo> musicList = mInstance.queryDataList();
        for (DownloadBookInfo m:musicList) {
            if (m.getId().equals(music.getId())) {
                mInstance.updateData(music);
                break;
            }
        }
    }

    /**
     * 打印数据库数据
     * */
    public void printDbTable(){
        List<DownloadBookInfo> musicList = mInstance.queryDataList();
        int leng = musicList.size();
        for (int i = 0; i<leng;i++){
            DownloadBookInfo u = musicList.get(i);
            Log.i("DownloadBookInfo:" , u.toString());
        }
    }

    private void insertData(DownloadBookInfo music) {
        musicDao.insertInTx(music);
    }

    private void deleteData(DownloadBookInfo music) {
        musicDao.delete(music);
    }

    private void deleteAllData() {
        musicDao.deleteAll();
    }

    private void updateData(DownloadBookInfo music) {
        musicDao.update(music);
    }

    private List<DownloadBookInfo> queryDataList() {
        QueryBuilder<DownloadBookInfo> qb = musicDao.queryBuilder();
        List<DownloadBookInfo> list = qb.list();
        return list;
    }

}