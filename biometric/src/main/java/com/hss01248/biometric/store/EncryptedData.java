package com.hss01248.biometric.store;

/**
 * @Despciption todo
 * @Author hss
 * @Date 27/10/2023 15:47
 * @Version 1.0
 */
public class EncryptedData {
    private final byte[] encryptedPayload;

    public EncryptedData(byte[] encryptedPayload) {
        this.encryptedPayload = encryptedPayload;
    }

    public byte[] getEncryptedPayload() {
        return encryptedPayload;
    }
}
