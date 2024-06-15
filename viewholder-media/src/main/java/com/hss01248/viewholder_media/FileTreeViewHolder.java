package com.hss01248.viewholder_media;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.core.util.Pair;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hss.utils.enhance.viewholder.ContainerActivity;
import com.hss.utils.enhance.viewholder.ContainerActivity2;
import com.hss.utils.enhance.viewholder.mvvm.BaseViewHolder;
import com.hss.utils.enhance.viewholder.mvvm.ContainerViewHolderWithTitleBar;
import com.hss01248.iwidget.singlechoose.ISingleChooseItem;
import com.hss01248.toast.MyToast;
import com.hss01248.viewholder.databinding.ActivityCommonContainerBinding;
import com.hss01248.viewholder_media.databinding.LayoutFileTreeBinding;
import com.hss01248.viewstate.StatefulLayout;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import io.reactivex.functions.Consumer;

/**
 * @Despciption todo
 * @Author hss
 * @Date 5/14/24 9:12 AM
 * @Version 1.0
 */
public class FileTreeViewHolder extends BaseViewHolder<LayoutFileTreeBinding,String> {
    StatefulLayout stateManager;
    String currentPath;
    MediaListViewHolder listViewHolder;
    DisplayAndFilterInfo filterInfo = new DisplayAndFilterInfo();
    Map<String,List<String>> cache = new TreeMap<>();


    public static void viewDirInActivity(String dir){
        ContainerActivity2.start( new Consumer<Pair<ContainerActivity2, ContainerViewHolderWithTitleBar>>() {
            @Override
            public void accept(Pair<ContainerActivity2, ContainerViewHolderWithTitleBar> pair) throws Exception {
                File externalStorageDirectory = new File(dir);
                FileTreeViewHolder viewHolder = new FileTreeViewHolder(pair.first);
                viewHolder.init(externalStorageDirectory.getAbsolutePath());
                pair.second.getBinding().rlContainer.addView(viewHolder.getRootView());
                pair.second.setTitleBarHidden(true);
                pair.second.setStatusBarFontBlack(true);
                //处理宽高:

                pair.first.setOnBackPressed(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        //ToastUtils.showShort("拦截后退键");
                        return viewHolder.onBackPressed();
                    }
                });
            }
        });
    }




    public FileTreeViewHolder(Context context) {
        super(context);
        stateManager = StatefulLayout.wrapWithStateOfPage(rootView, new Runnable() {
            @Override
            public void run() {
                loadDir(currentPath);
            }
        });
        rootView = stateManager;
        listViewHolder = new MediaListViewHolder(context);
        binding.llContainer.addView(listViewHolder.getRootView());

        listViewHolder.setOnItemClicked(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                File file = new File(s);
                if(file.isDirectory()){
                    loadDir(s);
                }else {
                    MyToast.show("打开文件: \n"+s);
                }
            }
        });

        binding.ivMenus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initMenus(view);

            }
        });
    }

    private void initMenus(View view) {
        List<ISingleChooseItem<String>> menus = new ArrayList<>();
        menus.add(new ISingleChooseItem<String>() {
            @Override
            public String text() {
                return "显示模式-表格";
            }

            @Override
            public void onItemClicked(int position, String bean) {

                filterInfo.displayType = 0;
                listViewHolder.setFilterInfo(filterInfo);
            }
        });
        menus.add(new ISingleChooseItem<String>() {
            @Override
            public String text() {
                return "显示模式-列表";
            }

            @Override
            public void onItemClicked(int position, String bean) {
                filterInfo.displayType = 1;
                listViewHolder.setFilterInfo(filterInfo);
            }
        });
        menus.add(new ISingleChooseItem<String>() {
            @Override
            public String text() {
                return "排序-文件名-顺序";
            }

            @Override
            public void onItemClicked(int position, String bean) {

            }
        });
        menus.add(new ISingleChooseItem<String>() {
            @Override
            public String text() {
                return "排序-文件名-倒序";
            }

            @Override
            public void onItemClicked(int position, String bean) {

            }
        });
        menus.add(new ISingleChooseItem<String>() {
            @Override
            public String text() {
                return "排序-文件大小-大到小";
            }

            @Override
            public void onItemClicked(int position, String bean) {

            }
        });
        menus.add(new ISingleChooseItem<String>() {
            @Override
            public String text() {
                return "排序-文件大小-小到大";
            }

            @Override
            public void onItemClicked(int position, String bean) {

            }
        });
        menus.add(new ISingleChooseItem<String>() {
            @Override
            public String text() {
                return "排序-时间-旧到新";
            }

            @Override
            public void onItemClicked(int position, String bean) {

            }
        });
        menus.add(new ISingleChooseItem<String>() {
            @Override
            public String text() {
                return "排序-时间-新到旧";
            }

            @Override
            public void onItemClicked(int position, String bean) {

            }
        });

        ISingleChooseItem.showAsMenu2(view,menus,currentPath);
    }


    public  boolean onBackPressed(){
        if("/storage/emulated/0".equals(currentPath)){
            return false;
        }
        File dir = new File(currentPath).getParentFile();
        if(dir !=null && dir.exists()){
            loadDir(dir.getAbsolutePath());
            return true;
        }else {
            return false;
        }
    }


    private void loadDir(String path) {
        currentPath = path;
        binding.tvPath.setText(path);
        stateManager.showLoading();
        File file = new File(path);
        if(!file.exists()){
            stateManager.showError("文件路径不存在:\n"+path);
            return;
        }
        if(!file.isDirectory()){
            stateManager.showError("需要文件夹,但传入的是文件:\n"+path);
            return;
        }
        if(cache.containsKey(path)){
            if(cache.get(path).isEmpty()){
                stateManager.showEmpty();
            }else{
                stateManager.showContent();
            }
            listViewHolder.init(cache.get(path));
        }

        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Pair<String,List<String>>>() {
            @Override
            public Pair<String,List<String>> doInBackground() throws Throwable {
                File[] files1 = file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }
                });

                File[] files2 = file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return !file.isDirectory();
                    }
                });
                //只列出文件名,不列出上级路径,蛋疼的api
                LogUtils.w(files1,files2);
                List<String> list = new ArrayList<>();

                //排序

                if(files1 !=null && files1.length > 0){
                    List<File> list1 = new ArrayList<>();
                    for (File s : files1) {
                        list1.add(s);
                    }
                    Collections.sort(list1, new Comparator<File>() {
                        @Override
                        public int compare(File file, File t1) {
                            return file.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                        }
                    });

                    for (File s : list1) {
                        list.add(s.getAbsolutePath());
                    }
                }
                if(files2 !=null && files2.length > 0){
                    List<File> list1 = new ArrayList<>();
                    for (File s : files2) {
                        list1.add(s);
                    }
                    Collections.sort(list1, new Comparator<File>() {
                        @Override
                        public int compare(File file, File t1) {
                            return file.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                        }
                    });

                    for (File s : list1) {
                        list.add(s.getAbsolutePath());
                    }
                }
                return new Pair<>(path,list);
            }

            @Override
            public void onSuccess(Pair<String,List<String>> result) {
                //更新数据
                cache.put(result.first,result.second);
                if(!currentPath.equals(result.first)){

                    LogUtils.d("路径已经变化(现-原)",currentPath,result.first);
                    return;
                }
                if(result.second.isEmpty()){
                    stateManager.showEmpty();
                }else{
                    stateManager.showContent();
                }
                listViewHolder.init(cache.get(path));
            }

            @Override
            public void onFail(Throwable t) {
                super.onFail(t);
                if(currentPath.equals(path)){
                    stateManager.showError("加载路径失败:\n"+t.getMessage()+"\n"+path);
                }
            }
        });
    }

    @Override
    protected void initDataAndEventInternal(LifecycleOwner lifecycleOwner, String bean) {
        loadDir(bean);
    }
}
