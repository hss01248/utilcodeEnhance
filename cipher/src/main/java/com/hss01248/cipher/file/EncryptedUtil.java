package com.hss01248.cipher.file;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Despciption todo
 * @Author hss
 * @Date 25/10/2023 08:56
 * @Version 1.0
 */
public class EncryptedUtil {

    public static InputStream readEncryptedFile(String encryptedFilePath) throws Throwable{
        Context context = Utils.getApp();
        MasterKey mainKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();


        EncryptedFile encryptedFile = new EncryptedFile.Builder(context,
                new File(encryptedFilePath),
                mainKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build();

        //EncryptedFileInputStream
        InputStream inputStream = encryptedFile.openFileInput();
        /*ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int nextByte = inputStream.read();
        while (nextByte != -1) {
            byteArrayOutputStream.write(nextByte);
            nextByte = inputStream.read();
        }

        byte[] plaintext = byteArrayOutputStream.toByteArray();*/
        return inputStream;
    }

    public static boolean decryptFile(String encryptedFilePath,String targetFilePath,boolean successIfTargetFileExist) throws Throwable{
        File file = new File(targetFilePath);
        if(file.exists() && file.length() >0 ){
            LogUtils.i("targetFile exist",targetFilePath,file.length());
            if(successIfTargetFileExist){
                return true;
            }
        }
        InputStream inputStream = readEncryptedFile(encryptedFilePath);
        ioByBuffer(inputStream,new FileOutputStream(file));
        LogUtils.d("解密后文件路径和大小: ",targetFilePath,file.length());
        if(file.exists() && file.length() >0 ){
            return true;
        }
        return false;


    }

    public static void writeToEncrypted(InputStream inputStream,File targetEncryptedFile) throws Throwable{
        long begin=System.currentTimeMillis();
        Context context = Utils.getApp();
        int length = inputStream.available();
        MasterKey mainKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

// Creates a file with this name, or replaces an existing file
// that has the same name. Note that the file name cannot contain
// path separators.
        //File fileToWrite = new File(DIRECTORY, "my_sensitive_data.txt");
        EncryptedFile encryptedFile = new EncryptedFile.Builder(context,
                targetEncryptedFile,
                mainKey,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build();

// File cannot exist before using openFileOutput
        if (targetEncryptedFile.exists()) {
            LogUtils.w("目标文件已经存在,执行删除操作",targetEncryptedFile.getAbsolutePath());
            targetEncryptedFile.delete();
        }
        LogUtils.d("加密前文件,大小: ",length);
        ioByBuffer(inputStream,encryptedFile.openFileOutput());
        LogUtils.d("加密后文件,大小: ",targetEncryptedFile.length());

    }

    //注意：DataStore 是一种现代数据存储解决方案，应代替 SharedPreferences。它基于 Kotlin 协程和 Flow 构建，并克服了 SharedPreferences 的许多缺点。

    /**
     * EncryptedUtil.getEncryptedSP()   cost(ms)====>348  会比较耗时,所有需要缓存SharedPreferences对象
     * @param fileName
     * @return
     */
    public static SharedPreferences getEncryptedSP(String fileName){
        Context context = Utils.getApp();
        SharedPreferences sharedPreferences = null;
        try{
            MasterKey mainKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

             sharedPreferences = EncryptedSharedPreferences
                    .create(
                            context,
                            fileName,
                            mainKey,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );
        }catch (Throwable throwable){
            LogUtils.w(throwable,"getEncryptedSP failed");
            sharedPreferences = context.getSharedPreferences(fileName+"-not-encrypted",Context.MODE_PRIVATE);
        }

        return sharedPreferences;

    }

   public static  void ioByBuffer(InputStream inputStream,OutputStream outputStream) throws Throwable{
        long begin=System.currentTimeMillis();
        BufferedOutputStream bos = null;
        int len = inputStream.available();
        if(outputStream instanceof BufferedOutputStream){
            bos = (BufferedOutputStream) outputStream;
        }else {
            bos = new BufferedOutputStream(outputStream);
        }

        BufferedInputStream bis = null;
        if(inputStream instanceof BufferedInputStream){
            bis = (BufferedInputStream) inputStream;
        }else {
            bis = new BufferedInputStream(inputStream);
        }
        int size=0;
        int length2 = 0;
        byte[] buffer=new byte[1024];
        while((size=bis.read(buffer))!=-1){
            bos.write(buffer, 0, size);
            length2+=size;
        }
        bos.flush();
        bis.close();
        bos.close();
        long end=System.currentTimeMillis();
        LogUtils.i("使用缓冲输出流和缓冲输入流实现文件的复制完毕！耗时："+(end-begin)+"毫秒,length: "+len,length2);
    }
}
