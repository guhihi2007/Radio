package cn.yuntk.radio.ibook.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Erosion on 2017/11/22.
 */
public class GsonUtils {
    private static Gson gson = new Gson();

    /**
     * 解析jsonObject
     * @param jsonString
     * @param classes
     * @param <T>
     * @return
     */
    public static <T> T parseObject(String jsonString, Class<T> classes){
        if(jsonString == null)
            return null;
        T t = null;
        try {
            t = gson.fromJson(jsonString,classes);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 解析jsonArray
     * @param jsonString
     * @param classes
     * @param <T>
     * @return
     */
    public static <T> List<T> parseArray(String jsonString, Class<T[]> classes){
        T[] list = gson.fromJson(jsonString,classes);
        return Arrays.asList(list);
    }

    /**
     * 把实体转换成json数据
     * @param o
     * @return
     */
    public static String parseToJsonString(Object o){
        return gson.toJson(o);
    }

    /**
     * 转成list
     * 解决泛型问题
     * @param json
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonToList(String json, Class<T> cls) {
        Gson gson = new Gson();
        List<T> list = new ArrayList<T>();
        JsonArray array = null;
        try {
            array = new JsonParser().parse(json).getAsJsonArray();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("GsonUtils:","collection jsonToList"+e.getMessage());
            return list;
        }
        for(final JsonElement elem : array){
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }

    /**
     * 转成json
     *
     * @param object
     * @return
     */
    public static String GsonString(Object object) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(object);
        }
        return gsonString;
    }
}
