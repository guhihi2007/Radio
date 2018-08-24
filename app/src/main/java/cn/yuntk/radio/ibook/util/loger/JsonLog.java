package cn.yuntk.radio.ibook.util.loger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonLog {

    public static void printJson(String tag, String msg, String headString) {

        String message;

        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(Loger.JSON_INDENT);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(Loger.JSON_INDENT);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        LogUtil.printLine(tag, true);
        message = headString + Loger.LINE_SEPARATOR + message;
        String[] lines = message.split(Loger.LINE_SEPARATOR);
        for (String line : lines) {
            android.util.Log.d(tag, "║ " + line);
        }
        LogUtil.printLine(tag, false);
    }
}
