package com.hss01248.bigimageviewpager;

import android.content.Context;
import android.graphics.PointF;
import android.view.View;

import androidx.annotation.Keep;
import androidx.core.util.Pair;
import androidx.lifecycle.LifecycleOwner;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.hss.utils.enhance.viewholder.ContainerActivity2;
import com.hss.utils.enhance.viewholder.mvvm.BaseViewHolder;
import com.hss.utils.enhance.viewholder.mvvm.ContainerViewHolderWithTitleBar;
import com.hss01248.bigimageviewpager.databinding.ImageComprareBinding;
import com.hss01248.fullscreendialog.FullScreenDialogUtil;

import io.reactivex.functions.Consumer;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/24/24 10:35 AM
 * @Version 1.0
 */
@Keep
public class ImageCompareViewHolder extends BaseViewHolder<ImageComprareBinding, Pair<String,String>> {
    public ImageCompareViewHolder(Context context) {
        super(context);
    }

    public static void start(String path1,String path2){
        ContainerActivity2.start(new Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>>() {
            @Override
            public void accept(Pair<ContainerActivity2, ContainerViewHolderWithTitleBar> pair) throws Exception {
                ImageCompareViewHolder holder = new ImageCompareViewHolder(pair.first);
                pair.second.setContentView(holder.getRootView());
                pair.second.setTitleBarHidden(false);
                holder.init(new Pair<>(path1,path2));
            }
        });
    }

    @Override
    protected void initDataAndEventInternal(LifecycleOwner lifecycleOwner, Pair<String, String> pair) {
        binding.largeTop.loadUri(pair.first,false);
        binding.largeBottom.loadUri(pair.second,false);

        String topInfo = binding.largeTop.getInfoStr();
        binding.tvTop.setText(pair.first);
        binding.tvTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenDialogUtil.showText("original",topInfo);
            }
        });

        String bottomInfo = binding.largeBottom.getInfoStr();
        binding.tvBottom.setText(pair.second);
        binding.tvBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenDialogUtil.showText("after compressed",bottomInfo);
            }
        });





        binding.largeTop.showScale = true;
        binding.largeBottom.showScale = true;

        binding.largeTop.jpgView.setOnStateChangedListener(new SubsamplingScaleImageView.OnStateChangedListener() {
            @Override
            public void onScaleChanged(float newScale, int origin) {
                binding.largeBottom.jpgView.setScaleAndCenter(newScale,binding.largeTop.jpgView.getCenter());
            }

            @Override
            public void onCenterChanged(PointF newCenter, int origin) {
                binding.largeBottom.jpgView.setScaleAndCenter(binding.largeTop.jpgView.getScale(),newCenter);
            }
        });
        binding.largeBottom.jpgView.setOnStateChangedListener(new SubsamplingScaleImageView.OnStateChangedListener() {
            @Override
            public void onScaleChanged(float newScale, int origin) {
                binding.largeTop.jpgView.setScaleAndCenter(newScale,binding.largeBottom.jpgView.getCenter());
            }

            @Override
            public void onCenterChanged(PointF newCenter, int origin) {
                binding.largeTop.jpgView.setScaleAndCenter(binding.largeBottom.jpgView.getScale(),newCenter);
            }
        });

    }
}
