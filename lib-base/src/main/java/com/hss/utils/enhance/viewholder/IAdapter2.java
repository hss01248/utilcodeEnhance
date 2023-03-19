package com.hss.utils.enhance.viewholder;

import android.view.ViewGroup;

public interface IAdapter2 {
    void notifyDataSetChanged();

    MyViewHolder generateNewHolder(  ViewGroup parent);
}
