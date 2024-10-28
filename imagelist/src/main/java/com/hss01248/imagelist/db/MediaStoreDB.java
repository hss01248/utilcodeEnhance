package com.hss01248.imagelist.db;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import com.hss01248.imagelist.NormalCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/28/24 9:55 AM
 * @Version 1.0
 */
public class MediaStoreDB {


   public static void getAlbums(final Context context,boolean isVideo, final NormalCallback<List<Album>> callback) {

        final Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String[] projection = new String[]{
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.BUCKET_ID,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.Media.DATA};
                if(isVideo){
                    projection = new String[]{
                            MediaStore.Video.Media._ID,
                            MediaStore.Video.Media.BUCKET_ID,
                            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                            MediaStore.Video.Media.DATA};
                }

                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);


                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                if(isVideo){
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }

                Cursor cursor = context.getApplicationContext().getContentResolver()
                        .query(uri, projection,
                                null, null, MediaStore.Images.Media.DATE_ADDED);
                if (cursor == null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFail(new Exception("no image found on this devices"));
                        }
                    });
                    return;
                }

                final ArrayList<Album> albums = new ArrayList<>(cursor.getCount());
                HashSet<Long> albumSet = new HashSet<>();
                File file;
                if (cursor.moveToLast()) {
                    do {
                        if (Thread.interrupted()) {
                            return;
                        }
                        long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                        long albumId = cursor.getLong(cursor.getColumnIndex(projection[1]));
                        String album = cursor.getString(cursor.getColumnIndex(projection[2]));
                        String image = cursor.getString(cursor.getColumnIndex(projection[3]));
                        //int size = cursor.getInt(cursor.getColumnIndex(COLUMN_COUNT));
// 根据ID获取图片的URI
                        Uri imageUri = ContentUris.withAppendedId(uri, id);
                        if (!albumSet.contains(albumId)) {
                        /*
                        It may happen that some image file paths are still present in cache,
                        though image file does not exist. These last as long as media
                        scanner is not run again. To avoid get such image file paths, check
                        if image file exists.
                         */
                            file = new File(image);
                            Log.d("ImageMediaCenterUtil", file.getAbsolutePath());
                            // if (file.exists()) {
                            Album album1 = new Album(album, image, albumId)
                                    .setVideo(isVideo)
                                    .setUri(imageUri);
                            albums.add(album1);
                            albumSet.add(albumId);

                            //File dir = file.getParentFile();
                            // File[] files = dir.listFiles();
                            int count = 0;
                            long fileSize = 0;
                                /*for (File file1 : files) {
                                    if(isImage(file1)){
                                    count++;
                                    fileSize = fileSize + file1.length();
                                    }
                                }*/
                            album1.count = count;
                            album1.fileSize = fileSize;
                            //}
                        }

                    } while (cursor.moveToPrevious());
                }
                cursor.close();


                //按文件大小排序:
                /*Collections.sort(albums, new Comparator<Album>() {
                    @Override
                    public int compare(Album o1, Album o2) {
                        return (int) (o2.fileSize - o1.fileSize);
                    }
                });*/

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(albums, null);
                    }
                });

            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    public static void listImagesByAlbumId(final Context context,
                                          @Nullable final long albumId,
                                           boolean isVideo,
                                           final NormalCallback<List<Image>> callback) {
        final Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String[] projection = new String[]{MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.SIZE,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media.DATE_MODIFIED,
                        MediaStore.Images.Media.WIDTH,
                        MediaStore.Images.Media.HEIGHT,
                        MediaStore.Images.Media.MIME_TYPE};

                if(isVideo){
                    projection = new String[]{MediaStore.Video.Media._ID,
                            MediaStore.Video.Media.DISPLAY_NAME,
                            MediaStore.Video.Media.DATA,
                            MediaStore.Video.Media.SIZE,
                            MediaStore.Video.Media.DATE_ADDED,
                            MediaStore.Video.Media.DATE_MODIFIED,
                            MediaStore.Video.Media.WIDTH,
                            MediaStore.Video.Media.HEIGHT,
                            MediaStore.Video.Media.MIME_TYPE};
                }

                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                if(isVideo){
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
                Cursor cursor = null;
                if(albumId != 0){
                    cursor = context.getContentResolver().query(uri, projection,
                            MediaStore.Images.Media.BUCKET_ID + " =?",
                            new String[]{albumId + ""},
                            MediaStore.Images.Media.DATE_ADDED);
                }else {
                    cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.Images.Media.DATE_ADDED);
                }

                if (cursor == null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFail(new Exception("no image found on this devices"));
                        }
                    });
                    return;
                }

            /*
            In case this runnable is executed to onChange calling loadImages,
            using countSelected variable can result in a race condition. To avoid that,
            tempCountSelected keeps track of number of selected images. On handling
            FETCH_COMPLETED message, countSelected is assigned value of tempCountSelected.
             */

                int count = 0;
                final ArrayList<Image> images = new ArrayList<>(cursor.getCount());
                if (cursor.moveToLast()) {
                    do {
                        if (Thread.interrupted()) {
                            return;
                        }

                        //long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                        // String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                        String path = cursor.getString(cursor.getColumnIndex(projection[2]));

                        //File file = new File(path);
                        //Log.i("path", path);
                        //  if (file.exists()) {


                        long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                        Uri imageUri = ContentUris.withAppendedId(uri, id);
                        images.add(new Image(
                                id,//_ID
                                cursor.getString(cursor.getColumnIndex(projection[1])),//DISPLAY_NAME
                                path,//DATA
                                cursor.getLong(cursor.getColumnIndex(projection[3])),//SIZE
                                cursor.getLong(cursor.getColumnIndex(projection[4])),//DATE_ADDED
                                cursor.getLong(cursor.getColumnIndex(projection[5])),//DATE_MODIFIED
                                cursor.getLong(cursor.getColumnIndex(projection[6])),//WIDTH
                                cursor.getLong(cursor.getColumnIndex(projection[6])),//HEIGHT
                                cursor.getString(cursor.getColumnIndex(projection[7]))//MIME_TYPE
                        ).setUri(imageUri));
                        // }
                        count++;
                        //加速
                        if(count == 50 && cursor.getCount() > 500){
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onFirst50Success(images,null);
                                }
                            });
                        }

                    } while (cursor.moveToPrevious());
                }
                cursor.close();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(images, null);
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
