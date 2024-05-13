package com.hss.utils.enhance.viewholder;

import java.util.List;
@Deprecated
public interface Refreshable2 {
    void refresh();
    public void refresh(List newData);

    public void addAll(List newData);

    public void clear();

    public void delete(int position);

    public void add(Object object);

    List getListData();
}
