package com.hss01248.cipher.keystore;

import android.content.Context;
import android.os.Build;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Aes加解密工具类
 */

public class AesUtil {

    private static final String AES_MODE_OAEP = "AES/CBC/PKCS5Padding";
    private final static String ALGORITHM = "SHA1PRNG";
    private static final String alias = "AesUtil333";

    public static void init(Context context){
        KeyStoreManager.turnInit(context);
    }

    /**
     * AES加密算法加密
     *
     * @param originalStr 原文
     * @return 密文
     */
    public static String encrypt(String originalStr) {
        SecretKey key = getAesKey();
        if (key == null) {
            return originalStr;
        }
        try {
            byte[] rawKey = key.getEncoded();
            return encrypt(rawKey, originalStr);
        } catch (Exception e) {
            e.printStackTrace();
            return originalStr;
        }

    }

    /**
     * AES解密算法解密
     *
     * @param encryptedStr 密文
     * @return 原文
     */
    public static String decrypt(String encryptedStr) {
        SecretKey key = getAesKey();
        if (key == null) {
            return encryptedStr;
        }
        try {
            byte[] rawKey = key.getEncoded();
            return decrypt(rawKey, encryptedStr);
        } catch (Exception e) {
            e.printStackTrace();
            return encryptedStr;
        }
    }


    private static String encrypt(byte[] raw, String cleartext) {
        try {
            byte[] rawKey = raw;
            byte[] result = encrypt(rawKey, cleartext.getBytes("UTF-8"));
            return Base64.encodeToString(result, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return cleartext;
        }
    }

    /**
     * @return AES加密算法加密
     * @throws Exception
     */
    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(raw);
        Cipher cipher = Cipher.getInstance(AES_MODE_OAEP);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }




    private static String decrypt(byte[] raw, String encrypted) {
        try {
            byte[] rawKey = raw;
            byte[] enc = Base64.decode(encrypted, Base64.NO_WRAP);
            byte[] result = decrypt(rawKey, enc);
            return new String(result);
        } catch (Exception e) {
            return encrypted;
        }
    }


    private static byte[] decrypt(byte[] raw, byte[] encrypted)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(raw);
        Cipher cipher = Cipher.getInstance(AES_MODE_OAEP);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParameterSpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }


    /**
     * 密钥管理:
     * 18开始,生成一个62位随机字符串,然后使用AndroidKeyStore保存的RSA公钥加密,将加密得到的字符串
     * @return
     */
    public static SecretKey getAesKey(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return KeyStoreManager.getSecretKey(alias);
        }
        return createKeyDownP();
    }


    private static SecretKey createKeyDownP() {
        KeyGenerator kgen;
        try {
            kgen = KeyGenerator.getInstance("AES");
            // SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
            SecureRandom sr = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                sr = SecureRandom.getInstance(ALGORITHM, new CryptoProvider());
            } else {
                sr = SecureRandom.getInstance(ALGORITHM);
            }
            sr.setSeed(AesUtil.alias.getBytes("UTF-8"));
            kgen.init(128, sr);
            return new SecretKeySpec(kgen.generateKey().getEncoded(), "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }


    }
}
