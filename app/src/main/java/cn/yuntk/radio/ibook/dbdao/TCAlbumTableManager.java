package cn.yuntk.radio.ibook.dbdao;

import android.content.Context;


import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.bean.TCAlbumTable;
import cn.yuntk.radio.ibook.bean.TCBean3;
import cn.yuntk.radio.ibook.dbdao.local.DaoSession;
import cn.yuntk.radio.ibook.dbdao.local.TCAlbumTableDao;


/** 查询章节进度的数据库操作 */
public class TCAlbumTableManager {

    private static TCAlbumTableManager mInstance;
    private static Context mContext;
    private TCAlbumTableDao beanDao;

    public TCAlbumTableDao getBeanDao() {
        return beanDao;
    }

    private TCAlbumTableManager() {
    }

    public static TCAlbumTableManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized ((TCAlbumTableManager.class)) {
                mContext = context;
                mInstance = new TCAlbumTableManager();
                DaoSession daoSession = XApplication.getDaoSession(mContext);
                mInstance.beanDao = daoSession.getTCAlbumTableDao();
            }
        }
        return mInstance;
    }

    /**提供外界调用的数据库操作接口*/
    public void delAll(){deleteAllData();}

    public void delMusicByKey(Long key){deleteByKey(key);}

    public void delBookList(List<TCAlbumTable> infos){deleteList(infos);}
    //更新一个数据
    public void updateMusic(TCAlbumTable info){updateData(info);}
    //插入一个数据
    public void insertMusic(TCAlbumTable info){insertData(info);}
    //插入一个集合
    public void insertMusicList(List<TCAlbumTable> infos){insertDataList(infos);}
    //更新一个集合
    public void updataList(List<TCAlbumTable> infos){
        updateDataList(infos);
    }

    //根据主键查询一个数据
    public TCAlbumTable queryBeanBykey(Long key){
        return beanDao.load(key);
    }
    //查询表中所有数据
    public List<TCAlbumTable> queryListDB() {
        return mInstance.queryDataList();
    }


    //    查询收藏数据
    public List<TCAlbumTable> queryCollectionList() {
        QueryBuilder<TCAlbumTable> qb = beanDao.queryBuilder().where(TCAlbumTableDao.Properties.Is_collect.eq("1"));
        List<TCAlbumTable> list = qb.list();
        return list;
    }

    //    查询历史数据
    public List<TCAlbumTable> queryHistoryList() {
        QueryBuilder<TCAlbumTable> qb = beanDao.queryBuilder().where(TCAlbumTableDao.Properties.Is_history.eq("1"));
        List<TCAlbumTable> list = qb.list();
        return list;
    }

    //    查询 历史 收藏的数据
    public List<TCAlbumTable> queryHCList(String h, String c) {
        QueryBuilder<TCAlbumTable> qb = beanDao.queryBuilder().where(
                TCAlbumTableDao.Properties.Is_history.eq(h),
                TCAlbumTableDao.Properties.Is_collect.eq(c));
        List<TCAlbumTable> list = qb.list();
        return list;
    }

    /**
     *TCAlbumTable 转换 TCBean3
     * */
    public static List<TCBean3> tCAlbumTable2TCBean3(List<TCAlbumTable> musics){
        List<TCBean3> bookBeans = new ArrayList<TCBean3>();
        if (musics!=null&&musics.size()!=0){
            for (TCAlbumTable music:musics){
                TCBean3 bookBean = new TCBean3();
                bookBean.setBookID(Integer.valueOf((music.getBookID()+"")));
                bookBean.setBookName(music.getBookName());
                bookBean.setHostName(music.getHostName());
                bookBean.setBookPhoto(music.getBookPhoto());
                bookBean.setIntro(music.getIntro());
                bookBean.setPlayNum(music.getPlayNum());
                bookBean.setBookType(music.getRemark1());
                bookBeans.add(bookBean);
            }
        }
        return bookBeans;
    }

    //插入一个数据
    private void insertData(TCAlbumTable music) {
        beanDao.insertInTx(music);
    }

    //插入一个数据集合
    private void insertDataList(List<TCAlbumTable> infos){
        beanDao.insertOrReplaceInTx(infos);
    }

    //删除所有数据
    private void deleteAllData() {
        beanDao.deleteAll();
    }

    //通过主键删除数据
    private void deleteByKey(Long key){
        beanDao.deleteByKey(key);
    }

    //删除一个数据集合
    private void deleteList(List<TCAlbumTable> infos){
        beanDao.deleteInTx(infos);
    }

    //更新一个数据
    private void updateData(TCAlbumTable music) {
        beanDao.update(music);
    }

    //更新一个数据集合
    private void updateDataList(List<TCAlbumTable> infos){
        beanDao.updateInTx(infos);
    }

    //查询表中所有数据
    private List<TCAlbumTable> queryDataList() {
        QueryBuilder<TCAlbumTable> qb = beanDao.queryBuilder();
        List<TCAlbumTable> list = qb.list();
        return list;
    }

}