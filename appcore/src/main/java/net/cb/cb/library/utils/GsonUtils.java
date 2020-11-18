package net.cb.cb.library.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Liszt
 * @date 2019/8/12
 * Description bean 转 json ，json转bean
 */
public class GsonUtils {

    public static <T extends Object> T getObject(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, clazz);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public static <T extends Object> String optObject(T t) {
        if (t == null) {
            return null;
        }
        try {
            Gson gson = new Gson();
            return gson.toJson(t);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //获取省市数据
    public static String getCityJson(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("city.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}
