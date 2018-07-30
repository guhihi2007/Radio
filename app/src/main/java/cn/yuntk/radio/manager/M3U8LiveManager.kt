package cn.yuntk.radio.manager

import cn.yuntk.radio.m3u8.bean.M3U8
import cn.yuntk.radio.m3u8.bean.M3U8Ts
import cn.yuntk.radio.m3u8.bean.OnUpdateM3U8UrlListener
import cn.yuntk.radio.utils.LT
import cn.yuntk.radio.utils.logE
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * Author : Gupingping
 * Date : 2018/7/18
 * Mail : gu12pp@163.com
 */
object M3U8LiveManager {
    private var mUpdateM3U8UrlListener: OnUpdateM3U8UrlListener? = null
    private var basePath: String? = null
    private val m3U8TsList = ArrayList<M3U8Ts>()
    private var mUrl: String? = null
    var isCancel = false
    //每隔2秒刷新最新m3u8
    fun updateM3U8LatestUrl(url: String, onUpdateM3U8UrlListener: OnUpdateM3U8UrlListener) {
        mUrl = url
        mUpdateM3U8UrlListener = onUpdateM3U8UrlListener
        isCancel = false
        updateM3U8LatestUrlTask.start()
    }

    fun stop() {
        isCancel = true
        "updateM3U8LatestUrlTask stop--->true".logE(LT.RadioNet)
    }

    private val updateM3U8LatestUrlTask by lazy {
        launch(CommonPool) {
            if (mUrl != null) {
                "update UrlTask --->start".logE(LT.RadioNet)
                while (true) {
                    while (!isCancel) {
                        try {
                            val m3u8 = parseIndex(mUrl!!)
                            if (m3u8 != null && m3u8.tsList.size > 0) {
                                basePath = m3u8.basepath
                                addListUrl(m3u8.tsList)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            mUpdateM3U8UrlListener?.onError(e)
                        }
                    }
                    delay(1000)
                }
            }
        }
    }

    @Throws(Throwable::class)
    private fun parseIndex(url: String): M3U8? {
        val conn = URL(url).openConnection() as HttpURLConnection
        if (conn.responseCode == 200) {
            val realUrl = conn.url.toString()
            val reader = BufferedReader(InputStreamReader(conn.inputStream))
            val basepath = realUrl.substring(0, realUrl.lastIndexOf("/") + 1)
            val ret = M3U8()
            ret.basepath = basepath

            var line: String
            var seconds = 0f

            while (true) {
                line = reader.readLine() ?: break

                if (line.startsWith("#")) {
                    if (line.startsWith("#EXTINF:")) {
                        line = line.substring(8)
                        if (line.endsWith(",")) {
                            line = line.substring(0, line.length - 1)
                        }
                        seconds = java.lang.Float.parseFloat(line)
                    }
                    continue
                }

                if (line.endsWith("m3u8")) {
                    return parseIndex(basepath + line)
                }
                ret.addTs(M3U8Ts(line, seconds))
                seconds = 0f
            }
            reader.close()
            return ret
        } else {
            return null
        }
    }


    //去重，并回调更新
    private fun addListUrl(tsList: List<M3U8Ts>) {
        val tempTsList = ArrayList<M3U8Ts>()
        for (m3U8Ts in tsList) {
            var isExisted = false
            for (mTs in m3U8TsList) {
                if (mTs.file == m3U8Ts.file) {
                    isExisted = true
                    break
                }
            }
            if (!isExisted) {
                tempTsList.add(m3U8Ts)
                val urlPath: String = if ("http" == m3U8Ts.file.substring(0, 4)) {
                    m3U8Ts.file
                } else {
                    basePath + m3U8Ts.file
                }
                mUpdateM3U8UrlListener?.onUpdateAddress(urlPath)
            }
        }
        if (tempTsList.size > 0) {
            m3U8TsList.addAll(tempTsList)
        }
    }
}