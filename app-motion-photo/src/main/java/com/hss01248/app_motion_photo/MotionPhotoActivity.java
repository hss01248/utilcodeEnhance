package com.hss01248.app_motion_photo;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss01248.app_motion_photo.databinding.ActivityMotionPhotoBinding;
import com.hss01248.bigimageviewpager.motion.MotionEditViewHolder;
import com.hss01248.media.pick.MediaPickUtil;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/15/24 11:29 AM
 * @Version 1.0
 */
public class MotionPhotoActivity extends AppCompatActivity {
    ActivityMotionPhotoBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = ActivityMotionPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnPickAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPickUtil.pickImage(new MyCommonCallback<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        MotionEditViewHolder.start(uri.toString());
                    }
                });

            }
        });
    }
}
