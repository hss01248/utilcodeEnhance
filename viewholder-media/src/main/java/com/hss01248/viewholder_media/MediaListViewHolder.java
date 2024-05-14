package com.hss01248.viewholder_media;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;

import com.hss.utils.enhance.viewholder.MyRecyclerViewAdapter;
import com.hss.utils.enhance.viewholder.MyRecyclerViewHolder;
import com.hss.utils.enhance.viewholder.mvvm.BaseViewHolder;
import com.hss01248.viewholder_media.databinding.LayoutFileItemGridBinding;
import com.hss01248.viewholder_media.databinding.LayoutMediaListBinding;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/14/24 9:29 AM
 * @Version 1.0
 */
public class MediaListViewHolder extends BaseViewHolder<LayoutMediaListBinding, List<String>> {
    public MediaListViewHolder(Context context) {
        super(context);
    }
    public MediaListViewHolder setOnItemClicked(Consumer<String> onItemClicked) {
        this.onItemClicked = onItemClicked;
        return this;
    }

    Consumer<String> onItemClicked;
    MyRecyclerViewAdapter adapter;

    @Override
    protected void initDataAndEventInternal(LifecycleOwner lifecycleOwner, List<String> bean) {

        binding.recyclerView.setLayoutManager(new GridLayoutManager(context,4));

         adapter = new MyRecyclerViewAdapter() {
            @Override
            protected MyRecyclerViewHolder generateNewViewHolder(int viewType) {
                LayoutFileItemGridBinding inflate = LayoutFileItemGridBinding.inflate(
                        LayoutInflater.from(context), binding.getRoot(), false);

                return new FileItemGridViewHolder(inflate.getRoot())
                        .setOnItemClicked(onItemClicked)
                        .setBinding(inflate);
            }
        };
        binding.recyclerView.setAdapter(adapter);
        adapter.refresh(bean);
    }
}
