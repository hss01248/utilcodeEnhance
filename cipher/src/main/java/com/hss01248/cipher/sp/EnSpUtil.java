package com.hss01248.cipher.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;


import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
/**
 * Created by huangshuisheng on 2017/12/13.
 */

public class EnSpUtil {

    private static Context context;

    public static void init(Context app) {
        context = app;

    }

    private static final String SP_FILE_NAME = "SpUtilEn";


    /**
     * 将此段代码拷贝使用
     * @param fileName
     * @param key
     * @param defVal
     */
    @Deprecated
    public static void copyBooleanKvFromNoneEncryt(String fileName,String key,Object defVal){
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName,Context.MODE_PRIVATE);
        if(!sharedPreferences.contains(key)){
            return;
        }
        if(defVal instanceof String){
            putString(key,sharedPreferences.getString(key,""));
        }else if(defVal instanceof Integer){
            putInt(key,sharedPreferences.getInt(key,0));
        }else if(defVal instanceof Boolean){
            putBoolean(key,sharedPreferences.getBoolean(key,false));
        }else if(defVal instanceof Long){
            putLong(key,sharedPreferences.getLong(key,0));
        }else if(defVal instanceof Float){
            putFloat(key,sharedPreferences.getFloat(key,0));
        }
        sharedPreferences.edit().remove(key).apply();
    }


    public static void putLong(String key,long val){
        getSP().edit().putString(md5(key),encrypt(val+""))
            .apply();
    }

    public static long getLong(String key, long defVal) {
        return getPrimateType(key, defVal, new IParse<Long>() {
            @Override
            public Long parse(String val) {
                return Long.parseLong(val);
            }
        });
    }

    public static void putBoolean(String key, boolean val) {
        getSP().edit().putString(md5(key), encrypt(val+"")).apply();
    }
    public static boolean putBooleanNow(String key, boolean val) {
        return getSP().edit().putString(md5(key), encrypt(val+"")).commit();
    }

    public static boolean getBoolean(String key, boolean defVal) {
        return getPrimateType(key, defVal, new IParse<Boolean>() {
            @Override
            public Boolean parse(String val) {
                return Boolean.parseBoolean(val);
            }
        });
    }

    public static void putInt(String key, int val) {
        getSP().edit().putString(md5(key), encrypt(val+"")).apply();
    }

    public static boolean putIntNow(String key, int val) {
       return getSP().edit().putString(md5(key), encrypt(val+"")).commit();
    }

    public static int getInt(String key, int defVal) {
        return getPrimateType(key, defVal, new IParse<Integer>() {
            @Override
            public Integer parse(String val) {
                return Integer.parseInt(val);
            }
        });
    }



    public static void putString(String key, String val) {
        getSP().edit().putString(md5(key), encrypt(val)).apply();
    }



    public static String getString(String key, String defVal) {
        String str = getSP().getString(md5(key),"");
        if(TextUtils.isEmpty(str)){
            return defVal;
        }
        String de = decrypt2(str);
        return de;
    }



    public static void putFloat(String key, float val) {
        getSP().edit().putString(md5(key), encrypt(val+"")).apply();
    }

    public static float getFloat(String key, float defVal) {
        return getPrimateType(key, defVal, new IParse<Float>() {
            @Override
            public Float parse(String val) {
                return Float.parseFloat(val);
            }
        });
    }

    public static void putDouble(String key, double val) {
        getSP().edit().putString(md5(key), encrypt(val+"")).apply();
    }

    public static double getDouble(String key, double defVal) {
        return getPrimateType(key, defVal, new IParse<Double>() {
            @Override
            public Double parse(String val) {
                return Double.parseDouble(val);
            }
        });
    }

    private static <T> T getPrimateType(String key,T defVal,IParse<T> parser){
        String str = getSP().getString(md5(key),"");
        if(TextUtils.isEmpty(str)){
            return defVal;
        }
        String de = decrypt2(str);
        try {
            return parser.parse(de);
        }catch (Throwable throwable){
           throwable.printStackTrace();
            return defVal;
        }
    }

    private static SharedPreferences getSP() {
        return context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
    }


    static String md5(String key){
        try {
            return MD5((key+"-md5").getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LogUtils.w(e);
            return key;
        }
    }

    static String MD5(byte[] srcBytes) {
        if (srcBytes == null) {
            throw new IllegalArgumentException("bytes cannot be null!");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(srcBytes);
            byte[] bytes = md.digest();
            int i = 0;
            StringBuffer buffer = new StringBuffer("");
            for (byte aByte : bytes) {
                i = aByte;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buffer.append("0");
                }
                buffer.append(Integer.toHexString(i));
            }
            return buffer.toString();
        } catch (Exception e) {
//            if (Configuration.debugMode) LoggerUtils.e(e.getMessage());
        }
        return "";
    }

    public static String encrypt(String val) {

        return doEn(val);
    }

    public static String decrypt2(String str) {

        return doDe(str);
    }



    public interface IParse<T>{

        T parse(String val);
    }


    private static String doEn(String val){
        try {
            // 加解密算法/模式/填充方式
            final String algorithmStr = "AES/CBC/PKCS5PADDING";
            byte[] keys = .getAes2().getBytes(Charset.forName("UTF-8"));
            byte[] base64 = EncryptUtils.encryptAES2Base64(val.getBytes(Charset.forName("UTF-8")),keys ,
                    algorithmStr,"0102030406960708".getBytes());
            // 原文链接：https://blog.csdn.net/u014133119/article/details/84965289
            String str =  new String(base64,Charset.forName("UTF-8"));
            //LogUtils.d("decode","decoded content:"+new String(EncryptUtils.decryptBase64AES(base64,keys, algorithmStr,"0102030406960708".getBytes()),Charset.forName("UTF-8")));
            return str;
        }catch (Throwable throwable){
            throwable.printStackTrace();
            return val;
        }
    }

    private static String doDe(String val){
        try {
            final String algorithmStr = "AES/CBC/PKCS5PADDING";
            byte[] keys = .getAes2().getBytes(Charset.forName("UTF-8"));
            byte[] base64 = val.getBytes(Charset.forName("UTF-8"));
            String str = new String(EncryptUtils.decryptBase64AES(base64,keys, algorithmStr,"0102030406960708".getBytes()),Charset.forName("UTF-8"));
            return str;
        }catch (Throwable throwable){
            LogUtils.w(throwable);
            return val;
        }
    }
}
