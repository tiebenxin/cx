package net.cb.cb.library.utils.encrypt;

import android.util.Base64;

import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Liszt
 * @date 2019/12/28
 * Description
 */
public class EncrypUtil {
    private static String p = "ed16b1f8a9e648d4";
    private static String k = "cb66g128j9e74md9";

    public static String aesEncode(String str) {
        try {
            byte[] iv = p.getBytes();
            byte[] key = k.getBytes();
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(str.getBytes("utf-8"));
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    public static String aesDecode(String str) {
        try {
            byte[] iv = p.getBytes();
            byte[] key = k.getBytes();

            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
            byte[] decode64 = Base64.decode(str, Base64.DEFAULT);
            byte[] encrypted = cipher.doFinal(decode64);
            return new String(encrypted, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    //获取随机 byte[16]
    public static byte[] getByte() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(16);
        return uuid.getBytes();
    }
}
