package com.hss01248.biometric;

import java.io.File;

import javax.crypto.Cipher;



public interface CryptographyManager2 {
    /**
     * This method first gets or generates an instance of SecretKey and then initializes the Cipher
     * with the key. The secret key uses [ENCRYPT_MODE][Cipher.ENCRYPT_MODE] is used.
     */
    Cipher getInitializedCipherForEncryption(String keyName) throws Throwable;

    /**
     * This method first gets or generates an instance of SecretKey and then initializes the Cipher
     * with the key. The secret key uses [DECRYPT_MODE][Cipher.DECRYPT_MODE] is used.
     */
    Cipher getInitializedCipherForDecryption(String keyName, byte[] initializationVector) throws Throwable;
    Cipher getInitializedCipherForDecryption(String keyName, File encryptedDataFile) throws Throwable;

    /**
     * The Cipher created with [getInitializedCipherForEncryption] is used here
     */
    byte[] encryptData(String plaintext, Cipher cipher) throws Throwable;

    /**
     * The Cipher created with [getInitializedCipherForDecryption] is used here
     */
    String decryptData(byte[] ciphertext, Cipher cipher) throws Throwable;
}
