package com.hss.downloader;

import android.content.Context;

import com.hss01248.fileoperation.FileOpenUtil;
import com.hss01248.viewholder_media.FileTreeViewHolder;

import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 9/5/24 11:28 AM
 * @Version 1.0
 */
public class DefaultLargeImageViewer implements ILargeImagesViewer{
    @Override
    public void showBig(Context context, List<String> uris0, int position) {
        FileOpenUtil.open(uris0.get(position),uris0 );

    }

    @Override
    public void viewDir(Context context, String dir, String file) {
        FileTreeViewHolder.viewDirInActivity(dir);

    }
}
