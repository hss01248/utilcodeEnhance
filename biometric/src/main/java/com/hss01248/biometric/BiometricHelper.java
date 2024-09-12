package com.hss01248.biometric;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyPermanentlyInvalidatedException;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;

/**
 * @Despciption todo
 * @Author hss
 * @Date 24/10/2023 16:43
 * @Version 1.0
 */
public class BiometricHelper {
    public static boolean isBiometricHardWareAvailable(Context con) {
        boolean result = false;
        BiometricManager biometricManager = BiometricManager.from(con);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    | BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    result = true;
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    result = false;
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    result = false;
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    result = false;
                    break;
                case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                    result = true;
                    break;
                case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                    result = true;
                    break;
                case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                    result = false;
                    break;
            }
        } else {
            switch (biometricManager.canAuthenticate()) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    result = true;
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    result = false;
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    result = false;
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    result = false;
                    break;
            }
        }
        return result;
    }

    public static boolean deviceHasPasswordPinLock(Context con) {
        KeyguardManager keymgr = (KeyguardManager) con.getSystemService(AppCompatActivity.KEYGUARD_SERVICE);
        if (keymgr.isKeyguardSecure()) {
            return true;
        }
        return false;
    }

    private static BiometricPrompt.PromptInfo initBiometricPrompt(String title,
                                                           String subtitle,
                                                           String description,
                                                           boolean setDeviceCred) {
        BiometricPrompt.PromptInfo promptInfo;
        if (setDeviceCred) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                int authFlag = BiometricManager.Authenticators.DEVICE_CREDENTIAL | BiometricManager.Authenticators.BIOMETRIC_STRONG;
                promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle(title)
                        .setSubtitle(subtitle)
                        .setDescription(description)
                        .setAllowedAuthenticators(authFlag)
                        .build();
            } else {
                        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle(title)
                        .setSubtitle(subtitle)
                        .setDescription(description)
                        .setDeviceCredentialAllowed(true)
                        .build();
            }
        } else {
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(title)
                    .setSubtitle(subtitle)
                    .setDescription(description)
                    .setNegativeButtonText("cancel")
                    .build();
        }
        return  promptInfo;
    }

    /**
     * 在Android设备上添加新的指纹时，系统会重新生成一个新的密钥，将旧的密钥无效化。这是为了保证安全性，防止旧的指纹信息被滥用 (返回true,此时需清除本地秘钥，重新生成)
     * 删除某个指纹时，系统并不会删除相应的密钥 返回false
     * @param userkey
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isFingerprintChanged(String userkey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            signature.initSign((PrivateKey)keyStore.getKey(userkey, null));
            signature.update("Hello, world!".getBytes());
            signature.sign();
            return false;
        } catch (KeyPermanentlyInvalidatedException var3) {
            return true;
        } catch (Exception var4) {
            return false;
        }
    }


    /**  https://zhuanlan.zhihu.com/p/489913461
     * 加密:
     * 首先通过 KeyStore，主要是得到一个包含密码的 SecretKey ，当然这里有一个关键操作，那就是 setUserAuthenticationRequired(true)，后面我们再解释；
     * 然后利用 SecretKey 创建 Clipher ， Clipher 就是 Java 里常用于加解密的对象；
     * 利用 BiometricPrompt.CryptoObject(cipher) 去调用生物认证授权；
     * 授权成功后会得到一个 AuthenticationResult ，Result 里面包含存在密钥信息的 cryptoObject?.cipher 和 cipher.iv 加密偏移向量；
     * 利用授权成功后的 cryptoObject?.cipher 对 Token 进行加密，然后和 cipher.iv 一起保存到 SharePerferences ，就完成了基于 BiometricPrompt 的加密保存；
     *
     * 解密:
     * 在 SharePerferences 里获取加密后的 Token 和 iv 信息；
     * 同样是利用 SecretKey 创建 Clipher ，不过这次要带上保存的 iv 信息；
     * 利用 BiometricPrompt.CryptoObject(cipher) 去调用生物认证授权；
     * 通过授权成功后的 cryptoObject?.cipher 对 Token 进行加密，得到原始的 Token 信息；
     * @param activityContext
     * @param crypto
     * @param callback
     */
    public static  void showBiometricDialog(FragmentActivity activityContext,
                                            @Nullable BiometricPrompt.CryptoObject crypto,
                                            boolean canUseOnlyPasswordPin,
                                            BiometricPrompt.AuthenticationCallback callback) {
        BiometricPrompt.PromptInfo promptInfo = null;
        if (isBiometricHardWareAvailable(activityContext)) {

            promptInfo = initBiometricPrompt(
                    BioConstants.BIOMETRIC_AUTHENTICATION,
                    BioConstants.BIOMETRIC_AUTHENTICATION_SUBTITLE,
                    BioConstants.BIOMETRIC_AUTHENTICATION_DESCRIPTION,
                    false
            );
        } else if (canUseOnlyPasswordPin && deviceHasPasswordPinLock(activityContext)) {

            promptInfo = initBiometricPrompt(
                    BioConstants.PASSWORD_PIN_AUTHENTICATION,
                    BioConstants.PASSWORD_PIN_AUTHENTICATION_SUBTITLE,
                    BioConstants.PASSWORD_PIN_AUTHENTICATION_DESCRIPTION,
                    true
            );
        }
        if (promptInfo == null) {
            callback.onAuthenticationError(BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL,"not supported");
            return;
        }
        BiometricPrompt biometricPrompt = new BiometricPrompt(activityContext, ContextCompat.getMainExecutor(activityContext), callback);
        if (crypto == null) {
            biometricPrompt.authenticate(promptInfo);
        } else {
            biometricPrompt.authenticate(promptInfo, crypto);

        }
    }

}
