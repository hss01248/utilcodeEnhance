package com.hss01248.cipher;

import android.text.TextUtils;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.hss.utils.base.api.MyCommonCallback3;

/**
 * @Despciption todo
 * @Author hss
 * @Date 27/03/2024 17:09
 * @Version 1.0
 */
public class PasswordLoginByBiometric {

    public static void savePw(String name,String pw){
        try {
            byte[] encryptedData = RsaCipherUtil.encryptByPublicKeyWithUserVerify("bio-rsaCipher", pw.getBytes());
            SPStaticUtils.put("pw-"+name, ConvertUtils.bytes2HexString(encryptedData));
        } catch (Throwable e) {
            LogUtils.w(e);
        }
    }

    public static void getPwByName(String name, boolean canUseOnlyPasswordPin,MyCommonCallback3<String> callback){
        try {
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
