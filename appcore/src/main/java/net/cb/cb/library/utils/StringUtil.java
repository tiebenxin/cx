package net.cb.cb.library.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public final static Pattern URL =
            Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]", Pattern.MULTILINE | Pattern.DOTALL);

    public static boolean isNotNull(String str) {
        if (str != null && str.length() > 0) {
            return true;
        }
        return false;
    }

    public static void testUrl(String msg) {
        msg = "http://baidu.com\n回复报告白拿的\nhttp://baidu.com\n发改委复合物号单位自己\nhttp://baidu.com";
        Matcher matcher = URL.matcher(msg);
        List<String> list = new ArrayList<>();
        int i = 0;
        int preLast = 0;
        int len = msg.length();
        while (matcher.find()) {
            int group = matcher.groupCount();
            if (group > 0) {
                int start = matcher.start();
                int end = matcher.end();
                if (i == 0) {
                    if (start != 0) {
                        list.add(msg.substring(0, start));
                        list.add(msg.substring(start, end));
                    } else {
                        list.add(msg.substring(0, end));
                    }
                } else {
                    if (end != len - 1) {
                        list.add(msg.substring(preLast, start));
                        list.add(msg.substring(start, end));
                    }
                }
                preLast = end;
            }
            i++;
        }
        list.add(msg.substring(preLast));
        int size = list.size();
        if (size > 0) {
            for (int j = 0; j < size; j++) {
                System.out.println(list.get(j));
            }
        }
    }

}
