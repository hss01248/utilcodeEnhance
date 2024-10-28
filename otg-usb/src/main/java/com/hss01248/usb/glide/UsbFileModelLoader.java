package com.hss01248.usb.glide;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/28/24 3:38 PM
 * @Version 1.0
 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

import java.io.InputStream;

import me.jahnen.libaums.core.fs.UsbFile;
import me.jahnen.libaums.core.fs.UsbFileInputStream;

public class UsbFileModelLoader implements ModelLoader<UsbFile, InputStream> {



    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull UsbFile usbFile, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(usbFile.getAbsolutePath()), new UsbFileFetcher(usbFile));
    }

    @Override
    public boolean handles(UsbFile usbFile) {
        return usbFile != null;
    }

    private static class UsbFileFetcher implements DataFetcher<InputStream> {
        private final UsbFile usbFile;

        public UsbFileFetcher(UsbFile usbFile) {
            this.usbFile = usbFile;
        }


        @Override
        public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super InputStream> callback) {
            callback.onDataReady(new UsbFileInputStream(usbFile));
        }

        @Override
        public void cleanup() {

        }

        @Override
        public void cancel() {

        }

        @NonNull
        @Override
        public Class<InputStream> getDataClass() {
            return InputStream.class;
        }

        @NonNull
        @Override
        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }

    public static class Factory implements ModelLoaderFactory<UsbFile, InputStream> {
        @Override
        public ModelLoader<UsbFile, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new UsbFileModelLoader();
        }

        @Override
        public void teardown() {
            // 清理资源，如果有的话
        }
    }
}
