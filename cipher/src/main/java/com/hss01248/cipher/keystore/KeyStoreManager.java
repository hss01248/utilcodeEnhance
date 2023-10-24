package com.hss01248.cipher.keystore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

/***
 * AndroidKeyStore密钥管理类   注意：在 Android 5.0（API 级别 21）和 Android 5.1（API 级别 22）中，您无法使用 Android 密钥库存储密钥集。
 */
public class KeyStoreManager {
    private final static String AndroidKeyStore = "AndroidKeyStore";
    private final static String SpName = "Sp_Name_Aes_Key";
    private final static String SP_KEY_NAME_AES = "Sp_Key_Name_Aes";
    private static final String RSA_MODE_OAEP = "RSA/ECB/PKCS1Padding";
    private static final String ALGORITHM = "SHA1PRNG";
    private static final String CRYPTO = "Crypto";
    private static Context appContext;
    private static KeyStore keyStore;


     static void turnInit(Context context) {
        if (context == null) {
            throw new NullPointerException("The Context cannot be null");
        }

        appContext = context;

        try {
            keyStore = KeyStore.getInstance(AndroidKeyStore);
            keyStore.load(null);
            Enumeration<String> aliases = keyStore.aliases();
            Log.d("dd2","aliases: "+aliases.nextElement());

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
   public   static SecretKey getSecretKey(String alias) {
        try {
            String enSeed = getAesKeyFromSp();
            String deSeed = "";
            //加密种子已存在,用RSA解密后,得到真正的密钥,然后拿
            if (!TextUtils.isEmpty(enSeed)) {
                deSeed = deSeed(alias, enSeed);
            } else {
                //没有真正密钥,那么就生成一个,保存到sp.同时返回刚生成的真正密钥
                deSeed = enSeed(alias, getRandomString());
            }
            //最终,也不是把字符串作为最终密钥,而是作为种子,传入SecureRandom,生成一个128位的最终密钥.
            //但这种做法官方不推荐,见http://www.voidcn.com/article/p-xfveygfh-bnb.html
            //直接使用那个字符串作为密钥即可.
            Log.d("dd2","getSecretKey: "+deSeed);
            return new SecretKeySpec(deSeed.getBytes("UTF-8"), "AES");
            //return getSecretKeyOfSeed(deSeed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SharedPreferences getSp() {
        return appContext.getSharedPreferences(SpName, Context.MODE_PRIVATE);
    }
    private static String getAesKeyFromSp() {
        return getSp().getString(SP_KEY_NAME_AES, "");
    }

    private static void saveAesKeyInSp(String seed) {
        getSp().edit().putString(SP_KEY_NAME_AES, seed).apply();
    }




    /**
     *
     * @param alias  keystore里密钥对的别名
     * @param seed  第一次生成的随机字符串
     * @return 返回原始的第一次生成的随机字符串
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static String enSeed(String alias, String seed) {
        PublicKey key = null;
        //Rsa已经存在
        if (!hasAlias(alias)) {
            //keystore里没有这个alias,就创建一个密钥对,然后拿到公钥
            key = createKeyPair(alias).getPublic();
        } else {
            try {
                //有的话,就直接拿对应的公钥
                key = keyStore.getCertificate(alias).getPublicKey();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        }

        if (key != null) {
            String encode = seed;
            try {
                //用公钥对第一次生成的随机字符串进行RSA加密
                encode = encryptRSA(seed, key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //然后保存到sp中
            saveAesKeyInSp(encode);
        }
        return seed;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static KeyPair createKeyPair(String alias) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", AndroidKeyStore);

            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 30);

            AlgorithmParameterSpec spec;
            spec = new KeyPairGeneratorSpec.Builder(appContext)
                    //使用别名来检索的关键。这是一个关键的关键!
                    .setAlias(alias)
                    // 用于生成自签名证书的主题 X500Principal 接受 RFC 1779/2253的专有名词
                    .setSubject(new X500Principal("CN=" + alias))
                    //用于自签名证书的序列号生成的一对。
                    .setSerialNumber(BigInteger.TEN)
                    // 签名在有效日期范围内
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();
            keyPairGenerator.initialize(spec);
            return keyPairGenerator.generateKeyPair();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 进行RSA加密
     * @param plainText 被加密数据
     * @param key 公钥值
     * @return
     * @throws Exception
     */
    private static String encryptRSA(String plainText, PublicKey key) throws Exception {

        Cipher cipher = Cipher.getInstance(RSA_MODE_OAEP);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedByte = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.encodeToString(encryptedByte, Base64.NO_WRAP);
    }

    /**
     *
     * @param alias Rsa密钥对在keystore里的别名
     * @param enseed 从sp取出来的,被Rsa加密后的密钥
     * @return 解密数据
     */
    private static String deSeed(String alias, String enseed) {
        //Rsa密钥对已经存在
        if (hasAlias(alias)) {
            KeyStore.PrivateKeyEntry privateKeyEntry = null;
            try {
                Cipher cipher = Cipher.getInstance(RSA_MODE_OAEP);
                privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore
                        .getEntry(alias, null);
                PrivateKey privateKey = privateKeyEntry.getPrivateKey();
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] encryptedByte = Base64.decode(enseed, Base64.NO_WRAP);
                //使用私钥对sp取出的字符串进行解密,得到原始16位的字符串. 注意编码与上面对应,utf-8
                return new String(cipher.doFinal(encryptedByte),"UTF-8");
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnrecoverableEntryException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return enseed;
        } else {
            return enseed;
        }
    }


    /**
     * 判断当前别名是否存在
     * @param alias
     * @return
     */
    private static boolean hasAlias(String alias) {
        try {
            return keyStore != null && keyStore.containsAlias(alias);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取16位的随机字符串,作为AES加密密钥
     *
     * @return
     */
    private static String getRandomString() {
        //定义一个字符串（A-Z，a-z，0-9）即62位；
        String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        //由Random生成随机数
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        //长度为几就循环几次
        for (int i = 0; i < 16; ++i) {
            //产生0-61的数字
            int number = random.nextInt(62);
            //将产生的数字通过length次承载到sb中
            sb.append(str.charAt(number));
        }
        //将承载的字符转换成字符串
        return sb.toString();
    }



    /**
     * 这种做法官方不推荐,见http://www.voidcn.com/article/p-xfveygfh-bnb.html
     * @param seed
     * @return
     * @throws Exception
     */
    @Deprecated
    @SuppressLint("DeletedProvider")
    private static SecretKey getSecretKeyOfSeed(String seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        // SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
        SecureRandom sr = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sr = SecureRandom.getInstance(ALGORITHM, CRYPTO);//new CryptoProvider()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            sr = SecureRandom.getInstance(ALGORITHM, CRYPTO);
        } else {
            sr = SecureRandom.getInstance(ALGORITHM);
        }
        sr.setSeed(seed.getBytes(Charset.forName("UTF-8")));
        //256 bits or 128 bits,192bits
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        return skey;
    }



}
