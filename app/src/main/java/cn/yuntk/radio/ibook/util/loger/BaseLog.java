package cn.yuntk.radio.ibook.util.loger;

public class BaseLog {

    private static final int MAX_LENGTH = 4000;

    public static void printDefault(int type, String tag, String msg) {

        int index = 0;
        int length = msg.length();
        int countOfSub = length / MAX_LENGTH;

        if (countOfSub > 0) {
            for (int i = 0; i < countOfSub; i++) {
                String sub = msg.substring(index, index + MAX_LENGTH);
                printSub(type, tag, sub);
                index += MAX_LENGTH;
            }
            printSub(type, tag, msg.substring(index, length));
        } else {
            printSub(type, tag, msg);
        }
    }

    private static void printSub(int type, String tag, String sub) {
        switch (type) {
            case Loger.V:
                android.util.Log.v(tag, sub);
                break;
            case Loger.D:
                android.util.Log.d(tag, sub);
                break;
            case Loger.I:
                android.util.Log.i(tag, sub);
                break;
            case Loger.W:
                android.util.Log.w(tag, sub);
                break;
            case Loger.E:
                android.util.Log.e(tag, sub);
                break;
            case Loger.A:
                android.util.Log.wtf(tag, sub);
                break;
        }
    }

}
