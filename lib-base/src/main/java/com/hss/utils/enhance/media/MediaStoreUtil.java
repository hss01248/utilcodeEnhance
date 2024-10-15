package com.hss.utils.enhance.media;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MediaStoreUtil {

    public static void saveFileToMediaStore(Context context, String filePath, String mimeType) {
        File file = new File(filePath);
        if (!file.exists() || !file.canRead()) {
            Log.e("MediaStoreUtil", "File does not exist or cannot be read.");
            return;
        }

        ContentResolver resolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        Uri collection;
        String fileName = file.getName();

        if (mimeType.startsWith("image/")) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp"); // for Images
        } else if (mimeType.startsWith("video/")) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, mimeType);
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/MyApp"); // for Videos
        } else {
            Log.e("MediaStoreUtil", "Unsupported mime type.");
            return;
        }

        Uri itemUri = resolver.insert(collection, contentValues);
        if (itemUri == null) {
            Log.e("MediaStoreUtil", "Failed to insert item.");
            return;
        }

        try (FileInputStream inStream = new FileInputStream(file);
             OutputStream outStream = resolver.openOutputStream(itemUri)) {

            if (outStream == null) {
                Log.e("MediaStoreUtil", "Failed to open output stream.");
                return;
            }

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            Log.e("MediaStoreUtil", "Error saving file: " + e.getMessage());
        }
    }
}

