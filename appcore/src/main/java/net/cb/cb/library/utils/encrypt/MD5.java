package net.cb.cb.library.utils.encrypt;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    public static String md5(String string) {
  /*      if(true)//test
            return string;*/

        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result.toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] toByte(String sourceString) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(sourceString.getBytes());
        byte[] messageDigest = digest.digest();
        return messageDigest;
    }
}
