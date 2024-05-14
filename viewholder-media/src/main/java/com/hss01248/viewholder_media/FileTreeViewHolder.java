package com.hss01248.viewholder_media;

import android.content.Context;

import androidx.core.util.Pair;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hss.utils.enhance.viewholder.mvvm.BaseViewHolder;
import com.hss01248.toast.MyToast;
import com.hss01248.viewholder_media.databinding.LayoutFileTreeBinding;
import com.hss01248.viewstate.StatefulLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    Map<String,List<String>> cache = new TreeMap<>();
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
        binding.llTree.addView(listViewHolder.getRootView());

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
                String[] files = file.list();
                //只列出文件名,不列出上级路径,蛋疼的api
                LogUtils.w(files);
                if(files ==null || files.length ==0){
                    return new Pair<>(path,new ArrayList<>());
                }
                List<String> list = new ArrayList<>(files.length);
                for (String s : files) {
                    list.add(path+"/"+s);
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
