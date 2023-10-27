package com.hss01248.biometric.store;

/**
 * @Despciption todo
 * @Author hss
 * @Date 27/10/2023 15:53
 * @Version 1.0
 */


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.security.keystore.KeyProperties;
import java.io.File;
import java.io.IOException;
import javax.crypto.Cipher;



public class BiometricStorageFile {

    private static final String DIRECTORY_NAME = "biometric_storage";
    private static final String FILE_SUFFIX_V2 = ".v2.txt";

    private final String masterKeyName;
    private final String fileNameV2;
    private final File fileV2;
    private final CryptographyManager cryptographyManager;
    private final InitOptions options;

    public BiometricStorageFile(Context context, String baseName, InitOptions options) {
        this.options = options;
        this.masterKeyName = baseName + "_master_key";
        this.fileNameV2 = baseName + FILE_SUFFIX_V2;
        File baseDir = new File(context.getFilesDir(), DIRECTORY_NAME);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        this.fileV2 = new File(baseDir, fileNameV2);

        logger.trace("Initialized " + this + " with " + options);

        validateOptions();

        this.cryptographyManager = new CryptographyManager(() -> {
            setUserAuthenticationRequired(options.authenticationRequired);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                boolean useStrongBox = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE);
                setIsStrongBoxBacked(useStrongBox);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (options.authenticationValidityDurationSeconds == -1) {
                    setUserAuthenticationParameters(0, KeyProperties.AUTH_BIOMETRIC_STRONG);
                } else {
                    setUserAuthenticationParameters(options.authenticationValidityDurationSeconds, KeyProperties.AUTH_DEVICE_CREDENTIAL | KeyProperties.AUTH_BIOMETRIC_STRONG);
                }
            } else {
                setUserAuthenticationValidityDurationSeconds(options.authenticationValidityDurationSeconds);
            }
        });
    }

    private void validateOptions() {
        if (options.authenticationValidityDurationSeconds == -1 && !options.androidBiometricOnly) {
            throw new IllegalArgumentException("when authenticationValidityDurationSeconds is -1, androidBiometricOnly must be true");
        }
    }

    public Cipher cipherForEncrypt() {
        return cryptographyManager.getInitializedCipherForEncryption(masterKeyName);
    }

    public Cipher cipherForDecrypt() {
        if (fileV2.exists()) {
            return cryptographyManager.getInitializedCipherForDecryption(masterKeyName, fileV2);
        }
        logger.debug("No file exists, no IV found. null cipher.");
        return null;
    }

    public boolean exists() {
        return fileV2.exists();
    }

    public synchronized void writeFile(Cipher cipher, String content) throws IOException {
        Cipher useCipher = cipher != null ? cipher : cipherForEncrypt();
        try {
            byte[] encrypted = cryptographyManager.encryptData(content, useCipher);
            FileUtils.writeByteArrayToFile(fileV2, encrypted);
            logger.debug("Successfully written " + encrypted.length + " bytes.");
            return;
        } catch (IOException ex) {
            logger.error("Error while writing encrypted file " + fileV2, ex);
            throw ex;
        }
    }

    public synchronized String readFile(Cipher cipher) {
        Cipher useCipher = cipher != null ? cipher : cipherForDecrypt();
        if (useCipher != null && fileV2.exists()) {
            try {
                byte[] bytes = FileUtils.readFileToByteArray(fileV2);
                logger.debug("read " + bytes.length);
                return cryptographyManager.decryptData(bytes, useCipher);
            } catch (IOException ex) {
                logger.error("Error while writing encrypted file " + fileV2, ex);
                return null;
            }
        }
        logger.debug("File " + fileV2 + " does not exist. returning null.");
        return null;
    }

    public synchronized boolean deleteFile() {
        cryptographyManager.deleteKey(masterKeyName);
        return fileV2.delete();
    }

    @Override
    public String toString() {
        return "BiometricStorageFile(masterKeyName='" + masterKeyName + "', fileName='" + fileNameV2 + "', file=" + fileV2 + ")";
    }

    public void dispose() {
        logger.trace("dispose");
    }

}
