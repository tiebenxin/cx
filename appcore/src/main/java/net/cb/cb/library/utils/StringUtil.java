package net.cb.cb.library.utils;

public class StringUtil {
    public static boolean isNotNull(String str){
        if(str!=null && str.length()>0){
            return true;
        }
        return false;
    }
}
