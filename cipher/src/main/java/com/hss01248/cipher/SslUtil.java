package com.hss01248.cipher;

import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;

import java.security.KeyPair;
import java.util.Random;

import javax.crypto.SecretKey;

/**
 * @Despciption todo
 * @Author hss
 * @Date 13/11/2023 09:18
 * @Version 1.0
 */
public class SslUtil {

    public static void test(){
        //服务端传递证书(链)(公钥)到客户端
        //客户端校验证书合法性-与系统内置证书对比-校验签名
        //额外的: 服务端证书锁定-对比公钥的指纹
        //客户端生成对称加密的密钥(每次随机生成)
        //用服务端给的公钥加密后,传递给服务端
        //服务端用私钥解密,拿到密钥
        //后续数据传递使用这个密钥进行对称加密

        //基于以上逻辑,设计一套java实现的ssl功能,在http接口层实现,使用okhttp拦截器
        String alias = "myssl";
        String aliasSign = "myssl-sign";
        String aliasKey = "myssl-randomkey-";
        try {
            KeyPair keyPair = RsaCipherUtil.getRsaCipherKeyPair(alias, false);
            byte[] publicKey = keyPair.getPublic().getEncoded();
            LogUtils.d("服务端拿到了密钥对");
            /*byte[] sign = SignUtil.sign(aliasSign, publicKey);
            LogUtils.d("服务端对公钥进行加签");*/
            String s = EncryptUtils.encryptSHA256ToString(publicKey);
            LogUtils.d("服务端拿到了自己存储的密钥对, sha256值为:"+s);

            LogUtils.i("客户端端接收到了密钥对, sha256值为:"+s+",校验通过");

            String key = aliasKey+new Random().nextInt(999);
            SecretKey secretKey = AesCipherUtil.getOrCreateSecretKey(key, false);
            byte[] bytes = RsaCipherUtil.encryptByPublicKey(alias, secretKey.getEncoded());
            String clientKey = EncodeUtils.base64Encode2String(secretKey.getEncoded());
            LogUtils.i("客户端生成随机密钥,并加密,base64为:"+ EncodeUtils.base64Encode2String(bytes),
                    "加密前base64", EncodeUtils.base64Encode2String(secretKey.getEncoded()));
            byte[] bytes1 = RsaCipherUtil.decryptByPrivateKey(alias, bytes);
            String clientKeyReceived = EncodeUtils.base64Encode2String(bytes1);
            LogUtils.d("服务端收到随机密钥,并解密,解密后base64为:"+ clientKeyReceived);
            if(clientKey.equals(clientKeyReceived)){
                LogUtils.d("随机密钥解密成功,一致");
                byte[] encrypt = AesCipherUtil.encrypt(key, "789".getBytes());
                byte[] bytes2 = EncryptUtils.decryptAES(encrypt, bytes1, "", null);
                LogUtils.d("客户端解密数据: "+new String(bytes2));
                String str = new String(bytes2);
                if("789".equals(str)){
                    LogUtils.d("服务端解密成功: "+ 789);
                }else {
                    LogUtils.w("服务端解密失败 ");
                }

            }else {
                LogUtils.w("随机密钥不一致");
            }



        } catch (Throwable e) {
            LogUtils.w(e);
        }
    }
}
