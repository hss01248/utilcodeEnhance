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
import java.security.Signature;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/11/2023 10:52
 * @Version 1.0
 */
public class SignUtil {


    final static  String KEY_NAME = "SignUtil-";


    public static boolean verify(String keyAlias,byte[] signature,byte[]... data) throws Throwable{
        KeyPair keyPair = getSignKeyPare(keyAlias,false);

        Signature mSignature = Signature.getInstance("SHA256withRSA");
        mSignature.initVerify(keyPair.getPublic());
        for (byte[] datum : data) {
            mSignature.update(datum);
        }
        boolean result1 = mSignature.verify(signature);
        return  result1;
    }
    public static byte[] sign(String keyAlias,byte[]... data) throws Throwable{
        KeyPair keyPair = getSignKeyPare(keyAlias, false);

        Signature mSignature = Signature.getInstance("SHA256withRSA");
        mSignature.initSign(keyPair.getPrivate());

        for (byte[] datum : data) {
            mSignature.update(datum);
        }
       return  mSignature.sign();
    }

    public static boolean verifyWithUserVerify(String keyAlias,byte[] signature,byte[]... data) throws Throwable{
        KeyPair keyPair = getSignKeyPare(keyAlias,true);

        Signature mSignature = Signature.getInstance("SHA256withRSA");
        mSignature.initVerify(keyPair.getPublic());
        for (byte[] datum : data) {
            mSignature.update(datum);
        }
        boolean result1 = mSignature.verify(signature);
        return  result1;
    }

    public static void signWithUserVerify(String keyAlias,
                                            boolean canUseOnlyPasswordPin,
                                            MyCommonCallback3<byte[]> callback3,
                                            byte[]... data) {
        try{
            KeyPair keyPair = getSignKeyPare(keyAlias, false);

            Signature mSignature = Signature.getInstance("SHA256withRSA");
            mSignature.initSign(keyPair.getPrivate());
            BiometricHelper.showBiometricDialog((FragmentActivity) ActivityUtils.getTopActivity(),
                    new BiometricPrompt.CryptoObject(mSignature),
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
                                for (byte[] datum : data) {
                                    result.getCryptoObject().getSignature().update(datum);
                                }
                                byte[] sign = result.getCryptoObject().getSignature().sign();
                                callback3.onSuccess(sign);
                            }catch (Throwable throwable){
                                callback3.onError(throwable.getClass().getSimpleName(),throwable.getMessage(),throwable);
                            }
                        }
                    });
        }catch (Throwable throwable){
            callback3.onError(throwable.getClass().getSimpleName(),throwable.getMessage(),throwable);
        }

    }




    /**
     * 获取用于 数据加签/验签的 秘钥对
     * @param userAuthenticationRequired
     * @param keyAlias
     * @return
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static KeyPair getSignKeyPare( String keyAlias,boolean userAuthenticationRequired) throws Throwable {

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        KeyStore.Entry entry = keyStore.getEntry(KEY_NAME + keyAlias, null);

        if (entry instanceof KeyStore.PrivateKeyEntry ) {
            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry)entry).getPrivateKey();
            PublicKey publicKey = ((KeyStore.PrivateKeyEntry)entry).getCertificate().getPublicKey();
            return new KeyPair(publicKey, privateKey);
        } else {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            KeyGenParameterSpec.Builder builder = null;
            builder = new KeyGenParameterSpec.Builder(KEY_NAME + keyAlias,
                    KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                    .setDigests(KeyProperties.DIGEST_SHA256)
//                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
//                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1) // 设置加密填充模式
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1) //设置签名填充模式
                    .setUserAuthenticationRequired(userAuthenticationRequired)
//                        .setUserAuthenticationValidityDurationSeconds(100)
                    .setKeySize(1024);
            keyPairGenerator.initialize(builder.build());
            return keyPairGenerator.generateKeyPair();
        }

    }
}
