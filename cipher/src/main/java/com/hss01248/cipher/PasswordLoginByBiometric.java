package com.hss01248.cipher;

import android.text.TextUtils;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.base.api.MyCommonCallback3;
import com.hss01248.biometric.BiometricHelper;

/**
 * @Despciption todo
 * @Author hss
 * @Date 27/03/2024 17:09
 * @Version 1.0
 */
public class PasswordLoginByBiometric {

    public static void savePw(String name,String pw){
        try {
            LogUtils.d("isBiometricHardWareAvailable: "+BiometricHelper.isBiometricHardWareAvailable(Utils.getApp())
                    +",deviceHasPasswordPinLock:"+BiometricHelper.deviceHasPasswordPinLock(Utils.getApp())
                    +",isFingerprintChanged:"+BiometricHelper.isFingerprintChanged(name));
            byte[] encryptedData = RsaCipherUtil.encryptByPublicKeyWithUserVerify("bio-rsaCipher", pw.getBytes());
            SPStaticUtils.put("pw-"+name, ConvertUtils.bytes2HexString(encryptedData));
        } catch (Throwable e) {
            LogUtils.w(e);
        }
    }

    public static void getPwByName(String name, boolean canUseOnlyPasswordPin,MyCommonCallback3<String> callback){
        try {
            LogUtils.d("isBiometricHardWareAvailable: "+BiometricHelper.isBiometricHardWareAvailable(Utils.getApp())
                    +",deviceHasPasswordPinLock:"+BiometricHelper.deviceHasPasswordPinLock(Utils.getApp())
                    +",isFingerprintChanged:"+BiometricHelper.isFingerprintChanged(name));
            String str = SPStaticUtils.getString("pw-"+name);
            if(TextUtils.isEmpty(str)){
                return;
            }
            byte[]  encryptedData = ConvertUtils.hexString2Bytes(str);
            RsaCipherUtil.decryptByPrivateKeyWithUserVerify("bio-rsaCipher", encryptedData, canUseOnlyPasswordPin,
                    new MyCommonCallback3<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            String str2 = new String(bytes);
                            LogUtils.i("bio-rsaCipher-解密后: "+str2);
                            callback.onSuccess(str2);
                        }
                    });


        } catch (Throwable e) {
            LogUtils.w(e);
        }
    }
}
