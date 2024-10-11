package com.hss01248.bigimageviewpager.motion;

import android.view.View;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.FileIOUtils;
import com.bumptech.glide.Glide;
import com.hss.utils.enhance.viewholder.MyRecyclerViewAdapter;
import com.hss.utils.enhance.viewholder.MyRecyclerViewHolder;
import com.hss01248.bigimageviewpager.databinding.MotionPhotoEditBinding;
import com.hss01248.bigimageviewpager.databinding.MotionPhotoVideoFrameBinding;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/10/24 5:08 PM
 * @Version 1.0
 */
public class VideoKeyFrameHolder extends MyRecyclerViewHolder<MotionPhotoVideoFrameBinding, byte[]> {
    public VideoKeyFrameHolder(@NonNull View itemView) {
        super(itemView);
    }

    public VideoKeyFrameHolder setParentBinding(MotionPhotoEditBinding parentBinding) {
        this.parentBinding = parentBinding;
        return this;
    }

    MotionPhotoEditBinding parentBinding;

    public VideoKeyFrameHolder setImageName(String imageName) {
        this.imageName = imageName;
        return this;
    }

    String imageName;

    @Override
    public void assignDatasAndEvents(byte[] data, int position, boolean isLast,
                                     boolean isListViewFling, List datas, MyRecyclerViewAdapter superRecyAdapter) {
        super.assignDatasAndEvents(data, position, isLast, isListViewFling, datas, superRecyAdapter);


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File image = new File(imageName);
                File file = new File(view.getContext().getExternalCacheDir(),"videocacheimgs");
                if(!file.exists()){
                    file.mkdirs();
                }
                file = new File(file,image.getName()+"-"+position+".jpg");
                if(file.exists() && file.length() >0){

                }else {
                    FileIOUtils.writeFileFromIS(file,new ByteArrayInputStream(data));
                }
                parentBinding.image.loadUri(file.getAbsolutePath(),false);
            }
        });
    }

    @Override
    public void assignDatasAndEvents(byte[] data) {
        Glide.with(binding.ivFrame)
                .load(data)
                .into(binding.ivFrame);
    }
}
