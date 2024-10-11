package com.hss01248.bigimageviewpager.motion;

import android.content.Context;
import android.os.Build;
import android.view.View;

import androidx.core.util.Pair;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ReflectUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hss.utils.base.api.MyCommonCallback3;
import com.hss.utils.enhance.viewholder.ContainerActivity2;
import com.hss.utils.enhance.viewholder.MyRecyclerViewAdapter;
import com.hss.utils.enhance.viewholder.MyRecyclerViewHolder;
import com.hss.utils.enhance.viewholder.mvvm.BaseViewHolder;
import com.hss.utils.enhance.viewholder.mvvm.ContainerViewHolderWithTitleBar;
import com.hss01248.bigimageviewpager.databinding.MotionPhotoEditBinding;
import com.hss01248.bigimageviewpager.databinding.MotionPhotoVideoFrameBinding;
import com.hss01248.motion_photos.MotionPhotoUtil;
import com.iknow.android.features.trim.VideoTrimmerUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/10/24 4:38 PM
 * @Version 1.0
 */
public class MotionEditViewHolder extends BaseViewHolder<MotionPhotoEditBinding,String> {

    public static void start(String path){
        ContainerActivity2.start(new Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>>() {
            @Override
            public void accept(Pair<ContainerActivity2, ContainerViewHolderWithTitleBar> pair) throws Exception {
                MotionEditViewHolder holder = new MotionEditViewHolder(pair.first);
                pair.second.setTitle("motion photo edit");
                pair.second.setContentView(holder);
                holder.init(path);
            }
        });
    }
    public MotionEditViewHolder(Context context) {
        super(context);
    }

    @Override
    protected void initDataAndEventInternal(LifecycleOwner lifecycleOwner, String bean) {
        binding.image.loadUri(bean,false);

        showKeyFrames(bean);

        binding.btnGoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String motionVideoPath = MotionPhotoUtil.getMotionVideoPath(bean);


                VideoTrimmerUtil.trimLast(motionVideoPath, 300, new MyCommonCallback3<String>() {
                    @Override
                    public void onSuccess(String s) { ToastUtils.showShort(s);
                        ReflectUtils.reflect("com.hss01248.fileoperation.FileOpenUtil")
                                .method("open",s,null);
                    }
                });
                /*VideoTrimmerActivity.start(motionVideoPath,new TheActivityListener<VideoTrimmerActivity>(){

                    @Override
                    protected void onResultOK(Intent data) {
                        super.onResultOK(data);
                        ToastUtils.showShort(data.getStringExtra("path"));
                        LogUtils.d(data,data.getData());
                        ReflectUtils.reflect("com.hss01248.fileoperation.FileOpenUtil")
                                .method("open",data.getStringExtra("path"),null);

                    }
                });*/

            }
        });



    }


    private void showKeyFrames(String path) {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<List<byte[]>>() {
            @Override
            public List<byte[]> doInBackground() throws Throwable {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String motionVideoPath = MotionPhotoUtil.getMotionVideoPath(path);
                    return KeyFrameExtractor.extractFrames2(motionVideoPath,15);
                }
                return new ArrayList<>();
            }

            @Override
            public void onSuccess(List<byte[]> result) {

                binding.rvImages.setLayoutManager(
                        new LinearLayoutManager(getRootView().getContext(), RecyclerView.HORIZONTAL,false));
                MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter() {
                    @Override
                    protected MyRecyclerViewHolder generateNewViewHolder(int viewType) {
                        MotionPhotoVideoFrameBinding binding1 = MotionPhotoVideoFrameBinding.inflate(ActivityUtils.getTopActivity().getLayoutInflater());
                        return new VideoKeyFrameHolder(binding1.getRoot())
                                .setParentBinding(binding)
                                .setImageName(path)
                                .setBinding(binding1);
                    }
                };
                binding.rvImages.setAdapter(adapter);
                adapter.refresh(result);



            }
        });
    }
}
