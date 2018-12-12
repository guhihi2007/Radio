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
import cn.yuntk.radio.ibook.dbdao.local.ListenerMusicInfoDao;
import cn.yuntk.radio.ibook.dbdao.local.ListenerBookInfoDao;

/**
 * 收听的书籍 信息
 * */
@Entity(nameInDb = "dao_listener_book")
public class ListenerBookInfo implements Serializable {

    static final long serialVersionUID = 63L;

    @Id(autoincrement = false)
    private String id;//书籍id
    private String title;//书籍标题
    private String type;//书籍类型
    private String con;//书籍简介
    private String zztt;//状态

    @ToMany(joinProperties = {
            @JoinProperty(name = "id", referencedName = "book_id")
    })
    private List<ListenerMusicInfo> musics;//章节进度
    private String mark1;//备用字段

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1027138425)
    private transient ListenerBookInfoDao myDao;

    @Generated(hash = 374960133)
    public ListenerBookInfo(String id, String title, String type, String con,
                            String zztt, String mark1) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.con = con;
        this.zztt = zztt;
        this.mark1 = mark1;
    }

    @Generated(hash = 1957595078)
    public ListenerBookInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public String getZztt() {
        return zztt;
    }

    public void setZztt(String zztt) {
        this.zztt = zztt;
    }
    
    public String getMark1() {
        return mark1;
    }

    public void setMark1(String mark1) {
        this.mark1 = mark1;
    }

    @Override
    public String toString() {
        return "ListenerBookInfo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", con='" + con + '\'' +
                ", zztt='" + zztt + '\'' +
                ", mark1='" + mark1 + '\'' +
                '}';
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 407795884)
    public List<ListenerMusicInfo> getMusics() {
        if (musics == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ListenerMusicInfoDao targetDao = daoSession.getListenerMusicInfoDao();
            List<ListenerMusicInfo> musicsNew = targetDao
                    ._queryListenerBookInfo_Musics(id);
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
    @Generated(hash = 1493361539)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getListenerBookInfoDao() : null;
    }
}
