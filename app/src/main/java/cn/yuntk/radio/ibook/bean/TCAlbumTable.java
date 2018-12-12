package cn.yuntk.radio.ibook.bean;


import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.Serializable;
import java.util.List;
import cn.yuntk.radio.ibook.dbdao.local.DaoSession;
import cn.yuntk.radio.ibook.dbdao.local.TCListenerTableDao;
import cn.yuntk.radio.ibook.dbdao.local.TCAlbumTableDao;

/** 专辑收藏历史表 */
@Entity(nameInDb = "tc_album_list")
public class TCAlbumTable implements Serializable {

    static final long serialVersionUID = 170L;

    @Id(autoincrement = false)
    private Long bookID;
    private String bookName;
    private String bookPhoto;
    private String hostName;
    private String intro;
    private int playNum;

    private String is_collect; // 是否收藏 0未收藏1收藏
    private String is_history; // 是否历史记录 0未记录1记录

    private String remark1;//备用字段1 专辑类型 1小说2相声
    private String remark2;//备用字段2

    @ToMany(joinProperties = {
            @JoinProperty(name = "bookID", referencedName = "bookID")
    })
    private List<TCListenerTable> musics;//章节进度

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1233831041)
    private transient TCAlbumTableDao myDao;

    @Generated(hash = 216061835)
    public TCAlbumTable(Long bookID, String bookName, String bookPhoto,
                        String hostName, String intro, int playNum, String is_collect,
                        String is_history, String remark1, String remark2) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.bookPhoto = bookPhoto;
        this.hostName = hostName;
        this.intro = intro;
        this.playNum = playNum;
        this.is_collect = is_collect;
        this.is_history = is_history;
        this.remark1 = remark1;
        this.remark2 = remark2;
    }

    @Generated(hash = 1563527187)
    public TCAlbumTable() {
    }

    public Long getBookID() {
        return this.bookID;
    }

    public void setBookID(Long bookID) {
        this.bookID = bookID;
    }

    public String getBookName() {
        return this.bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookPhoto() {
        return this.bookPhoto;
    }

    public void setBookPhoto(String bookPhoto) {
        this.bookPhoto = bookPhoto;
    }

    public String getHostName() {
        return this.hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIntro() {
        return this.intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getPlayNum() {
        return this.playNum;
    }

    public void setPlayNum(int playNum) {
        this.playNum = playNum;
    }

    public String getIs_collect() {
        return this.is_collect;
    }

    public void setIs_collect(String is_collect) {
        this.is_collect = is_collect;
    }

    public String getIs_history() {
        return this.is_history;
    }

    public void setIs_history(String is_history) {
        this.is_history = is_history;
    }

    public String getRemark1() {
        return this.remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public String getRemark2() {
        return this.remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 748879808)
    public List<TCListenerTable> getMusics() {
        if (musics == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TCListenerTableDao targetDao = daoSession.getTCListenerTableDao();
            List<TCListenerTable> musicsNew = targetDao
                    ._queryTCAlbumTable_Musics(bookID);
            synchronized (this) {
                if (musics == null) {
                    musics = musicsNew;
                }
            }
        }
        return musics;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1369131536)
    public synchronized void resetMusics() {
        musics = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1443278725)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTCAlbumTableDao() : null;
    }

}
