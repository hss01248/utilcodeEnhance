package com.hss01248.bitmap_saver;

import java.io.File;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/31/24 11:02 AM
 * @Version 1.0
 */
public interface BitmapSaveListener {

    void onSaved(File file, int width,int height);
}
