package cn.yuntk.radio.ibook.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

/**
 * Created by john on 2016/12/10.
 */
public class DateUtils {

    public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss.SSS";
    private static SimpleDateFormat sdf = new SimpleDateFormat();

    /**
     * 获取当前日期的指定格式的字符串
     *
     * @param format 指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:mm:ss.SSS"
     * @return
     */
    public static String getCurrentTimeString(String format) {
        if (format == null || format.trim().equals("")) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        return sdf.format(new Date());
    }

    //比较日期是否大于今天的日期
    public static boolean isBeforeNowDay(String myString) {
//        java.util.Date nowdate = new java.util.Date();
//        Time time = new Time();
        Calendar c = Calendar.getInstance();
        Calendar c1 = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); //获取当前年份

        int mMonth = c.get(Calendar.MONTH);//获取当前月份

        int mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码

        int mHour = c.get(Calendar.HOUR_OF_DAY);//获取当前的小时数

        int mMinute = c.get(Calendar.MINUTE);//获取当前的分钟数

        LogUtils.showLog("Calendar:" + c);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);

        Date d = null;

        try {
            d = sdf.parse(myString);
            c1.setTime(d);
            int mYear1 = c1.get(Calendar.YEAR); //

            int mMonth1 = c1.get(Calendar.MONTH);//

            int mDay1 = c1.get(Calendar.DAY_OF_MONTH);//

            int mHour1 = c1.get(Calendar.HOUR_OF_DAY);//

            int mMinute1 = c1.get(Calendar.MINUTE);//

            LogUtils.showLog("Calendar1:" + c1);

            if (mYear == mYear1 && mMonth == mMonth1 && mDay == mDay1) {
                LogUtils.showLog("=当天日期");
                return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        int days = c1.compareTo(c);
        LogUtils.showLog("days:" + days);
        if (days == 1 || days == 0) {
            return true;
        } else {
            return false;
        }

    }

    //    比较两个日期的大小
    public static boolean compareDate(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        try {
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);
            int result = d1.compareTo(d2);
            LogUtils.showLog("resutl:" + result);
            if (result > 0) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取两个日期之间的间隔天数
     *
     * @return
     */
    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    //判断两个日期大小
    public static int compareDate(Date date1, Date date2) {
//        date1>date2   1
//        date1<date2  -1
//          date1=date2  0  时分秒也必须相同
        LogUtils.showLog("compareDate12:" + date1 + ":" + date2);
        int days = date1.compareTo(date2);
        LogUtils.showLog("days:" + days);
        return days;
    }

    //判断年月日是否一样 0一样-1不一样
    public static int compareYMD(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
//        year = c.get(Calendar.YEAR)
//        month = c.get(Calendar.MONTH)
//        day = c.get(Calendar.DAY_OF_MONTH)
        int year1 = calendar1.get(Calendar.YEAR);
        int year2 = calendar2.get(Calendar.YEAR);
        if (year1 == year2) {
            int month1 = calendar1.get(Calendar.MONTH);
            int month2 = calendar2.get(Calendar.MONTH);
            if (month1 == month2) {
                int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
                int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
                if (day1 == day2) {
                    return 0;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * 用于清除时分秒 毫秒
     */
    public static void setMidnight(Calendar cal) {
        cal.set(HOUR_OF_DAY, 0);
        cal.set(MINUTE, 0);
        cal.set(SECOND, 0);
        cal.set(MILLISECOND, 0);
    }


    /**
     * 根据开始时间和结束时间返回时间段内的时间集合
     *
     * @param beginDate
     * @param endDate
     * @return List<Date>
     */
    @SuppressWarnings("unchecked")
    public static List<Date> getDatesBetweenTwoDate(Date beginDate, Date endDate) {
        List lDate = new ArrayList();
        lDate.add(beginDate);//把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        //使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            //根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else {
                break;
            }
        }
        lDate.add(endDate);//把结束时间加入集合
        return lDate;
    }

    /**
     * 获取今日日期
     * 格式：2017/06/29
     */
    public static String getTodayDate() {
        String nowDate = java.text.MessageFormat.format(
                "{0,date,yyyy/MM/dd}",
                new Object[]{new java.sql.Date(System.currentTimeMillis())});
        LogUtils.showLog("nowdate:" + nowDate);
        return nowDate;
    }


    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd
     *
     * @param dateDate
     * @return
     */
    public static String dateToStrLong(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong1(String strDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//        ParsePosition pos = new ParsePosition(1);//用于指定解析位置
//        Date strtodate = formatter.parse(strDate, pos);

        Date strtodate = formatter.parse(strDate);
        return strtodate;
    }

    /**
     * 将长时间格式时间转换为format格式
     * format如：HH:mm ，HH:mm:ss yyyy-MM-dd等
     *
     * @param dateDate
     * @return
     */
    public static String dateToStrLong1(Date dateDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(dateDate);
        return dateString;
    }


    //获取两个时间的间隔
    private String getSurplusTime(String creatTime, String currentTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder surplus = new StringBuilder();
        try {
            Date creatDate = sdf.parse(creatTime);
            Date currentData = sdf.parse(currentTime);
            long diff = currentData.getTime() - creatDate.getTime();//这样得到的差值是微秒级别
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            surplus.append("" + days + "天" + hours + "小时");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return surplus.toString();
    }

    // ===================== 时间处理  =====================

    /**
     * 传入时间，获取时间(一直保存00:00:00) - 不处理大于一天
     *
     * @param time 时间（秒为单位）
     * @return
     */
    public static String secToTimeRetain(int time) {
        return secToTimeRetain(time, false);
    }

    /**
     * 传入时间，获取时间(一直保存00:00:00)
     *
     * @param time          时间（秒为单位）
     * @param isHandlerMDay 是否处理大于一天的时间
     * @return
     */
    public static String secToTimeRetain(int time, boolean isHandlerMDay) {
        try {
            if (time <= 0) {
                return "00:00:00";
            } else {
                // 单位秒
                int minute = 60;
                int hour = 3600;
                int day = 86400;
                // 取模
                int rSecond = 0;
                int rMinute = 0;
                // 差数
                int dSecond = 0;
                int dMinute = 0;
                int dHour = 0;
                // 转换时间格式
                if (time < minute) { // 小于1分钟
                    return "00:00:" + ((time >= 10) ? time : ("0" + time));
                } else if (time >= minute && time < hour) { // 小于1小时
                    dSecond = time % minute; // 取模分钟，获取多出的秒数
                    dMinute = (time - dSecond) / minute;
                    return "00:" + ((dMinute >= 10) ? dMinute : ("0" + dMinute)) + ":" + ((dSecond >= 10) ? dSecond : ("0" + dSecond));
                } else if (time >= hour && time < day) { // 小于等于一天
                    rMinute = time % hour; // 取模小时，获取多出的分钟
                    dHour = (time - rMinute) / hour; // 获取小时
                    dSecond = (time - dHour * hour); // 获取多出的秒数
                    dMinute = dSecond / minute; // 获取多出的分钟
                    rSecond = dSecond % minute; // 取模分钟，获取多余的秒速
                    return ((dHour >= 10) ? dHour : ("0" + dHour)) + ":" + ((dMinute >= 10) ? dMinute : ("0" + dMinute)) + ":" + ((rSecond >= 10) ? rSecond : "0" + rSecond);
                } else { // 多余的时间，直接格式化
                    // 大于一天的情况
                    if (isHandlerMDay) {
                        rMinute = time % hour; // 取模小时，获取多出的分钟
                        dHour = (time - rMinute) / hour; // 获取小时
                        dSecond = (time - dHour * hour); // 获取多出的秒数
                        dMinute = dSecond / minute; // 获取多出的分钟
                        rSecond = dSecond % minute; // 取模分钟，获取多余的秒速
                        return ((dHour >= 10) ? dHour : ("0" + dHour)) + ":" + ((dMinute >= 10) ? dMinute : ("0" + dMinute)) + ":" + ((rSecond >= 10) ? rSecond : "0" + rSecond);
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean isToday(String day) {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = null;
        try {
            date = getDateFormat().parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为昨天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean isYesterday(String day) {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = null;
        try {
            date = getDateFormat().parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    }

    /*获取日期*/
    public static String getDay(String date){
        String h;
        String[] day = date.split("-");
        h = day[2];
        return h;
    }

    /*获取月份*/
    public static String getMonth(String date){
        String m;
        String[] day = date.split("-");
        m = day[1];
        return m;
    }

    /*获取年份*/
    public static String getYear(String date){
        String y;
        String[] day = date.split("-");
        y = day[0];
        return y;
    }

    /*获取当前系统时间*/
    public static String getSysDate(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(date);
    }

    /*格式化日期时间*/
    public static String formatDatetime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return sdf.format(date);
    }

    public static String formatDatetime(String date) throws ParseException {
        DateFormat fmt = new SimpleDateFormat("yyyy年MM月dd日");
        Date d = fmt.parse(date);
        return d.toString();
    }

    public static String formatDatetime(String date, int forid){
        if(date == null ||"".equals(date.trim())){
            return "";
        }else{
            String str = "";
            str = date.substring(0,date.indexOf("."));
            String[] array = str.split(" ");
            String[] dates = array[0].split("-");
            switch (forid) {
                case 0:  //yyyy-MM-dd HH:mm:ss
                    str = date.substring(0,date.indexOf("."));
                    break;
                case 1:  //yyyy-MM-dd
                    str = date.substring(0,date.indexOf("."));
                    str = str.substring(0,str.indexOf(" "));
                    break;
                case 2:  //yyyy年MM月dd日 HH:mm:ss
                    str = dates[0]+"年"+dates[1]+"月"+dates[2]+"日 "+array[1];
                    break;
                case 3:  //yyyy年MM月dd日 HH:mm
                    str = dates[0]+"年"+dates[1]+"月"+dates[2]+"日 "+array[1].substring(0, array[1].lastIndexOf(":"));
                    break;
                case 4:  //yyyy年MM月dd日 HH:mm:ss
                    str = dates[0]+"年"+dates[1]+"月"+dates[2]+"日 ";
                    break;
                default:
                    break;
            }
            return str;
        }
    }

    /*获取当前时间的毫秒*/
    public String getSysTimeMillise(){
        long i = System.currentTimeMillis();
        return String.valueOf(i);
    }

    /*获取星期几*/
    public static String getWeek(){
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }




}
