package net.cb.cb.library.utils;

import android.text.TextUtils;

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

    /***
     * 吧HTML转为text
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }


    /*
     * @param userRemarkName 好友备注名
     * @param mucNick 用户群昵称
     * @param userNick 用户昵称
     * @param uid 用户uid
     * 优先级：userRemarkName>mucNick>userNick>uid
     * */
    public static String getUserName(String userRemarkName, String mucNick, String userNick, Long uid) {
        String name = uid + "";
        if (!TextUtils.isEmpty(userRemarkName)) {
            name = userRemarkName;
        } else if (!TextUtils.isEmpty(mucNick)) {
            name = mucNick;
        } else if (!TextUtils.isEmpty(userNick)) {
            name = userNick;
        }
        return name;
    }

    public static int[] getVersionArr(String version) {
        if (TextUtils.isEmpty(version)) {
            return null;
        }
        String[] a = version.split("-");
        String oVersion = a[0];
        String[] oldArr = oVersion.split(".");
        int[] arr = null;
        if (oldArr != null && oldArr.length == 3) {
            arr = new int[3];
            arr[0] = Integer.valueOf(oldArr[0]);
            arr[1] = Integer.valueOf(oldArr[1]);
            arr[2] = Integer.valueOf(oldArr[2]);
        }
        return arr;
    }
}
