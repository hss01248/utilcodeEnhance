package com.hss01248.biometric.store;

/**
 * @Despciption todo
 * @Author hss
 * @Date 27/10/2023 15:56
 * @Version 1.0
 */
public class InitOptions {
     int authenticationValidityDurationSeconds;
     boolean authenticationRequired;

    public int getAuthenticationValidityDurationSeconds() {
        return authenticationValidityDurationSeconds;
    }

    public void setAuthenticationValidityDurationSeconds(int authenticationValidityDurationSeconds) {
        this.authenticationValidityDurationSeconds = authenticationValidityDurationSeconds;
    }

    public boolean isAuthenticationRequired() {
        return authenticationRequired;
    }

    public void setAuthenticationRequired(boolean authenticationRequired) {
        this.authenticationRequired = authenticationRequired;
    }

    public boolean isAndroidBiometricOnly() {
        return androidBiometricOnly;
    }

    public void setAndroidBiometricOnly(boolean androidBiometricOnly) {
        this.androidBiometricOnly = androidBiometricOnly;
    }

     boolean androidBiometricOnly;

    public InitOptions(int authenticationValidityDurationSeconds, boolean authenticationRequired, boolean androidBiometricOnly) {
        this.authenticationValidityDurationSeconds = authenticationValidityDurationSeconds;
        this.authenticationRequired = authenticationRequired;
        this.androidBiometricOnly = androidBiometricOnly;
    }


}
