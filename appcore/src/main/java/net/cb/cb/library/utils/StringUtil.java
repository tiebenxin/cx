package net.cb.cb.library.utils;

import java.util.regex.Pattern;

public class StringUtil {

    public final static Pattern URL =
            Pattern.compile("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");

    public static boolean isNotNull(String str) {
        if (str != null && str.length() > 0) {
            return true;
        }
        return false;
    }
}
