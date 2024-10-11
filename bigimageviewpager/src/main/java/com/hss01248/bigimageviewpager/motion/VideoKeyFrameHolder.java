package com.hss01248.bigimageviewpager.motion;

import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.hss.utils.enhance.viewholder.MyRecyclerViewHolder;
import com.hss01248.bigimageviewpager.databinding.MotionPhotoVideoFrameBinding;

import java.io.File;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/10/24 5:08 PM
 * @Version 1.0
 */
public class VideoKeyFrameHolder extends MyRecyclerViewHolder<MotionPhotoVideoFrameBinding, File> {
    public VideoKeyFrameHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void assignDatasAndEvents(File data) {
        Glide.with(binding.ivFrame)
                .load(data)
                .into(binding.ivFrame);

    }
}
