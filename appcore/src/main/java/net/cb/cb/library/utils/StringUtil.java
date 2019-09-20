package net.cb.cb.library.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public final static Pattern URL =
            Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]", Pattern.MULTILINE | Pattern.DOTALL);

    public final static Pattern EMOJI = Pattern.compile("(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)");

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

    /*
     * 截取之前，检测是否包含emoji
     * */
    public static String splitEmojiString(String content, int start, int end) {
//        LogUtil.getLog().i("检测Emoji", content + "--onCreate=" + onCreate + "--end=" + end);
        Matcher matcher = EMOJI.matcher(content);
        while (matcher.find()) {
            int first = matcher.start();
            int last = matcher.end();
            if (first < start && last > start) {
                start = first;
            } else if (first < end && last > end) {
                end = first;//只能少，不能多，左闭右开
            }
        }
//        LogUtil.getLog().i("检测Emoji", content.substring(onCreate, end) + "--onCreate=" + onCreate + "--end=" + end);
        return content.substring(start, end);
    }
}
