package com.hss01248.viewholder_media;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.hss.utils.enhance.viewholder.MyRecyclerViewHolder;
import com.hss01248.image.ImageLoader;
import com.hss01248.image.config.ScaleMode;
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

    public static final  float dividerWidth = 1.0f;

    public static int getSpanCount(){
        return DeviceUtils.isTablet() ? 6 : 5;
    }


    static int width = (ScreenUtils.getScreenWidth()-(getSpanCount()-1)* SizeUtils.dp2px(dividerWidth)) /getSpanCount();



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
        ViewGroup.LayoutParams layoutParams = binding.iv.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width;
        binding.iv.setLayoutParams(layoutParams);


        showInfo(data, itemView.getContext(), itemView,
                binding.tv,
                binding.iv,onItemClicked);
    }

    public static void showInfo(String data, Context context,
                                View rootView,
                                TextView textView,
                                ImageView iv,Consumer<String> onItemClicked) {
        File file = new File(data);
        if(file.isDirectory()){
            ImageLoader.with(context)
                    .res(R.drawable.icon_folder)
                    .scale(ScaleMode.CENTER_INSIDE)
                    .into(iv);
        }else {
            if(file.getName().endsWith(".jpg")||
            file.getName().endsWith(".png")||
                    file.getName().endsWith(".gif")||
                    file.getName().endsWith(".webp")||
            file.getName().endsWith(".mp4")){
                ImageLoader.with(context)
                        .file(data)
                        .defaultErrorRes(true)
                        .scale(ScaleMode.CENTER_CROP)
                        .into(iv);
            }else {
                ImageLoader.with(context)
                        .res(R.drawable.icon_file)
                        .scale(ScaleMode.CENTER_INSIDE)
                        .into(iv);
            }
        }
        String name = data.substring(data.lastIndexOf("/")+1);
        textView.setText(name);

        rootView.setOnClickListener(new View.OnClickListener() {
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
