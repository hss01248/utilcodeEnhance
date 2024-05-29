package com.hss01248.bitmap_saver;

import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.Keep;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPStaticUtils;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/11/24 3:43 PM
 * @Version 1.0
 */
@Keep
public class DirConfigInfo {


    //0: 公开,1 普通隐藏, 2 加密隐藏
    public static final int hiddenTypePublic = 0;
    public static final int hiddenTypeHidden = 1;
    public static final int hiddenTypeHiddenAndEncrypt = 2 ;
    public int hiddenType = 0;

    public  String subDir = "";
    public boolean subDirAsPrefix = true;
    public String prefix  = "";
    public boolean prefixAsSubDir = true;

    @Override
    public String toString() {
        return "DirConfigInfo{" +
                "hiddenType=" + hiddenType +
                ", subDir='" + subDir + '\'' +
                ", subDirAsPrefix=" + subDirAsPrefix +
                ", prefix='" + prefix + '\'' +
                ", prefixAsSubDir=" + prefixAsSubDir +
                '}';
    }

    static DirConfigInfo info;
    public static DirConfigInfo loadConfigInfo() {
        if(info !=null){
            return info;
        }
        String str =  SPStaticUtils.getString("dirConfig");
        if(TextUtils.isEmpty(str)){
            info =  new DirConfigInfo();
        }else {
            info =  GsonUtils.fromJson(str,DirConfigInfo.class);
        }
        return info;
    }

    public static void save(DirConfigInfo info){
        SPStaticUtils.put("dirConfig",GsonUtils.toJson(info));
    }

    public static void setPrefix(String name){
        if(TextUtils.isEmpty(name)){
            if(!TextUtils.isEmpty(loadConfigInfo().prefix)){
                loadConfigInfo().prefix = "";
                save(loadConfigInfo());
            }
        }else if(!name.equals(loadConfigInfo().prefix)){
            loadConfigInfo().prefix = name;
            save(loadConfigInfo());
        }

    }

    private static File parentDir(int hiddenType) {
        if(hiddenType ==0){
          return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"quick_screen_shot");
        }else if(hiddenType ==1){
            return  new File(Environment.getExternalStorageDirectory(),".tyuio");
        }else {
            File dir =   new File(Environment.getExternalStorageDirectory(),".tyuio");
            return  new File(dir,".0haden");
        }

    }

    public static String fileName() {
        // 获取当前的日期和时间
        Date now = new Date();
        // 创建SimpleDateFormat对象，定义日期时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        // 格式化当前日期和时间
        String formattedDate = sdf.format(now);
        return formattedDate;
    }
    public static String filePath(){
        loadConfigInfo();

        String fileName = fileName()+".jpg";
        String dir = parentDir(info.hiddenType).getAbsolutePath();

        if(!TextUtils.isEmpty(info.subDir)){
            dir = dir+"/"+info.subDir;
        }
        if(!TextUtils.isEmpty(info.prefix)){
            fileName = info.prefix + "-"+fileName;
            if(info.prefixAsSubDir){
                dir = dir+"/"+info.prefix;
            }
        }
        if(!TextUtils.isEmpty(info.subDir) && info.subDirAsPrefix){
            fileName = info.subDir + "-"+fileName;
        }

        String finalPath = dir+"/"+fileName;
        return  finalPath;

    }
}
