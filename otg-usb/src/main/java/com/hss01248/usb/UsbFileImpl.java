package com.hss01248.usb;

import com.blankj.utilcode.util.LogUtils;
import com.hss01248.viewholder_media.api.AbsFile;
import com.hss01248.viewholder_media.api.AbsFileFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import me.jahnen.libaums.core.fs.UsbFile;
import me.jahnen.libaums.core.fs.UsbFileInputStream;
import me.jahnen.libaums.core.fs.UsbFileOutputStream;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/28/24 2:41 PM
 * @Version 1.0
 */
public class UsbFileImpl implements AbsFile {
    public UsbFileImpl(UsbFile file) {
        this.file = file;
    }

    UsbFile file;

    @Override
    public Object getRealFile() {
        return file;
    }

    @Override
    public boolean isRoot() {
        return file.isRoot();
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public AbsFile getParent() {
        return new UsbFileImpl(file.getParent());
    }

    @Override
    public AbsFile[] listFiles() {
        if(isDirectory()){
            UsbFile[] files = new UsbFile[0];
            try {
                files = file.listFiles();
            } catch (IOException e) {
                LogUtils.w(e);
                return null;
            }
            if(files ==null || files.length ==0){
                return null;
            }
            AbsFile[] absFiles = new UsbFileImpl[files.length];
            for (int i = 0; i < files.length; i++) {
                absFiles[i] = new UsbFileImpl(files[i]);
            }
            return absFiles;
        }
        return null;
    }

    @Override
    public AbsFile[] listFiles(AbsFileFilter fileFilter) {
        if(isDirectory()){
            UsbFile[] files = new UsbFile[0];
            try {
                files = file.listFiles();
            } catch (IOException e) {
                LogUtils.w(e);
                return null;
            }
            if(files ==null || files.length ==0){
                return null;
            }
            List<AbsFile> files1 = new ArrayList<>();

            for (int i = 0; i < files.length; i++) {
                UsbFileImpl usbFile = new UsbFileImpl(files[i]);
                if(fileFilter.accept(usbFile)){
                    files1.add(usbFile);
                }
            }
            AbsFile[] absFiles = new UsbFileImpl[files1.size()];
            for (int i = 0; i < files1.size(); i++) {
                absFiles[i] = files1.get(i);
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
        if(isDirectory()){
            return 0;
        }
        return file.getLength();
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public boolean isFile() {
        return !file.isDirectory();
    }

    @Override
    public long lastModified() {
        return file.lastModified();
    }

    @Override
    public AbsFile createDirectory(String name) {
        if(isFile()){
            return null;
        }
        try {
            return new UsbFileImpl(file.createDirectory(name));
        } catch (IOException e) {
           LogUtils.w(e);
           return null;
        }
    }

    @Override
    public AbsFile createNewFile(String name) {
        if(isFile()){
            return null;
        }
        try {
            return new UsbFileImpl(file.createFile(name));
        } catch (IOException e) {
            LogUtils.w(e);
            return null;
        }
    }

    @Override
    public OutputStream createOutputStream() {
        if(isDirectory()){
            return null;
        }
        return new UsbFileOutputStream(file);
    }

    @Override
    public InputStream createInputStream() {
        if(isDirectory()){
            return null;
        }
        return new UsbFileInputStream(file);
    }
}
