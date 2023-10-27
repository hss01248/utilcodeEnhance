package com.hss01248.biometric.store;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.File;
import java.nio.charset.Charset;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;


// based on https://github.com/isaidamier/blogs.biometrics.cryptoBlog/blob/cryptoObject/app/src/main/java/com/example/android/biometricauth/CryptographyManager.kt

/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
/**
 * @Despciption todo
 * @Author hss
 * @Date 27/10/2023 15:46
 * @Version 1.0
 */
public class CryptographyManagerImpl  implements CryptographyManager {
    private final KeyGenParameterSpec.Builder configure;

    private static final int KEY_SIZE = 256;
    private static final String KEY_PREFIX = "_CM_";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM;
    private static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE;
    private static final String ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    private static final int IV_SIZE_IN_BYTES = 12;
    private static final int TAG_SIZE_IN_BYTES = 16;
    private final KotlinLogging logger = KotlinLogging.INSTANCE.logger();

    public CryptographyManagerImpl(KeyGenParameterSpec.Builder configure) {
        this.configure = configure;
    }

    public CryptographyManager cryptographyManager(KeyGenParameterSpec.Builder configure) {
        return new CryptographyManagerImpl(configure);
    }

    @Override
    public Cipher getInitializedCipherForEncryption(String keyName) {
        Cipher cipher = getCipher();
        SecretKey secretKey = getOrCreateSecretKey(keyName);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher;
    }
    @Override
    public Cipher getInitializedCipherForDecryption(String keyName, byte[] initializationVector) {
        Cipher cipher = getCipher();
        SecretKey secretKey = getOrCreateSecretKey(keyName);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_SIZE_IN_BYTES * 8, initializationVector));
        return cipher;
    }

    @Override
    public Cipher getInitializedCipherForDecryption(String keyName, File encryptedDataFile) {
        byte[] iv = new byte[IV_SIZE_IN_BYTES];
        int count;
        try {
            count = encryptedDataFile.inputStream().read(iv);
            assert count == IV_SIZE_IN_BYTES;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getInitializedCipherForDecryption(keyName, iv);
    }

    @Override
    public EncryptedData encryptData(String plaintext, Cipher cipher) {
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
        logger.debug(() -> "encrypted " + input.length + " (" + ciphertext.length + " output)");
        return new EncryptedData(ciphertext);
    }

    @Override
    public String decryptData(byte[] ciphertext, Cipher cipher) {
        logger.debug(() -> "decrypting " + ciphertext.length + " bytes (iv: " + IV_SIZE_IN_BYTES + ", tag: " + TAG_SIZE_IN_BYTES + ")");
        byte[] iv = new byte[IV_SIZE_IN_BYTES];
        System.arraycopy(ciphertext, 0, iv, 0, IV_SIZE_IN_BYTES);
        if (!java.util.Arrays.equals(iv, cipher.getIV())) {
            throw new IllegalStateException("expected first bytes of ciphertext to equal cipher iv.");
        }
        byte[] plaintext = cipher.doFinal(ciphertext, IV_SIZE_IN_BYTES, ciphertext.length - IV_SIZE_IN_BYTES);
        return new String(plaintext, Charset.forName("UTF-8"));
    }

    private Cipher getCipher() {
        String transformation = ENCRYPTION_ALGORITHM + "/" + ENCRYPTION_BLOCK_MODE + "/" + ENCRYPTION_PADDING;
        try {
            return Cipher.getInstance(transformation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteKey(String keyName) {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            keyStore.deleteEntry(KEY_PREFIX + keyName);
        } catch (Exception e) {
            logger.warn(() -> "Unable to delete key from KeyStore " + KEY_PREFIX + keyName);
        }
    }

    private SecretKey getOrCreateSecretKey(String keyName) {
        String realKeyName = KEY_PREFIX + keyName;
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(realKeyName, null);
            if (secretKey != null) {
                return secretKey;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        KeyGenParameterSpec.Builder paramsBuilder = new KeyGenParameterSpec.Builder(
                realKeyName,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        );
        paramsBuilder.setBlockModes(ENCRYPTION_BLOCK_MODE);
        paramsBuilder.setEncryptionPaddings(ENCRYPTION_PADDING);
        paramsBuilder.setKeySize(KEY_SIZE);
        paramsBuilder.setUserAuthenticationRequired(true);
        configure.configure(paramsBuilder);
        KeyGenParameterSpec keyGenParams = paramsBuilder.build();
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    ANDROID_KEYSTORE
            );
            keyGenerator.init(keyGenParams);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
