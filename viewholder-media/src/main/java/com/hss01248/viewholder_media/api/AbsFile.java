package com.hss01248.viewholder_media.api;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/28/24 10:20 AM
 * @Version 1.0
 */
public interface AbsFile<T> {


    T getRealFile();
    boolean isRoot();

    boolean exists();
    AbsFile getParent();
    AbsFile[] listFiles();

    AbsFile[] listFiles(AbsFileFilter fileFilter);

    String getFullPath();

    String getName();

    long getLength();

    boolean isDirectory();

    boolean isFile();

    long lastModified();

    /**
     * 在当前目录下创建一个目录
     * @return
     */
    AbsFile createDirectory(String name);

    /**
     * 在当前目录下创建一个新文件
     * @return
     */
    AbsFile createNewFile(String name);

    OutputStream createOutputStream();

    InputStream createInputStream();




}
