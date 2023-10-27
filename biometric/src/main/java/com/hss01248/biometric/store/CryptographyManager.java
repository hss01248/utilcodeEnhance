package com.hss01248.biometric.store;

import java.io.File;

import javax.crypto.Cipher;

/**
 * @Despciption todo
 * @Author hss
 * @Date 27/10/2023 15:46
 * @Version 1.0
 */
public interface CryptographyManager {

    /**
     * This method first gets or generates an instance of SecretKey and then initializes the Cipher
     * with the key. The secret key uses [ENCRYPT_MODE][Cipher.ENCRYPT_MODE] is used.
     */
    Cipher getInitializedCipherForEncryption(String keyName);

    /**
     * This method first gets or generates an instance of SecretKey and then initializes the Cipher
     * with the key. The secret key uses [DECRYPT_MODE][Cipher.DECRYPT_MODE] is used.
     */
    Cipher getInitializedCipherForDecryption(String keyName, byte[] initializationVector);
    Cipher getInitializedCipherForDecryption(String keyName, File encryptedDataFile);

    /**
     * The Cipher created with [getInitializedCipherForEncryption] is used here
     */
    EncryptedData encryptData(String plaintext, Cipher cipher);

    /**
     * The Cipher created with [getInitializedCipherForDecryption] is used here
     */
    String decryptData(byte[] ciphertext, Cipher cipher);
}
