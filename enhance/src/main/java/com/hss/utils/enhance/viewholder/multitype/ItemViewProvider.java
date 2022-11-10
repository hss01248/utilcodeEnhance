package com.hss.utils.enhance.viewholder.multitype;

import androidx.viewbinding.ViewBinding;

/**
 * @Despciption todo
 * @Author hss
 * @Date 21/06/2022 14:05
 * @Version 1.0
 */
public interface ItemViewProvider<VB extends ViewBinding,T extends IItemType> extends IItemType{

     ItemViewHolder<VB,T> createViewHolder();
}
