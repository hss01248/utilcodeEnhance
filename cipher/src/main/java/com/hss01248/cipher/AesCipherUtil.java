package com.hss01248.cipher;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;


/**
 * @Despciption todo
 * @Author hss
 * @Date 27/10/2023 16:43
 * @Version 1.0
 */
public class AesCipherUtil {


    private static final int KEY_SIZE = 256;
    private static final String KEY_PREFIX = "_CM_";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM;
    private static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE;
    private static final String ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    private static final int IV_SIZE_IN_BYTES = 12;
    private static final int TAG_SIZE_IN_BYTES = 16;


    /**
     * 使用: byte[] ciphertext = cipher.doFinal(plaintext);
     * byte[] iv = cipher.getIV();
     *
     * @param keyName
     * @return
     * @throws Throwable
     */
    //@Override
    public static Cipher getInitializedCipherForEncryption(String keyName) throws Throwable {
        Cipher cipher = getCipher();
        SecretKey secretKey = getOrCreateSecretKey(keyName, false);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher;
    }

    //@Override
    public static Cipher getInitializedCipherForDecryption(String keyName, byte[] initializationVector) throws Throwable {
        Cipher cipher = getCipher();
        SecretKey secretKey = getOrCreateSecretKey(keyName, false);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_SIZE_IN_BYTES * 8, initializationVector));
        return cipher;
    }

    //@Override
    public static Cipher getInitializedCipherForDecryption(String keyName, File encryptedDataFile) throws Throwable {
        byte[] iv = new byte[IV_SIZE_IN_BYTES];
        int count;
        try {
            count = new FileInputStream(encryptedDataFile).read(iv);
            assert count == IV_SIZE_IN_BYTES;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getInitializedCipherForDecryption(keyName, iv);
    }

    //@Override
    public static byte[] encryptData(String plaintext, Cipher cipher) throws Throwable {
        byte[] input = plaintext.getBytes(Charset.forName("UTF-8"));
        byte[] ciphertext = new byte[IV_SIZE_IN_BYTES + input.length + TAG_SIZE_IN_BYTES];
        int bytesWritten = cipher.doFinal(input, 0, input.length, ciphertext, IV_SIZE_IN_BYTES);
        System.arraycopy(cipher.getIV(), 0, ciphertext, 0, IV_SIZE_IN_BYTES);
        if (bytesWritten != input.length + TAG_SIZE_IN_BYTES) {
            throw new IllegalStateException("Cipher.doFinal didn't write all bytes to the output buffer");
        }
        if (cipher.getIV().length != IV_SIZE_IN_BYTES) {
            throw new IllegalStateException("Cipher.getIV() result incorrect length");
        }
        LogUtils.d("encrypted " + input.length + " (" + ciphertext.length + " output)");
        return ciphertext;
    }

    //@Override
    public static String decryptData(byte[] ciphertext, Cipher cipher) throws Throwable {
        LogUtils.d("decrypting " + ciphertext.length + " bytes (iv: " + IV_SIZE_IN_BYTES + ", tag: " + TAG_SIZE_IN_BYTES + ")");

        byte[] iv = new byte[IV_SIZE_IN_BYTES];
        System.arraycopy(ciphertext, 0, iv, 0, IV_SIZE_IN_BYTES);
        if (!java.util.Arrays.equals(iv, cipher.getIV())) {
            throw new IllegalStateException("expected first bytes of ciphertext to equal cipher iv.");
        }
        byte[] plaintext = cipher.doFinal(ciphertext, IV_SIZE_IN_BYTES, ciphertext.length - IV_SIZE_IN_BYTES);
        return new String(plaintext, Charset.forName("UTF-8"));
    }

    public static Cipher getCipher() throws Throwable {
        String transformation = ENCRYPTION_ALGORITHM + "/" + ENCRYPTION_BLOCK_MODE + "/" + ENCRYPTION_PADDING;
        return Cipher.getInstance(transformation);
    }

    public void deleteKey(String keyName) {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            keyStore.deleteEntry(KEY_PREFIX + keyName);
        } catch (Exception e) {
            LogUtils.w("Unable to delete key from KeyStore " + KEY_PREFIX + keyName);
        }
    }

    public static SecretKey getOrCreateSecretKey(String keyName, boolean userAuthenticationRequired) throws Throwable {
        String realKeyName = KEY_PREFIX + keyName;

        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        SecretKey secretKey = (SecretKey) keyStore.getKey(realKeyName, null);
        if (secretKey != null) {
            return secretKey;
        }

        //对称密钥 KeyGenParameterSpec   非对称加密的密钥对: KeyPairGenerator.getInstance(
        //            KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
        KeyGenParameterSpec.Builder paramsBuilder = new KeyGenParameterSpec.Builder(
                realKeyName,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        );
        paramsBuilder.setBlockModes(ENCRYPTION_BLOCK_MODE);
        paramsBuilder.setEncryptionPaddings(ENCRYPTION_PADDING);
        paramsBuilder.setKeySize(KEY_SIZE);
        paramsBuilder.setUserAuthenticationRequired(userAuthenticationRequired);
        //configure = paramsBuilder;
        //configure.configure(paramsBuilder);
        KeyGenParameterSpec keyGenParams = paramsBuilder.build();

        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
        );
        keyGenerator.init(keyGenParams);
        return keyGenerator.generateKey();

    }

    public static byte[] encrypt(String keyAlias, byte[] data) throws Throwable{
        SecretKey key = getOrCreateSecretKey(keyAlias, false);
       return EncryptUtils.encryptAES(data,key.getEncoded(),"AES/GCM/NoPadding",null);
    }

    public static byte[] decrypt(String keyAlias, byte[] data) throws Throwable{
        SecretKey key = getOrCreateSecretKey(keyAlias, false);
        return EncryptUtils.decryptAES(data,key.getEncoded(),"AES/GCM/NoPadding",null);
    }

}

/*
class EncryptedData {
    private final byte[] encryptedPayload;

    public EncryptedData(byte[] encryptedPayload) {
        this.encryptedPayload = encryptedPayload;
    }

    public byte[] getEncryptedPayload() {
        return encryptedPayload;
    }
}

    public CryptographyManager cryptographyManager(KeyGenParameterSpec.Builder configure) {
        return new CryptographyManagerImpl(configure);
    }*/
