package com.hss01248.viewholder_media;

import androidx.annotation.Keep;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/20/24 4:34 PM
 * @Version 1.0
 */
@Keep
public class DisplayAndFilterInfo {

    public boolean sortOrderAsc;

    public int sortType;

    // 0 - grid, 1 -linear
    public int displayType;

    public boolean showFileName = true;
}
