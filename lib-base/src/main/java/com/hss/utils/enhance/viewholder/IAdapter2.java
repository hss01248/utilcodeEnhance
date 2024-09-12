package com.hss.utils.enhance.viewholder;

import android.view.ViewGroup;
@Deprecated
public interface IAdapter2 {
    void notifyDataSetChanged();

    MyViewHolder generateNewHolder(  ViewGroup parent);
}
