package com.hss01248.viewholder_media;


import android.view.View;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.hss.utils.enhance.viewholder.MyRecyclerViewHolder;
import com.hss01248.image.ImageLoader;
import com.hss01248.viewholder_media.databinding.LayoutFileItemGridBinding;

import java.io.File;

import io.reactivex.functions.Consumer;


/**
 * @Despciption todo
 * @Author hss
 * @Date 5/14/24 9:47 AM
 * @Version 1.0
 */
public class FileItemGridViewHolder extends MyRecyclerViewHolder<LayoutFileItemGridBinding,String> {

    public FileItemGridViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public FileItemGridViewHolder setOnItemClicked(Consumer<String> onItemClicked) {
        this.onItemClicked = onItemClicked;
        return this;
    }

    Consumer<String> onItemClicked;

    @Override
    public void assignDatasAndEvents(String data) {
        File file = new File(data);
        if(file.isDirectory()){
            ImageLoader.with(itemView.getContext())
                    .res(R.drawable.icon_folder)
                    .into(binding.iv);
        }else {
           /* ImageLoader.with(itemView.getContext())
                    .load(data)
                    .into(binding.iv);*/
        }

        String name = data.substring(data.lastIndexOf("/")+1);
        binding.tv.setText(name);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onItemClicked.accept(data);
                } catch (Exception e) {
                    LogUtils.w(e);
                }
            }
        });
    }
}
