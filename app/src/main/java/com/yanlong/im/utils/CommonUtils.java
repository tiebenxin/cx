package com.yanlong.im.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @类名：通用工具类
 * @Date：2020/4/7
 * @by zjy
 * @备注：
 *
 *      1 List拆分
 */

public class CommonUtils {


    /**
     * List拆分
     *
     * @param source
     * @param len 集合的长度
     * @备注 按指定大小，分隔集合，将集合按规定个数分为多个部分
     */
    public static <T> List<List<T>> subWithLen(List<T> source, int len) {
        if (source == null || source.size() == 0 || len < 1) {
            return null;
        }

        List<List<T>> result = new ArrayList<List<T>>();
        int count = (source.size() + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<T> value = null;
            if ((i + 1) * len < source.size()) {
                value = source.subList(i * len, (i + 1) * len);
            } else {
                value = source.subList(i * len, source.size());
            }
            result.add(value);
        }
        return result;
    }

}
