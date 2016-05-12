package com.nd.gaea.waf.security;

import com.nd.gaea.WafException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

/**
 * @author wukf
 * @since 2.0
 * @date 2016/1/7
 */
public class DESUtil {

    public static final String INTERNAL_SERVER_ERROR = "WAF/INTERNAL_SERVER_ERROR";

    private static final String KEY = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4";

    private static final String CODEING = "UTF-8";

    //算法名称
    public static final String KEY_ALGORITHM = "DESede";

    public static final String CIPHER_ALGORITHM = KEY_ALGORITHM + "/ECB/PKCS5Padding";


    /**
     * 加密
     * @param data
     * @return
     */
    public static String encode(String data){
        String res = null;
        try {
            byte[] dataBtye = des3EncodeECB(new BASE64Decoder().decodeBuffer(KEY), data.getBytes(CODEING));
            res = new String(Base64.encodeBase64(dataBtye));
        } catch (Exception e) {
            throw new WafException(INTERNAL_SERVER_ERROR, "加密失败:"+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return res;
    }

    /**
     * 解密
     * @param data
     * @return
     */
    public static String decode(String data){
        String res = null;
        try {
            byte[] dataBtye = des3DecodeECB(new BASE64Decoder().decodeBuffer(KEY), Base64.decodeBase64(data));
            res = new String(dataBtye, CODEING);
        } catch (Exception e) {
            throw new WafException(INTERNAL_SERVER_ERROR, "解密失败:"+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return res;
    }

    /**
     * ECB加密,不要IV
     * @param key 密钥
     * @param data 明文
     * @return Base64编码的密文
     * @throws Exception
     */
    public static byte[] des3EncodeECB(byte[] key, byte[] data)
            throws Exception {

        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

        cipher.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);

        return bOut;
    }

    /**
     * ECB解密,不要IV
     * @param key 密钥
     * @param data Base64编码的密文
     * @return 明文
     * @throws Exception
     */
    public static byte[] des3DecodeECB(byte[] key, byte[] data)
            throws Exception {

        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

        cipher.init(Cipher.DECRYPT_MODE, deskey);

        byte[] bOut = cipher.doFinal(data);

        return bOut;

    }

    /**
     * CBC加密
     * @param key 密钥
     * @param keyiv IV
     * @param data 明文
     * @return Base64编码的密文
     * @throws Exception
     */
    public static byte[] des3EncodeCBC(byte[] key, byte[] keyiv, byte[] data)
            throws Exception {

        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        deskey = keyfactory.generateSecret(spec);

        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] bOut = cipher.doFinal(data);

        return bOut;
    }

    /**
     * CBC解密
     * @param key 密钥
     * @param keyiv IV
     * @param data Base64编码的密文
     * @return 明文
     * @throws Exception
     */
    public static byte[] des3DecodeCBC(byte[] key, byte[] keyiv, byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }

//    public static void main(String[] args) throws Exception {
//        long tt = new Date().getTime() + 100000L;
//        String encode = DESUtil.encode("166888," + tt);
//        System.out.println(encode);
//        System.out.println(DESUtil.decode(encode));
//
//        /*byte[] key=new BASE64Decoder().decodeBuffer(KEY);
//        byte[] keyiv = { 1, 2, 3, 4, 5, 6, 7, 8 };
//
//        byte[] data="中".getBytes("UTF-8");
//
//        System.out.println("ECB加密解密");
//        byte[] str3 = des3EncodeECB(key,data );
//        byte[] str4 = des3DecodeECB(key, str3);
//        System.out.println(new BASE64Encoder().encode(str3));
//        System.out.println(new String(str4, "UTF-8"));
//
//        System.out.println();
//
//        System.out.println("CBC加密解密");
//        byte[] str5 = des3EncodeCBC(key, keyiv, data);
//        byte[] str6 = des3DecodeCBC(key, keyiv, str5);
//        System.out.println(new BASE64Encoder().encode(str5));
//        System.out.println(new String(str6, "UTF-8"));*/
//    }
}
