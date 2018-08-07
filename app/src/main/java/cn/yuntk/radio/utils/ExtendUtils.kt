package cn.yuntk.radio.utils

/**
 * Created by Jone on 2018/3/27.
 */
import android.content.Context
import android.content.pm.ApplicationInfo
import android.text.TextUtils
import cn.yuntk.radio.BuildConfig
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * 检测是否处于debug模式。
 */
fun Context.isDebug(): Boolean {
    return try {
        this.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    } catch (e: Exception) {
        false
    }

}


fun String.decodeUnicode(): String {
    var aChar: Char
    val len = this.length
    val outBuffer = StringBuffer(len)
    var x = 0
    while (x < len) {
        aChar = this[x++]
        if (aChar == '\\') {
            aChar = this[x++]
            if (aChar == 'u') {
                // Read the xxxx
                var value = 0
                for (i in 0..3) {
                    aChar = this[x++]
                    when (aChar) {
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> value = (value shl 4) + aChar.toInt() - '0'.toInt()
                        'a', 'b', 'c', 'd', 'e', 'f' -> value = (value shl 4) + 10 + aChar.toInt() - 'a'.toInt()
                        'A', 'B', 'C', 'D', 'E', 'F' -> value = (value shl 4) + 10 + aChar.toInt() - 'A'.toInt()
                        else -> throw IllegalArgumentException("Malformed   \\uxxxx   encoding.")
                    }
                }
                outBuffer.append(value.toChar())
            } else {
                if (aChar == 't') aChar = '\t'
                else if (aChar == 'r') aChar = '\r'
                else if (aChar == 'n') aChar = '\n'
                else if (aChar == 'f') aChar = '\u000C'  //\f
                outBuffer.append(aChar)
            }
        } else outBuffer.append(aChar)
    }
    return outBuffer.toString()
}
//endregion

//region log
fun String.logE(tag: String? = null) {
    if (BuildConfig.DEBUG)
        Lg.e(this)
//        if (tag.isNullOrEmpty()) Lg.e(this) else LogUtils.e(this, tag = tag!!)
}

fun String.logI(tag: String? = null) {
    if (BuildConfig.DEBUG)
        if (tag.isNullOrEmpty()) Lg.i(this) else LogUtils.i(this, tag = tag!!)
}

fun String.logD(tag: String? = null) {
    if (BuildConfig.DEBUG)
        if (tag.isNullOrEmpty()) Lg.d(this) else LogUtils.d(this, tag = tag!!)
}

fun String.logV(tag: String? = null) {
    if (BuildConfig.DEBUG)
        if (tag.isNullOrEmpty()) Lg.v(this) else LogUtils.v(this, tag = tag!!)
}

fun String.logW(tag: String? = null) {
    if (BuildConfig.DEBUG)
        if (tag.isNullOrEmpty()) Lg.w(this) else LogUtils.w(this, tag = tag!!)
}

fun String.logWtf(tag: String? = null) {
    if (BuildConfig.DEBUG)
        if (tag.isNullOrEmpty()) LogUtils.wtf(this) else LogUtils.wtf(this, tag = tag!!)
}

fun Exception.log(tag: String? = null) {
    if (BuildConfig.DEBUG)
        if (tag.isNullOrEmpty()) Lg.e(this) else LogUtils.e(this, tag = tag!!)
}
//endregion

//region 各种hash
fun String.generateMD5(): String {
    val hash: ByteArray
    try {
        hash = MessageDigest.getInstance("MD5").digest(this.toByteArray(Charsets.UTF_8))
    } catch (e: NoSuchAlgorithmException) {
        e.log()
        return this
    } catch (e: UnsupportedEncodingException) {
        e.log()
        return this
    }

    val hex = StringBuilder(hash.size * 2)

    for (b in hash) {
        val i: Int = b.toInt() and 0xff //获取低八位有效值
        var hexString = Integer.toHexString(i) //将整数转化为16进制
        if (hexString.length < 2) {
            hexString = "0" + hexString //如果是一位的话，补0
        }
        hex.append(hexString)
    }
    return hex.toString()
}

fun String.generateSHA1(): String {
    val bytes = generateAlgorithmBytes("SHA-1", this) ?: return this

    val hex = StringBuilder(bytes.size * 2)

    for (b in bytes) {
        val i: Int = b.toInt() and 0xff //获取低八位有效值
        var hexString = Integer.toHexString(i) //将整数转化为16进制
        if (hexString.length < 2) {
            hexString = "0" + hexString //如果是一位的话，补0
        }
        hex.append(hexString)
    }
    return hex.toString()
}

private fun generateAlgorithmBytes(algorithm: String, valueStr: String): ByteArray? {
    var digest: MessageDigest?
    try {
        digest = MessageDigest.getInstance(algorithm)
        digest.reset()
        return digest.digest(valueStr.toByteArray())
    } catch (e1: NoSuchAlgorithmException) {
        e1.log()
        return null
    }

}
//endregion


/**
 * 去掉多余的小数点和0
 *
 * @param originalStr
 * @return
 */
fun String.subZeroAndDot(): String {
    var originalStr = this
    if (TextUtils.isEmpty(originalStr)) {
        return originalStr
    }
    if (originalStr.indexOf(".") > 0) {
        originalStr = originalStr.replace("0+?$".toRegex(), "")
        originalStr = originalStr.replace("[.]$".toRegex(), "")
    }
    return originalStr
}


