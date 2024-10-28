package com.hss01248.viewholder_media.scan;

import android.app.Dialog;
import android.os.Environment;

import androidx.core.util.Pair;

import com.blankj.utilcode.util.ThreadUtils;
import com.hss.utils.enhance.viewholder.ContainerActivity2;
import com.hss.utils.enhance.viewholder.mvvm.ContainerViewHolderWithTitleBar;
import com.hss01248.toast.MyToast;
import com.hss01248.viewholder_media.CommonMediaListViewHolder;
import com.hss01248.viewholder_media.api.AbsFile;
import com.hss01248.viewholder_media.api.JavaFile;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @Despciption todo
 * @Author hss
 * @Date 10/28/24 4:17 PM
 * @Version 1.0
 */
public class TreeScanUtil {

    public static void findAllImages(AbsFile root,boolean ignoreHiddenDir, MediaScanCallback callback){
        new TreeAsyncIterator().iteratorTree2(root, true, 0, new TreeIteratorCallback<AbsFile>() {
            List<AbsFile> list = new ArrayList<>();

            @Override
            public boolean skipDir(AbsFile container) {
                return ignoreHiddenDir && container.getName().startsWith(".");
            }

            @Override
            public boolean onFile(AbsFile node) {
                String name = node.getName();
                if(ignoreHiddenDir && name.startsWith(".")){
                    return true;
                }
                if(name.contains(".") && !name.endsWith(".")){
                    name = name.substring(name.lastIndexOf(".")+1).toLowerCase();
                    if("jpg".equals(name)
                            || "png".equals(name)
                            || "avif".equals(name)
                            || "gif".equals(name)
                            || "jpeg".equals(name)){
                        list.add(node);
                        callback.onProgress(node,list);
                    }else if("mp4".equals(name)
                            || "mov".equals(name)
                            || "mkv".equals(name)){
                        list.add(node);
                        callback.onProgress(node,list);
                    }
                }

                return true;
            }

            @Override
            public void onProgress(long total, long current, AbsFile node, String desc) {

            }

            @Override
            public void onFinished(long total, long timeCostMills, List<AbsFile> failedList) {
                callback.onFinish(list);

            }
        });
    }

    public static void scanExternalStorage(boolean ignoreHiddenDir){
        scan(ignoreHiddenDir,new JavaFile(Environment.getExternalStorageDirectory()).setRoot(true));
    }


    public static void scan(boolean ignoreHiddenDir,AbsFile root){
        ContainerActivity2.start(new Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>>() {
            @Override
            public void accept(Pair<ContainerActivity2, ContainerViewHolderWithTitleBar> pair) throws Exception {
                CommonMediaListViewHolder listViewHolder1 = new CommonMediaListViewHolder(pair.first);
                pair.second.getBinding().realTitleBar.setTitle("应用私有目录,前1个为内部,后1个为外部");
                pair.second.getBinding().rlContainer.addView(listViewHolder1.getRootView());
                listViewHolder1.setOnItemClicked(new Consumer<AbsFile>() {
                    @Override
                    public void accept(AbsFile s) throws Exception {
                        //viewDirInActivity(s);
                        MyToast.show(s.getAbsolutePath());
                    }
                });

                Dialog dialog = MyToast.showLoadingDialog("scanning...");
                findAllImages(root,
                        ignoreHiddenDir, new MediaScanCallback() {
                            @Override
                            public void onProgress(AbsFile file, List<AbsFile> list) {
                                MyToast.dismissLoadingDialog(dialog);
                                ThreadUtils.getMainHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(listViewHolder1.getAdapter() ==null){
                                            listViewHolder1.init(list);
                                        }else {
                                            listViewHolder1.getAdapter().add(file);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFinish(List<AbsFile> files) {

                            }
                        });


            }
        });
    }


}
