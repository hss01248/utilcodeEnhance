package com.hss01248.cipher.androidx;





/**
 * 有很大兼容性问题,暂不使用
 * https://developer.android.com/topic/security/data-android-versions
 */
/*@Deprecated
public class AndroidXEncryptUtil {

    static Context context;
    public static void init(Context context){
        AndroidXEncryptUtil.context = context;
    }

    *//**
     * 使用此api来操作文件
     * 需要Android sdk版本大于21
     * @param filePath
     * @return
     *//*
    public static EncryptedFile getFile(String filePath){
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return new EncryptedFile.Builder(context,
                    new File(filePath),
                    masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();
        }catch (Throwable throwable){
            throwable.printStackTrace();
            return null;
        }
    }

    *//**
     * 使用此api来操作sp
     * 需要Android sdk版本大于21
     * @param spName
     * @return
     *//*
    public static SharedPreferences getSp(String spName){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                MasterKey masterKey = new MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();
                return EncryptedSharedPreferences
                        .create(
                                context,
                                spName,
                                masterKey,
                                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                        );
            }
            return context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        }catch (Throwable throwable){
            throwable.printStackTrace();
            return context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        }

    }
}*/
