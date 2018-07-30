package cn.yuntk.radio.utils

/**
 * Created by Jone on 2018/3/27.
 */
import android.content.Context
import android.util.Log

/**
 * Created by luojun on 2017/12/2.
 */
object LogUtils
{
    private var isLog = true
    private var defaultTag = "UuU"

    fun init(context: Context, tag: String = defaultTag, isLog: Boolean = context.isDebug())
    {
        LogUtils.isLog = isLog
        defaultTag = tag
    }


    @JvmOverloads
    @JvmStatic
    fun e(vararg logs: Any, tag: String = defaultTag)
    {
        if (isLog)
        {
            val sbLog = StringBuilder()
            for (log in logs)
            {
                if (log is Exception)
                {
                    sbLog.append(log.message)
                    sbLog.append("\r\n")
                    for (element in log.stackTrace)
                    {
                        sbLog.append(element.toString())
                        sbLog.append("\r\n")
                    }
                } else
                {
                    sbLog.append(log.toString().decodeUnicode())
                }
                Log.e(tag, JsonFormat.format(sbLog.toString()))
            }
        }
    }

    @JvmOverloads
    @JvmStatic
    fun i(log: Any, tag: String = defaultTag)
    {
        if (isLog)
        {
            Log.i(tag, JsonFormat.format(log.toString().decodeUnicode()))
        }
    }


    @JvmOverloads
    @JvmStatic
    fun w(log: Any, tag: String = defaultTag)
    {
        if (isLog)
        {
            Log.w(tag, JsonFormat.format(log.toString().decodeUnicode()))
        }
    }

    @JvmOverloads
    @JvmStatic
    fun d(log: Any, tag: String = defaultTag)
    {
        if (isLog)
        {
            Log.d(tag, JsonFormat.format(log.toString().decodeUnicode()))
        }
    }


    @JvmOverloads
    @JvmStatic
    fun v(log: Any, tag: String = defaultTag)
    {
        if (isLog)
        {
            Log.v(tag, JsonFormat.format(log.toString().decodeUnicode()))
        }
    }


    @JvmOverloads
    @JvmStatic
    fun wtf(log: Any, tag: String = defaultTag)
    {
        if (isLog)
        {
            Log.wtf(tag, JsonFormat.format(log.toString().decodeUnicode()))
        }
    }


    internal object JsonFormat{
        /**
         * 默认每次缩进两个空格
         */
        private val empty = "  "

        fun format(json: String): String
        {
            try
            {
                var empty = 0
                val chs = json.toCharArray()
                val stringBuilder = StringBuilder()
                var i = 0
                while (i < chs.size)
                {
                    //若是双引号，则为字符串，下面if语句会处理该字符串
                    if (chs[i] == '\"')
                    {

                        stringBuilder.append(chs[i])
                        i++
                        //查找字符串结束位置
                        while (i < chs.size)
                        {
                            //如果当前字符是双引号，且前面有连续的偶数个\，说明字符串结束
                            if (chs[i] == '\"' && isDoubleSerialBackslash(chs, i - 1))
                            {
                                stringBuilder.append(chs[i])
                                i++
                                break
                            }
                            else
                            {
                                stringBuilder.append(chs[i])
                                i++
                            }

                        }
                    }
                    else if (chs[i] == ',')
                    {
                        stringBuilder.append(',').append('\n').append(getEmpty(empty))

                        i++
                    }
                    else if (chs[i] == '{' || chs[i] == '[')
                    {
                        empty++
                        stringBuilder.append(chs[i]).append('\n').append(getEmpty(empty))

                        i++
                    }
                    else if (chs[i] == '}' || chs[i] == ']')
                    {
                        empty--
                        stringBuilder.append('\n').append(getEmpty(empty)).append(chs[i])

                        i++
                    }
                    else
                    {
                        stringBuilder.append(chs[i])
                        i++
                    }


                }



                return stringBuilder.toString()
            } catch (e: Exception)
            {
                // TODO Auto-generated catch block
                e.printStackTrace()
                return json
            }

        }

        private fun isDoubleSerialBackslash(chs: CharArray, i: Int): Boolean
        {
            var count = 0
            for (j in i downTo -1 + 1)
            {
                if (chs[j] == '\\')
                {
                    count++
                }
                else
                {
                    return count % 2 == 0
                }
            }

            return count % 2 == 0
        }

        /**
         * 缩进
         * @param count
         * @return
         */
        private fun getEmpty(count: Int): String
        {
            val stringBuilder = StringBuilder()
            for (i in 0 until count)
            {
                stringBuilder.append(empty)
            }

            return stringBuilder.toString()
        }
    }
}
