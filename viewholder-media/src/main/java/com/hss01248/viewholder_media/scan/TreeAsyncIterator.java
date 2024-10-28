package com.hss01248.viewholder_media.scan;


import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hss01248.viewholder_media.api.AbsFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 02/02/2023 10:43
 * @Version 1.0
 */
public class TreeAsyncIterator<T> {



    public  void iteratorTree2(AbsFile<T> dir, boolean includeSubDir, long totalFileCount,
                               TreeIteratorCallback<AbsFile<T>> callback){
        iteratorTree(dir, includeSubDir, totalFileCount,
                new long[]{0}, new long[]{0}, System.currentTimeMillis(), new ArrayList<>(), callback);
    }

    protected   void iteratorTree(AbsFile<T> dir, boolean includeSubDir, long totalFileCount,
                                          long[] fileCount, long[] cal, long startTime, List<AbsFile<T>> failedList,
                                    TreeIteratorCallback<AbsFile<T>> callback) {


        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Integer>() {
            @Override
            public Integer doInBackground() throws Throwable {
                cal[0]++;
                if (dir == null) {
                    return 0;
                }
                // dir doesn't exist then return true
                if (!dir.exists()) {
                    return 0;
                }
                // dir isn't a directory then return false
                if (!dir.isDirectory()) {
                    return 0;
                }
                if (callback.doCancel(dir)) {
                    LogUtils.i("操作取消");
                    callback.onDirCanceled(dir);
                    return 0;
                }
                if(callback.skipDir(dir)){
                    LogUtils.d("跳过文件夹:"+dir.getFullPath());
                    return 0;
                }
                callback.onDirStart(dir);
                AbsFile<T>[] files = dir.listFiles();
                if (files == null || files.length == 0) {
                    callback.onDirFinished(dir);
                    return 0;
                }

                //广度优先遍历:
                List<AbsFile<T>> dirs = new ArrayList<>();
                for (AbsFile<T> file : files) {
                    if (callback.doCancel(file)) {
                        LogUtils.i("操作取消");
                        callback.onDirCanceled(dir);
                        return 0;
                    }
                    if (file.isFile()) {
                        fileCount[0]++;
                        boolean success = callback.onFile(file);
                        callback.onProgress(totalFileCount, fileCount[0], file, "");
                        if (!success) {
                            failedList.add(file);
                        }
                    } else {
                        dirs.add(file);
                    }
                }

                if (!includeSubDir) {
                    callback.onDirFinished(dir);
                    return files.length;
                }
                for (AbsFile<T> file : dirs) {
                    if (callback.doCancel(file)) {
                        LogUtils.i("操作取消");
                        callback.onDirCanceled(dir);
                        return 0;
                    }
                    iteratorTree(file, includeSubDir, totalFileCount, fileCount, cal, startTime, failedList, callback);
                }
                callback.onDirFinished(dir);
                return files.length;
            }

            @Override
            public void onSuccess(Integer result) {
                cal[0]--;
                LogUtils.v("当前文件夹遍历结束: " + cal[0] + "   ---> " + dir.getFullPath(), "文件个数:" + result, "总文件个数:" + totalFileCount);
                if (cal[0] == 0) {
                    callback.onFinished(totalFileCount, System.currentTimeMillis() - startTime, failedList);
                }
                if (cal[0] < 0) {
                    LogUtils.w("计数出错 : " + cal[0] + " , " + dir.getFullPath());
                }
            }
        });
    }
}
