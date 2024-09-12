package com.hss01248.cipher;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.hss.utils.base.api.MyCommonCallback3;
import com.hss01248.biometric.BiometricHelper;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/11/2023 10:51
 * @Version 1.0
 */
public class RsaCipherUtil {
    final static  String KEY_NAME = "";

    /**
     * 公钥不需要验证
     * @param keyAlias
     * @param input
     * @return
     * @throws Throwable
     */
    public static byte[] encryptByPublicKeyWithUserVerify(String keyAlias,byte[] input) throws Throwable{
        KeyPair keyPair = getRsaCipherKeyPair(keyAlias, true);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        // 公钥无需授权可直接使用
        byte[] encryptedData = cipher.doFinal(input);
        return encryptedData;
    }

    public static void decryptByPrivateKeyWithUserVerify(String keyAlias, byte[] encryptedData,
                                                         boolean canUseOnlyPasswordPin,
                                                         MyCommonCallback3<byte[]> callback3) {
        try{
            KeyPair keyPair = getRsaCipherKeyPair(keyAlias, true);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());

            BiometricHelper.showBiometricDialog((FragmentActivity) ActivityUtils.getTopActivity(),
                    new BiometricPrompt.CryptoObject(cipher),
                    canUseOnlyPasswordPin,
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            callback3.onError(errorCode+"",errString+"",null);
                        }

                        @Override
                        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            try{
                                byte[] decryptedData2 = result.getCryptoObject().getCipher().doFinal(encryptedData);
                                callback3.onSuccess(decryptedData2);
                            }catch (Throwable throwable){
                                callback3.onError(throwable.getClass().getSimpleName(),throwable.getMessage(),throwable);
                            }


                        }
                    }
            );
        }catch (Throwable throwable){
            callback3.onError(throwable.getClass().getSimpleName(),throwable.getMessage(),throwable);
        }
    }
    public static byte[] encryptByPublicKey(String keyAlias,byte[] input) throws Throwable{
        KeyPair keyPair = getRsaCipherKeyPair(keyAlias, false);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        // 公钥无需授权可直接使用
        byte[] encryptedData = cipher.doFinal(input);
        return encryptedData;
    }
    public static byte[] decryptByPrivateKey(String keyAlias,byte[] encryptedData) throws Throwable{
        KeyPair keyPair = getRsaCipherKeyPair(keyAlias, false);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());

        byte[] decryptedData = cipher.doFinal(encryptedData);
        return  decryptedData;
    }




    /**
     * 获取用于加解密的 密钥对
     * @param keyAlias
     * @return
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static  KeyPair getRsaCipherKeyPair( String keyAlias,boolean userAuthenticationRequired) throws Throwable {

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        KeyStore.Entry entry = keyStore.getEntry(KEY_NAME + keyAlias, (KeyStore.ProtectionParameter)null);

        if (entry instanceof KeyStore.PrivateKeyEntry ) {
            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry)entry).getPrivateKey();
            PublicKey publicKey = ((KeyStore.PrivateKeyEntry)entry).getCertificate().getPublicKey();
            return new KeyPair(publicKey, privateKey);
        } else {

           /* BiometricManager biometricManager = BiometricManager.from(Utils.getApp());
            boolean supportAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    || biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL);*/
            //Checks if the user can authenticate with an authenticator that meets the given requirements. This requires at
            // least one of the specified authenticators to be present, enrolled, and available on the device.
            //Note that not all combinations of authenticator types are supported prior to Android 11 (API 30).
            // Specifically, DEVICE_CREDENTIAL alone is unsupported prior to API 30, and BIOMETRIC_STRONG | DEVICE_CREDENTIAL
            // is unsupported on API 28-29. Developers that wish to check for the presence of a PIN, pattern, or password on these
            // versions should instead use KeyguardManager.isDeviceSecure().

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            KeyGenParameterSpec.Builder builder = null;
            builder = (new KeyGenParameterSpec.Builder(KEY_NAME + keyAlias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT))
//                        .setDigests(KeyProperties.DIGEST_SHA256)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1) // 设置加密填充模式
//                        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1) //设置签名填充模式
                    //todo 需要先检查硬件是否支持
                    .setUserAuthenticationRequired(userAuthenticationRequired)
//                        .setUserAuthenticationValidityDurationSeconds(100)
                    .setKeySize(1024);
            keyPairGenerator.initialize(builder.build());
            return keyPairGenerator.generateKeyPair();
        }

    }




}
