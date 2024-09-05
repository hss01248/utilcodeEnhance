package com.hss01248.webviewspider;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;

import com.hss01248.imagelist.album.IViewInit;
import com.hss01248.imagelist.album.ImageListView;
import com.hss01248.imagelist.album.ImageMediaCenterUtil;
import com.hss01248.viewholder_media.FileTreeViewHolder;

import java.util.List;
import java.util.Map;

/**
 * @Despciption todo
 * @Author hss
 * @Date 9/5/24 11:25 AM
 * @Version 1.0
 */
public class DefaultShowUrls implements IShowUrls{
    @Override
    public void showUrls(Context context, String pageTitle, List<String> urls,
                         @Nullable String downloadDir, boolean hideDir, boolean downloadImmediately) {
        ImageMediaCenterUtil.showViewAsActivityOrDialog(context, false, new IViewInit() {
            @Override
            public View init(Activity activity) {
                ImageListView listView = new ImageListView(context);
                listView.showUrls(pageTitle,urls, downloadDir,hideDir,downloadImmediately);
                return listView;
            }
        });
    }

    @Override
    public void showUrls(Context context, String pageTitle, Map<String, List<String>> titlesToImags,
                         List<String> urls, @Nullable String downloadDir, boolean hideDir,
                         boolean downloadImmediately) {
        ImageMediaCenterUtil.showViewAsActivityOrDialog(context, false, new IViewInit() {
            @Override
            public View init(Activity activity) {
                ImageListView listView = new ImageListView(context);
                listView.showUrlsFromMap(pageTitle,titlesToImags,urls, downloadDir,hideDir,downloadImmediately);
                return listView;
            }
        });
    }

    @Override
    public void showFolder(Context context, String absolutePath) {
        ImageMediaCenterUtil.showViewAsActivityOrDialog(context, false, new IViewInit() {
            @Override
            public View init(Activity activity) {
                ImageListView listView = new ImageListView(context);
                listView.showImagesInDir(absolutePath);
                return listView;
            }
        });
        FileTreeViewHolder.viewDirInActivity(absolutePath);
    }
}
