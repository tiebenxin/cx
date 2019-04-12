package net.cb.cb.library.utils.encrypt;

import java.util.Map;

/**
 * description:
 * author: Darren on 2018/1/10 10:13
 * version: 1.0
 */
public class AESEncrypt {

    public static final String RAS_ENCRYPT_KEY = "IDAQAB";
    public static String encrypt(Map<String, String> params) {

        String linkString = RequestParamConvert.createLinkString(params, false, true, false, true);

        try {
            byte[] encrypt = RSA.encrypt(RSA.publibKey(RAS_ENCRYPT_KEY), linkString);
            return new String(encrypt, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String encrypt(String linkString) {


        try {
            byte[] encrypt = RSA.encrypt(RSA.publibKey(RAS_ENCRYPT_KEY), linkString);
            return new String(encrypt, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
