package com.hss01248.usb.glide;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/28/24 3:50 PM
 * @Version 1.0
 */

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import me.jahnen.libaums.core.fs.UsbFile;

@GlideModule
public class UsbGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry.append(UsbFile.class, InputStream.class, new UsbFileModelLoader.Factory());
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
