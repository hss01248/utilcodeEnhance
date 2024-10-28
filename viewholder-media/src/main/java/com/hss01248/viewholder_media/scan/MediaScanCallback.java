package com.hss01248.viewholder_media.scan;

import com.hss01248.viewholder_media.api.AbsFile;

import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/28/24 4:42 PM
 * @Version 1.0
 */
public interface MediaScanCallback {

    void onProgress(AbsFile  file, List<AbsFile> list);


    void onFinish(List<AbsFile> files);
}
