package com.hss01248.viewholder_media.api;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/28/24 10:21 AM
 * @Version 1.0
 */
public class JavaFile implements AbsFile{

    public JavaFile(File file) {
        this.file = file;
    }

    protected File file;

    public JavaFile setRoot(boolean root) {
        this.root = root;
        return this;
    }

    private boolean root;

    @Override
    public Object getRealFile() {
        return file;
    }

    @Override
    public boolean isRoot() {
        return root;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public AbsFile getParent() {
        File file1 = file.getParentFile();
        if(file1 != null){
            return new JavaFile(file1);
        }
        return null;
    }

    @Override
    public AbsFile[] listFiles() {
        if(isDirectory()){
            File[] files = file.listFiles();
            if(files ==null || files.length ==0){
                return null;
            }
            AbsFile[] absFiles = new JavaFile[files.length];
            for (int i = 0; i < files.length; i++) {
                absFiles[i] = new JavaFile(files[i]);
            }
            return absFiles;
        }
        return null;
    }

    @Override
    public AbsFile[] listFiles(AbsFileFilter fileFilter) {
        if(isDirectory()){
            File[] files = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return fileFilter.accept(new JavaFile(file));
                }
            });
            if(files ==null || files.length ==0){
                return null;
            }
            AbsFile[] absFiles = new JavaFile[files.length];
            for (int i = 0; i < files.length; i++) {
                absFiles[i] = new JavaFile(files[i]);
            }
            return absFiles;
        }
        return null;
    }

    @Override
    public String getFullPath() {
        return file.getAbsolutePath();
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public long getLength() {
        return file.length();
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public boolean isFile() {
        return file.isFile();
    }

    @Override
    public long lastModified() {
        return file.lastModified();
    }

    @Override
    public AbsFile createDirectory(String name) {
        if(isDirectory()){
            File file1 = new File(file, name);
            if(!file1.exists()){
                file1.mkdirs();
            }else {
                if(file1.isFile()){
                    return null;
                }
            }
            JavaFile javaFile=  new JavaFile(file1);
            return javaFile;
        }
        return null;
    }

    @Override
    public AbsFile createNewFile(String name) {
        if(isDirectory()){
            File file1 = new File(file, name);
            if(!file1.exists()){
                try {
                    file1.createNewFile();
                } catch (IOException e) {
                    LogUtils.w(e);
                }
            }else {
                if(file1.isDirectory()){
                    return null;
                }
            }
            JavaFile javaFile=  new JavaFile(file1);
            return javaFile;
        }
        return null;
    }

    @Override
    public OutputStream createOutputStream() {
        if(isDirectory()){
            return null;
        }
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            LogUtils.w(e);
            return null;
        }
    }

    @Override
    public InputStream createInputStream() {
        if(isDirectory()){
            return null;
        }
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            LogUtils.w(e);
            return null;
        }
    }
}
