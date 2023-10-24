package com.hss01248.biometric;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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



    public static  void showBiometricDialog(FragmentActivity activityContext,BiometricPrompt.AuthenticationCallback callback){
        BiometricPrompt.PromptInfo promptInfo = null;
        if (isBiometricHardWareAvailable(activityContext)) {

             promptInfo = initBiometricPrompt(
                    BioConstants.BIOMETRIC_AUTHENTICATION,
                    BioConstants.BIOMETRIC_AUTHENTICATION_SUBTITLE,
                    BioConstants.BIOMETRIC_AUTHENTICATION_DESCRIPTION,
                    false
            );
        } else if (deviceHasPasswordPinLock(activityContext)) {

            promptInfo = initBiometricPrompt(
                    BioConstants.PASSWORD_PIN_AUTHENTICATION,
                    BioConstants.PASSWORD_PIN_AUTHENTICATION_SUBTITLE,
                    BioConstants.PASSWORD_PIN_AUTHENTICATION_DESCRIPTION,
                    true
            );
        }
        if(promptInfo ==null){
            return;
        }
        BiometricPrompt  biometricPrompt = new BiometricPrompt(activityContext, ContextCompat.getMainExecutor(activityContext),callback);
        biometricPrompt.authenticate(promptInfo);
    }
}
