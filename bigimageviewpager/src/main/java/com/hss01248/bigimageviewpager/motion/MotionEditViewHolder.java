package com.hss01248.bigimageviewpager.motion;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import androidx.core.util.Pair;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.hss.utils.enhance.ContentUriUtil;
import com.hss.utils.enhance.api.MyCommonCallback;
import com.hss.utils.enhance.viewholder.ContainerActivity2;
import com.hss.utils.enhance.viewholder.MyRecyclerViewAdapter;
import com.hss.utils.enhance.viewholder.MyRecyclerViewHolder;
import com.hss.utils.enhance.viewholder.mvvm.BaseViewHolder;
import com.hss.utils.enhance.viewholder.mvvm.ContainerViewHolderWithTitleBar;
import com.hss01248.activityresult.TheActivityListener;
import com.hss01248.bigimageviewpager.LargeImageViewer;
import com.hss01248.bigimageviewpager.databinding.MotionPhotoEditBinding;
import com.hss01248.bigimageviewpager.databinding.MotionPhotoVideoFrameBinding;
import com.hss01248.fullscreendialog.FullScreenDialogUtil;
import com.hss01248.media.metadata.MetaDataUtil;
import com.hss01248.motion_photos.MotionPhotoUtil;
import com.hss01248.motion_photos_android.AndroidMotionImpl;
import com.hss01248.motion_photos_android.AndroidMotionUtil;
import com.iknow.android.features.trim.VideoTrimmerActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    String relativePath;
    public MotionEditViewHolder(Context context) {
        super(context);
    }

    @Override
    protected void initDataAndEventInternal(LifecycleOwner lifecycleOwner, String bean) {
        //将content://的拷贝一份到应用私有目录:
        String realFilePath = copyUriToCacheDir(bean);
        initInfo = realFilePath;



        binding.image.loadUri(initInfo,false);

        showKeyFrames(initInfo);

        binding.btnVideoCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String motionVideoPath = MotionPhotoUtil.getMotionVideoPath(initInfo);
                VideoTrimmerActivity.start(motionVideoPath,new TheActivityListener<VideoTrimmerActivity>(){

                    @Override
                    protected void onResultOK(Intent data) {
                        super.onResultOK(data);
                        String path = data.getStringExtra("path");
                        ToastUtils.showShort(data.getStringExtra("path"));
                        LogUtils.d(data,data.getData());
                        //ReflectUtils.reflect("com.hss01248.fileoperation.FileOpenUtil").method("open",data.getStringExtra("path"),null);
                        changeVideo(initInfo,path);
                    }
                });

            }
        });

        binding.btnMetaInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidMotionUtil.metaInfo(initInfo);
            }
        });

        binding.btnRemoveVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = AndroidMotionUtil.removeVideo(initInfo);
                LargeImageViewer.showOne(s);
            }
        });

        binding.btnCompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidMotionUtil.forceCompressMotionPhoto(initInfo);
            }
        });

        binding.btnExtractVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidMotionUtil.extractVideo(initInfo,relativePath);
            }
        });



    }

    private  String copyUriToCacheDir(String bean)  {
        if(bean.startsWith("content://")){

            Map<String, Object> meta =  ContentUriUtil.queryMediaStore(Uri.parse(bean));

            String name = (String) meta.get(MediaStore.MediaColumns.DISPLAY_NAME);
            if(TextUtils.isEmpty(name)){
                name = System.currentTimeMillis()+".jpg";
            }
            relativePath = meta.get(MediaStore.MediaColumns.RELATIVE_PATH)+"";
            if(relativePath .equals("null") || relativePath.equals("")){
                relativePath = meta.get(MediaStore.MediaColumns.DATA)+"";
                relativePath = relativePath.substring(Environment.getExternalStorageDirectory().getAbsolutePath().length()+1);
                relativePath = relativePath.substring(0,relativePath.lastIndexOf("/"));
            }

            File file = new File(AndroidMotionImpl.motionImageCacheDir(),name);
            InputStream stream = null;
            try {
                stream = Utils.getApp().getContentResolver().openInputStream(Uri.parse(bean));
                boolean b = FileIOUtils.writeFileFromIS(file, stream, false);
                if(b && file.exists() && file.length() >0){
                    return file.getAbsolutePath();
                }
            } catch (FileNotFoundException e) {
                LogUtils.w(e);
            }
        }
        return bean;
    }

    private void changeVideo(String imageFilePath, String videoPath) {
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<File>() {
            @Override
            public File doInBackground() throws Throwable {
                return AndroidMotionImpl.replaceVideoFile(imageFilePath,videoPath);
            }

            @Override
            public void onSuccess(File result) {
                LargeImageViewer.showOne(result.getAbsolutePath());
                LogUtils.i("result image: "+result.getAbsolutePath(),
                        "length:"+result.length(),"isMotion:"+MotionPhotoUtil.isMotionImage(result.getAbsolutePath(),false));
                FullScreenDialogUtil.showMap("exif", MetaDataUtil.getMetaData(result.getAbsolutePath()));

                binding.image.loadUri(result.getAbsolutePath(),true);
                showKeyFrames(result.getAbsolutePath());
            }
        });

    }

    MyRecyclerViewAdapter adapter;
    private void showKeyFrames(String path) {
        if(adapter == null){
            binding.rvImages.setLayoutManager(
                    new LinearLayoutManager(getRootView().getContext(), RecyclerView.HORIZONTAL,false));
            adapter = new MyRecyclerViewAdapter() {
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
        }else {
            adapter.clear();
        }

        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<List<byte[]>>() {
            @Override
            public List<byte[]> doInBackground() throws Throwable {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String motionVideoPath = MotionPhotoUtil.getMotionVideoPath(path);
                    return KeyFrameExtractor.extractFrames2(motionVideoPath, 15,
                            new MyCommonCallback<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            ThreadUtils.getMainHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.add(bytes);
                                }
                            });

                        }
                    });
                }
                return new ArrayList<>();
            }

            @Override
            public void onSuccess(List<byte[]> result) {

                adapter.refresh(result);

            }
        });
    }
}
