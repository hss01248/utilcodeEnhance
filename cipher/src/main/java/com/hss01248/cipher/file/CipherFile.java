package com.hss01248.cipher.file;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

public class CipherFile {

    // 加密类型，支持这三种DESede,Blowfish,AES
    private static final String ENCRYPT_TYPE = "AES";



    public static boolean encrypt2File(InputStream is, String destFileName) {
        OutputStream out = null;
        CipherInputStream cis = null;
        try {
            out = new FileOutputStream(destFileName);
            //SecretKey deskey = new SecretKeySpec(getKey().getBytes(), ENCRYPT_TYPE);
            //Key length not 128/192/256 bits
            Cipher cipher = Cipher.getInstance(ENCRYPT_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            // 创建加密流
            cis = new CipherInputStream(is, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = cis.read(buffer)) > 0) {
                out.write(buffer, 0, r);
            }
            //System.out.println("文件"  + "加密完成，加密后的文件是:" + destFileName);
            return true;
        } catch (Exception e) {
            //System.out.println("加密文件"  + "出现异常");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (cis != null) {cis.close();}
            } catch (IOException e) {}
            try {
                if (is != null) {is.close();}
            } catch (IOException e) {}
            try {
                if (out != null) {out.close();}
            } catch (IOException e) {}
        }
    }



    /**
     * 加密文件
     * @param srcFileName  要加密的文件
     * @param destFileName 加密后存放的文件名
     */
    public static boolean encryptFile(String srcFileName, String destFileName) {
        try {
            return encrypt2File(new FileInputStream(srcFileName),destFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解密文件
     * @param srcFileName  要解密的文件
     * @param destFileName 解密后存放的文件名
     */
    public static boolean decryptFile(String srcFileName, String destFileName) {
        try {
            return decrypt2Stream(srcFileName,new FileOutputStream(destFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean decrypt2Stream(String srcFileName, OutputStream out) {
        InputStream is = null;

        CipherOutputStream cos = null;
        try {
            is = new FileInputStream(srcFileName);
           // SecretKey deskey = new SecretKeySpec(getKey().getBytes(), ENCRYPT_TYPE);
            Cipher cipher = Cipher.getInstance(ENCRYPT_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
            // 创建解密流
            cos = new CipherOutputStream(out, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = is.read(buffer)) > 0) {
                cos.write(buffer, 0, r);
            }
            //System.out.println("文件" + srcFileName + "解密完成，解密后的文件是:" );
            return true;
        } catch (Exception e) {
            //System.out.println("解密文件" + srcFileName + "出现异常");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (cos != null) {cos.close();}
            } catch (IOException e) {}
            try {
                if (is != null) {is.close();}
            } catch (IOException e) {}
            try {
                if (out != null) {out.close();}
            } catch (IOException e) {}
        }
    }

    private static String getKey() {
        return "";

    }

    private static Key getSecretKey() {
        return "";
    }


}
